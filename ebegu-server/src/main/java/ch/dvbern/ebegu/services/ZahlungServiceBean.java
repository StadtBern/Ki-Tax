package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.*;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.types.DateRange_;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.util.MathUtil;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;


/**
 * Service fuer Zahlungen
 */
@Stateless
@Local(ZahlungService.class)
@RolesAllowed(value = {UserRoleName.SUPER_ADMIN, UserRoleName.ADMIN, UserRoleName.SACHBEARBEITER_JA, UserRoleName.SACHBEARBEITER_INSTITUTION, UserRoleName.SACHBEARBEITER_TRAEGERSCHAFT})
@SuppressWarnings(value = {"PMD.AvoidDuplicateLiterals", "SpringAutowiredFieldsWarningInspection"})
public class ZahlungServiceBean extends AbstractBaseService implements ZahlungService {

	private static final Logger LOG = LoggerFactory.getLogger(ZahlungServiceBean.class.getSimpleName());

	@Inject
	private Persistence<Zahlungsauftrag> persistence;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;

	@Inject
	private VerfuegungService verfuegungService;

	@Inject
	private GesuchService gesuchService;

	@Inject
	private GesuchsperiodeService gesuchsperiodeService;

	@Override
	@RolesAllowed(value = {UserRoleName.SUPER_ADMIN, UserRoleName.ADMIN, UserRoleName.SACHBEARBEITER_JA})
	public Zahlungsauftrag zahlungsauftragErstellen(LocalDateTime datumFaelligkeit, String beschreibung) {
		Zahlungsauftrag zahlungsauftrag = new Zahlungsauftrag();
		zahlungsauftrag.setAusgeloest(Boolean.FALSE);
		zahlungsauftrag.setBeschrieb(beschreibung);
		zahlungsauftrag.setDatumFaellig(datumFaelligkeit);

		// Alle aktuellen (d.h. der letzte Antrag jedes Falles) Verfuegungen suchen, welche ein Kita-Angebot haben
		// Wir brauchen folgende Daten:
		// - Zeitraum, welcher fuer die (normale) Auszahlung gilt: Immer ganzer Monat, mindestens der Monat des DatumFaellig,
		// 		jedoch seit Ende Monat des letzten Auftrags -> 1 oder mehrere ganze Monate
		// - Zeitraum, welcher fuer die Berechnung der rueckwirkenden Korrekturen gilt: Zeitpunkt der letzten Zahlungserstellung bis aktueller Zeitpunkt
		// 		(Achtung: Es ist *nicht* das Faelligkeitsdatum relevant, sondern das Erstellungsdatum des letzten Auftrags!)
		// Den letzten Zahlungsauftrag lesen
		LocalDateTime lastZahlungErstellt = Constants.START_OF_DATETIME; // Default, falls dies der erste Auftrag ist
		Optional<Zahlungsauftrag> lastZahlungsauftrag = findLastZahlungsauftrag();
		if (lastZahlungsauftrag.isPresent()) {
			lastZahlungErstellt = lastZahlungsauftrag.get().getTimestampErstellt();
		}

		LocalDate datumVon = lastZahlungErstellt.plusMonths(1).with(TemporalAdjusters.firstDayOfMonth()).toLocalDate();
		LocalDate datumBis = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
		zahlungsauftrag.setGueltigkeit(new DateRange(datumVon, datumBis));

		Map<String, Zahlung> zahlungProInstitution = new HashMap<>();

		// "Normale" Zahlungen
		Collection<VerfuegungZeitabschnitt> gueltigeVerfuegungZeitabschnitte = getGueltigeVerfuegungZeitabschnitte(datumVon, datumBis);
		for (VerfuegungZeitabschnitt zeitabschnitt : gueltigeVerfuegungZeitabschnitte) {
			createZahlungsposition(zeitabschnitt, zahlungsauftrag, zahlungProInstitution);
		}
		// Korrekturen
		Collection<VerfuegungZeitabschnitt> mutationsVerfuegungsZeitabschnitte = getMutationsVerfuegungsZeitabschnitte(lastZahlungErstellt, LocalDateTime.now(), datumVon, datumBis);
		for (VerfuegungZeitabschnitt zeitabschnitt : mutationsVerfuegungsZeitabschnitte) {
			createZahlungspositionenKorrektur(zeitabschnitt, zahlungsauftrag, zahlungProInstitution);
		}
		//TODO ISO-File generieren
		zahlungsauftrag.setFilecontent("TODO das File");
		return persistence.persist(zahlungsauftrag);
	}

