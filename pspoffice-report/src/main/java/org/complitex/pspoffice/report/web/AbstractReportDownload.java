package org.complitex.pspoffice.report.web;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.request.target.resource.ResourceStreamRequestTarget;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.complitex.dictionary.util.DateUtil;
import org.complitex.pspoffice.report.service.CreateReportException;
import org.complitex.pspoffice.report.service.OdfReportService;
import org.complitex.pspoffice.report.service.PdfReportService;
import org.complitex.pspoffice.report.service.RtfReportService;

import javax.ejb.EJB;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 02.06.11 14:56
 */
public abstract class AbstractReportDownload extends Page {
    private final static SimpleDateFormat FILE_POSTFIX_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @EJB
    private RtfReportService rtfReportService;

    @EJB
    private PdfReportService pdfReportService;

    @EJB
    private OdfReportService odfReportService;

    protected AbstractReportDownload(final String reportName, final PageParameters parameters) {
        final String type = parameters.getString("type") != null ? parameters.getString("type") : "pdf";
        final String locale = parameters.getString("locale") != null ? "_" + parameters.getString("locale") : "";

        try {
            getRequestCycle().setRequestTarget(new ResourceStreamRequestTarget(new AbstractResourceStreamWriter(){

                @Override
                public void write(OutputStream output) {
                    try {
                        Map<String, String> map = getValues(parameters);

                        if ("pdf".equals(type)){
                            pdfReportService.createReport(reportName + locale + ".pdf", map, output);
                        }else if ("rtf".equals(type)){
                            rtfReportService.createReport(reportName + locale  + ".rtf", map, output);
                        }else if ("odt".equals(type)){
                            odfReportService.createReport(reportName + locale  +  ".ott", map, output);
                        }
                    } catch (CreateReportException e) {
                        e.printStackTrace();
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

    protected abstract Map<String, String> getValues(PageParameters parameters);

    protected abstract String getFileName(PageParameters parameters);
}
