package org.complitex.pspoffice.person.download;

import org.apache.wicket.PageParameters;
import org.complitex.pspoffice.report.entity.FamilyAndApartmentInfoField;
import org.complitex.pspoffice.report.entity.IReportField;
import org.complitex.pspoffice.report.web.AbstractReportDownload;

import java.util.Map;

import static org.complitex.pspoffice.report.entity.FamilyAndApartmentInfoField.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 09.06.11 17:51
 */
public class FamilyAndApartmentInfoDownload extends AbstractReportDownload {
    public FamilyAndApartmentInfoDownload(final PageParameters parameters) {
        super("FamilyAndApartmentInfo", values(), parameters);
    }

    @Override
    protected Map<IReportField, Object> getValues(PageParameters parameters) {
        Map<IReportField, Object> map = newValuesMap();

        map.put(DATE , "");
        map.put(NUMBER , "");
        map.put(TELEPHONE , "");
        map.put(STREET , "");
        map.put(BUILDING , "");
        map.put(APARTMENT , "");

        map.put(NAME0 , "");
        map.put(RELATION0 , "");
        map.put(BIRTH_DATE0 , "");
        map.put(REGISTRATION_DATE0 , "");

        map.put(ROOMS , "");
        map.put(ROOMS_AREA , "");
        map.put(KITCHEN_AREA , "");
        map.put(BATHROOM_AREA , "");
        map.put(TOILET_AREA , "");
        map.put(HALL_AREA , "");
        map.put(VERANDA_AREA , "");
        map.put(EMBEDDED_AREA , "");
        map.put(BALCONY_AREA , "");
        map.put(LOGGIA_AREA , "");
        map.put(FULL_APARTMENT_AREA , "");
        map.put(STOREROOM_AREA , "");
        map.put(BARN_AREA , "");
        map.put(ANOTHER_BUILDINGS_INFO0 , "");
        map.put(ANOTHER_BUILDINGS_INFO1 , "");
        map.put(ADDITIONAL_INFORMATION , "");
        map.put(MAINTENANCE_YEAR , "");

        return map;
    }

    @Override
    protected String getFileName(PageParameters parameters) {
        return "FamilyAndApartmentInfo";
    }
}
