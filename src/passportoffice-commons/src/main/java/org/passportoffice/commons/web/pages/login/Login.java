///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package org.passportoffice.commons.web.pages.login;
//
//import javax.ejb.EJB;
//import org.apache.wicket.PageParameters;
//import org.apache.wicket.markup.html.CSSPackageResource;
//import org.apache.wicket.markup.html.JavascriptPackageResource;
//import org.apache.wicket.markup.html.WebMarkupContainer;
//import org.apache.wicket.markup.html.WebPage;
//import org.apache.wicket.markup.html.basic.Label;
//import org.apache.wicket.model.ResourceModel;
//import org.odlabs.wiquery.core.commons.CoreJavaScriptResourceReference;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.vetcontrol.service.ClientBean;
//import org.vetcontrol.web.resource.WebCommonResourceInitializer;
//
///**
// *
// * @author Artem
// */
//public final class Login extends WebPage {
//
//    private static final Logger log = LoggerFactory.getLogger(Login.class);
//    private static final String CLIENT_REGISTRATION_PAGE_CLASS_NAME = "org.vetcontrol.sync.client.web.pages.RegistrationPage";
//    @EJB(name = "ClientBean")
//    private ClientBean clientBean;
//
//    public Login() {
//        init(false);
//    }
//
//    public Login(PageParameters pageParameters) {
//        init(true);
//    }
//
//    private void init(boolean isError) {
//        add(JavascriptPackageResource.getHeaderContribution(CoreJavaScriptResourceReference.get()));
//        add(JavascriptPackageResource.getHeaderContribution(WebCommonResourceInitializer.COMMON_JS));
//        add(JavascriptPackageResource.getHeaderContribution(getClass(), getClass().getSimpleName() + ".js"));
//        add(CSSPackageResource.getHeaderContribution(WebCommonResourceInitializer.STYLE_CSS));
//
//        add(new Label("login.title", new ResourceModel("login.title")));
//        add(new Label("login.header", new ResourceModel(isError ? "login.errorLabel" : "login.enterLabel")));
//        WebMarkupContainer errorPanel = new WebMarkupContainer("errorPanel");
//        errorPanel.setVisible(isError);
//        add(errorPanel);
//
//        try {
//            clientBean.getCurrentClient();
//        } catch (Exception e) {
//            if (e.getCause() instanceof org.vetcontrol.sync.NotRegisteredException) {
//                try {
//                    setRedirect(true);
//                    setResponsePage((Class) Class.forName(CLIENT_REGISTRATION_PAGE_CLASS_NAME));
//                } catch (ClassNotFoundException ex) {
//                    log.error("Class {} not found.", CLIENT_REGISTRATION_PAGE_CLASS_NAME);
//                    throw new RuntimeException(ex);
//                }
//            }
//        }
//    }
//}
//
