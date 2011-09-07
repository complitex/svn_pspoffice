/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.component;

import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.Application;
import org.apache.wicket.markup.html.panel.Panel;
import org.complitex.admin.service.UserBean;
import org.complitex.dictionary.entity.User;
import org.complitex.dictionary.service.SessionBean;
import org.complitex.dictionary.web.component.ShowMode;
import org.complitex.dictionary.web.component.search.ISearchCallback;
import org.complitex.dictionary.web.component.search.SearchComponentState;
import org.complitex.dictionary.web.component.search.WiQuerySearchComponent;
import org.complitex.dictionary.web.component.search.WiQuerySearchComponent.SearchFilterSettings;
import org.complitex.pspoffice.person.strategy.web.component.autocomplete.EnhancedAddressSearchComponent;
import org.complitex.template.web.template.TemplateWebApplication;

/**
 *
 * @author Artem
 */
public final class AddressSearchPanel extends Panel {

    private static final String SEARCH_COMPONENT_ID = "searchComponent";
    @EJB
    private SessionBean sessionBean;
    @EJB
    private UserBean userBean;

    public AddressSearchPanel(String id, SearchComponentState searchComponentState, List<String> searchFilters,
            ISearchCallback callback, ShowMode showMode, boolean enabled) {
        super(id);

        add(isEnhanced() ? new EnhancedAddressSearchComponent(SEARCH_COMPONENT_ID, searchComponentState, searchFilters,
                callback, showMode, enabled, getCurrentUser().getUserOrganizations())
                : new WiQuerySearchComponent("searchComponent", searchComponentState, searchFilters, callback, showMode, enabled));
    }

    public AddressSearchPanel(String id, SearchComponentState searchComponentState,
            List<SearchFilterSettings> searchFilterSettings, ISearchCallback callback) {
        super(id);

        add(isEnhanced() ? new EnhancedAddressSearchComponent(SEARCH_COMPONENT_ID, searchComponentState, searchFilterSettings,
                callback, getCurrentUser().getUserOrganizations())
                : new WiQuerySearchComponent("searchComponent", searchComponentState, searchFilterSettings, callback));
    }

    private boolean isEnhanced() {
        return !sessionBean.isAdmin() && ((TemplateWebApplication) Application.get()).hasAnyRole(
                SessionBean.CHILD_ORGANIZATION_VIEW_ROLE);
    }

    private User getCurrentUser() {
        long currentUserId = sessionBean.getCurrentUserId();
        return userBean.getUser(currentUserId);
    }
}
