/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.registration.report.web;

import static com.google.common.collect.Lists.*;
import java.util.Collection;
import javax.ejb.EJB;

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
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.complitex.pspoffice.person.download.HousingPaymentsDownload;
import static org.complitex.dictionary.util.StringUtil.*;
import org.complitex.pspoffice.person.registration.report.entity.FamilyMember;
import org.complitex.pspoffice.person.registration.report.entity.HousingPayments;
import org.complitex.pspoffice.person.registration.report.service.HousingPaymentsBean;
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
public final class HousingPaymentsPage extends WebPage {

    private static final Logger log = LoggerFactory.getLogger(HousingPaymentsPage.class);
    @EJB
    private HousingPaymentsBean housingPaymentsBean;

    private class MessagesFragment extends Fragment {

        private Collection<FeedbackMessage> messages;

        public MessagesFragment(String id, Collection<FeedbackMessage> messages) {
            super(id, "messages", HousingPaymentsPage.this);
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

        public ReportFragment(String id, final HousingPayments payments) {
            super(id, "report", HousingPaymentsPage.this);
            add(new Label("label", new ResourceModel("label")));
            add(new Label("headInfo", new StringResourceModel("headInfo", null, new Object[]{
                        payments.getName(), payments.getPersonalAccount(), payments.getAddress()
                    })));
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
            add(new Label("total", new StringResourceModel("total", null, new Object[]{payments.getFamilyMembers().size()})));

            add(new Label("formOfOwnership", new StringResourceModel("formOfOwnership", null, new Object[]{
                        valueOf(payments.getFormOfOwnership())
                    })));
            add(new Label("floorsInfo", new StringResourceModel("floorsInfo", null, new Object[]{
                        valueOf(payments.getFloors())
                    })));
            add(new Label("leftInfo", new StringResourceModel("leftInfo", null, new Object[]{
                        valueOf(payments.getLift())
                    })));
            add(new Label("hostelInfo", new StringResourceModel("hostelInfo", null, new Object[]{
                        valueOf(payments.getHostel())
                    })));
            add(new Label("roomsInfo", new StringResourceModel("roomsInfo", null, new Object[]{
                        valueOf(payments.getRooms())
                    })));
            add(new Label("floorInfo", new StringResourceModel("floorInfo", null, new Object[]{
                        valueOf(payments.getFloor())
                    })));
            add(new Label("stoveType", new StringResourceModel("stoveType", null, new Object[]{
                        valueOf(payments.getStoveType())
                    })));
            add(new Label("areaInfo", new StringResourceModel("areaInfo", null, new Object[]{
                        valueOf(payments.getApartmentArea()), valueOf(payments.getBalconyArea()),
                        valueOf(payments.getNormativeArea())
                    })));
            add(new Label("benefits", new StringResourceModel("benefits", null, new Object[]{
                        valueOf(payments.getBenefits()), valueOf(payments.getBenefitPersons())
                    })));
            add(new Label("paymentsAdjustedForBenefits",
                    new StringResourceModel("paymentsAdjustedForBenefits", null, new Object[]{
                        valueOf(payments.getPaymentsAdjustedForBenefits())
                    })));
            add(new Label("paymentsInfo", new StringResourceModel("paymentsInfo", null, new Object[]{
                        valueOf(payments.getNormativePayments()),
                        valueOf(payments.getApartmentPayments()), valueOf(payments.getApartmentTariff()),
                        valueOf(payments.getHeatPayments()), valueOf(payments.getHeatTariff()),
                        valueOf(payments.getGasPayments()), valueOf(payments.getGasTariff()),
                        valueOf(payments.getColdWaterPayments()), valueOf(payments.getColdWaterTariff()),
                        valueOf(payments.getHotWaterPayments()), valueOf(payments.getHotWaterTariff()),
                        valueOf(payments.getOutletPayments()), valueOf(payments.getOutletTariff()),
                        valueOf(payments.getCountersPresence())
                    })));
            add(new Label("debt", new StringResourceModel("debt", null, new Object[]{
                        valueOf(payments.getDebt()), valueOf(payments.getDebtMonth())
                    })));
        }
    }

    public HousingPaymentsPage(ApartmentCard apartmentCard) {
        add(CSSPackageResource.getHeaderContribution(WebCommonResourceInitializer.STYLE_CSS));

        add(new Label("title", new ResourceModel("title")));
        Collection<FeedbackMessage> messages = newArrayList();
        HousingPayments payments = null;
        try {
            payments = housingPaymentsBean.get(apartmentCard, getLocale());
        } catch (Exception e) {
            messages.add(new FeedbackMessage(this, getString("db_error"), ERROR));
            log.error("", e);
        }
        add(payments == null ? new MessagesFragment("content", messages) : new ReportFragment("content", payments));

        //Загрузка отчетов
        final ReportDownloadPanel reportDownloadPanel = new ReportDownloadPanel("report_download", getString("report_download"),
                new HousingPaymentsDownload(payments));
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

