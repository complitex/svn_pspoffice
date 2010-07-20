/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.passportoffice.commons.web.component;

import java.util.List;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.passportoffice.commons.entity.Entity;
import org.passportoffice.commons.web.PageParameterNames;

/**
 *
 * @author Artem
 */
public final class Children extends Panel {

    public Children(String id, String label, String childrenTitle, final Long entityId,
            IModel<List<Entity>> childrenEntities, final Class<WebPage> childEditPageClass) {
        super(id);

        Label title = new Label("title", label);
        add(title);

        final WebMarkupContainer content = new WebMarkupContainer("content");
        content.setOutputMarkupPlaceholderTag(true);
        content.setVisible(false);

        class ToggleModel extends AbstractReadOnlyModel<String> {

            private boolean expanded;

            @Override
            public String getObject() {
                return expanded ? "Show" : "Hide";
            }

            public void toggle() {
                expanded = !expanded;
            }

            public boolean isExpanded() {
                return expanded;
            }
        }
        final ToggleModel toggleModel = new ToggleModel();

        AjaxLink toggleLink = new AjaxLink("toggleLink", toggleModel) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                toggleModel.toggle();
                if (toggleModel.isExpanded()) {
                    content.setVisible(true);
                } else {
                    content.setVisible(false);
                }
                target.addComponent(content);
            }
        };
        add(toggleLink);

        content.add(new Label("childrenTitle", childrenTitle));

        ListView<Entity> children = new ListView<Entity>("children", childrenEntities) {

            @Override
            protected void populateItem(ListItem<Entity> item) {
                Entity child = item.getModelObject();
                PageParameters params = new PageParameters();
                params.put(PageParameterNames.ID, child.getId());
                BookmarkablePageLink<WebPage> link = new BookmarkablePageLink<WebPage>("link", childEditPageClass, params);
                link.add(new Label("displayName", child.getDisplayName()));
                item.add(link);
            }
        };
        content.add(children);

        PageParameters params = new PageParameters();
        params.put(PageParameterNames.PARENT_ID, entityId);
        params.put(PageParameterNames.PARENT_EDIT_PAGE, findPage().getClass().getName());
        BookmarkablePageLink<WebPage> add = new BookmarkablePageLink<WebPage>("add", childEditPageClass, params);
        content.add(add);
        add(content);
    }
}
