/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.pspoffice.person.strategy.util;

import org.complitex.pspoffice.person.strategy.entity.FullName;
import org.apache.wicket.util.string.Strings;

/**
 *
 * @author Artem
 */
public final class FullNameParser {

    private FullNameParser() {
    }

    public static FullName parse(String rawName) {
        if (rawName == null) {
            return null;
        }
        rawName = rawName.trim();
        if (Strings.isEmpty(rawName)) {
            return null;
        }

        String[] nameTokens = rawName.split("[\\s,]+");
        if (nameTokens != null && nameTokens.length > 0) {
            String lastName = nameTokens[0];
            String firstName = nameTokens.length > 1 ? nameTokens[1] : null;
            String middleName = nameTokens.length > 2 ? nameTokens[2] : null;
            return new FullName(lastName, firstName, middleName);
        }
        return new FullName(rawName, null, null);
    }
}
