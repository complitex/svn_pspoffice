/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.history.apartment_card;

import java.util.Date;
import javax.ejb.EJB;
import org.apache.wicket.Component;
import org.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import org.complitex.pspoffice.person.strategy.web.history.AbstractHistoryPage;

/**
 *
 * @author Artem
 */
public final class ApartmentCardHistoryPage extends AbstractHistoryPage {

    @EJB
    private ApartmentCardStrategy apartmentCardStrategy;

    public ApartmentCardHistoryPage(long apartmentCardId) {
        super(apartmentCardId);
    }

    @Override
    protected Date getPreviousModificationDate(long objectId, Date currentEndDate) {
        return apartmentCardStrategy.getPreviousModificationDate(objectId, currentEndDate);
    }

    @Override
    protected Date getNextModificationDate(long objectId, Date currentEndDate) {
        return apartmentCardStrategy.getNextModificationDate(objectId, currentEndDate);
    }

    @Override
    protected Component newHistoryContent(String id, long objectId, Date currentEndDate) {
        return new ApartmentCardHistoryPanel(id, objectId, currentEndDate);
    }
}
