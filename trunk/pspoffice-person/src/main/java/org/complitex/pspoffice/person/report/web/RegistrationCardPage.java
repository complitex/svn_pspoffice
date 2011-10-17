/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.report.web;

import static com.google.common.collect.Lists.*;
import static org.apache.wicket.feedback.FeedbackMessage.*;
import java.util.Collection;
import javax.ejb.EJB;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.complitex.pspoffice.person.report.download.RegistrationCardDownload;
import org.complitex.pspoffice.person.report.entity.RegistrationCard;
import org.complitex.pspoffice.person.report.service.RegistrationCardBean;
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
public class RegistrationCardPage extends WebPage {

    private static final Logger log = LoggerFactory.getLogger(RegistrationCardPage.class);
    @EJB
    private RegistrationCardBean registrationCardBean;

    private class MessagesFragment extends Fragment {

        private Collection<FeedbackMessage> messages;

        public MessagesFragment(String id, Collection<FeedbackMessage> messages) {
            super(id, "messages", RegistrationCardPage.this);
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

        public ReportFragment(String id, final RegistrationCard card) {
            super(id, "report", RegistrationCardPage.this);
            add(new Label("label", new StringResourceModel("label", null,
                    new Object[]{card.getLastName() + " " + card.getFirstName() + " " + card.getMiddleName()})));

            add(new Label("lastName", card.getLastName()));
            add(new Label("firstName", card.getFirstName()));
            add(new Label("middleName", card.getMiddleName()));
            add(new Label("nationality", card.getNationality()));
            add(new Label("birthDate", format(card.getBirthDate())));
            add(new Label("birthRegion", card.getBirthRegion()));
            add(new Label("birthDistrict", card.getBirthDistrict()));
            add(new Label("birthCity", card.getBirthCity()));
            add(new Label("arrivalRegion", card.getArrivalRegion()));
            add(new Label("arrivalStreet", card.getArrivalStreet()));
            add(new Label("arrivalDistrict", card.getArrivalDistrict()));
            add(new Label("arrivalBuilding", card.getArrivalBuilding()));
            add(new Label("arrivalCorp", card.getArrivalCorp()));
            add(new Label("arrivalApartment", card.getArrivalApartment()));
            add(new Label("arrivalCity", card.getArrivalCity()));
            add(new Label("arrivalDate", card.getArrivalDate() != null ? format(card.getArrivalDate()) : null));
            add(new Label("passportSeries", card.getPassportSeries()));
            add(new Label("passportNumber", card.getPassportNumber()));
            add(new Label("passportIssued", card.getPassportIssued()));
            add(new Label("address", card.getAddress()));
            add(new Label("child0", card.getChild0()));
            add(new Label("child1", card.getChild1()));
            add(new Label("child2", card.getChild2()));
            add(new Label("military", card.getMilitary()));
            add(new Label("registrationDate",
                    card.getRegistrationDate() != null ? format(card.getRegistrationDate()) : null));
            add(new Label("registrationType", card.getRegistrationType()));
            add(new Label("departureRegion", card.getDepartureRegion()));
            add(new Label("departureDistrict", card.getDepartureDistrict()));
            add(new Label("departureCity", card.getDepartureCity()));
            add(new Label("departureDate", card.getDepartureDate() != null ? format(card.getDepartureDate()) : null));
            add(new Label("departureReason", card.getDepartureReason()));
        }
    }

    public RegistrationCardPage(Registration registration, String address) {
        add(CSSPackageResource.getHeaderContribution(WebCommonResourceInitializer.STYLE_CSS));
        add(CSSPackageResource.getHeaderContribution(RegistrationCardPage.class, RegistrationCardPage.class.getSimpleName() + ".css"));

        add(new Label("title", new ResourceModel("title")));
        Collection<FeedbackMessage> messages = newArrayList();
        RegistrationCard card = null;
        try {
            card = registrationCardBean.get(registration, address, getLocale());
        } catch (Exception e) {
            messages.add(new FeedbackMessage(this, getString("db_error"), ERROR));
            log.error("", e);
        }
        add(card == null ? new MessagesFragment("content", messages) : new ReportFragment("content", card));

        //Загрузка отчетов
        final ReportDownloadPanel reportDownloadPanel = new ReportDownloadPanel("report_download", getString("report_download"),
                new RegistrationCardDownload(card));
        reportDownloadPanel.setVisible(card != null);
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

