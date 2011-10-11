package org.complitex.pspoffice.person.download;

import org.complitex.dictionary.util.StringUtil;
import org.complitex.pspoffice.report.entity.IReportField;
import org.complitex.pspoffice.report.entity.PersonCardField;
import org.complitex.pspoffice.report.web.AbstractReportDownload;

import java.util.Map;
import org.complitex.pspoffice.person.registration.report.entity.PersonCard;

import static org.complitex.pspoffice.report.entity.PersonCardField.*;

public class PersonCardDownload extends AbstractReportDownload<PersonCard> {

    public PersonCardDownload(PersonCard report) {
        super("PersonCard", PersonCardField.values(), report);
    }

    @Override
    protected Map<IReportField, Object> getValues() {
        PersonCard report = getReport();
        Map<IReportField, Object> map = newValuesMap();

        map.put(FIRST_NAME, report.getFirstName());
        map.put(LAST_NAME, report.getLastName());
        map.put(MIDDLE_NAME, report.getMiddleName());
        map.put(NATIONALITY, report.getNationality());
        map.put(BIRTH_DATE, report.getBirthDate());
        map.put(BIRTH_REGION, report.getBirthRegion());
        map.put(BIRTH_DISTRICT, report.getBirthDistrict());
        map.put(BIRTH_CITY, report.getBirthCity());
        map.put(ARRIVAL_REGION, report.getArrivalRegion());
        map.put(ARRIVAL_DISTRICT, report.getArrivalDistrict());
        map.put(ARRIVAL_CITY, report.getArrivalCity());
        map.put(ARRIVAL_DATE, report.getArrivalDate());
        map.put(ARRIVAL_STREET, report.getArrivalStreet());
        map.put(ARRIVAL_BUILDING, report.getArrivalBuilding());
        map.put(ARRIVAL_CORP, report.getArrivalCorp());
        map.put(ARRIVAL_APARTMENT, report.getArrivalApartment());
        map.put(PASSPORT_SERIES, report.getPassportSeries());
        map.put(PASSPORT_NUMBER, report.getPassportNumber());
        map.put(PASSPORT_ISSUED, report.getPassportIssued());
        map.put(ADDRESS, report.getAddress());

        map.put(CHILD0, StringUtil.valueOf(report.getChild0()));
        map.put(CHILD1, StringUtil.valueOf(report.getChild1()));
        map.put(CHILD2, StringUtil.valueOf(report.getChild2()));

        putMultilineValue(map, report.getMilitary(), 50, MILITARY0, MILITARY1, MILITARY2);

        map.put(REGISTRATION_DATE, report.getRegistrationDate());
        map.put(REGISTRATION_TYPE, report.getRegistrationType());
        map.put(DEPARTURE_REGION, report.getDepartureRegion());
        map.put(DEPARTURE_DISTRICT, report.getDepartureDistrict());
        map.put(DEPARTURE_CITY, report.getDepartureCity());
        map.put(DEPARTURE_DATE, report.getDepartureDate());
        map.put(DEPARTURE_REASON, report.getDepartureReason());

        return map;
    }

    @Override
    protected String getFileName() {
        return "PersonCard";
    }
}
