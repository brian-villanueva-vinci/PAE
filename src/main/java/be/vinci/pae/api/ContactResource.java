package be.vinci.pae.api;

import be.vinci.pae.api.filters.Authorize;
import be.vinci.pae.domain.dto.ContactDTO;
import be.vinci.pae.domain.dto.UserDTO;
import be.vinci.pae.domain.ucc.ContactUCC;
import be.vinci.pae.utils.Logs;
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
 * ContactResource class.
 */
@Singleton
@Path("/contacts")
public class ContactResource {

  private final ObjectMapper jsonMapper = new ObjectMapper();
  @Inject
  private ContactUCC contactUCC;

  /**
   * Starting contact route.
   *
   * @param request the token.
   * @param json    jsonNode containing user id and company id to start the contact.
   * @return the started contact.
   */
  @POST
  @Path("start")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public ObjectNode start(@Context ContainerRequest request, JsonNode json) {
    Logs.log(Level.INFO, "ContactResource (start) : entrance");
    if (!json.hasNonNull("company")) {
      throw new WebApplicationException("company", Response.Status.BAD_REQUEST);
    }
    if (json.get("company").asText().isBlank()) {
      throw new WebApplicationException("company required", Response.Status.BAD_REQUEST);
    }

    int company = json.get("company").asInt();
    UserDTO userDTO = (UserDTO) request.getProperty("user");
    int studentId = userDTO.getId();

    ContactDTO contactDTO = contactUCC.start(company, studentId);

    ObjectNode contact = jsonMapper.createObjectNode().putPOJO("contact", contactDTO);
    Logs.log(Level.DEBUG, "ContactResource (start) : success!");
    return contact;
  }

