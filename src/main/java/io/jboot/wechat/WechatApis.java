/**
 * Copyright (c) 2015-2017, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jboot.wechat;

import com.jfinal.kit.Kv;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.MenuApi;
import io.jboot.Jboot;

public class WechatApis {


    public static ApiResult createMenu(String jsonString) {
        return MenuApi.createMenu(jsonString);
    }


    /**
     * 网页授权获取用户信息，必须是最新的token，才能获得完整的用户资料
     *
     * @param token
     * @param openId
     * @return
     */
    public static ApiResult getUserInfo(String token, String openId) {
        Kv pm = Kv.by("access_token", token).set("openid", openId).set("lang", "zh_CN");
        String jsonResult = Jboot.httpGet("https://api.weixin.qq.com/sns/userinfo", pm);

        if (jsonResult == null)
            return null;

        return new ApiResult(jsonResult);
    }

    /**
     * 获取微信的openId
     *
     * @param code
     * @return
     */
    public static ApiResult getAccessTokenAndOpenId(String code) {

        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?"
                + "appid={appid}"
                + "&secret={secret}"
                + "&code={code}"
                + "&grant_type=authorization_code";

        String getOpenIdUrl = url.replace("{appid}", ApiConfigKit.getAppId())
                .replace("{secret}", ApiConfigKit.getApiConfig().getAppSecret())
                .replace("{code}", code);

        String jsonResult = Jboot.httpGet(getOpenIdUrl);

        if (jsonResult == null)
            return null;

        return new ApiResult(jsonResult);
    }


}
