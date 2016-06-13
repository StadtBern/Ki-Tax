package ch.dvbern.ebegu.api.converter;

import ch.dvbern.ebegu.api.dtos.*;
import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.services.*;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.util.StreamsUtil;
import ch.dvbern.lib.beanvalidation.embeddables.IBAN;
import ch.dvbern.lib.date.DateConvertUtils;
import org.apache.commons.lang3.Validate;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;


@Dependent
@SuppressWarnings({"PMD.NcssTypeCount", "unused"})
public class JaxBConverter {

	@Inject
	private GesuchstellerService gesuchstellerService;
	@Inject
	private AdresseService adresseService;
	@Inject
	private PensumFachstelleService pensumFachstelleService;
	@Inject
	private FachstelleService fachstelleService;
	@Inject
	private GesuchService gesuchService;
	@Inject
	private GesuchsperiodeService gesuchsperiodeService;
	@Inject
	private FinanzielleSituationService finanzielleSituationService;
	@Inject
	private ErwerbspensumService erwerbspensumService;
	@Inject
	private FallService fallService;
	@Inject
	private MandantService mandantService;
	@Inject
	private TraegerschaftService traegerschaftService;
	@Inject
	private InstitutionService institutionService;
	@Inject
	private InstitutionStammdatenService institutionStammdatenService;
	@Inject
	private BetreuungService betreuungService;


	private static final Logger LOG = LoggerFactory.getLogger(JaxBConverter.class);


	@Nonnull
	public String toResourceId(@Nonnull final AbstractEntity entity) {
		return String.valueOf(Objects.requireNonNull(entity.getId()));
	}

	@Nonnull
	public String toEntityId(@Nonnull final JaxId resourceId) {
		// TODO wahrscheinlich besser manuell auf NULL pruefen und gegebenenfalls eine IllegalArgumentException werfen
		return Objects.requireNonNull(resourceId.getId());
	}

	@Nonnull
	public JaxId toJaxId(@Nonnull final AbstractEntity entity) {
		return new JaxId(entity.getId());
	}

	@Nonnull
	public JaxId toJaxId(@Nonnull final JaxAbstractDTO entity) {
		return new JaxId(entity.getId());
	}

	@Nonnull
	private <T extends JaxAbstractDTO> T convertAbstractFieldsToJAX(@Nonnull final AbstractEntity abstEntity, T jaxDTOToConvertTo) {
		jaxDTOToConvertTo.setTimestampErstellt(abstEntity.getTimestampErstellt());
		jaxDTOToConvertTo.setTimestampMutiert(abstEntity.getTimestampMutiert());
		jaxDTOToConvertTo.setId(checkNotNull(abstEntity.getId()));
		return jaxDTOToConvertTo;
	}

	@Nonnull
	private <T extends AbstractEntity> T convertAbstractFieldsToEntity(JaxAbstractDTO jaxToConvert, @Nonnull final T abstEntityToConvertTo) {
		if (jaxToConvert.getId() != null) {
			abstEntityToConvertTo.setId(jaxToConvert.getId());
			//ACHTUNG hier timestamp erstellt und mutiert NICHT  konvertieren da diese immer auf dem server gesetzt werden muessen
		}

		return abstEntityToConvertTo;
	}

	/**
	 * Converts all person related fields from Jax to Entity
	 *
	 * @param personEntityJAXP das objekt als Jax
	 * @param personEntity     das object als Entity
	 */
	private void convertAbstractPersonFieldsToEntity(JaxAbstractPersonDTO personEntityJAXP, AbstractPersonEntity personEntity) {
		convertAbstractFieldsToEntity(personEntityJAXP, personEntity);
		personEntity.setNachname(personEntityJAXP.getNachname());
		personEntity.setVorname(personEntityJAXP.getVorname());
		personEntity.setGeburtsdatum(personEntityJAXP.getGeburtsdatum());
		personEntity.setGeschlecht(personEntityJAXP.getGeschlecht());
	}

	/**
	 * Converts all person related fields from Entity to Jax
	 *
	 * @param personEntity     das object als Entity
	 * @param personEntityJAXP das objekt als Jax
	 */
	private void convertAbstractPersonFieldsToJAX(AbstractPersonEntity personEntity, JaxAbstractPersonDTO personEntityJAXP) {
		convertAbstractFieldsToJAX(personEntity, personEntityJAXP);
		personEntityJAXP.setNachname(personEntity.getNachname());
		personEntityJAXP.setVorname(personEntity.getVorname());
		personEntityJAXP.setGeburtsdatum(personEntity.getGeburtsdatum());
		personEntityJAXP.setGeschlecht(personEntity.getGeschlecht());
	}

	/**
	 * Checks fields gueltigAb and gueltigBis from given object and stores the corresponding DateRange object in the given Jax Object
	 * If gueltigAb is null then current date is set instead
	 * If gueltigBis is null then end_of_time is set instead
	 *
	 * @param dateRangedJAXP   AbstractDateRanged jax where to take the date from
	 * @param dateRangedEntity AbstractDateRanged entity where to store the date into
	 */
	private void convertAbstractDateRangedFieldsToEntity(JaxAbstractDateRangedDTO dateRangedJAXP, AbstractDateRangedEntity dateRangedEntity) {
		convertAbstractFieldsToEntity(dateRangedJAXP, dateRangedEntity);
		LocalDate dateAb = dateRangedJAXP.getGueltigAb() == null ? LocalDate.now() : dateRangedJAXP.getGueltigAb();
		LocalDate dateBis = dateRangedJAXP.getGueltigBis() == null ? Constants.END_OF_TIME : dateRangedJAXP.getGueltigBis();
		dateRangedEntity.setGueltigkeit(new DateRange(dateAb, dateBis));
	}

	/***
	 * Konvertiert eine DateRange fuer den Client. Wenn das DatumBis {@link Constants#END_OF_TIME} entspricht wird es NICHT
	 * konvertiert
	 */
	private void convertAbstractDateRangedFieldsToJAX(@Nonnull AbstractDateRangedEntity dateRangedEntity, @Nonnull JaxAbstractDateRangedDTO jaxDateRanged) {
		Validate.notNull(dateRangedEntity.getGueltigkeit());
		convertAbstractFieldsToJAX(dateRangedEntity, jaxDateRanged);
		jaxDateRanged.setGueltigAb(dateRangedEntity.getGueltigkeit().getGueltigAb());
		if (Constants.END_OF_TIME.equals(dateRangedEntity.getGueltigkeit().getGueltigBis())) {
			jaxDateRanged.setGueltigBis(null); // end of time gueltigkeit wird nicht an client geschickt
		} else{
			jaxDateRanged.setGueltigBis(dateRangedEntity.getGueltigkeit().getGueltigBis());
		}
	}

	private void convertAbstractPensumFieldsToEntity(JaxAbstractPensumDTO jaxPensum, AbstractPensumEntity pensumEntity) {
		convertAbstractDateRangedFieldsToEntity(jaxPensum, pensumEntity);
		pensumEntity.setPensum(jaxPensum.getPensum());
	}

	private void convertAbstractPensumFieldsToJAX(AbstractPensumEntity pensum, JaxAbstractPensumDTO jaxPensum) {
		convertAbstractDateRangedFieldsToJAX(pensum, jaxPensum);
		jaxPensum.setPensum(pensum.getPensum());
	}

