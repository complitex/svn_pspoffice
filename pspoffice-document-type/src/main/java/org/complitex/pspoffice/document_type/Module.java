package org.complitex.pspoffice.document_type;

import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.service.LogManager;
import org.complitex.template.web.pages.DomainObjectEdit;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.complitex.template.strategy.TemplateStrategy;

@Singleton(name = "DocumentTypeModule")
@Startup
public class Module {

    public static final String NAME = "org.complitex.pspoffice.document_type";

    @PostConstruct
    public void init() {
        LogManager.get().registerLink(DomainObject.class.getName(), "document_type", DomainObjectEdit.class,
                "entity=document_type", TemplateStrategy.OBJECT_ID);
    }
}
