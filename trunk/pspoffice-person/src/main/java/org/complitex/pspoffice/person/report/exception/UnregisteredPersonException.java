/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.report.exception;

/**
 *
 * @author Artem
 */
@Deprecated
public class UnregisteredPersonException extends Exception {

    private String address;

    public UnregisteredPersonException(String address) {
        this.address = address;
    }

    public UnregisteredPersonException() {
    }

    public String getAddress() {
        return address;
    }
}
