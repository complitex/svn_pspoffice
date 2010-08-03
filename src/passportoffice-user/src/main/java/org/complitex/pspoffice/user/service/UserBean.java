package org.complitex.pspoffice.user.service;

import org.apache.ibatis.session.SqlSession;
import org.complitex.dictionaryfw.dao.SqlSessionFactory;
import org.complitex.pspoffice.commons.entity.User;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 22.07.2010 15:05:09
 */
@Stateless(name = "UserBean")
public class UserBean {
    @EJB(beanName = "SqlSessionFactory")
    private SqlSessionFactory sqlSessionFactory;

    @SuppressWarnings({"unchecked"})
    public List<User> getUsers(){
        SqlSession sqlSession = sqlSessionFactory.getCurrentSession();


        return sqlSession.selectList("org.complitex.pspoffice.commons.entity.User.getAll");
    }     

}

