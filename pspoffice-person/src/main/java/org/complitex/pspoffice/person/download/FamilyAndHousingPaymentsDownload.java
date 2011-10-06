package org.complitex.pspoffice.person.download;

import org.complitex.pspoffice.report.entity.IReportField;
import org.complitex.pspoffice.report.web.AbstractReportDownload;

import java.util.Map;
import org.complitex.pspoffice.person.registration.report.entity.FamilyAndHousingPayments;
import org.complitex.pspoffice.person.registration.report.entity.FamilyMember;

import static org.complitex.pspoffice.report.entity.FamilyAndHousingPaymentsField.*;

public class FamilyAndHousingPaymentsDownload extends AbstractReportDownload<FamilyAndHousingPayments> {

    public FamilyAndHousingPaymentsDownload(FamilyAndHousingPayments report) {
        super("FamilyAndHousingPayments", values(), report);
    }

    @Override
    protected Map<IReportField, Object> getValues() {
        FamilyAndHousingPayments report = getReport();
        Map<IReportField, Object> map = newValuesMap();

        map.put(NAME, report.getName());
        map.put(PERSONAL_ACCOUNT, report.getPersonalAccount());
        map.put(ADDRESS, report.getAddress());
        map.put(FORM_OF_OWNERSHIP, report.getFormOfOwnership());
        map.put(STOVE_TYPE, report.getStoveType());

        int counter = 0;
        for (FamilyMember member : report.getFamilyMembers()) {
            switch (counter) {
                case 0: {
                    map.put(NAME0, member.getName());
                    map.put(RELATION0, member.getRelation());
                    map.put(BIRTH_DATE0, member.getBirthDate());
                    map.put(REGISTRATION_DATE0, member.getRegistrationDate());
                }
                break;
                case 1: {
                    map.put(NAME1, member.getName());
                    map.put(RELATION1, member.getRelation());
                    map.put(BIRTH_DATE1, member.getBirthDate());
                    map.put(REGISTRATION_DATE1, member.getRegistrationDate());
                }
                break;
                case 2: {
                    map.put(NAME2, member.getName());
                    map.put(RELATION2, member.getRelation());
                    map.put(BIRTH_DATE2, member.getBirthDate());
                    map.put(REGISTRATION_DATE2, member.getRegistrationDate());
                }
                break;
                case 3: {
                    map.put(NAME3, member.getName());
                    map.put(RELATION3, member.getRelation());
                    map.put(BIRTH_DATE3, member.getBirthDate());
                    map.put(REGISTRATION_DATE3, member.getRegistrationDate());
                }
                break;
                case 4: {
                    map.put(NAME4, member.getName());
                    map.put(RELATION4, member.getRelation());
                    map.put(BIRTH_DATE4, member.getBirthDate());
                    map.put(REGISTRATION_DATE4, member.getRegistrationDate());
                }
                break;
                case 5: {
                    map.put(NAME5, member.getName());
                    map.put(RELATION5, member.getRelation());
                    map.put(BIRTH_DATE5, member.getBirthDate());
                    map.put(REGISTRATION_DATE5, member.getRegistrationDate());
                }
                break;
                case 6: {
                    map.put(NAME6, member.getName());
                    map.put(RELATION6, member.getRelation());
                    map.put(BIRTH_DATE6, member.getBirthDate());
                    map.put(REGISTRATION_DATE6, member.getRegistrationDate());
                }
                break;
            }
            counter++;
            if (counter > 6) {
                break;
            }
        }

        map.put(APARTMENT_AREA, report.getApartmentArea());
        map.put(HEATED_AREA, report.getHeatedArea());
        map.put(NORMATIVE_AREA, report.getNormativeArea());
        map.put(ROOMS, report.getRooms());
        map.put(BENEFITS, report.getBenefits());
        map.put(PAYMENTS_ADJUSTED_FOR_BENEFITS, report.getPaymentsAdjustedForBenefits());
        map.put(NORMATIVE_PAYMENTS, report.getNormativePayments());
        map.put(APARTMENT_PAYMENTS, report.getApartmentPayments());
        map.put(HEAT_PAYMENTS, report.getHeatPayments());
        map.put(GAS_PAYMENTS, report.getGasPayments());
        map.put(COLD_WATER_PAYMENTS, report.getColdWaterPayments());
        map.put(HOT_WATER_PAYMENTS, report.getHotWaterPayments());
        map.put(DEBT, report.getDebt());
        map.put(DEBT_MONTH, report.getDebtMonth());

        return map;
    }

    @Override
    protected String getFileName() {
        return "FamilyAndHousingPayments";
    }
}
