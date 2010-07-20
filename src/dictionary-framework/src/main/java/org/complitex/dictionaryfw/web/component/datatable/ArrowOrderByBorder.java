/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.dictionaryfw.web.component.datatable;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortState;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.complitex.dictionaryfw.web.component.datatable.wicket.EnhancedOrderByBorder;

/**
 *
 * @author Artem
 */
public class ArrowOrderByBorder extends EnhancedOrderByBorder {

    private static final String UP = "&#8593";

    private static final String DOWN = "&#8595";

    public ArrowOrderByBorder(String id, final String property, final ISortStateLocator stateLocator) {
        super(id, property, stateLocator);

        IModel<String> arrowModel = new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                if (stateLocator.getSortState().getPropertySortOrder(property) == ISortState.DESCENDING) {
                    return UP;
                } else if (stateLocator.getSortState().getPropertySortOrder(property) == ISortState.ASCENDING) {
                    return DOWN;
                }
                return null;
            }
        };
        Link link = (Link) get("orderByLink");
        Label arrow = new Label("arrow", arrowModel);
        arrow.setEscapeModelStrings(false);
        link.add(arrow);
    }
}
