/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.edit;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import static com.google.common.collect.ImmutableList.*;
import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Iterables.*;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.description.Entity;
import org.complitex.dictionary.entity.description.EntityAttributeType;
import org.complitex.dictionary.strategy.IStrategy;
import org.complitex.dictionary.strategy.StrategyFactory;
import static org.complitex.dictionary.strategy.web.DomainObjectAccessUtil.*;
import org.complitex.dictionary.util.StringUtil;
import org.complitex.dictionary.web.component.ShowMode;
import org.complitex.dictionary.web.component.search.SearchComponentState;
import org.complitex.dictionary.web.component.search.WiQuerySearchComponent;
import static org.complitex.dictionary.web.component.DomainObjectInputPanel.*;
import org.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.RegistrationStrategy;
import static org.complitex.pspoffice.person.strategy.ApartmentCardStrategy.*;
import org.complitex.pspoffice.person.strategy.entity.ApartmentCard;
import org.complitex.pspoffice.person.strategy.entity.Registration;

/**
 *
 * @author Artem
 */
public final class ApartmentCardInputPanel extends Panel {

    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("dd.MM.yyyy");
    @EJB
    private ApartmentCardStrategy apartmentCardStrategy;
    @EJB
    private StrategyFactory strategyFactory;
    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private RegistrationStrategy registrationStrategy;
    private ApartmentCard apartmentCard;
    private SearchComponentState addressSearchComponentState;
    private SearchComponentState ownerSearchComponentState;
    private String addressEntity;
    private long addressId;

    public ApartmentCardInputPanel(String id, ApartmentCard apartmentCard, String addressEntity, long addressId) {
        super(id);
        this.apartmentCard = apartmentCard;
        this.addressEntity = addressEntity;
        this.addressId = addressId;
        init();
    }

    private boolean isNew() {
        return apartmentCard.getId() == null;
    }

