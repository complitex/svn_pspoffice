/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.dictionaryfw.util;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionaryfw.dao.LocaleBean;
import org.complitex.dictionaryfw.entity.StringCulture;

/**
 *
 * @author Artem
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class DisplayLocalizedValueUtil {

    @EJB
    private LocaleBean localeDao;

    public String displayValue(List<StringCulture> strings, final Locale locale) {
        String value = null;
        try {
            value = Iterables.find(strings, new Predicate<StringCulture>() {

                @Override
                public boolean apply(StringCulture string) {
                    return locale.getLanguage().equalsIgnoreCase(string.getLocale());

                }
            }).getValue();

        } catch (NoSuchElementException e) {
        }
        if (Strings.isEmpty(value)) {
            try {
                value = Iterables.find(strings, new Predicate<StringCulture>() {

                    @Override
                    public boolean apply(StringCulture string) {
                        return localeDao.getSystemLocale().equalsIgnoreCase(string.getLocale());
                    }
                }).getValue();
            } catch (NoSuchElementException e) {
            }
        }
        return value;
    }
}
