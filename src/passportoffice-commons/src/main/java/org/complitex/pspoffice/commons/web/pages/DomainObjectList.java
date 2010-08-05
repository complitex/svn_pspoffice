/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.commons.web.pages;

import org.apache.wicket.PageParameters;
import org.complitex.dictionaryfw.strategy.web.DomainObjectListPanel;
import org.complitex.pspoffice.commons.web.template.TemplatePage;

/**
 * @author Artem
 */
public class DomainObjectList extends TemplatePage {

    public static final String ENTITY = "entity";

    public DomainObjectList(PageParameters params) {
        init(params.getString(ENTITY));
    }

    private void init(String entity) {
        add(new DomainObjectListPanel("listPanel", entity));
    }
}

