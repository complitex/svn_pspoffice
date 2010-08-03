package org.complitex.pspoffice.commons.web.security;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 22.07.2010 16:24:34
 *
 * Класс слушает создание http сессий и запросов, и сохраняет в сессию информацию об авторизованном пользователе.
 */
@WebListener
public class SecurityWebListener implements HttpSessionListener, ServletRequestListener, ServletContextListener {
        
    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void requestDestroyed(ServletRequestEvent servletRequestEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void requestInitialized(ServletRequestEvent servletRequestEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
