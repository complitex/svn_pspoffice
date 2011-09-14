/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.edit.person;

import java.util.Locale;
import javax.ejb.EJB;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.service.LocaleBean;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.entity.PersonName.PersonNameType;
import org.complitex.pspoffice.person.strategy.web.component.PersonNameAutocompleteComponent;

/**
 *
 * @author Artem
 */
class PersonFullNamePanel extends Panel {

    @EJB
    private LocaleBean localeBean;

    PersonFullNamePanel(String id, Person person) {
        super(id);
        init(person, null, null, null, null);
    }

    public PersonFullNamePanel(String id, Person person, Locale defaultNameLocale, String defaultLastName, String defaultFirstName,
            String defaultMiddleName) {
        super(id);
        init(person, defaultNameLocale, defaultLastName, defaultFirstName, defaultMiddleName);
    }

    protected void init(Person person, final Locale defaultNameLocale, final String defaultLastName, final String defaultFirstName,
            final String defaultMiddleName) {
        add(new ListView<Attribute>("lastNames", person.getAttributes(PersonStrategy.LAST_NAME)) {

            @Override
            protected void populateItem(ListItem<Attribute> item) {
                PersonFullNamePanel.this.populateItem(item, PersonNameType.LAST_NAME, "lastName", defaultNameLocale, defaultLastName);
            }
        });

        add(new ListView<Attribute>("firstNames", person.getAttributes(PersonStrategy.FIRST_NAME)) {

            @Override
            protected void populateItem(ListItem<Attribute> item) {
                PersonFullNamePanel.this.populateItem(item, PersonNameType.FIRST_NAME, "firstName", defaultNameLocale, defaultFirstName);
            }
        });

        add(new ListView<Attribute>("middleNames", person.getAttributes(PersonStrategy.MIDDLE_NAME)) {

            @Override
            protected void populateItem(ListItem<Attribute> item) {
                PersonFullNamePanel.this.populateItem(item, PersonNameType.MIDDLE_NAME, "middleName", defaultNameLocale, defaultMiddleName);
            }
        });
    }

    private void populateItem(ListItem<Attribute> item, PersonNameType personNameType, String personNameComponentId,
            Locale defaultNameLocale, String defaultNameValue) {
        Attribute personNameAttribute = item.getModelObject();
        Locale locale = localeBean.getLocale(personNameAttribute.getAttributeId());
        boolean isSystemLocale = localeBean.getLocaleObject(personNameAttribute.getAttributeId()).isSystem();
        final PersonNameAutocompleteComponent personNameComponent =
                (!Strings.isEmpty(defaultNameValue) && defaultNameLocale != null
                && localeBean.convert(defaultNameLocale).getId().equals(personNameAttribute.getAttributeId()))
                ? new PersonNameAutocompleteComponent(personNameComponentId, newNameModel(personNameAttribute), new Model<String>(defaultNameValue),
                personNameType, locale, true)
                : new PersonNameAutocompleteComponent(personNameComponentId, newNameModel(personNameAttribute), personNameType, locale, true);
        personNameComponent.setRequired(isSystemLocale);
        item.add(personNameComponent);
        Label language = new Label("language", locale.getDisplayLanguage(getLocale()));
        item.add(language);
        WebMarkupContainer requiredContainer = new WebMarkupContainer("required");
        requiredContainer.setVisible(isSystemLocale);
        item.add(requiredContainer);
    }

    private IModel<Long> newNameModel(final Attribute nameAttribute) {
        return new Model<Long>() {

            @Override
            public Long getObject() {
                return nameAttribute.getValueId();
            }

            @Override
            public void setObject(Long object) {
                nameAttribute.setValueId(object);
            }
        };
    }
}
