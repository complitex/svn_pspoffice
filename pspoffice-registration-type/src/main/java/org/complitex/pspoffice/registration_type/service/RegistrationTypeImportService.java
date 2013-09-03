package org.complitex.pspoffice.registration_type.service;

import au.com.bytecode.opencsv.CSVReader;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.StringCulture;
import org.complitex.dictionary.service.AbstractImportService;
import org.complitex.dictionary.service.IImportListener;
import org.complitex.dictionary.service.exception.ImportFileNotFoundException;
import org.complitex.dictionary.service.exception.ImportFileReadException;
import org.complitex.dictionary.util.CloneUtil;
import org.complitex.dictionary.util.DateUtil;
import org.complitex.pspoffice.registration_type.strategy.RegistrationTypeStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.io.IOException;
import java.util.Collection;

import static org.complitex.pspoffice.registration_type.entity.RegistrationTypeImportFile.REGISTRATION_TYPE;

@Stateless
public class RegistrationTypeImportService extends AbstractImportService {

    private final static Logger log = LoggerFactory.getLogger(RegistrationTypeImportService.class);
    @EJB
    private RegistrationTypeStrategy strategy;

    private void setValue(Attribute attribute, String value, long localeId) {
        for (StringCulture string : attribute.getLocalizedValues()) {
            if (string.getLocaleId().equals(localeId)) {
                string.setValue(value);
            }
        }
    }

    /**
     * REGISTRATION_TYPE_ID	Код	Название
     * @param listener
     * @throws ImportFileNotFoundException
     * @throws ImportFileReadException
     */
    public void process(IImportListener listener, long localeId)
            throws ImportFileNotFoundException, ImportFileReadException {
        listener.beginImport(REGISTRATION_TYPE, getRecordCount(REGISTRATION_TYPE));

        CSVReader reader = getCsvReader(REGISTRATION_TYPE);

        int recordIndex = 0;

        try {
            String[] line;

            final Collection<StringCulture> reservedObjectNames = strategy.reservedNames();

            while ((line = reader.readNext()) != null) {
                recordIndex++;

                final String externalId = line[0].trim();
                final String name = line[1].trim();

                // Сначала ищем среди предопределенных системой объектов.
                boolean isReserved = false;
                for (StringCulture string : reservedObjectNames) {
                    final String reservedName = string.getValue();
                    if (reservedName != null && reservedName.equalsIgnoreCase(name)) {
                        // нашли
                        isReserved = true;
                        break;
                    }
                }
                if (isReserved) {
                    // это зарезервированный системой объект, пропускаем его.
                } else {
                    // Ищем по externalId в базе.
                    final Long objectId = strategy.getObjectId(externalId);
                    if (objectId != null) {
                        DomainObject oldObject = strategy.findById(objectId, true);
                        if (oldObject != null) {
                            // нашли, обновляем (или дополняем) значения атрибутов и сохраняем.
                            DomainObject newObject = CloneUtil.cloneObject(oldObject);
                            setValue(newObject.getAttribute(RegistrationTypeStrategy.NAME), name, localeId);
                            strategy.update(oldObject, newObject, DateUtil.getCurrentDate());
                        }
                    } else {
                        // не нашли, создаём объект заполняем его атрибуты и сохраняем.
                        DomainObject object = strategy.newInstance();
                        object.setExternalId(externalId);
                        setValue(object.getAttribute(RegistrationTypeStrategy.NAME), name, localeId);
                        strategy.insert(object, DateUtil.getCurrentDate());
                    }
                }
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
