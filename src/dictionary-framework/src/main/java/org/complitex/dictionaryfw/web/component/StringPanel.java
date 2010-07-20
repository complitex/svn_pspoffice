/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.dictionaryfw.web.component;

import java.util.List;
import java.util.Locale;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.complitex.dictionaryfw.entity.StringCulture;

/**
 *
 * @author Artem
 */
public final class StringPanel extends Panel {

    public StringPanel(String id, IModel<List<StringCulture>> model, final String systemLocale, final String label, final boolean enabled) {
        super(id);

        add(new ListView<StringCulture>("strings", model) {

            @Override
            protected void populateItem(ListItem<StringCulture> item) {
                StringCulture culture = item.getModelObject();

                Label lang = new Label("lang", new Locale(culture.getLocale()).getDisplayLanguage(getLocale()));
                item.add(lang);

                boolean required = false;
                if (new Locale(culture.getLocale()).getLanguage().equalsIgnoreCase(new Locale(systemLocale).getLanguage())) {
                    required = true;
                }

                InputPanel<String> inputPanel = new InputPanel("inputPanel", new PropertyModel<String>(culture, "value"),
                        String.class, required, label, enabled);
                item.add(inputPanel);

                WebMarkupContainer requiredContainer = new WebMarkupContainer("bookFieldRequired");
                requiredContainer.setVisible(required);
                item.add(requiredContainer);
            }
        });
    }
}
