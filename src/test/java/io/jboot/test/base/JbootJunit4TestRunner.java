package io.jboot.test.base;

import com.jfinal.aop.Aop;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public class JbootJunit4TestRunner extends BlockJUnit4ClassRunner {
    public JbootJunit4TestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected Object createTest() throws Exception {
        return Aop.get(this.getTestClass().getJavaClass());
    }
}
