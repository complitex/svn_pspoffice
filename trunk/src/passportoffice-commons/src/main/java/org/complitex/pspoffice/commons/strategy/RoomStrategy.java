/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.commons.strategy;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.Locale;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.complitex.dictionaryfw.dao.aop.SqlSessionInterceptor;
import org.complitex.dictionaryfw.entity.DomainObject;
import org.complitex.dictionaryfw.entity.EntityAttribute;
import org.complitex.dictionaryfw.entity.description.AttributeDescription;
import org.complitex.dictionaryfw.entity.description.DomainObjectDescription;
import org.complitex.dictionaryfw.strategy.Strategy;
import org.complitex.dictionaryfw.util.DisplayLocalizedValueUtil;

/**
 *
 * @author Artem
 */
@Stateless
@Interceptors({SqlSessionInterceptor.class})
public class RoomStrategy extends Strategy {

    @EJB
    private DisplayLocalizedValueUtil displayLocalizedValueUtil;

    private AttributeDescription nameAttrDesc;

    @Override
    public String getEntityTable() {
        return "room";
    }

    @Override
    public DomainObjectDescription getDescription() {
        DomainObjectDescription description = super.getDescription();

        int minAttributeTypeId = Integer.MAX_VALUE;
        for (AttributeDescription attrDesc : description.getSimpleAttributeDescs()) {
            if (attrDesc.getId() < minAttributeTypeId) {
                nameAttrDesc = attrDesc;
            }
        }
        description.setFilterAttributes(Lists.newArrayList(nameAttrDesc));

        return description;
    }

    @Override
    public String dysplayDomainObject(DomainObject object, Locale locale) {
        return displayLocalizedValueUtil.displayValue(
                Iterables.find(object.getAttributes(), new Predicate<EntityAttribute>() {

            @Override
            public boolean apply(EntityAttribute attr) {
                return attr.getAttributeTypeId().equals(nameAttrDesc.getId());
            }
        }).getLocalizedValues(), locale);
    }
}
