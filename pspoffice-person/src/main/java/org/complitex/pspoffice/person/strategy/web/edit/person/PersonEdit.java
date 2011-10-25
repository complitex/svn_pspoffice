/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.edit.person;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.complitex.dictionary.service.StringCultureBean;
import org.complitex.dictionary.util.CloneUtil;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.entity.PersonAgeType;
import org.complitex.pspoffice.person.strategy.web.edit.apartment_card.ApartmentCardEdit;
import org.complitex.pspoffice.person.strategy.web.edit.person.toolbar.DeathButton;
import org.complitex.template.strategy.TemplateStrategy;
import org.complitex.template.web.component.toolbar.ToolbarButton;
import org.complitex.template.web.pages.DomainObjectList;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.FormTemplatePage;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public class PersonEdit extends FormTemplatePage {

    @EJB
    private PersonStrategy personStrategy;
    @EJB
    private StringCultureBean stringBean;
    private Person oldPerson;
    private Person newPerson;
    private final boolean isBackPage;
    private long backApartmentCardId;
    private PersonDeathDialog personDeathDialog;

    public PersonEdit(PageParameters parameters) {
        this.isBackPage = false;
        Long objectId = parameters.getAsLong(TemplateStrategy.OBJECT_ID);
        if (objectId == null) {
            //create new entity
            oldPerson = null;
            newPerson = personStrategy.newInstance();

        } else {
            //edit existing entity
            newPerson = personStrategy.findById(objectId, false);
            if (newPerson == null) {
                throw new RestartResponseException(personStrategy.getObjectNotFoundPage());
            }
            oldPerson = CloneUtil.cloneObject(newPerson);
        }
        init();
    }

    /**
     * Constructor for traverses from ApartmentCardEdit. It backs to ApartmentCardEdit page after person editing.
     */
    public PersonEdit(long apartmentCardId, long personId) {
        this.isBackPage = true;
        newPerson = personStrategy.findPersonById(personId, false, true, true, true);
        oldPerson = CloneUtil.cloneObject(newPerson);
        this.backApartmentCardId = apartmentCardId;
        init();
    }

    private void init() {
        Label title = new Label("title", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return stringBean.displayValue(personStrategy.getEntity().getEntityNames(), getLocale());
            }
        });
        add(title);

        add(new PersonEditPanel("personEditPanel", PersonAgeType.ANY, oldPerson, newPerson) {

            @Override
            protected void onBack(AjaxRequestTarget target) {
                PersonEdit.this.back();
            }

            @Override
            protected void onSave(Person oldPerson, Person newPerson, AjaxRequestTarget target) {
                this.onBack(target);
            }
        });

        personDeathDialog = new PersonDeathDialog("personDeathDialog");
        personDeathDialog.setVisible(oldPerson != null && !isBackPage);
        add(personDeathDialog);
    }

    private void back() {
        if (!isBackPage) {
            PageParameters listPageParams = personStrategy.getListPageParams();
            listPageParams.put(DomainObjectList.SCROLL_PARAMETER, newPerson.getId());
            setResponsePage(personStrategy.getListPage(), listPageParams);
        } else {
            setResponsePage(new ApartmentCardEdit(backApartmentCardId));
        }
    }

    @Override
    protected List<? extends ToolbarButton> getToolbarButtons(String id) {
        return ImmutableList.of(
                new DeathButton(id) {

                    @Override
                    protected void onClick(AjaxRequestTarget target) {
                        personDeathDialog.open(target, oldPerson);
                    }

                    @Override
                    protected void onBeforeRender() {
                        super.onBeforeRender();
                        setVisible(personDeathDialog.isVisible());
                    }
                });
    }
}

