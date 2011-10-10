package org.complitex.pspoffice.report.entity;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 15.06.11 18:37
 */
public enum FamilyAndCommunalApartmentInfoField implements IReportField{
    OWNER_COUNT1, OWNER_COUNT2,
    ADDRESS,
    ROOM0, ROOM1, ROOM2, ROOM3, ROOM4, ROOM5, ROOM6, ROOM7, ROOM8,
    OWNER_NAME0, OWNER_NAME1, OWNER_NAME2, OWNER_NAME3, OWNER_NAME4, OWNER_NAME5, OWNER_NAME6, OWNER_NAME7, OWNER_NAME8,
    ROOMS_AND_AREA_INFO0, ROOMS_AND_AREA_INFO1, ROOMS_AND_AREA_INFO2, ROOMS_AND_AREA_INFO3, ROOMS_AND_AREA_INFO4,
    ROOMS_AND_AREA_INFO5, ROOMS_AND_AREA_INFO6, ROOMS_AND_AREA_INFO7, ROOMS_AND_AREA_INFO8,
    OTHER_BUILDINGS_AND_AREA_INFO0, OTHER_BUILDINGS_AND_AREA_INFO1, OTHER_BUILDINGS_AND_AREA_INFO2,
    OTHER_BUILDINGS_AND_AREA_INFO3, OTHER_BUILDINGS_AND_AREA_INFO4, OTHER_BUILDINGS_AND_AREA_INFO5,
    OTHER_BUILDINGS_AND_AREA_INFO6, OTHER_BUILDINGS_AND_AREA_INFO7, OTHER_BUILDINGS_AND_AREA_INFO8,
    LOGGIA_AND_AREA_INFO0, LOGGIA_AND_AREA_INFO1, LOGGIA_AND_AREA_INFO2, LOGGIA_AND_AREA_INFO3, LOGGIA_AND_AREA_INFO4,
    LOGGIA_AND_AREA_INFO5, LOGGIA_AND_AREA_INFO6, LOGGIA_AND_AREA_INFO7, LOGGIA_AND_AREA_INFO8,
    KITCHEN_AREA, BATHROOM_AREA, TOILET_AREA, HALL_AREA,
    OTHER_SPACE_INFO, SHARED_AREA, FLOOR, NUMBER_OF_STOREYS,
    OWNER_NAME_1, OWNER_NAME_2,
    NAME0, NAME1, NAME2, NAME3, NAME4, NAME5,
    RELATION0, RELATION1, RELATION2, RELATION3, RELATION4, RELATION5,
    BIRTH_DATE0, BIRTH_DATE1, BIRTH_DATE2, BIRTH_DATE3, BIRTH_DATE4, BIRTH_DATE5,
    REGISTRATION_DATE0, REGISTRATION_DATE1, REGISTRATION_DATE2, REGISTRATION_DATE3, REGISTRATION_DATE4, REGISTRATION_DATE5,
    STOREROOM_AREA,
    BARN_AREA, OTHER_BUILDINGS;

    @Override
    public String getFieldName() {
        return name().toLowerCase();
    }
}
