/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.dictionaryfw.dao;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.apache.ibatis.session.SqlSession;
import org.complitex.dictionaryfw.dao.aop.SqlSessionInterceptor;
import org.complitex.dictionaryfw.entity.InsertParameter;
import org.complitex.dictionaryfw.entity.StringCulture;

/**
 *
 * @author Artem
 */
@Stateless
@Interceptors({SqlSessionInterceptor.class})
public class StringCultureBean {

    private SqlSession session;

    public void insert(StringCulture stringCulture, String entityTable) {
        session.insert("org.complitex.dictionaryfw.entity.StringCulture.insert", new InsertParameter(entityTable, stringCulture));
    }
}
