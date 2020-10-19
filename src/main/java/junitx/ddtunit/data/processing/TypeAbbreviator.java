//$Id: TypeAbbreviator.java 256 2006-10-23 21:56:10Z jg_hamburg $
/********************************************************************************
 * DDTUnit, a Datadriven Approach to Unit- and Moduletesting
 * Copyright (c) 2004, Joerg and Kai Gellien
 * All rights reserved.
 *
 * The Software is provided under the terms of the Common Public License 1.0
 * as provided with the distribution of DDTUnit in the file cpl-v10.html.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *     + Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     + Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 *     + Neither the name of the authors or DDTUnit, nor the
 *       names of its contributors may be used to endorse or promote
 *       products derived from this software without specific prior
 *       written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ********************************************************************************/
package junitx.ddtunit.data.processing;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

/**
 * @author jg
 */
public class TypeAbbreviator {
    /**
     * Properties file containing shortcut to java type mappings
     */
    public static final String TYPEABBREVIATOR_PROPERTIES = "TypeAbbreviator.properties";

    private static final String LF = "\n";

    private static final String DELIM = " - ";

    private static TypeAbbreviator singleton;

    private static Properties dictionary = new Properties();

    /**
     * @throws IOException
     * 
     */
    private TypeAbbreviator() throws IOException {
        dictionary.load(TypeAbbreviator.class
            .getResourceAsStream(TYPEABBREVIATOR_PROPERTIES));
    }

    /**
     * Provide singleton reference on a <code>TypeAbbreviator</code> class.
     * 
     * @return instance of TypeAbbreviator
     * @throws IOException
     */
    public final static TypeAbbreviator getInstance() throws IOException {
        if (singleton == null) {
            singleton = new TypeAbbreviator();
        }
        return singleton;
    }

    /**
     * Check for java type shortcut in internal type dictionary. If nothing
     * found, the original type is returned else the expanded type.
     * 
     * @param shortcut to resolve
     * @return resolved form if exists or shortcut input if no dictionary entry
     */
    public String resolve(String shortcut) {
        String resolved;
        String extended = (String) dictionary.get(shortcut);
        if (extended != null) {
            resolved = extended;
        } else {
            resolved = shortcut;
        }
        return resolved;
    }

    /**
     * Print out Mapping definitions.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer("Dictionary of Type Abbreviations:")
            .append(LF).append(" Key ").append(DELIM).append(" Values ")
            .append(LF);
        for (Iterator iter = dictionary.entrySet().iterator(); iter.hasNext();) {
            Map.Entry dictEntry = (Entry) iter.next();
            String key = (String) dictEntry.getKey();
            sb.append(key).append(DELIM).append(dictEntry.getValue());
            if (iter.hasNext()) {
                sb.append(LF);
            }
        }
        return sb.toString();
    }

    /**
     * @return Number of abbreviation entries in dictionary
     */
    public int size() {
        return dictionary.size();
    }

    public static void main(String[] argv) throws IOException {
        TypeAbbreviator abbrev = new TypeAbbreviator();
        System.out.println(abbrev.toString());
    }
}
