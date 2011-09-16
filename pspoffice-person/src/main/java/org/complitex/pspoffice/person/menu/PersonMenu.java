/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.menu;

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
@AuthorizeInstantiation(SecurityRole.PERSON_MODULE_VIEW)
public class PersonMenu extends ResourceTemplateMenu {

    private static IStrategy getPersonStrategy() {
        return EjbBeanLocator.getBean(StrategyFactory.class).getStrategy("person");
    }

    @Override
    public String getTitle(Locale locale) {
        return getString(MenuResources.class, locale, "person_menu");
    }

    @Override
    public List<ITemplateLink> getTemplateLinks(final Locale locale) {
        List<ITemplateLink> links = ImmutableList.<ITemplateLink>of(
                new ITemplateLink() {

                    @Override
                    public String getLabel(Locale locale) {
                        return getPersonStrategy().getPluralEntityLabel(locale);
                    }

                    @Override
                    public Class<? extends Page> getPage() {
                        return getPersonStrategy().getListPage();
                    }

                    @Override
                    public PageParameters getParameters() {
                        return getPersonStrategy().getListPageParams();
                    }

                    @Override
                    public String getTagId() {
                        return "person_item";
                    }
                }
//                new ITemplateLink() {
//
//                    @Override
//                    public String getLabel(Locale locale) {
//                        return getString(MenuResources.class, locale, "family_and_apartment_info");
//                    }
//
//                    @Override
//                    public Class<? extends Page> getPage() {
//                        return FamilyAndApartmentInfoAddressParamPage.class;
//                    }
//
//                    @Override
//                    public PageParameters getParameters() {
//                        return PageParameters.NULL;
//                    }
//
//                    @Override
//                    public String getTagId() {
//                        return "family_and_apartment_info_item";
//                    }
//                },
//                new ITemplateLink() {
//
//                    @Override
//                    public String getLabel(Locale locale) {
//                        return getString(MenuResources.class, locale, "family_and_communal_apartment_info");
//                    }
//
//                    @Override
//                    public Class<? extends Page> getPage() {
//                        return FamilyAndCommunalApartmentInfoParamPage.class;
//                    }
//
//                    @Override
//                    public PageParameters getParameters() {
//                        return PageParameters.NULL;
//                    }
//
//                    @Override
//                    public String getTagId() {
//                        return "family_and_communal_apartment_info_item";
//                    }
//                },
//                new ITemplateLink() {
//
//                    @Override
//                    public String getLabel(Locale locale) {
//                        return getString(MenuResources.class, locale, "family_and_housing_payments");
//                    }
//
//                    @Override
//                    public Class<? extends Page> getPage() {
//                        return FamilyAndHousingPaymentsAddressParamPage.class;
//                    }
//
//                    @Override
//                    public PageParameters getParameters() {
//                        return PageParameters.NULL;
//                    }
//
//                    @Override
//                    public String getTagId() {
//                        return "family_and_housing_payments_item";
//                    }
//                },
//                new ITemplateLink() {
//
//                    @Override
//                    public String getLabel(Locale locale) {
//                        return getString(MenuResources.class, locale, "housing_payments");
//                    }
//
//                    @Override
//                    public Class<? extends Page> getPage() {
//                        return HousingPaymentsAddressParamPage.class;
//                    }
//
//                    @Override
//                    public PageParameters getParameters() {
//                        return PageParameters.NULL;
//                    }
//
//                    @Override
//                    public String getTagId() {
//                        return "housing_payments_item";
//                    }
//                }
                );
        return links;
    }

    @Override
    public String getTagId() {
        return "registration_menu";
    }
}
