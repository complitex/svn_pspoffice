/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.edit.apartment_card.toolbar;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.ResourceModel;
import org.complitex.template.web.component.toolbar.ToolbarButton;

/**
 *
 * @author Artem
 */
public abstract class AddRegistrationToolbarButton extends ToolbarButton {

    private boolean isPostBack;

    public AddRegistrationToolbarButton(String id) {
        super(id, true, null);
        add(CSSPackageResource.getHeaderContribution(AddRegistrationToolbarButton.class,
                AddRegistrationToolbarButton.class.getSimpleName() + ".css"));
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        if (!isPostBack) {
            init(new ResourceReference(AddRegistrationToolbarButton.class, "add_person.png"),
                    new ResourceModel("add_registration_title"));
            isPostBack = true;
        }
    }

    @Override
    abstract protected AbstractLink newLink(String linkId);
}
