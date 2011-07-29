/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.edit;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import java.util.List;
import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Iterables.*;
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
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.complitex.address.service.AddressRendererBean;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.Log;
import org.complitex.dictionary.entity.description.Entity;
import org.complitex.dictionary.entity.description.EntityAttributeType;
import org.complitex.dictionary.service.LogBean;
import org.complitex.dictionary.service.StringCultureBean;
import org.complitex.dictionary.util.CloneUtil;
import org.complitex.dictionary.util.DateUtil;
import org.complitex.dictionary.web.component.DisableAwareDropDownChoice;
import org.complitex.dictionary.web.component.DomainObjectDisableAwareRenderer;
import org.complitex.dictionary.web.component.scroll.ScrollToElementUtil;
import org.complitex.pspoffice.ownerrelationship.strategy.OwnerRelationshipStrategy;
import org.complitex.pspoffice.person.Module;
import org.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import org.complitex.pspoffice.person.strategy.RegistrationStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.entity.Registration;
import org.complitex.pspoffice.person.strategy.web.component.PersonPicker;
import org.complitex.resources.WebCommonResourceInitializer;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.FormTemplatePage;
import static org.complitex.dictionary.strategy.web.DomainObjectAccessUtil.*;
import static org.complitex.pspoffice.person.strategy.RegistrationStrategy.*;
import static org.complitex.dictionary.web.component.DomainObjectInputPanel.*;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.PERSON_MODULE_EDIT)
public class RegistrationEdit extends FormTemplatePage {

    @EJB
    private RegistrationStrategy registrationStrategy;
    @EJB
    private AddressRendererBean addressRendererBean;
    @EJB
    private OwnerRelationshipStrategy ownerRelationshipStrategy;
    @EJB
    private StringCultureBean stringBean;
    @EJB
    private LogBean logBean;
    @EJB
    private ApartmentCardStrategy apartmentCardStrategy;
    private long apartmentCardId;
    private Registration newRegistration;
    private Registration oldRegistration;
    private String addressEntity;
    private long addressId;
    private IModel<Person> personModel;

    public RegistrationEdit(long apartmentCardId, String addressEntity, long addressId, Registration registration) {
        this.apartmentCardId = apartmentCardId;
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

    private void init() {
        add(JavascriptPackageResource.getHeaderContribution(WebCommonResourceInitializer.SCROLL_JS));

        final Entity entity = registrationStrategy.getEntity();

        IModel<String> addressModel = new LoadableDetachableModel<String>() {

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

        personModel = new Model<Person>(newRegistration.getPerson());
        PersonPicker person = new PersonPicker("person", personModel, true,
                labelModel(personAttributeType.getAttributeNames(), getLocale()), isNew());
        personContainer.add(person);
        form.add(personContainer);

        //system attributes:
        initSystemAttributeInput(form, "registrationDate", REGISTRATION_DATE, false);
        initSystemAttributeInput(form, "registrationType", REGISTRATION_TYPE, false);
        initSystemAttributeInput(form, "arrivalCountry", ARRIVAL_COUNTRY, true);
        initSystemAttributeInput(form, "arrivalRegion", ARRIVAL_REGION, true);
        initSystemAttributeInput(form, "arrivalStreet", ARRIVAL_STREET, true);
        initSystemAttributeInput(form, "arrivalDistrict", ARRIVAL_DISTRICT, true);
        initSystemAttributeInput(form, "arrivalBuildingNumber", ARRIVAL_BUILDING_NUMBER, true);
        initSystemAttributeInput(form, "arrivalCity", ARRIVAL_CITY, true);
        initSystemAttributeInput(form, "arrivalBuildingCorp", ARRIVAL_BUILDING_CORP, true);
        initSystemAttributeInput(form, "arrivalApartment", ARRIVAL_APARTMENT, true);
        initSystemAttributeInput(form, "arrivalDate", ARRIVAL_DATE, false);
        initSystemAttributeInput(form, "departureCountry", DEPARTURE_COUNTRY, true);
        initSystemAttributeInput(form, "departureRegion", DEPARTURE_REGION, true);
        initSystemAttributeInput(form, "departureDistrict", DEPARTURE_DISTRICT, true);
        initSystemAttributeInput(form, "departureCity", DEPARTURE_CITY, true);
        initSystemAttributeInput(form, "departureStreet", DEPARTURE_STREET, true);
        initSystemAttributeInput(form, "departureBuildingNumber", DEPARTURE_BUILDING_NUMBER, true);
        initSystemAttributeInput(form, "departureBuildingCorp", DEPARTURE_BUILDING_CORP, true);
        initSystemAttributeInput(form, "departureApartment", DEPARTURE_APARTMENT, true);
        initSystemAttributeInput(form, "departureDate", DEPARTURE_DATE, false);
        initSystemAttributeInput(form, "departureReason", DEPARTURE_REASON, false);

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

        AjaxSubmitLink submit = new AjaxSubmitLink("submit") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if (validate()) {
                    save();
                } else {
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
        final EntityAttributeType ownerAttributeType = registrationStrategy.getEntity().getAttributeType(OWNER_RELATIONSHIP);

        WebMarkupContainer ownerRelationshipContainer = new WebMarkupContainer("ownerRelationshipContainer");
        add(ownerRelationshipContainer);

        //label
        IModel<String> labelModel = labelModel(ownerAttributeType.getAttributeNames(), getLocale());
        ownerRelationshipContainer.add(new Label("label", labelModel));

        //required
        ownerRelationshipContainer.add(new WebMarkupContainer("required").setVisible(ownerAttributeType.isMandatory()));

        //owner relationship
        final List<DomainObject> allOwnerRelationships = ownerRelationshipStrategy.getAllWithoutOwnerAndResponsible();
        final Attribute ownerRelationshipAttribute = newRegistration.getAttribute(OWNER_RELATIONSHIP);
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

        DisableAwareDropDownChoice<DomainObject> ownerRelationship = new DisableAwareDropDownChoice<DomainObject>("input",
                ownerRelationshipModel, allOwnerRelationships, new DomainObjectDisableAwareRenderer() {

            @Override
            public Object getDisplayValue(DomainObject object) {
                return ownerRelationshipStrategy.displayDomainObject(object, getLocale());
            }
        });
        ownerRelationship.setRequired(true);
        ownerRelationship.setLabel(labelModel);
        ownerRelationship.setEnabled(canEdit(null, registrationStrategy.getEntityTable(), newRegistration));
        ownerRelationshipContainer.add(ownerRelationship);
        return ownerRelationshipContainer;
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
        Attribute personAttribute = newRegistration.getAttribute(PERSON);
        Person person = personModel.getObject();
        Long personId = person != null ? person.getId() : null;
        if (personId != null) {
            personAttribute.setValueId(personId);
        } else {
            throw new IllegalStateException("Person has not been filled in.");
        }
    }

    private boolean validate() {
        return true;
    }

    private void save() {
        beforePersist();
        if (isNew()) {
            apartmentCardStrategy.addRegistration(apartmentCardId, newRegistration, DateUtil.getCurrentDate());
        } else {
            registrationStrategy.update(oldRegistration, newRegistration, DateUtil.getCurrentDate());
        }
        logBean.log(Log.STATUS.OK, Module.NAME, RegistrationEdit.class, isNew() ? Log.EVENT.CREATE : Log.EVENT.EDIT,
                registrationStrategy, oldRegistration, newRegistration, getLocale(), null);
        back();
    }

    private void back() {
        setResponsePage(new ApartmentCardEdit(apartmentCardId));
    }
}