	public Collection<VerfuegungZeitabschnitt> getGueltigeVerfuegungZeitabschnitte(LocalDate zeitabschnittVon, LocalDate zeitabschnittBis) {
		Objects.requireNonNull(zeitabschnittVon, "zeitabschnittVon muss gesetzt sein");
		Objects.requireNonNull(zeitabschnittBis, "zeitabschnittBis muss gesetzt sein");

		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<VerfuegungZeitabschnitt> query = cb.createQuery(VerfuegungZeitabschnitt.class);
		Root<VerfuegungZeitabschnitt> root = query.from(VerfuegungZeitabschnitt.class);
		Join<Verfuegung, Betreuung> joinBetreuung = root.join(VerfuegungZeitabschnitt_.verfuegung).join(Verfuegung_.betreuung);

		List<Expression<Boolean>> predicates = new ArrayList<>();

		// Datum Von
		Predicate predicateStart = cb.lessThanOrEqualTo(root.get(VerfuegungZeitabschnitt_.gueltigkeit).get(DateRange_.gueltigAb), zeitabschnittBis);
		predicates.add(predicateStart);
		// Datum Bis
		Predicate predicateEnd = cb.greaterThanOrEqualTo(root.get(VerfuegungZeitabschnitt_.gueltigkeit).get(DateRange_.gueltigBis), zeitabschnittVon);
		predicates.add(predicateEnd);
		// Nur Angebot KITA
		Predicate predicateAngebot = cb.equal(joinBetreuung.get(Betreuung_.institutionStammdaten).get(InstitutionStammdaten_.betreuungsangebotTyp), BetreuungsangebotTyp.KITA);
		predicates.add(predicateAngebot);
		// Nur neueste Verfuegung jedes Falls beachten
		Optional<Gesuchsperiode> gesuchsperiodeAm = gesuchsperiodeService.getGesuchsperiodeAm(zeitabschnittBis);
		if (gesuchsperiodeAm.isPresent()) {
			List<String> gesuchIdsOfAktuellerAntrag = gesuchService.getNeuesteVerfuegteAntraege(gesuchsperiodeAm.get());
			Predicate predicateAktuellesGesuch = joinBetreuung.get(Betreuung_.kind).get(KindContainer_.gesuch).get(Gesuch_.id).in(gesuchIdsOfAktuellerAntrag);
			predicates.add(predicateAktuellesGesuch);
		} else {
			throw new EbeguRuntimeException("getGueltigeVerfuegungZeitabschnitte", "Keine Gesuchsperiode gefunden fuer Stichtag " + Constants.DATE_FORMATTER.format(zeitabschnittBis));
		}

		query.where(CriteriaQueryHelper.concatenateExpressions(cb, predicates));
		return persistence.getCriteriaResults(query);
	}

