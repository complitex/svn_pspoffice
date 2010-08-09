/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.dictionaryfw.web.component;

import javax.ejb.EJB;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionaryfw.entity.DomainObject;
import org.complitex.dictionaryfw.strategy.Strategy;
import org.complitex.dictionaryfw.strategy.StrategyFactory;
import org.complitex.dictionaryfw.strategy.web.IValidator;

/**
 *
 * @author Artem
 */
public final class SaveCancelPanel extends Panel {

    @EJB(name = "StrategyFactory")
    private StrategyFactory strategyFactory;

    private String entity;

    private DomainObject oldObject;

    private DomainObject newObject;

    private Long parentId;

    private String parentEntity;

    public SaveCancelPanel(String id, String entity, DomainObject oldObject, DomainObject newObject, Long parentId, String parentEntity) {
        super(id);
        this.entity = entity;
        this.oldObject = oldObject;
        this.newObject = newObject;
        this.parentId = parentId;
        this.parentEntity = parentEntity;
        init();
    }

    protected Strategy getStrategy() {
        return strategyFactory.getStrategy(entity);
    }

    protected boolean fromParent() {
        return parentId != null && !Strings.isEmpty(parentEntity);
    }

    protected void save(DomainObject oldObject, DomainObject newObject) {
        if (validate(newObject)) {
            if (oldObject == null) {
                getStrategy().insert(newObject);
            } else {
                getStrategy().update(oldObject, newObject);
            }
            cancel();
        }
    }

    protected boolean validate(DomainObject newObject) {
        boolean valid = validateParent();
        IValidator validator = getStrategy().getValidator();
        if (validator != null) {
            valid = validator.validate(newObject, this);
        }
        return valid;
    }

    protected void cancel() {
        if (!fromParent()) {
            //return to list page for current entity.
            setResponsePage(getStrategy().getListPage(), getStrategy().getListPageParams());
        } else {
            //return to edit page for parent entity.
            setResponsePage(strategyFactory.getStrategy(parentEntity).getEditPage(),
                    strategyFactory.getStrategy(parentEntity).getEditPageParams(parentId, null, null));
        }
    }

    private void init() {
        Button submit = new Button("submit") {

            @Override
            public void onSubmit() {
                save(oldObject, newObject);
            }
        };
        add(submit);
        Link cancel = new Link("cancel") {

            @Override
            public void onClick() {
                cancel();
            }
        };
        add(cancel);
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

    public DomainObject getNewObject() {
        return newObject;
    }

    public DomainObject getOldObject() {
        return oldObject;
    }

    public String getParentEntity() {
        return parentEntity;
    }

    public Long getParentId() {
        return parentId;
    }
}
