package org.complitex.pspoffice.report.entity;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 15.06.11 18:37
 */
public enum FamilyAndCommunalApartmentInfoField implements IReportField{
    NAME, ADDRESS, KITCHEN_AREA, BATHROOM_AREA, TOILET_AREA, HALL_AREA,
    OTHER_SPACE_INFO, SHARED_AREA, FLOOR, NUMBER_OF_STOREYS, STOREROOM_AREA,
    BARN_AREA, OTHER_BUILDINGS;

    @Override
    public String getFieldName() {
        return name().toLowerCase();
    }
}
