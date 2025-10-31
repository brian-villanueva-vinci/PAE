package be.vinci.pae.api.filters;

import be.vinci.pae.domain.dto.UserDTO;
import be.vinci.pae.domain.ucc.UserUCC;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * TeacherRequestFilter class.
 */
@Singleton
@Provider
@Teacher
public class TeacherRequestFilter implements ContainerRequestFilter {

  @Inject
  private UserUCC userUCC;

  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
    UserDTO authenticatedUser = TokenUtils.checkAuthorization(requestContext, userUCC);
    if (authenticatedUser != null) {
      if (!authenticatedUser.getRole().equals("Professeur")) {
        requestContext.abortWith(Response.status(Status.FORBIDDEN)
            .entity("Only teachers can access this resource.").build());
      }
      requestContext.setProperty("user", authenticatedUser);
    }
  }

}

