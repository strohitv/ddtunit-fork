package junitx.ddtunit.util;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This example is taken from Thinking in Java secd. ed. (p.680f) This class
 * used reflection to show all the methods of a class, even if the methods are
 * defined in the base class.
 * 
 * @author jgellien
 */
public class ClassAnalyser {
    /**
     * Constant to define constructor select in method
     * {@link #findMethodByParams(String, String, Class[])}
     */
    public final static String CLASS_CONSTRUCTOR = "class$";

    private final static String usage = "usage: \n"
            + "ShowMethods qualified.class.name\n"
            + "To show all methods in class or: \n"
            + "ShowMethods qualified.class.name word\n"
            + "To search for methods involving 'word'";

    private static Logger log = LoggerFactory.getLogger(ClassAnalyser.class);

    /**
     * Constructor for the ClassAnalyser object
     */
    private ClassAnalyser() {
        // no special init
    }

    /**
     * The main program for the ShowMethods class
     * 
     * @param args The command line arguments
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println(ClassAnalyser.usage);
            System.exit(0);
        }

        System.out.println("ClassAnalyser started with class " + args[0]);

        if (args.length == 1) {
            ClassAnalyser.showAllMethods(args[0]);
            ClassAnalyser.showAllFields(args[0]);
        } else {
            ClassAnalyser.showSelectedMethods(args[0], args[1]);
        }

        System.out.println("ClassAnalyser end.");
    }

    /**
     * Extract simple class name without package information
     * 
     * @param obj object to analyse
     * 
     * @return class name
     */
    public static String getShortName(Object obj) {
        String className = obj.getClass().getName();

        return getShortName(className);
    }

    /**
     * Extract simple class name without package information
     * 
     * @param className to analyse
     * 
     * @return name without package extension
     */
    public static String getShortName(Class className) {
        String localName = className.getName();

        return getShortName(localName);
    }

    /**
     * Extract simple class name without package information
     * 
     * @param className qualified class name to analyse
     * 
     * @return class name
     */
    public static String getShortName(String className) {
        String shortName = className.substring(className.lastIndexOf(".") + 1,
            className.length());

        return shortName;
    }

    /**
     * Display all attributes/fields of class quaifiedClassName to the
     * configured appender specified by Log4j
     * 
     * @param qualifiedClassName name of class under analysis
     * 
     * @throws RuntimeException ClassAnalyserException if an error occures
     */
    public static void showAllFields(String qualifiedClassName) {
        try {
            Class c = Class.forName(qualifiedClassName);
            Field[] fields = c.getDeclaredFields();
            log.debug("===Class " + qualifiedClassName + " fields:");

            for (int i = 0; i < fields.length; i++) {
                log.debug(fields[i].toString());
            }
        } catch (ClassNotFoundException ex) {
            log.error("No such class: " + qualifiedClassName, ex);
            throw new ClassAnalyserException("No class found of type "
                    + qualifiedClassName, ex);
        }
    }

