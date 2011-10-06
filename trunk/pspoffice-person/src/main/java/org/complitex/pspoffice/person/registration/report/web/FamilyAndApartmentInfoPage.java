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
import static org.apache.wicket.feedback.FeedbackMessage.*;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import static org.complitex.dictionary.util.StringUtil.*;
import org.complitex.pspoffice.person.download.FamilyAndApartmentInfoDownload;
import org.complitex.pspoffice.person.registration.report.entity.FamilyAndApartmentInfo;
import org.complitex.pspoffice.person.registration.report.entity.FamilyMember;
import org.complitex.pspoffice.person.registration.report.service.FamilyAndApartmentInfoBean;
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
public final class FamilyAndApartmentInfoPage extends WebPage {

    private static final Logger log = LoggerFactory.getLogger(FamilyAndApartmentInfoPage.class);
    @EJB
    private FamilyAndApartmentInfoBean familyAndApartmentInfoBean;
    private ReportDownloadPanel reportDownloadPanel;

    private class MessagesFragment extends Fragment {

        private Collection<FeedbackMessage> messages;

        public MessagesFragment(String id, Collection<FeedbackMessage> messages) {
            super(id, "messages", FamilyAndApartmentInfoPage.this);
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

        public ReportFragment(String id, final FamilyAndApartmentInfo info) {
            super(id, "report", FamilyAndApartmentInfoPage.this);
            final FeedbackPanel messages = new FeedbackPanel("messages");
            messages.setOutputMarkupId(true);
            add(messages);
            add(new Label("label", new ResourceModel("label")));
            add(new Label("labelDetails", new ResourceModel("labelDetails")));
            add(new Label("addressInfo", new StringResourceModel("addressInfo", null, new Object[]{info.getAddress()})));
            ListView<FamilyMember> familyMembers = new ListView<FamilyMember>("familyMembers", info.getFamilyMembers()) {

                @Override
                protected void populateItem(ListItem<FamilyMember> item) {
                    item.add(new Label("familyMemberNumber", String.valueOf(item.getIndex() + 1)));
                    final FamilyMember member = item.getModelObject();
                    item.add(new Label("familyMemberName", member.getName()));
                    item.add(new Label("familyMemberRelation", member.getRelation()));
                    item.add(new Label("familyMemberBirthDate", PersonDateFormatter.format(member.getBirthDate())));
                    item.add(new Label("familyMemberRegistrationDate", PersonDateFormatter.format(member.getRegistrationDate())));
                }
            };
            add(familyMembers);

            add(new Label("apartmentPartsInfo", new StringResourceModel("apartmentPartsInfo", null, new Object[]{
                        valueOf(info.getRooms()), valueOf(info.getRoomsArea()), valueOf(info.getKitchenArea()),
                        valueOf(info.getBathroomArea()), valueOf(info.getToiletArea()), valueOf(info.getHallArea()),
                        valueOf(info.getVerandaArea()), valueOf(info.getEmbeddedArae()), valueOf(info.getBalconyArea()),
                        valueOf(info.getLoggiaArea())
                    })));
            add(new Label("fullApartmentArea", new StringResourceModel("fullApartmentArea", null, new Object[]{
                        valueOf(info.getFullApartmentArea())
                    })));
            add(new Label("apartmentStoreroomInfo", new StringResourceModel("apartmentStoreroomInfo", null, new Object[]{
                        valueOf(info.getStoreroomArea()), valueOf(info.getBarnArea())
                    })));
            add(new Label("anotherBuildingsInfo", new StringResourceModel("anotherBuildingsInfo", null, new Object[]{
                        valueOf(info.getAnotherBuildingsInfo())
                    })));
            add(new Label("additionalInformation", new StringResourceModel("additionalInformation", null, new Object[]{
                        valueOf(info.getAdditionalInformation())
                    })));
            add(new Label("maintenanceInfo", new StringResourceModel("maintenanceInfo", null, new Object[]{
                        valueOf(info.getMaintenanceYear())
                    })));
        }
    }

    public FamilyAndApartmentInfoPage(ApartmentCard apartmentCard) {
        add(CSSPackageResource.getHeaderContribution(WebCommonResourceInitializer.STYLE_CSS));

        add(new Label("title", new ResourceModel("title")));
        Collection<FeedbackMessage> messages = newArrayList();
        FamilyAndApartmentInfo info = null;
        try {
            info = familyAndApartmentInfoBean.get(apartmentCard, getLocale());
        } catch (Exception e) {
            messages.add(new FeedbackMessage(this, getString("db_error"), ERROR));
            log.error("", e);
        }
        add(info == null ? new MessagesFragment("content", messages) : new ReportFragment("content", info));

        //Загрузка отчетов
        reportDownloadPanel = new ReportDownloadPanel("report_download", getString("report_download"),
                new FamilyAndApartmentInfoDownload(info));
        reportDownloadPanel.setVisible(info != null);
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

