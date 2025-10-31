package be.vinci.pae;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import be.vinci.pae.domain.User;
import be.vinci.pae.domain.UserFactory;
import be.vinci.pae.domain.dto.UserDTO;
import be.vinci.pae.domain.ucc.UserUCC;
import be.vinci.pae.services.dal.DalServices;
import be.vinci.pae.services.dao.UserDAO;
import be.vinci.pae.utils.exceptions.DuplicateException;
import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.exceptions.InvalidRequestException;
import be.vinci.pae.utils.exceptions.ResourceNotFoundException;
import be.vinci.pae.utils.exceptions.UnauthorizedAccessException;
import java.sql.Date;
import java.time.LocalDate;
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
 * UserUCC test class.
 */
public class UserUCCImplTest {

  private static final String email = "eleonore.martin@vinci.be";
  private static final String lastname = "Martin";
  private static final String firstname = "Eléonore";
  private static final String phoneNumber = "+32485123456";
  private static final String password = "123";
  private static final String hashPassword
      = "$2a$10$HG7./iXYemq7gF/v9Hc98eXJFGo3KajGwPLoaiU0r9TlaxlIFxsAu";
  private static final String role = "teacher";
  private static final Date registrationDate = Date.valueOf(LocalDate.now());
  private static final String schoolYear = "2023-2024";
  private static ServiceLocator serviceLocator;
  private static UserDAO userDAOMock;
  private static DalServices dalServicesMock;
  private UserUCC userUCC;
  private UserFactory userFactory;
  private UserDTO userDTO;

  @BeforeAll
  static void init() {
    serviceLocator = ServiceLocatorUtilities.bind(new BinderTest());
    userDAOMock = serviceLocator.getService(UserDAO.class);
    dalServicesMock = serviceLocator.getService(DalServices.class);
  }

  @BeforeEach
  void setup() {
    userUCC = serviceLocator.getService(UserUCC.class);
    userFactory = serviceLocator.getService(UserFactory.class);
    userDTO = userFactory.getUserDTO();
    Mockito.doNothing().when(dalServicesMock).startTransaction();
    Mockito.doNothing().when(dalServicesMock).commitTransaction();
    Mockito.doNothing().when(dalServicesMock).rollbackTransaction();
  }

  @AfterEach
  void reset() {
    Mockito.reset(userDAOMock);
  }

  @Test
  @DisplayName("Test login with unknown email")
  public void testLoginUnknownEmail() {
    Mockito.when(userDAOMock.getOneUserByEmail(email)).thenReturn(null);
    assertThrows(ResourceNotFoundException.class, () -> userUCC.login(email, password));
  }

  @Test
  @DisplayName("Test login with good email wrong password")
  public void testLoginGoodEmailWrongPassword() {
    userDTO.setEmail(email);
    userDTO.setPassword(hashPassword);
    Mockito.when(userDAOMock.getOneUserByEmail(email)).thenReturn(userDTO);
    assertThrows(UnauthorizedAccessException.class, () -> userUCC.login(email, "boom"));
  }

  @Test
  @DisplayName("Test login with good email and good password")
  public void testLoginGoodEmailGoodPassword() {
    userDTO.setId(42);
    userDTO.setEmail(email);
    userDTO.setPassword(hashPassword);
    Mockito.when(userDAOMock.getOneUserByEmail(email)).thenReturn(userDTO);
    assertNotNull(userUCC.login(email, password));
  }

  @Test
  @DisplayName("Test get all users as student")
  public void testGetAllUsersAsStudent() {
    userDTO.setRole("Etudiant");
    assertThrows(UnauthorizedAccessException.class, () -> userUCC.getAllUsers(userDTO));
  }

  @Test
  @DisplayName("Test get all users")
  public void testGetAllUsers() {
    userDTO.setId(56);
    userDTO.setRole("Professeur");
    Mockito.when(userDAOMock.getAllUsers()).thenReturn(List.of(userDTO));
    assertEquals(56, userUCC.getAllUsers(userDTO).get(0).getId());
  }

  @Test
  @DisplayName("Test get one by wrong id")
  public void testGetOneByUnknownId() {
    Mockito.when(userDAOMock.getOneUserById(49)).thenReturn(null);
    assertThrows(ResourceNotFoundException.class, () -> userUCC.getOneById(49));
  }

  @Test
  @DisplayName("Test get one by id")
  public void testGetOneByKnownId() {
    userDTO.setEmail(email);
    Mockito.when(userDAOMock.getOneUserById(1)).thenReturn(userDTO);
    assertNotNull(userUCC.getOneById(1));
  }

  @Test
  @DisplayName("Test unsupervise contact crash transaction")
  public void testCrashTransaction() {
    Mockito.doThrow(new FatalException(new RuntimeException()))
        .when(dalServicesMock).startTransaction();
    userDTO.setRole("Professeur");
    assertAll(
        () -> assertThrows(FatalException.class, () -> {
          userUCC.login(email, password);
        }),
        () -> assertThrows(FatalException.class, () -> {
          userUCC.getAllUsers(userDTO);
        }),
        () -> assertThrows(FatalException.class, () -> {
          userUCC.getOneById(1);
        })
    );
  }

