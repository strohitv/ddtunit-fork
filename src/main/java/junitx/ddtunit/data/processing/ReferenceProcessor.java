//$Id: ReferenceProcessor.java 240 2006-05-03 22:45:16Z jg_hamburg $
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

import java.util.List;
import java.util.Vector;

import junitx.ddtunit.data.IDataSet;

class ReferenceProcessor implements IReferenceListener {
    private List refList;

    private IDataSet clusterDataSet;

    private String groupId;

    private String testId;

    /**
     * @param clusterDataSet
     * 
     */
    public ReferenceProcessor(IDataSet clusterDataSet, String groupId,
            String testId) {
        this.refList = new Vector();
        this.clusterDataSet = clusterDataSet;
        setGroupId(groupId);
        setTestId(testId);
    }

    public ReferenceProcessor(IDataSet clusterDataSet) {
        this(clusterDataSet, ParserConstants.UNKNOWN, ParserConstants.UNKNOWN);
    }

    public void add(IReferenceInfo info) {
        this.refList.add(info);
    }

    public void resolve() {
        checkRankingOrder();
        sortRefList();
        for (int pos = 0; pos < this.refList.size(); pos++) {
            IReferenceInfo info = (IReferenceInfo) this.refList.get(pos);
            info.resolve(this.clusterDataSet, this.getGroupId(), this
                .getTestId());
        }
        clear();
    }

    private void checkRankingOrder() {
        for (int countA = 0; countA < this.refList.size(); countA++) {
            IReferenceInfo infoA = (IReferenceInfo) this.refList.get(countA);
            for (int countB = countA + 1; countB < this.refList.size(); countB++) {
                IReferenceInfo infoB = (IReferenceInfo) this.refList
                    .get(countB);
                infoA.raiseRankOf(infoB);
                infoB.raiseRankOf(infoA);
            }
        }
    }

    private void sortRefList() {
        boolean hasChanged = true;
        while (hasChanged) {
            hasChanged = false;
            for (int countA = 0; countA < this.refList.size(); countA++) {
                IReferenceInfo infoA = (IReferenceInfo) this.refList
                    .get(countA);
                int aPos = countA;
                for (int countB = countA + 1; countB < this.refList.size(); countB++) {
                    IReferenceInfo infoB = (IReferenceInfo) this.refList
                        .get(countB);
                    if (infoA.getRank() < infoB.getRank()) {
                        hasChanged = true;
                        this.refList.set(countB, infoA);
                        this.refList.set(aPos, infoB);
                        aPos = countB;
                    }
                }
                if (hasChanged) {
                    countA = -1;
                    hasChanged = false;
                }
            }
        }
    }

    private void clear() {
        this.refList.clear();
    }

    public String getGroupId() {
        return this.groupId;
    }

    public void setGroupId(String groupId) {
        if (groupId == null) {
            this.groupId = ParserConstants.UNKNOWN;
        } else {
            this.groupId = groupId;
        }
    }

    public String getTestId() {
        return this.testId;
    }

    public void setTestId(String testId) {
        if (testId == null) {
            this.testId = ParserConstants.UNKNOWN;
        } else {
            this.testId = testId;
        }
    }
}
