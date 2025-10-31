package be.vinci.pae.domain;

import be.vinci.pae.domain.dto.ContactDTO;

/**
 * Contact interface inheriting the ContactDTO interface and containing business methods.
 */
public interface Contact extends ContactDTO {

  /**
   * Check that the meeting is either remote or on site.
   *
   * @param meeting the meeting to check.
   * @return true if the meeting is correct, false otherwise.
   */
  boolean checkMeeting(String meeting);

  /**
   * Check that the state is one of the following : started, admitted, turned down, accepted, on
   * hold.
   *
   * @param state the state to check.
   * @return true if the state is correct, false otherwise.
   */
  boolean checkState(String state);

  /**
   * Check that the state is started.
   *
   * @return true if the state is started, false otherwise.
   */
  boolean isStarted();


  /**
   * Check if the state is admitted.*
   *
   * @return true if the state is correct, false otherwise.
   */
  boolean isAdmitted();

  /**
   * Check if the state is accepted.
   *
   * @return true if the state is correct, false otherwise.
   */
  boolean isAccepted();
}
