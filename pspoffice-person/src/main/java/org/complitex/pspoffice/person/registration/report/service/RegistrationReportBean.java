/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.registration.report.service;

import java.util.List;
import javax.ejb.Stateless;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.pspoffice.person.registration.report.entity.RegistrationReport;
import org.complitex.pspoffice.person.registration.report.example.RegistrationReportExample;

/**
 *
 * @author Artem
 */
@Stateless
public class RegistrationReportBean extends AbstractBean {

    private static final String REGISTRATION_REPORT_MAPPING = RegistrationReportBean.class.getName();

    public static enum OrderBy {

        LAST_NAME("last_name"), FIRST_NAME("first_name"), MIDDLE_NAME("middle_name"), START_DATE("start_date"), END_DATE("end_date");
        private String orderByExpression;

        private OrderBy(String orderByExpression) {
            this.orderByExpression = orderByExpression;
        }

        public String getOrderByExpression() {
            return orderByExpression;
        }
    }

    public List<RegistrationReport> getReport(RegistrationReportExample example) {
        return sqlSession().selectList(REGISTRATION_REPORT_MAPPING + ".getReport", example);
    }

    public int count(RegistrationReportExample example) {
        return (Integer) sqlSession().selectOne(REGISTRATION_REPORT_MAPPING + ".count", example);
    }
}
