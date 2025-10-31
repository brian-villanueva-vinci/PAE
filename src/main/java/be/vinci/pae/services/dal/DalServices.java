package be.vinci.pae.services.dal;

/**
 * DalService Interface.
 */
public interface DalServices {

  /**
   * Start a transaction.
   */
  void startTransaction();

  /**
   * Commit changes.
   */
  void commitTransaction();

  /**
   * Rollback changes.
   */
  void rollbackTransaction();
}
