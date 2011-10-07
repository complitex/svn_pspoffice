package org.complitex.pspoffice.person.download;

import org.complitex.pspoffice.report.entity.IReportField;
import org.complitex.pspoffice.report.web.AbstractReportDownload;

import java.util.Map;
import org.complitex.pspoffice.person.registration.report.entity.FamilyMember;
import org.complitex.pspoffice.person.registration.report.entity.HousingPayments;

import static org.complitex.pspoffice.report.entity.HousingPaymentsField.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 15.06.11 16:51
 */
public class HousingPaymentsDownload extends AbstractReportDownload<HousingPayments> {

    public HousingPaymentsDownload(HousingPayments report) {
        super("HousingPayments", values(), report);
    }

    @Override
    protected Map<IReportField, Object> getValues() {
        HousingPayments report = getReport();
        Map<IReportField, Object> map = newValuesMap();

        map.put(NAME, report.getName());
        map.put(PERSONAL_ACCOUNT, report.getPersonalAccount());
        map.put(ADDRESS, report.getAddress());
        map.put(FORM_OF_OWNERSHIP, report.getFormOfOwnership());

        int counter = 0;
        for (FamilyMember member : report.getFamilyMembers()) {
            switch (counter) {
                case 0: {
                    map.put(NAME0, member.getName());
                    map.put(RELATION0, member.getRelation());
                    map.put(BIRTH_DATE0, member.getBirthDate());
                    map.put(PASSPORT0, member.getPassport());
                }
                break;
                case 1: {
                    map.put(NAME1, member.getName());
                    map.put(RELATION1, member.getRelation());
                    map.put(BIRTH_DATE1, member.getBirthDate());
                    map.put(PASSPORT1, member.getPassport());
                }
                break;
                case 2: {
                    map.put(NAME2, member.getName());
                    map.put(RELATION2, member.getRelation());
                    map.put(BIRTH_DATE2, member.getBirthDate());
                    map.put(PASSPORT2, member.getPassport());
                }
                break;
                case 3: {
                    map.put(NAME3, member.getName());
                    map.put(RELATION3, member.getRelation());
                    map.put(BIRTH_DATE3, member.getBirthDate());
                    map.put(PASSPORT3, member.getPassport());
                }
                break;
                case 4: {
                    map.put(NAME4, member.getName());
                    map.put(RELATION4, member.getRelation());
                    map.put(BIRTH_DATE4, member.getBirthDate());
                    map.put(PASSPORT4, member.getPassport());
                }
                break;
                case 5: {
                    map.put(NAME5, member.getName());
                    map.put(RELATION5, member.getRelation());
                    map.put(BIRTH_DATE5, member.getBirthDate());
                    map.put(PASSPORT5, member.getPassport());
                }
                break;
                case 6: {
                    map.put(NAME6, member.getName());
                    map.put(RELATION6, member.getRelation());
                    map.put(BIRTH_DATE6, member.getBirthDate());
                    map.put(PASSPORT6, member.getPassport());
                }
                break;
            }
            counter++;
            if (counter > 6) {
                break;
            }
        }
        map.put(COUNT, String.valueOf(counter));

        map.put(FLOORS, report.getFloors());
        map.put(LIFT, report.getLift());
        map.put(HOSTEL, report.getHostel());
        map.put(FLOOR, report.getFloor());
        map.put(STOVE_TYPE, report.getStoveType());
        map.put(APARTMENT_AREA, report.getApartmentArea());
        map.put(BALCONY_AREA, report.getBalconyArea());
        map.put(BENEFIT_PERSONS, report.getBenefitPersons());
        map.put(NORMATIVE_AREA, report.getNormativeArea());
        map.put(ROOMS, report.getRooms());
        map.put(BENEFITS, report.getBenefits());
        map.put(PAYMENTS_ADJUSTED_FOR_BENEFITS, report.getPaymentsAdjustedForBenefits());
        map.put(NORMATIVE_PAYMENTS, report.getNormativePayments());
        map.put(APARTMENT_PAYMENTS, report.getApartmentPayments());
        map.put(APARTMENT_TARIFF, report.getApartmentTariff());
        map.put(HEAT_PAYMENTS, report.getHeatPayments());
        map.put(HEAT_TARIFF, report.getHeatTariff());
        map.put(GAS_PAYMENTS, report.getGasPayments());
        map.put(GAS_TARIFF, report.getGasTariff());
        map.put(COLD_WATER_PAYMENTS, report.getColdWaterPayments());
        map.put(COLD_WATER_TARIFF, report.getColdWaterTariff());
        map.put(HOT_WATER_PAYMENTS, report.getHotWaterPayments());
        map.put(HOT_WATER_TARIFF, report.getHotWaterTariff());
        map.put(OUTLET_PAYMENTS, report.getOutletPayments());
        map.put(OUTLET_TARIFF, report.getOutletTariff());
        map.put(COUNTERS_PRESENCE, report.getCountersPresence());
        map.put(DEBT, report.getDebt());
        map.put(DEBT_MONTH, report.getDebtMonth());

        return map;
    }

    @Override
    protected String getFileName() {
        return "HousingPayments";
    }
}
