/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.edit.person;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
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
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.description.Entity;
import org.complitex.dictionary.entity.description.EntityAttributeType;
import org.complitex.dictionary.service.StringCultureBean;
import org.complitex.dictionary.web.component.list.AjaxRemovableListView;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;

import javax.ejb.EJB;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.complitex.pspoffice.person.strategy.web.component.PersonPicker;
import org.complitex.resources.WebCommonResourceInitializer;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.complitex.dictionary.strategy.web.DomainObjectAccessUtil.canEdit;
import static org.complitex.dictionary.web.component.DomainObjectInputPanel.labelModel;
import static org.complitex.dictionary.web.component.DomainObjectInputPanel.newInputComponent;
import static org.complitex.pspoffice.person.strategy.PersonStrategy.*;

/**
 *
 * @author Artem
 */
public final class PersonInputPanel extends Panel {

    @EJB
    private StringCultureBean stringBean;
    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private Person person;
    private Date date;

    public PersonInputPanel(String id, Person person, Date date) {
        super(id);
        this.person = person;
        this.date = date;
        init();
    }

    public PersonInputPanel(String id, Person person) {
        super(id);
        this.person = person;
        init();
    }

    private boolean isNew() {
        return person.getId() == null;
    }

    private boolean isHistory() {
        return date != null;
    }

