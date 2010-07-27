/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.dictionaryfw.strategy;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.naming.InitialContext;
import org.apache.wicket.util.string.Strings;

/**
 *
 * @author Artem
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class StrategyFactory {

    public Strategy getStrategy(String entityTable) {
        try {
            InitialContext context = new InitialContext();
            return (Strategy) context.lookup("java:module/" + Strings.capitalize(entityTable) + "Strategy");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static StrategyFactory get() {
        try {
            InitialContext context = new InitialContext();
            return (StrategyFactory) context.lookup("java:module/StrategyFactory");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