	private void convertAbstractAntragFieldsToEntity(JaxAbstractAntragDTO antragJAXP, AbstractAntragEntity antrag) {
		convertAbstractFieldsToEntity(antragJAXP, antrag);
		String exceptionString = "gesuchToEntity";
		Optional<Fall> fallFromDB = fallService.findFall(antragJAXP.getFall().getId());
		if (fallFromDB.isPresent()) {
			antrag.setFall(this.fallToEntity(antragJAXP.getFall(), fallFromDB.get()));
		} else {
			throw new EbeguEntityNotFoundException(exceptionString, ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, antragJAXP.getFall());
		}

		if (antragJAXP.getGesuchsperiode() != null && antragJAXP.getGesuchsperiode().getId() != null) {
			Optional<Gesuchsperiode> gesuchsperiode = gesuchsperiodeService.findGesuchsperiode(antragJAXP.getGesuchsperiode().getId());
			if (gesuchsperiode.isPresent()) {
				antrag.setGesuchsperiode(gesuchsperiodeToEntity(antragJAXP.getGesuchsperiode(), gesuchsperiode.get()));
			} else {
				throw new EbeguEntityNotFoundException(exceptionString, ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, antragJAXP.getGesuchsperiode().getId());
			}
		}
		antrag.setEingangsdatum(antragJAXP.getEingangsdatum());
	}

	private void convertAbstractAntragFieldsToJAX(AbstractAntragEntity antrag, JaxAbstractAntragDTO antragJAXP) {
		convertAbstractFieldsToJAX(antrag, antragJAXP);
		antragJAXP.setFall(this.fallToJAX(antrag.getFall()));
		if (antrag.getGesuchsperiode() != null) {
			antragJAXP.setGesuchsperiode(gesuchsperiodeToJAX(antrag.getGesuchsperiode()));
		}
		antragJAXP.setEingangsdatum(antrag.getEingangsdatum());
	}

	@Nonnull
	public JaxApplicationProperties applicationPropertyToJAX(@Nonnull final ApplicationProperty applicationProperty) {
		JaxApplicationProperties jaxProperty = new JaxApplicationProperties();
		convertAbstractFieldsToJAX(applicationProperty, jaxProperty);
		jaxProperty.setName(applicationProperty.getName());
		jaxProperty.setValue(applicationProperty.getValue());
		return jaxProperty;
	}

	@Nonnull
	public ApplicationProperty applicationPropertieToEntity(JaxApplicationProperties jaxAP, @Nonnull final ApplicationProperty applicationProperty) {
		Validate.notNull(applicationProperty);
		Validate.notNull(jaxAP);
		convertAbstractFieldsToEntity(jaxAP, applicationProperty);
		applicationProperty.setName(jaxAP.getName());
		applicationProperty.setValue(jaxAP.getValue());

		return applicationProperty;
	}

	@Nonnull
	public GesuchstellerAdresse adresseToEntity(@Nonnull JaxAdresse jaxAdresse, @Nonnull final GesuchstellerAdresse gesuchstellerAdresse) {
		Validate.notNull(gesuchstellerAdresse);
		Validate.notNull(jaxAdresse);
		convertAbstractDateRangedFieldsToEntity(jaxAdresse, gesuchstellerAdresse);
		gesuchstellerAdresse.setStrasse(jaxAdresse.getStrasse());
		gesuchstellerAdresse.setHausnummer(jaxAdresse.getHausnummer());
		gesuchstellerAdresse.setZusatzzeile(jaxAdresse.getZusatzzeile());
		gesuchstellerAdresse.setPlz(jaxAdresse.getPlz());
		gesuchstellerAdresse.setOrt(jaxAdresse.getOrt());
		gesuchstellerAdresse.setGemeinde(jaxAdresse.getGemeinde());
		gesuchstellerAdresse.setLand(jaxAdresse.getLand());
		//adresse gilt per default von start of time an
		gesuchstellerAdresse.getGueltigkeit().setGueltigAb(jaxAdresse.getGueltigAb() == null ? Constants.START_OF_TIME : jaxAdresse.getGueltigAb());
		gesuchstellerAdresse.setAdresseTyp(jaxAdresse.getAdresseTyp());

		return gesuchstellerAdresse;
	}

	@Nonnull
	public JaxAdresse adresseToJAX(@Nonnull final GesuchstellerAdresse gesuchstellerAdresse) {
		JaxAdresse jaxAdresse = new JaxAdresse();
		convertAbstractDateRangedFieldsToJAX(gesuchstellerAdresse, jaxAdresse);
		jaxAdresse.setStrasse(gesuchstellerAdresse.getStrasse());
		jaxAdresse.setHausnummer(gesuchstellerAdresse.getHausnummer());
		jaxAdresse.setZusatzzeile(gesuchstellerAdresse.getZusatzzeile());
		jaxAdresse.setPlz(gesuchstellerAdresse.getPlz());
		jaxAdresse.setOrt(gesuchstellerAdresse.getOrt());
		jaxAdresse.setGemeinde(gesuchstellerAdresse.getGemeinde());
		jaxAdresse.setLand(gesuchstellerAdresse.getLand());
		jaxAdresse.setAdresseTyp(gesuchstellerAdresse.getAdresseTyp());
		return jaxAdresse;
	}

	@Nonnull
	public JaxEnversRevision enversRevisionToJAX(@Nonnull final DefaultRevisionEntity revisionEntity,
												 @Nonnull final AbstractEntity abstractEntity, RevisionType accessType) {

		JaxEnversRevision jaxEnversRevision = new JaxEnversRevision();
		if (abstractEntity instanceof ApplicationProperty) {
			jaxEnversRevision.setEntity(applicationPropertyToJAX((ApplicationProperty) abstractEntity));
		}
		jaxEnversRevision.setRev(revisionEntity.getId());
		jaxEnversRevision.setRevTimeStamp(DateConvertUtils.asLocalDateTime(revisionEntity.getRevisionDate()));
		jaxEnversRevision.setAccessType(accessType);
		return jaxEnversRevision;
	}

	public Gesuchsteller gesuchstellerToEntity(@Nonnull JaxGesuchsteller gesuchstellerJAXP, @Nonnull Gesuchsteller gesuchsteller) {
		Validate.notNull(gesuchsteller);
		Validate.notNull(gesuchstellerJAXP);
		Validate.notNull(gesuchstellerJAXP.getWohnAdresse(), "Wohnadresse muss gesetzt sein");
		convertAbstractPersonFieldsToEntity(gesuchstellerJAXP, gesuchsteller);
		gesuchsteller.setMail(gesuchstellerJAXP.getMail());
		gesuchsteller.setTelefon(gesuchstellerJAXP.getTelefon());
		gesuchsteller.setMobile(gesuchstellerJAXP.getMobile());
		gesuchsteller.setTelefonAusland(gesuchstellerJAXP.getTelefonAusland());
		gesuchsteller.setZpvNumber(gesuchstellerJAXP.getZpvNumber());

		//Relationen
		//Wir fuehren derzeit immer maximal  eine alternative Korrespondenzadressse -> diese updaten wenn vorhanden
		if (gesuchstellerJAXP.getAlternativeAdresse() != null) {
			GesuchstellerAdresse currentAltAdr = adresseService.getKorrespondenzAdr(gesuchsteller.getId()).orElse(new GesuchstellerAdresse());
			GesuchstellerAdresse altAddrToMerge = adresseToEntity(gesuchstellerJAXP.getAlternativeAdresse(), currentAltAdr);
			gesuchsteller.addAdresse(altAddrToMerge);
		}
		// Umzug und Wohnadresse
		GesuchstellerAdresse umzugAddr = null;
		if (gesuchstellerJAXP.getUmzugAdresse() != null) {
			umzugAddr = toStoreableAddresse(gesuchstellerJAXP.getUmzugAdresse());
			gesuchsteller.addAdresse(umzugAddr);
		}
		//Wohnadresse (abh von Umzug noch datum setzten)
		GesuchstellerAdresse wohnAddrToMerge = toStoreableAddresse(gesuchstellerJAXP.getWohnAdresse());
		if (umzugAddr != null) {
			wohnAddrToMerge.getGueltigkeit().endOnDayBefore(umzugAddr.getGueltigkeit());
		}
		// Finanzielle Situation
		gesuchsteller.addAdresse(wohnAddrToMerge);
		if (gesuchstellerJAXP.getFinanzielleSituationContainer() != null) {
			gesuchsteller.setFinanzielleSituationContainer(finanzielleSituationContainerToStorableEntity(gesuchstellerJAXP.getFinanzielleSituationContainer()));
		}

		//Erwerbspensum
		gesuchstellerJAXP.getErwerbspensenContainers()
			.stream()
			.map(this::erwerbspensumContainerToStoreableEntity)
			.forEach(gesuchsteller::addErwerbspensumContainer);
		return gesuchsteller;
	}

