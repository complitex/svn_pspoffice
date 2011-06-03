package org.complitex.pspoffice.report.web;

import org.apache.wicket.PageParameters;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.RegistrationStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.entity.Registration;
import org.complitex.pspoffice.report.entity.RegistrationCardField;

import javax.ejb.EJB;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.complitex.pspoffice.report.entity.RegistrationCardField.*;

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

        for(RegistrationCardField field : RegistrationCardField.values()){
            map.put(field.getFieldName(), "");
        }

        Person person = personStrategy.findById(objectId, true);
        Registration registration = person.getRegistration();

        map.put(FIRST_NAME.getFieldName(), person.getFirstName());
        map.put(LAST_NAME.getFieldName(), person.getLastName());
        map.put(MIDDLE_NAME.getFieldName(), person.getMiddleName());
        map.put(NATIONALITY.getFieldName(), person.getNationality());
        map.put(BIRTH_DATE.getFieldName(), getString(person.getBirthDate()));
        map.put(BIRTH_REGION.getFieldName(), person.getBirthRegion());
        map.put(BIRTH_DISTRICT.getFieldName(), person.getBirthDistrict());
        map.put(BIRTH_CITY.getFieldName(), person.getBirthCity());
        map.put(BIRTH_VILLAGE.getFieldName(), "");
        map.put(ARRIVAL_REGION.getFieldName(), registration.getArrivalRegion());
        map.put(ARRIVAL_DISTRICT.getFieldName(), registration.getArrivalDistrict());
        map.put(ARRIVAL_CITY.getFieldName(), registration.getArrivalCity());
        map.put(ARRIVAL_VILLAGE.getFieldName(), "");
        map.put(ARRIVAL_DATE.getFieldName(), getString(registration.getArrivalDate()));
        map.put(ARRIVAL_STREET.getFieldName(), registration.getArrivalStreet());
        map.put(ARRIVAL_BUILDING.getFieldName(), registration.getArrivalBuildingNumber());
        map.put(ARRIVAL_CORP.getFieldName(), registration.getArrivalBuildingCorp());
        map.put(ARRIVAL_APARTMENT.getFieldName(), registration.getArrivalApartment());
        map.put(PASSPORT_SERIES0.getFieldName(), person.getPassportSerialNumber());
        map.put(PASSPORT_NUMBER0.getFieldName(), person.getPassportNumber());
        map.put(PASSPORT_ISSUED0.getFieldName(), person.getPassportAcquisitionOrganization() +
                ", " + getString(person.getPassportAcquisitionDate()));
        map.put(RESIDENCE_CITY.getFieldName(),  registration.getCity(getLocale()));
        map.put(RESIDENCE_STREET.getFieldName(), registration.getStreet(getLocale()));
        map.put(RESIDENCE_BUILDING.getFieldName(), registration.getBuildingNumber(getLocale()));
        map.put(RESIDENCE_CORP.getFieldName(), registration.getBuildingCorp(getLocale()));
        map.put(RESIDENCE_APARTMENT.getFieldName(), registration.getApartment(getLocale()));
        map.put(WORKS0.getFieldName(), person.getJobInfo());

        if (person.getChildren() != null) {
            int size = person.getChildren().size();

            if (size == 1){
                map.put(CHILDREN0.getFieldName(), person.getChildren().get(0).getFullName());
            }else if (size == 2){
                map.put(CHILDREN0.getFieldName(), person.getChildren().get(0).getFullName());
                map.put(CHILDREN1.getFieldName(), person.getChildren().get(1).getFullName());
            }else if (size == 3){
                map.put(CHILDREN0.getFieldName(), person.getChildren().get(0).getFullName());
                map.put(CHILDREN1.getFieldName(), person.getChildren().get(1).getFullName());
                map.put(CHILDREN2.getFieldName(), person.getChildren().get(2).getFullName());
            }else if (size > 3){
                //todo
            }
        }

        map.put(MILITARY0.getFieldName(), person.getMilitaryServiceRelation());
        map.put(REGISTRATION_NOTE.getFieldName(), "");
        map.put(REGISTRATION_DATE.getFieldName(), getString(registration.getRegistrationDate()));
        map.put(REGISTRATION_TYPE.getFieldName(), registration.getRegistrationType());
        map.put(DEPARTURE_REGION.getFieldName(), registration.getDepartureRegion());
        map.put(DEPARTURE_DISTRICT.getFieldName(), registration.getDepartureDistrict());
        map.put(DEPARTURE_CITY.getFieldName(), registration.getDepartureCity());
        map.put(DEPARTURE_VILLAGE.getFieldName(), "");
        map.put(DEPARTURE_DATE.getFieldName(), getString(registration.getDepartureDate()));
        map.put(DEPARTURE_REASON.getFieldName(), registration.getDepartureReason());

        return map;
    }

    private  String getString(Date date){
        return REPORT_DATE_FORMAT.format(date);

    }
}
