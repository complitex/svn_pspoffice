/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.history.apartment_card;

import org.apache.wicket.markup.html.CSSPackageResource;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.description.Entity;
import org.complitex.dictionary.entity.description.EntityAttributeType;
import org.complitex.dictionary.util.StringUtil;
import org.complitex.dictionary.web.component.DisableAwareDropDownChoice;
import org.complitex.dictionary.web.component.DomainObjectDisableAwareRenderer;
import org.complitex.dictionary.web.component.ShowMode;
import org.complitex.dictionary.web.component.css.CssAttributeBehavior;
import org.complitex.dictionary.web.component.search.CollapsibleSearchComponent;
import org.complitex.pspoffice.ownerrelationship.strategy.OwnerRelationshipStrategy;
import org.complitex.pspoffice.ownership.strategy.OwnershipFormStrategy;
import org.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.RegistrationStrategy;
import org.complitex.pspoffice.person.strategy.entity.ApartmentCard;
import org.complitex.pspoffice.person.strategy.entity.ApartmentCardModification;
import org.complitex.pspoffice.person.strategy.entity.ModificationType;
import org.complitex.pspoffice.person.strategy.entity.Registration;
import org.complitex.pspoffice.person.strategy.entity.RegistrationModification;
import org.complitex.pspoffice.person.strategy.web.history.HistoryDateFormatter;
import org.complitex.pspoffice.person.util.PersonDateFormatter;
import org.complitex.pspoffice.registration_type.strategy.RegistrationTypeStrategy;

import static com.google.common.collect.ImmutableList.*;
import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Sets.*;

import static org.complitex.dictionary.web.component.DomainObjectInputPanel.*;
import static org.complitex.pspoffice.person.strategy.ApartmentCardStrategy.*;

/**
 *
 * @author Artem
 */
final class ApartmentCardHistoryPanel extends Panel {

    @EJB
    private ApartmentCardStrategy apartmentCardStrategy;
    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private OwnerRelationshipStrategy ownerRelationshipStrategy;
    @EJB
    private OwnershipFormStrategy ownershipFormStrategy;
    @EJB
    private RegistrationTypeStrategy registrationTypeStrategy;
    private final Entity ENTITY = apartmentCardStrategy.getEntity();

