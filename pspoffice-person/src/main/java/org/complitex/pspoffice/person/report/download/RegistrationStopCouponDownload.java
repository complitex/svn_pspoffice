package org.complitex.pspoffice.person.report.download;

import org.complitex.pspoffice.report.entity.IReportField;
import org.complitex.pspoffice.report.entity.RegistrationStopCouponField;
import org.complitex.pspoffice.report.web.AbstractReportDownload;

import java.util.HashMap;
import java.util.Map;
import org.complitex.pspoffice.person.report.entity.RegistrationStopCoupon;

import static org.complitex.pspoffice.report.entity.RegistrationStopCouponField.*;

public class RegistrationStopCouponDownload extends AbstractReportDownload<RegistrationStopCoupon> {

    public RegistrationStopCouponDownload(RegistrationStopCoupon report) {
        super("RegistrationStopCoupon", RegistrationStopCouponField.values(), report);
    }

    @Override
    protected Map<IReportField, Object> getValues() {
        RegistrationStopCoupon report = getReport();
        Map<IReportField, Object> map = new HashMap<IReportField, Object>();

        map.put(FIRST_NAME, report.getFirstName());
        map.put(LAST_NAME, report.getLastName());
        map.put(MIDDLE_NAME, report.getMiddleName());
        putMultilineValue(map, report.getPreviousNames(), 50, PREVIOUS_NAMES0, PREVIOUS_NAMES1);
        map.put(BIRTH_DATE, report.getBirthDate());
        map.put(BIRTH_COUNTRY, report.getBirthCountry());
        map.put(BIRTH_REGION, report.getBirthRegion());
        map.put(BIRTH_DISTRICT, report.getBirthDistrict());
        map.put(BIRTH_CITY, report.getBirthCity());
        map.put(GENDER, report.getGender());
        map.put(ADDRESS, report.getAddress());
        putMultilineValue(map, report.getRegistrationOrganization(), 50, REGISTRATION_ORGANIZATION0, REGISTRATION_ORGANIZATION1);
        map.put(DEPARTURE_COUNTRY, report.getDepartureCountry());
        map.put(DEPARTURE_REGION, report.getDepartureRegion());
        map.put(DEPARTURE_DISTRICT, report.getDepartureDistrict());
        map.put(DEPARTURE_CITY, report.getDepartureCity());
        map.put(DEPARTURE_DATE, report.getDepartureDate());
        putMultilineValue(map, report.getPassport(), 50, PASSPORT0, PASSPORT1, PASSPORT2);
        putMultilineValue(map, report.getBirthCertificateInfo(), 50, BIRTH_CERTIFICATE0, BIRTH_CERTIFICATE1);
        putMultilineValue(map, report.getChildrenInfo(), 50, CHILD0, CHILD1, CHILD2, CHILD3, CHILD4);
        putMultilineValue(map, report.getAdditionalInfo(), 50, ADDITIONAL_INFO0, ADDITIONAL_INFO1, ADDITIONAL_INFO2, ADDITIONAL_INFO3);

        return map;
    }

    @Override
    protected String getFileName() {
        return "RegistrationStopCoupon";
    }
}
