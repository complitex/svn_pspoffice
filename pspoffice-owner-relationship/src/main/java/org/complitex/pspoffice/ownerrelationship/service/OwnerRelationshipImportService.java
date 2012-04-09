package org.complitex.pspoffice.ownerrelationship.service;

import au.com.bytecode.opencsv.CSVReader;
import org.complitex.dictionary.entity.AbstractImportService;
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
import org.complitex.dictionary.entity.StringCulture;
import org.complitex.dictionary.util.CloneUtil;
import org.complitex.dictionary.util.DateUtil;

import org.complitex.pspoffice.ownerrelationship.strategy.OwnerRelationshipStrategy;
import static org.complitex.pspoffice.ownerrelationship.entity.OwnerRelationshipImportFile.OWNER_RELATIONSHIP;

@Stateless
public class OwnerRelationshipImportService extends AbstractImportService {

    private final static Logger log = LoggerFactory.getLogger(OwnerRelationshipImportService.class);
    @EJB
    private OwnerRelationshipStrategy strategy;

    private void setValue(Attribute attribute, String value, long localeId) {
        for (StringCulture string : attribute.getLocalizedValues()) {
            if (string.getLocaleId().equals(localeId)) {
                string.setValue(value);
            }
        }
    }

    /**
     * OWNER_RELATIONSHIP_ID	Название
     * @param listener
     * @throws ImportFileNotFoundException
     * @throws ImportFileReadException
     */
    public void process(IImportListener listener, long localeId)
            throws ImportFileNotFoundException, ImportFileReadException {
        listener.beginImport(OWNER_RELATIONSHIP, getRecordCount(OWNER_RELATIONSHIP));

        CSVReader reader = getCsvReader(OWNER_RELATIONSHIP);

        int recordIndex = 0;

        try {
            String[] line;

            while ((line = reader.readNext()) != null) {
                recordIndex++;

                final long externalId = Long.parseLong(line[0].trim());
                final String name = line[1].trim();

                // Ищем по externalId в базе.
                final Long objectId = strategy.getObjectId(externalId);
                if (objectId != null) {
                    DomainObject oldObject = strategy.findById(objectId, true);
                    if (oldObject != null) {
                        // нашли, обновляем (или дополняем) значения атрибутов и сохраняем.
                        DomainObject newObject = CloneUtil.cloneObject(oldObject);
                        setValue(newObject.getAttribute(OwnerRelationshipStrategy.NAME), name, localeId);
                        strategy.update(oldObject, newObject, DateUtil.getCurrentDate());
                    }
                } else {
                    // не нашли, создаём объект заполняем его атрибуты и сохраняем.
                    DomainObject object = strategy.newInstance();
                    object.setExternalId(externalId);
                    setValue(object.getAttribute(OwnerRelationshipStrategy.NAME), name, localeId);
                    strategy.insert(object, DateUtil.getCurrentDate());
                }
                listener.recordProcessed(OWNER_RELATIONSHIP, recordIndex);
            }

            listener.completeImport(OWNER_RELATIONSHIP, recordIndex);
        } catch (IOException e) {
            throw new ImportFileReadException(e, OWNER_RELATIONSHIP.getFileName(), recordIndex);
        } catch (NumberFormatException e) {
            throw new ImportFileReadException(e, OWNER_RELATIONSHIP.getFileName(), recordIndex);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                log.error("Ошибка закрытия потока", e);
            }
        }
    }
}
