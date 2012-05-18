/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.importing.legacy.service;

import java.util.Date;
import org.complitex.dictionary.util.DateUtil;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import org.complitex.pspoffice.importing.legacy.entity.ImportStatus;
import org.complitex.dictionary.util.ImportStorageUtil;
import java.io.IOException;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.apache.wicket.util.string.Strings;
import org.complitex.address.strategy.apartment.ApartmentStrategy;
import org.complitex.address.strategy.building.BuildingStrategy;
import org.complitex.address.strategy.building.entity.Building;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.service.LocaleBean;
import org.complitex.dictionary.service.exception.AbstractException;
import org.complitex.dictionary.service.exception.ImportCriticalException;
import org.complitex.dictionary.service.exception.ImportFileNotFoundException;
import org.complitex.dictionary.service.exception.ImportFileReadException;
import org.complitex.dictionary.util.ResourceUtil;
import org.complitex.pspoffice.document_type.strategy.DocumentTypeStrategy;
import org.complitex.pspoffice.importing.legacy.entity.ApartmentCardCorrection;
import org.complitex.pspoffice.importing.legacy.entity.BuildingCorrection;
import org.complitex.pspoffice.importing.legacy.entity.ImportMessage;
import org.complitex.pspoffice.importing.legacy.entity.PersonCorrection;
import org.complitex.pspoffice.importing.legacy.entity.ProcessItem;
import org.complitex.pspoffice.importing.legacy.entity.LegacyDataImportFile;
import org.complitex.pspoffice.importing.legacy.entity.ReferenceDataCorrection;
import org.complitex.pspoffice.importing.legacy.entity.StreetCorrection;
import org.complitex.pspoffice.importing.legacy.service.exception.OpenErrorDescriptionFileException;
import org.complitex.pspoffice.importing.legacy.service.exception.OpenErrorFileException;
import org.complitex.pspoffice.importing.legacy.service.exception.TooManyResultsException;
import org.complitex.pspoffice.military.strategy.MilitaryServiceRelationStrategy;
import org.complitex.pspoffice.ownerrelationship.strategy.OwnerRelationshipStrategy;
import org.complitex.pspoffice.ownership.strategy.OwnershipFormStrategy;
import org.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.RegistrationStrategy;
import org.complitex.pspoffice.person.strategy.entity.ApartmentCard;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.entity.Registration;
import org.complitex.pspoffice.registration_type.strategy.RegistrationTypeStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.complitex.pspoffice.importing.legacy.entity.ImportMessage.ImportMessageLevel.*;

