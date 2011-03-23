/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.edit;

import com.google.common.collect.ImmutableList;
import javax.ejb.EJB;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
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

    @Override
    protected void init() {
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

    @Override
    public void onInsert() {
        prepareAttributeData();
    }

    @Override
    public void onUpdate() {
        prepareAttributeData();
    }

    private void prepareAttributeData() {
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
                throw new RuntimeException("All building, apartment and room parts of address have not been filled in.");
            }
        }
    }

    private SearchComponentState initArrivalSearchComponentState() {
        SearchComponentState searchComponentState = new SearchComponentState();
        if (arrivalAttribute.getValueTypeId().equals(RegistrationStrategy.ARRIVAL_STRING)) {
            return searchComponentState;
        }
        long arrivalAddressObjectId = arrivalAttribute.getValueId();
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
        return searchComponentState;
    }
}
