package org.complitex.pspoffice.report.web;

import org.complitex.pspoffice.report.entity.IReportField;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.complitex.dictionary.util.StringUtil;

public abstract class AbstractReportDownload<T extends Serializable> implements Serializable {

    private static final SimpleDateFormat FILE_POSTFIX_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private final String reportName;
    private final IReportField[] reportFields;
    private final T report;

    protected AbstractReportDownload(final String reportName, final IReportField[] reportFields, T report) {
        this.reportName = reportName;
        this.reportFields = reportFields;
        this.report = report;
    }

    protected final T getReport() {
        return report;
    }

    protected final IReportField[] getReportFields() {
        return reportFields;
    }

    protected final String getReportName() {
        return reportName;
    }

    protected final Map<IReportField, Object> newValuesMap() {
        return new HashMap<IReportField, Object>();
    }

    protected final void putMultilineValue(Map<IReportField, Object> values, String value, int lineSize, IReportField... fields) {
        if (value == null) {
            return;
        }

        String[] wrap = StringUtil.wrap(value, lineSize, "\n", true).split("\n", fields.length);
        int index = 0;
        for (IReportField field : fields) {
            if (index < wrap.length) {
                values.put(field, wrap[index++]);
            }
        }
    }

    protected abstract Map<IReportField, Object> getValues(Locale locale);

    protected abstract String getFileName(Locale locale);
}
