/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.edit.person;

import java.util.Locale;
import javax.ejb.EJB;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.complitex.dictionary.entity.Log;
import org.complitex.dictionary.service.LogBean;
import org.complitex.dictionary.service.StringCultureBean;
import org.complitex.dictionary.util.DateUtil;
import org.complitex.dictionary.web.component.scroll.ScrollToElementUtil;
import org.complitex.pspoffice.person.Module;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.entity.PersonAgeType;
import org.complitex.resources.WebCommonResourceInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.complitex.dictionary.strategy.web.DomainObjectAccessUtil.*;

/**
 *
 * @author Artem
 */
public abstract class PersonEditPanel extends Panel {

    private static final Logger log = LoggerFactory.getLogger(PersonEditPanel.class);
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
//    private ReportDownloadPanel reportDownloadPanel;

    public PersonEditPanel(String id, PersonAgeType personAgeType, Person oldPerson, Person newPerson) {
        super(id);
        this.oldPerson = oldPerson;
        this.newPerson = newPerson;
        init(personAgeType, null, null, null, null);
    }

    public PersonEditPanel(String id, Person newPerson, PersonAgeType personAgeType,
            Locale defaultNameLocale, String defaultLastName, String defaultFirstName, String defaultMiddleName) {
        super(id);
        this.newPerson = newPerson;
        init(personAgeType, defaultNameLocale, defaultLastName, defaultFirstName, defaultMiddleName);
    }

    private void init(PersonAgeType personAgeType, Locale defaultNameLocale,
            String defaultLastName, String defaultFirstName, String defaultMiddleName) {
        add(JavascriptPackageResource.getHeaderContribution(WebCommonResourceInitializer.SCROLL_JS));

        final Label label = new Label("label", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return stringBean.displayValue(personStrategy.getEntity().getEntityNames(), getLocale());
            }
        });
        label.setOutputMarkupId(true);
        add(label);

        messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        add(messages);

        Form form = new Form("form");

        //input panel
        personInputPanel = new PersonInputPanel("personInputPanel", newPerson, messages, label, personAgeType,
                defaultNameLocale, defaultLastName, defaultFirstName, defaultMiddleName);
        form.add(personInputPanel);

        //save-cancel functional
        AjaxSubmitLink submit = new AjaxSubmitLink("submit") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    if (validate()) {
                        save();
                        onSave(oldPerson, newPerson, target);
                    } else {
                        target.addComponent(messages);
                        scrollToMessages(target);
                    }
                } catch (Exception e) {
                    log.error("", e);
                    error(getString("db_error"));
                    target.addComponent(messages);
                    scrollToMessages(target);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.addComponent(messages);
                scrollToMessages(target);
            }

            private void scrollToMessages(AjaxRequestTarget target) {
                target.appendJavascript(ScrollToElementUtil.scrollTo(label.getMarkupId()));
            }
        };
        submit.setVisible(canEdit(null, personStrategy.getEntityTable(), newPerson));
        form.add(submit);
        AjaxLink<Void> cancel = new AjaxLink<Void>("cancel") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                onCalcel(target);
            }
        };
        cancel.setVisible(canEdit(null, personStrategy.getEntityTable(), newPerson));
        form.add(cancel);
        AjaxLink<Void> back = new AjaxLink<Void>("back") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                onBack(target);
            }
        };
        back.setVisible(!canEdit(null, personStrategy.getEntityTable(), newPerson));
        form.add(back);
        add(form);

        //Загрузка отчетов
//        reportDownloadPanel = new ReportDownloadPanel("report_download", RegistrationCardDownload.class, newPerson.getId(), getString("report_download"));
//        add(reportDownloadPanel);
    }

    private boolean validate() {
        return personInputPanel.validate();
    }

    private boolean isNew() {
        return oldPerson == null;
    }

    private void save() {
        personInputPanel.beforePersist();
        if (isNew()) {
            personStrategy.insert(newPerson, DateUtil.getCurrentDate());
        } else {
            personStrategy.update(oldPerson, newPerson, DateUtil.getCurrentDate());
        }
        logBean.log(Log.STATUS.OK, Module.NAME, PersonEdit.class, isNew() ? Log.EVENT.CREATE : Log.EVENT.EDIT, personStrategy,
                oldPerson, newPerson, getLocale(), null);
    }

    protected abstract void onSave(Person oldPerson, Person newPerson, AjaxRequestTarget target);

    protected void onCalcel(AjaxRequestTarget target) {
        onBack(target);
    }

    protected abstract void onBack(AjaxRequestTarget target);
}
