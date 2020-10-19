/* This file is part of junitdoc.
 *
 * junitdoc is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * junitdoc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with junitdoc; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package junitdoc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.RootDoc;

/**
 * This doclet can be used to generate a JUnit Testreport. This doclet reads the
 * XML result file that can be generated with the apache ant task <code>
 * junit</code>.
 */
public class Doclet {
    //~ Static variables/initializers
    // ------------------------------------------

    /** where to store the html report. Default is the current directory. */
    private static File toDir = new File(".");

    /** where to find the XML report files. Default is the current directory. */
    private static File reportDir = new File(".");

    //~ Methods
    // ----------------------------------------------------------------

    /**
     * returns the number of option parameters for the given option. We
     * understand the option <code>-d</code> to define the output directory
     * and <code>-report</code> to define the directory where to find the XML
     * test reports.
     * 
     * @param option
     * 
     * @return 2 if the option is of <code>-d</code> or <code>-report</code>.
     *         0 in all other cases.
     */
    public static int optionLength(String option) {
        if (option.equals("-d") || option.equals("-report")) {
            return 2;
        } else {
            return 0;
        }
    }

    /**
     * This is the main entry point for JavaDoc. We create here the HTML report
     * pages.
     * 
     * @param root the root element of the java docs.
     * 
     * @return true, if everything was fine. False otherwise.
     * 
     * @throws Exception in case of any error.
     */
    public static boolean start(RootDoc root) throws Exception {
        ClassDoc[] classes = root.classes();

        // write the constant files, they don't have any dynamic content.
        writeConstantFiles();

        // start writing the overview file.
        FileWriter overview = writeOverview();

        // this is the document builder for parsing the XML files.
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setValidating(false);

        DocumentBuilder b = f.newDocumentBuilder();
        List testSuites = new ArrayList();

        // we count tests, failures, errors and time.
        int tests = 0;
        int failures = 0;
        int errors = 0;
        double time = 0;

        for (int i = 0; i < classes.length; ++i) {
            ClassDoc cd = classes[i];

            if (cd.name().endsWith("Test")) {
                // now parse the XML file!
                File report = new File(reportDir, "TEST-" + cd.qualifiedName()
                        + ".xml");

                if (report.exists()) {
                    Document d = b.parse(report);

                    // build the output file.
                    File file = new File(toDir, makeFile(cd.qualifiedName())
                            + ".html");

                    // make the directory structure, if needed.
                    file.getParentFile().mkdirs();

                    Doclet.ReportWriter w = new Doclet.ReportWriter(file, d, cd);
                    w.write();
                    testSuites.add(d.getDocumentElement());
                    tests += Integer.valueOf(
                        d.getDocumentElement().getAttribute("tests"))
                        .intValue();
                    errors += Integer.valueOf(
                        d.getDocumentElement().getAttribute("errors"))
                        .intValue();
                    failures += Integer.valueOf(
                        d.getDocumentElement().getAttribute("failures"))
                        .intValue();
                    time += Double.valueOf(
                        d.getDocumentElement().getAttribute("time"))
                        .doubleValue();
                } else {
                    System.out.println("missing report for " + cd.name());
                    System.out.println("not found as "
                            + report.getCanonicalPath());
                }
            }
        }

        FileWriter summary = writeSummary();
        summary.write("<TD>");
        summary.write(String.valueOf(tests));
        summary.write("</TD><TD class=\"Failure\">");
        summary.write(String.valueOf(failures));
        summary.write("</TD><TD class=\"Error\">");
        summary.write(String.valueOf(errors));
        summary.write("</TD><TD>");
        summary.write(NumberFormat.getPercentInstance().format(
            1.0 - (((double) failures + errors) / (double) tests)));
        summary.write("</TD><TD>");
        summary.write(NumberFormat.getNumberInstance().format(time));
        summary.write("</TD></TR></TABLE>\n");
        summary
            .write("Note: failures are anticipated and checked for with assertions while errors are unanticipated.\n");
        summary.write("<H3>Tests</H3>");
        summary.write("<TABLE summary=\"tests\" class=\"details\">\n");
        summary
            .write("<TR><TH>Name</TH><TH>Tests</TH><TH>Failures</TH><TH>Errors</TH><TH>Time</TH></TR>\n");

        Collections.sort(testSuites, new Doclet.ElementComparator());

        for (int i = 0; i < testSuites.size(); i++) {
            Element e = (Element) testSuites.get(i);
            summary.write("<TR class=\"");

            if (e.getElementsByTagName("error").getLength() != 0) {
                summary.write("Error");
            } else if (e.getElementsByTagName("failure").getLength() != 0) {
                summary.write("Failure");
            } else {
                summary.write("TableRowColor");
            }

            summary.write("\"><TD><A href=\"");
            summary.write(makeFile(e.getAttribute("name")));
            summary.write(".html\">");
            summary.write(e.getAttribute("name"));
            summary.write("</A></TD><TD>");
            summary.write(e.getAttribute("tests"));
            summary.write("</TD><TD>");
            summary.write(e.getAttribute("failures"));
            summary.write("</TD><TD>");
            summary.write(e.getAttribute("errors"));
            summary.write("</TD><TD>");
            summary.write(e.getAttribute("time"));
            summary.write("</TD></TR>\n");

            // add the element to the overview as well.
            overview.write("<A href=\"" + makeFile(e.getAttribute("name"))
                    + ".html\" target=\"classFrame\" class=\"");
            if (e.getElementsByTagName("error").getLength() != 0) {
                overview.write("Error");
            } else if (e.getElementsByTagName("failure").getLength() != 0) {
                overview.write("Failure");
            } else {
                overview.write("TableRowColor");
            }
            overview.write("\">");
            overview.write(e.getAttribute("name"));
            overview.write("</A><BR>\n");
        }

        summary.write("</TABLE>\n");
        summary.write("</BODY>\n");
        summary.write("</HTML>\n");
        summary.close();

        overview.write("</BODY>\n");
        overview.write("</HTML>\n");
        overview.close();

        return true;
    }

