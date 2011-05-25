/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.registration.report.web;

import java.util.Date;
import javax.ejb.EJB;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import static org.complitex.dictionary.util.StringUtil.*;
import org.complitex.dictionary.web.component.list.AjaxRemovableListView;
import org.complitex.dictionary.web.component.type.Date2Panel;
import org.complitex.dictionary.web.component.type.DatePanel;
import org.complitex.pspoffice.person.registration.report.entity.FamilyAndApartmentInfo;
import org.complitex.pspoffice.person.registration.report.entity.FamilyMember;
import org.complitex.pspoffice.person.registration.report.service.FamilyAndApartmentInfoBean;
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

    public FamilyAndApartmentInfoPage(String addressEntity, long addressId) {
        FamilyAndApartmentInfo info = null;
        try {
            info = familyAndApartmentInfoBean.getInfo(addressEntity, addressId, getLocale());
        } catch (Exception e) {
            error(getString("db_error"));
            log.error("", e);
        }
        init(info);
    }

    private void init(final FamilyAndApartmentInfo info) {
        add(new Label("title", new ResourceModel("title")));
        WebMarkupContainer emptyContainer = new WebMarkupContainer("emptyContainer");
        emptyContainer.setVisible(info.getFamilyMembers().isEmpty());
        add(emptyContainer);
        emptyContainer.add(new Label("empty", new StringResourceModel("empty", null, new Object[]{info.getAddress()})));

        WebMarkupContainer formContainer = new WebMarkupContainer("formContainer");
        formContainer.setVisible(!info.getFamilyMembers().isEmpty());
        add(formContainer);
        final FeedbackPanel messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        formContainer.add(messages);
        formContainer.add(new Label("label", new ResourceModel("label")));
        formContainer.add(new Label("labelDetails", new ResourceModel("labelDetails")));
        formContainer.add(new Label("addressInfo", new StringResourceModel("addressInfo", null, new Object[]{info.getAddress()})));
        final Form form = new Form("form");
        formContainer.add(form);
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

        formContainer.add(new Label("apartmentPartsInfo", new StringResourceModel("apartmentPartsInfo", null, new Object[]{
                    valueOf(info.getRooms()), valueOf(info.getRoomsArea()), valueOf(info.getKitchenArea()), valueOf(info.getBathroomArea()),
                    valueOf(info.getToiletArea()), valueOf(info.getHallArea()), valueOf(info.getVerandaArea()),
                    valueOf(info.getEmbeddedArae()), valueOf(info.getBalconyArea()), valueOf(info.getLoggiaArea())
                })));
        formContainer.add(new Label("fullApartmentArea", new StringResourceModel("fullApartmentArea", null, new Object[]{
                    valueOf(info.getFullApartmentArea())
                })));
        formContainer.add(new Label("apartmentStoreroomInfo", new StringResourceModel("apartmentStoreroomInfo", null, new Object[]{
                    valueOf(info.getStoreroomArea()), valueOf(info.getBarnArea())
                })));
        formContainer.add(new Label("anotherBuildingsInfo", new StringResourceModel("anotherBuildingsInfo", null, new Object[]{
                    valueOf(info.getAnotherBuildingsInfo())
                })));
        formContainer.add(new Label("additionalInformation", new StringResourceModel("additionalInformation", null, new Object[]{
                    valueOf(info.getAdditionalInformation())
                })));
        formContainer.add(new Label("maintenanceInfo", new StringResourceModel("maintenanceInfo", null, new Object[]{
                    valueOf(info.getMaintenanceYear())
                })));
        add(new Link("back") {

            @Override
            public void onClick() {
                setResponsePage(FamilyAndApartmentInfoAddressParamPage.class);
            }
        });
    }
}

