/*
 * Created on 06.08.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package junitx.ddtunit.util;

/**
 * Exception class for @see ClassAnalyser&. No special functionality
 * @author jgellien
 */
public class ClassAnalyserException extends RuntimeException {
  /**
   * 
   */
  public ClassAnalyserException() {
    super();
  }
  /**
   * @param prevThrowable
   */
  public ClassAnalyserException(Throwable prevThrowable) {
    super(prevThrowable);
  }
  /**
   * @param message
   * @param prevThrowable
   */
  public ClassAnalyserException(String message, Throwable prevThrowable) {
    super(message, prevThrowable);
  }
  /**
   * @param message
   */
  public ClassAnalyserException(String message) {
    super(message);
  }
}
