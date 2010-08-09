package org.complitex.pspoffice.admin.web;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.complitex.pspoffice.commons.web.template.ITemplateLink;
import org.complitex.pspoffice.commons.web.template.ResourceTemplateMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 29.07.2010 14:01:04
 *
 *   Меню администрирование
 */
public class AdminTemplateMenu extends ResourceTemplateMenu {
    private static final Logger log = LoggerFactory.getLogger(AdminTemplateMenu.class);

    @Override
    public String getTitle(Locale locale) {
        return getString(AdminTemplateMenu.class, locale, "admin.template.menu.title");
    }

    @Override
    public List<ITemplateLink> getTemplateLinks(Locale locale) {
        List<ITemplateLink> links = new ArrayList<ITemplateLink>();

        links.add(new ITemplateLink(){
            @Override
            public String getLabel(Locale locale) {
                return getString(AdminTemplateMenu.class, locale, "admin.template.menu.list");
            }
            @Override
            public Class<? extends Page> getPage() {
                return UserList.class;
            }

            @Override
            public PageParameters getParameters() {
                return PageParameters.NULL;
            }

            @Override
            public String getTagId() {
                return "UserList";
            }
        });        

        return links;
    }

    @Override
    public String getTagId() {
        return "admin_menu";
    }
}