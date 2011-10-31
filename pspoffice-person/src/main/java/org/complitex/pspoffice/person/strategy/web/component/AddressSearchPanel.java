/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.component;

import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.service.SessionBean;
import org.complitex.dictionary.web.component.ShowMode;
import org.complitex.dictionary.web.component.search.ISearchCallback;
import org.complitex.dictionary.web.component.search.SearchComponentState;
import org.complitex.dictionary.web.component.search.WiQuerySearchComponent.SearchFilterSettings;
import org.complitex.pspoffice.person.strategy.web.component.autocomplete.EnhancedAddressSearchComponent;

/**
 *
 * @author Artem
 */
public class AddressSearchPanel extends Panel {

    private static final String SEARCH_COMPONENT_ID = "searchComponent";
    @EJB
    private SessionBean sessionBean;

    public AddressSearchPanel(String id, SearchComponentState searchComponentState, List<String> searchFilters,
            ISearchCallback callback, ShowMode showMode, boolean enabled) {
        super(id);

        add(new EnhancedAddressSearchComponent(SEARCH_COMPONENT_ID, searchComponentState, searchFilters,
                callback, showMode, enabled, sessionBean.getUserOrganizationObjectIds()) {

            @Override
            protected void onUpdate(AjaxRequestTarget target, String entity) {
                super.onUpdate(target, entity);
                AddressSearchPanel.this.onUpdate(target, entity, getModelObject(entity));
            }
        });
    }

    public AddressSearchPanel(String id, SearchComponentState searchComponentState,
            List<SearchFilterSettings> searchFilterSettings, ISearchCallback callback) {
        super(id);

        add(new EnhancedAddressSearchComponent(SEARCH_COMPONENT_ID, searchComponentState,
                searchFilterSettings, callback, sessionBean.getUserOrganizationObjectIds()) {

            @Override
            protected void onUpdate(AjaxRequestTarget target, String entity) {
                super.onUpdate(target, entity);
                AddressSearchPanel.this.onUpdate(target, entity, getModelObject(entity));
            }
        });
    }

    protected void onUpdate(AjaxRequestTarget target, String entity, DomainObject object) {
    }
}
