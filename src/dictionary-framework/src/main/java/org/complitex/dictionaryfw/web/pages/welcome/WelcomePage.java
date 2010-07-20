package org.complitex.dictionaryfw.web.pages.welcome;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.ResourceModel;

/**
 * User: Anatoly A. Ivanov java@inheaven.ru
 * Date: 20.12.2009 23:57:26
 */
public class WelcomePage extends WebPage {

    public WelcomePage() {
        super();

        add(new Label("title", new ResourceModel("title")));

        add(new BookmarkablePageLink("link", getLinkPage()));
    }

    private Class<? extends Page> getLinkPage() {
        try {
            return (Class<? extends Page>) getApplication().getApplicationSettings().getClassResolver().
                    resolveClass("org.passportoffice.information.strategy.impl.apartment.web.list.ApartmentList");
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }
}
