package junitx.ddtunit.util;

import junit.framework.TestCase;
import junitx.ddtunit.resources.SimpleVO;

public class PrivilegedAccessorTest extends TestCase {
	private SimpleVO simpleVO;

	@Override
	protected void setUp() throws Exception {
		simpleVO = new SimpleVO();
	}

	public void testSetFieldValue() throws IllegalAccessException,
			NoSuchFieldException {
		String stringValue = "Hallo World";
		PrivilegedAccessor.setFieldValue(simpleVO, "stringValue", stringValue);
		assertEquals("Wrong stringValue", stringValue, simpleVO
				.getStringValue());
		try {
			PrivilegedAccessor.setFieldValue(simpleVO, "stringValue", null);
		} catch (RuntimeException ex) {
			fail("No Expected exception: " + ex.getMessage());
		}
	}

}
