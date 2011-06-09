/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.edit;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import static com.google.common.collect.ImmutableList.*;
import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Lists.*;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioChoice;
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
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.SimpleTypes;
import org.complitex.dictionary.entity.StringCulture;
import org.complitex.dictionary.entity.description.Entity;
import org.complitex.dictionary.entity.description.EntityAttributeType;
import org.complitex.dictionary.service.StringCultureBean;
import org.complitex.dictionary.strategy.IStrategy;
import org.complitex.dictionary.strategy.StrategyFactory;
import org.complitex.dictionary.util.ResourceUtil;
import org.complitex.dictionary.web.component.DisableAwareDropDownChoice;
import org.complitex.dictionary.web.component.DomainObjectDisableAwareRenderer;
import static org.complitex.dictionary.strategy.web.DomainObjectAccessUtil.*;
import org.complitex.dictionary.web.component.DomainObjectInputPanel.SimpleTypeModel;
import org.complitex.dictionary.web.component.ShowMode;
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
import org.complitex.pspoffice.ownerrelationship.strategy.OwnerRelationshipStrategy;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import static org.complitex.pspoffice.person.strategy.RegistrationStrategy.*;
import org.complitex.pspoffice.person.strategy.RegistrationStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.entity.Registration;

/**
 *
 * @author Artem
 */
public final class RegistrationInputPanel extends Panel {

    @EJB
    private StringCultureBean stringBean;
    @EJB
    private RegistrationStrategy registrationStrategy;
    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private StrategyFactory strategyFactory;
    @EJB(name = "Owner_relationshipStrategy")
    private OwnerRelationshipStrategy ownerRelationshipStrategy;
    private Registration registration;
    private Attribute addressAttribute;
    private SearchComponentState addressSearchComponentState;
    private Date date;
    private final IModel<ResidentStatus> residentStatusModel = new Model<ResidentStatus>();

    public RegistrationInputPanel(String id, Registration registration, Date date) {
        super(id);
        this.registration = registration;
        this.date = date;
        init();
    }

    private boolean isHistory() {
        return date != null;
    }

