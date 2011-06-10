/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.registration.report.web;

import static com.google.common.collect.Lists.*;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;

import com.google.common.collect.ImmutableList;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.feedback.FeedbackMessage;
import static org.apache.wicket.feedback.FeedbackMessage.*;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import static org.complitex.dictionary.util.StringUtil.*;
import org.complitex.dictionary.web.component.list.AjaxRemovableListView;
import org.complitex.dictionary.web.component.type.Date2Panel;
import org.complitex.pspoffice.person.download.FamilyAndApartmentInfoDownload;
import org.complitex.pspoffice.person.download.FamilyAndHousingPaymentsDownload;
import org.complitex.pspoffice.person.registration.report.entity.FamilyAndHousingPayments;
import org.complitex.pspoffice.person.registration.report.entity.FamilyMember;
import org.complitex.pspoffice.person.registration.report.service.FamilyAndHousingPaymentsBean;
import org.complitex.pspoffice.report.web.ReportDownloadPanel;
import org.complitex.template.web.component.toolbar.SaveButton;
import org.complitex.template.web.component.toolbar.ToolbarButton;
import org.complitex.template.web.template.TemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
public final class FamilyAndHousingPaymentsPage extends TemplatePage {

    private static final Logger log = LoggerFactory.getLogger(FamilyAndHousingPaymentsPage.class);
    @EJB
    private FamilyAndHousingPaymentsBean familyAndHousingPaymentsBean;

    private ReportDownloadPanel reportDownloadPanel;

    private class MessagesFragment extends Fragment {

        private Collection<FeedbackMessage> messages;

        public MessagesFragment(String id, Collection<FeedbackMessage> messages) {
            super(id, "messages", FamilyAndHousingPaymentsPage.this);
            this.messages = messages;
            add(new FeedbackPanel("messages"));
        }

        @Override
        protected void onBeforeRender() {
            super.onBeforeRender();
            for (FeedbackMessage message : messages) {
                getSession().getFeedbackMessages().add(message);
            }
        }
    }

    private class ReportFragment extends Fragment {

        public ReportFragment(String id, final FamilyAndHousingPayments payments) {
            super(id, "report", FamilyAndHousingPaymentsPage.this);
            final FeedbackPanel messages = new FeedbackPanel("messages");
            messages.setOutputMarkupId(true);
            add(messages);
            add(new Label("label", new ResourceModel("label")));
            add(new Label("labelDetails", new ResourceModel("labelDetails")));
            add(new Label("nameInfo", new StringResourceModel("nameInfo", null, new Object[]{payments.getName()})));
            add(new Label("personalAccount", new StringResourceModel("personalAccount", null,
                    new Object[]{valueOf(payments.getPersonalAccount())})));
            add(new Label("addressInfo", new StringResourceModel("addressInfo", null, new Object[]{payments.getAddress()})));
            final Form form = new Form("form");
            add(form);
            final WebMarkupContainer familyContainer = new WebMarkupContainer("familyContainer");
            familyContainer.setOutputMarkupId(true);
            form.add(familyContainer);
            AjaxRemovableListView<FamilyMember> familyMembers =
                    new AjaxRemovableListView<FamilyMember>("familyMembers", payments.getFamilyMembers()) {

                        @Override
                        protected void populateItem(ListItem<FamilyMember> item) {
                            final Label familyMemberNumber = new Label("familyMemberNumber");
                            IModel<Integer> familyMemberNumberModel = new AbstractReadOnlyModel<Integer>() {

                                @Override
                                public Integer getObject() {
                                    return getCurrentIndex(familyMemberNumber) + 1;
                                }
                            };
                            familyMemberNumber.setDefaultModel(familyMemberNumberModel);
                            item.add(familyMemberNumber);
                            final FamilyMember member = item.getModelObject();
                            item.add(new TextField<String>("familyMemberName", new PropertyModel<String>(member, "name")));
                            item.add(new TextField<String>("familyMemberRelation",
                                    new PropertyModel<String>(member, "relation")));
                            item.add(new Date2Panel("familyMemberBirthDate",
                                    new PropertyModel<Date>(member, "birthDate"), false,
                                    new ResourceModel("familyMemberBirthDateHeader"), true));
                            item.add(new TextField<String>("familyMemberPassport",
                                    new PropertyModel<String>(member, "passport")));
                            addRemoveSubmitLink("removeFamilyMember", form, item, null, familyContainer);
                        }
                    };
            familyContainer.add(familyMembers);
            familyContainer.add(new Label("total", new AbstractReadOnlyModel<String>() {

                @Override
                public String getObject() {
                    return getStringFormat("total", payments.getFamilyMembers().size());
                }
            }));
            form.add(new AjaxSubmitLink("addFamilyMember", form) {

                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    FamilyMember member = new FamilyMember();
                    payments.addFamilyMember(member);
                    target.addComponent(familyContainer);
                    target.addComponent(messages);
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.addComponent(messages);
                }
            });

