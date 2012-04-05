package org.complitex.pspoffice.document_type.service;

import au.com.bytecode.opencsv.CSVReader;
import org.complitex.dictionary.entity.AbstractImportService;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.service.IImportListener;
import org.complitex.dictionary.service.StringCultureBean;
import org.complitex.dictionary.service.exception.ImportFileNotFoundException;
import org.complitex.dictionary.service.exception.ImportFileReadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.IOException;
import org.complitex.dictionary.util.DateUtil;

import org.complitex.pspoffice.document_type.strategy.DocumentTypeStrategy;
import static org.complitex.pspoffice.document_type.entity.DocumentTypeImportFile.DOCUMENT_TYPE;

@Stateless
public class DocumentTypeImportService extends AbstractImportService {

    private final static Logger log = LoggerFactory.getLogger(DocumentTypeImportService.class);
    @EJB
    private DocumentTypeStrategy documentTypeStrategy;
    @EJB
    private StringCultureBean stringCultureBean;

    /**
     * DOCUMENT_TYPE_ID     Название
     * @param listener
     * @throws ImportFileNotFoundException
     * @throws ImportFileReadException
     */
    public void process(IImportListener listener)
            throws ImportFileNotFoundException, ImportFileReadException {
        listener.beginImport(DOCUMENT_TYPE, getRecordCount(DOCUMENT_TYPE));

        CSVReader reader = getCsvReader(DOCUMENT_TYPE);

        int recordIndex = 0;

        try {
            String[] line;

            while ((line = reader.readNext()) != null) {
                recordIndex++;

                DomainObject domainObject = documentTypeStrategy.newInstance();

                //DOCUMENT_TYPE_ID
                domainObject.setExternalId(Long.parseLong(line[0].trim()));

                //Название
                Attribute name = domainObject.getAttribute(DocumentTypeStrategy.NAME);
                stringCultureBean.getSystemStringCulture(name.getLocalizedValues()).setValue(line[1].trim());

                documentTypeStrategy.insert(domainObject, DateUtil.getCurrentDate());

                listener.recordProcessed(DOCUMENT_TYPE, recordIndex);
            }

            listener.completeImport(DOCUMENT_TYPE, recordIndex);
        } catch (IOException e) {
            throw new ImportFileReadException(e, DOCUMENT_TYPE.getFileName(), recordIndex);
        } catch (NumberFormatException e) {
            throw new ImportFileReadException(e, DOCUMENT_TYPE.getFileName(), recordIndex);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                log.error("Ошибка закрытия потока", e);
            }
        }
    }
}
