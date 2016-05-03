package ch.dvbern.ebegu.api.converter;

import ch.dvbern.ebegu.api.dtos.*;
import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.services.*;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.lib.date.DateConvertUtils;
import org.apache.commons.lang3.Validate;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;


@Dependent
@SuppressWarnings({"PMD.NcssTypeCount", "unused"})
public class JaxBConverter {

	@Inject
	private GesuchstellerService gesuchstellerService;

	@Inject
	private AdresseService adresseService;
	@Inject
	private FachstelleService fachstelleService;
	@Inject
	private GesuchService gesuchService;
	@Inject
	private FallService fallService;

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
	public String toEntityId(@Nonnull final JaxAbstractDTO resource) { return toEntityId(Objects.requireNonNull(resource.getId()));
	}

	@Nonnull
	private <T extends JaxAbstractDTO> T convertAbstractFieldsToJAX(@Nonnull final AbstractEntity abstEntity, T jaxDTOToConvertTo) {
		jaxDTOToConvertTo.setTimestampErstellt(abstEntity.getTimestampErstellt());
		jaxDTOToConvertTo.setTimestampMutiert(abstEntity.getTimestampMutiert());
		jaxDTOToConvertTo.setId(checkNotNull(new JaxId(abstEntity.getId())));
		return jaxDTOToConvertTo;
	}

	@Nonnull
	private <T extends AbstractEntity> T convertAbstractFieldsToEntity(JaxAbstractDTO jaxToConvert, @Nonnull final T abstEntityToConvertTo) {
		if (jaxToConvert.getId() != null) {
			abstEntityToConvertTo.setId(toEntityId(jaxToConvert));
			//ACHTUNG hier timestamp erstellt und mutiert NICHT  konvertieren da diese immer auf dem server gesetzt werden muessen
		}

		return abstEntityToConvertTo;
	}

