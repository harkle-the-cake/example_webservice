import eu.boxwork.example.webservice.services.Service;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.*;

public class TestRestService {

    public static final String USER_ID = "test";
    public static final String AUTHORIZE = "Bearer ABCD";
    public static final String AUTHORIZE_WRONG = "Bearer ABCD1";

    private static Service service = new Service();

    @BeforeClass
    public static void rampup()
    {
    }

    @Before
    public void beforeTest()
    {
    }

    /* #######################################################
     * # POSITIVE CASES
     * #######################################################
     * */
    @Test
    public void testHelloValidJSON()
    {
        Response response = service.getAnswerJSON(USER_ID,AUTHORIZE,"test");
        assertEquals(200,response.getStatus());
        String jsonResponse = (String)response.getEntity();
        try
        {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            assertTrue(jsonObject.has("answer"));
            assertEquals("test",jsonObject.getString("answer"));
        }
        catch (Exception e)
        {
            fail(e.toString());
        }
    }

    @Test
    public void testHelloValidPlainText()
    {
        Response response = service.getAnswerAsTextPlain(USER_ID,AUTHORIZE,"test");
        assertEquals(200,response.getStatus());
        String responseEntity = (String)response.getEntity();
        assertEquals("test",responseEntity);
    }

    @Test
    public void testHelloValidHTML()
    {
        Response response = service.getHelloAsHTML("test");
        assertEquals(200,response.getStatus());
        String responseEntityHTML = (String)response.getEntity();
        String exspected="<HTML><BODY><div style=\"color:red;\">test</div></BODY></HTML>";
        assertEquals(exspected,responseEntityHTML);
    }

    /* #######################################################
     * # NEGATIV CASES
     * #######################################################
     * */
    @Test
    public void testHelloValidJSONNoUser()
    {
        Response response = service.getAnswerJSON(null,AUTHORIZE,"test");
        assertEquals(400,response.getStatus());
    }

    @Test
    public void testHelloValidPlainTextNoUser()
    {
        Response response = service.getAnswerAsTextPlain(null,AUTHORIZE,"test");
        assertEquals(400,response.getStatus());
    }

    @Test
    public void testHelloValidJSONEmptyUser()
    {
        Response response = service.getAnswerJSON("",AUTHORIZE,"test");
        assertEquals(400,response.getStatus());
    }

    @Test
    public void testHelloValidPlainTextEmptyUser()
    {
        Response response = service.getAnswerAsTextPlain("",AUTHORIZE,"test");
        assertEquals(400,response.getStatus());
    }

    @Test
    public void testHelloValidJSONWrongUser()
    {
        Response response = service.getAnswerJSON("n",AUTHORIZE,"test");
        assertEquals(401,response.getStatus());
    }

    @Test
    public void testHelloValidPlainTextWrongUser()
    {
        Response response = service.getAnswerAsTextPlain("n",AUTHORIZE,"test");
        assertEquals(401,response.getStatus());
    }

    @Test
    public void testHelloValidJSONNoAuth()
    {
        Response response = service.getAnswerJSON(USER_ID,null,"test");
        assertEquals(400,response.getStatus());
    }

    @Test
    public void testHelloValidPlainTextNoAuth()
    {
        Response response = service.getAnswerAsTextPlain(USER_ID,null,"test");
        assertEquals(400,response.getStatus());
    }

    @Test
    public void testHelloValidJSONEmptyAuth()
    {
        Response response = service.getAnswerJSON(USER_ID,"","test");
        assertEquals(400,response.getStatus());
    }

    @Test
    public void testHelloValidPlainTextEmptyAuth()
    {
        Response response = service.getAnswerAsTextPlain(USER_ID,"","test");
        assertEquals(400,response.getStatus());
    }

    @Test
    public void testHelloValidJSONWrongAuth()
    {
        Response response = service.getAnswerJSON(USER_ID,"Bearer ABC","test");
        assertEquals(401,response.getStatus());
    }

    @Test
    public void testHelloValidPlainTextWrongAuth()
    {
        Response response = service.getAnswerAsTextPlain(USER_ID,"Bearer ABC","test");
        assertEquals(401,response.getStatus());
    }


    @Test
    public void testHelloValidJSONBearerMissing()
    {
        Response response = service.getAnswerJSON(USER_ID,"ABC","test");
        assertEquals(400,response.getStatus());
    }

    @Test
    public void testHelloValidPlainTextBearerMissing()
    {
        Response response = service.getAnswerAsTextPlain(USER_ID,"ABC","test");
        assertEquals(400,response.getStatus());
    }

    @Test
    public void testHelloValidJSONBearerWrongCoded()
    {
        Response response = service.getAnswerJSON(USER_ID,"bearer ABC","test");
        assertEquals(400,response.getStatus());
    }

    @Test
    public void testHelloValidPlainTextBearerWrongCoded()
    {
        Response response = service.getAnswerAsTextPlain(USER_ID,"bearer ABC","test");
        assertEquals(400,response.getStatus());
    }

    @Test
    public void testHelloValidEmptyHTML()
    {
        Response response = service.getHelloAsHTML("");
        assertEquals(400,response.getStatus());
    }
    @Test
    public void testHelloValidNullHTML()
    {
        Response response = service.getHelloAsHTML(null);
        assertEquals(400,response.getStatus());
    }

    @Test
    public void testHelloEmptyJSON()
    {
        Response response = service.getAnswerJSON(USER_ID,AUTHORIZE,"");
        assertEquals(400,response.getStatus());
    }

    @Test
    public void testHelloEmptyPlainText()
    {
        Response response = service.getAnswerAsTextPlain(USER_ID,AUTHORIZE,"");
        assertEquals(400,response.getStatus());
    }

    @Test
    public void testHelloNullJSON()
    {
        Response response = service.getAnswerJSON(USER_ID,AUTHORIZE,null);
        assertEquals(400,response.getStatus());
    }

    @Test
    public void testHelloNullPlainText()
    {
        Response response = service.getAnswerAsTextPlain(USER_ID,AUTHORIZE,null);
        assertEquals(400,response.getStatus());
    }


    /* #######################################################
     * # HELPER
     * #######################################################
     * */
}
