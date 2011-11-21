/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.imp.service;

import org.complitex.dictionary.util.DateUtil;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import org.complitex.pspoffice.imp.entity.ImportStatus;
import org.complitex.dictionary.util.ImportStorageUtil;
import java.io.IOException;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.collect.ImmutableMap;
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
import javax.annotation.PostConstruct;
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
import org.complitex.address.strategy.building.BuildingStrategy;
import org.complitex.address.strategy.building.entity.Building;
import org.complitex.address.strategy.building_address.BuildingAddressStrategy;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.service.ConfigBean;
import org.complitex.dictionary.service.LocaleBean;
import org.complitex.dictionary.service.exception.AbstractException;
import org.complitex.dictionary.service.exception.ImportCriticalException;
import org.complitex.dictionary.service.exception.ImportFileNotFoundException;
import org.complitex.dictionary.service.exception.ImportFileReadException;
import org.complitex.dictionary.util.ResourceUtil;
import org.complitex.pspoffice.imp.entity.BuildingCorrection;
import org.complitex.pspoffice.imp.entity.ImportMessage;
import org.complitex.pspoffice.imp.entity.ProcessItem;
import org.complitex.pspoffice.imp.entity.PspImportFile;
import org.complitex.pspoffice.imp.entity.StreetCorrection;
import org.complitex.pspoffice.imp.service.exception.OpenErrorDescriptionFileException;
import org.complitex.pspoffice.imp.service.exception.OpenErrorFileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.complitex.pspoffice.imp.entity.PspImportFile.*;
import static org.complitex.pspoffice.imp.entity.ProcessItem.*;
import static org.complitex.pspoffice.imp.entity.ImportMessage.ImportMessageLevel.*;