  @Test
  @DisplayName("Register new user should work")
  public void testRegisterNewUser() {

    UserDTO newUserDTO = userFactory.getUserDTO();

    newUserDTO.setEmail(email);
    newUserDTO.setLastname(lastname);
    newUserDTO.setFirstname(firstname);
    newUserDTO.setPhoneNumber(phoneNumber);
    newUserDTO.setPassword(password);
    newUserDTO.setRole(role);
    newUserDTO.setRegistrationDate(registrationDate);
    newUserDTO.setSchoolYear(schoolYear);
    newUserDTO.setPassword(hashPassword);

    Mockito.when(userDAOMock.getOneUserByEmail(newUserDTO.getEmail()))
        .thenReturn(null)
        .thenReturn(newUserDTO);
    Mockito.when(userDAOMock.addOneUser(newUserDTO)).thenReturn(newUserDTO);

    UserDTO returnedUser;
    returnedUser = userUCC.register(newUserDTO);

    assertNotNull(returnedUser);

  }

  @Test
  @DisplayName("Register existing user should not work")
  public void testRegisterExistingUser() {

    userDTO.setEmail(email);
    userDTO.setLastname(lastname);
    userDTO.setFirstname(firstname);
    userDTO.setPhoneNumber(phoneNumber);
    userDTO.setRole(role);
    userDTO.setRegistrationDate(registrationDate);
    userDTO.setSchoolYear(schoolYear);
    userDTO.setPassword(hashPassword);

    UserDTO newUserDTO = userFactory.getUserDTO();

    newUserDTO.setEmail(email);
    newUserDTO.setLastname(lastname);
    newUserDTO.setFirstname(firstname);
    newUserDTO.setPhoneNumber(phoneNumber);
    newUserDTO.setRole(role);
    newUserDTO.setRegistrationDate(registrationDate);
    newUserDTO.setSchoolYear(schoolYear);
    newUserDTO.setPassword(hashPassword);

    Mockito.when(userDAOMock.getOneUserByEmail(newUserDTO.getEmail()))
        .thenReturn(userDTO);

    assertThrows(DuplicateException.class, () -> userUCC.register(newUserDTO));

  }

  @Test
  @DisplayName("Edit user should work")
  public void testEditUser() {

    userDTO.setEmail(email);
    userDTO.setLastname(lastname);
    userDTO.setFirstname(firstname);
    userDTO.setPhoneNumber(phoneNumber);
    userDTO.setRole(role);
    userDTO.setRegistrationDate(registrationDate);
    userDTO.setSchoolYear(schoolYear);
    userDTO.setPassword(hashPassword);
    userDTO.setVersion(1);

    UserDTO newUserDTO = userFactory.getUserDTO();

    newUserDTO.setEmail(email);
    newUserDTO.setLastname(lastname);
    newUserDTO.setFirstname(firstname);
    newUserDTO.setPhoneNumber("0400300200");
    newUserDTO.setRole(role);
    newUserDTO.setRegistrationDate(registrationDate);
    newUserDTO.setSchoolYear(schoolYear);
    newUserDTO.setPassword(hashPassword);
    newUserDTO.setVersion(1);

    Mockito.when(userDAOMock.editOneUser(newUserDTO, 1))
        .thenReturn(newUserDTO);

    UserDTO returnedUser = userUCC.editOneUser(userDTO, newUserDTO);

    assertNotNull(returnedUser);

  }

  @Test
  @DisplayName("Edit user wrong version should not work")
  public void testEditUserWrongVersion() {

    userDTO.setEmail(email);
    userDTO.setLastname(lastname);
    userDTO.setFirstname(firstname);
    userDTO.setPhoneNumber(phoneNumber);
    userDTO.setRole(role);
    userDTO.setRegistrationDate(registrationDate);
    userDTO.setSchoolYear(schoolYear);
    userDTO.setPassword(hashPassword);
    userDTO.setVersion(1);

    UserDTO newUserDTO = userFactory.getUserDTO();

    newUserDTO.setEmail(email);
    newUserDTO.setLastname(lastname);
    newUserDTO.setFirstname(firstname);
    newUserDTO.setPhoneNumber("0400300200");
    newUserDTO.setRole(role);
    newUserDTO.setRegistrationDate(registrationDate);
    newUserDTO.setSchoolYear(schoolYear);
    newUserDTO.setPassword(hashPassword);
    newUserDTO.setVersion(2);

    Mockito.when(userDAOMock.editOneUser(newUserDTO, 1))
        .thenReturn(newUserDTO);

    assertThrows(DuplicateException.class, () -> userUCC.editOneUser(userDTO, newUserDTO));

  }

