/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.information.test;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.complitex.dictionaryfw.dao.aop.SqlSessionInterceptor;
import org.complitex.dictionaryfw.entity.EntityAttribute;
import org.complitex.pspoffice.information.strategy.impl.apartment.Apartment;
import org.complitex.pspoffice.information.strategy.impl.apartment.dao.ApartmentDao;

/**
 *
 * @author Artem
 */
@Stateless
@Interceptors({SqlSessionInterceptor.class})
public class TestDAO {

    @EJB
    private ApartmentDao apartmentDao;

    public void insertApartment() {
        Apartment a = new Apartment();
        EntityAttribute name = new EntityAttribute();
        name.setAttributeId(1L);
        name.setAttributeTypeId(1L);
        name.addLocalizedValue(new org.complitex.dictionaryfw.entity.StringCulture("ru", "Name 1"));
        a.addAttribute(name);
        apartmentDao.insert(a);
    }
}
