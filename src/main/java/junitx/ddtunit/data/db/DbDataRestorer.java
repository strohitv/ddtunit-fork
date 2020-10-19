//$Id: DbDataRestorer.java 282 2007-07-19 22:46:27Z jg_hamburg $
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
package junitx.ddtunit.data.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junitx.ddtunit.data.DDTTestDataException;
import junitx.ddtunit.data.ExceptionAsserter;
import junitx.ddtunit.data.IDataSetSerializer;
import junitx.ddtunit.data.ObjectAsserter;
import junitx.ddtunit.data.TestClusterDataSet;
import junitx.ddtunit.data.TestDataSet;
import junitx.ddtunit.data.TestGroupDataSet;
import junitx.ddtunit.data.TypedObject;
import junitx.ddtunit.data.processing.IParser;
import junitx.ddtunit.data.processing.parser.ParserImpl;

public class DbDataRestorer implements IDataSetSerializer {
    private static final String LF = System.getProperty("line.separator");

    private IParser parser;

    public DbDataRestorer() {
        this.parser = new ParserImpl();
    }

    public TestClusterDataSet restore(String resource, String clusterId) {
        TestClusterDataSet clusterDataSet = null;
        Connection connect = createConnection();
        try {
            String selectData = "select clusterid, groupid, testid, ogroup"
                    + ", otype, id, class_type, action, xml from TESTDATA_VIEW "
                    + "where clusterid=? ";
            PreparedStatement dbData = connect.prepareStatement(selectData);
            dbData.setString(1, clusterId);
            ResultSet resultSet = dbData.executeQuery();
            clusterDataSet = new TestClusterDataSet(clusterId, null);
            String oldGroupId = "<<<OldGroupId>>>";
            String oldTestId = "<<<OldTestId>>>";
            TestGroupDataSet groupDataSet = null;
            TestDataSet testDataSet = null;
            while (resultSet.next()) {
                String groupId = resultSet.getString("groupid");
                String testId = resultSet.getString("testid");
                String oType = resultSet.getString("otype");

                String id = resultSet.getString("id");
                String type = resultSet.getString("class_type");
                String action = resultSet.getString("action");
                String xml = resultSet.getString("xml");
                if (!oldGroupId.equals(groupId)) {
                    // on groupid change store old structure and create new
                    if (groupDataSet != null) {
                        // every group change adds up in a test change as well
                        if (testDataSet != null) {
                            groupDataSet.put(oldTestId, testDataSet);
                        }
                        clusterDataSet.put(oldGroupId, groupDataSet);
                    }
                    oldGroupId = groupId;
                    groupDataSet = new TestGroupDataSet(groupId, clusterDataSet);
                    oldTestId = testId;
                    testDataSet = new TestDataSet(testId, groupDataSet);
                    addParsedObject(testDataSet, oType, id, type, action, xml);
                } else {
                    if (!oldTestId.equals(testId)) {
                        // on testid change store dataset in groupDataSet and
                        // create new
                        if (testDataSet != null) {
                            groupDataSet.put(oldTestId, testDataSet);
                        }
                        oldTestId = testId;
                        testDataSet = new TestDataSet(testId, groupDataSet);
                    }
                    addParsedObject(testDataSet, oType, id, type, action, xml);
                }
            }
            // do not forget the last open entry in the dataset
            if (testDataSet != null) {
                groupDataSet.put(oldTestId, testDataSet);
                clusterDataSet.put(oldGroupId, groupDataSet);
            }
        } catch (SQLException ex) {
            throw new DDTTestDataException("Could not access db test data", ex);
        } finally {
            if (connect != null) {
                try {
                    connect.close();
                } catch (SQLException ex) {
                    // ignore exception
                }
            }
        }
        return clusterDataSet;
    }