	@Nonnull
	private GesuchstellerAdresse toStoreableAddresse(@Nonnull JaxAdresse adresseToPrepareForSaving) {
		GesuchstellerAdresse adrToMergeWith = new GesuchstellerAdresse();
		if (adresseToPrepareForSaving.getId() != null) {

			Optional<GesuchstellerAdresse> altAdr = adresseService.findAdresse(adresseToPrepareForSaving.getId());
			//wenn schon vorhanden updaten
			if (altAdr.isPresent()) {
				adrToMergeWith = altAdr.get();
			}
		}
		return adresseToEntity(adresseToPrepareForSaving, adrToMergeWith);
	}

	@Nonnull
	public JaxGesuchsteller gesuchstellerToJAX(@Nonnull Gesuchsteller persistedGesuchsteller) {
		Validate.isTrue(!persistedGesuchsteller.isNew(), "Gesuchsteller kann nicht nach REST transformiert werden weil sie noch " +
			"nicht persistiert wurde; Grund dafuer ist, dass wir die aktuelle Wohnadresse aus der Datenbank lesen wollen");
		JaxGesuchsteller jaxGesuchsteller = new JaxGesuchsteller();
		convertAbstractPersonFieldsToJAX(persistedGesuchsteller, jaxGesuchsteller);
		jaxGesuchsteller.setMail(persistedGesuchsteller.getMail());
		jaxGesuchsteller.setTelefon(persistedGesuchsteller.getTelefon());
		jaxGesuchsteller.setMobile(persistedGesuchsteller.getMobile());
		jaxGesuchsteller.setTelefonAusland(persistedGesuchsteller.getTelefonAusland());
		jaxGesuchsteller.setZpvNumber(persistedGesuchsteller.getZpvNumber());
		//relationen laden
		Optional<GesuchstellerAdresse> altAdr = adresseService.getKorrespondenzAdr(persistedGesuchsteller.getId());
		altAdr.ifPresent(adresse -> jaxGesuchsteller.setAlternativeAdresse(adresseToJAX(adresse)));
		GesuchstellerAdresse currentWohnadr = adresseService.getCurrentWohnadresse(persistedGesuchsteller.getId());
		jaxGesuchsteller.setWohnAdresse(adresseToJAX(currentWohnadr));

		//wenn heute gueltige Adresse von der Adresse divergiert die bis End of Time gilt dann wurde ein Umzug angegeben
		Optional<GesuchstellerAdresse> maybeUmzugadresse = adresseService.getNewestWohnadresse(persistedGesuchsteller.getId());
		maybeUmzugadresse.filter(umzugAdresse -> !currentWohnadr.equals(umzugAdresse))
			.ifPresent(umzugAdr -> jaxGesuchsteller.setUmzugAdresse(adresseToJAX(umzugAdr)));
		// Finanzielle Situation
		if (persistedGesuchsteller.getFinanzielleSituationContainer() != null) {
			JaxFinanzielleSituationContainer jaxFinanzielleSituationContainer = finanzielleSituationContainerToJAX(persistedGesuchsteller.getFinanzielleSituationContainer());
			jaxGesuchsteller.setFinanzielleSituationContainer(jaxFinanzielleSituationContainer);
		}
		// Erwerbspensen
		Collection<ErwerbspensumContainer> persistedPensen = erwerbspensumService.findErwerbspensenForGesuchsteller(persistedGesuchsteller);
		List<JaxErwerbspensumContainer> listOfPensen = persistedPensen.stream().map(this::erwerbspensumContainerToJAX).collect(Collectors.toList());
		jaxGesuchsteller.setErwerbspensenContainers(listOfPensen);
		return jaxGesuchsteller;
	}

	public Familiensituation familiensituationToEntity(@Nonnull JaxFamilienSituation familiensituationJAXP, @Nonnull Familiensituation familiensituation) {
		Validate.notNull(familiensituation);
		Validate.notNull(familiensituationJAXP);
		convertAbstractFieldsToEntity(familiensituationJAXP, familiensituation);
		familiensituation.setFamilienstatus(familiensituationJAXP.getFamilienstatus());
		familiensituation.setGesuchstellerKardinalitaet(familiensituationJAXP.getGesuchstellerKardinalitaet());
		familiensituation.setBemerkungen(familiensituationJAXP.getBemerkungen());
		familiensituation.setGesuch(this.gesuchToEntity(familiensituationJAXP.getGesuch(), new Gesuch())); //todo imanol sollte Gesuch nicht aus der DB geholt werden?
		familiensituation.setGemeinsameSteuererklaerung(familiensituationJAXP.getGemeinsameSteuererklaerung());
		return familiensituation;
	}

	public JaxFamilienSituation familiensituationToJAX(@Nonnull Familiensituation persistedFamiliensituation) {
		JaxFamilienSituation jaxFamiliensituation = new JaxFamilienSituation();
		convertAbstractFieldsToJAX(persistedFamiliensituation, jaxFamiliensituation);
		jaxFamiliensituation.setFamilienstatus(persistedFamiliensituation.getFamilienstatus());
		jaxFamiliensituation.setGesuchstellerKardinalitaet(persistedFamiliensituation.getGesuchstellerKardinalitaet());
		jaxFamiliensituation.setBemerkungen(persistedFamiliensituation.getBemerkungen());
		jaxFamiliensituation.setGesuch(this.gesuchToJAX(persistedFamiliensituation.getGesuch()));
		jaxFamiliensituation.setGemeinsameSteuererklaerung(persistedFamiliensituation.getGemeinsameSteuererklaerung());
		return jaxFamiliensituation;
	}

	public Fall fallToEntity(@Nonnull JaxFall fallJAXP, @Nonnull Fall fall) {
		Validate.notNull(fall);
		Validate.notNull(fallJAXP);
		convertAbstractFieldsToEntity(fallJAXP, fall);
		fall.setFallNummer(fallJAXP.getFallNummer());
		return fall;
	}

	public JaxFall fallToJAX(@Nonnull Fall persistedFall) {
		JaxFall jaxFall = new JaxFall();
		convertAbstractFieldsToJAX(persistedFall, jaxFall);
		jaxFall.setFallNummer(persistedFall.getFallNummer());
		return jaxFall;
	}

	public Gesuch gesuchToEntity(@Nonnull JaxGesuch gesuchJAXP, @Nonnull Gesuch gesuch) {
		Validate.notNull(gesuch);
		Validate.notNull(gesuchJAXP);
		convertAbstractAntragFieldsToEntity(gesuchJAXP, gesuch);

		String exceptionString = "gesuchToEntity";

		if (gesuchJAXP.getGesuchsteller1() != null && gesuchJAXP.getGesuchsteller1().getId() != null) {
			Optional<Gesuchsteller> gesuchsteller1 = gesuchstellerService.findGesuchsteller(gesuchJAXP.getGesuchsteller1().getId());
			if (gesuchsteller1.isPresent()) {
				gesuch.setGesuchsteller1(gesuchstellerToEntity(gesuchJAXP.getGesuchsteller1(), gesuchsteller1.get()));
			} else {
				throw new EbeguEntityNotFoundException(exceptionString, ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, gesuchJAXP.getGesuchsteller1());
			}
		}
		if (gesuchJAXP.getGesuchsteller2() != null && gesuchJAXP.getGesuchsteller2().getId() != null) {
			Optional<Gesuchsteller> gesuchsteller2 = gesuchstellerService.findGesuchsteller(gesuchJAXP.getGesuchsteller2().getId());
			if (gesuchsteller2.isPresent()) {
				gesuch.setGesuchsteller2(gesuchstellerToEntity(gesuchJAXP.getGesuchsteller2(), gesuchsteller2.get()));
			} else {
				throw new EbeguEntityNotFoundException(exceptionString, ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, gesuchJAXP.getGesuchsteller2().getId());
			}
		}
		gesuch.setEinkommensverschlechterung(gesuchJAXP.getEinkommensverschlechterung());

		return gesuch;
	}

