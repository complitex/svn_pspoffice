/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.web.template;

import javax.ejb.EJB;
import org.complitex.dictionary.service.SessionBean;
import org.complitex.pspoffice.person.strategy.web.list.apartment_card.ApartmentCardSearch;
import org.complitex.template.web.pages.welcome.WelcomePage;
import org.complitex.template.web.template.MenuManager;

/**
 *
 * @author Artem
 */
public final class PspWelcomePage extends WelcomePage {

    @EJB
    private SessionBean sessionBean;

    public PspWelcomePage() {
        if (!sessionBean.isAdmin()) {
            MenuManager.hideMainMenu();
            setRedirect(true);
            setResponsePage(ApartmentCardSearch.class);
        }
    }
}
