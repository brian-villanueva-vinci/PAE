package be.vinci.pae;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import be.vinci.pae.domain.ContactFactory;
import be.vinci.pae.domain.InternshipFactory;
import be.vinci.pae.domain.UserFactory;
import be.vinci.pae.domain.dto.ContactDTO;
import be.vinci.pae.domain.dto.InternshipDTO;
import be.vinci.pae.domain.dto.UserDTO;
import be.vinci.pae.domain.ucc.InternshipUCC;
import be.vinci.pae.services.dal.DalServices;
import be.vinci.pae.services.dao.ContactDAO;
import be.vinci.pae.services.dao.InternshipDAO;
import be.vinci.pae.utils.exceptions.DuplicateException;
import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.exceptions.InvalidRequestException;
import be.vinci.pae.utils.exceptions.NotAllowedException;
import be.vinci.pae.utils.exceptions.ResourceNotFoundException;
import be.vinci.pae.utils.exceptions.UnauthorizedAccessException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * InternshipUCC test class.
 */
public class InternshipUCCImplTest {

  private static ServiceLocator serviceLocator;
  private static InternshipDAO internshipDAOMock;
  private static ContactDAO contactDAOMock;
  private static DalServices dalServicesMock;
  private InternshipUCC internshipUCC;
  private InternshipDTO internshipDTO;
  private ContactDTO contactDTO;
  private UserDTO userDTO;

  @BeforeAll
  static void init() {
    serviceLocator = ServiceLocatorUtilities.bind(new BinderTest());
    internshipDAOMock = serviceLocator.getService(InternshipDAO.class);
    contactDAOMock = serviceLocator.getService(ContactDAO.class);
    dalServicesMock = serviceLocator.getService(DalServices.class);
  }

  @BeforeEach
  void setup() {
    internshipUCC = serviceLocator.getService(InternshipUCC.class);
    InternshipFactory internshipFactory = serviceLocator.getService(InternshipFactory.class);
    ContactFactory contactFactory = serviceLocator.getService(ContactFactory.class);
    UserFactory userFactory = serviceLocator.getService(UserFactory.class);
    internshipDTO = internshipFactory.getInternshipDTO();
    contactDTO = contactFactory.getContactDTO();
    userDTO = userFactory.getUserDTO();
    Mockito.doNothing().when(dalServicesMock).startTransaction();
    Mockito.doNothing().when(dalServicesMock).commitTransaction();
    Mockito.doNothing().when(dalServicesMock).rollbackTransaction();
  }

  @AfterEach
  void reset() {
    Mockito.reset(internshipDAOMock);
    Mockito.reset(contactDAOMock);
  }

  @Test
  @DisplayName("Test getOneByStudent student has no internship")
  public void testGetOneByStudentStudentNoInternship() {
    Mockito.when(internshipDAOMock.getOneInternshipByIdUserSchoolYear(1, "2023-2024"))
        .thenReturn(null);
    assertThrows(ResourceNotFoundException.class,
        () -> internshipUCC.getOneByStudentCurrentSchoolYear(1));
  }

  @Test
  @DisplayName("Test getOneByStudent correct")
  public void testGetOneByStudentCorrect() {
    Mockito.when(internshipDAOMock.getOneInternshipByIdUserSchoolYear(1, "2023-2024"))
        .thenReturn(internshipDTO);
    assertNotNull(internshipUCC.getOneByStudentCurrentSchoolYear(1));
  }

  @Test
  @DisplayName("Test getOneById wrong id")
  public void testGetOneByIdWrongId() {
    Mockito.when(internshipDAOMock.getOneInternshipById(1)).thenReturn(null);
    assertThrows(ResourceNotFoundException.class,
        () -> internshipUCC.getOneById(1, 1));
  }

  @Test
  @DisplayName("Test getOneById wrong user")
  public void testGetOneByIdWrongUser() {
    userDTO.setId(1);
    contactDTO.setId(1);
    contactDTO.setStudent(userDTO);
    internshipDTO.setContact(contactDTO);
    Mockito.when(contactDAOMock.findContactById(1)).thenReturn(contactDTO);
    Mockito.when(internshipDAOMock.getOneInternshipById(1)).thenReturn(internshipDTO);
    assertThrows(NotAllowedException.class, () -> internshipUCC.getOneById(1, 3));
  }

  @Test
  @DisplayName("Test getOneById correct")
  public void testGetOneByIdCorrect() {
    userDTO.setId(1);
    contactDTO.setId(1);
    contactDTO.setStudent(userDTO);
    internshipDTO.setContact(contactDTO);
    Mockito.when(contactDAOMock.findContactById(1)).thenReturn(contactDTO);
    Mockito.when(internshipDAOMock.getOneInternshipById(1)).thenReturn(internshipDTO);
    assertNotNull(internshipUCC.getOneById(1, 1));
  }

