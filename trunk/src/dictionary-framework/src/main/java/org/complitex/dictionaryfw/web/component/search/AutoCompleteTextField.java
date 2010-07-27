package org.complitex.dictionaryfw.web.component.search;

import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.IAutoCompleteRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.complitex.dictionaryfw.entity.DomainObject;
import org.complitex.dictionaryfw.web.component.AbstractAutoCompleteTextField;

/**
 *
 * @author Artem
 */
public abstract class AutoCompleteTextField extends AbstractAutoCompleteTextField<DomainObject> {

    public abstract static class AutoCompleteTextFieldModel extends Model<String> {

        private String entityTable;

        private IModel<DomainObject> model;

        private AbstractAutoCompleteTextField<DomainObject> autoComplete;

        public AutoCompleteTextFieldModel(IModel<DomainObject> model, String entityTable) {
            this.model = model;
            this.entityTable = entityTable;
        }

        @Override
        public String getObject() {
            DomainObject object = model.getObject();
            if (object != null) {
                return getTextValue(object);
            }
            return null;
        }

        @Override
        public void setObject(String object) {
            model.setObject(autoComplete.findChoice());
        }

        protected String getEntityTable() {
            return entityTable;
        }

        private void setAutoComplete(AbstractAutoCompleteTextField<DomainObject> autoComplete) {
            this.autoComplete = autoComplete;
        }

        public abstract String getTextValue(DomainObject object);
    }

    public AutoCompleteTextField(String id, AutoCompleteTextFieldModel model, IAutoCompleteRenderer renderer, AutoCompleteSettings settings) {
        super(id, null, String.class, renderer, settings);
        model.setAutoComplete(this);
        this.setModel(model);
    }

    @Override
    protected String getChoiceValue(DomainObject choice) throws Throwable {
        return ((AutoCompleteTextFieldModel) getModel()).getTextValue(choice);
    }
}
