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
import org.complitex.pspoffice.person.registration.report.entity.F3Reference;
import org.complitex.pspoffice.person.registration.report.entity.F3Reference.FamilyMember;
import org.complitex.pspoffice.person.registration.report.entity.F3Reference.NeighbourFamily;
import org.complitex.pspoffice.person.registration.report.exception.PersonNotRegisteredException;
import org.complitex.pspoffice.person.registration.report.service.F3ReferenceBean;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.TemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public final class F3ReferencePage extends TemplatePage {

    private static final Logger log = LoggerFactory.getLogger(F3ReferencePage.class);
    @EJB
    private F3ReferenceBean f3ReferenceBean;

    public F3ReferencePage(Person person) {
        F3Reference f3 = null;
        try {
            f3 = f3ReferenceBean.getReference(person, getLocale());
        } catch (PersonNotRegisteredException e) {
            error(getString("personNotRegistered"));
        } catch (Exception e) {
            error(getString("db_error"));
            log.error("", e);
        }
        init(f3);
    }

    private void init(final F3Reference f3) {
        add(new Label("title", new ResourceModel("title")));
        WebMarkupContainer errorContainer = new WebMarkupContainer("errorContainer");
        errorContainer.setVisible(f3 == null);
        add(errorContainer);
        errorContainer.add(new FeedbackPanel("errorMessages"));

        WebMarkupContainer formContainer = new WebMarkupContainer("formContainer");
        formContainer.setVisible(f3 != null);
        add(formContainer);
        final FeedbackPanel messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        formContainer.add(messages);
        formContainer.add(new Label("label", new ResourceModel("label")));
        formContainer.add(new Label("personName", new StringResourceModel("personName", null,
                new Object[]{valueOf(f3.getPersonName())})));
        formContainer.add(new Label("personAddress", new StringResourceModel("personAddress", null,
                new Object[]{valueOf(f3.getPersonAddress())})));
        formContainer.add(new Label("personArea", new StringResourceModel("personArea", null,
                new Object[]{valueOf(f3.getPersonArea())})));
        formContainer.add(new Label("apartmentArea", new StringResourceModel("apartmentArea", null,
                new Object[]{valueOf(f3.getApartmentArea())})));
        formContainer.add(new Label("apartmentInfo", new StringResourceModel("apartmentInfo", null,
                new Object[]{valueOf(f3.getPersonRooms()), valueOf(f3.getRooms())})));
        formContainer.add(new Label("floorInfo", new StringResourceModel("floorInfo", null,
                new Object[]{valueOf(f3.getFloor()), valueOf(f3.getNumberOfStoreys())})));
        formContainer.add(new Label("balance", new ResourceModel("balance")));
        formContainer.add(new Label("privateAccountOwner", new StringResourceModel("privateAccountOwner", null,
                new Object[]{valueOf(f3.getPrivateAccountOwnerName())})));
        formContainer.add(new Label("personOwnership", new StringResourceModel("personOwnership", null,
                new Object[]{valueOf(f3.getPersonOwnership())})));
        formContainer.add(new Label("facilities", new StringResourceModel("facilities", null,
                new Object[]{valueOf(f3.getFacilities())})));
        formContainer.add(new Label("technicalState", new StringResourceModel("technicalState", null,
                new Object[]{valueOf(f3.getTechnicalState())})));
        final Form form = new Form("form");
        formContainer.add(form);
        form.add(new Label("familyInfo", new ResourceModel("familyInfo")));
        final WebMarkupContainer familyContainer = new WebMarkupContainer("familyContainer");
        familyContainer.setOutputMarkupId(true);
        form.add(familyContainer);
        AjaxRemovableListView<FamilyMember> familyMembers =
                new AjaxRemovableListView<FamilyMember>("familyMembers", f3.getFamilyMembers()) {

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
                        item.add(new TextField<String>("familyMemberLastName", new PropertyModel<String>(member, "lastName")));
                        item.add(new TextField<String>("familyMemberFirstName", new PropertyModel<String>(member, "firstName")));
                        item.add(new TextField<String>("familyMemberMiddleName", new PropertyModel<String>(member, "middleName")));
                        item.add(new Date2Panel("familyMemberBirthDate", new PropertyModel<Date>(member, "birthDate"), false,
                                new ResourceModel("familyMemberBirthDateHeader"), true));
                        item.add(new TextField<String>("familyMemberRelation", new PropertyModel<String>(member, "relation")));
                        item.add(new DatePanel("familyMemberRegistrationDate", new PropertyModel<Date>(member, "registrationDate"),
                                false, new ResourceModel("familyMemberRegistrationDateHeader"), true));
                        addRemoveSubmitLink("removeFamilyMember", form, item, null, familyContainer);
                    }
                };
        familyContainer.add(familyMembers);
        form.add(new AjaxSubmitLink("addFamilyMember", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                FamilyMember member = f3.new FamilyMember();
                f3.addFamilyMember(member);
                target.addComponent(familyContainer);
                target.addComponent(messages);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.addComponent(messages);
            }
        });

        form.add(new Label("neighboursInfo", new ResourceModel("neighboursInfo")));
        final WebMarkupContainer neighboursContainer = new WebMarkupContainer("neighboursContainer");
        neighboursContainer.setOutputMarkupId(true);
        form.add(neighboursContainer);
        AjaxRemovableListView<NeighbourFamily> neighbourFamilies =
                new AjaxRemovableListView<NeighbourFamily>("neighbourFamilies", f3.getNeighbourFamilies()) {

                    @Override
                    protected void populateItem(ListItem<NeighbourFamily> item) {
                        final Label neighbourFamilyNumber = new Label("neighbourFamilyNumber");
                        IModel<Integer> neighbourFamilyNumberModel = new AbstractReadOnlyModel<Integer>() {

                            @Override
                            public Integer getObject() {
                                return getCurrentIndex(neighbourFamilyNumber) + 1;
                            }
                        };
                        neighbourFamilyNumber.setDefaultModel(neighbourFamilyNumberModel);
                        item.add(neighbourFamilyNumber);
                        final NeighbourFamily neighbourFamily = item.getModelObject();
                        item.add(new TextField<String>("neighbourFamilyName", new PropertyModel<String>(neighbourFamily, "name")));
                        item.add(new TextField<Integer>("neighbourFamilyAmount", new PropertyModel<Integer>(neighbourFamily, "amount")));
                        item.add(new TextField<Integer>("neighbourFamilyTakesRooms", new PropertyModel<Integer>(neighbourFamily, "takeRooms")));
                        item.add(new TextField<String>("neighbourFamilyTakesArea", new PropertyModel<String>(neighbourFamily, "takeArea")));
                        addRemoveSubmitLink("removeNeighbourFamily", form, item, null, neighboursContainer);
                    }
                };
        neighboursContainer.add(neighbourFamilies);
        form.add(new AjaxSubmitLink("addNeighbourFamily", form) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                NeighbourFamily neighbourFamily = f3.new NeighbourFamily();
                f3.addNeighbourFamily(neighbourFamily);
                target.addComponent(neighboursContainer);
                target.addComponent(messages);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.addComponent(messages);
            }
        });
    }
}