	private Collection<VerfuegungZeitabschnitt> getMutationsVerfuegungsZeitabschnitte(LocalDateTime datumVerfuegtVon, LocalDateTime datumVerfuegtBis, LocalDate zeitabschnittVon, LocalDate zeitabschnittBis) {
		Objects.requireNonNull(datumVerfuegtVon, "datumVerfuegtVon muss gesetzt sein");
		Objects.requireNonNull(datumVerfuegtBis, "datumVerfuegtBis muss gesetzt sein");
		Objects.requireNonNull(zeitabschnittVon, "zeitabschnittVon muss gesetzt sein");
		Objects.requireNonNull(zeitabschnittBis, "zeitabschnittBis muss gesetzt sein");

		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<VerfuegungZeitabschnitt> query = cb.createQuery(VerfuegungZeitabschnitt.class);
		Root<VerfuegungZeitabschnitt> root = query.from(VerfuegungZeitabschnitt.class);
		Join<Verfuegung, Betreuung> joinBetreuung = root.join(VerfuegungZeitabschnitt_.verfuegung).join(Verfuegung_.betreuung);

		List<Expression<Boolean>> predicates = new ArrayList<>();

		// Datum Bis muss VOR dem regulaeren Auszahlungszeitraum sein (sonst ist es keine Korrektur und schon im obigen Statement enthalten)
		Predicate predicateStart = cb.lessThanOrEqualTo(root.get(VerfuegungZeitabschnitt_.gueltigkeit).get(DateRange_.gueltigBis), zeitabschnittVon.minusDays(1));
		predicates.add(predicateStart);
		// Nur Angebot KITA
		Predicate predicateAngebot = cb.equal(joinBetreuung.get(Betreuung_.institutionStammdaten).get(InstitutionStammdaten_.betreuungsangebotTyp), BetreuungsangebotTyp.KITA);
		predicates.add(predicateAngebot);
		// Gesuche, welche seit dem letzten Zahlungslauf verfuegt wurden. Nur neueste Verfuegung jedes Falls beachten
		List<String> gesuchIdsOfAktuellerAntrag = gesuchService.getNeuesteVerfuegteAntraege(datumVerfuegtVon, datumVerfuegtBis);
		Predicate predicateAktuellesGesuch = joinBetreuung.get(Betreuung_.kind).get(KindContainer_.gesuch).get(Gesuch_.id).in(gesuchIdsOfAktuellerAntrag);
		predicates.add(predicateAktuellesGesuch);

		query.where(CriteriaQueryHelper.concatenateExpressions(cb, predicates));
		return persistence.getCriteriaResults(query);
	}

	private Zahlungsposition createZahlungsposition(VerfuegungZeitabschnitt zeitabschnitt, Zahlungsauftrag zahlungsauftrag, Map<String, Zahlung> zahlungProInstitution) {
		Zahlungsposition zahlungsposition = new Zahlungsposition();
		zahlungsposition.setVerfuegungZeitabschnitt(zeitabschnitt);
		zahlungsposition.setBetrag(zeitabschnitt.getVollkosten());
		zahlungsposition.setStatus(ZahlungspositionStatus.NORMAL);
		Zahlung zahlung = findZahlungForInstitution(zeitabschnitt, zahlungsauftrag, zahlungProInstitution);
		zahlungsposition.setZahlung(zahlung);
		zahlung.getZahlungspositionen().add(zahlungsposition);
		return zahlungsposition;
	}

