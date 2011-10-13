/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.report.download;

import java.util.Map;
import org.complitex.pspoffice.person.report.entity.F3Reference;
import org.complitex.pspoffice.person.report.entity.FamilyMember;
import org.complitex.pspoffice.person.report.entity.NeighbourFamily;
import org.complitex.pspoffice.report.entity.IReportField;
import org.complitex.pspoffice.report.web.AbstractReportDownload;
import static org.complitex.pspoffice.report.entity.F3ReferenceField.*;

/**
 *
 * @author Artem
 */
public class F3ReferenceDownload extends AbstractReportDownload<F3Reference> {

    public F3ReferenceDownload(F3Reference report) {
        super("F3Reference", values(), report);
    }

    @Override
    protected Map<IReportField, Object> getValues() {
        F3Reference report = getReport();
        Map<IReportField, Object> map = newValuesMap();

        map.put(NAME, report.getName());
        map.put(ADDRESS, report.getAddress());
        map.put(LIVING_AREA, report.getLivingArea());
        map.put(APARTMENT_AREA, report.getApartmentArea());
        map.put(TAKES_ROOMS, report.getTakesRooms());
        map.put(ROOMS, report.getRooms());
        map.put(FLOOR, report.getFloor());
        map.put(FLOORS, report.getFloors());
        map.put(PERSONAL_ACCOUNT_OWNER, report.getPersonalAccountOwnerName());
        map.put(FORM_OWNERSHIP, report.getFormOfOwnership());
        map.put(FACILITIES, report.getFacilities());
        map.put(TECHNICAL_STATE, report.getTechnicalState());

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
                case 7: {
                    map.put(NAME7, member.getName());
                    map.put(RELATION7, member.getRelation());
                    map.put(BIRTH_DATE7, member.getBirthDate());
                    map.put(REGISTRATION_DATE7, member.getRegistrationDate());
                }
                break;
            }
            counter++;
            if (counter > 7) {
                break;
            }
        }
        map.put(COUNT, String.valueOf(counter));

        counter = 0;
        for (NeighbourFamily family : report.getNeighbourFamilies()) {
            switch (counter) {
                case 0: {
                    map.put(FAMILY0, family.getName());
                    map.put(FAMILY_SIZE0, family.getAmount());
                    map.put(FAMILY_ROOMS0, family.getTakeRooms());
                    map.put(FAMILY_AREA0, family.getTakeArea());
                }
                break;
                case 1: {
                    map.put(FAMILY1, family.getName());
                    map.put(FAMILY_SIZE1, family.getAmount());
                    map.put(FAMILY_ROOMS1, family.getTakeRooms());
                    map.put(FAMILY_AREA1, family.getTakeArea());
                }
                break;
                case 2: {
                    map.put(FAMILY2, family.getName());
                    map.put(FAMILY_SIZE2, family.getAmount());
                    map.put(FAMILY_ROOMS2, family.getTakeRooms());
                    map.put(FAMILY_AREA2, family.getTakeArea());
                }
                break;
                case 3: {
                    map.put(FAMILY3, family.getName());
                    map.put(FAMILY_SIZE3, family.getAmount());
                    map.put(FAMILY_ROOMS3, family.getTakeRooms());
                    map.put(FAMILY_AREA3, family.getTakeArea());
                }
                break;
                case 4: {
                    map.put(FAMILY4, family.getName());
                    map.put(FAMILY_SIZE4, family.getAmount());
                    map.put(FAMILY_ROOMS4, family.getTakeRooms());
                    map.put(FAMILY_AREA4, family.getTakeArea());
                }
                break;
                case 5: {
                    map.put(FAMILY5, family.getName());
                    map.put(FAMILY_SIZE5, family.getAmount());
                    map.put(FAMILY_ROOMS5, family.getTakeRooms());
                    map.put(FAMILY_AREA5, family.getTakeArea());
                }
                break;
                case 6: {
                    map.put(FAMILY6, family.getName());
                    map.put(FAMILY_SIZE6, family.getAmount());
                    map.put(FAMILY_ROOMS6, family.getTakeRooms());
                    map.put(FAMILY_AREA6, family.getTakeArea());
                }
                break;
                case 7: {
                    map.put(FAMILY7, family.getName());
                    map.put(FAMILY_SIZE7, family.getAmount());
                    map.put(FAMILY_ROOMS7, family.getTakeRooms());
                    map.put(FAMILY_AREA7, family.getTakeArea());
                }
                break;
                case 8: {
                    map.put(FAMILY8, family.getName());
                    map.put(FAMILY_SIZE8, family.getAmount());
                    map.put(FAMILY_ROOMS8, family.getTakeRooms());
                    map.put(FAMILY_AREA8, family.getTakeArea());
                }
                break;
                case 9: {
                    map.put(FAMILY9, family.getName());
                    map.put(FAMILY_SIZE9, family.getAmount());
                    map.put(FAMILY_ROOMS9, family.getTakeRooms());
                    map.put(FAMILY_AREA9, family.getTakeArea());
                }
                break;
            }
            counter++;
            if (counter > 9) {
                break;
            }
        }

        return map;
    }

    @Override
    protected String getFileName() {
        return "F3Reference";
    }
}
