/**
 * Copyright (c) 2015-2022, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.components.valid;

import com.jfinal.kit.Ret;
import io.jboot.components.valid.interceptor.SimpleContext;
import org.hibernate.validator.HibernateValidator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

public class ValidUtil {

    /**
     * 验证器：用于数据验证
     */
    private static Validator validator = Validation.byProvider(HibernateValidator.class)
            .configure()
            .failFast(true)
            .buildValidatorFactory()
            .getValidator();

    private static int errorCode = 400;


    public static Validator getValidator() {
        return validator;
    }

    public static void setValidator(Validator validator) {
        ValidUtil.validator = validator;
    }


    public static int getErrorCode() {
        return errorCode;
    }

    public static void setErrorCode(int errorCode) {
        ValidUtil.errorCode = errorCode;
    }

    public static Set<ConstraintViolation<Object>> validate(Object object) {
        return validator.validate(object);
    }


    public static void throwValidException(String fieldName, String message, String reason) {
        throwValidException(fieldName, message, null, reason);
    }


    public static void throwValidException(String fieldName, String message, Ret paras, String reason) {
        if (message != null) {
            message = Validation.buildDefaultValidatorFactory().getMessageInterpolator().interpolate(message, new SimpleContext(paras));
        }

        if (fieldName != null) {
            message = fieldName + message;
        }

        throw new ValidException(message, reason);
    }
}