    /**
     * Display all attributes/fields of class quaifiedClassName matching
     * searchTerm to the configured appender specified by Log4j
     * 
     * @param qualifiedClassName name of class under analysis
     * @param searchTerm match string to check against
     * 
     * @throws RuntimeException ClassAnalyserException if an error occures
     */
    public static void showSelectedFields(String qualifiedClassName,
            String searchTerm) {
        try {
            Class c = Class.forName(qualifiedClassName);
            Field[] fields = c.getDeclaredFields();
            log.debug("===Class " + qualifiedClassName
                    + " fields selected by '" + searchTerm + "':");

            for (int i = 0; i < fields.length; i++) {
                if (fields[i].toString().indexOf(searchTerm) != -1) {
                    log.debug(fields[i].toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            log.error("No such class: " + qualifiedClassName, ex);
            throw new ClassAnalyserException("No class found of type "
                    + qualifiedClassName, ex);
        }
    }

    /**
     * Search for exact match of searchTerm in the list of declared fields of
     * the class qualifiedClassName.
     * 
     * @param qualifiedClassName Description of the Parameter
     * @param searchTerm Description of the Parameter
     * 
     * @return Field that was found or null if field does not exists
     * 
     * @throws RuntimeException ClassAnalyserException if an error occures
     */
    public static Field getSelectedField(String qualifiedClassName,
            String searchTerm) {
        Field localField = null;
        if (qualifiedClassName != null && searchTerm != null) {
            try {
                Class c = Class.forName(qualifiedClassName);

                while (c != null) {
                    // this gets you only not inherited fiels ==> you have to
                    // iterate over
                    // all parent classes
                    Field[] fields = c.getDeclaredFields();
                    String className = c.getName();
                    log.debug("===Class " + className + " fields selected by '"
                            + searchTerm + "':");

                    boolean found = false;

                    for (int i = 0; i < fields.length; i++) {
                        // use Field.getName() instead of Field.toString()
                        // if field does not exists getName() returns a string
                        // starting with
                        // "class$<full qualified class name>"
                        String fieldName = fields[i].getName();
                        log.debug("check search term <" + searchTerm
                                + "> in field <" + fieldName + ">");

                        if (!fieldName.startsWith("class$")
                                && fieldName.equals(searchTerm)) {
                            // first hit
                            if (!found) {
                                localField = fields[i];
                                found = true;
                                log.debug("First hit.");

                                break;
                            } else {
                                throw new IllegalArgumentException(
                                        "double count of field " + searchTerm);
                            }
                        }
                    }

                    if (!found) {
                        c = c.getSuperclass();
                        log.debug("=== No Hit");
                    } else {
                        c = null;
                        log.debug("=== End of Check");
                    }
                }
            } catch (ClassNotFoundException ex) {
                log.error("No such class: " + qualifiedClassName, ex);
                throw new ClassAnalyserException("No class found of type "
                        + qualifiedClassName, ex);
            }
        }
        return localField;
    }

    /**
     * Display all methods of class quaifiedClassName to the configured appender
     * specified by Log4j
     * 
     * @param qualifiedClassName name of class under analysis
     * 
     * @throws RuntimeException ClassAnalyserException if an error occures
     */
    public static void showAllMethods(String qualifiedClassName) {
        try {
            Class c = Class.forName(qualifiedClassName);
            Method[] m = c.getMethods();
            Constructor[] ctor = c.getConstructors();
            log.debug("===Class " + qualifiedClassName + " methods:");

            for (int i = 0; i < m.length; i++) {
                log.debug(m[i].toString());
            }

            log.debug("===Class " + qualifiedClassName + " constructors:");

            for (int i = 0; i < ctor.length; i++) {
                log.debug(ctor[i].toString());
            }
        } catch (ClassNotFoundException ex) {
            log.error("No such class: " + qualifiedClassName, ex);
            throw new ClassAnalyserException("No class found of type "
                    + qualifiedClassName, ex);
        }
    }

    /**
     * Put all method names of the class qualifiedClassName into a String array
     * and return it.
     * 
     * @param qualifiedClassName Description of the Parameter
     * 
     * @return array of all method names of the class to analyse
     * 
     * @throws RuntimeException ClassAnalyserException if an error occures
     */
    public static String[] getAllMethods(String qualifiedClassName) {
        Class c;
        Method[] m;

        try {
            c = Class.forName(qualifiedClassName);
            m = c.getMethods();
            log.debug("===Class " + qualifiedClassName + " methods:");

            String[] methods = new String[m.length];

            for (int i = 0; i < m.length; i++) {
                log.debug(m[i].toString());
                methods[i] = m[i].getName();
            }

            return methods;
        } catch (ClassNotFoundException ex) {
            log.error("No such class: " + qualifiedClassName, ex);
            throw new ClassAnalyserException("No class found of type "
                    + qualifiedClassName, ex);
        }
    }

    /**
     * Display all constructors and methods of qualifiedClassName class which
     * match with searchTerm. <br/>Output is set to Log4J Info level of this
     * classes Logger.
     * 
     * @param qualifiedClassName Name of class to analyse
     * @param searchTerm Match term for methods to display
     * 
     * @throws RuntimeException ClassAnalyserException if an error occures
     */
    public static void showSelectedMethods(String qualifiedClassName,
            String searchTerm) {
        try {
            Class c = Class.forName(qualifiedClassName);
            Method[] m = c.getMethods();
            Constructor[] ctor = c.getConstructors();

            for (int i = 0; i < m.length; i++) {
                if (m[i].toString().indexOf(searchTerm) != -1) {
                    log.info(m[i].toString());
                }
            }

            for (int i = 0; i < ctor.length; i++) {
                if (ctor[i].toString().indexOf(searchTerm) != -1) {
                    log.info(ctor[i].toString());
                }
            }
        } catch (ClassNotFoundException ex) {
            log.error("No such class: " + qualifiedClassName, ex);
            throw new ClassAnalyserException("No class found of type "
                    + qualifiedClassName, ex);
        }
    }

    /**
     * Find a Constructor/Method of class className by using the argument list
     * args and try to vary arguments which could be of primitive type. <br/>
     * The args list only contains classes.
     * 
     * @param className
     * @param methodName
     * @param args
     * @return Method/Constructor object
     */
    public static Object findMethodByParams(String className,
            String methodName, Class[] args) {
        Object method = null;

        try {
            Object[] methods;
            Class myClass = Class.forName(className);
            if (CLASS_CONSTRUCTOR.compareTo(methodName) == 0) {
                methods = myClass.getDeclaredConstructors();
            } else {
                methods = myClass.getDeclaredMethods();
            }

            Vector searchMethods = filterByNameAndParamCount(methods,
                methodName, args.length);
            boolean found = false;
            for (Iterator iter = searchMethods.iterator(); iter.hasNext();) {
                Object myMethod = iter.next();

                found = filterByParam(myMethod, args, 0);

                if (found) {
                    // exit with first valid method
                    method = myMethod;

                    break;
                }
            }
            // if method not found in active clazz search in superclazzes
            if (!found) {
                Set superClazzes = getSuperElements(myClass);
                for (Iterator iter = superClazzes.iterator(); iter.hasNext();) {
                    Class clazz = (Class) iter.next();
                    method = findMethodByParams(clazz.getName(), methodName,
                        args);
                    if (method != null) {
                        found = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            throw new ClassAnalyserException(
                    "Could not find constructor of class " + className, e);
        }

        return method;
    }

    /**
     * Display all methods of class quaifiedClassName to the configured appender
     * specified by Log4j
     * 
     * @param qualifiedClassName name of class under analysis
     * @param methodName to search for
     * @throws RuntimeException ClassAnalyserException if an error occures
     */
    public static Method findMethodByName(String qualifiedClassName, String methodName) {
        try {
            Class c = Class.forName(qualifiedClassName);
            Method[] m = c.getMethods();
            Method firstMethod = null;
            log.debug("===Class " + qualifiedClassName + " methods:");

            for (int i = 0; i < m.length; i++) {
            	if (m[i].getName().endsWith(methodName)){
                  firstMethod = m[i];
                  break;
            	}
            }
            return firstMethod;
        } catch (ClassNotFoundException ex) {
            log.error("No such class: " + qualifiedClassName, ex);
            throw new ClassAnalyserException("No class found of type "
                    + qualifiedClassName, ex);
        }
    }


    /**
     * Check if parameter type on position pos of constructor method and args
     * are the same. <br/>If the actual position arguments are the same the
     * method is called with (pos+1).
     * 
     * @param constructor
     * @param list of class arguments to match
     * @param pos of argument to match
     * @return true if parameter matches
     */
    private static boolean filterByParam(Object method, Class[] args, int pos) {
        boolean valid = false;
        Class arg;
        Class argOnPos;

        // exit criterion for recursion
        if (pos >= args.length) {
            valid = true;
        } else {
            arg = args[pos];

            if (java.lang.reflect.Constructor.class.isInstance(method)) {
                argOnPos = ((Constructor) method).getParameterTypes()[pos];
            } else {
                argOnPos = ((Method) method).getParameterTypes()[pos];
            }

            // check if argument class is a valid argument substitution of
            // constructor signature argument class. E.g. check if argument
            // is perhaps derived from a superclass of selected
            // constructor/method argument
            if (arg.equals(argOnPos)
                    || getSuperElements(arg).contains(argOnPos)) {
                valid = filterByParam(method, args, pos + 1);

                // This path does not work, go the primitive way
                if (!valid) {
                    valid = filterByPrimitiveParam(method, args, pos + 1);
                }
            } else {
                valid = filterByPrimitiveParam(method, args, pos);
            }
        }

        return valid;
    }

    public static Set getSuperElements(Class clazz) {
        Set clazzList = new HashSet();
        Class[] interfaces = clazz.getInterfaces();
        for (int count = 0; count < interfaces.length; count++) {
            clazzList.addAll(getSuperElements(interfaces[count]));
            clazzList.add(interfaces[count]);
        }
        Class superClazz = clazz.getSuperclass();
        if (superClazz == null) {
            return clazzList;
        } else {
            Set result = getSuperElements(superClazz);
            result.add(superClazz);
            result.addAll(clazzList);
            return result;
        }
    }

    private static boolean filterByPrimitiveParam(Object method, Class[] args,
            int pos) {
        boolean valid = false;
        Class arg;
        Class argOnPos;

        // exit criterion for recursion
        if (pos >= args.length) {
            valid = true;
        } else {
            arg = args[pos];

            if (java.lang.reflect.Constructor.class.isInstance(method)) {
                argOnPos = ((Constructor) method).getParameterTypes()[pos];
            } else {
                argOnPos = ((Method) method).getParameterTypes()[pos];
            }

            // check if primitiv type of argOnPos exists
            if (hasPrimitive(arg) && argOnPos.equals(getPrimitiveClass(arg))) {
                valid = filterByParam(method, args, pos + 1);
            }
        }

        return valid;
    }

    /**
     * Retrieve primitove type of specified clazz
     * 
     * @param checkClass to retrieve associated primitive type from
     * 
     * @return associated primitive type or null if non exists
     */
    public static Class getPrimitiveClass(Class checkClass) {
        Class primitive = null;
        boolean arrayFound = false;
        Class verifyClazz = checkClass;
        if (hasPrimitive(checkClass)) {
            // check for array class
            String name = checkClass.getName();
            if (name.startsWith("[L")) {
                arrayFound = true;
                try {
                    verifyClazz = Class.forName(name.substring(2,
                        name.length() - 1));
                } catch (ClassNotFoundException ex) {
                    throw new ClassAnalyserException(
                            "Could not construct base class from array");
                }
            }
            if (verifyClazz.equals(java.lang.Integer.class)) {
                if (arrayFound) {
                    primitive = getPrimitiveArrayClass("[I");
                } else {
                    primitive = Integer.TYPE;
                }
            } else if (verifyClazz.equals(java.lang.Long.class)) {
                if (arrayFound) {
                    primitive = getPrimitiveArrayClass("[J");
                } else {
                    primitive = Long.TYPE;
                }
            } else if (verifyClazz.equals(java.lang.Short.class)) {
                if (arrayFound) {
                    primitive = getPrimitiveArrayClass("[S");
                } else {
                    primitive = Short.TYPE;
                }
            } else if (verifyClazz.equals(java.lang.Double.class)) {
                if (arrayFound) {
                    primitive = getPrimitiveArrayClass("[D");
                } else {
                    primitive = Double.TYPE;
                }
            } else if (verifyClazz.equals(java.lang.Float.class)) {
                if (arrayFound) {
                    primitive = getPrimitiveArrayClass("[F");
                } else {
                    primitive = Float.TYPE;
                }
            } else if (verifyClazz.equals(Character.class)) {
                if (arrayFound) {
                    primitive = getPrimitiveArrayClass("[C");
                } else {
                    primitive = Character.TYPE;
                }
            } else if (verifyClazz.equals(Byte.class)) {
                if (arrayFound) {
                    primitive = getPrimitiveArrayClass("[B");
                } else {
                    primitive = Byte.TYPE;
                }
            } else if (verifyClazz.equals(Boolean.class)) {
                if (arrayFound) {
                    primitive = getPrimitiveArrayClass("[Z");
                } else {
                    primitive = Boolean.TYPE;
                }
            }
        }

        return primitive;
    }

    /**
     * Convert an array of object to its primitive counterpart. Values of null
     * will be replaced by the primitive default value.
     * 
     * @param value - array of Object to convert
     * @return array of primitive counterpart
     */
    public static Object createPrimitiveArray(Object value) {
        int valueLength = Array.getLength(value);
        Object valueArray;
        valueArray = Array.newInstance(ClassAnalyser
            .getPrimitiveArrayBaseType(value.getClass()), valueLength);
        Object obj = null;
        for (int count = 0; count < valueLength; count++) {
            obj = Array.get(value, count);
            if (obj != null) {
                Array.set(valueArray, count, obj);
            }
        }
        return valueArray;
    }

    /**
     * 
     * @param clazz
     * @return primitive clazz
     */
    static public Class getPrimitiveArrayBaseType(Class clazz) {
        String clazzName = clazz.getName();
        Class baseClazz = null;
        if (clazzName != null && clazzName.startsWith("[L")) {
            clazzName = clazzName.substring(2, clazzName.length() - 1);
            try {
                baseClazz = Class.forName(clazzName);
            } catch (ClassNotFoundException ex) {
                throw new ClassAnalyserException(
                        "Could not create base class of array");
            }
        }
        Class primitiveClazz = getPrimitiveClass(baseClazz);
        return primitiveClazz;
    }

    /**
     * @param primitive array string presentation
     * @return array class of primitives
     */
    static private Class getPrimitiveArrayClass(String primitive) {
        Class primitiveArrayClazz = null;
        try {
            primitiveArrayClazz = Class.forName(primitive);
        } catch (ClassNotFoundException ex) {
            throw new ClassAnalyserException("Could not create " + primitive
                    + " array");
        }
        return primitiveArrayClazz;
    }

    /**
     * Verify if provided clazz has primitive type
     * 
     * @param checkClass to look for associated primitive type
     * 
     * @return true if primitive type is found
     */
    public static boolean hasPrimitive(Class checkClass) {
        boolean check = false;
        Class verifyClazz = checkClass;
        // check for array class and process with content class
        String name = checkClass.getName();
        if (name.startsWith("[L")) {
            try {
                verifyClazz = Class.forName(name
                    .substring(2, name.length() - 1));
            } catch (ClassNotFoundException ex) {
                throw new ClassAnalyserException(
                        "Could not construct base class from array");
            }
        }
        if (verifyClazz.equals(java.lang.Integer.class)
                || verifyClazz.equals(java.lang.Short.class)
                || verifyClazz.equals(java.lang.Long.class)
                || verifyClazz.equals(java.lang.Float.class)
                || verifyClazz.equals(java.lang.Double.class)
                || verifyClazz.equals(java.lang.Character.class)
                || verifyClazz.equals(java.lang.Byte.class)
                || verifyClazz.equals(java.lang.Boolean.class)) {
            check = true;
        }

        return check;
    }

    /**
     * Take array and return all constructors that have the signature count
     * defined by parameter count.
     * 
     * @param set of constructors to filer
     * @param name of method to process
     * @param count of signature to filter
     * @return Vector of valid constructors
     */
    private static Vector filterByNameAndParamCount(Object[] methods,
            String methodName, int count) {
        Vector selected = new Vector();

        for (int i = 0; i < methods.length; i++) {
            int paramCount;
            String myName;

            if (java.lang.reflect.Constructor.class.isInstance(methods[i])) {
                Constructor myConstr = (Constructor) methods[i];
                myName = CLASS_CONSTRUCTOR;
                paramCount = myConstr.getParameterTypes().length;
            } else {
                Method myMethod = (Method) methods[i];
                myName = myMethod.getName();
                paramCount = myMethod.getParameterTypes().length;
            }

            if ((methodName.compareTo(myName) == 0) && (paramCount == count)) {
                selected.add(methods[i]);
            }
        }

        return selected;
    }

    /**
     * Extract package from object instance
     * 
     * @return package name of object instance
     */
    public static String classPackage(Object obj) {
        String packageName = "";
        String pathSeparator = "/";
        if (obj != null) {
            if (obj.getClass().getPackage() != null) {
                packageName = obj.getClass().getPackage().getName().replaceAll(
                    "\\.", pathSeparator);
            }
        }
        return packageName;
    }
}
