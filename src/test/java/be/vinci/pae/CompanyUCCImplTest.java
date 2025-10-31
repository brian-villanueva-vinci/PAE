package be.vinci.pae;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import be.vinci.pae.domain.CompanyFactory;
import be.vinci.pae.domain.ContactFactory;
import be.vinci.pae.domain.UserFactory;
import be.vinci.pae.domain.dto.CompanyDTO;
import be.vinci.pae.domain.dto.ContactDTO;
import be.vinci.pae.domain.dto.UserDTO;
import be.vinci.pae.domain.ucc.CompanyUCC;
import be.vinci.pae.services.dal.DalServices;
import be.vinci.pae.services.dao.CompanyDAO;
import be.vinci.pae.services.dao.ContactDAO;
import be.vinci.pae.services.dao.UserDAO;
import be.vinci.pae.utils.exceptions.DuplicateException;
import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.exceptions.InvalidRequestException;
import be.vinci.pae.utils.exceptions.ResourceNotFoundException;
import java.util.ArrayList;
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
 * CompanyUCC test class.
 */
public class CompanyUCCImplTest {

  private static ServiceLocator serviceLocator;
  private static CompanyDAO companyDAOMock;
  private static ContactDAO contactDAOMock;
  private static UserDAO userDAOMock;
  private static DalServices dalServicesMock;
  private CompanyUCC companyUCC;
  private CompanyDTO companyDTO;
  private ContactDTO contactDTO;
  private UserDTO userDTO;

  @BeforeAll
  static void init() {
    serviceLocator = ServiceLocatorUtilities.bind(new BinderTest());
    userDAOMock = serviceLocator.getService(UserDAO.class);
    contactDAOMock = serviceLocator.getService(ContactDAO.class);
    companyDAOMock = serviceLocator.getService(CompanyDAO.class);
    dalServicesMock = serviceLocator.getService(DalServices.class);
  }

  @BeforeEach
  void setup() {
    companyUCC = serviceLocator.getService(CompanyUCC.class);
    CompanyFactory companyFactory = serviceLocator.getService(CompanyFactory.class);
    UserFactory userFactory = serviceLocator.getService(UserFactory.class);
    ContactFactory contactFactory = serviceLocator.getService(ContactFactory.class);
    companyDTO = companyFactory.getCompanyDTO();
    userDTO = userFactory.getUserDTO();
    contactDTO = contactFactory.getContactDTO();
    Mockito.doNothing().when(dalServicesMock).startTransaction();
    Mockito.doNothing().when(dalServicesMock).commitTransaction();
    Mockito.doNothing().when(dalServicesMock).rollbackTransaction();
  }

  @AfterEach
  void reset() {
    Mockito.reset(companyDAOMock);
  }

  @Test
  @DisplayName("Test find one by id")
  public void testFindOneById() {
    Mockito.when(companyDAOMock.getOneCompanyById(1)).thenReturn(companyDTO);
    assertNotNull(companyUCC.findOneById(1));
  }

  @Test
  @DisplayName("Test find one by id crash transaction")
  public void testFindOneByIdCrashTransaction() {
    Mockito.doThrow(new FatalException(new RuntimeException())).when(dalServicesMock)
        .startTransaction();
    assertThrows(FatalException.class, () -> companyUCC.findOneById(1));
  }

  @Test
  @DisplayName("Test get all companies should return not null")
  public void testGetAllCompanies() {
    Map<String, Integer> dataMap = new HashMap<>();
    dataMap.put("2023-2024", 2);
    Map<CompanyDTO, Map<String, Integer>> companyMap = new HashMap<>();
    companyMap.put(companyDTO, dataMap);
    Map<Integer, Map<CompanyDTO, Map<String, Integer>>>
        companiesMap = new HashMap<>();
    companiesMap.put(1, companyMap);

    Mockito.when(companyDAOMock.getAllCompanies()).thenReturn(companiesMap);

    Map<Integer, Map<CompanyDTO, Map<String, Integer>>>
        companyDTOList = companyUCC.getAllCompanies();
    assertNotNull(companyDTOList);
  }

