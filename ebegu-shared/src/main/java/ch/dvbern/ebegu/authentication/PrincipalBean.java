package ch.dvbern.ebegu.authentication;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.security.Principal;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * ACHTUNG:  Das  injecten funktioniert anscheinend leider nicht
 */
@RequestScoped
public class PrincipalBean {


	@Inject
	private Principal principal;

	@Resource
	private EJBContext ejbContext;



	@Nonnull
	public Principal getPrincipal() {
		return principal;
	}


	public boolean isCallerInRole(@Nonnull String roleName) {
		checkNotNull(roleName);
		return ejbContext.isCallerInRole(roleName);
	}
}
