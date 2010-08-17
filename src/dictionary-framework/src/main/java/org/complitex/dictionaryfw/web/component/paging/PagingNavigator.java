package org.complitex.dictionaryfw.web.component.paging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigationIncrementLink;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigationLink;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
public class PagingNavigator extends Panel {

    private static final Logger log = LoggerFactory.getLogger(PagingNavigator.class);

    private static final int LEFT_OFFSET = 3;

    private static final int RIGHT_OFFSET = 3;

    private static final List<Integer> SUPPORTED_PAGE_SIZES = Arrays.asList(10, 20, 30, 50, 100);

    private static final String PAGE_SIZE_PREFERENCE_KEY = "PageSize";

    private DataView dataView;

    private WebMarkupContainer pageBar;

    private Form newPageForm;

    private WebMarkupContainer allPagesRegion;

    private PropertyModel<Integer> rowsPerPagePropertyModel;

    private String pageNumberPreferenceKey;

    private MarkupContainer[] toUpdate;

    public PagingNavigator(String id, final DataView dataView, MarkupContainer... toUpdate) {
        super(id);
        setOutputMarkupId(true);

        this.dataView = dataView;
        this.toUpdate = toUpdate;

        rowsPerPagePropertyModel = new PropertyModel<Integer>(dataView, "itemsPerPage");

        //retrieve table page size from preferences.
        Integer rowsPerPage = null; //preferences.getPreference(UIPreferences.PreferenceType.PAGE_SIZE, PAGE_SIZE_PREFERENCE_KEY, Integer.class);
        if (rowsPerPage == null) {
            rowsPerPage = SUPPORTED_PAGE_SIZES.get(0);
        }

        rowsPerPagePropertyModel.setObject(rowsPerPage);

        //retrieve table page index from preferences.
        Integer pageIndex = null;//preferences.getPreference(UIPreferences.PreferenceType.PAGE_NUMBER, pageNumberPreferenceKey, Integer.class);
        if (pageIndex != null && pageIndex < dataView.getPageCount()) {
            dataView.setCurrentPage(pageIndex);
        }

        WebMarkupContainer pageNavigator = new WebMarkupContainer("pageNavigator");
        add(pageNavigator);
        pageBar = new WebMarkupContainer("pageBar");
        pageNavigator.add(pageBar);

        // Add additional page links
        pageBar.add(newPagingNavigationLink("first", dataView, 0).
                add(new Image("firstImage", new ResourceReference("images/pageNavStart.gif"))).
                add(new TitleResourceAppender("PagingNavigator.first")));
        pageBar.add(newPagingNavigationIncrementLink("prev", dataView, -1).
                add(new Image("prevImage", new ResourceReference("images/pageNavPrev.gif"))).
                add(new TitleResourceAppender("PagingNavigator.previous")));
        pageBar.add(newPagingNavigationIncrementLink("next", dataView, 1).
                add(new Image("nextImage", new ResourceReference("images/pageNavNext.gif"))).
                add(new TitleResourceAppender("PagingNavigator.next")));
        pageBar.add(newPagingNavigationLink("last", dataView, -1).
                add(new Image("lastImage", new ResourceReference("images/pageNavEnd.gif"))).
                add(new TitleResourceAppender("PagingNavigator.last")));

        //navigation before
        IModel<List<Integer>> navigationBeforeModel = new AbstractReadOnlyModel<List<Integer>>() {

            @Override
            public List<Integer> getObject() {
                List result = new ArrayList();

                int currentPage = dataView.getCurrentPage();
                for (int i = LEFT_OFFSET; i > 0; i--) {
                    if ((currentPage - i) >= 0) {
                        result.add(currentPage - i);
                    }
                }
                return result;
            }
        };
        pageBar.add(newNavigation("navigationBefore", "pageLinkBefore", "pageNumberBefore", dataView, navigationBeforeModel));

        //navigation after
        IModel<List<Integer>> navigationAfterModel = new AbstractReadOnlyModel<List<Integer>>() {

            @Override
            public List<Integer> getObject() {
                List result = new ArrayList();

                int currentPage = dataView.getCurrentPage();
                for (int i = 1; i <= RIGHT_OFFSET; i++) {
                    if ((currentPage + i) < dataView.getPageCount()) {
                        result.add(currentPage + i);
                    }
                }
                return result;
            }
        };
        pageBar.add(newNavigation("navigationAfter", "pageLinkAfter", "pageNumberAfter", dataView, navigationAfterModel));

        //navigation current
        IModel<List<Integer>> navigationCurrentModel = new AbstractReadOnlyModel<List<Integer>>() {

            @Override
            public List<Integer> getObject() {
                return Arrays.asList(dataView.getCurrentPage());
            }
        };
        pageBar.add(newNavigation("navigationCurrent", "pageLinkCurrent", "pageNumberCurrent", dataView, navigationCurrentModel));


        //new page form
        newPageForm = new Form("newPageForm");
        IModel<String> newPageNumberModel = new Model<String>() {

            @Override
            public void setObject(String input) {
                if (!Strings.isEmpty(input)) {
                    Integer newPageNumber = null;
                    try {
                        newPageNumber = Integer.parseInt(input);
                    } catch (NumberFormatException e) {
                    }

                    if (newPageNumber != null) {
                        if (newPageNumber <= 0) {
                            dataView.setCurrentPage(0);
                        } else if (newPageNumber > dataView.getPageCount()) {
                            dataView.setCurrentPage(dataView.getPageCount() - 1);
                        } else {
                            dataView.setCurrentPage(newPageNumber - 1);
                        }
                    }
                }
            }
//            @Override
//            public String getObject() {
//                return String.valueOf(pageable.getCurrentPage() + 1);
//            }
        };
        TextField<String> newPageNumber = new TextField<String>("newPageNumber", newPageNumberModel);
        AjaxButton goToPage = new AjaxButton("goToPage", newPageForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                //update model - newPageNumberModel
                updatePageComponents(target);
            }
        };

