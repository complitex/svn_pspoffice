/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.complitex.pspoffice.commons.strategy.search.behaviour;

import java.io.Serializable;
import java.util.Map;
import org.complitex.dictionaryfw.entity.DomainObject;
import org.complitex.dictionaryfw.entity.example.DomainObjectExample;
import org.complitex.dictionaryfw.strategy.StrategyFactory;
import org.complitex.dictionaryfw.web.component.search.ISearchBehaviour;

/**
 *
 * @author Artem
 */
public class StreetSearchBehaviour implements ISearchBehaviour, Serializable{

    @Override
    public String getEntityTable() {
        return "street";
    }

    @Override
    public DomainObjectExample getExample(String searchTextInput, Map<String, DomainObject> previousInfo) {
        DomainObjectExample example = new DomainObjectExample();
        example.setTable(getEntityTable());
        StrategyFactory.get().getStrategy(getEntityTable()).configureSearchAttribute(example, searchTextInput);
        return example;
    }

}
