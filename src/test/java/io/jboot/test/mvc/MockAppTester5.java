package io.jboot.test.mvc;

import io.jboot.test.MockMvc;
import io.jboot.test.MockMvcResult;
import io.jboot.test.junit5.JbootExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(JbootExtension.class)
public class MockAppTester5 {

    private static MockMvc mvc = new MockMvc();

//    @Inject
//    private MyService myService;

    @Test
    public void test_url_aaa() {
        MockMvcResult mvcResult = mvc.get("/aaa");

        mvcResult.printResult()
                .assertThat(result -> Assertions.assertNotNull(result.getContent()))
                .assertTrue(result -> result.getStatus() == 200);
    }

    @Test
    public void test_url_bbb() {
        MockMvcResult mvcResult = mvc.get("/bbb");

        mvcResult.printResult()
                .assertThat(result -> Assertions.assertNotNull(result.getContent()))
                .assertTrue(result -> result.getStatus() == 200);
    }

//    @Test
//    public void test_my_service() {
//        Ret ret = myService.doSomeThing();
//        Assertions.assertNotNull(ret);
//        //.....
//    }
}
