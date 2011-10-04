package org.complitex.pspoffice.registration_type;

import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.service.LogManager;
import org.complitex.template.web.pages.DomainObjectEdit;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.complitex.template.strategy.TemplateStrategy;

@Singleton(name = "RegistrationTypeModule")
@Startup
public class Module {

    public static final String NAME = "org.complitex.pspoffice.registration_type";

    @PostConstruct
    public void init() {
        LogManager.get().registerLink(DomainObject.class.getName(), "registration_type", DomainObjectEdit.class,
                "entity=registration_type", TemplateStrategy.OBJECT_ID);
    }
}