    /**
     * This method checks the given options. We expect the following possible
     * options:
     * 
     * <P>
     * <code>-d</code> with the directory where to store the html report.
     * </p>
     * 
     * <P>
     * <code>-report</code> with the directory where to find the XML report
     * files.
     * </p>
     * 
     * @param options the options that JavaDoc has found.
     * @param reporter report your error here.
     * 
     * @return true, if all options are valid.
     */
    public static boolean validOptions(String[][] options,
            DocErrorReporter reporter) {
        boolean check = true;

        for (int j = 0; j < options.length; j++) {
            String[] params = options[j];

            if (params[0].equals("-d")) {
                toDir = new File(params[1]);

                if (checkDir(toDir, reporter, true) == false) {
                    return false;
                }
            }

            if (params[0].equals("-report")) {
                reportDir = new File(params[1]);

                if (checkDir(reportDir, reporter, false) == false) {
                    return false;
                }
            }

            for (int i = 1; i < params.length; i++) {
                reporter.printNotice("parameter: " + params[i]);
            }
        }

        return true;
    }

    /**
     * a small utility method to check if the given file is a directory and that
     * it is writeable. It writes error messages to the reporter.
     * 
     * @param dir file to check.
     * @param reporter write errors here.
     * @param write if true, the directory must be writeable.
     * 
     * @return true, if the file is a directory (and if <code>write</code> is
     *         true, the directory is also writeable).
     */
    private static boolean checkDir(File dir, DocErrorReporter reporter,
            boolean write) {
        if (!dir.isDirectory()) {
            reporter.printError(dir.getAbsolutePath() + " is not a directory!");

            return false;
        }

        if (write) {
            if (!dir.canWrite()) {
                reporter.printError(dir.getAbsolutePath()
                        + " is not writeable!");

                return false;
            }
        }

        return true;
    }

    /**
     * utility to replace the . in a class name into <code>File.separatorChar
     * </code>
     * for a file spearator.
     * 
     * @param qualifiedName the full qualified name of a class.
     * 
     * @return the filename for the given class, where to write the HTML report
     *         to.
     */
    private static String makeFile(String qualifiedName) {
        return qualifiedName.replace('.', File.separatorChar);
    }

    /**
     * this utility writes the files with constant content. This are
     * <code>index.html</code> and <code>stylesheet.css</code>.
     * 
     * @throws IOException in case of any error.
     */
    private static void writeConstantFiles() throws IOException {
        // first write the index.html file.
        FileWriter writer = new FileWriter(new File(toDir, "index.html"));
        writer.write("<HTML>\n");
        writer.write("<HEAD><TITLE>JUnit Test Report</TITLE></HEAD>\n");
        writer.write("<FRAMESET cols=\"20%,80%\">\n");
        writer
            .write("<FRAME src=\"overview-frame.html\" name=\"overviewFrame\">\n");
        writer
            .write("<FRAME src=\"overview-summary.html\" name=\"classFrame\">\n");
        writer.write("</FRAMESET>\n");
        writer.write("<NOFRAMES><H2>Frame Alert</H2><P>\n");
        writer
            .write("This document is designed to be viewed using the frames feature. If you see this message, you are using a non-frame-capable web client.\n");
        writer.write("</NOFRAMES>\n");
        writer.write("</HTML>\n");
        writer.close();
        writer = new FileWriter(new File(toDir, "stylesheet.css"));
        writer
            .write("body { font:normal verdana,arial,helvetica; color:#000000; }\n");
        writer
            .write("th{ font-weight: bold; text-align:left; background:#a6caf0; }\n");
        writer.write("td{ background:#eeeee0; }\n");
        writer.write(".Error { font-weight:bold; color:red; }\n");
        writer.write(".Failure {  font-weight:bold; color:purple; }\n");
        writer.close();
    }

