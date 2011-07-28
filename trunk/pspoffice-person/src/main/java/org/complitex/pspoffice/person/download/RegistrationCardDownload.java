//package org.complitex.pspoffice.person.download;
//
//import org.apache.wicket.PageParameters;
//import org.complitex.dictionary.util.DateUtil;
//import org.complitex.dictionary.util.StringUtil;
//import org.complitex.pspoffice.person.strategy.PersonStrategy;
//import org.complitex.pspoffice.person.strategy.entity.Person;
//import org.complitex.pspoffice.person.strategy.entity.Registration;
//import org.complitex.pspoffice.report.entity.IReportField;
//import org.complitex.pspoffice.report.entity.RegistrationCardField;
//import org.complitex.pspoffice.report.web.AbstractReportDownload;
//
//import javax.ejb.EJB;
//import java.io.UnsupportedEncodingException;
//import java.net.URLEncoder;
//import java.util.Map;
//
//import static org.complitex.pspoffice.report.entity.RegistrationCardField.*;
//
///**
// * @author Anatoly A. Ivanov java@inheaven.ru
// *         Date: 31.05.11 15:50
// */
//public class RegistrationCardDownload extends AbstractReportDownload {
//    @EJB
//    private PersonStrategy personStrategy;
//
//    public RegistrationCardDownload(PageParameters parameters) {
//        super("RegistrationCard", RegistrationCardField.values(), parameters);
//    }
//
//    protected Map<IReportField, Object> getValues(PageParameters parameters){
//        Person person = personStrategy.findById(parameters.getLong("object_id"), true);
//        personStrategy.loadName(person);
//
//        Registration registration = person.getRegistration();
//
//        Map<IReportField, Object> map = newValuesMap();
//
//        map.put(FIRST_NAME, person.getFirstName().toUpperCase());
//        map.put(LAST_NAME, person.getLastName().toUpperCase());
//        map.put(MIDDLE_NAME, person.getMiddleName().toUpperCase());
//        map.put(NATIONALITY, person.getNationality());
//        map.put(BIRTH_DATE, person.getBirthDate());
//        map.put(BIRTH_REGION, person.getBirthRegion());
//        map.put(BIRTH_DISTRICT, person.getBirthDistrict());
//        map.put(BIRTH_CITY, person.getBirthCity());
//        map.put(ARRIVAL_REGION, registration.getArrivalRegion());
//        map.put(ARRIVAL_DISTRICT, registration.getArrivalDistrict());
//        map.put(ARRIVAL_CITY, registration.getArrivalCity());
//        map.put(ARRIVAL_DATE, registration.getArrivalDate());
//        map.put(ARRIVAL_STREET, registration.getArrivalStreet());
//        map.put(ARRIVAL_BUILDING, registration.getArrivalBuildingNumber());
//        map.put(ARRIVAL_CORP, registration.getArrivalBuildingCorp());
//        map.put(ARRIVAL_APARTMENT, registration.getArrivalApartment());
//        map.put(PASSPORT_SERIES0, person.getPassportSerialNumber());
//        map.put(PASSPORT_NUMBER0, person.getPassportNumber());
//        map.put(PASSPORT_ISSUED0, person.getPassportAcquisitionOrganization() + ", " + getString(person.getPassportAcquisitionDate()));
//        map.put(RESIDENCE_CITY,  registration.getCity(getLocale()));
//        map.put(RESIDENCE_STREET, registration.getStreet(getLocale()));
//        map.put(RESIDENCE_BUILDING, registration.getBuildingNumber(getLocale()));
//        map.put(RESIDENCE_CORP, registration.getBuildingCorp(getLocale()));
//        map.put(RESIDENCE_APARTMENT, registration.getApartment(getLocale()));
//
//        //Где и кем работает
//        putMultilineValue(map, person.getJobInfo(), 50, WORKS0, WORKS1);
//
//        //Дети до 16 лет
//        if (person.getChildren() != null) {
//            int size = person.getChildren().size();
//
//            if (size == 1){
//                map.put(CHILDREN0, person.getChildren().get(0).getFullName());
//            }else if (size == 2){
//                map.put(CHILDREN0, person.getChildren().get(0).getFullName());
//                map.put(CHILDREN1, person.getChildren().get(1).getFullName());
//            }else if (size == 3){
//                map.put(CHILDREN0, person.getChildren().get(0).getFullName());
//                map.put(CHILDREN1, person.getChildren().get(1).getFullName());
//                map.put(CHILDREN2, person.getChildren().get(2).getFullName());
//            }else if (size > 3){
//                //todo
//            }
//        }
//
//        //Отношение к воинской службе
//        putMultilineValue(map, person.getMilitaryServiceRelation(), 50, MILITARY0, MILITARY1, MILITARY2);
//
//        map.put(REGISTRATION_DATE, registration.getRegistrationDate());
//        map.put(REGISTRATION_TYPE, registration.getRegistrationType());
//        map.put(DEPARTURE_REGION, registration.getDepartureRegion());
//        map.put(DEPARTURE_DISTRICT, registration.getDepartureDistrict());
//        map.put(DEPARTURE_CITY, registration.getDepartureCity());
//        map.put(DEPARTURE_DATE, registration.getDepartureDate());
//        map.put(DEPARTURE_REASON, registration.getDepartureReason());
//
//        //Заполнение пустых значений
//        for(RegistrationCardField field : RegistrationCardField.values()){
//            if (map.get(field) == null) {
//                map.put(field, "");
//            }
//        }
//
//        return map;
//    }
//
//    @Override
//    protected String getFileName(PageParameters parameters) {
//        Person person = personStrategy.findById(parameters.getLong("object_id"), true);
//        personStrategy.loadName(person);
//
//        try {
//            String name = "Карточка-" + person.getLastName() + "-" + getString(DateUtil.getCurrentDate());
//
//            return URLEncoder.encode(name, "utf-8");
//        } catch (UnsupportedEncodingException e) {
//            //oops
//        }
//
//        return "RegistrationCard";
//    }
//}
