package org.complitex.pspoffice.person.download;

import org.complitex.pspoffice.report.entity.IReportField;
import org.complitex.pspoffice.report.web.AbstractReportDownload;

import java.util.Map;
import org.complitex.pspoffice.person.registration.report.entity.FamilyAndCommunalApartmentInfo;
import org.complitex.pspoffice.person.registration.report.entity.FamilyMember;
import org.complitex.pspoffice.person.registration.report.entity.NeighbourFamily;

import static org.complitex.pspoffice.report.entity.FamilyAndCommunalApartmentInfoField.*;

public class FamilyAndCommunalApartmentInfoDownload extends AbstractReportDownload<FamilyAndCommunalApartmentInfo> {

    public FamilyAndCommunalApartmentInfoDownload(FamilyAndCommunalApartmentInfo report) {
        super("FamilyAndCommunalApartmentInfo", values(), report);
    }

    @Override
    protected Map<IReportField, Object> getValues() {
        FamilyAndCommunalApartmentInfo report = getReport();
        Map<IReportField, Object> map = newValuesMap();

        map.put(OWNER_COUNT1, report.getNeighbourFamilies().size());
        map.put(OWNER_COUNT2, report.getNeighbourFamilies().size());
        map.put(ADDRESS, report.getAddress());

        int counter = 0;
        for (NeighbourFamily family : report.getNeighbourFamilies()) {
            switch (counter) {
                case 0: {
                    map.put(APARTMENT0, family.getApartmentNumber());
                    map.put(OWNER_NAME0, family.getName());
                    map.put(ROOMS_AND_AREA_INFO0, family.getRoomsAndAreaInfo());
                    map.put(OTHER_BUILDINGS_AND_AREA_INFO0, family.getOtherBuildingsAndAreaInfo());
                    map.put(LOGGIA_AND_AREA_INFO0, family.getLoggiaAndAreaInfo());
                }
                break;
                case 1: {
                    map.put(APARTMENT1, family.getApartmentNumber());
                    map.put(OWNER_NAME1, family.getName());
                    map.put(ROOMS_AND_AREA_INFO1, family.getRoomsAndAreaInfo());
                    map.put(OTHER_BUILDINGS_AND_AREA_INFO1, family.getOtherBuildingsAndAreaInfo());
                    map.put(LOGGIA_AND_AREA_INFO1, family.getLoggiaAndAreaInfo());
                }
                break;
                case 2: {
                    map.put(APARTMENT2, family.getApartmentNumber());
                    map.put(OWNER_NAME2, family.getName());
                    map.put(ROOMS_AND_AREA_INFO2, family.getRoomsAndAreaInfo());
                    map.put(OTHER_BUILDINGS_AND_AREA_INFO2, family.getOtherBuildingsAndAreaInfo());
                    map.put(LOGGIA_AND_AREA_INFO2, family.getLoggiaAndAreaInfo());
                }
                break;
                case 3: {
                    map.put(APARTMENT3, family.getApartmentNumber());
                    map.put(OWNER_NAME3, family.getName());
                    map.put(ROOMS_AND_AREA_INFO3, family.getRoomsAndAreaInfo());
                    map.put(OTHER_BUILDINGS_AND_AREA_INFO3, family.getOtherBuildingsAndAreaInfo());
                    map.put(LOGGIA_AND_AREA_INFO3, family.getLoggiaAndAreaInfo());
                }
                break;
                case 4: {
                    map.put(APARTMENT4, family.getApartmentNumber());
                    map.put(OWNER_NAME4, family.getName());
                    map.put(ROOMS_AND_AREA_INFO4, family.getRoomsAndAreaInfo());
                    map.put(OTHER_BUILDINGS_AND_AREA_INFO4, family.getOtherBuildingsAndAreaInfo());
                    map.put(LOGGIA_AND_AREA_INFO4, family.getLoggiaAndAreaInfo());
                }
                break;
                case 5: {
                    map.put(APARTMENT5, family.getApartmentNumber());
                    map.put(OWNER_NAME5, family.getName());
                    map.put(ROOMS_AND_AREA_INFO5, family.getRoomsAndAreaInfo());
                    map.put(OTHER_BUILDINGS_AND_AREA_INFO5, family.getOtherBuildingsAndAreaInfo());
                    map.put(LOGGIA_AND_AREA_INFO5, family.getLoggiaAndAreaInfo());
                }
                break;
                case 6: {
                    map.put(APARTMENT6, family.getApartmentNumber());
                    map.put(OWNER_NAME6, family.getName());
                    map.put(ROOMS_AND_AREA_INFO6, family.getRoomsAndAreaInfo());
                    map.put(OTHER_BUILDINGS_AND_AREA_INFO6, family.getOtherBuildingsAndAreaInfo());
                    map.put(LOGGIA_AND_AREA_INFO6, family.getLoggiaAndAreaInfo());
                }
                break;
                case 7: {
                    map.put(APARTMENT7, family.getApartmentNumber());
                    map.put(OWNER_NAME7, family.getName());
                    map.put(ROOMS_AND_AREA_INFO7, family.getRoomsAndAreaInfo());
                    map.put(OTHER_BUILDINGS_AND_AREA_INFO7, family.getOtherBuildingsAndAreaInfo());
                    map.put(LOGGIA_AND_AREA_INFO7, family.getLoggiaAndAreaInfo());
                }
                break;
                case 8: {
                    map.put(APARTMENT8, family.getApartmentNumber());
                    map.put(OWNER_NAME8, family.getName());
                    map.put(ROOMS_AND_AREA_INFO8, family.getRoomsAndAreaInfo());
                    map.put(OTHER_BUILDINGS_AND_AREA_INFO8, family.getOtherBuildingsAndAreaInfo());
                    map.put(LOGGIA_AND_AREA_INFO8, family.getLoggiaAndAreaInfo());
                }
                break;
            }
            counter++;
            if (counter > 8) {
                break;
            }
        }
        map.put(KITCHEN_AREA, report.getKitchenArea());
        map.put(BATHROOM_AREA, report.getBathroomArea());
        map.put(TOILET_AREA, report.getToiletArea());
        map.put(HALL_AREA, report.getHallArea());
        map.put(OTHER_SPACE_INFO, report.getOtherSpaceInfo());
        map.put(SHARED_AREA, report.getSharedArea());
        map.put(FLOOR, report.getFloor());
        map.put(NUMBER_OF_STOREYS, report.getNumberOfStoreys());

        map.put(OWNER_NAME_1, report.getName());
        map.put(OWNER_NAME_2, report.getName());

        counter = 0;
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
            }
            counter++;
            if (counter > 5) {
                break;
            }
        }

        map.put(STOREROOM_AREA, report.getStoreroomArea());
        map.put(BARN_AREA, report.getBarnArea());
        putMultilineValue(map, report.getOtherBuildings(), 50, OTHER_BUILDINGS0, OTHER_BUILDINGS1);

        return map;
    }

    @Override
    protected String getFileName() {
        return "FamilyAndCommunalApartmentInfo";
    }
}
