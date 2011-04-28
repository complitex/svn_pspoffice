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
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.complitex.dictionary.converter.BooleanConverter;
import org.complitex.dictionary.converter.DateConverter;
import org.complitex.dictionary.converter.DoubleConverter;
import org.complitex.dictionary.converter.IntegerConverter;
import org.complitex.dictionary.converter.StringConverter;
import org.complitex.dictionary.entity.Attribute;
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
import org.complitex.dictionary.web.component.search.SearchComponent;
import org.complitex.dictionary.web.component.search.SearchComponentState;
import org.complitex.dictionary.web.component.type.BigStringPanel;
import org.complitex.dictionary.web.component.type.BooleanPanel;
import org.complitex.dictionary.web.component.type.Date2Panel;
import org.complitex.dictionary.web.component.type.DatePanel;
import org.complitex.dictionary.web.component.type.DoublePanel;
import org.complitex.dictionary.web.component.type.IntegerPanel;
import org.complitex.dictionary.web.component.type.StringCulturePanel;
import org.complitex.dictionary.web.component.type.StringPanel;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import static org.complitex.pspoffice.person.strategy.PersonStrategy.*;
import org.complitex.pspoffice.person.strategy.entity.Person;

/**
 *
 * @author Artem
 */
public final class PersonInputPanel extends Panel {

    @EJB
    private StringCultureBean stringBean;
    @EJB
    private PersonStrategy personStrategy;
    private Person person;
    private RegistrationInputPanel registrationInputPanel;

    public PersonInputPanel(String id, Person person) {
        super(id);
        this.person = person;
        init();
    }

