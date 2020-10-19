//$Id: UserBeanTest.java 358 2009-08-05 13:58:44Z jg_hamburg $
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
package junitx.ddtunit.usecases;

import junitx.ddtunit.DDTTestCase;

/**
 * Verify bean instanciation as requested by Rafael Luque, 20.10.2006
 * <code><pre>
 *   &lt;ddtunit xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot;
 *     xsi:noNamespaceSchemaLocation=&quot;http://ddtunit.sourceforge.net/ddtunit.xsd&quot;&gt;
 *   &lt;cluster id=&quot;usertest&quot;&gt;
 *    &lt;group id=&quot;testCreateUser&quot;&gt; 
 *     &lt;test id=&quot;complexUser&quot;&gt; 
 *      &lt;objs&gt; 
 *        &lt;obj id=&quot;user&quot; type=&quot;com.orangesoft.disnet.model.User&quot; hint=&quot;bean&quot;&gt;
 *          &lt;id&gt;myUsername&lt;/id&gt;
 *          &lt;password&gt;myPassword&lt;/password&gt;
 *          &lt;picture hint=&quot;array&quot; type=&quot;byte&quot;&gt;100&lt;/picture&gt; 
 *        &lt;/obj&gt;
 *      &lt;/objs&gt;
 *   ...
 * </pre></code>
 * 
 * Verify bean instanciation as requested by Eduardo Solanas, 04.06.2009
 * <code><pre>
 *   &lt;obj id="test" hint="bean" type="junitx.ddtunit.usecases.UserBean"&gt;!NULL!&lt;/obj&gt;
 * </pre></code>
 * 
 * @author jg
 */
public class UserBeanTest extends DDTTestCase {

    @Override
    protected void initContext() {
        initTestData("UserBeanTest");
    }

    public void testCreateUser() throws Exception {
        UserBean user = (UserBean) getObject("user");
        addObjectToAssert("first", user);
    }
    
}
