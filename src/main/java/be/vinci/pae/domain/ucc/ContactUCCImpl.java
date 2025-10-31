package be.vinci.pae.domain.ucc;

import be.vinci.pae.domain.Company;
import be.vinci.pae.domain.Contact;
import be.vinci.pae.domain.dto.ContactDTO;
import be.vinci.pae.domain.dto.InternshipDTO;
import be.vinci.pae.domain.dto.UserDTO;
import be.vinci.pae.services.dal.DalServices;
import be.vinci.pae.services.dao.CompanyDAO;
import be.vinci.pae.services.dao.ContactDAO;
import be.vinci.pae.services.dao.InternshipDAO;
import be.vinci.pae.services.dao.UserDAO;
import be.vinci.pae.utils.Logs;
import be.vinci.pae.utils.exceptions.DuplicateException;
import be.vinci.pae.utils.exceptions.InvalidRequestException;
import be.vinci.pae.utils.exceptions.NotAllowedException;
import be.vinci.pae.utils.exceptions.ResourceNotFoundException;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import org.apache.logging.log4j.Level;

/**
 * Implementation of ContactUCC.
 */
public class ContactUCCImpl implements ContactUCC {

  @Inject
  private ContactDAO contactDAO;
  @Inject
  private DalServices dalServices;
  @Inject
  private UserDAO userDAO;
  @Inject
  private CompanyDAO companyDAO;
  @Inject
  private InternshipUCC internshipUCC;
  @Inject
  private InternshipDAO internshipDAO;


  @Override
  public ContactDTO start(int company, int studentId) {
    Logs.log(Level.DEBUG, "ContactUCC (start) : entrance");
    ContactDTO contact;
    try {
      dalServices.startTransaction();
      UserDTO studentDTO = userDAO.getOneUserById(studentId);
      if (studentDTO == null) {
        Logs.log(Level.ERROR,
            "ContactUCC (start) : student not found");
        throw new ResourceNotFoundException();
      }
      Company company2 = (Company) companyDAO.getOneCompanyById(company);
      if (company2 == null) {
        Logs.log(Level.ERROR,
            "ContactUCC (start) : company not found");
        throw new ResourceNotFoundException();
      } else if (!company2.studentCanContact()) {
        Logs.log(Level.ERROR,
            "ContactUCC (start) : company is blacklisted");
        throw new InvalidRequestException();
      }

      LocalDate date = LocalDate.now();
      String schoolYear;
      if (date.getMonthValue() < 9) {
        schoolYear = date.getYear() - 1 + "-" + date.getYear();
      } else {
        schoolYear = date.getYear() + "-" + date.getYear() + 1;
      }

      if (internshipDAO.getOneInternshipByIdUserSchoolYear(studentId, schoolYear) != null) {
        Logs.log(Level.INFO, "ContactUCC (start) : student already has an internship");
        throw new InvalidRequestException();
      }

      ContactDTO contactFound = contactDAO
          .findContactByCompanyStudentSchoolYear(company, studentId, schoolYear);
      if (contactFound != null) {
        Logs.log(Level.ERROR,
            "ContactUCC (start) : contact already exist with this student, company, year");
        throw new DuplicateException("This contact already exist for this year.");
      }

      contact = contactDAO.startContact(company, studentId, schoolYear);
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }
    dalServices.commitTransaction();
    Logs.log(Level.DEBUG, "ContactUCC (start) : success!");
    return contact;
  }

  @Override
  public List<ContactDTO> getAllContactsByStudent(int student) {
    List<ContactDTO> listContactDTO;
    try {
      dalServices.startTransaction();
      listContactDTO = contactDAO.getAllContactsByStudent(student);
      if (listContactDTO == null) {
        throw new ResourceNotFoundException();
      }
      dalServices.commitTransaction();
      return listContactDTO;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }
  }

  @Override
  public ContactDTO getOneById(int id) {
    ContactDTO contact;
    try {
      dalServices.startTransaction();
      contact = contactDAO.findContactById(id);
      if (contact == null) {
        throw new ResourceNotFoundException();
      }
      dalServices.commitTransaction();
      return contact;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }
  }

  @Override
  public ContactDTO unsupervise(int contactId, int student, int version) {
    Contact contact;
    ContactDTO contactDTO;
    try {
      dalServices.startTransaction();
      contact = (Contact) contactDAO.findContactById(contactId);
      if (contact == null) {
        Logs.log(Level.ERROR,
            "ContactUCC (unsupervise) : contact not found");
        throw new ResourceNotFoundException();
      }
      if (contact.getVersion() != version) {
        Logs.log(Level.ERROR,
            "ContactUCC (unsupervise) : different version");
        throw new DuplicateException("User looking at old version");
      }
      if (!contact.isStarted() && !contact.isAdmitted()) {
        throw new NotAllowedException();
      } else if (contact.getStudent().getId() != student) {
        throw new NotAllowedException();
      }

      contact.setVersion(version + 1);
      contact.setState("non suivi");

      contactDTO = contactDAO.updateContact(contact, version);

      if (contactDTO == null) {
        Logs.log(Level.ERROR, "ContactUCC (unsupervise) : the contact's version isn't matching");
        throw new DuplicateException("Someone updated before us");
      }

      dalServices.commitTransaction();
      Logs.log(Level.DEBUG, "ContactUCC (unsupervise) : success!");
      return contactDTO;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }
  }

