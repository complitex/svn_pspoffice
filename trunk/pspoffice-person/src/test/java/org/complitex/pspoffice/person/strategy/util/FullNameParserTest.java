/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.complitex.pspoffice.person.strategy.util;

import org.complitex.pspoffice.person.strategy.util.FullNameParser;
import org.complitex.pspoffice.person.strategy.entity.FullName;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author Artem
 */
public class FullNameParserTest {

    @Test
    public void parseNameTest(){
        FullName fullName = FullNameParser.parse("");
        Assert.assertNull(fullName);

        fullName = FullNameParser.parse("   ");
        Assert.assertNull(fullName);

        fullName = FullNameParser.parse("abc");
        Assert.assertEquals(fullName, new FullName("abc", null, null));

        fullName = FullNameParser.parse(" abc   de");
        Assert.assertEquals(fullName, new FullName("abc", "de", null));

        fullName = FullNameParser.parse(" abc   de fgh  ");
        Assert.assertEquals(fullName, new FullName("abc", "de", "fgh"));

        fullName = FullNameParser.parse(" abc,  de fgh");
        Assert.assertEquals(fullName, new FullName("abc", "de", "fgh"));

        fullName = FullNameParser.parse("abc, de, fgh");
        Assert.assertEquals(fullName, new FullName("abc", "de", "fgh"));

        fullName = FullNameParser.parse(" abc   de fgh ij");
        Assert.assertEquals(fullName, new FullName("abc", "de", "fgh"));
    }

}
