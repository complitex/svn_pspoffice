/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.document.strategy.entity;

import java.util.Date;
import static org.complitex.dictionary.util.AttributeUtil.*;

/**
 *
 * @author Artem
 */
public class Passport extends Document {

    public Passport(Document document) {
        super(document);
    }

    public String getSeries() {
        return getStringValue(this, 2811);
    }

    public String getNumber() {
        return getStringValue(this, 2812);
    }

    public String getOrganizationIssued() {
        return getStringValue(this, 2813);
    }

    public Date getDateIssued() {
        return getDateValue(this, 2814);
    }
}
