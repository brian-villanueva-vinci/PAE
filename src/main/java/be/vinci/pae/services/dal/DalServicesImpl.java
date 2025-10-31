package be.vinci.pae.services.dal;

import be.vinci.pae.utils.Config;
import be.vinci.pae.utils.Logs;
import be.vinci.pae.utils.exceptions.FatalException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.Level;

/**
 * Implementation of DalService.
 */
public class DalServicesImpl implements DalServices, DalBackendServices {

  private static final BasicDataSource dataSource = getDataSource();
  private static final ThreadLocal<Connection> connectionThreadLocal = ThreadLocal.withInitial(
      () -> {
        try {
          Logs.log(Level.DEBUG, "DalServices (connectionThreadLocal) : success!");
          return dataSource.getConnection();
        } catch (SQLException e) {
          Logs.log(Level.FATAL, "DalServices (connectionThreadLocal) : internal error");
          throw new FatalException(e);
        }
      });

  /**
   * Create connection pool.
   *
   * @return BasicDataSource of connection.
   */
  private static BasicDataSource getDataSource() {
    Logs.log(Level.DEBUG, "DalServices (getDataSource) : entrance");
    BasicDataSource dataSource = new BasicDataSource();
    try {
      dataSource.setUrl(Config.getProperty("postgresUrl"));
      dataSource.setUsername(Config.getProperty("postgresUser"));
      dataSource.setPassword(Config.getProperty("postgresPassword"));
      dataSource.setMaxTotal(5);
      Logs.log(Level.DEBUG, "DalServices (getDataSource) : success!");
      return dataSource;
    } catch (Exception e) {
      Logs.log(Level.FATAL, "DalServices (getDataSource) : internal error ! " + e);
      throw new FatalException(e);
    }
  }

  /**
   * Close a connection and remove it from ThreadLocal.
   *
   * @param connection BasicDataSource connection.
   */
  private static void closeConnection(Connection connection) {
    Logs.log(Level.DEBUG, "DalServices (closeConnection) : entrance");
    try {
      connection.close();
      Logs.log(Level.DEBUG, "DalServices (closeConnection) : success!");
    } catch (SQLException e) {
      Logs.log(Level.FATAL, "DalServices (closeConnection) : internal error ! " + e);
      throw new FatalException(e);
    } finally {
      connectionThreadLocal.remove();
    }
  }

  /**
   * Get a connection.
   *
   * @return Connection.
   */
  private Connection getConnection() {
    try {
      Logs.log(Level.DEBUG, "DalServices (getConnection) : entrance");
      Connection connection = connectionThreadLocal.get();
      if (connection == null || connection.isClosed()) {
        connectionThreadLocal.remove();
        Logs.log(Level.DEBUG, "DalServices (getConnection) : connection null -> getDataSource");
        connection = dataSource.getConnection();
        connectionThreadLocal.set(connection);
        Logs.log(Level.DEBUG, "DalServices (getConnection) : success!");
        return connectionThreadLocal.get();
      }
      Logs.log(Level.DEBUG, "DalServices (getConnection) : success!");
      return connection;
    } catch (SQLException e) {
      Logs.log(Level.FATAL, "DalServices (getConnection) : internal error ! " + e);
      throw new FatalException(e);
    }
  }

  /**
   * Get a prepared statement.
   *
   * @param query an sql request.
   * @return a prepared statement.
   */
  public PreparedStatement getPreparedStatement(String query) {
    Logs.log(Level.DEBUG, "DalServices (getPreparedStatement) : entrance");
    try {
      Logs.log(Level.DEBUG, "DalServices (getPreparedStatement) : getting connection");
      Connection connection = getConnection();
      Logs.log(Level.DEBUG, "DalServices (getPreparedStatement) : success!");
      return connection.prepareStatement(query);
    } catch (SQLException e) {
      Logs.log(Level.FATAL, "DalServices (getPreparedStatement) : internal error ! " + e);
      throw new FatalException(e);
    }
  }

  /**
   * Start a transation.
   */
  @Override
  public void startTransaction() {
    Logs.log(Level.DEBUG, "DalServices (startTransaction) : entrance");
    Connection connection = getConnection();
    try {
      connection.setAutoCommit(false);
      Logs.log(Level.DEBUG, "DalServices (startTransaction) : success!");
    } catch (SQLException e) {
      Logs.log(Level.FATAL, "DalServices (startTransaction) : internal error ! " + e);
      throw new FatalException(e);
    }
  }

  /**
   * Commit changes.
   */
  @Override
  public void commitTransaction() {
    Logs.log(Level.DEBUG, "DalServices (commitTransaction) : entrance");
    Connection connection = getConnection();
    try {
      connection.commit();
      Logs.log(Level.DEBUG, "DalServices (commitTransaction) : success!");
    } catch (SQLException e) {
      Logs.log(Level.FATAL, "DalServices (commitTransaction) : internal error ! " + e);
      throw new FatalException(e);
    } finally {
      try {
        connection.setAutoCommit(true);
      } catch (SQLException e) {
        throw new FatalException(e);
      }
      closeConnection(connection);
    }
  }

  /**
   * Rollback changes.
   */
  @Override
  public void rollbackTransaction() {
    Logs.log(Level.DEBUG, "DalServices (rollbackTransaction) : entrance");
    Connection connection = getConnection();
    try {
      connection.rollback();
      Logs.log(Level.DEBUG, "DalServices (rollbackTransaction) : success!");
    } catch (SQLException e) {
      Logs.log(Level.FATAL, "DalServices (rollbackTransaction) : internal error ! " + e);
      throw new FatalException(e);
    } finally {
      try {
        connection.setAutoCommit(true);
      } catch (SQLException e) {
        throw new FatalException(e);
      }
      closeConnection(connection);
    }
  }
}
