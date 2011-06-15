package org.complitex.pspoffice.person.download;

import org.apache.wicket.PageParameters;
import org.complitex.pspoffice.report.entity.HousingPaymentsField;
import org.complitex.pspoffice.report.entity.IReportField;
import org.complitex.pspoffice.report.web.AbstractReportDownload;

import java.util.Map;

import static org.complitex.pspoffice.report.entity.HousingPaymentsField.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 15.06.11 16:51
 */
public class HousingPaymentsDownload extends AbstractReportDownload{
    public HousingPaymentsDownload(final PageParameters parameters) {
        super("HousingPayments", values(), parameters);
    }

    @Override
    protected Map<IReportField, Object> getValues(PageParameters parameters) {
        Map<IReportField, Object> map = newValuesMap();

        map.put(NAME, "");
        map.put(PERSONAL_ACCOUNT, "");
        map.put(ADDRESS, "");
        map.put(FORM_OF_OWNERSHIP, "");

        map.put(NAME0, "");
        map.put(RELATION0, "");
        map.put(BIRTH_DATE0, "");
        map.put(REGISTRATION_DATE0, "");

        map.put(FLOORS, "");
        map.put(LIFT, "");
        map.put(HOSTEL, "");
        map.put(FLOOR, "");
        map.put(STOVE_TYPE, "");
        map.put(APARTMENT_AREA, "");
        map.put(BALCONY_AREA, "");
        map.put(BENEFIT_PERSONS, "");
        map.put(NORMATIVE_AREA, "");
        map.put(ROOMS, "");
        map.put(BENEFITS, "");
        map.put(PAYMENTS_ADJUSTED_FOR_BENEFITS, "");
        map.put(NORMATIVE_PAYMENTS, "");
        map.put(APARTMENT_PAYMENTS, "");
        map.put(APARTMENT_TARIFF, "");
        map.put(HEAT_PAYMENTS, "");
        map.put(HEAT_TARIFF, "");
        map.put(GAS_PAYMENTS, "");
        map.put(GAS_TARIFF, "");
        map.put(COLD_WATER_PAYMENTS, "");
        map.put(COLD_WATER_TARIFF, "");
        map.put(HOT_WATER_PAYMENTS, "");
        map.put(HOT_WATER_TARIFF, "");
        map.put(OUTLET_PAYMENTS, "");
        map.put(OUTLET_TARIFF, "");
        map.put(COUNTERS_PRESENCE, "");
        map.put(DEBT, "");
        map.put(DEBT_MONTH, "");

        return map;
    }

    @Override
    protected String getFileName(PageParameters parameters) {
        return "HousingPayments";
    }
}
