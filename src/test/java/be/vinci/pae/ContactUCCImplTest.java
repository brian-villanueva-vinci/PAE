package be.vinci.pae;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import be.vinci.pae.domain.CompanyFactory;
import be.vinci.pae.domain.ContactFactory;
import be.vinci.pae.domain.InternshipFactory;
import be.vinci.pae.domain.UserFactory;
import be.vinci.pae.domain.dto.CompanyDTO;
import be.vinci.pae.domain.dto.ContactDTO;
import be.vinci.pae.domain.dto.InternshipDTO;
import be.vinci.pae.domain.dto.UserDTO;
import be.vinci.pae.domain.ucc.ContactUCC;
import be.vinci.pae.domain.ucc.InternshipUCC;
import be.vinci.pae.services.dal.DalServices;
import be.vinci.pae.services.dao.CompanyDAO;
import be.vinci.pae.services.dao.ContactDAO;
import be.vinci.pae.services.dao.InternshipDAO;
import be.vinci.pae.services.dao.UserDAO;
import be.vinci.pae.utils.exceptions.DuplicateException;
import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.exceptions.InvalidRequestException;
import be.vinci.pae.utils.exceptions.NotAllowedException;
import be.vinci.pae.utils.exceptions.ResourceNotFoundException;
import java.util.List;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * ContactUCC test class.
 */
public class ContactUCCImplTest {

  private static ServiceLocator serviceLocator;
  private static ContactDAO contactDAOMock;
  private static UserDAO userDAOMock;
  private static CompanyDAO companyDAOMock;
  private static InternshipDAO internshipDAOMock;
  private static DalServices dalServicesMock;
  private static InternshipUCC internshipUCCMock;
  private static ContactFactory contactFactory;
  private static InternshipFactory internshipFactory;
  private ContactUCC contactUCC;
  private ContactDTO contactDTO;
  private UserDTO userDTO;
  private CompanyDTO companyDTO;
  private InternshipDTO internshipDTO;

  @BeforeAll
  static void init() {
    serviceLocator = ServiceLocatorUtilities.bind(new BinderTest());
    contactDAOMock = serviceLocator.getService(ContactDAO.class);
    userDAOMock = serviceLocator.getService(UserDAO.class);
    companyDAOMock = serviceLocator.getService(CompanyDAO.class);
    internshipDAOMock = serviceLocator.getService(InternshipDAO.class);
    dalServicesMock = serviceLocator.getService(DalServices.class);
    contactFactory = serviceLocator.getService(ContactFactory.class);
    internshipFactory = serviceLocator.getService(InternshipFactory.class);
  }

  @BeforeEach
  void setup() {
    contactUCC = serviceLocator.getService(ContactUCC.class);

    internshipUCCMock = Mockito.mock(InternshipUCC.class);

    contactDTO = contactFactory.getContactDTO();

    UserFactory userFactory = serviceLocator.getService(UserFactory.class);
    userDTO = userFactory.getUserDTO();

    CompanyFactory companyFactory = serviceLocator.getService(CompanyFactory.class);
    companyDTO = companyFactory.getCompanyDTO();

    internshipDTO = internshipFactory.getInternshipDTO();

    Mockito.doNothing().when(dalServicesMock).startTransaction();
    Mockito.doNothing().when(dalServicesMock).commitTransaction();
    Mockito.doNothing().when(dalServicesMock).rollbackTransaction();
  }

  @AfterEach
  void reset() {
    Mockito.reset(contactDAOMock);
    Mockito.reset(userDAOMock);
    Mockito.reset(companyDAOMock);
    Mockito.reset(internshipDAOMock);
  }

  @Test
  @DisplayName("Test start with wrong student id")
  public void testStartWrongStudentId() {
    Mockito.when(userDAOMock.getOneUserById(1)).thenReturn(null);
    Mockito.when(companyDAOMock.getOneCompanyById(1)).thenReturn(companyDTO);
    assertThrows(ResourceNotFoundException.class, () -> contactUCC.start(1, 1));
  }

