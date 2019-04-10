package edu.asupoly.ser422.restexample.api;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.jms.*;
import javax.servlet.ServletContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jmx.snmp.Timestamp;

import edu.asupoly.ser422.restexample.model.Author;
import edu.asupoly.ser422.restexample.services.BooktownService;
import edu.asupoly.ser422.restexample.services.BooktownServiceFactory;
import edu.asupoly.ser422.restexample.api.AuthorJMSHelper;

@Path("/authors")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML })

public class AuthorResource {
	private static BooktownService __bService = BooktownServiceFactory.getInstance();
	public AuthorJMSHelper JMSHelperActiveMQ = new AuthorJMSHelper();
	Timestamp timestamp = new Timestamp();
	@Context
	ServletContext context;

	// Technique for location header taken from
	// http://usna86-techbits.blogspot.com/2013/02/how-to-return-location-header-from.html
	@Context
	private UriInfo _uriInfo;

	/**
	 * @apiDefine BadRequestError
	 * @apiError (Error 4xx) {400} BadRequest Bad Request Encountered
	 */
	/**
	 * @apiDefine ActivityNotFoundError
	 * @apiError (Error 4xx) {404} NotFound Activity cannot be found
	 */
	/**
	 * @apiDefine InternalServerError
	 * @apiError (Error 5xx) {500} InternalServerError Something went wrong at
	 *           server, Please contact the administrator!
	 */
	/**
	 * @apiDefine NotImplementedError
	 * @apiError (Error 5xx) {501} NotImplemented The resource has not been
	 *           implemented. Please keep patience, our developers are working hard
	 *           on it!!
	 */

	/**
	 * @api {get} /authors Get list of Authors
	 * @apiName getAuthors
	 * @apiGroup Authors
	 *
	 * @apiUse BadRequestError
	 * @apiUse InternalServerError
	 * 
	 * @apiSuccessExample Success-Response: HTTP/1.1 200 OK [
	 *                    {"authorId":1111,"firstName":"Ariel","lastName":"Denham"},
	 *                    {"authorId":1212,"firstName":"John","lastName":"Worsley"}
	 *                    ]
	 * 
	 */

	public void jmsLogger(String timestamp, String url, String verb, int respCode) {
		try {
			Connection connection = JMSHelperActiveMQ.getJMSConnection();
			connection.start();
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue("lab4log");

			// Create a MessageProducer from the Session to the Topic or Queue
			MessageProducer producer = session.createProducer(destination);
			producer.setDeliveryMode(DeliveryMode.PERSISTENT);

			TextMessage msg = session.createTextMessage("Timestamp: " + timestamp + " ; URL Path: " + url
					+ " ; HTTP Verb: " + verb + " ; Response Code: " + respCode);
			producer.send(msg);

			session.close();
			connection.stop();
			connection.close();
		} catch (Throwable tw) {
			tw.printStackTrace();
		}
	}

	@GET
	public List<Author> getAuthors() {

		String date = timestamp.getDate().toString();

		jmsLogger(date, _uriInfo.getAbsolutePath().toString(), "GET", 204);

		// int code = Response.StatusType.getStatusCode();
		return __bService.getAuthors();
	}

	/*
	 * This is the first version of GET we did, using defaults and letting Jersey
	 * internally serialize
	 * 
	 * @GET
	 * 
	 * @Path("/{authorId}") public Author getAuthor(@PathParam("authorId") int aid)
	 * { return __bService.getAuthor(aid); }
	 */
	/*
	 * This is a second version - it uses Jackson's default mapping via
	 * ObjectMapper, which spits out the same JSON as Jersey's internal version, so
	 * the output will look the same as version 1 when you run
	 * 
	 * @GET
	 * 
	 * @Path("/{authorId}") public Response getAuthor(@PathParam("authorId") int
	 * aid) { // This isn't correct - what if the authorId is not for an active
	 * author? Author author = __bService.getAuthor(aid); // let's use Jackson
	 * instead. ObjectMapper will build a JSON string and we use // the
	 * ResponseBuilder to use that. Note the result looks the same try { String
	 * aString = new ObjectMapper().writeValueAsString(author); return
	 * Response.status(Response.Status.OK).entity(aString).build(); } catch
	 * (Exception exc) { exc.printStackTrace(); return null; } }
	 */
	// This is a 3rd version using a custom serializer I've encapsulated over in the
	// new helper class
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/{authorId}")
	public Response getAuthor(@PathParam("authorId") int aid) {
		Author author = __bService.getAuthor(aid);

		String date = timestamp.getDate().toString();

		// AuthorSerializationHelper will build a slightly different JSON string and we
		// still use
		// the ResponseBuilder to use that. The key property names are changed in the
		// result.
		try {
			String aString = AuthorSerializationHelper.getHelper().generateJSON(author);
			jmsLogger(date, _uriInfo.getAbsolutePath().toString(), "GET", 200);
			return Response.status(Response.Status.OK).entity(aString).build();
		} catch (Exception exc) {
			exc.printStackTrace();
			return null;
		}
	}

