package org.complitex.pspoffice.report.service;

import java.io.InputStream;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 31.05.11 15:30
 */
public abstract class AbstractReportService implements IReportService{
    protected final static String TEMPLATE_PATH = "/org/complitex/pspoffice/report/template";

    protected InputStream getTemplateInputStream(String templateName){
        return getClass().getResourceAsStream(TEMPLATE_PATH + "/" + templateName);
    }
}
