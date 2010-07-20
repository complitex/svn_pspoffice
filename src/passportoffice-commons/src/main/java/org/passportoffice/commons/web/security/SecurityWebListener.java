//package org.passportoffice.commons.web.security;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.vetcontrol.entity.Log;
//import org.vetcontrol.entity.User;
//import org.vetcontrol.service.LogBean;
//import org.vetcontrol.util.DateUtil;
//
//import javax.ejb.EJB;
//import javax.servlet.ServletContextEvent;
//import javax.servlet.ServletContextListener;
//import javax.servlet.ServletRequestEvent;
//import javax.servlet.ServletRequestListener;
//import javax.servlet.annotation.WebListener;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpSession;
//import javax.servlet.http.HttpSessionEvent;
//import javax.servlet.http.HttpSessionListener;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * @author Anatoly A. Ivanov java@inheaven.ru
// *         Date: 30.12.2009 4:21:43
// *
// * Класс слушает создание http сессий и запросов, и сохраняет в сессию информацию
// * об авторизованном пользователе.
// */
//@WebListener
//public class SecurityWebListener implements HttpSessionListener, ServletRequestListener, ServletContextListener {
//    @EJB
//    private LogBean logBean;
//
//    private static final Logger log = LoggerFactory.getLogger(SecurityWebListener.class);
//
//    public final static String PRINCIPAL = "org.vetcontrol.web.security.PRINCIPAL";
//
//    private final static ConcurrentHashMap<String, HttpSession> activeSession = new ConcurrentHashMap<String, HttpSession>();
//
//    @Override
//    public void sessionCreated(HttpSessionEvent event) {
//        activeSession.put(event.getSession().getId(), event.getSession());
//    }
//
//    @Override
//    public void sessionDestroyed(HttpSessionEvent event) {
//        activeSession.remove(event.getSession().getId());
//
//        //logout
//        long start = event.getSession().getCreationTime();
//        long end = event.getSession().getLastAccessedTime();
//        String d = "Длительность сессии: " + DateUtil.getTimeDiff(start, end);
//
//        logBean.info(Log.MODULE.COMMONS, Log.EVENT.USER_LOGOUT, SecurityWebListener.class,  User.class, d);
//        log.info("Сессия пользователя деактивированна [" + d + "]");
//    }
//
//    @Override
//    public void requestDestroyed(ServletRequestEvent event) {
//    }
//
//    @Override
//    public void requestInitialized(ServletRequestEvent event) {
//        HttpServletRequest request = (HttpServletRequest) event.getServletRequest();
//
//        //login
//        if (request.getUserPrincipal() != null && request.getSession().getAttribute(PRINCIPAL) == null){
//            request.getSession().setAttribute(PRINCIPAL, request.getUserPrincipal().getName());
//
//            String d = "IP: " + request.getRemoteAddr();
//            String login = request.getUserPrincipal().getName();
//
//            logBean.info(login, Log.MODULE.COMMONS, Log.EVENT.USER_LOGIN, SecurityWebListener.class, User.class, d);
//            log.info("Пользователь авторизирован [login: " + login + ", " + d + "]");
//        }
//    }
//
//    public static synchronized List<HttpSession> getSessions(String principal){
//        List<HttpSession> sessions = new ArrayList<HttpSession>();
//
//        for (HttpSession session : activeSession.values()){
//            if(principal.equals(session.getAttribute(PRINCIPAL))){
//                sessions.add(session);
//            }
//        }
//
//        return sessions;
//    }
//
//    public static synchronized Collection<HttpSession> getSessions(){
//        return activeSession.values();
//    }
//
//    @Override
//    public void contextInitialized(ServletContextEvent servletContextEvent) {
//        logBean.info(Log.MODULE.COMMONS, Log.EVENT.SYSTEM_START, SecurityWebListener.class, null);
//    }
//
//    @Override
//    public void contextDestroyed(ServletContextEvent servletContextEvent) {
//        try {
//            logBean.info(Log.MODULE.COMMONS, Log.EVENT.SYSTEM_STOP, SecurityWebListener.class, null);
//        } catch (Exception e) {
//            //nothing
//        }
//    }
//}
