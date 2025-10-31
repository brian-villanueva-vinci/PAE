package be.vinci.pae.services.dao;

import be.vinci.pae.domain.dto.ContactDTO;
import java.util.List;

/**
 * ContactDAO interface.
 */
public interface ContactDAO {

  /**
   * Find a contact by its company, student and school year.
   *
   * @param company    the company.
   * @param student    the student.
   * @param schoolYear the school year.
   * @return a ContactDTO if the contact was found, null otherwise.
   */
  ContactDTO findContactByCompanyStudentSchoolYear(int company, int student, String schoolYear);

  /**
   * Create the contact.
   *
   * @param student    the student.
   * @param company    the company.
   * @param schoolYear the school year.
   * @return the contact created.
   */
  ContactDTO startContact(int company, int student, String schoolYear);


  /**
   * Get all contacts by a student id.
   *
   * @param student student' id.
   * @return a list of all contacts.
   */
  List<ContactDTO> getAllContactsByStudent(int student);

  /**
   * Find a contact by its id.
   *
   * @param contactId the contact id.
   * @return the contact found.
   */
  ContactDTO findContactById(int contactId);

  /**
   * admit the contact.
   *
   * @param contactId the id of the contact.
   * @param meeting   the way how they met.
   * @param version   the version of the objet
   * @return the contact updated.
   */
  ContactDTO admitContact(int contactId, String meeting, int version);

  /**
   * Unsupervise the contact.
   *
   * @param contactId the contact id.
   * @param version   the version of the contact
   * @return the unsupervised contact.
   */
  ContactDTO unsupervise(int contactId, int version);

  /**
   * Turn down a contact and give the reason for refusal.
   *
   * @param contactId        the id of the contact.
   * @param reasonForRefusal the reason of the refusal.
   * @param version          the version of the contact
   * @return a ContactDTO if the update of contact was successful, null otherwise.
   */
  ContactDTO turnDown(int contactId, String reasonForRefusal, int version);

  /**
   * Get all the student's contact that are in started or admitted state.
   *
   * @param student the student to get contacts from.
   * @return a list containing all the contacts in the right state.
   */
  List<ContactDTO> getAllContactsByStudentStartedOrAdmitted(int student);

  /**
   * Put a contact on hold.
   *
   * @param contactDTO the contact to put on hold.
   * @return the updated contactDTO.
   */
  ContactDTO putContactOnHold(ContactDTO contactDTO);

  /**
   * Accept a contact.
   *
   * @param contactId the contact id.
   * @param version   the version of the contact.
   * @return the accepted contact.
   */
  ContactDTO accept(int contactId, int version);

  /**
   * Get all contacts by a company id.
   *
   * @param company company's id.
   * @return a list of all contacts.
   */
  List<ContactDTO> getAllContactsByCompany(int company);

  /**
   * Update a contact.
   *
   * @param contact               contactDTO.
   * @param contactCurrentVersion contact version.
   * @return the updated contact.
   */
  ContactDTO updateContact(ContactDTO contact, int contactCurrentVersion);

  /**
   * Get all contacts by a company id.
   *
   * @param company the company.
   * @return a list of contacts.
   */
  List<ContactDTO> getAllContactsByCompanyStartedOrAdmitted(int company);
}
