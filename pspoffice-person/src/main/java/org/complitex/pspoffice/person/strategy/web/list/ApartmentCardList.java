/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.list;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.complitex.address.service.AddressRendererBean;
import org.complitex.dictionary.strategy.web.DomainObjectAccessUtil;
import org.complitex.dictionary.util.StringUtil;
import org.complitex.dictionary.web.component.datatable.DataProvider;
import org.complitex.dictionary.web.component.paging.PagingNavigator;
import org.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import org.complitex.pspoffice.person.strategy.PersonStrategy;
import org.complitex.pspoffice.person.strategy.entity.ApartmentCard;
import org.complitex.pspoffice.person.strategy.web.edit.ApartmentCardEdit;
import org.complitex.template.web.component.toolbar.AddItemButton;
import org.complitex.template.web.component.toolbar.ToolbarButton;
import org.complitex.template.web.pages.ListPage;
import org.complitex.template.web.security.SecurityRole;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.PERSON_MODULE_EDIT)
public class ApartmentCardList extends ListPage {

    @EJB
    private AddressRendererBean addressRendererBean;
    @EJB
    private ApartmentCardStrategy apartmentCardStrategy;
    @EJB
    private PersonStrategy personStrategy;
    private String addressEntity;
    private long addressId;

    public ApartmentCardList(String addressEntity, long addressId) {
        this.addressEntity = addressEntity;
        this.addressId = addressId;
        init();
    }

    private void init() {

        add(new Label("title", new ResourceModel("title")));
        add(new Label("label", new StringResourceModel("label", null, new Object[]{
                    addressRendererBean.displayAddress(addressEntity, addressId, getLocale())
                })));

        final WebMarkupContainer content = new WebMarkupContainer("content");
        content.setOutputMarkupPlaceholderTag(true);
        add(content);

        //Data Provider
        final DataProvider<ApartmentCard> dataProvider = new DataProvider<ApartmentCard>() {

            @Override
            protected Iterable<ApartmentCard> getData(int first, int count) {
                return apartmentCardStrategy.findByAddress(addressEntity, addressId, first, count);
            }

            @Override
            protected int getSize() {
                return apartmentCardStrategy.countByAddress(addressEntity, addressId);
            }
        };
        //Data View
        DataView<ApartmentCard> dataView = new DataView<ApartmentCard>("data", dataProvider, 1) {

            @Override
            protected void populateItem(Item<ApartmentCard> item) {
                final ApartmentCard apartmentCard = item.getModelObject();

                item.add(new Label("id", StringUtil.valueOf(apartmentCard.getId())));
                item.add(new Label("address", addressRendererBean.displayAddress(apartmentCard.getAddressEntity(),
                        apartmentCard.getAddressId(), getLocale())));
                item.add(new Label("owner", personStrategy.displayDomainObject(apartmentCard.getOwner(), getLocale())));
                item.add(new Label("registeredCount", String.valueOf(apartmentCard.getRegisteredCount())));
                item.add(new Label("formOfOwnership", StringUtil.valueOf(apartmentCard.getFormOfOwnership())));

                Link<Void> detailsLink = new Link<Void>("detailsLink") {

                    @Override
                    public void onClick() {
                        setResponsePage(new ApartmentCardEdit(apartmentCard));
                    }
                };
                detailsLink.add(new Label("editMessage", new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        if (DomainObjectAccessUtil.canAddNew(null, "apartment_card")) {
                            return getString("edit");
                        } else {
                            return getString("view");
                        }
                    }
                }));
                item.add(detailsLink);
            }
        };
        content.add(dataView);
        content.add(new PagingNavigator("navigator", dataView, getClass().getName(), content));
    }

    @Override
    protected List<? extends ToolbarButton> getToolbarButtons(String id) {
        return ImmutableList.of(new AddItemButton(id) {

            @Override
            protected void onClick() {
                setResponsePage(new ApartmentCardEdit(addressEntity, addressId));
            }
        });
    }
}

