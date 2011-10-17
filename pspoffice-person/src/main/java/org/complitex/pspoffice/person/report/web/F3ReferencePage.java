/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.report.web;

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
import org.complitex.pspoffice.person.report.download.F3ReferenceDownload;
import static org.complitex.dictionary.util.StringUtil.*;
import org.complitex.pspoffice.person.report.entity.F3Reference;
import org.complitex.pspoffice.person.report.entity.FamilyMember;
import org.complitex.pspoffice.person.report.entity.NeighbourFamily;
import org.complitex.pspoffice.person.report.service.F3ReferenceBean;
import org.complitex.pspoffice.person.strategy.entity.ApartmentCard;
import org.complitex.pspoffice.person.strategy.entity.Registration;
import static org.complitex.pspoffice.report.util.ReportDateFormatter.format;
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
public final class F3ReferencePage extends WebPage {

    private static final Logger log = LoggerFactory.getLogger(F3ReferencePage.class);
    @EJB
    private F3ReferenceBean f3ReferenceBean;

    private class MessagesFragment extends Fragment {

        private Collection<FeedbackMessage> messages;

        public MessagesFragment(String id, Collection<FeedbackMessage> messages) {
            super(id, "messages", F3ReferencePage.this);
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

        public ReportFragment(String id, final F3Reference f3) {
            super(id, "report", F3ReferencePage.this);
            add(new Label("label", new ResourceModel("label")));
            add(new Label("name", new StringResourceModel("name", null, new Object[]{valueOf(f3.getName())})));
            add(new Label("address", new StringResourceModel("address", null, new Object[]{valueOf(f3.getAddress())})));
            add(new Label("livingArea", new StringResourceModel("livingArea", null, new Object[]{valueOf(f3.getLivingArea())})));
            add(new Label("apartmentArea", new StringResourceModel("apartmentArea", null, new Object[]{valueOf(f3.getApartmentArea())})));
            add(new Label("roomsInfo", new StringResourceModel("roomsInfo", null,
                    new Object[]{valueOf(f3.getTakesRooms()), valueOf(f3.getRooms())})));
            add(new Label("floorInfo", new StringResourceModel("floorInfo", null,
                    new Object[]{valueOf(f3.getFloor()), valueOf(f3.getFloors())})));
            add(new Label("balance", new ResourceModel("balance")));
            add(new Label("personalAccountOwner", new StringResourceModel("personalAccountOwner", null,
                    new Object[]{valueOf(f3.getPersonalAccountOwnerName())})));
            add(new Label("formOfOwnership", new StringResourceModel("formOfOwnership", null,
                    new Object[]{valueOf(f3.getFormOfOwnership())})));
            add(new Label("facilities", new StringResourceModel("facilities", null, new Object[]{valueOf(f3.getFacilities())})));
            add(new Label("technicalState", new StringResourceModel("technicalState", null, new Object[]{valueOf(f3.getTechnicalState())})));
            add(new Label("familyInfo", new StringResourceModel("familyInfo", null, new Object[]{String.valueOf(f3.getFamilyMembers().size())})));
            ListView<FamilyMember> familyMembers = new ListView<FamilyMember>("familyMembers", f3.getFamilyMembers()) {

                @Override
                protected void populateItem(ListItem<FamilyMember> item) {
                    item.add(new Label("familyMemberNumber", String.valueOf(item.getIndex() + 1)));
                    final FamilyMember member = item.getModelObject();
                    item.add(new Label("familyMemberName", member.getName()));
                    item.add(new Label("familyMemberBirthDate", format(member.getBirthDate())));
                    item.add(new Label("familyMemberRelation", member.getRelation()));
                    item.add(new Label("familyMemberRegistrationDate", format(member.getRegistrationDate())));
                }
            };
            add(familyMembers);

            add(new Label("neighboursInfo", new ResourceModel("neighboursInfo")));
            ListView<NeighbourFamily> neighbourFamilies = new ListView<NeighbourFamily>("neighbourFamilies", f3.getNeighbourFamilies()) {

                @Override
                protected void populateItem(ListItem<NeighbourFamily> item) {
                    item.add(new Label("neighbourFamilyNumber", String.valueOf(item.getIndex() + 1)));
                    final NeighbourFamily neighbourFamily = item.getModelObject();
                    item.add(new Label("neighbourFamilyName", neighbourFamily.getName()));
                    item.add(new Label("neighbourFamilyAmount", valueOf(neighbourFamily.getAmount())));
                    item.add(new Label("neighbourFamilyTakesRooms", valueOf(neighbourFamily.getTakeRooms())));
                    item.add(new Label("neighbourFamilyTakesArea", valueOf(neighbourFamily.getTakeArea())));
                }
            };
            add(neighbourFamilies);
        }
    }

    public F3ReferencePage(Registration registration, ApartmentCard apartmentCard) {
        add(CSSPackageResource.getHeaderContribution(WebCommonResourceInitializer.STYLE_CSS));

        add(new Label("title", new ResourceModel("title")));
        Collection<FeedbackMessage> messages = newArrayList();
        F3Reference f3 = null;
        try {
            f3 = f3ReferenceBean.get(registration, apartmentCard, getLocale());
        } catch (Exception e) {
            messages.add(new FeedbackMessage(this, getString("db_error"), ERROR));
            log.error("", e);
        }
        add(f3 == null ? new MessagesFragment("content", messages) : new ReportFragment("content", f3));

        //Загрузка отчетов
        final ReportDownloadPanel reportDownloadPanel = new ReportDownloadPanel("report_download", getString("report_download"),
                new F3ReferenceDownload(f3));
        reportDownloadPanel.setVisible(f3 != null);
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

