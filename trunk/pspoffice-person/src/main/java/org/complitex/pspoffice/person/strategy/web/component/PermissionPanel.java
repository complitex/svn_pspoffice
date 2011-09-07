/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.component;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.UserOrganization;
import org.complitex.dictionary.strategy.organization.IOrganizationStrategy;
import org.complitex.dictionary.web.component.DisableAwareDropDownChoice;
import org.complitex.dictionary.web.component.DomainObjectDisableAwareRenderer;

/**
 *
 * @author Artem
 */
public final class PermissionPanel extends Panel {

    @EJB(name = "OrganizationStrategy")
    private IOrganizationStrategy organizationStrategy;

    public PermissionPanel(String id, List<UserOrganization> userOrganizations, Set<Long> subjectIds) {
        super(id);
        init(userOrganizations, subjectIds);
    }

    private void init(final List<UserOrganization> userOrganizations, final Set<Long> subjectIds) {
        final IModel<List<DomainObject>> userOrganizationsModel = new LoadableDetachableModel<List<DomainObject>>() {

            @Override
            protected List<DomainObject> load() {
                List<DomainObject> list = Lists.newArrayList();
                for (UserOrganization userOrganization : userOrganizations) {
                    list.add(organizationStrategy.findById(userOrganization.getId(), true));
                }
                return list;
            }
        };
        DomainObjectDisableAwareRenderer renderer = new DomainObjectDisableAwareRenderer() {

            @Override
            public String getDisplayValue(DomainObject object) {
                return organizationStrategy.displayDomainObject(object, getLocale());
            }
        };

        IModel<DomainObject> model = new Model<DomainObject>() {

            @Override
            public void setObject(DomainObject userOrganization) {
                super.setObject(userOrganization);
                subjectIds.clear();
                subjectIds.add(userOrganization.getId());
            }
        };
        DisableAwareDropDownChoice<DomainObject> organizationPicker =
                new DisableAwareDropDownChoice<DomainObject>("organizationPicker", model, userOrganizationsModel, renderer);
        organizationPicker.setRequired(true);
        add(organizationPicker);

        if (userOrganizationsModel.getObject().size() == 1) {
            model.setObject(userOrganizationsModel.getObject().get(0));
            setVisible(false);
        }
    }
}
