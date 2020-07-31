import eu.boxwork.example.webservice.RestClient;
import eu.boxwork.example.webservice.services.Service;
import eu.boxwork.example.webservice.Webserver;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;
public class TestRestClient {
	@Rule
	public WireMockRule service = new WireMockRule(8089); // No-args constructor defaults to port 8080

	private static RestClient client = null;
	private static final String BASE_URL = "http://localhost:8089/"+ Webserver.BASEURL;
	private static final String BASE_URL_SERVICE = "/"+Webserver.BASEURL + Service.SERVICE_PATH+"answer/";
	private static final int DELAYTIME = 10000;
	public static final String USER_ID = "test";
	public static final String AUTHORIZE = "Bearer ABCD";
	public static final String AUTHORIZE_WRONG = "Bearer ABCD1";

	@BeforeClass
	public static void rampup()
	{
		client = new RestClient();
		client.initialise(BASE_URL, USER_ID, AUTHORIZE);
		client.setTimeouts(5000,5000);
		assertTrue(client.connect());
	}

	@Before
	public void beforeTest()
	{
		if (!service.isRunning()) service.start();
		client.setAuthorization(AUTHORIZE);
		client.setTimeouts(500,500);
	}
	
	/* #######################################################
	 * # POSITIVE CASES
	 * #######################################################
	 * */
	@Test
	public void testHello() {
		stubFor(get(urlPathEqualTo(BASE_URL_SERVICE+"world"))
				.withHeader("user", equalTo(USER_ID))
				.withHeader("authorize", equalTo(AUTHORIZE))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")
						.withBody(
								"{\"answer\":\"world\"}"
						)));


		JSONObject answer = client.requestHelloJSON("world");
		try
		{

			assertEquals("world",answer.getString("answer"));
		}
		catch (Exception e)
		{
			fail (e.toString());
		}
	}

	@Test
	public void testHelloString() {
		stubFor(get(urlPathEqualTo(BASE_URL_SERVICE+"world"))
				.withHeader("user", equalTo(USER_ID))
				.withHeader("authorize", equalTo(AUTHORIZE))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "text/plain")
						.withBody(
								"world"
						)));

		String answer = client.requestHello("world");
		assertEquals("world",answer);
	}


	/* #######################################################
	 * # NEGATIV CASES
	 * #######################################################
	 * */
	@Test
	public void testHelloNull1() {
		stubFor(get(urlPathEqualTo(BASE_URL_SERVICE+"null"))
				.withHeader("user", equalTo(USER_ID))
				.withHeader("authorize", equalTo(AUTHORIZE))
				.willReturn(aResponse()
						.withStatus(400)
						.withHeader("Content-Type", "application/json")
						.withBody(
								"{\"error\":\"no name given\"}"
						)));

		JSONObject answer = client.requestHelloJSON(null);
		assertEquals(null,answer);
	}

	@Test
	public void testHelloStringNull1() {
		stubFor(get(urlPathEqualTo(BASE_URL_SERVICE+"null"))
				.withHeader("user", equalTo(USER_ID))
				.withHeader("authorize", equalTo(AUTHORIZE))
				.willReturn(aResponse()
						.withStatus(400)
						.withHeader("Content-Type", "text/plain")
						.withBody(
								"error"
						)));

		String answer = client.requestHello(null);
		assertEquals(null,answer);
	}


	@Test
	public void testNoAnswerTag() {
		stubFor(get(urlPathEqualTo(BASE_URL_SERVICE+"name"))
				.withHeader("user", equalTo(USER_ID))
				.withHeader("authorize", equalTo(AUTHORIZE))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")
						.withBody(
								"{\"wrongtag\":\"name\"}"
						)));

		JSONObject answer = client.requestHelloJSON("name");
		assertEquals(null,answer);
	}

	@Test
	public void testHelloEmpty() {
		stubFor(get(urlPathEqualTo(BASE_URL_SERVICE))
				.withHeader("user", equalTo(USER_ID))
				.withHeader("authorize", equalTo(AUTHORIZE))
				.willReturn(aResponse()
						.withStatus(400)
						.withHeader("Content-Type", "application/json")
						.withBody(
								"{\"error\":\"no name given\"}"
						)));

		JSONObject answer = client.requestHelloJSON("");
		assertEquals(null,answer);
	}

	@Test
	public void testHelloStringEmpty() {
		stubFor(get(urlPathEqualTo(BASE_URL_SERVICE))
				.withHeader("user", equalTo(USER_ID))
				.withHeader("authorize", equalTo(AUTHORIZE))
				.willReturn(aResponse()
						.withStatus(400)
						.withHeader("Content-Type", "text/plain")
						.withBody(
								"error empty string"
						)));

		String answer = client.requestHello("");
		assertEquals(null,answer);
	}

	@Test
	public void testHelloWorldWrongToken() {
		stubFor(get(urlPathEqualTo(BASE_URL_SERVICE + "name"))
				.withHeader("user", equalTo(USER_ID))
				.withHeader("authorize", equalTo(AUTHORIZE_WRONG))
				.willReturn(aResponse()
						.withStatus(404)
						.withHeader("Content-Type", "application/json")
						.withBody(
								"{\"error\":\"user not valid\"}"
						)));
		client.setAuthorization(AUTHORIZE_WRONG);
		JSONObject answer = client.requestHelloJSON("name");
		assertEquals(null, answer);
	}

	@Test
	public void testHelloWorldStringWrongToken() {
		stubFor(get(urlPathEqualTo(BASE_URL_SERVICE + "name"))
				.withHeader("user", equalTo(USER_ID))
				.withHeader("authorize", equalTo(AUTHORIZE_WRONG))
				.willReturn(aResponse()
						.withStatus(404)
						.withHeader("Content-Type", "text/plain")
										.withBody(
								"user not authorized"
						)));
		client.setAuthorization(AUTHORIZE_WRONG);
		String answer = client.requestHello("name");
		assertEquals(null, answer);
	}

	/* #######################################################
	 * # TIMEOUTs
	 * #######################################################
	 * */
	@Test
	public void testHelloWorldTimedout() {
		stubFor(get(urlPathEqualTo(BASE_URL_SERVICE+"myname"))
				.withHeader("user", equalTo(USER_ID))
				.withHeader("authorize", equalTo(AUTHORIZE))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "application/json")
						.withBody(
								"{\"answer\":\"myname\"}"
						).withFixedDelay(DELAYTIME)));

		JSONObject answer = client.requestHelloJSON("myname");
		assertEquals(null,answer);
	}

	@Test
	public void testHelloWorldStringTimedout() {
		stubFor(get(urlPathEqualTo(BASE_URL_SERVICE+"myname"))
				.withHeader("user", equalTo(USER_ID))
				.withHeader("authorize", equalTo(AUTHORIZE))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-Type", "text/plain")
						.withBody(
								"myname"
						).withFixedDelay(DELAYTIME)));

		String answer = client.requestHello("myname");
		assertEquals(null,answer);
	}
}
