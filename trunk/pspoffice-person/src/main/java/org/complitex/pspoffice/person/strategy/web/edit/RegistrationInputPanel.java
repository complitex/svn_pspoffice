/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.edit;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Lists.*;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
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
import static org.complitex.pspoffice.person.strategy.RegistrationStrategy.*;
import org.complitex.pspoffice.person.strategy.RegistrationStrategy;

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
    private StrategyFactory strategyFactory;
    private DomainObject registration;
    private Attribute addressAttribute;
    private SearchComponentState addressSearchComponentState;
    private Date date;

    public RegistrationInputPanel(String id, DomainObject registration, Date date) {
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
                ImmutableList.of("city", "street", "building", "apartment", "room"), null, ShowMode.ACTIVE,
                !isHistory() && canEdit(null, registrationStrategy.getEntityTable(), registration));
        currentAddressContainer.add(addressSearchPanel);

        //system attributes:
        initSystemAttributeInput(this, "arrivalRegion", ARRIVAL_REGION, true);
        initSystemAttributeInput(this, "arrivalStreet", ARRIVAL_STREET, true);
        initSystemAttributeInput(this, "arrivalDistrict", ARRIVAL_DISTRICT, true);
        initSystemAttributeInput(this, "arrivalBuilding", ARRIVAL_BUILDING, true);
        initSystemAttributeInput(this, "arrivalCity", ARRIVAL_CITY, true);
        initSystemAttributeInput(this, "arrivalCorp", ARRIVAL_CORP, true);
        initSystemAttributeInput(this, "arrivalVillage", ARRIVAL_VILLAGE, true);
        initSystemAttributeInput(this, "arrivalApartment", ARRIVAL_APARTMENT, true);
        initSystemAttributeInput(this, "arrivalDate", ARRIVAL_DATE, false);
        initSystemAttributeInput(this, "departureRegion", DEPARTURE_REGION, true);
        initSystemAttributeInput(this, "departureDistrict", DEPARTURE_DISTRICT, false);
        initSystemAttributeInput(this, "departureCity", DEPARTURE_CITY, true);
        initSystemAttributeInput(this, "departureVillage", DEPARTUREL_VILLAGE, false);
        initSystemAttributeInput(this, "departureDate", DEPARTURE_DATE, false);
        initSystemAttributeInput(this, "departureReason", DEPARTURE_REASON, false);
        initSystemAttributeInput(this, "ownerRelationship", OWNER_RELATIONSHIP, false);
        initSystemAttributeInput(this, "otherRelationship", OTHERS_RELATIONSHIP, false);
        initSystemAttributeInput(this, "housingRights", HOUSING_RIGHTS, false);
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
                initAttributeInput(item, userAttributeTypeId, false);
            }
        };
        add(userAttributesView);
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
        final EntityAttributeType attributeType = registrationStrategy.getEntity().getAttributeType(attributeTypeId);

        //label
        IModel<String> labelModel = newLabelModel(attributeType.getAttributeNames());
        parent.add(new Label("label", labelModel));

        //required container
        WebMarkupContainer requiredContainer = new WebMarkupContainer("required");
        requiredContainer.setVisible(attributeType.isMandatory());
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
                input = new StringPanel("input", model, attributeType.isMandatory(), labelModel,
                        !isHistory() && canEdit(null, registrationStrategy.getEntityTable(), registration));
            }
            break;
            case BIG_STRING: {
                IModel<String> model = new SimpleTypeModel<String>(systemLocaleStringCulture, new StringConverter());
                input = new BigStringPanel("input", model, attributeType.isMandatory(), labelModel,
                        !isHistory() && canEdit(null, registrationStrategy.getEntityTable(), registration));
            }
            break;
            case STRING_CULTURE: {
                IModel<List<StringCulture>> model = new PropertyModel<List<StringCulture>>(attribute, "localizedValues");
                input = new StringCulturePanel("input", model, attributeType.isMandatory(), labelModel,
                        !isHistory() && canEdit(null, registrationStrategy.getEntityTable(), registration));
            }
            break;
            case INTEGER: {
                IModel<Integer> model = new SimpleTypeModel<Integer>(systemLocaleStringCulture, new IntegerConverter());
                input = new IntegerPanel("input", model, attributeType.isMandatory(), labelModel,
                        !isHistory() && canEdit(null, registrationStrategy.getEntityTable(), registration));
            }
            break;
            case DATE: {
                IModel<Date> model = new SimpleTypeModel<Date>(systemLocaleStringCulture, new DateConverter());
                input = new DatePanel("input", model, attributeType.isMandatory(), labelModel,
                        !isHistory() && canEdit(null, registrationStrategy.getEntityTable(), registration));
            }
            break;
            case DATE2: {
                IModel<Date> model = new SimpleTypeModel<Date>(systemLocaleStringCulture, new DateConverter());
                input = new Date2Panel("input", model, attributeType.isMandatory(), labelModel,
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
                input = new DoublePanel("input", model, attributeType.isMandatory(), labelModel,
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
    }

    public boolean validate() {
        DomainObject building = addressSearchComponentState.get("building");
        if (building == null || building.getId().equals(SearchComponent.NOT_SPECIFIED_ID)) {
            error(getString("address_failing"));
            return false;
        } else {
            DomainObject apartment = addressSearchComponentState.get("apartment");
            if (apartment == null || apartment.getId().equals(SearchComponent.NOT_SPECIFIED_ID)) {
                if (!validateOrphanBuilding(building.getId())) {
                    return false;
                }
            } else {
                DomainObject room = addressSearchComponentState.get("room");
                if (room == null || room.getId().equals(SearchComponent.NOT_SPECIFIED_ID)) {
                    if (!validateOrphanApartment(apartment.getId())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean validateOrphanBuilding(long buildingId) {
        if (!registrationStrategy.validateOrphans(buildingId, "building")) {
            error(getString("address_failing"));
            return false;
        }
        return true;
    }

    private boolean validateOrphanApartment(long apartmentId) {
        if (!registrationStrategy.validateOrphans(apartmentId, "apartment")) {
            error(getString("address_failing"));
            return false;
        }
        return true;
    }
}