    private void init() {
        final Entity entity = apartmentCardStrategy.getEntity();

        //personal account
        initSystemAttributeInput(this, "personalAccount", PERSONAL_ACCOUNT);

        //address
        WebMarkupContainer addressContainer = new WebMarkupContainer("addressContainer");
        final EntityAttributeType addressAttributeType = entity.getAttributeType(ADDRESS);
        addressContainer.add(new Label("label", labelModel(addressAttributeType.getAttributeNames(), getLocale())));
        addressContainer.add(new WebMarkupContainer("required").setVisible(addressAttributeType.isMandatory()));

        addressSearchComponentState = initAddressSearchComponentState();
        WiQuerySearchComponent address = new WiQuerySearchComponent("address", addressSearchComponentState,
                of("city", "street", "building", "apartment", "room"), null, ShowMode.ACTIVE, isNew());
        addressContainer.add(address);
        add(addressContainer);

        //owner
        WebMarkupContainer ownerContainer = new WebMarkupContainer("ownerContainer");
        final EntityAttributeType ownerAttributeType = entity.getAttributeType(OWNER);
        ownerContainer.add(new Label("label", labelModel(ownerAttributeType.getAttributeNames(), getLocale())));
        ownerContainer.add(new WebMarkupContainer("required").setVisible(ownerAttributeType.isMandatory()));

        ownerSearchComponentState = initOwnerSearchComponentState();
        WiQuerySearchComponent owner = new WiQuerySearchComponent("owner", ownerSearchComponentState,
                of("person"), null, ShowMode.ACTIVE, isNew());
        ownerContainer.add(owner);
        add(ownerContainer);

        // form of ownership
        initSystemAttributeInput(this, "formOfOwnership", FORM_OF_OWNERSHIP);

        //registrations
        ListView<Registration> registrations = new ListView<Registration>("registrations", apartmentCard.getRegistrations()) {

            @Override
            protected void populateItem(ListItem<Registration> item) {
                final Registration registration = item.getModelObject();

                item.add(new Label("personName", personStrategy.displayDomainObject(registration.getPerson(), getLocale())));
                Date birthDate = registration.getPerson().getBirthDate();
                item.add(new Label("personBirthDate", birthDate != null ? DATE_FORMATTER.format(birthDate) : null));
                item.add(new Label("registrationType", StringUtil.valueOf(registration.getRegistrationType())));
                Date registrationStartDate = registration.getRegistrationDate();
                item.add(new Label("registrationStartDate", registrationStartDate != null ? DATE_FORMATTER.format(registrationStartDate) : null));
                Date registrationEndDate = registration.getDepartureDate();
                item.add(new Label("registrationEndDate", registrationEndDate != null ? DATE_FORMATTER.format(registrationEndDate) : null));

                Link<Void> registrationDetails = new Link<Void>("registrationDetails") {

                    @Override
                    public void onClick() {
                        setResponsePage(new RegistrationEdit(apartmentCard.getId(), apartmentCard.getAddressEntity(),
                                apartmentCard.getAddressId(), registration));
                    }
                };
                registrationDetails.add(new Label("editMessage", new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        return canEdit(null, "registration", registration) ? getString("edit") : getString("view");
                    }
                }));
                item.add(registrationDetails);
            }
        };
        add(registrations);
        Link<Void> addRegistration = new Link<Void>("addRegistration") {

            @Override
            public void onClick() {
                setResponsePage(new RegistrationEdit(apartmentCard.getId(), apartmentCard.getAddressEntity(),
                        apartmentCard.getAddressId(), registrationStrategy.newInstance()));
            }
        };
        add(addRegistration);

        //housing rights
        initSystemAttributeInput(this, "housingRights", HOUSING_RIGHTS);

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
            Attribute userAttribute = apartmentCard.getAttribute(attributeTypeId);
            if (userAttribute != null) {
                userAttributes.add(userAttribute);
            }
        }

        ListView<Attribute> userAttributesView = new ListView<Attribute>("userAttributesView", userAttributes) {

            @Override
            protected void populateItem(ListItem<Attribute> item) {
                long userAttributeTypeId = item.getModelObject().getAttributeTypeId();
                initAttributeInput(item, userAttributeTypeId);
            }
        };
        add(userAttributesView);
    }

    private void initSystemAttributeInput(MarkupContainer parent, String id, long attributeTypeId) {
        WebMarkupContainer container = new WebMarkupContainer(id + "Container");
        parent.add(container);
        initAttributeInput(container, attributeTypeId);
    }

    private void initAttributeInput(MarkupContainer parent, long attributeTypeId) {
        final EntityAttributeType attributeType = apartmentCardStrategy.getEntity().getAttributeType(attributeTypeId);

        //label
        parent.add(new Label("label", labelModel(attributeType.getAttributeNames(), getLocale())));

        //required container
        WebMarkupContainer requiredContainer = new WebMarkupContainer("required");
        requiredContainer.setVisible(attributeType.isMandatory());
        parent.add(requiredContainer);

        //input component
        Attribute attribute = apartmentCard.getAttribute(attributeTypeId);
        parent.add(newInputComponent(apartmentCardStrategy.getEntityTable(), null, apartmentCard, attribute, getLocale(), false));
    }

    public void beforePersist() {
        // address
        Attribute addressAttribute = apartmentCard.getAttribute(ADDRESS);
        if (addressSearchComponentState.get("room") != null
                && addressSearchComponentState.get("room").getId() > 0) {
            addressAttribute.setValueTypeId(ADDRESS_ROOM);
            addressAttribute.setValueId(addressSearchComponentState.get("room").getId());
        } else if (addressSearchComponentState.get("apartment") != null
                && addressSearchComponentState.get("apartment").getId() > 0) {
            addressAttribute.setValueTypeId(ADDRESS_APARTMENT);
            addressAttribute.setValueId(addressSearchComponentState.get("apartment").getId());
        } else if (addressSearchComponentState.get("building") != null
                && addressSearchComponentState.get("building").getId() > 0) {
            addressAttribute.setValueTypeId(ADDRESS_BUILDING);
            addressAttribute.setValueId(addressSearchComponentState.get("building").getId());
        } else {
            throw new IllegalStateException("All building, apartment and room parts of address have not been filled in.");
        }

        // owner
        Attribute ownerAttribute = apartmentCard.getAttribute(OWNER);
        DomainObject owner = ownerSearchComponentState.get("person");
        Long ownerId = owner != null ? owner.getId() : null;
        if (ownerId != null) {
            ownerAttribute.setValueId(ownerId);
            ownerAttribute.setValueTypeId(OWNER_TYPE);
        } else {
            throw new IllegalStateException("Owner has not been filled in.");
        }
    }

    public boolean validate() {
        //address validation
        DomainObject building = addressSearchComponentState.get("building");
        if (building == null || building.getId() <= 0) {
            error(getString("address_failing"));
        } else {
            DomainObject apartment = addressSearchComponentState.get("apartment");
            if (apartment == null || apartment.getId() <= 0) {
                if (!apartmentCardStrategy.isLeafAddress(building.getId(), "building")) {
                    error(getString("address_failing"));
                }
            } else {
                DomainObject room = addressSearchComponentState.get("room");
                if (room == null || room.getId() <= 0) {
                    if (!apartmentCardStrategy.isLeafAddress(apartment.getId(), "apartment")) {
                        error(getString("address_failing"));
                    }
                } else {
                    if (room.getId() == null || room.getId() <= 0) {
                        error(getString("address_failing"));
                    }
                }
            }
        }

        //owner validation
        if (ownerSearchComponentState.get("person") == null || ownerSearchComponentState.get("person").getId() == null
                || ownerSearchComponentState.get("person").getId() <= 0) {
            error(getString("owner_required"));
        }

        return getSession().getFeedbackMessages().isEmpty();
    }

    private SearchComponentState initAddressSearchComponentState() {
        SearchComponentState searchComponentState = new SearchComponentState();
        IStrategy addressStrategy = strategyFactory.getStrategy(addressEntity);
        DomainObject addressObject = addressStrategy.findById(addressId, true);
        SimpleObjectInfo info = addressStrategy.findParentInSearchComponent(addressId, null);
        if (info != null) {
            searchComponentState = addressStrategy.getSearchComponentStateForParent(info.getId(), info.getEntityTable(), null);
            searchComponentState.put(addressEntity, addressObject);
        }
        if (addressEntity.equals("apartment")) {
            DomainObject room = new DomainObject();
            room.setId(SearchComponentState.NOT_SPECIFIED_ID);
            searchComponentState.put("room", room);
        } else if (addressEntity.equals("building")) {
            DomainObject room = new DomainObject();
            room.setId(SearchComponentState.NOT_SPECIFIED_ID);
            searchComponentState.put("room", room);
            DomainObject apartment = new DomainObject();
            apartment.setId(SearchComponentState.NOT_SPECIFIED_ID);
            searchComponentState.put("apartment", apartment);
        }
        return searchComponentState;
    }

    private SearchComponentState initOwnerSearchComponentState() {
        if (isNew()) {
            return new SearchComponentState();
        }

        SearchComponentState searchComponentState = new SearchComponentState();
        searchComponentState.put("person", apartmentCard.getOwner());
        return searchComponentState;
    }
}
