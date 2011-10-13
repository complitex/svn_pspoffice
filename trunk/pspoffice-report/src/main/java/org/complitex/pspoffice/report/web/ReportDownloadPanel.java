package org.complitex.pspoffice.report.web;

import java.io.OutputStream;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.ajax.calldecorator.AjaxCallDecorator;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.odlabs.wiquery.ui.dialog.Dialog;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.apache.wicket.request.target.resource.ResourceStreamRequestTarget;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionary.util.EjbBeanLocator;
import org.complitex.dictionary.util.StringUtil;
import org.complitex.pspoffice.report.entity.IReportField;
import org.complitex.pspoffice.report.service.CreateReportException;
import org.complitex.pspoffice.report.service.IReportService;
import org.complitex.pspoffice.report.util.ReportDateFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportDownloadPanel extends Panel {

    private static final Logger log = LoggerFactory.getLogger(ReportDownloadPanel.class);
    private Dialog dialog;

    public static class DownloadPage extends Page {

        public DownloadPage(PageParameters parameters) {
            String sessionKey = parameters.getString("key");
            String type = parameters.getString("type") != null ? parameters.getString("type").toLowerCase() : "pdf";
            String locale = parameters.getString("locale") != null ? "_" + parameters.getString("locale") : "";

            AbstractReportDownload reportDownload = retrieveReportDownload(sessionKey);
            getRequestCycle().setRequestTarget(getResourceStreamRequestTarget(reportDownload, type, locale));
        }

        private AbstractReportDownload retrieveReportDownload(String key) {
            WebRequestCycle webRequestCycle = (WebRequestCycle) RequestCycle.get();
            HttpSession session = webRequestCycle.getWebRequest().getHttpServletRequest().getSession();
            AbstractReportDownload reportDownload = (AbstractReportDownload) session.getAttribute(key);
            session.setAttribute(key, null);
            return reportDownload;
        }

        private ResourceStreamRequestTarget getResourceStreamRequestTarget(AbstractReportDownload reportDownload,
                String type, String locale) {
            return new ResourceStreamRequestTarget(getResourceStreamWriter(reportDownload, type, locale),
                    reportDownload.getFileName() + "." + type);
        }

        private IResourceStream getResourceStreamWriter(final AbstractReportDownload reportDownload, final String type,
                final String locale) {
            return new AbstractResourceStreamWriter() {

                @Override
                public String getContentType() {
                    if ("pdf".equals(type)) {
                        return "application/pdf";
                    } else if ("rtf".equals(type)) {
                        return "application/rtf";
                    } else if ("odt".equals(type)) {
                        return "application/vnd.oasis.opendocument.text";
                    }

                    return null;
                }

                @Override
                public void write(OutputStream output) {
                    try {
                        Map<IReportField, Object> values = reportDownload.getValues();
                        Map<String, String> map = new HashMap<String, String>();

                        for (IReportField key : reportDownload.getReportFields()) {
                            map.put(key.getFieldName(), displayValue(values.get(key)));
                        }

                        IReportService reportService = getReportService(type);
                        reportService.createReport(reportDownload.getReportName() + locale + "." + type, map, output);
                    } catch (CreateReportException e) {
                        log.error("Couldn't create report.", e);
                    }
                }
            };
        }

        private IReportService getReportService(String type) {
            String beanName = Strings.capitalize(type) + "ReportService";
            return EjbBeanLocator.getBean(beanName, true);
        }

        private String displayValue(Object value) {
            if (value instanceof Date) {
                return ReportDateFormatter.format((Date) value);
            }
            return StringUtil.valueOf(value);
        }
    }

    public ReportDownloadPanel(String id, String title, final AbstractReportDownload reportDownload) {
        super(id);

        dialog = new Dialog("dialog");
        dialog.setModal(true);
        dialog.setWidth(420);
        dialog.setTitle(title);
        add(dialog);

        Form form = new Form("form");
        dialog.add(form);

        final IModel<String> typeModel = new Model<String>("PDF");
        final IModel<String> localeModel = new Model<String>("ru_RU");

        form.add(new DropDownChoice<String>("type", typeModel, Arrays.asList("PDF", "RTF")));

        form.add(new DropDownChoice<String>("locale", localeModel, Arrays.asList("ru_RU", "uk_UA"),
                new IChoiceRenderer<String>() {

                    @Override
                    public Object getDisplayValue(String object) {
                        if ("ru_RU".equals(object)) {
                            return getString("ru");
                        }
                        if ("uk_UA".equals(object)) {
                            return getString("uk");
                        }
                        return null;
                    }

                    @Override
                    public String getIdValue(String object, int index) {
                        return object;
                    }
                }));

        //Загрузить
        form.add(new AjaxButton("download") {

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return new AjaxCallDecorator() {

                    @Override
                    public CharSequence decorateScript(CharSequence script) {
                        return dialog.close().render().toString() + script;
                    }
                };
            }

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                dialog.close(target);

                String sessionKey = reportDownload.getFileName();
                storeReportDownload(reportDownload, sessionKey);

                PageParameters params = new PageParameters();
                params.put("key", sessionKey);
                params.put("type", typeModel.getObject());
                params.put("locale", localeModel.getObject());
                setResponsePage(DownloadPage.class, params);
            }

            protected void storeReportDownload(AbstractReportDownload reportDownload, String key) {
                WebRequestCycle webRequestCycle = (WebRequestCycle) RequestCycle.get();
                HttpSession session = webRequestCycle.getWebRequest().getHttpServletRequest().getSession();
                session.setAttribute(key, reportDownload);
            }
        });

        //Отмена
        form.add(new AjaxLink<Void>("cancel") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                dialog.close(target);
            }
        });
    }

    public void open(AjaxRequestTarget target) {
        dialog.open(target);
    }
}
