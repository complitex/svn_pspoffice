/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.imp.service;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.Stateless;
import org.apache.wicket.util.string.Strings;
import org.complitex.address.strategy.building.BuildingStrategy;
import org.complitex.address.strategy.building_address.BuildingAddressStrategy;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.pspoffice.imp.entity.BuildingCorrection;
import org.complitex.pspoffice.imp.service.exception.TooManyResultsException;

/**
 *
 * @author Artem
 */
@Stateless
public class BuildingCorrectionBean extends AbstractBean {

    private static final String MAPPING_NAMESPACE = BuildingCorrectionBean.class.getName();

    public void insert(BuildingCorrection buildingCorrection) {
        sqlSession().insert(MAPPING_NAMESPACE + ".insert", buildingCorrection);
    }

    public BuildingCorrection find(long id, String idjek) {
        return (BuildingCorrection) sqlSession().selectOne(MAPPING_NAMESPACE + ".findById",
                ImmutableMap.of("id", id, "idjek", idjek));
    }

    public boolean exists(String idjek) {
        return (Integer) sqlSession().selectOne(MAPPING_NAMESPACE + ".exists", idjek) > 0;
    }

    public void cleanData(Set<String> jekIds) {
        sqlSession().delete(MAPPING_NAMESPACE + ".delete", ImmutableMap.of("value", jekIds));
    }

    public void update(BuildingCorrection buildingCorrection) {
        sqlSession().update(MAPPING_NAMESPACE + ".update", buildingCorrection);
    }

    public void clearProcessingStatus(Set<String> jekIds) {
        sqlSession().update(MAPPING_NAMESPACE + ".clearProcessingStatus", ImmutableMap.of("value", jekIds));
    }

    public int countForProcessing(Set<String> jekIds) {
        return (Integer) sqlSession().selectOne(MAPPING_NAMESPACE + ".countForProcessingForJeks",
                ImmutableMap.of("value", jekIds));
    }

    public int countForProcessing(String idjek) {
        return (Integer) sqlSession().selectOne(MAPPING_NAMESPACE + ".countForProcessing", idjek);
    }

    public List<BuildingCorrection> findForProcessing(String idjek, int size) {
        return sqlSession().selectList(MAPPING_NAMESPACE + ".findForProcessing",
                ImmutableMap.of("idjek", idjek, "size", size));
    }

    public Long findSystemBuilding(long streetId, String dom, String korpus) throws TooManyResultsException {
        if (Strings.isEmpty(korpus)) {
            korpus = null;
        }
        Map<String, Object> params = Maps.newHashMap(ImmutableMap.<String, Object>of(
                "buildingBuildingAddressAT", BuildingStrategy.BUILDING_ADDRESS,
                "buildingAddressNumberAT", BuildingAddressStrategy.NUMBER,
                "buildingAddressCorpAT", BuildingAddressStrategy.CORP,
                "dom", dom,
                "parentEntityId", BuildingAddressStrategy.PARENT_STREET_ENTITY_ID));
        params.put("parentId", streetId);
        params.put("korpus", korpus);

        List<Long> ids = sqlSession().selectList(MAPPING_NAMESPACE + ".findSystemBuilding", params);
        if (ids.isEmpty()) {
            return null;
        } else if (ids.size() == 1) {
            return ids.get(0);
        } else {
            throw new TooManyResultsException();
        }
    }
}
