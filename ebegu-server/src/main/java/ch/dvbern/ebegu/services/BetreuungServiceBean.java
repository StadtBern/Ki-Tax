/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2017 City of Bern Switzerland
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ch.dvbern.ebegu.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;

import ch.dvbern.ebegu.entities.Abwesenheit;
import ch.dvbern.ebegu.entities.AbwesenheitContainer;
import ch.dvbern.ebegu.entities.AbwesenheitContainer_;
import ch.dvbern.ebegu.entities.Abwesenheit_;
import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Betreuung_;
import ch.dvbern.ebegu.entities.Betreuungsmitteilung;
import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.entities.Fall_;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuch_;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.entities.Gesuchsperiode_;
import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.entities.InstitutionStammdaten_;
import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.entities.KindContainer_;
import ch.dvbern.ebegu.entities.Mitteilung;
import ch.dvbern.ebegu.entities.Verfuegung_;
import ch.dvbern.ebegu.enums.AnmeldungMutationZustand;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.AntragTyp;
import ch.dvbern.ebegu.enums.ApplicationPropertyKey;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.enums.Eingangsart;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.GesuchsperiodeStatus;
import ch.dvbern.ebegu.enums.WizardStepName;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.errors.MailException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.ebegu.validationgroups.BetreuungBestaetigenValidationGroup;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ch.dvbern.ebegu.enums.UserRoleName.ADMIN;
import static ch.dvbern.ebegu.enums.UserRoleName.ADMINISTRATOR_SCHULAMT;
import static ch.dvbern.ebegu.enums.UserRoleName.GESUCHSTELLER;
import static ch.dvbern.ebegu.enums.UserRoleName.JURIST;
import static ch.dvbern.ebegu.enums.UserRoleName.REVISOR;
import static ch.dvbern.ebegu.enums.UserRoleName.SACHBEARBEITER_INSTITUTION;
import static ch.dvbern.ebegu.enums.UserRoleName.SACHBEARBEITER_JA;
import static ch.dvbern.ebegu.enums.UserRoleName.SACHBEARBEITER_TRAEGERSCHAFT;
import static ch.dvbern.ebegu.enums.UserRoleName.SCHULAMT;
import static ch.dvbern.ebegu.enums.UserRoleName.STEUERAMT;
import static ch.dvbern.ebegu.enums.UserRoleName.SUPER_ADMIN;

/**
 * Service fuer Betreuung
 */
@Stateless
@Local(BetreuungService.class)
@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, GESUCHSTELLER, STEUERAMT })
public class BetreuungServiceBean extends AbstractBaseService implements BetreuungService {

	public static final String BETREUUNG_DARF_NICHT_NULL_SEIN = "betreuung darf nicht null sein";
	private static final Pattern COMPILE = Pattern.compile("^0+(?!$)");

	@Inject
	private Persistence persistence;
	@Inject
	private WizardStepService wizardStepService;
	@Inject
	private InstitutionService institutionService;
	@Inject
	private MitteilungService mitteilungService;
	@Inject
	private GesuchService gesuchService;
	@Inject
	private Authorizer authorizer;
	@Inject
	private MailService mailService;
	@Inject
	private GesuchsperiodeService gesuchsperiodeService;
	@Inject
	private BenutzerService benutzerService;
	@Inject
	private ApplicationPropertyService applicationPropertyService;

	private final Logger LOG = LoggerFactory.getLogger(BetreuungServiceBean.class.getSimpleName());

