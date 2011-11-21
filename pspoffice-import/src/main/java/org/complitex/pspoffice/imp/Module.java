package org.complitex.pspoffice.imp;

import org.complitex.pspoffice.imp.entity.PspImportConfig;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.complitex.dictionary.service.ConfigBean;

@Singleton(name = "PspImportModule")
@Startup
public class Module {

    public static final String NAME = "org.complitex.pspoffice.imp";
    @EJB
    private ConfigBean configBean;

    @PostConstruct
    public void init() {
        configBean.init(PspImportConfig.class.getName(), PspImportConfig.values());
    }
}
