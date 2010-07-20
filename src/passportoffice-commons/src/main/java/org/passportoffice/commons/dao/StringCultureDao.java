/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.passportoffice.commons.dao;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.apache.ibatis.session.SqlSession;
import org.passportoffice.commons.dao.aop.SqlSessionInterceptor;
import org.passportoffice.commons.entity.InsertParameter;
import org.passportoffice.commons.entity.StringCulture;

/**
 *
 * @author Artem
 */
@Stateless
@Interceptors({SqlSessionInterceptor.class})
public class StringCultureDao {

    private SqlSession session;

    public void insert(StringCulture stringCulture, String entityTable) {
        session.insert("org.passportoffice.commons.entity.StringCulture.insert", new InsertParameter(entityTable, stringCulture));
    }
}
