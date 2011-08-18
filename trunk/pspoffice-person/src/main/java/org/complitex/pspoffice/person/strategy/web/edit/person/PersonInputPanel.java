/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.edit.person;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import static com.google.common.collect.Iterables.*;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.description.Entity;
import org.complitex.dictionary.entity.description.EntityAttributeType;
import org.complitex.dictionary.service.StringCultureBean;
import org.complitex.dictionary.web.component.list.AjaxRemovableListView;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;

import javax.ejb.EJB;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.ResourceModel;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.web.component.DisableAwareDropDownChoice;
import org.complitex.dictionary.web.component.DomainObjectDisableAwareRenderer;
import org.complitex.dictionary.web.component.DomainObjectInputPanel;
import org.complitex.dictionary.web.component.fieldset.CollapsibleFieldset;
import org.complitex.dictionary.web.component.scroll.ScrollToElementUtil;
import org.complitex.pspoffice.document.strategy.DocumentStrategy;
import org.complitex.pspoffice.document.strategy.entity.Document;
import org.complitex.pspoffice.document_type.strategy.DocumentTypeStrategy;
import org.complitex.pspoffice.person.strategy.web.component.PersonPicker;
import org.odlabs.wiquery.ui.dialog.Dialog;
import org.odlabs.wiquery.ui.effects.CoreEffectJavaScriptResourceReference;
import org.odlabs.wiquery.ui.effects.SlideEffectJavaScriptResourceReference;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.complitex.dictionary.strategy.web.DomainObjectAccessUtil.canEdit;
import static org.complitex.dictionary.web.component.DomainObjectInputPanel.labelModel;
import static org.complitex.dictionary.web.component.DomainObjectInputPanel.newInputComponent;
import static org.complitex.pspoffice.person.strategy.PersonStrategy.*;

/**
 *
 * @author Artem
 */
public final class PersonInputPanel extends Panel {

    @EJB
    private StringCultureBean stringBean;
    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private DocumentStrategy documentStrategy;
    @EJB
    private DocumentTypeStrategy documentTypeStrategy;
    private Person person;
    private Date date;
    private FeedbackPanel messages;
    private Component scrollToComponent;
    private boolean documentReplacedFlag;

    public PersonInputPanel(String id, Person person, Date date) {
        super(id);
        this.person = person;
        this.date = date;
        init();
    }

    public PersonInputPanel(String id, Person person, FeedbackPanel messages, Component scrollToComponent) {
        super(id);
        this.person = person;
        this.messages = messages;
        this.scrollToComponent = scrollToComponent;
        init();
    }

    private boolean isNew() {
        return person.getId() == null;
    }

    private boolean isHistory() {
        return date != null;
    }

