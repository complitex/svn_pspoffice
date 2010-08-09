package org.complitex.pspoffice.admin.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.complitex.dictionaryfw.service.AbstractBean;
import org.complitex.pspoffice.commons.entity.User;
import org.complitex.pspoffice.commons.entity.UserGroup;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 22.07.2010 15:05:09
 */
@Stateless(name = "UserBean")
public class UserBean extends AbstractBean {
    public static final String STATEMENT_PREFIX = UserBean.class.getCanonicalName();       

    @SuppressWarnings({"unchecked"})
    public List<User> getUsers(){

        return sqlSession.selectList(STATEMENT_PREFIX + ".getAll");
    }

    public User getUser(Long id){

        return (User) sqlSession.selectOne(STATEMENT_PREFIX + ".selectUser", id);
    }

    public void save(User user){
        if (user.getId() == null){ //Сохранение нового пользователя
            user.setPassword(DigestUtils.md5Hex(user.getLogin())); //md5 password

            sqlSession.insert(STATEMENT_PREFIX + ".insertUser", user);

            //сохранение групп привилегий
            for(UserGroup userGroup : user.getUserGroups()){
                userGroup.setLogin(user.getLogin());

                sqlSession.insert(STATEMENT_PREFIX + ".insertUserGroup", userGroup);
            }

            //todo save attribute
        }else{ //Редактирование пользователя
            User dbUser = (User) sqlSession.selectOne(STATEMENT_PREFIX + ".selectUser", user.getId());

            //удаление групп привилегий
            for (UserGroup dbUserGroup : dbUser.getUserGroups()){
                boolean contain = false;

                for (UserGroup userGroup : user.getUserGroups()){
                    if (userGroup.getGroupName().equals(dbUserGroup.getGroupName())){
                        contain = true;
                        break;
                    }
                }

                if (!contain){
                    sqlSession.delete(STATEMENT_PREFIX + ".deleteUserGroup", dbUserGroup.getId());
                }
            }

            //добавление групп привилегий
            for (UserGroup userGroup : user.getUserGroups()){
                boolean contain = false;

                for (UserGroup dbUserGroup : dbUser.getUserGroups()){
                    if (userGroup.getGroupName().equals(dbUserGroup.getGroupName())){
                        contain = true;
                        break;
                    }
                }

                if (!contain){
                    sqlSession.insert(STATEMENT_PREFIX + ".insertUserGroup", userGroup);
                }
            }


            //изменение пароля
            if(user.getNewPassword() != null){
                user.setPassword(DigestUtils.md5Hex(user.getNewPassword())); //md5 password
                sqlSession.update(STATEMENT_PREFIX + ".updateUser", user);
            }

            //todo save attribute

        }

    }

}

