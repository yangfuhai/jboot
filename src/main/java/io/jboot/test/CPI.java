package io.jboot.test;

import io.jboot.test.web.MockHttpServletRequest;
import io.jboot.test.web.MockHttpServletResponse;

public class CPI {

    public static void startApp(Class<?> testClass) {
        MockApp.getInstance().start(testClass);
    }

    public static void stopApp() {
        MockApp.getInstance().stop();
    }

    public static void mockRequest(MockHttpServletRequest request, MockHttpServletResponse response) {
        MockApp.mockRequest(request, response);
    }

}
