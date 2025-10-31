package be.vinci.pae.services.dal;

import java.sql.PreparedStatement;

/**
 * DalService Interface.
 */
public interface DalBackendServices {

  /**
   * Get a prepared statement.
   *
   * @param query an sql request.
   * @return a prepared statement.
   */
  PreparedStatement getPreparedStatement(String query);

}
