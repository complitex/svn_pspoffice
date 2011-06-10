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
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
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
import org.complitex.dictionary.web.component.type.DatePanel;
import org.complitex.pspoffice.person.download.FamilyAndApartmentInfoDownload;
import org.complitex.pspoffice.person.download.RegistrationStopCouponDownload;
import org.complitex.pspoffice.person.registration.report.entity.FamilyAndApartmentInfo;
import org.complitex.pspoffice.person.registration.report.entity.FamilyMember;
import org.complitex.pspoffice.person.registration.report.exception.UnregisteredPersonException;
import org.complitex.pspoffice.person.registration.report.service.FamilyAndApartmentInfoBean;
import org.complitex.pspoffice.report.entity.FamilyAndApartmentInfoField;
import org.complitex.pspoffice.report.web.ReportDownloadPanel;
import org.complitex.template.web.component.toolbar.SaveButton;
import org.complitex.template.web.component.toolbar.ToolbarButton;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.TemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public final class FamilyAndApartmentInfoPage extends TemplatePage {

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
            final Form form = new Form("form");
            add(form);
            final WebMarkupContainer familyContainer = new WebMarkupContainer("familyContainer");
            familyContainer.setOutputMarkupId(true);
            form.add(familyContainer);
            AjaxRemovableListView<FamilyMember> familyMembers =
                    new AjaxRemovableListView<FamilyMember>("familyMembers", info.getFamilyMembers()) {

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
                            item.add(new TextField<String>("familyMemberRelation", new PropertyModel<String>(member, "relation")));
                            item.add(new Date2Panel("familyMemberBirthDate", new PropertyModel<Date>(member, "birthDate"), false,
                                    new ResourceModel("familyMemberBirthDateHeader"), true));
                            item.add(new DatePanel("familyMemberRegistrationDate", new PropertyModel<Date>(member, "registrationDate"),
                                    false, new ResourceModel("familyMemberRegistrationDateHeader"), true));
                            addRemoveSubmitLink("removeFamilyMember", form, item, null, familyContainer);
                        }
                    };
            familyContainer.add(familyMembers);
            form.add(new AjaxSubmitLink("addFamilyMember", form) {

                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    info.addFamilyMember(new FamilyMember());
                    target.addComponent(familyContainer);
                    target.addComponent(messages);
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.addComponent(messages);
                }
            });

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

    public FamilyAndApartmentInfoPage(String addressEntity, long addressId) {
        add(new Label("title", new ResourceModel("title")));
        Collection<FeedbackMessage> messages = newArrayList();
        FamilyAndApartmentInfo info = null;
        try {
            info = familyAndApartmentInfoBean.get(addressEntity, addressId, getLocale());
        } catch (UnregisteredPersonException e) {
            messages.add(new FeedbackMessage(this, getStringFormat("unregistered", e.getAddress()), INFO));
        } catch (Exception e) {
            messages.add(new FeedbackMessage(this, getString("db_error"), ERROR));
            log.error("", e);
        }
        add(info == null ? new MessagesFragment("content", messages) : new ReportFragment("content", info));
        add(new Link<Void>("back") {

            @Override
            public void onClick() {
                setResponsePage(FamilyAndApartmentInfoAddressParamPage.class);
            }
        });

         //Загрузка отчетов
        reportDownloadPanel = new ReportDownloadPanel("report_download", FamilyAndApartmentInfoDownload.class, null,
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

