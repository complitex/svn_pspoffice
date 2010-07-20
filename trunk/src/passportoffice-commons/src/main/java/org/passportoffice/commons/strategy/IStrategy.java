/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.passportoffice.commons.strategy;

import org.apache.wicket.markup.html.WebPage;

/**
 *
 * @author Artem
 */
public interface IStrategy {

    IDao getDao();

    WebPage getEditPage();

    WebPage getListPage();
}
