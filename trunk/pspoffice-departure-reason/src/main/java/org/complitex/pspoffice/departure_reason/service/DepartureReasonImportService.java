package org.complitex.pspoffice.departure_reason.service;

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

import org.complitex.pspoffice.departure_reason.strategy.DepartureReasonStrategy;
import static org.complitex.pspoffice.departure_reason.entity.DepartureReasonImportFile.DEPARTURE_REASON;

@Stateless
public class DepartureReasonImportService extends AbstractImportService {

    private final static Logger log = LoggerFactory.getLogger(DepartureReasonImportService.class);
    @EJB
    private DepartureReasonStrategy departureReasonStrategy;
    @EJB
    private StringCultureBean stringCultureBean;

    /**
     * DEPARTURE_REASON_ID	Код	Название
     * @param listener
     * @throws ImportFileNotFoundException
     * @throws ImportFileReadException
     */
    public void process(IImportListener listener)
            throws ImportFileNotFoundException, ImportFileReadException {
        listener.beginImport(DEPARTURE_REASON, getRecordCount(DEPARTURE_REASON));

        CSVReader reader = getCsvReader(DEPARTURE_REASON);

        int recordIndex = 0;

        try {
            String[] line;

            while ((line = reader.readNext()) != null) {
                recordIndex++;

                DomainObject domainObject = departureReasonStrategy.newInstance();

                //DEPARTURE_REASON_ID
                domainObject.setExternalId(Long.parseLong(line[0].trim()));

                //Код
                Attribute code = domainObject.getAttribute(DepartureReasonStrategy.CODE);
                stringCultureBean.getSystemStringCulture(code.getLocalizedValues()).setValue(line[1].trim());

                //Название
                Attribute name = domainObject.getAttribute(DepartureReasonStrategy.NAME);
                stringCultureBean.getSystemStringCulture(name.getLocalizedValues()).setValue(line[2].trim());

                departureReasonStrategy.insert(domainObject, DateUtil.getCurrentDate());

                listener.recordProcessed(DEPARTURE_REASON, recordIndex);
            }

            listener.completeImport(DEPARTURE_REASON, recordIndex);
        } catch (IOException e) {
            throw new ImportFileReadException(e, DEPARTURE_REASON.getFileName(), recordIndex);
        } catch (NumberFormatException e) {
            throw new ImportFileReadException(e, DEPARTURE_REASON.getFileName(), recordIndex);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                log.error("Ошибка закрытия потока", e);
            }
        }
    }
}
