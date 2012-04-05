/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.importing.reference_data.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.util.time.Duration;
import org.complitex.address.entity.AddressImportFile;
import org.complitex.dictionary.entity.IImportFile;
import org.complitex.dictionary.entity.ImportMessage;
import org.complitex.pspoffice.departure_reason.entity.DepartureReasonImportFile;
import org.complitex.pspoffice.document_type.entity.DocumentTypeImportFile;
import org.complitex.pspoffice.housing_rights.entity.HousingRightsImportFile;
import org.complitex.pspoffice.importing.reference_data.service.ReferenceDataImportService;
import org.complitex.pspoffice.military.entity.MilitaryServiceRelationImportFile;
import org.complitex.pspoffice.ownerrelationship.entity.OwnerRelationshipImportFile;
import org.complitex.pspoffice.ownership.entity.OwnershipFormImportFile;
import org.complitex.pspoffice.registration_type.entity.RegistrationTypeImportFile;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.TemplatePage;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public final class ReferenceDataImportPage extends TemplatePage {

    @EJB
    private ReferenceDataImportService importService;
    private final IModel<List<IImportFile>> referenceDataModel;

    public ReferenceDataImportPage() {
        add(new Label("title", new ResourceModel("title")));

        final WebMarkupContainer container = new WebMarkupContainer("container");
        container.setOutputMarkupPlaceholderTag(true);
        add(container);

        referenceDataModel = new ListModel<IImportFile>();

        container.add(new FeedbackPanel("messages"));

        Form<Void> form = new Form<Void>("form");
        container.add(form);

        //Справочники
        final List<IImportFile> referenceDataList = new ArrayList<IImportFile>();
        Collections.addAll(referenceDataList, AddressImportFile.values());
        Collections.addAll(referenceDataList, OwnerRelationshipImportFile.values());
        Collections.addAll(referenceDataList, OwnershipFormImportFile.values());
        Collections.addAll(referenceDataList, RegistrationTypeImportFile.values());
        Collections.addAll(referenceDataList, DocumentTypeImportFile.values());
        Collections.addAll(referenceDataList, MilitaryServiceRelationImportFile.values());
        Collections.addAll(referenceDataList, DepartureReasonImportFile.values());
        Collections.addAll(referenceDataList, HousingRightsImportFile.values());

        form.add(new CheckBoxMultipleChoice<IImportFile>("referenceData", referenceDataModel, referenceDataList,
                new IChoiceRenderer<IImportFile>() {

                    @Override
                    public Object getDisplayValue(IImportFile importFile) {
                        return importFile.getFileName() + getStatus(importService.getReferenceDataMessage(importFile));
                    }

                    @Override
                    public String getIdValue(IImportFile importFile, int index) {
                        return importFile.name();
                    }
                }));

        //Кнопка импортировать
        Button process = new Button("process") {

            @Override
            public void onSubmit() {
                if (!importService.isProcessing()) {
                    importService.process(referenceDataModel.getObject());

                    container.add(newTimer());
                }
            }

            @Override
            public boolean isVisible() {
                return !importService.isProcessing();
            }
        };
        form.add(process);

        //Ошибки
        container.add(new Label("error", new LoadableDetachableModel<Object>() {

            @Override
            protected Object load() {
                return importService.getErrorMessage();
            }
        }) {

            @Override
            public boolean isVisible() {
                return importService.isError();
            }
        });
    }

    private AjaxSelfUpdatingTimerBehavior newTimer() {
        return new AjaxSelfUpdatingTimerBehavior(Duration.seconds(3)) {

            long stopTimer = 0;

            @Override
            protected void onPostProcessTarget(AjaxRequestTarget target) {
                if (!importService.isProcessing()) {
                    referenceDataModel.setObject(null);
                    stopTimer++;
                }

                if (stopTimer > 2) {
                    if (importService.isSuccess()) {
                        info(getString("success"));
                    }
                    stop();
                }
            }
        };
    }

    private String getStatus(ImportMessage im) {
        if (im != null) {
            if (!im.isCompleted() && !importService.isProcessing()) {
                return " - " + getStringOrKey("error");
            } else if (im.isCompleted()) {
                return " - " + getStringFormat("complete", im.getIndex());
            } else {
                return " - " + getStringFormat("processing", im.getIndex(), im.getCount());
            }
        }
        return "";
    }
}
