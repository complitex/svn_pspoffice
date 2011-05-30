///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package org.complitex.pspoffice.person.registration.report.web;
//
//import org.apache.wicket.behavior.SimpleAttributeModifier;
//import org.apache.wicket.markup.html.panel.Panel;
//import org.apache.wicket.model.AbstractReadOnlyModel;
//import org.apache.wicket.model.IModel;
//import org.apache.wicket.validation.validator.MinimumValidator;
//import org.complitex.dictionary.web.component.MonthDropDownChoice;
//import org.complitex.dictionary.web.component.hint.HintTextFieldPanel;
//
///**
// *
// * @author Artem
// */
//public final class RegistrationReportDateFilter extends Panel {
//
//    public RegistrationReportDateFilter(String id, IModel<Integer> monthModel, IModel<Integer> yearModel) {
//        super(id);
//        init(monthModel, yearModel);
//    }
//
//    private void init(IModel<Integer> monthModel, IModel<Integer> yearModel) {
//        add(new MonthDropDownChoice("month", monthModel).setNullValid(true));
//        IModel<String> placeholderModel = new AbstractReadOnlyModel<String>() {
//
//            @Override
//            public String getObject() {
//                return getString("year.placeholder");
//            }
//        };
//        HintTextFieldPanel<Integer> year = new HintTextFieldPanel<Integer>("year", yearModel, Integer.class,
//                placeholderModel, RegistrationReportDateFilter.class.getSimpleName() + ".year");
//        year.getTextField().add(new MinimumValidator<Integer>(1900));
//        year.getTextField().add(new SimpleAttributeModifier("size", String.valueOf(4))).
//                add(new SimpleAttributeModifier("maxlength", String.valueOf(4)));
//        add(year);
//    }
//}
