/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.passportoffice.information.test;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 *
 * @author Artem
 */
@Singleton
//@Startup
public class TestStarter {

    @EJB
    private TestDAO testDAO;

    @PostConstruct
    public void start() {
//        testDAO.insert();

//        Place p = new Place();
//        p.setStartDate(new Date());
//        System.out.println("TestStarter:Insert2: " + testDAO.insert2(p));

//        System.out.println("TestStarter:Insert3: " + testDAO.insert3());

//        testDAO.insertNamedEntity();
//        testDAO.selectCityById();
//        testDAO.selectCities();

//        testDAO.selectCountryById();
//        testDAO.selectCountries();
//        testDAO.insertBuilding();
//        testDAO.selectBuildingById();
//        testDAO.selectBuildings();
//        testDAO.selectApartmentById();
//        testDAO.count();

        testDAO.insertApartment();
    }
}
