package org.complitex.pspoffice.report.web;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.request.target.resource.ResourceStreamRequestTarget;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.complitex.dictionary.util.StringUtil;
import org.complitex.pspoffice.report.entity.IReportField;
import org.complitex.pspoffice.report.entity.RegistrationCardField;
import org.complitex.pspoffice.report.service.CreateReportException;
import org.complitex.pspoffice.report.service.OdfReportService;
import org.complitex.pspoffice.report.service.PdfReportService;
import org.complitex.pspoffice.report.service.RtfReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 02.06.11 14:56
 */
public abstract class AbstractReportDownload extends Page {
    private static final Logger log = LoggerFactory.getLogger(AbstractReportDownload.class);

    private final static SimpleDateFormat FILE_POSTFIX_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    protected final static SimpleDateFormat REPORT_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    @EJB
    private RtfReportService rtfReportService;

    @EJB
    private PdfReportService pdfReportService;

    @EJB
    private OdfReportService odfReportService;

    protected AbstractReportDownload(final String reportName, final IReportField[] fields, final PageParameters parameters) {
        final String type = parameters.getString("type") != null ? parameters.getString("type") : "pdf";
        final String locale = parameters.getString("locale") != null ? "_" + parameters.getString("locale") : "";

        try {
            getRequestCycle().setRequestTarget(new ResourceStreamRequestTarget(new AbstractResourceStreamWriter(){

                @Override
                public void write(OutputStream output) {
                    try {
                        Map<IReportField, Object> values = getValues(parameters);

                        if (values == null){
                            //todo error handling

                            return;
                        }

                        Map<String, String> map = new HashMap<String, String>();

                        for (IReportField key : fields){
                            map.put(key.getFieldName(), getString(values.get(key)));
                        }

                        if ("pdf".equals(type)){
                            pdfReportService.createReport(reportName + locale + ".pdf", map, output);
                        }else if ("rtf".equals(type)){
                            rtfReportService.createReport(reportName + locale  + ".rtf", map, output);
                        }else if ("odt".equals(type)){
                            odfReportService.createReport(reportName + locale  +  ".ott", map, output);
                        }
                    } catch (CreateReportException e) {
                        log.error("Ошибка создания документа", e);
                    }
                }

                @Override
                public String getContentType() {
                    if ("pdf".equals(type)){
                        return "application/pdf";
                    }else if ("rtf".equals(type)){
                        return "application/rtf";
                    }else if ("odt".equals(type)){
                        return "application/vnd.oasis.opendocument.text";
                    }

                    return null;
                }
            }, getFileName(parameters) + "." + type));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected Map<IReportField, Object> newValuesMap(){
        return new HashMap<IReportField, Object>();
    }

    protected String getString(Object object){
        if (object instanceof Date){
            return REPORT_DATE_FORMAT.format((Date)object);
        }

        return object != null ? object.toString() : "";
    }

    protected void putMultilineValue(Map<IReportField, Object> values, String value, int lineSize, IReportField... fields){
        if (value == null){
            return;
        }

        String[] wrap = StringUtil.wrap(value, lineSize, "\n", true).split("\n", fields.length);

        int index = 0;

        for (IReportField field : fields){
            if (index < wrap.length){
                values.put(field, wrap[index++]);
            }
        }
    }

    protected abstract Map<IReportField, Object> getValues(PageParameters parameters);

    protected abstract String getFileName(PageParameters parameters);
}
