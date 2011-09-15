/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.edit.apartment_card;

import org.complitex.pspoffice.person.strategy.web.edit.apartment_card.toolbar.AddRegistrationToolbarButton;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.complitex.pspoffice.person.strategy.web.edit.registration.RegistrationEdit;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import static com.google.common.collect.Maps.*;
import static com.google.common.collect.ImmutableList.*;
import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Lists.*;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.AjaxIndicatorAppender;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.Log;
import org.complitex.dictionary.entity.description.Entity;
import org.complitex.dictionary.entity.description.EntityAttributeType;
import org.complitex.dictionary.service.LogBean;
import org.complitex.dictionary.service.SessionBean;
import org.complitex.dictionary.service.StringCultureBean;
import org.complitex.dictionary.strategy.IStrategy;
import org.complitex.dictionary.strategy.StrategyFactory;
import org.complitex.dictionary.util.CloneUtil;
import org.complitex.dictionary.util.DateUtil;
import org.complitex.dictionary.util.StringUtil;
import org.complitex.dictionary.web.component.DisableAwareDropDownChoice;
import org.complitex.dictionary.web.component.DomainObjectDisableAwareRenderer;
import org.complitex.dictionary.web.component.ShowMode;
import org.complitex.dictionary.web.component.css.CssAttributeBehavior;
import org.complitex.dictionary.web.component.scroll.ScrollToElementUtil;
import org.complitex.dictionary.web.component.search.SearchComponentState;
import org.complitex.pspoffice.ownerrelationship.strategy.OwnerRelationshipStrategy;
import org.complitex.pspoffice.ownership.strategy.OwnershipFormStrategy;
import org.complitex.pspoffice.person.Module;
import org.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.RegistrationStrategy;
import org.complitex.pspoffice.person.strategy.entity.ApartmentCard;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.entity.PersonAgeType;
import org.complitex.pspoffice.person.strategy.entity.Registration;
import org.complitex.pspoffice.person.strategy.web.component.AddApartmentCardButton;
import org.complitex.pspoffice.person.strategy.web.component.AddressSearchPanel;
import org.complitex.pspoffice.person.strategy.web.component.PermissionPanel;
import org.complitex.pspoffice.person.strategy.web.component.PersonPicker;
import org.complitex.pspoffice.person.strategy.web.edit.apartment_card.toolbar.ArchiveApartmentCardButton;
import org.complitex.pspoffice.person.strategy.web.edit.person.PersonEdit;
import org.complitex.pspoffice.person.strategy.web.list.apartment_card.ApartmentCardList;
import org.complitex.pspoffice.person.util.PersonDateFormatter;
import org.complitex.pspoffice.registration_type.strategy.RegistrationTypeStrategy;
import org.complitex.resources.WebCommonResourceInitializer;
import org.complitex.template.web.component.toolbar.ToolbarButton;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.FormTemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.complitex.dictionary.strategy.web.DomainObjectAccessUtil.*;
import static org.complitex.dictionary.web.component.DomainObjectInputPanel.*;
import static org.complitex.pspoffice.person.strategy.ApartmentCardStrategy.*;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public final class ApartmentCardEdit extends FormTemplatePage {

    private static final Logger log = LoggerFactory.getLogger(ApartmentCardEdit.class);
    @EJB
    private ApartmentCardStrategy apartmentCardStrategy;
    @EJB
    private StringCultureBean stringBean;
    @EJB
    private LogBean logBean;
    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private RegistrationStrategy registrationStrategy;
    @EJB
    private StrategyFactory strategyFactory;
    @EJB
    private OwnerRelationshipStrategy ownerRelationshipStrategy;
    @EJB
    private OwnershipFormStrategy ownershipFormStrategy;
    @EJB
    private RegistrationTypeStrategy registrationTypeStrategy;
    @EJB
    private SessionBean sessionBean;
    private String addressEntity;
    private Long addressId;
    private ApartmentCard oldApartmentCard;
    private ApartmentCard newApartmentCard;
    private SearchComponentState addressSearchComponentState;
    private Form form;
    private FeedbackPanel messages;
    private Component scrollToComponent;
    private ArchiveApartmentCardDialog archiveApartmentCardDialog;

    private class ApartmentCardSubmitLink extends AjaxSubmitLink {

        ApartmentCardSubmitLink(String id) {
            super(id, form);
        }

        @Override
        protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
            try {
                if (validate()) {
                    save();
                    afterSave();
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
            super.onError(target, form);
            target.addComponent(messages);
            scrollToMessages(target);
        }

        protected void afterSave() {
        }
    }

    private abstract class ApartmentCardIndicatingSubmitLink extends ApartmentCardSubmitLink implements IAjaxIndicatorAware {

        private final AjaxIndicatorAppender indicatorAppender = new AjaxIndicatorAppender();

        ApartmentCardIndicatingSubmitLink(String id) {
            super(id);
            add(indicatorAppender);
        }

        /**
         * @see IAjaxIndicatorAware#getAjaxIndicatorMarkupId()
         * @return the markup id of the ajax indicator
         *
         */
        @Override
        public String getAjaxIndicatorMarkupId() {
            return indicatorAppender.getMarkupId();
        }
    }

    private class AddRegistrationToolbarLink extends ApartmentCardSubmitLink {

        AddRegistrationToolbarLink(String id) {
            super(id);
        }

        @Override
        protected void afterSave() {
            setResponsePage(new RegistrationEdit(newApartmentCard,
                    getAddressEntity(), getAddressId(), registrationStrategy.newInstance()));
        }
    }

    /**
     * Edit existing apartment card.
     * @param apartmentCard
     */
    public ApartmentCardEdit(ApartmentCard apartmentCard) {
        init(apartmentCard);
    }

    /**
     * New apartment card.
     * @param addressEntity
     * @param addressId
     */
    public ApartmentCardEdit(String addressEntity, long addressId) {
        this.addressEntity = addressEntity;
        this.addressId = addressId;
        init(apartmentCardStrategy.newInstance());
    }

    public ApartmentCardEdit(long apartmentCardId) {
        init(apartmentCardStrategy.findById(apartmentCardId, true));
    }

    private void init(final ApartmentCard apartmentCard) {
        if (apartmentCard.getId() == null) {
            oldApartmentCard = null;
            newApartmentCard = apartmentCard;
        } else {
            newApartmentCard = apartmentCard;
            oldApartmentCard = CloneUtil.cloneObject(newApartmentCard);
        }

        add(JavascriptPackageResource.getHeaderContribution(WebCommonResourceInitializer.SCROLL_JS));
        add(JavascriptPackageResource.getHeaderContribution(ApartmentCardEdit.class, ApartmentCardEdit.class.getSimpleName() + ".js"));
        add(CSSPackageResource.getHeaderContribution(ApartmentCardEdit.class, ApartmentCardEdit.class.getSimpleName() + ".css"));

        IModel<String> labelModel = new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return stringBean.displayValue(apartmentCardStrategy.getEntity().getEntityNames(), getLocale());
            }
        };
        Label title = new Label("title", labelModel);
        add(title);
        Label label = new Label("label", labelModel);
        label.setOutputMarkupId(true);
        add(label);
        scrollToComponent = label;

        messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        add(messages);

        form = new Form("form");

        final Entity entity = apartmentCardStrategy.getEntity();

        //address
        WebMarkupContainer addressContainer = new WebMarkupContainer("addressContainer");
        final EntityAttributeType addressAttributeType = entity.getAttributeType(ADDRESS);
        addressContainer.add(new Label("label", labelModel(addressAttributeType.getAttributeNames(), getLocale())));
        addressContainer.add(new WebMarkupContainer("required").setVisible(addressAttributeType.isMandatory()));

        addressSearchComponentState = initAddressSearchComponentState();
        AddressSearchPanel address = new AddressSearchPanel("address", addressSearchComponentState,
                of("city", "street", "building", "apartment", "room"), null, ShowMode.ACTIVE, true);
        addressContainer.add(address);
        form.add(addressContainer);

        //owner
        WebMarkupContainer ownerContainer = new WebMarkupContainer("ownerContainer");
        final EntityAttributeType ownerAttributeType = entity.getAttributeType(OWNER);
        IModel<String> ownerLabelModel = labelModel(ownerAttributeType.getAttributeNames(), getLocale());
        ownerContainer.add(new Label("label", ownerLabelModel));
        ownerContainer.add(new WebMarkupContainer("required").setVisible(ownerAttributeType.isMandatory()));

        PersonPicker owner = new PersonPicker("owner", PersonAgeType.ADULT, new PropertyModel<Person>(newApartmentCard, "owner"),
                true, ownerLabelModel, true);
        ownerContainer.add(owner);
        form.add(ownerContainer);

        // form of ownership
        form.add(initFormOfOwnership());

        //permission panel
        form.add(new PermissionPanel("permissionPanel", sessionBean.getUserOrganizationObjectIds(), newApartmentCard.getSubjectIds()));

        //register owner
        WebMarkupContainer registerOwnerContainer = new WebMarkupContainer("registerOwnerContainer");
        form.add(registerOwnerContainer);
        registerOwnerContainer.setVisible(isNew());

        final CheckBox registerOwnerCheckBox = new CheckBox("registerOwnerCheckBox", new Model<Boolean>(false));
        registerOwnerCheckBox.setOutputMarkupId(true);
        registerOwnerContainer.add(registerOwnerCheckBox);

        final RegisterOwnerDialog registerOwnerDialog = new RegisterOwnerDialog("registerOwnerDialog");
        add(registerOwnerDialog);

        registerOwnerCheckBox.add(new AjaxFormSubmitBehavior(form, "onclick") {

            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                if (registerOwnerCheckBox.getModelObject()) {
                    try {
                        if (validate()) {
                            save();
                            registerOwnerDialog.open(target, newApartmentCard);
                        } else {
                            registerOwnerCheckBox.setModelObject(false);
                            target.addComponent(registerOwnerCheckBox);
                            target.addComponent(messages);
                            scrollToMessages(target);
                        }
                    } catch (Exception e) {
                        log.error("", e);
                        error(getString("db_error"));
                        registerOwnerCheckBox.setModelObject(false);
                        target.addComponent(registerOwnerCheckBox);
                        target.addComponent(messages);
                        scrollToMessages(target);
                    }
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                target.addComponent(messages);
                registerOwnerCheckBox.clearInput();
                target.addComponent(registerOwnerCheckBox);
                scrollToMessages(target);
            }
        });

        //registrations
        final Map<Long, IModel<Boolean>> selectedMap = newHashMap();
        for (Registration registration : apartmentCard.getRegistrations()) {
            if (!registration.isFinished()) {
                selectedMap.put(registration.getId(), new Model<Boolean>(false));
            }
        }

        final RemoveRegistrationDialog removeRegistrationDialog = new RemoveRegistrationDialog("removeRegistrationDialog");
        add(removeRegistrationDialog);

        final ChangeRegistrationTypeDialog changeRegistrationTypeDialog = new ChangeRegistrationTypeDialog("changeRegistrationTypeDialog");
        add(changeRegistrationTypeDialog);

        final AjaxLink<Void> removeRegistration = new AjaxLink<Void>("removeRegistration") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                List<Registration> registrationsToRemove = newArrayList(filter(apartmentCard.getRegistrations(), new Predicate<Registration>() {

                    @Override
                    public boolean apply(Registration registration) {
                        IModel<Boolean> model = selectedMap.get(registration.getId());
                        return model != null ? model.getObject() : false;
                    }
                }));
                removeRegistrationDialog.open(target, apartmentCard.getId(), registrationsToRemove);
            }

            @Override
            public boolean isVisible() {
                return isAnySelected(selectedMap.values());
            }
        };
        removeRegistration.setOutputMarkupPlaceholderTag(true);
        form.add(removeRegistration);

        final WebMarkupContainer changeRegistrationType = new WebMarkupContainer("changeRegistrationType") {

            @Override
            public boolean isVisible() {
                return isAnySelected(selectedMap.values());
            }
        };
        changeRegistrationType.setOutputMarkupPlaceholderTag(true);
        form.add(changeRegistrationType);

        AjaxLink<Void> changeRegistrationTypeButton = new AjaxLink<Void>("changeRegistrationTypeButton") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                List<Registration> registrationsToChangeType = newArrayList(filter(apartmentCard.getRegistrations(), new Predicate<Registration>() {

                    @Override
                    public boolean apply(Registration registration) {
                        IModel<Boolean> model = selectedMap.get(registration.getId());
                        return model != null ? model.getObject() : false;
                    }
                }));
                changeRegistrationTypeDialog.open(target, apartmentCard.getId(), registrationsToChangeType);
            }
        };
        changeRegistrationType.add(changeRegistrationTypeButton);

        AjaxCheckBox allSelected = new AjaxCheckBox("allSelected", new Model<Boolean>(false)) {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                for (IModel<Boolean> selectedModel : selectedMap.values()) {
                    selectedModel.setObject(getModelObject());
                }
                target.addComponent(removeRegistration);
                target.addComponent(changeRegistrationType);
            }
        };
        allSelected.setVisible(!selectedMap.isEmpty());
        form.add(allSelected);

        ListView<Registration> registrations = new ListView<Registration>("registrations", apartmentCard.getRegistrations()) {

            @Override
            protected void populateItem(ListItem<Registration> item) {
                final Registration registration = item.getModelObject();

                AjaxCheckBox selected = new AjaxCheckBox("selected", selectedMap.get(registration.getId())) {

                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        target.addComponent(removeRegistration);
                        target.addComponent(changeRegistrationType);
                    }
                };
                selected.setEnabled(!registration.isFinished());
                item.add(selected);

                Link<Void> personLink = new Link<Void>("personLink") {

                    @Override
                    public void onClick() {
                        setResponsePage(new PersonEdit(ApartmentCardEdit.this, registration.getPerson().getId()));
                    }
                };
                personLink.add(new Label("personName", personStrategy.displayDomainObject(registration.getPerson(), getLocale())));
                item.add(personLink);
                Date birthDate = registration.getPerson().getBirthDate();
                item.add(new Label("personBirthDate", birthDate != null ? PersonDateFormatter.format(birthDate) : null));
                item.add(new Label("registrationType",
                        StringUtil.valueOf(registrationTypeStrategy.displayDomainObject(registration.getRegistrationType(), getLocale()))));
                Date registrationStartDate = registration.getRegistrationDate();
                item.add(new Label("registrationStartDate", registrationStartDate != null ? PersonDateFormatter.format(registrationStartDate) : null));
                Date registrationEndDate = registration.getDepartureDate();
                item.add(new Label("registrationEndDate", registrationEndDate != null ? PersonDateFormatter.format(registrationEndDate) : null));
                DomainObject ownerRelationship = registration.getOwnerRelationship();
                item.add(new Label("registrationOwnerRelationship", ownerRelationship != null
                        ? ownerRelationshipStrategy.displayDomainObject(ownerRelationship, getLocale()) : null));

                Link<Void> registrationDetails = new Link<Void>("registrationDetails") {

                    @Override
                    public void onClick() {
                        setResponsePage(new RegistrationEdit(newApartmentCard, ApartmentCardStrategy.getAddressEntity(newApartmentCard),
                                newApartmentCard.getAddressId(), registration));
                    }
                };
                registrationDetails.add(new Label("editMessage", new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        return canEdit(null, "registration", registration) ? getString("edit") : getString("view");
                    }
                }));
                item.add(registrationDetails);

                if (registration.isFinished()) {
                    item.add(new CssAttributeBehavior("finished_registration"));
                }
            }
        };
        form.add(registrations);

        ApartmentCardIndicatingSubmitLink addRegistration = new ApartmentCardIndicatingSubmitLink("addRegistration") {

            @Override
            protected void afterSave() {
                setResponsePage(new RegistrationEdit(newApartmentCard,
                        getAddressEntity(), getAddressId(), registrationStrategy.newInstance()));
            }
        };
        addRegistration.setVisible(canEdit(null, apartmentCardStrategy.getEntityTable(), newApartmentCard));
        form.add(addRegistration);

        //housing rights
        initSystemAttributeInput(form, "housingRights", HOUSING_RIGHTS);

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
        form.add(userAttributesView);

        //save-cancel functional
        ApartmentCardIndicatingSubmitLink submit = new ApartmentCardIndicatingSubmitLink("submit") {

            @Override
            protected void afterSave() {
                back();
            }
        };
        submit.setVisible(canEdit(null, apartmentCardStrategy.getEntityTable(), newApartmentCard));
        form.add(submit);
        Link<Void> cancel = new Link<Void>("cancel") {

            @Override
            public void onClick() {
                back();
            }
        };
        cancel.setVisible(canEdit(null, apartmentCardStrategy.getEntityTable(), newApartmentCard));
        form.add(cancel);
        Link<Void> back = new Link<Void>("back") {

            @Override
            public void onClick() {
                back();
            }
        };
        back.setVisible(!canEdit(null, apartmentCardStrategy.getEntityTable(), newApartmentCard));
        form.add(back);
        add(form);

        //archiveApartmentCardDialog
        archiveApartmentCardDialog = new ArchiveApartmentCardDialog("archiveApartmentCardDialog");
        archiveApartmentCardDialog.setVisible(!isNew());
        add(archiveApartmentCardDialog);
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
        Attribute attribute = newApartmentCard.getAttribute(attributeTypeId);
        parent.add(newInputComponent(apartmentCardStrategy.getEntityTable(), null, newApartmentCard, attribute, getLocale(), false));
    }

    private void beforePersist() {
        // address
        Attribute addressAttribute = newApartmentCard.getAttribute(ADDRESS);
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
        Attribute ownerAttribute = newApartmentCard.getAttribute(OWNER);
        DomainObject owner = newApartmentCard.getOwner();
        Long ownerId = owner != null ? owner.getId() : null;
        ownerAttribute.setValueId(ownerId);
        ownerAttribute.setValueTypeId(OWNER_TYPE);

        // form of ownership
        Attribute formOfOwnershipAttribute = newApartmentCard.getAttribute(FORM_OF_OWNERSHIP);
        DomainObject ownershipForm = newApartmentCard.getOwnershipForm();
        Long ownershipFormId = ownershipForm != null ? ownershipForm.getId() : null;
        formOfOwnershipAttribute.setValueId(ownershipFormId);
        formOfOwnershipAttribute.setValueTypeId(FORM_OF_OWNERSHIP_TYPE);
    }

    private boolean validate() {
        //address validation
        Long addressObjectId = null;
        Long addressTypeId = null;

        DomainObject building = addressSearchComponentState.get("building");
        if (building == null || building.getId() <= 0) {
            error(getString("address_failing"));
        } else {
            DomainObject apartment = addressSearchComponentState.get("apartment");
            if (apartment == null || apartment.getId() <= 0) {
                if (!apartmentCardStrategy.isLeafAddress(building.getId(), "building")) {
                    error(getString("address_failing"));
                } else {
                    addressObjectId = building.getId();
                    addressTypeId = ADDRESS_BUILDING;
                }
            } else {
                DomainObject room = addressSearchComponentState.get("room");
                if (room == null || room.getId() <= 0) {
                    if (!apartmentCardStrategy.isLeafAddress(apartment.getId(), "apartment")) {
                        error(getString("address_failing"));
                    } else {
                        addressObjectId = apartment.getId();
                        addressTypeId = ADDRESS_APARTMENT;
                    }
                } else {
                    if (room.getId() == null || room.getId() <= 0) {
                        error(getString("address_failing"));
                    } else {
                        addressObjectId = room.getId();
                        addressTypeId = ADDRESS_ROOM;
                    }
                }
            }
        }

        //one of registered people can have OWNER owner relationship but house owner can be another man
        long ownerId = newApartmentCard.getOwner().getId();
        for (Registration registration : newApartmentCard.getRegistrations()) {
            if (!registration.isFinished()) {
                if (registration.getOwnerRelationship().getId().equals(OwnerRelationshipStrategy.OWNER)
                        && !registration.getPerson().getId().equals(ownerId)) {
                    error(getString("owner_relationship_mismatch"));
                    break;
                }
            }
        }

        //check address-owner pair is unique
        if (addressObjectId != null && addressTypeId != null) {
            if (!apartmentCardStrategy.validateOwnerAddressUniqueness(addressObjectId, addressTypeId, ownerId, newApartmentCard.getId())) {
                error(getString("owner_address_uniqueness_error"));
            }
        }

        return getSession().getFeedbackMessages().isEmpty();
    }

    private String getAddressEntity() {
        return addressEntity != null ? addressEntity : ApartmentCardStrategy.getAddressEntity(newApartmentCard);
    }

    private long getAddressId() {
        return addressId != null ? addressId : newApartmentCard.getAddressId();
    }

    private SearchComponentState initAddressSearchComponentState() {
        String currentAddressEntity = getAddressEntity();
        long currentAddressId = getAddressId();
        SearchComponentState searchComponentState = new SearchComponentState();
        IStrategy addressStrategy = strategyFactory.getStrategy(currentAddressEntity);
        DomainObject addressObject = addressStrategy.findById(currentAddressId, true);
        SimpleObjectInfo info = addressStrategy.findParentInSearchComponent(currentAddressId, null);
        if (info != null) {
            searchComponentState = addressStrategy.getSearchComponentStateForParent(info.getId(), info.getEntityTable(), null);
            searchComponentState.put(currentAddressEntity, addressObject);
        }
        if (currentAddressEntity.equals("apartment")) {
            DomainObject room = new DomainObject();
            room.setId(SearchComponentState.NOT_SPECIFIED_ID);
            searchComponentState.put("room", room);
        } else if (currentAddressEntity.equals("building")) {
            DomainObject room = new DomainObject();
            room.setId(SearchComponentState.NOT_SPECIFIED_ID);
            searchComponentState.put("room", room);
            DomainObject apartment = new DomainObject();
            apartment.setId(SearchComponentState.NOT_SPECIFIED_ID);
            searchComponentState.put("apartment", apartment);
        }
        return searchComponentState;
    }

    private boolean isNew() {
        return oldApartmentCard == null;
    }

    private void save() {
        beforePersist();
        if (isNew()) {
            apartmentCardStrategy.insert(newApartmentCard, DateUtil.getCurrentDate());
        } else {
            apartmentCardStrategy.update(oldApartmentCard, newApartmentCard, DateUtil.getCurrentDate());
        }
        logBean.log(Log.STATUS.OK, Module.NAME, ApartmentCardEdit.class, isNew() ? Log.EVENT.CREATE : Log.EVENT.EDIT, apartmentCardStrategy,
                oldApartmentCard, newApartmentCard, getLocale(), null);
    }

    private void archive() {
        apartmentCardStrategy.archive(oldApartmentCard, DateUtil.getCurrentDate());
        logBean.logArchivation(Log.STATUS.OK, Module.NAME, ApartmentCardEdit.class, apartmentCardStrategy.getEntityTable(),
                oldApartmentCard.getId(), getString("archivation_log_message"));
    }

    private void back() {
        String backAddressEntity = null;
        Long backAddressId = null;
        DomainObject room = addressSearchComponentState.get("room");
        if (room != null && room.getId() > 0) {
            backAddressId = room.getParentId();
            backAddressEntity = room.getParentEntityId().equals(100L) ? "apartment" : "building";
        } else {
            backAddressEntity = getAddressEntity();
            backAddressId = getAddressId();
        }
        setResponsePage(new ApartmentCardList(backAddressEntity, backAddressId));
    }

    private Component initFormOfOwnership() {
        final EntityAttributeType formOfOwnershipAttributeType = apartmentCardStrategy.getEntity().getAttributeType(FORM_OF_OWNERSHIP);

        WebMarkupContainer formOfOwnershipContainer = new WebMarkupContainer("formOfOwnershipContainer");
        add(formOfOwnershipContainer);

        //label
        IModel<String> labelModel = labelModel(formOfOwnershipAttributeType.getAttributeNames(), getLocale());
        formOfOwnershipContainer.add(new Label("label", labelModel));

        //required
        formOfOwnershipContainer.add(new WebMarkupContainer("required").setVisible(formOfOwnershipAttributeType.isMandatory()));

        //form of ownership
        final List<DomainObject> allOwnershipForms = ownershipFormStrategy.getAll();
        IModel<DomainObject> formOfOwnershipModel = new Model<DomainObject>() {

            @Override
            public void setObject(DomainObject object) {
                newApartmentCard.setOwnershipForm(object);
            }

            @Override
            public DomainObject getObject() {
                return newApartmentCard.getOwnershipForm();
            }
        };

        DisableAwareDropDownChoice<DomainObject> formOfOwnership = new DisableAwareDropDownChoice<DomainObject>("formOfOwnership",
                formOfOwnershipModel, allOwnershipForms, new DomainObjectDisableAwareRenderer() {

            @Override
            public Object getDisplayValue(DomainObject object) {
                return ownershipFormStrategy.displayDomainObject(object, getLocale());
            }
        });
        formOfOwnership.setRequired(formOfOwnershipAttributeType.isMandatory());
        formOfOwnership.setLabel(labelModel);
        formOfOwnership.setEnabled(canEdit(null, apartmentCardStrategy.getEntityTable(), newApartmentCard));
        formOfOwnershipContainer.add(formOfOwnership);
        return formOfOwnershipContainer;
    }

    private boolean isAnySelected(Collection<IModel<Boolean>> models) {
        return any(models, new Predicate<IModel<Boolean>>() {

            @Override
            public boolean apply(IModel<Boolean> model) {
                return model.getObject();
            }
        });
    }

    private void scrollToMessages(AjaxRequestTarget target) {
        target.appendJavascript(ScrollToElementUtil.scrollTo(scrollToComponent.getMarkupId()));
    }

    @Override
    protected List<? extends ToolbarButton> getToolbarButtons(String id) {
        return of(
                new AddApartmentCardButton(id) {

                    @Override
                    protected void onClick() {
                        setResponsePage(new ApartmentCardEdit(
                                ApartmentCardStrategy.getAddressEntity(oldApartmentCard), oldApartmentCard.getAddressId()));
                    }

                    @Override
                    protected void onBeforeRender() {
                        super.onBeforeRender();
                        setVisible(!isNew());
                    }
                },
                new AddRegistrationToolbarButton(id) {

                    @Override
                    protected AbstractLink newLink(String linkId) {
                        return new AddRegistrationToolbarLink(linkId);
                    }
                },
                new ArchiveApartmentCardButton(id) {

                    @Override
                    protected void onClick(AjaxRequestTarget target) {
                        if (archiveApartmentCardDialog != null) {
                            if (oldApartmentCard.getRegisteredCount() > 0) {
                                archiveApartmentCardDialog.open(target);
                            } else {
                                archive();
                                back();
                            }
                        }
                    }

                    @Override
                    protected void onBeforeRender() {
                        super.onBeforeRender();
                        setVisible(!isNew());
                    }
                });
    }
}