  @Test
  @DisplayName("Test start with wrong company id")
  public void testStartWrongCompanyId() {
    Mockito.when(userDAOMock.getOneUserById(1)).thenReturn(userDTO);
    Mockito.when(companyDAOMock.getOneCompanyById(1)).thenReturn(null);
    assertThrows(ResourceNotFoundException.class, () -> contactUCC.start(1, 1));
  }

  @Test
  @DisplayName("Test start with blacklisted company")
  public void testStartBlacklistedCompany() {
    companyDTO.setIsBlacklisted(true);
    Mockito.when(userDAOMock.getOneUserById(1)).thenReturn(userDTO);
    Mockito.when(companyDAOMock.getOneCompanyById(1)).thenReturn(companyDTO);
    assertThrows(InvalidRequestException.class, () -> contactUCC.start(1, 1));
  }

  @Test
  @DisplayName("Test start with user with internship")
  public void testStartWithUserWithInternship() {
    Mockito.when(userDAOMock.getOneUserById(1)).thenReturn(userDTO);
    Mockito.when(companyDAOMock.getOneCompanyById(1)).thenReturn(companyDTO);
    Mockito.when(internshipDAOMock.getOneInternshipByIdUserSchoolYear(1, "2023-2024"))
        .thenReturn(internshipDTO);
    assertThrows(InvalidRequestException.class, () -> contactUCC.start(1, 1));
  }

  @Test
  @DisplayName("Test start duplicated")
  public void testStartDuplicated() {
    userDTO.setSchoolYear("2023-2024");
    Mockito.when(userDAOMock.getOneUserById(1)).thenReturn(userDTO);
    Mockito.when(companyDAOMock.getOneCompanyById(1)).thenReturn(companyDTO);
    Mockito.when(contactDAOMock.findContactByCompanyStudentSchoolYear(1, 1, "2023-2024"))
        .thenReturn(contactDTO);
    assertThrows(DuplicateException.class, () -> contactUCC.start(1, 1));
  }

  @Test
  @DisplayName("Test start good contact and company")
  public void testStartGoodContactCompany() {
    contactDTO.setId(50);
    userDTO.setSchoolYear("2023-2024");
    Mockito.when(userDAOMock.getOneUserById(1)).thenReturn(userDTO);
    Mockito.when(companyDAOMock.getOneCompanyById(1)).thenReturn(companyDTO);
    Mockito.when(contactDAOMock.findContactByCompanyStudentSchoolYear(1, 1, "2023-2024"))
        .thenReturn(null);
    Mockito.when(contactDAOMock.startContact(1, 1, "2023-2024")).thenReturn(contactDTO);
    assertNotNull(contactUCC.start(1, 1));
  }

  @Test
  @DisplayName("Test unsupervise contact is null")
  public void testUnsuperviseContactNotFound() {
    Mockito.when(contactDAOMock.findContactById(1)).thenReturn(null);
    assertThrows(ResourceNotFoundException.class, () -> contactUCC.unsupervise(1, 1, 1));
  }

  @Test
  @DisplayName("Test unsupervise contact with wrong student")
  public void testUnsuperviseContactWrongStudent() {
    userDTO.setId(5);
    contactDTO.setStudent(userDTO);
    contactDTO.setState("initié");
    contactDTO.setVersion(1);
    Mockito.when(contactDAOMock.findContactById(1)).thenReturn(contactDTO);
    assertThrows(NotAllowedException.class, () -> contactUCC.unsupervise(1, 1, 1));
  }

