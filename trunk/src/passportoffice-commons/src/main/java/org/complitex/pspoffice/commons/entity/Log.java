package org.complitex.pspoffice.commons.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 22.07.2010 16:28:03
 *
 * Модель лога журнала событий
 */
public class Log implements Serializable{
    public static enum EVENT {SYSTEM_START, SYSTEM_STOP, USER_LOGIN, USER_LOGOUT, LIST, VIEW, CREATE, EDIT, REMOVE}
    public static enum STATUS {OK, ERROR}

    private Long id;
    private Date date;
    private User user;
    private String modelClass;
    private String controllerClass;
    private String module;
    private EVENT event;
    private STATUS status;
    private String description;
    private List<LogChange> logChanges;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getModelClass() {
        return modelClass;
    }

    public void setModelClass(String modelClass) {
        this.modelClass = modelClass;
    }

    public String getControllerClass() {
        return controllerClass;
    }

    public void setControllerClass(String controllerClass) {
        this.controllerClass = controllerClass;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public EVENT getEvent() {
        return event;
    }

    public void setEvent(EVENT event) {
        this.event = event;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<LogChange> getLogChanges() {
        return logChanges;
    }

    public void setLogChanges(List<LogChange> logChanges) {
        this.logChanges = logChanges;
    }
}
