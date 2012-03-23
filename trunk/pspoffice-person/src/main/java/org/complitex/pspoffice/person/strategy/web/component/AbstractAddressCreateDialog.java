/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.component;

import java.text.MessageFormat;
import static com.google.common.collect.Sets.*;
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
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.complitex.address.service.AddressRendererBean;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.util.AddressNumberParser;
import org.odlabs.wiquery.core.javascript.JsStatement;
import org.odlabs.wiquery.ui.core.JsScopeUiEvent;
import org.odlabs.wiquery.ui.dialog.Dialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
public abstract class AbstractAddressCreateDialog extends Panel {

    private static final Logger log = LoggerFactory.getLogger(AbstractAddressCreateDialog.class);
    @EJB
    private AddressRendererBean addressRendererBean;
    private Dialog dialog;
    private IModel<String> numberModel;
    private WebMarkupContainer content;
    private Form<Void> form;
    private String parentEntity;
    private DomainObject parentObject;
    private List<Long> userOrganizationIds;
    private final Set<Long> subjectIds;

    protected AbstractAddressCreateDialog(String id, List<Long> userOrganizationIds) {
        super(id);
        this.userOrganizationIds = userOrganizationIds;
        this.subjectIds = newHashSet();
        init();
    }

    protected DomainObject getParentObject() {
        return parentObject;
    }

    protected String getParentEntity() {
        return parentEntity;
    }

    public void open(AjaxRequestTarget target, String number, String parentEntity, DomainObject parentObject) {
        this.parentEntity = parentEntity;
        this.parentObject = parentObject;
        this.numberModel.setObject(number);
        dialog.open(target);
        content.setVisible(true);
        subjectIds.clear();
        form.replace(newPermissionPanel(subjectIds, parentObject.getSubjectIds()));
        target.addComponent(content);
    }

    private void init() {
        dialog = new Dialog("dialog");
        dialog.setModal(true);
        dialog.setOpenEvent(JsScopeUiEvent.quickScope(new JsStatement().self().chain("parents", "'.ui-dialog:first'").
                chain("find", "'.ui-dialog-titlebar-close'").
                chain("hide").render()));
        dialog.setCloseOnEscape(false);
        dialog.setWidth(600);
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

        form = new Form<Void>("form");
        content.add(form);
        form.add(new Label("address", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return addressRendererBean.displayAddress(parentEntity, parentObject.getId(), getLocale());
            }
        }));
        final IModel<String> numberLabelModel = getNumberLabelModel();
        form.add(new Label("numberLabel", numberLabelModel));
        numberModel = new Model<String>();
        TextField<String> numberField = new RequiredTextField<String>("number", numberModel);
        numberField.setOutputMarkupId(true);
        numberField.setLabel(numberLabelModel);
        form.add(numberField);

        form.add(new EmptyPanel("permissionPanel").setRenderBodyOnly(true));

        form.add(new IndicatingAjaxButton("submit", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    if (AddressNumberParser.matches(numberModel.getObject())) {
                        final String[] numbers = AddressNumberParser.parse(numberModel.getObject());
                        if (numbers.length == 1) {
                            DomainObject object = initObject(numbers[0]);
                            object.setSubjectIds(subjectIds);
                            if (AbstractAddressCreateDialog.this.validate(object)) {
                                DomainObject saved = save(object);
                                close(target);
                                onCreate(target, saved);
                            } else {
                                target.addComponent(messages);
                            }
                        } else {
                            beforeBulkSave(numberModel.getObject());
                            boolean bulkOperationSuccess = true;
                            for (String number : numbers) {
                                DomainObject object = initObject(number);
                                object.setSubjectIds(subjectIds);
                                if (AbstractAddressCreateDialog.this.validate(object)) {
                                    try {
                                        bulkSave(object);
                                    } catch (Exception e) {
                                        bulkOperationSuccess = false;
                                        log.error("", e);
                                        onFailBulkSave(target, object, numberModel.getObject(), number);
                                    }
                                } else {
                                    onInvalidateBulkSave(target, object, numberModel.getObject(), number);
                                }
                            }
                            afterBulkSave(target, numberModel.getObject(), bulkOperationSuccess);

                            getSession().getFeedbackMessages().clear();

                            close(target);
                            onCancel(target);
                        }
                    } else {
                        error(MessageFormat.format(getString("invalid_number_format"), numberLabelModel.getObject()));
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

        form.add(new AjaxLink<Void>("cancel") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                close(target);
                onCancel(target);
            }
        });
    }

    private PermissionPanel newPermissionPanel(Set<Long> subjectIds, Set<Long> parentSubjectIds) {
        return new PermissionPanel("permissionPanel", userOrganizationIds, subjectIds, parentSubjectIds);
    }

    protected abstract String getTitle();

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

    private String getLabel() {
        return getTitle();
    }

    protected abstract DomainObject initObject(String numbers);

    protected abstract boolean validate(DomainObject object);

    protected abstract String getNumberLabel();

    protected void beforeBulkSave(String numbers) {
    }

    protected void afterBulkSave(AjaxRequestTarget target, String numbers, boolean operationSuccessed) {
    }

    protected void onFailBulkSave(AjaxRequestTarget target, DomainObject failObject, String numbers, String failNumber) {
    }

    protected void onInvalidateBulkSave(AjaxRequestTarget target, DomainObject invalidObject, String numbers, String invalidNumber) {
    }

    private IModel<String> getNumberLabelModel() {
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return getNumberLabel();
            }
        };
    }

    protected abstract void bulkSave(DomainObject object);

    protected abstract DomainObject save(DomainObject object);

    private void close(AjaxRequestTarget target) {
        dialog.close(target);
        content.setVisible(false);
        target.addComponent(content);
    }

    protected void onCancel(AjaxRequestTarget target) {
    }

    protected abstract void onCreate(AjaxRequestTarget target, DomainObject object);
}
