///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package org.complitex.pspoffice.person.strategy.web.edit;
//
//import org.apache.wicket.model.IModel;
//import org.apache.wicket.model.Model;
//import org.complitex.dictionary.entity.DomainObject;
//import org.complitex.dictionary.strategy.web.AbstractComplexAttributesPanel;
//import org.complitex.dictionary.strategy.web.DomainObjectAccessUtil;
//import org.complitex.dictionary.web.component.name.FullNamePanel;
//import static org.complitex.pspoffice.person.strategy.PersonStrategy.*;
//
///**
// *
// * @author Artem
// */
//public class PersonNameEditComponent extends AbstractComplexAttributesPanel {
//
//    public PersonNameEditComponent(String id, boolean disabled) {
//        super(id, disabled);
//    }
//
//    @Override
//    protected void init() {
//        DomainObject person = getDomainObject();
//
//        FullNamePanel fullNamePanel = new FullNamePanel("fullName", newModel(person, FIRST_NAME),
//                newModel(person, MIDDLE_NAME), newModel(person, LAST_NAME));
//        fullNamePanel.setEnabled(!isDisabled() && DomainObjectAccessUtil.canEdit(null, "person", person));
//        add(fullNamePanel);
//    }
//
//    private IModel<Long> newModel(final DomainObject person, final Long attributeTypeId) {
//        return new Model<Long>() {
//
//            @Override
//            public Long getObject() {
//                return person.getAttribute(attributeTypeId).getValueId();
//            }
//
//            @Override
//            public void setObject(Long object) {
//                person.getAttribute(attributeTypeId).setValueId(object);
//            }
//        };
//    }
//}
