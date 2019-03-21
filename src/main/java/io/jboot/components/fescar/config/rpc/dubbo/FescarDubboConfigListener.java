package io.jboot.components.fescar.config.rpc.dubbo;

import com.jfinal.config.Constants;
import com.jfinal.config.Interceptors;
import com.jfinal.config.Routes;
import com.jfinal.log.Log;
import com.jfinal.template.Engine;

import io.jboot.aop.jfinal.JfinalHandlers;
import io.jboot.aop.jfinal.JfinalPlugins;
import io.jboot.components.fescar.annotation.JbootGlobalTransactionManager;
import io.jboot.components.fescar.annotation.JbootGlobalTransactionalInterceptor;
import io.jboot.components.fescar.config.rpc.dubbo.interceptor.JbootTransactionPropagationInterceptor;
import io.jboot.core.listener.JbootAppListener;
import io.jboot.web.fixedinterceptor.FixedInterceptors;

public class FescarDubboConfigListener implements JbootAppListener {

	public void onInit() {
		
	}

	public void onConstantConfig(Constants constants) {
		// TODO Auto-generated method stub

	}

	public void onRouteConfig(Routes routes) {
		// TODO Auto-generated method stub

	}

	public void onEngineConfig(Engine engine) {
		// TODO Auto-generated method stub

	}

	public void onPluginConfig(JfinalPlugins plugins) {
		// TODO Auto-generated method stub

	}

	public void onInterceptorConfig(Interceptors interceptors) {
		interceptors.addGlobalActionInterceptor(new JbootTransactionPropagationInterceptor());
interceptors.addGlobalServiceInterceptor(new JbootGlobalTransactionalInterceptor(null));
	}

	public void onFixedInterceptorConfig(FixedInterceptors fixedInterceptors) {
		// TODO Auto-generated method stub

	}

	public void onHandlerConfig(JfinalHandlers handlers) {
		// TODO Auto-generated method stub

	}

	public void onStartBefore() {
		// TODO Auto-generated method stub

	}

	public void onStart() {
		JbootGlobalTransactionManager jbootGlobalTransactionManager = new JbootGlobalTransactionManager("Dubbo_Fescar_Account_Service",
				"dubbo_fescar_tx_group");
		Log log = Log.getLog(FescarDubboConfigListener.class);
		jbootGlobalTransactionManager.init();
		log.debug("GlobalTransactionScanner init" + jbootGlobalTransactionManager);


		}

	public void onStop() {
		// TODO Auto-generated method stub

	}

}
