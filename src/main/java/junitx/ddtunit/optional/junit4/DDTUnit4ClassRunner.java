// $Id: DDTTestCase.java 351 2008-08-14 20:20:56Z jg_hamburg $
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

package junitx.ddtunit.optional.junit4;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;

import junit.framework.TestCase;
import junit.framework.TestFailure;
import junit.framework.TestResult;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

public class DDTUnit4ClassRunner extends JUnit4ClassRunner {

	private final Class<? extends TestCase> clazz;

	public DDTUnit4ClassRunner(Class<? extends TestCase> clazz)
			throws InitializationError {
		super(clazz);
		this.clazz = clazz;
	}

	@Override
	protected void invokeTestMethod(Method method, RunNotifier runNotifier) {

		Description testDescription = Description.createTestDescription(clazz,
				method.getName());

		try {
			if (method.isAnnotationPresent(Ignore.class)) {
				runNotifier.fireTestIgnored(testDescription);
			}
			Constructor<? extends TestCase> constructor = clazz
					.getConstructor(String.class);

			TestCase newInstance = constructor.newInstance(method.getName());

			runNotifier.fireTestStarted(testDescription);

			TestResult result;
			try {
				setUp(newInstance);

				result = newInstance.run();
			} finally {

				tearDown(newInstance);
			}

			if (!result.wasSuccessful()) {

				for (Enumeration<TestFailure> en = result.errors(); en
						.hasMoreElements();) {
					TestFailure failure = en.nextElement();
					runNotifier.fireTestFailure(new Failure(testDescription,
							failure.thrownException()));
				}

				for (Enumeration<TestFailure> en = result.failures(); en
						.hasMoreElements();) {
					TestFailure failure = en.nextElement();
					runNotifier.fireTestFailure(new Failure(testDescription,
							failure.thrownException()));
				}

			}

			runNotifier.fireTestFinished(testDescription);

		} catch (Throwable e) {
			runNotifier.fireTestFailure(new Failure(testDescription, e));
		}
	}

	private void tearDown(TestCase newInstance)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			if ((method.isAnnotationPresent(After.class) && !method
					.isAnnotationPresent(Ignore.class))) {
				method.invoke(newInstance, new Object[] {});
			}
		}

	}

	private void setUp(TestCase newInstance) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			// System.out.println("su " + method.getName() );
			if ((method.isAnnotationPresent(Before.class) && !method
					.isAnnotationPresent(Ignore.class))) {
				method.invoke(newInstance, new Object[] {});
			}
		}

	}

}
