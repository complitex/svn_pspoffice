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
import org.complitex.pspoffice.person.download.FamilyAndCommunalApartmentInfoDownload;
import static org.complitex.dictionary.util.StringUtil.*;
import org.complitex.pspoffice.person.registration.report.entity.FamilyAndCommunalApartmentInfo;
import org.complitex.pspoffice.person.registration.report.entity.FamilyMember;
import org.complitex.pspoffice.person.registration.report.entity.NeighbourFamily;
import org.complitex.pspoffice.person.registration.report.service.FamilyAndCommunalApartmentInfoBean;
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
public final class FamilyAndCommunalApartmentInfoPage extends WebPage {

    private static final Logger log = LoggerFactory.getLogger(FamilyAndCommunalApartmentInfoPage.class);
    @EJB
    private FamilyAndCommunalApartmentInfoBean familyAndCommunalApartmentInfoBean;

    private class MessagesFragment extends Fragment {

        private Collection<FeedbackMessage> messages;

        public MessagesFragment(String id, Collection<FeedbackMessage> messages) {
            super(id, "messages", FamilyAndCommunalApartmentInfoPage.this);
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

        public ReportFragment(String id, final FamilyAndCommunalApartmentInfo info) {
            super(id, "report", FamilyAndCommunalApartmentInfoPage.this);
            add(new Label("label", new ResourceModel("label")));
            add(new Label("labelDetails", new StringResourceModel("labelDetails", null,
                    new Object[]{info.getNeighbourFamilies().size()})));
            add(new Label("addressInfo", new StringResourceModel("addressInfo", null,
                    new Object[]{info.getAddress(), info.getNeighbourFamilies().size()})));

            ListView<NeighbourFamily> neighbourFamilies = new ListView<NeighbourFamily>("neighbourFamilies",
                    info.getNeighbourFamilies()) {

                @Override
                protected void populateItem(ListItem<NeighbourFamily> item) {
                    final NeighbourFamily neighbourFamily = item.getModelObject();
                    item.add(new Label("neighbourFamilyRoomNumber", neighbourFamily.getRoomNumber()));
                    item.add(new Label("neighbourFamilyName", neighbourFamily.getName()));
                    item.add(new Label("neighbourFamilyRoomsAndArea", neighbourFamily.getRoomsAndAreaInfo()));
                    item.add(new Label("neighbourFamilyOtherBuildingsAndArea", neighbourFamily.getOtherBuildingsAndAreaInfo()));
                    item.add(new Label("neighbourFamilyLoggiaPresenceAndArea", neighbourFamily.getLoggiaAndAreaInfo()));
                }
            };
            add(neighbourFamilies);

            add(new Label("apartmentInfo", new StringResourceModel("apartmentInfo", null, new Object[]{
                        valueOf(info.getKitchenArea()), valueOf(info.getBathroomArea()), valueOf(info.getToiletArea()),
                        valueOf(info.getHallArea()), valueOf(info.getOtherSpaceInfo())
                    })));
            add(new Label("sharedSpaceInfo", new StringResourceModel("sharedSpaceInfo", null, new Object[]{
                        valueOf(info.getSharedArea())
                    })));
            add(new Label("floorInfo", new StringResourceModel("floorInfo", null, new Object[]{
                        valueOf(info.getFloor()), valueOf(info.getNumberOfStoreys())
                    })));
            add(new Label("familyLabel", new StringResourceModel("familyLabel", null, new Object[]{
                        valueOf(info.getName())
                    })));

            ListView<FamilyMember> familyMembers = new ListView<FamilyMember>("familyMembers", info.getFamilyMembers()) {

                @Override
                protected void populateItem(ListItem<FamilyMember> item) {
                    item.add(new Label("familyMemberNumber", String.valueOf(item.getIndex() + 1)));
                    final FamilyMember member = item.getModelObject();
                    item.add(new Label("familyMemberName", member.getName()));
                    item.add(new Label("familyMemberRelation", member.getRelation()));
                    item.add(new Label("familyMemberBirthDate", PersonDateFormatter.format(member.getBirthDate())));
                    item.add(new Label("familyMemberRegistrationDate",
                            PersonDateFormatter.format(member.getRegistrationDate())));
                }
            };
            add(familyMembers);

            add(new Label("apartmentStoreroomInfo", new StringResourceModel("apartmentStoreroomInfo", null, new Object[]{
                        valueOf(info.getStoreroomArea()), valueOf(info.getBarnArea())
                    })));
            add(new Label("otherBuildingsInfo", new StringResourceModel("otherBuildingsInfo", null, new Object[]{
                        valueOf(info.getOtherBuildings())
                    })));
            add(new Label("maintenanceInfo", new ResourceModel("maintenanceInfo")));
        }
    }

    public FamilyAndCommunalApartmentInfoPage(ApartmentCard apartmentCard) {
        add(CSSPackageResource.getHeaderContribution(WebCommonResourceInitializer.STYLE_CSS));

        add(new Label("title", new ResourceModel("title")));
        Collection<FeedbackMessage> messages = newArrayList();
        FamilyAndCommunalApartmentInfo info = null;
        try {
            info = familyAndCommunalApartmentInfoBean.get(apartmentCard, getLocale());
        } catch (Exception e) {
            messages.add(new FeedbackMessage(this, getString("db_error"), ERROR));
            log.error("", e);
        }
        add(info == null ? new MessagesFragment("content", messages) : new ReportFragment("content", info));

        //Загрузка отчетов
        final ReportDownloadPanel reportDownloadPanel = new ReportDownloadPanel("report_download", getString("report_download"),
                new FamilyAndCommunalApartmentInfoDownload(info));
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

