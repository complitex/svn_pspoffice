/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.legacy_import.service;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.example.AttributeExample;
import org.complitex.dictionary.entity.example.ComparisonType;
import org.complitex.dictionary.entity.example.DomainObjectExample;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.dictionary.strategy.IStrategy;
import org.complitex.dictionary.strategy.StrategyFactory;
import org.complitex.pspoffice.document_type.strategy.DocumentTypeStrategy;
import org.complitex.pspoffice.legacy_import.entity.ReferenceDataCorrection;
import org.complitex.pspoffice.legacy_import.service.exception.TooManyResultsException;
import org.complitex.pspoffice.registration_type.strategy.RegistrationTypeStrategy;

/**
 *
 * @author Artem
 */
@Stateless
public class ReferenceDataCorrectionBean extends AbstractBean {

    private static final String MAPPING_NAMESPACE = ReferenceDataCorrectionBean.class.getName();
    /**
     * Owner relationships consts
     */
    public static final long OWNER = 85;
    public static final long DAUGHTER = 3;
    public static final long SON = 4;
    /**
     * Registration types consts
     */
    public static final long PERMANENT = 1;
    public static final long TEMPORAL = 2;
    /**
     * Document types consts
     */
    public static final long PASSPORT = 7;
    public static final long BIRTH_CERTIFICATE = 10;
    /**
     * Owner type consts
     */
    public static final long OWNER_TYPE = 1;
    @EJB
    private StrategyFactory strategyFactory;

    public void insert(ReferenceDataCorrection referenceDataCorrection) {
        sqlSession().insert(MAPPING_NAMESPACE + ".insert", referenceDataCorrection);
    }

    public void update(ReferenceDataCorrection referenceDataCorrection) {
        sqlSession().update(MAPPING_NAMESPACE + ".update", referenceDataCorrection);
    }

    public boolean exists(String entity, String idjek) {
        return (Integer) sqlSession().selectOne(MAPPING_NAMESPACE + ".exists",
                ImmutableMap.of("entity", entity, "idjek", idjek)) > 0;
    }

    public void cleanData(String entity, Set<String> jekIds) {
        sqlSession().delete(MAPPING_NAMESPACE + ".delete", ImmutableMap.of("entity", entity, "jekIds", jekIds));
    }

    public int countForProcessing(String entity, Set<String> jekIds) {
        return (Integer) sqlSession().selectOne(MAPPING_NAMESPACE + ".countForProcessingForJeks",
                ImmutableMap.of("entity", entity, "jekIds", jekIds));
    }

    public int countForProcessing(String entity, String idjek) {
        return (Integer) sqlSession().selectOne(MAPPING_NAMESPACE + ".countForProcessing",
                ImmutableMap.of("entity", entity, "idjek", idjek));
    }

    public List<ReferenceDataCorrection> findForProcessing(String entity, String idjek, int size) {
        return sqlSession().selectList(MAPPING_NAMESPACE + ".findForProcessing",
                ImmutableMap.of("entity", entity, "idjek", idjek, "size", size));
    }

    public void clearProcessingStatus(String entity, Set<String> jekIds) {
        sqlSession().update(MAPPING_NAMESPACE + ".clearProcessingStatus",
                ImmutableMap.of("entity", entity, "jekIds", jekIds));
    }

    public Long findSystemObject(String entity, String nkod) throws TooManyResultsException {
        IStrategy strategy = strategyFactory.getStrategy(entity);
        long nameAttributeTypeId = strategy.getEntity().getId();

        DomainObjectExample example = new DomainObjectExample();
        example.setAdmin(true);
        AttributeExample nameExample = new AttributeExample(nameAttributeTypeId);
        nameExample.setValue(nkod);
        example.addAttributeExample(nameExample);
        example.setComparisonType(ComparisonType.EQUALITY.name());

        List<? extends DomainObject> objects = strategy.find(example);
        if (objects.size() == 1) {
            return objects.get(0).getId();
        } else if (objects.isEmpty()) {
            return null;
        } else {
            throw new TooManyResultsException();
        }
    }

    public static class OwnerRelationshipsNotResolved extends Exception {

        private final boolean ownerResolved;
        private final boolean daughterResolved;
        private final boolean sonResolved;

        public OwnerRelationshipsNotResolved(boolean ownerResolved, boolean daughterResolved, boolean sonResolved) {
            this.ownerResolved = ownerResolved;
            this.daughterResolved = daughterResolved;
            this.sonResolved = sonResolved;
        }

        public boolean isDaughterResolved() {
            return daughterResolved;
        }

        public boolean isOwnerResolved() {
            return ownerResolved;
        }

        public boolean isSonResolved() {
            return sonResolved;
        }
    }

    public void checkReservedOwnerRelationships(Set<String> jekIds) throws OwnerRelationshipsNotResolved {
        final String entity = "owner_relationship";
        final boolean ownerResolved = isReservedObjectResolved(entity, OWNER, jekIds);
        final boolean daughterResolved = isReservedObjectResolved(entity, DAUGHTER, jekIds);
        final boolean sonResolved = isReservedObjectResolved(entity, SON, jekIds);
        if (!ownerResolved || !daughterResolved || !sonResolved) {
            throw new OwnerRelationshipsNotResolved(ownerResolved, daughterResolved, sonResolved);
        }
    }