    /**
     * Create xml testdata resource for specified clusterId.
     * 
     * @param clusterId to create xml resource for
     * @return StringBuffer representation of xml resource
     */
    public StringBuffer createXml(String clusterId) {
        StringBuffer sb = new StringBuffer();
        sb
            .append("<?xml version=\"1.0\" ?>" + LF)
            .append(
                "<ddtunit xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                        + LF)
            .append(
                "  xsi:noNamespaceSchemaLocation=\"http://ddtunit.sourceforge.net/ddtunit.xsd\">"
                        + LF).append("  <cluster id=\"").append(clusterId)
            .append("\">" + LF);

        Connection connect = createConnection();
        try {
            String selectData = "select clusterid, groupid, testid, ogroup"
                    + ", otype, id, class_type, action, xml from TESTDATA_VIEW "
                    + "where clusterid=? ";
            PreparedStatement dbData = connect.prepareStatement(selectData);
            dbData.setString(1, clusterId);
            ResultSet resultSet = dbData.executeQuery();
            String oldGroupId = "<<<OldGroupId>>>";
            String oldTestId = "<<<OldTestId>>>";
            String oldOGroup = "<<<OldOGroup>>>";
            boolean startGroups = true;
            boolean startTests = true;
            boolean startOGroups = true;
            while (resultSet.next()) {
                String groupId = resultSet.getString("groupid");
                String testId = resultSet.getString("testid");
                String oType = resultSet.getString("otype");
                String oGroup = resultSet.getString("ogroup");

                String id = resultSet.getString("id");
                String type = resultSet.getString("class_type");
                String action = resultSet.getString("action");
                String xml = resultSet.getString("xml");
                // ----------------------------------------------------
                Pattern idPattern = Pattern.compile("id=\"\\w*\"");
                Matcher idMatcher = idPattern.matcher(xml);
                String replacement = idMatcher
                    .replaceFirst("id=\"" + id + "\"");
                if ("noaction".equals(action)) {
                    replacement = replacement.replaceFirst("<obj", "<" + oType);
                } else {
                    replacement = replacement.replaceFirst("<obj", "<" + oType
                            + " action=\"" + action + "\"");
                }
                // ----------------------------------------------------
                replacement = replacement.replaceFirst("obj>", oType + ">");
                if (!oldGroupId.equals(groupId)) {
                    // on groupid change close old structure and open new
                    if (!startGroups) {
                        startGroups = false;
                        sb.append("        </").append(oldOGroup).append(
                            ">" + LF);
                        sb.append("      </test>" + LF);
                        sb.append("    </group>" + LF);
                    }
                    sb.append("    <group id=\"").append(groupId).append(
                        "\">" + LF);
                    startGroups = false;
                    oldGroupId = groupId;
                }
                if (!oldTestId.equals(testId)) {
                    // on testid change store dataset in groupDataSet and
                    // create new
                    if (!startTests) {
                        startTests = false;
                        sb.append("        </").append(oldOGroup).append(
                            ">" + LF);
                        sb.append("      </test>" + LF);
                    }
                    oldTestId = testId;
                    sb.append("      <test id=\"").append(testId).append(
                        "\">" + LF);
                    startTests = false;
                    startOGroups = true;
                }
                if (!oldOGroup.equals(oGroup)) {
                    if (!startOGroups) {
                        sb.append("        </").append(oldOGroup).append(
                            ">" + LF);
                        startOGroups = false;
                    }
                    sb.append("        <").append(oGroup).append(">" + LF);
                    startOGroups = false;
                    oldOGroup = oGroup;
                }
                sb.append("          " + replacement + LF);
            }
            sb.append("        </").append(oldOGroup).append(">" + LF);
            sb.append("      </test>" + LF);
            sb.append("    </group>" + LF);
            sb.append("  </cluster>" + LF);
        } catch (SQLException ex) {
            throw new DDTTestDataException("Could not access db test data", ex);
        } finally {
            if (connect != null) {
                try {
                    connect.close();
                } catch (SQLException ex) {
                    // ignore exception
                }
            }
        }
        return sb;
    }

    private Connection createConnection() {
        Properties dbProp = new Properties();
        dbProp.put("userid", "sa");
        dbProp.put("password", "");

        try {
            Class.forName("org.hsqldb.jdbcDriver");
            Connection connect = DriverManager.getConnection(
                "jdbc:hsqldb:hsql://localhost/ddt", dbProp);
            return connect;
        } catch (ClassNotFoundException ex) {
            throw new DDTTestDataException(
                    "Could not create connection to db repository", ex);
        } catch (SQLException ex) {
            throw new DDTTestDataException(
                    "Could not create connection to db repository", ex);
        }
    }

    /**
     * Retrieve single object instance from database.
     * 
     * @param id used to identify object
     * @param type of object to retrieve
     * @return TypedObject created by repository
     */
    public TypedObject getObject(String id, String type) {
        TypedObject tObj = null;
        Connection connect = createConnection();
        try {
            String selectData = "select id, class_type, xml from objects_tab"
                    + " where id=? and class_type=?";
            PreparedStatement dbData = connect.prepareStatement(selectData);
            dbData.setString(1, id);
            dbData.setString(2, type);
            ResultSet resultSet = dbData.executeQuery();
            resultSet.next();
            String xml = resultSet.getString("xml");
            tObj = this.parser.parseElement(xml, true);
        } catch (SQLException ex) {
            throw new DDTTestDataException(
                    "Error during access on db testdata", ex);
        } finally {
            if (connect != null) {
                try {
                    connect.close();
                } catch (SQLException ex) {
                    // ignore exception
                }
            }
        }
        return tObj;
    }

    /**
     * @param testDataSet
     * @param oType specifies tag type: obj, assert, exception
     * @param id of object for reference usage
     * @param type of object used as additional reference attribute
     * @param xml
     */
    private void addParsedObject(TestDataSet testDataSet, String oType,
            String id, String type, String action, String xml) {
        TypedObject tObj = this.parser.parseElement(xml, true);
        tObj.setId(id);
        tObj.setType(type);
        if ("obj".equals(oType)) {
            testDataSet.putObject(id, tObj);
        } else if ("assert".equals(oType)) {
            ObjectAsserter asserter = new ObjectAsserter(id, type, action);
            asserter.setValue(tObj.getValue());
            testDataSet.putAssert(id, asserter);
        } else if ("exception".equals(oType)) {
            ExceptionAsserter asserter = new ExceptionAsserter(id, type, action);
            asserter.setValue(tObj.getValue());
            testDataSet.putAssert(id, asserter);
        } else {
            throw new DDTTestDataException("Wrong tag type " + oType);
        }
    }

}
