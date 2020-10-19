<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<!--
  Stylesheet for presenting DDTUnit xml data files as html.
  First draft by J&ouml;rg Gellien, 2007.
 -->
	<xsl:output doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" encoding="UTF-8" indent="yes" method="xml"/>
	<xsl:template match="ddtunit">
		<html>
			<head>
				<title>DDTUnit - Desctription of Test Cases</title>
				<style type="text/css">	  body {
		font:normal 68% verdana,arial,helvetica;
		color:#000000;
	  }
	  table tr td, tr th {
		  font-size: 68%;
	  }
	  table.details tr th{
		font-weight: bold;
		text-align:left;
		background:#a6caf0;
	  }
	  table.details tr td{
		background:#eeeee0;
	  }

	  p {
		line-height:1.5em;
		margin-top:0.5em; margin-bottom:1.0em;
		margin-left:2em;
		margin-right:2em;
	  }
	  h1 {
		margin: 0px 0px 5px; font: 165% verdana,arial,helvetica
	  }
	  h2 {
		margin-top: 1em; margin-bottom: 0.5em; font: bold 125% verdana,arial,helvetica
	  }
	  h3 {
		margin-bottom: 0.5em; font: bold 115% verdana,arial,helvetica
	  }
	  h4 {
		margin-bottom: 0.5em; font: bold 100% verdana,arial,helvetica
	  }
	  h5 {
		margin-bottom: 0.5em; font: bold 100% verdana,arial,helvetica
	  }
	  h6 {
		margin-bottom: 0.5em; font: bold 100% verdana,arial,helvetica
	  }
	  .Error {
		font-weight:bold; color:red;
	  }
	  .Failure {
		font-weight:bold; color:purple;
	  }
	  .Properties {
		text-align:right;
	  }</style>
			</head>
			<body>
				<h1>
					<a name="top">DDTUnit Test Description</a>
				</h1>
				<p align="right">
Designed for use with					<a href="http://ddtunit.sorceforge.net">DDTUnit</a>
           and					<a href="http://jakarta.apache.org">Ant</a>
.				</p>
				<hr size="2"/>
				<h2>General Description</h2>
				<xsl:variable name="global-desc" select="description"/>
				<p>
					<xsl:value-of select="$global-desc"/>
				</p>
				<xsl:variable name="class-count" select="count(./cluster)"/>
List of TestClasses (					<xsl:value-of select="$class-count"/>
)
				<xsl:if test="$class-count &gt; 0">
<!--                                                                -->
<!-- Iterate over all test classes inside of the document           -->
<!--                                                                -->
					<xsl:for-each select="./cluster">
						<h2>
Test Class -							<xsl:value-of select="@id"/>
						</h2>
						<xsl:variable name="method-count" select="count(group)"/>
Test Methods (							<xsl:value-of select="$method-count"/>
)						<h3>Description</h3>
						<xsl:variable name="class-desc" select="description"/>
						<xsl:value-of select="$class-desc"/>
<!-- display global parameters -->
						<h3>Global Parameters</h3>
						<xsl:apply-templates select="objs"/>
<!-- display all test methods -->
						<xsl:if test="count(group) &gt; 0">
<!--                                                              -->
<!-- List all test methods with test cases                        -->
<!--                                                              -->
							<xsl:for-each select="group">
								<xsl:call-template name="displayGroup"/>
							</xsl:for-each>
						</xsl:if>
<!-- count(method) &gt; 0 -->
						<xsl:if test="count(group) = 0">              No test methods.</xsl:if>
					</xsl:for-each>
<!-- select="./class" -->
				</xsl:if>
<!-- class-count &gt; 0 -->
			</body>
		</html>
	</xsl:template>
	<xsl:template match="objs">
<!--                                                              -->
<!-- Check for objects                                            -->
<!--                                                              -->
		<xsl:variable name="global-var-count" select="count(obj)"/>
		<xsl:if test="count(obj) &gt; 0">
<!--                                                              -->
<!-- List all global parameters                                   -->
<!--                                                              -->
			<ul>
				<xsl:for-each select="./obj">
					<xsl:variable name="obj-value" select="../obj"/>
					<li>
						<strong>
							<xsl:value-of select="@type"/>
						</strong>
						<strong>
							<xsl:value-of select="@id"/>
						</strong>
						<xsl:variable name="hint-value" select="@hint"/>
						<xsl:if test="$hint-value != ''">
						(hint:<xsl:value-of select="@hint"/>)						
						</xsl:if>
 =					<pre>"<xsl:value-of select="$obj-value"/>"</pre>
					</li>
				</xsl:for-each>
<!-- objs/obj -->
			</ul>
		</xsl:if>
<!-- count(params/param) -->
		<xsl:if test="count(obj) = 0">No parameters defined<br/></xsl:if>
	</xsl:template>
	<xsl:template match="asserts">
<!--                                                              -->
<!-- Check for asserts                                            -->
<!--                                                              -->
		<xsl:variable name="assert-count" select="count(assert)"/>
		<xsl:if test="count(assert) &gt; 0">
<!--                                                              -->
<!-- List all global parameters                                   -->
<!--                                                              -->
			<ul>
				<xsl:for-each select="./assert">
					<xsl:variable name="assert-value" select="../assert"/>
					<li>
Assert on <strong>
							<xsl:value-of select="@id"/>
						</strong>
                with action						<strong>
							<xsl:value-of select="@action"/>
						</strong>
                of type						<strong>
							<xsl:value-of select="@type"/>
						</strong>
                expected value = <pre>"<xsl:value-of select="$assert-value"/>"</pre>
					</li>
				</xsl:for-each>
<!-- asserts/assert -->
			</ul>
		</xsl:if>
<!-- count(asserts/assert) -->
		<xsl:if test="count(assert) = 0">No asserts defined<br/></xsl:if>
	</xsl:template>
<!--                                                              -->
<!-- List of methods                                         -->
<!--                                                              -->
	<xsl:template name="displayGroup">
		<xsl:variable name="testcase-in-method" select="test/@id"/>
		<xsl:variable name="method-name" select="@id"/>
Method Name: <strong><xsl:value-of select="@id"/></strong><br/>
		<xsl:for-each select="test">
			<xsl:call-template name="displayTest"/>
		</xsl:for-each>
	</xsl:template>
<!--                                                              -->
<!-- List of test methods                                         -->
<!--                                                              -->
	<xsl:template name="displayTest">
		<xsl:variable name="testcase-in-method" select="@id"/>
		<li>
			<p>
Test: <strong><xsl:value-of select="$testcase-in-method"/></strong><br/>
<!--                                                              -->
<!-- Check for method parameters                                  -->
<!--                                                              -->
				<xsl:variable name="method-var-count" select="count(objs/obj)"/>
Objects (<xsl:value-of select="$method-var-count"/>)				
<!--                                                              -->
<!-- Check for method assertions                                  -->
<!--                                                              -->
				<xsl:variable name="method-assert-count" select="count(asserts/assert)"/>
Assertions (<xsl:value-of select="$method-assert-count"/>)				
<!--                                                              -->
<!-- List all objects                                             -->
<!--                                                              -->
				<xsl:apply-templates select="objs"/>
<!--                                                              -->
<!-- List all test case parameters                                   -->
<!--                                                              -->
				<xsl:apply-templates select="asserts"/>
			</p>
		</li>
	</xsl:template>
</xsl:stylesheet>
