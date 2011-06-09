/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.ownerrelationship.strategy;

import com.google.common.base.Predicate;
import static com.google.common.collect.ImmutableList.*;
import com.google.common.collect.ImmutableMap;
import static com.google.common.collect.Iterables.*;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import static com.google.common.collect.Lists.*;
import javax.ejb.Stateless;
import static org.apache.wicket.util.string.Strings.*;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.example.AttributeExample;
import org.complitex.dictionary.entity.example.DomainObjectExample;
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.strategy.DeleteException;
import static org.complitex.dictionary.util.AttributeUtil.*;
import static org.complitex.dictionary.util.ResourceUtil.*;
import org.complitex.template.strategy.TemplateStrategy;
import org.complitex.template.web.security.SecurityRole;

/**
 *
 * @author Artem
 */
@Stateless(name = "Owner_relationshipStrategy")
public class OwnerRelationshipStrategy extends TemplateStrategy {

    private static final String RESOURCE_BUNDLE = OwnerRelationshipStrategy.class.getName();
    /**
     * Attribute type ids
     */
    public static final long NAME = 2200;
    /**
     * Owner relationship instance ids
     */
    public static final long OWNER = 1;
    public static final long RESPONSIBLE = 2;
    private static final List<Long> RESERVED_INSTANCE_IDS = of(OWNER, RESPONSIBLE);

    @Override
    public String getEntityTable() {
        return "owner_relationship";
    }

    @Override
    protected List<Long> getListAttributeTypes() {
        return newArrayList(NAME);
    }

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        return getStringCultureValue(object, NAME, locale);
    }

    @Override
    public void configureExample(DomainObjectExample example, Map<String, Long> ids, String searchTextInput) {
        if (isEmpty(searchTextInput)) {
            AttributeExample attrExample = example.getAttributeExample(NAME);
            if (attrExample == null) {
                attrExample = new AttributeExample(NAME);
                example.addAttributeExample(attrExample);
            }
            attrExample.setValue(searchTextInput);
        }
    }

    @Override
    public String getPluralEntityLabel(Locale locale) {
        return getString(RESOURCE_BUNDLE, getEntityTable(), locale);
    }

    @Transactional
    public List<DomainObject> getAll() {
        DomainObjectExample example = new DomainObjectExample();
        example.setOrderByAttributeTypeId(NAME);
        configureExample(example, ImmutableMap.<String, Long>of(), null);
        return (List<DomainObject>) find(example);
    }

    @Transactional
    public List<DomainObject> getAllWithoutOwnerAndResponsible() {
        List<DomainObject> all = getAll();
        return newArrayList(filter(all, new Predicate<DomainObject>() {

            @Override
            public boolean apply(DomainObject input) {
                return !RESERVED_INSTANCE_IDS.contains(input.getId());
            }
        }));
    }

    @Override
    public String[] getEditRoles() {
        return new String[]{SecurityRole.OWNER_RELATIONSHIP_MODULE_EDIT};
    }

    @Transactional
    @Override
    protected void deleteChecks(long objectId, Locale locale) throws DeleteException {
        if (RESERVED_INSTANCE_IDS.contains(objectId)) {
            throw new DeleteException(getString(RESOURCE_BUNDLE, "delete_reserved_instance_error", locale));
        }
        super.deleteChecks(objectId, locale);
    }
}
