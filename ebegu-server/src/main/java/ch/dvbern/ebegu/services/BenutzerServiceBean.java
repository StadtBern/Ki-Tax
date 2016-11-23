package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.authentication.PrincipalBean;
import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Benutzer_;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.security.PermitAll;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.security.Principal;
import java.util.*;

/**
 * Service fuer Benutzer
 */
@PermitAll
@Stateless
@Local(BenutzerService.class)
public class BenutzerServiceBean extends AbstractBaseService implements BenutzerService {

	public static final String ID_SUPER_ADMIN = "22222222-2222-2222-2222-222222222222";

	@Inject
	private Persistence<Benutzer> persistence;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;

	@Inject
	private PrincipalBean principalBean;


	@Nonnull
	@Override
	public Benutzer saveBenutzer(@Nonnull Benutzer benutzer) {
		Objects.requireNonNull(benutzer);
		return persistence.merge(benutzer);
	}

	@Nonnull
	@Override
	public Optional<Benutzer> findBenutzer(@Nonnull String username) {
		Objects.requireNonNull(username, "username muss gesetzt sein");
		return criteriaQueryHelper.getEntityByUniqueAttribute(Benutzer.class, username, Benutzer_.username);
	}

	@Nonnull
	@Override
	public Collection<Benutzer> getAllBenutzer() {
		return new ArrayList<>(criteriaQueryHelper.getAll(Benutzer.class));
	}

	@Nonnull
	@Override
	public Collection<Benutzer> getBenutzerJAorAdmin() {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Benutzer> query = cb.createQuery(Benutzer.class);
		Root<Benutzer> root = query.from(Benutzer.class);
		query.select(root);
		Predicate isAdmin = cb.equal(root.get(Benutzer_.role), UserRole.ADMIN);
		Predicate isSachbearbeiterJA = cb.equal(root.get(Benutzer_.role), UserRole.SACHBEARBEITER_JA);
		Predicate orRoles = cb.or(isAdmin, isSachbearbeiterJA);
		query.where(orRoles);
		return persistence.getCriteriaResults(query);
	}


	@Override
	public void removeBenutzer(@Nonnull String username) {
		Objects.requireNonNull(username);
		Optional<Benutzer> benutzerToRemove = findBenutzer(username);
		benutzerToRemove.orElseThrow(() -> new EbeguEntityNotFoundException("removeBenutzer", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, username));
		persistence.remove(benutzerToRemove.get());
	}

	@Nonnull
	@Override
	public Optional<Benutzer> getCurrentBenutzer() {
		String username = null;
		if (principalBean != null) {
			final Principal principal = principalBean.getPrincipal();
			username = principal.getName();
		}
		if (StringUtils.isNotEmpty(username)) {
			if ("anonymous".equals(username) && principalBean.isCallerInRole(UserRole.SUPER_ADMIN.name())) {
				return loadSuperAdmin();
			}
			return findBenutzer(username);
		}
		return Optional.empty();
	}

	@Override
	public Benutzer updateOrStoreUserFromIAM(Benutzer benutzer) {
		Optional<Benutzer> foundUser = this.findBenutzer(benutzer.getUsername());
		if (foundUser.isPresent()) {
            // Unsere Metadaten werden in das IAM Objekt kopiert und dieses wird gespeichert
			benutzer.setId(foundUser.get().getId());
			benutzer.setVersion(foundUser.get().getVersion()); //we circumveil the optimistic locking and just save the new version
			benutzer.setTimestampErstellt(foundUser.get().getTimestampErstellt());
			//noinspection ConstantConditions
			benutzer.setUserErstellt(foundUser.get().getUserErstellt());

		}
		return this.saveBenutzer(benutzer);
	}

	private Optional<Benutzer> loadSuperAdmin() {
		Benutzer benutzer = persistence.find(Benutzer.class, ID_SUPER_ADMIN);
		return Optional.ofNullable(benutzer);
	}
}
