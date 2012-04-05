package org.complitex.pspoffice.registration_type.service;

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

import org.complitex.pspoffice.registration_type.strategy.RegistrationTypeStrategy;
import static org.complitex.pspoffice.registration_type.entity.RegistrationTypeImportFile.REGISTRATION_TYPE;

@Stateless
public class RegistrationTypeImportService extends AbstractImportService {

    private final static Logger log = LoggerFactory.getLogger(RegistrationTypeImportService.class);
    @EJB
    private RegistrationTypeStrategy registrationTypeStrategy;
    @EJB
    private StringCultureBean stringCultureBean;

    /**
     * REGISTRATION_TYPE_ID	Код	Название
     * @param listener
     * @throws ImportFileNotFoundException
     * @throws ImportFileReadException
     */
    public void process(IImportListener listener)
            throws ImportFileNotFoundException, ImportFileReadException {
        listener.beginImport(REGISTRATION_TYPE, getRecordCount(REGISTRATION_TYPE));

        CSVReader reader = getCsvReader(REGISTRATION_TYPE);

        int recordIndex = 0;

        try {
            String[] line;

            while ((line = reader.readNext()) != null) {
                recordIndex++;

                DomainObject domainObject = registrationTypeStrategy.newInstance();

                //REGISTRATION_TYPE_ID
                domainObject.setExternalId(Long.parseLong(line[0].trim()));

                //Название
                Attribute name = domainObject.getAttribute(RegistrationTypeStrategy.NAME);
                stringCultureBean.getSystemStringCulture(name.getLocalizedValues()).setValue(line[1].trim());

                registrationTypeStrategy.insert(domainObject, DateUtil.getCurrentDate());

                listener.recordProcessed(REGISTRATION_TYPE, recordIndex);
            }

            listener.completeImport(REGISTRATION_TYPE, recordIndex);
        } catch (IOException e) {
            throw new ImportFileReadException(e, REGISTRATION_TYPE.getFileName(), recordIndex);
        } catch (NumberFormatException e) {
            throw new ImportFileReadException(e, REGISTRATION_TYPE.getFileName(), recordIndex);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                log.error("Ошибка закрытия потока", e);
            }
        }
    }
}
