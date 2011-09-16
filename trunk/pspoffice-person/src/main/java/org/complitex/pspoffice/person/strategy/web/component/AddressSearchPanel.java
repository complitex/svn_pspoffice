/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.component;

import com.google.common.base.Function;
import static com.google.common.collect.Lists.*;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.complitex.admin.service.UserBean;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.User;
import org.complitex.dictionary.entity.UserGroup;
import org.complitex.dictionary.entity.UserOrganization;
import org.complitex.dictionary.service.SessionBean;
import org.complitex.dictionary.web.component.ShowMode;
import org.complitex.dictionary.web.component.search.ISearchCallback;
import org.complitex.dictionary.web.component.search.SearchComponentState;
import org.complitex.dictionary.web.component.search.WiQuerySearchComponent;
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
    @EJB
    private UserBean userBean;

    public AddressSearchPanel(String id, SearchComponentState searchComponentState, List<String> searchFilters,
            ISearchCallback callback, ShowMode showMode, boolean enabled) {
        super(id);

        User user = getCurrentUser();
        add(isEnhanced(user) ? new EnhancedAddressSearchComponent(SEARCH_COMPONENT_ID, searchComponentState, searchFilters,
                callback, showMode, enabled, getUserOrganizationObjectIds(user)) {

            @Override
            protected void onUpdate(AjaxRequestTarget target, String entity) {
                super.onUpdate(target, entity);
                AddressSearchPanel.this.onUpdate(target, entity, getModelObject(entity));
            }
        }
                : new WiQuerySearchComponent("searchComponent", searchComponentState, searchFilters, callback, showMode, enabled) {

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

        User user = getCurrentUser();
        add(isEnhanced(user) ? new EnhancedAddressSearchComponent(SEARCH_COMPONENT_ID, searchComponentState,
                searchFilterSettings, callback, getUserOrganizationObjectIds(user)) {

            @Override
            protected void onUpdate(AjaxRequestTarget target, String entity) {
                super.onUpdate(target, entity);
                AddressSearchPanel.this.onUpdate(target, entity, getModelObject(entity));
            }
        }
                : new WiQuerySearchComponent("searchComponent", searchComponentState, searchFilterSettings, callback) {

            @Override
            protected void onUpdate(AjaxRequestTarget target, String entity) {
                super.onUpdate(target, entity);
                AddressSearchPanel.this.onUpdate(target, entity, getModelObject(entity));
            }
        });
    }

    private User getCurrentUser() {
        long userId = sessionBean.getCurrentUserId();
        return userBean.getUser(userId);
    }

    private boolean isEnhanced(User user) {
        List<UserGroup> userGroups = user.getUserGroups();
        if (userGroups == null) {
            return false;
        }
        boolean suit = false;
        for (UserGroup group : userGroups) {
            if (group.getGroupName().equals(UserGroup.GROUP_NAME.EMPLOYEES_CHILD_VIEW)
                    || group.getGroupName().equals(UserGroup.GROUP_NAME.ADMINISTRATORS)) {
                suit = true;
                break;
            }
        }
        return suit;
    }

    private List<Long> getUserOrganizationObjectIds(User user) {
        return newArrayList(transform(user.getUserOrganizations(), new Function<UserOrganization, Long>() {

            @Override
            public Long apply(UserOrganization userOrganization) {
                return userOrganization.getOrganizationObjectId();
            }
        }));
    }

    protected void onUpdate(AjaxRequestTarget target, String entity, DomainObject object) {
    }
}
