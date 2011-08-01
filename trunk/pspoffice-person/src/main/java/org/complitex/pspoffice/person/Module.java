package org.complitex.pspoffice.person;

import org.complitex.dictionary.service.LogManager;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.web.edit.person.PersonEdit;
import org.complitex.template.strategy.TemplateStrategy;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 17.08.2010 18:41:01
 */
@Singleton(name = "PersonModule")
@Startup
public class Module {

    public static final String NAME = "org.complitex.pspoffice.person";

    @PostConstruct
    public void init() {
       LogManager.get().registerLink(Person.class.getName(), PersonEdit.class, null, TemplateStrategy.OBJECT_ID);
    }
}
