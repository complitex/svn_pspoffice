package org.complitex.pspoffice.admin.service;

import org.complitex.dictionaryfw.entity.example.AttributeExample;
import org.complitex.pspoffice.commons.service.AbstractFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.08.2010 17:55:35
 */
public class UserFilter extends AbstractFilter{
    private String login;
    private List<AttributeExample> attributeExamples = new ArrayList<AttributeExample>();

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public List<AttributeExample> getAttributeExamples() {
        return attributeExamples;
    }

    public void setAttributeExamples(List<AttributeExample> attributeExamples) {
        this.attributeExamples = attributeExamples;
    }

    public boolean isFilterAttributes(){
        for(AttributeExample attributeExample : attributeExamples){
            if (attributeExample.getValue() != null){
                return true;
            }
        }

        return false;
    }
}
