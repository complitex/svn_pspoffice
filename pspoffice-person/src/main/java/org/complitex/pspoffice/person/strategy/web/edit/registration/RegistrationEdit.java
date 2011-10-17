/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.edit.registration;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import static com.google.common.collect.ImmutableList.*;
import java.text.MessageFormat;
import java.util.List;
import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Iterables.*;
import javax.ejb.EJB;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.string.Strings;
import org.complitex.address.service.AddressRendererBean;
import org.complitex.dictionary.converter.DateConverter;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.Log;
import org.complitex.dictionary.entity.StatusType;
import org.complitex.dictionary.entity.description.Entity;
import org.complitex.dictionary.entity.description.EntityAttributeType;
import org.complitex.dictionary.service.LogBean;
import org.complitex.dictionary.service.StringCultureBean;
import org.complitex.dictionary.util.CloneUtil;
import org.complitex.dictionary.util.DateUtil;
import org.complitex.dictionary.web.component.DisableAwareDropDownChoice;
import org.complitex.dictionary.web.component.DomainObjectDisableAwareRenderer;
import org.complitex.dictionary.web.component.dateinput.MaskedDateInput;
import org.complitex.dictionary.web.component.fieldset.CollapsibleFieldset;
import org.complitex.dictionary.web.component.scroll.ScrollToElementUtil;
import org.complitex.dictionary.web.component.type.MaskedDateInputPanel;
import org.complitex.pspoffice.ownerrelationship.strategy.OwnerRelationshipStrategy;
import org.complitex.pspoffice.person.Module;
import org.complitex.pspoffice.person.report.web.F3ReferencePage;
import org.complitex.pspoffice.person.report.web.RegistrationCardPage;
import org.complitex.pspoffice.person.report.web.RegistrationStopCouponPage;
import org.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.RegistrationStrategy;
import org.complitex.pspoffice.person.strategy.entity.ApartmentCard;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.entity.PersonAgeType;
import org.complitex.pspoffice.person.strategy.entity.Registration;
import org.complitex.pspoffice.person.strategy.web.component.PersonPicker;
import org.complitex.pspoffice.person.strategy.web.edit.apartment_card.ApartmentCardEdit;
import org.complitex.pspoffice.person.strategy.web.edit.registration.toolbar.F3ReferenceButton;
import org.complitex.pspoffice.person.strategy.web.edit.registration.toolbar.RegistrationCardButton;
import org.complitex.pspoffice.person.strategy.web.edit.registration.toolbar.RegistrationStopCouponButton;
import org.complitex.pspoffice.registration_type.strategy.RegistrationTypeStrategy;
import org.complitex.resources.WebCommonResourceInitializer;
import org.complitex.template.web.component.toolbar.ToolbarButton;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.FormTemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.complitex.dictionary.strategy.web.DomainObjectAccessUtil.*;
import static org.complitex.pspoffice.person.strategy.RegistrationStrategy.*;
import static org.complitex.dictionary.web.component.DomainObjectInputPanel.*;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class RegistrationEdit extends FormTemplatePage {

    private static final Logger log = LoggerFactory.getLogger(RegistrationEdit.class);
    @EJB
    private RegistrationStrategy registrationStrategy;
    @EJB
    private AddressRendererBean addressRendererBean;
    @EJB
    private OwnerRelationshipStrategy ownerRelationshipStrategy;
    @EJB
    private RegistrationTypeStrategy registrationTypeStrategy;
    @EJB
    private ApartmentCardStrategy apartmentCardStrategy;
    @EJB
    private StringCultureBean stringBean;
    @EJB
    private LogBean logBean;
    @EJB
    private PersonStrategy personStrategy;
    private ApartmentCard apartmentCard;
    private Registration newRegistration;
    private Registration oldRegistration;
    private String addressEntity;
    private long addressId;
    private IModel<String> addressModel;

    public RegistrationEdit(ApartmentCard apartmentCard, String addressEntity, long addressId, Registration registration) {
        this.apartmentCard = apartmentCard;
        this.addressEntity = addressEntity;
        this.addressId = addressId;

        if (registration.getId() == null) {
            newRegistration = registration;
        } else {
            newRegistration = registration;
            oldRegistration = CloneUtil.cloneObject(newRegistration);
        }
        init();
    }

    private boolean isNew() {
        return oldRegistration == null;
    }

    private boolean isHistory() {
        return newRegistration.getStatus() == StatusType.ARCHIVE;
    }

    private void init() {
        add(JavascriptPackageResource.getHeaderContribution(WebCommonResourceInitializer.SCROLL_JS));

        final Entity entity = registrationStrategy.getEntity();

        addressModel = new LoadableDetachableModel<String>() {

            @Override
            protected String load() {
                return addressRendererBean.displayAddress(addressEntity, addressId, getLocale());
            }
        };

        IModel<String> labelModel = new StringResourceModel("label", null, new Object[]{addressModel.getObject()});
        Label title = new Label("title", labelModel);
        add(title);
        final Label label = new Label("label", labelModel);
        label.setOutputMarkupId(true);
        add(label);

        final FeedbackPanel messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        add(messages);

        Form form = new Form("form");

        //address
        form.add(new Label("address", addressModel));

        //person
        WebMarkupContainer personContainer = new WebMarkupContainer("personContainer");
        final EntityAttributeType personAttributeType = entity.getAttributeType(PERSON);
        personContainer.add(new WebMarkupContainer("required").setVisible(personAttributeType.isMandatory()));

        PersonPicker person = new PersonPicker("person", PersonAgeType.ANY, new PropertyModel<Person>(newRegistration, "person"),
                true, labelModel(personAttributeType.getAttributeNames(), getLocale()), isNew());
        personContainer.add(person);
        form.add(personContainer);

        //registration date
        initSystemAttributeInput(form, "registrationDate", REGISTRATION_DATE, true);
        if (!isHistory()) {
            stringBean.getSystemStringCulture(newRegistration.getAttribute(REGISTRATION_DATE).getLocalizedValues()).
                    setValue(new DateConverter().toString(DateUtil.getCurrentDate()));

            if (!isNew()) {
                MaskedDateInput registrationDate = (MaskedDateInput) form.get("registrationDateContainer:input:"
                        + MaskedDateInputPanel.DATE_INPUT_ID);
                registrationDate.setMinDate(newRegistration.getPerson().getBirthDate());
            }
        }

        form.add(initRegistrationType());

        CollapsibleFieldset arrivalAddressFieldset = new CollapsibleFieldset("arrivalAddressFieldset",
                new ResourceModel("arrival_address"), !isNew());
        form.add(arrivalAddressFieldset);
        initSystemAttributeInput(arrivalAddressFieldset, "arrivalCountry", ARRIVAL_COUNTRY, true);
        initSystemAttributeInput(arrivalAddressFieldset, "arrivalRegion", ARRIVAL_REGION, true);
        initSystemAttributeInput(arrivalAddressFieldset, "arrivalStreet", ARRIVAL_STREET, true);
        initSystemAttributeInput(arrivalAddressFieldset, "arrivalDistrict", ARRIVAL_DISTRICT, true);
        initSystemAttributeInput(arrivalAddressFieldset, "arrivalBuildingNumber", ARRIVAL_BUILDING_NUMBER, true);
        initSystemAttributeInput(arrivalAddressFieldset, "arrivalCity", ARRIVAL_CITY, true);
        initSystemAttributeInput(arrivalAddressFieldset, "arrivalBuildingCorp", ARRIVAL_BUILDING_CORP, true);
        initSystemAttributeInput(arrivalAddressFieldset, "arrivalApartment", ARRIVAL_APARTMENT, true);
        initSystemAttributeInput(arrivalAddressFieldset, "arrivalDate", ARRIVAL_DATE, true);

        CollapsibleFieldset departureAddressFieldset = new CollapsibleFieldset("departureAddressFieldset",
                new ResourceModel("departure_address"), false);
        departureAddressFieldset.setVisible(isHistory());
        form.add(departureAddressFieldset);
        initSystemAttributeInput(departureAddressFieldset, "departureCountry", DEPARTURE_COUNTRY, true);
        initSystemAttributeInput(departureAddressFieldset, "departureRegion", DEPARTURE_REGION, true);
        initSystemAttributeInput(departureAddressFieldset, "departureDistrict", DEPARTURE_DISTRICT, true);
        initSystemAttributeInput(departureAddressFieldset, "departureCity", DEPARTURE_CITY, true);
        initSystemAttributeInput(departureAddressFieldset, "departureStreet", DEPARTURE_STREET, true);
        initSystemAttributeInput(departureAddressFieldset, "departureBuildingNumber", DEPARTURE_BUILDING_NUMBER, true);
        initSystemAttributeInput(departureAddressFieldset, "departureBuildingCorp", DEPARTURE_BUILDING_CORP, true);
        initSystemAttributeInput(departureAddressFieldset, "departureApartment", DEPARTURE_APARTMENT, true);
        initSystemAttributeInput(departureAddressFieldset, "departureDate", DEPARTURE_DATE, true);
        initSystemAttributeInput(departureAddressFieldset, "departureReason", DEPARTURE_REASON, true);

        form.add(initOwnerRelationship());

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
            Attribute userAttribute = newRegistration.getAttribute(attributeTypeId);
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
        form.add(userAttributesView);

        IndicatingAjaxButton submit = new IndicatingAjaxButton("submit") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    if (RegistrationEdit.this.validate()) {
                        save();
                        back();
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

            private void scrollToMessages(AjaxRequestTarget target) {
                target.appendJavascript(ScrollToElementUtil.scrollTo(label.getMarkupId()));
            }
        };
        submit.setVisible(canEdit(null, registrationStrategy.getEntityTable(), newRegistration));
        form.add(submit);
        Link<Void> back = new Link<Void>("back") {

            @Override
            public void onClick() {
                back();
            }
        };
        form.add(back);
        add(form);
    }

    private Component initOwnerRelationship() {
        final EntityAttributeType ownerRelationshipAttributeType = registrationStrategy.getEntity().getAttributeType(OWNER_RELATIONSHIP);

        WebMarkupContainer ownerRelationshipContainer = new WebMarkupContainer("ownerRelationshipContainer");
        add(ownerRelationshipContainer);

        //label
        IModel<String> labelModel = labelModel(ownerRelationshipAttributeType.getAttributeNames(), getLocale());
        ownerRelationshipContainer.add(new Label("label", labelModel));

        //required
        ownerRelationshipContainer.add(new WebMarkupContainer("required").setVisible(ownerRelationshipAttributeType.isMandatory()));

        //owner relationship
        final List<DomainObject> allOwnerRelationships = ownerRelationshipStrategy.getAll();
        IModel<DomainObject> ownerRelationshipModel = new Model<DomainObject>() {

            @Override
            public void setObject(DomainObject object) {
                newRegistration.setOwnerRelationship(object);
            }

            @Override
            public DomainObject getObject() {
                return newRegistration.getOwnerRelationship();
            }
        };

        DisableAwareDropDownChoice<DomainObject> ownerRelationship = new DisableAwareDropDownChoice<DomainObject>("input",
                ownerRelationshipModel, allOwnerRelationships, new DomainObjectDisableAwareRenderer() {

            @Override
            public Object getDisplayValue(DomainObject object) {
                return ownerRelationshipStrategy.displayDomainObject(object, getLocale());
            }
        });
        ownerRelationship.setRequired(ownerRelationshipAttributeType.isMandatory());
        ownerRelationship.setLabel(labelModel);
        ownerRelationship.setEnabled(canEdit(null, registrationStrategy.getEntityTable(), newRegistration));
        ownerRelationshipContainer.add(ownerRelationship);
        return ownerRelationshipContainer;
    }

    private Component initRegistrationType() {
        final EntityAttributeType registrationTypeAttributeType = registrationStrategy.getEntity().getAttributeType(REGISTRATION_TYPE);

        WebMarkupContainer registrationTypeContainer = new WebMarkupContainer("registrationTypeContainer");
        add(registrationTypeContainer);

        //label
        IModel<String> labelModel = labelModel(registrationTypeAttributeType.getAttributeNames(), getLocale());
        registrationTypeContainer.add(new Label("label", labelModel));

        //required
        registrationTypeContainer.add(new WebMarkupContainer("required").setVisible(registrationTypeAttributeType.isMandatory()));

        //registration type
        final List<DomainObject> allRegistrationTypes = registrationTypeStrategy.getAll();
        IModel<DomainObject> registrationTypeModel = new Model<DomainObject>() {

            @Override
            public void setObject(DomainObject object) {
                newRegistration.setRegistrationType(object);
            }

            @Override
            public DomainObject getObject() {
                return newRegistration.getRegistrationType();
            }
        };

        DisableAwareDropDownChoice<DomainObject> registrationType = new DisableAwareDropDownChoice<DomainObject>("input",
                registrationTypeModel, allRegistrationTypes, new DomainObjectDisableAwareRenderer() {

            @Override
            public Object getDisplayValue(DomainObject object) {
                return registrationTypeStrategy.displayDomainObject(object, getLocale());
            }
        });
        registrationType.setRequired(registrationTypeAttributeType.isMandatory());
        registrationType.setLabel(labelModel);
        registrationType.setEnabled(canEdit(null, registrationStrategy.getEntityTable(), newRegistration));
        registrationTypeContainer.add(registrationType);
        return registrationTypeContainer;
    }

    private void initSystemAttributeInput(MarkupContainer parent, String id, long attributeTypeId, boolean showIfMissing) {
        WebMarkupContainer container = new WebMarkupContainer(id + "Container");
        parent.add(container);
        initAttributeInput(container, attributeTypeId, showIfMissing);
    }

    private void initAttributeInput(MarkupContainer parent, long attributeTypeId, boolean showIfMissing) {
        final EntityAttributeType attributeType = registrationStrategy.getEntity().getAttributeType(attributeTypeId);

        //label
        parent.add(new Label("label", labelModel(attributeType.getAttributeNames(), getLocale())));

        //required container
        WebMarkupContainer requiredContainer = new WebMarkupContainer("required");
        requiredContainer.setVisible(attributeType.isMandatory());
        parent.add(requiredContainer);

        //input component
        Attribute attribute = newRegistration.getAttribute(attributeTypeId);
        if (attribute == null) {
            attribute = new Attribute();
            attribute.setLocalizedValues(stringBean.newStringCultures());
            attribute.setAttributeTypeId(attributeTypeId);
            parent.setVisible(showIfMissing);
        }
        parent.add(newInputComponent(registrationStrategy.getEntityTable(), null, newRegistration, attribute, getLocale(), false));
    }

    public void beforePersist() {
        // person
        newRegistration.getAttribute(PERSON).setValueId(newRegistration.getPerson().getId());

        // owner relationship
        Attribute ownerRelationshipAttribute = newRegistration.getAttribute(OWNER_RELATIONSHIP);
        DomainObject ownerRelationship = newRegistration.getOwnerRelationship();
        Long ownerRelationshipId = ownerRelationship != null ? ownerRelationship.getId() : null;
        ownerRelationshipAttribute.setValueId(ownerRelationshipId);

        // registration type
        Attribute registrationTypeAttribute = newRegistration.getAttribute(REGISTRATION_TYPE);
        DomainObject registrationType = newRegistration.getRegistrationType();
        Long registrationTypeId = registrationType != null ? registrationType.getId() : null;
        registrationTypeAttribute.setValueId(registrationTypeId);
    }

    private boolean validate() {
        //registration date must be greater than person's birth date
        if (newRegistration.getPerson().getBirthDate().after(newRegistration.getRegistrationDate())) {
            error(getString("registration_date_error"));
        }

        //permanent registration type
        Long oldRegistrationTypeId = oldRegistration == null ? null : oldRegistration.getRegistrationType().getId();
        if (newRegistration.getRegistrationType().getId().equals(RegistrationTypeStrategy.PERMANENT)
                && !newRegistration.getRegistrationType().getId().equals(oldRegistrationTypeId)) {
            String address = personStrategy.findPermanentRegistrationAddress(newRegistration.getPerson().getId(), getLocale());
            if (!Strings.isEmpty(address)) {
                error(MessageFormat.format(getString("permanent_registration_error"), address));
            }
        }

        //owner and owner relationship
        DomainObject ownerRelationship = newRegistration.getOwnerRelationship();
        Long ownerRelationshipId = ownerRelationship != null ? ownerRelationship.getId() : null;
        String ownerName = registrationStrategy.checkOwner(apartmentCard.getId(), ownerRelationshipId,
                newRegistration.getPerson().getId(), getLocale());
        if (!Strings.isEmpty(ownerName)) {
            error(MessageFormat.format(getString("owner_is_another_man"), ownerName));
        }

        //duplicate person registration check
        if (isNew() && !registrationStrategy.validateDuplicatePerson(apartmentCard.getId(), newRegistration.getPerson().getId())) {
            error(getString("person_already_registered"));
        }

        return getSession().getFeedbackMessages().isEmpty();
    }

    private void save() {
        beforePersist();
        if (isNew()) {
            apartmentCardStrategy.addRegistration(apartmentCard, newRegistration, DateUtil.getCurrentDate());
        } else {
            registrationStrategy.update(oldRegistration, newRegistration, DateUtil.getCurrentDate());
        }
        logBean.log(Log.STATUS.OK, Module.NAME, RegistrationEdit.class, isNew() ? Log.EVENT.CREATE : Log.EVENT.EDIT,
                registrationStrategy, oldRegistration, newRegistration, null);
    }

    private void back() {
        setResponsePage(new ApartmentCardEdit(apartmentCard.getId()));
    }

    @Override
    protected List<? extends ToolbarButton> getToolbarButtons(String id) {
        return of(new RegistrationStopCouponButton(id) {

            @Override
            protected void onClick() {
                setResponsePage(new RegistrationStopCouponPage(oldRegistration, addressEntity, addressId));
            }

            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                setVisible(isHistory());
            }
        }, new RegistrationCardButton(id) {

            @Override
            protected void onClick() {
                setResponsePage(new RegistrationCardPage(oldRegistration, addressEntity, addressId));
            }

            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                setVisible(!isNew());
            }
        }, new F3ReferenceButton(id) {

            @Override
            protected void onClick() {
                setResponsePage(new F3ReferencePage(oldRegistration, apartmentCard));
            }

            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                setVisible(!isHistory() && !isNew());
            }
        });
    }
}

