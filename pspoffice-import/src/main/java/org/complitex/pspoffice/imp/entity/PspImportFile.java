/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.imp.entity;

import org.complitex.dictionary.entity.IImportFile;

/**
 *
 * @author Artem
 */
public enum PspImportFile implements IImportFile {

    STREET("sul.csv", "ID	UTYPE	NKOD	RTYPE	NKOD1"),
    BUILDING("sbud.csv",
    "ID	IDJEK	IDUL	DOM	KORPUS	NKOD	ETAG	BEGLS	ENDLS	DILN	TIPB	NDOP	UTYPE	UL"),
    OWNERSHIP_FORM("sprivat.csv", "ID	NKOD	CLR"),
    MILITARY_DUTY("sarm.csv", "ID	NKOD"),
    OWNER_RELATIONSHIP("srel.csv", "ID	NKOD	STAT"),
    DEPARTURE_REASON("svip.csv", "ID	NKOD"),
    REGISTRATION_TYPE("svidp.csv", "ID	NKOD"),
    DOCUMENT_TYPE("sdoc.csv", "ID	NKOD"),
    OWNER_TYPE("svlas.csv", "ID	NKOD");
    
    private final String fileName;
    private final String csvHeader;

    private PspImportFile(String fileName, String csvHeader) {
        this.fileName = fileName;
        this.csvHeader = csvHeader;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    public String getCsvHeader() {
        return csvHeader;
    }
}
