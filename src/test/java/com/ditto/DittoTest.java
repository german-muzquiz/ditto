//package com.ditto;
//
//import org.junit.Test;
//
//import java.io.FileNotFoundException;
//import java.util.List;
//
//import static org.junit.Assert.assertEquals;
//
//public class DittoTest {
//
//    @Test
//    public void testGetSuccess() throws FileNotFoundException {
//        // prepare
//        String url = "/service/user/123";
//        Configuration conf = new Configuration(new String[]{"replay", "8080",
//                "src/test/resources/test_messages.txt"});
//        MessageFactory messageFactory = MessageFactory.newInstance(conf);
//
//        // execute
//        List<RequestResponse> messages = messageFactory.responsesGetByUrl();
//        Response response = null;
//        for (RequestResponse requestResponse : messages) {
//            if (requestResponse.getRequest().getUrl().equals(url)) {
//                response = requestResponse.getResponse();
//            }
//        }
//
//        // verify
//        assertEquals(TestUtils.RESPONSES_FOR_GET.get(url + "?param=value").getResponse(), response);
//    }
//
//    @Test
//    public void testHeadSuccess() throws FileNotFoundException {
//        // prepare
//        String url = "/otherservice/orders/456";
//        Configuration conf = new Configuration(new String[]{"replay", "8080",
//                "src/test/resources/test_messages.txt"});
//        MessageFactory messageFactory = MessageFactory.newInstance(conf);
//
//        // execute
//        List<RequestResponse> messages = messageFactory.responsesHeadByUrl();
//        Response response = null;
//        for (RequestResponse requestResponse : messages) {
//            if (requestResponse.getRequest().getUrl().equals(url)) {
//                response = requestResponse.getResponse();
//            }
//        }
//
//        // verify
//        assertEquals(TestUtils.RESPONSES_FOR_HEAD.get(url).getResponse(), response);
//    }
//
//    @Test
//    public void testPostError() throws FileNotFoundException {
//        // prepare
//        String url = "/service/user";
//        String body = "{\"operation\":\"GET\"}";
//        Configuration conf = new Configuration(new String[]{"replay", "8080",
//                "src/test/resources/test_messages.txt"});
//        MessageFactory messageFactory = MessageFactory.newInstance(conf);
//
//        // execute
//        List<RequestResponse> messages = messageFactory.responsesPostByUrlAndBody();
//        Response response = null;
//        for (RequestResponse requestResponse : messages) {
//            if (requestResponse.getRequest().getUrl().equals(url)) {
//                response = requestResponse.getResponse();
//            }
//        }
//
//        // verify
//        assertEquals(TestUtils.RESPONSES_FOR_POST.get(url).getResponse(), response);
//    }
//
//    @Test
//    public void testPutSuccess() throws FileNotFoundException {
//        // prepare
//        String url = "/service/user";
//        String body = "{\"operation\":\"GET\"}";
//        Configuration conf = new Configuration(new String[]{"replay", "8080",
//                "src/test/resources/test_messages.txt"});
//        MessageFactory messageFactory = MessageFactory.newInstance(conf);
//
//        // execute
//        List<RequestResponse> messages = messageFactory.responsesPutByUrlAndBody();
//        Response response = null;
//        for (RequestResponse requestResponse : messages) {
//            if (requestResponse.getRequest().getUrl().equals(url)) {
//                response = requestResponse.getResponse();
//            }
//        }
//
//        // verify
//        assertEquals(TestUtils.RESPONSES_FOR_PUT.get(url).getResponse(), response);
//    }
//
//    @Test
//    public void testDeleteError() throws FileNotFoundException {
//        // prepare
//        String url = "/service/user";
//        Configuration conf = new Configuration(new String[]{"replay", "8080",
//                "src/test/resources/test_messages.txt"});
//        MessageFactory messageFactory = MessageFactory.newInstance(conf);
//
//        // execute
//        List<RequestResponse> messages = messageFactory.responsesDeleteByUrl();
//        Response response = null;
//        for (RequestResponse requestResponse : messages) {
//            if (requestResponse.getRequest().getUrl().equals(url)) {
//                response = requestResponse.getResponse();
//            }
//        }
//
//        // verify
//        assertEquals(TestUtils.RESPONSES_FOR_DELETE.get(url).getResponse(), response);
//    }
//}
