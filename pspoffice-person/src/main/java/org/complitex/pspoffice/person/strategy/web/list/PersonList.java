/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.list;

import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionary.entity.PreferenceKey;
import org.complitex.dictionary.entity.description.EntityAttributeType;
import org.complitex.dictionary.entity.example.DomainObjectExample;
import org.complitex.dictionary.service.LocaleBean;
import org.complitex.dictionary.service.StringCultureBean;
import org.complitex.dictionary.strategy.web.DomainObjectAccessUtil;
import org.complitex.dictionary.util.StringUtil;
import org.complitex.dictionary.web.DictionaryFwSession;
import org.complitex.dictionary.web.component.ShowMode;
import org.complitex.dictionary.web.component.ShowModePanel;
import org.complitex.dictionary.web.component.datatable.ArrowOrderByBorder;
import org.complitex.dictionary.web.component.paging.PagingNavigator;
import org.complitex.dictionary.web.component.scroll.ScrollBookmarkablePageLink;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.template.web.component.toolbar.AddItemButton;
import org.complitex.template.web.component.toolbar.ToolbarButton;
import org.complitex.template.web.pages.ScrollListPage;
import org.complitex.template.web.security.SecurityRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public final class PersonList extends ScrollListPage {

    private static final Logger log = LoggerFactory.getLogger(PersonList.class);
    private DomainObjectExample example;
    private WebMarkupContainer content;
    private DataView<Person> dataView;
    private final String page = PersonList.class.getName();
    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private LocaleBean localeBean;
    @EJB
    private StringCultureBean stringBean;

    private class ColumnLabelModel extends AbstractReadOnlyModel<String> {

        private long attributeTypeId;

        public ColumnLabelModel(long attributeTypeId) {
            this.attributeTypeId = attributeTypeId;
        }

        @Override
        public String getObject() {
            EntityAttributeType attributeType = personStrategy.getEntity().getAttributeType(attributeTypeId);
            return stringBean.displayValue(attributeType.getAttributeNames(), getLocale());
        }
    }

    public PersonList() {
        init();
    }

    public PersonList(PageParameters params) {
        super(params);
        init();
    }

    private void init() {
        IModel<String> labelModel = new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return personStrategy.getPluralEntityLabel(getLocale());
            }
        };

        add(new Label("title", labelModel));
        add(new Label("label", labelModel));

        content = new WebMarkupContainer("content");
        content.setOutputMarkupPlaceholderTag(true);
        add(content);

        //Example
        example = (DomainObjectExample) getTemplateSession().getPreferenceObject(page, PreferenceKey.FILTER_OBJECT, null);

        if (example == null) {
            example = new DomainObjectExample();
            getTemplateSession().putPreferenceObject(page, PreferenceKey.FILTER_OBJECT, example);
        }

        //Form
        final Form filterForm = new Form("filterForm");
        content.add(filterForm);

        //Show Mode
        final IModel<ShowMode> showModeModel = new Model<ShowMode>(ShowMode.ACTIVE);
        ShowModePanel showModePanel = new ShowModePanel("showModePanel", showModeModel);
        filterForm.add(showModePanel);

        //Data Provider
        final SortableDataProvider<Person> dataProvider = new SortableDataProvider<Person>() {

            @Override
            public Iterator<Person> iterator(int first, int count) {
                boolean asc = getSort().isAscending();
                String sortProperty = getSort().getProperty();

                //store preference
                DictionaryFwSession session = getTemplateSession();
                session.putPreference(page, PreferenceKey.SORT_PROPERTY, getSort().getProperty(), true);
                session.putPreference(page, PreferenceKey.SORT_ORDER, getSort().isAscending(), true);
                session.putPreferenceObject(page, PreferenceKey.FILTER_OBJECT, example);

                if (!Strings.isEmpty(sortProperty)) {
                    example.setOrderByAttributeTypeId(Long.valueOf(sortProperty));
                }
                example.setStatus(showModeModel.getObject().name());
                example.setLocaleId(localeBean.convert(getLocale()).getId());
                example.setAsc(asc);
                example.setStart(first);
                example.setSize(count);
                return personStrategy.find(example).iterator();
            }

            @Override
            public int size() {
                example.setStatus(showModeModel.getObject().name());
                example.setLocaleId(localeBean.convert(getLocale()).getId());
                return personStrategy.count(example);
            }

            @Override
            public IModel<Person> model(Person object) {
                return new Model<Person>(object);
            }
        };
        dataProvider.setSort(getTemplateSession().getPreferenceString(page, PreferenceKey.SORT_PROPERTY, ""),
                getTemplateSession().getPreferenceBoolean(page, PreferenceKey.SORT_ORDER, true));

        //Filters
        filterForm.add(new TextField<Long>("id", new PropertyModel<Long>(example, "id")));
        filterForm.add(new TextField<String>("lastNameFilter", new Model<String>() {

            @Override
            public String getObject() {
                return (String) example.getAdditionalParam(PersonStrategy.LAST_NAME_FILTER);
            }

            @Override
            public void setObject(String number) {
                example.addAdditionalParam(PersonStrategy.LAST_NAME_FILTER, number);
            }
        }));
        filterForm.add(new TextField<String>("firstNameFilter", new Model<String>() {

            @Override
            public String getObject() {
                return (String) example.getAdditionalParam(PersonStrategy.FIRST_NAME_FILTER);
            }

            @Override
            public void setObject(String corp) {
                example.addAdditionalParam(PersonStrategy.FIRST_NAME_FILTER, corp);
            }
        }));
        filterForm.add(new TextField<String>("middleNameFilter", new Model<String>() {

            @Override
            public String getObject() {
                return (String) example.getAdditionalParam(PersonStrategy.MIDDLE_NAME_FILTER);
            }

            @Override
            public void setObject(String structure) {
                example.addAdditionalParam(PersonStrategy.MIDDLE_NAME_FILTER, structure);
            }
        }));

        //Data View
        dataView = new DataView<Person>("data", dataProvider, 1) {

            @Override
            protected void populateItem(Item<Person> item) {
                Person person = item.getModelObject();

                item.add(new Label("id", StringUtil.valueOf(person.getId())));
                item.add(new Label("lastName", person.getLastName()));
                item.add(new Label("firstName", person.getFirstName()));
                item.add(new Label("middleName", person.getMiddleName()));

                ScrollBookmarkablePageLink<WebPage> detailsLink = new ScrollBookmarkablePageLink<WebPage>("detailsLink", personStrategy.getEditPage(),
                        personStrategy.getEditPageParams(person.getId(), null, null), String.valueOf(person.getId()));
                detailsLink.add(new Label("editMessage", new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        if (DomainObjectAccessUtil.canAddNew(null, "person")) {
                            return getString("edit");
                        } else {
                            return getString("view");
                        }
                    }
                }));
                item.add(detailsLink);
            }
        };
        filterForm.add(dataView);

        filterForm.add(new ArrowOrderByBorder("lastNameHeader", String.valueOf(PersonStrategy.OrderBy.LAST_NAME.getOrderByAttributeId()), dataProvider,
                dataView, content).add(new Label("last_name", new ColumnLabelModel(PersonStrategy.LAST_NAME))));
        filterForm.add(new ArrowOrderByBorder("firstNameHeader", String.valueOf(PersonStrategy.OrderBy.FIRST_NAME.getOrderByAttributeId()), dataProvider,
                dataView, content).add(new Label("first_name", new ColumnLabelModel(PersonStrategy.FIRST_NAME))));
        filterForm.add(new ArrowOrderByBorder("middleNameHeader", String.valueOf(PersonStrategy.OrderBy.MIDDLE_NAME.getOrderByAttributeId()),
                dataProvider, dataView, content).add(new Label("middle_name", new ColumnLabelModel(PersonStrategy.MIDDLE_NAME))));

        //Reset Action
        AjaxLink reset = new AjaxLink("reset") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                filterForm.clearInput();
                example.setId(null);
                example.addAdditionalParam(PersonStrategy.LAST_NAME_FILTER, null);
                example.addAdditionalParam(PersonStrategy.FIRST_NAME_FILTER, null);
                example.addAdditionalParam(PersonStrategy.MIDDLE_NAME_FILTER, null);
                target.addComponent(content);
            }
        };
        filterForm.add(reset);

        //Submit Action
        AjaxButton submit = new AjaxButton("submit", filterForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                target.addComponent(content);
            }
        };
        filterForm.add(submit);

        //Navigator
        content.add(new PagingNavigator("navigator", dataView, getClass().getName(), content));
    }

    @Override
    protected List<? extends ToolbarButton> getToolbarButtons(String id) {
        return ImmutableList.of(new AddItemButton(id) {

            @Override
            protected void onClick() {
                setResponsePage(personStrategy.getEditPage(), personStrategy.getEditPageParams(null, null, null));
            }

            @Override
            protected void onBeforeRender() {
                if (!DomainObjectAccessUtil.canAddNew(null, "person")) {
                    setVisible(false);
                }
                super.onBeforeRender();
            }
        });
    }
}

