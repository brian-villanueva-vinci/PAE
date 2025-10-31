package be.vinci.pae.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Implementation of company.
 */
public class CompanyImpl implements Company {

  private int id;

  private String name;
  private String designation;
  private String address;
  private String phoneNumber;
  private String email;
  private boolean blacklisted;
  private String blacklistMotivation;
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
  public String getName() {
    return this.name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getDesignation() {
    return this.designation;
  }

  @Override
  public void setDesignation(String designation) {
    this.designation = designation;
  }

  @Override
  public String getAddress() {
    return this.address;
  }

  @Override
  public void setAddress(String address) {
    this.address = address;
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

  @Override
  public boolean isBlacklisted() {
    return this.blacklisted;
  }

  @Override
  public void setIsBlacklisted(boolean blacklisted) {
    this.blacklisted = blacklisted;
  }

  @Override
  public String getBlacklistMotivation() {
    return this.blacklistMotivation;
  }

  @Override
  public void setBlacklistMotivation(String blacklistMotivation) {
    this.blacklistMotivation = blacklistMotivation;
  }

  @Override
  public int getVersion() {
    return this.version;
  }

  @Override
  public void setVersion(int version) {
    this.version = version;
  }

  @Override
  @JsonIgnore
  public boolean studentCanContact() {
    return !this.blacklisted;
  }
}