  @Test
  @DisplayName("Edit user with someone updating at same time should not work")
  public void testEditUserConcurrentUpdate() {

    userDTO.setEmail(email);
    userDTO.setLastname(lastname);
    userDTO.setFirstname(firstname);
    userDTO.setPhoneNumber(phoneNumber);
    userDTO.setRole(role);
    userDTO.setRegistrationDate(registrationDate);
    userDTO.setSchoolYear(schoolYear);
    userDTO.setPassword(hashPassword);
    userDTO.setVersion(1);

    UserDTO newUserDTO = userFactory.getUserDTO();

    newUserDTO.setEmail(email);
    newUserDTO.setLastname(lastname);
    newUserDTO.setFirstname(firstname);
    newUserDTO.setPhoneNumber("0400300200");
    newUserDTO.setRole(role);
    newUserDTO.setRegistrationDate(registrationDate);
    newUserDTO.setSchoolYear(schoolYear);
    newUserDTO.setPassword(hashPassword);
    newUserDTO.setVersion(1);

    Mockito.when(userDAOMock.editOneUser(newUserDTO, 1))
        .thenReturn(null);

    assertThrows(DuplicateException.class, () -> userUCC.editOneUser(userDTO, newUserDTO));

  }

  @Test
  @DisplayName("Edit user password should work")
  public void testEditPassword() {

    UserDTO currentUser = userFactory.getUserDTO();
    currentUser.setId(1);
    currentUser.setEmail(email);
    currentUser.setLastname(lastname);
    currentUser.setFirstname(firstname);
    currentUser.setPhoneNumber(phoneNumber);
    currentUser.setRole(role);
    currentUser.setRegistrationDate(registrationDate);
    currentUser.setSchoolYear(schoolYear);
    currentUser.setPassword(hashPassword);
    currentUser.setVersion(1);

    UserDTO updatedUserDTO = userFactory.getUserDTO();
    updatedUserDTO.setId(1);
    updatedUserDTO.setEmail(email);
    updatedUserDTO.setLastname(lastname);
    updatedUserDTO.setFirstname(firstname);
    updatedUserDTO.setPhoneNumber(phoneNumber);
    updatedUserDTO.setRole(role);
    updatedUserDTO.setRegistrationDate(registrationDate);
    updatedUserDTO.setSchoolYear(schoolYear);
    String newPwd = "test";
    updatedUserDTO.setPassword(newPwd);
    updatedUserDTO.setVersion(2);

    User userMock = Mockito.mock(User.class);
    Mockito.doNothing().when(userMock).hashPassword();
    Mockito.when(userDAOMock.editOneUser(currentUser, 1))
        .thenReturn(updatedUserDTO);

    String oldPwd = "123";
    String newPwdRepeat = "test";
    UserDTO returnedUser = userUCC.editPassword(currentUser, oldPwd, newPwd, newPwdRepeat);

    assertNotNull(returnedUser);

  }

  @Test
  @DisplayName("Edit user password with wrong password should not work")
  public void testEditPasswordWrongPwd() {

    UserDTO currentUser = userFactory.getUserDTO();
    currentUser.setId(1);
    currentUser.setEmail(email);
    currentUser.setLastname(lastname);
    currentUser.setFirstname(firstname);
    currentUser.setPhoneNumber(phoneNumber);
    currentUser.setRole(role);
    currentUser.setRegistrationDate(registrationDate);
    currentUser.setSchoolYear(schoolYear);
    currentUser.setPassword(hashPassword);
    currentUser.setVersion(1);

    String oldPwd = "12";
    String newPwd = "test";
    String newPwdRepeat = "test";

    assertThrows(UnauthorizedAccessException.class,
        () -> userUCC.editPassword(currentUser, oldPwd, newPwd, newPwdRepeat));

  }

  @Test
  @DisplayName("Edit user password with different new password should not work")
  public void testEditPasswordDifferentPwd() {

    UserDTO currentUser = userFactory.getUserDTO();
    currentUser.setId(1);
    currentUser.setEmail(email);
    currentUser.setLastname(lastname);
    currentUser.setFirstname(firstname);
    currentUser.setPhoneNumber(phoneNumber);
    currentUser.setRole(role);
    currentUser.setRegistrationDate(registrationDate);
    currentUser.setSchoolYear(schoolYear);
    currentUser.setPassword(hashPassword);
    currentUser.setVersion(1);

    String oldPwd = "123";
    String newPwd = "test";
    String newPwdRepeat = "tes";

    assertThrows(InvalidRequestException.class,
        () -> userUCC.editPassword(currentUser, oldPwd, newPwd, newPwdRepeat));

  }

  @Test
  @DisplayName("Edit user password with someone updating at same time should work")
  public void testEditPasswordConcurrentUpdate() {

    UserDTO currentUser = userFactory.getUserDTO();
    currentUser.setId(1);
    currentUser.setEmail(email);
    currentUser.setLastname(lastname);
    currentUser.setFirstname(firstname);
    currentUser.setPhoneNumber(phoneNumber);
    currentUser.setRole(role);
    currentUser.setRegistrationDate(registrationDate);
    currentUser.setSchoolYear(schoolYear);
    currentUser.setPassword(hashPassword);
    currentUser.setVersion(1);

    String oldPwd = "123";
    String newPwd = "test";
    String newPwdRepeat = "test";

    Mockito.when(userDAOMock.editOneUser(currentUser, 1))
        .thenReturn(null);

    assertThrows(DuplicateException.class,
        () -> userUCC.editPassword(currentUser, oldPwd, newPwd, newPwdRepeat));

  }

}


