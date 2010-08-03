package org.complitex.pspoffice.commons.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 22.07.2010 17:50:26
 */
@Stateless(name = "LogBean")
public class LogBean {
    private static final Logger logger = LoggerFactory.getLogger(LogBean.class);

    public void info(Object... args){

    }

    public void error(Object... args){

    }        
}
