/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.web.list.apartment_card.grid;

import java.util.Locale;
import javax.ejb.EJB;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.complitex.address.service.AddressRendererBean;
import org.complitex.address.strategy.apartment.ApartmentStrategy;
import org.complitex.address.strategy.room.RoomStrategy;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.service.LocaleBean;
import org.complitex.dictionary.strategy.IStrategy;
import org.complitex.dictionary.strategy.organization.IOrganizationStrategy;
import org.complitex.dictionary.util.StringUtil;
import org.complitex.dictionary.web.component.back.BackInfo;
import org.complitex.dictionary.web.component.back.BackInfoManager;
import org.complitex.dictionary.web.component.back.BookmarkableBackInfo;
import org.complitex.dictionary.web.component.datatable.DataProvider;
import org.complitex.dictionary.web.component.paging.PagingNavigator;
import org.complitex.pspoffice.person.strategy.ApartmentCardStrategy;
import org.complitex.pspoffice.person.strategy.entity.ApartmentCard;
import org.complitex.pspoffice.person.strategy.entity.Person;
import org.complitex.pspoffice.person.strategy.entity.grid.ApartmentsGridEntity;
import org.complitex.pspoffice.person.strategy.entity.grid.ApartmentsGridFilter;
import org.complitex.pspoffice.person.strategy.service.ApartmentsGridBean;
import org.complitex.pspoffice.person.strategy.web.list.apartment_card.ApartmentCardSearch;
import org.complitex.template.strategy.TemplateStrategy;
import org.complitex.template.web.pages.ListPage;
import org.complitex.template.web.security.SecurityRole;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public final class ApartmentsGrid extends ListPage {
    
    public static final String BUILDING_PARAM = "buildingId";
    private static final String PAGE_SESSION_KEY = "apartments_grid_page";
    @EJB
    private ApartmentStrategy apartmentStrategy;
    @EJB
    private RoomStrategy roomStrategy;
    @EJB
    private ApartmentsGridBean apartmentGridBean;
    @EJB(name = "OrganizationStrategy")
    private IOrganizationStrategy organizationStrategy;
    @EJB
    private ApartmentCardStrategy apartmentCardStrategy;
    @EJB
    private AddressRendererBean addressRendererBean;
    @EJB
    private LocaleBean localeBean;
    private final Locale systemLocale = localeBean.getSystemLocale();
    
    public ApartmentsGrid(PageParameters parameters) {
        final long buildingId = parameters.getAsLong(BUILDING_PARAM);
        
        IModel<String> labelModel = new StringResourceModel("label", null,
                new Object[]{addressRendererBean.displayAddress("building", buildingId, getLocale())});
        
        add(new Label("title", labelModel));
        add(new Label("label", labelModel));
        
        final WebMarkupContainer content = new WebMarkupContainer("content");
        content.setOutputMarkupPlaceholderTag(true);
        add(content);

        //Filter
        final ApartmentsGridFilter filter = apartmentGridBean.newFilter(buildingId, getLocale());

        //Form
        final Form<Void> filterForm = new Form<Void>("filterForm");
        content.add(filterForm);

        //Data Provider
        final DataProvider<ApartmentsGridEntity> dataProvider = new DataProvider<ApartmentsGridEntity>() {
            
            @Override
            protected Iterable<ApartmentsGridEntity> getData(int first, int count) {
                filter.setStart(first);
                filter.setSize(count);
                return apartmentGridBean.find(filter);
            }
            
            @Override
            protected int getSize() {
                return apartmentGridBean.count(filter);
            }
        };

        //Filters
        //number
        filterForm.add(new TextField<String>("numberFilter", new PropertyModel<String>(filter, "number")));

        //Data View
        DataView<ApartmentsGridEntity> apartments = new DataView<ApartmentsGridEntity>("apartments", dataProvider, 1) {
            
            @Override
            protected void populateItem(Item<ApartmentsGridEntity> item) {
                final ApartmentsGridEntity apartmentsGridEntity = item.getModelObject();

                //order
                item.add(new Label("order", StringUtil.valueOf(getViewOffset() + item.getIndex() + 1)));

                //apartment/room
                Link<Void> objectLink = new Link<Void>("objectLink") {
                    
                    @Override
                    public void onClick() {
                        IStrategy strategy = "apartment".equals(apartmentsGridEntity.getEntity()) ? apartmentStrategy
                                : roomStrategy;
                        PageParameters params = strategy.getEditPageParams(apartmentsGridEntity.getObjectId(), null, null);
                        BackInfoManager.put(this, PAGE_SESSION_KEY, gridBackInfo(buildingId));
                        params.put(TemplateStrategy.BACK_INFO_SESSION_KEY, PAGE_SESSION_KEY);
                        setResponsePage(strategy.getEditPage(), params);
                    }
                };
                item.add(objectLink);
                objectLink.add(new Label("number", apartmentsGridEntity.getNumber()));

                //rooms
                item.add(new ListView<DomainObject>("rooms", apartmentsGridEntity.getRooms()) {
                    
                    @Override
                    protected void populateItem(ListItem<DomainObject> item) {
                        final DomainObject room = item.getModelObject();
                        
                        Link<Void> roomLink = new Link<Void>("roomLink") {
                            
                            @Override
                            public void onClick() {
                                PageParameters params = roomStrategy.getEditPageParams(room.getId(), null, null);
                                BackInfoManager.put(this, PAGE_SESSION_KEY, gridBackInfo(buildingId));
                                params.put(TemplateStrategy.BACK_INFO_SESSION_KEY, PAGE_SESSION_KEY);
                                setResponsePage(roomStrategy.getEditPage(), params);
                            }
                        };
                        item.add(roomLink);
                        roomLink.add(new Label("room", roomStrategy.displayDomainObject(room, getLocale())));
                    }
                });

                //apartment cards
                item.add(new ListView<ApartmentCard>("apartmentCards", apartmentsGridEntity.getApartmentCards()) {
                    
                    @Override
                    protected void populateItem(ListItem<ApartmentCard> item) {
                        final ApartmentCard apartmentCard = item.getModelObject();
                        
                        Link<Void> apartmentCardLink = new Link<Void>("apartmentCardLink") {
                            
                            @Override
                            public void onClick() {
                                PageParameters params = apartmentCardStrategy.getEditPageParams(apartmentCard.getId(), null, null);
                                BackInfoManager.put(this, PAGE_SESSION_KEY, gridBackInfo(buildingId));
                                params.put(TemplateStrategy.BACK_INFO_SESSION_KEY, PAGE_SESSION_KEY);
                                setResponsePage(apartmentCardStrategy.getEditPage(), params);
                            }
                        };
                        item.add(apartmentCardLink);
                        apartmentCardLink.add(new Label("apartmentCard", apartmentCardInfo(apartmentCard, getLocale(), systemLocale)));
                    }
                });

                //registered
                item.add(new Label("registered", apartmentsGridEntity.getApartmentCards().isEmpty() ? null
                        : String.valueOf(apartmentsGridEntity.getRegistered())));

                //organizations
                item.add(new ListView<DomainObject>("organizations", apartmentsGridEntity.getOrganizations()) {
                    
                    @Override
                    protected void populateItem(ListItem<DomainObject> item) {
                        final DomainObject organization = item.getModelObject();
                        
                        Link<Void> organizationLink = new Link<Void>("organizationLink") {
                            
                            @Override
                            public void onClick() {
                                PageParameters params = organizationStrategy.getEditPageParams(organization.getId(), null, null);
                                BackInfoManager.put(this, PAGE_SESSION_KEY, gridBackInfo(buildingId));
                                params.put(TemplateStrategy.BACK_INFO_SESSION_KEY, PAGE_SESSION_KEY);
                                setResponsePage(organizationStrategy.getEditPage(), params);
                            }
                        };
                        item.add(organizationLink);
                        organizationLink.add(new Label("organizationCode",
                                organizationStrategy.getUniqueCode(organization)));
                    }
                });
            }
        };
        filterForm.add(apartments);

        //Reset Action
        AjaxLink<Void> reset = new AjaxLink<Void>("reset") {
            
            @Override
            public void onClick(AjaxRequestTarget target) {
                filterForm.clearInput();
                filter.reset();
                target.addComponent(content);
            }
        };
        filterForm.add(reset);

        //Submit Action
        AjaxButton submit = new AjaxButton("submit", filterForm) {
            
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                target.addComponent(content);
            }
        };
        filterForm.add(submit);
        
        content.add(new PagingNavigator("navigator", apartments, getPreferencesPage(), content));
        
        Link<Void> back = new Link<Void>("back") {
            
            @Override
            public void onClick() {
                setResponsePage(ApartmentCardSearch.class);
            }
        };
        content.add(back);
    }
    
    private static BackInfo gridBackInfo(long buildingId) {
        PageParameters backPageParams = new PageParameters();
        backPageParams.put(BUILDING_PARAM, buildingId);
        return new BookmarkableBackInfo(ApartmentsGrid.class, backPageParams);
    }
    
    private static String apartmentCardInfo(ApartmentCard apartmentCard, Locale locale, Locale systemLocale) {
        Person owner = apartmentCard.getOwner();
        return owner.getLastName(locale, systemLocale) + " "
                + owner.getFirstName(locale, systemLocale).substring(0, 1).toUpperCase(locale) + "."
                + owner.getMiddleName(locale, systemLocale).substring(0, 1).toUpperCase(locale) + ".";
    }
}
