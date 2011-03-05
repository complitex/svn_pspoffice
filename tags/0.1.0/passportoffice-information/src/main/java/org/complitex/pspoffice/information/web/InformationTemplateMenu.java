/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.information.web;

import com.google.common.collect.Lists;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.complitex.dictionaryfw.strategy.Strategy;
import org.complitex.dictionaryfw.strategy.StrategyFactoryStatic;
import org.complitex.pspoffice.commons.web.template.ITemplateLink;
import org.complitex.pspoffice.commons.web.template.ResourceTemplateMenu;
import org.complitex.pspoffice.information.BookEntities;
import org.complitex.pspoffice.information.resource.CommonResources;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author Artem
 */
public class InformationTemplateMenu extends ResourceTemplateMenu {

    private static Strategy getStrategy(String entity) {
        return StrategyFactoryStatic.getStrategy(entity);
    }

    @Override
    public String getTitle(Locale locale) {
        return getString(CommonResources.class, locale, "information_menu");
    }

    @Override
    public List<ITemplateLink> getTemplateLinks(final Locale locale) {
        List<ITemplateLink> links = Lists.newArrayList();
        for (final String bookEntity : BookEntities.getEntities()) {
            links.add(new ITemplateLink() {

                @Override
                public String getLabel(Locale locale) {
                    return getStrategy(bookEntity).getPluralEntityLabel(locale);
                }

                @Override
                public Class<? extends Page> getPage() {
                    return getStrategy(bookEntity).getListPage();
                }

                @Override
                public PageParameters getParameters() {
                    return getStrategy(bookEntity).getListPageParams();
                }

                @Override
                public String getTagId() {
                    return bookEntity + "_book_item";
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
        return "information_menu";
    }
}
