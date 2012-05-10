package org.complitex.pspoffice.report.web;

import java.io.OutputStream;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.ajax.calldecorator.AjaxCallDecorator;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.odlabs.wiquery.ui.dialog.Dialog;

import java.util.Arrays;
import java.util.Locale;
import javax.servlet.http.HttpSession;
import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.apache.wicket.request.target.resource.ResourceStreamRequestTarget;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.apache.wicket.util.resource.IResourceStream;
import org.complitex.pspoffice.report.service.CreateReportException;
import org.complitex.pspoffice.report.util.ReportGenerationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportDownloadPanel extends Panel {

    private static final Logger log = LoggerFactory.getLogger(ReportDownloadPanel.class);
    public static final String RUSSIAN_REPORT_LOCALE = "ru_RU";
    public static final String UKRAIN_REPORT_LOCALE = "uk_UA";
    public static final String PDF_REPORT_FORMAT = "PDF";
    public static final String RTF_REPORT_FORMAT = "RTF";
    private Dialog dialog;

    private class AttributeModifier extends AbstractBehavior {

        private final String attribute;
        private final String valueKey;

        private AttributeModifier(String attribute, String valueKey) {
            this.attribute = attribute;
            this.valueKey = valueKey;
        }

        @Override
        public void onComponentTag(final Component component, final ComponentTag tag) {
            if (isEnabled(component)) {
                tag.getAttributes().put(attribute, ReportDownloadPanel.this.getString(valueKey));
            }
        }
    }

    public static class DownloadPage extends Page {

        public DownloadPage(PageParameters parameters) {
            String sessionKey = parameters.getString("key");
            String type = parameters.getString("type") != null ? parameters.getString("type").toLowerCase()
                    : PDF_REPORT_FORMAT.toLowerCase();
            String locale = parameters.getString("locale") != null ? parameters.getString("locale")
                    : RUSSIAN_REPORT_LOCALE;

            AbstractReportDownload<?> reportDownload = retrieveReportDownload(sessionKey);
            getRequestCycle().setRequestTarget(getResourceStreamRequestTarget(reportDownload, type, locale));
        }

        private AbstractReportDownload<?> retrieveReportDownload(String key) {
            WebRequestCycle webRequestCycle = (WebRequestCycle) RequestCycle.get();
            HttpSession session = webRequestCycle.getWebRequest().getHttpServletRequest().getSession();
            AbstractReportDownload<?> reportDownload = (AbstractReportDownload) session.getAttribute(key);
            session.removeAttribute(key);
            return reportDownload;
        }

        private ResourceStreamRequestTarget getResourceStreamRequestTarget(AbstractReportDownload<?> reportDownload,
                String type, String locale) {
            return new ResourceStreamRequestTarget(getResourceStreamWriter(reportDownload, type, locale),
                    reportDownload.getFileName(ReportGenerationUtil.getLocale(locale)));
        }

        private IResourceStream getResourceStreamWriter(final AbstractReportDownload<?> reportDownload, final String type,
                final String locale) {
            return new AbstractResourceStreamWriter() {

                @Override
                public String getContentType() {
                    if (PDF_REPORT_FORMAT.equalsIgnoreCase(type)) {
                        return "application/pdf";
                    } else if (RTF_REPORT_FORMAT.equalsIgnoreCase(type)) {
                        return "application/rtf";
                    }
                    return null;
                }

                @Override
                public void write(OutputStream output) {
                    try {
                        ReportGenerationUtil.write(type, reportDownload, output, locale);
                    } catch (CreateReportException e) {
                        log.error("Couldn't create report.", e);
                    }
                }
            };
        }
    }

    public ReportDownloadPanel(String id, String title, final AbstractReportDownload<?> reportDownload, final boolean print) {
        super(id);

        dialog = new Dialog("dialog");
        dialog.setModal(true);
        dialog.setWidth(420);
        dialog.setTitle(title);
        dialog.setMinHeight(0);
        add(dialog);

        final Form<Void> form = new Form<Void>("form");
        dialog.add(form);

        final WebMarkupContainer typeContainer = new WebMarkupContainer("typeContainer");
        typeContainer.setVisible(!print);
        form.add(typeContainer);

        final IModel<String> typeModel = new Model<String>(PDF_REPORT_FORMAT);
        final IModel<String> localeModel = new Model<String>(getLocale().equals(new Locale("ru"))
                ? RUSSIAN_REPORT_LOCALE
                : UKRAIN_REPORT_LOCALE);

        typeContainer.add(new DropDownChoice<String>("type", typeModel,
                Arrays.asList(PDF_REPORT_FORMAT, RTF_REPORT_FORMAT)));

        form.add(new DropDownChoice<String>("locale", localeModel,
                Arrays.asList(RUSSIAN_REPORT_LOCALE, UKRAIN_REPORT_LOCALE),
                new IChoiceRenderer<String>() {

                    @Override
                    public Object getDisplayValue(String object) {
                        if (RUSSIAN_REPORT_LOCALE.equals(object)) {
                            return getString("ru");
                        }
                        if (UKRAIN_REPORT_LOCALE.equals(object)) {
                            return getString("uk");
                        }
                        return null;
                    }

                    @Override
                    public String getIdValue(String object, int index) {
                        return object;
                    }
                }));

        final WebMarkupContainer printContainer = new WebMarkupContainer("printContainer");
        printContainer.setOutputMarkupId(true);
        printContainer.setVisible(print);
        add(printContainer);
        final IModel<String> printKeyModel = new Model<String>();
        printContainer.add(new HiddenField<String>("printKey", printKeyModel).add(new SimpleAttributeModifier("name", "key")));
        printContainer.add(new HiddenField<String>("printLocale", localeModel).add(new SimpleAttributeModifier("name", "locale")));

        //Загрузить
        AjaxButton download = new AjaxButton("download", form) {

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
                final String sessionKey = reportDownload.getFileName(ReportGenerationUtil.getLocale(localeModel.getObject()));
                storeReportDownload(reportDownload, sessionKey);

                if (!print) {
                    PageParameters params = new PageParameters();
                    params.put("key", sessionKey);
                    params.put("type", typeModel.getObject());
                    params.put("locale", localeModel.getObject());
                    setResponsePage(DownloadPage.class, params);
                } else {
                    printKeyModel.setObject(sessionKey);
                    target.addComponent(printContainer);
                    target.appendJavascript("(function(){ $('#printForm').submit();})()");
                }
            }

            private void storeReportDownload(AbstractReportDownload<?> reportDownload, String key) {
                WebRequestCycle webRequestCycle = (WebRequestCycle) RequestCycle.get();
                HttpSession session = webRequestCycle.getWebRequest().getHttpServletRequest().getSession();
                session.setAttribute(key, reportDownload);
            }
        };
        download.add(new AttributeModifier("value", print ? "print" : "download"));
        form.add(download);

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
