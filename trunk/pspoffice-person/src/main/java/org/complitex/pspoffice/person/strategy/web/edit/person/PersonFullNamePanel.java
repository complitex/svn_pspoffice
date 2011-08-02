/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.edit.person;

import org.apache.wicket.model.IModel;
import org.complitex.dictionary.web.component.name.FullNamePanel;

/**
 *
 * @author Artem
 */
public final class PersonFullNamePanel extends FullNamePanel {

    public PersonFullNamePanel(String id, IModel<Long> firstNameId, IModel<Long> middleNameId, IModel<Long> lastNameId) {
        super(id, firstNameId, middleNameId, lastNameId);
    }
}
