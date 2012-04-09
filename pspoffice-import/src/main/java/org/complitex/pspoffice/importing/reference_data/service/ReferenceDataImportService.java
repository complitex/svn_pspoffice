/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.importing.reference_data.service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.UserTransaction;
import org.complitex.address.entity.AddressImportFile;
import org.complitex.address.service.AddressImportService;
import org.complitex.dictionary.entity.DictionaryConfig;
import org.complitex.dictionary.entity.IImportFile;
import org.complitex.dictionary.entity.ImportMessage;
import org.complitex.dictionary.entity.Log;
import org.complitex.dictionary.service.ConfigBean;
import org.complitex.dictionary.service.IImportListener;
import org.complitex.dictionary.service.LogBean;
import org.complitex.dictionary.service.exception.AbstractException;
import org.complitex.dictionary.service.exception.ImportCriticalException;
import org.complitex.dictionary.service.exception.ImportFileNotFoundException;
import org.complitex.dictionary.service.exception.ImportFileReadException;
import org.complitex.dictionary.service.exception.ImportObjectLinkException;
import org.complitex.pspoffice.departure_reason.entity.DepartureReasonImportFile;
import org.complitex.pspoffice.departure_reason.service.DepartureReasonImportService;
import org.complitex.pspoffice.document_type.entity.DocumentTypeImportFile;
import org.complitex.pspoffice.document_type.service.DocumentTypeImportService;
import org.complitex.pspoffice.housing_rights.entity.HousingRightsImportFile;
import org.complitex.pspoffice.housing_rights.service.HousingRightsImportService;
import org.complitex.pspoffice.military.entity.MilitaryServiceRelationImportFile;
import org.complitex.pspoffice.military.service.MilitaryServiceRelationImportService;
import org.complitex.pspoffice.ownerrelationship.entity.OwnerRelationshipImportFile;
import org.complitex.pspoffice.ownerrelationship.service.OwnerRelationshipImportService;
import org.complitex.pspoffice.ownership.entity.OwnershipFormImportFile;
import org.complitex.pspoffice.ownership.service.OwnershipFormImportService;
import org.complitex.pspoffice.person.Module;
import org.complitex.pspoffice.registration_type.entity.RegistrationTypeImportFile;
import org.complitex.pspoffice.registration_type.service.RegistrationTypeImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@TransactionManagement(TransactionManagementType.BEAN)
public class ReferenceDataImportService {

    private static final Logger log = LoggerFactory.getLogger(ReferenceDataImportService.class);
    @Resource
    private UserTransaction userTransaction;
    @EJB
    private AddressImportService addressImportService;
    @EJB
    private OwnerRelationshipImportService ownerRelationshipImportService;
    @EJB
    private OwnershipFormImportService ownershipFormImportService;
    @EJB
    private RegistrationTypeImportService registrationTypeImportService;
    @EJB
    private DocumentTypeImportService documentTypeImportService;
    @EJB
    private MilitaryServiceRelationImportService militaryServiceRelationImportService;
    @EJB
    private DepartureReasonImportService departureReasonImportService;
    @EJB
    private HousingRightsImportService housingRightsImportService;
    @EJB
    private ConfigBean configBean;
    @EJB
    private LogBean logBean;
    private volatile boolean processing;
    private volatile boolean error;
    private volatile boolean success;
    private volatile String errorMessage;
    private final Map<IImportFile, ImportMessage> referenceDataMap =
            Collections.synchronizedMap(new LinkedHashMap<IImportFile, ImportMessage>());

    public boolean isProcessing() {
        return processing;
    }

    public boolean isError() {
        return error;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public ImportMessage getReferenceDataMessage(IImportFile importFile) {
        return referenceDataMap.get(importFile);
    }

    private void init() {
        referenceDataMap.clear();
        processing = true;
        error = false;
        success = false;
        errorMessage = null;
    }

    private <T extends IImportFile> void processReferenceData(T importFile, final long localeId)
            throws ImportFileNotFoundException, ImportObjectLinkException, ImportFileReadException {

        final IImportListener referenceDataListener = new IImportListener() {

            @Override
            public void beginImport(IImportFile importFile, int recordCount) {
                referenceDataMap.put(importFile, new ImportMessage(importFile, recordCount, 0));
            }

            @Override
            public void recordProcessed(IImportFile importFile, int recordIndex) {
                referenceDataMap.get(importFile).setIndex(recordIndex);
            }

            @Override
            public void completeImport(IImportFile importFile, int recordCount) {
                referenceDataMap.get(importFile).setCompleted();
                logBean.info(Module.NAME, ReferenceDataImportService.class, importFile.getClass(), null, Log.EVENT.CREATE,
                        "Имя файла: {0}, количество записей: {1}, Идентификатор локали: {2}",
                        importFile.getFileName(), recordCount, localeId);
            }
        };

        if (importFile instanceof AddressImportFile) { //address
            addressImportService.process(importFile, referenceDataListener);
        } else if (importFile instanceof OwnerRelationshipImportFile) { // owner relationship
            ownerRelationshipImportService.process(referenceDataListener, localeId);
        } else if (importFile instanceof OwnershipFormImportFile) { //ownership form
            ownershipFormImportService.process(referenceDataListener, localeId);
        } else if (importFile instanceof RegistrationTypeImportFile) {//registration type
            registrationTypeImportService.process(referenceDataListener, localeId);
        } else if (importFile instanceof DocumentTypeImportFile) {//document type
            documentTypeImportService.process(referenceDataListener, localeId);
        } else if (importFile instanceof MilitaryServiceRelationImportFile) {//military service relation
            militaryServiceRelationImportService.process(referenceDataListener, localeId);
        } else if (importFile instanceof DepartureReasonImportFile) {//departure reason
            departureReasonImportService.process(referenceDataListener, localeId);
        } else if (importFile instanceof HousingRightsImportFile) {//housing rights
            housingRightsImportService.process(referenceDataListener, localeId);
        }
    }

    @Asynchronous
    public <T extends IImportFile> void process(List<T> referenceDataFiles, long localeId) {
        if (processing) {
            return;
        }

        init();
        configBean.getString(DictionaryConfig.IMPORT_FILE_STORAGE_DIR, true); //reload config cache

        try {
            for (T referenceDataFile : referenceDataFiles) {
                userTransaction.begin();

                processReferenceData(referenceDataFile, localeId);

                userTransaction.commit();
            }
            success = true;
        } catch (Exception e) {
            log.error("Import error.", e);

            try {
                userTransaction.rollback();
            } catch (Exception e1) {
                log.error("Couldn't rollback transaction.", e1);
            }

            error = true;
            errorMessage = e instanceof AbstractException ? e.getMessage() : new ImportCriticalException(e).getMessage();

            logBean.error(Module.NAME, ReferenceDataImportService.class, null, null, Log.EVENT.CREATE, errorMessage);
        } finally {
            processing = false;
        }
    }
}
