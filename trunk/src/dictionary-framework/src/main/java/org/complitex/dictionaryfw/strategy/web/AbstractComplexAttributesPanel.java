/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.dictionaryfw.strategy.web;

import org.apache.wicket.markup.html.panel.Panel;

/**
 *
 * @author Artem
 */
public abstract class AbstractComplexAttributesPanel extends Panel {

    public AbstractComplexAttributesPanel(String id) {
        super(id);
    }

    protected DomainObjectEditPanel getEditPagePanel() {
        return this.findParent(DomainObjectEditPanel.class);
    }
}
