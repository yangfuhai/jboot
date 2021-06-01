package io.jboot.test.mvc;

import io.jboot.test.MockMvc;
import io.jboot.test.junit5.JbootExtension;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(JbootExtension.class)
public class MockAppTester5 {

    private static MockMvc app = new MockMvc();

    @BeforeAll
    public static void start() {
        System.out.println(">>>>>>>>app.start()....");
//        app.start();
    }

    @AfterAll
    public static void stop(){
        System.out.println(">>>>>>>>app.stop()....");
//        app.stop();
    }

    @Test
    public void testRequest(){
        System.out.println(">>>>>>>>app.testRequest()....");
        app.get("/aaa");
    }

    @Test
    public void testOtherRequest(){
        System.out.println(">>>>>>>>app.testOtherRequest()....");
        app.post("/bbb");
    }
}