  @Test
  @DisplayName("Test unsupervise contact with state different than started or admitted")
  public void testUnsuperviseContactWrongState() {
    userDTO.setId(1);
    contactDTO.setStudent(userDTO);
    contactDTO.setState("refusé");
    contactDTO.setVersion(1);
    Mockito.when(contactDAOMock.findContactById(1)).thenReturn(contactDTO);
    assertThrows(NotAllowedException.class, () -> contactUCC.unsupervise(1, 1, 1));
  }

  @Test
  @DisplayName("Test unsupervise contact correctly started")
  public void testUnsuperviseContactCorrectlyStarted() {
    userDTO.setId(1);
    contactDTO.setStudent(userDTO);
    contactDTO.setState("initié");
    contactDTO.setVersion(1);
    Mockito.when(contactDAOMock.findContactById(1)).thenReturn(contactDTO);
    Mockito.when(contactDAOMock.updateContact(contactDTO, 1)).thenReturn(contactDTO);
    assertNotNull(contactUCC.unsupervise(1, 1, 1));
  }

  @Test
  @DisplayName("Test unsupervise contact with wrong version")
  public void testUnsuperviseContactWithWrongVersion() {
    userDTO.setId(1);
    contactDTO.setStudent(userDTO);
    contactDTO.setState("pris");
    contactDTO.setVersion(2);
    Mockito.when(contactDAOMock.findContactById(1)).thenReturn(contactDTO);
    assertThrows(DuplicateException.class, () -> contactUCC.unsupervise(1, 1, 1));
  }

  @Test
  @DisplayName("Test unsupervise contact correctly admitted")
  public void testUnsuperviseContactCorrectlyAdmitted() {
    userDTO.setId(1);
    contactDTO.setStudent(userDTO);
    contactDTO.setState("pris");
    contactDTO.setVersion(1);
    Mockito.when(contactDAOMock.findContactById(1)).thenReturn(contactDTO);
    Mockito.when(contactDAOMock.updateContact(contactDTO, 1)).thenReturn(contactDTO);
    assertNotNull(contactUCC.unsupervise(1, 1, 1));
  }

  @Test
  @DisplayName("Test unsupervise contact with wrong version")
  public void testUnsuperviseContactConcurrentUpdate() {
    userDTO.setId(1);
    contactDTO.setStudent(userDTO);
    contactDTO.setState("pris");
    contactDTO.setVersion(1);
    Mockito.when(contactDAOMock.findContactById(1)).thenReturn(contactDTO);
    Mockito.when(contactDAOMock.updateContact(contactDTO, 1)).thenReturn(null);
    assertThrows(DuplicateException.class, () -> contactUCC.unsupervise(1, 1, 1));
  }

  @Test
  @DisplayName("Test start, unsupervise, admit, turn down, getOneById, getAllContactsByStudent,"
      + " getAllContactsByCompany contact crash transaction")
  public void testCrashTransaction() {
    Mockito.doThrow(new FatalException(new RuntimeException()))
        .when(dalServicesMock).startTransaction();
    assertAll(
        () -> assertThrows(FatalException.class, () -> {
          contactUCC.start(1, 1);
        }),
        () -> assertThrows(FatalException.class, () -> {
          contactUCC.accept(1, 1, internshipDTO, 1);
        }),
        () -> assertThrows(FatalException.class, () -> {
          contactUCC.unsupervise(1, 1, 1);
        }),
        () -> assertThrows(FatalException.class, () -> {
          contactUCC.admit(1, "sur place", 1, 1);
        }),
        () -> assertThrows(FatalException.class, () -> {
          contactUCC.turnDown(1, "Student has not answered fast enough", 1, 1);
        }),
        () -> assertThrows(FatalException.class, () -> {
          contactUCC.getOneById(1);
        }),
        () -> assertThrows(FatalException.class, () -> {
          contactUCC.getAllContactsByStudent(1);
        }),
        () -> assertThrows(FatalException.class, () -> {
          contactUCC.getAllContactsByCompany(1);
        })
    );
  }