    private void init() {
        final Entity entity = registrationStrategy.getEntity();

        //current address
        WebMarkupContainer currentAddressContainer = new WebMarkupContainer("currentAddressContainer");
        add(currentAddressContainer);
        currentAddressContainer.add(new Label("label", newLabelModel(entity.getAttributeType(ADDRESS).getAttributeNames())));
        WebMarkupContainer requiredContainer = new WebMarkupContainer("required");
        requiredContainer.setVisible(entity.getAttributeType(ADDRESS).isMandatory());
        currentAddressContainer.add(requiredContainer);
        addressAttribute = registration.getAttribute(ADDRESS);
        addressSearchComponentState = initAddressSearchComponentState();
        SearchComponent addressSearchPanel = new SearchComponent("input", addressSearchComponentState,
                of("city", "street", "building", "apartment", "room"), null, ShowMode.ACTIVE,
                !isHistory() && canEdit(null, registrationStrategy.getEntityTable(), registration));
        currentAddressContainer.add(addressSearchPanel);

        //system attributes:
        initSystemAttributeInput(this, "arrivalCountry", ARRIVAL_COUNTRY, true);
        initSystemAttributeInput(this, "arrivalRegion", ARRIVAL_REGION, true);
        initSystemAttributeInput(this, "arrivalStreet", ARRIVAL_STREET, true);
        initSystemAttributeInput(this, "arrivalDistrict", ARRIVAL_DISTRICT, true);
        initSystemAttributeInput(this, "arrivalBuildingNumber", ARRIVAL_BUILDING_NUMBER, true);
        initSystemAttributeInput(this, "arrivalCity", ARRIVAL_CITY, true);
        initSystemAttributeInput(this, "arrivalBuildingCorp", ARRIVAL_BUILDING_CORP, true);
        initSystemAttributeInput(this, "arrivalApartment", ARRIVAL_APARTMENT, true);
        initSystemAttributeInput(this, "arrivalDate", ARRIVAL_DATE, false);
        initSystemAttributeInput(this, "departureCountry", DEPARTURE_COUNTRY, true);
        initSystemAttributeInput(this, "departureRegion", DEPARTURE_REGION, true);
        initSystemAttributeInput(this, "departureDistrict", DEPARTURE_DISTRICT, true);
        initSystemAttributeInput(this, "departureCity", DEPARTURE_CITY, true);
        initSystemAttributeInput(this, "departureStreet", DEPARTURE_STREET, true);
        initSystemAttributeInput(this, "departureBuildingNumber", DEPARTURE_BUILDING_NUMBER, true);
        initSystemAttributeInput(this, "departureBuildingCorp", DEPARTURE_BUILDING_CORP, true);
        initSystemAttributeInput(this, "departureApartment", DEPARTURE_APARTMENT, true);
        initSystemAttributeInput(this, "departureDate", DEPARTURE_DATE, false);
        initSystemAttributeInput(this, "departureReason", DEPARTURE_REASON, false);

        //owner attributes:
        initOwnerAttributes();

        WebMarkupContainer registrationNoteContainer = new WebMarkupContainer("registrationNoteContainer");
        add(registrationNoteContainer);
        initSystemAttributeInput(registrationNoteContainer, "registrationDate", REGISTRATION_DATE, false);
        initSystemAttributeInput(registrationNoteContainer, "registrationType", REGISTRATION_TYPE, false);
        if (isHistory() && (registration.getAttribute(REGISTRATION_DATE) == null) && (registration.getAttribute(REGISTRATION_TYPE) == null)) {
            registrationNoteContainer.setVisible(false);
        }

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
            Attribute userAttribute = registration.getAttribute(attributeTypeId);
            if (userAttribute != null) {
                userAttributes.add(userAttribute);
            }
        }

