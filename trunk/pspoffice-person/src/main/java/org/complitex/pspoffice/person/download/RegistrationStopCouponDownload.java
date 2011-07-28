//package org.complitex.pspoffice.person.download;
//
//import org.apache.wicket.PageParameters;
//import org.complitex.dictionary.entity.Gender;
//import org.complitex.dictionary.util.DateUtil;
//import org.complitex.pspoffice.person.registration.report.entity.RegistrationStopCoupon;
//import org.complitex.pspoffice.person.registration.report.exception.UnregisteredPersonException;
//import org.complitex.pspoffice.person.registration.report.service.RegistrationStopCouponBean;
//import org.complitex.pspoffice.person.strategy.PersonStrategy;
//import org.complitex.pspoffice.person.strategy.entity.Person;
//import org.complitex.pspoffice.report.entity.IReportField;
//import org.complitex.pspoffice.report.entity.RegistrationStopCouponField;
//import org.complitex.pspoffice.report.web.AbstractReportDownload;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.ejb.EJB;
//import java.io.UnsupportedEncodingException;
//import java.net.URLEncoder;
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.complitex.pspoffice.report.entity.RegistrationStopCouponField.*;
//
///**
// * @author Anatoly A. Ivanov java@inheaven.ru
// *         Date: 08.06.11 17:17
// */
//public class RegistrationStopCouponDownload  extends AbstractReportDownload {
//    private static final Logger log = LoggerFactory.getLogger(RegistrationStopCouponDownload.class);
//
//    @EJB
//    private PersonStrategy personStrategy;
//
//    @EJB
//    private RegistrationStopCouponBean registrationStopCouponBean;
//
//    public RegistrationStopCouponDownload(PageParameters parameters) {
//        super("RegistrationStopCoupon", RegistrationStopCouponField.values(), parameters);
//    }
//
//    @Override
//    protected Map<IReportField, Object> getValues(PageParameters parameters) {
//        Person person = personStrategy.findById(parameters.getLong("object_id"), true);
//
//        RegistrationStopCoupon coupon;
//
//        try {
//            coupon = registrationStopCouponBean.get(person, getLocale(), null);
//        } catch (UnregisteredPersonException e) {
//            log.error("Ошибка создания документа", e);
//
//            return null;
//        }
//
//        Map<IReportField, Object> map = new HashMap<IReportField, Object>();
//
//        map.put(FIRST_NAME, coupon.getFirstName().toUpperCase());
//        map.put(LAST_NAME, coupon.getLastName().toUpperCase());
//        map.put(MIDDLE_NAME, coupon.getMiddleName().toUpperCase());
//        map.put(PREVIOUS_NAMES, coupon.getPreviousNames());
//        map.put(BIRTH_DATE, coupon.getBirthDate());
//        map.put(BIRTH_COUNTRY, coupon.getBirthCountry());
//        map.put(BIRTH_REGION, coupon.getBirthRegion());
//        map.put(BIRTH_DISTRICT, coupon.getBirthDistrict());
//        map.put(BIRTH_CITY, coupon.getBirthCity());
//        map.put(GENDER, Gender.MALE.equals(coupon.getGender()) ? "МУЖСКОЙ" : "ЖЕНСКИЙ");
//        map.put(ADDRESS_COUNTRY, coupon.getAddressCountry());
//        map.put(ADDRESS_REGION, coupon.getAddressRegion());
//        map.put(ADDRESS_DISTRICT, coupon.getAddressDistrict());
//        map.put(ADDRESS_CITY, coupon.getAddressCity());
//        map.put(ADDRESS_STREET, coupon.getAddressStreet());
//        map.put(ADDRESS_BUILDING, coupon.getAddressBuildingNumber());
//        map.put(ADDRESS_CORP, coupon.getAddressBuildingCorp());
//        map.put(ADDRESS_APARTMENT, coupon.getAddressApartment());
//        map.put(REGISTRATION_ORGANIZATION, coupon.getRegistrationOrganization());
//        map.put(DEPARTURE_COUNTRY, coupon.getDepartureCountry());
//        map.put(DEPARTURE_REGION, coupon.getDepartureRegion());
//        map.put(DEPARTURE_DISTRICT, coupon.getDepartureDistrict());
//        map.put(DEPARTURE_CITY, coupon.getDepartureCity());
//        map.put(DEPARTURE_STREET, coupon.getDepartureStreet());
//        map.put(DEPARTURE_BUILDING, coupon.getDepartureBuildingNumber());
//        map.put(DEPARTURE_CORP, coupon.getDepartureBuildingCorp());
//        map.put(DEPARTURE_APARTMENT, coupon.getDepartureApartment());
//        map.put(DEPARTURE_DATE, coupon.getDepartureDate());
//        map.put(PASSPORT, coupon.getPassportSerialNumber() + " " + coupon.getPassportNumber() + " " +
//                coupon.getPassportAcquisitionOrganization() + ", " + getString(coupon.getPassportAcquisitionDate()));
//        map.put(BIRTH_CERTIFICATE, coupon.getBirthCertificateInfo());
//        map.put(UKRAINE_CITIZENSHIP, "");
//        putMultilineValue(map, coupon.getChildrenInfo(), 50, CHILDREN0, CHILDREN1);
//        putMultilineValue(map, coupon.getAdditionalInfo(), 50, ADDITIONAL_INFO0, ADDITIONAL_INFO1);
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
//            String name = "Талон-" + person.getLastName() + "-" + getString(DateUtil.getCurrentDate());
//
//            return URLEncoder.encode(name, "utf-8");
//        } catch (UnsupportedEncodingException e) {
//            //oops
//        }
//
//        return "RegistrationStopCoupon";
//    }
//}