        newPageForm.add(newPageNumber);
        newPageForm.add(goToPage);
        pageNavigator.add(newPageForm);

        //page size
        IModel<Integer> pageSizeModel = new Model<Integer>() {

            @Override
            public Integer getObject() {
                return rowsPerPagePropertyModel.getObject();
            }

            @Override
            public void setObject(Integer rowsPerPage) {
//                preferences.putPreference(UIPreferences.PreferenceType.PAGE_SIZE, PAGE_SIZE_PREFERENCE_KEY, rowsPerPage);
                rowsPerPagePropertyModel.setObject(rowsPerPage);
            }
        };
        DropDownChoice<Integer> pageSize = new DropDownChoice<Integer>("pageSize", pageSizeModel, SUPPORTED_PAGE_SIZES);
        pageSize.add(new AjaxFormComponentUpdatingBehavior("onchange") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                //update model - pageSizeModel
                updatePageComponents(target);
            }
        });
        pageNavigator.add(pageSize);

        //all pages region
        allPagesRegion = new WebMarkupContainer("allPagesRegion");
        Label allPages = new Label("allPages", new AbstractReadOnlyModel<Integer>() {

            @Override
            public Integer getObject() {
                return dataView.getPageCount();
            }
        });
        allPagesRegion.add(allPages);
        pageNavigator.add(allPagesRegion);
    }

    protected ListView<Integer> newNavigation(String navigationId, final String pageLinkId, final String pageNumberId, final IPageable pageable,
            IModel<List<Integer>> navigationModel) {
        return new ListView<Integer>(navigationId, navigationModel) {

            @Override
            protected void populateItem(ListItem<Integer> item) {
                Integer pageIndex = item.getModelObject();
                AbstractLink pageLink = newPagingNavigationLink(pageLinkId, pageable, pageIndex);
                pageLink.add(new TitlePageNumberAppender(pageIndex));
                Label pageNumber = new Label(pageNumberId, String.valueOf(pageIndex + 1));
                pageLink.add(pageNumber);
                item.add(pageLink);
            }
        };
    }

    /**
     * Create a new increment link. May be subclassed to make use of specialized links, e.g. Ajaxian
     * links.
     *
     * @param id
     *            the link id
     * @param pageable
     *            the pageable to control
     * @param increment
     *            the increment
     * @return the increment link
     */
    protected AbstractLink newPagingNavigationIncrementLink(String id, IPageable pageable,
            int increment) {
        return new AjaxPagingNavigationIncrementLink(id, pageable, increment) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                super.onClick(target);
                updatePageComponents(target);
            }
        };
    }

    protected void updatePageComponents(AjaxRequestTarget target) {
        boolean updateSelf = true;
        if (toUpdate != null) {
            for (MarkupContainer container : toUpdate) {
                if (updateSelf && container.contains(this, true)) {
                    updateSelf = false;
                }
                target.addComponent(container);
            }
        }
        if (updateSelf) {
            target.addComponent(this);
        }
    }

    /**
     * Create a new pagenumber link. May be subclassed to make use of specialized links, e.g.
     * Ajaxian links.
     *
     * @param id
     *            the link id
     * @param pageable
     *            the pageable to control
     * @param pageNumber
     *            the page to jump to
     * @return the pagenumber link
     */
    protected AbstractLink newPagingNavigationLink(String id, IPageable pageable, int pageNumber) {
        return new AjaxPagingNavigationLink(id, pageable, pageNumber) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                super.onClick(target);
                updatePageComponents(target);
            }
        };
    }

    @Override
    protected void onBeforeRender() {
        boolean visibility = dataView.getPageCount() <= 1 ? false : true;
        pageBar.setVisible(visibility);
        newPageForm.setVisible(visibility);
        allPagesRegion.setVisible(visibility);

        super.onBeforeRender();
    }

//    @Override
//    protected void onAfterRender() {
//        super.onAfterRender();
//
//        preferences.putPreference(UIPreferences.PreferenceType.PAGE_NUMBER, pageNumberPreferenceKey, dataView.getCurrentPage());
//    }
    /**
     * Appends title attribute to navigation links
     *
     * @author igor.vaynberg
     */
    private final class TitleResourceAppender extends AbstractBehavior {

        private static final long serialVersionUID = 1L;

        private final String resourceKey;

        /**
         * Constructor
         *
         * @param resourceKey
         *            resource key of the message
         */
        public TitleResourceAppender(String resourceKey) {
            this.resourceKey = resourceKey;
        }

        /** {@inheritDoc} */
        @Override
        public void onComponentTag(Component component, ComponentTag tag) {
            tag.put("title", PagingNavigator.this.getString(resourceKey));
        }
    }

    /**
     * Appends title attribute to navigation links
     *
     * @author igor.vaynberg
     */
    private final class TitlePageNumberAppender extends AbstractBehavior {

        private static final long serialVersionUID = 1L;

        /** resource key for the message */
        private static final String RES = "PagingNavigation.page";

        /** page number */
        private final int page;

        /**
         * Constructor
         *
         * @param page
         *            page number to use as the ${page} var
         */
        public TitlePageNumberAppender(int page) {
            this.page = page;
        }

        /** {@inheritDoc} */
        @Override
        public void onComponentTag(Component component, ComponentTag tag) {
            tag.put("title", page + 1);
        }
    }
}
