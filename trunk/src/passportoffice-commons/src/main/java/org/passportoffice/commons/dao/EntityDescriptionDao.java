/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.passportoffice.commons.dao;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.apache.ibatis.session.SqlSession;
import org.passportoffice.commons.dao.aop.SqlSessionInterceptor;
import org.passportoffice.commons.entity.EntityDescription;

/**
 *
 * @author Artem
 */
@Stateless
@Interceptors({SqlSessionInterceptor.class})
public class EntityDescriptionDao {

    private SqlSession session;

     public EntityDescription getEntityDescription(String entityTable) {
        return (EntityDescription) session.selectOne("org.passportoffice.commons.entity.EntityDescription.load", entityTable);
    }

}
