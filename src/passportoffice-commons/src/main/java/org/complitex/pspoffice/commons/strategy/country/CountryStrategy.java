/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.commons.strategy.country;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionaryfw.dao.aop.SqlSessionInterceptor;
import org.complitex.dictionaryfw.entity.DomainObject;
import org.complitex.dictionaryfw.entity.Attribute;
import org.complitex.dictionaryfw.entity.description.AttributeDescription;
import org.complitex.dictionaryfw.entity.description.DomainObjectDescription;
import org.complitex.dictionaryfw.entity.example.DomainObjectAttributeExample;
import org.complitex.dictionaryfw.entity.example.DomainObjectExample;
import org.complitex.dictionaryfw.strategy.Strategy;
import org.complitex.dictionaryfw.util.DisplayLocalizedValueUtil;
import org.complitex.dictionaryfw.web.component.search.ISearchCallback;

/**
 *
 * @author Artem
 */
@Stateless
@Interceptors({SqlSessionInterceptor.class})
public class CountryStrategy extends Strategy {

    @EJB
    private DisplayLocalizedValueUtil displayLocalizedValueUtil;

    private static final long NAME_ATTRIBUTE_TYPE_ID = 800L;

    @Override
    public boolean isSimpleAttributeDesc(AttributeDescription attributeDescription) {
        return attributeDescription.getId() >= NAME_ATTRIBUTE_TYPE_ID;
    }

    @Override
    public DomainObjectDescription getDescription() {
        DomainObjectDescription description = super.getDescription();

        description.setFilterAttributes(Lists.newArrayList(Iterables.filter(description.getAttributeDescriptions(), new Predicate<AttributeDescription>() {

            @Override
            public boolean apply(AttributeDescription attrDesc) {
                return attrDesc.getId().equals(NAME_ATTRIBUTE_TYPE_ID);
            }
        })));

        return description;
    }

    @Override
    public String getEntityTable() {
        return "country";
    }

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        return displayLocalizedValueUtil.displayValue(Iterables.find(object.getAttributes(), new Predicate<Attribute>() {

            @Override
            public boolean apply(Attribute attr) {
                return attr.getAttributeTypeId().equals(NAME_ATTRIBUTE_TYPE_ID);
            }
        }).getLocalizedValues(), locale);
    }

    @Override
    public ISearchCallback getSearchCallback() {
        return null;
    }

    @Override
    public void configureExample(DomainObjectExample example, Map<String, Long> ids, String searchTextInput) {
        configureExampleImpl(example, ids, searchTextInput);
    }

    private static void configureExampleImpl(DomainObjectExample example, Map<String, Long> ids, String searchTextInput) {
        if (!Strings.isEmpty(searchTextInput)) {
            DomainObjectAttributeExample attrExample = null;
            try {
                attrExample = Iterables.find(example.getAttributeExamples(), new Predicate<DomainObjectAttributeExample>() {

                    @Override
                    public boolean apply(DomainObjectAttributeExample attrExample) {
                        return attrExample.getAttributeTypeId().equals(NAME_ATTRIBUTE_TYPE_ID);
                    }
                });
            } catch (NoSuchElementException e) {
                attrExample = new DomainObjectAttributeExample(NAME_ATTRIBUTE_TYPE_ID);
                example.addAttributeExample(attrExample);
            }
            attrExample.setValue(searchTextInput);
        }
    }

    @Override
    public List<String> getSearchFilters() {
        return null;
    }

    @Override
    public ISearchCallback getParentSearchCallback() {
        return null;
    }

    @Override
    public Map<String, String> getChildrenInfo(Locale locale) {
        return ImmutableMap.of("region", "Regions");
    }
}
