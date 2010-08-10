/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.information.web;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Locale;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.complitex.dictionaryfw.strategy.Strategy;
import org.complitex.dictionaryfw.strategy.StrategyFactory;
import org.complitex.pspoffice.commons.web.template.ITemplateLink;
import org.complitex.pspoffice.commons.web.template.ResourceTemplateMenu;
import org.complitex.pspoffice.information.resource.CommonResources;

/**
 *
 * @author Artem
 */
public class InformationTemplateMenu extends ResourceTemplateMenu {

    private static Strategy getStrategy(String entity) {
        return StrategyFactory.get().getStrategy(entity);
    }

    @Override
    public String getTitle(Locale locale) {
        return getString(CommonResources.class, locale, "title");
    }

    @Override
    public List<ITemplateLink> getTemplateLinks(Locale locale) {
        List<ITemplateLink> links = Lists.newArrayList();
        links.add(newLink("country"));
        links.add(newLink("region"));
        links.add(newLink("city"));
        links.add(newLink("district"));
        links.add(newLink("street"));
        links.add(newLink("building"));
        links.add(newLink("apartment"));
        links.add(newLink("room"));
        return links;
    }

    private ITemplateLink newLink(final String entityTable) {
        return new ITemplateLink() {

            @Override
            public String getLabel(Locale locale) {
                return getString(CommonResources.class, locale, entityTable);
            }

            @Override
            public Class<? extends Page> getPage() {
                return getStrategy(entityTable).getListPage();
            }

            @Override
            public PageParameters getParameters() {
                return getStrategy(entityTable).getListPageParams();
            }

            @Override
            public String getTagId() {
                return entityTable + "_item";
            }
        };
    }

    @Override
    public String getTagId() {
        return "information_menu";
    }
}
