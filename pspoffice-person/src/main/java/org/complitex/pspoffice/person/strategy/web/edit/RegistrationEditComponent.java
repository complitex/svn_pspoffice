/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.edit;

import com.google.common.collect.ImmutableList;
import javax.ejb.EJB;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.description.EntityAttributeType;
import org.complitex.dictionary.service.StringCultureBean;
import org.complitex.dictionary.strategy.IStrategy;
import org.complitex.dictionary.strategy.Strategy;
import org.complitex.dictionary.strategy.StrategyFactory;
import org.complitex.dictionary.strategy.web.AbstractComplexAttributesPanel;
import org.complitex.dictionary.strategy.web.DomainObjectAccessUtil;
import org.complitex.dictionary.web.component.ShowMode;
import org.complitex.dictionary.web.component.search.SearchComponent;
import org.complitex.dictionary.web.component.search.SearchComponentState;
import org.complitex.pspoffice.person.strategy.RegistrationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
public final class RegistrationEditComponent extends AbstractComplexAttributesPanel {

    private static final Logger log = LoggerFactory.getLogger(RegistrationEditComponent.class);
    @EJB
    private StringCultureBean stringBean;
    @EJB
    private StrategyFactory strategyFactory;
    @EJB
    private RegistrationStrategy registrationStrategy;

    public RegistrationEditComponent(String id, boolean disabled) {
        super(id, disabled);
    }
    private Attribute arrivalAttribute;
    private SearchComponentState arrivalSearchComponentState;
    private IModel<Boolean> arrivalAddressPickerModel;
    private IModel<String> arrivalTextModel;
    private Attribute addressAttribute;
    private SearchComponentState addressSearchComponentState;
    private Attribute departureAttribute;
    private SearchComponentState departureSearchComponentState;
    private IModel<Boolean> departureAddressPickerModel;
    private IModel<String> departureTextModel;