/**
 *
 * @author Artem
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@TransactionManagement(TransactionManagementType.BEAN)
public class LegacyDataImportService {

    private static final Logger log = LoggerFactory.getLogger(LegacyDataImportService.class);
    private static final String RESOURCE_BUNDLE = LegacyDataImportService.class.getName();
    private static final char SEPARATOR = '\t';
    private static final String CHARSET = "UTF-8";
    private static final String ERROR_FILE_SUFFIX = "_errors.csv";
    private static final String ERROR_DESCRIPTION_FILE_SUFFIX = "_errors_description.txt";
    private static final int PROCESSING_BATCH = 100;
    @Resource
    private UserTransaction userTransaction;
    @EJB
    private StreetCorrectionBean streetCorrectionBean;
    @EJB
    private BuildingCorrectionBean buildingCorrectionBean;
    @EJB
    private BuildingStrategy buildingStrategy;
    @EJB
    private OwnershipFormStrategy ownershipFormStrategy;
    @EJB
    private OwnerRelationshipStrategy ownerRelationshipStrategy;
    @EJB
    private RegistrationTypeStrategy registrationTypeStrategy;
    @EJB
    private DocumentTypeStrategy documentTypeStrategy;
    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private ReferenceDataCorrectionBean referenceDataCorrectionBean;
    @EJB
    private PersonCorrectionBean personCorrectionBean;
    @EJB
    private ApartmentCardCorrectionBean apartmentCardCorrectionBean;
    @EJB
    private ApartmentStrategy apartmentStrategy;
    @EJB
    private ApartmentCardStrategy apartmentCardStrategy;
    @EJB
    private RegistrationCorrectionBean registrationCorrectionBean;
    @EJB
    private RegistrationStrategy registrationStrategy;
    @EJB
    private MilitaryServiceRelationStrategy militaryServiceRelationStrategy;
    @EJB
    private LocaleBean localeBean;
    private boolean processing;
    private Locale locale;
    private Long cityId;
    private String ownerType;
    private boolean reservedDocumentTypesResolved;
    private boolean reservedRegistrationTypesResolved;
    private boolean reservedOwnerRelationshipResolved;
    private Set<String> jekIds;
    private Map<String, Long> organizationMap;
    private String importDirectory;
    private String errorsDirectory;
    private Map<LegacyDataImportFile, ImportStatus> loadingStatuses = new EnumMap<LegacyDataImportFile, ImportStatus>(LegacyDataImportFile.class);
    private Map<ProcessItem, ImportStatus> processingStatuses = new EnumMap<ProcessItem, ImportStatus>(ProcessItem.class);
    private Queue<ImportMessage> messages = new ConcurrentLinkedQueue<ImportMessage>();

    public boolean isProcessing() {
        return processing;
    }

    public ImportStatus getLoadingStatus(LegacyDataImportFile importFile) {
        return loadingStatuses.get(importFile);
    }

    public ImportStatus getProcessingStatus(ProcessItem processItem) {
        return processingStatuses.get(processItem);
    }

    public ImportMessage getNextMessage() {
        return messages.poll();
    }

    private void clean() {
        loadingStatuses.clear();
        processingStatuses.clear();
        messages.clear();
        cityId = null;
        ownerType = null;
        reservedDocumentTypesResolved = false;
        reservedRegistrationTypesResolved = false;
        reservedOwnerRelationshipResolved = false;
        locale = null;
        jekIds = null;
        organizationMap = null;
        importDirectory = null;
        errorsDirectory = null;
    }

    @Asynchronous
    public void startImport(long cityId, Map<String, Long> organizationMap, String importDirectiry, String errorsDirectory, Locale locale) {
        if (processing) {
            return;
        }

        clean();
        processing = true;

        this.cityId = cityId;
        this.organizationMap = organizationMap;
        this.jekIds = organizationMap.keySet();
        this.importDirectory = importDirectiry;
        this.errorsDirectory = errorsDirectory;
        this.locale = locale;

        //load files
        try {
            userTransaction.begin();
            loadFiles();
            userTransaction.commit();
        } catch (Exception e) {
            processing = false;
            log.error("File loading error.", e);
            try {
                userTransaction.rollback();
            } catch (SystemException e1) {
                log.error("Couldn't to rollback transaction.", e1);
            }

            String errorMessage = e instanceof AbstractException ? e.getMessage() : new ImportCriticalException(e).getMessage();
            messages.add(new ImportMessage(errorMessage, ERROR));
        }

        if (!processing) {
            return;
        }

        //process files
        try {
            processFiles();
        } catch (Exception e) {
            log.error("Processing error.", e);
            String errorMessage = e instanceof AbstractException ? e.getMessage() : new ImportCriticalException(e).getMessage();
            messages.add(new ImportMessage(errorMessage, ERROR));
        } finally {
            processing = false;
        }
    }

    private void loadFiles() throws ImportFileReadException, ImportFileNotFoundException {
        loadStreets();
        loadBuildings();
        loadReferenceData(LegacyDataImportFile.OWNERSHIP_FORM, "ownership_form");
        loadReferenceData(LegacyDataImportFile.MILITARY_DUTY, "military_duty");
        loadReferenceData(LegacyDataImportFile.OWNER_RELATIONSHIP, "owner_relationship");
        loadReferenceData(LegacyDataImportFile.DEPARTURE_REASON, "departure_reason");
        loadReferenceData(LegacyDataImportFile.REGISTRATION_TYPE, "registration_type");
        loadReferenceData(LegacyDataImportFile.DOCUMENT_TYPE, "document_type");
        loadReferenceData(LegacyDataImportFile.OWNER_TYPE, "owner_type");
        loadPersons();
        loadApartmentCards();
    }

    /**
     * id    Street type string(ukr)     Street name(ukr)     Street type string(rus)     Street name(rus)
     */
    private void loadStreets() throws ImportFileReadException, ImportFileNotFoundException {
        final LegacyDataImportFile file = LegacyDataImportFile.STREET;

        final Map<String, Boolean> loadFileStatusMap = Maps.newHashMap();
        for (String idjek : jekIds) {
            loadFileStatusMap.put(idjek, !streetCorrectionBean.exists(idjek));
        }

        final Set<String> loadJekIds = Sets.newHashSet();
        for (Map.Entry<String, Boolean> e : loadFileStatusMap.entrySet()) {
            String idjek = e.getKey();
            if (e.getValue()) {
                loadJekIds.add(idjek);
            } else {
                messages.add(new ImportMessage(getString("already_loaded_file", file.getFileName(), idjek), WARN));
            }
        }

        if (!loadJekIds.isEmpty()) {

            //start file importing:
            loadingStatuses.put(file, new ImportStatus(0));
            messages.add(new ImportMessage(getString("begin_loading_file", file.getFileName()), INFO));

            final CSVReader reader = getCsvReader(importDirectory, file, CHARSET, SEPARATOR);

            int recordIndex = 0;

            try {
                String[] line;

                while ((line = reader.readNext()) != null) {
                    recordIndex++;

                    long id = Long.parseLong(line[0].trim());

                    for (String idjek : loadJekIds) {
                        streetCorrectionBean.insert(new StreetCorrection(id, idjek, line[1].trim(), line[2].trim(),
                                line[3].trim(), line[4].trim(), getContent(line)));
                    }
                    loadingStatuses.get(file).increment();
                }

                //finish file importing
                messages.add(new ImportMessage(getString("finish_loading_file", file.getFileName()), INFO));
                loadingStatuses.get(file).finish();
            } catch (IOException e) {
                throw new ImportFileReadException(e, file.getFileName(), recordIndex);
            } catch (NumberFormatException e) {
                throw new ImportFileReadException(e, file.getFileName(), recordIndex);
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error("Couldn't to close csv reader.", e);
                }
            }
        }
    }

    /**
     * id   idjek   idul    dom     korpus
     */
    private void loadBuildings() throws ImportFileReadException, ImportFileNotFoundException {
        final LegacyDataImportFile file = LegacyDataImportFile.BUILDING;

        final Map<String, Boolean> loadFileStatusMap = Maps.newHashMap();
        for (String idjek : jekIds) {
            loadFileStatusMap.put(idjek, !buildingCorrectionBean.exists(idjek));
        }

        final Set<String> loadJekIds = Sets.newHashSet();
        for (Map.Entry<String, Boolean> e : loadFileStatusMap.entrySet()) {
            String idjek = e.getKey();
            if (e.getValue()) {
                loadJekIds.add(idjek);
            } else {
                messages.add(new ImportMessage(getString("already_loaded_file", file.getFileName(), idjek), WARN));
            }
        }

        if (!loadJekIds.isEmpty()) {

            //start file importing:
            loadingStatuses.put(file, new ImportStatus(0));
            messages.add(new ImportMessage(getString("begin_loading_file", file.getFileName()), INFO));

            final CSVReader reader = getCsvReader(importDirectory, file, CHARSET, SEPARATOR);

            int recordIndex = 0;

            try {
                String[] line;

                while ((line = reader.readNext()) != null) {
                    recordIndex++;

                    long id = Long.parseLong(line[0].trim());

                    String idjek = line[1].trim();

                    if (loadJekIds.contains(idjek)) {
                        buildingCorrectionBean.insert(new BuildingCorrection(id, idjek, line[2].trim(),
                                line[3].trim(), line[4].trim(), getContent(line)));
                        loadingStatuses.get(file).increment();
                    }
                }

                //finish file importing
                messages.add(new ImportMessage(getString("finish_loading_file", file.getFileName()), INFO));
                loadingStatuses.get(file).finish();
            } catch (IOException e) {
                throw new ImportFileReadException(e, file.getFileName(), recordIndex);
            } catch (NumberFormatException e) {
                throw new ImportFileReadException(e, file.getFileName(), recordIndex);
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error("Couldn't to close csv reader.", e);
                }
            }
        }
    }

    /**
     * id    nkod
     */
    private void loadReferenceData(final LegacyDataImportFile file, final String entity) throws ImportFileReadException, ImportFileNotFoundException {
        final Map<String, Boolean> loadFileStatusMap = Maps.newHashMap();
        for (String idjek : jekIds) {
            loadFileStatusMap.put(idjek, !referenceDataCorrectionBean.exists(entity, idjek));
        }

        final Set<String> loadJekIds = Sets.newHashSet();
        for (Map.Entry<String, Boolean> e : loadFileStatusMap.entrySet()) {
            String idjek = e.getKey();
            if (e.getValue()) {
                loadJekIds.add(idjek);
            } else {
                messages.add(new ImportMessage(getString("already_loaded_file", file.getFileName(), idjek), WARN));
            }
        }

        if (!loadJekIds.isEmpty()) {

            //start file importing:
            loadingStatuses.put(file, new ImportStatus(0));
            messages.add(new ImportMessage(getString("begin_loading_file", file.getFileName()), INFO));

            final CSVReader reader = getCsvReader(importDirectory, file, CHARSET, SEPARATOR);

            int recordIndex = 0;

            try {
                String[] line;

                while ((line = reader.readNext()) != null) {
                    recordIndex++;

                    long id = Long.parseLong(line[0].trim());

                    for (String idjek : loadJekIds) {
                        referenceDataCorrectionBean.insert(new ReferenceDataCorrection(entity, id, idjek, line[1].trim(),
                                getContent(line)));
                    }
                    loadingStatuses.get(file).increment();
                }

                //finish file importing
                messages.add(new ImportMessage(getString("finish_loading_file", file.getFileName()), INFO));
                loadingStatuses.get(file).finish();
            } catch (IOException e) {
                throw new ImportFileReadException(e, file.getFileName(), recordIndex);
            } catch (NumberFormatException e) {
                throw new ImportFileReadException(e, file.getFileName(), recordIndex);
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error("Couldn't to close csv reader.", e);
                }
            }
        }
    }

    /**
     * 0  2     3   4  5   6  7  8     9            11    12  13    17   18   19     20     21    22       23     24
     * id idbud rah kv fam im ot datar realtovlaskv grajd pol idrel nkra nobl nrayon nmisto iddok dokseria doknom dokvidan
     * 
     * 25        28    30   31   32     33     34       35    36   37    38  39       40    41     43   44   45     46
     * dokdatvid idarm pkra pobl prayon pmisto pdpribza pidul pbud pkorp pkv pdpribvm dprop idvidp vkra vobl vrayon vmisto
     * 
     * 47    48   49    50  51    52    53        56   57
     * vidul vbud vkorp vkv vdata idvip parentnom larc nom
     */
    private void loadPersons() throws ImportFileReadException, ImportFileNotFoundException {
        final LegacyDataImportFile file = LegacyDataImportFile.PERSON;

        final boolean exists = personCorrectionBean.exists();

        if (exists) {
            messages.add(new ImportMessage(getString("already_loaded_person_file", file.getFileName()), WARN));
        }

        if (!exists) {

            //start file importing:
            loadingStatuses.put(file, new ImportStatus(0));
            messages.add(new ImportMessage(getString("begin_loading_file", file.getFileName()), INFO));

            final CSVReader reader = getCsvReader(importDirectory, file, CHARSET, SEPARATOR);

            int recordIndex = 0;

            try {
                String[] line;

                while ((line = reader.readNext()) != null) {
                    recordIndex++;

                    long id = Long.parseLong(line[0].trim());
                    PersonCorrection p = new PersonCorrection(id, getContent(line));
                    p.setIdbud(line[2].trim());
                    p.setRah(line[3].trim());
                    p.setKv(line[4].trim());
                    p.setFam(line[5].trim());
                    p.setIm(line[6].trim());
                    p.setOt(line[7].trim());
                    p.setDatar(line[8].trim());
                    p.setReltovlaskv(line[9].trim());
                    p.setGrajd(line[11].trim());
                    p.setPol(line[12].trim());
                    p.setIdrel(line[13].trim());
                    p.setNkra(line[17].trim());
                    p.setNobl(line[18].trim());
                    p.setNrayon(line[19].trim());
                    p.setNmisto(line[20].trim());
                    p.setIddok(line[21].trim());
                    p.setDokseria(line[22].trim());
                    p.setDoknom(line[23].trim());
                    p.setDokvidan(line[24].trim());
                    p.setDokdatvid(line[25].trim());
                    p.setIdarm(line[28].trim());
                    p.setPkra(line[30].trim());
                    p.setPobl(line[31].trim());
                    p.setPrayon(line[32].trim());
                    p.setPmisto(line[33].trim());
                    p.setPdpribza(line[34].trim());
                    p.setPidul(line[35].trim());
                    p.setPbud(line[36].trim());
                    p.setPkorp(line[37].trim());
                    p.setPkv(line[38].trim());
                    p.setPdpribvm(line[39].trim());
                    p.setDprop(line[40].trim());
                    p.setIdvidp(line[41].trim());
                    p.setVkra(line[43].trim());
                    p.setVobl(line[44].trim());
                    p.setVrayon(line[45].trim());
                    p.setVmisto(line[46].trim());
                    p.setVidul(line[47].trim());
                    p.setVbud(line[48].trim());
                    p.setVkorp(line[49].trim());
                    p.setVkv(line[50].trim());
                    p.setVdata(line[51].trim());
                    p.setIdvip(line[52].trim());
                    p.setLarc(line[56].trim());
                    p.setNom(line[57].trim());
                    p.setParentnom(line[53].trim());

                    personCorrectionBean.insert(p);

                    loadingStatuses.get(file).increment();
                }

                //finish file importing
                messages.add(new ImportMessage(getString("finish_loading_file", file.getFileName()), INFO));
                loadingStatuses.get(file).finish();
            } catch (IOException e) {
                throw new ImportFileReadException(e, file.getFileName(), recordIndex);
            } catch (NumberFormatException e) {
                throw new ImportFileReadException(e, file.getFileName(), recordIndex);
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error("Couldn't to close csv reader.", e);
                }
            }
        }
    }

    /**
     * 0  1     2   3  4   5        29
     * id idbud rah kv fio idprivat larc
     */
    private void loadApartmentCards() throws ImportFileReadException, ImportFileNotFoundException {
        final LegacyDataImportFile file = LegacyDataImportFile.APARTMENT_CARD;

        final boolean exists = apartmentCardCorrectionBean.exists();

        if (exists) {
            messages.add(new ImportMessage(getString("already_loaded_apartment_card_file", file.getFileName()), WARN));
        }

        if (!exists) {

            //start file importing:
            loadingStatuses.put(file, new ImportStatus(0));
            messages.add(new ImportMessage(getString("begin_loading_file", file.getFileName()), INFO));

            final CSVReader reader = getCsvReader(importDirectory, file, CHARSET, SEPARATOR);

            int recordIndex = 0;

            try {
                String[] line;

                while ((line = reader.readNext()) != null) {
                    recordIndex++;

                    long id = Long.parseLong(line[0].trim());
                    ApartmentCardCorrection c = new ApartmentCardCorrection(id, getContent(line));
                    c.setIdbud(line[1].trim());
                    c.setRah(line[2].trim());
                    c.setKv(line[3].trim());
                    c.setFio(line[4].trim());
                    c.setIdprivat(line[5].trim());
                    c.setLarc(line[29].trim());

                    apartmentCardCorrectionBean.insert(c);

                    loadingStatuses.get(file).increment();
                }

                //finish file importing
                messages.add(new ImportMessage(getString("finish_loading_file", file.getFileName()), INFO));
                loadingStatuses.get(file).finish();
            } catch (IOException e) {
                throw new ImportFileReadException(e, file.getFileName(), recordIndex);
            } catch (NumberFormatException e) {
                throw new ImportFileReadException(e, file.getFileName(), recordIndex);
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error("Couldn't to close csv reader.", e);
                }
            }
        }
    }

    private void processFiles() throws OpenErrorFileException, OpenErrorDescriptionFileException {
        processStreetsAndBuildings();
        processOwnershipForms();
        processOwnerRelationships();
        processMilitaryDuties();
        processRegistrationsTypes();
        processDocumentsTypes();
        processOwnerTypes();
        processPersons();
        processApartmentCards();
        processRegistrations();
    }

    private void processStreetsAndBuildings() throws OpenErrorFileException, OpenErrorDescriptionFileException {
        try {
            final ProcessItem item = ProcessItem.STREET_BUILDING;

            BufferedWriter streetErrorFile = null;
            BufferedWriter streetErrorDescriptionFile = null;
            BufferedWriter buildingErrorFile = null;
            BufferedWriter buildingErrorDescriptionFile = null;

            processingStatuses.put(item, new ImportStatus(0));
            final int count = buildingCorrectionBean.countForProcessing(jekIds);
            messages.add(new ImportMessage(getString("begin_street_building_processing", count), INFO));
            boolean wasErrors = false;

            try {
                for (String idjek : jekIds) {
                    int jekCount = buildingCorrectionBean.countForProcessing(idjek);
                    while (jekCount > 0) {
                        List<BuildingCorrection> buildings = buildingCorrectionBean.findForProcessing(idjek, PROCESSING_BATCH);
                        for (BuildingCorrection building : buildings) {
                            if (building.getSystemBuildingId() == null) {
                                userTransaction.begin();

                                String buildingErrorDescription = null;
                                String streetErrorDescription = null;

                                String idul = building.getIdul();
                                final StreetCorrection street = streetCorrectionBean.getById(idul, idjek);
                                if (street == null) {
                                    buildingErrorDescription = getString("building_invalid_street_id",
                                            building.getId(), building.getIdjek(), idul);
                                } else {
                                    Long systemStreetId = street.getSystemStreetId();
                                    if (systemStreetId == null && !street.isProcessed()) {
                                        final String ukrStreet = street.getNkod();
                                        final String rusStreet = street.getNkod1();
                                        final String ukrStreetType = street.getUtype();
                                        final String rusStreetType = street.getRtype();

                                        //street type
                                        Long systemStreetTypeId = streetCorrectionBean.findSystemStreetType(ukrStreetType, rusStreetType);
                                        if (systemStreetTypeId == null) {
                                            streetErrorDescription = getString("street_type_not_resolved", street.getId(),
                                                    street.getIdjek(), ukrStreetType, rusStreetType);
                                        } else {
                                            //street
                                            systemStreetId = streetCorrectionBean.findSystemStreet(cityId,
                                                    systemStreetTypeId, ukrStreet, rusStreet);
                                            if (systemStreetId == null) {
                                                streetErrorDescription = getString("street_not_resolved", street.getId(),
                                                        street.getIdjek(), ukrStreet, rusStreet);
                                            } else {
                                                street.setSystemStreetId(systemStreetId);
                                            }
                                        }
                                        street.setProcessed(true);
                                        streetCorrectionBean.update(street);
                                    }
                                    if (systemStreetId != null) {
                                        final String dom = building.getDom();
                                        final String korpus = building.getKorpus();

                                        try {
                                            Long systemBuildingId =
                                                    buildingCorrectionBean.findSystemBuilding(systemStreetId, dom, korpus);
                                            if (systemBuildingId == null) {
                                                Building systemBuilding =
                                                        buildingCorrectionBean.newBuilding(systemStreetId, dom, korpus,
                                                        organizationMap.get(building.getIdjek()));
                                                buildingStrategy.insert(systemBuilding, DateUtil.getCurrentDate());
                                                systemBuildingId = systemBuilding.getId();
                                            }
                                            building.setSystemBuildingId(systemBuildingId);
                                        } catch (TooManyResultsException e) {
                                            buildingErrorDescription = getString("building_system_many_objects",
                                                    building.getId(), building.getIdjek(), building.getDom(), building.getKorpus(),
                                                    building.getIdul());
                                        }
                                    } else {
                                        buildingErrorDescription = getString("building_system_street_not_resolved",
                                                building.getId(), building.getIdjek(), idul);
                                    }
                                }

                                building.setProcessed(true);
                                buildingCorrectionBean.update(building);
                                userTransaction.commit();

                                processingStatuses.get(item).increment();

                                if (streetErrorDescription != null) {
                                    wasErrors = true;
                                    if (streetErrorFile == null) {
                                        streetErrorFile = getErrorFile(errorsDirectory, LegacyDataImportFile.STREET);
                                        streetErrorFile.write(LegacyDataImportFile.STREET.getCsvHeader());
                                        streetErrorFile.newLine();
                                    }
                                    if (streetErrorDescriptionFile == null) {
                                        streetErrorDescriptionFile = getErrorDescriptionFile(errorsDirectory,
                                                LegacyDataImportFile.STREET);
                                    }

                                    streetErrorFile.write(street.getContent());
                                    streetErrorFile.newLine();

                                    streetErrorDescriptionFile.write(streetErrorDescription);
                                    streetErrorDescriptionFile.newLine();
                                }

                                if (buildingErrorDescription != null) {
                                    wasErrors = true;
                                    if (buildingErrorFile == null) {
                                        buildingErrorFile = getErrorFile(errorsDirectory, LegacyDataImportFile.BUILDING);
                                        buildingErrorFile.write(LegacyDataImportFile.BUILDING.getCsvHeader());
                                        buildingErrorFile.newLine();
                                    }
                                    if (buildingErrorDescriptionFile == null) {
                                        buildingErrorDescriptionFile = getErrorDescriptionFile(errorsDirectory,
                                                LegacyDataImportFile.BUILDING);
                                    }

                                    buildingErrorFile.write(building.getContent());
                                    buildingErrorFile.newLine();

                                    buildingErrorDescriptionFile.write(buildingErrorDescription);
                                    buildingErrorDescriptionFile.newLine();
                                }
                            }
                        }
                        jekCount = buildingCorrectionBean.countForProcessing(idjek);
                    }
                }
            } catch (Exception e) {
                try {
                    if (userTransaction.getStatus() != Status.STATUS_NO_TRANSACTION) {
                        userTransaction.rollback();
                    }
                } catch (Exception e1) {
                    log.error("Couldn't to rollback transaction.", e1);
                }

                throw new RuntimeException(e);
            } finally {
                if (streetErrorFile != null) {
                    try {
                        streetErrorFile.close();
                    } catch (IOException e) {
                        log.error("Couldn't to close file stream.", e);
                    }
                }
                if (streetErrorDescriptionFile != null) {
                    try {
                        streetErrorDescriptionFile.close();
                    } catch (IOException e) {
                        log.error("Couldn't to close file stream.", e);
                    }
                }
                if (buildingErrorFile != null) {
                    try {
                        buildingErrorFile.close();
                    } catch (IOException e) {
                        log.error("Couldn't to close file stream.", e);
                    }
                }
                if (buildingErrorDescriptionFile != null) {
                    try {
                        buildingErrorDescriptionFile.close();
                    } catch (IOException e) {
                        log.error("Couldn't to close file stream.", e);
                    }
                }
            }

            if (wasErrors) {
                messages.add(new ImportMessage(getString("fail_finish_street_building_processing", count), WARN));
            } else {
                messages.add(new ImportMessage(getString("success_finish_street_building_processing", count), INFO));
            }
            processingStatuses.get(item).finish();
        } catch (RuntimeException e) {
            throw e;
        } finally {
            try {
                userTransaction.begin();

                streetCorrectionBean.clearProcessingStatus(jekIds);
                buildingCorrectionBean.clearProcessingStatus(jekIds);

                userTransaction.commit();
            } catch (Exception e) {
                try {
                    if (userTransaction.getStatus() != Status.STATUS_NO_TRANSACTION) {
                        userTransaction.rollback();
                    }
                } catch (SystemException e1) {
                    log.error("Couldn't to rollback transaction.", e1);
                }
                log.error("Couldn't to clear processing status for streets and buildings.", e);
            }
        }
    }

    private void processOwnershipForms() throws OpenErrorFileException, OpenErrorDescriptionFileException {
        final String entity = "ownership_form";
        try {
            final ProcessItem item = ProcessItem.OWNERSHIP_FORM;

            BufferedWriter ownershipFormErrorFile = null;
            BufferedWriter ownershipFormErrorDescriptionFile = null;

            processingStatuses.put(item, new ImportStatus(0));
            final int count = referenceDataCorrectionBean.countForProcessing(entity, jekIds);
            messages.add(new ImportMessage(getString("begin_ownership_form_processing", count), INFO));
            boolean wasErrors = false;

            try {
                for (String idjek : jekIds) {
                    int jekCount = referenceDataCorrectionBean.countForProcessing(entity, idjek);
                    while (jekCount > 0) {
                        List<ReferenceDataCorrection> ownershipForms =
                                referenceDataCorrectionBean.findForProcessing(entity, idjek, PROCESSING_BATCH);

                        userTransaction.begin();
                        for (ReferenceDataCorrection ownershipForm : ownershipForms) {
                            String errorDescription = null;
                            try {
                                Long systemOwnershipFormId = referenceDataCorrectionBean.findSystemObject(entity, ownershipForm.getNkod());
                                if (systemOwnershipFormId == null) {
                                    DomainObject systemOwnershipForm = ownershipFormStrategy.newInstance();
                                    Utils.setValue(systemOwnershipForm.getAttribute(OwnershipFormStrategy.NAME),
                                            ownershipForm.getNkod());
                                    ownershipFormStrategy.insert(systemOwnershipForm, DateUtil.getCurrentDate());
                                    systemOwnershipFormId = systemOwnershipForm.getId();
                                }
                                ownershipForm.setSystemObjectId(systemOwnershipFormId);
                            } catch (TooManyResultsException e) {
                                errorDescription = getString("ownership_form_system_many_objects", ownershipForm.getId(),
                                        idjek, ownershipForm.getNkod());
                            }

                            ownershipForm.setProcessed(true);
                            referenceDataCorrectionBean.update(ownershipForm);

                            processingStatuses.get(item).increment();

                            if (errorDescription != null) {
                                wasErrors = true;
                                if (ownershipFormErrorFile == null) {
                                    ownershipFormErrorFile = getErrorFile(errorsDirectory, LegacyDataImportFile.OWNERSHIP_FORM);
                                    ownershipFormErrorFile.write(LegacyDataImportFile.OWNERSHIP_FORM.getCsvHeader());
                                    ownershipFormErrorFile.newLine();
                                }
                                if (ownershipFormErrorDescriptionFile == null) {
                                    ownershipFormErrorDescriptionFile = getErrorDescriptionFile(errorsDirectory,
                                            LegacyDataImportFile.OWNERSHIP_FORM);
                                }

                                ownershipFormErrorFile.write(ownershipForm.getContent());
                                ownershipFormErrorFile.newLine();

                                ownershipFormErrorDescriptionFile.write(errorDescription);
                                ownershipFormErrorDescriptionFile.newLine();
                            }
                        }

                        userTransaction.commit();
                        jekCount = referenceDataCorrectionBean.countForProcessing(entity, idjek);
                    }
                }
            } catch (Exception e) {
                try {
                    if (userTransaction.getStatus() != Status.STATUS_NO_TRANSACTION) {
                        userTransaction.rollback();
                    }
                } catch (Exception e1) {
                    log.error("Couldn't to rollback transaction.", e1);
                }

                throw new RuntimeException(e);
            } finally {
                if (ownershipFormErrorFile != null) {
                    try {
                        ownershipFormErrorFile.close();
                    } catch (IOException e) {
                        log.error("Couldn't to close file stream.", e);
                    }
                }
                if (ownershipFormErrorDescriptionFile != null) {
                    try {
                        ownershipFormErrorDescriptionFile.close();
                    } catch (IOException e) {
                        log.error("Couldn't to close file stream.", e);
                    }
                }
            }

            if (wasErrors) {
                messages.add(new ImportMessage(getString("fail_finish_ownership_form_processing", count), WARN));
            } else {
                messages.add(new ImportMessage(getString("success_finish_ownership_form_processing", count), INFO));
            }
            processingStatuses.get(item).finish();
        } catch (RuntimeException e) {
            throw e;
        } finally {
            try {
                userTransaction.begin();

                referenceDataCorrectionBean.clearProcessingStatus(entity, jekIds);

                userTransaction.commit();
            } catch (Exception e) {
                try {
                    if (userTransaction.getStatus() != Status.STATUS_NO_TRANSACTION) {
                        userTransaction.rollback();
                    }
                } catch (SystemException e1) {
                    log.error("Couldn't to rollback transaction.", e1);
                }
                log.error("Couldn't to clear processing status for ownership forms.", e);
            }
        }
    }

    private void processOwnerRelationships() throws OpenErrorFileException, OpenErrorDescriptionFileException {
        final String entity = "owner_relationship";
        try {
            final ProcessItem item = ProcessItem.OWNER_RELATIONSHIP;

            BufferedWriter ownerRelationshipErrorFile = null;
            BufferedWriter ownerRelationshipErrorDescriptionFile = null;

            processingStatuses.put(item, new ImportStatus(0));
            final int count = referenceDataCorrectionBean.countForProcessing(entity, jekIds);
            messages.add(new ImportMessage(getString("begin_owner_relationship_processing", count), INFO));
            boolean wasErrors = false;

            try {
                for (String idjek : jekIds) {
                    int jekCount = referenceDataCorrectionBean.countForProcessing(entity, idjek);
                    while (jekCount > 0) {
                        List<ReferenceDataCorrection> ownerRelationships =
                                referenceDataCorrectionBean.findForProcessing(entity, idjek, PROCESSING_BATCH);

                        userTransaction.begin();
                        for (ReferenceDataCorrection ownerRelationship : ownerRelationships) {
                            String errorDescription = null;

                            if (ownerRelationship.getId() == ReferenceDataCorrectionBean.DAUGHTER) { // дочь
                                ownerRelationship.setSystemObjectId(OwnerRelationshipStrategy.DAUGHTER);
                            } else if (ownerRelationship.getId() == ReferenceDataCorrectionBean.SON) { // сын
                                ownerRelationship.setSystemObjectId(OwnerRelationshipStrategy.SON);
                            } else {
                                try {
                                    Long systemOwnerRelationshipId =
                                            referenceDataCorrectionBean.findSystemObject(entity, ownerRelationship.getNkod());
                                    if (systemOwnerRelationshipId == null) {
                                        DomainObject systemOwnerRelationship = ownerRelationshipStrategy.newInstance();
                                        Utils.setValue(systemOwnerRelationship.getAttribute(OwnerRelationshipStrategy.NAME),
                                                ownerRelationship.getNkod());
                                        ownerRelationshipStrategy.insert(systemOwnerRelationship, DateUtil.getCurrentDate());
                                        systemOwnerRelationshipId = systemOwnerRelationship.getId();
                                    }
                                    ownerRelationship.setSystemObjectId(systemOwnerRelationshipId);
                                } catch (TooManyResultsException e) {
                                    errorDescription = getString("owner_relationship_system_many_objects", ownerRelationship.getId(),
                                            idjek, ownerRelationship.getNkod());
                                }
                            }

                            ownerRelationship.setProcessed(true);
                            referenceDataCorrectionBean.update(ownerRelationship);

                            processingStatuses.get(item).increment();

                            if (errorDescription != null) {
                                wasErrors = true;
                                if (ownerRelationshipErrorFile == null) {
                                    ownerRelationshipErrorFile = getErrorFile(errorsDirectory, LegacyDataImportFile.OWNER_RELATIONSHIP);
                                    ownerRelationshipErrorFile.write(LegacyDataImportFile.OWNER_RELATIONSHIP.getCsvHeader());
                                    ownerRelationshipErrorFile.newLine();
                                }
                                if (ownerRelationshipErrorDescriptionFile == null) {
                                    ownerRelationshipErrorDescriptionFile = getErrorDescriptionFile(errorsDirectory,
                                            LegacyDataImportFile.OWNER_RELATIONSHIP);
                                }

                                ownerRelationshipErrorFile.write(ownerRelationship.getContent());
                                ownerRelationshipErrorFile.newLine();

                                ownerRelationshipErrorDescriptionFile.write(errorDescription);
                                ownerRelationshipErrorDescriptionFile.newLine();
                            }
                        }

                        userTransaction.commit();
                        jekCount = referenceDataCorrectionBean.countForProcessing(entity, idjek);
                    }
                }

                try {
                    referenceDataCorrectionBean.checkReservedOwnerRelationships(jekIds);
                    reservedOwnerRelationshipResolved = true;
                } catch (ReferenceDataCorrectionBean.OwnerRelationshipsNotResolved e) {
                    wasErrors = true;

                    StringBuilder sb = new StringBuilder();
                    if (!e.isDaughterResolved()) {
                        sb.append(ownerRelationshipStrategy.displayDomainObject(
                                ownerRelationshipStrategy.findById(OwnerRelationshipStrategy.DAUGHTER, true), localeBean.getSystemLocale())).
                                append(", ");
                    }
                    if (!e.isSonResolved()) {
                        sb.append(ownerRelationshipStrategy.displayDomainObject(
                                ownerRelationshipStrategy.findById(OwnerRelationshipStrategy.SON, true), localeBean.getSystemLocale())).
                                append(", ");
                    }
                    sb.delete(sb.length() - 2, sb.length());
                    String error = getString("reserved_owner_relationship_not_resolved", sb.toString(), jekIds.toString());
                    if (ownerRelationshipErrorDescriptionFile == null) {
                        ownerRelationshipErrorDescriptionFile = getErrorDescriptionFile(errorsDirectory,
                                LegacyDataImportFile.OWNER_RELATIONSHIP);
                    }
                    ownerRelationshipErrorDescriptionFile.write(error);
                    ownerRelationshipErrorDescriptionFile.newLine();

                    messages.add(new ImportMessage(error, WARN));
                }
            } catch (Exception e) {
                try {
                    if (userTransaction.getStatus() != Status.STATUS_NO_TRANSACTION) {
                        userTransaction.rollback();
                    }
                } catch (Exception e1) {
                    log.error("Couldn't to rollback transaction.", e1);
                }

                throw new RuntimeException(e);
            } finally {
                if (ownerRelationshipErrorFile != null) {
                    try {
                        ownerRelationshipErrorFile.close();
                    } catch (IOException e) {
                        log.error("Couldn't to close file stream.", e);
                    }
                }
                if (ownerRelationshipErrorDescriptionFile != null) {
                    try {
                        ownerRelationshipErrorDescriptionFile.close();
                    } catch (IOException e) {
                        log.error("Couldn't to close file stream.", e);
                    }
                }
            }

            if (wasErrors) {
                messages.add(new ImportMessage(getString("fail_finish_owner_relationship_processing", count), WARN));
            } else {
                messages.add(new ImportMessage(getString("success_finish_owner_relationship_processing", count), INFO));
            }
            processingStatuses.get(item).finish();
        } catch (RuntimeException e) {
            throw e;
        } finally {
            try {
                userTransaction.begin();

                referenceDataCorrectionBean.clearProcessingStatus(entity, jekIds);

                userTransaction.commit();
            } catch (Exception e) {
                try {
                    if (userTransaction.getStatus() != Status.STATUS_NO_TRANSACTION) {
                        userTransaction.rollback();
                    }
                } catch (SystemException e1) {
                    log.error("Couldn't to rollback transaction.", e1);
                }
                log.error("Couldn't to clear processing status for owner relationships.", e);
            }
        }
    }

    private void processMilitaryDuties() throws OpenErrorFileException, OpenErrorDescriptionFileException {
        final String entity = "military_duty";
        try {
            final ProcessItem item = ProcessItem.MILITARY_DUTY;

            BufferedWriter militaryDutyErrorFile = null;
            BufferedWriter militaryDutyErrorDescriptionFile = null;

            processingStatuses.put(item, new ImportStatus(0));
            final int count = referenceDataCorrectionBean.countForProcessing(entity, jekIds);
            messages.add(new ImportMessage(getString("begin_military_duty_processing", count), INFO));
            boolean wasErrors = false;

            try {
                for (String idjek : jekIds) {
                    int jekCount = referenceDataCorrectionBean.countForProcessing(entity, idjek);
                    while (jekCount > 0) {
                        List<ReferenceDataCorrection> militaryDuties =
                                referenceDataCorrectionBean.findForProcessing(entity, idjek, PROCESSING_BATCH);

                        userTransaction.begin();
                        for (ReferenceDataCorrection militaryDuty : militaryDuties) {
                            String errorDescription = null;
                            try {
                                Long systemMilitaryDutyId =
                                        referenceDataCorrectionBean.findSystemObject("military_service_relation", militaryDuty.getNkod());
                                if (systemMilitaryDutyId == null) {
                                    DomainObject systemMilitaryDuty = militaryServiceRelationStrategy.newInstance();
                                    Utils.setValue(systemMilitaryDuty.getAttribute(MilitaryServiceRelationStrategy.NAME),
                                            militaryDuty.getNkod());
                                    militaryServiceRelationStrategy.insert(systemMilitaryDuty, DateUtil.getCurrentDate());
                                    systemMilitaryDutyId = systemMilitaryDuty.getId();
                                }
                                militaryDuty.setSystemObjectId(systemMilitaryDutyId);
                            } catch (TooManyResultsException e) {
                                errorDescription = getString("military_duty_system_many_objects", militaryDuty.getId(),
                                        idjek, militaryDuty.getNkod());
                            }

                            militaryDuty.setProcessed(true);
                            referenceDataCorrectionBean.update(militaryDuty);

                            processingStatuses.get(item).increment();

                            if (errorDescription != null) {
                                wasErrors = true;
                                if (militaryDutyErrorFile == null) {
                                    militaryDutyErrorFile = getErrorFile(errorsDirectory, LegacyDataImportFile.MILITARY_DUTY);
                                    militaryDutyErrorFile.write(LegacyDataImportFile.MILITARY_DUTY.getCsvHeader());
                                    militaryDutyErrorFile.newLine();
                                }
                                if (militaryDutyErrorDescriptionFile == null) {
                                    militaryDutyErrorDescriptionFile = getErrorDescriptionFile(errorsDirectory,
                                            LegacyDataImportFile.MILITARY_DUTY);
                                }

                                militaryDutyErrorFile.write(militaryDuty.getContent());
                                militaryDutyErrorFile.newLine();

                                militaryDutyErrorDescriptionFile.write(errorDescription);
                                militaryDutyErrorDescriptionFile.newLine();
                            }
                        }

                        userTransaction.commit();
                        jekCount = referenceDataCorrectionBean.countForProcessing(entity, idjek);
                    }
                }
            } catch (Exception e) {
                try {
                    if (userTransaction.getStatus() != Status.STATUS_NO_TRANSACTION) {
                        userTransaction.rollback();
                    }
                } catch (Exception e1) {
                    log.error("Couldn't to rollback transaction.", e1);
                }

                throw new RuntimeException(e);
            } finally {
                if (militaryDutyErrorFile != null) {
                    try {
                        militaryDutyErrorFile.close();
                    } catch (IOException e) {
                        log.error("Couldn't to close file stream.", e);
                    }
                }
                if (militaryDutyErrorDescriptionFile != null) {
                    try {
                        militaryDutyErrorDescriptionFile.close();
                    } catch (IOException e) {
                        log.error("Couldn't to close file stream.", e);
                    }
                }
            }

            if (wasErrors) {
                messages.add(new ImportMessage(getString("fail_finish_military_duty_processing", count), WARN));
            } else {
                messages.add(new ImportMessage(getString("success_finish_military_duty_processing", count), INFO));
            }
            processingStatuses.get(item).finish();
        } catch (RuntimeException e) {
            throw e;
        } finally {
            try {
                userTransaction.begin();

                referenceDataCorrectionBean.clearProcessingStatus(entity, jekIds);

                userTransaction.commit();
            } catch (Exception e) {
                try {
                    if (userTransaction.getStatus() != Status.STATUS_NO_TRANSACTION) {
                        userTransaction.rollback();
                    }
                } catch (SystemException e1) {
                    log.error("Couldn't to rollback transaction.", e1);
                }
                log.error("Couldn't to clear processing status for military duties.", e);
            }
        }
    }

    private void processRegistrationsTypes() throws OpenErrorFileException, OpenErrorDescriptionFileException {
        final String entity = "registration_type";
        try {
            final ProcessItem item = ProcessItem.REGISTRATION_TYPE;

            BufferedWriter registrationTypeErrorFile = null;
            BufferedWriter registrationTypeErrorDescriptionFile = null;

            processingStatuses.put(item, new ImportStatus(0));
            final int count = referenceDataCorrectionBean.countForProcessing(entity, jekIds);
            messages.add(new ImportMessage(getString("begin_registration_type_processing", count), INFO));
            boolean wasErrors = false;

            try {
                for (String idjek : jekIds) {
                    int jekCount = referenceDataCorrectionBean.countForProcessing(entity, idjek);
                    while (jekCount > 0) {
                        List<ReferenceDataCorrection> registrationTypes =
                                referenceDataCorrectionBean.findForProcessing(entity, idjek, PROCESSING_BATCH);

                        userTransaction.begin();
                        for (ReferenceDataCorrection registrationType : registrationTypes) {
                            String errorDescription = null;

                            if (registrationType.getId() == ReferenceDataCorrectionBean.PERMANENT) { // постоянная регистрация
                                registrationType.setSystemObjectId(RegistrationTypeStrategy.PERMANENT);
                            } else {
                                try {
                                    Long systemRegistrationTypeId =
                                            referenceDataCorrectionBean.findSystemObject(entity, registrationType.getNkod());
                                    if (systemRegistrationTypeId == null) {
                                        DomainObject systemRegistrationType = registrationTypeStrategy.newInstance();
                                        Utils.setValue(systemRegistrationType.getAttribute(RegistrationTypeStrategy.NAME),
                                                registrationType.getNkod());
                                        registrationTypeStrategy.insert(systemRegistrationType, DateUtil.getCurrentDate());
                                        systemRegistrationTypeId = systemRegistrationType.getId();
                                    }
                                    registrationType.setSystemObjectId(systemRegistrationTypeId);
                                } catch (TooManyResultsException e) {
                                    errorDescription = getString("registration_type_system_many_objects", registrationType.getId(),
                                            idjek, registrationType.getNkod());
                                }
                            }

                            registrationType.setProcessed(true);
                            referenceDataCorrectionBean.update(registrationType);

                            processingStatuses.get(item).increment();

                            if (errorDescription != null) {
                                wasErrors = true;
                                if (registrationTypeErrorFile == null) {
                                    registrationTypeErrorFile = getErrorFile(errorsDirectory, LegacyDataImportFile.REGISTRATION_TYPE);
                                    registrationTypeErrorFile.write(LegacyDataImportFile.REGISTRATION_TYPE.getCsvHeader());
                                    registrationTypeErrorFile.newLine();
                                }
                                if (registrationTypeErrorDescriptionFile == null) {
                                    registrationTypeErrorDescriptionFile = getErrorDescriptionFile(errorsDirectory,
                                            LegacyDataImportFile.REGISTRATION_TYPE);
                                }

                                registrationTypeErrorFile.write(registrationType.getContent());
                                registrationTypeErrorFile.newLine();

                                registrationTypeErrorDescriptionFile.write(errorDescription);
                                registrationTypeErrorDescriptionFile.newLine();
                            }
                        }

                        userTransaction.commit();
                        jekCount = referenceDataCorrectionBean.countForProcessing(entity, idjek);
                    }
                }

                try {
                    referenceDataCorrectionBean.checkReservedRegistrationTypes(jekIds);
                    reservedRegistrationTypesResolved = true;
                } catch (ReferenceDataCorrectionBean.RegistrationTypeNotResolved e) {
                    wasErrors = true;

                    final String error = getString("reserved_registration_type_not_resolved",
                            registrationTypeStrategy.displayDomainObject(
                            registrationTypeStrategy.findById(RegistrationTypeStrategy.PERMANENT, true),
                            localeBean.getSystemLocale()),
                            jekIds.toString());

                    if (registrationTypeErrorDescriptionFile == null) {
                        registrationTypeErrorDescriptionFile = getErrorDescriptionFile(errorsDirectory,
                                LegacyDataImportFile.REGISTRATION_TYPE);
                    }
                    registrationTypeErrorDescriptionFile.write(error);
                    registrationTypeErrorDescriptionFile.newLine();

                    messages.add(new ImportMessage(error, WARN));
                }
            } catch (Exception e) {
                try {
                    if (userTransaction.getStatus() != Status.STATUS_NO_TRANSACTION) {
                        userTransaction.rollback();
                    }
                } catch (Exception e1) {
                    log.error("Couldn't to rollback transaction.", e1);
                }

                throw new RuntimeException(e);
            } finally {
                if (registrationTypeErrorFile != null) {
                    try {
                        registrationTypeErrorFile.close();
                    } catch (IOException e) {
                        log.error("Couldn't to close file stream.", e);
                    }
                }
                if (registrationTypeErrorDescriptionFile != null) {
                    try {
                        registrationTypeErrorDescriptionFile.close();
                    } catch (IOException e) {
                        log.error("Couldn't to close file stream.", e);
                    }
                }
            }

            if (wasErrors) {
                messages.add(new ImportMessage(getString("fail_finish_registration_type_processing", count), WARN));
            } else {
                messages.add(new ImportMessage(getString("success_finish_registration_type_processing", count), INFO));
            }
            processingStatuses.get(item).finish();
        } catch (RuntimeException e) {
            throw e;
        } finally {
            try {
                userTransaction.begin();

                referenceDataCorrectionBean.clearProcessingStatus(entity, jekIds);

                userTransaction.commit();
            } catch (Exception e) {
                try {
                    if (userTransaction.getStatus() != Status.STATUS_NO_TRANSACTION) {
                        userTransaction.rollback();
                    }
                } catch (SystemException e1) {
                    log.error("Couldn't to rollback transaction.", e1);
                }
                log.error("Couldn't to clear processing status for registration types.", e);
            }
        }
    }

    private void processDocumentsTypes() throws OpenErrorFileException, OpenErrorDescriptionFileException {
        final ProcessItem item = ProcessItem.DOCUMENT_TYPE;
        BufferedWriter documentTypeErrorDescriptionFile = null;

        try {
            userTransaction.begin();
            referenceDataCorrectionBean.putReservedDocumentTypes(jekIds);
            userTransaction.commit();

            try {
                referenceDataCorrectionBean.checkReservedDocumentTypes(jekIds);
                messages.add(new ImportMessage(getString("success_finish_document_type_processing"), INFO));
                reservedDocumentTypesResolved = true;
            } catch (ReferenceDataCorrectionBean.DocumentTypesNotResolved e) {
                StringBuilder sb = new StringBuilder();
                if (!e.isPassportResolved()) {
                    sb.append(documentTypeStrategy.displayDomainObject(
                            documentTypeStrategy.findById(DocumentTypeStrategy.PASSPORT, true),
                            localeBean.getSystemLocale())).
                            append(", ");
                }
                if (!e.isBirthCertificateResolved()) {
                    sb.append(documentTypeStrategy.displayDomainObject(
                            documentTypeStrategy.findById(DocumentTypeStrategy.BIRTH_CERTIFICATE, true),
                            localeBean.getSystemLocale())).
                            append(", ");
                }
                sb.delete(sb.length() - 2, sb.length());
                String error = getString("fail_finish_document_type_processing", sb.toString(), jekIds.toString());

                documentTypeErrorDescriptionFile = getErrorDescriptionFile(errorsDirectory, LegacyDataImportFile.DOCUMENT_TYPE);
                documentTypeErrorDescriptionFile.write(error);
                documentTypeErrorDescriptionFile.newLine();

                messages.add(new ImportMessage(error, WARN));
            }

            ImportStatus status = new ImportStatus(2);
            status.finish();
            processingStatuses.put(item, status);
        } catch (Exception e) {
            try {
                if (userTransaction.getStatus() != Status.STATUS_NO_TRANSACTION) {
                    userTransaction.rollback();
                }
            } catch (Exception e1) {
                log.error("Couldn't to rollback transaction.", e1);
            }
            throw new RuntimeException(e);
        } finally {
            if (documentTypeErrorDescriptionFile != null) {
                try {
                    documentTypeErrorDescriptionFile.close();
                } catch (IOException e) {
                    log.error("Couldn't to close file stream.", e);
                }
            }
        }
    }

    private void processOwnerTypes() throws OpenErrorFileException, OpenErrorDescriptionFileException {
        final ProcessItem item = ProcessItem.OWNER_TYPE;
        BufferedWriter ownerTypeErrorDescriptionFile = null;

        try {
            ownerType = referenceDataCorrectionBean.getReservedOwnerType(jekIds);
            if (!Strings.isEmpty(ownerType)) {
                messages.add(new ImportMessage(getString("success_finish_owner_type_processing"), INFO));
            } else {
                String error = getString("fail_finish_owner_type_processing", jekIds.toString());

                ownerTypeErrorDescriptionFile = getErrorDescriptionFile(errorsDirectory, LegacyDataImportFile.OWNER_TYPE);
                ownerTypeErrorDescriptionFile.write(error);
                ownerTypeErrorDescriptionFile.newLine();

                messages.add(new ImportMessage(error, WARN));
            }

            ImportStatus status = new ImportStatus(1);
            status.finish();
            processingStatuses.put(item, status);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (ownerTypeErrorDescriptionFile != null) {
                try {
                    ownerTypeErrorDescriptionFile.close();
                } catch (IOException e) {
                    log.error("Couldn't to close file stream.", e);
                }
            }
        }
    }

    private void processPersons() throws OpenErrorFileException, OpenErrorDescriptionFileException {
        if (!reservedDocumentTypesResolved) {
            return;
        }

        final Date creationDate = DateUtil.getCurrentDate();

        try {
            final ProcessItem item = ProcessItem.PERSON;

            BufferedWriter personErrorFile = null;
            BufferedWriter personErrorDescriptionFile = null;

            processingStatuses.put(item, new ImportStatus(0));
            final int count = personCorrectionBean.countForProcessing();
            final int archiveCount = personCorrectionBean.archiveCount();
            messages.add(new ImportMessage(getString("begin_person_processing", count), INFO));
            boolean wasErrors = false;

            try {
                int leftPersons = count;
                while (leftPersons > 0) {
                    List<PersonCorrection> persons = personCorrectionBean.findForProcessing(PROCESSING_BATCH);

                    userTransaction.begin();
                    for (PersonCorrection p : persons) {
                        String errorDescription = null;

                        if (p.getSystemPersonId() == null) {
                            if (!Strings.isEmpty(p.getFam()) && !Strings.isEmpty(p.getIm()) && !Strings.isEmpty(p.getOt())) {
                                try {
                                    Person systemPerson = personCorrectionBean.findSystemPerson(p);
                                    if (systemPerson != null) {
                                        p.setSystemPersonId(systemPerson.getId());
                                        p.setKid(systemPerson.isKid());
                                        errorDescription = getString("person_exists", p.getId());
                                    } else {
                                        if (PersonCorrectionBean.isBirthDateValid(p.getDatar())
                                                && PersonCorrectionBean.isDocumentDataValid(p.getIddok(), p.getDokseria(), p.getDoknom())) {

                                            Date birthDate = DateUtil.asDate(p.getDatar(), Utils.DATE_PATTERN);
                                            p.setKid(!DateUtil.isValidDateInterval(creationDate, birthDate, PersonStrategy.AGE_THRESHOLD));

                                            if (PersonCorrectionBean.isSupportedDocumentType(p.getIddok())) {

                                                //military service relation
                                                final String searchIdJek = jekIds.iterator().next();
                                                ReferenceDataCorrection militaryDity =
                                                        referenceDataCorrectionBean.getById("military_duty", p.getIdarm(), searchIdJek);
                                                if (militaryDity == null) {
                                                    errorDescription = getString("person_military_duty_not_found",
                                                            p.getId(), p.getIdarm(), searchIdJek);
                                                } else if (militaryDity.getSystemObjectId() == null) {
                                                    errorDescription = getString("person_military_duty_not_resolved",
                                                            p.getId(), p.getIdarm(), searchIdJek);
                                                }

                                                systemPerson = personCorrectionBean.newSystemPerson(p, birthDate,
                                                        militaryDity != null ? militaryDity.getSystemObjectId() : null);
                                                personStrategy.insert(systemPerson, creationDate);
                                                p.setSystemPersonId(systemPerson.getId());
                                            } else {
                                                errorDescription = getString("unsupported_document_type", p.getId(), p.getIddok());
                                            }
                                        } else {
                                            errorDescription = getString("invalid_person_data", p.getId(), p.getDatar(),
                                                    p.getIddok(), p.getDokseria(), p.getDoknom());
                                        }
                                    }
                                } catch (TooManyResultsException e) {
                                    errorDescription = getString("person_system_many_objects", p.getId(),
                                            p.getFam(), p.getIm(), p.getOt());
                                }
                            } else {
                                errorDescription = getString("invalid_person_fio", p.getId(), p.getFam(), p.getIm(),
                                        p.getOt());
                            }
                        } else {
                            errorDescription = getString("person_exists", p.getId());
                        }

                        p.setProcessed(true);
                        personCorrectionBean.update(p);

                        processingStatuses.get(item).increment();

                        if (errorDescription != null) {
                            wasErrors = true;
                            if (personErrorFile == null) {
                                personErrorFile = getErrorFile(errorsDirectory, LegacyDataImportFile.PERSON);
                                personErrorFile.write(LegacyDataImportFile.PERSON.getCsvHeader());
                                personErrorFile.newLine();
                            }
                            if (personErrorDescriptionFile == null) {
                                personErrorDescriptionFile = getErrorDescriptionFile(errorsDirectory,
                                        LegacyDataImportFile.PERSON);
                            }

                            personErrorFile.write(p.getContent());
                            personErrorFile.newLine();

                            personErrorDescriptionFile.write(errorDescription);
                            personErrorDescriptionFile.newLine();
                        }
                    }

                    userTransaction.commit();
                    leftPersons = personCorrectionBean.countForProcessing();
                }

                //children
                userTransaction.begin();
                personCorrectionBean.clearProcessingStatus();
                userTransaction.commit();

                List<PersonCorrection> children = personCorrectionBean.findChildren(PROCESSING_BATCH);
                while (!children.isEmpty()) {
                    userTransaction.begin();
                    for (PersonCorrection child : children) {
                        String errorDescription = null;
                        if (PersonCorrectionBean.isParentDataValid(child.getIdbud(), child.getKv(), child.getParentnom())) {
                            List<Long> systemParentIds = personCorrectionBean.findSystemParent(child.getIdbud(),
                                    child.getKv(), child.getParentnom());
                            if (systemParentIds.isEmpty()) {
                                errorDescription = getString("person_parent_not_found", child.getId(),
                                        child.getIdbud(), child.getKv(), child.getParentnom());
                            } else if (systemParentIds.size() > 1) {
                                errorDescription = getString("person_too_many_parents", child.getId(),
                                        child.getIdbud(), child.getKv(), child.getParentnom(), systemParentIds.toString());
                            } else {
                                personCorrectionBean.addChild(systemParentIds.get(0), child.getSystemPersonId(),
                                        child.getDatar(), creationDate);
                            }
                        } else {
                            errorDescription = getString("person_parent_data_invalid", child.getId(),
                                    child.getIdbud(), child.getKv(), child.getParentnom());
                        }

                        child.setProcessed(true);
                        personCorrectionBean.update(child);

                        if (errorDescription != null) {
                            wasErrors = true;
                            if (personErrorFile == null) {
                                personErrorFile = getErrorFile(errorsDirectory, LegacyDataImportFile.PERSON);
                                personErrorFile.write(LegacyDataImportFile.PERSON.getCsvHeader());
                                personErrorFile.newLine();
                            }
                            if (personErrorDescriptionFile == null) {
                                personErrorDescriptionFile = getErrorDescriptionFile(errorsDirectory,
                                        LegacyDataImportFile.PERSON);
                            }

                            personErrorFile.write(child.getContent());
                            personErrorFile.newLine();

                            personErrorDescriptionFile.write(errorDescription);
                            personErrorDescriptionFile.newLine();
                        }
                    }
                    userTransaction.commit();
                    children = personCorrectionBean.findChildren(PROCESSING_BATCH);
                }
            } catch (Exception e) {
                try {
                    if (userTransaction.getStatus() != Status.STATUS_NO_TRANSACTION) {
                        userTransaction.rollback();
                    }
                } catch (Exception e1) {
                    log.error("Couldn't to rollback transaction.", e1);
                }

                throw new RuntimeException(e);
            } finally {
                if (personErrorFile != null) {
                    try {
                        personErrorFile.close();
                    } catch (IOException e) {
                        log.error("Couldn't to close file stream.", e);
                    }
                }
                if (personErrorDescriptionFile != null) {
                    try {
                        personErrorDescriptionFile.close();
                    } catch (IOException e) {
                        log.error("Couldn't to close file stream.", e);
                    }
                }
            }

            if (wasErrors) {
                messages.add(new ImportMessage(getString("fail_finish_person_processing", count, archiveCount), WARN));
            } else {
                messages.add(new ImportMessage(getString("success_finish_person_processing", count, archiveCount), INFO));
            }
            processingStatuses.get(item).finish();
        } catch (RuntimeException e) {
            throw e;
        } finally {
            try {
                userTransaction.begin();

                personCorrectionBean.clearProcessingStatus();

                userTransaction.commit();
            } catch (Exception e) {
                try {
                    if (userTransaction.getStatus() != Status.STATUS_NO_TRANSACTION) {
                        userTransaction.rollback();
                    }
                } catch (SystemException e1) {
                    log.error("Couldn't to rollback transaction.", e1);
                }
                log.error("Couldn't to clear processing status for persons.", e);
            }
        }
    }

    private void processApartmentCards() throws OpenErrorFileException, OpenErrorDescriptionFileException {
        if (Strings.isEmpty(ownerType) || !reservedDocumentTypesResolved) {
            return;
        }

        try {
            final ProcessItem item = ProcessItem.APARTMENT_CARD;

            BufferedWriter apartmentCardErrorFile = null;
            BufferedWriter apartmentCardErrorDescriptionFile = null;

            processingStatuses.put(item, new ImportStatus(0));
            final int count = apartmentCardCorrectionBean.countForProcessing();
            final int archiveCount = apartmentCardCorrectionBean.archiveCount();
            messages.add(new ImportMessage(getString("begin_apartment_card_processing", count), INFO));
            boolean wasErrors = false;

            final Date creationDate = DateUtil.getCurrentDate();

            try {
                int leftCards = count;
                while (leftCards > 0) {
                    List<ApartmentCardCorrection> cards = apartmentCardCorrectionBean.findForProcessing(PROCESSING_BATCH);

                    userTransaction.begin();
                    for (ApartmentCardCorrection c : cards) {
                        String errorDescription = null;

                        if (c.getSystemApartmentCardId() == null) {

                            //building
                            if (!Strings.isEmpty(c.getIdbud())) {
                                BuildingCorrection building = null;
                                try {
                                    building = buildingCorrectionBean.findById(c.getIdbud(), jekIds);
                                    if (building != null) {
                                        final Long systemBuildingId = building.getSystemBuildingId();

                                        //apartment
                                        if (systemBuildingId != null) {
                                            if (!Strings.isEmpty(c.getKv())) {
                                                try {
                                                    Long systemApartmentId =
                                                            apartmentCardCorrectionBean.findSystemApartment(systemBuildingId, c.getKv());
                                                    if (systemApartmentId == null) {
                                                        //create new system apartment
                                                        DomainObject systemApartment =
                                                                apartmentCardCorrectionBean.newApartment(systemBuildingId, c.getKv(),
                                                                organizationMap.get(building.getIdjek()));
                                                        apartmentStrategy.insert(systemApartment, creationDate);
                                                        systemApartmentId = systemApartment.getId();
                                                    } else {
                                                        //system apartment already exists. Do nothing.
                                                    }

                                                    //apartment card itself
                                                    try {
                                                        Long systemApartmentCardId =
                                                                apartmentCardCorrectionBean.findSystemApartmentCard(c.getId(), systemApartmentId);
                                                        if (systemApartmentCardId == null) {
                                                            //create new system apartment card

                                                            //form of ownership: idprivat
                                                            if (!Strings.isEmpty(c.getIdprivat())) {
                                                                ReferenceDataCorrection ownershipForm =
                                                                        referenceDataCorrectionBean.getById("ownership_form", c.getIdprivat(), building.getIdjek());
                                                                if (ownershipForm == null) {
                                                                    errorDescription = getString("apartment_card_ownership_form_not_found",
                                                                            c.getId(), c.getIdprivat(), building.getIdjek());
                                                                } else if (ownershipForm.getSystemObjectId() == null) {
                                                                    errorDescription = getString("apartment_card_system_ownership_form_not_resolved",
                                                                            c.getId(), c.getIdprivat(), building.getIdjek());
                                                                } else {
                                                                    //owner.
                                                                    PersonCorrection owner =
                                                                            personCorrectionBean.getOwner(c.getIdbud(), c.getKv(), ownerType, c.getFio());
                                                                    if (owner == null) {
                                                                        errorDescription = getString("apartment_card_owner_not_resolved",
                                                                                c.getId(), c.getIdbud(), c.getKv(), ownerType);
                                                                    } else if (owner.getSystemPersonId() == null) {
                                                                        errorDescription = getString("apartment_card_system_owner_not_resolved",
                                                                                c.getId(), c.getIdbud(), c.getKv(), ownerType, owner.getId());
                                                                    } else {
                                                                        //system owner found. Now we can create new apartment card.
                                                                        ApartmentCard apartmentCard =
                                                                                apartmentCardCorrectionBean.newApartmentCard(
                                                                                c.getId(), systemApartmentId, owner.getSystemPersonId(),
                                                                                ownershipForm.getSystemObjectId(), organizationMap.get(building.getIdjek()));
                                                                        apartmentCardStrategy.insert(apartmentCard, creationDate);
                                                                        c.setSystemApartmentCardId(apartmentCard.getId());
                                                                    }
                                                                }
                                                            } else {
                                                                errorDescription = getString("apartment_card_ownership_form_not_found",
                                                                        c.getId(), c.getIdprivat(), building.getIdjek());
                                                            }
                                                        } else {
                                                            //system apartment card already exists. Do nothing.
                                                            errorDescription = getString("apartment_card_exists", c.getId());
                                                            c.setSystemApartmentCardId(systemApartmentCardId);
                                                        }
                                                    } catch (TooManyResultsException e) {
                                                        errorDescription = getString("apartment_card_too_many_system_apartment_cards",
                                                                c.getId(), c.getIdbud(), systemBuildingId, c.getKv(), systemApartmentId);
                                                    }
                                                } catch (TooManyResultsException e) {
                                                    errorDescription = getString("apartment_card_too_many_system_apartments",
                                                            c.getId(), c.getIdbud(), systemBuildingId, c.getKv());
                                                }
                                            } else {
                                                errorDescription = getString("apartment_card_apartment_empty", c.getId(),
                                                        c.getKv());
                                            }
                                        } else {
                                            errorDescription = getString("apartment_card_system_building_not_resolved", c.getId(),
                                                    c.getIdbud(), building.getIdjek());
                                        }
                                    } else {
                                        errorDescription = getString("apartment_card_building_not_found", c.getId(),
                                                c.getIdbud(), jekIds.toString());
                                    }
                                } catch (TooManyResultsException e) {
                                    errorDescription = getString("apartment_card_too_many_buildings", c.getId(),
                                            c.getIdbud(), jekIds.toString());
                                }
                            } else {
                                errorDescription = getString("apartment_card_building_empty", c.getId(),
                                        c.getIdbud());
                            }
                        } else {
                            //system apartment card already exists. Do nothing.
                            errorDescription = getString("apartment_card_exists", c.getId());
                        }

                        c.setProcessed(true);
                        apartmentCardCorrectionBean.update(c);

                        processingStatuses.get(item).increment();

                        if (errorDescription != null) {
                            wasErrors = true;
                            if (apartmentCardErrorFile == null) {
                                apartmentCardErrorFile = getErrorFile(errorsDirectory, LegacyDataImportFile.APARTMENT_CARD);
                                apartmentCardErrorFile.write(LegacyDataImportFile.APARTMENT_CARD.getCsvHeader());
                                apartmentCardErrorFile.newLine();
                            }
                            if (apartmentCardErrorDescriptionFile == null) {
                                apartmentCardErrorDescriptionFile = getErrorDescriptionFile(errorsDirectory,
                                        LegacyDataImportFile.APARTMENT_CARD);
                            }

                            apartmentCardErrorFile.write(c.getContent());
                            apartmentCardErrorFile.newLine();

                            apartmentCardErrorDescriptionFile.write(errorDescription);
                            apartmentCardErrorDescriptionFile.newLine();
                        }
                    }

                    userTransaction.commit();
                    leftCards = apartmentCardCorrectionBean.countForProcessing();
                }
            } catch (Exception e) {
                try {
                    if (userTransaction.getStatus() != Status.STATUS_NO_TRANSACTION) {
                        userTransaction.rollback();
                    }
                } catch (Exception e1) {
                    log.error("Couldn't to rollback transaction.", e1);
                }

                throw new RuntimeException(e);
            } finally {
                if (apartmentCardErrorFile != null) {
                    try {
                        apartmentCardErrorFile.close();
                    } catch (IOException e) {
                        log.error("Couldn't to close file stream.", e);
                    }
                }
                if (apartmentCardErrorDescriptionFile != null) {
                    try {
                        apartmentCardErrorDescriptionFile.close();
                    } catch (IOException e) {
                        log.error("Couldn't to close file stream.", e);
                    }
                }
            }

            if (wasErrors) {
                messages.add(new ImportMessage(getString("fail_finish_apartment_card_processing", count, archiveCount), WARN));
            } else {
                messages.add(new ImportMessage(getString("success_finish_apartment_card_processing", count, archiveCount), INFO));
            }
            processingStatuses.get(item).finish();
        } catch (RuntimeException e) {
            throw e;
        } finally {
            try {
                userTransaction.begin();

                apartmentCardCorrectionBean.clearProcessingStatus();

                userTransaction.commit();
            } catch (Exception e) {
                try {
                    userTransaction.rollback();
                } catch (SystemException e1) {
                    log.error("Couldn't to rollback transaction.", e1);
                }
                log.error("Couldn't to clear processing status for apartment cards.", e);
            }
        }
    }

    private void processRegistrations() throws OpenErrorFileException, OpenErrorDescriptionFileException {
        if (Strings.isEmpty(ownerType) || !reservedDocumentTypesResolved
                || !reservedOwnerRelationshipResolved || !reservedRegistrationTypesResolved) {
            return;
        }

        try {
            final ProcessItem item = ProcessItem.REGISTRATION;

            BufferedWriter registrationErrorFile = null;
            BufferedWriter registrationErrorDescriptionFile = null;

            processingStatuses.put(item, new ImportStatus(0));
            final int count = registrationCorrectionBean.countForProcessing();
            messages.add(new ImportMessage(getString("begin_registration_processing", count), INFO));
            boolean wasErrors = false;

            final Date creationDate = DateUtil.getCurrentDate();

            try {
                int leftCards = count;
                while (leftCards > 0) {
                    List<ApartmentCardCorrection> cards = registrationCorrectionBean.findApartmentCardsForProcessing(PROCESSING_BATCH);

                    for (ApartmentCardCorrection c : cards) {
                        List<String> errorDescriptions = Lists.newArrayList();

                        List<PersonCorrection> persons = personCorrectionBean.findByAddress(c.getIdbud(), c.getKv());
                        for (PersonCorrection p : persons) {
                            if (p.getSystemRegistrationId() == null) {
                                if (p.getSystemPersonId() == null) {
                                    errorDescriptions.add(getString("registration_system_person_not_resolved", p.getId()));
                                } else {
                                    ApartmentCard systemApartmentCard = apartmentCardStrategy.findById(c.getSystemApartmentCardId(),
                                            true, true, true, false);

                                    boolean exists = false;

                                    Date departureDate = DateUtil.asDate(p.getVdata(), Utils.DATE_PATTERN);
                                    boolean isFinishedRegistration = departureDate != null;
                                    for (Registration r : systemApartmentCard.getRegistrations()) {
                                        if (p.getSystemPersonId().equals(r.getPerson().getId())
                                                && ((isFinishedRegistration && r.isFinished())
                                                || (!isFinishedRegistration && !r.isFinished()))) {
                                            exists = true;
                                            break;
                                        }
                                    }

                                    if (exists) {
                                        errorDescriptions.add(getString("registration_exists", p.getId()));
                                    } else {
                                        //create new system registration

                                        Long systemRegistrationId = null;

                                        //registration date
                                        final Date registrationDate = DateUtil.asDate(p.getDprop(), Utils.DATE_PATTERN);
                                        if (registrationDate != null) {
                                            //owner relationship

                                            //at first find jek id:
                                            BuildingCorrection building = null;
                                            try {
                                                building = buildingCorrectionBean.findById(c.getIdbud(), jekIds);
                                            } catch (TooManyResultsException e) {
                                                //impossible case because we handle apartment cards that already 
                                                // were processed by processApartmentCards() and that processing 
                                                // includes search the building and check that there only one building found.
                                            }
                                            ReferenceDataCorrection ownerRelationship =
                                                    referenceDataCorrectionBean.getById("owner_relationship", p.getIdrel(), building.getIdjek());
                                            if (ownerRelationship == null) {
                                                errorDescriptions.add(getString("registration_owner_relationship_not_found",
                                                        p.getId(), p.getIdrel(), building.getIdjek()));
                                            } else if (ownerRelationship.getSystemObjectId() == null) {
                                                errorDescriptions.add(getString("registration_system_owner_relationship_not_resolved",
                                                        p.getId(), p.getIdrel(), building.getIdjek()));
                                            } else {
                                                //registration type
                                                ReferenceDataCorrection registrationType =
                                                        referenceDataCorrectionBean.getById("registration_type", p.getIdvidp(), building.getIdjek());
                                                if (registrationType == null) {
                                                    errorDescriptions.add(getString("registration_registration_type_not_found",
                                                            p.getId(), p.getIdrel(), building.getIdjek()));
                                                } else if (registrationType.getSystemObjectId() == null) {
                                                    errorDescriptions.add(getString("registration_system_registration_type_not_resolved",
                                                            p.getId(), p.getIdvidp(), building.getIdjek()));
                                                } else {
                                                    final long systemRegistrationTypeId = registrationType.getSystemObjectId();

                                                    //arrival street
                                                    String arrivalStreet = null;
                                                    StreetCorrection arrivalStreetCorrection =
                                                            streetCorrectionBean.getById(p.getPidul(), building.getIdjek());
                                                    if (arrivalStreetCorrection != null) {
                                                        //at first try to take ukrainian street name
                                                        if (!Strings.isEmpty(arrivalStreetCorrection.getNkod())) {
                                                            arrivalStreet = arrivalStreetCorrection.getUtype()
                                                                    + " " + arrivalStreetCorrection.getNkod();
                                                        } else {
                                                            arrivalStreet = arrivalStreetCorrection.getRtype()
                                                                    + " " + arrivalStreetCorrection.getNkod1();
                                                        }
                                                        if (Strings.isEmpty(arrivalStreet)) {
                                                            errorDescriptions.add(getString("registration_arrival_street_empty",
                                                                    p.getId(), p.getPidul(),
                                                                    arrivalStreetCorrection.getUtype() + " " + arrivalStreetCorrection.getNkod(),
                                                                    arrivalStreetCorrection.getRtype() + " " + arrivalStreetCorrection.getNkod1(),
                                                                    building.getIdjek()));
                                                        }
                                                    } else {
                                                        errorDescriptions.add(getString("registration_arrival_street_not_found",
                                                                p.getId(), p.getPidul(), building.getIdjek()));
                                                    }

                                                    //arrival date
                                                    Date arrivalDate1 = DateUtil.asDate(p.getPdpribza(), Utils.DATE_PATTERN);
                                                    Date arrivalDate2 = DateUtil.asDate(p.getPdpribvm(), Utils.DATE_PATTERN);
                                                    Date arrivalDate = DateUtil.getMax(arrivalDate1, arrivalDate2);
                                                    if (arrivalDate == null) {
                                                        errorDescriptions.add(getString("registration_arrival_date_invalid",
                                                                p.getId(), p.getPdpribza(), p.getPdpribvm(), building.getIdjek()));
                                                    }

                                                    String departureStreet = null;
                                                    String departureReason = null;
                                                    if (isFinishedRegistration) {
                                                        //departure street
                                                        StreetCorrection departureStreetCorrection =
                                                                streetCorrectionBean.getById(p.getVidul(), building.getIdjek());
                                                        if (departureStreetCorrection != null) {
                                                            //at first try to take ukrainian street name
                                                            if (!Strings.isEmpty(departureStreetCorrection.getNkod())) {
                                                                departureStreet = departureStreetCorrection.getUtype()
                                                                        + " " + departureStreetCorrection.getNkod();
                                                            } else {
                                                                departureStreet = departureStreetCorrection.getRtype()
                                                                        + " " + departureStreetCorrection.getNkod1();
                                                            }
                                                            if (Strings.isEmpty(departureStreet)) {
                                                                errorDescriptions.add(getString("registration_departure_street_empty",
                                                                        p.getId(), p.getVidul(),
                                                                        departureStreetCorrection.getUtype() + " " + departureStreetCorrection.getNkod(),
                                                                        departureStreetCorrection.getRtype() + " " + departureStreetCorrection.getNkod1(),
                                                                        building.getIdjek()));
                                                            }
                                                        } else {
                                                            errorDescriptions.add(getString("registration_departure_street_not_found",
                                                                    p.getId(), p.getVidul(), building.getIdjek()));
                                                        }

                                                        //departure reason
                                                        ReferenceDataCorrection departureReasonCorrection =
                                                                referenceDataCorrectionBean.getById("departure_reason", p.getIdvip(), building.getIdjek());
                                                        if (departureReasonCorrection != null) {
                                                            departureReason = departureReasonCorrection.getNkod();
                                                            if (Strings.isEmpty(departureReason)) {
                                                                errorDescriptions.add(getString("registration_departure_reason_empty",
                                                                        p.getId(), p.getIdvip(), building.getIdjek()));
                                                            }
                                                        } else {
                                                            errorDescriptions.add(getString("registration_departure_reason_not_found",
                                                                    p.getId(), p.getIdvip(), building.getIdjek()));
                                                        }

                                                        //departure date
                                                        if (departureDate == null) {
                                                            errorDescriptions.add(getString("registration_departure_date_invalid",
                                                                    p.getId(), p.getVdata(), building.getIdjek()));
                                                        }
                                                    }

                                                    //now we can create new registration.
                                                    Registration registration =
                                                            registrationCorrectionBean.newRegistration(p, isFinishedRegistration,
                                                            p.getSystemPersonId(), ownerRelationship.getSystemObjectId(),
                                                            systemRegistrationTypeId, registrationDate,
                                                            arrivalDate, arrivalStreet,
                                                            departureDate, departureStreet, departureReason);

                                                    if (!isFinishedRegistration) {
                                                        //need validation

                                                        boolean isValid = true;
                                                        {
                                                            //registration date must be greater than person's birth date
                                                            if (registration.getPerson().getBirthDate().after(registration.getRegistrationDate())) {
                                                                isValid = false;
                                                                errorDescriptions.add(getString("registration_date_validation_error",
                                                                        p.getId(), Utils.displayDate(registrationDate),
                                                                        Utils.displayDate(registration.getPerson().getBirthDate())));
                                                            }

                                                            //permanent registration type
                                                            if (systemRegistrationTypeId == RegistrationTypeStrategy.PERMANENT) {
                                                                String address = personStrategy.findPermanentRegistrationAddress(registration.getPerson().getId(),
                                                                        localeBean.getSystemLocale());
                                                                if (!Strings.isEmpty(address)) {
                                                                    isValid = false;
                                                                    errorDescriptions.add(getString("registration_permanent_registration_type_validation_error",
                                                                            p.getId(), address));
                                                                }
                                                            }

                                                            //duplicate person registration check
                                                            if (!registrationStrategy.validateDuplicatePerson(systemApartmentCard.getId(), registration.getPerson().getId())) {
                                                                isValid = false;
                                                                errorDescriptions.add(getString("registration_person_already_registered_validation_error",
                                                                        p.getId()));
                                                            }
                                                        }
                                                        if (isValid) {
                                                            systemRegistrationId =
                                                                    registrationCorrectionBean.addRegistration(systemApartmentCard, registration, creationDate);
                                                        }
                                                    } else { // validation not needed.
                                                        userTransaction.begin();
                                                        systemRegistrationId =
                                                                registrationCorrectionBean.addRegistration(systemApartmentCard, registration, creationDate);
                                                        registrationStrategy.disable(registration, creationDate);
                                                        userTransaction.commit();
                                                    }
                                                }
                                            }
                                        } else {
                                            errorDescriptions.add(getString("registration_date_invalid", p.getId(), p.getDprop()));
                                        }

                                        if (systemRegistrationId != null) {
                                            p.setSystemRegistrationId(systemRegistrationId);
                                            userTransaction.begin();
                                            registrationCorrectionBean.updatePerson(p);
                                            userTransaction.commit();
                                        }
                                    }
                                }
                            } else {
                                errorDescriptions.add(getString("registration_exists", p.getId()));
                            }
                        }

                        c.setProcessed(true);
                        userTransaction.begin();
                        registrationCorrectionBean.updateApartmentCard(c);
                        userTransaction.commit();

                        processingStatuses.get(item).increment();

                        if (!errorDescriptions.isEmpty()) {
                            wasErrors = true;
                            if (registrationErrorFile == null) {
                                registrationErrorFile = getErrorFile(errorsDirectory, RegistrationCorrectionBean.REGISTRATION_FILE_NAME);
                                registrationErrorFile.write(RegistrationCorrectionBean.REGISTRATION_FILE_HEADER);
                                registrationErrorFile.newLine();
                            }
                            if (registrationErrorDescriptionFile == null) {
                                registrationErrorDescriptionFile = getErrorDescriptionFile(errorsDirectory,
                                        RegistrationCorrectionBean.REGISTRATION_FILE_NAME);
                            }

                            registrationErrorFile.write(c.getContent());
                            registrationErrorFile.newLine();

                            for (String error : errorDescriptions) {
                                registrationErrorDescriptionFile.write(error);
                                registrationErrorDescriptionFile.newLine();
                            }
                        }
                    }

                    leftCards = registrationCorrectionBean.countForProcessing();
                }
            } catch (Exception e) {
                try {
                    if (userTransaction.getStatus() != Status.STATUS_NO_TRANSACTION) {
                        userTransaction.rollback();
                    }
                } catch (Exception e1) {
                    log.error("Couldn't to rollback transaction.", e1);
                }

                throw new RuntimeException(e);
            } finally {
                if (registrationErrorFile != null) {
                    try {
                        registrationErrorFile.close();
                    } catch (IOException e) {
                        log.error("Couldn't to close file stream.", e);
                    }
                }
                if (registrationErrorDescriptionFile != null) {
                    try {
                        registrationErrorDescriptionFile.close();
                    } catch (IOException e) {
                        log.error("Couldn't to close file stream.", e);
                    }
                }
            }

            if (wasErrors) {
                messages.add(new ImportMessage(getString("fail_finish_registration_processing", count), WARN));
            } else {
                messages.add(new ImportMessage(getString("success_finish_registration_processing", count), INFO));
            }
            processingStatuses.get(item).finish();
        } catch (RuntimeException e) {
            throw e;
        } finally {
            try {
                userTransaction.begin();

                apartmentCardCorrectionBean.clearProcessingStatus();

                userTransaction.commit();
            } catch (Exception e) {
                try {
                    userTransaction.rollback();
                } catch (SystemException e1) {
                    log.error("Couldn't to rollback transaction.", e1);
                }
                log.error("Couldn't to clear processing status for registrations.", e);
            }
        }
    }

    private String getString(String key, Object... parameters) {
        return ResourceUtil.getFormatString(RESOURCE_BUNDLE, key, locale, parameters);
    }

    private static String getContent(String[] line) {
        StringBuilder content = new StringBuilder();
        boolean firstLine = true;
        for (String field : line) {
            if (!firstLine) {
                content.append(SEPARATOR);
            } else {
                firstLine = false;
            }
            content.append(field);
        }
        return content.toString();
    }

    public void cleanData(Set<String> jekIds) {
        try {
            clean();

            userTransaction.begin();

            streetCorrectionBean.cleanData(jekIds);
            buildingCorrectionBean.cleanData(jekIds);
            referenceDataCorrectionBean.cleanData("ownership_form", jekIds);
            referenceDataCorrectionBean.cleanData("military_duty", jekIds);
            referenceDataCorrectionBean.cleanData("owner_relationship", jekIds);
            referenceDataCorrectionBean.cleanData("departure_reason", jekIds);
            referenceDataCorrectionBean.cleanData("registration_type", jekIds);
            referenceDataCorrectionBean.cleanData("document_type", jekIds);
            referenceDataCorrectionBean.cleanData("owner_type", jekIds);
            personCorrectionBean.cleanData();
            apartmentCardCorrectionBean.cleanData();

            userTransaction.commit();
        } catch (Exception e) {
            try {
                userTransaction.rollback();
            } catch (SystemException e1) {
                log.error("Couldn't to rollback transaction.", e1);
            }
            throw new RuntimeException(e);
        }
    }

    private int getRecordCount(String dir, LegacyDataImportFile file) throws ImportFileNotFoundException, ImportFileReadException {
        return ImportStorageUtil.getRecordCount(dir, file);
    }

    private CSVReader getCsvReader(String dir, LegacyDataImportFile file, String charsetName, char separator) throws ImportFileNotFoundException {
        try {
            return new CSVReader(new InputStreamReader(new FileInputStream(
                    new File(dir, file.getFileName())), charsetName), separator, CSVWriter.NO_QUOTE_CHARACTER, 1);
        } catch (Exception e) {
            throw new ImportFileNotFoundException(e, file.getFileName());
        }
    }

    private BufferedWriter getErrorFile(String dir, LegacyDataImportFile file) throws OpenErrorFileException {
        return getErrorFile(dir, file.getFileName());
    }

    private BufferedWriter getErrorFile(String dir, String fileName) throws OpenErrorFileException {
        String name = fileName.substring(0, fileName.lastIndexOf(".")) + ERROR_FILE_SUFFIX;
        try {
            return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(dir, name)), CHARSET));
        } catch (Exception e) {
            throw new OpenErrorFileException(e, name);
        }
    }

    private BufferedWriter getErrorDescriptionFile(String dir, LegacyDataImportFile file) throws OpenErrorDescriptionFileException {
        return getErrorDescriptionFile(dir, file.getFileName());
    }

    private BufferedWriter getErrorDescriptionFile(String dir, String fileName) throws OpenErrorDescriptionFileException {
        String name = fileName.substring(0, fileName.lastIndexOf(".")) + ERROR_DESCRIPTION_FILE_SUFFIX;
        try {
            return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(dir, name)), CHARSET));
        } catch (Exception e) {
            throw new OpenErrorDescriptionFileException(e, name);
        }
    }
}