  @Test
  @DisplayName("Test admit contact is null")
  public void testAdmitContactNotFound() {
    Mockito.when(contactDAOMock.findContactById(1)).thenReturn(null);
    assertThrows(ResourceNotFoundException.class,
        () -> contactUCC.admit(1, "Dans l entreprise", 1, 1));
  }

  @Test
  @DisplayName("Test admit contact with wrong student")
  public void testAdmitContactWrongStudent() {
    userDTO.setId(2);
    contactDTO.setStudent(userDTO);
    contactDTO.setState("initié");
    contactDTO.setVersion(1);
    Mockito.when(contactDAOMock.findContactById(1)).thenReturn(contactDTO);
    assertThrows(NotAllowedException.class, () -> contactUCC.admit(1, "A distance", 1, 1));
  }

  @Test
  @DisplayName("Test admit contact with state admitted")
  public void testAdmitContactStateAdmitted() {
    userDTO.setId(1);
    contactDTO.setStudent(userDTO);
    contactDTO.setState("pris");
    contactDTO.setVersion(1);
    Mockito.when(contactDAOMock.findContactById(1)).thenReturn(contactDTO);
    assertThrows(InvalidRequestException.class,
        () -> contactUCC.admit(1, "Dans l entreprise", 1, 1));
  }

  @Test
  @DisplayName("Test admit contact with state turn down")
  public void testAdmitContactStateTurnDown() {
    userDTO.setId(1);
    contactDTO.setStudent(userDTO);
    contactDTO.setState("refusé");
    contactDTO.setVersion(1);
    Mockito.when(contactDAOMock.findContactById(1)).thenReturn(contactDTO);
    assertThrows(InvalidRequestException.class,
        () -> contactUCC.admit(1, "Dans l entreprise", 1, 1));
  }

  @Test
  @DisplayName("Test admit contact with state unsupervise")
  public void testAdmitContactUnsupervise() {
    userDTO.setId(1);
    contactDTO.setStudent(userDTO);
    contactDTO.setState("non suivi");
    contactDTO.setVersion(1);
    Mockito.when(contactDAOMock.findContactById(1)).thenReturn(contactDTO);
    assertThrows(InvalidRequestException.class,
        () -> contactUCC.admit(1, "Dans l entreprise", 1, 1));
  }

  @Test
  @DisplayName("Test admit contact with state on hold")
  public void testAdmitContactOnHold() {
    userDTO.setId(1);
    contactDTO.setStudent(userDTO);
    contactDTO.setState("suspendu");
    contactDTO.setVersion(1);
    Mockito.when(contactDAOMock.findContactById(1)).thenReturn(contactDTO);
    assertThrows(InvalidRequestException.class,
        () -> contactUCC.admit(1, "Dans l entreprise", 1, 1));
  }

  @Test
  @DisplayName("Test admit contact with state different than started")
  public void testAdmitContactWrongState() {
    userDTO.setId(1);
    contactDTO.setStudent(userDTO);
    contactDTO.setState("accepté");
    contactDTO.setVersion(1);
    Mockito.when(contactDAOMock.findContactById(1)).thenReturn(contactDTO);
    assertThrows(InvalidRequestException.class,
        () -> contactUCC.admit(1, "Dans l entreprise", 1, 1));
  }

  @Test
  @DisplayName("Test admit contact with type of meeting different than on site or remote")
  public void testAdmitContactWrongMeeting() {
    userDTO.setId(1);
    contactDTO.setStudent(userDTO);
    contactDTO.setState("initié");
    contactDTO.setVersion(1);
    Mockito.when(contactDAOMock.findContactById(1)).thenReturn(contactDTO);
    assertThrows(InvalidRequestException.class, () -> contactUCC.admit(1, "test", 1, 1));
  }