	@Nonnull
	public JaxApplicationProperties applicationPropertieToJAX(@Nonnull final ApplicationProperty applicationProperty) {
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
	public Adresse adresseToEntity(@Nonnull JaxAdresse jaxAdresse, @Nonnull final Adresse adresse) {
		Validate.notNull(adresse);
		Validate.notNull(jaxAdresse);
		convertAbstractFieldsToEntity(jaxAdresse, adresse);
		adresse.setStrasse(jaxAdresse.getStrasse());
		adresse.setHausnummer(jaxAdresse.getHausnummer());
		adresse.setZusatzzeile(jaxAdresse.getZusatzzeile());
		adresse.setPlz(jaxAdresse.getPlz());
		adresse.setOrt(jaxAdresse.getOrt());
		adresse.setGemeinde(jaxAdresse.getGemeinde());
		adresse.setLand(jaxAdresse.getLand());
		adresse.setGueltigAb(jaxAdresse.getGueltigAb() == null ? Constants.START_OF_TIME : jaxAdresse.getGueltigAb());
		adresse.setGueltigBis(jaxAdresse.getGueltigBis() == null ? Constants.END_OF_TIME : jaxAdresse.getGueltigBis());
		adresse.setAdresseTyp(jaxAdresse.getAdresseTyp());

		return adresse;
	}

	@Nonnull
	public JaxAdresse adresseToJAX(@Nonnull final Adresse adresse) {
		JaxAdresse jaxAdresse = new JaxAdresse();
		convertAbstractFieldsToJAX(adresse, jaxAdresse);
		jaxAdresse.setStrasse(adresse.getStrasse());
		jaxAdresse.setHausnummer(adresse.getHausnummer());
		jaxAdresse.setZusatzzeile(adresse.getZusatzzeile());
		jaxAdresse.setPlz(adresse.getPlz());
		jaxAdresse.setOrt(adresse.getOrt());
		jaxAdresse.setGemeinde(adresse.getGemeinde());
		jaxAdresse.setLand(adresse.getLand());
		jaxAdresse.setGueltigAb(adresse.getGueltigAb());
		jaxAdresse.setGueltigBis(adresse.getGueltigBis());
		jaxAdresse.setAdresseTyp(adresse.getAdresseTyp());
		return jaxAdresse;
	}

	@Nonnull
	public JaxEnversRevision enversRevisionToJAX(@Nonnull final DefaultRevisionEntity revisionEntity,
												 @Nonnull final AbstractEntity abstractEntity, RevisionType accessType) {

		JaxEnversRevision jaxEnversRevision = new JaxEnversRevision();
		if (abstractEntity instanceof ApplicationProperty) {
			jaxEnversRevision.setEntity(applicationPropertieToJAX((ApplicationProperty) abstractEntity));
		}
		jaxEnversRevision.setRev(revisionEntity.getId());
		jaxEnversRevision.setRevTimeStamp(DateConvertUtils.asLocalDateTime(revisionEntity.getRevisionDate()));
		jaxEnversRevision.setAccessType(accessType);
		return jaxEnversRevision;
	}

	public Gesuchsteller gesuchstellerToEntity(@Nonnull JaxGesuchsteller gesuchstellerJAXP, @Nonnull Gesuchsteller gesuchsteller) {
		Validate.notNull(gesuchsteller);
		Validate.notNull(gesuchstellerJAXP);
		Validate.notNull(gesuchstellerJAXP.getWohnAdresse(),"Wohnadresse muss gesetzt sein");
		convertAbstractFieldsToEntity(gesuchstellerJAXP, gesuchsteller);
		gesuchsteller.setNachname(gesuchstellerJAXP.getNachname());
		gesuchsteller.setVorname(gesuchstellerJAXP.getVorname());
		gesuchsteller.setGeburtsdatum(gesuchstellerJAXP.getGeburtsdatum());
		gesuchsteller.setGeschlecht(gesuchstellerJAXP.getGeschlecht());
		gesuchsteller.setMail(gesuchstellerJAXP.getMail());
		gesuchsteller.setTelefon(gesuchstellerJAXP.getTelefon());
		gesuchsteller.setMobile(gesuchstellerJAXP.getMobile());
		gesuchsteller.setTelefonAusland(gesuchstellerJAXP.getTelefonAusland());
		gesuchsteller.setZpvNumber(gesuchstellerJAXP.getZpvNumber());

		//Relationen
		//Wir fuehren derzeit immer maximal  eine alternative Korrespondenzadressse -> diese updaten wenn vorhanden
		if (gesuchstellerJAXP.getAlternativeAdresse() != null) {
			Adresse currentAltAdr = adresseService.getKorrespondenzAdr(gesuchsteller.getId()).orElse(new Adresse());
			Adresse altAddrToMerge = adresseToEntity(gesuchstellerJAXP.getAlternativeAdresse(), currentAltAdr);
			gesuchsteller.addAdresse(altAddrToMerge);
		}
		// Umzug und Wohnadresse
		Adresse umzugAddr = null;
		if (gesuchstellerJAXP.getUmzugAdresse() != null) {
			umzugAddr = toStoreableAddresse(gesuchstellerJAXP.getUmzugAdresse());
			gesuchsteller.addAdresse(umzugAddr);
		}
		//Wohnadresse (abh von Umzug noch datum setzten)
		Adresse wohnAddrToMerge = toStoreableAddresse(gesuchstellerJAXP.getWohnAdresse());
		if (umzugAddr != null) {
			wohnAddrToMerge.setGueltigBis(umzugAddr.getGueltigAb().minusDays(1));
		}
		gesuchsteller.addAdresse(wohnAddrToMerge);
		return gesuchsteller;
	}

	@Nonnull
	private Adresse toStoreableAddresse(@Nonnull JaxAdresse adresseToPrepareForSaving) {
		Adresse adrToMergeWith = new Adresse();
		if (adresseToPrepareForSaving.getId() != null ) {

			Optional<Adresse> altAdr = adresseService.findAdresse(toEntityId(adresseToPrepareForSaving));
			//wenn schon vorhanden updaten
			if (altAdr.isPresent()) {
				adrToMergeWith = altAdr.get();
			}
		}
		return  adresseToEntity(adresseToPrepareForSaving, adrToMergeWith);
	}

	public JaxGesuchsteller gesuchstellerToJAX(@Nonnull Gesuchsteller persistedGesuchsteller) {
		Validate.isTrue(!persistedGesuchsteller.isNew(), "Gesuchsteller kann nicht nach REST transformiert werden weil sie noch " +
			"nicht persistiert wurde; Grund dafuer ist, dass wir die aktuelle Wohnadresse aus der Datenbank lesen wollen");
		JaxGesuchsteller jaxGesuchsteller = new JaxGesuchsteller();
		convertAbstractFieldsToJAX(persistedGesuchsteller, jaxGesuchsteller);
		jaxGesuchsteller.setNachname(persistedGesuchsteller.getNachname());
		jaxGesuchsteller.setVorname(persistedGesuchsteller.getVorname());
		jaxGesuchsteller.setGeburtsdatum(persistedGesuchsteller.getGeburtsdatum());
		jaxGesuchsteller.setGeschlecht(persistedGesuchsteller.getGeschlecht());
		jaxGesuchsteller.setMail(persistedGesuchsteller.getMail());
		jaxGesuchsteller.setTelefon(persistedGesuchsteller.getTelefon());
		jaxGesuchsteller.setMobile(persistedGesuchsteller.getMobile());
		jaxGesuchsteller.setTelefonAusland(persistedGesuchsteller.getTelefonAusland());
		jaxGesuchsteller.setZpvNumber(persistedGesuchsteller.getZpvNumber());
		//relationen laden
		Optional<Adresse> altAdr = adresseService.getKorrespondenzAdr(persistedGesuchsteller.getId());
		altAdr.ifPresent(adresse -> jaxGesuchsteller.setAlternativeAdresse(adresseToJAX(adresse)));
		Adresse currentWohnadr = adresseService.getCurrentWohnadresse(persistedGesuchsteller.getId());
		jaxGesuchsteller.setWohnAdresse(adresseToJAX(currentWohnadr));

		//wenn heute gueltige Adresse von der Adresse divergiert die bis End of Time gilt dann wurde ein Umzug angegeben
		Optional<Adresse> maybeUmzugadresse = adresseService.getNewestWohnadresse(persistedGesuchsteller.getId());
		maybeUmzugadresse.filter(umzugAdresse -> !currentWohnadr.equals(umzugAdresse))
			.ifPresent(umzugAdr -> jaxGesuchsteller.setUmzugAdresse(adresseToJAX(umzugAdr)));
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
		return familiensituation;
	}

	public JaxFamilienSituation familiensituationToJAX(@Nonnull Familiensituation persistedFamiliensituation) {
		JaxFamilienSituation jaxFamiliensituation = new JaxFamilienSituation();
		convertAbstractFieldsToJAX(persistedFamiliensituation, jaxFamiliensituation);
		jaxFamiliensituation.setFamilienstatus(persistedFamiliensituation.getFamilienstatus());
		jaxFamiliensituation.setGesuchstellerKardinalitaet(persistedFamiliensituation.getGesuchstellerKardinalitaet());
		jaxFamiliensituation.setBemerkungen(persistedFamiliensituation.getBemerkungen());
		jaxFamiliensituation.setGesuch(this.gesuchToJAX(persistedFamiliensituation.getGesuch()));
		return jaxFamiliensituation;
	}

	public Fall fallToEntity(@Nonnull JaxFall fallJAXP, @Nonnull Fall fall) {
		Validate.notNull(fall);
		Validate.notNull(fallJAXP);
		convertAbstractFieldsToEntity(fallJAXP, fall);
		return fall;
	}

	public JaxFall fallToJAX(@Nonnull Fall persistedFall) {
		JaxFall jaxFall = new JaxFall();
		convertAbstractFieldsToJAX(persistedFall, jaxFall);
		return jaxFall;
	}

	public Gesuch gesuchToEntity(@Nonnull JaxGesuch gesuchJAXP, @Nonnull Gesuch gesuch) {
		Validate.notNull(gesuch);
		Validate.notNull(gesuchJAXP);
		convertAbstractFieldsToEntity(gesuchJAXP, gesuch);
		Optional<Fall> fallFromDB =  fallService.findFall(toEntityId(gesuchJAXP.getFall()));
		if(fallFromDB.isPresent()) {
			gesuch.setFall(this.fallToEntity(gesuchJAXP.getFall(), fallFromDB.get()));
		} else {
			throw new EbeguEntityNotFoundException("gesuchToEntity", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, toEntityId(gesuchJAXP.getFall()));
		}
		if (gesuchJAXP.getGesuchsteller1() != null && gesuchJAXP.getGesuchsteller1().getId() != null) {
			Optional<Gesuchsteller> gesuchsteller1 = gesuchstellerService.findGesuchsteller(toEntityId(gesuchJAXP.getGesuchsteller1()));
			if (gesuchsteller1.isPresent()) {
				gesuch.setGesuchsteller1(gesuchstellerToEntity(gesuchJAXP.getGesuchsteller1(), gesuchsteller1.get()));
			} else {
				throw new EbeguEntityNotFoundException("gesuchToEntity", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, toEntityId(gesuchJAXP.getGesuchsteller1()));
			}
		}
		if (gesuchJAXP.getGesuchsteller2() != null && gesuchJAXP.getGesuchsteller2().getId() != null) {
			Optional<Gesuchsteller> gesuchsteller2 = gesuchstellerService.findGesuchsteller(toEntityId(gesuchJAXP.getGesuchsteller2()));
			if (gesuchsteller2.isPresent()){
				gesuch.setGesuchsteller2(gesuchstellerToEntity(gesuchJAXP.getGesuchsteller2(), gesuchsteller2.get()));
			} else {
				throw new EbeguEntityNotFoundException("gesuchToEntity", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, toEntityId(gesuchJAXP.getGesuchsteller2()));
			}
		}
		return gesuch;
	}

	public JaxGesuch gesuchToJAX(@Nonnull Gesuch persistedGesuch) {
		JaxGesuch jaxGesuch = new JaxGesuch();
		convertAbstractFieldsToJAX(persistedGesuch, jaxGesuch);
		jaxGesuch.setFall(this.fallToJAX(persistedGesuch.getFall()));
		if(persistedGesuch.getGesuchsteller1() != null) {
			jaxGesuch.setGesuchsteller1(this.gesuchstellerToJAX(persistedGesuch.getGesuchsteller1()));
		}
		if(persistedGesuch.getGesuchsteller2() != null) {
			jaxGesuch.setGesuchsteller2(this.gesuchstellerToJAX(persistedGesuch.getGesuchsteller2()));
		}
		return jaxGesuch;
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

	public JaxKind kindToJAX(Kind persistedKind) {
		JaxKind jaxKind = new JaxKind();
		convertAbstractFieldsToJAX(persistedKind, jaxKind);
		jaxKind.setNachname(persistedKind.getNachname());
		jaxKind.setVorname(persistedKind.getVorname());
		jaxKind.setGeburtsdatum(persistedKind.getGeburtsdatum());
		jaxKind.setGeschlecht(persistedKind.getGeschlecht());
		jaxKind.setWohnhaftImGleichenHaushalt(persistedKind.getWohnhaftImGleichenHaushalt());
		jaxKind.setUnterstuetzungspflicht(persistedKind.getUnterstuetzungspflicht());
		jaxKind.setFamilienErgaenzendeBetreuung(persistedKind.getFamilienErgaenzendeBetreuung());
		jaxKind.setMutterspracheDeutsch(persistedKind.getMutterspracheDeutsch());
		jaxKind.setBetreuungspensumFachstelle(persistedKind.getBetreuungspensumFachstelle());
		jaxKind.setBemerkungen(persistedKind.getBemerkungen());
		jaxKind.setFachstelle(fachstelleToJAX(persistedKind.getFachstelle()));
		jaxKind.setGesuch(gesuchToJAX(persistedKind.getGesuch()));
		return jaxKind;
	}

	public Kind kindToEntity(JaxKind kindJAXP, Kind kind) {
		Validate.notNull(kindJAXP);
		Validate.notNull(kind);
		convertAbstractFieldsToEntity(kindJAXP, kind);
		kind.setNachname(kindJAXP.getNachname());
		kind.setVorname(kindJAXP.getVorname());
		kind.setGeburtsdatum(kindJAXP.getGeburtsdatum());
		kind.setGeschlecht(kindJAXP.getGeschlecht());
		kind.setWohnhaftImGleichenHaushalt(kindJAXP.getWohnhaftImGleichenHaushalt());
		kind.setUnterstuetzungspflicht(kindJAXP.getUnterstuetzungspflicht());
		kind.setFamilienErgaenzendeBetreuung(kindJAXP.getFamilienErgaenzendeBetreuung());
		kind.setMutterspracheDeutsch(kindJAXP.getMutterspracheDeutsch());
		kind.setBetreuungspensumFachstelle(kindJAXP.getBetreuungspensumFachstelle());
		kind.setBemerkungen(kindJAXP.getBemerkungen());
		kind.setFachstelle(findFachstelleToEntity(kindJAXP.getFachstelle()));
		kind.setGesuch(findGesuchToEntity(kindJAXP.getGesuch()));
		return kind;
	}

	/**
	 * Sucht die Fachstelle in der DB und fuegt sie mit der als Parameter gegebenen Fachstelle zusammen.
	 * Sollte sie in der DB nicht existieren, gibt die Methode eine neue Fachstelle mit den gegebenen Daten zurueck
	 * @param fachstelleToFind die Fachstelle als JAX
	 * @return die Fachstelle als Entity
     */
	@Nonnull
	private Fachstelle findFachstelleToEntity(JaxFachstelle fachstelleToFind) {
		Validate.notNull(fachstelleToFind);
		Fachstelle fachstelleToMergeWith = new Fachstelle();
		if (fachstelleToFind.getId() != null ) {
			Optional<Fachstelle> altFachstelle = fachstelleService.findFachstelle(toEntityId(fachstelleToFind));
			if (altFachstelle.isPresent()) {
				fachstelleToMergeWith = altFachstelle.get();
			}
		}
		return fachstelleToEntity(fachstelleToFind, fachstelleToMergeWith);
	}

	/**
	 * Sucht das Gesuch in der DB und fuegt es mit dem als Parameter gegebenen Gesuch zusammen.
	 * Sollte es in der DB nicht existieren, gibt die Methode ein neues Gesuch mit den gegebenen Daten zurueck
	 * @param gesuchToFind das Gesuch als JAX
	 * @return das Gesuch als Entity
     */
	@Nonnull
	private Gesuch findGesuchToEntity(JaxGesuch gesuchToFind) {
		Validate.notNull(gesuchToFind);
		Gesuch gesuchToMergeWith = new Gesuch();
		if (gesuchToFind.getId() != null ) {
			Optional<Gesuch> altGesuch = gesuchService.findGesuch(toEntityId(gesuchToFind));
			if (altGesuch.isPresent()) {
				gesuchToMergeWith = altGesuch.get();
			}
		}
		return gesuchToEntity(gesuchToFind, gesuchToMergeWith);
	}
}
