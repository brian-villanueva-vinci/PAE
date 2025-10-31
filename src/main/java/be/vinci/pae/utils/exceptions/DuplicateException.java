package be.vinci.pae.utils.exceptions;

/**
 * Duplicate exception class.
 */
public class DuplicateException extends RuntimeException {

  /**
   * Duplicate exception.
   */
  public DuplicateException() {
    super();
  }

  /**
   * Duplicate exception with a parameter.
   *
   * @param e message.
   */
  public DuplicateException(String e) {
    super(e);
  }

}
