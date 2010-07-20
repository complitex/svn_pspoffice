/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.passportoffice.commons.web.component;

import java.util.List;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.passportoffice.commons.entity.Entity;

/**
 *
 * @author Artem
 */
public final class Children extends Panel {

    public Children(String id, List<Entity> childrenEntities, final Class<WebPage> pageEditClass) {
        super(id);

        ListView<Entity> children = new ListView<Entity>("children", childrenEntities) {

            @Override
            protected void populateItem(ListItem<Entity> item) {
                Entity child = item.getModelObject();
                PageParameters params = new PageParameters();
                params.put("id", child.getId());
                BookmarkablePageLink<WebPage> link = new BookmarkablePageLink<WebPage>("link", pageEditClass, params);
                link.add(new Label("displayName", child.getDisplayName()));
                item.add(link);
            }
        };
        add(children);
    }
}
