package org.complitex.pspoffice.report.service;

import com.lowagie.text.DocumentException;
import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

import javax.ejb.Stateless;
import java.io.*;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 17.05.11 18:07
 */
@Stateless
public class PdfReportService implements IReportService {
    private final static String FONT = "org/complitex/pspoffice/report/font/times.ttf";
    private final static String TEMPLATE_PATH = "org/complitex/pspoffice/report/template";

    @Override
    public void createReport(String templateName, Map<String, String> parameters, OutputStream out) throws CreateReportException {
        try {
            PdfStamper stamper = new PdfStamper(new PdfReader(getTemplateInputStream(templateName)), out);
            stamper.setFormFlattening(true);

            AcroFields acroFields = stamper.getAcroFields();

            acroFields.addSubstitutionFont(BaseFont.createFont(getFontURLPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED));

            for (String key : parameters.keySet()){
                acroFields.setField(key.toLowerCase(), parameters.get(key));
            }

            stamper.close();
        } catch (Exception e) {
            throw new CreateReportException(e);
        }
    }

    private InputStream getTemplateInputStream(String templateName){
        return getClass().getClassLoader().getResourceAsStream(TEMPLATE_PATH + "/" + templateName);
    }

    private String getFontURLPath(){
        return getClass().getClassLoader().getResource(FONT).getFile();
    }
}
