package org.complitex.pspoffice.report.web;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxCallDecorator;
import org.apache.wicket.ajax.calldecorator.AjaxCallDecorator;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.odlabs.wiquery.ui.dialog.Dialog;

import java.util.Arrays;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 02.06.11 15:25
 */
public class ReportDownloadPanel extends Panel {
    private Dialog dialog;

    public ReportDownloadPanel(String id, final Class<? extends AbstractReportDownload> reportClass, final Long objectId, String title) {
        super(id);

        dialog = new Dialog("dialog");
        dialog.setModal(true);
        dialog.setWidth(420);
        dialog.setMinHeight(100);
        dialog.setOutputMarkupId(true);
        dialog.setOutputMarkupPlaceholderTag(true);
        dialog.setTitle(title);
        add(dialog);

        Form form = new Form("form");
        dialog.add(form);

        final IModel<String> typeModel = new Model<String>("PDF");

        form.add(new DropDownChoice<String>("type", typeModel, Arrays.asList("PDF", "RTF", "ODT")));

        //Загрузить
        form.add(new AjaxButton("download"){

            @Override
            protected IAjaxCallDecorator getAjaxCallDecorator() {
                return new AjaxCallDecorator(){
                    @Override
                    public CharSequence decorateScript(CharSequence script) {
                        return dialog.close().render().toString() + script;
                    }
                };
            }

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                PageParameters pageParameters = new PageParameters();
                pageParameters.add("type", typeModel.getObject().toLowerCase());
                pageParameters.add("object_id", objectId.toString());

                dialog.close(target);

                setResponsePage(reportClass, pageParameters);
            }
        });

        //Отмена
        form.add(new AjaxButton("cancel") {
            {
                setDefaultFormProcessing(false);
            }

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form) {
                dialog.close(target);
            }
        });
    }

     public void open(AjaxRequestTarget target){
         dialog.open(target);
     }
}