	private Zahlungsposition createZahlungspositionenKorrektur(VerfuegungZeitabschnitt zeitabschnitt, Zahlungsauftrag zahlungsauftrag, Map<String, Zahlung> zahlungProInstitution) {
		// Ermitteln, ob die Vollkosten geaendert haben
		Optional<Verfuegung> vorgaengerVerfuegung = verfuegungService.findVorgaengerVerfuegung(zeitabschnitt.getVerfuegung().getBetreuung());
		if (vorgaengerVerfuegung.isPresent()) {
			List<VerfuegungZeitabschnitt> zeitabschnittOnVorgaengerVerfuegung = findZeitabschnittOnVorgaengerVerfuegung(zeitabschnitt.getGueltigkeit(), vorgaengerVerfuegung.get());
			BigDecimal vollkostenVorgaenger = BigDecimal.ZERO;
			BigDecimal elternbeitragVorgaenger = BigDecimal.ZERO;
			// Eventuell gab es fuer diesen Zeitabschnitt in der Vorgaengerverfuegung mehrere Abschnitte:
			// Total der Vollkosten bzw. Elternbeitraege zusammenzaehlen
			for (VerfuegungZeitabschnitt verfuegungZeitabschnitt : zeitabschnittOnVorgaengerVerfuegung) {
				MathUtil.DEFAULT.add(vollkostenVorgaenger, verfuegungZeitabschnitt.getVollkosten());
				MathUtil.DEFAULT.add(elternbeitragVorgaenger, verfuegungZeitabschnitt.getElternbeitrag());
			}
			// Und ermitteln, ob es sich im Vergleich nur neuen Verfuegung geaendert hat
			// Falls keine Aenderung -> Keine KorrekturZahlung notwendig!
			boolean vollkostenChanged = vollkostenVorgaenger.compareTo(zeitabschnitt.getVollkosten()) > 0;
			boolean elternbeitragChanged = elternbeitragVorgaenger.compareTo(zeitabschnitt.getElternbeitrag()) > 0;
			if (!vollkostenChanged && !elternbeitragChanged) {
				return null;
			}
			// Irgendetwas hat geaendert, wir brauchen eine Korrekturzahlung
			Zahlungsposition zahlungsposition = new Zahlungsposition();
			zahlungsposition.setVerfuegungZeitabschnitt(zeitabschnitt);
			zahlungsposition.setBetrag(zeitabschnitt.getVollkosten());
			Zahlung zahlung = findZahlungForInstitution(zeitabschnitt, zahlungsauftrag, zahlungProInstitution);
			zahlungsposition.setZahlung(zahlung);
			zahlung.getZahlungspositionen().add(zahlungsposition);
			if (vollkostenChanged) {
				zahlungsposition.setStatus(ZahlungspositionStatus.KORREKTUR_VOLLKOSTEN);
			}
			else {
				zahlungsposition.setStatus(ZahlungspositionStatus.KORREKTUR_ELTERNBEITRAG);
			}
			for (VerfuegungZeitabschnitt verfuegungZeitabschnitt : zeitabschnittOnVorgaengerVerfuegung) {
				// Fuer die "alten" Verfuegungszeitabschnitte muessen Korrekturbuchungen erstellt werden
				Zahlungsposition korrekturPosition = new Zahlungsposition();
				korrekturPosition.setVerfuegungZeitabschnitt(verfuegungZeitabschnitt);
				korrekturPosition.setBetrag(verfuegungZeitabschnitt.getVollkosten().negate());
				korrekturPosition.setZahlung(zahlung);
				zahlung.getZahlungspositionen().add(korrekturPosition);
			}
			return zahlungsposition;
		}
		return null;
	}

	/**
	 * Findet das anspruchberechtigtes Pensum zum Zeitpunkt des neuen Zeitabschnitt-Start
	 */
	private List<VerfuegungZeitabschnitt> findZeitabschnittOnVorgaengerVerfuegung(DateRange newVerfuegungGueltigkeit, Verfuegung lastVerfuegung) {
		List<VerfuegungZeitabschnitt> lastVerfuegungsZeitabschnitte = new ArrayList<>();
		if (lastVerfuegung != null) {
			for (VerfuegungZeitabschnitt verfuegungZeitabschnitt : lastVerfuegung.getZeitabschnitte()) {
				final DateRange gueltigkeit = verfuegungZeitabschnitt.getGueltigkeit();
				if (gueltigkeit.contains(newVerfuegungGueltigkeit.getGueltigAb()) || gueltigkeit.contains(newVerfuegungGueltigkeit.getGueltigBis())) {
					lastVerfuegungsZeitabschnitte.add(verfuegungZeitabschnitt);
				}
			}
		}
		return lastVerfuegungsZeitabschnitte;
	}

	private Zahlung findZahlungForInstitution(VerfuegungZeitabschnitt zeitabschnitt,  Zahlungsauftrag zahlungsauftrag, Map<String, Zahlung> zahlungProInstitution) {
		InstitutionStammdaten institution = zeitabschnitt.getVerfuegung().getBetreuung().getInstitutionStammdaten();
		if (zahlungProInstitution.containsKey(institution.getId())) {
			return zahlungProInstitution.get(institution.getId());
		} else {
			return createZahlung(institution, zahlungsauftrag);
		}
	}

	private Zahlung createZahlung(InstitutionStammdaten institution, Zahlungsauftrag zahlungsauftrag) {
		Zahlung zahlung = new Zahlung();
		zahlung.setStatus(ZahlungStatus.AUSGELOEST);
		zahlung.setInstitutionStammdaten(institution);
		zahlung.setZahlungsauftrag(zahlungsauftrag);
		zahlungsauftrag.getZahlungen().add(zahlung);
		return zahlung;
	}

