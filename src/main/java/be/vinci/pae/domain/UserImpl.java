package be.vinci.pae.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.sql.Date;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Implementation of User.
 */
public class UserImpl implements User {

  private int id;
  private String email;
  private String lastname;
  private String firstname;
  private String phoneNumber;
  @JsonProperty(access = Access.WRITE_ONLY)
  private String password;
  private Date registrationDate;
  private String schoolYear;
  private String role;
  private int version;

  @Override
  public int getId() {
    return this.id;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

  @Override
  public String getEmail() {
    return this.email;
  }

  @Override
  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  public String getLastname() {
    return this.lastname;
  }

  @Override
  public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  @Override
  public String getFirstname() {
    return this.firstname;
  }

  @Override
  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  @Override
  public String getPhoneNumber() {
    return this.phoneNumber;
  }

  @Override
  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public void setPassword(String password) {
    this.password = password;
  }

  public Date getRegistrationDate() {
    return this.registrationDate;
  }

  @Override
  public void setRegistrationDate(Date registrationDate) {
    this.registrationDate = registrationDate;
  }

  @Override
  public String getSchoolYear() {
    return this.schoolYear;
  }

  @Override
  public void setSchoolYear(String schoolYear) {
    this.schoolYear = schoolYear;
  }

  @Override
  public String getRole() {
    return this.role;
  }

  @Override
  public void setRole(String role) {
    this.role = role;
  }

  @Override
  public int getVersion() {
    return version;
  }

  @Override
  public void setVersion(int version) {
    this.version = version;
  }

  @Override
  public boolean checkPassword(String password) {
    return BCrypt.checkpw(password, this.password);
  }

  @Override
  public void hashPassword() {
    this.password = BCrypt.hashpw(this.password, BCrypt.gensalt());
  }
}
