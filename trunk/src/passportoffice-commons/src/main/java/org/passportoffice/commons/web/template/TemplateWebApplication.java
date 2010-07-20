//package org.passportoffice.commons.web.template;
//
//import org.apache.wicket.Request;
//import org.apache.wicket.Response;
//import org.apache.wicket.Session;
//import org.odlabs.wiquery.core.commons.WiQueryInstantiationListener;
//import org.vetcontrol.web.security.ServeltAuthWebApplication;
//import org.wicketstuff.javaee.injection.JavaEEComponentInjector;
//
//import java.io.InputStream;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.vetcontrol.util.template.loader.TemplateLoader;
//
///**
// * @author Anatoly A. Ivanov java@inheaven.ru
// * Date: 22.12.2009 21:18:32
// *
// * Wicket приложение, которое использует независимые модули и единый шаблон.
// */
//public abstract class TemplateWebApplication extends ServeltAuthWebApplication {
//
//    private static final Logger log = LoggerFactory.getLogger(TemplateWebApplication.class);
//
//    private static final String TEMPLATE_CONFIG_FILE_NAME = "vetcontrol-template.xml";
//
//    private List<Class<ITemplateMenu>> menuClasses;
//
//    @Override
//    protected void init() {
//        super.init();
//
//        initializeWiQuery();
//        initializeJEEInjector();
//        initializeTemplateConfig();
//    }
//
//    private void initializeTemplateConfig() throws RuntimeException {
//        try {
//            Iterator<URL> resources = getApplicationSettings().getClassResolver().getResources(TEMPLATE_CONFIG_FILE_NAME);
//            InputStream inputStream = null;
//            if (resources.hasNext()) {
//                inputStream = resources.next().openStream();
//                if (resources.hasNext()) {
//                    log.warn("There are more than one template config {} files. What file will be picked is unpredictable.", TEMPLATE_CONFIG_FILE_NAME);
//                }
//            } else {
//                throw new RuntimeException("Template config file " + TEMPLATE_CONFIG_FILE_NAME + " was not found.");
//            }
//            TemplateLoader templateLoader = new TemplateLoader(inputStream);
//            List<String> menuClassNames = templateLoader.getMenuClassNames();
//
//            menuClasses = new ArrayList<Class<ITemplateMenu>>();
//            for (String menuClassName : menuClassNames) {
//                try {
//                    Class menuClass = getApplicationSettings().getClassResolver().resolveClass(menuClassName);
//                    menuClasses.add(menuClass);
//                } catch (Exception e) {
//                    log.warn("Меню не найдено : {}", menuClassName);
//                }
//            }
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private void initializeJEEInjector() {
//        addComponentInstantiationListener(new JavaEEComponentInjector(this));
//    }
//
//    private void initializeWiQuery() {
//        addComponentInstantiationListener(new WiQueryInstantiationListener());
//    }
//
//    public List<Class<ITemplateMenu>> getMenuClasses() {
//        return menuClasses;
//    }
//
//    @Override
//    public Session newSession(Request request, Response response) {
//        return new VetControlSession(request);
//    }
//}
