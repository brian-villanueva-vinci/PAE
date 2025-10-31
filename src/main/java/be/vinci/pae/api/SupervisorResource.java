package be.vinci.pae.api;

import be.vinci.pae.api.filters.Authorize;
import be.vinci.pae.domain.dto.SupervisorDTO;
import be.vinci.pae.domain.ucc.SupervisorUCC;
import be.vinci.pae.utils.Logs;
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
import java.util.List;
import org.apache.logging.log4j.Level;
import org.glassfish.jersey.server.ContainerRequest;

/**
 * SupervisorResource class.
 */
@Singleton
@Path("/supervisors")
public class SupervisorResource {

  @Inject
  private SupervisorUCC supervisorUCC;

  /**
   * returns a supervisor by a supervisor id.
   *
   * @param request the token from the front.
   * @param id      of the supervisor
   * @return supervisorDTO
   */
  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public SupervisorDTO getOneSupervisorById(@Context ContainerRequest request,
      @PathParam("id") int id) {
    Logs.log(Level.INFO, "SupervisorResource (getOneSupervisorById) : entrance");
    return supervisorUCC.getOneById(id);
  }

  /**
   * Get all supervisors of a company.
   *
   * @param companyId the company's id.
   * @return all the possible supervisors.
   */
  @GET
  @Path("allByCompany/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public List<SupervisorDTO> getAllByCompany(@PathParam("id") int companyId) {
    Logs.log(Level.INFO, "SupervisorResource (getAllByCompany) : entrance");
    return supervisorUCC.getAllByCompany(companyId);
  }

  /**
   * Add a supervisor.
   *
   * @param supervisorDTO the supervisor to add.
   * @return the added supervisor.
   */
  @POST
  @Path("add")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public SupervisorDTO addSupervisor(SupervisorDTO supervisorDTO) {
    Logs.log(Level.INFO, "SupervisorResource (addSupervisor) : entrance");
    if (supervisorDTO.getFirstname().isBlank() || supervisorDTO.getLastname().isBlank()
        || supervisorDTO.getPhoneNumber().isBlank()) {
      Logs.log(Level.WARN, "SupervisorResource (addSupervisor) : missing input");
      throw new WebApplicationException("Inputs cannot be blank", Response.Status.BAD_REQUEST);
    }

    SupervisorDTO addedSupervisor = supervisorUCC.addSupervisor(supervisorDTO);
    Logs.log(Level.INFO, "SupervisorResource (addSupervisor) : success");
    return addedSupervisor;
  }

}
