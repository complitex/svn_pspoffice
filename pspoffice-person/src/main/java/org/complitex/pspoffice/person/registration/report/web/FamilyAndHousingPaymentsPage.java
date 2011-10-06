/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.registration.report.web;

import static com.google.common.collect.Lists.*;
import java.util.Collection;
import javax.ejb.EJB;

import java.text.MessageFormat;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebPage;
import static org.apache.wicket.feedback.FeedbackMessage.*;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import static org.complitex.dictionary.util.StringUtil.*;
import org.complitex.pspoffice.person.download.FamilyAndHousingPaymentsDownload;
import org.complitex.pspoffice.person.registration.report.entity.FamilyAndHousingPayments;
import org.complitex.pspoffice.person.registration.report.entity.FamilyMember;
import org.complitex.pspoffice.person.registration.report.service.FamilyAndHousingPaymentsBean;
import org.complitex.pspoffice.person.strategy.entity.ApartmentCard;
import org.complitex.pspoffice.person.util.PersonDateFormatter;
import org.complitex.pspoffice.report.web.ReportDownloadPanel;
import org.complitex.resources.WebCommonResourceInitializer;
import org.complitex.template.web.component.toolbar.SaveButton;
import org.complitex.template.web.security.SecurityRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public final class FamilyAndHousingPaymentsPage extends WebPage {

    private static final Logger log = LoggerFactory.getLogger(FamilyAndHousingPaymentsPage.class);
    @EJB
    private FamilyAndHousingPaymentsBean familyAndHousingPaymentsBean;

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

            ListView<FamilyMember> familyMembers = new ListView<FamilyMember>("familyMembers", payments.getFamilyMembers()) {

                @Override
                protected void populateItem(ListItem<FamilyMember> item) {
                    item.add(new Label("familyMemberNumber", String.valueOf(item.getIndex() + 1)));
                    final FamilyMember member = item.getModelObject();
                    item.add(new Label("familyMemberName", member.getName()));
                    item.add(new Label("familyMemberRelation", member.getRelation()));
                    item.add(new Label("familyMemberBirthDate", PersonDateFormatter.format(member.getBirthDate())));
                    item.add(new Label("familyMemberPassport", member.getPassport()));
                }
            };
            add(familyMembers);
            add(new Label("total", new AbstractReadOnlyModel<String>() {

                @Override
                public String getObject() {
                    return MessageFormat.format(getString("total"), payments.getFamilyMembers().size());
                }
            }));

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

    public FamilyAndHousingPaymentsPage(ApartmentCard apartmentCard) {
        add(CSSPackageResource.getHeaderContribution(WebCommonResourceInitializer.STYLE_CSS));

        add(new Label("title", new ResourceModel("title")));
        Collection<FeedbackMessage> messages = newArrayList();
        FamilyAndHousingPayments payments = null;
        try {
            payments = familyAndHousingPaymentsBean.get(apartmentCard, getLocale());
        } catch (Exception e) {
            messages.add(new FeedbackMessage(this, getString("db_error"), ERROR));
            log.error("", e);
        }
        add(payments == null ? new MessagesFragment("content", messages) : new ReportFragment("content", payments));

        //Загрузка отчетов
        final ReportDownloadPanel reportDownloadPanel = new ReportDownloadPanel("report_download",
                getString("report_download"), new FamilyAndHousingPaymentsDownload(payments));
        reportDownloadPanel.setVisible(payments != null);
        add(reportDownloadPanel);

        SaveButton saveReportButton = new SaveButton("saveReportButton", true) {

            @Override
            protected void onClick(AjaxRequestTarget target) {
                reportDownloadPanel.open(target);
            }
        };
        saveReportButton.setVisible(reportDownloadPanel.isVisible());
        add(saveReportButton);
    }
}

