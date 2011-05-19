package org.complitex.pspoffice.report.service;

import org.odftoolkit.odfdom.dom.element.form.FormTextElement;
import org.odftoolkit.simple.TextDocument;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 19.05.11 14:58
 */
public class OdfReportService implements IReportService{
    private final static String TEMPLATE_PATH = "/org/complitex/pspoffice/report/template";

    @Override
    public void createReport(String templateName, Map<String, String> parameters, OutputStream out) throws CreateReportException {
        try {
            TextDocument textDocument = TextDocument.loadDocument(getTemplateInputStream(templateName));

            NodeList nodeList = textDocument.getContentRoot().getElementsByTagName("form:text");

            for (int i=0; i < nodeList.getLength(); ++i){
                FormTextElement formTextElement = (FormTextElement) nodeList.item(i);

                formTextElement.setFormCurrentValueAttribute(parameters.get(formTextElement.getFormNameAttribute()));
            }

            textDocument.save(out);


        } catch (Exception e) {
            throw new CreateReportException(e);
        }
    }

    private InputStream getTemplateInputStream(String templateName){
        return getClass().getResourceAsStream(TEMPLATE_PATH + "/" + templateName);
    }
}
