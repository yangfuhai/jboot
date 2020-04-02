package io.jboot.components.restful;

import com.jfinal.core.ActionException;
import com.jfinal.log.Log;
import com.jfinal.render.Render;
import com.jfinal.render.RenderException;
import com.jfinal.render.RenderManager;
import io.jboot.components.restful.exception.ParameterNullErrorException;
import io.jboot.components.restful.exception.ParameterParseErrorException;
import io.jboot.components.restful.exception.RequestMethodErrorException;
import io.jboot.web.handler.JbootActionHandler;

import java.io.Serializable;

/**
 * 默认的restful错误响应处理器
 */
public class DefaultRestfulErrorRender extends RestfulErrorRender {

    private static final Log log = Log.getLog(JbootActionHandler.class);

    public static class Error implements Serializable {
        private String errorClass;
        private int code;
        private String message;

        public Error(String errorClass, int code, String message) {
            this.errorClass = errorClass;
            this.code = code;
            this.message = message;
        }

        public String getErrorClass() {
            return errorClass;
        }

        public Error setErrorClass(String errorClass) {
            this.errorClass = errorClass;
            return this;
        }

        public int getCode() {
            return code;
        }

        public Error setCode(int code) {
            this.code = code;
            return this;
        }

        public String getMessage() {
            return message;
        }

        public Error setMessage(String message) {
            this.message = message;
            return this;
        }
    }

    public void render() {
        log.error("The restful handler intercepted the error", super.getError());
        Error error = null;
        if(super.getError() instanceof ParameterNullErrorException
            || super.getError() instanceof ParameterParseErrorException){
            //400
            error = new Error(super.getError().getClass().getName(),
                    HttpStatus.BAD_REQUEST.value(), super.getError().getMessage());
        } else if(super.getError() instanceof RequestMethodErrorException){
            error = new Error(super.getError().getClass().getName(),
                    HttpStatus.METHOD_NOT_ALLOWED.value(), super.getError().getMessage());
        } else if(super.getError() instanceof ActionException){
            //解析错误代码
            ActionException actionException = (ActionException)super.getError();
            int errorCode = actionException.getErrorCode();
            String msg = "";
            if (errorCode == 404) {
                msg = HttpStatus.NOT_FOUND.getReasonPhrase();
            } else if (errorCode == 400) {
                msg = HttpStatus.BAD_REQUEST.getReasonPhrase();
            } else if (errorCode == 401) {
                msg = HttpStatus.UNAUTHORIZED.getReasonPhrase();
            } else if (errorCode == 403) {
                msg = HttpStatus.FORBIDDEN.getReasonPhrase();
            } else if(errorCode == 405){
                msg = HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase();
            } else {
                msg = super.getError().getMessage();
            }
            error = new Error(super.getError().getClass().getName(),
                    errorCode, msg);
        } else if(super.getError() instanceof RenderException){
            //500
            error = new Error(super.getError().getClass().getName(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), super.getError().getMessage());
        } else {
            //500
            error = new Error(super.getError().getClass().getName(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), super.getError().getMessage());
        }
        Render jsonRender = RenderManager.me().getRenderFactory().getJsonRender(error);
        jsonRender.setContext(super.request, super.response, super.getAction() == null? "" : super.getAction().getViewPath());
        super.response.setStatus(error.getCode());
        jsonRender.render();
    }



}
