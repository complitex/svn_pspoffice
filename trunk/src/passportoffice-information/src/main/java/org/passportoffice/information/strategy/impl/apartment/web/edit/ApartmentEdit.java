/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.passportoffice.information.strategy.impl.apartment.web.edit;

import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.passportoffice.commons.dao.EntityDescriptionDao;
import org.passportoffice.commons.dao.LocaleDao;
import org.passportoffice.commons.entity.AttributeDescription;
import org.passportoffice.commons.entity.EntityAttribute;
import org.passportoffice.commons.entity.EntityDescription;
import org.passportoffice.commons.entity.SimpleTypes;
import org.passportoffice.commons.entity.StringCulture;
import org.passportoffice.commons.util.CloneUtil;
import org.passportoffice.commons.web.component.StringPanel;
import org.passportoffice.information.strategy.impl.apartment.Apartment;
import org.passportoffice.information.strategy.impl.apartment.dao.ApartmentDao;
import org.passportoffice.information.strategy.impl.apartment.example.ApartmentExample;
import org.passportoffice.information.strategy.impl.apartment.web.list.ApartmentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
public final class ApartmentEdit extends WebPage {

    private static final Logger log = LoggerFactory.getLogger(ApartmentEdit.class);

    @EJB(name = "ApartmentDao")
    private ApartmentDao dao;

    @EJB(name="LocaleDao")
    private LocaleDao localeDao;

    @EJB(name="EntityDescriptionDao")
    private EntityDescriptionDao entityDescriptionDao;

    private boolean isNew;

    public ApartmentEdit() {
        init(null);
    }

    public ApartmentEdit(PageParameters parameters) {
        init(parameters.getAsLong(ApartmentList.ID));
    }

    private void init(Long id) {
        final EntityDescription entityDesc = entityDescriptionDao.getEntityDescription(dao.getTable());
        final List<String> allLocales = localeDao.getAllLocales();

        final Apartment oldEntity;
        final Apartment currentEntity;
        if (id == null) {
            //create new entity
            isNew = true;
            oldEntity = null;
            currentEntity = dao.newInstance();
        } else {
            //edit existing entity
            currentEntity = dao.findById(new ApartmentExample(getLocale(), id));
            oldEntity = CloneUtil.cloneObject(currentEntity);
        }

        final FeedbackPanel messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        add(messages);

        Form form = new Form("form");

        ListView<EntityAttribute> simpleAttributes = new ListView<EntityAttribute>("simpleAttributes", currentEntity.getSimpleAttributes(entityDesc)) {

            @Override
            protected void populateItem(ListItem<EntityAttribute> item) {
                EntityAttribute attr = item.getModelObject();
                AttributeDescription desc = entityDesc.getAttributeDesc(attr.getAttributeTypeId());

                boolean isSimpleInput = false;
                boolean isDate = false;
                boolean isString = false;
                boolean isSelectable = false;
                boolean isAutoComplete = false;
                boolean isBoolean = false;

                String valueType = desc.getAttributeValueDescriptions().get(0).getValueType();

                if (valueType.equalsIgnoreCase(SimpleTypes.STRING.name())) {
                    isString = true;
                }

                String label = desc.getLocalizedAttributeName(getLocale());
                item.add(new Label("label", label));

                WebMarkupContainer required = new WebMarkupContainer("required");
                item.add(required);

                required.setVisible(isString ? false : desc.isMandatory());

                Panel stringPanel = new EmptyPanel("stringPanel");

                if (isString) {
                    IModel<List<StringCulture>> model = new PropertyModel<List<StringCulture>>(attr, "localizedValues");
                    stringPanel = new StringPanel("stringPanel", model, allLocales.get(0), label, true);
                }

                item.add(stringPanel);
            }
        };
        simpleAttributes.setReuseItems(true);
        form.add(simpleAttributes);

        Button submit = new Button("submit") {

            @Override
            public void onSubmit() {
                if (isNew) {
                    log.info("Storing new entity.");
                    dao.insert(currentEntity);
                } else {
                    log.info("Updating entity.");
                    dao.update(oldEntity, currentEntity);
                }

                setResponsePage(ApartmentList.class);
            }
        };
        form.add(submit);
        add(form);
    }
}

