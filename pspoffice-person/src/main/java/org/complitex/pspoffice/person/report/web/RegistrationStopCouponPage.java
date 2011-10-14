package org.complitex.pspoffice.person.report.web;

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
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.ResourceModel;
import org.complitex.pspoffice.person.report.download.RegistrationStopCouponDownload;
import org.complitex.pspoffice.person.report.entity.RegistrationStopCoupon;
import org.complitex.pspoffice.person.report.service.RegistrationStopCouponBean;
import org.complitex.pspoffice.person.strategy.entity.Registration;
import org.complitex.pspoffice.report.web.ReportDownloadPanel;
import org.complitex.resources.WebCommonResourceInitializer;
import org.complitex.template.web.component.toolbar.SaveButton;
import org.complitex.template.web.security.SecurityRole;
import static org.complitex.pspoffice.report.util.ReportDateFormatter.format;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class RegistrationStopCouponPage extends WebPage {

    private static final Logger log = LoggerFactory.getLogger(RegistrationStopCouponPage.class);
    @EJB
    private RegistrationStopCouponBean registrationStopCouponBean;

    private class MessagesFragment extends Fragment {

        private Collection<FeedbackMessage> messages;

        public MessagesFragment(String id, Collection<FeedbackMessage> messages) {
            super(id, "messages", RegistrationStopCouponPage.this);
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

        public ReportFragment(String id, final RegistrationStopCoupon coupon) {
            super(id, "report", RegistrationStopCouponPage.this);
            add(new Label("label", new ResourceModel("label")));
            add(new Label("lastName", coupon.getLastName()));
            add(new Label("firstName", coupon.getFirstName()));
            add(new Label("middleName", coupon.getMiddleName()));
            add(new Label("previousNames", coupon.getPreviousNames()));
            add(new Label("birthCountry", coupon.getBirthCountry()));
            add(new Label("birthRegion", coupon.getBirthRegion()));
            add(new Label("birthDistrict", coupon.getBirthDistrict()));
            add(new Label("birthCity", coupon.getBirthCity()));
            add(new Label("birthDate", format(coupon.getBirthDate())));
            add(new Label("gender", coupon.getGender()));
            add(new Label("address", coupon.getAddress()));
            add(new Label("registrationOrganization", coupon.getRegistrationOrganization()));
            add(new Label("departureCountry", coupon.getDepartureCountry()));
            add(new Label("departureRegion", coupon.getDepartureRegion()));
            add(new Label("departureDistrict", coupon.getDepartureDistrict()));
            add(new Label("departureCity", coupon.getDepartureCity()));
            add(new Label("departureDate", format(coupon.getDepartureDate())));
            add(new Label("passport", coupon.getPassport()));
            add(new Label("birthCertificateInfo", coupon.getBirthCertificateInfo()));
            add(new Label("childrenInfo", coupon.getChildrenInfo()));
            add(new Label("additionalInfo", coupon.getAdditionalInfo()));
        }
    }

    public RegistrationStopCouponPage(Registration registration, String address) {
        add(CSSPackageResource.getHeaderContribution(WebCommonResourceInitializer.STYLE_CSS));
        add(CSSPackageResource.getHeaderContribution(RegistrationStopCouponPage.class,
                RegistrationStopCouponPage.class.getSimpleName() + ".css"));

        add(new Label("title", new ResourceModel("label")));
        Collection<FeedbackMessage> messages = newArrayList();
        RegistrationStopCoupon coupon = null;
        try {
            coupon = registrationStopCouponBean.get(registration, address, getLocale());
        } catch (Exception e) {
            messages.add(new FeedbackMessage(this, getString("db_error"), ERROR));
            log.error("", e);
        }
        add(coupon == null ? new MessagesFragment("content", messages) : new ReportFragment("content", coupon));

        //Загрузка отчетов
        final ReportDownloadPanel reportDownloadPanel = new ReportDownloadPanel("report_download", getString("report_download"),
                new RegistrationStopCouponDownload(coupon));
        reportDownloadPanel.setVisible(coupon != null);
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