	private Optional<Zahlungsauftrag> findLastZahlungsauftrag() {
		final CriteriaBuilder cb = persistence.getCriteriaBuilder();
		final CriteriaQuery<Zahlungsauftrag> query = cb.createQuery(Zahlungsauftrag.class);
		Root<Zahlungsauftrag> root = query.from(Zahlungsauftrag.class);
		query.orderBy(cb.desc(root.get(Zahlungsauftrag_.datumFaellig)));
		List<Zahlungsauftrag> criteriaResults = persistence.getCriteriaResults(query, 1);
		if (!criteriaResults.isEmpty()) {
			return Optional.of(criteriaResults.get(0));
		}
		return Optional.empty();
	}

	@Override
	@RolesAllowed(value = {UserRoleName.SUPER_ADMIN, UserRoleName.ADMIN, UserRoleName.SACHBEARBEITER_JA})
	public Zahlungsauftrag zahlungsauftragAusloesen(String auftragId) {
		Objects.requireNonNull(auftragId, "auftragId muss gesetzt sein");
		Zahlungsauftrag zahlungsauftrag = persistence.find(Zahlungsauftrag.class, auftragId);
		zahlungsauftrag.setAusgeloest(Boolean.TRUE);
		return persistence.merge(zahlungsauftrag);
	}

	@Override
	@RolesAllowed(value = {UserRoleName.SUPER_ADMIN, UserRoleName.ADMIN, UserRoleName.SACHBEARBEITER_JA, UserRoleName.SACHBEARBEITER_INSTITUTION, UserRoleName.SACHBEARBEITER_TRAEGERSCHAFT})
	public Optional<Zahlungsauftrag> findZahlungsauftrag(String auftragId) {
		Objects.requireNonNull(auftragId, "auftragId muss gesetzt sein");
		Zahlungsauftrag zahlungsauftrag = persistence.find(Zahlungsauftrag.class, auftragId);
		return Optional.ofNullable(zahlungsauftrag);
	}

	@Override
	@RolesAllowed(value = {UserRoleName.SUPER_ADMIN, UserRoleName.ADMIN, UserRoleName.SACHBEARBEITER_JA})
	public void deleteZahlungsauftrag(String auftragId) {
		Objects.requireNonNull(auftragId, "auftragId muss gesetzt sein");
		Optional<Zahlungsauftrag> auftragOptional = findZahlungsauftrag(auftragId);
		Zahlungsauftrag auftragToRemove = auftragOptional.orElseThrow(() -> new EbeguEntityNotFoundException("deleteZahlungsauftrag", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, auftragId));
		persistence.remove(auftragToRemove);
	}

	@Override
	@RolesAllowed(value = {UserRoleName.SUPER_ADMIN, UserRoleName.ADMIN, UserRoleName.SACHBEARBEITER_JA, UserRoleName.SACHBEARBEITER_INSTITUTION, UserRoleName.SACHBEARBEITER_TRAEGERSCHAFT})
	public Collection<Zahlungsauftrag> getAllZahlungsauftraege() {
		return new ArrayList<>(criteriaQueryHelper.getAll(Zahlungsauftrag.class));
	}

	@Override
	@RolesAllowed(value = {UserRoleName.SUPER_ADMIN, UserRoleName.ADMIN, UserRoleName.SACHBEARBEITER_JA})
	public void createIsoFile(String auftragId) {
		//TODO implement
		LOG.info("Creation of ISO File ...");
	}

	@Override
	@RolesAllowed(value = {UserRoleName.SUPER_ADMIN, UserRoleName.ADMIN, UserRoleName.SACHBEARBEITER_JA, UserRoleName.SACHBEARBEITER_INSTITUTION, UserRoleName.SACHBEARBEITER_TRAEGERSCHAFT})
	public Zahlung zahlungBestaetigen(String zahlungId) {
		Objects.requireNonNull(zahlungId, "zahlungId muss gesetzt sein");
		Zahlung zahlung = persistence.find(Zahlung.class, zahlungId);
		zahlung.setStatus(ZahlungStatus.BESTAETIGT);
		return persistence.merge(zahlung);
	}
}


