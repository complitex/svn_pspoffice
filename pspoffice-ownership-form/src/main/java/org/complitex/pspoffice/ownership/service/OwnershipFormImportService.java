package org.complitex.pspoffice.ownership.service;

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

import org.complitex.pspoffice.ownership.strategy.OwnershipFormStrategy;
import static org.complitex.pspoffice.ownership.entity.OwnershipFormImportFile.OWNERSHIP_FORM;

@Stateless
public class OwnershipFormImportService extends AbstractImportService {

    private final static Logger log = LoggerFactory.getLogger(OwnershipFormImportService.class);
    @EJB
    private OwnershipFormStrategy ownershipFormStrategy;
    @EJB
    private StringCultureBean stringCultureBean;

    /**
     * OWNERSHIP_FORM_ID	Название
     * @param listener
     * @throws ImportFileNotFoundException
     * @throws ImportFileReadException
     */
    public void process(IImportListener listener)
            throws ImportFileNotFoundException, ImportFileReadException {
        listener.beginImport(OWNERSHIP_FORM, getRecordCount(OWNERSHIP_FORM));

        CSVReader reader = getCsvReader(OWNERSHIP_FORM);

        int recordIndex = 0;

        try {
            String[] line;

            while ((line = reader.readNext()) != null) {
                recordIndex++;

                DomainObject domainObject = ownershipFormStrategy.newInstance();

                //OWNERSHIP_FORM_ID
                domainObject.setExternalId(Long.parseLong(line[0].trim()));

                //Название
                Attribute name = domainObject.getAttribute(OwnershipFormStrategy.NAME);
                stringCultureBean.getSystemStringCulture(name.getLocalizedValues()).setValue(line[1].trim());

                ownershipFormStrategy.insert(domainObject, DateUtil.getCurrentDate());

                listener.recordProcessed(OWNERSHIP_FORM, recordIndex);
            }

            listener.completeImport(OWNERSHIP_FORM, recordIndex);
        } catch (IOException e) {
            throw new ImportFileReadException(e, OWNERSHIP_FORM.getFileName(), recordIndex);
        } catch (NumberFormatException e) {
            throw new ImportFileReadException(e, OWNERSHIP_FORM.getFileName(), recordIndex);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                log.error("Ошибка закрытия потока", e);
            }
        }
    }
}
