/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.imp.service;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import javax.ejb.Stateless;
import org.complitex.dictionary.service.AbstractBean;
import org.complitex.pspoffice.imp.entity.ApartmentCardCorrection;

/**
 *
 * @author Artem
 */
@Stateless
public class ApartmentCardCorrectionBean extends AbstractBean {

    private static final String MAPPING_NAMESPACE = ApartmentCardCorrectionBean.class.getName();

    public void insert(ApartmentCardCorrection apartmentCardCorrection) {
        sqlSession().insert(MAPPING_NAMESPACE + ".insert", apartmentCardCorrection);
    }

    public ApartmentCardCorrection find(long id) {
        return (ApartmentCardCorrection) sqlSession().selectOne(MAPPING_NAMESPACE + ".findById", id);
    }

    public boolean exists() {
        return (Integer) sqlSession().selectOne(MAPPING_NAMESPACE + ".exists") > 0;
    }

    public void cleanData() {
        sqlSession().delete(MAPPING_NAMESPACE + ".delete");
    }

    public void update(ApartmentCardCorrection apartmentCardCorrection) {
        sqlSession().update(MAPPING_NAMESPACE + ".update", apartmentCardCorrection);
    }

    public void clearProcessingStatus() {
        sqlSession().update(MAPPING_NAMESPACE + ".clearProcessingStatus");
    }

    public int countForProcessing() {
        return (Integer) sqlSession().selectOne(MAPPING_NAMESPACE + ".countForProcessing", Utils.NONARCHIVE_INDICATOR);
    }

    public int archiveCount() {
        return (Integer) sqlSession().selectOne(MAPPING_NAMESPACE + ".archiveCount", Utils.NONARCHIVE_INDICATOR);
    }

    public List<ApartmentCardCorrection> findForProcessing(int size) {
        return sqlSession().selectList(MAPPING_NAMESPACE + ".findForProcessing",
                ImmutableMap.of("size", size, "NONARCHIVE_INDICATOR", Utils.NONARCHIVE_INDICATOR));
    }
//    public Long findSystemPerson(PersonCorrection p) throws TooManyResultsException {
//        List<Long> ids = sqlSession().selectList(MAPPING_NAMESPACE + ".findSystemPerson",
//                ImmutableMap.builder().
//                put("personBirthDateAT", PersonStrategy.BIRTH_DATE).
//                put("localeId", Utils.UKRAINIAN_LOCALE_ID).
//                put("lastName", p.getFam()).put("firstName", p.getIm()).put("middleName", p.getOt()).
//                put("birthDate", getBirthDateAsDateString(p.getDatar())).
//                build());
//        if (ids.size() == 1) {
//            return ids.get(0);
//        } else if (ids.isEmpty()) {
//            return null;
//        } else {
//            throw new TooManyResultsException();
//        }
//    }
}
