package org.complitex.pspoffice.person.download;

import org.apache.wicket.PageParameters;
import org.complitex.pspoffice.report.entity.IReportField;
import org.complitex.pspoffice.report.web.AbstractReportDownload;

import java.util.Map;

import static org.complitex.pspoffice.report.entity.FamilyAndCommunalApartmentInfoField.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 15.06.11 18:40
 */
public class FamilyAndCommunalApartmentInfoDownload extends AbstractReportDownload{
    public FamilyAndCommunalApartmentInfoDownload(final PageParameters parameters) {
        super("FamilyAndCommunalApartmentInfo", values(), parameters);
    }

    @Override
    protected Map<IReportField, Object> getValues(PageParameters parameters) {
        Map<IReportField, Object> map = newValuesMap();

        map.put(NAME, " ");
        map.put(ADDRESS, " ");
        map.put(KITCHEN_AREA, " ");
        map.put(BATHROOM_AREA, " ");
        map.put(TOILET_AREA, " ");
        map.put(HALL_AREA, " ");
        map.put(OTHER_SPACE_INFO, " ");
        map.put(SHARED_AREA, " ");
        map.put(FLOOR, " ");
        map.put(NUMBER_OF_STOREYS, " ");
        map.put(STOREROOM_AREA, " ");
        map.put(BARN_AREA, " ");
        map.put(OTHER_BUILDINGS, " ");

        return map;
    }

    @Override
    protected String getFileName(PageParameters parameters) {
        return "FamilyAndCommunalApartmentInfo";
    }
}
