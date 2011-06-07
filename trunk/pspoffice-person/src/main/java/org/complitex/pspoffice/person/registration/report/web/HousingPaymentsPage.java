/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.registration.report.web;

import static com.google.common.collect.Lists.*;
import java.util.Collection;
import java.util.Date;
import javax.ejb.EJB;
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
import org.complitex.pspoffice.person.registration.report.entity.FamilyMember;
import org.complitex.pspoffice.person.registration.report.entity.HousingPayments;
import org.complitex.pspoffice.person.registration.report.service.HousingPaymentsBean;
import org.complitex.template.web.template.TemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
public final class HousingPaymentsPage extends TemplatePage {

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
            final FeedbackPanel messages = new FeedbackPanel("messages");
            messages.setOutputMarkupId(true);
            add(messages);
            add(new Label("label", new ResourceModel("label")));
            add(new Label("headInfo", new StringResourceModel("headInfo", null, new Object[]{
                        payments.getName(), payments.getPersonalAccount(), payments.getAddress()
                    })));
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

    public HousingPaymentsPage(String addressEntity, long addressId) {
        add(new Label("title", new ResourceModel("title")));
        Collection<FeedbackMessage> messages = newArrayList();
        HousingPayments payments = null;
        try {
            payments = housingPaymentsBean.get(addressEntity, addressId, getLocale());
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
    }
}

