package be.vinci.pae.api.filters;

import be.vinci.pae.domain.dto.UserDTO;
import be.vinci.pae.domain.ucc.UserUCC;
import be.vinci.pae.utils.Config;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * handle some methods to verify tokens.
 */
public class TokenUtils {

  private static final Algorithm jwtAlgorithm = Algorithm.HMAC256(Config.getProperty("JWTSecret"));
  private static final JWTVerifier jwtVerifier = JWT.require(jwtAlgorithm).withIssuer("auth0")
      .build();

  /**
   * Verify token then check authenticated user.
   *
   * @param requestContext request context containing token.
   * @param userUCC        userUCC used to check user.
   * @return UserDTO if user exist.
   */
  public static UserDTO checkAuthorization(ContainerRequestContext requestContext,
      UserUCC userUCC) {
    String token = requestContext.getHeaderString("Authorization");
    DecodedJWT decodedToken;
    if (token == null) {
      requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
          .entity("A token is needed to access this resource").build());
    } else {
      try {
        decodedToken = jwtVerifier.verify(token);
      } catch (Exception e) {
        throw new WebApplicationException(Response.status(Status.UNAUTHORIZED)
            .entity("Malformed token : " + e.getMessage()).type("text/plain").build());
      }
      UserDTO authenticatedUser = userUCC.getOneById(decodedToken.getClaim("user").asInt());

      if (authenticatedUser == null) {
        requestContext.abortWith(Response.status(Status.FORBIDDEN)
            .entity("You are forbidden to access this resource").build());
      }
      return authenticatedUser;
    }
    return null;
  }
}
