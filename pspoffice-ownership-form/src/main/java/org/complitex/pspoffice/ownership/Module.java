package org.complitex.pspoffice.ownership;

import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.service.LogManager;
import org.complitex.template.web.pages.DomainObjectEdit;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.complitex.template.strategy.TemplateStrategy;

@Singleton(name = "OwnershipModule")
@Startup
public class Module {

    public static final String NAME = "org.complitex.pspoffice.ownership";

    @PostConstruct
    public void init() {
        LogManager.get().registerLink(DomainObject.class.getName(), "ownership_form", DomainObjectEdit.class,
                "entity=ownership_form", TemplateStrategy.OBJECT_ID);
    }
}
