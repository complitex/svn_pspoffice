package org.complitex.dictionaryfw.web.component.search;

///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package org.passportoffice.information.web.component.search;
//
//import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
//import org.apache.wicket.extensions.ajax.markup.html.autocomplete.IAutoCompleteRenderer;
//import org.apache.wicket.model.IModel;
//import org.apache.wicket.model.Model;
//import org.complitex.dictionaryfw.entity.AbstractEntity;
//import org.complitex.dictionaryfw.web.component.AbstractAutoCompleteTextField;
//
///**
// *
// * @author Artem
// */
//public abstract class AutoCompleteTextField<T extends AbstractEntity> extends AbstractAutoCompleteTextField<T> {
//
//    public abstract static class AutoCompleteTextFieldModel<S extends AbstractEntity> extends Model<String> {
//
//        private IModel<S> model;
//
//        private AbstractAutoCompleteTextField<S> autoComplete;
//
//        public AutoCompleteTextFieldModel(IModel<S> model) {
//            this.model = model;
//        }
//
//        @Override
//        public String getObject() {
//            S entity = model.getObject();
//            if (entity != null) {
//                return getTextValue(entity);
//            }
//            return null;
//        }
//
//        @Override
//        public void setObject(String object) {
//            model.setObject(autoComplete.findChoice());
//        }
//
//        private void setAutoComplete(AbstractAutoCompleteTextField<S> autoComplete) {
//            this.autoComplete = autoComplete;
//        }
//
//        public abstract String getTextValue(S entity);
//    }
//
//    public AutoCompleteTextField(String id, AutoCompleteTextFieldModel<T> model, IAutoCompleteRenderer renderer, AutoCompleteSettings settings) {
//        super(id, null, String.class, renderer, settings);
//        model.setAutoComplete(this);
//        this.setModel(model);
//    }
//
//    @Override
//    protected String getChoiceValue(T choice) throws Throwable {
//        return ((AutoCompleteTextFieldModel<T>) getModel()).getTextValue(choice);
//    }
//}
//
