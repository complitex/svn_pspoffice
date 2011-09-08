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
import org.complitex.dictionary.service.PermissionBean;
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
    private static final DomainObject VISIBLE_BY_ALL = new DomainObject();

    static {
        VISIBLE_BY_ALL.setId(PermissionBean.VISIBLE_BY_ALL_PERMISSION_ID);
    }

    public PermissionPanel(String id, List<Long> userOrganizationIds, Set<Long> subjectIds) {
        super(id);
        init(userOrganizationIds, subjectIds);
    }

    private void init(final List<Long> userOrganizationIds, final Set<Long> subjectIds) {
        final Long existingSubjectId = subjectIds.isEmpty() ? null : subjectIds.iterator().next();
        final IModel<List<DomainObject>> userOrganizationsModel = new LoadableDetachableModel<List<DomainObject>>() {

            @Override
            protected List<DomainObject> load() {
                List<DomainObject> list = Lists.newArrayList();
                if (existingSubjectId == null) {
                    for (Long userOrganizationId : userOrganizationIds) {
                        list.add(organizationStrategy.findById(userOrganizationId, true));
                    }
                    if (list.isEmpty()) {
                        list.add(VISIBLE_BY_ALL);
                    }
                } else {
                    if (existingSubjectId == PermissionBean.VISIBLE_BY_ALL_PERMISSION_ID) {
                        list.add(VISIBLE_BY_ALL);
                    } else {
                        list.add(organizationStrategy.findById(existingSubjectId, true));
                    }
                }
                return list;
            }
        };
        DomainObjectDisableAwareRenderer renderer = new DomainObjectDisableAwareRenderer() {

            @Override
            public String getDisplayValue(DomainObject object) {
                if (object.getId().equals(VISIBLE_BY_ALL.getId())) {
                    return getString("visible_by_all");
                } else {
                    return organizationStrategy.displayDomainObject(object, getLocale());
                }
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

        if (existingSubjectId != null) {
            DomainObject selected = null;
            for (DomainObject organization : userOrganizationsModel.getObject()) {
                if (organization.getId().equals(existingSubjectId)) {
                    selected = organization;
                    break;
                }
            }
            model.setObject(selected);
            organizationPicker.setEnabled(false);
        } else if (userOrganizationsModel.getObject().size() == 1) {
            model.setObject(userOrganizationsModel.getObject().get(0));
            organizationPicker.setEnabled(false);
        }

        setRenderBodyOnly(true);
    }
}