    @Override
    protected void init() {
        // arrival address
        Label arrivalLabel = new Label("arrivalLabel", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                EntityAttributeType attributeType = registrationStrategy.getEntity().getAttributeType(RegistrationStrategy.ARRIVAL);
                return stringBean.displayValue(attributeType.getAttributeNames(), getLocale());
            }
        });
        add(arrivalLabel);

        final DomainObject registration = getDomainObject();
        arrivalAttribute = registration.getAttribute(RegistrationStrategy.ARRIVAL);
        boolean isSimpleArrivalAddress = false;
        if (arrivalAttribute.getValueTypeId().equals(RegistrationStrategy.ARRIVAL_STRING)) {
            isSimpleArrivalAddress = true;
        }
        arrivalAddressPickerModel = new Model<Boolean>(isSimpleArrivalAddress);

        arrivalTextModel = new Model<String>();
        if (isSimpleArrivalAddress) {
            arrivalTextModel.setObject(stringBean.getSystemStringCulture(arrivalAttribute.getLocalizedValues()).getValue());
        }
        final TextField<String> arrivalTextField = new TextField<String>("arrivalTextField", arrivalTextModel);
        arrivalTextField.setOutputMarkupPlaceholderTag(true);
        add(arrivalTextField);
        arrivalTextField.setVisible(arrivalAddressPickerModel.getObject());

        arrivalSearchComponentState = initArrivalSearchComponentState();
        final SearchComponent arrivalSearchComponent = new SearchComponent("arrivalSearchComponent", arrivalSearchComponentState,
                ImmutableList.of("city", "street", "building", "apartment", "room"), null, ShowMode.ACTIVE,
                !isDisabled() && DomainObjectAccessUtil.canEdit(null, "registration", registration));
        arrivalSearchComponent.setOutputMarkupPlaceholderTag(true);
        arrivalSearchComponent.setVisible(!arrivalAddressPickerModel.getObject());
        add(arrivalSearchComponent);

        RadioGroup<Boolean> arrivalPicker = new RadioGroup<Boolean>("arrivalPicker", arrivalAddressPickerModel);
        Radio<Boolean> showArrivalTextField = new Radio<Boolean>("showArrivalTextField", Model.of(true));
        Radio<Boolean> showArrivalSearchComponent = new Radio<Boolean>("showArrivalSearchComponent", Model.of(false));
        arrivalPicker.add(showArrivalTextField);
        arrivalPicker.add(showArrivalSearchComponent);
        arrivalPicker.add(new AjaxFormChoiceComponentUpdatingBehavior() {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                arrivalSearchComponent.setVisible(!arrivalAddressPickerModel.getObject());
                arrivalTextField.setVisible(arrivalAddressPickerModel.getObject());
                target.addComponent(arrivalTextField);
                target.addComponent(arrivalSearchComponent);
            }
        });
        add(arrivalPicker);

        // current address
        Label addressLabel = new Label("addressLabel", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                EntityAttributeType attributeType = registrationStrategy.getEntity().getAttributeType(RegistrationStrategy.ADDRESS);
                return stringBean.displayValue(attributeType.getAttributeNames(), getLocale());
            }
        });
        add(addressLabel);
        addressAttribute = registration.getAttribute(RegistrationStrategy.ADDRESS);
        addressSearchComponentState = initAddressSearchComponentState();
        SearchComponent addressSearchPanel = new SearchComponent("addressSearchPanel", addressSearchComponentState,
                ImmutableList.of("city", "street", "building", "apartment", "room"), null, ShowMode.ACTIVE,
                !isDisabled() && DomainObjectAccessUtil.canEdit(null, "registration", registration));
        add(addressSearchPanel);

        // departure address
        departureAttribute = registration.getAttribute(RegistrationStrategy.DEPARTURE);
        WebMarkupContainer departureContainer = new WebMarkupContainer("departureContainer");
        departureContainer.setVisible(departureAttribute != null);
        add(departureContainer);

        Label departureLabel = new Label("departureLabel", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                EntityAttributeType attributeType = registrationStrategy.getEntity().getAttributeType(RegistrationStrategy.DEPARTURE);
                return stringBean.displayValue(attributeType.getAttributeNames(), getLocale());
            }
        });
        departureContainer.add(departureLabel);
        
        boolean isSimpleDepartureAddress = false;
        if ((departureAttribute != null) && departureAttribute.getValueTypeId().equals(RegistrationStrategy.DEPARTURE_STRING)) {
            isSimpleDepartureAddress = true;
        }
        departureAddressPickerModel = new Model<Boolean>(isSimpleDepartureAddress);

        departureTextModel = new Model<String>();
        if (isSimpleDepartureAddress) {
            departureTextModel.setObject(stringBean.getSystemStringCulture(departureAttribute.getLocalizedValues()).getValue());
        }
        final TextField<String> departureTextField = new TextField<String>("departureTextField", departureTextModel);
        departureTextField.setOutputMarkupPlaceholderTag(true);
        departureContainer.add(departureTextField);
        departureTextField.setVisible(departureAddressPickerModel.getObject());

        if(departureAttribute != null){
            departureSearchComponentState = initDepartureSearchComponentState();
        } else {
            departureSearchComponentState = new SearchComponentState();
        }
        final SearchComponent departureSearchComponent = new SearchComponent("departureSearchComponent", departureSearchComponentState,
                ImmutableList.of("city", "street", "building", "apartment", "room"), null, ShowMode.ACTIVE,
                !isDisabled() && DomainObjectAccessUtil.canEdit(null, "registration", registration));
        departureSearchComponent.setOutputMarkupPlaceholderTag(true);
        departureSearchComponent.setVisible(!departureAddressPickerModel.getObject());
        departureContainer.add(departureSearchComponent);

        RadioGroup<Boolean> departurePicker = new RadioGroup<Boolean>("departurePicker", departureAddressPickerModel);
        Radio<Boolean> showDepartureTextField = new Radio<Boolean>("showDepartureTextField", Model.of(true));
        Radio<Boolean> showDepartureSearchComponent = new Radio<Boolean>("showDepartureSearchComponent", Model.of(false));
        departurePicker.add(showDepartureTextField);
        departurePicker.add(showDepartureSearchComponent);
        departurePicker.add(new AjaxFormChoiceComponentUpdatingBehavior() {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                departureSearchComponent.setVisible(!departureAddressPickerModel.getObject());
                departureTextField.setVisible(departureAddressPickerModel.getObject());
                target.addComponent(departureTextField);
                target.addComponent(departureSearchComponent);
            }
        });
        departureContainer.add(departurePicker);
    }

    public SearchComponentState getArrivalSearchComponentState() {
        return arrivalSearchComponentState;
    }

    public boolean isSimpleArrivalAddressText() {
        return arrivalAddressPickerModel.getObject();
    }

    public boolean isArrivalAddressTextEmpty() {
        return Strings.isEmpty(arrivalTextModel.getObject());
    }

    public SearchComponentState getAddressSearchComponentState() {
        return addressSearchComponentState;
    }

    public boolean isSimpleDepartureAddressText() {
        return departureAddressPickerModel.getObject();
    }

    public boolean isDepartureAddressTextEmpty() {
        return Strings.isEmpty(departureTextModel.getObject());
    }

    public SearchComponentState getDepartureSearchComponentState() {
        return departureSearchComponentState;
    }

    @Override
    public void onInsert() {
        prepareAttributeData();
    }

    @Override
    public void onUpdate() {
        prepareAttributeData();
    }

    private void prepareAttributeData() {
        // arrival address attributes
        if (arrivalAddressPickerModel.getObject()) {
            arrivalAttribute.setLocalizedValues(stringBean.newStringCultures());
            stringBean.getSystemStringCulture(arrivalAttribute.getLocalizedValues()).setValue(arrivalTextModel.getObject());
            arrivalAttribute.setValueTypeId(RegistrationStrategy.ARRIVAL_STRING);
        } else {
            arrivalAttribute.setLocalizedValues(null);
            if (!arrivalSearchComponentState.get("room").getId().equals(SearchComponent.NOT_SPECIFIED_ID)) {
                arrivalAttribute.setValueTypeId(RegistrationStrategy.ARRIVAL_ROOM);
                arrivalAttribute.setValueId(arrivalSearchComponentState.get("room").getId());
            } else if (!arrivalSearchComponentState.get("apartment").getId().equals(SearchComponent.NOT_SPECIFIED_ID)) {
                arrivalAttribute.setValueTypeId(RegistrationStrategy.ARRIVAL_APARTMENT);
                arrivalAttribute.setValueId(arrivalSearchComponentState.get("apartment").getId());
            } else if (!arrivalSearchComponentState.get("building").getId().equals(SearchComponent.NOT_SPECIFIED_ID)) {
                arrivalAttribute.setValueTypeId(RegistrationStrategy.ARRIVAL_BUILDING);
                arrivalAttribute.setValueId(arrivalSearchComponentState.get("building").getId());
            } else {
                throw new RuntimeException("All building, apartment and room parts of arrival address have not been filled in.");
            }
        }

        // current address attributes
        if (!addressSearchComponentState.get("room").getId().equals(SearchComponent.NOT_SPECIFIED_ID)) {
            addressAttribute.setValueTypeId(RegistrationStrategy.ADDRESS_ROOM);
            addressAttribute.setValueId(addressSearchComponentState.get("room").getId());
        } else if (!addressSearchComponentState.get("apartment").getId().equals(SearchComponent.NOT_SPECIFIED_ID)) {
            addressAttribute.setValueTypeId(RegistrationStrategy.ADDRESS_APARTMENT);
            addressAttribute.setValueId(addressSearchComponentState.get("apartment").getId());
        } else if (!addressSearchComponentState.get("building").getId().equals(SearchComponent.NOT_SPECIFIED_ID)) {
            addressAttribute.setValueTypeId(RegistrationStrategy.ADDRESS_BUILDING);
            addressAttribute.setValueId(addressSearchComponentState.get("building").getId());
        } else {
            throw new RuntimeException("All building, apartment and room parts of current address have not been filled in.");
        }

        // departure address
        if (departureAddressPickerModel.getObject()) {
            departureAttribute.setLocalizedValues(stringBean.newStringCultures());
            stringBean.getSystemStringCulture(departureAttribute.getLocalizedValues()).setValue(departureTextModel.getObject());
            departureAttribute.setValueTypeId(RegistrationStrategy.DEPARTURE_STRING);
        } else {
            boolean departureAddressEntered = true;
            DomainObject room = departureSearchComponentState.get("room");
            DomainObject apartment = departureSearchComponentState.get("apartment");
            DomainObject building = departureSearchComponentState.get("building");
            departureAddressEntered = (building != null) && (apartment != null) && (room != null);

            if (departureAddressEntered) {
                departureAttribute.setLocalizedValues(null);
                if (!room.getId().equals(SearchComponent.NOT_SPECIFIED_ID)) {
                    departureAttribute.setValueTypeId(RegistrationStrategy.DEPARTURE_ROOM);
                    departureAttribute.setValueId(departureSearchComponentState.get("room").getId());
                } else if (!apartment.getId().equals(SearchComponent.NOT_SPECIFIED_ID)) {
                    departureAttribute.setValueTypeId(RegistrationStrategy.DEPARTURE_APARTMENT);
                    departureAttribute.setValueId(departureSearchComponentState.get("apartment").getId());
                } else if (!building.getId().equals(SearchComponent.NOT_SPECIFIED_ID)) {
                    departureAttribute.setValueTypeId(RegistrationStrategy.DEPARTURE_BUILDING);
                    departureAttribute.setValueId(departureSearchComponentState.get("building").getId());
                } else {
                    throw new RuntimeException("All building, apartment and room parts of departure address have not been filled in.");
                }
            }
        }
    }

    private SearchComponentState initArrivalSearchComponentState() {
        SearchComponentState searchComponentState = new SearchComponentState();
        if (arrivalAttribute.getValueTypeId().equals(RegistrationStrategy.ARRIVAL_STRING)) {
            return searchComponentState;
        }
        Long arrivalAddressObjectId = arrivalAttribute.getValueId();
        IStrategy arrivalAddressStrategy = null;
        DomainObject arrivalAddressObject = null;
        String arrivalAddressEntity = null;

        if (arrivalAttribute.getValueTypeId().equals(RegistrationStrategy.ARRIVAL_ROOM)) {
            arrivalAddressEntity = "room";
        } else if (arrivalAttribute.getValueTypeId().equals(RegistrationStrategy.ARRIVAL_APARTMENT)) {
            arrivalAddressEntity = "apartment";
        } else if (arrivalAttribute.getValueTypeId().equals(RegistrationStrategy.ARRIVAL_BUILDING)) {
            arrivalAddressEntity = "building";
        } else {
            throw new RuntimeException("Registration arrival attribute has unknown value type id: " + arrivalAttribute.getValueTypeId());
        }
        arrivalAddressStrategy = strategyFactory.getStrategy(arrivalAddressEntity);
        if (arrivalAddressObjectId != null) {
            arrivalAddressObject = arrivalAddressStrategy.findById(arrivalAddressObjectId, true);
            Strategy.RestrictedObjectInfo info = arrivalAddressStrategy.findParentInSearchComponent(arrivalAddressObjectId, null);
            if (info != null) {
                searchComponentState = arrivalAddressStrategy.getSearchComponentStateForParent(info.getId(), info.getEntityTable(), null);
                searchComponentState.put(arrivalAddressEntity, arrivalAddressObject);
            }
            if (arrivalAddressEntity.equals("apartment")) {
                DomainObject room = new DomainObject();
                room.setId(SearchComponent.NOT_SPECIFIED_ID);
                searchComponentState.put("room", room);
            } else if (arrivalAddressEntity.equals("building")) {
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

    private SearchComponentState initDepartureSearchComponentState() {
        SearchComponentState searchComponentState = new SearchComponentState();
        if (departureAttribute.getValueTypeId().equals(RegistrationStrategy.DEPARTURE_STRING)) {
            return searchComponentState;
        }
        Long departureAddressObjectId = departureAttribute.getValueId();
        IStrategy departureAddressStrategy = null;
        DomainObject departureAddressObject = null;
        String departureAddressEntity = null;

        if (departureAttribute.getValueTypeId().equals(RegistrationStrategy.DEPARTURE_ROOM)) {
            departureAddressEntity = "room";
        } else if (departureAttribute.getValueTypeId().equals(RegistrationStrategy.DEPARTURE_APARTMENT)) {
            departureAddressEntity = "apartment";
        } else if (departureAttribute.getValueTypeId().equals(RegistrationStrategy.DEPARTURE_BUILDING)) {
            departureAddressEntity = "building";
        } else {
            throw new RuntimeException("Registration departure attribute has unknown value type id: " + departureAttribute.getValueTypeId());
        }
        departureAddressStrategy = strategyFactory.getStrategy(departureAddressEntity);
        if (departureAddressObjectId != null) {
            departureAddressObject = departureAddressStrategy.findById(departureAddressObjectId, true);
            Strategy.RestrictedObjectInfo info = departureAddressStrategy.findParentInSearchComponent(departureAddressObjectId, null);
            if (info != null) {
                searchComponentState = departureAddressStrategy.getSearchComponentStateForParent(info.getId(), info.getEntityTable(), null);
                searchComponentState.put(departureAddressEntity, departureAddressObject);
            }
            if (departureAddressEntity.equals("apartment")) {
                DomainObject room = new DomainObject();
                room.setId(SearchComponent.NOT_SPECIFIED_ID);
                searchComponentState.put("room", room);
            } else if (departureAddressEntity.equals("building")) {
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

    private SearchComponentState initAddressSearchComponentState() {
        SearchComponentState searchComponentState = new SearchComponentState();
        Long addressObjectId = addressAttribute.getValueId();
        IStrategy addressStrategy = null;
        DomainObject addressObject = null;
        String addressEntity = null;

        if (addressAttribute.getValueTypeId().equals(RegistrationStrategy.ADDRESS_ROOM)) {
            addressEntity = "room";
        } else if (addressAttribute.getValueTypeId().equals(RegistrationStrategy.ADDRESS_APARTMENT)) {
            addressEntity = "apartment";
        } else if (addressAttribute.getValueTypeId().equals(RegistrationStrategy.ADDRESS_BUILDING)) {
            addressEntity = "building";
        } else {
            throw new RuntimeException("Registration address attribute has unknown value type id: " + addressAttribute.getValueTypeId());
        }
        addressStrategy = strategyFactory.getStrategy(addressEntity);
        if (addressObjectId != null) {
            addressObject = addressStrategy.findById(addressObjectId, true);
            Strategy.RestrictedObjectInfo info = addressStrategy.findParentInSearchComponent(addressObjectId, null);
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
}
