package be.vinci.pae.api.filters;

import be.vinci.pae.domain.dto.UserDTO;
import be.vinci.pae.domain.ucc.UserUCC;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * AuthorizationRequestFilter class.
 */
@Singleton
@Provider
@Authorize
public class AuthorizationRequestFilter implements ContainerRequestFilter {

  @Inject
  private UserUCC userUCC;

  /**
   * Filters.
   *
   * @param requestContext requestContext.
   * @throws IOException exception.
   */
  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
    UserDTO authenticatedUser = TokenUtils.checkAuthorization(requestContext, userUCC);
    requestContext.setProperty("user", authenticatedUser);
  }
}