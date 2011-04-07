/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.registration.report.web;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import javax.ejb.EJB;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.Strings;
import org.complitex.address.service.AddressRendererBean;
import org.complitex.dictionary.util.StringUtil;
import org.complitex.dictionary.web.component.datatable.ArrowOrderByBorder;
import org.complitex.dictionary.web.component.paging.PagingNavigator;
import org.complitex.pspoffice.person.registration.report.entity.RegistrationReport;
import org.complitex.pspoffice.person.registration.report.example.RegistrationReportExample;
import org.complitex.pspoffice.person.registration.report.service.RegistrationReportBean;
import org.complitex.template.web.pages.ListPage;
import org.complitex.template.web.security.SecurityRole;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public final class RegistrationReportList extends ListPage {

    private static final String DATE_FORMAT = "HH:mm:ss dd.MM.yyyy";
    @EJB
    private AddressRendererBean addressRendererBean;
    @EJB
    private RegistrationReportBean registrationReportBean;
    private RegistrationReportExample example;

    public RegistrationReportList(PageParameters params) {
        super(params);
        example = new RegistrationReportExample();
        example.setAddressId(params.getAsLong(RegistrationReportParamsPage.ADDRESS_ID));
        example.setAddressEntity(params.getString(RegistrationReportParamsPage.ADDRESS_ENTITY));
        init();
    }

    private void init() {
        add(new Label("title", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return getString("title");
            }
        }));
        add(new Label("label", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return MessageFormat.format(getString("label"),
                        addressRendererBean.displayAddress(example.getAddressEntity(), example.getAddressId(), getLocale()));
            }
        }));

        final FeedbackPanel messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        add(messages);

        final WebMarkupContainer content = new WebMarkupContainer("content");
        content.setOutputMarkupPlaceholderTag(true);
        add(content);

        //Form
        final Form filterForm = new Form("filterForm");
        content.add(filterForm);

        //Data Provider
        final SortableDataProvider<RegistrationReport> dataProvider = new SortableDataProvider<RegistrationReport>() {

            @Override
            public Iterator<RegistrationReport> iterator(int first, int count) {
                boolean asc = getSort().isAscending();
                String sortProperty = getSort().getProperty();

                if (!Strings.isEmpty(sortProperty)) {
                    example.setOrderByExpression(sortProperty);
                }
                example.setAsc(asc);
                example.setStart(first);
                example.setSize(count);
                return registrationReportBean.getReport(example).iterator();
            }

            @Override
            public int size() {
                return registrationReportBean.count(example);
            }

            @Override
            public IModel<RegistrationReport> model(RegistrationReport object) {
                return new Model<RegistrationReport>(object);
            }
        };
        dataProvider.setSort(RegistrationReportBean.OrderBy.END_DATE.getOrderByExpression(), true);

        //Filters
        filterForm.add(new TextField<String>("id", new PropertyModel<String>(example, "id")));
        filterForm.add(new TextField<String>("lastNameFilter", new PropertyModel<String>(example, "lastName")));
        filterForm.add(new TextField<String>("firstNameFilter", new PropertyModel<String>(example, "firstName")));
        filterForm.add(new TextField<String>("middleNameFilter", new PropertyModel<String>(example, "middleName")));
        filterForm.add(new RegistrationReportDateFilter("dateFilter", new PropertyModel<Integer>(example, "month"),
                new PropertyModel<Integer>(example, "year")));

        //Data View
        DataView<RegistrationReport> dataView = new DataView<RegistrationReport>("data", dataProvider, 1) {

            @Override
            protected void populateItem(Item<RegistrationReport> item) {
                RegistrationReport registrationReport = item.getModelObject();
                item.add(new Label("id", StringUtil.valueOf(registrationReport.getId())));
                item.add(new Label("lastName", registrationReport.getLastName()));
                item.add(new Label("firstName", registrationReport.getFirstName()));
                item.add(new Label("middleName", registrationReport.getMiddleName()));
                item.add(new Label("startDate", new SimpleDateFormat(DATE_FORMAT, getLocale()).format(registrationReport.getStartDate())));
                String endDateAsString = registrationReport.getEndDate() != null
                        ? new SimpleDateFormat(DATE_FORMAT, getLocale()).format(registrationReport.getEndDate())
                        : getString("null_end_date");
                item.add(new Label("endDate", endDateAsString));
            }
        };
        filterForm.add(dataView);

        filterForm.add(new ArrowOrderByBorder("lastNameHeader", RegistrationReportBean.OrderBy.LAST_NAME.getOrderByExpression(), dataProvider,
                dataView, content));
        filterForm.add(new ArrowOrderByBorder("firstNameHeader", RegistrationReportBean.OrderBy.FIRST_NAME.getOrderByExpression(), dataProvider,
                dataView, content));
        filterForm.add(new ArrowOrderByBorder("middleNameHeader", RegistrationReportBean.OrderBy.MIDDLE_NAME.getOrderByExpression(), dataProvider,
                dataView, content));
        filterForm.add(new ArrowOrderByBorder("startDateHeader", RegistrationReportBean.OrderBy.START_DATE.getOrderByExpression(), dataProvider,
                dataView, content));
        filterForm.add(new ArrowOrderByBorder("endDateHeader", RegistrationReportBean.OrderBy.END_DATE.getOrderByExpression(), dataProvider,
                dataView, content));

        //Reset Action
        AjaxLink reset = new AjaxLink("reset") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                filterForm.clearInput();
                example.clear();
                target.addComponent(messages);
                target.addComponent(content);
            }
        };
        filterForm.add(reset);

        //Submit Action
        AjaxButton submit = new AjaxButton("submit", filterForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                target.addComponent(messages);
                target.addComponent(content);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.addComponent(messages);
            }
        };
        filterForm.add(submit);

        //Navigator
        content.add(new PagingNavigator("navigator", dataView, getClass().getName(), content));

        //Change address parameter
        add(new Link("changeAddress") {

            @Override
            public void onClick() {
                setResponsePage(RegistrationReportParamsPage.class);
            }
        });
    }
}