        ListView<Attribute> userAttributesView = new ListView<Attribute>("userAttributesView", userAttributes) {

            @Override
            protected void populateItem(ListItem<Attribute> item) {
                long userAttributeTypeId = item.getModelObject().getAttributeTypeId();
                initAttributeInput(item, userAttributeTypeId, false, null);
            }
        };
        add(userAttributesView);
    }

    enum ResidentStatus {

        OWNER, RESPONSIBLE, OTHER;
    }

    private Component initOwnerRelationship() {
        final EntityAttributeType attributeType = registrationStrategy.getEntity().getAttributeType(OWNER_RELATIONSHIP);

        WebMarkupContainer ownerRelationshipContainer = new WebMarkupContainer("ownerRelationshipContainer");
        ownerRelationshipContainer.setOutputMarkupPlaceholderTag(true);
        this.add(ownerRelationshipContainer);

        //label
        IModel<String> labelModel = newLabelModel(attributeType.getAttributeNames());
        ownerRelationshipContainer.add(new Label("label", labelModel));

        //select
        final List<DomainObject> allOwnerRelationships = ownerRelationshipStrategy.getAllWithoutOwnerAndResponsible();
        final Attribute ownerRelationshipAttribute = registration.getAttribute(OWNER_RELATIONSHIP);
        IModel<DomainObject> ownerRelationshipModel = new Model<DomainObject>() {

            @Override
            public void setObject(DomainObject object) {
                ownerRelationshipAttribute.setValueId(object != null ? object.getId() : null);
            }

            @Override
            public DomainObject getObject() {
                final Long ownerRelationshipId = ownerRelationshipAttribute.getValueId();
                for (DomainObject ownerRelationship : allOwnerRelationships) {
                    if (ownerRelationship.getId().equals(ownerRelationshipId)) {
                        return ownerRelationship;
                    }
                }
                return null;
            }
        };

        DisableAwareDropDownChoice<DomainObject> ownerRelationship = new DisableAwareDropDownChoice<DomainObject>("ownerRelationship",
                ownerRelationshipModel, allOwnerRelationships, new DomainObjectDisableAwareRenderer() {

            @Override
            public Object getDisplayValue(DomainObject object) {
                return ownerRelationshipStrategy.displayDomainObject(object, getLocale());
            }
        });
        ownerRelationship.setRequired(true);
        ownerRelationship.setLabel(labelModel);
        ownerRelationship.setEnabled(!isHistory() && canEdit(null, registrationStrategy.getEntityTable(), registration));
        ownerRelationshipContainer.add(ownerRelationship);
        return ownerRelationshipContainer;
    }

    private void initOwnerAttributes() {
        final Component ownerRelationshipContainer = initOwnerRelationship();
        initSystemAttributeInput(this, "ownerName", OWNER_NAME, false, true);
        initSystemAttributeInput(this, "personalAccount", PERSONAL_ACCOUNT, false, true);
        initSystemAttributeInput(this, "formOfOwnership", FORM_OF_OWNERSHIP, false, true);
        //TODO: make housing rights mandatory afterwards
        initSystemAttributeInput(this, "housingRights", HOUSING_RIGHTS, false, false);

        final Component ownerNameContainer = get("ownerNameContainer");
        ownerNameContainer.setOutputMarkupPlaceholderTag(true);
        final Component personalAccountContainer = get("personalAccountContainer");
        personalAccountContainer.setOutputMarkupPlaceholderTag(true);
        final Component formOfOwnershipContainer = get("formOfOwnershipContainer");
        formOfOwnershipContainer.setOutputMarkupPlaceholderTag(true);
        final Component housingRightsContainer = get("housingRightsContainer");
        housingRightsContainer.setOutputMarkupPlaceholderTag(true);

        if (registration.isOwner()) {
            residentStatusModel.setObject(ResidentStatus.OWNER);
            ownerRelationshipContainer.setVisible(false);
            ownerNameContainer.setVisible(false);
        } else if (registration.isResponsible()) {
            residentStatusModel.setObject(ResidentStatus.RESPONSIBLE);
            ownerRelationshipContainer.setVisible(false);
        } else {
            ownerNameContainer.setVisible(false);
            personalAccountContainer.setVisible(false);
            housingRightsContainer.setVisible(false);
            formOfOwnershipContainer.setVisible(false);
            residentStatusModel.setObject(ResidentStatus.OTHER);
        }

        RadioChoice<ResidentStatus> ownerResponsibleChoice = new RadioChoice<ResidentStatus>("ownerResponsibleChoice",
                residentStatusModel, of(ResidentStatus.values()), new EnumChoiceRenderer<ResidentStatus>(this));
        ownerResponsibleChoice.setSuffix("");
        ownerResponsibleChoice.setRequired(true);
        ownerResponsibleChoice.setEnabled(!isHistory() && canEdit(null, registrationStrategy.getEntityTable(), registration));
        ownerResponsibleChoice.add(new AjaxFormChoiceComponentUpdatingBehavior() {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                switch (residentStatusModel.getObject()) {
                    case OWNER:
                        ownerNameContainer.setVisible(false);
                        ownerRelationshipContainer.setVisible(false);
                        personalAccountContainer.setVisible(true);
                        formOfOwnershipContainer.setVisible(true);
                        housingRightsContainer.setVisible(true);
                        break;
                    case RESPONSIBLE:
                        ownerNameContainer.setVisible(true);
                        ownerRelationshipContainer.setVisible(false);
                        personalAccountContainer.setVisible(true);
                        formOfOwnershipContainer.setVisible(true);
                        housingRightsContainer.setVisible(true);
                        break;
                    case OTHER:
                        ownerNameContainer.setVisible(false);
                        ownerRelationshipContainer.setVisible(true);
                        personalAccountContainer.setVisible(false);
                        formOfOwnershipContainer.setVisible(false);
                        housingRightsContainer.setVisible(false);
                        break;
                }
                target.addComponent(ownerNameContainer);
                target.addComponent(ownerRelationshipContainer);
                target.addComponent(personalAccountContainer);
                target.addComponent(formOfOwnershipContainer);
                target.addComponent(housingRightsContainer);
            }
        });
        add(ownerResponsibleChoice);
    }

    private void initSystemAttributeInput(MarkupContainer parent, String id, long attributeTypeId, boolean showIfMissing) {
        WebMarkupContainer container = new WebMarkupContainer(id + "Container");
        parent.add(container);
        initAttributeInput(container, attributeTypeId, showIfMissing, null);
    }

    private void initSystemAttributeInput(MarkupContainer parent, String id, long attributeTypeId, boolean showIfMissing,
            boolean mandatory) {
        WebMarkupContainer container = new WebMarkupContainer(id + "Container");
        parent.add(container);
        initAttributeInput(container, attributeTypeId, showIfMissing, mandatory);
    }

    private IModel<String> newLabelModel(final List<StringCulture> attributeTypeNames) {
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return stringBean.displayValue(attributeTypeNames, getLocale());
            }
        };
    }

    private void initAttributeInput(MarkupContainer parent, long attributeTypeId, boolean showIfMissing, Boolean mandatory) {
        final EntityAttributeType attributeType = registrationStrategy.getEntity().getAttributeType(attributeTypeId);

        //label
        IModel<String> labelModel = newLabelModel(attributeType.getAttributeNames());
        parent.add(new Label("label", labelModel));

        //required container
        boolean isRequired = mandatory == null ? attributeType.isMandatory() : mandatory;
        WebMarkupContainer requiredContainer = new WebMarkupContainer("required");
        requiredContainer.setVisible(isRequired);
        parent.add(requiredContainer);

        //input component
        Attribute attribute = registration.getAttribute(attributeTypeId);
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
                input = new StringPanel("input", model, isRequired, labelModel,
                        !isHistory() && canEdit(null, registrationStrategy.getEntityTable(), registration));
            }
            break;
            case BIG_STRING: {
                IModel<String> model = new SimpleTypeModel<String>(systemLocaleStringCulture, new StringConverter());
                input = new BigStringPanel("input", model, isRequired, labelModel,
                        !isHistory() && canEdit(null, registrationStrategy.getEntityTable(), registration));
            }
            break;
            case STRING_CULTURE: {
                IModel<List<StringCulture>> model = new PropertyModel<List<StringCulture>>(attribute, "localizedValues");
                input = new StringCulturePanel("input", model, isRequired, labelModel,
                        !isHistory() && canEdit(null, registrationStrategy.getEntityTable(), registration));
            }
            break;
            case INTEGER: {
                IModel<Integer> model = new SimpleTypeModel<Integer>(systemLocaleStringCulture, new IntegerConverter());
                input = new IntegerPanel("input", model, isRequired, labelModel,
                        !isHistory() && canEdit(null, registrationStrategy.getEntityTable(), registration));
            }
            break;
            case DATE: {
                IModel<Date> model = new SimpleTypeModel<Date>(systemLocaleStringCulture, new DateConverter());
                input = new DatePanel("input", model, isRequired, labelModel,
                        !isHistory() && canEdit(null, registrationStrategy.getEntityTable(), registration));
            }
            break;
            case DATE2: {
                IModel<Date> model = new SimpleTypeModel<Date>(systemLocaleStringCulture, new DateConverter());
                input = new Date2Panel("input", model, isRequired, labelModel,
                        !isHistory() && canEdit(null, registrationStrategy.getEntityTable(), registration));
            }
            break;
            case BOOLEAN: {
                IModel<Boolean> model = new SimpleTypeModel<Boolean>(systemLocaleStringCulture, new BooleanConverter());
                input = new BooleanPanel("input", model, labelModel,
                        !isHistory() && canEdit(null, registrationStrategy.getEntityTable(), registration));
            }
            break;
            case DOUBLE: {
                IModel<Double> model = new SimpleTypeModel<Double>(systemLocaleStringCulture, new DoubleConverter());
                input = new DoublePanel("input", model, isRequired, labelModel,
                        !isHistory() && canEdit(null, registrationStrategy.getEntityTable(), registration));
            }
            break;
        }
        parent.add(input);
    }

    private SearchComponentState initAddressSearchComponentState() {
        SearchComponentState searchComponentState = new SearchComponentState();
        Long addressObjectId = addressAttribute.getValueId();
        IStrategy addressStrategy = null;
        DomainObject addressObject = null;
        String addressEntity = null;

        if (addressAttribute.getValueTypeId().equals(ADDRESS_ROOM)) {
            addressEntity = "room";
        } else if (addressAttribute.getValueTypeId().equals(ADDRESS_APARTMENT)) {
            addressEntity = "apartment";
        } else if (addressAttribute.getValueTypeId().equals(ADDRESS_BUILDING)) {
            addressEntity = "building";
        } else {
            throw new IllegalStateException("Registration address attribute has unknown value type id: " + addressAttribute.getValueTypeId());
        }
        addressStrategy = strategyFactory.getStrategy(addressEntity);
        if (addressObjectId != null) {
            addressObject = addressStrategy.findById(addressObjectId, true);
            SimpleObjectInfo info = addressStrategy.findParentInSearchComponent(addressObjectId, null);
            if (info != null) {
                searchComponentState = addressStrategy.getSearchComponentStateForParent(info.getId(), info.getEntityTable(), null);
                searchComponentState.put(addressEntity, addressObject);
            }
            if (addressEntity.equals("apartment")) {
                DomainObject room = new DomainObject();
                room.setId(SearchComponent.NOT_SPECIFIED_ID);
                searchComponentState.put("room", room);
            } else if (addressEntity.equals("building")) {
                DomainObject room = new DomainObject();
                room.setId(SearchComponent.NOT_SPECIFIED_ID);
                searchComponentState.put("room", room);
                DomainObject apartment = new DomainObject();
                apartment.setId(SearchComponent.NOT_SPECIFIED_ID);
                searchComponentState.put("apartment", apartment);
            }
        }
        return searchComponentState;
    }

    public void beforePersist() {
        // current address attributes
        if (!addressSearchComponentState.get("room").getId().equals(SearchComponent.NOT_SPECIFIED_ID)) {
            addressAttribute.setValueTypeId(ADDRESS_ROOM);
            addressAttribute.setValueId(addressSearchComponentState.get("room").getId());
        } else if (!addressSearchComponentState.get("apartment").getId().equals(SearchComponent.NOT_SPECIFIED_ID)) {
            addressAttribute.setValueTypeId(ADDRESS_APARTMENT);
            addressAttribute.setValueId(addressSearchComponentState.get("apartment").getId());
        } else if (!addressSearchComponentState.get("building").getId().equals(SearchComponent.NOT_SPECIFIED_ID)) {
            addressAttribute.setValueTypeId(ADDRESS_BUILDING);
            addressAttribute.setValueId(addressSearchComponentState.get("building").getId());
        } else {
            throw new IllegalStateException("All building, apartment and room parts of current address have not been filled in.");
        }

        //owner
        switch (residentStatusModel.getObject()) {
            case OWNER:
                boolean wasOwner = new BooleanConverter().toObject(stringBean.getSystemStringCulture(
                        registration.getAttribute(IS_OWNER).getLocalizedValues()).getValue());
                if (!wasOwner) {
                    stringBean.getSystemStringCulture(registration.getAttribute(IS_OWNER).getLocalizedValues()).
                            setValue(new BooleanConverter().toString(Boolean.TRUE));
                    registration.removeAttribute(IS_RESPONSIBLE);
                    registration.removeAttribute(OWNER_NAME);
                    registration.getAttribute(OWNER_RELATIONSHIP).setValueId(OwnerRelationshipStrategy.OWNER);
                }
                break;
            case RESPONSIBLE:
                boolean wasResponsible = new BooleanConverter().toObject(stringBean.getSystemStringCulture(
                        registration.getAttribute(IS_RESPONSIBLE).getLocalizedValues()).getValue());
                if (!wasResponsible) {
                    stringBean.getSystemStringCulture(registration.getAttribute(IS_RESPONSIBLE).getLocalizedValues()).
                            setValue(new BooleanConverter().toString(Boolean.TRUE));
                    registration.removeAttribute(IS_OWNER);
                    registration.getAttribute(OWNER_RELATIONSHIP).setValueId(OwnerRelationshipStrategy.RESPONSIBLE);
                }
                break;
            case OTHER:
                registration.removeAttribute(IS_OWNER);
                registration.removeAttribute(IS_RESPONSIBLE);
                registration.removeAttribute(OWNER_NAME);
                registration.removeAttribute(PERSONAL_ACCOUNT);
                registration.removeAttribute(HOUSING_RIGHTS);
                break;
        }
    }

    public boolean validate() {
        //address validity
        String addressEntity = null;
        Long addressId = null;
        DomainObject building = addressSearchComponentState.get("building");
        if (building == null || building.getId().equals(SearchComponent.NOT_SPECIFIED_ID)) {
            error(getString("address_failing"));
        } else {
            DomainObject apartment = addressSearchComponentState.get("apartment");
            if (apartment == null || apartment.getId().equals(SearchComponent.NOT_SPECIFIED_ID)) {
                if (isLeafBuilding(building.getId())) {
                    addressEntity = "building";
                    addressId = building.getId();
                }
            } else {
                DomainObject room = addressSearchComponentState.get("room");
                if (room == null || room.getId().equals(SearchComponent.NOT_SPECIFIED_ID)) {
                    if (isLeafApartment(apartment.getId())) {
                        addressEntity = "apartment";
                        addressId = apartment.getId();
                    }
                } else {
                    addressEntity = "room";
                    addressId = room.getId();
                }
            }
        }
        if (addressEntity == null || addressId == null) {
            return false;
        }

        //owner validation
        switch (residentStatusModel.getObject()) {
            case OWNER: {
                Person responsible = personStrategy.findResponsibleByAddress(addressEntity, addressId);
                if (responsible != null) {
                    error(ResourceUtil.getFormatString(this, "responsible_exists",
                            personStrategy.displayDomainObject(responsible, getLocale())));
                }
            }
            break;
            case RESPONSIBLE: {
                List<Person> owners = personStrategy.findOwnersByAddress(addressEntity, addressId);
                if (owners != null && !owners.isEmpty()) {
                    if (owners.size() == 1) {
                        error(ResourceUtil.getFormatString(this, "owner_exists",
                                personStrategy.displayDomainObject(owners.get(0), getLocale())));
                    } else {
                        StringBuilder ownersParam = new StringBuilder();
                        for (int i = 0; i < owners.size(); i++) {
                            ownersParam.append(personStrategy.displayDomainObject(owners.get(i), getLocale()));
                            if (i < owners.size() - 1) {
                                ownersParam.append(", ");
                            }
                        }
                        error(ResourceUtil.getFormatString(this, "owners_exists", ownersParam));
                    }
                }
                Person responsible = personStrategy.findResponsibleByAddress(addressEntity, addressId);
                if (responsible != null) {
                    error(ResourceUtil.getFormatString(this, "responsible_exists",
                            personStrategy.displayDomainObject(responsible, getLocale())));
                }
            }
            break;

        }
        return getSession().getFeedbackMessages().isEmpty();
    }

    private boolean isLeafBuilding(long buildingId) {
        if (!registrationStrategy.isLeafAddress(buildingId, "building")) {
            error(getString("address_failing"));
            return false;
        }
        return true;
    }

    private boolean isLeafApartment(long apartmentId) {
        if (!registrationStrategy.isLeafAddress(apartmentId, "apartment")) {
            error(getString("address_failing"));
            return false;
        }
        return true;
    }
}
