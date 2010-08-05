/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.dictionaryfw.dao;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.apache.ibatis.session.SqlSession;
import org.complitex.dictionaryfw.dao.aop.SqlSessionInterceptor;
import org.complitex.dictionaryfw.entity.description.Entity;

/**
 *
 * @author Artem
 */
@Stateless
@Interceptors({SqlSessionInterceptor.class})
public class EntityBean {

    private SqlSession session;

    public Entity getEntity(String entity) {
        return (Entity) session.selectOne("org.complitex.dictionaryfw.entity.description.EntityDescription.load", entity);
    }
}
