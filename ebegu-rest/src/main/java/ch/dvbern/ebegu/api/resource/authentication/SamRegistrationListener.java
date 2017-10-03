package ch.dvbern.ebegu.api.resource.authentication;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;

import ch.dvbern.ebegu.config.EbeguConfiguration;
import org.omnifaces.security.jaspic.core.Jaspic;
import org.omnifaces.security.jaspic.listeners.BaseServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class SamRegistrationListener extends BaseServletContextListener {

	private static final Logger LOG = LoggerFactory.getLogger(SamRegistrationListener.class);

	@Inject
	private EbeguConfiguration configuration;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		final CookieTokenAuthModule authModule;
		String intUsr = configuration.getInternalAPIUser();
		String intPW = configuration.getInternalAPIPassword();
		//either construct the module with or without password for external login
		if (intUsr == null && intPW == null) {
			authModule = new CookieTokenAuthModule();
			LOG.debug("No user or password for internal api configured, the api will be inactive");
		} else {
			authModule = new CookieTokenAuthModule(intUsr, intPW);
		}

		Jaspic.registerServerAuthModule(authModule, sce.getServletContext());
	}

}