  @Test
  @DisplayName("Test admit contact with type of meeting is null")
  public void testAdmitContactMeetingIsNull() {
    userDTO.setId(1);
    contactDTO.setStudent(userDTO);
    contactDTO.setState("initié");
    contactDTO.setVersion(1);
    Mockito.when(contactDAOMock.findContactById(1)).thenReturn(contactDTO);
    assertThrows(InvalidRequestException.class, () -> contactUCC.admit(1, "", 1, 1));
  }

  @Test
  @DisplayName("Test admit contact with wrong version")
  public void testAdmitContactWithWrongVersion() {
    userDTO.setId(1);
    contactDTO.setStudent(userDTO);
    contactDTO.setState("initié");
    contactDTO.setVersion(2);
    Mockito.when(contactDAOMock.findContactById(1)).thenReturn(contactDTO);
    Mockito.when(contactDAOMock.updateContact(contactDTO, 1)).thenReturn(null);
    assertThrows(DuplicateException.class,
        () -> contactUCC.admit(1, "Dans l entreprise", 1, 1));
  }

  @Test
  @DisplayName("Test admit contact is not started")
  public void testAdmitContactNotStarted() {
    userDTO.setId(1);
    contactDTO.setStudent(userDTO);
    contactDTO.setState("accepté");
    contactDTO.setVersion(2);
    Mockito.when(contactDAOMock.findContactById(1)).thenReturn(contactDTO);
    Mockito.when(contactDAOMock.updateContact(contactDTO, 1)).thenReturn(null);
    assertThrows(DuplicateException.class,
        () -> contactUCC.admit(1, "Dans l entreprise", 1, 1));
  }

  @Test
  @DisplayName("Test admit contact correctly started")
  public void testAdmitContactCorrectlyStarted() {
    userDTO.setId(1);
    contactDTO.setStudent(userDTO);
    contactDTO.setState("initié");
    contactDTO.setVersion(1);
    Mockito.when(contactDAOMock.findContactById(1)).thenReturn(contactDTO);
    Mockito.when(contactDAOMock.updateContact(contactDTO, 1)).thenReturn(contactDTO);
    assertNotNull(contactUCC.admit(1, "Dans l entreprise", 1, 1));
  }

  @Test
  @DisplayName("Test admit contact with wrong version")
  public void testAdmitContactConcurrentUpdate() {
    userDTO.setId(1);
    contactDTO.setStudent(userDTO);
    contactDTO.setState("initié");
    contactDTO.setVersion(1);
    Mockito.when(contactDAOMock.findContactById(1)).thenReturn(contactDTO);
    Mockito.when(contactDAOMock.updateContact(contactDTO, 1)).thenReturn(null);
    assertThrows(DuplicateException.class, () -> contactUCC.admit(1, "Dans l entreprise", 1, 1));
  }

  @Test
  @DisplayName("Test turn down contact is null")
  public void testTurnDownContactNotFound() {
    Mockito.when(contactDAOMock.findContactById(1)).thenReturn(null);
    assertThrows(ResourceNotFoundException.class,
        () -> contactUCC.turnDown(1, "Student has not answered fast enough", 1, 1));
  }

  @Test
  @DisplayName("Test turn down contact with wrong student")
  public void testTurnDownContactWrongStudent() {
    userDTO.setId(5);
    contactDTO.setStudent(userDTO);
    contactDTO.setState("pris");
    contactDTO.setVersion(1);
    Mockito.when(contactDAOMock.findContactById(1)).thenReturn(contactDTO);
    assertThrows(NotAllowedException.class,
        () -> contactUCC.turnDown(1, "Student has not answered fast enough", 1, 1));
  }

  @Test
  @DisplayName("Test turn down contact with state different than admitted")
  public void testTurnDownContactWrongState() {
    userDTO.setId(1);
    contactDTO.setStudent(userDTO);
    contactDTO.setState("initié");
    contactDTO.setVersion(1);
    Mockito.when(contactDAOMock.findContactById(1)).thenReturn(contactDTO);
    assertThrows(NotAllowedException.class,
        () -> contactUCC.turnDown(1, "Student has not answered fast enough", 1, 1));
  }

