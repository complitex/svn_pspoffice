/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.history;

import java.util.Date;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.strategy.web.HistoryPanel;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.web.edit.person.PersonInputPanel;
import org.complitex.template.web.pages.HistoryPage;
import org.complitex.template.web.security.SecurityRole;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.PERSON_MODULE_EDIT)
public final class PersonHistoryPage extends HistoryPage {

    public PersonHistoryPage(PageParameters params) {
        super(params);
    }

    @Override
    protected HistoryPanel newHistoryPanel(String id, String strategyName, String entity, long objectId) {
        return new HistoryPanel(id, strategyName, entity, objectId) {

            @Override
            protected Component newInputPanel(String id, DomainObject historyObject, Date historyDate) {
                Person person = (Person) historyObject;
                return new PersonInputPanel(id, person, historyDate);
            }
        };
    }
}

