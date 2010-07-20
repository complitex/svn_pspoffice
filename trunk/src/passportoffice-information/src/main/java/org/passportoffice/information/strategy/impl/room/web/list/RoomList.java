/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.passportoffice.information.strategy.impl.room.web.list;

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
import org.passportoffice.commons.entity.Entity;
import org.passportoffice.commons.web.PageParameterNames;
import org.passportoffice.commons.web.component.datatable.ArrowOrderByBorder;
import org.passportoffice.information.strategy.impl.room.dao.RoomDao;
import org.passportoffice.information.strategy.impl.room.example.RoomExample;
import org.passportoffice.information.strategy.impl.room.web.edit.RoomEdit;

/**
 *
 * @author Artem
 */
public final class RoomList extends WebPage {

    @EJB(name = "RoomDao")
    private RoomDao dao;

    public RoomList() {
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
                return dao.find(new RoomExample(nameFilterModel.getObject(), first, count, getLocale(), null, asc)).iterator();
            }

            @Override
            public int size() {
                return dao.count(new RoomExample(nameFilterModel.getObject()));
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
                item.add(new BookmarkablePageLink("edit", RoomEdit.class, params));
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

