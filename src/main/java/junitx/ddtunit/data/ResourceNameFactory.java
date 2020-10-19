//$Id: ResourceNameFactory.java 227 2006-04-04 21:17:56Z jg_hamburg $
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
package junitx.ddtunit.data;

/**
 * @author jg
 */
public class ResourceNameFactory {
    private static ResourceNameFactory mySingleton;

    private ResourceNameFactory() {
        // no special init
    }

    /**
     * @return instance of factory
     */
    public static ResourceNameFactory getInstance() {
        if (mySingleton == null) {
            mySingleton = new ResourceNameFactory();
        }
        return mySingleton;
    }

    public String getName(String packagePath, String resource) {
        return new DefaultResourceName().getName(packagePath, resource);
    }

    static class DefaultResourceName {
        private String prefix = "DDT-";

        private String postfix = ".xml";

        public DefaultResourceName() {
            // no special init
        }

        public String getName(String packagePath, String resource) {
            StringBuffer sb = new StringBuffer();
            if (resource != null && !resource.startsWith("/")) {
                if (!"".equals(packagePath)) {
                    sb.append("/").append(packagePath);
                }
                sb.append("/").append(prefix).append(resource).append(postfix);
            } else {
                sb.append(resource);
            }
            return sb.toString();
        }
    }
}