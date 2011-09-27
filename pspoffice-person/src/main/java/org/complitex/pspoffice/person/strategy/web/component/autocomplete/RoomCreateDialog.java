/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.component.autocomplete;

import java.text.MessageFormat;
import java.util.List;
import javax.ejb.EJB;
import org.complitex.address.strategy.room.RoomStrategy;
import org.complitex.address.strategy.room.web.edit.RoomEdit;
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
abstract class RoomCreateDialog extends AbstractAddressCreateDialog {

    @EJB
    private StringCultureBean stringBean;
    @EJB
    private LocaleBean localeBean;
    @EJB
    private LogBean logBean;
    @EJB
    private RoomStrategy roomStrategy;

    RoomCreateDialog(String id, Autocomplete<String> autocomplete, List<Long> userOrganizationIds) {
        super(id, autocomplete, userOrganizationIds);
    }

    @Override
    String getTitle() {
        return getString("room_title");
    }

    @Override
    String getNumberLabel() {
        return getString("number");
    }

    @Override
    DomainObject initObject(String number) {
        DomainObject room = roomStrategy.newInstance();
        stringBean.getSystemStringCulture(room.getAttribute(RoomStrategy.NAME).getLocalizedValues()).setValue(number);
        room.setParentEntityId("apartment".equals(getParentEntity()) ? 100L : 500L);
        room.setParentId(getParentObject().getId());
        return room;
    }

    @Override
    boolean validate(DomainObject object) {
        Long existingObjectId = roomStrategy.performDefaultValidation(object, localeBean.getSystemLocale());
        if (existingObjectId != null) {
            error(MessageFormat.format(getString("validation_error"), existingObjectId));
        }
        return existingObjectId == null;
    }

    @Override
    DomainObject save(DomainObject object) {
        roomStrategy.insert(object, DateUtil.getCurrentDate());
        logBean.log(Log.STATUS.OK, Module.NAME, RoomCreateDialog.class,
                Log.EVENT.CREATE, roomStrategy, null, object, getLocale(), null);

        return roomStrategy.findById(object.getId(), true);
    }

    @Override
    void bulkSave(DomainObject object) {
        roomStrategy.insert(object, DateUtil.getCurrentDate());
    }

    @Override
    void beforeBulkSave(String numbers) {
        RoomEdit.beforeBulkSave(Module.NAME, RoomCreateDialog.class, numbers, getLocale());
    }

    @Override
    void afterBulkSave(String numbers, boolean operationSuccessed) {
        RoomEdit.afterBulkSave(Module.NAME, RoomCreateDialog.class, numbers, operationSuccessed, getLocale());
    }

    @Override
    void onFailBulkSave(DomainObject failObject, String numbers, String failNumber) {
        RoomEdit.onFailBulkSave(Module.NAME, RoomCreateDialog.class, failObject, numbers, failNumber, getLocale());
    }

    @Override
    void onInvalidateBulkSave(DomainObject invalidObject, String numbers, String invalidNumber) {
        RoomEdit.onInvalidateBulkSave(Module.NAME, RoomCreateDialog.class, invalidObject, numbers,
                invalidNumber, getLocale());
    }
}