    /**
     * this method writes the static beginning of the overview file.
     * 
     * @return a file writer where the caller can write the rest of the file.
     *         Remember to close this FileWriter!
     * 
     * @throws IOException in case of any error.
     */
    private static FileWriter writeOverview() throws IOException {
        FileWriter overview = new FileWriter(new File(toDir,
                "overview-frame.html"));
        overview.write("<HTML>\n");
        overview
            .write("<HEAD><TITLE>Test Overview</TITLE>\n<link title=\"Style\" type=\"text/css\" rel=\"stylesheet\" href=\"stylesheet.css\"></HEAD>\n");
        overview.write("<BODY>\n");
        overview
            .write("<H2><A href=\"overview-summary.html\" target=\"classFrame\">");
        overview.write("Home</A></H2>\n");
        overview.write("<H2>Tests</H2>\n");

        return overview;
    }

    /**
     * this method writes the static beginning of the overview-summary file.
     * 
     * @return a file writer where the caller can write the rest of the file.
     *         Remember to close this FileWriter!
     * 
     * @throws IOException in case of any error.
     */
    private static FileWriter writeSummary() throws IOException {
        FileWriter writer = new FileWriter(new File(toDir,
                "overview-summary.html"));
        writer.write("<HTML>\n");
        writer.write("<HEAD><TITLE>Test Summary</TITLE>\n");
        writer
            .write("<link title=\"Style\" type=\"text/css\" rel=\"stylesheet\" href=\"stylesheet.css\">\n");
        writer.write("</HEAD>\n");
        writer.write("<BODY>\n");
        writer.write("<H2>Unit Test Results</H2>\n");
        writer.write("<HR>\n");
        writer.write("<H3>Summary</H3>\n");
        writer
            .write("<TABLE summary=\"summary\" cellspacing=\"2\" cellpadding=\"5\" border=\"0\" class=\"details\"><TR>\n");
        writer.write("<TH>Tests</TH><TH>Failures</TH><TH>Errors</TH>");
        writer.write("<TH>Success rate</TH><TH>Time</TH>\n</TR>");

        return writer;
    }

    //~ Classes
    // ----------------------------------------------------------------

    /**
     * A utility class that writes the HTML report page of a class.
     */
    public static class ReportWriter {
        private ClassDoc cd;

        private Document document = null;

        private FileWriter writer = null;

        private MethodDoc[] mets;

        /**
         * Creates a new instance of ReportWriter
         * 
         * @param file
         * @param document
         * @param cd
         * 
         * @throws IOException
         */
        public ReportWriter(File file, Document document, ClassDoc cd)
                throws IOException {
            this.writer = new FileWriter(file);
            this.document = document;
            this.cd = cd;
            this.mets = cd.methods();
        }

        /**
         * DOCUMENT ME!
         * 
         * @throws IOException
         */
        public void write() throws IOException {
            writeHeader();
            writeTestSuite(document.getDocumentElement());
            writeTrailer();
            writer.close();
        }

        private void writeHeader() throws IOException {
            writer.write("<HTML>\n<HEAD><TITLE>Testsuite ");
            writer.write(cd.name());
            writer.write("</TITLE>\n");
            writer
                .write("<link title=\"Style\" type=\"text/css\" rel=\"stylesheet\" href=\"");

            int p = -1;

            while ((p = cd.qualifiedName().indexOf('.', p + 1)) != -1) {
                writer.write("../");
            }

            writer.write("stylesheet.css\">\n");
            writer.write("</HEAD>\n<BODY>\n");
            writer.write("<H2>Unit Test Results of class " + cd.name()
                    + "</H2>\n");
            writer.write("<HR>\n");
        }