	public JaxGesuch gesuchToJAX(@Nonnull Gesuch persistedGesuch) {
		JaxGesuch jaxGesuch = new JaxGesuch();
		convertAbstractAntragFieldsToJAX(persistedGesuch, jaxGesuch);

		if (persistedGesuch.getGesuchsteller1() != null) {
			jaxGesuch.setGesuchsteller1(this.gesuchstellerToJAX(persistedGesuch.getGesuchsteller1()));
		}
		if (persistedGesuch.getGesuchsteller2() != null) {
			jaxGesuch.setGesuchsteller2(this.gesuchstellerToJAX(persistedGesuch.getGesuchsteller2()));
		}
		for (KindContainer kind : persistedGesuch.getKindContainers()) {
			jaxGesuch.getKinder().add(kindContainerToJAX(kind));
		}
		jaxGesuch.setEinkommensverschlechterung(persistedGesuch.getEinkommensverschlechterung());

		return jaxGesuch;
	}

	public JaxMandant mandantToJAX(@Nonnull Mandant persistedMandant) {
		JaxMandant jaxMandant = new JaxMandant();
		convertAbstractFieldsToJAX(persistedMandant, jaxMandant);
		jaxMandant.setName(persistedMandant.getName());
		return jaxMandant;
	}

	public JaxTraegerschaft traegerschaftToJAX(Traegerschaft persistedTraegerschaft) {
		JaxTraegerschaft jaxTraegerschaft = new JaxTraegerschaft();
		convertAbstractFieldsToJAX(persistedTraegerschaft, jaxTraegerschaft);
		jaxTraegerschaft.setName(persistedTraegerschaft.getName());
		return jaxTraegerschaft;
	}

	public Mandant mandantToEntity(JaxMandant mandantJAXP, Mandant mandant) {
		Validate.notNull(mandant);
		Validate.notNull(mandantJAXP);
		convertAbstractFieldsToEntity(mandantJAXP, mandant);
		mandant.setName(mandantJAXP.getName());
		return mandant;
	}

	public Traegerschaft traegerschaftToEntity(@Nonnull JaxTraegerschaft traegerschaftJAXP, @Nonnull Traegerschaft traegerschaft) {
		Validate.notNull(traegerschaft);
		Validate.notNull(traegerschaftJAXP);
		convertAbstractFieldsToEntity(traegerschaftJAXP, traegerschaft);
		traegerschaft.setName(traegerschaftJAXP.getName());
		return traegerschaft;
	}

	public Fachstelle fachstelleToEntity(JaxFachstelle fachstelleJAXP, Fachstelle fachstelle) {
		Validate.notNull(fachstelleJAXP);
		Validate.notNull(fachstelle);
		convertAbstractFieldsToEntity(fachstelleJAXP, fachstelle);
		fachstelle.setName(fachstelleJAXP.getName());
		fachstelle.setBeschreibung(fachstelleJAXP.getBeschreibung());
		fachstelle.setBehinderungsbestaetigung(fachstelleJAXP.isBehinderungsbestaetigung());
		return fachstelle;
	}

	public JaxFachstelle fachstelleToJAX(@Nonnull Fachstelle persistedFachstelle) {
		JaxFachstelle jaxFachstelle = new JaxFachstelle();
		convertAbstractFieldsToJAX(persistedFachstelle, jaxFachstelle);
		jaxFachstelle.setName(persistedFachstelle.getName());
		jaxFachstelle.setBeschreibung(persistedFachstelle.getBeschreibung());
		jaxFachstelle.setBehinderungsbestaetigung(persistedFachstelle.isBehinderungsbestaetigung());
		return jaxFachstelle;
	}


	public JaxInstitution institutionToJAX(Institution persistedInstitution) {
		JaxInstitution jaxInstitution = new JaxInstitution();
		convertAbstractFieldsToJAX(persistedInstitution, jaxInstitution);
		jaxInstitution.setName(persistedInstitution.getName());
		jaxInstitution.setMandant(mandantToJAX(persistedInstitution.getMandant()));
		jaxInstitution.setTraegerschaft(traegerschaftToJAX(persistedInstitution.getTraegerschaft()));
		return jaxInstitution;
	}

	public Institution institutionToEntity(JaxInstitution institutionJAXP, Institution institution) {
		Validate.notNull(institutionJAXP);
		Validate.notNull(institution);
		convertAbstractFieldsToEntity(institutionJAXP, institution);
		institution.setName(institutionJAXP.getName());

		if (institutionJAXP.getMandant().getId() != null) {
			Optional<Mandant> mandantFromDB = mandantService.findMandant(institutionJAXP.getMandant().getId());
			if (mandantFromDB.isPresent()) {
				institution.setMandant(mandantToEntity(institutionJAXP.getMandant(), mandantFromDB.get()));
			} else {
				throw new EbeguEntityNotFoundException("institutionToEntity -> mandant", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, institutionJAXP.getMandant().getId());
			}

		} else {
			//todo homa ebegu 82 review wie reagieren wir hier
			throw new EbeguEntityNotFoundException("institutionToEntity -> mandant", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND);
//			institution.setMandant(mandantToEntity(institutionJAXP.getMandant(), new Mandant()));
		}

		if (institutionJAXP.getTraegerschaft().getId() != null) {
			Optional<Traegerschaft> traegerschaftFromDB = traegerschaftService.findTraegerschaft(institutionJAXP.getTraegerschaft().getId());
			if (traegerschaftFromDB.isPresent()) {
				institution.setTraegerschaft(traegerschaftToEntity(institutionJAXP.getTraegerschaft(), traegerschaftFromDB.get()));
			} else {
				throw new EbeguEntityNotFoundException("institutionToEntity -> traegerschaft", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, institutionJAXP.getTraegerschaft().getId());
			}
		} else {
			//todo homa ebegu 82 review wie reagieren wir hier
			throw new EbeguEntityNotFoundException("institutionToEntity -> traegerschaft", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND);
//			institution.setTraegerschaft(traegerschaftToEntity(institutionJAXP.getTraegerschaft(), new Traegerschaft()));
		}
		return institution;
	}

	public JaxInstitutionStammdaten institutionStammdatenToJAX(@Nonnull InstitutionStammdaten persistedInstStammdaten) {
		JaxInstitutionStammdaten jaxInstStammdaten = new JaxInstitutionStammdaten();
		convertAbstractDateRangedFieldsToJAX(persistedInstStammdaten, jaxInstStammdaten);
		jaxInstStammdaten.setOeffnungstage(persistedInstStammdaten.getOeffnungstage());
		jaxInstStammdaten.setOeffnungsstunden(persistedInstStammdaten.getOeffnungsstunden());
		jaxInstStammdaten.setIban(persistedInstStammdaten.getIban().getIban());
		jaxInstStammdaten.setBetreuungsangebotTyp(persistedInstStammdaten.getBetreuungsangebotTyp());
		jaxInstStammdaten.setInstitution(institutionToJAX(persistedInstStammdaten.getInstitution()));
		return jaxInstStammdaten;
	}

