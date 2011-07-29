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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.Log;
import org.complitex.dictionary.entity.description.Entity;
import org.complitex.dictionary.entity.description.EntityAttributeType;
import org.complitex.dictionary.service.LogBean;
import org.complitex.dictionary.service.StringCultureBean;
import org.complitex.dictionary.strategy.IStrategy;
import org.complitex.dictionary.strategy.StrategyFactory;
import org.complitex.dictionary.util.CloneUtil;
import org.complitex.dictionary.util.DateUtil;
import org.complitex.dictionary.util.StringUtil;
import org.complitex.dictionary.web.component.ShowMode;
import org.complitex.dictionary.web.component.scroll.ScrollToElementUtil;
import org.complitex.dictionary.web.component.search.SearchComponentState;
import org.complitex.dictionary.web.component.search.WiQuerySearchComponent;
import org.complitex.pspoffice.person.Module;
import org.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.RegistrationStrategy;
import org.complitex.pspoffice.person.strategy.entity.ApartmentCard;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.entity.Registration;
import org.complitex.pspoffice.person.strategy.web.component.PersonPicker;
import org.complitex.pspoffice.person.strategy.web.list.ApartmentCardSearch;
import org.complitex.resources.WebCommonResourceInitializer;
import org.complitex.template.web.component.toolbar.AddItemButton;
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
@AuthorizeInstantiation(SecurityRole.PERSON_MODULE_EDIT)
public final class ApartmentCardEdit extends FormTemplatePage {

    private static final Logger log = LoggerFactory.getLogger(ApartmentCardEdit.class);
    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("dd.MM.yyyy");
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
    private String addressEntity;
    private Long addressId;
    private ApartmentCard oldApartmentCard;
    private ApartmentCard newApartmentCard;
    private SearchComponentState addressSearchComponentState;
    private IModel<Person> ownerModel;

    private class SubmitLink extends AjaxSubmitLink {

        private FeedbackPanel messages;
        private Component scrollToComponent;

        public SubmitLink(String id, Form<?> form, FeedbackPanel messages, Component scrollToComponent) {
            super(id, form);
            this.messages = messages;
            this.scrollToComponent = scrollToComponent;
        }

        @Override
        protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
            try {
                if (validate()) {
                    save();
                    additionalAction();
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
            target.appendJavascript(ScrollToElementUtil.scrollTo(scrollToComponent.getMarkupId()));
        }

        protected void additionalAction() {
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

    private void init(ApartmentCard apartmentCard) {
        if (apartmentCard.getId() == null) {
            oldApartmentCard = null;
            newApartmentCard = apartmentCard;
        } else {
            newApartmentCard = apartmentCard;
            oldApartmentCard = CloneUtil.cloneObject(newApartmentCard);
        }

        add(JavascriptPackageResource.getHeaderContribution(WebCommonResourceInitializer.SCROLL_JS));

        IModel<String> labelModel = new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return stringBean.displayValue(apartmentCardStrategy.getEntity().getEntityNames(), getLocale());
            }
        };
        Label title = new Label("title", labelModel);
        add(title);
        final Label label = new Label("label", labelModel);
        label.setOutputMarkupId(true);
        add(label);

        final FeedbackPanel messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        add(messages);

        Form form = new Form("form");

        final Entity entity = apartmentCardStrategy.getEntity();

        //personal account
        initSystemAttributeInput(form, "personalAccount", PERSONAL_ACCOUNT);

        //address
        WebMarkupContainer addressContainer = new WebMarkupContainer("addressContainer");
        final EntityAttributeType addressAttributeType = entity.getAttributeType(ADDRESS);
        addressContainer.add(new Label("label", labelModel(addressAttributeType.getAttributeNames(), getLocale())));
        addressContainer.add(new WebMarkupContainer("required").setVisible(addressAttributeType.isMandatory()));

        addressSearchComponentState = initAddressSearchComponentState();
        WiQuerySearchComponent address = new WiQuerySearchComponent("address", addressSearchComponentState,
                of("city", "street", "building", "apartment", "room"), null, ShowMode.ACTIVE, true);
        addressContainer.add(address);
        form.add(addressContainer);

        //owner
        WebMarkupContainer ownerContainer = new WebMarkupContainer("ownerContainer");
        final EntityAttributeType ownerAttributeType = entity.getAttributeType(OWNER);
        ownerContainer.add(new Label("label", labelModel(ownerAttributeType.getAttributeNames(), getLocale())));
        ownerContainer.add(new WebMarkupContainer("required").setVisible(ownerAttributeType.isMandatory()));


        ownerModel = new Model<Person>(newApartmentCard.getOwner());
        PersonPicker owner = new PersonPicker("owner", ownerModel, true, labelModel, true);
        ownerContainer.add(owner);
        form.add(ownerContainer);

        // form of ownership
        initSystemAttributeInput(form, "formOfOwnership", FORM_OF_OWNERSHIP);

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
                        setResponsePage(new RegistrationEdit(newApartmentCard.getId(), newApartmentCard.getAddressEntity(),
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
            }
        };
        form.add(registrations);
        AjaxSubmitLink addRegistration = new SubmitLink("addRegistration", form, messages, label) {

            @Override
            protected void additionalAction() {
                setResponsePage(new RegistrationEdit(newApartmentCard.getId(),
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
        AjaxSubmitLink submit = new SubmitLink("submit", form, messages, label);
        submit.setVisible(canEdit(null, apartmentCardStrategy.getEntityTable(), newApartmentCard));
        form.add(submit);
        Link cancel = new Link("cancel") {

            @Override
            public void onClick() {
                back();
            }
        };
        cancel.setVisible(canEdit(null, apartmentCardStrategy.getEntityTable(), newApartmentCard));
        form.add(cancel);
        Link back = new Link("back") {

            @Override
            public void onClick() {
                back();
            }
        };
        back.setVisible(!canEdit(null, apartmentCardStrategy.getEntityTable(), newApartmentCard));
        form.add(back);
        add(form);
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
        DomainObject owner = ownerModel.getObject();
        Long ownerId = owner != null ? owner.getId() : null;
        if (ownerId != null) {
            ownerAttribute.setValueId(ownerId);
            ownerAttribute.setValueTypeId(OWNER_TYPE);
        } else {
            throw new IllegalStateException("Owner has not been filled in.");
        }
    }

    private boolean validate() {
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

        return getSession().getFeedbackMessages().isEmpty();
    }

    private String getAddressEntity() {
        return addressEntity != null ? addressEntity : newApartmentCard.getAddressEntity();
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
        back();
    }

    private void back() {
        setResponsePage(ApartmentCardSearch.class);
    }

    @Override
    protected List<? extends ToolbarButton> getToolbarButtons(String id) {
        return of(new AddItemButton(id) {

            @Override
            protected void onClick() {
                setResponsePage(new ApartmentCardEdit(oldApartmentCard.getAddressEntity(), oldApartmentCard.getAddressId()));
            }

            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                if (isNew()) {
                    setVisibilityAllowed(false);
                }
            }
        });
    }
}

