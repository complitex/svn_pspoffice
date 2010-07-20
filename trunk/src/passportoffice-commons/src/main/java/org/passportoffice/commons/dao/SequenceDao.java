/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.passportoffice.commons.dao;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.apache.ibatis.session.SqlSession;
import org.passportoffice.commons.dao.aop.SqlSessionInterceptor;

/**
 *
 * @author Artem
 */
@Interceptors({SqlSessionInterceptor.class})
@Stateless
public class SequenceDao {

    public static final String SEQUENCE_NAMESPACE = "org.passportoffice.commons.entity.Sequence";

    private SqlSession session;

    public long nextStringId(String entityTable) {
        long nextStringId = (Long) session.selectOne(SEQUENCE_NAMESPACE + ".nextStringId", entityTable);
        session.update(SEQUENCE_NAMESPACE + ".incrementStringId", entityTable);
        return nextStringId;
    }

    public long nextId(String entityTable) {
        long nextId = (Long) session.selectOne(SEQUENCE_NAMESPACE + ".nextId", entityTable);
        session.update(SEQUENCE_NAMESPACE + ".incrementId", entityTable);
        return nextId;
    }
}
