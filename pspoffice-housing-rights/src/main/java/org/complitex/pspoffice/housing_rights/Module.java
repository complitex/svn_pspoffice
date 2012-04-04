package org.complitex.pspoffice.housing_rights;

import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.service.LogManager;
import org.complitex.template.web.pages.DomainObjectEdit;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.complitex.template.strategy.TemplateStrategy;

@Singleton(name = "HousingRightsModule")
@Startup
public class Module {

    public static final String NAME = "org.complitex.pspoffice.housing_rights";

    @PostConstruct
    public void init() {
        LogManager.get().registerLink(DomainObject.class.getName(), "housing_rights", DomainObjectEdit.class,
                "entity=housing_rights", TemplateStrategy.OBJECT_ID);
    }
}
