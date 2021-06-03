package io.jboot.test.mvc;

import io.jboot.test.MockMvc;
import io.jboot.test.junit4.JbootRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

@RunWith(JbootRunner.class)
public class MockAppTester4 {

    private static MockMvc mvc = new MockMvc();


    @Test
    public void test_url_aaa() {
        Map<String, Object> paras = new HashMap<>();
        paras.put("p1","v1");
        paras.put("p2","v2");
        mvc.get("/mvc/aaa",paras).printResult()
                .assertThat(result -> Assert.assertEquals(result.getContent(),"aaa"))
                .assertTrue(result -> result.getStatus() == 200);
    }

    @Test
    public void test_url_bbb() {
        Map<String, Object> paras = new HashMap<>();
        paras.put("p1","v1");
        paras.put("p2","v2");
        mvc.post("/mvc/bbb",paras).printResult()
                .assertThat(result -> Assert.assertEquals(result.getContent(),"bbb"))
                .assertTrue(result -> result.getStatus() == 200);
    }

//    @Inject
//    private MyService myService;


//    @Test
//    public void test_my_service() {
//        Ret ret = myService.doSomeThing();
//        Assert.assertNotNull(ret);
//        //.....
//    }


}
