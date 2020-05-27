package io.jboot.web.render;

import javax.servlet.http.HttpServletResponse;

/**
 * Redirect301Render.
 */
public class JbootRedirect301Render extends JbootRedirectRender {
	
	public JbootRedirect301Render(String url) {
		super(url);
	}
	
	public JbootRedirect301Render(String url, boolean withQueryString) {
		super(url, withQueryString);
	}
	
	@Override
	public void render() {
		String finalUrl = buildFinalUrl();
		
		response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
		response.setHeader("Location", finalUrl);
	}
}