	public InstitutionStammdaten institutionStammdatenToEntity(JaxInstitutionStammdaten institutionStammdatenJAXP, InstitutionStammdaten institutionStammdaten) {
		Validate.notNull(institutionStammdatenJAXP);
		Validate.notNull(institutionStammdaten);

		convertAbstractDateRangedFieldsToEntity(institutionStammdatenJAXP, institutionStammdaten);
		institutionStammdaten.setOeffnungstage(institutionStammdatenJAXP.getOeffnungstage());
		institutionStammdaten.setOeffnungsstunden(institutionStammdatenJAXP.getOeffnungsstunden());
		institutionStammdaten.setIban(new IBAN(institutionStammdatenJAXP.getIban()));
		institutionStammdaten.setBetreuungsangebotTyp(institutionStammdatenJAXP.getBetreuungsangebotTyp());

		Optional<Institution> institutionFromDB = institutionService.findInstitution(institutionStammdatenJAXP.getInstitution().getId());
		if (institutionFromDB.isPresent()) {
			institutionStammdaten.setInstitution(institutionToEntity(institutionStammdatenJAXP.getInstitution(), institutionFromDB.get()));
		} else {
			throw new EbeguEntityNotFoundException("institutionStammdatenToEntity", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, institutionStammdatenJAXP.getInstitution().getId());
		}

		return institutionStammdaten;

	}

	public FinanzielleSituationContainer finanzielleSituationContainerToStorableEntity(@Nonnull JaxFinanzielleSituationContainer containerJAX) {
		Validate.notNull(containerJAX);
		FinanzielleSituationContainer containerToMergeWith = new FinanzielleSituationContainer();
		if (containerJAX.getId() != null) {
			Optional<FinanzielleSituationContainer> existingFSC = finanzielleSituationService.findFinanzielleSituation(containerJAX.getId());
			if (existingFSC.isPresent()) {
				containerToMergeWith = existingFSC.get();
			}
		}
		FinanzielleSituationContainer mergedContainer = finanzielleSituationContainerToEntity(containerJAX, containerToMergeWith);
		return mergedContainer;
	}

	@Nonnull
	public JaxKind kindToJAX(@Nonnull Kind persistedKind) {
		JaxKind jaxKind = new JaxKind();
		convertAbstractPersonFieldsToJAX(persistedKind, jaxKind);
		jaxKind.setWohnhaftImGleichenHaushalt(persistedKind.getWohnhaftImGleichenHaushalt());
		jaxKind.setUnterstuetzungspflicht(persistedKind.getUnterstuetzungspflicht());
		jaxKind.setFamilienErgaenzendeBetreuung(persistedKind.getFamilienErgaenzendeBetreuung());
		jaxKind.setMutterspracheDeutsch(persistedKind.getMutterspracheDeutsch());
		jaxKind.setPensumFachstelle(pensumFachstelleToJax(persistedKind.getPensumFachstelle()));
		jaxKind.setBemerkungen(persistedKind.getBemerkungen());
		return jaxKind;
	}

	@Nullable
	public JaxPensumFachstelle pensumFachstelleToJax(@Nullable PensumFachstelle persistedPensumFachstelle) {
		if (persistedPensumFachstelle == null) {
			return null;
		}
		JaxPensumFachstelle jaxPensumFachstelle = new JaxPensumFachstelle();
		convertAbstractPensumFieldsToJAX(persistedPensumFachstelle, jaxPensumFachstelle);
		jaxPensumFachstelle.setFachstelle(fachstelleToJAX(persistedPensumFachstelle.getFachstelle()));
		return jaxPensumFachstelle;
	}

	public PensumFachstelle pensumFachstelleToEntity(JaxPensumFachstelle pensumFachstelleJAXP, PensumFachstelle pensumFachstelle) {
		Validate.notNull(pensumFachstelleJAXP.getFachstelle(), "Fachstelle muss existieren");
		Validate.notNull(pensumFachstelleJAXP.getFachstelle().getId(), "Fachstelle muss bereits gespeichert sein");
		convertAbstractPensumFieldsToEntity(pensumFachstelleJAXP, pensumFachstelle);

		Optional<Fachstelle> fachstelleFromDB = fachstelleService.findFachstelle(pensumFachstelleJAXP.getFachstelle().getId());
		if (fachstelleFromDB.isPresent()) {
			pensumFachstelle.setFachstelle(fachstelleToEntity(pensumFachstelleJAXP.getFachstelle(), fachstelleFromDB.get()));
		} else {
			throw new EbeguEntityNotFoundException("pensumFachstelleToEntity", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, pensumFachstelleJAXP.getFachstelle().getId());
		}

		return pensumFachstelle;
	}

	private PensumFachstelle toStorablePensumFachstelle(@Nonnull JaxPensumFachstelle pensumFsToSave) {
		PensumFachstelle pensumToMergeWith = new PensumFachstelle();
		if (pensumFsToSave.getId() != null) {
			Optional<PensumFachstelle> pensumFachstelleOpt = pensumFachstelleService.findPensumFachstelle(pensumFsToSave.getId());
			if (pensumFachstelleOpt.isPresent()) {
				pensumToMergeWith = pensumFachstelleOpt.get();
			} else {
				throw new EbeguEntityNotFoundException("toStorablePensumFachstelle", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, pensumFsToSave.getId());
			}
		}
		return pensumFachstelleToEntity(pensumFsToSave, pensumToMergeWith);
	}


	public JaxKindContainer kindContainerToJAX(KindContainer persistedKind) {
		JaxKindContainer jaxKindContainer = new JaxKindContainer();
		convertAbstractFieldsToJAX(persistedKind, jaxKindContainer);
		if (persistedKind.getKindGS() != null) {
			jaxKindContainer.setKindGS(kindToJAX(persistedKind.getKindGS()));
		}
		if (persistedKind.getKindJA() != null) {
			jaxKindContainer.setKindJA(kindToJAX(persistedKind.getKindJA()));
		}
		jaxKindContainer.setBetreuungen(betreuungListToJax(persistedKind.getBetreuungen()));
		return jaxKindContainer;
	}

	public Kind kindToEntity(JaxKind kindJAXP, Kind kind) {
		Validate.notNull(kindJAXP);
		Validate.notNull(kind);
		convertAbstractPersonFieldsToEntity(kindJAXP, kind);
		kind.setWohnhaftImGleichenHaushalt(kindJAXP.getWohnhaftImGleichenHaushalt());
		kind.setUnterstuetzungspflicht(kindJAXP.getUnterstuetzungspflicht());
		kind.setFamilienErgaenzendeBetreuung(kindJAXP.getFamilienErgaenzendeBetreuung());
		kind.setMutterspracheDeutsch(kindJAXP.getMutterspracheDeutsch());

		PensumFachstelle updtPensumFachstelle = null;
		if (kindJAXP.getPensumFachstelle() != null) {
			updtPensumFachstelle = toStorablePensumFachstelle(kindJAXP.getPensumFachstelle());
		}
		kind.setPensumFachstelle(updtPensumFachstelle);

		kind.setBemerkungen(kindJAXP.getBemerkungen());
		return kind;
	}

	public KindContainer kindContainerToEntity(@Nonnull JaxKindContainer kindContainerJAXP, @Nonnull KindContainer kindContainer) {
		Validate.notNull(kindContainer);
		Validate.notNull(kindContainerJAXP);
		convertAbstractFieldsToEntity(kindContainerJAXP, kindContainer);
		//kind daten koennen nicht verschwinden
		if (kindContainerJAXP.getKindGS() != null) {
			Kind kindGS = new Kind();
			if (kindContainer.getKindGS() != null) {
				kindGS = kindContainer.getKindGS();
			}
			kindContainer.setKindGS(kindToEntity(kindContainerJAXP.getKindGS(), kindGS));
		}
		if (kindContainerJAXP.getKindJA() != null) {
			Kind kindJA = new Kind();
			if (kindContainer.getKindJA() != null) {
				kindJA = kindContainer.getKindJA();
			}
			kindContainer.setKindJA(kindToEntity(kindContainerJAXP.getKindJA(), kindJA));
		}
		return kindContainer;
	}

