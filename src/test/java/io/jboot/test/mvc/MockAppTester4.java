package io.jboot.test.mvc;

import io.jboot.test.MockMvc;
import io.jboot.test.junit4.JbootRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;

@RunWith(JbootRunner.class)
public class MockAppTester4 {

    private static MockMvc mvc = new MockMvc();

    @Before
    public void start() {
        System.out.println(">>>>>>>>app.start()....");
//        app.start();
    }

    @After
    public void stop() {
        System.out.println(">>>>>>>>app.stop()....");
//        app.stop();

//        assertThat
//        assertthat
    }

    @Test
    public void testRequest() {
        System.out.println(">>>>>>>>app.testRequest()....");
        mvc.get("/aaa").printResult()
                .assertThat(result -> Assertions.assertEquals(result.getHttpCode(), 300))
                .assertTrue(result -> result.getHttpCode() == 300);
    }

    @Test
    public void testOtherRequest() {
        System.out.println(">>>>>>>>app.testOtherRequest()....");
        mvc.get("/bbb").printResult();
    }
}
