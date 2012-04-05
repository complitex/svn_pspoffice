package org.complitex.pspoffice.military.service;

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

import org.complitex.pspoffice.military.strategy.MilitaryServiceRelationStrategy;
import static org.complitex.pspoffice.military.entity.MilitaryServiceRelationImportFile.MILITARY_SERVICE_RELATION;

@Stateless
public class MilitaryServiceRelationImportService extends AbstractImportService {

    private final static Logger log = LoggerFactory.getLogger(MilitaryServiceRelationImportService.class);
    @EJB
    private MilitaryServiceRelationStrategy militaryServiceRelationStrategy;
    @EJB
    private StringCultureBean stringCultureBean;

    /**
     * MILITARY_SERVICE_RELATION_ID	Код	Название
     * @param listener
     * @throws ImportFileNotFoundException
     * @throws ImportFileReadException
     */
    public void process(IImportListener listener)
            throws ImportFileNotFoundException, ImportFileReadException {
        listener.beginImport(MILITARY_SERVICE_RELATION, getRecordCount(MILITARY_SERVICE_RELATION));

        CSVReader reader = getCsvReader(MILITARY_SERVICE_RELATION);

        int recordIndex = 0;

        try {
            String[] line;

            while ((line = reader.readNext()) != null) {
                recordIndex++;

                DomainObject domainObject = militaryServiceRelationStrategy.newInstance();

                //MILITARY_SERVICE_RELATION_ID
                domainObject.setExternalId(Long.parseLong(line[0].trim()));

                //Код
                Attribute code = domainObject.getAttribute(MilitaryServiceRelationStrategy.CODE);
                stringCultureBean.getSystemStringCulture(code.getLocalizedValues()).setValue(line[1].trim());

                //Название
                Attribute name = domainObject.getAttribute(MilitaryServiceRelationStrategy.NAME);
                stringCultureBean.getSystemStringCulture(name.getLocalizedValues()).setValue(line[2].trim());

                militaryServiceRelationStrategy.insert(domainObject, DateUtil.getCurrentDate());

                listener.recordProcessed(MILITARY_SERVICE_RELATION, recordIndex);
            }

            listener.completeImport(MILITARY_SERVICE_RELATION, recordIndex);
        } catch (IOException e) {
            throw new ImportFileReadException(e, MILITARY_SERVICE_RELATION.getFileName(), recordIndex);
        } catch (NumberFormatException e) {
            throw new ImportFileReadException(e, MILITARY_SERVICE_RELATION.getFileName(), recordIndex);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                log.error("Ошибка закрытия потока", e);
            }
        }
    }
}
