/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.passportoffice.information.strategy.impl.apartment.dao;

import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.apache.ibatis.session.SqlSession;
import org.apache.wicket.util.string.Strings;
import org.passportoffice.commons.dao.AbstractEntityDao;
import org.passportoffice.commons.dao.EntityDescriptionDao;
import org.passportoffice.commons.dao.LocaleDao;
import org.passportoffice.commons.dao.SequenceDao;
import org.passportoffice.commons.dao.aop.SqlSessionInterceptor;
import org.passportoffice.commons.entity.AttributeDescription;
import org.passportoffice.commons.entity.AttributeValueDescription;
import org.passportoffice.commons.entity.Entity;
import org.passportoffice.commons.entity.EntityAttribute;
import org.passportoffice.commons.entity.EntityDescription;
import org.passportoffice.commons.entity.InsertParameter;
import org.passportoffice.commons.entity.SimpleTypes;
import org.passportoffice.commons.entity.StatusType;
import org.passportoffice.commons.entity.StringCulture;
import org.passportoffice.commons.entity.example.IEntityExample;
import org.passportoffice.commons.strategy.IDao;
import org.passportoffice.information.strategy.impl.apartment.Apartment;
import org.passportoffice.information.strategy.impl.apartment.example.ApartmentExample;

/**
 *
 * @author Artem
 */
@Stateless
@LocalBean
@Interceptors({SqlSessionInterceptor.class})
public class ApartmentDao extends AbstractEntityDao<Apartment, ApartmentExample> {

    public static final String TABLE_NAME = "apartment";

    public static enum OrderBy {

        NAME;
    }

    @Override
    protected String getNamespace() {
        return "org.passportoffice.information.strategy.impl.apartment.Apartment";
    }

    @Override
    public Apartment newInstance() {
        Apartment apartment = new Apartment();
        configureNewEntity(apartment);
        return apartment;
    }

    @Override
    public String getTable() {
        return TABLE_NAME;
    }
}