    private boolean isReservedObjectResolved(String entity, long objectId, Set<String> jekIds) {
        return (Integer) sqlSession().selectOne(MAPPING_NAMESPACE + ".isReservedObjectResolved",
                ImmutableMap.of("entity", entity, "objectId", objectId, "jekIds", jekIds)) == 1;
    }

    public static class RegistrationTypesNotResolved extends Exception {

        private final boolean permanentResolved;
        private final boolean temporalResolved;

        public RegistrationTypesNotResolved(boolean permanentResolved, boolean temporalResolved) {
            this.permanentResolved = permanentResolved;
            this.temporalResolved = temporalResolved;
        }

        public boolean isPermanentResolved() {
            return permanentResolved;
        }

        public boolean isTemporalResolved() {
            return temporalResolved;
        }
    }

    public void checkReservedRegistrationTypes(Set<String> jekIds) throws RegistrationTypesNotResolved {
        final String entity = "registration_type";
        final boolean permanentResolved = isReservedObjectResolved(entity, PERMANENT, jekIds);
        final boolean temporalResolved = isReservedObjectResolved(entity, TEMPORAL, jekIds);
        if (!permanentResolved || !temporalResolved) {
            throw new RegistrationTypesNotResolved(permanentResolved, temporalResolved);
        }
    }

    public static class DocumentTypesNotResolved extends Exception {

        private final boolean passportResolved;
        private final boolean birthCertificateResolved;

        public DocumentTypesNotResolved(boolean passportResolved, boolean birthCertificateResolved) {
            this.passportResolved = passportResolved;
            this.birthCertificateResolved = birthCertificateResolved;
        }

        public boolean isBirthCertificateResolved() {
            return birthCertificateResolved;
        }

        public boolean isPassportResolved() {
            return passportResolved;
        }
    }

    public void putReservedDocumentTypes(Set<String> jekIds) {
        final String operation = ".putReservedDocumentType";
        sqlSession().update(MAPPING_NAMESPACE + operation,
                ImmutableMap.of("id", PASSPORT, "systemObjectId", DocumentTypeStrategy.PASSPORT,
                "jekIds", jekIds));
        sqlSession().update(MAPPING_NAMESPACE + operation,
                ImmutableMap.of("id", BIRTH_CERTIFICATE, "systemObjectId", DocumentTypeStrategy.BIRTH_CERTIFICATE,
                "jekIds", jekIds));
    }

    public void putReservedRegistrationTypes(Set<String> jekIds) {
        final String operation = ".putReservedRegistrationType";
        sqlSession().update(MAPPING_NAMESPACE + operation,
                ImmutableMap.of("id", PERMANENT, "systemObjectId", RegistrationTypeStrategy.PERMANENT,
                "jekIds", jekIds));
        sqlSession().update(MAPPING_NAMESPACE + operation,
                ImmutableMap.of("id", TEMPORAL, "systemObjectId", RegistrationTypeStrategy.TEMPORAL,
                "jekIds", jekIds));
    }

    public void checkReservedDocumentTypes(Set<String> jekIds) throws DocumentTypesNotResolved {
        final String entity = "document_type";
        final boolean passportResolved = isReservedObjectResolved(entity, PASSPORT, jekIds);
        final boolean birthCertificateResolved = isReservedObjectResolved(entity, BIRTH_CERTIFICATE, jekIds);
        if (!passportResolved || !birthCertificateResolved) {
            throw new DocumentTypesNotResolved(passportResolved, birthCertificateResolved);
        }
    }

    public String getReservedOwnerType(Set<String> jekIds) {
        List<String> ownerTypes = sqlSession().selectList(MAPPING_NAMESPACE + ".getReservedOwnerType",
                ImmutableMap.of("OWNER_TYPE", OWNER_TYPE, "jekIds", jekIds));
        return ownerTypes.size() == 1 ? ownerTypes.get(0) : null;
    }

    public String getMilitaryServiceRelationById(String id, Set<String> jekIds) throws TooManyResultsException {
        List<String> values = sqlSession().selectList(MAPPING_NAMESPACE + ".getMilitaryServiceRelationById",
                ImmutableMap.of("id", id, "jekIds", jekIds));
        if (values.isEmpty()) {
            return null;
        } else if (values.size() == 1) {
            return values.get(0);
        } else {
            throw new TooManyResultsException();
        }
    }

    public ReferenceDataCorrection getById(String entity, String id, String idjek) {
        if (!Strings.isEmpty(id)) {
            return (ReferenceDataCorrection) sqlSession().selectOne(MAPPING_NAMESPACE + ".getByIdAndJek",
                    ImmutableMap.of("entity", entity, "id", id, "idjek", idjek));
        } else {
            return null;
        }
    }
}
