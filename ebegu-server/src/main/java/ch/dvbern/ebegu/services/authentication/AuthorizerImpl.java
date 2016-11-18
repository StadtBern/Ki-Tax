package ch.dvbern.ebegu.services.authentication;

import ch.dvbern.ebegu.authentication.PrincipalBean;
import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.services.Authorizer;
import ch.dvbern.ebegu.services.FallService;
import ch.dvbern.ebegu.services.InstitutionService;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.ejb.EJBAccessException;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Optional;

/**
 * Authorizer Implementation
 */
@RequestScoped
public class AuthorizerImpl implements Authorizer {

	private static final Logger LOG = LoggerFactory.getLogger(AuthorizerImpl.class);

	@Inject
	private PrincipalBean principalBean;

	@Inject
	private Persistence<Gesuch> persistence;

	@Inject
	private FallService fallService;

	@Inject
	private InstitutionService institutionService;

	@Override
	public void checkReadAuthorizationGesuchId(@Nullable String gesuchId) {
		if (gesuchId != null) {
			LOG.warn("homa: Ineffiziente Authorisierungspruefung. Gesuchid sollte moeglichst nicht verwendet werden");
			checkReadAuthorization(getGesuchById(gesuchId));

		}
	}

	@Override
	public void checkReadAuthorization(@Nullable Gesuch gesuch) {
		if (gesuch != null) {
			boolean allowed = isReadAuthorized(gesuch);
			if (!allowed) {
				throwViolation(gesuch);
			}
		}
	}

	@Override
	public void checkCreateAuthorizationGesuch() {
		if (principalBean.isCallerInAnyOfRole(UserRole.GESUCHSTELLER, UserRole.SACHBEARBEITER_JA, UserRole.ADMIN, UserRole.SUPER_ADMIN)) {
			return;
		}
		throwCreateViolation();
	}

	@Override
	public void checkReadAuthorizationFall(String fallId) {
		Optional<Fall> fallOptional = fallService.findFall(fallId);
		if (fallOptional.isPresent()) {
			Fall fall = fallOptional.get();
			checkReadAuthorizationFall(fall);
		}
	}

	@Override
	public void checkReadAuthorizationFall(Fall fall) {
		boolean allowed = isReadAuthorizedFall(fall);

		if (!allowed) {
			throwViolation(fall);
		}

	}

	@Override
	public void checkReadAuthorizationFaelle(Collection<Fall> faelle) {
		if (faelle != null) {
			faelle.forEach(this::checkReadAuthorizationFall);
		}
	}

	private boolean isReadAuthorizedFall(Fall fall) {
		validateMandantMatches(fall);
		if (principalBean.isCallerInAnyOfRole(UserRole.SUPER_ADMIN, UserRole.ADMIN, UserRole.SACHBEARBEITER_JA, UserRole.SACHBEARBEITER_TRAEGERSCHAFT, UserRole.SACHBEARBEITER_INSTITUTION)) {
			return true;
		}

		if (principalBean.isCallerInRole(UserRole.GESUCHSTELLER.name())
			&& (fall.getUserErstellt() != null && fall.getUserErstellt().equals(principalBean.getPrincipal().getName()))) {
			return true;
		}
		return false;
	}