	/**
	 * Sucht die Fachstelle in der DB und fuegt sie mit der als Parameter gegebenen Fachstelle zusammen.
	 * Sollte sie in der DB nicht existieren, gibt die Methode eine neue Fachstelle mit den gegebenen Daten zurueck
	 *
	 * @param fachstelleToFind die Fachstelle als JAX
	 * @return die Fachstelle als Entity
	 */
	@Nonnull
	public Fachstelle fachstelleToStoreableEntity(@Nonnull JaxFachstelle fachstelleToFind) {
		Validate.notNull(fachstelleToFind);
		Fachstelle fachstelleToMergeWith = new Fachstelle();
		if (fachstelleToFind.getId() != null) {
			Optional<Fachstelle> altFachstelle = fachstelleService.findFachstelle(fachstelleToFind.getId());
			if (altFachstelle.isPresent()) {
				fachstelleToMergeWith = altFachstelle.get();
			}
		}
		return fachstelleToEntity(fachstelleToFind, fachstelleToMergeWith);
	}

	/**
	 * Sucht das Gesuch in der DB und fuegt es mit dem als Parameter gegebenen Gesuch zusammen.
	 * Sollte es in der DB nicht existieren, gibt die Methode ein neues Gesuch mit den gegebenen Daten zurueck
	 *
	 * @param gesuchToFind das Gesuch als JAX
	 * @return das Gesuch als Entity
	 */
	@Nonnull
	public Gesuch gesuchToStoreableEntity(JaxGesuch gesuchToFind) {
		Validate.notNull(gesuchToFind);
		Gesuch gesuchToMergeWith = new Gesuch();
		if (gesuchToFind.getId() != null) {
			Optional<Gesuch> altGesuch = gesuchService.findGesuch(gesuchToFind.getId());
			if (altGesuch.isPresent()) {
				gesuchToMergeWith = altGesuch.get();
			}
		}
		return gesuchToEntity(gesuchToFind, gesuchToMergeWith);
	}

	private FinanzielleSituationContainer finanzielleSituationContainerToEntity(@Nonnull JaxFinanzielleSituationContainer containerJAX,
																				@Nonnull FinanzielleSituationContainer container) {
		Validate.notNull(container);
		Validate.notNull(containerJAX);
		convertAbstractFieldsToEntity(containerJAX, container);
		container.setJahr(containerJAX.getJahr());
		FinanzielleSituation finSitToMergeWith;
		//Im moment kann eine einmal gespeicherte Finanzielle Situation nicht mehr entfernt werden.
		if (containerJAX.getFinanzielleSituationGS() != null) {
			finSitToMergeWith = Optional.ofNullable(container.getFinanzielleSituationGS()).orElse(new FinanzielleSituation());
			container.setFinanzielleSituationGS(finanzielleSituationToEntity(containerJAX.getFinanzielleSituationGS(), finSitToMergeWith));
		}
		if (containerJAX.getFinanzielleSituationJA() != null) {
			finSitToMergeWith = Optional.ofNullable(container.getFinanzielleSituationJA()).orElse(new FinanzielleSituation());
			container.setFinanzielleSituationJA(finanzielleSituationToEntity(containerJAX.getFinanzielleSituationJA(), finSitToMergeWith));
		}
		if (containerJAX.getFinanzielleSituationSV() != null) {
			finSitToMergeWith = Optional.ofNullable(container.getFinanzielleSituationSV()).orElse(new FinanzielleSituation());
			container.setFinanzielleSituationSV(finanzielleSituationToEntity(containerJAX.getFinanzielleSituationSV(), finSitToMergeWith));
		}
		return container;
	}

	public JaxFinanzielleSituationContainer finanzielleSituationContainerToJAX(FinanzielleSituationContainer persistedFinanzielleSituation) {
		JaxFinanzielleSituationContainer jaxPerson = new JaxFinanzielleSituationContainer();
		convertAbstractFieldsToJAX(persistedFinanzielleSituation, jaxPerson);
		jaxPerson.setJahr(persistedFinanzielleSituation.getJahr());
		jaxPerson.setFinanzielleSituationGS(finanzielleSituationToJAX(persistedFinanzielleSituation.getFinanzielleSituationGS()));
		jaxPerson.setFinanzielleSituationJA(finanzielleSituationToJAX(persistedFinanzielleSituation.getFinanzielleSituationJA()));
		jaxPerson.setFinanzielleSituationSV(finanzielleSituationToJAX(persistedFinanzielleSituation.getFinanzielleSituationSV()));
		return jaxPerson;
	}

	private FinanzielleSituation finanzielleSituationToEntity(@Nonnull JaxFinanzielleSituation finanzielleSituationJAXP, @Nonnull FinanzielleSituation finanzielleSituation) {
		Validate.notNull(finanzielleSituation);
		Validate.notNull(finanzielleSituationJAXP);
		convertAbstractFieldsToEntity(finanzielleSituationJAXP, finanzielleSituation);
		finanzielleSituation.setSteuerveranlagungErhalten(finanzielleSituationJAXP.getSteuerveranlagungErhalten());
		finanzielleSituation.setSteuererklaerungAusgefuellt(finanzielleSituationJAXP.getSteuererklaerungAusgefuellt());
		finanzielleSituation.setNettolohn(finanzielleSituationJAXP.getNettolohn());
		finanzielleSituation.setFamilienzulage(finanzielleSituationJAXP.getFamilienzulage());
		finanzielleSituation.setErsatzeinkommen(finanzielleSituationJAXP.getErsatzeinkommen());
		finanzielleSituation.setErhalteneAlimente(finanzielleSituationJAXP.getErhalteneAlimente());
		finanzielleSituation.setBruttovermoegen(finanzielleSituationJAXP.getBruttovermoegen());
		finanzielleSituation.setSchulden(finanzielleSituationJAXP.getSchulden());
		finanzielleSituation.setSelbstaendig(finanzielleSituationJAXP.getSelbstaendig());
		finanzielleSituation.setGeschaeftsgewinnBasisjahrMinus2(finanzielleSituationJAXP.getGeschaeftsgewinnBasisjahrMinus2());
		finanzielleSituation.setGeschaeftsgewinnBasisjahrMinus1(finanzielleSituationJAXP.getGeschaeftsgewinnBasisjahrMinus1());
		finanzielleSituation.setGeschaeftsgewinnBasisjahr(finanzielleSituationJAXP.getGeschaeftsgewinnBasisjahr());
		finanzielleSituation.setGeleisteteAlimente(finanzielleSituationJAXP.getGeleisteteAlimente());
		return finanzielleSituation;
	}

