/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.document.strategy.entity;

import org.complitex.dictionary.entity.Attribute;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.pspoffice.document_type.strategy.DocumentTypeStrategy;
import static org.complitex.pspoffice.document.strategy.DocumentStrategy.*;

/**
 *
 * @author Artem
 */
public class Document extends DomainObject {

    private DomainObject documentType;

    public Document() {
    }

    public Document(DomainObject object) {
        super(object);
    }

    public long getDocumentTypeId() {
        Attribute documentTypeAttribute = getAttribute(DOCUMENT_TYPE);
        return documentTypeAttribute != null ? documentTypeAttribute.getValueId() : null;
    }

    public DomainObject getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DomainObject documentType) {
        this.documentType = documentType;
    }

    public boolean isChildrenDocument() {
        return DocumentTypeStrategy.isChildrenDocumentType(getDocumentTypeId());
    }

    public boolean isAdultDocument() {
        return DocumentTypeStrategy.isAdultDocumentType(getDocumentTypeId());
    }
}
