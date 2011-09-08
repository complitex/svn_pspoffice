/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.component.autocomplete;

import java.text.MessageFormat;
import java.util.List;
import javax.ejb.EJB;
import org.complitex.address.strategy.apartment.ApartmentStrategy;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.Log;
import org.complitex.dictionary.service.LocaleBean;
import org.complitex.dictionary.service.LogBean;
import org.complitex.dictionary.service.StringCultureBean;
import org.complitex.dictionary.util.DateUtil;
import org.complitex.pspoffice.person.Module;
import org.odlabs.wiquery.ui.autocomplete.Autocomplete;

/**
 *
 * @author Artem
 */
abstract class ApartmentCreateDialog extends AbstractAddressCreateDialog {

    @EJB
    private StringCultureBean stringBean;
    @EJB
    private LocaleBean localeBean;
    @EJB
    private LogBean logBean;
    @EJB
    private ApartmentStrategy apartmentStrategy;

    ApartmentCreateDialog(String id, Autocomplete<String> autocomplete, List<Long> userOrganizationIds) {
        super(id, autocomplete, userOrganizationIds);
    }

    @Override
    String getTitle() {
        return getString("apartment_title");
    }

    @Override
    String getNameLabel() {
        return getString("number");
    }

    @Override
    DomainObject initObject() {
        DomainObject apartment = apartmentStrategy.newInstance();
        stringBean.getSystemStringCulture(apartment.getAttribute(ApartmentStrategy.NAME).getLocalizedValues()).
                setValue(getNameModel().getObject());
        apartment.setParentEntityId(ApartmentStrategy.PARENT_ENTITY_ID);
        apartment.setParentId(getParentId());
        return apartment;
    }

    @Override
    boolean validate(DomainObject object) {
        Long existingObjectId = apartmentStrategy.performDefaultValidation(object, localeBean.getSystemLocale());
        if (existingObjectId != null) {
            error(MessageFormat.format(getString("validation_error"), existingObjectId));
        }
        return existingObjectId == null;
    }

    @Override
    DomainObject save(DomainObject object) {
        apartmentStrategy.insert(object, DateUtil.getCurrentDate());
        logBean.log(Log.STATUS.OK, Module.NAME, ApartmentCreateDialog.class,
                Log.EVENT.CREATE, apartmentStrategy, null, object, getLocale(), null);

        return apartmentStrategy.findById(object.getId(), true);
    }
}
