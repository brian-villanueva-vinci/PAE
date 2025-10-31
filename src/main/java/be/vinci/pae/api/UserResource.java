package be.vinci.pae.api;

import be.vinci.pae.api.filters.Authorize;
import be.vinci.pae.api.filters.TeacherAndAdministrative;
import be.vinci.pae.domain.dto.UserDTO;
import be.vinci.pae.domain.ucc.UserUCC;
import be.vinci.pae.utils.Config;
import be.vinci.pae.utils.Logs;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.util.List;
import org.apache.logging.log4j.Level;
import org.glassfish.jersey.server.ContainerRequest;

/**
 * UserResource class.
 */
@Singleton
@Path("/users")
public class UserResource {

  private final Algorithm jwtAlgorithm = Algorithm.HMAC256(Config.getProperty("JWTSecret"));
  private final ObjectMapper jsonMapper = new ObjectMapper();
  @Inject
  private UserUCC userUCC;

  /**
   * Login route.
   *
   * @param json object containing login and password.
   * @return a User and their token as a JsonNode.
   */
  @POST
  @Path("login")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public ObjectNode login(JsonNode json) {
    Logs.log(Level.INFO, "UserResource (login) : entrance");
    if (!json.hasNonNull("email") || !json.hasNonNull("password")) {
      Logs.log(Level.WARN, "UserResource (login) : email or password is null");
      throw new WebApplicationException("Email and password required", Response.Status.BAD_REQUEST);
    }
    if (json.get("email").asText().isBlank() || json.get("password").asText().isBlank()) {
      throw new WebApplicationException("Email and password cannot be blank",
          Response.Status.BAD_REQUEST);
    }

    String email = json.get("email").asText();
    String password = json.get("password").asText();

    UserDTO userDTO = userUCC.login(email, password);
    return buildToken(userDTO);
  }

  /**
   * Get all users.
   *
   * @param request the token.
   * @return a list containing all the users.
   */
  @GET
  @Path("all")
  @Produces(MediaType.APPLICATION_JSON)
  @TeacherAndAdministrative
  public List<UserDTO> getAll(@Context ContainerRequest request) {
    Logs.log(Level.INFO, "UserResource (getAll) : entrance");
    UserDTO user = (UserDTO) request.getProperty("user");
    List<UserDTO> userDTOList;
    userDTOList = userUCC.getAllUsers(user);
    Logs.log(Level.DEBUG, "UserResource(getAll) : success!");
    return userDTOList;
  }

  /**
   * Check if a token is correct.
   *
   * @param request the token.
   * @return a new token.
   */
  @GET
  @Path("checkToken")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public ObjectNode checkToken(@Context ContainerRequest request) {
    Logs.log(Level.INFO, "UserResource (rememberMe) : entrance");
    UserDTO userDTO = (UserDTO) request.getProperty("user");
    Logs.log(Level.DEBUG, "UserResource (rememberMe) : success!");
    return buildToken(userDTO);
  }

  /**
   * Build a token based on a UserDTO.
   *
   * @param userDTO the userDTO.
   * @return the token built.
   */
  private ObjectNode buildToken(UserDTO userDTO) {
    String token = JWT.create().withIssuer("auth0")
        .withClaim("user", userDTO.getId()).sign(this.jwtAlgorithm);
    ObjectNode publicUser = jsonMapper.createObjectNode()
        .put("token", token)
        .putPOJO("user", userDTO);
    Logs.log(Level.WARN, "UserResource (login) : success!");
    return publicUser;
  }

  /**
   * returns a user by its id.
   *
   * @param request the token from the front.
   * @param id      of the user
   * @return userDTO
   */
  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public Response getOneUser(@Context ContainerRequest request, @PathParam("id") int id) {
    Logs.log(Level.INFO, "UserResource (getOneUser) : entrance");
    UserDTO userCheck = (UserDTO) request.getProperty("user");
    if (userCheck.getId() != id && !userCheck.getRole().equals("Professeur") && !userCheck.getRole()
        .equals("Administratif")) {
      throw new WebApplicationException("you can't see this user", Response.Status.UNAUTHORIZED);
    }

    UserDTO userDTO = userUCC.getOneById(id);
    String user;
    try {
      user = jsonMapper.writeValueAsString(userDTO);
    } catch (JsonProcessingException e) {
      throw new WebApplicationException("internal error", Status.INTERNAL_SERVER_ERROR);
    }
    Logs.log(Level.INFO, "UserResource (getOneUser) : success!");
    return Response.ok(user).build();
  }

  /**
   * Register route.
   *
   * @param userToRegister UserDTO object containing email, lastname, firstname, phone number,
   *                       password, role.
   * @return a User.
   */
  @POST
  @Path("register")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public UserDTO register(UserDTO userToRegister) {

    Logs.log(Level.INFO, "UserResource (register) : entrance");
    if (userToRegister.getEmail().isBlank() || userToRegister.getLastname().isBlank()
        || userToRegister.getFirstname().isBlank() || userToRegister.getPhoneNumber().isBlank()
        || userToRegister.getPassword().isBlank() || userToRegister.getRole().isBlank()) {
      Logs.log(Level.WARN, "UserResource (register) : missing input");
      throw new WebApplicationException("Inputs cannot be blank", Response.Status.BAD_REQUEST);
    }

    UserDTO registeredUser = userUCC.register(userToRegister);
    Logs.log(Level.INFO, "UserResource (register) : success!");
    return registeredUser;

  }

  /**
   * Edit a user.
   *
   * @param request     the token from the front.
   * @param userDTOEdit UserDTO object.
   * @return An edited user.
   */
  @POST
  @Path("editUser")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public UserDTO editUser(@Context ContainerRequest request, UserDTO userDTOEdit) {
    Logs.log(Level.INFO, "UserResource (editUser) : entrance");
    UserDTO userDTO = (UserDTO) request.getProperty("user");
    if (userDTOEdit.getId() != userDTO.getId()) {
      Logs.log(Level.ERROR, "UserResource (editUser) : unauthorized");
      throw new WebApplicationException("you can't see this user", Response.Status.UNAUTHORIZED);
    }

    UserDTO editedUser = userUCC.editOneUser(userDTO, userDTOEdit);
    Logs.log(Level.INFO, "UserResource (editUser) : success!");
    return editedUser;
  }

  /**
   * Edit a password.
   *
   * @param request the token from the front.
   * @param json    object containing oldPassword, newPassword, repeatedPassword.
   * @return An edited password.
   */
  @POST
  @Path("editPassword")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public UserDTO editPassword(@Context ContainerRequest request, JsonNode json) {
    Logs.log(Level.INFO, "UserResource (editPassword) : entrance");
    if (!json.hasNonNull("oldPassword")
        || !json.hasNonNull("newPassword")
        || !json.hasNonNull(
        "repeatedPassword")) {
      throw new WebApplicationException("old ,new and repeated password required",
          Response.Status.BAD_REQUEST);
    }
    if (json.get("oldPassword").asText().isBlank()
        || json.get("newPassword").asText().isBlank()
        || json.get("repeatedPassword").asText().isBlank()) {
      throw new WebApplicationException("old ,new and repeated password required",
          Response.Status.BAD_REQUEST);
    }

    UserDTO user = (UserDTO) request.getProperty("user");

    String oldPassword = json.get("oldPassword").asText();
    String newPassword = json.get("newPassword").asText();
    String repeatedPassword = json.get("repeatedPassword").asText();

    return userUCC.editPassword(user, oldPassword, newPassword, repeatedPassword);
  }
}