	private JaxFinanzielleSituation finanzielleSituationToJAX(@Nullable FinanzielleSituation persistedFinanzielleSituation) {
		if (persistedFinanzielleSituation != null) {
			JaxFinanzielleSituation jaxPerson = new JaxFinanzielleSituation();
			convertAbstractFieldsToJAX(persistedFinanzielleSituation, jaxPerson);
			jaxPerson.setSteuerveranlagungErhalten(persistedFinanzielleSituation.getSteuerveranlagungErhalten());
			jaxPerson.setSteuererklaerungAusgefuellt(persistedFinanzielleSituation.getSteuererklaerungAusgefuellt());
			jaxPerson.setNettolohn(persistedFinanzielleSituation.getNettolohn());
			jaxPerson.setFamilienzulage(persistedFinanzielleSituation.getFamilienzulage());
			jaxPerson.setErsatzeinkommen(persistedFinanzielleSituation.getErsatzeinkommen());
			jaxPerson.setErhalteneAlimente(persistedFinanzielleSituation.getErhalteneAlimente());
			jaxPerson.setBruttovermoegen(persistedFinanzielleSituation.getBruttovermoegen());
			jaxPerson.setSchulden(persistedFinanzielleSituation.getSchulden());
			jaxPerson.setSelbstaendig(persistedFinanzielleSituation.getSelbstaendig());
			jaxPerson.setGeschaeftsgewinnBasisjahrMinus2(persistedFinanzielleSituation.getGeschaeftsgewinnBasisjahrMinus2());
			jaxPerson.setGeschaeftsgewinnBasisjahrMinus1(persistedFinanzielleSituation.getGeschaeftsgewinnBasisjahrMinus1());
			jaxPerson.setGeschaeftsgewinnBasisjahr(persistedFinanzielleSituation.getGeschaeftsgewinnBasisjahr());
			jaxPerson.setGeleisteteAlimente(persistedFinanzielleSituation.getGeleisteteAlimente());
			return jaxPerson;
		}
		return null;
	}

	public ErwerbspensumContainer erwerbspensumContainerToStoreableEntity(@Nonnull JaxErwerbspensumContainer jaxEwpCont) {
		Validate.notNull(jaxEwpCont);
		ErwerbspensumContainer containerToMergeWith = new ErwerbspensumContainer();
		if (jaxEwpCont.getId() != null) {
			Optional<ErwerbspensumContainer> existingEwpCont = erwerbspensumService.findErwerbspensum(jaxEwpCont.getId());
			if (existingEwpCont.isPresent()) {
				containerToMergeWith = existingEwpCont.get();
			}
		}
		return erwerbspensumContainerToEntity(jaxEwpCont, containerToMergeWith);

	}

	public ErwerbspensumContainer erwerbspensumContainerToEntity(@Nonnull JaxErwerbspensumContainer jaxEwpCont, @Nonnull ErwerbspensumContainer erwerbspensumCont) {
		Validate.notNull(jaxEwpCont);
		Validate.notNull(erwerbspensumCont);
		convertAbstractFieldsToEntity(jaxEwpCont, erwerbspensumCont);
		Erwerbspensum pensumToMergeWith;
		if (jaxEwpCont.getErwerbspensumGS() != null) {
			pensumToMergeWith = Optional.ofNullable(erwerbspensumCont.getErwerbspensumGS()).orElse(new Erwerbspensum());
			erwerbspensumCont.setErwerbspensumGS(erbwerbspensumToEntity(jaxEwpCont.getErwerbspensumGS(), pensumToMergeWith));
		}
		if (jaxEwpCont.getErwerbspensumJA() != null) {
			pensumToMergeWith = Optional.ofNullable(erwerbspensumCont.getErwerbspensumJA()).orElse(new Erwerbspensum());
			erwerbspensumCont.setErwerbspensumJA(erbwerbspensumToEntity(jaxEwpCont.getErwerbspensumJA(), pensumToMergeWith));
		}

		return erwerbspensumCont;
	}

	@Nonnull
	public JaxErwerbspensumContainer erwerbspensumContainerToJAX(@Nonnull ErwerbspensumContainer storedErwerbspensumCont) {
		Validate.notNull(storedErwerbspensumCont);
		JaxErwerbspensumContainer jaxEwpCont = new JaxErwerbspensumContainer();
		convertAbstractFieldsToJAX(storedErwerbspensumCont, jaxEwpCont);
		jaxEwpCont.setErwerbspensumGS(erbwerbspensumToJax(storedErwerbspensumCont.getErwerbspensumGS()));
		jaxEwpCont.setErwerbspensumJA(erbwerbspensumToJax(storedErwerbspensumCont.getErwerbspensumJA()));
		return jaxEwpCont;
	}

	private Erwerbspensum erbwerbspensumToEntity(@Nonnull JaxErwerbspensum jaxErwerbspensum, @Nonnull Erwerbspensum erwerbspensum) {
		Validate.notNull(jaxErwerbspensum);
		Validate.notNull(erwerbspensum);
		convertAbstractPensumFieldsToEntity(jaxErwerbspensum, erwerbspensum);
		erwerbspensum.setZuschlagZuErwerbspensum(jaxErwerbspensum.getZuschlagZuErwerbspensum());
		erwerbspensum.setZuschlagsgrund(jaxErwerbspensum.getZuschlagsgrund());
		erwerbspensum.setZuschlagsprozent(jaxErwerbspensum.getZuschlagsprozent());
		erwerbspensum.setGesundheitlicheEinschraenkungen(jaxErwerbspensum.getGesundheitlicheEinschraenkungen());
		erwerbspensum.setTaetigkeit(jaxErwerbspensum.getTaetigkeit());
		erwerbspensum.setPensum(jaxErwerbspensum.getPensum());
		return erwerbspensum;
	}

	private JaxErwerbspensum erbwerbspensumToJax(@Nullable Erwerbspensum pensum) {
		if (pensum != null) {
			JaxErwerbspensum jaxErwerbspensum = new JaxErwerbspensum();
			jaxErwerbspensum = convertAbstractFieldsToJAX(pensum, jaxErwerbspensum);
			jaxErwerbspensum.setGueltigAb(pensum.getGueltigkeit().getGueltigAb());
			jaxErwerbspensum.setGueltigBis(pensum.getGueltigkeit().getGueltigBis());
			jaxErwerbspensum.setZuschlagZuErwerbspensum(pensum.getZuschlagZuErwerbspensum());
			jaxErwerbspensum.setZuschlagsgrund(pensum.getZuschlagsgrund());
			jaxErwerbspensum.setZuschlagsprozent(pensum.getZuschlagsprozent());
			jaxErwerbspensum.setGesundheitlicheEinschraenkungen(pensum.getGesundheitlicheEinschraenkungen());
			jaxErwerbspensum.setTaetigkeit(pensum.getTaetigkeit());
			jaxErwerbspensum.setPensum(pensum.getPensum());
			return jaxErwerbspensum;
		}
		return null;
	}

	public Betreuung betreuungToEntity(@Nonnull JaxBetreuung betreuungJAXP, @Nonnull Betreuung betreuung) {
		Validate.notNull(betreuung);
		Validate.notNull(betreuungJAXP);
		convertAbstractFieldsToEntity(betreuungJAXP, betreuung);
		betreuung.setBemerkungen(betreuungJAXP.getBemerkungen());

		betreuungsPensumContainersToEntity(betreuungJAXP.getBetreuungspensumContainers(), betreuung.getBetreuungspensumContainers());
		setBetreuungInbetreuungsPensumContainers(betreuung.getBetreuungspensumContainers(), betreuung);
		betreuung.setBetreuungsstatus(betreuungJAXP.getBetreuungsstatus());
		betreuung.setSchulpflichtig(betreuungJAXP.getSchulpflichtig());
		// InstitutionStammdaten muessen bereits existieren
		if (betreuungJAXP.getInstitutionStammdaten() != null) {
			String instStammdatenID = betreuungJAXP.getInstitutionStammdaten().getId();
			Optional<InstitutionStammdaten> optInstStammdaten = institutionStammdatenService.findInstitutionStammdaten(instStammdatenID);
			InstitutionStammdaten instStammdatenToMerge =
				optInstStammdaten.orElseThrow(() -> new EbeguEntityNotFoundException("betreuungToEntity", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, instStammdatenID));
			betreuung.setInstitutionStammdaten(institutionStammdatenToEntity(betreuungJAXP.getInstitutionStammdaten(), instStammdatenToMerge));
		}
		return betreuung;
	}

