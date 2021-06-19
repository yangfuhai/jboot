/**
 * Copyright (c) 2015-2021, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jboot.support.jwt;

import com.jfinal.core.Controller;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.LogKit;
import com.jfinal.log.Log;
import io.jboot.Jboot;
import io.jboot.exception.JbootIllegalConfigException;
import io.jboot.utils.StrUtil;
import io.jsonwebtoken.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
public class JwtManager {

    private static final JwtManager me = new JwtManager();
    private static final Log LOG = Log.getLog(JwtManager.class);
    public static final Map EMPTY_MAP = new HashMap();
    private static JwtConfig config = Jboot.config(JwtConfig.class);


    public static JwtManager me() {
        return me;
    }

    public String getHttpHeaderName() {
        return config.getHttpHeaderName();
    }

    public String getHttpParameterKey() {
        return config.getHttpParameterKey();
    }

    /**
     * 通过 Controller 解析 Map
     *
     * @param controller 控制器
     * @return 所有 JWT 数据
     */
    public Map parseJwtToken(Controller controller) {

        if (!config.isConfigOk()) {
            LogKit.debug("Jwt secret not config well, please config jboot.web.jwt.secret in jboot.properties.");
            return EMPTY_MAP;
        }

        String token = controller.getHeader(getHttpHeaderName());

        if (StrUtil.isBlank(token) && StrUtil.isNotBlank(getHttpParameterKey())) {
            token = controller.get(getHttpParameterKey());
        }

        return StrUtil.isBlank(token) ? EMPTY_MAP : parseJwtToken(token);
    }


    /**
     * 解析 JWT Token 内容
     *
     * @param token 加密的 token
     * @return 返回 JWT 的 MAP 数据
     */
    public Map parseJwtToken(String token) {
        SecretKey secretKey = createSecretKey();
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token).getBody();

            String jsonString = claims.getSubject();
            if (StrUtil.isNotBlank(jsonString)) {
                return JsonKit.parse(jsonString, HashMap.class);
            }
        } catch (SignatureException | MalformedJwtException ex) {
            // don't trust the JWT!
            // jwt 签名错误或解析错误，可能是伪造的，不能相信
            LOG.error("Do not trast the jwt. " + ex.getMessage());
        } catch (ExpiredJwtException ex) {
            // jwt 已经过期
            LOG.error("Jwt is expired. " + ex.getMessage());
        } catch (Exception ex) {
            //其他错误
            LOG.error("Jwt parseJwtToken error. " + ex.getMessage());
        }

        return EMPTY_MAP;
    }


    public String createJwtToken(Map map) {

        if (!config.isConfigOk()) {
            throw new JbootIllegalConfigException("Can not create jwt, please config jboot.web.jwt.secret in jboot.properties.");
        }

        SecretKey secretKey = createSecretKey();

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //追加保存 JWT 的生成时间
        map.put(JwtInterceptor.ISUUED_AT, nowMillis);
        String subject = JsonKit.toJson(map);

        JwtBuilder builder = Jwts.builder()
                .setIssuedAt(now)
                .setSubject(subject)
                .signWith(signatureAlgorithm, secretKey);

        if (config.getValidityPeriod() > 0) {
            long expMillis = nowMillis + config.getValidityPeriod();
            builder.setExpiration(new Date(expMillis));
        }

        return builder.compact();
    }


    private SecretKey createSecretKey() {
        byte[] encodedKey = DatatypeConverter.parseBase64Binary(config.getSecret());
        return new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
    }

    public static JwtConfig getConfig() {
        return config;
    }

    public static void setConfig(JwtConfig config) {
        JwtManager.config = config;
    }
}
