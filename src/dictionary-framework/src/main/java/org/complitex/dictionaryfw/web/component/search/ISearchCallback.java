/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.dictionaryfw.web.component.search;

import java.util.Map;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;

/**
 *
 * @author Artem
 */
public interface ISearchCallback {

    void found(WebPage page, Map<String, Long> ids, AjaxRequestTarget target);
}
