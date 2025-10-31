package be.vinci.pae.utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Logs handler class.
 */
public class Logs {

  private static final String LOG_NAME = "proStage.logs";
  private static final Logger logger = LogManager.getLogger(LOG_NAME);

  /**
   * Write a log with a certain level and a message.
   *
   * @param level   the level of log.
   * @param message the message of log.
   */
  public static void log(Level level, String message) {
    logger.log(level, message);
  }
}