/**
 *
 * @author Artem
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@TransactionManagement(TransactionManagementType.BEAN)
public class PspImportService {

    private static final Logger log = LoggerFactory.getLogger(PspImportService.class);
    private static final String RESOURCE_BUNDLE = PspImportService.class.getName();
    private static final char SEPARATOR = '\t';
    private static final String CHARSET = "UTF-8";
    private static final String ERROR_FILE_SUFFIX = "_errors.csv";
    private static final String ERROR_DESCRIPTION_FILE_SUFFIX = "_errors_description.txt";
    private static final int PROCESSING_BATCH = 100;
    private static final Map<PspImportFile, String> CSV_FILE_HEADERS = ImmutableMap.of(
            STREET, "ID	UTYPE	NKOD	RTYPE	NKOD1",
            BUILDING, "ID	IDJEK	IDUL	DOM	KORPUS	NKOD	ETAG	BEGLS	ENDLS	DILN	TIPB	NDOP	UTYPE	UL");
    @Resource
    private UserTransaction userTransaction;
    @EJB
    private ConfigBean configBean;
    @EJB
    private StreetCorrectionBean streetCorrectionBean;
    @EJB
    private BuildingCorrectionBean buildingCorrectionBean;
    @EJB
    private BuildingStrategy buildingStrategy;
    @EJB
    private LocaleBean localeBean;
    private long SYSTEM_LOCALE_ID;
    private boolean processing;
    private Locale locale;
    private Long cityId;
    private Set<String> jekIds;
    private String importDirectory;
    private String errorsDirectory;
    private Map<PspImportFile, ImportStatus> loadingStatuses = new EnumMap<PspImportFile, ImportStatus>(PspImportFile.class);
    private Map<ProcessItem, ImportStatus> processingStatuses = new EnumMap<ProcessItem, ImportStatus>(ProcessItem.class);
    private Queue<ImportMessage> messages = new ConcurrentLinkedQueue<ImportMessage>();

    @PostConstruct
    private void init() {
        this.SYSTEM_LOCALE_ID = localeBean.getSystemLocaleObject().getId();
    }

    public boolean isProcessing() {
        return processing;
    }

    public ImportStatus getLoadingStatus(PspImportFile importFile) {
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
        locale = null;
        jekIds = null;
        importDirectory = null;
        errorsDirectory = null;
    }

    @Asynchronous
    public void startImport(long cityId, Set<String> jekIds, String importDirectiry, String errorsDirectory, Locale locale) {
        if (processing) {
            return;
        }

        clean();
        processing = true;

        this.cityId = cityId;
        this.jekIds = jekIds;
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
    }

    /**
     * id    Street type string(ukr)     Street name(ukr)     Street type string(rus)     Street name(rus)
     */
    private void loadStreets() throws ImportFileReadException, ImportFileNotFoundException {
        final PspImportFile file = STREET;

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
        final PspImportFile file = BUILDING;

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

    private void processFiles() throws OpenErrorFileException, OpenErrorDescriptionFileException {
        processStreetsAndBuildings();
    }

    private void processStreetsAndBuildings() throws OpenErrorFileException, OpenErrorDescriptionFileException {
        try {
            final ProcessItem item = STREET_BUILDING;

            BufferedWriter streetErrorFile = null;
            BufferedWriter streetErrorDescriptionFile = null;
            BufferedWriter buildingErrorFile = null;
            BufferedWriter buildingErrorDescriptionFile = null;

            processingStatuses.put(item, new ImportStatus(0));
            final int count = buildingCorrectionBean.countForProcessing(jekIds);
            messages.add(new ImportMessage(getString("begin_street_building_processing", count), INFO));
            boolean wasErrors = false;

            if (count > 0) {
                for (String idjek : jekIds) {
                    try {
                        List<BuildingCorrection> buildings = buildingCorrectionBean.findForProcessing(idjek, PROCESSING_BATCH);
                        for (BuildingCorrection building : buildings) {
                            if (building.getSystemBuildingId() == null) {
                                userTransaction.begin();

                                String buildingErrorDescription = null;
                                String streetErrorDescription = null;

                                String idul = building.getIdul();
                                final StreetCorrection street = streetCorrectionBean.findById(idul, idjek);
                                if (street == null) {
                                    buildingErrorDescription = getString("buiding_invalid_street_id",
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

                                        Long systemBuildingId = buildingCorrectionBean.findSystemBuilding(systemStreetId, dom, korpus);
                                        if (systemBuildingId == null) {
                                            Building systemBuilding = buildingStrategy.newInstance();
                                            DomainObject systemBuildingAddress = systemBuilding.getPrimaryAddress();

                                            systemBuildingAddress.setParentEntityId(BuildingAddressStrategy.PARENT_STREET_ENTITY_ID);
                                            systemBuildingAddress.setParentId(systemStreetId);

                                            AttributeUtil.setValue(systemBuildingAddress.getAttribute(BuildingAddressStrategy.NUMBER),
                                                    SYSTEM_LOCALE_ID, dom);
                                            AttributeUtil.setValue(systemBuildingAddress.getAttribute(BuildingAddressStrategy.CORP),
                                                    SYSTEM_LOCALE_ID, korpus);

                                            buildingStrategy.insert(systemBuilding, DateUtil.getCurrentDate());
                                        } else {
                                            building.setSystemBuildingId(systemBuildingId);
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
                                        streetErrorFile = getErrorFile(errorsDirectory, STREET);
                                        streetErrorFile.write(CSV_FILE_HEADERS.get(STREET));
                                        streetErrorFile.newLine();
                                    }
                                    if (streetErrorDescriptionFile == null) {
                                        streetErrorDescriptionFile = getErrorDescriptionFile(errorsDirectory, STREET);
                                    }

                                    streetErrorFile.write(street.getContent());
                                    streetErrorFile.newLine();

                                    streetErrorDescriptionFile.write(streetErrorDescription);
                                    streetErrorDescriptionFile.newLine();
                                }

                                if (buildingErrorDescription != null) {
                                    wasErrors = true;
                                    if (buildingErrorFile == null) {
                                        buildingErrorFile = getErrorFile(errorsDirectory, BUILDING);
                                        buildingErrorFile.write(CSV_FILE_HEADERS.get(BUILDING));
                                        buildingErrorFile.newLine();
                                    }
                                    if (buildingErrorDescriptionFile == null) {
                                        buildingErrorDescriptionFile = getErrorDescriptionFile(errorsDirectory, BUILDING);
                                    }

                                    buildingErrorFile.write(building.getContent());
                                    buildingErrorFile.newLine();

                                    buildingErrorDescriptionFile.write(buildingErrorDescription);
                                    buildingErrorDescriptionFile.newLine();
                                }
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
                }
            }

            if (wasErrors) {
                messages.add(new ImportMessage(getString("fail_finish_street_building_processing", count), WARN));
            } else {
                messages.add(new ImportMessage(getString("finish_street_building_processing", count), INFO));
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
                    userTransaction.rollback();
                } catch (SystemException e1) {
                    log.error("Couldn't to rollback transaction.", e1);
                }
                log.error("Couldn't to clear processing status for streets and buildings.", e);
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

    private int getRecordCount(String dir, PspImportFile file) throws ImportFileNotFoundException, ImportFileReadException {
        return ImportStorageUtil.getRecordCount(dir, file);
    }

    private CSVReader getCsvReader(String dir, PspImportFile file, String charsetName, char separator) throws ImportFileNotFoundException {
        try {
            return new CSVReader(new InputStreamReader(new FileInputStream(
                    new File(dir, file.getFileName())), charsetName), separator, CSVWriter.NO_QUOTE_CHARACTER, 1);
        } catch (Exception e) {
            throw new ImportFileNotFoundException(e, file.getFileName());
        }
    }

    private BufferedWriter getErrorFile(String dir, PspImportFile file) throws OpenErrorFileException {
        String name = file.getFileName().substring(0, file.getFileName().lastIndexOf(".")) + ERROR_FILE_SUFFIX;
        try {
            return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(dir, name)), CHARSET));
        } catch (Exception e) {
            throw new OpenErrorFileException(e, name);
        }
    }

    private BufferedWriter getErrorDescriptionFile(String dir, PspImportFile file) throws OpenErrorDescriptionFileException {
        String name = file.getFileName().substring(0, file.getFileName().lastIndexOf(".")) + ERROR_DESCRIPTION_FILE_SUFFIX;
        try {
            return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(dir, name)), CHARSET));
        } catch (Exception e) {
            throw new OpenErrorDescriptionFileException(e, name);
        }
    }
}
