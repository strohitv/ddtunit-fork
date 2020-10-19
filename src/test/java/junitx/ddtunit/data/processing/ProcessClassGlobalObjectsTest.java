//$Id: ProcessClassGlobalObjectsTest.java 153 2005-06-02 19:06:21Z jg_hamburg $
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

import junitx.ddtunit.DDTTestCase;

public class ProcessClassGlobalObjectsTest extends DDTTestCase {

    /**
     * @param name
     */
    public ProcessClassGlobalObjectsTest(String name) {
        super(name);
    }

    /**
     * initialize test context
     */
    protected void initContext() {
        initTestData("ProcessClassGlobalObjectsTest",
            "ProcessClassGlobalObjectsTest");
    }

    /**
     * Read object information from class global scope of xml test resource.
     */
    public void testDirectReadOnScope() {
        Object local = getObject("local");
        addObjectToAssert("expectedLocal", local);
        Object global = getGlobalObject("global");
        addObjectToAssert("expectedGlobal", global);
        Object resource = getResourceObject("resource");
        addObjectToAssert("expectedResource", resource);
    }

    /**
     * Read object information from class global scope of xml test resource.
     */
    public void testLocalGlobalScopeAccess() {
        Object local = getObject("local");
        addObjectToAssert("expectedLocal", local);
        Object global = getObject("global");
        addObjectToAssert("expectedGlobal", global);
        Object resource = getObject("resource");
        assertObject("expectedResource", resource, false);
        resource = getGlobalObject("resource");
        addObjectToAssert("expectedResource", resource);
    }
}
