package org.complitex.pspoffice.imp.web;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.File;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.time.Duration;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.TemplatePage;

import javax.ejb.EJB;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.service.ConfigBean;
import org.complitex.dictionary.strategy.organization.IOrganizationStrategy;
import org.complitex.dictionary.web.component.ShowMode;
import org.complitex.dictionary.web.component.UserOrganizationPicker;
import org.complitex.dictionary.web.component.css.CssAttributeBehavior;
import org.complitex.dictionary.web.component.list.AjaxRemovableListView;
import org.complitex.dictionary.web.component.search.SearchComponentState;
import org.complitex.dictionary.web.component.search.WiQuerySearchComponent;
import org.complitex.pspoffice.imp.entity.ImportMessage;
import org.complitex.pspoffice.imp.entity.PspImportFile;
import org.complitex.pspoffice.imp.service.PspImportService;
import org.complitex.pspoffice.imp.entity.ImportStatus;
import org.complitex.pspoffice.imp.entity.ProcessItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.complitex.pspoffice.imp.entity.PspImportConfig.*;

/**
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class ImportPage extends TemplatePage {

    private static final Logger log = LoggerFactory.getLogger(ImportPage.class);
    @EJB
    private PspImportService importService;
    @EJB(name = "OrganizationStrategy")
    private IOrganizationStrategy organizationStrategy;
    @EJB
    private ConfigBean configBean;
    private int stopTimer = 0;
    private final IModel<List<ImportMessage>> messagesModel;
    private final List<DomainObject> selectedOrganizations;
    private final SearchComponentState citySearchComponentState;
    private final IModel<String> importDirectoryModel;
    private final IModel<String> errorsDirectoryModel;

    public ImportPage() {
        add(CSSPackageResource.getHeaderContribution(ImportPage.class, ImportPage.class.getSimpleName() + ".css"));

        add(new Label("title", getString("title")));

        final FeedbackPanel messages = new FeedbackPanel("messages");
        messages.setOutputMarkupId(true);
        add(messages);

        Form<Void> dataForm = new Form<Void>("dataForm");
        add(dataForm);

        importDirectoryModel = new Model<String>(configBean.getString(DEFAULT_IMPORT_FILE_DIR, true));
        dataForm.add(new TextField<String>("importDirectory", importDirectoryModel).setRequired(true));

        errorsDirectoryModel = new Model<String>(configBean.getString(DEFAULT_IMPORT_FILE_ERRORS_DIR, true));
        dataForm.add(new TextField<String>("errorsDirectory", errorsDirectoryModel).setRequired(true));

        citySearchComponentState = new SearchComponentState();
        dataForm.add(new WiQuerySearchComponent("citySearchComponent", citySearchComponentState,
                ImmutableList.of("city"), null, ShowMode.ACTIVE, true));

        final List<? extends DomainObject> allOrganizations = organizationStrategy.getAllOrganizations(getLocale());

        selectedOrganizations = Lists.newArrayList();
        selectedOrganizations.add(null);

        final WebMarkupContainer organizationsContainer = new WebMarkupContainer("organizationsContainer");
        organizationsContainer.setOutputMarkupId(true);
        dataForm.add(organizationsContainer);

        ListView<DomainObject> organizations = new AjaxRemovableListView<DomainObject>("organizations", selectedOrganizations) {

            @Override
            protected void populateItem(ListItem<DomainObject> item) {
                final WebMarkupContainer fakeContainer = new WebMarkupContainer("fakeContainer");
                item.add(fakeContainer);

                IModel<Long> organizationModel = new Model<Long>() {

                    @Override
                    public Long getObject() {
                        int index = getCurrentIndex(fakeContainer);
                        DomainObject organization = selectedOrganizations.get(index);
                        return organization != null ? organization.getId() : null;
                    }

                    @Override
                    public void setObject(final Long organizationId) {
                        int index = getCurrentIndex(fakeContainer);
                        DomainObject organization = null;
                        if (organizationId != null) {
                            organization = Iterables.find(allOrganizations, new Predicate<DomainObject>() {

                                @Override
                                public boolean apply(DomainObject input) {
                                    return organizationId.equals(input.getId());
                                }
                            });
                        }
                        selectedOrganizations.set(index, organization);
                    }
                };
                organizationModel.setObject(item.getModelObject() != null ? item.getModelObject().getId() : null);
                item.add(new UserOrganizationPicker("organizationPicker", organizationModel, true));

                addRemoveLink("removeOrganization", item, null, organizationsContainer);
            }
        };
        organizationsContainer.add(organizations);
        AjaxLink<Void> addOrganization = new AjaxLink<Void>("addOrganization") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                DomainObject newOrganization = null;
                selectedOrganizations.add(newOrganization);
                target.addComponent(organizationsContainer);
            }
        };
        dataForm.add(addOrganization);

        final WebMarkupContainer container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        add(container);

        messagesModel = new ListModel<ImportMessage>(new LinkedList<ImportMessage>());

        container.add(new ListView<ImportMessage>("messages", messagesModel) {

            @Override
            protected void populateItem(ListItem<ImportMessage> item) {
                final ImportMessage message = item.getModelObject();
                Label messageLabel = new Label("message", message.getMessage());
                messageLabel.add(new CssAttributeBehavior(message.getLevel().name()));
                item.add(messageLabel);
            }
        }.setReuseItems(true));

        container.add(new ListView<PspImportFile>("files", Lists.newArrayList(PspImportFile.values())) {

            @Override
            protected void populateItem(ListItem<PspImportFile> item) {
                final PspImportFile importFile = item.getModelObject();
                item.add(new Label("file", new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        return getString(importFile.name()) + " ("
                                + importFile.getFileName() + ") "
                                + displayLoadingStatus(importService.getLoadingStatus(importFile));
                    }
                }));
            }

            private String displayLoadingStatus(ImportStatus status) {
                if (status != null) {
                    if (status.getIndex() < 1 && !importService.isProcessing()) {
                        return " - " + getStringOrKey("error");
                    } else if (status.isFinished()) {
                        return " - " + getStringFormat("finish_loading", status.getIndex());
                    } else {
                        return " - " + getStringFormat("loading_continue", status.getIndex());
                    }
                }
                return "";
            }
        }.setReuseItems(true));

        container.add(new ListView<ProcessItem>("processingItems", Lists.newArrayList(ProcessItem.values())) {

            @Override
            protected void populateItem(ListItem<ProcessItem> item) {
                final ProcessItem processItem = item.getModelObject();
                item.add(new Label("processingItem", new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        return getString(processItem.name())
                                + displayProcessingStatus(importService.getProcessingStatus(processItem));
                    }

                    private String displayProcessingStatus(ImportStatus status) {
                        if (status != null) {
                            if (status.getIndex() < 1 && !importService.isProcessing()) {
                                return " - " + getStringOrKey("error");
                            } else if (status.isFinished()) {
                                return " - " + getStringFormat("finish_processing", status.getIndex());
                            } else {
                                return " - " + getStringFormat("processing_continue", status.getIndex());
                            }
                        }
                        return "";
                    }
                }));
            }
        }.setReuseItems(true));

        container.add(new AjaxSubmitLink("startImport", dataForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if (!importService.isProcessing()) {
                    if (ImportPage.this.validate()) {
                        Set<String> jekIds = getSelectedJekIds();
                        long cityId = citySearchComponentState.get("city").getId();

                        messagesModel.getObject().clear();
                        importService.startImport(cityId, jekIds, importDirectoryModel.getObject(),
                                errorsDirectoryModel.getObject(), getLocale());

                        container.add(newTimer());
                        target.addComponent(container);
                        target.appendJavascript("(function(){$('.import_indicator').css('visibility','visible');})()");
                    }
                    target.addComponent(messages);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                super.onError(target, form);
                target.addComponent(messages);
            }

            @Override
            public boolean isVisible() {
                return !importService.isProcessing();
            }
        });
        container.add(new IndicatingAjaxLink<Void>("cleanData") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                messagesModel.getObject().clear();
                target.addComponent(container);

                Set<String> jekIds = getSelectedJekIds();
                if (jekIds.isEmpty()) {
                    error(getString("clean_data_organization_required"));
                } else {
                    try {
                        importService.cleanData(jekIds);
                        info(getString("clean_data_successful"));
                    } catch (Exception e) {
                        log.error("", e);
                        error(getString("db_error"));
                    }
                }
                target.addComponent(messages);
            }

            @Override
            public boolean isVisible() {
                return !importService.isProcessing();
            }
        });
    }

    private Set<String> getSelectedJekIds() {
        Set<String> jekIds = Sets.newHashSet();
        for (DomainObject o : selectedOrganizations) {
            if (o != null && o.getId() != null && o.getId() > 0) {
                jekIds.add(organizationStrategy.getUniqueCode(o));
            }
        }
        return jekIds;
    }

    private boolean validateFiles() {
        boolean valid = true;
        File importDirectory = new File(importDirectoryModel.getObject());
        if (!importDirectory.exists()) {
            valid = false;
            error(getString("import_directory_does_not_exists"));
        } else if (!importDirectory.isDirectory()) {
            valid = false;
            error(getString("import_directory_is_not_directory"));
        } else {
            Set<String> notFoundFiles = Sets.newLinkedHashSet();
            Set<String> notFiles = Sets.newLinkedHashSet();

            for (PspImportFile importFile : PspImportFile.values()) {
                final File file = new File(importDirectory, importFile.getFileName());
                if (!file.exists()) {
                    notFoundFiles.add(importFile.getFileName());
                } else if (!file.isFile()) {
                    notFiles.add(importFile.getFileName());
                }
            }

            if (!notFoundFiles.isEmpty()) {
                valid = false;
                final StringBuilder files = new StringBuilder();
                boolean first = true;
                for (String fileName : notFoundFiles) {
                    files.append(!first ? ", " : "").append(fileName);
                    if (first) {
                        first = false;
                    }
                }
                error(getStringFormat("import_file_does_not_exists", files));
            }
            if (!notFiles.isEmpty()) {
                valid = false;
                final StringBuilder files = new StringBuilder();
                boolean first = true;
                for (String fileName : notFoundFiles) {
                    files.append(!first ? ", " : "").append(fileName);
                    if (first) {
                        first = false;
                    }
                }
                error(getStringFormat("import_file_is_not_file", files));
            }
        }

        File errorsDirectory = new File(errorsDirectoryModel.getObject());
        if (!errorsDirectory.exists()) {
            valid = false;
            error(getString("errors_directory_does_not_exists"));
        } else if (!errorsDirectory.isDirectory()) {
            valid = false;
            error(getString("errors_directory_is_not_directory"));
        }
        return valid;
    }

    private boolean validate() {
        boolean valid = true;

        valid = validateFiles();

        DomainObject city = citySearchComponentState.get("city");
        if (city == null || city.getId() == null || city.getId() <= 0) {
            error(getString("unselected_city"));
            valid = false;
        }

        if (selectedOrganizations.isEmpty()) {
            error(getString("organization_required"));
            valid = false;
        } else {
            for (DomainObject selectedOrganization : selectedOrganizations) {
                if (selectedOrganization == null || selectedOrganization.getId() == null
                        || selectedOrganization.getId() <= 0) {
                    error(getString("unselected_organization"));
                    valid = false;
                    break;
                }
            }
        }

        return valid;
    }

    private AjaxSelfUpdatingTimerBehavior newTimer() {
        stopTimer = 0;

        return new AjaxSelfUpdatingTimerBehavior(Duration.seconds(1)) {

            @Override
            protected void onPostProcessTarget(AjaxRequestTarget target) {
                ImportMessage importMessage = null;
                while ((importMessage = importService.getNextMessage()) != null) {
                    messagesModel.getObject().add(importMessage);
                }

                if (!importService.isProcessing()) {
                    target.appendJavascript("(function(){$('.import_indicator').css('visibility','hidden');})()");
                    stopTimer++;
                }
                if (stopTimer > 2) {
                    stop();
                }
            }
        };
    }
}
