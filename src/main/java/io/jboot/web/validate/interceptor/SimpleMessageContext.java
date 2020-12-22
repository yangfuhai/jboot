package io.jboot.web.validate.interceptor;

import javax.validation.ConstraintTarget;
import javax.validation.ConstraintValidator;
import javax.validation.MessageInterpolator;
import javax.validation.Payload;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.ValidateUnwrappedValue;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SimpleMessageContext implements MessageInterpolator.Context{

    private Map<String, Object> attrs = new HashMap();
    private ConstraintDescriptor constraintDescriptor = new ConstraintDescriptor() {
        @Override
        public Annotation getAnnotation() {
            return null;
        }

        @Override
        public String getMessageTemplate() {
            return null;
        }

        @Override
        public Set<Class<?>> getGroups() {
            return null;
        }

        @Override
        public Set<Class<? extends Payload>> getPayload() {
            return null;
        }

        @Override
        public ConstraintTarget getValidationAppliesTo() {
            return null;
        }

        @Override
        public List<Class<? extends ConstraintValidator>> getConstraintValidatorClasses() {
            return null;
        }

        @Override
        public Map<String, Object> getAttributes() {
            return attrs;
        }

        @Override
        public Set<ConstraintDescriptor<?>> getComposingConstraints() {
            return null;
        }

        @Override
        public boolean isReportAsSingleViolation() {
            return false;
        }

        @Override
        public ValidateUnwrappedValue getValueUnwrapping() {
            return null;
        }

        @Override
        public Object unwrap(Class type) {
            return null;
        }
    };

    public SimpleMessageContext() {
    }

    public SimpleMessageContext(Map<String, Object> attrs) {
        if (attrs != null){
            this.attrs.putAll(attrs);
        }
    }


    @Override
    public ConstraintDescriptor<?> getConstraintDescriptor() {
        return constraintDescriptor;
    }

    @Override
    public Object getValidatedValue() {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> type) {
        return null;
    }


}
