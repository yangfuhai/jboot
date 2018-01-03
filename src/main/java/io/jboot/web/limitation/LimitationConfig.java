package io.jboot.web.limitation;

import io.jboot.config.annotation.PropertyConfig;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.web.limitation
 */
@PropertyConfig(prefix = "jboot.limitation")
public class LimitationConfig {

    private int limitAjaxCode = 886;
    private String limitAjaxMessage = "request limit";

    private String limitView;


    public int getLimitAjaxCode() {
        return limitAjaxCode;
    }

    public void setLimitAjaxCode(int limitAjaxCode) {
        this.limitAjaxCode = limitAjaxCode;
    }

    public String getLimitAjaxMessage() {
        return limitAjaxMessage;
    }

    public void setLimitAjaxMessage(String limitAjaxMessage) {
        this.limitAjaxMessage = limitAjaxMessage;
    }

    public String getLimitView() {
        return limitView;
    }

    public void setLimitView(String limitView) {
        this.limitView = limitView;
    }
}