	private boolean isSachbearbeiterJAOrGSOwner(AbstractEntity entity, String principalName) {
		if (principalBean.isCallerInAnyOfRole(UserRole.SUPER_ADMIN, UserRole.ADMIN, UserRole.SACHBEARBEITER_JA)) {
			return true;
		}

		if (principalBean.isCallerInRole(UserRole.GESUCHSTELLER.name())
			&& (entity.getUserErstellt() != null && entity.getUserErstellt().equals(principalName))) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("PMD.CollapsibleIfStatements")
	private void validateMandantMatches(HasMandant mandantEntity) {
		Mandant mandant = mandantEntity.getMandant();
		if (mandant == null) {
			return;
		}
		if (!mandant.equals(principalBean.getMandant())) {
			if (!principalBean.isCallerInRole(UserRole.SUPER_ADMIN)) {
				throwMandantViolation(mandantEntity); // super admin darf auch wenn er keinen mandant hat
			}

		}
	}

	@Override
	public void checkWriteAuthorization(@Nullable Fall fall) {
		if (fall != null) {
			boolean allowed = isWriteAuthorized(fall, principalBean.getPrincipal().getName());
			if (!allowed) {
				throwViolation(fall);
			}
		}
	}

	@Override
	@SuppressWarnings("PMD.CollapsibleIfStatements")
	public void checkWriteAuthorizationFall(String fallId) {
		Optional<Fall> fallOptional = fallService.findFall(fallId);
		if (!fallOptional.isPresent()) {
			return;
		}
		Fall fall = fallOptional.get();
		checkWriteAuthorization(fall);
	}

	@Override
	public void checkWriteAuthorization(Gesuch gesuch) throws EJBAccessException {
		boolean allowed = isWriteAuthorized(gesuch, principalBean.getPrincipal().getName());
		if (!allowed) {
			throwViolation(gesuch);
		}
	}

	@Override
	public void checkWriteAuthorization(Verfuegung verfuegung) {
		//nur sachbearbeiter ja und admins duefen verfuegen
		if (!principalBean.isCallerInAnyOfRole(UserRole.SUPER_ADMIN, UserRole.ADMIN, UserRole.SACHBEARBEITER_JA)) {
			throwViolation(verfuegung);
		}

	}

	@Override
	public void checkReadAuthorization(Betreuung betr) {
		boolean allowed = isReadAuthorized(betr);
		if (!allowed) {
			throwViolation(betr);
		}
	}


	@Override
	public void checkReadAuthorizationBetreuungen(@Nullable Collection<Betreuung> betreuungen) {
		if (betreuungen != null) {
			betreuungen.stream()
				.filter(betreuung -> !isReadAuthorized(betreuung))
				.findAny()
				.ifPresent(this::throwViolation);
		}
	}


	@Override
	public void checkReadAuthorization(Verfuegung verfuegung) {
		if (verfuegung != null) {
			//an betreuung delegieren
			checkReadAuthorization(verfuegung.getBetreuung());
		}
	}

	@Override
	public void checkReadAuthorizationVerfuegungen(Collection<Verfuegung> verfuegungen) {
		if (verfuegungen != null) {
			verfuegungen.forEach(this::checkReadAuthorization);
		}
	}

	@Override
	public void checkWriteAuthorization(Betreuung betreuungToRemove) {
		boolean allowed = isWriteAuthorized(betreuungToRemove, principalBean.getPrincipal().getName());
		if (!allowed) {
			throwViolation(betreuungToRemove);
		}
	}


	private boolean isReadAuthorized(Betreuung betreuung) {
		boolean isOwnerOrAdmin = isSachbearbeiterJAOrGSOwner(betreuung, principalBean.getPrincipal().getName());
		if (isOwnerOrAdmin) {
			return true;
		}

		if (principalBean.isCallerInRole(UserRole.SACHBEARBEITER_INSTITUTION)) {
			Institution institution = principalBean.getBenutzer().getInstitution();
			Validate.notNull(institution, "Institution des Sachbearbeiters muss gesetzt sein " + principalBean.getBenutzer());
			return betreuung.getInstitutionStammdaten().getInstitution().equals(institution);
		}
		if (principalBean.isCallerInRole(UserRole.SACHBEARBEITER_TRAEGERSCHAFT)) {
			Traegerschaft traegerschaft = principalBean.getBenutzer().getTraegerschaft();
			Validate.notNull(traegerschaft, "Traegerschaft des des Sachbearbeiters muss gesetzt sein " + principalBean.getBenutzer());
			Collection<Institution> institutions = institutionService.getAllInstitutionenFromTraegerschaft(traegerschaft.getId());
			Institution instToMatch = betreuung.getInstitutionStammdaten().getInstitution();
			return institutions.stream().anyMatch(instToMatch::equals);
		}

		return false;


	}

	private boolean isReadAuthorized(Gesuch entity) {
		boolean isOwnerOrAdmin = isSachbearbeiterJAOrGSOwner(entity, principalBean.getPrincipal().getName());
		if (isOwnerOrAdmin) {
			return true;
		}

		if (principalBean.isCallerInRole(UserRole.SACHBEARBEITER_INSTITUTION)) {
			Institution institution = principalBean.getBenutzer().getInstitution();
			Validate.notNull(institution, "Institution des Sachbearbeiters muss gesetzt sein " + principalBean.getBenutzer());
			return entity.hasBetreuungOfInstitution(institution); //@reviewer: oder besser ueber service ?
		}

		if (principalBean.isCallerInRole(UserRole.SACHBEARBEITER_TRAEGERSCHAFT)) {
			Traegerschaft traegerschaft = principalBean.getBenutzer().getTraegerschaft();
			Validate.notNull(traegerschaft, "Traegerschaft des des Sachbearbeiters muss gesetzt sein " + principalBean.getBenutzer());
			Collection<Institution> institutions = institutionService.getAllInstitutionenFromTraegerschaft(traegerschaft.getId());
			return institutions.stream().anyMatch(entity::hasBetreuungOfInstitution);  // irgend eine der betreuungen des gesuchs matched
		}

		return false;

	}


	private boolean isWriteAuthorized(AbstractEntity entity, String principalName) {
		return isSachbearbeiterJAOrGSOwner(entity, principalName);
	}


	private void throwCreateViolation() {
		throw new EJBAccessException(
			"Access Violation"
				+ " user is not allowed to create entity:"
				+ " for current user: " + principalBean.getPrincipal()
		);
	}

	private void throwViolation(AbstractEntity abstractEntity) {
		throw new EJBAccessException(
			"Access Violation"
				+ " for Entity: " + abstractEntity.getClass().getSimpleName() + "(id=" + abstractEntity.getId() + "):"
				+ " for current user: " + principalBean.getPrincipal()
		);
	}

	private void throwMandantViolation(HasMandant mandantEntity) {
		throw new EJBAccessException(
			"Mandant Access Violation"
				+ " for Entity: " + mandantEntity.getClass().getSimpleName() + "(id=" + mandantEntity.getId() + "):"
				+ " for current user: " + principalBean.getPrincipal()
		);

	}


	public Gesuch getGesuchById(String gesuchID) {
		return persistence.find(Gesuch.class, gesuchID);
	}
}
