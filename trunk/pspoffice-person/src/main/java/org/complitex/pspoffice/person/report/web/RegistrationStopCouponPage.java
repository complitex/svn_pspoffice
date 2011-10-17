package org.complitex.pspoffice.person.report.web;

import static com.google.common.collect.Lists.*;
import java.util.Collection;
import java.util.Locale;
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
import org.complitex.address.service.AddressRendererBean;
import org.complitex.dictionary.service.LocaleBean;
import org.complitex.dictionary.strategy.StrategyFactory;
import org.complitex.pspoffice.person.report.download.RegistrationStopCouponDownload;
import org.complitex.pspoffice.person.report.entity.RegistrationStopCoupon;
import org.complitex.pspoffice.person.report.service.RegistrationStopCouponBean;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;
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
    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private LocaleBean localeBean;
    @EJB
    private AddressRendererBean addressRendererBean;
    @EJB
    private StrategyFactory strategyFactory;

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

            Registration registration = coupon.getRegistration();
            Person person = registration.getPerson();

            final Locale systemLocale = localeBean.getSystemLocale();
            add(new Label("lastName", person.getLastName(getLocale(), systemLocale)));
            add(new Label("firstName", person.getFirstName(getLocale(), systemLocale)));
            add(new Label("middleName", person.getMiddleName(getLocale(), systemLocale)));
            add(new Label("previousNames", registrationStopCouponBean.getPreviousNames(person.getId(), getLocale())));
            add(new Label("birthCountry", person.getBirthCountry()));
            add(new Label("birthRegion", person.getBirthRegion()));
            add(new Label("birthDistrict", person.getBirthDistrict()));
            add(new Label("birthCity", person.getBirthCity()));
            add(new Label("birthDate", format(person.getBirthDate())));
            add(new Label("gender", person.getGender() != null ? RegistrationStopCouponPage.this.getString(person.getGender().name()) : ""));
            add(new Label("address", addressRendererBean.displayAddress(coupon.getAddressEntity(), coupon.getAddressId(), getLocale())));
            add(new Label("registrationOrganization", coupon.getRegistrationOrganization() != null
                    ? strategyFactory.getStrategy("organization").displayDomainObject(coupon.getRegistrationOrganization(), getLocale())
                    : ""));
            add(new Label("departureCountry", registration.getDepartureCountry()));
            add(new Label("departureRegion", registration.getDepartureRegion()));
            add(new Label("departureDistrict", registration.getDepartureDistrict()));
            add(new Label("departureCity", registration.getDepartureCity()));
            add(new Label("departureDate", format(registration.getDepartureDate())));
            add(new Label("passport", coupon.getPassportInfo()));
            add(new Label("birthCertificateInfo", coupon.getBirthCertificateInfo()));
            add(new ListView<Person>("children", person.getChildren()) {

                @Override
                protected void populateItem(ListItem<Person> item) {
                    Person child = item.getModelObject();
                    item.add(new Label("child", personStrategy.displayDomainObject(child, getLocale()) + ", "
                            + format(child.getBirthDate()) + getString("children_birth_date_suffix")));
                }
            });
            add(new Label("additionalInfo", coupon.getAdditionalInfo()));
        }
    }

    public RegistrationStopCouponPage(Registration registration, String addressEntity, long addressId) {
        add(CSSPackageResource.getHeaderContribution(WebCommonResourceInitializer.STYLE_CSS));
        add(CSSPackageResource.getHeaderContribution(RegistrationStopCouponPage.class,
                RegistrationStopCouponPage.class.getSimpleName() + ".css"));

        add(new Label("title", new ResourceModel("label")));
        Collection<FeedbackMessage> messages = newArrayList();
        RegistrationStopCoupon coupon = null;
        try {
            coupon = registrationStopCouponBean.get(registration, addressEntity, addressId);
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

