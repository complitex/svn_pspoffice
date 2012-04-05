package org.complitex.pspoffice.importing;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.complitex.dictionary.service.ConfigBean;
import org.complitex.pspoffice.importing.legacy.entity.PspImportConfig;

@Singleton(name = "PspImportModule")
@Startup
public class Module {

    public static final String NAME = "org.complitex.pspoffice.importing";
    @EJB
    private ConfigBean configBean;

    @PostConstruct
    public void init() {
        configBean.init(PspImportConfig.class.getName(), PspImportConfig.values());
    }
}
