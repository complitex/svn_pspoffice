/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.edit;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import java.text.MessageFormat;
import java.util.Collection;
import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.*;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.complitex.dictionary.converter.BooleanConverter;
import org.complitex.dictionary.converter.DateConverter;
import org.complitex.dictionary.converter.DoubleConverter;
import org.complitex.dictionary.converter.GenderConverter;
import org.complitex.dictionary.converter.IntegerConverter;
import org.complitex.dictionary.converter.StringConverter;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.Gender;
import org.complitex.dictionary.entity.SimpleTypes;
import org.complitex.dictionary.entity.StringCulture;
import org.complitex.dictionary.entity.description.Entity;
import org.complitex.dictionary.entity.description.EntityAttributeType;
import org.complitex.dictionary.service.StringCultureBean;
import static org.complitex.dictionary.strategy.web.DomainObjectAccessUtil.*;
import org.complitex.dictionary.web.component.DomainObjectInputPanel.SimpleTypeModel;
import org.complitex.dictionary.web.component.ShowMode;
import org.complitex.dictionary.web.component.list.AjaxRemovableListView;
import org.complitex.dictionary.web.component.name.FullNamePanel;
import org.complitex.dictionary.web.component.scroll.ScrollToElementUtil;
import org.complitex.dictionary.web.component.search.SearchComponent;
import org.complitex.dictionary.web.component.search.SearchComponentState;
import org.complitex.dictionary.web.component.type.BigStringPanel;
import org.complitex.dictionary.web.component.type.BooleanPanel;
import org.complitex.dictionary.web.component.type.Date2Panel;
import org.complitex.dictionary.web.component.type.DatePanel;
import org.complitex.dictionary.web.component.type.DoublePanel;
import org.complitex.dictionary.web.component.type.GenderPanel;
import org.complitex.dictionary.web.component.type.IntegerPanel;
import org.complitex.dictionary.web.component.type.StringCulturePanel;
import org.complitex.dictionary.web.component.type.StringPanel;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.RegistrationStrategy;
import static org.complitex.pspoffice.person.strategy.PersonStrategy.*;
import org.complitex.pspoffice.person.strategy.entity.Person;

/**
 *
 * @author Artem
 */
public final class PersonInputPanel extends Panel {

    private static final String REGISTRATION_PANEL_ID = "registrationPanel";
    private static final String REGISTRATION_FOCUS_JS = "$('#" + REGISTRATION_PANEL_ID + " input[type=\"text\"]:enabled:first').focus()";
    @EJB
    private StringCultureBean stringBean;
    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private RegistrationStrategy registrationStrategy;
    private Person person;
    private RegistrationInputPanel registrationInputPanel;
    private Date date;
    private FeedbackPanel messages;

    public PersonInputPanel(String id, Person person, Date date) {
        super(id);
        this.person = person;
        this.date = date;
        init();
    }

    public PersonInputPanel(String id, Person person, FeedbackPanel messages) {
        super(id);
        this.person = person;
        this.messages = messages;
        init();
    }

    private boolean isNew() {
        return person.getId() == null;
    }

    private boolean isHistory() {
        return date != null;
    }

