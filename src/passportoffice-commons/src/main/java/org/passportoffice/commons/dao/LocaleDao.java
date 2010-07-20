/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.passportoffice.commons.dao;

import java.util.List;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.apache.ibatis.session.SqlSession;
import org.passportoffice.commons.dao.aop.SqlSessionInterceptor;

/**
 *
 * @author Artem
 */
@Stateless
@Interceptors({SqlSessionInterceptor.class})
public class LocaleDao {

    private SqlSession session;

    public List<String> getAllLocales() {
        return session.selectList("org.passportoffice.commons.entity.Locale.getAll");
    }
}
