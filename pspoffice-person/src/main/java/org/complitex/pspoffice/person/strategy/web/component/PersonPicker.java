/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionary.entity.Gender;
import org.complitex.dictionary.web.component.type.GenderPanel;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.odlabs.wiquery.core.javascript.JsStatement;
import org.odlabs.wiquery.ui.core.JsScopeUiEvent;
import org.odlabs.wiquery.ui.dialog.Dialog;

/**
 *
 * @author Artem
 */
public final class PersonPicker extends FormComponentPanel<Person> {

    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("dd.MM.yyyy");
    @EJB
    private PersonStrategy personStrategy;
    private final boolean enabled;
    private final boolean required;
    private final IModel<String> labelModel;

    public PersonPicker(String id, IModel<Person> model, boolean required, IModel<String> labelModel, boolean enabled) {
        super(id, model);
        this.required = required;
        this.enabled = enabled;
        this.labelModel = labelModel;
        init();
    }

    private void init() {
        setRequired(required);
        setLabel(labelModel);

        final Label personLabel = new Label("personLabel", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                Person person = getModelObject();
                if (person != null) {
                    return personStrategy.displayDomainObject(person, getLocale());
                } else {
                    return getString("person_not_selected");
                }
            }
        });
        personLabel.setOutputMarkupId(true);
        add(personLabel);

        //lookup dialog
        final Dialog lookupDialog = new Dialog("lookupDialog");
        lookupDialog.setModal(true);
        lookupDialog.setWidth(650);
        lookupDialog.setOpenEvent(JsScopeUiEvent.quickScope(new JsStatement().self().chain("parents", "'.ui-dialog:first'").
                chain("find", "'.ui-dialog-titlebar-close'").
                chain("hide").render()));
        lookupDialog.setCloseOnEscape(false);
        lookupDialog.setOutputMarkupId(true);
        add(lookupDialog);

        //content
        final WebMarkupContainer content = new WebMarkupContainer("content");
        content.setOutputMarkupId(true);
        lookupDialog.add(content);

        //last name
        final IModel<String> lastNameModel = new Model<String>();
        TextField<String> lastName = new TextField<String>("lastName", lastNameModel);
        lastName.add(new AjaxFormComponentUpdatingBehavior("onchange") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
            }
        });
        content.add(lastName);

        //first name
        final IModel<String> firstNameModel = new Model<String>();
        TextField<String> firstName = new TextField<String>("firstName", firstNameModel);
        firstName.add(new AjaxFormComponentUpdatingBehavior("onchange") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
            }
        });
        content.add(firstName);

        //middle name
        final IModel<String> middleNameModel = new Model<String>();
        TextField<String> middleName = new TextField<String>("middleName", middleNameModel);
        middleName.add(new AjaxFormComponentUpdatingBehavior("onchange") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
            }
        });
        content.add(middleName);

        //personsContainer
        final WebMarkupContainer personsContainer = new WebMarkupContainer("personsContainer");
        personsContainer.setOutputMarkupId(true);
        content.add(personsContainer);

        final IModel<List<? extends Person>> personsModel = Model.ofList(null);
        final IModel<Person> personModel = new Model<Person>();

        //select
        final AjaxLink<Void> select = new AjaxLink<Void>("select") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                if (personModel.getObject() == null) {
                    throw new IllegalStateException("Oops...");
                } else {
                    PersonPicker.this.getModel().setObject(personModel.getObject());
                    clearAndCloseLookupDialog(lastNameModel, firstNameModel, middleNameModel, personModel, personsModel,
                            target, lookupDialog, content, this);
                    target.addComponent(personLabel);
                }
            }
        };
        select.setOutputMarkupPlaceholderTag(true);
        select.setVisible(false);
        content.add(select);

        //radio group
        final RadioGroup<Person> radioGroup = new RadioGroup<Person>("radioGroup", personModel);
        radioGroup.add(new AjaxFormChoiceComponentUpdatingBehavior() {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                toggleSelectButton(select, target, personModel);
            }
        });
        personsContainer.add(radioGroup);

        //persons list view
        ListView<Person> persons = new ListView<Person>("persons", personsModel) {

            @Override
            protected void populateItem(ListItem<Person> item) {
                Person person = item.getModelObject();
                item.add(new Radio<Person>("radio", item.getModel(), radioGroup));
                item.add(new Label("fullName", personStrategy.displayDomainObject(person, getLocale())));
                Date birthDate = person.getBirthDate();
                item.add(new Label("birthDate", birthDate != null ? DATE_FORMATTER.format(birthDate) : null));
                Gender gender = person.getGender();
                item.add(new Label("gender", gender != null ? GenderPanel.display(person.getGender(), getLocale()) : null));
                item.add(new Label("idCode", ""));
            }
        };
        radioGroup.add(persons);

        //find
        AjaxLink<Void> find = new AjaxLink<Void>("find") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                find(lastNameModel.getObject(), firstNameModel.getObject(), middleNameModel.getObject(), personModel,
                        personsModel);

                target.addComponent(personsContainer);
                toggleSelectButton(select, target, personModel);
            }
        };
        content.add(find);

        //cancel
        AjaxLink<Void> cancel = new AjaxLink<Void>("cancel") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                clearAndCloseLookupDialog(lastNameModel, firstNameModel, middleNameModel, personModel, personsModel,
                        target, lookupDialog, content, select);
            }
        };
        content.add(cancel);

        //choose
        AjaxLink<Void> choose = new AjaxLink<Void>("choose") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                lookupDialog.open(target);
            }
        };
        choose.setVisible(enabled);
        add(choose);
    }

    private void clearAndCloseLookupDialog(IModel<String> lastNameModel, IModel<String> firstNameModel,
            IModel<String> middleNameModel, IModel<Person> personModel, IModel<List<? extends Person>> personsModel,
            AjaxRequestTarget target, Dialog lookupDialog, WebMarkupContainer content, Component select) {
        lastNameModel.setObject(null);
        firstNameModel.setObject(null);
        middleNameModel.setObject(null);
        personsModel.setObject(null);
        personModel.setObject(null);
        select.setVisible(false);

        target.addComponent(content);
        lookupDialog.close(target);
    }

    private void toggleSelectButton(Component select, AjaxRequestTarget target, IModel<Person> personModel) {
        boolean wasVisible = select.isVisible();
        select.setVisible(personModel.getObject() != null);
        if (select.isVisible() ^ wasVisible) {
            target.addComponent(select);
        }
    }

    private void find(String lastName, String firstName, String middleName, IModel<Person> personModel,
            IModel<List<? extends Person>> personsModel) {
        if (Strings.isEmpty(lastName)) {
            personsModel.setObject(null);
        } else {
            personsModel.setObject(personStrategy.findByName(lastName, firstName, middleName));
        }
        personModel.setObject(null);
    }

    @Override
    protected void convertInput() {
        setConvertedInput(getModelObject());
    }
}
