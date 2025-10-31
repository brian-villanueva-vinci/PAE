package be.vinci.pae.domain;

import be.vinci.pae.domain.dto.CompanyDTO;
import be.vinci.pae.domain.dto.UserDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

/**
 * Implementation of Contact.
 */
public class ContactImpl implements Contact {

  private int id;
  private CompanyDTO company;
  private UserDTO student;

  private String meeting;
  private String state;
  private String reasonRefusal;
  private String schoolYear;
  private int version;

  @Override
  public boolean checkMeeting(String meeting) {
    return meeting.equals("A distance") || meeting.equals("Dans l entreprise");
  }

  @Override
  public boolean checkState(String state) {
    return List.of("initié", "pris", "refusé", "accepté", "non suivi", "suspendu").contains(state);
  }


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
  public UserDTO getStudent() {
    return this.student;
  }

  @Override
  public void setStudent(UserDTO student) {
    this.student = student;
  }

  @Override
  public String getMeeting() {
    return meeting;
  }

  @Override
  public void setMeeting(String meeting) {
    this.meeting = meeting;
  }

  @Override
  public String getState() {
    return this.state;
  }

  @Override
  public void setState(String state) {
    this.state = state;
  }

  @Override
  public String getReasonRefusal() {
    return reasonRefusal;
  }

  @Override
  public void setReasonRefusal(String reasonRefusal) {
    this.reasonRefusal = reasonRefusal;
  }

  @Override
  public String getSchoolYear() {
    return schoolYear;
  }

  @Override
  public void setSchoolYear(String schoolYear) {
    this.schoolYear = schoolYear;
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
  @JsonIgnore
  public boolean isStarted() {
    return this.state.equals("initié");
  }

  @Override
  @JsonIgnore
  public boolean isAdmitted() {
    return this.state.equals("pris");
  }

  @Override
  @JsonIgnore
  public boolean isAccepted() {
    return this.state.equals("accepté");
  }
}
