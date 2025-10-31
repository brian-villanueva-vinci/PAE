package be.vinci.pae.api;

import be.vinci.pae.api.filters.Authorize;
import be.vinci.pae.api.filters.Teacher;
import be.vinci.pae.api.filters.TeacherAndAdministrative;
import be.vinci.pae.domain.dto.CompanyDTO;
import be.vinci.pae.domain.dto.UserDTO;
import be.vinci.pae.domain.ucc.CompanyUCC;
import be.vinci.pae.utils.Logs;
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
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.glassfish.jersey.server.ContainerRequest;

/**
 * CompanyResource class.
 */
@Singleton
@Path("/companies")
public class CompanyResource {

  private final ObjectMapper jsonMapper = new ObjectMapper();
  @Inject
  private CompanyUCC companyUCC;

  /**
   * Get one company by its id.
   *
   * @param id the company's id.
   * @return the company in object node.
   */
  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public ObjectNode getOneById(@PathParam("id") int id) {
    Logs.log(Level.INFO, "CompanyResource (getOneById) : entrance");
    CompanyDTO company = companyUCC.findOneById(id);
    Logs.log(Level.DEBUG, "CompanyResource (getOneById) : success!");
    return jsonMapper.createObjectNode().putPOJO("company", company);
  }

  /**
   * Get all companies.
   *
   * @return a list containing all the companies including their internship count by year.
   */
  @GET
  @Path("all")
  @Produces(MediaType.APPLICATION_JSON)
  @TeacherAndAdministrative
  public ObjectNode getAll() {

    Logs.log(Level.INFO, "CompanyResource (getAll) : entrance");
    Map<Integer, Map<CompanyDTO, Map<String, Integer>>> companyList;
    companyList = companyUCC.getAllCompanies();

    ObjectNode statObject = jsonMapper.createObjectNode();
    for (Map.Entry<Integer, Map<CompanyDTO, Map<String, Integer>>>
        companies : companyList.entrySet()) {

      int id = companies.getKey();
      Map<CompanyDTO, Map<String, Integer>> companyValue = companies.getValue();

      for (Map.Entry<CompanyDTO, Map<String, Integer>> companyData : companyValue.entrySet()) {

        CompanyDTO company = companyData.getKey();

        ObjectNode companyNode = jsonMapper.createObjectNode();
        companyNode.put("id", company.getId());
        companyNode.put("name", company.getName());
        companyNode.put("designation", company.getDesignation());
        companyNode.put("address", company.getAddress());
        companyNode.put("phoneNumber", company.getPhoneNumber());
        companyNode.put("email", company.getEmail());
        companyNode.put("isBlacklisted", company.isBlacklisted());
        companyNode.put("blacklistMotivation", company.getBlacklistMotivation());
        companyNode.put("version", company.getVersion());

        ObjectNode data = jsonMapper.createObjectNode();
        Map<String, Integer> statMap = companyData.getValue();
        for (Map.Entry<String, Integer> internshipData : statMap.entrySet()) {
          data.put(internshipData.getKey(), internshipData.getValue());
        }

        companyNode.set("data", data);

        statObject.set(String.valueOf(id), companyNode);
      }


    }

    Logs.log(Level.INFO, "CompanyResource (getAll) : success!");
    return statObject;

  }

  /**
   * Get all companies available for the logged users.
   *
   * @param request the token.
   * @return a list containing all the companies.
   */
  @GET
  @Path("all/user")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public List<CompanyDTO> getAllByUser(@Context ContainerRequest request) {
    Logs.log(Level.INFO, "CompanyResource (getAllByUser) : entrance");
    UserDTO loggedUser = (UserDTO) request.getProperty("user");

    List<CompanyDTO> companyDTOList;
    companyDTOList = companyUCC.getAllCompaniesByUser(loggedUser.getId());
    Logs.log(Level.INFO, "CompanyResource (getAllByUser) : success!");
    return companyDTOList;
  }

  /**
   * Register route.
   *
   * @param companyToRegister CompanyDTO object containing name, designation if needed, address,
   *                          phone number or email not null.
   * @return a CompanyDTO.
   */
  @POST
  @Path("register")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public CompanyDTO register(CompanyDTO companyToRegister) {

    Logs.log(Level.INFO, "CompanyResource (register) : entrance");
    if (companyToRegister.getName().isBlank() || companyToRegister.getAddress().isBlank()) {
      Logs.log(Level.WARN, "CompanyResource (register) : missing input");
      throw new WebApplicationException("Inputs cannot be blank", Status.BAD_REQUEST);
    }
    if (companyToRegister.getEmail().isBlank() && companyToRegister.getPhoneNumber().isBlank()) {
      Logs.log(Level.WARN, "CompanyResource (register) : missing phoneNumber or email");
      throw new WebApplicationException("Need to have either a phone number or email",
          Status.BAD_REQUEST);
    }

    CompanyDTO registeredCompany;

    registeredCompany = companyUCC.registerCompany(companyToRegister);
    Logs.log(Level.INFO, "CompanyResource (register) : success!");
    return registeredCompany;

  }

  /**
   * POST to blacklist one company by its id.
   *
   * @param json    containing the id of the company.
   * @param request containing the token of the user
   * @return the company in object node.
   */
  @POST
  @Path("/blacklist")
  @Produces(MediaType.APPLICATION_JSON)
  @Teacher
  public ObjectNode blacklist(@Context ContainerRequest request, JsonNode json) {
    Logs.log(Level.INFO, "CompanyResource (blacklist) : entrance");
    if (!json.hasNonNull("company")) {
      Logs.log(Level.WARN, "ContactResource (start) : Company is null");
      throw new WebApplicationException("company required", Response.Status.BAD_REQUEST);
    }
    if (json.get("company").asText().isBlank()) {
      Logs.log(Level.WARN, "ContactResource (start) : Company is blank");
      throw new WebApplicationException("company required", Response.Status.BAD_REQUEST);
    }

    if (!json.hasNonNull("blacklistMotivation")) {
      Logs.log(Level.WARN, "ContactResource (start) : blacklistMotivation is null");
      throw new WebApplicationException("blacklist's motivation required",
          Response.Status.BAD_REQUEST);
    }
    if (json.get("blacklistMotivation").asText().isBlank()) {
      Logs.log(Level.WARN, "ContactResource (start) : blacklistMotivation is blank");
      throw new WebApplicationException("blacklist's motivation required",
          Response.Status.BAD_REQUEST);
    }

    int companyId = json.get("company").asInt();
    int version = json.get("version").asInt();
    String blacklistMotivation = json.get("blacklistMotivation").asText();

    CompanyDTO company = companyUCC.blacklist(companyId, blacklistMotivation, version);

    Logs.log(Level.DEBUG, "CompanyResource (getOneById) : success!");
    return jsonMapper.createObjectNode().putPOJO("company", company);
  }

}
