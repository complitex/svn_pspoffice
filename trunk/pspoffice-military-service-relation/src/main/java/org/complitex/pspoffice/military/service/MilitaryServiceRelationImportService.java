package org.complitex.pspoffice.military.service;

import org.complitex.dictionary.entity.StringCulture;
import org.complitex.dictionary.util.CloneUtil;
import au.com.bytecode.opencsv.CSVReader;
import org.complitex.dictionary.service.AbstractImportService;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.service.IImportListener;
import org.complitex.dictionary.service.exception.ImportFileNotFoundException;
import org.complitex.dictionary.service.exception.ImportFileReadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.IOException;
import org.complitex.dictionary.service.LocaleBean;
import org.complitex.dictionary.util.DateUtil;

import org.complitex.pspoffice.military.strategy.MilitaryServiceRelationStrategy;
import static org.complitex.pspoffice.military.entity.MilitaryServiceRelationImportFile.MILITARY_SERVICE_RELATION;

@Stateless
public class MilitaryServiceRelationImportService extends AbstractImportService {

    private final static Logger log = LoggerFactory.getLogger(MilitaryServiceRelationImportService.class);
    @EJB
    private MilitaryServiceRelationStrategy strategy;
    @EJB
    private LocaleBean localeBean;

    private void setValue(Attribute attribute, String value, long localeId) {
        for (StringCulture string : attribute.getLocalizedValues()) {
            if (string.getLocaleId().equals(localeId)) {
                string.setValue(value);
            }
        }
    }

    /**
     * MILITARY_SERVICE_RELATION_ID	Код	Название
     * @param listener
     * @throws ImportFileNotFoundException
     * @throws ImportFileReadException
     */
    public void process(IImportListener listener, long localeId)
            throws ImportFileNotFoundException, ImportFileReadException {
        listener.beginImport(MILITARY_SERVICE_RELATION, getRecordCount(MILITARY_SERVICE_RELATION));

        CSVReader reader = getCsvReader(MILITARY_SERVICE_RELATION);

        int recordIndex = 0;

        try {
            String[] line;

            while ((line = reader.readNext()) != null) {
                recordIndex++;

                final long externalId = Long.parseLong(line[0].trim());
                final String code = line[1].trim();
                final String name = line[2].trim();

                // Ищем по externalId в базе.
                final Long objectId = strategy.getObjectId(externalId);
                if (objectId != null) {
                    DomainObject oldObject = strategy.findById(objectId, true);
                    if (oldObject != null) {
                        // нашли, обновляем (или дополняем) значения атрибутов и сохраняем.
                        DomainObject newObject = CloneUtil.cloneObject(oldObject);
                        setValue(newObject.getAttribute(MilitaryServiceRelationStrategy.NAME), name, localeId);
                        strategy.update(oldObject, newObject, DateUtil.getCurrentDate());
                    }
                } else {
                    // не нашли, создаём объект заполняем его атрибуты и сохраняем.
                    DomainObject object = strategy.newInstance();
                    object.setExternalId(externalId);
                    setValue(object.getAttribute(MilitaryServiceRelationStrategy.CODE), code,
                            localeBean.getSystemLocaleObject().getId());
                    setValue(object.getAttribute(MilitaryServiceRelationStrategy.NAME), name, localeId);
                    strategy.insert(object, DateUtil.getCurrentDate());
                }
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
