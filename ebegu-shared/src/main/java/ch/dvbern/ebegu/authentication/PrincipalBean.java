package ch.dvbern.ebegu.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.TransactionSynchronizationRegistry;
import java.security.Principal;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * ACHTUNG:  Das  injecten funktioniert anscheinend leider nicht
 */
@RequestScoped
public class PrincipalBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(PrincipalBean.class);


	@Inject
	private Principal principal;

	@Resource
	private EJBContext ejbContext;

	@Resource
	private TransactionSynchronizationRegistry txReg;


	@Nonnull
	public Principal getPrincipal() {
		return principal;
	}

	public boolean isCallerInRole(@Nonnull String roleName) {
		LOGGER.debug("isCallerInRole 1: {}/{}", txReg.getTransactionKey(), txReg.getRollbackOnly());
		checkNotNull(roleName);
		return ejbContext.isCallerInRole(roleName);
	}
}