  @Test
  @DisplayName("Test create one internship wrong user")
  public void testCreateOneInternshipWrongUser() {
    userDTO.setId(2);
    contactDTO.setStudent(userDTO);
    internshipDTO.setContact(contactDTO);
    assertThrows(UnauthorizedAccessException.class,
        () -> internshipUCC.createInternship(internshipDTO, 1));
  }

  @Test
  @DisplayName("Test create one internship contact not accepted")
  public void testCreateOneInternshipContactNotAccepted() {
    userDTO.setId(1);
    contactDTO.setStudent(userDTO);
    contactDTO.setState("initié");
    internshipDTO.setContact(contactDTO);
    assertThrows(InvalidRequestException.class,
        () -> internshipUCC.createInternship(internshipDTO, 1));
  }

  @Test
  @DisplayName("Test get internship stat for every year should return not null")
  public void testgetInternshipCountByYear() {
    Map<String, Integer[]> internshipStats = new HashMap<>();
    //internshipStats.put("2023-2024", new Integer[]{3, 5});
    Mockito.when(internshipDAOMock.getInternshipCountByYear()).thenReturn(internshipStats);
    Map<String, Integer[]> testInternshipMap = internshipUCC.getInternshipCountByYear();
    assertNotNull(testInternshipMap);
  }

  @Test
  @DisplayName("Test editProject with wrong version")
  public void testEditProjectWithWrongVersion() {
    internshipDTO.setVersion(3);
    Mockito.when(internshipDAOMock.editProject("test", 1, 1)).thenReturn(internshipDTO);
    assertThrows(InvalidRequestException.class, () -> internshipUCC.editProject("test", 1, 1));
  }

  @Test
  @DisplayName("Test editProject correctly")
  public void testEditProjectCorrectly() {
    internshipDTO.setVersion(2);
    Mockito.when(internshipDAOMock.editProject("test", 1, 1)).thenReturn(internshipDTO);
    assertNotNull(internshipUCC.editProject("test", 1, 1));
  }

  @Test
  @DisplayName("Test get all internships")
  public void testGetAllInternships() {
    Mockito.when(internshipDAOMock.getAllInternships()).thenReturn(List.of(internshipDTO));
    assertEquals(List.of(internshipDTO), internshipUCC.getAllInternships());
  }

  @Test
  @DisplayName("Test create duplicated internship")
  public void testCreateDuplicatedInternship() {
    userDTO.setId(1);
    contactDTO.setStudent(userDTO);
    contactDTO.setSchoolYear("ee");
    contactDTO.setState("pris");
    internshipDTO.setContact(contactDTO);
    Mockito.when(internshipDAOMock.getOneInternshipByIdUserSchoolYear(1, "2023-2024"))
        .thenReturn(internshipDTO);
    assertThrows(DuplicateException.class, () -> internshipUCC.createInternship(internshipDTO, 1));
  }

  @Test
  @DisplayName("Test create internship correctly")
  public void testCreateInternshipCorrectly() {
    userDTO.setId(1);
    contactDTO.setStudent(userDTO);
    contactDTO.setSchoolYear("ee");
    contactDTO.setState("pris");
    internshipDTO.setContact(contactDTO);
    Mockito.when(internshipDAOMock.getOneInternshipByIdUserSchoolYear(1, "2023-2024"))
        .thenReturn(null);
    Mockito.when(internshipDAOMock.createInternship(internshipDTO)).thenReturn(internshipDTO);
    assertEquals(internshipDTO, internshipUCC.createInternship(internshipDTO, 1));
  }

  @Test
  @DisplayName("Test crash transaction")
  public void testCrashTransaction() {
    userDTO.setId(1);
    contactDTO.setStudent(userDTO);
    contactDTO.setState("accepté");
    internshipDTO.setContact(contactDTO);
    Mockito.doThrow(new FatalException(new RuntimeException()))
        .when(dalServicesMock).startTransaction();
    assertAll(
        () -> assertThrows(FatalException.class, () -> {
          internshipUCC.getOneByStudentCurrentSchoolYear(1);
        }),
        () -> assertThrows(FatalException.class, () -> {
          internshipUCC.getOneById(1, 1);
        }),
        () -> assertThrows(FatalException.class, () -> {
          internshipUCC.editProject("test", 1, 1);
        }),
        () -> assertThrows(FatalException.class, () -> {
          internshipUCC.getInternshipCountByYear();
        }),
        () -> assertThrows(FatalException.class, () -> {
          internshipUCC.getAllInternships();
        })
    );
  }
}

