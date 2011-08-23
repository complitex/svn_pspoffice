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
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionary.entity.Gender;
import org.complitex.dictionary.util.StringUtil;
import org.complitex.dictionary.web.component.type.GenderPanel;
import org.complitex.pspoffice.document.strategy.DocumentStrategy;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.web.edit.person.PersonEditPanel;
import org.complitex.resources.WebCommonResourceInitializer;
import org.odlabs.wiquery.core.javascript.JsStatement;
import org.odlabs.wiquery.ui.core.JsScopeUiEvent;

/**
 *
 * @author Artem
 */
public final class PersonPicker extends FormComponentPanel<Person> {

    private static class Dialog extends org.odlabs.wiquery.ui.dialog.Dialog {

        public Dialog(String id) {
            super(id);
            getOptions().putLiteral("width", "auto");
        }
    }
    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("dd.MM.yyyy");
    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private DocumentStrategy documentStrategy;
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

        add(JavascriptPackageResource.getHeaderContribution(WebCommonResourceInitializer.SCROLL_JS));

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
        lookupDialog.setOpenEvent(JsScopeUiEvent.quickScope(new JsStatement().self().chain("parents", "'.ui-dialog:first'").
                chain("find", "'.ui-dialog-titlebar-close'").
                chain("hide").render()));
        lookupDialog.setCloseOnEscape(false);
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

        //personContainer
        final WebMarkupContainer personContainer = new WebMarkupContainer("personContainer");
        personContainer.setOutputMarkupPlaceholderTag(true);
        content.add(personContainer);

        //personNotFoundContainer
        final WebMarkupContainer personNotFoundContainer = new WebMarkupContainer("personNotFoundContainer");
        personNotFoundContainer.setVisible(false);
        personContainer.add(personNotFoundContainer);

        //personsDataContainer
        final WebMarkupContainer personsDataContainer = new WebMarkupContainer("personsDataContainer");
        personsDataContainer.setVisible(false);
        personContainer.add(personsDataContainer);

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
                    clearAndCloseLookupDialog(personModel, personsModel, target, lookupDialog, content,
                            personNotFoundContainer, personsDataContainer, this);
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
        personsDataContainer.add(radioGroup);

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
                item.add(new Label("idCode", StringUtil.valueOf(person.getIdentityCode())));
                item.add(new Label("document", documentStrategy.displayDomainObject(person.getDocument(), getLocale())));
            }
        };
        radioGroup.add(persons);

        //find
        IndicatingAjaxLink<Void> find = new IndicatingAjaxLink<Void>("find") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                find(lastNameModel.getObject(), firstNameModel.getObject(), middleNameModel.getObject(), personModel,
                        personsModel);

                boolean isPersonsDataContainerVisible = personsModel.getObject() != null && !personsModel.getObject().isEmpty();
                personNotFoundContainer.setVisible(!isPersonsDataContainerVisible);
                personsDataContainer.setVisible(isPersonsDataContainerVisible);

                target.addComponent(personContainer);
                toggleSelectButton(select, target, personModel);
            }
        };
        content.add(find);

        //cancel
        AjaxLink<Void> cancel = new AjaxLink<Void>("cancel") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                clearAndCloseLookupDialog(personModel, personsModel,
                        target, lookupDialog, content, personNotFoundContainer, personsDataContainer, select);
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

        //person creation dialog
        final Dialog personCreationDialog = new Dialog("personCreationDialog");
        personCreationDialog.setModal(true);
        personCreationDialog.setOpenEvent(JsScopeUiEvent.quickScope(new JsStatement().self().chain("parents", "'.ui-dialog:first'").
                chain("find", "'.ui-dialog-titlebar-close'").
                chain("hide").render()));
        personCreationDialog.setCloseOnEscape(false);
        add(personCreationDialog);

        //person edit container
        final WebMarkupContainer personEditContainer = new WebMarkupContainer("personEditContainer");
        personEditContainer.setOutputMarkupId(true);
        personCreationDialog.add(personEditContainer);

        //person edit panel
        personEditContainer.add(new EmptyPanel("personEditPanel"));

        //create new
        AjaxLink<Void> createNew = new AjaxLink<Void>("createNew") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                clearAndCloseLookupDialog(personModel, personsModel,
                        target, lookupDialog, content, personNotFoundContainer, personsDataContainer, select);

                personEditContainer.replace(newPersonEditPanel(personCreationDialog, personEditContainer, personLabel));

                target.addComponent(personEditContainer);
                personCreationDialog.open(target);
            }
        };
        content.add(createNew);
    }

    private PersonEditPanel newPersonEditPanel(final Dialog personCreationDialog, final WebMarkupContainer personEditContainer,
            final Label personLabel) {
        return new PersonEditPanel("personEditPanel", null, personStrategy.newInstance()) {

            @Override
            protected void onSave(Person oldPerson, Person newPerson, AjaxRequestTarget target) {
                Person createdPerson = personStrategy.findById(newPerson.getId(), false, false, false);
                personStrategy.loadName(createdPerson);
                PersonPicker.this.getModel().setObject(createdPerson);

                target.addComponent(personLabel);
                this.onBack(target);
            }

            @Override
            protected void onBack(AjaxRequestTarget target) {
                personEditContainer.replace(new EmptyPanel("personEditPanel"));
                target.addComponent(personEditContainer);
                personCreationDialog.close(target);
            }
        };
    }

    private void clearAndCloseLookupDialog(IModel<Person> personModel, IModel<List<? extends Person>> personsModel,
            AjaxRequestTarget target, Dialog lookupDialog, WebMarkupContainer content,
            WebMarkupContainer personNotFoundContainer, WebMarkupContainer personsDataContainer, Component select) {
        personsModel.setObject(null);
        personModel.setObject(null);
        select.setVisible(false);
        personNotFoundContainer.setVisible(false);
        personsDataContainer.setVisible(false);

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
