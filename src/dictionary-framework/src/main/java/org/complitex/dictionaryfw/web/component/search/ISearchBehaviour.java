/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.dictionaryfw.web.component.search;

import java.util.Map;
import org.complitex.dictionaryfw.entity.DomainObject;
import org.complitex.dictionaryfw.entity.example.DomainObjectExample;

/**
 *
 * @author Artem
 */
public interface ISearchBehaviour {

    String getEntityTable();

    DomainObjectExample getExample(String searchTextInput, Map<String, DomainObject> previousInfo);
}
