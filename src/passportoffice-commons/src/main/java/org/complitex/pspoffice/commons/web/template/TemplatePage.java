package org.complitex.pspoffice.commons.web.template;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.util.string.Strings;
import org.complitex.pspoffice.commons.web.component.LocalePicker;
import org.complitex.pspoffice.commons.web.component.toolbar.HelpButton;
import org.complitex.pspoffice.commons.web.component.toolbar.ToolbarButton;
import org.complitex.pspoffice.web.resource.WebCommonResourceInitializer;
import org.odlabs.wiquery.core.commons.CoreJavaScriptResourceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 22.07.2010 16:09:45
 */
public class TemplatePage extends WebPage {
    private static final Logger log = LoggerFactory.getLogger(TemplatePage.class);


    public TemplatePage() {
        add(JavascriptPackageResource.getHeaderContribution(CoreJavaScriptResourceReference.get()));
        add(JavascriptPackageResource.getHeaderContribution(WebCommonResourceInitializer.COMMON_JS));
        add(JavascriptPackageResource.getHeaderContribution(TemplatePage.class, TemplatePage.class.getSimpleName() + ".js"));
        add(CSSPackageResource.getHeaderContribution(WebCommonResourceInitializer.STYLE_CSS));

        add(new Link("home"){

            @Override
            public void onClick() {
                setResponsePage(getApplication().getHomePage());
            }
        });

        //locale picker
        add(new LocalePicker("localePicker", Arrays.asList(getLocale())));

        //toolbar
        WebMarkupContainer toolbar = new WebMarkupContainer("toolbar");
        add(toolbar);
        WebMarkupContainer commonPart = new WebMarkupContainer("commonPart");
        toolbar.add(commonPart);

        //add common buttons.
        HelpButton help = new HelpButton("help");
        commonPart.add(help);

        //add page custom buttons.
        List<ToolbarButton> pageToolbarButtonsList = getToolbarButtons("pageToolbarButton");
        if (pageToolbarButtonsList == null) {
            pageToolbarButtonsList = Collections.emptyList();
        }
        Component pagePart = new ListView<ToolbarButton>("pagePart", pageToolbarButtonsList) {

            @Override
            protected void populateItem(ListItem<ToolbarButton> item) {
                item.add(item.getModelObject());
            }
        };
        toolbar.add(pagePart);


        //menu
        add(new ListView<ITemplateMenu>("sidebar", newTemplateMenus()) {

            @Override
            protected void populateItem(ListItem<ITemplateMenu> item) {
                item.add(new TemplateMenu("menu_placeholder", "menu", this, item.getModelObject()));
            }
        });

//        User user = userProfileBean.getCurrentUser();
//
//
//        add(new Label("current_user_fullname", user.getFullName()
//                + (user.getJob() != null ? ", " + user.getJob().getDisplayName(getLocale(), systemLocale) : "")));
//        add(new Label("current_user_department", user.getDepartment().getDisplayName(getLocale(), systemLocale)));

        add(new Form("exit") {

            @Override
            public void onSubmit() {
                getTemplateWebApplication().logout();
            }
        });
    }

    /**
     * Боковая панель с меню, которое устанавливается в конфигурационном файле.
     */
    private class TemplateMenu extends Fragment {

        private String tagId;

        public TemplateMenu(String id, String markupId, MarkupContainer markupProvider, ITemplateMenu menu) {
            super(id, markupId, markupProvider);
            this.tagId = menu.getTagId();

            add(new Label("menu_title", menu.getTitle(getLocale())));
            add(new ListView<ITemplateLink>("menu_items", menu.getTemplateLinks(getLocale())) {

                @Override
                protected void populateItem(ListItem<ITemplateLink> item) {
                    final ITemplateLink templateLink = item.getModelObject();
                    BookmarkablePageLink link = new BookmarkablePageLink<Class<? extends Page>>("link", templateLink.getPage(),
                            templateLink.getParameters()) {

                        @Override
                        protected void onComponentTag(ComponentTag tag) {
                            super.onComponentTag(tag);
                            if (!Strings.isEmpty(templateLink.getTagId())) {
                                tag.put("id", templateLink.getTagId());
                            }
                        }
                    };
                    link.add(new Label("label", templateLink.getLabel(getLocale())));
                    item.add(link);
                }
            });
        }

        @Override
        protected void onComponentTag(ComponentTag tag) {
            super.onComponentTag(tag);
            if (!Strings.isEmpty(tagId)) {
                tag.put("id", tagId);
            }
        }
    }

    private List<ITemplateMenu> newTemplateMenus() {
        List<ITemplateMenu> templateMenus = new ArrayList<ITemplateMenu>();
        for (Class<ITemplateMenu> menuClass : getTemplateWebApplication().getMenuClasses()) {
            if (isTemplateMenuAuthorized(menuClass)) {
                try {
                    ITemplateMenu templateMenu = menuClass.newInstance();
                    templateMenus.add(templateMenu);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return templateMenus;
    }

    /**
     * Проверка роли пользователя для отображения меню модуля.
     * @param menuClass Класс меню.
     * @return Отображать ли меню пользователю в зависимости от его роли.
     */
    private boolean isTemplateMenuAuthorized(Class<?> menuClass) {
        boolean authorized = true;

        final AuthorizeInstantiation classAnnotation = menuClass.getAnnotation(AuthorizeInstantiation.class);
        if (classAnnotation != null) {
            authorized = getTemplateWebApplication().hasAnyRole(classAnnotation.value());
        }

        return authorized;
    }

    /**
     * Subclass can override method in order to specify custom page toolbar buttons.
     * @param id Component id
     * @return List of ToolbarButton to add to Template
     */
    protected List<ToolbarButton> getToolbarButtons(String id) {
        return null;
    }

    protected boolean hasAnyRole(String... roles) {
        return getTemplateWebApplication().hasAnyRole(roles);
    }

    protected TemplateWebApplication getTemplateWebApplication() {
        return (TemplateWebApplication) getApplication();
    }

    protected TemplateSession getTemplateSession(){
        return (TemplateSession) getSession();
    }

    protected String getStringOrKey(String key) {
        return key != null ? getString(key, null, key) : "";
    }

    public Locale getSystemLocale() {
        return getLocale();
    }
}

