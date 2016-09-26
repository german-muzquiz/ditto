package com.ditto;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class DittoTest {

    @Test
    public void testGetSuccess() throws FileNotFoundException {
        // prepare
        String url = "/service/user/123";
        Configuration conf = new Configuration(new String[]{"replay", "8080",
                "src/test/resources/test_messages.txt"});
        MessageFactory messageFactory = MessageFactory.newInstance(conf);

        // execute
        Map<String, Response> responsesByUrl = messageFactory.responsesGetByUrl();
        Response response = responsesByUrl.get(url);

        // verify
        assertEquals(TestUtils.RESPONSES_FOR_GET.get(url), response);
    }

    @Test
    public void testHeadSuccess() throws FileNotFoundException {
        // prepare
        String url = "/otherservice/orders/456";
        Configuration conf = new Configuration(new String[]{"replay", "8080",
                "src/test/resources/test_messages.txt"});
        MessageFactory messageFactory = MessageFactory.newInstance(conf);

        // execute
        Map<String, Response> responsesByUrl = messageFactory.responsesHeadByUrl();
        Response response = responsesByUrl.get(url);

        // verify
        assertEquals(TestUtils.RESPONSES_FOR_HEAD.get(url), response);
    }

    @Test
    public void testPostError() throws FileNotFoundException {
        // prepare
        String url = "/service/user";
        String body = "{\"operation\":\"GET\"}";
        Configuration conf = new Configuration(new String[]{"replay", "8080",
                "src/test/resources/test_messages.txt"});
        MessageFactory messageFactory = MessageFactory.newInstance(conf);

        // execute
        Map<String, Map<String, Response>> responses = messageFactory.responsesPostByUrlAndBody();
        Response response = responses.get(url).get(body);

        // verify
        assertEquals(TestUtils.RESPONSES_FOR_POST.get(url), response);
    }

    @Test
    public void testPutSuccess() throws FileNotFoundException {
        // prepare
        String url = "/service/user";
        String body = "{\"operation\":\"GET\"}";
        Configuration conf = new Configuration(new String[]{"replay", "8080",
                "src/test/resources/test_messages.txt"});
        MessageFactory messageFactory = MessageFactory.newInstance(conf);

        // execute
        Map<String, Map<String, Response>> responses = messageFactory.responsesPutByUrlAndBody();
        Response response = responses.get(url).get(body);

        // verify
        assertEquals(TestUtils.RESPONSES_FOR_PUT.get(url), response);
    }

    @Test
    public void testDeleteError() throws FileNotFoundException {
        // prepare
        String url = "/service/user";
        Configuration conf = new Configuration(new String[]{"replay", "8080",
                "src/test/resources/test_messages.txt"});
        MessageFactory messageFactory = MessageFactory.newInstance(conf);

        // execute
        Map<String, Response> responsesByUrl = messageFactory.responsesDeleteByUrl();
        Response response = responsesByUrl.get(url);

        // verify
        assertEquals(TestUtils.RESPONSES_FOR_DELETE.get(url), response);
    }
}
