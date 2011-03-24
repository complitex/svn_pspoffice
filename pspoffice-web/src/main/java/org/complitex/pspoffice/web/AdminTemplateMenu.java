package org.complitex.pspoffice.web;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.complitex.address.web.ImportPage;
import org.complitex.template.web.template.ITemplateLink;

import java.util.List;
import java.util.Locale;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.03.11 16:40
 */
public class AdminTemplateMenu extends org.complitex.admin.web.AdminTemplateMenu {

    @Override
    public List<ITemplateLink> getTemplateLinks(Locale locale) {
        List<ITemplateLink> links = super.getTemplateLinks(locale);

        links.add(new ITemplateLink(){
            @Override
            public String getLabel(Locale locale) {
                return getString(ImportPage.class, locale, "title");
            }

            @SuppressWarnings({"unchecked"})
            @Override
            public Class<? extends Page> getPage() {
                return ImportPage.class;
            }

            @Override
            public PageParameters getParameters() {
                return PageParameters.NULL;
            }

            @Override
            public String getTagId() {
                return "ImportPage";
            }
        });

        return links;
    }
}
