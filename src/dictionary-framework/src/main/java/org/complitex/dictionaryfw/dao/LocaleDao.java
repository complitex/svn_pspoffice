/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.dictionaryfw.dao;

import java.util.List;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.apache.ibatis.session.SqlSession;
import org.complitex.dictionaryfw.dao.aop.SqlSessionInterceptor;

/**
 *
 * @author Artem
 */
@Stateless
@Interceptors({SqlSessionInterceptor.class})
public class LocaleDao {

    private SqlSession session;

    public List<String> getAllLocales() {
        return session.selectList("org.complitex.dictionaryfw.entity.Locale.getAll");
    }

    public String getSystemLocale(){
        return (String)session.selectOne("org.complitex.dictionaryfw.entity.Locale.getSystem");
    }
}
