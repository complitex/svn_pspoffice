/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.legacy_import.entity;

import java.io.Serializable;

/**
 *
 * @author Artem
 */
public class ImportMessage implements Serializable {

    public enum ImportMessageLevel {

        INFO, WARN, ERROR
    }
    private String message;
    private ImportMessageLevel level;

    public ImportMessage(String message, ImportMessageLevel level) {
        this.message = message;
        this.level = level;
    }

    public ImportMessageLevel getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }
}
