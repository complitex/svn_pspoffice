package org.complitex.pspoffice.departure_reason;

import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.service.LogManager;
import org.complitex.template.web.pages.DomainObjectEdit;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.complitex.template.strategy.TemplateStrategy;

@Singleton(name = "DepartureReasonModule")
@Startup
public class Module {

    public static final String NAME = "org.complitex.pspoffice.departure_reason";

    @PostConstruct
    public void init() {
        LogManager.get().registerLink(DomainObject.class.getName(), "departure_reason", DomainObjectEdit.class,
                "entity=departure_reason", TemplateStrategy.OBJECT_ID);
    }
}
