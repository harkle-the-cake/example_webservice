package eu.boxwork.example.webservice;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * a simple exception manager just printing the execption to log
 * */
@Provider
public class ServiceExceptionMapper implements ExceptionMapper<Exception> {
    private static final Logger logger = LogManager.getLogger(ServiceExceptionMapper.class);

    /**
     * logs the exception and returns it
     * @param e the exception
     * @return a {@link Response} with the exception as JSON
     * */
    @Override
    public Response toResponse(Exception e) {
        logger.error(e.getLocalizedMessage());
        String cause = "{\"cause\":\""+e.toString()+"\"}";
        return Response
                    .status(Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(cause)
                    .build();
    }

}