  @Test
  @DisplayName("Test turn down contact with wrong version")
  public void testTurnDownContactWithWrongVersion() {
    userDTO.setId(1);
    contactDTO.setStudent(userDTO);
    contactDTO.setState("pris");
    contactDTO.setVersion(3);
    Mockito.when(contactDAOMock.findContactById(1)).thenReturn(contactDTO);
    assertThrows(DuplicateException.class,
        () -> contactUCC.turnDown(1, "Student has not answered fast enough", 1, 1));
  }

  @Test
  @DisplayName("Test turn down contact correctly admitted")
  public void testTurnDownContactCorrectlyAdmitted() {
    userDTO.setId(1);
    contactDTO.setStudent(userDTO);
    contactDTO.setState("pris");
    contactDTO.setVersion(1);
    Mockito.when(contactDAOMock.findContactById(1)).thenReturn(contactDTO);
    Mockito.when(contactDAOMock.updateContact(contactDTO, 1))
        .thenReturn(contactDTO);
    assertNotNull(contactUCC.turnDown(1, "Student has not answered fast enough", 1, 1));
  }

  @Test
  @DisplayName("Test turn down contact with wrong version")
  public void testTurnDownContactConcurrentUpdate() {
    userDTO.setId(1);
    contactDTO.setStudent(userDTO);
    contactDTO.setState("pris");
    contactDTO.setVersion(1);
    Mockito.when(contactDAOMock.findContactById(1)).thenReturn(contactDTO);
    Mockito.when(contactDAOMock.updateContact(contactDTO, 1)).thenReturn(null);
    assertThrows(DuplicateException.class,
        () -> contactUCC.turnDown(1, "Student has not answered fast enough", 1, 1));
  }

  @Test
  @DisplayName("Test get all contact by student")
  public void testGetAllContactByStudent() {
    Mockito.when(contactDAOMock.getAllContactsByStudent(1)).thenReturn(List.of(contactDTO));
    assertNotNull(contactUCC.getAllContactsByStudent(1));
  }

  @Test
  @DisplayName("Test get one by id with contact not found")
  public void testGetOneByIdWithContactNotFound() {
    Mockito.when(contactDAOMock.findContactById(1)).thenReturn(null);
    assertThrows(ResourceNotFoundException.class, () -> contactUCC.getOneById(1));
  }

  @Test
  @DisplayName("Test get one by id correct")
  public void testGetOneByIdCorrect() {
    Mockito.when(contactDAOMock.findContactById(1)).thenReturn(contactDTO);
    assertNotNull(contactUCC.getOneById(1));
  }

  @Test
  @DisplayName("Test put contact on hold")
  public void testPutContactOnHold() {
    Mockito.when(contactDAOMock.getAllContactsByStudentStartedOrAdmitted(1))
        .thenReturn(List.of(contactDTO));
    contactDTO.setState("suspendu");
    contactDTO.setVersion(1);
    Mockito.when(contactDAOMock.updateContact(contactDTO, 1)).thenReturn(contactDTO);
    contactUCC.putStudentContactsOnHold(1);
    assertEquals("suspendu", contactDTO.getState());
  }

  @Test
  @DisplayName("Test get all contact by company")
  public void testGetAllContactByCompany() {
    Mockito.when(contactDAOMock.getAllContactsByCompany(1)).thenReturn(List.of(contactDTO));
    assertNotNull(contactUCC.getAllContactsByCompany(1));
  }

  @Test
  @DisplayName("Test get all contact by company returning null")
  public void testGetAllContactByCompanyReturningNull() {
    Mockito.when(contactDAOMock.getAllContactsByCompany(1)).thenReturn(null);
    assertThrows(ResourceNotFoundException.class,
        () -> contactUCC.getAllContactsByCompany(1));
  }

