package org.complitex.pspoffice.admin.web;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ResourceModel;
import org.complitex.pspoffice.admin.service.UserBean;
import org.complitex.pspoffice.commons.entity.User;
import org.complitex.pspoffice.commons.web.component.toolbar.AddUserButton;
import org.complitex.pspoffice.commons.web.component.toolbar.ToolbarButton;
import org.complitex.pspoffice.commons.web.template.TemplatePage;

import javax.ejb.EJB;
import java.util.Arrays;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 22.07.2010 15:03:45
 */
public class UserList extends TemplatePage {
    @EJB(name = "UserBean")
    private UserBean userBean;

    public UserList() {
        super();

        add(new Label("title", new ResourceModel("admin.user_list.title")));

        String users = "";

        List<User> list = userBean.getUsers();

        for (User u : list){
            users += u.getLogin() + ":" + u.getPassword() + "\n";
        }

        add(new Label("users", users));        
    }

    @Override
    protected List<ToolbarButton> getToolbarButtons(String id) {
        return Arrays.asList((ToolbarButton) new AddUserButton(id) {

            @Override
            protected void onClick() {
                setResponsePage(UserEdit.class);
            }
        });
    }
}
