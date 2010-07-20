/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.passportoffice.commons.web.component;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 *
 * @author Artem
 */
public class InputPanel<T> extends Panel {

    public InputPanel(String id, IModel<T> model, Class<T> type, boolean required, String label, boolean enabled) {
        super(id);
        
        IModel<String> labelModel = new Model<String>(label);
        TextField<T> textField = new TextField<T>("textField", model);
        textField.setType(type);
        textField.setEnabled(enabled);
        textField.setLabel(labelModel);
        textField.setRequired(required);
        add(textField);
    }
}