  @Test
  @DisplayName("Test get all companies for existing user should return not null")
  public void testGetAllCompaniesByExistingUser() {
    userDTO.setId(1);
    Mockito.when(userDAOMock.getOneUserById(1)).thenReturn(userDTO);
    Mockito.when(companyDAOMock.getAllCompaniesByUserIdSchoolYear(1, "2023-2024"))
        .thenReturn(List.of(companyDTO));
    List<CompanyDTO> companyDTOList = companyUCC.getAllCompaniesByUser(1);
    assertNotNull(companyDTOList);
  }

  @Test
  @DisplayName("Test get all companies for non existing "
      + "user should throw ResourceNotFoundException")
  public void testGetAllCompaniesByNonExistingUser() {
    userDTO.setId(1);
    Mockito.when(userDAOMock.getOneUserById(1)).thenReturn(null);
    Mockito.when(companyDAOMock.getAllCompaniesByUserIdSchoolYear(1, "2023-2024"))
        .thenReturn(List.of(companyDTO));
    assertThrows(ResourceNotFoundException.class, () -> companyUCC.getAllCompaniesByUser(1));
  }

  @Test
  @DisplayName("Test register non existing company with blank designation "
      + "and null email should return a company")
  public void testRegisterCompanyNonDuplicateDesignationNullEmailNull() {
    companyDTO.setDesignation("");
    companyDTO.setName("test");
    companyDTO.setPhoneNumber("0400000000");
    companyDTO.setEmail("");
    List<CompanyDTO> emptyList = new ArrayList<>();

    Mockito.when(companyDAOMock.getAllCompaniesByName(companyDTO.getName()))
        .thenReturn(emptyList);
    Mockito.when(companyDAOMock.addOneCompany(companyDTO))
        .thenReturn(companyDTO);

    CompanyDTO returnedCompany;
    returnedCompany = companyUCC.registerCompany(companyDTO);
    assertNotNull(returnedCompany);
  }

  @Test
  @DisplayName("Test register non existing company with blank designation "
      + "and null phoneNumber should return a company")
  public void testRegisterCompanyNonDuplicateDesignationNullPhoneNull() {
    companyDTO.setDesignation("");
    companyDTO.setName("test");
    companyDTO.setPhoneNumber("");
    companyDTO.setEmail("test@test.be");
    List<CompanyDTO> emptyList = new ArrayList<>();

    Mockito.when(companyDAOMock.getAllCompaniesByName(companyDTO.getName()))
        .thenReturn(emptyList);
    Mockito.when(companyDAOMock.addOneCompany(companyDTO))
        .thenReturn(companyDTO);

    CompanyDTO returnedCompany;
    returnedCompany = companyUCC.registerCompany(companyDTO);
    assertNotNull(returnedCompany);
  }

  @Test
  @DisplayName("Test register existing company with new designation "
      + "and null phoneNumber should return a company")
  public void testRegisterCompanyNonDuplicateDesignationNotNullPhoneNull() {
    companyDTO.setDesignation("not null");
    companyDTO.setName("test");
    companyDTO.setPhoneNumber("");
    companyDTO.setEmail("test@test.be");

    Mockito.when(companyDAOMock.getOneCompanyByNameDesignation(companyDTO.getName(),
            companyDTO.getDesignation()))
        .thenReturn(null);
    Mockito.when(companyDAOMock.addOneCompany(companyDTO))
        .thenReturn(companyDTO);

    CompanyDTO returnedCompany;
    returnedCompany = companyUCC.registerCompany(companyDTO);
    assertNotNull(returnedCompany);
  }

