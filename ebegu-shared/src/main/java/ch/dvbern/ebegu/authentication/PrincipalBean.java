package ch.dvbern.ebegu.authentication;

import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Mandant;
import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.services.BenutzerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.TransactionSynchronizationRegistry;
import java.security.Principal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
 	private SessionContext sessionContext;

	@Inject
	private BenutzerService benutzerService;
	@Resource
	private TransactionSynchronizationRegistry txReg;


	private Benutzer benutzer = null;
	private Mandant mandant = null;

//	@PostConstruct
//	private  void init(){
//		loadNormalUser();
//
//	}

	private void loadNormalUser() {
		String name = principal.getName();
		benutzer = benutzerService.findBenutzer(name)
			.orElseThrow(() -> new IllegalStateException("Could not find Benutzer with username " + name));
		mandant = benutzer.getMandant();

	}

	@Nonnull
	public Benutzer getBenutzer() {
		if (benutzer == null || !benutzer.getUsername().equals(principal.getName())) {
			loadNormalUser();
		}
		return benutzer;
	}

	@Nullable
	public Mandant getMandant() {
		if (mandant == null || !getBenutzer().getUsername().equals(principal.getName())) {
			mandant = getBenutzer().getMandant();
		}
		return mandant;
	}


	public Set<String> discoverRoles() {
		Set<String> roleNames = new HashSet<>();
		Arrays.stream(UserRole.values()).map(Enum::name).forEach(roleName -> {
			if (sessionContext.isCallerInRole(roleName)){
				roleNames.add(roleName);
			}
		});

		return roleNames;
	}

	@Nonnull
	public Principal getPrincipal() {
		return principal;
	}

	public boolean isCallerInRole(@Nonnull String roleName) {
		LOGGER.trace("isCallerInRole: {}/{}", txReg.getTransactionKey(), txReg.getRollbackOnly());
		checkNotNull(roleName);
		return sessionContext.isCallerInRole(roleName);
	}

	public boolean isCallerInAnyOfRole(@Nonnull String... roleNames) {
		checkNotNull(roleNames);
		return Arrays.stream(roleNames).anyMatch(this::isCallerInRole);
	}

	public boolean isCallerInAnyOfRole(@Nonnull UserRole... role) {
		checkNotNull(role);
		return Arrays.stream(role).map(Enum::name).anyMatch(this::isCallerInRole);
	}

	public boolean isCallerInRole(@Nonnull UserRole role) {
		checkNotNull(role);
		return this.isCallerInRole(role.name());
	}
}