    private void init() {
        //full name:
        FullNamePanel fullNamePanel = new FullNamePanel("fullNamePanel", newNameModel(FIRST_NAME), newNameModel(MIDDLE_NAME),
                newNameModel(LAST_NAME));
        fullNamePanel.setEnabled(!isHistory() && canEdit(null, personStrategy.getEntityTable(), person));
        add(fullNamePanel);

        Entity entity = personStrategy.getEntity();

        //registration panel:
        Label registrationLabel = new Label("registrationLabel", newLabelModel(entity.getAttributeType(REGISTRATION).getAttributeNames()));
        registrationLabel.setOutputMarkupId(true);
        final String registrationLabelMarkupId = registrationLabel.getMarkupId();
        add(registrationLabel);
        Form registrationForm = new Form("registrationForm");
        add(registrationForm);
        final WebMarkupContainer registrationContainer = new WebMarkupContainer("registrationContainer");
        registrationContainer.setOutputMarkupId(true);
        registrationForm.add(registrationContainer);

        if (person.getRegistration() != null) {
            registrationInputPanel = new RegistrationInputPanel(REGISTRATION_PANEL_ID, person.getRegistration(), date);
            registrationContainer.add(registrationInputPanel);
        } else {
            registrationContainer.add(new NoRegistrationPanel(REGISTRATION_PANEL_ID));
        }

        final WebMarkupContainer registrationControlContainer = new WebMarkupContainer("registrationControlContainer");
        registrationControlContainer.setOutputMarkupPlaceholderTag(true);
        registrationContainer.add(registrationControlContainer);
        AjaxLink<Void> addRegistration = new AjaxLink<Void>("addRegistration") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                person.setRegistration(registrationStrategy.newInstance());
                registrationInputPanel = new RegistrationInputPanel(REGISTRATION_PANEL_ID, person.getRegistration(), date);
                updateRegistrationContainer(registrationContainer, registrationControlContainer, registrationInputPanel,
                        target, registrationLabelMarkupId);
            }
        };
        addRegistration.setVisible(!isHistory() && (person.getRegistration() == null)
                && canEdit(null, personStrategy.getEntityTable(), person));
        registrationControlContainer.add(addRegistration);
        AjaxSubmitLink changeRegistration = new AjaxSubmitLink("changeRegistration", registrationForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if (registrationInputPanel.validate()) {
                    registrationInputPanel.beforePersist();
                    person.setNewRegistration(registrationStrategy.newInstance());
                    registrationInputPanel = new RegistrationInputPanel(REGISTRATION_PANEL_ID, person.getNewRegistration(), date);
                    updateRegistrationContainer(registrationContainer, registrationControlContainer, registrationInputPanel,
                            target, registrationLabelMarkupId);
                } else {
                    target.appendJavascript(ScrollToElementUtil.scrollTo(messages.getMarkupId()));
                }
                target.addComponent(messages);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.appendJavascript(ScrollToElementUtil.scrollTo(messages.getMarkupId()));
                target.addComponent(messages);
            }
        };
        changeRegistration.setVisible(!isHistory() && (person.getRegistration() != null)
                && !isNew() && canEdit(null, personStrategy.getEntityTable(), person));
        registrationControlContainer.add(changeRegistration);
        AjaxSubmitLink closeRegistration = new AjaxSubmitLink("closeRegistration", registrationForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if (registrationInputPanel.validate()) {
                    registrationInputPanel.beforePersist();
                    person.setRegistrationClosed(true);
                    updateRegistrationContainer(registrationContainer, registrationControlContainer,
                            new NoRegistrationPanel(REGISTRATION_PANEL_ID), target, registrationLabelMarkupId);
                } else {
                    target.appendJavascript(ScrollToElementUtil.scrollTo(messages.getMarkupId()));
                }
                target.addComponent(messages);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.appendJavascript(ScrollToElementUtil.scrollTo(messages.getMarkupId()));
                target.addComponent(messages);
            }
        };
        closeRegistration.setVisible(!isHistory() && (person.getRegistration() != null)
                && !isNew() && canEdit(null, personStrategy.getEntityTable(), person));
        registrationControlContainer.add(closeRegistration);

        //system attributes:
        initSystemAttributeInput(this, "birthRegion", BIRTH_REGION, true);
        initSystemAttributeInput(this, "birthDistrict", BIRTH_DISTRICT, false);
        initSystemAttributeInput(this, "birthCity", BIRTH_CITY, true);
        initSystemAttributeInput(this, "birthVillage", BIRTH_VILLAGE, false);
        initSystemAttributeInput(this, "birthDate", BIRTH_DATE, false);
        WebMarkupContainer passportInfoContainer = new WebMarkupContainer("passportInfoContainer");
        add(passportInfoContainer);
        initSystemAttributeInput(passportInfoContainer, "passportSerialNumber", PASSPORT_SERIAL_NUMBER, false);
        initSystemAttributeInput(passportInfoContainer, "passportNumber", PASSPORT_NUMBER, false);
        initSystemAttributeInput(passportInfoContainer, "passportAcquisitionInfo", PASSPORT_ACQUISITION_INFO, false);
        if (isHistory() && (person.getAttribute(PASSPORT_SERIAL_NUMBER) == null) && (person.getAttribute(PASSPORT_NUMBER) == null)
                && (person.getAttribute(PASSPORT_ACQUISITION_INFO) == null)) {
            passportInfoContainer.setVisible(false);
        }
        initSystemAttributeInput(this, "gender", GENDER, false);
        initSystemAttributeInput(this, "nationality", NATIONALITY, false);
        initSystemAttributeInput(this, "jobInfo", JOB_INFO, false);
        initSystemAttributeInput(this, "militaryServiceRelation", MILITARY_SERVISE_RELATION, false);

        //user attributes:
        List<Long> userAttributeTypeIds = newArrayList(transform(filter(entity.getEntityAttributeTypes(),
                new Predicate<EntityAttributeType>() {

                    @Override
                    public boolean apply(EntityAttributeType attributeType) {
                        return !attributeType.isSystem();
                    }
                }),
                new Function<EntityAttributeType, Long>() {

                    @Override
                    public Long apply(EntityAttributeType attributeType) {
                        return attributeType.getId();
                    }
                }));

        List<Attribute> userAttributes = newArrayList();
        for (Long attributeTypeId : userAttributeTypeIds) {
            Attribute userAttribute = person.getAttribute(attributeTypeId);
            userAttributes.add(userAttribute);
        }

        ListView<Attribute> userAttributesView = new ListView<Attribute>("userAttributesView", userAttributes) {

            @Override
            protected void populateItem(ListItem<Attribute> item) {
                long userAttributeTypeId = item.getModelObject().getAttributeTypeId();
                initAttributeInput(item, userAttributeTypeId, false);
            }
        };
        add(userAttributesView);

        //children
        WebMarkupContainer childrenFieldsetContainer = new WebMarkupContainer("childrenFieldsetContainer");
        add(childrenFieldsetContainer);
        childrenFieldsetContainer.add(new Label("childrenLabel", newLabelModel(entity.getAttributeType(CHILDREN).getAttributeNames())));
        final WebMarkupContainer childrenContainer = new WebMarkupContainer("childrenContainer");
        childrenContainer.setOutputMarkupId(true);
        childrenFieldsetContainer.add(childrenContainer);
        ListView<Person> children = new AjaxRemovableListView<Person>("children", person.getChildren()) {

            @Override
            protected void populateItem(ListItem<Person> item) {
                final WebMarkupContainer fakeContainer = new WebMarkupContainer("fakeContainer");
                item.add(fakeContainer);
                item.add(new Label("label", new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        return MessageFormat.format(getString("children_number"), getCurrentIndex(fakeContainer) + 1);
                    }
                }));

                SearchComponentState<Person> searchComponentState = new SearchComponentState<Person>() {

                    @Override
                    public void put(String entity, Person child) {
                        super.put(entity, child);
                        int index = getCurrentIndex(fakeContainer);
                        person.setChild(index, child);
                    }
                };
                Person child = item.getModelObject();
                if (child != null) {
                    searchComponentState.getState().put(personStrategy.getEntityTable(), child);
                }

                SearchComponent searchChildComponent = new SearchComponent("searchChildComponent", searchComponentState,
                        ImmutableList.of(personStrategy.getEntityTable()), null, ShowMode.ACTIVE,
                        !isHistory() && canEdit(null, personStrategy.getEntityTable(), person));
                item.add(searchChildComponent);

                addRemoveLink("removeChild", item, null, childrenContainer).
                        setVisible(!isHistory() && canEdit(null, personStrategy.getEntityTable(), person));
            }
        };
        AjaxLink<Void> addChild = new AjaxLink<Void>("addChild") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                Person newChild = null;
                person.addChild(newChild);
                target.addComponent(childrenContainer);
            }
        };
        addChild.setVisible(!isHistory() && canEdit(null, personStrategy.getEntityTable(), person));
        childrenFieldsetContainer.add(addChild);
        childrenContainer.add(children);
        if (isHistory() && person.getChildren().isEmpty()) {
            childrenFieldsetContainer.setVisible(false);
        }
    }

    private IModel<Long> newNameModel(final long attributeTypeId) {
        return new Model<Long>() {

            @Override
            public Long getObject() {
                return person.getAttribute(attributeTypeId).getValueId();
            }

            @Override
            public void setObject(Long object) {
                person.getAttribute(attributeTypeId).setValueId(object);
            }
        };
    }

    private void updateRegistrationContainer(WebMarkupContainer registrationContainer,
            WebMarkupContainer registrationControlContainer, Panel contentPanel, AjaxRequestTarget target, String scrollToElementId) {
        registrationContainer.get(REGISTRATION_PANEL_ID).replaceWith(contentPanel);
        registrationControlContainer.setVisible(false);
        target.appendJavascript(ScrollToElementUtil.scrollTo(scrollToElementId));
        target.appendJavascript(REGISTRATION_FOCUS_JS);
        target.addComponent(registrationContainer);
    }

    private void initSystemAttributeInput(MarkupContainer parent, String id, long attributeTypeId, boolean showIfMissing) {
        WebMarkupContainer container = new WebMarkupContainer(id + "Container");
        parent.add(container);
        initAttributeInput(container, attributeTypeId, showIfMissing);
    }

    private IModel<String> newLabelModel(final List<StringCulture> attributeTypeNames) {
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return stringBean.displayValue(attributeTypeNames, getLocale());
            }
        };
    }

    private void initAttributeInput(MarkupContainer parent, long attributeTypeId, boolean showIfMissing) {
        final EntityAttributeType attributeType = personStrategy.getEntity().getAttributeType(attributeTypeId);

        //label
        IModel<String> labelModel = newLabelModel(attributeType.getAttributeNames());
        parent.add(new Label("label", labelModel));

        //required container
        WebMarkupContainer requiredContainer = new WebMarkupContainer("required");
        requiredContainer.setVisible(attributeType.isMandatory());
        parent.add(requiredContainer);

        //input component
        Attribute attribute = person.getAttribute(attributeTypeId);
        if (attribute == null) {
            attribute = new Attribute();
            attribute.setLocalizedValues(stringBean.newStringCultures());
            parent.setVisible(showIfMissing);
        }

        String valueType = attributeType.getEntityAttributeValueTypes().get(0).getValueType();
        SimpleTypes type = SimpleTypes.valueOf(valueType.toUpperCase());

        Component input = null;
        final StringCulture systemLocaleStringCulture = stringBean.getSystemStringCulture(attribute.getLocalizedValues());
        switch (type) {
            case STRING: {
                IModel<String> model = new SimpleTypeModel<String>(systemLocaleStringCulture, new StringConverter());
                input = new StringPanel("input", model, attributeType.isMandatory(), labelModel,
                        !isHistory() && canEdit(null, personStrategy.getEntityTable(), person));
            }
            break;
            case BIG_STRING: {
                IModel<String> model = new SimpleTypeModel<String>(systemLocaleStringCulture, new StringConverter());
                input = new BigStringPanel("input", model, attributeType.isMandatory(), labelModel,
                        !isHistory() && canEdit(null, personStrategy.getEntityTable(), person));
            }
            break;
            case STRING_CULTURE: {
                IModel<List<StringCulture>> model = new PropertyModel<List<StringCulture>>(attribute, "localizedValues");
                input = new StringCulturePanel("input", model, attributeType.isMandatory(), labelModel,
                        !isHistory() && canEdit(null, personStrategy.getEntityTable(), person));
            }
            break;
            case INTEGER: {
                IModel<Integer> model = new SimpleTypeModel<Integer>(systemLocaleStringCulture, new IntegerConverter());
                input = new IntegerPanel("input", model, attributeType.isMandatory(), labelModel,
                        !isHistory() && canEdit(null, personStrategy.getEntityTable(), person));
            }
            break;
            case DATE: {
                IModel<Date> model = new SimpleTypeModel<Date>(systemLocaleStringCulture, new DateConverter());
                input = new DatePanel("input", model, attributeType.isMandatory(), labelModel,
                        !isHistory() && canEdit(null, personStrategy.getEntityTable(), person));
            }
            break;
            case DATE2: {
                IModel<Date> model = new SimpleTypeModel<Date>(systemLocaleStringCulture, new DateConverter());
                input = new Date2Panel("input", model, attributeType.isMandatory(), labelModel,
                        !isHistory() && canEdit(null, personStrategy.getEntityTable(), person));
            }
            break;
            case BOOLEAN: {
                IModel<Boolean> model = new SimpleTypeModel<Boolean>(systemLocaleStringCulture, new BooleanConverter());
                input = new BooleanPanel("input", model, labelModel,
                        !isHistory() && canEdit(null, personStrategy.getEntityTable(), person));
            }
            break;
            case DOUBLE: {
                IModel<Double> model = new SimpleTypeModel<Double>(systemLocaleStringCulture, new DoubleConverter());
                input = new DoublePanel("input", model, attributeType.isMandatory(), labelModel,
                        !isHistory() && canEdit(null, personStrategy.getEntityTable(), person));
            }
            break;
            case GENDER: {
                IModel<Gender> model = new SimpleTypeModel<Gender>(systemLocaleStringCulture, new GenderConverter());
                input = new GenderPanel("input", model, attributeType.isMandatory(), labelModel,
                        !isHistory() && canEdit(null, personStrategy.getEntityTable(), person));
            }
            break;
        }
        parent.add(input);
    }

    public void beforePersist() {
        if (registrationInputPanel != null) {
            registrationInputPanel.beforePersist();
        }
    }

    public boolean validate() {
        boolean childrenValid = validateChildren();
        boolean registrationValid = registrationInputPanel != null ? registrationInputPanel.validate() : true;
        return childrenValid && registrationValid;
    }

    private boolean validateChildren() {
        boolean valid = true;

        Collection<Person> nonNullChildren = newArrayList(filter(person.getChildren(), new Predicate<Person>() {

            @Override
            public boolean apply(Person child) {
                return child != null && child.getId() != null && child.getId() > 0;
            }
        }));
        if (nonNullChildren.size() != person.getChildren().size()) {
            error(getString("children_error"));
            valid = false;
        }

        Set<Long> childrenIds = newHashSet(transform(nonNullChildren, new Function<Person, Long>() {

            @Override
            public Long apply(Person child) {
                return child.getId();
            }
        }));
        if (childrenIds.size() != nonNullChildren.size()) {
            error(getString("children_dublicate"));
            valid = false;
        }
        return valid;
    }
}
