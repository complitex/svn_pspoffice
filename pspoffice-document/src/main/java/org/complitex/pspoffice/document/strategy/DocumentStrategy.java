/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.document.strategy;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import static com.google.common.collect.Lists.*;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.description.EntityAttributeType;
import org.complitex.dictionary.entity.description.EntityAttributeValueType;
import org.complitex.dictionary.service.StringCultureBean;
import org.complitex.pspoffice.document.strategy.entity.Document;
import org.complitex.template.strategy.TemplateStrategy;
import org.complitex.template.web.security.SecurityRole;

/**
 *
 * @author Artem
 */
@Stateless
public class DocumentStrategy extends TemplateStrategy {

    /**
     * Common attribute type ids
     */
    public static final long DOCUMENT_TYPE = 2800;
    /**
     * Document type to attributes map
     */
    private static final Multimap<Long, Long> DOCUMENT_TYPE_TO_ATTRIBUTES_MAP = ImmutableMultimap.<Long, Long>builder().
            putAll(1L, 2811L, 2812L, 2813L, 2814L).
            putAll(2L, 2815L, 2816L, 2817L).build();
    @EJB
    private StringCultureBean stringBean;

    @Override
    public DomainObject newInstance() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Document findById(long id, boolean runAsAdmin) {
        DomainObject object = super.findById(id, runAsAdmin);
        if (object == null) {
            return null;
        }
        return new Document(object);
    }

    public Document newInstance(long documentTypeId) {
        Document document = new Document();
        Attribute documentTypeAttribute = new Attribute();
        documentTypeAttribute.setAttributeId(1L);
        documentTypeAttribute.setAttributeTypeId(DOCUMENT_TYPE);
        documentTypeAttribute.setValueId(documentTypeId);
        documentTypeAttribute.setValueTypeId(DOCUMENT_TYPE);
        document.addAttribute(documentTypeAttribute);
        fillAttributes(document);
        return document;
    }

    @Override
    public Document findHistoryObject(long objectId, Date date) {
        DomainObject object = super.findHistoryObject(objectId, date);
        return new Document(object);
    }

    private boolean isSupportedAttribute(long attributeTypeId, long documentTypeId) {
        return DOCUMENT_TYPE_TO_ATTRIBUTES_MAP.get(documentTypeId).contains(attributeTypeId);
    }

    @Override
    protected void fillAttributes(DomainObject document) {
        long documentTypeId = document.getAttribute(DOCUMENT_TYPE).getValueId();

        List<Attribute> toAdd = newArrayList();

        for (EntityAttributeType attributeType : getEntity().getEntityAttributeTypes()) {
            if (document.getAttributes(attributeType.getId()).isEmpty()
                    && (attributeType.getEntityAttributeValueTypes().size() == 1)
                    && !attributeType.isObsolete()
                    && !attributeType.getId().equals(DOCUMENT_TYPE)
                    && isSupportedAttribute(attributeType.getId(), documentTypeId)) {
                Attribute attribute = new Attribute();
                EntityAttributeValueType attributeValueType = attributeType.getEntityAttributeValueTypes().get(0);
                attribute.setAttributeTypeId(attributeType.getId());
                attribute.setValueTypeId(attributeValueType.getId());
                attribute.setObjectId(document.getId());
                attribute.setAttributeId(1L);

                if (isSimpleAttributeType(attributeType)) {
                    attribute.setLocalizedValues(stringBean.newStringCultures());
                }
                toAdd.add(attribute);
            }
        }
        if (!toAdd.isEmpty()) {
            document.getAttributes().addAll(toAdd);
        }
    }

    @Override
    public String displayDomainObject(DomainObject object, Locale locale) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getEntityTable() {
        return "document";
    }

    @Override
    public String[] getEditRoles() {
        return new String[]{SecurityRole.PERSON_MODULE_EDIT};
    }
}
