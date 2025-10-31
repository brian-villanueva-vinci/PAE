package be.vinci.pae.domain;

import be.vinci.pae.domain.dto.CompanyDTO;
import be.vinci.pae.domain.dto.SupervisorDTO;

/**
 * Implementation of supervisor's interface.
 */
public class SupervisorImpl implements SupervisorDTO {

  private int id;
  private CompanyDTO company;
  private String lastname;
  private String firstname;
  private String phoneNumber;
  private String email;

  @Override
  public int getId() {
    return this.id;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

  @Override
  public CompanyDTO getCompany() {
    return this.company;
  }

  @Override
  public void setCompany(CompanyDTO company) {
    this.company = company;
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
  public String getEmail() {
    return this.email;
  }

  @Override
  public void setEmail(String email) {
    this.email = email;
  }
}
