package io.jboot.web.limitation;

import com.google.common.util.concurrent.RateLimiter;
import io.jboot.Jboot;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.web.limitation
 */
public class LimitationManager {

    private static final LimitationManager me = new LimitationManager();

    public static LimitationManager me() {
        return me;
    }

    private Map<String, RateLimiter> requestRateLimiterMap = new ConcurrentHashMap<>();
    private Map<String, Object> ajaxJsonMap = new HashMap();
    private String limitView;

    private LimitationManager() {
        LimitationConfig config = Jboot.config(LimitationConfig.class);
        ajaxJsonMap.put("code", config.getLimitAjaxCode());
        ajaxJsonMap.put("message", config.getLimitAjaxMessage());

        this.limitView = config.getLimitView();
    }


    public RateLimiter initRateLimiter(String target, double rate) {
        RateLimiter limiter = requestRateLimiterMap.get(target);
        if (limiter == null) {
            limiter = RateLimiter.create(rate);
            requestRateLimiterMap.put(target, limiter);
            return limiter;
        }

        if (limiter.getRate() == rate) {
            return limiter;
        }

        limiter.setRate(rate);
        requestRateLimiterMap.put(target, limiter);

        return limiter;
    }


    public RateLimiter getLimiter(String target) {
        return requestRateLimiterMap.get(target);
    }

    public Map<String, Object> getAjaxJsonMap() {
        return ajaxJsonMap;
    }

    public String getLimitView() {
        return limitView;
    }
}
