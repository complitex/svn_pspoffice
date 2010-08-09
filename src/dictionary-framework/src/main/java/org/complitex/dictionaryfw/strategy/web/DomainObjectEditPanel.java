/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.dictionaryfw.strategy.web;

import javax.ejb.EJB;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionaryfw.dao.StringCultureBean;
import org.complitex.dictionaryfw.entity.DomainObject;
import org.complitex.dictionaryfw.entity.description.Entity;
import org.complitex.dictionaryfw.strategy.Strategy;
import org.complitex.dictionaryfw.strategy.StrategyFactory;
import org.complitex.dictionaryfw.util.CloneUtil;
import org.complitex.dictionaryfw.web.component.ChildrenContainer;
import org.complitex.dictionaryfw.web.component.DomainObjectInputPanel;
import org.complitex.dictionaryfw.web.component.search.SearchComponentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
public final class DomainObjectEditPanel extends Panel {

    private static final Logger log = LoggerFactory.getLogger(DomainObjectEditPanel.class);

    @EJB(name = "StrategyFactory")
    private StrategyFactory strategyFactory;

    @EJB(name = "StringCultureBean")
    private StringCultureBean stringBean;

    private String entity;

    private DomainObject oldObject;

    private DomainObject newObject;

    private Long parentId;

    private String parentEntity;

    private DomainObjectInputPanel objectInputPanel;

    public DomainObjectEditPanel(String id, String entity, Long objectId, Long parentId, String parentEntity) {
        super(id);
        this.entity = entity;
        this.parentId = parentId;
        this.parentEntity = parentEntity;

        if (objectId == null) {
            //create new entity
            oldObject = null;
            newObject = getStrategy().newInstance();

        } else {
            //edit existing entity
            newObject = getStrategy().findById(objectId);
            oldObject = CloneUtil.cloneObject(newObject);
        }
        init();
    }

    private Strategy getStrategy() {
        return strategyFactory.getStrategy(entity);
    }

    public DomainObject getObject() {
        return newObject;
    }

    private void init() {
        final Entity description = getStrategy().getEntity();

        Label title = new Label("title", stringBean.displayValue(description.getEntityNames(), getLocale()));
        add(title);

        Label label = new Label("label", stringBean.displayValue(description.getEntityNames(), getLocale()));
        add(label);

        final FeedbackPanel messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        add(messages);

        Form form = new Form("form");

        objectInputPanel = new DomainObjectInputPanel("domainObjectInputPanel", newObject, entity, parentId, parentEntity);
        form.add(objectInputPanel);

        //complex attributes
        AbstractComplexAttributesPanel complexAttributes = null;
        Class<? extends AbstractComplexAttributesPanel> clazz = getStrategy().getComplexAttributesPanelClass();
        if (clazz != null) {
            try {
                complexAttributes = clazz.getConstructor(String.class).newInstance("complexAttributes");

            } catch (Exception e) {
                log.warn("Couldn't instantiate complex attributes panel object.", e);
            }
        }
        if (complexAttributes == null) {
            form.add(new EmptyPanel("complexAttributes"));
        } else {
            form.add(complexAttributes);
        }

        //children
        Component childrenContainer = new EmptyPanel("childrenContainer");
        if (oldObject != null) {
            childrenContainer = new ChildrenContainer("childrenContainer", entity, newObject.getId());
        }
        form.add(childrenContainer);

        //save-cancel functional
        Button submit = new Button("submit") {

            @Override
            public void onSubmit() {
                save();
            }
        };
        form.add(submit);
        Link cancel = new Link("cancel") {

            @Override
            public void onClick() {
                cancel();
            }
        };
        form.add(cancel);
        add(form);
    }

    protected boolean validateParent() {
        if (!(getStrategy().getParentSearchFilters() == null
                || getStrategy().getParentSearchFilters().isEmpty()
                || getStrategy().getParentSearchCallback() == null)) {
            if ((newObject.getParentId() == null) || (newObject.getParentEntityId() == null)) {
                error(getString("parent_required"));
                return false;
            }
        }
        return true;
    }

    protected boolean validate() {
        boolean valid = validateParent();
        IValidator validator = getStrategy().getValidator();
        if (validator != null) {
            valid = validator.validate(newObject, this);
        }
        return valid;
    }

    protected void save() {
        if (validate()) {
            if (oldObject == null) {
                getStrategy().insert(newObject);
            } else {
                getStrategy().update(oldObject, newObject);
            }
            cancel();
        }
    }

    private void cancel() {
        if (!fromParent()) {
            //return to list page for current entity.
            setResponsePage(getStrategy().getListPage(), getStrategy().getListPageParams());
        } else {
            //return to edit page for parent entity.
            setResponsePage(strategyFactory.getStrategy(parentEntity).getEditPage(),
                    strategyFactory.getStrategy(parentEntity).getEditPageParams(parentId, null, null));
        }
    }

    private boolean fromParent() {
        return parentId != null && !Strings.isEmpty(parentEntity);
    }

    public SearchComponentState getParentSearchComponentState() {
        return objectInputPanel.getParentSearchComponentState();
    }
}