    private void init() {
        add(JavascriptPackageResource.getHeaderContribution(WebCommonResourceInitializer.COLLAPSIBLE_FS_JS));

        //full name:
        PersonFullNamePanel personFullNamePanel = new PersonFullNamePanel("personFullNamePanel",
                newNameModel(FIRST_NAME), newNameModel(MIDDLE_NAME), newNameModel(LAST_NAME));
        personFullNamePanel.setEnabled(!isHistory() && canEdit(null, personStrategy.getEntityTable(), person));
        add(personFullNamePanel);

        Entity entity = personStrategy.getEntity();

        //system attributes:
        initSystemAttributeInput(this, "identityCode", IDENTITY_CODE, true);
        initSystemAttributeInput(this, "birthDate", BIRTH_DATE, true);
        initSystemAttributeInput(this, "gender", GENDER, false);

        WebMarkupContainer birthPlaceContainer = new WebMarkupContainer("birthPlaceContainer");
        birthPlaceContainer.setVisible(isBirthPlaceContainerVisible());
        add(birthPlaceContainer);
        initSystemAttributeInput(birthPlaceContainer, "birthCountry", BIRTH_COUNTRY, false);
        initSystemAttributeInput(birthPlaceContainer, "birthRegion", BIRTH_REGION, false);
        initSystemAttributeInput(birthPlaceContainer, "birthDistrict", BIRTH_DISTRICT, false);
        initSystemAttributeInput(birthPlaceContainer, "birthCity", BIRTH_CITY, false);

        //passport info
        WebMarkupContainer passportContainer = new WebMarkupContainer("passportContainer");
        passportContainer.setVisible(isPassportContainerVisible());
        add(passportContainer);
        initSystemAttributeInput(passportContainer, "passportSerialNumber", PASSPORT_SERIAL_NUMBER, false);
        initSystemAttributeInput(passportContainer, "passportNumber", PASSPORT_NUMBER, false);
        initSystemAttributeInput(passportContainer, "passportAcquisitionDate", PASSPORT_ACQUISITION_DATE, false);
        initSystemAttributeInput(passportContainer, "passportAcquisitionOrganization", PASSPORT_ACQUISITION_ORGANIZATION, false);

        // birth certificate info
        WebMarkupContainer birthCertificateContainer = new WebMarkupContainer("birthCertificateContainer");
        birthCertificateContainer.setVisible(isBirthCertificateContainerVisible());
        add(birthCertificateContainer);
        initSystemAttributeInput(birthCertificateContainer, "birthCertificateInfo", BIRTH_CERTIFICATE_INFO, false);
        initSystemAttributeInput(birthCertificateContainer, "birthCertificateAcquisitionDate", BIRTH_CERTIFICATE_ACQUISITION_DATE, false);
        initSystemAttributeInput(birthCertificateContainer, "birthCertificateAcquisitionOrganization", BIRTH_CERTIFICATE_ACQUISITION_ORGANIZATION, false);

        initSystemAttributeInput(this, "ukraineCitizenship", UKRAINE_CITIZENSHIP, false);
        initSystemAttributeInput(this, "deathDate", DEATH_DATE, false);
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
            if (userAttribute != null) {
                userAttributes.add(userAttribute);
            }
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
        childrenFieldsetContainer.add(new Label("childrenLabel",
                labelModel(entity.getAttributeType(CHILDREN).getAttributeNames(), getLocale())));
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

                IModel<Person> childModel = new Model<Person>() {

                    @Override
                    public Person getObject() {
                        int index = getCurrentIndex(fakeContainer);
                        return person.getChildren().get(index);
                    }

                    @Override
                    public void setObject(Person child) {
                        int index = getCurrentIndex(fakeContainer);
                        person.setChild(index, child);
                    }
                };
                childModel.setObject(item.getModelObject());

                PersonPicker personPicker = new PersonPicker("searchChildComponent", childModel, false, null,
                        !isHistory() && canEdit(null, personStrategy.getEntityTable(), person));
                item.add(personPicker);

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

    private void initSystemAttributeInput(MarkupContainer parent, String id, long attributeTypeId, boolean showIfMissing) {
        WebMarkupContainer container = new WebMarkupContainer(id + "Container");
        parent.add(container);
        initAttributeInput(container, attributeTypeId, showIfMissing);
    }

    private boolean isPassportContainerVisible() {
        return !(isHistory() && (person.getAttribute(PASSPORT_SERIAL_NUMBER) == null) && (person.getAttribute(PASSPORT_NUMBER) == null)
                && (person.getAttribute(PASSPORT_ACQUISITION_ORGANIZATION) == null)
                && (person.getAttribute(PASSPORT_ACQUISITION_DATE) == null));
    }

    private boolean isBirthCertificateContainerVisible() {
        return !(isHistory() && (person.getAttribute(BIRTH_CERTIFICATE_INFO) == null)
                && (person.getAttribute(BIRTH_CERTIFICATE_ACQUISITION_DATE) == null)
                && (person.getAttribute(BIRTH_CERTIFICATE_ACQUISITION_ORGANIZATION) == null));
    }

    private boolean isBirthPlaceContainerVisible() {
        return !(isHistory() && (person.getAttribute(BIRTH_COUNTRY) == null) && (person.getAttribute(BIRTH_DISTRICT) == null)
                && (person.getAttribute(BIRTH_REGION) == null)
                && (person.getAttribute(BIRTH_CITY) == null));
    }

    private void initAttributeInput(MarkupContainer parent, long attributeTypeId, boolean showIfMissing) {
        final EntityAttributeType attributeType = personStrategy.getEntity().getAttributeType(attributeTypeId);

        //label
        parent.add(new Label("label", labelModel(attributeType.getAttributeNames(), getLocale())));

        //required container
        WebMarkupContainer requiredContainer = new WebMarkupContainer("required");
        requiredContainer.setVisible(attributeType.isMandatory());
        parent.add(requiredContainer);

        //input component
        Attribute attribute = person.getAttribute(attributeTypeId);
        if (attribute == null) {
            attribute = new Attribute();
            attribute.setLocalizedValues(stringBean.newStringCultures());
            attribute.setAttributeTypeId(attributeTypeId);
            parent.setVisible(showIfMissing);
        }
        parent.add(newInputComponent(personStrategy.getEntityTable(), null, person, attribute, getLocale(), isHistory()));
    }

    public void beforePersist() {
        updateChildrenAttributes();
    }

    private void updateChildrenAttributes() {
        person.getAttributes().removeAll(Collections2.filter(person.getAttributes(), new Predicate<Attribute>() {

            @Override
            public boolean apply(Attribute attr) {
                return attr.getAttributeTypeId().equals(CHILDREN);
            }
        }));
        long attributeId = 1;
        for (Person child : person.getChildren()) {
            Attribute childrenAttribute = new Attribute();
            childrenAttribute.setAttributeId(attributeId++);
            childrenAttribute.setAttributeTypeId(CHILDREN);
            childrenAttribute.setValueTypeId(CHILDREN);
            childrenAttribute.setValueId(child.getId());
            person.addAttribute(childrenAttribute);
        }
    }

    public boolean validate() {
        return validateChildren();
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

        if (!isNew()) {
            if (childrenIds.contains(person.getId())) {
                error(getString("references_themselves"));
                valid = false;
            }
        }

        if (childrenIds.size() != nonNullChildren.size()) {
            error(getString("children_duplicate"));
            valid = false;
        }
        return valid;
    }
}