  @Test
  @DisplayName("Test get all contact by student returning null")
  public void testGetAllContactByStudentReturningNull() {
    Mockito.when(contactDAOMock.getAllContactsByStudent(1)).thenReturn(null);
    assertThrows(ResourceNotFoundException.class,
        () -> contactUCC.getAllContactsByStudent(1));
  }

  @Test
  @DisplayName("Test accept contact null")
  public void testAcceptContactNull() {
    Mockito.when(contactDAOMock.findContactById(1)).thenReturn(null);
    assertThrows(ResourceNotFoundException.class, () -> contactUCC.accept(1, 1, internshipDTO, 1));
  }

  @Test
  @DisplayName("Test accept contact is not admitted")
  public void testAcceptContactNotAdmitted() {
    contactDTO.setState("refusé");
    contactDTO.setVersion(1);

    Mockito.when(contactDAOMock.findContactById(1)).thenReturn(contactDTO);

    assertThrows(NotAllowedException.class, () -> contactUCC.accept(1, 1, internshipDTO, 1));
  }

  @Test
  @DisplayName("Test accept wrong student")
  public void testAcceptWrongStudent() {
    userDTO.setId(1);
    contactDTO.setState("pris");
    contactDTO.setStudent(userDTO);
    contactDTO.setVersion(1);

    Mockito.when(contactDAOMock.findContactById(1)).thenReturn(contactDTO);

    assertThrows(NotAllowedException.class, () -> contactUCC.accept(1, 2, internshipDTO, 1));
  }

  @Test
  @DisplayName("Test accept wrong version")
  public void testAcceptWrongVersion() {
    userDTO.setId(1);
    contactDTO.setState("pris");
    contactDTO.setStudent(userDTO);
    contactDTO.setVersion(2);

    Mockito.when(contactDAOMock.findContactById(1)).thenReturn(contactDTO);

    assertThrows(DuplicateException.class, () -> contactUCC.accept(1, 1, internshipDTO, 1));
  }

  @Test
  @DisplayName("Test accept contact with wrong version")
  public void testAcceptContactConcurrentUpdate() {
    userDTO.setId(1);
    contactDTO.setStudent(userDTO);
    contactDTO.setState("pris");
    contactDTO.setVersion(1);

    Mockito.when(contactDAOMock.findContactById(1)).thenReturn(contactDTO);
    Mockito.when(contactDAOMock.updateContact(contactDTO, 1)).thenReturn(null);

    assertThrows(DuplicateException.class,
        () -> contactUCC.accept(1, 1, internshipDTO, 1));
  }

  @Test
  @DisplayName("Test accept contact correctly admitted")
  public void testAcceptContactCorrectlyAdmitted() {
    userDTO.setId(1);
    contactDTO.setStudent(userDTO);
    contactDTO.setState("pris");
    contactDTO.setVersion(1);

    ContactDTO contactIntern = contactFactory.getContactDTO();
    contactIntern.setStudent(userDTO);
    contactIntern.setState("pris");
    internshipDTO.setContact(contactIntern);

    InternshipDTO internshipCreated = internshipFactory.getInternshipDTO();
    internshipCreated.setId(1);

    Mockito.when(contactDAOMock.findContactById(1)).thenReturn(contactDTO);
    Mockito.when(contactDAOMock.updateContact(contactDTO, 1))
        .thenReturn(contactDTO);
    Mockito.when(internshipDAOMock.getOneInternshipByIdUserSchoolYear(1, "2023-2024"))
        .thenReturn(null);
    Mockito.when(internshipDAOMock.createInternship(internshipDTO)).thenReturn(internshipCreated);
    Mockito.when(internshipUCCMock.createInternship(internshipDTO, 1))
        .thenReturn(internshipCreated);

    InternshipDTO result = contactUCC.accept(1, 1, internshipDTO, 1);

    assertNotNull(result);
  }

}
