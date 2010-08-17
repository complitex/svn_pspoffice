package org.complitex.pspoffice.admin.web;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.*;
import org.complitex.dictionaryfw.web.component.DomainObjectInputPanel;
import org.complitex.pspoffice.admin.service.UserBean;
import org.complitex.pspoffice.commons.entity.User;
import org.complitex.pspoffice.commons.entity.UserGroup;
import org.complitex.pspoffice.commons.web.template.FormTemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
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
        add(new Label("title", new ResourceModel("title")));
        add(new FeedbackPanel("messages"));

        //Модель данных
        //todo catch exception
        final IModel<User> userModel = new Model<User>(id != null ? userBean.getUser(id) : userBean.newUser());

        //Форма
        Form form = new Form<User>("form");
        add(form);

        //Сохранить
        Button save = new Button("save"){
            @Override
            public void onSubmit() {
                try {
                    userBean.save(userModel.getObject());

                    log.info("Пользователь сохранен: {}", userModel.getObject());
                    getSession().info(getString("info.saved"));

                } catch (Exception e) {
                    log.error("Ошибка сохранения пользователя", e);
                    getSession().error(getString("error.saved"));
                }

                setResponsePage(UserList.class);
            }
        };
        form.add(save);

        //Отмена
        Button cancel = new Button("cancel"){
            @Override
            public void onSubmit() {
               setResponsePage(UserList.class);
            }
        };
        cancel.setDefaultFormProcessing(false);
        form.add(cancel);

        //Логин
        RequiredTextField login = new RequiredTextField<String>("login", new PropertyModel<String>(userModel, "login"));
        login.setEnabled(id == null);
        form.add(login);

        //Пароль
        PasswordTextField password = new PasswordTextField("password", new PropertyModel<String>(userModel, "newPassword"));
        password.setEnabled(id != null);
        password.setRequired(false);
        form.add(password);

        //Информация о пользователе
        DomainObjectInputPanel userInfo = new DomainObjectInputPanel("user_info", userModel.getObject().getUserInfo(),
                UserBean.USER_INFO_ENTITY_TABLE, null, null);
        form.add(userInfo);

        //Группы привилегий
        CheckGroup<UserGroup> usergroups = new CheckGroup<UserGroup>("usergroups",
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
        if (!user.getUserGroups().isEmpty()) {
            for (UserGroup userGroup : user.getUserGroups()) {
                if (userGroup.getGroupName().equals(group_name)) {
                    return new Model<UserGroup>(userGroup);
                }
            }
        }

        UserGroup userGroup = new UserGroup();
        userGroup.setGroupName(group_name);
        return new Model<UserGroup>(userGroup);
    }

}