	/*
	 * This was the first version of POST we did
	 * 
	 * @POST
	 * 
	 * @Consumes("text/plain") public int createAuthor(String name) { String[] names
	 * = name.split(" "); // not handled - what if this returns -1? int aid =
	 * __bService.createAuthor(names[0], names[1]); return aid; }
	 */
	/*
	 * This was the second version that added simple custom response headers and
	 * payload
	 */
	@POST
	@Consumes("text/plain")
	public Response createAuthor(String name) {
		String[] names = name.split(" ");
		String date = timestamp.getDate().toString();
		int aid = __bService.createAuthor(names[0], names[1]);
		if (aid == -1) {
			jmsLogger(date, _uriInfo.getAbsolutePath().toString(), "POST", 500);
			return Response.status(500).entity("{ \" EXCEPTION INSERTING INTO DATABASE! \"}").build();
		} else if (aid == 0) {
			jmsLogger(date, _uriInfo.getAbsolutePath().toString(), "POST", 500);
			return Response.status(500).entity("{ \" ERROR INSERTING INTO DATABASE! \"}").build();
		}
		jmsLogger(date, _uriInfo.getAbsolutePath().toString(), "POST", 201);
		return Response.status(201)
				.header("Location", String.format("%s/%d", _uriInfo.getAbsolutePath().toString(), aid))
				.entity("{ \"Author\" : \"" + aid + "\"}").build();
	}

	/*
	 * This is the original PUT method that consumed the default JSON Jersey
	 * produces. Would work with the JSON produced by getAuthor versions 1 and 2
	 * above, but not version 3
	 * 
	 * @PUT
	 * 
	 * @Consumes("application/json") public Response updateAuthor(Author a) { if
	 * (__bService.updateAuthor(a)) { return
	 * Response.status(201).entity("{ \"Author\" : \"" + a.getAuthorId() +
	 * "\"}").build(); } else { return Response.status(404,
	 * "{ \"message \" : \"No such Author " + a.getAuthorId() + "\"}").build(); } }
	 */
	/*
	 * This 2nd version of PUT uses the deserializer from AuthorSerializationHelper,
	 * and process the JSON given in GET version 3 above. Note that when you use the
	 * custom serializer/deserializer, it will not be compatible with methods that
	 * do not use it (which will continue to use the Jersey default). If you decide
	 * to customize, then you should be certain to use your (de)serializer
	 * throughout your resource!
	 */
	@PUT
	@Consumes("application/json")
	public Response updateAuthor(String json) {
		String date = timestamp.getDate().toString();
		try {
			Author a = AuthorSerializationHelper.getHelper().consumeJSON(json);
			if (__bService.updateAuthor(a)) {
				// In the response payload it would still use Jackson's default serializer,
				// so we directly invoke our serializer so the PUT payload reflects what it
				// should.
				String aString = AuthorSerializationHelper.getHelper().generateJSON(a);
				jmsLogger(date, _uriInfo.getAbsolutePath().toString(), "PUT", 201);
				return Response.status(201).entity(aString).build();
			} else {
				jmsLogger(date, _uriInfo.getAbsolutePath().toString(), "PUT", 404);
				return Response.status(404, "{ \"message \" : \"No such Author " + a.getAuthorId() + "\"}").build();
			}
		} catch (Exception exc) {
			exc.printStackTrace();
			jmsLogger(date, _uriInfo.getAbsolutePath().toString(), "PUT", 500);
			return Response.status(500, "{ \"message \" : \"Internal server error deserializing Author JSON\"}")
					.build();
		}
	}

	@DELETE
	public Response deleteAuthor(@QueryParam("id") int aid) {
		String date = timestamp.getDate().toString();
		if (__bService.deleteAuthor(aid)) {
			jmsLogger(date, _uriInfo.getAbsolutePath().toString(), "DELETE", 204);
			return Response.status(204).build();
		} else {
			jmsLogger(date, _uriInfo.getAbsolutePath().toString(), "DELETE", 404);
			return Response.status(404, "{ \"message \" : \"No such Author " + aid + "\"}").build();
		}
	}
	/*
	 * @PATCH public Response patchAuthor(@QueryParam("id") int aid) { return
	 * Response.status(405, "{ \"message \" : \"PATCH not supported\"}").build(); }
	 */
}
