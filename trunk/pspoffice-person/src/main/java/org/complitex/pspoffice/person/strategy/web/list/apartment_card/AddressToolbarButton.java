/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.list.apartment_card;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.model.ResourceModel;
import org.complitex.dictionary.web.component.css.CssAttributeBehavior;
import org.complitex.template.web.component.toolbar.ToolbarButton;

/**
 *
 * @author Artem
 */
abstract class AddressToolbarButton extends ToolbarButton {

    private boolean isPostBack;

    AddressToolbarButton(String id) {
        super(id, true, null);
        add(CSSPackageResource.getHeaderContribution(AddressToolbarButton.class, AddressToolbarButton.class.getSimpleName() + ".css"));
        add(JavascriptPackageResource.getHeaderContribution(AddressToolbarButton.class, AddressToolbarButton.class.getSimpleName() + ".js"));
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        if (!isPostBack) {
            isPostBack = true;
            init(new ResourceReference(AddressToolbarButton.class, "gear_blue.png"), new ResourceModel("address_toolbar_button_title"));
            if (isFullAddressEnabled()) {
                getLink().add(new CssAttributeBehavior("down"));
            }
        }
    }

    abstract boolean isFullAddressEnabled();

    @Override
    protected abstract void onClick(AjaxRequestTarget target);
}
