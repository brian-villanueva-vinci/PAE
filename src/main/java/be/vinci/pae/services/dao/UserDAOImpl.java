package be.vinci.pae.services.dao;

import be.vinci.pae.domain.UserFactory;
import be.vinci.pae.domain.dto.UserDTO;
import be.vinci.pae.services.dal.DalBackendServices;
import be.vinci.pae.utils.Logs;
import be.vinci.pae.utils.exceptions.FatalException;
import jakarta.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.Level;

/**
 * Implementation of UserDAO.
 */
public class UserDAOImpl implements UserDAO {

  @Inject
  private DalBackendServices dalBackendServices;
  @Inject
  private UserFactory userFactory;

  /**
   * Get one user by email then set the userDTO if user exist.
   *
   * @param email user' email.
   * @return userDTO with setter corresponding to the email, null otherwise.
   */
  @Override
  public UserDTO getOneUserByEmail(String email) {
    Logs.log(Level.DEBUG, "UserDAO (getOneUserByEmail) : entrance");
    String requestSql = """
        SELECT us.user_id, us.email AS us_email, us.lastname AS us_lastname,
        us.firstname AS us_firstname,
        us.phone_number AS us_phone_number, us.password, us.registration_date,
        us.school_year AS us_school_year, us.role, us.version AS us_version
        FROM prostage.users us
        WHERE us.email = ?
        """;

    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(requestSql)) {
      ps.setString(1, email);
      Logs.log(Level.DEBUG, "UserDAO (getOneUserByEmail) : success!");
      return buildUserDTO(ps);
    } catch (SQLException e) {
      Logs.log(Level.FATAL, "UserDAO (getOneUserByEmail) : internal error");
      throw new FatalException(e);
    }
  }

  @Override
  public UserDTO getOneUserById(int id) {
    Logs.log(Level.INFO, "UserDAO (getOneUserById) : entrance");
    String requestSql = """
        SELECT us.user_id, us.email AS us_email, us.lastname AS us_lastname,
        us.firstname AS us_firstname,
        us.phone_number AS us_phone_number, us.password, us.registration_date,
        us.school_year AS us_school_year, us.role, us.version AS us_version
        FROM prostage.users us
        WHERE us.user_id = ?
        """;

    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(requestSql)) {
      ps.setInt(1, id);
      Logs.log(Level.DEBUG, "UserDAO (getOneUserById) : success!");
      return buildUserDTO(ps);
    } catch (SQLException e) {
      Logs.log(Level.FATAL, "UserDAO (getOneUserById) : internal error");
      throw new FatalException(e);
    }
  }

  /**
   * Build the UserDTO based on the prepared statement.
   *
   * @param ps the prepared statement.
   * @return the userDTO built.
   */
  private UserDTO buildUserDTO(PreparedStatement ps) {
    try (ResultSet rs = ps.executeQuery()) {
      if (rs.next()) {
        return DTOSetServices.setUserDTO(userFactory.getUserDTO(), rs);
      }
      return null;
    } catch (SQLException e) {
      Logs.log(Level.FATAL, "UserDAO (buildUserDTO) : internal error!");
      throw new FatalException(e);
    }
  }

  @Override
  public List<UserDTO> getAllUsers() {
    Logs.log(Level.INFO, "UserDAO (getAllUsers) : entrance");
    List<UserDTO> userDTOList = new ArrayList<>();

    String requestSql = """
        SELECT us.user_id, us.email AS us_email, us.lastname AS us_lastname,
        us.firstname AS us_firstname,
        us.phone_number AS us_phone_number, us.password, us.registration_date,
        us.school_year AS us_school_year, us.role, us.version AS us_version
        FROM prostage.users us
        ORDER BY us.user_id
        """;

    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(requestSql)) {
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          userDTOList.add(DTOSetServices.setUserDTO(userFactory.getUserDTO(), rs));
        }
      }
    } catch (SQLException e) {
      Logs.log(Level.FATAL, "UserDAO (getAllUsers) : internal error!");
      throw new FatalException(e);
    }
    Logs.log(Level.DEBUG, "UserDAO (getAllUsers) : success!");
    return userDTOList;
  }

  @Override
  public UserDTO editOneUser(UserDTO user, int version) {
    Logs.log(Level.DEBUG, "UserDAO (editOneUser) : entrance");
    String requestSql = """
        UPDATE prostage.users
        SET email = ?, lastname = ?, firstname = ?,
            phone_number = ?, password = ?, registration_date = ?,
            school_year = ?, role = ?, version = ?
        WHERE user_id = ? AND version = ? RETURNING *;
        """;

    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(requestSql)) {
      ps.setString(2, user.getLastname());
      ps.setString(1, user.getEmail());
      ps.setString(4, user.getPhoneNumber());
      ps.setString(3, user.getFirstname());
      ps.setString(5, user.getPassword());
      ps.setDate(6, user.getRegistrationDate());
      ps.setString(7, user.getSchoolYear());
      ps.setString(8, user.getRole());
      ps.setInt(9, user.getVersion());
      ps.setInt(10, user.getId());
      ps.setInt(11, version);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return getOneUserById(user.getId());
        }
      }
      return null;
    } catch (SQLException e) {
      Logs.log(Level.FATAL, "UserDAO (editOneUser) : internal error!");
      throw new FatalException(e);
    }
  }

  /**
   * Add one user.
   *
   * @param user user to add.
   * @return UserDTO of added user, null otherwise.
   */
  @Override
  public UserDTO addOneUser(UserDTO user) {
    Logs.log(Level.DEBUG, "UserDAO (addOneUser) : entrance");
    String requestSql = """
        INSERT INTO prostage.users(email, lastname, firstname, phone_number,
        password, registration_date, school_year, role, version) VALUES (?,?,?,?,?,?,?,?,1)
        RETURNING email AS inserted_email
        """;

    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(requestSql)) {
      ps.setString(1, user.getEmail());
      ps.setString(2, user.getLastname());
      ps.setString(3, user.getFirstname());
      ps.setString(4, user.getPhoneNumber());
      ps.setString(5, user.getPassword());
      ps.setDate(6, user.getRegistrationDate());
      ps.setString(7, user.getSchoolYear());
      ps.setString(8, user.getRole());
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          Logs.log(Level.DEBUG, "UserDAO (addOneUser) : success!");
          return getOneUserByEmail(rs.getString("inserted_email"));
        }
        return null;
      }
    } catch (SQLException e) {
      Logs.log(Level.FATAL, "UserDAO (addOneUser) : internal error");
      throw new FatalException(e);
    }

  }
}
