/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.edit;

import javax.ejb.EJB;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.complitex.dictionary.entity.Log;
import org.complitex.dictionary.service.LogBean;
import org.complitex.dictionary.service.StringCultureBean;
import org.complitex.dictionary.strategy.DeleteException;
import org.complitex.dictionary.strategy.web.DomainObjectAccessUtil;
import org.complitex.dictionary.util.CloneUtil;
import org.complitex.dictionary.util.DateUtil;
import org.complitex.dictionary.web.component.permission.DomainObjectPermissionsPanel;
import org.complitex.dictionary.web.component.permission.PermissionPropagationDialogPanel;
import org.complitex.pspoffice.person.Module;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.template.strategy.TemplateStrategy;
import org.complitex.template.web.pages.DomainObjectList;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.FormTemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public final class PersonEdit extends FormTemplatePage {

    private static final Logger log = LoggerFactory.getLogger(PersonEdit.class);
    @EJB
    private StringCultureBean stringBean;
    @EJB
    private LogBean logBean;
    @EJB
    private PersonStrategy personStrategy;

    private Person oldPerson;
    private Person newPerson;
    private PersonInputPanel personInputPanel;
    private FeedbackPanel messages;

    public PersonEdit(PageParameters parameters) {
        Long objectId = parameters.getAsLong(TemplateStrategy.OBJECT_ID);
        if (objectId == null) {
            //create new entity
            oldPerson = null;
            newPerson = personStrategy.newInstance();

        } else {
            //edit existing entity
            newPerson = personStrategy.findById(objectId, false);
            oldPerson = CloneUtil.cloneObject(newPerson);
        }
        init();
    }

    private void init() {
        IModel<String> labelModel = new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return stringBean.displayValue(personStrategy.getEntity().getEntityNames(), getLocale());
            }
        };
        Label title = new Label("title", labelModel);
        add(title);
        Label label = new Label("label", labelModel);
        add(label);

        messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        add(messages);

        Form form = new Form("form");

        //input panel
        personInputPanel = new PersonInputPanel("personInputPanel", newPerson);
        form.add(personInputPanel);

        //permissions panel
        DomainObjectPermissionsPanel permissionsPanel = new DomainObjectPermissionsPanel("permissionsPanel", newPerson.getSubjectIds());
        permissionsPanel.setEnabled(DomainObjectAccessUtil.canEdit(null, personStrategy.getEntityTable(), newPerson));
        form.add(permissionsPanel);

        //permissionPropagationDialogPanel
        final PermissionPropagationDialogPanel permissionPropagationDialogPanel =
                new PermissionPropagationDialogPanel("permissionPropagationDialogPanel") {

                    @Override
                    protected void applyPropagation(boolean propagate) {
                        try {
                            save(propagate);
                        } catch (Exception e) {
                            log.error("", e);
                            error(getString("db_error"));
                        }

                    }
                };
        add(permissionPropagationDialogPanel);

        //save-cancel functional
        AjaxSubmitLink submit = new AjaxSubmitLink("submit") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    if (validate()) {
                        if (isNew()) {
                            save(false);
                        } else {
                            boolean canPopagatePermissions = personStrategy.canPropagatePermissions(newPerson);
                            if (canPopagatePermissions && personStrategy.isNeedToChangePermission(oldPerson.getSubjectIds(),
                                    newPerson.getSubjectIds())) {
                                permissionPropagationDialogPanel.open(target);
                            } else {
                                save(false);
                            }
                        }
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
                target.addComponent(messages);
            }
        };
        submit.setVisible(DomainObjectAccessUtil.canEdit(null, personStrategy.getEntityTable(), newPerson));
        form.add(submit);
        Link cancel = new Link("cancel") {

            @Override
            public void onClick() {
                back();
            }
        };
        cancel.setVisible(DomainObjectAccessUtil.canEdit(null, personStrategy.getEntityTable(), newPerson));
        form.add(cancel);
        Link back = new Link("back") {

            @Override
            public void onClick() {
                back();
            }
        };
        back.setVisible(!DomainObjectAccessUtil.canEdit(null, personStrategy.getEntityTable(), newPerson));
        form.add(back);
        add(form);
    }

    private boolean validate() {
        return personInputPanel.validate();
    }

    private boolean isNew() {
        return oldPerson == null;
    }

    private void save(boolean propagate) {
        //permission related logic
        personInputPanel.beforePersist();
        if (isNew()) {
            personStrategy.insert(newPerson, DateUtil.getCurrentDate());
        } else {
            if (!propagate) {
                personStrategy.update(oldPerson, newPerson, DateUtil.getCurrentDate());
            } else {
                personStrategy.updateAndPropagate(oldPerson, newPerson, DateUtil.getCurrentDate());
            }
        }

        logBean.log(Log.STATUS.OK, Module.NAME, PersonEdit.class,
                isNew() ? Log.EVENT.CREATE : Log.EVENT.EDIT, personStrategy,
                oldPerson, newPerson, getLocale(), null);
        back();
    }

    private void back() {
        PageParameters listPageParams = personStrategy.getListPageParams();
        listPageParams.put(DomainObjectList.SCROLL_PARAMETER, newPerson.getId());
        setResponsePage(personStrategy.getListPage(), listPageParams);
    }

    public void disable() {
        try {
            personStrategy.disable(newPerson);
            back();
        } catch (Exception e) {
            log.error("", e);
            error(getString("db_error"));
        }
    }

    public void enable() {
        try {
            personStrategy.enable(newPerson);
            back();
        } catch (Exception e) {
            log.error("", e);
            error(getString("db_error"));
        }
    }

    public void delete() {
        try {
            personStrategy.delete(newPerson.getId());
            back();
        } catch (DeleteException e) {
            error(getString("delete_error"));
        } catch (Exception e) {
            log.error("", e);
            error(getString("db_error"));
        }
    }
}