  /**
   * returns a contact by its id.
   *
   * @param id of the internship
   * @return internshipDTO
   */
  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public Response getOneContact(@PathParam("id") int id) {
    Logs.log(Level.INFO, "ContactResource (getOneContact) : entrance");
    ContactDTO contactDTO = contactUCC.getOneById(id);
    String contact;

    try {
      contact = jsonMapper.writeValueAsString(contactDTO);
      return Response.ok(contact).build();
    } catch (JsonProcessingException e) {
      Logs.log(Level.FATAL, "ContactResource (getOneContact) : internal error");
      throw new WebApplicationException("internal error", Status.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Get all contacts by a student id.
   *
   * @param request the token.
   * @param student the student.
   * @return a list containing all the contacts by a student id.
   */
  @GET
  @Path("/all/{idStudent}")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public Response getAllByStudent(@Context ContainerRequest request,
      @PathParam("idStudent") int student) {
    Logs.log(Level.INFO, "ContactResource (getAllByStudent) : entrance");
    UserDTO user = (UserDTO) request.getProperty("user");
    if (user.getId() != student && !user.getRole().equals("Professeur") && !user.getRole()
        .equals("Administratif")) {
      Logs.log(Level.ERROR, "ContactResource (getAllByStudent) : not allowed");
      throw new WebApplicationException("unauthorized", Response.Status.UNAUTHORIZED);
    }
    List<ContactDTO> contactDTOList = contactUCC.getAllContactsByStudent(student);
    String r = null;

    try {
      r = jsonMapper.writeValueAsString(contactDTOList);
    } catch (JsonProcessingException e) {
      Logs.log(Level.FATAL, "UserResource (getOneUser) : internal error");
      throw new WebApplicationException("internal error", Status.INTERNAL_SERVER_ERROR);
    }

    Logs.log(Level.DEBUG, "ContactResource (getAllByStudent) : success!");
    return Response.ok(r).build();

  }

  /**
   * admitting a contact with the type of the meeting("on site" or "remote").
   *
   * @param request the token.
   * @param json    jsonNode containing contact id and the type of the meeting.
   * @return ObjectNode containing all information about the contact admitted.
   * @throws WebApplicationException when the contact_id and/or the meeting field is invalid.
   */
  @POST
  @Path("admit")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public ContactDTO admit(@Context ContainerRequest request, JsonNode json) {
    Logs.log(Level.INFO, "ContactResource (admit) : entrance");
    if (!json.hasNonNull("contactId") || !json.hasNonNull("meeting") || json.get("contactId")
        .asText().isBlank() || json.get("meeting").asText().isBlank()) {
      Logs.log(Level.WARN, "ContactResource (admit) : contactId or meeting is null");
      throw new WebApplicationException("contact or meeting required", Response.Status.BAD_REQUEST);
    }
    int contactId = json.get("contactId").asInt();
    String meeting = json.get("meeting").asText();
    int version = json.get("version").asInt();
    UserDTO userDTO = (UserDTO) request.getProperty("user");
    int studentId = userDTO.getId();

    ContactDTO contactDTO = contactUCC.admit(contactId, meeting, studentId, version);
    Logs.log(Level.DEBUG, "ContactResource (admit) : success!");

    return contactDTO;
  }

  /**
   * Unsupervised contact route.
   *
   * @param request the token.
   * @param json    jsonNode containing contact id to unsupervised the contact.
   * @return the unsupervised state of a contact.
   */
  @POST
  @Path("unsupervise")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public ContactDTO unsupervise(@Context ContainerRequest request, JsonNode json) {
    Logs.log(Level.INFO, "ContactResource (unsupervise) : entrance");
    UserDTO userDTO = (UserDTO) request.getProperty("user");
    if (!json.hasNonNull("contactId")) {
      Logs.log(Level.ERROR, "ContactResource (unsupervise) : contact null");
      throw new WebApplicationException("Contact id required", Response.Status.BAD_REQUEST);
    }
    if (json.get("contactId").asText().isBlank()) {
      Logs.log(Level.ERROR, "ContactResource (unsupervise) : contact blank");
      throw new WebApplicationException("Contact id cannot be blank", Response.Status.BAD_REQUEST);
    }
    int contactId = json.get("contactId").asInt();
    int version = json.get("version").asInt();
    int studentId = userDTO.getId();

    Logs.log(Level.DEBUG, "ContactResource (unsupervise) : success!");
    return contactUCC.unsupervise(contactId, studentId, version);
  }

  /**
   * a contact has turned down the internship.
   *
   * @param request the token.
   * @param json    jsonNode containing contact id of the contact and the reason for refusal.
   * @return ObjectNode containing all information about the contact to turn down.
   * @throws WebApplicationException when the contactId and/or the meeting field is invalid.
   */
  @POST
  @Path("turnDown")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public ContactDTO turnDown(@Context ContainerRequest request, JsonNode json) {
    Logs.log(Level.INFO, "ContactResource (turnDown) : entrance");
    if (!json.hasNonNull("contactId") || json.get("contactId").asText().isBlank()
        || !json.hasNonNull("reasonForRefusal") || json.get("reasonForRefusal").asText()
        .isBlank()) {
      Logs.log(Level.WARN, "ContactResource (turnDown) : contactId or reasonForRefusal is null");
      throw new WebApplicationException("contact or reason for refusal required",
          Response.Status.BAD_REQUEST);
    }

    int contactId = json.get("contactId").asInt();
    String reasonForRefusal = json.get("reasonForRefusal").asText();
    int version = json.get("version").asInt();
    UserDTO userDTO = (UserDTO) request.getProperty("user");
    int studentId = userDTO.getId();

    ContactDTO contactDTO = contactUCC.turnDown(contactId, reasonForRefusal, studentId, version);

    Logs.log(Level.DEBUG, "ContactResource (turnDown) : success!");

    return contactDTO;
  }


  /**
   * Get all contacts by a student id.
   *
   * @param company the student.
   * @return a list containing all the contacts by a student id.
   */
  @GET
  @Path("/all/company/{companyId}")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public List<ContactDTO> getAllByCompany(@PathParam("companyId") int company) {
    if (company == 0) {
      Logs.log(Level.INFO, "ContactResource (getAllByCompany) : entrance");
    }
    List<ContactDTO> contactDTOList = contactUCC.getAllContactsByCompany(company);

    Logs.log(Level.DEBUG, "ContactResource (getAllByCompany) : success!");
    return contactDTOList;

  }
}
