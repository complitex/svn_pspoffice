package org.complitex.pspoffice.admin.web;

import org.apache.wicket.PageParameters;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.complitex.dictionaryfw.entity.Attribute;
import org.complitex.dictionaryfw.web.component.AttributeColumnsPanel;
import org.complitex.dictionaryfw.web.component.AttributeFiltersPanel;
import org.complitex.dictionaryfw.web.component.AttributeHeadersPanel;
import org.complitex.dictionaryfw.web.component.BookmarkablePageLinkPanel;
import org.complitex.dictionaryfw.web.component.datatable.ArrowOrderByBorder;
import org.complitex.pspoffice.admin.service.UserBean;
import org.complitex.pspoffice.admin.service.UserFilter;
import org.complitex.pspoffice.commons.entity.User;
import org.complitex.pspoffice.commons.web.component.toolbar.AddUserButton;
import org.complitex.pspoffice.commons.web.component.toolbar.ToolbarButton;
import org.complitex.pspoffice.commons.web.template.TemplatePage;

import javax.ejb.EJB;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 22.07.2010 15:03:45
 */
public class UserList extends TemplatePage {
    @EJB(name = "UserBean")
    private UserBean userBean;

    public UserList() {
        super();

        add(new Label("title", new ResourceModel("title")));
        add(new FeedbackPanel("messages"));

        //Фильтр
        UserFilter filterObject = userBean.newUserFilter();
        final IModel<UserFilter> filterModel = new Model<UserFilter>(filterObject);

        final Form filterForm = new Form("filter_form");
        filterForm.setOutputMarkupId(true);
        add(filterForm);

        Link filterReset = new Link("reset"){
            @Override
            public void onClick() {
                filterForm.clearInput();
            }
        };
        filterForm.add(filterReset);

        filterForm.add(new TextField<String>("login", new PropertyModel<String>(filterModel, "login")));
        filterForm.add(new AttributeFiltersPanel("user_info", filterObject.getAttributeExamples()));

        //Модель
        final SortableDataProvider<User> dataProvider = new SortableDataProvider<User>(){
            @Override
            public Iterator<? extends User> iterator(int first, int count) {
                UserFilter filter = filterModel.getObject();

                filter.setFirst(first);
                filter.setCount(count);
                try {
                    filter.setSortProperty(null);
                    filter.setSortAttributeTypeId(Long.valueOf(getSort().getProperty()));
                } catch (NumberFormatException e) {
                    filter.setSortProperty(getSort().getProperty());
                    filter.setSortAttributeTypeId(null);
                }                
                filter.setAscending(getSort().isAscending());

                return userBean.getUsers(filterModel.getObject()).iterator();
            }

            @Override
            public int size() {
                return userBean.getUsersCount(filterModel.getObject());
            }

            @Override
            public IModel<User> model(User object) {
                return new Model<User>(object);
            }
        };
        dataProvider.setSort("login", true);

        //Таблица
        DataView<User> dataView = new DataView<User>("users", dataProvider, 10){
            @Override
            protected void populateItem(Item<User> item) {
                User user = item.getModelObject();

                item.add(new Label("login", user.getLogin()));

                List<Attribute> attributeColumns = userBean.getUserInfoStrategy().getAttributeColumns(user.getUserInfo());
                item.add(new AttributeColumnsPanel("user_info", attributeColumns));

                item.add(new BookmarkablePageLinkPanel<User>("action_edit", getString("action_edit"), UserEdit.class,
                                new PageParameters("user_id=" + user.getId())));
            }
        };
        filterForm.add(dataView);

        //Названия колонок и сортировка
        filterForm.add(new ArrowOrderByBorder("header.login", "login", dataProvider, dataView, filterForm));
        filterForm.add(new AttributeHeadersPanel("header.user_info", userBean.getUserInfoStrategy().getListColumns(),
                dataProvider, dataView, filterForm));
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        return Arrays.asList((ToolbarButton) new AddUserButton(id) {

            @Override
            protected void onClick() {
                setResponsePage(UserEdit.class);
            }
        });
    }
}
