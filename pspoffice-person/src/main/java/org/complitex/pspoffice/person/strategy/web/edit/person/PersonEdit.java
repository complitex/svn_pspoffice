/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.edit.person;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionary.service.StringCultureBean;
import org.complitex.dictionary.strategy.DeleteException;
import static org.complitex.dictionary.strategy.web.DomainObjectAccessUtil.*;
import org.complitex.dictionary.util.CloneUtil;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.resources.WebCommonResourceInitializer;
import org.complitex.template.strategy.TemplateStrategy;
import org.complitex.template.web.component.toolbar.DeleteItemButton;
import org.complitex.template.web.component.toolbar.ToolbarButton;
import org.complitex.template.web.pages.DomainObjectList;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.FormTemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.PERSON_MODULE_EDIT)
public class PersonEdit extends FormTemplatePage {

    private static final Logger log = LoggerFactory.getLogger(PersonEdit.class);
    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private StringCultureBean stringBean;
    private Person oldPerson;
    private Person newPerson;

    public PersonEdit(PageParameters parameters) {
        Long objectId = parameters.getAsLong(TemplateStrategy.OBJECT_ID);
        if (objectId == null) {
            //create new entity
            oldPerson = null;
            newPerson = personStrategy.newInstance();

        } else {
            //edit existing entity
            newPerson = personStrategy.findById(objectId, false);
            oldPerson = CloneUtil.cloneObject(newPerson);
        }

        add(JavascriptPackageResource.getHeaderContribution(WebCommonResourceInitializer.SCROLL_JS));

        Label title = new Label("title", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return stringBean.displayValue(personStrategy.getEntity().getEntityNames(), getLocale());
            }
        });
        add(title);

        add(new PersonEditPanel("personEditPanel", oldPerson, newPerson) {

            @Override
            protected void onBack(AjaxRequestTarget target) {
                PersonEdit.this.back();
            }

            @Override
            protected void onSave(Person oldPerson, Person newPerson, AjaxRequestTarget target) {
                this.onBack(target);
            }
        });
    }

    private void back() {
        PageParameters listPageParams = personStrategy.getListPageParams();
        listPageParams.put(DomainObjectList.SCROLL_PARAMETER, newPerson.getId());
        setResponsePage(personStrategy.getListPage(), listPageParams);
    }

    private void delete() {
        try {
            personStrategy.delete(newPerson.getId(), getLocale());
            back();
        } catch (DeleteException e) {
            if (!Strings.isEmpty(e.getMessage())) {
                error(e.getMessage());
            } else {
                error(getString("delete_error"));
            }
        } catch (Exception e) {
            log.error("", e);
            error(getString("db_error"));
        }
    }

    @Override
    protected List<? extends ToolbarButton> getToolbarButtons(String id) {
        return ImmutableList.of(
                //                new F3ReferenceButton(id) {
                //
                //                    @Override
                //                    protected void onClick() {
                //                        setResponsePage(new F3ReferencePage(newPerson));
                //                    }
                //
                //                    @Override
                //                    protected void onBeforeRender() {
                //                        if (isNew() || newPerson.getRegistration() == null) {
                //                            setVisibilityAllowed(false);
                //                        }
                //                        super.onBeforeRender();
                //                    }
                //                },
                new DeleteItemButton(id) {

            @Override
            protected void onClick() {
                delete();
            }

            @Override
            protected void onBeforeRender() {
                if (!canDelete(null, "person", newPerson)) {
                    setVisibilityAllowed(false);
                }
                super.onBeforeRender();
            }
        } //                new SaveButton(id, true) {
                //
                //                    @Override
                //                    protected void onClick(AjaxRequestTarget target) {
                //                        reportDownloadPanel.open(target);
                //                    }
                //
                //                    @Override
                //                    public boolean isVisible() {
                //                        return !isNew();
                //                    }
                //                }
                );
    }
}

