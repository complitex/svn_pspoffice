/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.component.autocomplete;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.complitex.address.service.AddressRendererBean;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.pspoffice.person.strategy.web.component.PermissionPanel;
import org.odlabs.wiquery.core.javascript.JsStatement;
import org.odlabs.wiquery.ui.autocomplete.Autocomplete;
import org.odlabs.wiquery.ui.core.JsScopeUiEvent;
import org.odlabs.wiquery.ui.dialog.Dialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
abstract class AbstractAddressCreateDialog extends Panel {

    private static final Logger log = LoggerFactory.getLogger(AbstractAddressCreateDialog.class);
    @EJB
    private AddressRendererBean addressRendererBean;
    private String entity;
    private Dialog dialog;
    private IModel<String> nameModel;
    private WebMarkupContainer content;
    private Autocomplete<String> autocomplete;
    private String parentEntity;
    private long parentId;
    private List<Long> userOrganizationIds;

    AbstractAddressCreateDialog(String id, Autocomplete<String> autocomplete, List<Long> userOrganizationIds) {
        super(id);
        this.autocomplete = autocomplete;
        this.userOrganizationIds = userOrganizationIds;
        init();
    }

    String getEntity() {
        return entity;
    }

    IModel<String> getNameModel() {
        return nameModel;
    }

    long getParentId() {
        return parentId;
    }

    String getParentEntity() {
        return parentEntity;
    }

    void open(AjaxRequestTarget target, String entity, String name, String parentEntity, long parentId) {
        this.entity = entity;
        this.parentEntity = parentEntity;
        this.parentId = parentId;
        this.nameModel.setObject(name);
        dialog.open(target);
        content.setVisible(true);
        target.addComponent(content);
    }

    void init() {
        dialog = new Dialog("dialog");
        dialog.setModal(true);
        dialog.setOpenEvent(JsScopeUiEvent.quickScope(new JsStatement().self().chain("parents", "'.ui-dialog:first'").
                chain("find", "'.ui-dialog-titlebar-close'").
                chain("hide").render()));
        dialog.setCloseOnEscape(false);
        dialog.setWidth(500);
        dialog.setTitle(getTitleModel());
        add(dialog);

        content = new WebMarkupContainer("content");
        content.setOutputMarkupPlaceholderTag(true);
        dialog.add(content);
        content.setVisible(false);

        content.add(new Label("label", getLabelModel()));
        final FeedbackPanel messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        content.add(messages);

        Form form = new Form("form");
        content.add(form);
        form.add(new Label("address", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return addressRendererBean.displayAddress(parentEntity, parentId, getLocale());
            }
        }));
        IModel<String> nameLabelModel = getNameLabelModel();
        form.add(new Label("nameLabel", nameLabelModel));
        nameModel = new Model<String>();
        TextField<String> nameField = new RequiredTextField<String>("name", nameModel);
        nameField.setOutputMarkupId(true);
        nameField.setLabel(nameLabelModel);
        form.add(nameField);

        final Set<Long> subjectIds = Sets.newHashSet();
        form.add(new PermissionPanel("permissionPanel", userOrganizationIds, subjectIds));

        form.add(new IndicatingAjaxButton("submit", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    DomainObject object = initObject();
                    object.setSubjectIds(subjectIds);
                    if (AbstractAddressCreateDialog.this.validate(object)) {
                        DomainObject saved = save(object);
                        close(target);
                        onCreate(target, saved);
                    } else {
                        target.addComponent(messages);
                    }
                } catch (Exception e) {
                    log.error("", e);
                    error(getString("db_error"));
                    target.addComponent(messages);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                super.onError(target, form);
                target.addComponent(messages);
            }
        });

        form.add(new AjaxLink("cancel") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                close(target);
                onCancel(target);
            }
        });
    }

    abstract String getTitle();

    private IModel<String> getTitleModel() {
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return getTitle();
            }
        };
    }

    private IModel<String> getLabelModel() {
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return getLabel();
            }
        };
    }

    String getLabel() {
        return getTitle();
    }

    abstract DomainObject initObject();

    abstract boolean validate(DomainObject object);

    abstract String getNameLabel();

    private IModel<String> getNameLabelModel() {
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return getNameLabel();
            }
        };
    }

    abstract DomainObject save(DomainObject object);

    void close(AjaxRequestTarget target) {
        dialog.close(target);
        content.setVisible(false);
        target.addComponent(content);
    }

    private void onCancel(AjaxRequestTarget target) {
        autocomplete.setModelObject(null);
        target.addComponent(autocomplete);
        target.focusComponent(autocomplete);
    }

    abstract void onCreate(AjaxRequestTarget target, DomainObject object);
}