	public Betreuung betreuungToStoreableEntity(@Nonnull JaxBetreuung betreuungJAXP) {
		Validate.notNull(betreuungJAXP);
		Betreuung betreuungToMergeWith = new Betreuung();
		if (betreuungJAXP.getId() != null) {
			Optional<Betreuung> optionalBetreuung = betreuungService.findBetreuung(betreuungJAXP.getId());
			betreuungToMergeWith = optionalBetreuung.orElse(new Betreuung());
		}
		return this.betreuungToEntity(betreuungJAXP, betreuungToMergeWith);
	}

	private void setBetreuungInbetreuungsPensumContainers(Set<BetreuungspensumContainer> betreuungspensumContainers, Betreuung betreuung) {
		for (BetreuungspensumContainer betreuungspensumContainer : betreuungspensumContainers) {
			betreuungspensumContainer.setBetreuung(betreuung);
		}
	}

	/**
	 * Goes through the whole list of jaxBetPenContainers. For each (jax)Container that already exists as Entity it merges both and adds the resulting
	 * (jax) container to the list. If the container doesn't exist it creates a new one and adds it to the list. Thus all containers that existed as entity
	 * but not in the list of jax, won't be added to the list and then removed (cascade and orphanremoval)
	 *
	 * @param jaxBetPenContainers Betreuungspensen DTOs from Client
	 * @param existingBetreuungspensen List of currently stored BetreungspensumContainers
	 */
	private void betreuungsPensumContainersToEntity(List<JaxBetreuungspensumContainer> jaxBetPenContainers,
																			   Collection<BetreuungspensumContainer> existingBetreuungspensen) {
		Set<BetreuungspensumContainer> transformedBetPenContainers = new HashSet<>();
		for (JaxBetreuungspensumContainer jaxBetPensContainer : jaxBetPenContainers) {
			BetreuungspensumContainer containerToMergeWith = existingBetreuungspensen
				.stream()
				.filter(existingBetPensumEntity -> existingBetPensumEntity.getId().equals(jaxBetPensContainer.getId()))
				.reduce(StreamsUtil.toOnlyElement())
				.orElse(new BetreuungspensumContainer());
			transformedBetPenContainers.add(betreuungspensumContainerToEntity(jaxBetPensContainer, containerToMergeWith));
		}

		//change the existing collection to reflect changes
		existingBetreuungspensen.clear();
		existingBetreuungspensen.addAll(transformedBetPenContainers);
	}


	private BetreuungspensumContainer betreuungspensumContainerToEntity(JaxBetreuungspensumContainer jaxBetPenContainers, BetreuungspensumContainer bpContainer) {
		Validate.notNull(jaxBetPenContainers);
		Validate.notNull(bpContainer);
		convertAbstractFieldsToEntity(jaxBetPenContainers, bpContainer);
		if (jaxBetPenContainers.getBetreuungspensumGS() != null) {
			Betreuungspensum betPensGS = new Betreuungspensum();
			if (bpContainer.getBetreuungspensumGS() != null) {
				betPensGS = bpContainer.getBetreuungspensumGS();
			}
			bpContainer.setBetreuungspensumGS(betreuungspensumToEntity(jaxBetPenContainers.getBetreuungspensumGS(), betPensGS));
		}
		if (jaxBetPenContainers.getBetreuungspensumJA() != null) {
			Betreuungspensum betPensJA = new Betreuungspensum();
			if (bpContainer.getBetreuungspensumJA() != null) {
				betPensJA = bpContainer.getBetreuungspensumJA();
			}
			bpContainer.setBetreuungspensumJA(betreuungspensumToEntity(jaxBetPenContainers.getBetreuungspensumJA(), betPensJA));
		}
		return bpContainer;
	}

	private Betreuungspensum betreuungspensumToEntity(JaxBetreuungspensum jaxBetreuungspensum, Betreuungspensum betreuungspensum) {
		convertAbstractPensumFieldsToEntity(jaxBetreuungspensum, betreuungspensum);
		return betreuungspensum;
	}

	private Set<JaxBetreuung> betreuungListToJax(Set<Betreuung> betreuungen) {
		Set<JaxBetreuung> jaxBetreuungen = new HashSet<>();
		if (betreuungen != null) {
			jaxBetreuungen.addAll(betreuungen.stream().map(this::betreuungToJAX).collect(Collectors.toList()));
		}
		return jaxBetreuungen;
	}

	public JaxBetreuung betreuungToJAX(Betreuung persistedBetreuung) {
		JaxBetreuung jaxBetreuung = new JaxBetreuung();
		convertAbstractFieldsToJAX(persistedBetreuung, jaxBetreuung);
		jaxBetreuung.setBemerkungen(persistedBetreuung.getBemerkungen());
		jaxBetreuung.setBetreuungspensumContainers(betreuungsPensumContainersToJax(persistedBetreuung.getBetreuungspensumContainers()));
		jaxBetreuung.setBetreuungsstatus(persistedBetreuung.getBetreuungsstatus());
		jaxBetreuung.setSchulpflichtig(persistedBetreuung.getSchulpflichtig());
		jaxBetreuung.setInstitutionStammdaten(institutionStammdatenToJAX(persistedBetreuung.getInstitutionStammdaten()));
		return jaxBetreuung;
	}

	/**
	 * calls betreuungsPensumContainerToJax for each betreuungspensumContainer found in given the list
	 *
	 * @param betreuungspensumContainers
	 * @return
	 */
	private List<JaxBetreuungspensumContainer> betreuungsPensumContainersToJax(Set<BetreuungspensumContainer> betreuungspensumContainers) {
		List<JaxBetreuungspensumContainer> jaxContainers = new ArrayList<>();
		if (betreuungspensumContainers != null) {
			for (BetreuungspensumContainer betreuungspensumContainer : betreuungspensumContainers) {
				jaxContainers.add(betreuungsPensumContainerToJax(betreuungspensumContainer));
			}
		}
		return jaxContainers;
	}

	private JaxBetreuungspensumContainer betreuungsPensumContainerToJax(BetreuungspensumContainer betreuungspensumContainer) {
		if (betreuungspensumContainer != null) {
			JaxBetreuungspensumContainer jaxBetreuungspensumContainer = new JaxBetreuungspensumContainer();
			convertAbstractFieldsToJAX(betreuungspensumContainer, jaxBetreuungspensumContainer);
			if (betreuungspensumContainer.getBetreuungspensumGS() != null) {
				jaxBetreuungspensumContainer.setBetreuungspensumGS(betreuungspensumToJax(betreuungspensumContainer.getBetreuungspensumGS()));
			}
			if (betreuungspensumContainer.getBetreuungspensumJA() != null) {
				jaxBetreuungspensumContainer.setBetreuungspensumJA(betreuungspensumToJax(betreuungspensumContainer.getBetreuungspensumJA()));
			}
			return jaxBetreuungspensumContainer;
		}
		return null;
	}

	private JaxBetreuungspensum betreuungspensumToJax(Betreuungspensum betreuungspensum) {
		JaxBetreuungspensum jaxBetreuungspensum = new JaxBetreuungspensum();
		convertAbstractPensumFieldsToJAX(betreuungspensum, jaxBetreuungspensum);
		return jaxBetreuungspensum;
	}


	public JaxGesuchsperiode gesuchsperiodeToJAX(Gesuchsperiode persistedGesuchsperiode) {
		JaxGesuchsperiode jaxGesuchsperiode = new JaxGesuchsperiode();
		convertAbstractDateRangedFieldsToJAX(persistedGesuchsperiode, jaxGesuchsperiode);
		jaxGesuchsperiode.setActive(persistedGesuchsperiode.getActive());
		return jaxGesuchsperiode;
	}

	public Gesuchsperiode gesuchsperiodeToEntity(JaxGesuchsperiode jaxGesuchsperiode, Gesuchsperiode gesuchsperiode) {
		convertAbstractDateRangedFieldsToEntity(jaxGesuchsperiode, gesuchsperiode);
		gesuchsperiode.setActive(jaxGesuchsperiode.getActive());
		return gesuchsperiode;
	}
}