  @Test
  @DisplayName("Test register existing company with blank designation "
      + "should throw InvalidRequestException")
  public void testRegisterCompanyDuplicateDesignationNull() {
    companyDTO.setDesignation("");
    companyDTO.setName("test");
    companyDTO.setPhoneNumber("0400000000");
    companyDTO.setEmail("");
    List<CompanyDTO> notEmptyList = new ArrayList<>();
    notEmptyList.add(companyDTO);

    Mockito.when(companyDAOMock.getAllCompaniesByName(companyDTO.getName()))
        .thenReturn(notEmptyList);

    assertThrows(DuplicateException.class, () -> companyUCC.registerCompany(companyDTO));
  }

  @Test
  @DisplayName("Test register existing company with same designation"
      + " and null phoneNumber should throw DuplicateException")
  public void testRegisterCompanyDuplicateDesignationNotNullPhoneNull() {
    companyDTO.setDesignation("not null");
    companyDTO.setName("test");
    companyDTO.setPhoneNumber("");
    companyDTO.setEmail("test@test.be");

    Mockito.when(companyDAOMock.getOneCompanyByNameDesignation(companyDTO.getName(),
            companyDTO.getDesignation()))
        .thenReturn(companyDTO);

    assertThrows(DuplicateException.class, () -> companyUCC.registerCompany(companyDTO));
  }

  @Test
  @DisplayName("Test get all companies crash transaction")
  public void testGetAllCompaniesCrashTransaction() {
    Mockito.doThrow(new FatalException(new RuntimeException()))
        .when(dalServicesMock).startTransaction();
    assertAll(
        () -> assertThrows(FatalException.class, () -> companyUCC.getAllCompanies()),
        () -> assertThrows(FatalException.class, () -> companyUCC.getAllCompaniesByUser(1)),
        () -> assertThrows(FatalException.class, () ->
            companyUCC.blacklist(1, "l'entreprise pratique la fraude", 1)),
        () -> assertThrows(FatalException.class, () -> companyUCC.getAllCompaniesByUser(1)),
        () -> assertThrows(FatalException.class, () -> companyUCC.registerCompany(companyDTO))
    );
  }

  @Test
  @DisplayName("Test blacklist if a company is already blacklisted")
  public void testBlacklistCompanyAlreadyBlacklisted() {
    companyDTO.setIsBlacklisted(true);
    Mockito.when(companyDAOMock.getOneCompanyById(1)).thenReturn(companyDTO);
    assertThrows(DuplicateException.class,
        () -> companyUCC.blacklist(1, "l'entreprise pratique la fraude", 1));
  }

  @Test
  @DisplayName("Test blacklist with wrong version")
  public void testBlacklistWithWrongVersion() {
    companyDTO.setIsBlacklisted(false);
    companyDTO.setId(1);
    companyDTO.setVersion(3);
    Mockito.when(companyDAOMock.getOneCompanyById(1)).thenReturn(companyDTO);
    Mockito.when(companyDAOMock.blacklist(1, "l'entreprise pratique la fraude", 1))
        .thenReturn(companyDTO);
    assertThrows(InvalidRequestException.class,
        () -> companyUCC.blacklist(1, "l'entreprise pratique la fraude", 1));
  }

  @Test
  @DisplayName("Test blacklist correctly")
  public void testBlacklistCorrectly() {
    companyDTO.setIsBlacklisted(false);
    companyDTO.setId(1);
    companyDTO.setVersion(2);
    Mockito.when(contactDAOMock.getAllContactsByCompanyStartedOrAdmitted(1))
        .thenReturn(List.of(contactDTO));
    Mockito.when(contactDAOMock.putContactOnHold(contactDTO)).thenReturn(contactDTO);
    Mockito.when(companyDAOMock.getOneCompanyById(1)).thenReturn(companyDTO);
    Mockito.when(companyDAOMock.blacklist(1, "l'entreprise pratique la fraude", 1))
        .thenReturn(companyDTO);
    assertNotNull(companyUCC.blacklist(1, "l'entreprise pratique la fraude", 1));
  }
}
