package io.jboot.web.render;

import com.jfinal.kit.StrKit;
import com.jfinal.render.RedirectRender;

/**
 * RedirectRender with status: 302 Found.
 * 
 * 
 * 注意：使用 nginx 代理实现 https 的场景，解决 https 被重定向到了 http 的问题，需要在 nginx 中添加如下配置：
 *      proxy_set_header X-Forwarded-Proto $scheme;
 *      proxy_set_header X-Forwarded-Port $server_port;
 *      
 *      
 * PS：nginx 将 http 重定向到 https 的配置为：
 *     proxy_redirect http:// https://;
 *     注意: 需要同时支持 http 与 https 的场景不能使用该配置
 *     
 */
public class JbootRedirectRender extends RedirectRender {
	
	public JbootRedirectRender(String url) {
		super(url);
	}
	
	public JbootRedirectRender(String url, boolean withQueryString) {
		super(url, withQueryString);
	}
	
	@Override
	public String buildFinalUrl() {
		String ret;
		// 如果一个url为/login/connect?goto=http://www.jfinal.com，则有错误
		// ^((https|http|ftp|rtsp|mms)?://)$   ==> indexOf 取值为 (3, 5)
		if (contextPath != null && (url.indexOf("://") == -1 || url.indexOf("://") > 5)) {
			ret = contextPath + url;
		} else {
			ret = url;
		}
		
		if (withQueryString) {
			String queryString = request.getQueryString();
			if (queryString != null) {
				if (ret.indexOf('?') == -1) {
					ret = ret + "?" + queryString;
				} else {
					ret = ret + "&" + queryString;
				}
			}
		}
		
		// 跳过 http/https 已指定过协议类型的 url，用于支持跨域名重定向
		if (ret.toLowerCase().startsWith("http")) {
			return ret;
		}
		
		/**
		 * 注意：nginx 代理 https 的场景，需要使用如下配置:
		 *       proxy_set_header X-Forwarded-Proto $scheme;
		 *       proxy_set_header X-Forwarded-Port $server_port;
		 */
		if ("https".equalsIgnoreCase(request.getHeader("X-Forwarded-Proto"))) {
			String serverName = request.getServerName();
			
			/**
			 * 获取 nginx 端通过配置 proxy_set_header X-Forwarded-Port $server_port;
			 * 传递过来的端口号，保障重定向时端口号是正确的
			 */
			String port = request.getHeader("X-Forwarded-Port");
			if (StrKit.notBlank(port)) {
				serverName = serverName + ":" + port;
			}
			
			if (ret.charAt(0) != '/') {
				return "https://" + serverName + "/" + ret;
			} else {
				return "https://" + serverName + ret;
			}
			
		} else {
			return ret;
		}
	}
}








