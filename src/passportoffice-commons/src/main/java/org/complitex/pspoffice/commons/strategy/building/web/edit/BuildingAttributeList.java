/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.commons.strategy.building.web.edit;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import org.complitex.dictionaryfw.entity.DomainObject;
import org.complitex.dictionaryfw.entity.EntityAttribute;
import org.complitex.dictionaryfw.entity.StringCulture;

/**
 *
 * @author Artem
 */
public class BuildingAttributeList extends AbstractList<BuildingAttribute> implements Serializable {

    private DomainObject object;

    private List<BuildingAttribute> buildingAttributes = Lists.newArrayList();

    private List<String> locales;

    public BuildingAttributeList(DomainObject object, List<String> locales) {
        this.object = object;
        this.locales = locales;
        Iterable<EntityAttribute> suitedAttributes = Iterables.filter(object.getAttributes(), new Predicate<EntityAttribute>() {

            @Override
            public boolean apply(EntityAttribute attr) {
                Long attributeTypeId = attr.getAttributeTypeId();
                return attributeTypeId.equals(500L) || attributeTypeId.equals(501L) || attributeTypeId.equals(502L) || attributeTypeId.equals(503L);
            }
        });
        Set<Long> attributeIds = Sets.newTreeSet(Iterables.transform(suitedAttributes, new Function<EntityAttribute, Long>() {

            @Override
            public Long apply(EntityAttribute attr) {
                return attr.getAttributeId();
            }
        }));
        for (Long attributeId : attributeIds) {
            buildingAttributes.add(new BuildingAttribute(attributeId,
                    findEntityAttribute(suitedAttributes, attributeId, 500L),
                    findEntityAttribute(suitedAttributes, attributeId, 501L),
                    findEntityAttribute(suitedAttributes, attributeId, 502L),
                    findEntityAttribute(suitedAttributes, attributeId, 503L)));

        }

    }

    private static EntityAttribute findEntityAttribute(Iterable<EntityAttribute> suitedAttributes, final long attributeId, final long attributeTypeId) {
        return Iterables.find(suitedAttributes, new Predicate<EntityAttribute>() {

            @Override
            public boolean apply(EntityAttribute attr) {
                return attr.getAttributeId().equals(attributeId) && attr.getAttributeTypeId().equals(attributeTypeId);
            }
        });
    }

    public void addNew() {
        add(newBuildingAttribute());
    }

    private BuildingAttribute newBuildingAttribute() {
        long attributeId = calculateMaxAttributeId() + 1;
        BuildingAttribute buildingAttribute = new BuildingAttribute(attributeId,
                newEntityAttribute(attributeId, 500, 500),
                newEntityAttribute(attributeId, 501, 501),
                newEntityAttribute(attributeId, 502, 502),
                newStreetAttribute(attributeId));
        return buildingAttribute;
    }

    private EntityAttribute newEntityAttribute(long attributeId, long attributeTypeId, long attributeValueId) {
        EntityAttribute attribute = new EntityAttribute();
        attribute.setObjectId(object.getId());
        attribute.setAttributeTypeId(attributeTypeId);
        attribute.setValueTypeId(attributeValueId);
        attribute.setAttributeId(attributeId);
        for (String locale : locales) {
            attribute.addLocalizedValue(new StringCulture(locale, null));
        }
        object.addAttribute(attribute);
        return attribute;
    }

    private EntityAttribute newStreetAttribute(long attributeId){
        EntityAttribute attribute = new EntityAttribute();
        attribute.setObjectId(object.getId());
        attribute.setAttributeTypeId(503L);
        attribute.setValueTypeId(503L);
        attribute.setAttributeId(attributeId);
        for (String locale : locales) {
            attribute.addLocalizedValue(new StringCulture(locale, null));
        }
        object.addAttribute(attribute);
        return attribute;
    }

    private long calculateMaxAttributeId() {
        try {
            return Collections.max(Collections2.transform(buildingAttributes, new Function<BuildingAttribute, Long>() {

                @Override
                public Long apply(BuildingAttribute buildingAttribute) {
                    return buildingAttribute.getAttributeId();
                }
            }));
        } catch (NoSuchElementException e) {
            return 0;
        }

    }

    @Override
    public BuildingAttribute get(int index) {
        return buildingAttributes.get(index);
    }

    @Override
    public int size() {
        return buildingAttributes.size();
    }

    @Override
    public void add(int index, BuildingAttribute element) {
        buildingAttributes.add(index, element);
    }

    @Override
    public BuildingAttribute remove(int index) {
        final BuildingAttribute toRemove = buildingAttributes.get(index);
        List<EntityAttribute> attrs = Lists.newArrayList(Iterables.filter(object.getAttributes(), new Predicate<EntityAttribute>() {

            @Override
            public boolean apply(EntityAttribute attr) {
                Long attributeTypeId = attr.getAttributeTypeId();
                return attr.getAttributeId().equals(toRemove.getAttributeId())
                        && (attributeTypeId.equals(500L) || attributeTypeId.equals(501L) 
                            || attributeTypeId.equals(502L) || attributeTypeId.equals(503L));
            }
        }));
        object.getAttributes().removeAll(attrs);
        return buildingAttributes.remove(index);
    }

    @Override
    public BuildingAttribute set(int index, BuildingAttribute element) {
        return buildingAttributes.set(index, element);
    }
}
