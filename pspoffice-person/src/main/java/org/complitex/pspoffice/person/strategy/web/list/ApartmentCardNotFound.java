/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.list;

import javax.ejb.EJB;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.complitex.address.service.AddressRendererBean;
import org.complitex.pspoffice.person.strategy.web.edit.apartment_card.ApartmentCardEdit;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.TemplatePage;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.PERSON_MODULE_EDIT)
public final class ApartmentCardNotFound extends TemplatePage {

    @EJB
    private AddressRendererBean addressRendererBean;

    public ApartmentCardNotFound(final String addressEntity, final long addressId) {
        add(new Label("title", new ResourceModel("title")));
        add(new Label("label", new StringResourceModel("label", null, new Object[]{
                    addressRendererBean.displayAddress(addressEntity, addressId, getLocale())
                })));
        add(new Link<Void>("yes") {

            @Override
            public void onClick() {
                setResponsePage(new ApartmentCardEdit(addressEntity, addressId));
            }
        });
        add(new Link<Void>("no") {

            @Override
            public void onClick() {
                setResponsePage(ApartmentCardSearch.class);
            }
        });
    }
}

