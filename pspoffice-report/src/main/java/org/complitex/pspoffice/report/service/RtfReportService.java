package org.complitex.pspoffice.report.service;

import org.complitex.dictionary.util.RtfTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 11.05.11 17:45
 */
@Stateless
public class RtfReportService extends AbstractReportService{

    @Override
    public void createReport(String templateName, Map<String, String> values, OutputStream out) throws CreateReportException {
        new RtfTemplate(getTemplateInputStream(templateName), values).fill(out, RtfTemplate.VARIABLE_TYPE.FORM);
    }
}
