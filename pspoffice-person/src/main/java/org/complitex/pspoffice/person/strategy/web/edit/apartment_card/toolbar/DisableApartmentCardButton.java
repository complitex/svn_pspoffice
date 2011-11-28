/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.edit.apartment_card.toolbar;

import org.apache.wicket.ResourceReference;
import org.complitex.template.web.component.toolbar.ToolbarButton;

/**
 *
 * @author Artem
 */
public abstract class DisableApartmentCardButton extends ToolbarButton {

    private static final String IMAGE_SRC = "images/icon-deleteDocument.gif";
    private static final String TITLE_KEY = "disable";

    public DisableApartmentCardButton(String id) {
        super(id, new ResourceReference(IMAGE_SRC), TITLE_KEY, true);
    }
}