    private void init() {
        add(JavascriptPackageResource.getHeaderContribution(CoreEffectJavaScriptResourceReference.get()));
        add(JavascriptPackageResource.getHeaderContribution(SlideEffectJavaScriptResourceReference.get()));

        //full name:
        PersonFullNamePanel personFullNamePanel = new PersonFullNamePanel("personFullNamePanel",
                newNameModel(FIRST_NAME), newNameModel(MIDDLE_NAME), newNameModel(LAST_NAME));
        personFullNamePanel.setEnabled(!isHistory() && canEdit(null, personStrategy.getEntityTable(), person));
        add(personFullNamePanel);

        Entity entity = personStrategy.getEntity();

        //system attributes:
        initSystemAttributeInput(this, "identityCode", IDENTITY_CODE, true);
        initSystemAttributeInput(this, "birthDate", BIRTH_DATE, true);
        initSystemAttributeInput(this, "gender", GENDER, false);

        CollapsibleFieldset birthPlaceFieldset = new CollapsibleFieldset("birthPlaceFieldset", new ResourceModel("birthPlaceLabel"));
        birthPlaceFieldset.setVisible(isBirthPlaceFieldsetVisible());
        add(birthPlaceFieldset);
        initSystemAttributeInput(birthPlaceFieldset, "birthCountry", BIRTH_COUNTRY, false);
        initSystemAttributeInput(birthPlaceFieldset, "birthRegion", BIRTH_REGION, false);
        initSystemAttributeInput(birthPlaceFieldset, "birthDistrict", BIRTH_DISTRICT, false);
        initSystemAttributeInput(birthPlaceFieldset, "birthCity", BIRTH_CITY, false);
        initSystemAttributeInput(this, "ukraineCitizenship", UKRAINE_CITIZENSHIP, false);
        initSystemAttributeInput(this, "deathDate", DEATH_DATE, false);
        initSystemAttributeInput(this, "militaryServiceRelation", MILITARY_SERVISE_RELATION, false);

        //document
        add(initDocument());

        //user attributes:
        List<Long> userAttributeTypeIds = newArrayList(transform(filter(entity.getEntityAttributeTypes(),
                new Predicate<EntityAttributeType>() {

                    @Override
                    public boolean apply(EntityAttributeType attributeType) {
                        return !attributeType.isSystem();
                    }
                }),
                new Function<EntityAttributeType, Long>() {

                    @Override
                    public Long apply(EntityAttributeType attributeType) {
                        return attributeType.getId();
                    }
                }));

        List<Attribute> userAttributes = newArrayList();
        for (Long attributeTypeId : userAttributeTypeIds) {
            Attribute userAttribute = person.getAttribute(attributeTypeId);
            if (userAttribute != null) {
                userAttributes.add(userAttribute);
            }
        }

        ListView<Attribute> userAttributesView = new ListView<Attribute>("userAttributesView", userAttributes) {

            @Override
            protected void populateItem(ListItem<Attribute> item) {
                long userAttributeTypeId = item.getModelObject().getAttributeTypeId();
                initAttributeInput(item, userAttributeTypeId, false);
            }
        };
        add(userAttributesView);

        //children
        CollapsibleFieldset childrenFieldset = new CollapsibleFieldset("childrenFieldset",
                labelModel(entity.getAttributeType(CHILDREN).getAttributeNames(), getLocale()));
        add(childrenFieldset);
        final WebMarkupContainer childrenContainer = new WebMarkupContainer("childrenContainer");
        childrenContainer.setOutputMarkupId(true);
        childrenFieldset.add(childrenContainer);
        ListView<Person> children = new AjaxRemovableListView<Person>("children", person.getChildren()) {

            @Override
            protected void populateItem(ListItem<Person> item) {
                final WebMarkupContainer fakeContainer = new WebMarkupContainer("fakeContainer");
                item.add(fakeContainer);
                item.add(new Label("label", new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        return MessageFormat.format(getString("children_number"), getCurrentIndex(fakeContainer) + 1);
                    }
                }));

                IModel<Person> childModel = new Model<Person>() {

                    @Override
                    public Person getObject() {
                        int index = getCurrentIndex(fakeContainer);
                        return person.getChildren().get(index);
                    }

                    @Override
                    public void setObject(Person child) {
                        int index = getCurrentIndex(fakeContainer);
                        person.setChild(index, child);
                    }
                };
                childModel.setObject(item.getModelObject());

                PersonPicker personPicker = new PersonPicker("searchChildComponent", childModel, false, null,
                        !isHistory() && canEdit(null, personStrategy.getEntityTable(), person));
                item.add(personPicker);

                addRemoveLink("removeChild", item, null, childrenContainer).
                        setVisible(!isHistory() && canEdit(null, personStrategy.getEntityTable(), person));
            }
        };
        AjaxLink<Void> addChild = new AjaxLink<Void>("addChild") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                Person newChild = null;
                person.addChild(newChild);
                target.addComponent(childrenContainer);
            }
        };
        addChild.setVisible(!isHistory() && canEdit(null, personStrategy.getEntityTable(), person));
        childrenFieldset.add(addChild);
        childrenContainer.add(children);
        if (isHistory() && person.getChildren().isEmpty()) {
            childrenFieldset.setVisible(false);
        }
    }

    private IModel<Long> newNameModel(final long attributeTypeId) {
        return new Model<Long>() {

            @Override
            public Long getObject() {
                return person.getAttribute(attributeTypeId).getValueId();
            }

            @Override
            public void setObject(Long object) {
                person.getAttribute(attributeTypeId).setValueId(object);
            }
        };
    }

    private void initSystemAttributeInput(MarkupContainer parent, String id, long attributeTypeId, boolean showIfMissing) {
        WebMarkupContainer container = new WebMarkupContainer(id + "Container");
        parent.add(container);
        initAttributeInput(container, attributeTypeId, showIfMissing);
    }

    private boolean isBirthPlaceFieldsetVisible() {
        return !(isHistory() && (person.getAttribute(BIRTH_COUNTRY) == null) && (person.getAttribute(BIRTH_DISTRICT) == null)
                && (person.getAttribute(BIRTH_REGION) == null)
                && (person.getAttribute(BIRTH_CITY) == null));
    }

    private void initAttributeInput(MarkupContainer parent, long attributeTypeId, boolean showIfMissing) {
        final EntityAttributeType attributeType = personStrategy.getEntity().getAttributeType(attributeTypeId);

        //label
        parent.add(new Label("label", labelModel(attributeType.getAttributeNames(), getLocale())));

        //required container
        WebMarkupContainer requiredContainer = new WebMarkupContainer("required");
        requiredContainer.setVisible(attributeType.isMandatory());
        parent.add(requiredContainer);

        //input component
        Attribute attribute = person.getAttribute(attributeTypeId);
        if (attribute == null) {
            attribute = new Attribute();
            attribute.setLocalizedValues(stringBean.newStringCultures());
            attribute.setAttributeTypeId(attributeTypeId);
            parent.setVisible(showIfMissing);
        }
        parent.add(newInputComponent(personStrategy.getEntityTable(), null, person, attribute, getLocale(), isHistory()));
    }

    public void beforePersist() {
        //children
        person.getAttributes().removeAll(Collections2.filter(person.getAttributes(), new Predicate<Attribute>() {

            @Override
            public boolean apply(Attribute attr) {
                return attr.getAttributeTypeId().equals(CHILDREN);
            }
        }));
        long attributeId = 1;
        for (Person child : person.getChildren()) {
            Attribute childrenAttribute = new Attribute();
            childrenAttribute.setAttributeId(attributeId++);
            childrenAttribute.setAttributeTypeId(CHILDREN);
            childrenAttribute.setValueTypeId(CHILDREN);
            childrenAttribute.setValueId(child.getId());
            person.addAttribute(childrenAttribute);
        }
    }

    public boolean validate() {
        //document
        if ((!documentReplacedFlag ? person.getDocument() : person.getReplacedDocument()) == null) {
            error(getString("empty_document"));
        }

        //children
        Collection<Person> nonNullChildren = newArrayList(filter(person.getChildren(), new Predicate<Person>() {

            @Override
            public boolean apply(Person child) {
                return child != null && child.getId() != null && child.getId() > 0;
            }
        }));
        if (nonNullChildren.size() != person.getChildren().size()) {
            error(getString("children_error"));
        }

        Set<Long> childrenIds = newHashSet(transform(nonNullChildren, new Function<Person, Long>() {

            @Override
            public Long apply(Person child) {
                return child.getId();
            }
        }));

        if (!isNew()) {
            if (childrenIds.contains(person.getId())) {
                error(getString("references_themselves"));
            }
        }

        if (childrenIds.size() != nonNullChildren.size()) {
            error(getString("children_duplicate"));
        }

        return getSession().getFeedbackMessages().isEmpty();
    }

    private Component initDocument() {
        CollapsibleFieldset documentFieldset = new CollapsibleFieldset("documentFieldset",
                new ResourceModel("documentLabel"), false);

        final Form documentForm = new Form("documentForm");
        documentFieldset.add(documentForm);

        final WebMarkupContainer documentButtonsContainer = new WebMarkupContainer("documentButtonsContainer");
        documentButtonsContainer.setOutputMarkupId(true);
        documentButtonsContainer.setVisible(!isNew());
        documentFieldset.add(documentButtonsContainer);

        final WebMarkupContainer documentInputPanelContainer = new WebMarkupContainer("documentInputPanelContainer");
        documentInputPanelContainer.setOutputMarkupId(true);
        if (person.getDocument() == null) {
            documentInputPanelContainer.add(new EmptyPanel("documentInputPanel"));
        } else {
            documentInputPanelContainer.add(newDocumentInputPanel(person.getDocument()));
        }
        documentForm.add(documentInputPanelContainer);

        //document type
        final EntityAttributeType documentTypeAttributeType = documentStrategy.getEntity().getAttributeType(DocumentStrategy.DOCUMENT_TYPE);
        //label
        IModel<String> labelModel = labelModel(documentTypeAttributeType.getAttributeNames(), getLocale());
        documentForm.add(new Label("label", labelModel));
        //required
        documentForm.add(new WebMarkupContainer("required").setVisible(documentTypeAttributeType.isMandatory()));
        final List<DomainObject> allDocumentTypes = documentTypeStrategy.getAll();
        final IModel<DomainObject> documentTypeModel = new Model<DomainObject>();
        if (person.getDocument() != null) {
            documentTypeModel.setObject(find(allDocumentTypes, new Predicate<DomainObject>() {

                @Override
                public boolean apply(DomainObject documentType) {
                    return documentType.getId().equals(person.getDocument().getDocumentTypeId());
                }
            }));
        }
        final DisableAwareDropDownChoice<DomainObject> documentType = new DisableAwareDropDownChoice<DomainObject>("documentType",
                documentTypeModel, allDocumentTypes, new DomainObjectDisableAwareRenderer() {

            @Override
            public Object getDisplayValue(DomainObject object) {
                return documentTypeStrategy.displayDomainObject(object, getLocale());
            }
        });
        documentType.setOutputMarkupId(true);
        documentType.setLabel(labelModel);
        documentType.setEnabled(person.getDocument() == null && !isHistory() && canEdit(null, personStrategy.getEntityTable(), person));
        documentType.add(new AjaxFormComponentUpdatingBehavior("onchange") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                DomainObject newDocumentType = documentTypeModel.getObject();
                if (newDocumentType != null && newDocumentType.getId() != null) {
                    documentType.setEnabled(false);
                    Document document = documentStrategy.newInstance(newDocumentType.getId());
                    if (!documentReplacedFlag) {
                        person.setDocument(document);
                    } else {
                        person.setReplacedDocument(document);
                    }
                    documentInputPanelContainer.replace(newDocumentInputPanel(document));
                    target.addComponent(documentInputPanelContainer);
                    target.addComponent(documentType);
                    target.prependJavascript("$('#documentInputPanelWrapper').hide();");
                    target.appendJavascript("$('#documentInputPanelWrapper').slideDown('fast',"
                            + "function(){ $('input, textarea, select', this).filter(':enabled:not(:hidden)').first().focus(); });");
                }
            }
        });
        documentForm.add(documentType);

        //replace document
        AjaxSubmitLink replaceDocument = new AjaxSubmitLink("replaceDocument", documentForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                documentReplacedFlag = true;
                setVisible(false);
                target.addComponent(documentButtonsContainer);
                documentTypeModel.setObject(null);
                documentType.setEnabled(true);

                target.prependJavascript("$('#documentInputPanelWrapper').hide('slide', {}, 750);");
                target.focusComponent(documentType);
                target.addComponent(documentType);
                target.addComponent(messages);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.addComponent(messages);
                target.appendJavascript(ScrollToElementUtil.scrollTo(scrollToComponent.getMarkupId()));
            }
        };
        replaceDocument.setVisible(person.getDocument() != null && !isHistory() && canEdit(null, personStrategy.getEntityTable(), person));
        documentButtonsContainer.add(replaceDocument);

        //previous documents
        final Dialog previousDocumentsDialog = new Dialog("previousDocumentsDialog") {

            {
                getOptions().putLiteral("width", "auto");
            }
        };
        previousDocumentsDialog.setModal(true);
        add(previousDocumentsDialog);

        final WebMarkupContainer content = new WebMarkupContainer("content");
        content.setOutputMarkupPlaceholderTag(true);
        content.setVisible(false);
        previousDocumentsDialog.add(content);

        content.add(new Label("previousDocumentsLabel",
                new AbstractReadOnlyModel<String>() {

                    private boolean nameLoaded;

                    @Override
                    public String getObject() {
                        if (!nameLoaded) {
                            personStrategy.loadName(person);
                            nameLoaded = true;
                        }
                        return MessageFormat.format(getString("previous_documents_dialog_label"),
                                personStrategy.displayDomainObject(person, getLocale()));
                    }
                }));

        IModel<List<Document>> previousDocumentsModel = new AbstractReadOnlyModel<List<Document>>() {

            private List<Document> previousDocuments;

            @Override
            public List<Document> getObject() {
                if (previousDocuments == null) {
                    previousDocuments = personStrategy.findPreviousDocuments(person.getId());
                }
                return previousDocuments;
            }
        };

        ListView<Document> previousDocuments = new ListView<Document>("previousDocuments", previousDocumentsModel) {

            @Override
            protected void populateItem(ListItem<Document> item) {
                Document previousDocument = item.getModelObject();
                item.add(new Label("previousDocumentLabel",
                        documentTypeStrategy.displayDomainObject(previousDocument.getDocumentType(), getLocale())));
                item.add(new DomainObjectInputPanel("previousDocument", previousDocument, documentStrategy.getEntityTable(),
                        null, null, null, previousDocument.getStartDate()));
            }
        };
        content.add(previousDocuments);

        AjaxLink<Void> showPreviousDocuments = new AjaxLink<Void>("showPreviousDocuments") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                content.setVisible(true);
                target.addComponent(content);
                previousDocumentsDialog.open(target);
            }
        };
        showPreviousDocuments.setVisible(previousDocumentsModel.getObject() != null && !previousDocumentsModel.getObject().isEmpty());
        documentButtonsContainer.add(showPreviousDocuments);

        return documentFieldset;
    }

    private DomainObjectInputPanel newDocumentInputPanel(Document document) {
        return new DomainObjectInputPanel("documentInputPanel", document, documentStrategy.getEntityTable(), null, null, null);
    }
}
