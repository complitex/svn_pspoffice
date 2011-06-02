package org.complitex.pspoffice.report.web;

import org.apache.wicket.PageParameters;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.RegistrationStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.entity.Registration;

import javax.ejb.EJB;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import static org.complitex.pspoffice.report.entity.RegistrationCardFields.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 31.05.11 15:50
 */
public class RegistrationCard extends AbstractReportDownload {
    private final static SimpleDateFormat REPORT_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    @EJB
    private PersonStrategy personStrategy;

    @EJB
    private RegistrationStrategy registrationStrategy;

    public RegistrationCard(PageParameters parameters) {
        super("RegistrationCard", parameters);
    }

    protected Map<String, String> getValues(PageParameters parameters){
        Long objectId = parameters.getLong("object_id");

        final Map<String, String> map = new HashMap<String, String>();



        /*Person person = personStrategy.findById(objectId, true);
        Registration registration = person.getRegistration();

        map.put(FIRST_NAME.getFieldName(), person.getFirstName());
        map.put(LAST_NAME.getFieldName(), person.getLastName());
        map.put(MIDDLE_NAME.getFieldName(), person.getMiddleName());
        map.put(NATIONALITY.getFieldName(), "[TODO]");
        map.put(BIRTH_DATE.getFieldName(), REPORT_DATE_FORMAT.format(person.getBirthDate()));
        map.put(BIRTH_REGION.getFieldName(), "");
        map.put(BIRTH_DISTRICT.getFieldName(), "");
        map.put(BIRTH_CITY.getFieldName(), "");
        map.put(BIRTH_VILLAGE.getFieldName(), "");
        map.put(ARRIVAL_REGION.getFieldName(), "");
        map.put(ARRIVAL_DISTRICT.getFieldName(), "");
        map.put(ARRIVAL_CITY.getFieldName(), "");
        map.put(ARRIVAL_VILLAGE.getFieldName(), "");
        map.put(ARRIVAL_DATE.getFieldName(), "");
        map.put(ARRIVAL_STREET.getFieldName(), "");
        map.put(ARRIVAL_BUILDING.getFieldName(), "");
        map.put(ARRIVAL_CORP.getFieldName(), "");
        map.put(ARRIVAL_APARTMENT.getFieldName(), "");
        map.put(PASSPORT_SERIES0.getFieldName(), "");
        map.put(PASSPORT_NUMBER0.getFieldName(), "");
        map.put(PASSPORT_ISSUED0.getFieldName(), "");
        map.put(RESIDENCE_CITY.getFieldName(), "");
        map.put(RESIDENCE_STREET.getFieldName(), "");
        map.put(RESIDENCE_BUILDING.getFieldName(), "");
        map.put(RESIDENCE_CORP.getFieldName(), "");
        map.put(RESIDENCE_APARTMENT.getFieldName(), "");
        map.put(WORKS0.getFieldName(), "");
        map.put(CHILDREN0.getFieldName(), "");
        map.put(CHILDREN1.getFieldName(), "");
        map.put(CHILDREN2.getFieldName(), "");
        map.put(MILITARY0.getFieldName(), "");
        map.put(REGISTRATION_NOTE.getFieldName(), "");
        map.put(REGISTRATION_DATE.getFieldName(), "");
        map.put(REGISTRATION_TYPE.getFieldName(), "");
        map.put(LEAVE_REGION.getFieldName(), "");
        map.put(LEAVE_DISTRICT.getFieldName(), "");
        map.put(LEAVE_CITY.getFieldName(), "");
        map.put(LEAVE_VILLAGE.getFieldName(), "");
        map.put(LEAVE_DATE.getFieldName(), "");
        map.put(LEAVE_REASON.getFieldName(), "");*/

        return map;
    }
}
