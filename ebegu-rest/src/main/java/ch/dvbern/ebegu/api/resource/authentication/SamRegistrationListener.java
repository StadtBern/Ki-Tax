package ch.dvbern.ebegu.api.resource.authentication;

import org.omnifaces.security.jaspic.core.Jaspic;
import org.omnifaces.security.jaspic.listeners.BaseServletContextListener;

import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;

@WebListener
public class SamRegistrationListener extends BaseServletContextListener {

    @Override
	public void contextInitialized(ServletContextEvent sce) {
        Jaspic.registerServerAuthModule(new CookieTokenAuthModule(), sce.getServletContext());
    }
}
