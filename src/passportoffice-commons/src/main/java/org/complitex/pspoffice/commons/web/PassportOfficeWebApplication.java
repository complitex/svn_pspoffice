package org.complitex.pspoffice.commons.web;

import org.apache.wicket.Page;
import org.complitex.pspoffice.commons.web.pages.welcome.WelcomePage;
import org.complitex.pspoffice.commons.web.template.TemplateWebApplication;

/**
 * User: Anatoly A. Ivanov java@inheaven.ru
 * Date: 20.12.2009 23:56:14
 */
public class PassportOfficeWebApplication extends TemplateWebApplication {

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return WelcomePage.class;
    }

//    @Override
//    protected WebRequest newWebRequest(HttpServletRequest servletRequest) {
//        return new UploadWebRequest(servletRequest);
//    }
}
