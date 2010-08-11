/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.information.web;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.complitex.pspoffice.commons.web.pages.EntityDescription;
import org.complitex.pspoffice.commons.web.template.ITemplateLink;
import org.complitex.pspoffice.commons.web.template.ResourceTemplateMenu;
import org.complitex.pspoffice.information.BookEntities;
import org.complitex.pspoffice.information.resource.CommonResources;

/**
 *
 * @author Artem
 */
public class InformationDescriptionTemplateMenu extends ResourceTemplateMenu {

    @Override
    public String getTitle(Locale locale) {
        return getString(CommonResources.class, locale, "description_menu");
    }

    @Override
    public List<ITemplateLink> getTemplateLinks(final Locale locale) {
        List<ITemplateLink> links = Lists.newArrayList();
        for (final String bookEntity : BookEntities.getEntities()) {
            links.add(new ITemplateLink() {

                @Override
                public String getLabel(Locale locale) {
                    return getString(CommonResources.class, locale, bookEntity);
                }

                @Override
                public Class<? extends Page> getPage() {
                    return EntityDescription.class;
                }

                @Override
                public PageParameters getParameters() {
                    return new PageParameters(ImmutableMap.of(EntityDescription.ENTITY, bookEntity));
                }

                @Override
                public String getTagId() {
                    return bookEntity + "_description_item";
                }
            });
        }
        Collections.sort(links, new Comparator<ITemplateLink>() {

            @Override
            public int compare(ITemplateLink o1, ITemplateLink o2) {
                return o1.getLabel(locale).compareTo(o2.getLabel(locale));
            }
        });
        return links;
    }

    @Override
    public String getTagId() {
        return "description_menu";
    }
}
