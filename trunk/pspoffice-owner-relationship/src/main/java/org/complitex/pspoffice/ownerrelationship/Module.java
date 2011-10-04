package org.complitex.pspoffice.ownerrelationship;

import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.service.LogManager;
import org.complitex.template.web.pages.DomainObjectEdit;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.complitex.template.strategy.TemplateStrategy;

@Singleton(name = "OwnerRelationshipModule")
@Startup
public class Module {

    public static final String NAME = "org.complitex.pspoffice.ownerrelationship";

    @PostConstruct
    public void init() {
        LogManager.get().registerLink(DomainObject.class.getName(), "owner_relationship", DomainObjectEdit.class,
                "entity=owner_relationship", TemplateStrategy.OBJECT_ID);
    }
}
