/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.importing.legacy.entity;

import org.complitex.dictionary.entity.IConfig;

/**
 *
 * @author Artem
 */
public enum PspImportConfig implements IConfig{
    
    DEFAULT_IMPORT_FILE_DIR("c:\\storage\\pspoffice\\import"),
    DEFAULT_IMPORT_FILE_ERRORS_DIR("c:\\storage\\pspoffice\\import\\errors");
    
    private String defaultValue;
    
    private PspImportConfig(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String getGroupKey() {
        return "psp_import";
    }
    
}
