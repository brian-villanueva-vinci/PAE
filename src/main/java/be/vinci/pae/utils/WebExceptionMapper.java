package be.vinci.pae.utils;

import be.vinci.pae.utils.exceptions.DuplicateException;
import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.exceptions.InvalidRequestException;
import be.vinci.pae.utils.exceptions.NotAllowedException;
import be.vinci.pae.utils.exceptions.ResourceNotFoundException;
import be.vinci.pae.utils.exceptions.UnauthorizedAccessException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import org.apache.logging.log4j.Level;

/**
 * WebExceptionMapper class.
 */
public class WebExceptionMapper implements ExceptionMapper<Throwable> {

  @Override
  public Response toResponse(Throwable exception) {
    if (exception instanceof FatalException) {
      Logs.log(Level.ERROR, "Error : " + exception);
      return Response.status(Status.INTERNAL_SERVER_ERROR)
          .entity(exception.getMessage())
          .build();
    }
    if (exception instanceof InvalidRequestException) {
      Logs.log(Level.ERROR, "Error : " + exception);
      return Response.status(Status.BAD_REQUEST)
          .entity(exception.getMessage())
          .build();
    }
    if (exception instanceof ResourceNotFoundException) {
      Logs.log(Level.ERROR, "Error : " + exception);
      return Response.status(Status.NOT_FOUND)
          .entity(exception.getMessage())
          .build();
    }
    if (exception instanceof UnauthorizedAccessException) {
      Logs.log(Level.ERROR, "Error : " + exception);
      return Response.status(Status.UNAUTHORIZED)
          .entity(exception.getMessage())
          .build();
    }
    if (exception instanceof DuplicateException) {
      Logs.log(Level.ERROR, "Error : " + exception);
      return Response.status(Status.CONFLICT)
          .entity(exception.getMessage())
          .build();
    }
    if (exception instanceof NotAllowedException) {
      Logs.log(Level.ERROR, "Error : " + exception);
      return Response.status(Status.FORBIDDEN)
          .entity(exception.getMessage())
          .build();
    }
    if (exception instanceof WebApplicationException) {
      Logs.log(Level.ERROR, "Error : " + exception);
      return ((WebApplicationException) exception).getResponse();
    }
    Logs.log(Level.FATAL, "Error : " + exception);
    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
        .entity(exception.getMessage())
        .type(MediaType.TEXT_PLAIN)
        .build();
  }
}
