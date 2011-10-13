/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.edit.person.toolbar;

import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.link.Link;
import org.complitex.dictionary.web.component.css.CssAttributeBehavior;
import org.complitex.template.web.component.toolbar.ToolbarButton;

/**
 *
 * @author Artem
 */
public abstract class PersonCardButton extends ToolbarButton {

    private static final String IMAGE_SRC = "images/icon-editDocument.gif";
    private static final String TITLE_KEY = "image.title.person_card";

    public PersonCardButton(String id) {
        super(id, new ResourceReference(IMAGE_SRC), TITLE_KEY);
        add(CSSPackageResource.getHeaderContribution(PersonCardButton.class, PersonCardButton.class.getSimpleName() + ".css"));
    }

    @Override
    protected Link newLink(String linkId) {
        Link<Void> link = new Link<Void>(linkId) {

            @Override
            public void onClick() {
                PersonCardButton.this.onClick();
            }

            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);
                tag.put("target", "_blank");
            }
        };
        link.add(new CssAttributeBehavior("person_card"));
        return link;
    }
}