    private void init() {
        //full name:
        FullNamePanel fullNamePanel = new FullNamePanel("fullNamePanel", newNameModel(FIRST_NAME), newNameModel(MIDDLE_NAME),
                newNameModel(LAST_NAME));
        fullNamePanel.setEnabled(canEdit(null, personStrategy.getEntityTable(), person));
        add(fullNamePanel);

        Entity entity = personStrategy.getEntity();

        //registration panel:
        add(new Label("registrationLabel", newLabelModel(entity.getAttributeType(REGISTRATION).getAttributeNames())));
        registrationInputPanel = new RegistrationInputPanel("registrationPanel", person.getRegistration());
        add(registrationInputPanel);

        //system attributes:
        initSystemAttributeInput(this, "birthRegion", BIRTH_REGION);
        initSystemAttributeInput(this, "birthDistrict", BIRTH_DISTRICT);
        initSystemAttributeInput(this, "birthCity", BIRTH_CITY);
        initSystemAttributeInput(this, "birthVillage", BIRTH_VILLAGE);
        initSystemAttributeInput(this, "birthDate", BIRTH_DATE);
        initSystemAttributeInput(this, "passportSerialNumber", PASSPORT_SERIAL_NUMBER);
        initSystemAttributeInput(this, "passportNumber", PASSPORT_NUMBER);
        initSystemAttributeInput(this, "passportAcquisitionInfo", PASSPORT_ACQUISITION_INFO);
        initSystemAttributeInput(this, "nationality", NATIONALITY);
        initSystemAttributeInput(this, "jobInfo", JOB_INFO);
        initSystemAttributeInput(this, "militaryServiceRelation", MILITARY_SERVISE_RELATION);

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
                initAttributeInput(item, userAttributeTypeId);
            }
        };
        add(userAttributesView);

        //children
        add(new Label("childrenLabel", newLabelModel(entity.getAttributeType(CHILDREN).getAttributeNames())));
        final WebMarkupContainer childrenContainer = new WebMarkupContainer("childrenContainer");
        childrenContainer.setOutputMarkupId(true);
        add(childrenContainer);
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
                        canEdit(null, personStrategy.getEntityTable(), person));
                item.add(searchChildComponent);

                addRemoveLink("remove", item, null, childrenContainer).
                        setVisible(canEdit(null, personStrategy.getEntityTable(), person));
            }
        };
        AjaxLink<Void> add = new AjaxLink<Void>("add") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                Person newChild = null;
                person.addChild(newChild);
                target.addComponent(childrenContainer);
            }
        };
        add.setVisible(canEdit(null, personStrategy.getEntityTable(), person));
        add(add);
        childrenContainer.add(children);
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

    private void initSystemAttributeInput(MarkupContainer parent, String id, long attributeTypeId) {
        WebMarkupContainer container = new WebMarkupContainer(id + "Container");
        parent.add(container);
        initAttributeInput(container, attributeTypeId);
    }

    private IModel<String> newLabelModel(final List<StringCulture> attributeTypeNames) {
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return stringBean.displayValue(attributeTypeNames, getLocale());
            }
        };
    }

    private void initAttributeInput(MarkupContainer parent, long attributeTypeId) {
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

        String valueType = attributeType.getEntityAttributeValueTypes().get(0).getValueType();
        SimpleTypes type = SimpleTypes.valueOf(valueType.toUpperCase());

        Component input = null;
        final StringCulture systemLocaleStringCulture = stringBean.getSystemStringCulture(attribute.getLocalizedValues());
        switch (type) {
            case STRING: {
                IModel<String> model = new SimpleTypeModel<String>(systemLocaleStringCulture, new StringConverter());
                input = new StringPanel("input", model, attributeType.isMandatory(), labelModel,
                        canEdit(null, personStrategy.getEntityTable(), person));
            }
            break;
            case BIG_STRING: {
                IModel<String> model = new SimpleTypeModel<String>(systemLocaleStringCulture, new StringConverter());
                input = new BigStringPanel("input", model, attributeType.isMandatory(), labelModel,
                        canEdit(null, personStrategy.getEntityTable(), person));
            }
            break;
            case STRING_CULTURE: {
                IModel<List<StringCulture>> model = new PropertyModel<List<StringCulture>>(attribute, "localizedValues");
                input = new StringCulturePanel("input", model, attributeType.isMandatory(), labelModel,
                        canEdit(null, personStrategy.getEntityTable(), person));
            }
            break;
            case INTEGER: {
                IModel<Integer> model = new SimpleTypeModel<Integer>(systemLocaleStringCulture, new IntegerConverter());
                input = new IntegerPanel("input", model, attributeType.isMandatory(), labelModel,
                        canEdit(null, personStrategy.getEntityTable(), person));
            }
            break;
            case DATE: {
                IModel<Date> model = new SimpleTypeModel<Date>(systemLocaleStringCulture, new DateConverter());
                input = new DatePanel("input", model, attributeType.isMandatory(), labelModel,
                        canEdit(null, personStrategy.getEntityTable(), person));
            }
            break;
            case DATE2: {
                IModel<Date> model = new SimpleTypeModel<Date>(systemLocaleStringCulture, new DateConverter());
                input = new Date2Panel("input", model, attributeType.isMandatory(), labelModel,
                        canEdit(null, personStrategy.getEntityTable(), person));
            }
            break;
            case BOOLEAN: {
                IModel<Boolean> model = new SimpleTypeModel<Boolean>(systemLocaleStringCulture, new BooleanConverter());
                input = new BooleanPanel("input", model, labelModel,
                        canEdit(null, personStrategy.getEntityTable(), person));
            }
            break;
            case DOUBLE: {
                IModel<Double> model = new SimpleTypeModel<Double>(systemLocaleStringCulture, new DoubleConverter());
                input = new DoublePanel("input", model, attributeType.isMandatory(), labelModel,
                        canEdit(null, personStrategy.getEntityTable(), person));
            }
            break;
        }
        parent.add(input);
    }

    public void beforePersist() {
        registrationInputPanel.beforePersist();
    }

    public boolean validate() {
        boolean childrenValid = validateChildren();
        boolean registrationValid = registrationInputPanel.validate();
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
