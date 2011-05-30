/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.registration.report.web;

/**
 *
 * @author Artem
 */
public final class FamilyAndHousingPaymentsAddressParamPage extends AbstractAddressParamPage {

    @Override
    protected void toReferencePage(String addressEntity, long addressId) {
        setResponsePage(new FamilyAndHousingPaymentsPage(addressEntity, addressId));
    }
}

