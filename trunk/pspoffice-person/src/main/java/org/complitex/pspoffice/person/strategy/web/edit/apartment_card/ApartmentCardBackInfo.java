/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.edit.apartment_card;

import org.apache.wicket.Component;
import org.complitex.dictionary.web.component.back.BackInfo;

/**
 *
 * @author Artem
 */
public final class ApartmentCardBackInfo extends BackInfo {

    private final long apartmentCardId;

    public ApartmentCardBackInfo(long apartmentCardId) {
        this.apartmentCardId = apartmentCardId;
    }

    @Override
    public void back(Component pageComponent) {
        pageComponent.setResponsePage(new ApartmentCardEdit(apartmentCardId, null));
    }
}
