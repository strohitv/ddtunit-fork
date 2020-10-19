package junitx.ddtunit.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * a.k.a. The "ObjectMolester"
 * <p>
 * 
 * This class is used to access a method or field of an object no matter what
 * the access modifier of the method or field. The syntax for accessing fields
 * and methods is out of the ordinary because this class uses reflection to peel
 * away protection.
 * <p>
 * 
 * Here is an example of using this to access a private member.
 * <code>resolveName</code> is a private method of <code>Class</code>.
 * 
 * <pre>
 * Class c = Class.class;
 * System.out.println(PrivilegedAccessor.invokeMethod(c, &quot;resolveName&quot;,
 * 		&quot;/net/iss/common/PrivilegeAccessor&quot;));
 * </pre>
 * 
 * @author Charlie Hubbard (chubbard@iss.net)
 * @author Prashant Dhokte (pdhokte@iss.net)
 * @created 1. November 2002
 */
public class PrivilegedAccessor {
	/**
	 * Gets the value of the named field and returns it as an object.
	 * 
	 * @param instance
	 *            the object instance
	 * @param fieldName
	 *            the name of the field
	 * @return an object representing the value of the field
	 * @exception IllegalAccessException
	 *                Description of the Exception
	 * @exception NoSuchFieldException
	 *                Description of the Exception
	 */
	public static Object getFieldValue(Object instance, String fieldName)
			throws IllegalAccessException, NoSuchFieldException {
		Field field = getField(instance.getClass(), fieldName);
		field.setAccessible(true);

		return field.get(instance);
	}

	/**
	 * Set the value of the named field and returns it as an object.
	 * 
	 * @param instance
	 *            the object instance
	 * @param fieldName
	 *            the name of the field
	 * @param value
	 *            The new value value
	 * @exception IllegalAccessException
	 *                Description of the Exception
	 * @exception NoSuchFieldException
	 *                Description of the Exception
	 */
	public static void setFieldValue(Object instance, String fieldName,
			Object value) throws IllegalAccessException, NoSuchFieldException {
		Field field = getField(instance.getClass(), fieldName);
		field.setAccessible(true);
		String fieldType = field.getType().getName();
		// if value != null check type
		if (value == null) {
			field.set(instance, value);
		} else {
			String valueType = value.getClass().getName();
			if (fieldType.equals(valueType)
					|| (!fieldType.startsWith("[") && !valueType
							.startsWith("["))) {
				field.set(instance, value);
			} else {
				// try to transform to primitive array
				Object valueArray = ClassAnalyser.createPrimitiveArray(value);
				field.set(instance, valueArray);
			}
		}
	}

	/**
	 * Calls a method on the given object instance with the given argument.
	 * 
	 * @param instance
	 *            the object instance
	 * @param methodName
	 *            the name of the method to invoke
	 * @param arg
	 *            the argument to pass to the method
	 * @return Description of the Return Value
	 * @exception NoSuchMethodException
	 *                Description of the Exception
	 * @exception IllegalAccessException
	 *                Description of the Exception
	 * @exception InvocationTargetException
	 *                Description of the Exception
	 * @see PrivilegedAccessor#invokeMethod(Object,String,Object[])
	 */
	public static Object invokeMethod(Object instance, String methodName,
			Object arg) throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException {
		Object[] args = new Object[1];
		args[0] = arg;

		return invokeMethod(instance, methodName, args);
	}

	/**
	 * Calls a method on the given object instance with the given arguments.
	 * 
	 * @param instance
	 *            the object instance
	 * @param methodName
	 *            the name of the method to invoke
	 * @param args
	 *            an array of objects to pass as arguments
	 * @return Description of the Return Value
	 * @exception NoSuchMethodException
	 *                Description of the Exception
	 * @exception IllegalAccessException
	 *                Description of the Exception
	 * @exception InvocationTargetException
	 *                Description of the Exception
	 * @see PrivilegedAccessor#invokeMethod(Object,String,Object)
	 */
	public static Object invokeMethod(Object instance, String methodName,
			Object[] args) throws NoSuchMethodException,
			IllegalAccessException, InvocationTargetException {
		Class[] classTypes = null;

		if (args != null) {
			classTypes = new Class[args.length];

			for (int i = 0; i < args.length; i++) {
				if (args[i] != null) {
					classTypes[i] = args[i].getClass();
				}
			}
		}

		return getMethod(instance, methodName, classTypes).invoke(instance,
				args);
	}

	/**
	 * @param instance
	 *            the object instance
	 * @param methodName
	 *            the
	 * @param classTypes
	 *            Description of the Parameter
	 * @return The method value
	 * @exception NoSuchMethodException
	 *                Description of the Exception
	 */
	public static Method getMethod(Object instance, String methodName,
			Class[] classTypes) throws NoSuchMethodException {
		Method accessMethod = getMethod(instance.getClass(), methodName,
				classTypes);
		accessMethod.setAccessible(true);

		return accessMethod;
	}

	/**
	 * Return the named field from the given class.
	 * 
	 * @param thisClass
	 *            Description of the Parameter
	 * @param fieldName
	 *            Description of the Parameter
	 * @return The field value
	 * @exception NoSuchFieldException
	 *                Description of the Exception
	 */
	private static Field getField(Class thisClass, String fieldName)
			throws NoSuchFieldException {
		if (thisClass == null) {
			throw new NoSuchFieldException("Invalid class field : " + fieldName);
		}

		Field[] fields = thisClass.getDeclaredFields();
		Field selField = null;
		boolean found = false;

		for (int i = 0; i < fields.length; i++) {
			selField = fields[i];

			if (selField.getName().compareTo(fieldName) == 0) {
				found = true;

				break;
			} else {
				selField = null;
			}
		}

		if (!found) {
			return getField(thisClass.getSuperclass(), fieldName);
		} else if (selField != null) {
			return selField;
		} else {
			throw new NoSuchFieldException("Invalid field : " + fieldName
					+ " for class " + thisClass.getName());
		}
	}

	/**
	 * Return the named method with a method signature matching classTypes from
	 * the given class.
	 * 
	 * @param thisClass
	 *            Description of the Parameter
	 * @param methodName
	 *            Description of the Parameter
	 * @param classTypes
	 *            Description of the Parameter
	 * @return The method value
	 * @exception NoSuchMethodException
	 *                Description of the Exception
	 */
	private static Method getMethod(Class thisClass, String methodName,
			Class[] classTypes) throws NoSuchMethodException {
		if (thisClass == null) {
			throw new NoSuchMethodException("Invalid method : " + methodName);
		}
		Method clazzMethod;
		try {
			clazzMethod = thisClass.getDeclaredMethod(methodName, classTypes);
		} catch (NoSuchMethodException e) {
			clazzMethod = getMethod(thisClass.getSuperclass(), methodName,
					classTypes);
		}
		return clazzMethod;
	}
}
