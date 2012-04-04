package org.complitex.pspoffice.web;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.complitex.address.web.ImportPage;
import org.complitex.template.web.template.ITemplateLink;

import java.util.List;
import java.util.Locale;

/**
 * @author Artem
 */
public class AdminTemplateMenu extends org.complitex.admin.web.AdminTemplateMenu {

    @Override
    public List<ITemplateLink> getTemplateLinks(Locale locale) {
        List<ITemplateLink> links = super.getTemplateLinks(locale);

        links.add(new ITemplateLink() {

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
                return "complitex_import";
            }
        });

        links.add(new ITemplateLink() {

            @Override
            public String getLabel(Locale locale) {
                return getString(org.complitex.pspoffice.legacy_import.web.ImportPage.class, locale, "title");
            }

            @Override
            public Class<? extends Page> getPage() {
                return org.complitex.pspoffice.legacy_import.web.ImportPage.class;
            }

            @Override
            public PageParameters getParameters() {
                return PageParameters.NULL;
            }

            @Override
            public String getTagId() {
                return "psp_import";
            }
        });

        return links;
    }
}