            add(new Label("formOfOwnership", new StringResourceModel("formOfOwnership", null, new Object[]{
                        valueOf(payments.getFormOfOwnership())
                    })));
            add(new Label("stoveType", new StringResourceModel("stoveType", null, new Object[]{
                        valueOf(payments.getStoveType())
                    })));
            add(new Label("areaInfo", new StringResourceModel("areaInfo", null, new Object[]{
                        valueOf(payments.getApartmentArea()), valueOf(payments.getHeatedArea()),
                        valueOf(payments.getNormativeArea()), valueOf(payments.getRooms())
                    })));
            add(new Label("benefits", new StringResourceModel("benefits", null, new Object[]{
                        valueOf(payments.getBenefits())
                    })));
            add(new Label("paymentsAdjustedForBenefits",
                    new StringResourceModel("paymentsAdjustedForBenefits", null, new Object[]{
                        valueOf(payments.getPaymentsAdjustedForBenefits())
                    })));
            add(new Label("paymentsInfo", new StringResourceModel("paymentsInfo", null, new Object[]{
                        valueOf(payments.getNormativePayments()), valueOf(payments.getApartmentPayments()),
                        valueOf(payments.getHeatPayments()), valueOf(payments.getGasPayments()),
                        valueOf(payments.getColdWaterPayments()), valueOf(payments.getHotWaterPayments())
                    })));
            add(new Label("debt", new StringResourceModel("debt", null, new Object[]{
                        valueOf(payments.getDebt()), valueOf(payments.getDebtMonth())
                    })));
        }
    }

    public FamilyAndHousingPaymentsPage(String addressEntity, long addressId) {
        add(new Label("title", new ResourceModel("title")));
        Collection<FeedbackMessage> messages = newArrayList();
        FamilyAndHousingPayments payments = null;
        try {
            payments = familyAndHousingPaymentsBean.get(addressEntity, addressId, getLocale());
        } catch (Exception e) {
            messages.add(new FeedbackMessage(this, getString("db_error"), ERROR));
            log.error("", e);
        }
        add(payments == null ? new MessagesFragment("content", messages) : new ReportFragment("content", payments));
        add(new Link<Void>("back") {

            @Override
            public void onClick() {
                setResponsePage(FamilyAndHousingPaymentsAddressParamPage.class);
            }
        });

        //Загрузка отчетов
        reportDownloadPanel = new ReportDownloadPanel("report_download", FamilyAndHousingPaymentsDownload.class, null,
                getString("report_download"));
        add(reportDownloadPanel);
    }

     @Override
    protected List<? extends ToolbarButton> getToolbarButtons(String id) {
        return ImmutableList.of(
                new SaveButton(id, true) {

                    @Override
                    protected void onClick(AjaxRequestTarget target) {
                        reportDownloadPanel.open(target);
                    }
                }
        );
    }
}

