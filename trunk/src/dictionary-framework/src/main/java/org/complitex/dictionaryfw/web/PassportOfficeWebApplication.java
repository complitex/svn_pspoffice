package org.complitex.dictionaryfw.web;

import org.apache.wicket.Page;

import org.apache.wicket.protocol.http.WebApplication;
import org.complitex.dictionaryfw.web.pages.welcome.WelcomePage;
import org.wicketstuff.javaee.injection.JavaEEComponentInjector;

/**
 * User: Anatoly A. Ivanov java@inheaven.ru
 * Date: 20.12.2009 23:56:14
 */
public class PassportOfficeWebApplication extends WebApplication {

    @Override
    protected void init() {
//        getApplicationSettings().setPageExpiredErrorPage(SessionExpiredPage.class);
        super.init();

        initializeJEEInjector();
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return WelcomePage.class;
    }

    private void initializeJEEInjector() {
        addComponentInstantiationListener(new JavaEEComponentInjector(this));
    }
//    @Override
//    protected WebRequest newWebRequest(HttpServletRequest servletRequest) {
//        return new UploadWebRequest(servletRequest);
//    }
}