    ApartmentCardHistoryPanel(String id, long apartmentCardId, final Date endDate) {
        super(id);

        final Date startDate = apartmentCardStrategy.getPreviousModificationDate(apartmentCardId, endDate);
        final ApartmentCard card = apartmentCardStrategy.getHistoryApartmentCard(apartmentCardId, startDate);
        if (card == null) {
            throw new NullPointerException("History apartment card is null. Id: " + apartmentCardId
                    + ", startDate:" + startDate + ", endDate: " + endDate);
        }
        final ApartmentCardModification modification = apartmentCardStrategy.getDistinctions(card, startDate);

        add(CSSPackageResource.getHeaderContribution(ApartmentCardHistoryPanel.class,
                ApartmentCardHistoryPanel.class.getSimpleName() + ".css"));

        add(new Label("label", endDate != null ? new StringResourceModel("label", null,
                new Object[]{apartmentCardId, HistoryDateFormatter.format(startDate),
                    HistoryDateFormatter.format(endDate)})
                : new StringResourceModel("label_current", null, new Object[]{apartmentCardId,
                    HistoryDateFormatter.format(startDate)})));

        //address
        WebMarkupContainer addressContainer = new WebMarkupContainer("addressContainer");
        final EntityAttributeType addressAttributeType = ENTITY.getAttributeType(ADDRESS);
        addressContainer.add(new Label("label", labelModel(addressAttributeType.getAttributeNames(), getLocale())));
        addressContainer.add(new WebMarkupContainer("required").setVisible(addressAttributeType.isMandatory()));
        final Component address = new CollapsibleSearchComponent("address",
                apartmentCardStrategy.initAddressSearchComponentState(getAddressEntity(card), card.getAddressId()),
                of("city", "street", "building", "apartment", "room"), null, ShowMode.ALL, false);
        address.add(new CssAttributeBehavior(modification.getModificationType(ADDRESS).getCssClass()));
        addressContainer.add(address);
        add(addressContainer);

        //owner
        WebMarkupContainer ownerContainer = new WebMarkupContainer("ownerContainer");
        final EntityAttributeType ownerAttributeType = ENTITY.getAttributeType(OWNER);
        IModel<String> ownerLabelModel = labelModel(ownerAttributeType.getAttributeNames(), getLocale());
        ownerContainer.add(new Label("label", ownerLabelModel));
        ownerContainer.add(new WebMarkupContainer("required").setVisible(ownerAttributeType.isMandatory()));
        final Component owner = new Label("owner", personStrategy.displayDomainObject(card.getOwner(), getLocale()));
        owner.add(new CssAttributeBehavior(modification.getModificationType(OWNER).getCssClass()));
        ownerContainer.add(owner);
        add(ownerContainer);

        //form of ownership
        final EntityAttributeType formOfOwnershipAttributeType = apartmentCardStrategy.getEntity().getAttributeType(FORM_OF_OWNERSHIP);
        WebMarkupContainer formOfOwnershipContainer = new WebMarkupContainer("formOfOwnershipContainer");
        IModel<String> labelModel = labelModel(formOfOwnershipAttributeType.getAttributeNames(), getLocale());
        formOfOwnershipContainer.add(new Label("label", labelModel));
        formOfOwnershipContainer.add(new WebMarkupContainer("required").setVisible(formOfOwnershipAttributeType.isMandatory()));
        final List<DomainObject> allOwnershipForms = ownershipFormStrategy.getAll();
        final Component formOfOwnership = new DisableAwareDropDownChoice<DomainObject>("formOfOwnership",
                new Model<DomainObject>(card.getOwnershipForm()), allOwnershipForms, new DomainObjectDisableAwareRenderer() {

            @Override
            public Object getDisplayValue(DomainObject object) {
                return ownershipFormStrategy.displayDomainObject(object, getLocale());
            }
        });
        formOfOwnership.setEnabled(false);
        formOfOwnership.add(new CssAttributeBehavior(modification.getModificationType(FORM_OF_OWNERSHIP).getCssClass()));
        formOfOwnershipContainer.add(formOfOwnership);
        add(formOfOwnershipContainer);

        //registrations
        ListView<Registration> registrations = new ListView<Registration>("registrations", card.getRegistrations()) {

            @Override
            protected void populateItem(ListItem<Registration> item) {
                final Registration registration = item.getModelObject();
                final RegistrationModification registrationModification =
                        modification.getRegistrationModification(registration.getId());

                Set<String> rowCss = newHashSet();
                rowCss.add(registrationModification.getModificationType().getCssClass());

                item.add(new Label("personName", personStrategy.displayDomainObject(registration.getPerson(), getLocale())));

                Date birthDate = registration.getPerson().getBirthDate();
                item.add(new Label("personBirthDate", birthDate != null ? PersonDateFormatter.format(birthDate) : null));

                Component registrationType = new Label("registrationType",
                        StringUtil.valueOf(registrationTypeStrategy.displayDomainObject(
                        registration.getRegistrationType(), getLocale())));
                addRegistrationAttributeCss(registrationType, RegistrationStrategy.REGISTRATION_TYPE, registrationModification);
                item.add(registrationType);

                Date registrationStartDate = registration.getRegistrationDate();
                Component registrationStartDateLabel = new Label("registrationStartDate", registrationStartDate != null
                        ? PersonDateFormatter.format(registrationStartDate) : null);
                addRegistrationAttributeCss(registrationStartDateLabel, RegistrationStrategy.REGISTRATION_DATE, registrationModification);
                item.add(registrationStartDateLabel);

                Date registrationEndDate = registration.getDepartureDate();
                item.add(new Label("registrationEndDate", registrationEndDate != null
                        ? PersonDateFormatter.format(registrationEndDate) : null));

                DomainObject ownerRelationship = registration.getOwnerRelationship();
                Component registrationOwnerRelationship = new Label("registrationOwnerRelationship", ownerRelationship != null
                        ? ownerRelationshipStrategy.displayDomainObject(ownerRelationship, getLocale()) : null);
                addRegistrationAttributeCss(registrationOwnerRelationship, RegistrationStrategy.OWNER_RELATIONSHIP,
                        registrationModification);
                item.add(registrationOwnerRelationship);

                if (registration.isFinished()) {
                    rowCss.add("finished_registration");
                }
                item.add(new CssAttributeBehavior(rowCss));
            }

            private void addRegistrationAttributeCss(Component component, long attributeTypeId,
                    RegistrationModification registrationModification) {
                ModificationType attributeModificationType = registrationModification.getAttributeModificationType(attributeTypeId);
                if (attributeModificationType != null) {
                    component.add(new CssAttributeBehavior(attributeModificationType.getCssClass()));
                }
            }
        };
        add(registrations);

        //user attributes and housing rights:
        List<Long> userAttributeTypeIds = newArrayList(transform(filter(ENTITY.getEntityAttributeTypes(),
                new Predicate<EntityAttributeType>() {

                    @Override
                    public boolean apply(EntityAttributeType attributeType) {
                        return !attributeType.isSystem() || attributeType.getId().equals(HOUSING_RIGHTS);
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
            Attribute userAttribute = card.getAttribute(attributeTypeId);
            if (userAttribute != null) {
                userAttributes.add(userAttribute);
            }
        }

        ListView<Attribute> userAttributesView = new ListView<Attribute>("userAttributesView", userAttributes) {

            @Override
            protected void populateItem(ListItem<Attribute> item) {
                long userAttributeTypeId = item.getModelObject().getAttributeTypeId();
                initAttributeInput(card, modification, item, userAttributeTypeId);
            }
        };
        add(userAttributesView);
    }

    private void initAttributeInput(ApartmentCard card, ApartmentCardModification modification, MarkupContainer parent,
            long attributeTypeId) {
        final EntityAttributeType attributeType = ENTITY.getAttributeType(attributeTypeId);

        //label
        parent.add(new Label("label", labelModel(attributeType.getAttributeNames(), getLocale())));

        //required container
        WebMarkupContainer requiredContainer = new WebMarkupContainer("required");
        requiredContainer.setVisible(attributeType.isMandatory());
        parent.add(requiredContainer);

        //input component
        Attribute attribute = card.getAttribute(attributeTypeId);
        Component inputComponent = newInputComponent(apartmentCardStrategy.getEntityTable(), null, card, attribute,
                getLocale(), true);
        inputComponent.add(new CssAttributeBehavior(modification.getModificationType(attributeTypeId).getCssClass()));
        parent.add(inputComponent);
    }
}
