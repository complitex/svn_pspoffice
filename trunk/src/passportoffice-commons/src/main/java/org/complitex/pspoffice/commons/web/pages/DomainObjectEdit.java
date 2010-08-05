/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.commons.web.pages;

import org.apache.wicket.PageParameters;
import org.complitex.dictionaryfw.strategy.web.DomainObjectEditPanel;
import org.complitex.pspoffice.commons.web.template.FormTemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Artem
 */
public final class DomainObjectEdit extends FormTemplatePage {

    private static final Logger log = LoggerFactory.getLogger(DomainObjectEdit.class);

    public static final String ENTITY = "entity";

    public static final String OBJECT_ID = "object_id";

    public static final String PARENT_ID = "parent_id";

    public static final String PARENT_ENTITY = "parent_entity";

    public DomainObjectEdit(PageParameters parameters) {
        init(parameters.getString(ENTITY), parameters.getAsLong(OBJECT_ID), parameters.getAsLong(PARENT_ID), parameters.getString(PARENT_ENTITY));
    }

    private void init(String entity, Long object_id, Long parentId, String parentEntity) {
        add(new DomainObjectEditPanel("editPanel", entity, object_id, parentId, parentEntity));
    }
}

