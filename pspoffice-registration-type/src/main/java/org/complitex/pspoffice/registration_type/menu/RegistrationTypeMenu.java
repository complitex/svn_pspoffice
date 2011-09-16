/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.registration_type.menu;

import com.google.common.collect.ImmutableList;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.complitex.template.web.template.ITemplateLink;
import org.complitex.template.web.template.ResourceTemplateMenu;

import java.util.List;
import java.util.Locale;
import org.complitex.dictionary.strategy.IStrategy;
import org.complitex.dictionary.strategy.StrategyFactory;
import org.complitex.dictionary.util.EjbBeanLocator;
import org.complitex.template.web.security.SecurityRole;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.REGISTRATION_TYPE_MODULE_VIEW)
public class RegistrationTypeMenu extends ResourceTemplateMenu {

    private static IStrategy getStrategy() {
        return EjbBeanLocator.getBean(StrategyFactory.class).getStrategy("registration_type");
    }

    @Override
    public String getTitle(Locale locale) {
        return getString(MenuResources.class, locale, "registration_type_menu");
    }

    @Override
    public List<ITemplateLink> getTemplateLinks(final Locale locale) {
        List<ITemplateLink> links = ImmutableList.<ITemplateLink>of(new ITemplateLink() {

            @Override
            public String getLabel(Locale locale) {
                return getStrategy().getPluralEntityLabel(locale);
            }

            @Override
            public Class<? extends Page> getPage() {
                return getStrategy().getListPage();
            }

            @Override
            public PageParameters getParameters() {
                return getStrategy().getListPageParams();
            }

            @Override
            public String getTagId() {
                return "registration_type_item";
            }
        });
        return links;
    }

    @Override
    public String getTagId() {
        return "registration_type_menu";
    }
}
