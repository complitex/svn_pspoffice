package org.complitex.pspoffice.commons.web.pages.welcome;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.ResourceModel;
import org.complitex.dictionaryfw.strategy.web.DomainObjectList;

/**
 * User: Anatoly A. Ivanov java@inheaven.ru
 * Date: 20.12.2009 23:57:26
 */
public class WelcomePage extends WebPage {

    public WelcomePage() {
        super();

        add(new Label("title", new ResourceModel("title")));

        add(new BookmarkablePageLink("link", DomainObjectList.class, new PageParameters("entity=apartment")));
    }
}
