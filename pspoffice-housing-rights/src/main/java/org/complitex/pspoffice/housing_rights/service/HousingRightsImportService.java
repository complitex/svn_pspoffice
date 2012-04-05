package org.complitex.pspoffice.housing_rights.service;

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

import org.complitex.pspoffice.housing_rights.strategy.HousingRightsStrategy;
import static org.complitex.pspoffice.housing_rights.entity.HousingRightsImportFile.HOUSING_RIGHTS;

@Stateless
public class HousingRightsImportService extends AbstractImportService {

    private final static Logger log = LoggerFactory.getLogger(HousingRightsImportService.class);
    @EJB
    private HousingRightsStrategy housingRightsStrategy;
    @EJB
    private StringCultureBean stringCultureBean;

    /**
     * HOUSING_RIGHTS_ID	Код	Название
     * @param listener
     * @throws ImportFileNotFoundException
     * @throws ImportFileReadException
     */
    public void process(IImportListener listener)
            throws ImportFileNotFoundException, ImportFileReadException {
        listener.beginImport(HOUSING_RIGHTS, getRecordCount(HOUSING_RIGHTS));

        CSVReader reader = getCsvReader(HOUSING_RIGHTS);

        int recordIndex = 0;

        try {
            String[] line;

            while ((line = reader.readNext()) != null) {
                recordIndex++;

                DomainObject domainObject = housingRightsStrategy.newInstance();

                //HOUSING_RIGHTS_ID
                domainObject.setExternalId(Long.parseLong(line[0].trim()));

                //Код
                Attribute code = domainObject.getAttribute(HousingRightsStrategy.CODE);
                stringCultureBean.getSystemStringCulture(code.getLocalizedValues()).setValue(line[1].trim());

                //Название
                Attribute name = domainObject.getAttribute(HousingRightsStrategy.NAME);
                stringCultureBean.getSystemStringCulture(name.getLocalizedValues()).setValue(line[2].trim());

                housingRightsStrategy.insert(domainObject, DateUtil.getCurrentDate());

                listener.recordProcessed(HOUSING_RIGHTS, recordIndex);
            }

            listener.completeImport(HOUSING_RIGHTS, recordIndex);
        } catch (IOException e) {
            throw new ImportFileReadException(e, HOUSING_RIGHTS.getFileName(), recordIndex);
        } catch (NumberFormatException e) {
            throw new ImportFileReadException(e, HOUSING_RIGHTS.getFileName(), recordIndex);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                log.error("Ошибка закрытия потока", e);
            }
        }
    }
}
