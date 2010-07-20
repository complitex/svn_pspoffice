/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.passportoffice.commons.web.component.datatable.wicket;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByLink;
import org.apache.wicket.markup.html.border.Border;

/**
 *
 * @author Artem
 */
public class EnhancedOrderByBorder extends Border {

    /**
     * @param id
     *            see
     *            {@link OrderByLink#OrderByLink(String, String, ISortStateLocator, OrderByLink.ICssProvider) }
     * @param property
     *            see
     *            {@link OrderByLink#OrderByLink(String, String, ISortStateLocator, OrderByLink.ICssProvider) }
     * @param stateLocator
     *            see
     *            {@link OrderByLink#OrderByLink(String, String, ISortStateLocator, OrderByLink.ICssProvider) }
     * @param cssProvider
     *            see
     *            {@link OrderByLink#OrderByLink(String, String, ISortStateLocator, OrderByLink.ICssProvider) }
     */
    public EnhancedOrderByBorder(String id, String property, ISortStateLocator stateLocator,
            EnhancedOrderByLink.ICssProvider cssProvider) {
        super(id);
        EnhancedOrderByLink link = new EnhancedOrderByLink("orderByLink", property, stateLocator,
                EnhancedOrderByLink.VoidCssProvider.getInstance()) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onSortChanged() {
                EnhancedOrderByBorder.this.onSortChanged();
            }
        };
        add(link);
        add(new EnhancedOrderByLink.CssModifier(link, cssProvider));
        link.add(getBodyContainer());
    }

    /**
     * This method is a hook for subclasses to perform an action after sort has changed
     */
    protected void onSortChanged() {
        // noop
    }

    /**
     * @param id
     *            see {@link OrderByLink#OrderByLink(String, String, ISortStateLocator)}
     * @param property
     *            see {@link OrderByLink#OrderByLink(String, String, ISortStateLocator)}
     * @param stateLocator
     *            see {@link OrderByLink#OrderByLink(String, String, ISortStateLocator)}
     */
    public EnhancedOrderByBorder(String id, String property, ISortStateLocator stateLocator) {
        this(id, property, stateLocator, EnhancedOrderByLink.DefaultCssProvider.getInstance());
    }
}
