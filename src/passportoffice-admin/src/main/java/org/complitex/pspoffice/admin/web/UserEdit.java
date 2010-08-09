package org.complitex.pspoffice.admin.web;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.*;
import org.complitex.pspoffice.admin.service.UserBean;
import org.complitex.pspoffice.commons.entity.User;
import org.complitex.pspoffice.commons.entity.UserGroup;
import org.complitex.pspoffice.commons.web.template.FormTemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.Collection;

import static org.complitex.pspoffice.commons.entity.UserGroup.GROUP_NAME.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 31.07.2010 14:12:33
 *
 *  Страница создания и редактирования пользователя
 */
public class UserEdit extends FormTemplatePage{
    private static final Logger log = LoggerFactory.getLogger(UserEdit.class);

    @EJB(name = "UserBean")
    private UserBean userBean;

    public UserEdit() {
        super();
        init(null);
    }

    public UserEdit(final PageParameters parameters) {
        super();
        init(parameters.getAsLong("user_id"));
    }

    private void init(final Long id){
        add(new Label("title", new ResourceModel("admin.user_edit.title")));
        add(new FeedbackPanel("messages"));

        //Модель данных
        final LoadableDetachableModel<User> userModel = new LoadableDetachableModel<User>() {

            @Override
            protected User load() {
                try {
                    if (id != null){
                        return userBean.getUser(id);
                    }
                    else{
                        User user = new User();
                        user.setUserGroups(new ArrayList<UserGroup>());
                        return user;
                    }
                } catch (Exception e) {
                    //log
                }

                return null;
            }
        };

        //Форма
        Form form = new Form<User>("admin.user_edit.form");
        add(form);

        //Сохранить
        Button save = new Button("admin.user_edit.save"){
            @Override
            public void onSubmit() {
                try {
                    userBean.save(userModel.getObject());

                    log.info("Пользователь сохранен: {}", userModel.getObject());
                    getSession().info(getString("admin.user_edit.info.saved"));

                } catch (Exception e) {
                    log.error("Ошибка сохранения пользователя", e);
                    getSession().error(getString("admin.user_edit.error.saved"));
                }

                setResponsePage(UserList.class);
            }
        };
        form.add(save);

        //Отмена
        Button cancel = new Button("admin.user_edit.cancel"){
            @Override
            public void onSubmit() {
               setResponsePage(UserList.class);
            }
        };
        form.add(cancel);

        //Логин
        RequiredTextField login = new RequiredTextField<String>("admin.user_edit.login", new PropertyModel<String>(userModel, "login"));
        login.setEnabled(id == null);
        form.add(login);

        //Пароль
        PasswordTextField password = new PasswordTextField("admin.user_edit.password", new PropertyModel<String>(userModel, "newPassword"));
        password.setEnabled(id != null);
        password.setRequired(false);
        form.add(password);

        //todo domain object from
        form.add(new TextField<String>("admin.user_edit.last_name", new Model<String>()));
        form.add(new TextField<String>("admin.user_edit.first_name", new Model<String>()));
        form.add(new TextField<String>("admin.user_edit.middle_name", new Model<String>()));

        //Группы привилегий
        CheckGroup<UserGroup> usergroups = new CheckGroup<UserGroup>("admin.user_edit.usergroups",
                new PropertyModel<Collection<UserGroup>>(userModel, "userGroups"));

        usergroups.add(new Check<UserGroup>("ADMINISTRATORS", getUserGroup(userModel.getObject(), ADMINISTRATORS)));
        usergroups.add(new Check<UserGroup>("DEPARTMENT_OFFICERS", getUserGroup(userModel.getObject(), DEPARTMENT_OFFICERS)));
        usergroups.add(new Check<UserGroup>("LOCAL_OFFICERS", getUserGroup(userModel.getObject(), LOCAL_OFFICERS)));
        usergroups.add(new Check<UserGroup>("LOCAL_OFFICERS_EDIT", getUserGroup(userModel.getObject(), LOCAL_OFFICERS_EDIT)));
        usergroups.add(new Check<UserGroup>("LOCAL_OFFICERS_DEP_VIEW", getUserGroup(userModel.getObject(), LOCAL_OFFICERS_DEP_VIEW)));
        usergroups.add(new Check<UserGroup>("LOCAL_OFFICERS_DEP_EDIT", getUserGroup(userModel.getObject(), LOCAL_OFFICERS_DEP_EDIT)));
        usergroups.add(new Check<UserGroup>("LOCAL_OFFICERS_DEP_CHILD_VIEW", getUserGroup(userModel.getObject(), LOCAL_OFFICERS_DEP_CHILD_VIEW)));
        usergroups.add(new Check<UserGroup>("LOCAL_OFFICERS_DEP_CHILD_EDIT", getUserGroup(userModel.getObject(), LOCAL_OFFICERS_DEP_CHILD_EDIT)));

        form.add(usergroups);

    }

    private IModel<UserGroup> getUserGroup(User user, UserGroup.GROUP_NAME group_name){
        for (UserGroup userGroup : user.getUserGroups()) {
            if (userGroup.getGroupName().equals(group_name)) {
                return new Model<UserGroup>(userGroup);
            }
        }

        UserGroup userGroup = new UserGroup();
        userGroup.setGroupName(group_name);
        return new Model<UserGroup>(userGroup);
    }

}
