import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import io.jboot.Jboot;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HystrixTest {
    /**
     * 查看监控数据，通过docker来运行hystrix-dashboard
     * docker run --rm -ti -p 7979:7979 kennedyoliveira/hystrix-dashboard
     *
     * 然后访问：http://127.0.0.1:7979/hystrix-dashboard 并填写观测的url
     *
     * @param args
     */
    public static void main(String[] args) {
        Jboot.setBootArg("jboot.hystrix.url", "/hystrix.stream");
        Jboot.run(args);


        while (true) {
            try {
             String hello = new CommandHelloWorld1("World").execute();
            }catch (Throwable ex){}
        }


//        while (true){
//            String helloString = new HystrixTest().getHello();
//            System.out.println(helloString);
//        }

    }



    public String getHello(){
        return "hello";
    }




    @Test
    public void testHello() {
        CommandHelloWorld cmd = new CommandHelloWorld("World");
        System.out.println(cmd.execute());

        assertEquals("Hello World!", new CommandHelloWorld("World").execute());
        assertEquals("Hello Bob!", new CommandHelloWorld("Bob").execute());
    }


    public static class CommandHelloWorld extends HystrixCommand<String> {

        private final String name;

        public CommandHelloWorld(String name) {
            super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
            this.name = name;
        }

        @Override
        protected String run() {
            // a real example would do work like a network call here


            return "Hello " + name + "!";
        }
    }

    public static class CommandHelloWorld1 extends HystrixCommand<String> {

        private final String name;

        public CommandHelloWorld1(String name) {
            super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
            this.name = name;
        }

        @Override
        protected String run() {
            // a real example would do work like a network call here
            try {
                Thread.sleep((int) (Math.random() * 10) + 2);
            } catch (InterruptedException e) {
                // do nothing
            }

        /* fail 20% of the time to show how fallback works */
            if (Math.random() > 0.80) {
                throw new RuntimeException("random failure processing UserAccount network response");
            }

        /* latency spike 20% of the time so timeouts can be triggered occasionally */
            if (Math.random() > 0.80) {
                // random latency spike
                try {
                    Thread.sleep((int) (Math.random() * 300) + 25);
                } catch (InterruptedException e) {
                    // do nothing
                }
            }


            return "Hello " + name + "!";
        }

    }
}


