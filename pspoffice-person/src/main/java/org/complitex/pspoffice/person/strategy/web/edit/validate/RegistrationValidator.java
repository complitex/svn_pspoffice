/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.edit.validate;

import java.text.MessageFormat;
import org.apache.wicket.Component;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.strategy.web.DomainObjectEditPanel;
import org.complitex.dictionary.strategy.web.validate.IValidator;
import org.complitex.dictionary.web.component.search.SearchComponent;
import org.complitex.dictionary.web.component.search.SearchComponentState;
import org.complitex.pspoffice.person.strategy.web.edit.RegistrationEditComponent;

/**
 *
 * @author Artem
 */
public class RegistrationValidator implements IValidator {

    private RegistrationEditComponent editComponent;

    @Override
    public boolean validate(DomainObject registration, DomainObjectEditPanel editPanel) {
        findEditComponent(editPanel);

        boolean isValid = true;
        //arrival address:
        if (editComponent.isSimpleArrivalAddressText()) {
            if (editComponent.isArrivalAddressTextEmpty()) {
                error("arrival_address_empty", editPanel);
                isValid = false;
            }
        } else {
            isValid &= validateArrivalBuildingPresence(editPanel);
        }

        //current address:
        isValid &= validateCurrentBuildingPresence(editPanel);

        return isValid;
    }

    private boolean validateArrivalBuildingPresence(DomainObjectEditPanel editPanel) {
        SearchComponentState arrivalAddressComponentState = editComponent.getArrivalSearchComponentState();
        DomainObject building = arrivalAddressComponentState.get("building");
        if (building == null || building.getId().equals(SearchComponent.NOT_SPECIFIED_ID)) {
            error("arrival_address_failing", editPanel);
            return false;
        }
        return true;
    }

    private boolean validateCurrentBuildingPresence(DomainObjectEditPanel editPanel) {
        SearchComponentState addressComponentState = editComponent.getAddressSearchComponentState();
        DomainObject building = addressComponentState.get("building");
        if (building == null || building.getId().equals(SearchComponent.NOT_SPECIFIED_ID)) {
            error("address_failing", editPanel);
            return false;
        }
        return true;
    }

    private boolean validateDepartureBuildingPresence(DomainObjectEditPanel editPanel) {
        SearchComponentState departureAddressComponentState = editComponent.getDepartureSearchComponentState();
        DomainObject building = departureAddressComponentState.get("building");
        if (building == null || building.getId().equals(SearchComponent.NOT_SPECIFIED_ID)) {
            error("departure_address_failing", editPanel);
            return false;
        }
        return true;
    }

    private void error(String key, Component component, Object... formatArguments) {
        if (formatArguments == null) {
            component.error(editComponent.getString(key));
        } else {
            component.error(MessageFormat.format(editComponent.getString(key), formatArguments));
        }
    }

    private void findEditComponent(Component component) {
        if (editComponent == null) {
            component.getPage().visitChildren(RegistrationEditComponent.class, new Component.IVisitor<RegistrationEditComponent>() {

                @Override
                public Object component(RegistrationEditComponent comp) {
                    editComponent = comp;
                    return STOP_TRAVERSAL;
                }
            });
        }
    }
}
