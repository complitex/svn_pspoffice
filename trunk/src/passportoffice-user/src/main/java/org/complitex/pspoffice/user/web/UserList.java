package org.complitex.pspoffice.user.web;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.complitex.pspoffice.commons.entity.User;
import org.complitex.pspoffice.user.service.UserBean;

import javax.ejb.EJB;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 22.07.2010 15:03:45
 */
public class UserList extends WebPage {
    @EJB(name = "UserBean")
    private UserBean userBean;

    public UserList() {
        String users = "";

        List<User> list = userBean.getUsers();

        for (User u : list){
            users += u.getLogin() + ":" + u.getPassword() + "\n";
        }

        add(new Label("users", users));        
    }
}