        private void writeTestCaseOverview(Element testCase) throws IOException {
            // is this test successfull
            NodeList failures = testCase.getElementsByTagName("failure");
            NodeList errors = testCase.getElementsByTagName("error");
            writer.write("<TR class=\"");

            if (errors.getLength() != 0) {
                writer.write("Error");
            } else if (failures.getLength() != 0) {
                writer.write("Failure");
            } else {
                writer.write("TableRowColor");
            }

            writer.write("\"><TD>\n");
            writer.write(testCase.getAttribute("name"));
            writer.write("</TD>\n<TD>");

            if (errors.getLength() != 0) {
                writer.write("Error");
            } else if (failures.getLength() != 0) {
                writer.write("Failure");
            } else {
                writer.write("Success");
            }

            writer.write("</TD><TD>");
            writer.write(testCase.getAttribute("time"));
            writer.write("</TD><TD>");

            // write the javadoc.
            for (int i = 0; i < mets.length; i++) {
                if (mets[i].name().equals(testCase.getAttribute("name"))) {
                    String methodDoc = mets[i].commentText();
                    writer.write(methodDoc);
                    break;
                }
            }

            // and write the error - if any.
            if (errors.getLength() != 0) {
                writer.write("<BR>");
                for (int i = 0; i < errors.getLength(); i++) {
                    Element e = (Element) errors.item(i);
                    if (e.getAttribute("message") != null) {
                        writer.write(replaceTagFix(e.getAttribute("message")));
                    } else {
                        writer.write("\n<PRE>");

                        NodeList childs = e.getChildNodes();
                        for (int j = 0; j < childs.getLength(); j++) {
                            if (childs.item(j).getNodeType() == Node.TEXT_NODE) {
                                writer.write(childs.item(j).getNodeValue());
                            }
                        }
                        writer.write("</PRE>\n");
                    }
                }
            } else if (failures.getLength() != 0) {
                writer.write("<BR><HR>");
                for (int i = 0; i < failures.getLength(); i++) {
                    Element e = (Element) failures.item(i);
                    if (e.getAttribute("message") != null) {
                        String failNodeText = e.getFirstChild().getNodeValue();
                        writer.write(replaceTagFix(failNodeText));
                        writer.write("<HR>");
                        ;
                    } else {
                        writer.write("\n<PRE>");

                        NodeList childs = e.getChildNodes();
                        for (int j = 0; j < childs.getLength(); j++) {
                            if (childs.item(j).getNodeType() == Node.TEXT_NODE) {
                                writer.write(replaceTagFix(childs.item(j)
                                    .getNodeValue()));
                            }
                        }
                        writer.write("</PRE>\n");
                    }
                }
            }

            writer.write("</TD></TR>");
        }

        private String replaceTagFix(String message) {
            StringBuffer replacement = replaceAll(replaceAll(new StringBuffer(
                    message), "<", "&lt;"), ">", "&gt;");
            
            return replaceAll(replacement, "\n", "<BR>").toString();
        }

        private StringBuffer replaceAll(StringBuffer replaceIn, String find,
                String replace) {
            int start;
            while (true) {
                start = replaceIn.indexOf(find);
                if (start == -1) {
                    break;
                }
                replaceIn.replace(start, start + find.length(), replace);
            }
            return replaceIn;
        }

        private void writeTestSuite(Element testSuite) throws IOException {
            writer.write("<H3>Class ");
            writer.write(testSuite.getAttribute("name"));
            writer.write("</H3>\n");
            writer.write(cd.commentText());
            writer
                .write("<TABLE summary=\"summary\" cellspacing=\"2\" cellpadding=\"5\" border=\"1\" class=\"details\"><TR>\n");
            writer.write("<TH>Tests</TH><TH>Failures</TH><TH>Errors</TH>");
            writer.write("<TH>Time</TH>\n</TR><TR>");
            writer.write("<TD>");
            writer.write(testSuite.getAttribute("tests"));
            writer.write("</TD><TD>");
            writer.write(testSuite.getAttribute("failures"));
            writer.write("</TD><TD>");
            writer.write(testSuite.getAttribute("errors"));
            writer.write("</TD><TD>");
            writer.write(testSuite.getAttribute("time"));
            writer.write("</TD></TR></TABLE>\n");
            writer.write("<HR>");
            writer.write("<H3>Tests</H3>\n");
            writer
                .write("<TABLE summary=\"summary\" cellspacing=\"2\" cellpadding=\"5\" border=\"1\" class=\"details\"><TR>\n");
            writer
                .write("<TH>Name</TH><TH>Status</TH><TH>Time</TH><TH>Description</TH>\n</TR>");

            NodeList testcases = testSuite.getElementsByTagName("testcase");

            for (int i = 0; i < testcases.getLength(); i++) {
                writeTestCaseOverview((Element) testcases.item(i));
            }

            writer.write("</TABLE>\n");
        }

        private void writeTrailer() throws IOException {
            writer.write("</BODY></HTML>\n");
        }
    }

    /**
     * a private class that compares to elements of the junit-XML file to sort
     * them by name.
     */
    private static class ElementComparator implements java.util.Comparator {

        /** Creates a new instance of ElementComparator */
        public ElementComparator() {
        }

        public int compare(Object o1, Object o2) {
            Element e1 = (Element) o1;
            Element e2 = (Element) o2;
            return e1.getAttribute("name").compareTo(e2.getAttribute("name"));
        }

        public boolean equals(Object o) {
            return false;
        }
    }
}