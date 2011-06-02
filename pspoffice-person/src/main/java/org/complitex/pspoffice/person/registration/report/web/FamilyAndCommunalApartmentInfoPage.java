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
import org.complitex.pspoffice.person.registration.report.entity.FamilyAndCommunalApartmentInfo;
import org.complitex.pspoffice.person.registration.report.entity.FamilyMember;
import org.complitex.pspoffice.person.registration.report.entity.NeighbourFamily;
import org.complitex.pspoffice.person.registration.report.service.FamilyAndCommunalApartmentInfoBean;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.TemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public final class FamilyAndCommunalApartmentInfoPage extends TemplatePage {

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
            final FeedbackPanel messages = new FeedbackPanel("messages");
            messages.setOutputMarkupId(true);
            add(messages);
            add(new Label("label", new ResourceModel("label")));
            add(new Label("labelDetails", new StringResourceModel("labelDetails", null, new Object[]{info.getNeighbourFamilies().size()})));
            add(new Label("addressInfo", new StringResourceModel("addressInfo", null,
                    new Object[]{info.getAddress(), info.getNeighbourFamilies().size()})));
            final Form form = new Form("form");
            add(form);

            final WebMarkupContainer neighbourFamilyConatiner = new WebMarkupContainer("neighbourFamilyConatiner");
            neighbourFamilyConatiner.setOutputMarkupId(true);
            form.add(neighbourFamilyConatiner);
            AjaxRemovableListView<NeighbourFamily> neighbourFamilies =
                    new AjaxRemovableListView<NeighbourFamily>("neighbourFamilies", info.getNeighbourFamilies()) {

                        @Override
                        protected void populateItem(ListItem<NeighbourFamily> item) {
                            final NeighbourFamily neighbourFamily = item.getModelObject();
                            item.add(new TextField<String>("neighbourFamilyRoomNumber",
                                    new PropertyModel<String>(neighbourFamily, "roomNumber")));
                            item.add(new TextField<String>("neighbourFamilyName",
                                    new PropertyModel<String>(neighbourFamily, "name")));
                            item.add(new TextField<String>("neighbourFamilyRoomsAndArea",
                                    new PropertyModel<String>(neighbourFamily, "roomsAndAreaInfo")));
                            item.add(new TextField<String>("neighbourFamilyOtherBuildingsAndArea",
                                    new PropertyModel<String>(neighbourFamily, "otherBuildingsAndAreaInfo")));
                            item.add(new TextField<String>("neighbourFamilyLoggiaPresenceAndArea",
                                    new PropertyModel<String>(neighbourFamily, "loggiaAndAreaInfo")));
                            addRemoveSubmitLink("removeNeighbourFamily", form, item, null, neighbourFamilyConatiner);
                        }
                    };
            neighbourFamilyConatiner.add(neighbourFamilies);
            form.add(new AjaxSubmitLink("addNeighbourFamily", form) {

                @Override
                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                    info.addNeighbourFamily(new NeighbourFamily());
                    target.addComponent(neighbourFamilyConatiner);
                    target.addComponent(messages);
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.addComponent(messages);
                }
            });

            form.add(new Label("apartmentInfo", new StringResourceModel("apartmentInfo", null, new Object[]{
                        valueOf(info.getKitchenArea()), valueOf(info.getBathroomArea()), valueOf(info.getToiletArea()),
                        valueOf(info.getHallArea()), valueOf(info.getOtherSpaceInfo())
                    })));
            form.add(new Label("sharedSpaceInfo", new StringResourceModel("sharedSpaceInfo", null, new Object[]{
                        valueOf(info.getSharedArea())
                    })));
            form.add(new Label("floorInfo", new StringResourceModel("floorInfo", null, new Object[]{
                        valueOf(info.getFloor()), valueOf(info.getNumberOfStoreys())
                    })));
            form.add(new Label("familyLabel", new StringResourceModel("familyLabel", null, new Object[]{
                        valueOf(info.getName())
                    })));

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
                    FamilyMember member = new FamilyMember();
                    info.addFamilyMember(member);
                    target.addComponent(familyContainer);
                    target.addComponent(messages);
                }

                @Override
                protected void onError(AjaxRequestTarget target, Form<?> form) {
                    target.addComponent(messages);
                }
            });

            add(new Label("apartmentStoreroomInfo", new StringResourceModel("apartmentStoreroomInfo", null, new Object[]{
                        valueOf(info.getStoreroomArea()), valueOf(info.getBarnArea())
                    })));
            add(new Label("otherBuildingsInfo", new StringResourceModel("otherBuildingsInfo", null, new Object[]{
                        valueOf(info.getOtherBuildings())
                    })));
            add(new Label("maintenanceInfo", new ResourceModel("maintenanceInfo")));
        }
    }

    public FamilyAndCommunalApartmentInfoPage(List<NeighbourFamily> neighbourFamilies, NeighbourFamily selectedNeighbourFamily) {
        add(new Label("title", new ResourceModel("title")));
        Collection<FeedbackMessage> messages = newArrayList();
        FamilyAndCommunalApartmentInfo info = null;
        try {
            info = familyAndCommunalApartmentInfoBean.get(neighbourFamilies, selectedNeighbourFamily, getLocale());
        } catch (Exception e) {
            messages.add(new FeedbackMessage(this, getString("db_error"), ERROR));
            log.error("", e);
        }
        add(info == null ? new MessagesFragment("content", messages) : new ReportFragment("content", info));
        add(new Link<Void>("back") {

            @Override
            public void onClick() {
                setResponsePage(FamilyAndCommunalApartmentInfoParamPage.class);
            }
        });
    }
}

