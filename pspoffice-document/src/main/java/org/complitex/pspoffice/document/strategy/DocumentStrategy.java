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
import org.complitex.dictionary.mybatis.Transactional;
import org.complitex.dictionary.service.StringCultureBean;
import org.complitex.dictionary.util.StringUtil;
import static org.complitex.dictionary.util.AttributeUtil.*;
import org.complitex.pspoffice.document.strategy.entity.Document;
import org.complitex.pspoffice.document.strategy.entity.Passport;
import org.complitex.pspoffice.document_type.strategy.DocumentTypeStrategy;
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
            putAll(DocumentTypeStrategy.PASSPORT, 2811L, 2812L, 2813L, 2814L).
            putAll(DocumentTypeStrategy.BIRTH_CERTIFICATE, 2815L, 2816L, 2817L).build();
    @EJB
    private StringCultureBean stringBean;

    @Override
    public DomainObject newInstance() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Transactional
    public void disable(Document document, Date endDate) {
        document.setEndDate(endDate);
        changeActivity(document, false);
    }

    @Transactional
    public Document findById(long id) {
        DomainObject object = super.findById(id, true);
        if (object != null) {
            Document doc = new Document(object);
            if (doc.getDocumentTypeId() == DocumentTypeStrategy.PASSPORT) {
                return new Passport(doc);
            } else {
                return doc;
            }
        } else {
            return null;
        }
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

        if (document.getDocumentTypeId() == DocumentTypeStrategy.PASSPORT) {
            return new Passport(document);
        } else {
            return document;
        }
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
        Document document = (Document) object;

        long documentTypeId = document.getDocumentTypeId();
        if (documentTypeId == DocumentTypeStrategy.PASSPORT) {
            Passport passport = (Passport) document;
            return passport.getSeries() + " " + passport.getNumber();
        } else if (documentTypeId == DocumentTypeStrategy.BIRTH_CERTIFICATE) {
            return StringUtil.valueOf(getStringValue(document, 2815));
        } else {
            throw new IllegalStateException("Unknown document type: " + documentTypeId);
        }
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