	@Override
	@Nonnull
	@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, GESUCHSTELLER, SCHULAMT,
		ADMINISTRATOR_SCHULAMT })
	public Betreuung saveBetreuung(@Valid @Nonnull Betreuung betreuung, @Nonnull Boolean isAbwesenheit) {
		Objects.requireNonNull(betreuung);
		boolean isNew = betreuung.isNew(); // needed hier before it gets saved
		if (betreuung.getBetreuungsstatus().isSchulamt()) {
			// Wir setzen auch Schulamt-Betreuungen auf gueltig, for future use
			betreuung.setGueltig(true);
			if (betreuung.getVorgaengerId() != null) {
				Optional<Betreuung> vorgaengerBetreuungOptional = findBetreuung(betreuung.getVorgaengerId());
				vorgaengerBetreuungOptional.ifPresent(vorgaenger -> vorgaenger.setGueltig(false));
			}
		}
		final Betreuung mergedBetreuung = persistence.merge(betreuung);

		// We need to update (copy) all other Betreuungen with same BGNummer (on all other Mutationen and Erstgesuch)
		final List<Betreuung> betreuungByBGNummer = findBetreuungenByBGNummer(mergedBetreuung.getBGNummer());
		betreuungByBGNummer.stream().filter(b -> b.isAngebotSchulamt() && !Objects.equals(betreuung.getId(), b.getId())).forEach(b -> {
			b.copyAnmeldung(betreuung);
			persistence.merge(b);
		});

		// we need to manually add this new Betreuung to the Kind
		final Set<Betreuung> betreuungen = mergedBetreuung.getKind().getBetreuungen();
		betreuungen.add(mergedBetreuung);
		mergedBetreuung.getKind().setBetreuungen(betreuungen);

		//jetzt noch wizard step updaten
		if (isAbwesenheit) {
			wizardStepService.updateSteps(mergedBetreuung.getKind().getGesuch().getId(), null, null, WizardStepName.ABWESENHEIT);
		} else {
			wizardStepService.updateSteps(mergedBetreuung.getKind().getGesuch().getId(), null, null, WizardStepName.BETREUUNG);
		}

		Gesuch mergedGesuch = gesuchService.updateBetreuungenStatus(mergedBetreuung.extractGesuch());

		if (updateVerantwortlicheNeeded(mergedGesuch.getEingangsart(), mergedBetreuung.getBetreuungsstatus(), isNew)) {
			String propertyDefaultVerantwortlicher = applicationPropertyService.findApplicationPropertyAsString(
				ApplicationPropertyKey.DEFAULT_VERANTWORTLICHER);
			String propertyDefaultVerantwortlicherSch = applicationPropertyService.findApplicationPropertyAsString(
				ApplicationPropertyKey.DEFAULT_VERANTWORTLICHER_SCH);
			gesuchService.setVerantwortliche(propertyDefaultVerantwortlicher, propertyDefaultVerantwortlicherSch, mergedBetreuung.extractGesuch(), true, true);
		}


		return mergedBetreuung;
	}

	private boolean updateVerantwortlicheNeeded(Eingangsart eingangsart, Betreuungsstatus betreuungsstatus, boolean isNew) {
		if (!isNew) {
			// nur neue Betreuungen duerfen den Verantwortlichen setzen
			return false;
		}
		if (eingangsart == Eingangsart.PAPIER) {
			// immer bei eingangsart Papier
			return true;
		} else if (betreuungsstatus == Betreuungsstatus.SCHULAMT_ANMELDUNG_AUSGELOEST) {
			// bei eingangsart Online nur wenn die Anmeldung direkt ausgelöst wurde (über TS oder FI hinzufügen Knöpfe)
			return true;
		}
		return false;
	}

	@Override
	@Nonnull
	@RolesAllowed({ SUPER_ADMIN, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION })
	public Betreuung betreuungPlatzAbweisen(@Valid @Nonnull Betreuung betreuung) {
		Objects.requireNonNull(betreuung, BETREUUNG_DARF_NICHT_NULL_SEIN);
		betreuung.setBetreuungsstatus(Betreuungsstatus.ABGEWIESEN);
		Betreuung persistedBetreuung = saveBetreuung(betreuung, false);
		try {
			// Bei Ablehnung einer Betreuung muss eine E-Mail geschickt werden
			mailService.sendInfoBetreuungAbgelehnt(persistedBetreuung);
		} catch (MailException e) {
			LOG.error("Mail InfoBetreuungAbgelehnt konnte nicht verschickt werden fuer Betreuung {}", betreuung.getId(), e);
		}
		return persistedBetreuung;
	}

	@Override
	@Nonnull
	@RolesAllowed({ SUPER_ADMIN, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION })
	public Betreuung betreuungPlatzBestaetigen(@Valid @Nonnull Betreuung betreuung) {
		Objects.requireNonNull(betreuung, BETREUUNG_DARF_NICHT_NULL_SEIN);

		// Erst jetzt kann der Zeitraum der Betreuung im Vergleich zur Institution geprueft werden
		Validator validator = Validation.byDefaultProvider().configure().buildValidatorFactory().getValidator();
		Set<ConstraintViolation<Betreuung>> violations = validator.validate(betreuung, BetreuungBestaetigenValidationGroup.class);
		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(violations);
		}

		betreuung.setBetreuungsstatus(Betreuungsstatus.BESTAETIGT);
		Betreuung persistedBetreuung = saveBetreuung(betreuung, false);
		try {
			Gesuch gesuch = betreuung.extractGesuch();
			if (gesuch.areAllBetreuungenBestaetigt()) {
				// Sobald alle Betreuungen bestaetigt sind, eine Mail schreiben
				mailService.sendInfoBetreuungenBestaetigt(gesuch);
			}
		} catch (MailException e) {
			LOG.error("Mail InfoBetreuungenBestaetigt konnte nicht verschickt werden fuer Betreuung {}", betreuung.getId(), e);
		}
		return persistedBetreuung;
	}

	@Override
	@Nonnull
	@RolesAllowed({ SUPER_ADMIN, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, SCHULAMT, ADMINISTRATOR_SCHULAMT })
	public Betreuung anmeldungSchulamtUebernehmen(@Valid @Nonnull Betreuung betreuung) {
		Objects.requireNonNull(betreuung, BETREUUNG_DARF_NICHT_NULL_SEIN);
		betreuung.setBetreuungsstatus(Betreuungsstatus.SCHULAMT_ANMELDUNG_UEBERNOMMEN);
		Betreuung persistedBetreuung = saveBetreuung(betreuung, false);
		try {
			// Bei Uebernahme einer Anmeldung muss eine E-Mail geschickt werden
			mailService.sendInfoSchulamtAnmeldungUebernommen(persistedBetreuung);
		} catch (MailException e) {
			LOG.error("Mail InfoSchulamtAnmeldungUebernommen konnte nicht verschickt werden fuer Betreuung {}", betreuung.getId(), e);
		}
		return persistedBetreuung;
	}

	@Override
	@Nonnull
	@RolesAllowed({ SUPER_ADMIN, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, SCHULAMT, ADMINISTRATOR_SCHULAMT })
	public Betreuung anmeldungSchulamtAblehnen(@Valid @Nonnull Betreuung betreuung) {
		Objects.requireNonNull(betreuung, BETREUUNG_DARF_NICHT_NULL_SEIN);
		betreuung.setBetreuungsstatus(Betreuungsstatus.SCHULAMT_ANMELDUNG_ABGELEHNT);
		Betreuung persistedBetreuung = saveBetreuung(betreuung, false);
		try {
			// Bei Ablehnung einer Anmeldung muss eine E-Mail geschickt werden
			mailService.sendInfoSchulamtAnmeldungAbgelehnt(persistedBetreuung);
		} catch (MailException e) {
			LOG.error("Mail InfoSchulamtAnmeldungAbgelehnt konnte nicht verschickt werden fuer Betreuung {}", betreuung.getId(), e);
		}
		return persistedBetreuung;
	}

	@Override
	@Nonnull
	@RolesAllowed({ SUPER_ADMIN, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, SCHULAMT, ADMINISTRATOR_SCHULAMT })
	public Betreuung anmeldungSchulamtFalscheInstitution(@Valid @Nonnull Betreuung betreuung) {
		Objects.requireNonNull(betreuung, BETREUUNG_DARF_NICHT_NULL_SEIN);
		betreuung.setBetreuungsstatus(Betreuungsstatus.SCHULAMT_FALSCHE_INSTITUTION);
		Betreuung persistedBetreuung = saveBetreuung(betreuung, false);
		return persistedBetreuung;
	}

	@Override
	@Nonnull
	@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, GESUCHSTELLER,
		ADMINISTRATOR_SCHULAMT, SCHULAMT })
	public Optional<Betreuung> findBetreuung(@Nonnull String key) {
		return findBetreuung(key, true);
	}

	@Override
	@Nonnull
	@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, GESUCHSTELLER,
		ADMINISTRATOR_SCHULAMT, SCHULAMT })
	public Optional<Betreuung> findBetreuung(@Nonnull String key, boolean doAuthCheck) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		Betreuung betr = persistence.find(Betreuung.class, key);
		if (doAuthCheck && betr != null) {
			authorizer.checkReadAuthorization(betr);
		}
		return Optional.ofNullable(betr);
	}

	@Override
	@Nonnull
	public List<Betreuung> findBetreuungenByBGNummer(@Nonnull String bgNummer) {
		return findBetreuungenByBGNummer(bgNummer, false);
	}

	@Override
	public List<Betreuung> findNewestBetreuungByBGNummer(@Nonnull String bgNummer) {
		return findBetreuungenByBGNummer(bgNummer, true);
	}

	@Nonnull
	private List<Betreuung> findBetreuungenByBGNummer(@Nonnull String bgNummer, boolean getOnlyAktuelle) {
		final int betreuungNummer = getBetreuungNummerFromBGNummer(bgNummer);
		final int kindNummer = getKindNummerFromBGNummer(bgNummer);
		final int yearFromBGNummer = getYearFromBGNummer(bgNummer);
		// der letzte Tag im Jahr, von der BetreuungsId sollte immer zur richtigen Gesuchsperiode zählen.
		final Optional<Gesuchsperiode> gesuchsperiodeOptional = gesuchsperiodeService.getGesuchsperiodeAm(LocalDate.ofYearDay(yearFromBGNummer, 365));
		Gesuchsperiode gesuchsperiode;
		if (gesuchsperiodeOptional.isPresent()) {
			gesuchsperiode = gesuchsperiodeOptional.get();
		} else {
			return new ArrayList<>();
		}
		final long fallnummer = getFallnummerFromBGNummer(bgNummer);

		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Betreuung> query = cb.createQuery(Betreuung.class);

		Root<Betreuung> root = query.from(Betreuung.class);
		final Join<Betreuung, KindContainer> kindjoin = root.join(Betreuung_.kind, JoinType.LEFT);
		final Join<KindContainer, Gesuch> kindContainerGesuchJoin = kindjoin.join(KindContainer_.gesuch, JoinType.LEFT);
		final Join<Gesuch, Fall> gesuchFallJoin = kindContainerGesuchJoin.join(Gesuch_.fall, JoinType.LEFT);

		Predicate predBetreuungNummer = cb.equal(root.get(Betreuung_.betreuungNummer), betreuungNummer);
		Predicate predBetreuungAusgeloest = root.get(Betreuung_.betreuungsstatus).in(Betreuungsstatus.betreuungsstatusAusgeloest);
		Predicate predKindNummer = cb.equal(kindjoin.get(KindContainer_.kindNummer), kindNummer);
		Predicate predFallNummer = cb.equal(gesuchFallJoin.get(Fall_.fallNummer), fallnummer);
		Predicate predGesuchsperiode = cb.equal(kindContainerGesuchJoin.get(Gesuch_.gesuchsperiode), gesuchsperiode);

		List<Predicate> predicates = new ArrayList<>();
		predicates.add(predFallNummer);
		predicates.add(predGesuchsperiode);
		predicates.add(predKindNummer);
		predicates.add(predBetreuungNummer);
		predicates.add(predBetreuungAusgeloest);

		if (getOnlyAktuelle) {
			Predicate predAktuelleBetreuung = cb.equal(root.get(Betreuung_.anmeldungMutationZustand), AnmeldungMutationZustand.AKTUELLE_ANMELDUNG);
			Predicate predNormaleBetreuung = cb.isNull(root.get(Betreuung_.anmeldungMutationZustand));
			predicates.add(cb.or(predAktuelleBetreuung, predNormaleBetreuung));
		}

		query.where(CriteriaQueryHelper.concatenateExpressions(cb, predicates));

		return persistence.getCriteriaResults(query);
	}

	@Override
	public Long getFallnummerFromBGNummer(String bgNummer) {
		//17.000120.1.1 -> 120 (long)
		return Long.valueOf(COMPILE.matcher(bgNummer.substring(3, 9)).replaceFirst(""));
	}

	@Override
	public int getYearFromBGNummer(String bgNummer) {
		//17.000120.1.1 -> 17 (int)
		return Integer.valueOf(bgNummer.substring(0, 2)) + 2000;
	}

	@Override
	public int getKindNummerFromBGNummer(String bgNummer) {
		//17.000120.1.1 -> 1 (int) can have more than 9 Kind
		return Integer.valueOf(bgNummer.split("\\.", -1)[2]);
	}

	@Override
	public int getBetreuungNummerFromBGNummer(String bgNummer) {
		return Integer.valueOf(bgNummer.split("\\.", -1)[3]);
	}

	@Override
	public boolean validateBGNummer(String bgNummer) {
		return bgNummer.matches("^\\d{2}\\.\\d{6}\\.\\d+\\.\\d+$");
	}

	@Override
	@Nonnull
	@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, GESUCHSTELLER,
		ADMINISTRATOR_SCHULAMT, SCHULAMT })
	public Optional<Betreuung> findBetreuungWithBetreuungsPensen(@Nonnull String key) {
		Objects.requireNonNull(key, "id muss gesetzt sein");
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Betreuung> query = cb.createQuery(Betreuung.class);
		Root<Betreuung> root = query.from(Betreuung.class);
		root.fetch(Betreuung_.betreuungspensumContainers, JoinType.LEFT);
		root.fetch(Betreuung_.abwesenheitContainers, JoinType.LEFT);
		query.select(root);
		Predicate idPred = cb.equal(root.get(Betreuung_.id), key);
		query.where(idPred);
		Betreuung result = persistence.getCriteriaSingleResult(query);
		if (result != null) {
			authorizer.checkReadAuthorization(result);
		}
		return Optional.ofNullable(result);
	}

	@Override
	@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, GESUCHSTELLER, ADMINISTRATOR_SCHULAMT,
		SCHULAMT })
	public void removeBetreuung(@Nonnull String betreuungId) {
		Objects.requireNonNull(betreuungId);
		Optional<Betreuung> betrToRemoveOpt = findBetreuung(betreuungId);
		Betreuung betreuungToRemove = betrToRemoveOpt.orElseThrow(() -> new EbeguEntityNotFoundException("removeBetreuung", ErrorCodeEnum
			.ERROR_ENTITY_NOT_FOUND, betreuungId));
		Collection<Mitteilung> mitteilungenForBetreuung = this.mitteilungService.findAllMitteilungenForBetreuung(betreuungToRemove);
		mitteilungenForBetreuung.stream()
			.filter(mitteilung -> mitteilung.getClass().equals(Mitteilung.class))
			.forEach((mitteilung) ->
			{
				mitteilung.setBetreuung(null);
				this.LOG.debug("Betreuung '{}' will be removed. Removing Relation in Mitteilung: '{}'", betreuungId, mitteilung.getId());
				persistence.merge(mitteilung);
			});

		mitteilungenForBetreuung.stream()
			.filter(mitteilung -> mitteilung.getClass().equals(Betreuungsmitteilung.class))
			.forEach((betMitteilung) -> {
				this.LOG.debug("Betreuung '{}' will be removed. Removing dependent Betreuungsmitteilung: '{}'", betreuungId, betMitteilung.getId());
				persistence.remove(Betreuungsmitteilung.class, betMitteilung.getId());
			});

		final String gesuchId = betreuungToRemove.getKind().getGesuch().getId();
		removeBetreuung(betreuungToRemove);
		wizardStepService.updateSteps(gesuchId, null, null, WizardStepName.BETREUUNG); //auch bei entfernen wizard updaten

		List<Betreuung> betreuungen = new ArrayList<>();
		betreuungen.add(betreuungToRemove);
		mailService.sendInfoBetreuungGeloescht(betreuungen);
	}

	@Override
	@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, GESUCHSTELLER, ADMINISTRATOR_SCHULAMT,
		SCHULAMT })
	public void removeBetreuung(@Nonnull Betreuung betreuung) {
		Objects.requireNonNull(betreuung);
		authorizer.checkWriteAuthorization(betreuung);
		final Gesuch gesuch = betreuung.extractGesuch();

		persistence.remove(betreuung);

		// the betreuung needs to be removed from the object as well
		gesuch.getKindContainers()
			.forEach(kind -> kind.getBetreuungen().removeIf(bet -> bet.getId().equalsIgnoreCase(betreuung.getId())));

		gesuchService.updateBetreuungenStatus(gesuch);
	}

	@Override
	@Nonnull
	@RolesAllowed({ ADMIN, SUPER_ADMIN, SACHBEARBEITER_JA, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT, ADMINISTRATOR_SCHULAMT,
		SCHULAMT })
	public Collection<Betreuung> getPendenzenBetreuungen() {
		Collection<Institution> instForCurrBenutzer = institutionService.getAllowedInstitutionenForCurrentBenutzer(true);
		if (!instForCurrBenutzer.isEmpty()) {
			return getPendenzenForInstitution((Institution[]) instForCurrBenutzer.toArray(new Institution[instForCurrBenutzer.size()]));
		}
		return Collections.emptyList();
	}

	@Override
	@Nonnull
	@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, GESUCHSTELLER,
		ADMINISTRATOR_SCHULAMT, SCHULAMT })
	public List<Betreuung> findAllBetreuungenFromGesuch(@Nonnull String gesuchId) {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Betreuung> query = cb.createQuery(Betreuung.class);
		Root<Betreuung> root = query.from(Betreuung.class);
		// Betreuung from Gesuch
		Predicate predicateInstitution = root.get(Betreuung_.kind).get(KindContainer_.gesuch).get(Gesuch_.id).in(gesuchId);

		query.where(predicateInstitution);
		authorizer.checkReadAuthorizationGesuchId(gesuchId);
		return persistence.getCriteriaResults(query);
	}

	@Nonnull
	@Override
	@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION, GESUCHSTELLER,
		ADMINISTRATOR_SCHULAMT, SCHULAMT })
	public List<Betreuung> findAllBetreuungenWithVerfuegungFromFall(@Nonnull Fall fall) {
		Objects.requireNonNull(fall, "fall muss gesetzt sein");

		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Betreuung> query = cb.createQuery(Betreuung.class);

		Root<Betreuung> root = query.from(Betreuung.class);
		List<Predicate> predicatesToUse = new ArrayList<>();

		Predicate fallPredicate = cb.equal(root.get(Betreuung_.kind).get(KindContainer_.gesuch).get(Gesuch_.fall), fall);
		predicatesToUse.add(fallPredicate);

		Predicate predicateBetreuung = root.get(Betreuung_.betreuungsstatus).in(Betreuungsstatus.hasVerfuegung);
		predicatesToUse.add(predicateBetreuung);

		Predicate verfuegungPredicate = cb.isNotNull(root.get(Betreuung_.verfuegung));
		predicatesToUse.add(verfuegungPredicate);

		Collection<Institution> institutionen = institutionService.getAllowedInstitutionenForCurrentBenutzer(false);
		Predicate predicateInstitution = root.get(Betreuung_.institutionStammdaten).get(InstitutionStammdaten_.institution).in(Arrays.asList(institutionen));
		predicatesToUse.add(predicateInstitution);

		query.where(CriteriaQueryHelper.concatenateExpressions(cb, predicatesToUse)).orderBy(cb.desc(root.get(Betreuung_.verfuegung).get(Verfuegung_
			.timestampErstellt)));

		List<Betreuung> criteriaResults = persistence.getCriteriaResults(query);

		criteriaResults.forEach(betreuung -> authorizer.checkReadAuthorization(betreuung));

		return criteriaResults;
	}

	/**
	 * Liest alle Betreuungen die zu einer der mitgegebenen Institution gehoeren und die im Status WARTEN sind. Wenn der Benutzer vom Schulamt
	 * ist, dann werden nur die Institutionsstammdaten der Art FERIENINSEL oder TAGESSCHULE betrachtet.
	 */
	@Nonnull
	private Collection<Betreuung> getPendenzenForInstitution(@Nonnull Institution... institutionen) {
		Objects.requireNonNull(institutionen, "institutionen muss gesetzt sein");

		Optional<Benutzer> benutzerOptional = benutzerService.getCurrentBenutzer();
		if (!benutzerOptional.isPresent()) {
			throw new EbeguRuntimeException("getPendenzenForInstitution", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, "current user not found");
		}
		Benutzer benutzer = benutzerOptional.get();

		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Betreuung> query = cb.createQuery(Betreuung.class);
		Root<Betreuung> root = query.from(Betreuung.class);

		List<Predicate> predicates = new ArrayList<>();

		if (benutzer.getRole().isRoleSchulamt()) {
			predicates.add(root.get(Betreuung_.betreuungsstatus).in(Arrays.asList(Betreuungsstatus.forPendenzSchulamt)));
		} else { // for Institution or Traegerschaft. bz default
			predicates.add(root.get(Betreuung_.betreuungsstatus).in(Arrays.asList(Betreuungsstatus.forPendenzInstitution)));
		}

		// nur Aktuelle Anmeldungen
		Predicate predAktuelleBetreuung = cb.equal(root.get(Betreuung_.anmeldungMutationZustand), AnmeldungMutationZustand.AKTUELLE_ANMELDUNG);
		Predicate predNormaleBetreuung = cb.isNull(root.get(Betreuung_.anmeldungMutationZustand));
		predicates.add(cb.or(predAktuelleBetreuung, predNormaleBetreuung));

		// Institution
		predicates.add(root.get(Betreuung_.institutionStammdaten).get(InstitutionStammdaten_.institution).in(Arrays.asList(institutionen)));
		// Gesuchsperiode darf nicht geschlossen sein
		predicates.add(root.get(Betreuung_.kind).get(KindContainer_.gesuch).get(Gesuch_.gesuchsperiode).get(Gesuchsperiode_.status).in
			(GesuchsperiodeStatus.AKTIV, GesuchsperiodeStatus.INAKTIV));

		if (benutzer.getRole().isRoleSchulamt()) {
			// SCH darf nur Gesuche sehen, die bereits freigegebn wurden
			predicates.add(root.get(Betreuung_.kind).get(KindContainer_.gesuch).get(Gesuch_.status).in
				(AntragStatus.FOR_ADMIN_ROLE));
			// SCH darf nur Schulamt Betreuungen als PEndeny erhalten
			Predicate predicateTagesschule = cb.equal(root.get(Betreuung_.institutionStammdaten).get(InstitutionStammdaten_.betreuungsangebotTyp), BetreuungsangebotTyp.TAGESSCHULE);
			Predicate predicateFerieninsel = cb.equal(root.get(Betreuung_.institutionStammdaten).get(InstitutionStammdaten_.betreuungsangebotTyp), BetreuungsangebotTyp.FERIENINSEL);
			predicates.add(cb.or(predicateTagesschule, predicateFerieninsel));
		}

		query.where(CriteriaQueryHelper.concatenateExpressions(cb, predicates));

		List<Betreuung> betreuungen = persistence.getCriteriaResults(query);
		authorizer.checkReadAuthorizationForAllBetreuungen(betreuungen);
		return betreuungen;
	}

	@Override
	@Nonnull
	@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA })
	public Betreuung schliessenOhneVerfuegen(@Nonnull Betreuung betreuung) {
		return closeBetreuung(betreuung, Betreuungsstatus.GESCHLOSSEN_OHNE_VERFUEGUNG);
	}

	@Nonnull
	@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA })
	private Betreuung closeBetreuung(@Nonnull Betreuung betreuung, @Nonnull Betreuungsstatus status) {
		betreuung.setBetreuungsstatus(status);
		final Betreuung persistedBetreuung = saveBetreuung(betreuung, false);
		authorizer.checkWriteAuthorization(persistedBetreuung);
		wizardStepService.updateSteps(persistedBetreuung.extractGesuch().getId(), null, null, WizardStepName.VERFUEGEN);
		return persistedBetreuung;
	}

	@Override
	@Nonnull
	@RolesAllowed(value = { ADMIN, SUPER_ADMIN, SACHBEARBEITER_JA, ADMINISTRATOR_SCHULAMT, SCHULAMT, REVISOR })
	public List<Betreuung> getAllBetreuungenWithMissingStatistics() {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Betreuung> query = cb.createQuery(Betreuung.class);

		Root<Betreuung> root = query.from(Betreuung.class);
		Join<Betreuung, KindContainer> joinKindContainer = root.join(Betreuung_.kind, JoinType.LEFT);
		Join<KindContainer, Gesuch> joinGesuch = joinKindContainer.join(KindContainer_.gesuch, JoinType.LEFT);

		Predicate predicateMutation = cb.equal(joinGesuch.get(Gesuch_.typ), AntragTyp.MUTATION);
		Predicate predicateFlag = cb.isNull(root.get(Betreuung_.betreuungMutiert));
		Predicate predicateStatus = joinGesuch.get(Gesuch_.status).in(AntragStatus.getAllVerfuegtStates());

		query.where(predicateMutation, predicateFlag, predicateStatus);
		query.orderBy(cb.desc(joinGesuch.get(Gesuch_.laufnummer)));
		return persistence.getCriteriaResults(query);
	}

	@Override
	@Nonnull
	@RolesAllowed(value = { ADMIN, SUPER_ADMIN, SACHBEARBEITER_JA, ADMINISTRATOR_SCHULAMT, SCHULAMT, REVISOR })
	public List<Abwesenheit> getAllAbwesenheitenWithMissingStatistics() {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Abwesenheit> query = cb.createQuery(Abwesenheit.class);

		Root<Abwesenheit> root = query.from(Abwesenheit.class);
		Join<Abwesenheit, AbwesenheitContainer> joinAbwesenheitContainer = root.join(Abwesenheit_.abwesenheitContainer, JoinType.LEFT);
		Join<AbwesenheitContainer, Betreuung> joinBetreuung = joinAbwesenheitContainer.join(AbwesenheitContainer_.betreuung, JoinType.LEFT);
		Join<Betreuung, KindContainer> joinKindContainer = joinBetreuung.join(Betreuung_.kind, JoinType.LEFT);
		Join<KindContainer, Gesuch> joinGesuch = joinKindContainer.join(KindContainer_.gesuch, JoinType.LEFT);

		Predicate predicateMutation = cb.equal(joinGesuch.get(Gesuch_.typ), AntragTyp.MUTATION);
		Predicate predicateFlag = cb.isNull(joinBetreuung.get(Betreuung_.abwesenheitMutiert));
		Predicate predicateStatus = joinGesuch.get(Gesuch_.status).in(AntragStatus.getAllVerfuegtStates());

		query.where(predicateMutation, predicateFlag, predicateStatus);
		query.orderBy(cb.desc(joinGesuch.get(Gesuch_.laufnummer)));
		return persistence.getCriteriaResults(query);
	}

	@Override
	public int changeAnmeldungMutationZustand(String betreuungsId, AnmeldungMutationZustand anmeldungMutationZustand) {
		CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaUpdate<Betreuung> update = cb.createCriteriaUpdate(Betreuung.class);
		Root<Betreuung> root = update.from(Betreuung.class);
		update.set(Betreuung_.anmeldungMutationZustand, anmeldungMutationZustand);

		Predicate predBetreuung = cb.equal(root.get(Betreuung_.id), betreuungsId);
		update.where(predBetreuung);

		return persistence.getEntityManager().createQuery(update).executeUpdate();
	}
}