  @Override
  public ContactDTO admit(int contactId, String meeting, int studentId, int version) {
    Logs.log(Level.DEBUG, "ContactUCC (admit) : entrance");
    Contact contact;
    ContactDTO contactDTO;
    try {
      dalServices.startTransaction();
      contact = (Contact) contactDAO.findContactById(contactId);
      if (contact == null) {
        Logs.log(Level.ERROR, "ContactUCC (admit) : contact not found");
        throw new ResourceNotFoundException();
      }
      if (contact.getVersion() != version) {
        Logs.log(Level.ERROR,
            "ContactUCC (unsupervise) : different version");
        throw new DuplicateException("User looking at old version");
      }
      if (contact.getStudent().getId() != studentId) {
        Logs.log(Level.ERROR,
            "ContactUCC (admit) : the student of the contact isn't the student from the token");
        throw new NotAllowedException();
      }
      if (!contact.checkMeeting(meeting)) {
        Logs.log(Level.ERROR, "ContactUCC (admit) : type meeting is invalid");
        throw new InvalidRequestException();
      }
      if (!contact.isStarted()) {
        Logs.log(Level.ERROR, "ContactUCC (admit) : contact's state isn't started");
        throw new InvalidRequestException();
      }

      contact.setVersion(version + 1);
      contact.setState("pris");
      contact.setMeeting(meeting);

      contactDTO = contactDAO.updateContact(contact, version);

      if (contactDTO == null) {
        Logs.log(Level.ERROR, "ContactUCC (admit) : the contact's version isn't matching");
        throw new DuplicateException("Someone updated before us");
      }

      dalServices.commitTransaction();
      Logs.log(Level.DEBUG, "ContactUCC (admit) : success!");
      return contactDTO;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }
  }

  @Override
  public ContactDTO turnDown(int contactId, String reasonForRefusal, int studentId, int version) {
    Logs.log(Level.DEBUG, "ContactUCC (turnDown) : entrance");
    Contact contact;
    ContactDTO contactDTO;
    try {
      dalServices.startTransaction();
      contact = (Contact) contactDAO.findContactById(contactId);
      if (contact == null) {
        dalServices.rollbackTransaction();
        Logs.log(Level.ERROR, "ContactUCC (turnDown) : contact not found");
        throw new ResourceNotFoundException();
      }
      if (contact.getVersion() != version) {
        Logs.log(Level.ERROR,
            "ContactUCC (turnDown) : different version");
        throw new DuplicateException("User looking at old version");
      }
      if (contact.getStudent().getId() != studentId) {
        Logs.log(Level.ERROR,
            "ContactUCC (turnDown) : the student of the contact isn't the student from the token");
        throw new NotAllowedException();
      }
      if (!contact.isAdmitted()) {
        Logs.log(Level.ERROR, "ContactUCC (turnDown) : contact's state not admitted");
        throw new NotAllowedException();
      }

      contact.setVersion(version + 1);
      contact.setState("refusé");
      contact.setReasonRefusal(reasonForRefusal);

      contactDTO = contactDAO.updateContact(contact, version);

      if (contactDTO == null) {
        Logs.log(Level.ERROR, "ContactUCC (turndown) : the contact's version isn't matching");
        throw new DuplicateException("Someone updated before us");
      }

      dalServices.commitTransaction();
      Logs.log(Level.DEBUG, "ContactUCC (turnDown) : success!");
      return contactDTO;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }
  }

  @Override
  public void putStudentContactsOnHold(int studentId) {
    Logs.log(Level.DEBUG, "ContactUCC (putStudentContactsOnHold) : entrance");
    List<ContactDTO> contactDTOList =
        contactDAO.getAllContactsByStudentStartedOrAdmitted(studentId);
    for (ContactDTO c : contactDTOList) {
      int currentVersion = c.getVersion();
      c.setVersion(currentVersion + 1);
      c.setState("suspendu");

      contactDAO.updateContact(c, currentVersion);
    }
  }

  @Override
  public InternshipDTO accept(int contactId, int studentId, InternshipDTO internshipDTO,
      int version) {
    Logs.log(Level.DEBUG, "ContactUCC (accept) : entrance");
    Contact contact;
    try {
      dalServices.startTransaction();
      contact = (Contact) contactDAO.findContactById(contactId);

      if (contact == null) {
        Logs.log(Level.ERROR,
            "ContactUCC (accept) : contact not found");
        throw new ResourceNotFoundException();
      }
      if (contact.getVersion() != version) {
        Logs.log(Level.ERROR,
            "ContactUCC (accept) : different version");
        throw new DuplicateException("User looking at old version");
      }
      if (!contact.isAdmitted()) {
        Logs.log(Level.ERROR, "ContactUCC (accept) : contact's state not admitted");
        throw new NotAllowedException();
      } else if (contact.getStudent().getId() != studentId) {
        throw new NotAllowedException();
      }

      contact.setVersion(version + 1);
      contact.setState("accepté");

      ContactDTO contactDTO = contactDAO.updateContact(contact, version);

      if (contactDTO == null) {
        Logs.log(Level.ERROR, "ContactUCC (accept) : the contact's version isn't matching");
        throw new DuplicateException("Someone updated before us");
      }
      InternshipDTO internshipDTO1 = internshipUCC.createInternship(internshipDTO, studentId);
      putStudentContactsOnHold(studentId);

      dalServices.commitTransaction();
      return internshipDTO1;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }
  }

  @Override
  public List<ContactDTO> getAllContactsByCompany(int company) {
    List<ContactDTO> listContactDTO;
    try {
      dalServices.startTransaction();
      listContactDTO = contactDAO.getAllContactsByCompany(company);
      if (listContactDTO == null) {
        throw new ResourceNotFoundException();
      }
      dalServices.commitTransaction();
      return listContactDTO;
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw e;
    }
  }
}
