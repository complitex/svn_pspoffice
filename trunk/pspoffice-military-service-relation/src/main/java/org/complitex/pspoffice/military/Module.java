package org.complitex.pspoffice.military;

import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.service.LogManager;
import org.complitex.template.web.pages.DomainObjectEdit;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.complitex.template.strategy.TemplateStrategy;

@Singleton(name = "MilitaryServiceRelationModule")
@Startup
public class Module {

    public static final String NAME = "org.complitex.pspoffice.military_service_relation";

    @PostConstruct
    public void init() {
        LogManager.get().registerLink(DomainObject.class.getName(), "military_service_relation", DomainObjectEdit.class,
                "entity=military_service_relation", TemplateStrategy.OBJECT_ID);
    }
}
