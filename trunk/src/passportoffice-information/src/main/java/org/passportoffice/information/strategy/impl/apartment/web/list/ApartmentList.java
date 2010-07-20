/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.passportoffice.information.strategy.impl.apartment.web.list;

import java.util.Iterator;
import javax.ejb.EJB;
import org.apache.wicket.PageParameters;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.complitex.dictionaryfw.entity.Entity;
import org.complitex.dictionaryfw.web.PageParameterNames;
import org.complitex.dictionaryfw.web.component.datatable.ArrowOrderByBorder;
import org.passportoffice.information.strategy.impl.apartment.dao.ApartmentDao;
import org.passportoffice.information.strategy.impl.apartment.example.ApartmentExample;
import org.passportoffice.information.strategy.impl.apartment.web.edit.ApartmentEdit;

/**
 *
 * @author Artem
 */
public final class ApartmentList extends WebPage {

    @EJB(name = "ApartmentDao")
    private ApartmentDao dao;

    public ApartmentList() {
        init();
    }

    private void init() {

        final IModel<String> nameFilterModel = new Model<String>();

        final Form filterForm = new Form("filterForm");

        TextField<String> nameFilter = new TextField<String>("nameFilter", nameFilterModel);
        filterForm.add(nameFilter);

        Link reset = new Link("reset") {

            @Override
            public void onClick() {
                filterForm.clearInput();
                nameFilterModel.setObject(null);
            }
        };
        filterForm.add(reset);

        SortableDataProvider<Entity> dataProvider = new SortableDataProvider<Entity>() {

            @Override
            public Iterator<? extends Entity> iterator(int first, int count) {
                boolean asc = getSort().isAscending();
                return dao.find(new ApartmentExample(nameFilterModel.getObject(), first, count, getLocale(), null, asc)).iterator();
            }

            @Override
            public int size() {
                return dao.count(new ApartmentExample(nameFilterModel.getObject()));
            }

            @Override
            public IModel<Entity> model(Entity object) {
                return new Model<Entity>(object);
            }
        };
        dataProvider.setSort("", true);

        final DataView<Entity> list = new DataView<Entity>("list", dataProvider, 5) {

            @Override
            protected void populateItem(Item<Entity> item) {
                Entity entity = item.getModelObject();
                item.add(new Label("displayName", entity.getDisplayName()));

                PageParameters params = new PageParameters();
                params.put(PageParameterNames.ID, entity.getId());
                item.add(new BookmarkablePageLink("edit", ApartmentEdit.class, params));
            }
        };
        filterForm.add(list);

        filterForm.add(new ArrowOrderByBorder("orderByName", "", dataProvider) {

            @Override
            protected void onSortChanged() {
                list.setCurrentPage(0);
            }
        });

        add(filterForm);

    }
}

