package ch.dvbern.ebegu.api.resource.authentication;

import ch.dvbern.ebegu.config.EbeguConfiguration;
import org.omnifaces.security.jaspic.core.Jaspic;
import org.omnifaces.security.jaspic.listeners.BaseServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;
import java.io.File;

@WebListener
public class SamRegistrationListener extends BaseServletContextListener {

	private static final Logger LOG = LoggerFactory.getLogger(SamRegistrationListener.class);
	private static final String FEDLET_CONFIGURATION_FOLDER = "com.sun.identity.fedlet.home";

	@Inject
	EbeguConfiguration configuration;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		setConfigurationLocationForFedlet(sce);

		Jaspic.registerServerAuthModule(new CookieTokenAuthModule(), sce.getServletContext());
	}

	/**
	 * Das Fedlet erwartet dummerweise einen Ordner mit Konfigurationsfiles der ueber dessen Pfad im Filesystem
	 * mit dem Property com.sun.identity.fedlet.home angegeben wird
	 * JBoss entpackt das war in ein temp folder. Wir lesen hier die location des wars damit wir die configuration
	 * in der Applikation mitdeployen koennen
	 */
	private void setConfigurationLocationForFedlet(ServletContextEvent sce) {

		String pathoToFedletConfig = configuration.getFedletConfigPath();
		String fullPath = sce.getServletContext().getRealPath("/WEB-INF/classes/" + pathoToFedletConfig);
		File fedletConfFolder = new File(fullPath);
		if (fedletConfFolder.exists() && fedletConfFolder.isDirectory()) {
			System.setProperty(FEDLET_CONFIGURATION_FOLDER, fullPath);
		} else {
			LOG.info("Could not load configuration path for Fedlet. '" + fullPath + "'. SAML will use default config folder location");
		}

	}
}
