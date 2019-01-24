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

package ch.dvbern.ebegu.api.converter;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import ch.dvbern.ebegu.api.dtos.JaxAbstractDTO;
import ch.dvbern.ebegu.api.dtos.JaxAbstractDateRangedDTO;
import ch.dvbern.ebegu.api.dtos.JaxAbstractFinanzielleSituation;
import ch.dvbern.ebegu.api.dtos.JaxAbstractPensumDTO;
import ch.dvbern.ebegu.api.dtos.JaxAbstractPersonDTO;
import ch.dvbern.ebegu.api.dtos.JaxAbwesenheit;
import ch.dvbern.ebegu.api.dtos.JaxAbwesenheitContainer;
import ch.dvbern.ebegu.api.dtos.JaxAdresse;
import ch.dvbern.ebegu.api.dtos.JaxAdresseContainer;
import ch.dvbern.ebegu.api.dtos.JaxAntragStatusHistory;
import ch.dvbern.ebegu.api.dtos.JaxApplicationProperties;
import ch.dvbern.ebegu.api.dtos.JaxAuthLoginElement;
import ch.dvbern.ebegu.api.dtos.JaxBelegungFerieninsel;
import ch.dvbern.ebegu.api.dtos.JaxBelegungFerieninselTag;
import ch.dvbern.ebegu.api.dtos.JaxBelegungTagesschule;
import ch.dvbern.ebegu.api.dtos.JaxBerechtigung;
import ch.dvbern.ebegu.api.dtos.JaxBerechtigungHistory;
import ch.dvbern.ebegu.api.dtos.JaxBetreuung;
import ch.dvbern.ebegu.api.dtos.JaxBetreuungsmitteilung;
import ch.dvbern.ebegu.api.dtos.JaxBetreuungsmitteilungPensum;
import ch.dvbern.ebegu.api.dtos.JaxBetreuungspensum;
import ch.dvbern.ebegu.api.dtos.JaxBetreuungspensumContainer;
import ch.dvbern.ebegu.api.dtos.JaxDokument;
import ch.dvbern.ebegu.api.dtos.JaxDokumentGrund;
import ch.dvbern.ebegu.api.dtos.JaxDokumente;
import ch.dvbern.ebegu.api.dtos.JaxDownloadFile;
import ch.dvbern.ebegu.api.dtos.JaxEbeguParameter;
import ch.dvbern.ebegu.api.dtos.JaxEbeguVorlage;
import ch.dvbern.ebegu.api.dtos.JaxEinkommensverschlechterung;
import ch.dvbern.ebegu.api.dtos.JaxEinkommensverschlechterungContainer;
import ch.dvbern.ebegu.api.dtos.JaxEinkommensverschlechterungInfo;
import ch.dvbern.ebegu.api.dtos.JaxEinkommensverschlechterungInfoContainer;
import ch.dvbern.ebegu.api.dtos.JaxEnversRevision;
import ch.dvbern.ebegu.api.dtos.JaxErwerbspensum;
import ch.dvbern.ebegu.api.dtos.JaxErwerbspensumContainer;
import ch.dvbern.ebegu.api.dtos.JaxFachstelle;
import ch.dvbern.ebegu.api.dtos.JaxFall;
import ch.dvbern.ebegu.api.dtos.JaxFamiliensituation;
import ch.dvbern.ebegu.api.dtos.JaxFamiliensituationContainer;
import ch.dvbern.ebegu.api.dtos.JaxFerieninselStammdaten;
import ch.dvbern.ebegu.api.dtos.JaxFerieninselZeitraum;
import ch.dvbern.ebegu.api.dtos.JaxFile;
import ch.dvbern.ebegu.api.dtos.JaxFinanzielleSituation;
import ch.dvbern.ebegu.api.dtos.JaxFinanzielleSituationContainer;
import ch.dvbern.ebegu.api.dtos.JaxGesuch;
import ch.dvbern.ebegu.api.dtos.JaxGesuchsperiode;
import ch.dvbern.ebegu.api.dtos.JaxGesuchsteller;
import ch.dvbern.ebegu.api.dtos.JaxGesuchstellerContainer;
import ch.dvbern.ebegu.api.dtos.JaxId;
import ch.dvbern.ebegu.api.dtos.JaxInstitution;
import ch.dvbern.ebegu.api.dtos.JaxInstitutionStammdaten;
import ch.dvbern.ebegu.api.dtos.JaxInstitutionStammdatenFerieninsel;
import ch.dvbern.ebegu.api.dtos.JaxInstitutionStammdatenTagesschule;
import ch.dvbern.ebegu.api.dtos.JaxKind;
import ch.dvbern.ebegu.api.dtos.JaxKindContainer;
import ch.dvbern.ebegu.api.dtos.JaxMahnung;
import ch.dvbern.ebegu.api.dtos.JaxMandant;
import ch.dvbern.ebegu.api.dtos.JaxMitteilung;
import ch.dvbern.ebegu.api.dtos.JaxModulTagesschule;
import ch.dvbern.ebegu.api.dtos.JaxPensumFachstelle;
import ch.dvbern.ebegu.api.dtos.JaxTraegerschaft;
import ch.dvbern.ebegu.api.dtos.JaxVerfuegung;
import ch.dvbern.ebegu.api.dtos.JaxVerfuegungZeitabschnitt;
import ch.dvbern.ebegu.api.dtos.JaxVorlage;
import ch.dvbern.ebegu.api.dtos.JaxWizardStep;
import ch.dvbern.ebegu.api.dtos.JaxZahlung;
import ch.dvbern.ebegu.api.dtos.JaxZahlungsauftrag;
import ch.dvbern.ebegu.api.util.RestUtil;
import ch.dvbern.ebegu.dto.JaxAntragDTO;
import ch.dvbern.ebegu.entities.AbstractDateRangedEntity;
import ch.dvbern.ebegu.entities.AbstractEntity;
import ch.dvbern.ebegu.entities.AbstractFinanzielleSituation;
import ch.dvbern.ebegu.entities.AbstractPensumEntity;
import ch.dvbern.ebegu.entities.AbstractPersonEntity;
import ch.dvbern.ebegu.entities.Abwesenheit;
import ch.dvbern.ebegu.entities.AbwesenheitContainer;
import ch.dvbern.ebegu.entities.Adresse;
import ch.dvbern.ebegu.entities.AntragStatusHistory;
import ch.dvbern.ebegu.entities.ApplicationProperty;
import ch.dvbern.ebegu.entities.BelegungFerieninsel;
import ch.dvbern.ebegu.entities.BelegungFerieninselTag;
import ch.dvbern.ebegu.entities.BelegungTagesschule;
import ch.dvbern.ebegu.entities.Benutzer;
import ch.dvbern.ebegu.entities.Berechtigung;
import ch.dvbern.ebegu.entities.BerechtigungHistory;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Betreuungsmitteilung;
import ch.dvbern.ebegu.entities.BetreuungsmitteilungPensum;
import ch.dvbern.ebegu.entities.Betreuungspensum;
import ch.dvbern.ebegu.entities.BetreuungspensumContainer;
import ch.dvbern.ebegu.entities.Dokument;
import ch.dvbern.ebegu.entities.DokumentGrund;
import ch.dvbern.ebegu.entities.DownloadFile;
import ch.dvbern.ebegu.entities.EbeguParameter;
import ch.dvbern.ebegu.entities.EbeguVorlage;
import ch.dvbern.ebegu.entities.Einkommensverschlechterung;
import ch.dvbern.ebegu.entities.EinkommensverschlechterungContainer;
import ch.dvbern.ebegu.entities.EinkommensverschlechterungInfo;
import ch.dvbern.ebegu.entities.EinkommensverschlechterungInfoContainer;
import ch.dvbern.ebegu.entities.Erwerbspensum;
import ch.dvbern.ebegu.entities.ErwerbspensumContainer;
import ch.dvbern.ebegu.entities.Fachstelle;
import ch.dvbern.ebegu.entities.Fall;
import ch.dvbern.ebegu.entities.Familiensituation;
import ch.dvbern.ebegu.entities.FamiliensituationContainer;
import ch.dvbern.ebegu.entities.FerieninselStammdaten;
import ch.dvbern.ebegu.entities.FerieninselZeitraum;
import ch.dvbern.ebegu.entities.FileMetadata;
import ch.dvbern.ebegu.entities.FinanzielleSituation;
import ch.dvbern.ebegu.entities.FinanzielleSituationContainer;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.entities.Gesuchsteller;
import ch.dvbern.ebegu.entities.GesuchstellerAdresse;
import ch.dvbern.ebegu.entities.GesuchstellerAdresseContainer;
import ch.dvbern.ebegu.entities.GesuchstellerContainer;
import ch.dvbern.ebegu.entities.Institution;
import ch.dvbern.ebegu.entities.InstitutionStammdaten;
import ch.dvbern.ebegu.entities.InstitutionStammdatenFerieninsel;
import ch.dvbern.ebegu.entities.InstitutionStammdatenTagesschule;
import ch.dvbern.ebegu.entities.Kind;
import ch.dvbern.ebegu.entities.KindContainer;
import ch.dvbern.ebegu.entities.Mahnung;
import ch.dvbern.ebegu.entities.Mandant;
import ch.dvbern.ebegu.entities.Mitteilung;
import ch.dvbern.ebegu.entities.ModulTagesschule;
import ch.dvbern.ebegu.entities.PensumFachstelle;
import ch.dvbern.ebegu.entities.Traegerschaft;
import ch.dvbern.ebegu.entities.Verfuegung;
import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.entities.Vorlage;
import ch.dvbern.ebegu.entities.WizardStep;
import ch.dvbern.ebegu.entities.Zahlung;
import ch.dvbern.ebegu.entities.Zahlungsauftrag;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.AntragStatusDTO;
import ch.dvbern.ebegu.enums.ApplicationPropertyKey;
import ch.dvbern.ebegu.enums.BetreuungsangebotTyp;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.services.BenutzerService;
import ch.dvbern.ebegu.services.BetreuungService;
import ch.dvbern.ebegu.services.EinkommensverschlechterungInfoService;
import ch.dvbern.ebegu.services.EinkommensverschlechterungService;
import ch.dvbern.ebegu.services.ErwerbspensumService;
import ch.dvbern.ebegu.services.FachstelleService;
import ch.dvbern.ebegu.services.FallService;
import ch.dvbern.ebegu.services.FamiliensituationService;
import ch.dvbern.ebegu.services.FinanzielleSituationService;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.services.GesuchsperiodeService;
import ch.dvbern.ebegu.services.GesuchstellerAdresseService;
import ch.dvbern.ebegu.services.GesuchstellerService;
import ch.dvbern.ebegu.services.InstitutionService;
import ch.dvbern.ebegu.services.InstitutionStammdatenService;
import ch.dvbern.ebegu.services.MandantService;
import ch.dvbern.ebegu.services.PensumFachstelleService;
import ch.dvbern.ebegu.services.TraegerschaftService;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.AntragStatusConverterUtil;
import ch.dvbern.ebegu.util.Constants;
import ch.dvbern.ebegu.util.MathUtil;
import ch.dvbern.ebegu.util.StreamsUtil;
import ch.dvbern.lib.date.DateConvertUtils;
import ch.dvbern.oss.lib.beanvalidation.embeddables.IBAN;
import com.google.common.base.Strings;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

@Dependent
@SuppressWarnings({ "PMD.NcssTypeCount", "unused" })
public class JaxBConverter {

	public static final String DROPPED_DUPLICATE_CONTAINER = "dropped duplicate container ";

	@Inject
	private GesuchstellerService gesuchstellerService;
	@Inject
	private GesuchstellerAdresseService gesuchstellerAdresseService;
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
	private FamiliensituationService familiensituationService;
	@Inject
	private EinkommensverschlechterungInfoService einkommensverschlechterungInfoService;
	@Inject
	private EinkommensverschlechterungService einkommensverschlechterungService;
	@Inject
	private MandantService mandantService;
	@Inject
	private TraegerschaftService traegerschaftService;
	@Inject
	private InstitutionService institutionService;
	@Inject
	private BenutzerService benutzerService;
	@Inject
	private InstitutionStammdatenService institutionStammdatenService;
	@Inject
	private BetreuungService betreuungService;

	private static final Logger LOGGER = LoggerFactory.getLogger(JaxBConverter.class);

	@Nonnull
	public String toResourceId(@Nonnull final AbstractEntity entity) {
		return String.valueOf(Objects.requireNonNull(entity.getId()));
	}

	@Nonnull
	public String toEntityId(@Nonnull final JaxId resourceId) {
		return Validate.notNull(resourceId.getId());
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
	private <T extends JaxAbstractDTO> T convertAbstractFieldsToJAX(@Nonnull final AbstractEntity abstEntity, final T jaxDTOToConvertTo) {
		jaxDTOToConvertTo.setTimestampErstellt(abstEntity.getTimestampErstellt());
		jaxDTOToConvertTo.setTimestampMutiert(abstEntity.getTimestampMutiert());
		jaxDTOToConvertTo.setId(checkNotNull(abstEntity.getId()));
		jaxDTOToConvertTo.setVorgaengerId(abstEntity.getVorgaengerId());
		return jaxDTOToConvertTo;
	}

	@Nonnull
	private <T extends AbstractEntity> T convertAbstractFieldsToEntity(final JaxAbstractDTO jaxToConvert, @Nonnull final T abstEntityToConvertTo) {
		if (jaxToConvert.getId() != null) {
			abstEntityToConvertTo.setId(jaxToConvert.getId());
			abstEntityToConvertTo.setVorgaengerId(jaxToConvert.getVorgaengerId());
			//ACHTUNG hier timestamp erstellt und mutiert NICHT  konvertieren da diese immer auf dem server gesetzt werden muessen
		}

		return abstEntityToConvertTo;
	}

	/**
	 * Converts all person related fields from Jax to Entity
	 *
	 * @param personEntityJAXP das objekt als Jax
	 * @param personEntity das object als Entity
	 */
	private void convertAbstractPersonFieldsToEntity(final JaxAbstractPersonDTO personEntityJAXP, final AbstractPersonEntity personEntity) {
		convertAbstractFieldsToEntity(personEntityJAXP, personEntity);
		personEntity.setNachname(personEntityJAXP.getNachname());
		personEntity.setVorname(personEntityJAXP.getVorname());
		personEntity.setGeburtsdatum(personEntityJAXP.getGeburtsdatum());
		personEntity.setGeschlecht(personEntityJAXP.getGeschlecht());
	}

	/**
	 * Converts all person related fields from Entity to Jax
	 *
	 * @param personEntity das object als Entity
	 * @param personEntityJAXP das objekt als Jax
	 */
	private void convertAbstractPersonFieldsToJAX(final AbstractPersonEntity personEntity, final JaxAbstractPersonDTO personEntityJAXP) {
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
	 * @param dateRangedJAXP AbstractDateRanged jax where to take the date from
	 * @param dateRangedEntity AbstractDateRanged entity where to store the date into
	 */
	private AbstractDateRangedEntity convertAbstractDateRangedFieldsToEntity(final JaxAbstractDateRangedDTO dateRangedJAXP, final AbstractDateRangedEntity
		dateRangedEntity) {
		convertAbstractFieldsToEntity(dateRangedJAXP, dateRangedEntity);
		final LocalDate dateAb = dateRangedJAXP.getGueltigAb() == null ? LocalDate.now() : dateRangedJAXP.getGueltigAb();
		final LocalDate dateBis = dateRangedJAXP.getGueltigBis() == null ? Constants.END_OF_TIME : dateRangedJAXP.getGueltigBis();
		dateRangedEntity.setGueltigkeit(new DateRange(dateAb, dateBis));
		return dateRangedEntity;
	}

	/***
	 * Konvertiert eine DateRange fuer den Client. Wenn das DatumBis {@link Constants#END_OF_TIME} entspricht wird es NICHT
	 * konvertiert
	 */
	private void convertAbstractDateRangedFieldsToJAX(@Nonnull final AbstractDateRangedEntity dateRangedEntity, @Nonnull final JaxAbstractDateRangedDTO
		jaxDateRanged) {
		Validate.notNull(dateRangedEntity.getGueltigkeit());
		convertAbstractFieldsToJAX(dateRangedEntity, jaxDateRanged);
		jaxDateRanged.setGueltigAb(dateRangedEntity.getGueltigkeit().getGueltigAb());
		if (Constants.END_OF_TIME.equals(dateRangedEntity.getGueltigkeit().getGueltigBis())) {
			jaxDateRanged.setGueltigBis(null); // end of time gueltigkeit wird nicht an client geschickt
		} else {
			jaxDateRanged.setGueltigBis(dateRangedEntity.getGueltigkeit().getGueltigBis());
		}
	}

	private void convertAbstractPensumFieldsToEntity(final JaxAbstractPensumDTO jaxPensum, final AbstractPensumEntity pensumEntity) {
		convertAbstractDateRangedFieldsToEntity(jaxPensum, pensumEntity);
		pensumEntity.setPensum(jaxPensum.getPensum());
	}

	private void convertAbstractPensumFieldsToJAX(final AbstractPensumEntity pensum, final JaxAbstractPensumDTO jaxPensum) {
		convertAbstractDateRangedFieldsToJAX(pensum, jaxPensum);
		jaxPensum.setPensum(pensum.getPensum());
	}

	@Nonnull
	public JaxApplicationProperties applicationPropertyToJAX(@Nonnull final ApplicationProperty applicationProperty) {
		final JaxApplicationProperties jaxProperty = new JaxApplicationProperties();
		convertAbstractFieldsToJAX(applicationProperty, jaxProperty);
		jaxProperty.setName(applicationProperty.getName().toString());
		jaxProperty.setValue(applicationProperty.getValue());
		return jaxProperty;
	}

	@Nonnull
	public ApplicationProperty applicationPropertieToEntity(final JaxApplicationProperties jaxAP, @Nonnull final ApplicationProperty applicationProperty) {
		Validate.notNull(applicationProperty);
		Validate.notNull(jaxAP);
		convertAbstractFieldsToEntity(jaxAP, applicationProperty);
		applicationProperty.setName(Enum.valueOf(ApplicationPropertyKey.class, jaxAP.getName()));
		applicationProperty.setValue(jaxAP.getValue());

		return applicationProperty;
	}

	@Nonnull
	public JaxEbeguParameter ebeguParameterToJAX(@Nonnull final EbeguParameter ebeguParameter) {
		final JaxEbeguParameter jaxEbeguParameter = new JaxEbeguParameter();
		convertAbstractDateRangedFieldsToJAX(ebeguParameter, jaxEbeguParameter);
		jaxEbeguParameter.setName(ebeguParameter.getName());
		jaxEbeguParameter.setValue(ebeguParameter.getValue());
		jaxEbeguParameter.setProGesuchsperiode(ebeguParameter.getName().isProGesuchsperiode());
		return jaxEbeguParameter;
	}

	@Nonnull
	public EbeguParameter ebeguParameterToEntity(final JaxEbeguParameter jaxEbeguParameter, @Nonnull final EbeguParameter ebeguParameter) {
		Validate.notNull(ebeguParameter);
		Validate.notNull(jaxEbeguParameter);
		convertAbstractDateRangedFieldsToEntity(jaxEbeguParameter, ebeguParameter);
		ebeguParameter.setName(jaxEbeguParameter.getName());
		ebeguParameter.setValue(jaxEbeguParameter.getValue());
		return ebeguParameter;
	}

	@Nonnull
	public GesuchstellerAdresse gesuchstellerAdresseToEntity(@Nonnull final JaxAdresse jaxAdresse, @Nonnull final GesuchstellerAdresse gesuchstellerAdresse) {

		adresseToEntity(jaxAdresse, gesuchstellerAdresse);
		gesuchstellerAdresse.setAdresseTyp(jaxAdresse.getAdresseTyp());
		gesuchstellerAdresse.setNichtInGemeinde(jaxAdresse.isNichtInGemeinde());

		return gesuchstellerAdresse;
	}

	@Nonnull
	public JaxAdresse gesuchstellerAdresseToJAX(@Nonnull final GesuchstellerAdresse gesuchstellerAdresse) {
		final JaxAdresse jaxAdresse = adresseToJAX(gesuchstellerAdresse);
		jaxAdresse.setAdresseTyp(gesuchstellerAdresse.getAdresseTyp());
		jaxAdresse.setNichtInGemeinde(gesuchstellerAdresse.isNichtInGemeinde());
		return jaxAdresse;
	}

	@Nonnull
	public Adresse adresseToEntity(@Nonnull final JaxAdresse jaxAdresse, @Nonnull final Adresse adresse) {
		Validate.notNull(adresse);
		Validate.notNull(jaxAdresse);
		convertAbstractDateRangedFieldsToEntity(jaxAdresse, adresse);
		adresse.setStrasse(jaxAdresse.getStrasse());
		adresse.setHausnummer(jaxAdresse.getHausnummer());
		adresse.setZusatzzeile(jaxAdresse.getZusatzzeile());
		adresse.setPlz(jaxAdresse.getPlz());
		adresse.setOrt(jaxAdresse.getOrt());
		adresse.setGemeinde(jaxAdresse.getGemeinde());
		adresse.setLand(jaxAdresse.getLand());
		adresse.setOrganisation(jaxAdresse.getOrganisation());
		//adresse gilt per default von start of time an
		adresse.getGueltigkeit().setGueltigAb(jaxAdresse.getGueltigAb() == null ? Constants.START_OF_TIME : jaxAdresse.getGueltigAb());

		return adresse;
	}

	@Nonnull
	public JaxAdresse adresseToJAX(@Nonnull final Adresse adresse) {
		final JaxAdresse jaxAdresse = new JaxAdresse();
		convertAbstractDateRangedFieldsToJAX(adresse, jaxAdresse);
		jaxAdresse.setStrasse(adresse.getStrasse());
		jaxAdresse.setHausnummer(adresse.getHausnummer());
		jaxAdresse.setZusatzzeile(adresse.getZusatzzeile());
		jaxAdresse.setPlz(adresse.getPlz());
		jaxAdresse.setOrt(adresse.getOrt());
		jaxAdresse.setGemeinde(adresse.getGemeinde());
		jaxAdresse.setLand(adresse.getLand());
		jaxAdresse.setOrganisation(adresse.getOrganisation());
		return jaxAdresse;
	}

	@Nonnull
	public JaxEnversRevision enversRevisionToJAX(@Nonnull final DefaultRevisionEntity revisionEntity,
		@Nonnull final AbstractEntity abstractEntity, final RevisionType accessType) {

		final JaxEnversRevision jaxEnversRevision = new JaxEnversRevision();
		if (abstractEntity instanceof ApplicationProperty) {
			jaxEnversRevision.setEntity(applicationPropertyToJAX((ApplicationProperty) abstractEntity));
		} else {
			throw new NotImplementedException("Diese Funktion ist erst fuer ApplicationProperties umgesetzt!");
		}
		jaxEnversRevision.setRev(revisionEntity.getId());
		jaxEnversRevision.setRevTimeStamp(DateConvertUtils.asLocalDateTime(revisionEntity.getRevisionDate()));
		jaxEnversRevision.setAccessType(accessType);
		return jaxEnversRevision;
	}

	public Gesuchsteller gesuchstellerToEntity(@Nonnull final JaxGesuchsteller gesuchstellerJAXP, @Nonnull final Gesuchsteller gesuchsteller) {
		Validate.notNull(gesuchsteller);
		Validate.notNull(gesuchstellerJAXP);
		convertAbstractPersonFieldsToEntity(gesuchstellerJAXP, gesuchsteller);
		gesuchsteller.setMail(gesuchstellerJAXP.getMail());
		gesuchsteller.setTelefon(gesuchstellerJAXP.getTelefon());
		gesuchsteller.setMobile(gesuchstellerJAXP.getMobile());
		gesuchsteller.setTelefonAusland(gesuchstellerJAXP.getTelefonAusland());
		gesuchsteller.setEwkPersonId(gesuchstellerJAXP.getEwkPersonId());
		gesuchsteller.setEwkAbfrageDatum(gesuchstellerJAXP.getEwkAbfrageDatum());
		gesuchsteller.setDiplomatenstatus(gesuchstellerJAXP.isDiplomatenstatus());
		return gesuchsteller;
	}

	private void sortAndAddAdressenToGesuchstellerContainer(@Nonnull JaxGesuchstellerContainer gesuchstellerContJAXP,
		@Nonnull GesuchstellerContainer gesuchstellerCont) {
		// Zuerst wird geguckt, welche Entities nicht im JAX sind und werden dann geloescht
		for (Iterator<GesuchstellerAdresseContainer> iterator = gesuchstellerCont.getAdressen().iterator(); iterator.hasNext(); ) {
			GesuchstellerAdresseContainer next = iterator.next();
			boolean needsToBeRemoved = true;
			for (JaxAdresseContainer jaxAdresse : gesuchstellerContJAXP.getAdressen()) {
				if (next.extractIsKorrespondenzAdresse() || next.extractIsRechnungsAdresse() || next.getId().equals(jaxAdresse.getId())) {
					needsToBeRemoved = false; // Korrespondezadresse, Rechnungsadresse und Adressen die gefunden werden, werden nicht geloescht
				}
			}
			if (needsToBeRemoved) {
				iterator.remove();
			}
		}
		// Jetzt werden alle Adressen vom Jax auf Entity kopiert
		gesuchstellerContJAXP.getAdressen().forEach(jaxAdresse -> gesuchstellerCont.addAdresse(toStoreableAddresse(jaxAdresse)));

		// Zuletzt werden alle gueltigen Adressen sortiert und mit dem entsprechenden AB und BIS aktualisiert
		List<GesuchstellerAdresseContainer> wohnadressen = gesuchstellerCont.getAdressen().stream()
			.filter(gesuchstellerAdresse -> !gesuchstellerAdresse.extractIsKorrespondenzAdresse() && !gesuchstellerAdresse.extractIsRechnungsAdresse())
			.sorted(Comparator.comparing(o -> o.extractGueltigkeit().getGueltigAb()))
			.collect(Collectors.toList());
		for (int i = 0; i < wohnadressen.size(); i++) {
			if ((i < wohnadressen.size() - 1)) {
				wohnadressen.get(i).extractGueltigkeit().setGueltigBis(wohnadressen.get(i + 1)
					.extractGueltigkeit().getGueltigAb().minusDays(1));
			} else {
				wohnadressen.get(i).extractGueltigkeit().setGueltigBis(Constants.END_OF_TIME); // by default das letzte Datum hat BIS=END_OF_TIME
			}
		}
	}

	@Nonnull
	private GesuchstellerAdresseContainer toStoreableAddresse(@Nonnull final JaxAdresseContainer adresseToPrepareForSaving) {
		GesuchstellerAdresseContainer adrToMergeWith = new GesuchstellerAdresseContainer();
		if (adresseToPrepareForSaving.getId() != null) {

			final Optional<GesuchstellerAdresseContainer> altAdr = gesuchstellerAdresseService.findAdresse(adresseToPrepareForSaving.getId());
			//wenn schon vorhanden updaten
			if (altAdr.isPresent()) {
				adrToMergeWith = altAdr.get();
			}
		}
		return gesuchstellerAdresseContainerToEntity(adresseToPrepareForSaving, adrToMergeWith);
	}

	public JaxGesuchstellerContainer gesuchstellerContainerToJAX(GesuchstellerContainer persistedGesuchstellerCont) {
		JaxGesuchstellerContainer jaxGesuchstellerCont = new JaxGesuchstellerContainer();
		convertAbstractFieldsToJAX(persistedGesuchstellerCont, jaxGesuchstellerCont);

		if (persistedGesuchstellerCont.getGesuchstellerGS() != null) {
			jaxGesuchstellerCont.setGesuchstellerGS(gesuchstellerToJAX(persistedGesuchstellerCont.getGesuchstellerGS()));
		}
		if (persistedGesuchstellerCont.getGesuchstellerJA() != null) {
			jaxGesuchstellerCont.setGesuchstellerJA(gesuchstellerToJAX(persistedGesuchstellerCont.getGesuchstellerJA()));
		}

		if (!persistedGesuchstellerCont.isNew()) {
			//relationen laden
			final Optional<GesuchstellerAdresseContainer> alternativeAdr = gesuchstellerAdresseService.getKorrespondenzAdr(persistedGesuchstellerCont.getId());
			final Optional<GesuchstellerAdresseContainer> rechnungsAdr = gesuchstellerAdresseService.getRechnungsAdr(persistedGesuchstellerCont.getId());
			alternativeAdr.ifPresent(adresse -> jaxGesuchstellerCont.setAlternativeAdresse(gesuchstellerAdresseContainerToJAX(adresse)));
			rechnungsAdr.ifPresent(adresse -> jaxGesuchstellerCont.setRechnungsAdresse(gesuchstellerAdresseContainerToJAX(adresse)));

			jaxGesuchstellerCont.setAdressen(gesuchstellerAdresseContainerListToJAX(
				persistedGesuchstellerCont.getAdressen().stream().filter(gesuchstellerAdresse
					-> !gesuchstellerAdresse.extractIsKorrespondenzAdresse() && !gesuchstellerAdresse.extractIsRechnungsAdresse()).sorted((o1, o2) ->
				{
					if (o1.extractGueltigkeit() == null && o2.extractGueltigkeit() == null) {
						return 0;
					} else if (o1.extractGueltigkeit() == null) {
						return 1;
					} else if (o2.extractGueltigkeit() == null) {
						return -1;
					} else {
						return o1.extractGueltigkeit().getGueltigAb().compareTo(o2.extractGueltigkeit().getGueltigAb());
					}
				}).collect(Collectors.toList())
			));
		}

		// Finanzielle Situation
		if (persistedGesuchstellerCont.getFinanzielleSituationContainer() != null) {
			final JaxFinanzielleSituationContainer jaxFinanzielleSituationContainer = finanzielleSituationContainerToJAX(persistedGesuchstellerCont
				.getFinanzielleSituationContainer());
			jaxGesuchstellerCont.setFinanzielleSituationContainer(jaxFinanzielleSituationContainer);
		}
		// Erwerbspensen
		final Collection<ErwerbspensumContainer> persistedPensen = persistedGesuchstellerCont.getErwerbspensenContainers();
		final List<JaxErwerbspensumContainer> listOfPensen = persistedPensen.stream().map(this::erwerbspensumContainerToJAX).collect(Collectors.toList());
		jaxGesuchstellerCont.setErwerbspensenContainers(listOfPensen);

		// Einkommensverschlechterung
		if (persistedGesuchstellerCont.getEinkommensverschlechterungContainer() != null) {
			final JaxEinkommensverschlechterungContainer jaxEinkVerContainer = einkommensverschlechterungContainerToJAX(persistedGesuchstellerCont
				.getEinkommensverschlechterungContainer());
			jaxGesuchstellerCont.setEinkommensverschlechterungContainer(jaxEinkVerContainer);
		}

		return jaxGesuchstellerCont;
	}

	private List<JaxAdresseContainer> gesuchstellerAdresseContainerListToJAX(@Nonnull Collection<GesuchstellerAdresseContainer> adressen) {
		return adressen.stream().map(this::gesuchstellerAdresseContainerToJAX).collect(Collectors.toList());
	}

	private JaxAdresseContainer gesuchstellerAdresseContainerToJAX(GesuchstellerAdresseContainer persistedAdresse) {
		JaxAdresseContainer jaxAdresse = new JaxAdresseContainer();
		convertAbstractFieldsToJAX(persistedAdresse, jaxAdresse);

		if (persistedAdresse.getGesuchstellerAdresseGS() != null) {
			jaxAdresse.setAdresseGS(gesuchstellerAdresseToJAX(persistedAdresse.getGesuchstellerAdresseGS()));
		}
		if (persistedAdresse.getGesuchstellerAdresseJA() != null) {
			jaxAdresse.setAdresseJA(gesuchstellerAdresseToJAX(persistedAdresse.getGesuchstellerAdresseJA()));
		}
		return jaxAdresse;
	}

	@Nonnull
	public JaxGesuchsteller gesuchstellerToJAX(@Nonnull final Gesuchsteller persistedGesuchsteller) {
		Validate.isTrue(!persistedGesuchsteller.isNew(), "Gesuchsteller kann nicht nach REST transformiert werden weil sie noch " +
			"nicht persistiert wurde; Grund dafuer ist, dass wir die aktuelle Wohnadresse aus der Datenbank lesen wollen");
		final JaxGesuchsteller jaxGesuchsteller = new JaxGesuchsteller();
		convertAbstractPersonFieldsToJAX(persistedGesuchsteller, jaxGesuchsteller);
		jaxGesuchsteller.setMail(persistedGesuchsteller.getMail());
		jaxGesuchsteller.setTelefon(persistedGesuchsteller.getTelefon());
		jaxGesuchsteller.setMobile(persistedGesuchsteller.getMobile());
		jaxGesuchsteller.setTelefonAusland(persistedGesuchsteller.getTelefonAusland());
		jaxGesuchsteller.setEwkPersonId(persistedGesuchsteller.getEwkPersonId());
		jaxGesuchsteller.setEwkAbfrageDatum(persistedGesuchsteller.getEwkAbfrageDatum());
		jaxGesuchsteller.setDiplomatenstatus(persistedGesuchsteller.isDiplomatenstatus());
		return jaxGesuchsteller;
	}

	public Familiensituation familiensituationToEntity(@Nonnull final JaxFamiliensituation familiensituationJAXP, @Nonnull final Familiensituation
		familiensituation) {
		Validate.notNull(familiensituation);
		Validate.notNull(familiensituationJAXP);
		convertAbstractFieldsToEntity(familiensituationJAXP, familiensituation);
		familiensituation.setFamilienstatus(familiensituationJAXP.getFamilienstatus());
		familiensituation.setGesuchstellerKardinalitaet(familiensituationJAXP.getGesuchstellerKardinalitaet());
		familiensituation.setGemeinsameSteuererklaerung(familiensituationJAXP.getGemeinsameSteuererklaerung());
		familiensituation.setAenderungPer(familiensituationJAXP.getAenderungPer());
		familiensituation.setSozialhilfeBezueger(familiensituationJAXP.getSozialhilfeBezueger());
		familiensituation.setVerguenstigungGewuenscht(familiensituationJAXP.getVerguenstigungGewuenscht());
		return familiensituation;
	}

	public JaxFamiliensituation familiensituationToJAX(@Nonnull final Familiensituation persistedFamiliensituation) {
		final JaxFamiliensituation jaxFamiliensituation = new JaxFamiliensituation();
		convertAbstractFieldsToJAX(persistedFamiliensituation, jaxFamiliensituation);
		jaxFamiliensituation.setFamilienstatus(persistedFamiliensituation.getFamilienstatus());
		jaxFamiliensituation.setGesuchstellerKardinalitaet(persistedFamiliensituation.getGesuchstellerKardinalitaet());
		jaxFamiliensituation.setGemeinsameSteuererklaerung(persistedFamiliensituation.getGemeinsameSteuererklaerung());
		jaxFamiliensituation.setAenderungPer(persistedFamiliensituation.getAenderungPer());
		jaxFamiliensituation.setSozialhilfeBezueger(persistedFamiliensituation.getSozialhilfeBezueger());
		jaxFamiliensituation.setVerguenstigungGewuenscht(persistedFamiliensituation.getVerguenstigungGewuenscht());
		return jaxFamiliensituation;
	}

	public FamiliensituationContainer familiensituationContainerToEntity(@Nonnull final JaxFamiliensituationContainer containerJAX,
		@Nonnull final FamiliensituationContainer container) {
		Validate.notNull(container);
		Validate.notNull(containerJAX);
		convertAbstractFieldsToEntity(containerJAX, container);
		Familiensituation famsitToMergeWith;

		if (containerJAX.getFamiliensituationGS() != null) {
			famsitToMergeWith = Optional.ofNullable(container.getFamiliensituationGS()).orElse(new Familiensituation());
			container.setFamiliensituationGS(familiensituationToEntity(containerJAX.getFamiliensituationGS(), famsitToMergeWith));
		}
		if (containerJAX.getFamiliensituationJA() != null) {
			famsitToMergeWith = Optional.ofNullable(container.getFamiliensituationJA()).orElse(new Familiensituation());
			container.setFamiliensituationJA(familiensituationToEntity(containerJAX.getFamiliensituationJA(), famsitToMergeWith));
		}
		if (containerJAX.getFamiliensituationErstgesuch() != null) {
			famsitToMergeWith = Optional.ofNullable(container.getFamiliensituationErstgesuch()).orElse(new Familiensituation());
			container.setFamiliensituationErstgesuch(familiensituationToEntity(containerJAX.getFamiliensituationErstgesuch(), famsitToMergeWith));
		}
		return container;
	}

	public JaxEinkommensverschlechterungInfoContainer einkommensverschlechterungInfoContainerToJAX(final EinkommensverschlechterungInfoContainer
		persistedEinkommensverschlechterungInfo) {
		final JaxEinkommensverschlechterungInfoContainer jaxEkvic = new JaxEinkommensverschlechterungInfoContainer();
		convertAbstractFieldsToJAX(persistedEinkommensverschlechterungInfo, jaxEkvic);
		if (persistedEinkommensverschlechterungInfo.getEinkommensverschlechterungInfoGS() != null) {
			jaxEkvic.setEinkommensverschlechterungInfoGS(einkommensverschlechterungInfoToJAX(persistedEinkommensverschlechterungInfo
				.getEinkommensverschlechterungInfoGS()));
		}
		jaxEkvic.setEinkommensverschlechterungInfoJA(einkommensverschlechterungInfoToJAX(persistedEinkommensverschlechterungInfo
			.getEinkommensverschlechterungInfoJA()));
		return jaxEkvic;
	}

	public EinkommensverschlechterungInfoContainer einkommensverschlechterungInfoContainerToEntity(@Nonnull final JaxEinkommensverschlechterungInfoContainer
		containerJAX,
		@Nonnull final EinkommensverschlechterungInfoContainer container) {
		Validate.notNull(container);
		Validate.notNull(containerJAX);
		convertAbstractFieldsToEntity(containerJAX, container);
		EinkommensverschlechterungInfo evkInfoToMergeWith;
		//Im moment kann eine einmal gespeicherte Finanzielle Situation nicht mehr entfernt werden.
		if (containerJAX.getEinkommensverschlechterungInfoGS() != null) {
			evkInfoToMergeWith = Optional.ofNullable(container.getEinkommensverschlechterungInfoGS()).orElse(new EinkommensverschlechterungInfo());
			container.setEinkommensverschlechterungInfoGS(einkommensverschlechterungInfoToEntity(containerJAX.getEinkommensverschlechterungInfoGS(),
				evkInfoToMergeWith));
		}
		if (containerJAX.getEinkommensverschlechterungInfoJA() != null) {
			evkInfoToMergeWith = Optional.ofNullable(container.getEinkommensverschlechterungInfoJA()).orElse(new EinkommensverschlechterungInfo());
			container.setEinkommensverschlechterungInfoJA(einkommensverschlechterungInfoToEntity(containerJAX.getEinkommensverschlechterungInfoJA(),
				evkInfoToMergeWith));
		}
		return container;
	}

	public JaxFamiliensituationContainer familiensituationContainerToJAX(final FamiliensituationContainer persistedFamiliensituation) {
		final JaxFamiliensituationContainer jaxfc = new JaxFamiliensituationContainer();
		convertAbstractFieldsToJAX(persistedFamiliensituation, jaxfc);
		if (persistedFamiliensituation.getFamiliensituationGS() != null) {
			jaxfc.setFamiliensituationGS(familiensituationToJAX(persistedFamiliensituation.getFamiliensituationGS()));
		}
		if (persistedFamiliensituation.getFamiliensituationJA() != null) {
			jaxfc.setFamiliensituationJA(familiensituationToJAX(persistedFamiliensituation.getFamiliensituationJA()));
		}
		if (persistedFamiliensituation.getFamiliensituationErstgesuch() != null) {
			jaxfc.setFamiliensituationErstgesuch(familiensituationToJAX(persistedFamiliensituation.getFamiliensituationErstgesuch()));
		}
		return jaxfc;
	}

	public EinkommensverschlechterungInfo einkommensverschlechterungInfoToEntity(@Nonnull final JaxEinkommensverschlechterungInfo
		einkommensverschlechterungInfoJAXP, @Nonnull final EinkommensverschlechterungInfo einkommensverschlechterungInfo) {
		Validate.notNull(einkommensverschlechterungInfo);
		Validate.notNull(einkommensverschlechterungInfoJAXP);
		convertAbstractFieldsToEntity(einkommensverschlechterungInfoJAXP, einkommensverschlechterungInfo);
		einkommensverschlechterungInfo.setEinkommensverschlechterung(einkommensverschlechterungInfoJAXP.getEinkommensverschlechterung());
		einkommensverschlechterungInfo.setEkvFuerBasisJahrPlus1(einkommensverschlechterungInfoJAXP.getEkvFuerBasisJahrPlus1());
		einkommensverschlechterungInfo.setEkvFuerBasisJahrPlus2(einkommensverschlechterungInfoJAXP.getEkvFuerBasisJahrPlus2());
		einkommensverschlechterungInfo.setGrundFuerBasisJahrPlus1(einkommensverschlechterungInfoJAXP.getGrundFuerBasisJahrPlus1());
		einkommensverschlechterungInfo.setGrundFuerBasisJahrPlus2(einkommensverschlechterungInfoJAXP.getGrundFuerBasisJahrPlus2());
		einkommensverschlechterungInfo.setStichtagFuerBasisJahrPlus1(einkommensverschlechterungInfoJAXP.getStichtagFuerBasisJahrPlus1());
		einkommensverschlechterungInfo.setStichtagFuerBasisJahrPlus2(einkommensverschlechterungInfoJAXP.getStichtagFuerBasisJahrPlus2());
		einkommensverschlechterungInfo.setGemeinsameSteuererklaerung_BjP1(einkommensverschlechterungInfoJAXP.getGemeinsameSteuererklaerung_BjP1());
		einkommensverschlechterungInfo.setGemeinsameSteuererklaerung_BjP2(einkommensverschlechterungInfoJAXP.getGemeinsameSteuererklaerung_BjP2());
		einkommensverschlechterungInfo.setEkvBasisJahrPlus1Annulliert(einkommensverschlechterungInfoJAXP.getEkvBasisJahrPlus1Annulliert());
		einkommensverschlechterungInfo.setEkvBasisJahrPlus2Annulliert(einkommensverschlechterungInfoJAXP.getEkvBasisJahrPlus2Annulliert());
		return einkommensverschlechterungInfo;
	}

	public JaxEinkommensverschlechterungInfo einkommensverschlechterungInfoToJAX(@Nonnull final EinkommensverschlechterungInfo
		persistedEinkommensverschlechterungInfo) {
		final JaxEinkommensverschlechterungInfo jaxEinkommensverschlechterungInfo = new JaxEinkommensverschlechterungInfo();
		convertAbstractFieldsToJAX(persistedEinkommensverschlechterungInfo, jaxEinkommensverschlechterungInfo);

		jaxEinkommensverschlechterungInfo.setEinkommensverschlechterung(persistedEinkommensverschlechterungInfo.getEinkommensverschlechterung());
		jaxEinkommensverschlechterungInfo.setEkvFuerBasisJahrPlus1(persistedEinkommensverschlechterungInfo.getEkvFuerBasisJahrPlus1());
		jaxEinkommensverschlechterungInfo.setEkvFuerBasisJahrPlus2(persistedEinkommensverschlechterungInfo.getEkvFuerBasisJahrPlus2());
		jaxEinkommensverschlechterungInfo.setGrundFuerBasisJahrPlus1(persistedEinkommensverschlechterungInfo.getGrundFuerBasisJahrPlus1());
		jaxEinkommensverschlechterungInfo.setGrundFuerBasisJahrPlus2(persistedEinkommensverschlechterungInfo.getGrundFuerBasisJahrPlus2());
		jaxEinkommensverschlechterungInfo.setStichtagFuerBasisJahrPlus1(persistedEinkommensverschlechterungInfo.getStichtagFuerBasisJahrPlus1());
		jaxEinkommensverschlechterungInfo.setStichtagFuerBasisJahrPlus2(persistedEinkommensverschlechterungInfo.getStichtagFuerBasisJahrPlus2());
		jaxEinkommensverschlechterungInfo.setGemeinsameSteuererklaerung_BjP1(persistedEinkommensverschlechterungInfo.getGemeinsameSteuererklaerung_BjP1());
		jaxEinkommensverschlechterungInfo.setGemeinsameSteuererklaerung_BjP2(persistedEinkommensverschlechterungInfo.getGemeinsameSteuererklaerung_BjP2());
		jaxEinkommensverschlechterungInfo.setEkvBasisJahrPlus1Annulliert(persistedEinkommensverschlechterungInfo.getEkvBasisJahrPlus1Annulliert());
		jaxEinkommensverschlechterungInfo.setEkvBasisJahrPlus2Annulliert(persistedEinkommensverschlechterungInfo.getEkvBasisJahrPlus2Annulliert());
		return jaxEinkommensverschlechterungInfo;
	}

	public Fall fallToEntity(@Nonnull final JaxFall fallJAXP, @Nonnull final Fall fall) {
		Validate.notNull(fall);
		Validate.notNull(fallJAXP);
		convertAbstractFieldsToEntity(fallJAXP, fall);
		//Fall nummer wird auf server bzw DB verwaltet und daher hier nicht gesetzt
		if (fallJAXP.getVerantwortlicher() != null) {
			Optional<Benutzer> verantwortlicher = benutzerService.findBenutzer(fallJAXP.getVerantwortlicher().getUsername());
			if (verantwortlicher.isPresent()) {
				fall.setVerantwortlicher(verantwortlicher.get()); // because the user doesn't come from the client but from the server
			} else {
				throw new EbeguEntityNotFoundException("fallToEntity", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, fallJAXP.getVerantwortlicher());
			}
		} else {
			fall.setVerantwortlicher(null);
		}
		if (fallJAXP.getVerantwortlicherSCH() != null) {
			Optional<Benutzer> verantwortlicherSCH = benutzerService.findBenutzer(fallJAXP.getVerantwortlicherSCH().getUsername());
			if (verantwortlicherSCH.isPresent()) {
				fall.setVerantwortlicherSCH(verantwortlicherSCH.get()); // because the user doesn't come from the client but from the server
			} else {
				throw new EbeguEntityNotFoundException("fallToEntity", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, fallJAXP.getVerantwortlicherSCH());
			}
		} else {
			fall.setVerantwortlicherSCH(null);
		}
		if (fallJAXP.getNextNumberKind() != null) {
			fall.setNextNumberKind(fallJAXP.getNextNumberKind());
		}
		if (fallJAXP.getBesitzer() != null) {
			Optional<Benutzer> besitzer = benutzerService.findBenutzer(fallJAXP.getBesitzer().getUsername());
			if (besitzer.isPresent()) {
				fall.setBesitzer(besitzer.get()); // because the user doesn't come from the client but from the server
			} else {
				throw new EbeguEntityNotFoundException("fallToEntity", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, fallJAXP.getBesitzer());
			}
		}
		return fall;
	}

	public JaxFall fallToJAX(@Nonnull final Fall persistedFall) {
		final JaxFall jaxFall = new JaxFall();
		convertAbstractFieldsToJAX(persistedFall, jaxFall);
		jaxFall.setFallNummer(persistedFall.getFallNummer());
		if (persistedFall.getVerantwortlicher() != null) {
			jaxFall.setVerantwortlicher(benutzerToAuthLoginElement(persistedFall.getVerantwortlicher()));
		}
		if (persistedFall.getVerantwortlicherSCH() != null) {
			jaxFall.setVerantwortlicherSCH(benutzerToAuthLoginElement(persistedFall.getVerantwortlicherSCH()));
		}
		jaxFall.setNextNumberKind(persistedFall.getNextNumberKind());
		if (persistedFall.getBesitzer() != null) {
			jaxFall.setBesitzer(benutzerToAuthLoginElement(persistedFall.getBesitzer()));
		}
		return jaxFall;
	}

	@SuppressWarnings("PMD.NcssMethodCount")
	public Gesuch gesuchToEntity(@Nonnull final JaxGesuch antragJAXP, @Nonnull final Gesuch antrag) {
		Validate.notNull(antrag);
		Validate.notNull(antragJAXP);

		convertAbstractFieldsToEntity(antragJAXP, antrag);
		final String exceptionString = "gesuchToEntity";
		final Optional<Fall> fallFromDB = fallService.findFall(antragJAXP.getFall().getId());
		if (fallFromDB.isPresent()) {
			antrag.setFall(this.fallToEntity(antragJAXP.getFall(), fallFromDB.get()));
		} else {
			throw new EbeguEntityNotFoundException(exceptionString, ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, antragJAXP.getFall());
		}

		if (antragJAXP.getGesuchsperiode() != null && antragJAXP.getGesuchsperiode().getId() != null) {
			final Optional<Gesuchsperiode> gesuchsperiode = gesuchsperiodeService.findGesuchsperiode(antragJAXP.getGesuchsperiode().getId());
			if (gesuchsperiode.isPresent()) {
				// Gesuchsperiode darf nicht vom Client ueberschrieben werden
				antrag.setGesuchsperiode(gesuchsperiode.get());
			} else {
				throw new EbeguEntityNotFoundException(exceptionString, ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, antragJAXP.getGesuchsperiode().getId());
			}
		}

		antrag.setEingangsdatum(antragJAXP.getEingangsdatum());
		antrag.setFreigabeDatum(antragJAXP.getFreigabeDatum());
		antrag.setStatus(AntragStatusConverterUtil.convertStatusToEntity(antragJAXP.getStatus()));
		if (antragJAXP.getTyp() != null) {
			antrag.setTyp(antragJAXP.getTyp());
		}
		antrag.setEingangsart(antragJAXP.getEingangsart());

		if (antragJAXP.getGesuchsteller1() != null && antragJAXP.getGesuchsteller1().getId() != null) {
			final Optional<GesuchstellerContainer> gesuchsteller1 = gesuchstellerService.findGesuchsteller(antragJAXP.getGesuchsteller1().getId());
			if (gesuchsteller1.isPresent()) {
				antrag.setGesuchsteller1(gesuchstellerContainerToEntity(antragJAXP.getGesuchsteller1(), gesuchsteller1.get()));
			} else {
				throw new EbeguEntityNotFoundException(exceptionString, ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, antragJAXP.getGesuchsteller1().getId());
			}
		}
		if (antragJAXP.getGesuchsteller2() != null && antragJAXP.getGesuchsteller2().getId() != null) {
			final Optional<GesuchstellerContainer> gesuchsteller2 = gesuchstellerService.findGesuchsteller(antragJAXP.getGesuchsteller2().getId());
			if (gesuchsteller2.isPresent()) {
				antrag.setGesuchsteller2(gesuchstellerContainerToEntity(antragJAXP.getGesuchsteller2(), gesuchsteller2.get()));
			} else {
				throw new EbeguEntityNotFoundException(exceptionString, ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, antragJAXP.getGesuchsteller2().getId());
			}
		}
		if (antragJAXP.getFamiliensituationContainer() != null) {
			if (antragJAXP.getFamiliensituationContainer().getId() != null) {
				final Optional<FamiliensituationContainer> familiensituationContainer = familiensituationService.findFamiliensituation(antragJAXP
					.getFamiliensituationContainer().getId());
				if (familiensituationContainer.isPresent()) {
					antrag.setFamiliensituationContainer(familiensituationContainerToEntity(antragJAXP.getFamiliensituationContainer(),
						familiensituationContainer.get()));
				} else {
					throw new EbeguEntityNotFoundException(exceptionString, ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, antragJAXP.getFamiliensituationContainer()
						.getId());
				}
			} else {
				antrag.setFamiliensituationContainer(familiensituationContainerToEntity(antragJAXP.getFamiliensituationContainer(), new
					FamiliensituationContainer()));
			}
		}

		if (antragJAXP.getEinkommensverschlechterungInfoContainer() != null) {
			if (antragJAXP.getEinkommensverschlechterungInfoContainer().getId() != null) {
				final Optional<EinkommensverschlechterungInfoContainer> evkiSituation = einkommensverschlechterungInfoService
					.findEinkommensverschlechterungInfo(antragJAXP.getEinkommensverschlechterungInfoContainer().getId());
				if (evkiSituation.isPresent()) {
					antrag.setEinkommensverschlechterungInfoContainer(einkommensverschlechterungInfoContainerToEntity(antragJAXP
						.getEinkommensverschlechterungInfoContainer(), evkiSituation.get()));
				} else {
					throw new EbeguEntityNotFoundException(exceptionString, ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, antragJAXP
						.getEinkommensverschlechterungInfoContainer().getId());
				}
			} else {
				antrag.setEinkommensverschlechterungInfoContainer(einkommensverschlechterungInfoContainerToEntity(antragJAXP
					.getEinkommensverschlechterungInfoContainer(), new EinkommensverschlechterungInfoContainer()));
			}
		}

		antrag.setBemerkungen(antragJAXP.getBemerkungen());
		antrag.setBemerkungenSTV(antragJAXP.getBemerkungenSTV());
		antrag.setBemerkungenPruefungSTV(antragJAXP.getBemerkungenPruefungSTV());
		antrag.setLaufnummer(antragJAXP.getLaufnummer());
		antrag.setGesuchBetreuungenStatus(antragJAXP.getGesuchBetreuungenStatus());
		antrag.setGeprueftSTV(antragJAXP.isGeprueftSTV());
		antrag.setHasFSDokument(antragJAXP.isHasFSDokument());
		antrag.setFinSitStatus(antragJAXP.getFinSitStatus());
		antrag.setDokumenteHochgeladen(antragJAXP.isDokumenteHochgeladen());
		return antrag;
	}

	public GesuchstellerAdresseContainer gesuchstellerAdresseContainerToEntity(JaxAdresseContainer jaxAdresseCont, GesuchstellerAdresseContainer adresseCont) {
		Validate.notNull(jaxAdresseCont);
		Validate.notNull(adresseCont);
		convertAbstractFieldsToEntity(jaxAdresseCont, adresseCont);
		// ein einmal erstellter GS Container kann nie mehr entfernt werden, daher mergen wir hier nichts wenn null kommt vom client
		if (jaxAdresseCont.getAdresseGS() != null) {
			GesuchstellerAdresse gesuchstellerAdresseGS = new GesuchstellerAdresse();
			if (adresseCont.getGesuchstellerAdresseGS() != null) {
				gesuchstellerAdresseGS = adresseCont.getGesuchstellerAdresseGS();
			}
			adresseCont.setGesuchstellerAdresseGS(gesuchstellerAdresseToEntity(jaxAdresseCont.getAdresseGS(), gesuchstellerAdresseGS));
		}
		// ein erstellter AdresseJA Container kann durch das Jugendamt entfernt werden wenn es sich um eine Korrespondenzaddr oder eine Rechnungsaddr handelt
		if (jaxAdresseCont.getAdresseJA() != null) {
			GesuchstellerAdresse gesuchstellerAdresseJA = new GesuchstellerAdresse();
			if (adresseCont.getGesuchstellerAdresseJA() != null) {
				gesuchstellerAdresseJA = adresseCont.getGesuchstellerAdresseJA();
			}
			adresseCont.setGesuchstellerAdresseJA(gesuchstellerAdresseToEntity(jaxAdresseCont.getAdresseJA(), gesuchstellerAdresseJA));
		} else {
			Validate.isTrue(adresseCont.extractIsKorrespondenzAdresse() || adresseCont.extractIsRechnungsAdresse(), "Nur bei der Korrespondenz- oder "
				+ "Rechnungsadresse kann der AdresseJA Container entfernt werden");
			adresseCont.setGesuchstellerAdresseJA(null);
		}

		return adresseCont;
	}

	public GesuchstellerContainer gesuchstellerContainerToEntity(JaxGesuchstellerContainer jaxGesuchstellerCont, GesuchstellerContainer gesuchstellerCont) {
		Validate.notNull(gesuchstellerCont);
		Validate.notNull(jaxGesuchstellerCont);
		Validate.notNull(jaxGesuchstellerCont.getAdressen(), "Adressen muessen gesetzt sein");

		convertAbstractFieldsToEntity(jaxGesuchstellerCont, gesuchstellerCont);
		//kind daten koennen nicht verschwinden
		if (jaxGesuchstellerCont.getGesuchstellerGS() != null) {
			Gesuchsteller gesuchstellerGS = new Gesuchsteller();
			if (gesuchstellerCont.getGesuchstellerGS() != null) {
				gesuchstellerGS = gesuchstellerCont.getGesuchstellerGS();
			}
			gesuchstellerCont.setGesuchstellerGS(gesuchstellerToEntity(jaxGesuchstellerCont.getGesuchstellerGS(), gesuchstellerGS));
		}
		if (jaxGesuchstellerCont.getGesuchstellerJA() != null) {
			Gesuchsteller gesuchstellerJA = new Gesuchsteller();
			if (gesuchstellerCont.getGesuchstellerJA() != null) {
				gesuchstellerJA = gesuchstellerCont.getGesuchstellerJA();
			}
			gesuchstellerCont.setGesuchstellerJA(gesuchstellerToEntity(jaxGesuchstellerCont.getGesuchstellerJA(), gesuchstellerJA));
		}

		//Relationen
		//Wir fuehren derzeit immer maximal  eine alternative Korrespondenzadressse -> diese updaten wenn vorhanden
		if (jaxGesuchstellerCont.getAlternativeAdresse() != null) {
			final GesuchstellerAdresseContainer currentAltAdr = gesuchstellerAdresseService
				.getKorrespondenzAdr(gesuchstellerCont.getId()).orElse(new GesuchstellerAdresseContainer());
			final GesuchstellerAdresseContainer altAddrToMerge = gesuchstellerAdresseContainerToEntity(jaxGesuchstellerCont.getAlternativeAdresse(),
				currentAltAdr);
			gesuchstellerCont.addAdresse(altAddrToMerge);
		} else { //else case: Wenn das haeklein "Zustell / Postadrsse" auf client weggenommen wird muss die Korrespondezadr auf dem Server geloescht werden.
			gesuchstellerCont.getAdressen().removeIf(GesuchstellerAdresseContainer::extractIsKorrespondenzAdresse);
		}
		if (jaxGesuchstellerCont.getRechnungsAdresse() != null) {
			final GesuchstellerAdresseContainer currentrechnungsAdr = gesuchstellerAdresseService
				.getRechnungsAdr(gesuchstellerCont.getId()).orElse(new GesuchstellerAdresseContainer());
			final GesuchstellerAdresseContainer rechnungsAddrToMerge = gesuchstellerAdresseContainerToEntity(jaxGesuchstellerCont.getRechnungsAdresse(),
				currentrechnungsAdr);
			gesuchstellerCont.addAdresse(rechnungsAddrToMerge);
		} else {//else case: Wenn das haeklein "abweichende Rchnungsadresse" auf client weggenommen wird muss diese adresse auf dem Server geloescht werden.
			gesuchstellerCont.getAdressen().removeIf(GesuchstellerAdresseContainer::extractIsRechnungsAdresse);
		}
		sortAndAddAdressenToGesuchstellerContainer(jaxGesuchstellerCont, gesuchstellerCont);

		// Finanzielle Situation
		if (jaxGesuchstellerCont.getFinanzielleSituationContainer() != null) {
			gesuchstellerCont.setFinanzielleSituationContainer(finanzielleSituationContainerToStorableEntity(jaxGesuchstellerCont
				.getFinanzielleSituationContainer(), null));
		}
		//Erwerbspensum
		jaxGesuchstellerCont.getErwerbspensenContainers()
			.stream()
			.map(this::erwerbspensumContainerToStoreableEntity)
			.forEach(gesuchstellerCont::addErwerbspensumContainer);

		//Einkommensverschlechterung
		final JaxEinkommensverschlechterungContainer einkommensverschlechterungContainer = jaxGesuchstellerCont.getEinkommensverschlechterungContainer();
		if (einkommensverschlechterungContainer != null) {
			gesuchstellerCont.setEinkommensverschlechterungContainer(einkommensverschlechterungContainerToStorableEntity(einkommensverschlechterungContainer));
		}

		return gesuchstellerCont;
	}

	public JaxGesuch gesuchToJAX(@Nonnull final Gesuch persistedGesuch) {
		final JaxGesuch jaxGesuch = new JaxGesuch();
		convertAbstractFieldsToJAX(persistedGesuch, jaxGesuch);
		jaxGesuch.setFall(this.fallToJAX(persistedGesuch.getFall()));
		if (persistedGesuch.getGesuchsperiode() != null) {
			jaxGesuch.setGesuchsperiode(gesuchsperiodeToJAX(persistedGesuch.getGesuchsperiode()));
		}
		jaxGesuch.setEingangsdatum(persistedGesuch.getEingangsdatum());
		jaxGesuch.setFreigabeDatum(persistedGesuch.getFreigabeDatum());
		jaxGesuch.setStatus(AntragStatusConverterUtil.convertStatusToDTO(persistedGesuch, persistedGesuch.getStatus()));
		jaxGesuch.setTyp(persistedGesuch.getTyp());
		jaxGesuch.setEingangsart(persistedGesuch.getEingangsart());

		if (persistedGesuch.getGesuchsteller1() != null) {
			jaxGesuch.setGesuchsteller1(this.gesuchstellerContainerToJAX(persistedGesuch.getGesuchsteller1()));
		}
		if (persistedGesuch.getGesuchsteller2() != null) {
			jaxGesuch.setGesuchsteller2(this.gesuchstellerContainerToJAX(persistedGesuch.getGesuchsteller2()));
		}
		if (persistedGesuch.getFamiliensituationContainer() != null) {
			jaxGesuch.setFamiliensituationContainer(this.familiensituationContainerToJAX(persistedGesuch.getFamiliensituationContainer()));
		}
		for (final KindContainer kind : persistedGesuch.getKindContainers()) {
			jaxGesuch.getKindContainers().add(kindContainerToJAX(kind));
		}
		if (persistedGesuch.getEinkommensverschlechterungInfoContainer() != null) {
			jaxGesuch.setEinkommensverschlechterungInfoContainer(this.einkommensverschlechterungInfoContainerToJAX(persistedGesuch
				.getEinkommensverschlechterungInfoContainer()));
		}
		jaxGesuch.setBemerkungen(persistedGesuch.getBemerkungen());
		jaxGesuch.setBemerkungenSTV(persistedGesuch.getBemerkungenSTV());
		jaxGesuch.setBemerkungenPruefungSTV(persistedGesuch.getBemerkungenPruefungSTV());
		jaxGesuch.setLaufnummer(persistedGesuch.getLaufnummer());
		jaxGesuch.setGesuchBetreuungenStatus(persistedGesuch.getGesuchBetreuungenStatus());
		jaxGesuch.setGeprueftSTV(persistedGesuch.isGeprueftSTV());
		jaxGesuch.setHasFSDokument(persistedGesuch.isHasFSDokument());
		jaxGesuch.setGesperrtWegenBeschwerde(persistedGesuch.isGesperrtWegenBeschwerde());
		jaxGesuch.setDatumGewarntNichtFreigegeben(persistedGesuch.getDatumGewarntNichtFreigegeben());
		jaxGesuch.setDatumGewarntFehlendeQuittung(persistedGesuch.getDatumGewarntFehlendeQuittung());
		jaxGesuch.setTimestampVerfuegt(persistedGesuch.getTimestampVerfuegt());
		jaxGesuch.setGueltig(persistedGesuch.isGueltig());
		jaxGesuch.setDokumenteHochgeladen(persistedGesuch.getDokumenteHochgeladen());
		jaxGesuch.setFinSitStatus(persistedGesuch.getFinSitStatus());
		return jaxGesuch;
	}

	public JaxMandant mandantToJAX(@Nonnull final Mandant persistedMandant) {
		final JaxMandant jaxMandant = new JaxMandant();
		convertAbstractFieldsToJAX(persistedMandant, jaxMandant);
		jaxMandant.setName(persistedMandant.getName());
		return jaxMandant;
	}

	public JaxTraegerschaft traegerschaftToJAX(final Traegerschaft persistedTraegerschaft) {
		final JaxTraegerschaft jaxTraegerschaft = new JaxTraegerschaft();
		convertAbstractFieldsToJAX(persistedTraegerschaft, jaxTraegerschaft);
		jaxTraegerschaft.setName(persistedTraegerschaft.getName());
		jaxTraegerschaft.setActive(persistedTraegerschaft.getActive());
		jaxTraegerschaft.setMail(persistedTraegerschaft.getMail());
		return jaxTraegerschaft;
	}

	public Mandant mandantToEntity(final JaxMandant mandantJAXP, final Mandant mandant) {
		Validate.notNull(mandant);
		Validate.notNull(mandantJAXP);
		convertAbstractFieldsToEntity(mandantJAXP, mandant);
		mandant.setName(mandantJAXP.getName());
		return mandant;
	}

	public Traegerschaft traegerschaftToEntity(@Nonnull final JaxTraegerschaft traegerschaftJAXP, @Nonnull final Traegerschaft traegerschaft) {
		Validate.notNull(traegerschaft);
		Validate.notNull(traegerschaftJAXP);
		convertAbstractFieldsToEntity(traegerschaftJAXP, traegerschaft);
		traegerschaft.setName(traegerschaftJAXP.getName());
		traegerschaft.setActive(traegerschaftJAXP.getActive());
		traegerschaft.setMail(traegerschaftJAXP.getMail());
		return traegerschaft;
	}

	public Fachstelle fachstelleToEntity(final JaxFachstelle fachstelleJAXP, final Fachstelle fachstelle) {
		Validate.notNull(fachstelleJAXP);
		Validate.notNull(fachstelle);
		convertAbstractFieldsToEntity(fachstelleJAXP, fachstelle);
		fachstelle.setName(fachstelleJAXP.getName());
		fachstelle.setBeschreibung(fachstelleJAXP.getBeschreibung());
		fachstelle.setBehinderungsbestaetigung(fachstelleJAXP.isBehinderungsbestaetigung());
		return fachstelle;
	}

	public JaxFachstelle fachstelleToJAX(@Nonnull final Fachstelle persistedFachstelle) {
		final JaxFachstelle jaxFachstelle = new JaxFachstelle();
		convertAbstractFieldsToJAX(persistedFachstelle, jaxFachstelle);
		jaxFachstelle.setName(persistedFachstelle.getName());
		jaxFachstelle.setBeschreibung(persistedFachstelle.getBeschreibung());
		jaxFachstelle.setBehinderungsbestaetigung(persistedFachstelle.isBehinderungsbestaetigung());
		return jaxFachstelle;
	}

	public JaxInstitution institutionToJAX(final Institution persistedInstitution) {
		final JaxInstitution jaxInstitution = new JaxInstitution();
		convertAbstractFieldsToJAX(persistedInstitution, jaxInstitution);
		jaxInstitution.setName(persistedInstitution.getName());
		jaxInstitution.setMandant(mandantToJAX(persistedInstitution.getMandant()));
		if (persistedInstitution.getTraegerschaft() != null) {
			jaxInstitution.setTraegerschaft(traegerschaftToJAX(persistedInstitution.getTraegerschaft()));
		}
		jaxInstitution.setMail(persistedInstitution.getMail());
		return jaxInstitution;
	}

	public Institution institutionToEntity(final JaxInstitution institutionJAXP, final Institution institution) {
		Validate.notNull(institutionJAXP);
		Validate.notNull(institution);
		convertAbstractFieldsToEntity(institutionJAXP, institution);
		institution.setName(institutionJAXP.getName());
		institution.setMail(institutionJAXP.getMail());

		if (institutionJAXP.getMandant() != null && institutionJAXP.getMandant().getId() != null) {
			final Optional<Mandant> mandantFromDB = mandantService.findMandant(institutionJAXP.getMandant().getId());
			if (mandantFromDB.isPresent()) {
				// Mandant darf nicht vom Client ueberschrieben werden
				institution.setMandant(mandantFromDB.get());
			} else {
				throw new EbeguEntityNotFoundException("institutionToEntity -> mandant", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND,
					institutionJAXP.getMandant().getId());
			}
		} else {
			throw new EbeguEntityNotFoundException("institutionToEntity -> mandant", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND);
		}

		// Traegerschaft ist nicht required!
		if (institutionJAXP.getTraegerschaft() != null) {
			if (institutionJAXP.getTraegerschaft().getId() != null) {
				final Optional<Traegerschaft> traegerschaftFromDB = traegerschaftService.findTraegerschaft(institutionJAXP.getTraegerschaft().getId());
				if (traegerschaftFromDB.isPresent()) {
					// Traegerschaft darf nicht vom Client ueberschrieben werden
					institution.setTraegerschaft(traegerschaftFromDB.get());
				} else {
					throw new EbeguEntityNotFoundException("institutionToEntity -> traegerschaft", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, institutionJAXP
						.getTraegerschaft().getId());
				}
			} else {
				throw new EbeguEntityNotFoundException("institutionToEntity -> traegerschaft", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND);
			}
		} else {
			institution.setTraegerschaft(null);
		}
		return institution;
	}

	public JaxInstitutionStammdaten institutionStammdatenToJAX(@Nonnull final InstitutionStammdaten persistedInstStammdaten) {
		final JaxInstitutionStammdaten jaxInstStammdaten = new JaxInstitutionStammdaten();
		convertAbstractDateRangedFieldsToJAX(persistedInstStammdaten, jaxInstStammdaten);
		jaxInstStammdaten.setOeffnungstage(persistedInstStammdaten.getOeffnungstage());
		jaxInstStammdaten.setOeffnungsstunden(persistedInstStammdaten.getOeffnungsstunden());
		if (persistedInstStammdaten.getIban() != null) {
			jaxInstStammdaten.setIban(persistedInstStammdaten.getIban().getIban());
		}
		jaxInstStammdaten.setBetreuungsangebotTyp(persistedInstStammdaten.getBetreuungsangebotTyp());
		if (persistedInstStammdaten.getInstitutionStammdatenTagesschule() != null) {
			jaxInstStammdaten.setInstitutionStammdatenTagesschule(institutionStammdatenTagesschuleToJAX(persistedInstStammdaten
				.getInstitutionStammdatenTagesschule()));
		}
		if (persistedInstStammdaten.getInstitutionStammdatenFerieninsel() != null) {
			jaxInstStammdaten.setInstitutionStammdatenFerieninsel(institutionStammdatenFerieninselToJAX(persistedInstStammdaten
				.getInstitutionStammdatenFerieninsel()));
		}
		jaxInstStammdaten.setInstitution(institutionToJAX(persistedInstStammdaten.getInstitution()));
		jaxInstStammdaten.setAdresse(adresseToJAX(persistedInstStammdaten.getAdresse()));
		jaxInstStammdaten.setKontoinhaber(persistedInstStammdaten.getKontoinhaber());
		if (persistedInstStammdaten.getAdresseKontoinhaber() != null) {
			jaxInstStammdaten.setAdresseKontoinhaber(adresseToJAX(persistedInstStammdaten.getAdresseKontoinhaber()));
		}
		return jaxInstStammdaten;
	}

	public InstitutionStammdaten institutionStammdatenToEntity(final JaxInstitutionStammdaten institutionStammdatenJAXP,
		final InstitutionStammdaten institutionStammdaten) {

		Validate.notNull(institutionStammdatenJAXP);
		Validate.notNull(institutionStammdatenJAXP.getInstitution());
		Validate.notNull(institutionStammdaten);
		Validate.notNull(institutionStammdaten.getAdresse());

		convertAbstractDateRangedFieldsToEntity(institutionStammdatenJAXP, institutionStammdaten);
		institutionStammdaten.setOeffnungstage(institutionStammdatenJAXP.getOeffnungstage());
		institutionStammdaten.setOeffnungsstunden(institutionStammdatenJAXP.getOeffnungsstunden());
		if (institutionStammdatenJAXP.getIban() != null) {
			institutionStammdaten.setIban(new IBAN(institutionStammdatenJAXP.getIban()));
		}
		institutionStammdaten.setBetreuungsangebotTyp(institutionStammdatenJAXP.getBetreuungsangebotTyp());
		if (institutionStammdatenJAXP.getInstitutionStammdatenTagesschule() != null) {
			//			wenn InstitutionStammdatenTagesschule vorhanden ist es eine Tagesschule und Objekt muss, wenn noch nicht vorhanden, erzeugt werden
			if (institutionStammdaten.getInstitutionStammdatenTagesschule() != null) {
				institutionStammdaten.setInstitutionStammdatenTagesschule(institutionStammdatenTagesschuleToEntity(institutionStammdatenJAXP
					.getInstitutionStammdatenTagesschule(), institutionStammdaten.getInstitutionStammdatenTagesschule()));
			} else {
				institutionStammdaten.setInstitutionStammdatenTagesschule(institutionStammdatenTagesschuleToEntity(institutionStammdatenJAXP
					.getInstitutionStammdatenTagesschule(), new InstitutionStammdatenTagesschule()));
			}
		}
		if (institutionStammdatenJAXP.getInstitutionStammdatenFerieninsel() != null) {
			if (institutionStammdaten.getInstitutionStammdatenFerieninsel() != null) {
				institutionStammdaten.setInstitutionStammdatenFerieninsel(institutionStammdatenFerieninselToEntity(institutionStammdatenJAXP
					.getInstitutionStammdatenFerieninsel(), institutionStammdaten.getInstitutionStammdatenFerieninsel()));
			} else {
				institutionStammdaten.setInstitutionStammdatenFerieninsel(institutionStammdatenFerieninselToEntity(institutionStammdatenJAXP
					.getInstitutionStammdatenFerieninsel(), new InstitutionStammdatenFerieninsel()));
			}
		}
		institutionStammdaten.setKontoinhaber(institutionStammdatenJAXP.getKontoinhaber());
		if (institutionStammdatenJAXP.getAdresseKontoinhaber() != null) {
			if (institutionStammdaten.getAdresseKontoinhaber() != null) {
				institutionStammdaten.setAdresseKontoinhaber(adresseToEntity(institutionStammdatenJAXP.getAdresseKontoinhaber(), institutionStammdaten
					.getAdresseKontoinhaber()));
			} else {
				institutionStammdaten.setAdresseKontoinhaber(adresseToEntity(institutionStammdatenJAXP.getAdresseKontoinhaber(), new Adresse()));
			}
		}

		adresseToEntity(institutionStammdatenJAXP.getAdresse(), institutionStammdaten.getAdresse());

		final Optional<Institution> institutionFromDB = institutionService.findInstitution(institutionStammdatenJAXP.getInstitution().getId());
		if (institutionFromDB.isPresent()) {
			// Institution darf nicht vom Client ueberschrieben werden
			institutionStammdaten.setInstitution(institutionFromDB.get());
		} else {
			throw new EbeguEntityNotFoundException("institutionStammdatenToEntity", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, institutionStammdatenJAXP
				.getInstitution().getId());
		}

		return institutionStammdaten;

	}

	public JaxInstitutionStammdatenFerieninsel institutionStammdatenFerieninselToJAX(@Nonnull final InstitutionStammdatenFerieninsel
		persistedInstStammdatenFerieninsel) {
		final JaxInstitutionStammdatenFerieninsel jaxInstStammdatenFerieninsel = new JaxInstitutionStammdatenFerieninsel();
		convertAbstractFieldsToJAX(persistedInstStammdatenFerieninsel, jaxInstStammdatenFerieninsel);
		jaxInstStammdatenFerieninsel.setAusweichstandortFruehlingsferien(persistedInstStammdatenFerieninsel.getAusweichstandortFruehlingsferien());
		jaxInstStammdatenFerieninsel.setAusweichstandortHerbstferien(persistedInstStammdatenFerieninsel.getAusweichstandortHerbstferien());
		jaxInstStammdatenFerieninsel.setAusweichstandortSommerferien(persistedInstStammdatenFerieninsel.getAusweichstandortSommerferien());
		jaxInstStammdatenFerieninsel.setAusweichstandortSportferien(persistedInstStammdatenFerieninsel.getAusweichstandortSportferien());
		return jaxInstStammdatenFerieninsel;
	}

	@Nullable
	public InstitutionStammdatenFerieninsel institutionStammdatenFerieninselToEntity(final JaxInstitutionStammdatenFerieninsel
		institutionStammdatenFerieninselJAXP, final InstitutionStammdatenFerieninsel institutionStammdatenFerieninsel) {
		Validate.notNull(institutionStammdatenFerieninselJAXP);
		Validate.notNull(institutionStammdatenFerieninsel);
		convertAbstractFieldsToEntity(institutionStammdatenFerieninselJAXP, institutionStammdatenFerieninsel);

		institutionStammdatenFerieninsel.setAusweichstandortFruehlingsferien(institutionStammdatenFerieninselJAXP.getAusweichstandortFruehlingsferien());
		institutionStammdatenFerieninsel.setAusweichstandortHerbstferien(institutionStammdatenFerieninselJAXP.getAusweichstandortHerbstferien());
		institutionStammdatenFerieninsel.setAusweichstandortSommerferien(institutionStammdatenFerieninselJAXP.getAusweichstandortSommerferien());
		institutionStammdatenFerieninsel.setAusweichstandortSportferien(institutionStammdatenFerieninselJAXP.getAusweichstandortSportferien());

		return institutionStammdatenFerieninsel;
	}

	public JaxInstitutionStammdatenTagesschule institutionStammdatenTagesschuleToJAX(@Nonnull final InstitutionStammdatenTagesschule
		persistedInstStammdatenTagesschule) {
		final JaxInstitutionStammdatenTagesschule jaxInstStammdatenTagesschule = new JaxInstitutionStammdatenTagesschule();
		convertAbstractFieldsToJAX(persistedInstStammdatenTagesschule, jaxInstStammdatenTagesschule);
		jaxInstStammdatenTagesschule.setModuleTagesschule(moduleTagesschuleListToJax(persistedInstStammdatenTagesschule.getModuleTagesschule()));
		return jaxInstStammdatenTagesschule;
	}

	@Nullable
	public InstitutionStammdatenTagesschule institutionStammdatenTagesschuleToEntity(final JaxInstitutionStammdatenTagesschule
		institutionStammdatenTagesschuleJAXP, final InstitutionStammdatenTagesschule institutionStammdatenTagesschule) {
		Validate.notNull(institutionStammdatenTagesschuleJAXP);
		Validate.notNull(institutionStammdatenTagesschule);
		convertAbstractFieldsToEntity(institutionStammdatenTagesschuleJAXP, institutionStammdatenTagesschule);

		final Set<ModulTagesschule> convertedModuleTagesschule = moduleTagesschuleListToEntity(institutionStammdatenTagesschuleJAXP.getModuleTagesschule(),
			institutionStammdatenTagesschule.getModuleTagesschule(), institutionStammdatenTagesschule);
		if (convertedModuleTagesschule != null) {
			//change the existing collection to reflect changes
			// Already tested: All existing module of the list remain as they were, that means their data are updated
			// and the objects are not created again. ID and InsertTimeStamp are the same as before
			institutionStammdatenTagesschule.getModuleTagesschule().clear();
			institutionStammdatenTagesschule.getModuleTagesschule().addAll(convertedModuleTagesschule);
		}

		if (institutionStammdatenTagesschule.getModuleTagesschule() != null && !institutionStammdatenTagesschule.getModuleTagesschule().isEmpty()) {
			return institutionStammdatenTagesschule;
		}
		return null;

	}

	@Nullable
	private Set<ModulTagesschule> moduleTagesschuleListToEntity(@Nullable List<JaxModulTagesschule> jaxModuleTagesschule, @Nullable Set<ModulTagesschule> moduleOfInstitution,
		@NotNull InstitutionStammdatenTagesschule institutionStammdatenTagesschule) {

		if (moduleOfInstitution != null && jaxModuleTagesschule != null) {
			final Set<ModulTagesschule> transformedModule = new TreeSet<>();
			for (final JaxModulTagesschule jaxModulTagesschule : jaxModuleTagesschule) {
				final ModulTagesschule modulTagesschuleToMergeWith = moduleOfInstitution
					.stream()
					.filter(existingModul -> existingModul.getId().equalsIgnoreCase(jaxModulTagesschule.getId()))
					.reduce(StreamsUtil.toOnlyElement())
					.orElse(new ModulTagesschule());
				final ModulTagesschule modulTagesschuleToAdd = modulTagesschuleToEntity(jaxModulTagesschule, modulTagesschuleToMergeWith);
				if (modulTagesschuleToAdd != null) {
					modulTagesschuleToAdd.setInstitutionStammdatenTagesschule(institutionStammdatenTagesschule);
					final boolean added = transformedModule.add(modulTagesschuleToAdd);
					if (!added) {
						LOGGER.warn(DROPPED_DUPLICATE_CONTAINER + "{}", modulTagesschuleToAdd);
					}
				}
			}
			return transformedModule;
		}
		return null;
	}

	@Nullable
	private ModulTagesschule modulTagesschuleToEntity(@Nullable JaxModulTagesschule jaxModulTagesschule, @NotNull ModulTagesschule modulTagesschule) {
		if (jaxModulTagesschule != null && jaxModulTagesschule.getZeitVon() != null && jaxModulTagesschule.getZeitBis() != null) {
			convertAbstractFieldsToEntity(jaxModulTagesschule, modulTagesschule);
			modulTagesschule.setModulTagesschuleName(jaxModulTagesschule.getModulTagesschuleName());
			modulTagesschule.setWochentag(jaxModulTagesschule.getWochentag());
			modulTagesschule.setZeitVon(jaxModulTagesschule.getZeitVon().toLocalTime());
			modulTagesschule.setZeitBis(jaxModulTagesschule.getZeitBis().toLocalTime());
			return modulTagesschule;
		}
		return null;
	}

	@Nonnull
	public FinanzielleSituationContainer finanzielleSituationContainerToStorableEntity(@Nonnull final JaxFinanzielleSituationContainer containerJAX,
		@Nullable FinanzielleSituationContainer container) {
		Validate.notNull(containerJAX);
		FinanzielleSituationContainer containerToMergeWith = container != null ? container : new FinanzielleSituationContainer();
		if (containerJAX.getId() != null) {
			final Optional<FinanzielleSituationContainer> existingFSC = finanzielleSituationService.findFinanzielleSituation(containerJAX.getId());
			if (existingFSC.isPresent()) {
				containerToMergeWith = existingFSC.get();
			}
		}
		final FinanzielleSituationContainer mergedContainer = finanzielleSituationContainerToEntity(containerJAX, containerToMergeWith);
		return mergedContainer;
	}

	public EinkommensverschlechterungContainer einkommensverschlechterungContainerToStorableEntity(@Nonnull final JaxEinkommensverschlechterungContainer
		containerJAX) {
		Validate.notNull(containerJAX);
		EinkommensverschlechterungContainer containerToMergeWith = new EinkommensverschlechterungContainer();
		if (containerJAX.getId() != null) {
			final Optional<EinkommensverschlechterungContainer> existingEkvC = einkommensverschlechterungService.findEinkommensverschlechterungContainer
				(containerJAX.getId());
			if (existingEkvC.isPresent()) {
				containerToMergeWith = existingEkvC.get();
			}
		}
		final EinkommensverschlechterungContainer mergedContainer = einkommensverschlechterungContainerToEntity(containerJAX, containerToMergeWith);
		return mergedContainer;
	}

	@Nonnull
	public JaxKind kindToJAX(@Nonnull final Kind persistedKind) {
		final JaxKind jaxKind = new JaxKind();
		convertAbstractPersonFieldsToJAX(persistedKind, jaxKind);
		jaxKind.setWohnhaftImGleichenHaushalt(persistedKind.getWohnhaftImGleichenHaushalt());
		jaxKind.setKinderabzug(persistedKind.getKinderabzug());
		jaxKind.setFamilienErgaenzendeBetreuung(persistedKind.getFamilienErgaenzendeBetreuung());
		jaxKind.setMutterspracheDeutsch(persistedKind.getMutterspracheDeutsch());
		jaxKind.setEinschulung(persistedKind.getEinschulung());
		jaxKind.setPensumFachstelle(pensumFachstelleToJax(persistedKind.getPensumFachstelle()));
		return jaxKind;
	}

	@Nullable
	public JaxPensumFachstelle pensumFachstelleToJax(@Nullable final PensumFachstelle persistedPensumFachstelle) {
		if (persistedPensumFachstelle == null) {
			return null;
		}
		final JaxPensumFachstelle jaxPensumFachstelle = new JaxPensumFachstelle();
		convertAbstractPensumFieldsToJAX(persistedPensumFachstelle, jaxPensumFachstelle);
		jaxPensumFachstelle.setFachstelle(fachstelleToJAX(persistedPensumFachstelle.getFachstelle()));
		return jaxPensumFachstelle;
	}

	public PensumFachstelle pensumFachstelleToEntity(final JaxPensumFachstelle pensumFachstelleJAXP, final PensumFachstelle pensumFachstelle) {
		Validate.notNull(pensumFachstelleJAXP.getFachstelle(), "Fachstelle muss existieren");
		Validate.notNull(pensumFachstelleJAXP.getFachstelle().getId(), "Fachstelle muss bereits gespeichert sein");
		convertAbstractPensumFieldsToEntity(pensumFachstelleJAXP, pensumFachstelle);

		final Optional<Fachstelle> fachstelleFromDB = fachstelleService.findFachstelle(pensumFachstelleJAXP.getFachstelle().getId());
		if (fachstelleFromDB.isPresent()) {
			// Fachstelle darf nicht vom Client ueberschrieben werden
			pensumFachstelle.setFachstelle(fachstelleFromDB.get());
		} else {
			throw new EbeguEntityNotFoundException("pensumFachstelleToEntity", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, pensumFachstelleJAXP.getFachstelle()
				.getId());
		}

		return pensumFachstelle;
	}

	private PensumFachstelle toStorablePensumFachstelle(@Nonnull final JaxPensumFachstelle pensumFsToSave) {
		PensumFachstelle pensumToMergeWith = new PensumFachstelle();
		if (pensumFsToSave.getId() != null) {
			final Optional<PensumFachstelle> pensumFachstelleOpt = pensumFachstelleService.findPensumFachstelle(pensumFsToSave.getId());
			if (pensumFachstelleOpt.isPresent()) {
				pensumToMergeWith = pensumFachstelleOpt.get();
			} else {
				throw new EbeguEntityNotFoundException("toStorablePensumFachstelle", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, pensumFsToSave.getId());
			}
		}
		return pensumFachstelleToEntity(pensumFsToSave, pensumToMergeWith);
	}

	public JaxKindContainer kindContainerToJAX(final KindContainer persistedKind) {
		final JaxKindContainer jaxKindContainer = new JaxKindContainer();
		convertAbstractFieldsToJAX(persistedKind, jaxKindContainer);
		if (persistedKind.getKindGS() != null) {
			jaxKindContainer.setKindGS(kindToJAX(persistedKind.getKindGS()));
		}
		if (persistedKind.getKindJA() != null) {
			jaxKindContainer.setKindJA(kindToJAX(persistedKind.getKindJA()));
		}
		jaxKindContainer.setBetreuungen(betreuungListToJax(persistedKind.getBetreuungen()));
		jaxKindContainer.setKindNummer(persistedKind.getKindNummer());
		jaxKindContainer.setNextNumberBetreuung(persistedKind.getNextNumberBetreuung());
		return jaxKindContainer;
	}

	public Kind kindToEntity(final JaxKind kindJAXP, final Kind kind) {
		Validate.notNull(kindJAXP);
		Validate.notNull(kind);
		convertAbstractPersonFieldsToEntity(kindJAXP, kind);
		kind.setWohnhaftImGleichenHaushalt(kindJAXP.getWohnhaftImGleichenHaushalt());
		kind.setKinderabzug(kindJAXP.getKinderabzug());
		kind.setFamilienErgaenzendeBetreuung(kindJAXP.getFamilienErgaenzendeBetreuung());
		kind.setMutterspracheDeutsch(kindJAXP.getMutterspracheDeutsch());
		kind.setEinschulung(kindJAXP.getEinschulung());

		PensumFachstelle updtPensumFachstelle = null;
		if (kindJAXP.getPensumFachstelle() != null) {
			updtPensumFachstelle = toStorablePensumFachstelle(kindJAXP.getPensumFachstelle());
		}
		kind.setPensumFachstelle(updtPensumFachstelle);

		return kind;
	}

	public KindContainer kindContainerToEntity(@Nonnull final JaxKindContainer kindContainerJAXP, @Nonnull final KindContainer kindContainer) {
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
		if (kindContainerJAXP.getNextNumberBetreuung() != null) {
			kindContainer.setNextNumberBetreuung(kindContainerJAXP.getNextNumberBetreuung());
		}
		return kindContainer;
	}

	/**
	 * Sucht das Gesuch in der DB und fuegt es mit dem als Parameter gegebenen Gesuch zusammen.
	 * Sollte es in der DB nicht existieren, gibt die Methode ein neues Gesuch mit den gegebenen Daten zurueck
	 *
	 * @param gesuchToFind das Gesuch als JAX
	 * @return das Gesuch als Entity
	 */
	@Nonnull
	public Gesuch gesuchToStoreableEntity(final JaxGesuch gesuchToFind) {
		Validate.notNull(gesuchToFind);
		Gesuch gesuchToMergeWith = new Gesuch();
		if (gesuchToFind.getId() != null) {
			final Optional<Gesuch> altGesuch = gesuchService.findGesuch(gesuchToFind.getId());
			if (altGesuch.isPresent()) {
				gesuchToMergeWith = altGesuch.get();
			}
		}
		return gesuchToEntity(gesuchToFind, gesuchToMergeWith);
	}

	@Nonnull
	public FinanzielleSituationContainer finanzielleSituationContainerToEntity(@Nonnull final JaxFinanzielleSituationContainer containerJAX,
		@Nonnull final FinanzielleSituationContainer container) {
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
		return container;
	}

	@Nonnull
	public JaxFinanzielleSituationContainer finanzielleSituationContainerToJAX(@Nonnull final FinanzielleSituationContainer
		persistedFinanzielleSituation) {
		final JaxFinanzielleSituationContainer jaxPerson = new JaxFinanzielleSituationContainer();
		convertAbstractFieldsToJAX(persistedFinanzielleSituation, jaxPerson);
		jaxPerson.setJahr(persistedFinanzielleSituation.getJahr());
		jaxPerson.setFinanzielleSituationGS(finanzielleSituationToJAX(persistedFinanzielleSituation.getFinanzielleSituationGS()));
		jaxPerson.setFinanzielleSituationJA(finanzielleSituationToJAX(persistedFinanzielleSituation.getFinanzielleSituationJA()));
		return jaxPerson;
	}

	public EinkommensverschlechterungContainer einkommensverschlechterungContainerToEntity(@Nonnull final JaxEinkommensverschlechterungContainer containerJAX,
		@Nonnull final EinkommensverschlechterungContainer container) {
		Validate.notNull(container);
		Validate.notNull(containerJAX);
		convertAbstractFieldsToEntity(containerJAX, container);

		Einkommensverschlechterung einkommensverschlechterung;

		if (containerJAX.getEkvGSBasisJahrPlus1() != null) {
			einkommensverschlechterung = Optional.ofNullable(container.getEkvGSBasisJahrPlus1()).orElse(new Einkommensverschlechterung());
			container.setEkvGSBasisJahrPlus1(einkommensverschlechterungToEntity(containerJAX.getEkvGSBasisJahrPlus1(), einkommensverschlechterung));
		}
		if (containerJAX.getEkvGSBasisJahrPlus2() != null) {
			einkommensverschlechterung = Optional.ofNullable(container.getEkvGSBasisJahrPlus2()).orElse(new Einkommensverschlechterung());
			container.setEkvGSBasisJahrPlus2(einkommensverschlechterungToEntity(containerJAX.getEkvGSBasisJahrPlus2(), einkommensverschlechterung));
		}
		if (containerJAX.getEkvJABasisJahrPlus1() != null) {
			einkommensverschlechterung = Optional.ofNullable(container.getEkvJABasisJahrPlus1()).orElse(new Einkommensverschlechterung());
			container.setEkvJABasisJahrPlus1(einkommensverschlechterungToEntity(containerJAX.getEkvJABasisJahrPlus1(), einkommensverschlechterung));
		}
		if (containerJAX.getEkvJABasisJahrPlus2() != null) {
			einkommensverschlechterung = Optional.ofNullable(container.getEkvJABasisJahrPlus2()).orElse(new Einkommensverschlechterung());
			container.setEkvJABasisJahrPlus2(einkommensverschlechterungToEntity(containerJAX.getEkvJABasisJahrPlus2(), einkommensverschlechterung));
		}

		return container;
	}

	public JaxEinkommensverschlechterungContainer einkommensverschlechterungContainerToJAX(final EinkommensverschlechterungContainer persistedEkv) {
		if (persistedEkv != null) {
			final JaxEinkommensverschlechterungContainer jaxEinkommensverschlechterung = new JaxEinkommensverschlechterungContainer();
			convertAbstractFieldsToJAX(persistedEkv, jaxEinkommensverschlechterung);
			jaxEinkommensverschlechterung.setEkvGSBasisJahrPlus1(einkommensverschlechterungToJAX(persistedEkv.getEkvGSBasisJahrPlus1()));
			jaxEinkommensverschlechterung.setEkvGSBasisJahrPlus2(einkommensverschlechterungToJAX(persistedEkv.getEkvGSBasisJahrPlus2()));
			jaxEinkommensverschlechterung.setEkvJABasisJahrPlus1(einkommensverschlechterungToJAX(persistedEkv.getEkvJABasisJahrPlus1()));
			jaxEinkommensverschlechterung.setEkvJABasisJahrPlus2(einkommensverschlechterungToJAX(persistedEkv.getEkvJABasisJahrPlus2()));
			return jaxEinkommensverschlechterung;
		}
		return null;
	}

	private AbstractFinanzielleSituation abstractFinanzielleSituationToEntity(@Nonnull final JaxAbstractFinanzielleSituation abstractFinanzielleSituationJAXP,
		@Nonnull final AbstractFinanzielleSituation abstractFinanzielleSituation) {
		Validate.notNull(abstractFinanzielleSituation);
		Validate.notNull(abstractFinanzielleSituationJAXP);
		convertAbstractFieldsToEntity(abstractFinanzielleSituationJAXP, abstractFinanzielleSituation);
		abstractFinanzielleSituation.setSteuerveranlagungErhalten(abstractFinanzielleSituationJAXP.getSteuerveranlagungErhalten());
		abstractFinanzielleSituation.setSteuererklaerungAusgefuellt(abstractFinanzielleSituationJAXP.getSteuererklaerungAusgefuellt());

		abstractFinanzielleSituation.setFamilienzulage(abstractFinanzielleSituationJAXP.getFamilienzulage());
		abstractFinanzielleSituation.setErsatzeinkommen(abstractFinanzielleSituationJAXP.getErsatzeinkommen());
		abstractFinanzielleSituation.setErhalteneAlimente(abstractFinanzielleSituationJAXP.getErhalteneAlimente());
		abstractFinanzielleSituation.setBruttovermoegen(abstractFinanzielleSituationJAXP.getBruttovermoegen());
		abstractFinanzielleSituation.setSchulden(abstractFinanzielleSituationJAXP.getSchulden());
		abstractFinanzielleSituation.setGeschaeftsgewinnBasisjahr(abstractFinanzielleSituationJAXP.getGeschaeftsgewinnBasisjahr());
		abstractFinanzielleSituation.setGeleisteteAlimente(abstractFinanzielleSituationJAXP.getGeleisteteAlimente());

		return abstractFinanzielleSituation;
	}

	private void abstractFinanzielleSituationToJAX(@Nullable final AbstractFinanzielleSituation persistedAbstractFinanzielleSituation,
		JaxAbstractFinanzielleSituation jaxAbstractFinanzielleSituation) {
		if (persistedAbstractFinanzielleSituation != null) {
			convertAbstractFieldsToJAX(persistedAbstractFinanzielleSituation, jaxAbstractFinanzielleSituation);
			jaxAbstractFinanzielleSituation.setSteuerveranlagungErhalten(persistedAbstractFinanzielleSituation.getSteuerveranlagungErhalten());
			jaxAbstractFinanzielleSituation.setSteuererklaerungAusgefuellt(persistedAbstractFinanzielleSituation.getSteuererklaerungAusgefuellt());
			jaxAbstractFinanzielleSituation.setFamilienzulage(persistedAbstractFinanzielleSituation.getFamilienzulage());
			jaxAbstractFinanzielleSituation.setErsatzeinkommen(persistedAbstractFinanzielleSituation.getErsatzeinkommen());
			jaxAbstractFinanzielleSituation.setErhalteneAlimente(persistedAbstractFinanzielleSituation.getErhalteneAlimente());
			jaxAbstractFinanzielleSituation.setBruttovermoegen(persistedAbstractFinanzielleSituation.getBruttovermoegen());
			jaxAbstractFinanzielleSituation.setSchulden(persistedAbstractFinanzielleSituation.getSchulden());
			jaxAbstractFinanzielleSituation.setGeschaeftsgewinnBasisjahr(persistedAbstractFinanzielleSituation.getGeschaeftsgewinnBasisjahr());
			jaxAbstractFinanzielleSituation.setGeleisteteAlimente(persistedAbstractFinanzielleSituation.getGeleisteteAlimente());
		}
	}

	private FinanzielleSituation finanzielleSituationToEntity(@Nonnull final JaxFinanzielleSituation finanzielleSituationJAXP, @Nonnull final
	FinanzielleSituation finanzielleSituation) {
		Validate.notNull(finanzielleSituation);
		Validate.notNull(finanzielleSituationJAXP);
		abstractFinanzielleSituationToEntity(finanzielleSituationJAXP, finanzielleSituation);

		finanzielleSituation.setNettolohn(finanzielleSituationJAXP.getNettolohn());
		finanzielleSituation.setGeschaeftsgewinnBasisjahrMinus2(finanzielleSituationJAXP.getGeschaeftsgewinnBasisjahrMinus2());
		finanzielleSituation.setGeschaeftsgewinnBasisjahrMinus1(finanzielleSituationJAXP.getGeschaeftsgewinnBasisjahrMinus1());
		return finanzielleSituation;
	}

	@Nullable
	private JaxFinanzielleSituation finanzielleSituationToJAX(@Nullable final FinanzielleSituation persistedFinanzielleSituation) {

		if (persistedFinanzielleSituation != null) {
			JaxFinanzielleSituation jaxFinanzielleSituation = new JaxFinanzielleSituation();
			abstractFinanzielleSituationToJAX(persistedFinanzielleSituation, jaxFinanzielleSituation);
			jaxFinanzielleSituation.setGeschaeftsgewinnBasisjahrMinus2(persistedFinanzielleSituation.getGeschaeftsgewinnBasisjahrMinus2());
			jaxFinanzielleSituation.setGeschaeftsgewinnBasisjahrMinus1(persistedFinanzielleSituation.getGeschaeftsgewinnBasisjahrMinus1());
			jaxFinanzielleSituation.setNettolohn(persistedFinanzielleSituation.getNettolohn());
			return jaxFinanzielleSituation;

		}
		return null;
	}

	private Einkommensverschlechterung einkommensverschlechterungToEntity(@Nonnull final JaxEinkommensverschlechterung einkommensverschlechterungJAXP,
		@Nonnull final Einkommensverschlechterung einkommensverschlechterung) {
		Validate.notNull(einkommensverschlechterung);
		Validate.notNull(einkommensverschlechterungJAXP);
		abstractFinanzielleSituationToEntity(einkommensverschlechterungJAXP, einkommensverschlechterung);

		einkommensverschlechterung.setNettolohnJan(einkommensverschlechterungJAXP.getNettolohnJan());
		einkommensverschlechterung.setNettolohnFeb(einkommensverschlechterungJAXP.getNettolohnFeb());
		einkommensverschlechterung.setNettolohnMrz(einkommensverschlechterungJAXP.getNettolohnMrz());
		einkommensverschlechterung.setNettolohnApr(einkommensverschlechterungJAXP.getNettolohnApr());
		einkommensverschlechterung.setNettolohnMai(einkommensverschlechterungJAXP.getNettolohnMai());
		einkommensverschlechterung.setNettolohnJun(einkommensverschlechterungJAXP.getNettolohnJun());
		einkommensverschlechterung.setNettolohnJul(einkommensverschlechterungJAXP.getNettolohnJul());
		einkommensverschlechterung.setNettolohnAug(einkommensverschlechterungJAXP.getNettolohnAug());
		einkommensverschlechterung.setNettolohnSep(einkommensverschlechterungJAXP.getNettolohnSep());
		einkommensverschlechterung.setNettolohnOkt(einkommensverschlechterungJAXP.getNettolohnOkt());
		einkommensverschlechterung.setNettolohnNov(einkommensverschlechterungJAXP.getNettolohnNov());
		einkommensverschlechterung.setNettolohnDez(einkommensverschlechterungJAXP.getNettolohnDez());
		einkommensverschlechterung.setNettolohnZus(einkommensverschlechterungJAXP.getNettolohnZus());
		einkommensverschlechterung.setGeschaeftsgewinnBasisjahrMinus1(einkommensverschlechterungJAXP.getGeschaeftsgewinnBasisjahrMinus1());
		return einkommensverschlechterung;
	}

	private JaxEinkommensverschlechterung einkommensverschlechterungToJAX(@Nullable final Einkommensverschlechterung persistedEinkommensverschlechterung) {

		if (persistedEinkommensverschlechterung != null) {
			JaxEinkommensverschlechterung jaxEinkommensverschlechterung = new JaxEinkommensverschlechterung();

			abstractFinanzielleSituationToJAX(persistedEinkommensverschlechterung, jaxEinkommensverschlechterung);

			jaxEinkommensverschlechterung.setNettolohnJan(persistedEinkommensverschlechterung.getNettolohnJan());
			jaxEinkommensverschlechterung.setNettolohnFeb(persistedEinkommensverschlechterung.getNettolohnFeb());
			jaxEinkommensverschlechterung.setNettolohnMrz(persistedEinkommensverschlechterung.getNettolohnMrz());
			jaxEinkommensverschlechterung.setNettolohnApr(persistedEinkommensverschlechterung.getNettolohnApr());
			jaxEinkommensverschlechterung.setNettolohnMai(persistedEinkommensverschlechterung.getNettolohnMai());
			jaxEinkommensverschlechterung.setNettolohnJun(persistedEinkommensverschlechterung.getNettolohnJun());
			jaxEinkommensverschlechterung.setNettolohnJul(persistedEinkommensverschlechterung.getNettolohnJul());
			jaxEinkommensverschlechterung.setNettolohnAug(persistedEinkommensverschlechterung.getNettolohnAug());
			jaxEinkommensverschlechterung.setNettolohnSep(persistedEinkommensverschlechterung.getNettolohnSep());
			jaxEinkommensverschlechterung.setNettolohnOkt(persistedEinkommensverschlechterung.getNettolohnOkt());
			jaxEinkommensverschlechterung.setNettolohnNov(persistedEinkommensverschlechterung.getNettolohnNov());
			jaxEinkommensverschlechterung.setNettolohnDez(persistedEinkommensverschlechterung.getNettolohnDez());
			jaxEinkommensverschlechterung.setNettolohnZus(persistedEinkommensverschlechterung.getNettolohnZus());
			jaxEinkommensverschlechterung.setGeschaeftsgewinnBasisjahrMinus1(persistedEinkommensverschlechterung.getGeschaeftsgewinnBasisjahrMinus1());

			return jaxEinkommensverschlechterung;
		}
		return null;
	}

	public ErwerbspensumContainer erwerbspensumContainerToStoreableEntity(@Nonnull final JaxErwerbspensumContainer jaxEwpCont) {
		Validate.notNull(jaxEwpCont);
		ErwerbspensumContainer containerToMergeWith = new ErwerbspensumContainer();
		if (jaxEwpCont.getId() != null) {
			final Optional<ErwerbspensumContainer> existingEwpCont = erwerbspensumService.findErwerbspensum(jaxEwpCont.getId());
			if (existingEwpCont.isPresent()) {
				containerToMergeWith = existingEwpCont.get();
			}
		}
		return erwerbspensumContainerToEntity(jaxEwpCont, containerToMergeWith);

	}

	public ErwerbspensumContainer erwerbspensumContainerToEntity(@Nonnull final JaxErwerbspensumContainer jaxEwpCont, @Nonnull final ErwerbspensumContainer
		erwerbspensumCont) {
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
	public JaxErwerbspensumContainer erwerbspensumContainerToJAX(@Nonnull final ErwerbspensumContainer storedErwerbspensumCont) {
		Validate.notNull(storedErwerbspensumCont);
		final JaxErwerbspensumContainer jaxEwpCont = new JaxErwerbspensumContainer();
		convertAbstractFieldsToJAX(storedErwerbspensumCont, jaxEwpCont);
		jaxEwpCont.setErwerbspensumGS(erbwerbspensumToJax(storedErwerbspensumCont.getErwerbspensumGS()));
		jaxEwpCont.setErwerbspensumJA(erbwerbspensumToJax(storedErwerbspensumCont.getErwerbspensumJA()));
		return jaxEwpCont;
	}

	private Erwerbspensum erbwerbspensumToEntity(@Nonnull final JaxErwerbspensum jaxErwerbspensum, @Nonnull final Erwerbspensum erwerbspensum) {
		Validate.notNull(jaxErwerbspensum);
		Validate.notNull(erwerbspensum);
		convertAbstractPensumFieldsToEntity(jaxErwerbspensum, erwerbspensum);
		erwerbspensum.setZuschlagZuErwerbspensum(jaxErwerbspensum.getZuschlagZuErwerbspensum());
		erwerbspensum.setZuschlagsgrund(jaxErwerbspensum.getZuschlagsgrund());
		erwerbspensum.setZuschlagsprozent(jaxErwerbspensum.getZuschlagsprozent());
		erwerbspensum.setTaetigkeit(jaxErwerbspensum.getTaetigkeit());
		erwerbspensum.setBezeichnung(jaxErwerbspensum.getBezeichnung());
		return erwerbspensum;
	}

	@Nullable
	private JaxErwerbspensum erbwerbspensumToJax(@Nullable final Erwerbspensum pensum) {
		if (pensum != null) {
			JaxErwerbspensum jaxErwerbspensum = new JaxErwerbspensum();
			convertAbstractPensumFieldsToJAX(pensum, jaxErwerbspensum);
			jaxErwerbspensum.setZuschlagZuErwerbspensum(pensum.getZuschlagZuErwerbspensum());
			jaxErwerbspensum.setZuschlagsgrund(pensum.getZuschlagsgrund());
			jaxErwerbspensum.setZuschlagsprozent(pensum.getZuschlagsprozent());
			jaxErwerbspensum.setTaetigkeit(pensum.getTaetigkeit());
			jaxErwerbspensum.setBezeichnung(pensum.getBezeichnung());
			return jaxErwerbspensum;
		}
		return null;
	}

	public Betreuung betreuungToEntity(@Nonnull final JaxBetreuung betreuungJAXP, @Nonnull final Betreuung betreuung) {
		Validate.notNull(betreuung);
		Validate.notNull(betreuungJAXP);
		convertAbstractFieldsToEntity(betreuungJAXP, betreuung);
		betreuung.setGrundAblehnung(betreuungJAXP.getGrundAblehnung());
		betreuung.setDatumAblehnung(betreuungJAXP.getDatumAblehnung());
		betreuung.setDatumBestaetigung(betreuungJAXP.getDatumBestaetigung());

		betreuungsPensumContainersToEntity(betreuungJAXP.getBetreuungspensumContainers(), betreuung.getBetreuungspensumContainers());
		setBetreuungInbetreuungsPensumContainers(betreuung.getBetreuungspensumContainers(), betreuung);

		abwesenheitContainersToEntity(betreuungJAXP.getAbwesenheitContainers(), betreuung.getAbwesenheitContainers());
		setBetreuungInAbwesenheiten(betreuung.getAbwesenheitContainers(), betreuung);

		betreuung.setBetreuungsstatus(betreuungJAXP.getBetreuungsstatus());
		betreuung.setVertrag(betreuungJAXP.getVertrag());
		betreuung.setErweiterteBeduerfnisse(betreuungJAXP.getErweiterteBeduerfnisse());

		// InstitutionStammdaten muessen bereits existieren
		if (betreuungJAXP.getInstitutionStammdaten() != null) {
			final String instStammdatenID = betreuungJAXP.getInstitutionStammdaten().getId();
			Objects.requireNonNull(instStammdatenID, "Die Institutionsstammdaten muessen gesetzt sein");
			final Optional<InstitutionStammdaten> optInstStammdaten = institutionStammdatenService.findInstitutionStammdaten(instStammdatenID);
			final InstitutionStammdaten instStammdatenToMerge =
				optInstStammdaten.orElseThrow(() -> new EbeguEntityNotFoundException("betreuungToEntity", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND,
					instStammdatenID));
			// InstitutionsStammdaten darf nicht vom Client ueberschrieben werden
			betreuung.setInstitutionStammdaten(instStammdatenToMerge);
		}
		betreuung.setBetreuungNummer(betreuungJAXP.getBetreuungNummer());
		betreuung.setBetreuungMutiert(betreuungJAXP.getBetreuungMutiert());
		betreuung.setAbwesenheitMutiert(betreuungJAXP.getAbwesenheitMutiert());
		betreuung.setAnmeldungMutationZustand(betreuungJAXP.getAnmeldungMutationZustand());
		betreuung.setKeineDetailinformationen(betreuungJAXP.isKeineDetailinformationen());
		if (betreuungJAXP.getBelegungTagesschule() != null) {
			Objects.requireNonNull(betreuung.getInstitutionStammdaten().getInstitutionStammdatenTagesschule(),
				"InstitutionsStammdatenTagesschule muessen gesetzt sein");
			if (betreuung.getBelegungTagesschule() != null) {
				betreuung.setBelegungTagesschule(belegungTagesschuleToEntity(betreuungJAXP.getBelegungTagesschule(), betreuung.getBelegungTagesschule(),
					betreuung.getInstitutionStammdaten().getInstitutionStammdatenTagesschule()));
			} else {
				betreuung.setBelegungTagesschule(belegungTagesschuleToEntity(betreuungJAXP.getBelegungTagesschule(), new BelegungTagesschule(),
					betreuung.getInstitutionStammdaten().getInstitutionStammdatenTagesschule()));
			}
		} else {
			betreuung.setBelegungTagesschule(belegungTagesschuleToEntity(betreuungJAXP.getBelegungTagesschule(), new BelegungTagesschule(), null));
		}
		if (betreuung.getBelegungFerieninsel() != null) {
			betreuung.setBelegungFerieninsel(belegungFerieninselToEntity(betreuungJAXP.getBelegungFerieninsel(), betreuung.getBelegungFerieninsel()));
		} else {
			betreuung.setBelegungFerieninsel(belegungFerieninselToEntity(betreuungJAXP.getBelegungFerieninsel(), new BelegungFerieninsel()));
		}

		//ACHTUNG: Verfuegung wird hier nicht synchronisiert aus sicherheitsgruenden
		return betreuung;
	}

	@Nullable
	private BelegungTagesschule belegungTagesschuleToEntity(@Nullable JaxBelegungTagesschule belegungTagesschuleJAXP,
		@NotNull BelegungTagesschule belegungTagesschule, @Nullable InstitutionStammdatenTagesschule instStammdatenTagesschule) {

		if (belegungTagesschuleJAXP != null && instStammdatenTagesschule != null) {
			convertAbstractFieldsToEntity(belegungTagesschuleJAXP, belegungTagesschule);

			final Set<ModulTagesschule> convertedModule = moduleTagesschuleListToEntity(belegungTagesschuleJAXP.getModuleTagesschule(),
				instStammdatenTagesschule.getModuleTagesschule(), instStammdatenTagesschule);
			if (convertedModule != null) {
				//change the existing collection to reflect changes
				// Already tested: All existing module of the list remain as they were, that means their data are updated
				// and the objects are not created again. ID and InsertTimeStamp are the same as before
				belegungTagesschule.getModuleTagesschule().clear();
				belegungTagesschule.getModuleTagesschule().addAll(convertedModule);
			}

			belegungTagesschule.setEintrittsdatum(belegungTagesschuleJAXP.getEintrittsdatum());
			return belegungTagesschule;
		}
		return null;
	}

	public Betreuung betreuungToStoreableEntity(@Nonnull final JaxBetreuung betreuungJAXP) {
		Validate.notNull(betreuungJAXP);
		Betreuung betreuungToMergeWith = new Betreuung();
		if (betreuungJAXP.getId() != null) {
			final Optional<Betreuung> optionalBetreuung = betreuungService.findBetreuung(betreuungJAXP.getId());
			betreuungToMergeWith = optionalBetreuung.orElse(new Betreuung());
		}
		return this.betreuungToEntity(betreuungJAXP, betreuungToMergeWith);
	}

	private void setBetreuungInbetreuungsPensumContainers(final Set<BetreuungspensumContainer> betreuungspensumContainers, final Betreuung betreuung) {
		for (final BetreuungspensumContainer betreuungspensumContainer : betreuungspensumContainers) {
			betreuungspensumContainer.setBetreuung(betreuung);
		}
	}

	private void setBetreuungInAbwesenheiten(final Set<AbwesenheitContainer> abwesenheiten, final Betreuung betreuung) {
		for (final AbwesenheitContainer abwesenheit : abwesenheiten) {
			abwesenheit.setBetreuung(betreuung);
		}
	}

	/**
	 * Goes through the whole list of jaxBetPenContainers. For each (jax)Container that already exists as Entity it merges both and adds the resulting
	 * (jax) container to the list. If the container doesn't exist it creates a new one and adds it to the list. Thus all containers that existed as entity
	 * but not in the list of jax, won't be added to the list and are then removed (cascade and orphanremoval)
	 *
	 * @param jaxBetPenContainers Betreuungspensen DTOs from Client
	 * @param existingBetreuungspensen List of currently stored BetreungspensumContainers
	 */
	private void betreuungsPensumContainersToEntity(final List<JaxBetreuungspensumContainer> jaxBetPenContainers,
		final Collection<BetreuungspensumContainer> existingBetreuungspensen) {
		final Set<BetreuungspensumContainer> transformedBetPenContainers = new TreeSet<>();
		for (final JaxBetreuungspensumContainer jaxBetPensContainer : jaxBetPenContainers) {
			final BetreuungspensumContainer containerToMergeWith = existingBetreuungspensen
				.stream()
				.filter(existingBetPenEntity -> existingBetPenEntity.getId().equals(jaxBetPensContainer.getId()))
				.reduce(StreamsUtil.toOnlyElement())
				.orElse(new BetreuungspensumContainer());
			final BetreuungspensumContainer contToAdd = betreuungspensumContainerToEntity(jaxBetPensContainer, containerToMergeWith);
			final boolean added = transformedBetPenContainers.add(contToAdd);
			if (!added) {
				LOGGER.warn(DROPPED_DUPLICATE_CONTAINER + "{}", contToAdd);
			}
		}

		//change the existing collection to reflect changes
		// Already tested: All existing Betreuungspensen of the list remain as they were, that means their data are updated
		// and the objects are not created again. ID and InsertTimeStamp are the same as before
		existingBetreuungspensen.clear();
		existingBetreuungspensen.addAll(transformedBetPenContainers);
	}

	private void abwesenheitContainersToEntity(final List<JaxAbwesenheitContainer> jaxAbwesenheitContainers,
		final Collection<AbwesenheitContainer> existingAbwesenheiten) {
		final Set<AbwesenheitContainer> transformedAbwesenheitContainers = new TreeSet<>();
		for (final JaxAbwesenheitContainer jaxAbwesenheitContainer : jaxAbwesenheitContainers) {
			final AbwesenheitContainer containerToMergeWith = existingAbwesenheiten
				.stream()
				.filter(existingAbwesenheitEntity -> existingAbwesenheitEntity.getId().equals(jaxAbwesenheitContainer.getId()))
				.reduce(StreamsUtil.toOnlyElement())
				.orElse(new AbwesenheitContainer());
			final String oldID = containerToMergeWith.getId();
			final AbwesenheitContainer contToAdd = abwesenheitContainerToEntity(jaxAbwesenheitContainer, containerToMergeWith);
			contToAdd.setId(oldID);
			final boolean added = transformedAbwesenheitContainers.add(contToAdd);
			if (!added) {
				LOGGER.warn(DROPPED_DUPLICATE_CONTAINER + "{}", contToAdd);
			}
		}

		//change the existing collection to reflect changes
		// Already tested: All existing Betreuungspensen of the list remain as they were, that means their data are updated
		// and the objects are not created again. ID and InsertTimeStamp are the same as before
		existingAbwesenheiten.clear();
		existingAbwesenheiten.addAll(transformedAbwesenheitContainers);
	}

	private Abwesenheit abwesenheitToEntity(final JaxAbwesenheit jaxAbwesenheit, final Abwesenheit abwesenheit) {
		convertAbstractDateRangedFieldsToEntity(jaxAbwesenheit, abwesenheit);
		return abwesenheit;
	}

	private BetreuungspensumContainer betreuungspensumContainerToEntity(final JaxBetreuungspensumContainer jaxBetPenContainers, final
	BetreuungspensumContainer bpContainer) {
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

	private AbwesenheitContainer abwesenheitContainerToEntity(final JaxAbwesenheitContainer jaxAbwesenheitContainers, final AbwesenheitContainer
		abwesenheitContainer) {
		Validate.notNull(jaxAbwesenheitContainers);
		Validate.notNull(abwesenheitContainer);
		convertAbstractFieldsToEntity(jaxAbwesenheitContainers, abwesenheitContainer);
		if (jaxAbwesenheitContainers.getAbwesenheitGS() != null) {
			Abwesenheit abwesenheitGS = new Abwesenheit();
			if (abwesenheitContainer.getAbwesenheitGS() != null) {
				abwesenheitGS = abwesenheitContainer.getAbwesenheitGS();
			}
			// Das Setzen von alten IDs ist noetigt im Fall dass Betreuungsangebot fuer eine existierende Abwesenheit geaendert wird, da sonst doppelte
			// Verknuepfungen gemacht werden
			final String oldID = abwesenheitGS.getId();
			final Abwesenheit convertedAbwesenheitGS = abwesenheitToEntity(jaxAbwesenheitContainers.getAbwesenheitGS(), abwesenheitGS);
			convertedAbwesenheitGS.setId(oldID);
			abwesenheitContainer.setAbwesenheitGS(convertedAbwesenheitGS);
		}
		if (jaxAbwesenheitContainers.getAbwesenheitJA() != null) {
			Abwesenheit abwesenheitJA = new Abwesenheit();
			if (abwesenheitContainer.getAbwesenheitJA() != null) {
				abwesenheitJA = abwesenheitContainer.getAbwesenheitJA();
			}
			//siehe Kommentar oben bei abwesenheitGS
			final String oldID = abwesenheitJA.getId();
			final Abwesenheit convertedAbwesenheitJA = abwesenheitToEntity(jaxAbwesenheitContainers.getAbwesenheitJA(), abwesenheitJA);
			convertedAbwesenheitJA.setId(oldID);
			abwesenheitContainer.setAbwesenheitJA(convertedAbwesenheitJA);
		}
		return abwesenheitContainer;
	}

	private Betreuungspensum betreuungspensumToEntity(final JaxBetreuungspensum jaxBetreuungspensum, final Betreuungspensum betreuungspensum) {
		convertAbstractPensumFieldsToEntity(jaxBetreuungspensum, betreuungspensum);
		betreuungspensum.setNichtEingetreten(jaxBetreuungspensum.getNichtEingetreten());
		return betreuungspensum;
	}

	private Set<JaxBetreuung> betreuungListToJax(final Set<Betreuung> betreuungen) {
		final Set<JaxBetreuung> jaxBetreuungen = new TreeSet<>();
		if (betreuungen != null) {
			jaxBetreuungen.addAll(betreuungen.stream().map(this::betreuungToJAX).collect(Collectors.toList()));
		}
		return jaxBetreuungen;
	}

	private BetreuungsmitteilungPensum betreuungsmitteilungpensumToEntity(final JaxBetreuungsmitteilungPensum jaxBetreuungspensum, final
	BetreuungsmitteilungPensum betreuungspensum) {
		convertAbstractPensumFieldsToEntity(jaxBetreuungspensum, betreuungspensum);
		return betreuungspensum;
	}

	private JaxBetreuungsmitteilungPensum betreuungsmitteilungPensumToJax(final BetreuungsmitteilungPensum betreuungspensum) {
		final JaxBetreuungsmitteilungPensum jaxBetreuungspensum = new JaxBetreuungsmitteilungPensum();
		convertAbstractPensumFieldsToJAX(betreuungspensum, jaxBetreuungspensum);
		return jaxBetreuungspensum;
	}

	/**
	 * converts the given betreuungList into a JaxBetreuungList
	 *
	 * @return List with Betreuung DTOs
	 */
	public Collection<JaxBetreuung> betreuungListToJax(Collection<Betreuung> betreuungList) {
		Collection<JaxBetreuung> returnList = new ArrayList<>();
		betreuungList.forEach(betreuung -> returnList.add(betreuungToJAX(betreuung)));
		return returnList;
	}

	public JaxBetreuung betreuungToJAX(final Betreuung betreuungFromServer) {
		final JaxBetreuung jaxBetreuung = new JaxBetreuung();
		convertAbstractFieldsToJAX(betreuungFromServer, jaxBetreuung);
		jaxBetreuung.setGrundAblehnung(betreuungFromServer.getGrundAblehnung());
		jaxBetreuung.setDatumAblehnung(betreuungFromServer.getDatumAblehnung());
		jaxBetreuung.setDatumBestaetigung(betreuungFromServer.getDatumBestaetigung());
		jaxBetreuung.setBetreuungspensumContainers(betreuungsPensumContainersToJax(betreuungFromServer.getBetreuungspensumContainers()));
		jaxBetreuung.setAbwesenheitContainers(abwesenheitContainersToJax(betreuungFromServer.getAbwesenheitContainers()));
		jaxBetreuung.setBetreuungsstatus(betreuungFromServer.getBetreuungsstatus());
		jaxBetreuung.setVertrag(betreuungFromServer.getVertrag());
		jaxBetreuung.setErweiterteBeduerfnisse(betreuungFromServer.getErweiterteBeduerfnisse());
		jaxBetreuung.setInstitutionStammdaten(institutionStammdatenToJAX(betreuungFromServer.getInstitutionStammdaten()));
		jaxBetreuung.setBetreuungNummer(betreuungFromServer.getBetreuungNummer());
		if (betreuungFromServer.getKind() != null) {
			jaxBetreuung.setKindFullname(betreuungFromServer.getKind().getKindJA().getFullName());
			jaxBetreuung.setKindNummer(betreuungFromServer.getKind().getKindNummer());
			if (betreuungFromServer.getKind().getGesuch() != null) {
				jaxBetreuung.setGesuchId(betreuungFromServer.getKind().getGesuch().getId());
				jaxBetreuung.setGesuchsperiode(gesuchsperiodeToJAX(betreuungFromServer.getKind().getGesuch().getGesuchsperiode()));
			}
		}
		if (betreuungFromServer.getVerfuegung() != null) {
			jaxBetreuung.setVerfuegung(verfuegungToJax(betreuungFromServer.getVerfuegung()));
		}
		jaxBetreuung.setBetreuungMutiert(betreuungFromServer.getBetreuungMutiert());
		jaxBetreuung.setAbwesenheitMutiert(betreuungFromServer.getAbwesenheitMutiert());
		jaxBetreuung.setGueltig(betreuungFromServer.isGueltig());
		jaxBetreuung.setAnmeldungMutationZustand(betreuungFromServer.getAnmeldungMutationZustand());
		jaxBetreuung.setKeineDetailinformationen(betreuungFromServer.isKeineDetailinformationen());
		jaxBetreuung.setBelegungTagesschule(belegungTagesschuleToJax(betreuungFromServer.getBelegungTagesschule()));
		jaxBetreuung.setBelegungFerieninsel(belegungFerieninselToJAX(betreuungFromServer.getBelegungFerieninsel()));
		jaxBetreuung.setBgNummer(betreuungFromServer.getBGNummer());
		return jaxBetreuung;
	}

	@Nullable
	private JaxBelegungTagesschule belegungTagesschuleToJax(@Nullable BelegungTagesschule belegungFromServer) {
		if (belegungFromServer != null) {
			final JaxBelegungTagesschule jaxBelegungTagesschule = new JaxBelegungTagesschule();
			convertAbstractFieldsToJAX(belegungFromServer, jaxBelegungTagesschule);
			jaxBelegungTagesschule.setModuleTagesschule(moduleTagesschuleListToJax(belegungFromServer.getModuleTagesschule()));
			jaxBelegungTagesschule.setEintrittsdatum(belegungFromServer.getEintrittsdatum());
			return jaxBelegungTagesschule;
		}
		return null;
	}

	private List<JaxModulTagesschule> moduleTagesschuleListToJax(@Nullable final Set<ModulTagesschule> module) {
		final List<JaxModulTagesschule> jaxModulTagesschule = new ArrayList<>();
		if (module != null) {
			jaxModulTagesschule.addAll(module.stream().map(this::modulTagesschuleToJAX).collect(Collectors.toList()));
		}
		return jaxModulTagesschule;
	}

	@Nullable
	public JaxModulTagesschule modulTagesschuleToJAX(@Nullable ModulTagesschule modulTagesschule) {
		if (modulTagesschule != null) {
			final JaxModulTagesschule jaxModulTagesschule = new JaxModulTagesschule();
			convertAbstractFieldsToJAX(modulTagesschule, jaxModulTagesschule);
			jaxModulTagesschule.setModulTagesschuleName(modulTagesschule.getModulTagesschuleName());
			jaxModulTagesschule.setWochentag(modulTagesschule.getWochentag());
			jaxModulTagesschule.setZeitVon(LocalDateTime.of(LocalDate.now(), modulTagesschule.getZeitVon()));
			jaxModulTagesschule.setZeitBis(LocalDateTime.of(LocalDate.now(), modulTagesschule.getZeitBis()));
			return jaxModulTagesschule;
		}
		return null;
	}

	/**
	 * converts the given verfuegung into a JaxVerfuegung
	 *
	 * @return dto with the values of the verfuegung
	 */
	@Nullable
	public JaxVerfuegung verfuegungToJax(Verfuegung verfuegung) {
		if (verfuegung != null) {
			final JaxVerfuegung jaxVerfuegung = new JaxVerfuegung();
			convertAbstractFieldsToJAX(verfuegung, jaxVerfuegung);
			jaxVerfuegung.setGeneratedBemerkungen(verfuegung.getGeneratedBemerkungen());
			jaxVerfuegung.setManuelleBemerkungen(verfuegung.getManuelleBemerkungen());
			jaxVerfuegung.setKategorieZuschlagZumErwerbspensum(verfuegung.isKategorieZuschlagZumErwerbspensum());
			jaxVerfuegung.setKategorieKeinPensum(verfuegung.isKategorieKeinPensum());
			jaxVerfuegung.setKategorieMaxEinkommen(verfuegung.isKategorieMaxEinkommen());
			jaxVerfuegung.setKategorieNichtEintreten(verfuegung.isKategorieNichtEintreten());
			jaxVerfuegung.setKategorieNormal(verfuegung.isKategorieNormal());

			jaxVerfuegung.getZeitabschnitte().addAll(
				verfuegung.getZeitabschnitte()
					.stream()
					.map(this::verfuegungZeitabschnittToJax)
					.collect(Collectors.toList()));

			return jaxVerfuegung;
		}
		return null;
	}

	/**
	 * converts the given verfuegung into a JaxVerfuegung
	 *
	 * @return dto with the values of the verfuegung
	 */
	public Verfuegung verfuegungToEntity(final JaxVerfuegung jaxVerfuegung, final Verfuegung verfuegung) {
		Validate.notNull(jaxVerfuegung);
		Validate.notNull(verfuegung);
		convertAbstractFieldsToEntity(jaxVerfuegung, verfuegung);
		verfuegung.setGeneratedBemerkungen(jaxVerfuegung.getGeneratedBemerkungen());
		verfuegung.setManuelleBemerkungen(jaxVerfuegung.getManuelleBemerkungen());
		verfuegung.setKategorieZuschlagZumErwerbspensum(jaxVerfuegung.isKategorieZuschlagZumErwerbspensum());
		verfuegung.setKategorieKeinPensum(jaxVerfuegung.isKategorieKeinPensum());
		verfuegung.setKategorieMaxEinkommen(jaxVerfuegung.isKategorieMaxEinkommen());
		verfuegung.setKategorieNichtEintreten(jaxVerfuegung.isKategorieNichtEintreten());
		verfuegung.setKategorieNormal(jaxVerfuegung.isKategorieNormal());

		//List of Verfuegungszeitabschnitte converten
		verfuegungZeitabschnitteToEntity(verfuegung.getZeitabschnitte(), jaxVerfuegung.getZeitabschnitte());
		return verfuegung;

	}

	private void verfuegungZeitabschnitteToEntity(List<VerfuegungZeitabschnitt> existingZeitabschnitte,
		List<JaxVerfuegungZeitabschnitt> zeitabschnitteFromClient) {
		final Set<VerfuegungZeitabschnitt> convertedZeitabschnitte = new TreeSet<>();
		for (final JaxVerfuegungZeitabschnitt jaxZeitabschnitt : zeitabschnitteFromClient) {
			final VerfuegungZeitabschnitt containerToMergeWith = existingZeitabschnitte
				.stream()
				.filter(existingBetPensumEntity -> existingBetPensumEntity.getId().equals(jaxZeitabschnitt.getId()))
				.reduce(StreamsUtil.toOnlyElement())
				.orElse(new VerfuegungZeitabschnitt());
			final VerfuegungZeitabschnitt abschnittToAdd = verfuegungZeitabschnittToEntity(jaxZeitabschnitt, containerToMergeWith);
			final boolean added = convertedZeitabschnitte.add(abschnittToAdd);
			if (!added) {
				LOGGER.warn("dropped duplicate zeitabschnitt {}", abschnittToAdd);
			}
		}

		//change the existing collection to reflect changes
		existingZeitabschnitte.clear();
		existingZeitabschnitte.addAll(convertedZeitabschnitte);

	}

	@Nullable
	private JaxVerfuegungZeitabschnitt verfuegungZeitabschnittToJax(VerfuegungZeitabschnitt zeitabschnitt) {
		if (zeitabschnitt != null) {
			final JaxVerfuegungZeitabschnitt jaxZeitabschn = new JaxVerfuegungZeitabschnitt();
			convertAbstractDateRangedFieldsToJAX(zeitabschnitt, jaxZeitabschn);
			jaxZeitabschn.setAbzugFamGroesse(zeitabschnitt.getAbzugFamGroesse());
			jaxZeitabschn.setErwerbspensumGS1(zeitabschnitt.getErwerbspensumGS1());
			jaxZeitabschn.setErwerbspensumGS2(zeitabschnitt.getErwerbspensumGS2());
			jaxZeitabschn.setBetreuungspensum(zeitabschnitt.getBetreuungspensum());
			jaxZeitabschn.setFachstellenpensum(zeitabschnitt.getFachstellenpensum());
			jaxZeitabschn.setAnspruchspensumRest(zeitabschnitt.getAnspruchspensumRest());
			jaxZeitabschn.setBgPensum(zeitabschnitt.getBgPensum());
			jaxZeitabschn.setAnspruchberechtigtesPensum(zeitabschnitt.getAnspruchberechtigtesPensum());
			jaxZeitabschn.setBetreuungsstunden(zeitabschnitt.getBetreuungsstunden());
			jaxZeitabschn.setVollkosten(zeitabschnitt.getVollkosten());
			jaxZeitabschn.setElternbeitrag(zeitabschnitt.getElternbeitrag());
			jaxZeitabschn.setMassgebendesEinkommenVorAbzugFamgr(zeitabschnitt.getMassgebendesEinkommenVorAbzFamgr());
			jaxZeitabschn.setBemerkungen(zeitabschnitt.getBemerkungen());
			jaxZeitabschn.setFamGroesse(zeitabschnitt.getFamGroesse());
			jaxZeitabschn.setEinkommensjahr(zeitabschnitt.getEinkommensjahr());
			jaxZeitabschn.setKategorieKeinPensum(zeitabschnitt.isKategorieKeinPensum());
			jaxZeitabschn.setKategorieMaxEinkommen(zeitabschnitt.isKategorieMaxEinkommen());
			jaxZeitabschn.setKategorieZuschlagZumErwerbspensum(zeitabschnitt.isKategorieZuschlagZumErwerbspensum());
			jaxZeitabschn.setZuSpaetEingereicht(zeitabschnitt.isZuSpaetEingereicht());
			jaxZeitabschn.setZahlungsstatus(zeitabschnitt.getZahlungsstatus());
			jaxZeitabschn.setSameVerfuegungsdaten(zeitabschnitt.isSameVerfuegungsdaten());
			jaxZeitabschn.setSameVerguenstigung(zeitabschnitt.isSameVerguenstigung());
			return jaxZeitabschn;
		}
		return null;
	}

	private VerfuegungZeitabschnitt verfuegungZeitabschnittToEntity(final JaxVerfuegungZeitabschnitt jaxVerfuegungZeitabschnitt,
		final VerfuegungZeitabschnitt verfuegungZeitabschnitt) {
		Validate.notNull(jaxVerfuegungZeitabschnitt);
		Validate.notNull(verfuegungZeitabschnitt);
		convertAbstractDateRangedFieldsToEntity(jaxVerfuegungZeitabschnitt, verfuegungZeitabschnitt);
		verfuegungZeitabschnitt.setErwerbspensumGS1(jaxVerfuegungZeitabschnitt.getErwerbspensumGS1());
		verfuegungZeitabschnitt.setErwerbspensumGS2(jaxVerfuegungZeitabschnitt.getErwerbspensumGS2());
		verfuegungZeitabschnitt.setBetreuungspensum(jaxVerfuegungZeitabschnitt.getBetreuungspensum());
		verfuegungZeitabschnitt.setFachstellenpensum(jaxVerfuegungZeitabschnitt.getFachstellenpensum());
		verfuegungZeitabschnitt.setAnspruchspensumRest(jaxVerfuegungZeitabschnitt.getAnspruchspensumRest());
		verfuegungZeitabschnitt.setAnspruchberechtigtesPensum(jaxVerfuegungZeitabschnitt.getAnspruchberechtigtesPensum());
		verfuegungZeitabschnitt.setBetreuungsstunden(jaxVerfuegungZeitabschnitt.getBetreuungsstunden());
		verfuegungZeitabschnitt.setVollkosten(jaxVerfuegungZeitabschnitt.getVollkosten());
		verfuegungZeitabschnitt.setElternbeitrag(jaxVerfuegungZeitabschnitt.getElternbeitrag());
		verfuegungZeitabschnitt.setAbzugFamGroesse(jaxVerfuegungZeitabschnitt.getAbzugFamGroesse());
		verfuegungZeitabschnitt.setMassgebendesEinkommenVorAbzugFamgr(jaxVerfuegungZeitabschnitt.getMassgebendesEinkommenVorAbzugFamgr());
		verfuegungZeitabschnitt.setBemerkungen(jaxVerfuegungZeitabschnitt.getBemerkungen());
		verfuegungZeitabschnitt.setFamGroesse(jaxVerfuegungZeitabschnitt.getFamGroesse());
		verfuegungZeitabschnitt.setEinkommensjahr(jaxVerfuegungZeitabschnitt.getEinkommensjahr());
		verfuegungZeitabschnitt.setKategorieMaxEinkommen(jaxVerfuegungZeitabschnitt.isKategorieMaxEinkommen());
		verfuegungZeitabschnitt.setKategorieKeinPensum(jaxVerfuegungZeitabschnitt.isKategorieKeinPensum());
		verfuegungZeitabschnitt.setKategorieZuschlagZumErwerbspensum(jaxVerfuegungZeitabschnitt.isKategorieZuschlagZumErwerbspensum());
		verfuegungZeitabschnitt.setZuSpaetEingereicht(jaxVerfuegungZeitabschnitt.isZuSpaetEingereicht());
		verfuegungZeitabschnitt.setZahlungsstatus(jaxVerfuegungZeitabschnitt.getZahlungsstatus());
		verfuegungZeitabschnitt.setSameVerfuegungsdaten(jaxVerfuegungZeitabschnitt.isSameVerfuegungsdaten());
		verfuegungZeitabschnitt.setSameVerguenstigung(jaxVerfuegungZeitabschnitt.isSameVerguenstigung());
		return verfuegungZeitabschnitt;
	}

	/**
	 * calls betreuungsPensumContainerToJax for each betreuungspensumContainer found in given the list
	 */
	private List<JaxBetreuungspensumContainer> betreuungsPensumContainersToJax(final Set<BetreuungspensumContainer> betreuungspensumContainers) {
		final List<JaxBetreuungspensumContainer> jaxContainers = new ArrayList<>();
		if (betreuungspensumContainers != null) {
			for (final BetreuungspensumContainer betreuungspensumContainer : betreuungspensumContainers) {
				jaxContainers.add(betreuungsPensumContainerToJax(betreuungspensumContainer));
			}
		}
		return jaxContainers;
	}

	private List<JaxAbwesenheitContainer> abwesenheitContainersToJax(final Set<AbwesenheitContainer> abwesenheiten) {
		final List<JaxAbwesenheitContainer> jaxContainers = new ArrayList<>();
		if (abwesenheiten != null) {
			for (final AbwesenheitContainer abwesenheitContainer : abwesenheiten) {
				jaxContainers.add(abwesenheitContainerToJax(abwesenheitContainer));
			}
		}
		return jaxContainers;
	}

	@Nullable
	private JaxAbwesenheit abwesenheitToJax(final Abwesenheit abwesenheit) {
		if (abwesenheit != null) {
			final JaxAbwesenheit jaxAbwesenheit = new JaxAbwesenheit();
			convertAbstractDateRangedFieldsToJAX(abwesenheit, jaxAbwesenheit);
			return jaxAbwesenheit;
		}
		return null;
	}

	@Nullable
	private JaxAbwesenheitContainer abwesenheitContainerToJax(final AbwesenheitContainer abwesenheitContainer) {
		if (abwesenheitContainer != null) {
			final JaxAbwesenheitContainer jaxAbwesenheitContainer = new JaxAbwesenheitContainer();
			convertAbstractFieldsToJAX(abwesenheitContainer, jaxAbwesenheitContainer);
			if (abwesenheitContainer.getAbwesenheitGS() != null) {
				jaxAbwesenheitContainer.setAbwesenheitGS(abwesenheitToJax(abwesenheitContainer.getAbwesenheitGS()));
			}
			if (abwesenheitContainer.getAbwesenheitJA() != null) {
				jaxAbwesenheitContainer.setAbwesenheitJA(abwesenheitToJax(abwesenheitContainer.getAbwesenheitJA()));
			}
			return jaxAbwesenheitContainer;
		}
		return null;
	}

	@Nullable
	private JaxBetreuungspensumContainer betreuungsPensumContainerToJax(final BetreuungspensumContainer betreuungspensumContainer) {
		if (betreuungspensumContainer != null) {
			final JaxBetreuungspensumContainer jaxBetreuungspensumContainer = new JaxBetreuungspensumContainer();
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

	private JaxBetreuungspensum betreuungspensumToJax(final Betreuungspensum betreuungspensum) {
		final JaxBetreuungspensum jaxBetreuungspensum = new JaxBetreuungspensum();
		convertAbstractPensumFieldsToJAX(betreuungspensum, jaxBetreuungspensum);
		jaxBetreuungspensum.setNichtEingetreten(betreuungspensum.getNichtEingetreten());
		return jaxBetreuungspensum;
	}

	public JaxGesuchsperiode gesuchsperiodeToJAX(final Gesuchsperiode persistedGesuchsperiode) {
		final JaxGesuchsperiode jaxGesuchsperiode = new JaxGesuchsperiode();
		convertAbstractDateRangedFieldsToJAX(persistedGesuchsperiode, jaxGesuchsperiode);
		jaxGesuchsperiode.setStatus(persistedGesuchsperiode.getStatus());
		jaxGesuchsperiode.setDatumFreischaltungTagesschule(persistedGesuchsperiode.getDatumFreischaltungTagesschule());
		jaxGesuchsperiode.setDatumErsterSchultag(persistedGesuchsperiode.getDatumErsterSchultag());
		return jaxGesuchsperiode;
	}

	public Gesuchsperiode gesuchsperiodeToEntity(final JaxGesuchsperiode jaxGesuchsperiode, final Gesuchsperiode gesuchsperiode) {
		convertAbstractDateRangedFieldsToEntity(jaxGesuchsperiode, gesuchsperiode);
		gesuchsperiode.setStatus(jaxGesuchsperiode.getStatus());
		gesuchsperiode.setDatumFreischaltungTagesschule(jaxGesuchsperiode.getDatumFreischaltungTagesschule());
		gesuchsperiode.setDatumErsterSchultag(jaxGesuchsperiode.getDatumErsterSchultag());
		return gesuchsperiode;
	}

	public Benutzer authLoginElementToBenutzer(JaxAuthLoginElement jaxLoginElement, Benutzer benutzer) {
		benutzer.setUsername(jaxLoginElement.getUsername());
		benutzer.setExternalUUID(
			Strings.isNullOrEmpty(jaxLoginElement.getExternalUUID()) ? null : jaxLoginElement.getExternalUUID()
		);
		benutzer.setEmail(jaxLoginElement.getEmail());
		benutzer.setNachname(jaxLoginElement.getNachname());
		benutzer.setVorname(jaxLoginElement.getVorname());
		benutzer.setGesperrt(jaxLoginElement.isGesperrt());
		if (jaxLoginElement.getMandant() != null && jaxLoginElement.getMandant().getId() != null) {
			final Optional<Mandant> mandantFromDB = mandantService.findMandant(jaxLoginElement.getMandant().getId());
			if (mandantFromDB.isPresent()) {
				// Mandant darf nicht vom Client ueberschrieben werden
				benutzer.setMandant(mandantFromDB.get());
			} else {
				throw new EbeguEntityNotFoundException("authLoginElementToBenutzer -> mandant", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND,
					benutzer.getMandant().getId());
			}
		} else {
			throw new EbeguEntityNotFoundException("authLoginElementToBenutzer -> mandant", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND);
		}
		// Berechtigungen
		final Set<Berechtigung> convertedBerechtigungen = berechtigungenListToEntity(jaxLoginElement.getBerechtigungen(), benutzer.getBerechtigungen(),
			benutzer);
		//change the existing collection to reflect changes
		// Already tested: All existing module of the list remain as they were, that means their data are updated
		// and the objects are not created again. ID and InsertTimeStamp are the same as before
		benutzer.getBerechtigungen().clear();
		benutzer.getBerechtigungen().addAll(convertedBerechtigungen);
		return benutzer;
	}

	private Set<Berechtigung> berechtigungenListToEntity(@Nonnull Set<JaxBerechtigung> jaxBerechtigungenList,
			@Nonnull Set<Berechtigung> berechtigungenList, @Nonnull Benutzer benutzer) {

		final Set<Berechtigung> convertedBerechtigungen = new TreeSet<>();
		for (final JaxBerechtigung jaxBerechtigung : jaxBerechtigungenList) {
			final Berechtigung berechtigungToMergeWith = berechtigungenList
				.stream()
				.filter(existingBerechtigung -> existingBerechtigung.getId().equals(jaxBerechtigung.getId()))
				.reduce(StreamsUtil.toOnlyElement())
				.orElse(new Berechtigung());
			final Berechtigung berechtigungToAdd = berechtigungToEntity(jaxBerechtigung, berechtigungToMergeWith);
			berechtigungToAdd.setBenutzer(benutzer);
			final boolean added = convertedBerechtigungen.add(berechtigungToAdd);
			if (!added) {
				LOGGER.warn("dropped duplicate berechtigung {}", berechtigungToAdd);
			}
		}
		return convertedBerechtigungen;
	}

	public JaxAuthLoginElement benutzerToAuthLoginElement(Benutzer benutzer) {
		JaxAuthLoginElement jaxLoginElement = new JaxAuthLoginElement();
		jaxLoginElement.setVorname(benutzer.getVorname());
		jaxLoginElement.setNachname(benutzer.getNachname());
		jaxLoginElement.setEmail(benutzer.getEmail());
		if (benutzer.getMandant() != null) {
			jaxLoginElement.setMandant(mandantToJAX(benutzer.getMandant()));
		}
		jaxLoginElement.setUsername(benutzer.getUsername());
		jaxLoginElement.setExternalUUID(benutzer.getExternalUUID());
		jaxLoginElement.setGesperrt(benutzer.getGesperrt());
		jaxLoginElement.setCurrentBerechtigung(berechtigungToJax(benutzer.getCurrentBerechtigung()));
		// Berechtigungen
		Set<JaxBerechtigung> jaxBerechtigungen = new TreeSet<>();
		if (benutzer.getBerechtigungen() != null) {
			jaxBerechtigungen = benutzer.getBerechtigungen().stream()
				.map(this::berechtigungToJax)
				.sorted()
				.collect(Collectors.toCollection(TreeSet::new));
		}
		jaxLoginElement.setBerechtigungen(jaxBerechtigungen);
		return jaxLoginElement;
	}

	public Berechtigung berechtigungToEntity(JaxBerechtigung jaxBerechtigung, Berechtigung berechtigung) {
		convertAbstractDateRangedFieldsToEntity(jaxBerechtigung, berechtigung);
		berechtigung.setRole(jaxBerechtigung.getRole());

		// wir muessen Traegerschaft und Institution auch updaten wenn sie null sind. Es koennte auch so aus dem IAM kommen
		if (jaxBerechtigung.getInstitution() != null && jaxBerechtigung.getInstitution().getId() != null) {
			final Optional<Institution> institutionFromDB = institutionService.findInstitution(jaxBerechtigung.getInstitution().getId());
			if (institutionFromDB.isPresent()) {
				// Institution darf nicht vom Client ueberschrieben werden
				berechtigung.setInstitution(institutionFromDB.get());
			} else {
				throw new EbeguEntityNotFoundException("berechtigungToEntity", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, jaxBerechtigung
					.getInstitution().getId());
			}
		} else {
			berechtigung.setInstitution(null);
		}

		if (jaxBerechtigung.getTraegerschaft() != null && jaxBerechtigung.getTraegerschaft().getId() != null) {
			final Optional<Traegerschaft> traegerschaftFromDB = traegerschaftService.findTraegerschaft(jaxBerechtigung.getTraegerschaft().getId());
			if (traegerschaftFromDB.isPresent()) {
				// Traegerschaft darf nicht vom Client ueberschrieben werden
				berechtigung.setTraegerschaft(traegerschaftFromDB.get());
			} else {
				throw new EbeguEntityNotFoundException("berechtigungToEntity -> traegerschaft", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, jaxBerechtigung
					.getTraegerschaft().getId());
			}
		} else {
			berechtigung.setTraegerschaft(null);
		}
		return berechtigung;
	}

	public JaxBerechtigung berechtigungToJax(Berechtigung berechtigung) {
		JaxBerechtigung jaxBerechtigung = new JaxBerechtigung();
		convertAbstractDateRangedFieldsToJAX(berechtigung, jaxBerechtigung);
		jaxBerechtigung.setRole(berechtigung.getRole());
		if (berechtigung.getInstitution() != null) {
			jaxBerechtigung.setInstitution(institutionToJAX(berechtigung.getInstitution()));
		}
		if (berechtigung.getTraegerschaft() != null) {
			jaxBerechtigung.setTraegerschaft(traegerschaftToJAX(berechtigung.getTraegerschaft()));
		}
		return jaxBerechtigung;
	}

	public JaxBerechtigungHistory berechtigungHistoryToJax(BerechtigungHistory history) {
		JaxBerechtigungHistory jaxHistory = new JaxBerechtigungHistory();
		convertAbstractDateRangedFieldsToJAX(history, jaxHistory);
		Validate.notNull(history.getUserErstellt());
		jaxHistory.setUserErstellt(history.getUserErstellt());
		jaxHistory.setUsername(history.getUsername());
		jaxHistory.setRole(history.getRole());
		if (history.getInstitution() != null) {
			jaxHistory.setInstitution(institutionToJAX(history.getInstitution()));
		}
		if (history.getTraegerschaft() != null) {
			jaxHistory.setTraegerschaft(traegerschaftToJAX(history.getTraegerschaft()));
		}
		jaxHistory.setGesperrt(history.getGesperrt());
		jaxHistory.setGeloescht(history.getGeloescht());
		return jaxHistory;
	}

	public JaxDokumente dokumentGruendeToJAX(Set<DokumentGrund> dokumentGrunds) {
		JaxDokumente jaxDokumente = new JaxDokumente();

		for (DokumentGrund dokumentGrund : dokumentGrunds) {
			jaxDokumente.getDokumentGruende().add(dokumentGrundToJax(dokumentGrund));
		}

		return jaxDokumente;

	}

	public JaxDokumentGrund dokumentGrundToJax(DokumentGrund dokumentGrund) {
		JaxDokumentGrund jaxDokumentGrund = convertAbstractFieldsToJAX(dokumentGrund, new JaxDokumentGrund());
		jaxDokumentGrund.setDokumentGrundTyp(dokumentGrund.getDokumentGrundTyp());
		jaxDokumentGrund.setFullName(dokumentGrund.getFullName());
		jaxDokumentGrund.setTag(dokumentGrund.getTag());
		jaxDokumentGrund.setPersonType(dokumentGrund.getPersonType());
		jaxDokumentGrund.setPersonNumber(dokumentGrund.getPersonNumber());
		jaxDokumentGrund.setDokumentTyp(dokumentGrund.getDokumentTyp());
		jaxDokumentGrund.setNeeded(dokumentGrund.isNeeded());
		if (dokumentGrund.getDokumente() != null) {
			if (jaxDokumentGrund.getDokumente() == null) {
				jaxDokumentGrund.setDokumente(new HashSet<>());
			}
			for (Dokument dokument : dokumentGrund.getDokumente()) {

				jaxDokumentGrund.getDokumente().add(dokumentToJax(dokument));
			}
		}
		return jaxDokumentGrund;
	}

	private JaxDokument dokumentToJax(Dokument dokument) {
		JaxDokument jaxDokument = convertAbstractFieldsToJAX(dokument, new JaxDokument());
		convertFileToJax(dokument, jaxDokument);
		jaxDokument.setTimestampUpload(dokument.getTimestampUpload());
		if (StringUtils.isNotEmpty(dokument.getUserErstellt())) {
			Optional<Benutzer> userUploadedOptional = benutzerService.findBenutzer(dokument.getUserErstellt());
			if (userUploadedOptional.isPresent()) {
				JaxAuthLoginElement jaxAuthLoginElement = benutzerToAuthLoginElement(userUploadedOptional.get());
				jaxDokument.setUserUploaded(jaxAuthLoginElement);
			}
		}
		return jaxDokument;
	}

	public DokumentGrund dokumentGrundToEntity(@Nonnull final JaxDokumentGrund dokumentGrundJAXP, @Nonnull final DokumentGrund dokumentGrund) {
		Validate.notNull(dokumentGrund);
		Validate.notNull(dokumentGrundJAXP);
		convertAbstractFieldsToEntity(dokumentGrundJAXP, dokumentGrund);

		dokumentGrund.setDokumentGrundTyp(dokumentGrundJAXP.getDokumentGrundTyp());
		dokumentGrund.setFullName(dokumentGrundJAXP.getFullName());
		dokumentGrund.setTag(dokumentGrundJAXP.getTag());
		dokumentGrund.setPersonType(dokumentGrundJAXP.getPersonType());
		dokumentGrund.setPersonNumber(dokumentGrundJAXP.getPersonNumber());
		dokumentGrund.setDokumentTyp(dokumentGrundJAXP.getDokumentTyp());
		dokumentGrund.setNeeded(dokumentGrundJAXP.isNeeded());

		dokumenteToEntity(dokumentGrundJAXP.getDokumente(), dokumentGrund.getDokumente(), dokumentGrund);
		return dokumentGrund;
	}

	/**
	 * Goes through the whole list of jaxDokuments. For each (jax)dokument that already exists as Entity it merges both and adds the resulting
	 * (jax) dokument to the list. If the dokument doesn't exist it creates a new one and adds it to the list. Thus all dokumente that existed as entity
	 * but not in the list of jax, won't be added to the list and then removed (cascade and orphanremoval)
	 *
	 * @param jaxDokuments Dokumente DTOs from Client
	 * @param existingDokumente List of currently stored Dokumente
	 */
	private void dokumenteToEntity(final Set<JaxDokument> jaxDokuments,
		final Collection<Dokument> existingDokumente, final DokumentGrund dokumentGrund) {
		final Set<Dokument> transformedDokumente = new HashSet<>();
		for (final JaxDokument jaxDokument : jaxDokuments) {
			final Dokument dokumenteToMergeWith = existingDokumente
				.stream()
				.filter(existingDokumentEntity -> existingDokumentEntity.getId().equals(jaxDokument.getId()))
				.reduce(StreamsUtil.toOnlyElement())
				.orElse(new Dokument());
			final Dokument dokToAdd = dokumentToEntity(jaxDokument, dokumenteToMergeWith, dokumentGrund);
			final boolean added = transformedDokumente.add(dokToAdd);
			if (!added) {
				LOGGER.warn(DROPPED_DUPLICATE_CONTAINER + "{}", dokToAdd);
			}
		}

		//change the existing collection to reflect changes
		// Already tested: All existing Dokumente of the list remain as they were, that means their data are updated
		// and the objects are not created again. ID and InsertTimeStamp are the same as before
		existingDokumente.clear();
		existingDokumente.addAll(transformedDokumente);
	}

	private Dokument dokumentToEntity(JaxDokument jaxDokument, Dokument dokument, DokumentGrund dokumentGrund) {
		Validate.notNull(dokument);
		Validate.notNull(jaxDokument);
		Validate.notNull(dokumentGrund);
		convertAbstractFieldsToEntity(jaxDokument, dokument);

		dokument.setDokumentGrund(dokumentGrund);
		dokument.setTimestampUpload(jaxDokument.getTimestampUpload());
		convertFileToEnity(jaxDokument, dokument);
		return dokument;
	}

	public JaxDownloadFile downloadFileToJAX(DownloadFile downloadFile) {
		JaxDownloadFile jaxDownloadFile = new JaxDownloadFile();
		convertFileToJax(downloadFile, jaxDownloadFile);
		jaxDownloadFile.setAccessToken(downloadFile.getAccessToken());
		return jaxDownloadFile;
	}

	public JaxWizardStep wizardStepToJAX(WizardStep wizardStep) {
		final JaxWizardStep jaxWizardStep = convertAbstractFieldsToJAX(wizardStep, new JaxWizardStep());
		jaxWizardStep.setGesuchId(wizardStep.getGesuch().getId());
		jaxWizardStep.setVerfuegbar(wizardStep.getVerfuegbar());
		jaxWizardStep.setWizardStepName(wizardStep.getWizardStepName());
		jaxWizardStep.setWizardStepStatus(wizardStep.getWizardStepStatus());
		jaxWizardStep.setBemerkungen(wizardStep.getBemerkungen());
		return jaxWizardStep;
	}

	public WizardStep wizardStepToEntity(final JaxWizardStep jaxWizardStep, final WizardStep wizardStep) {
		convertAbstractFieldsToEntity(jaxWizardStep, wizardStep);
		wizardStep.setVerfuegbar(jaxWizardStep.isVerfuegbar());
		wizardStep.setWizardStepName(jaxWizardStep.getWizardStepName());
		wizardStep.setWizardStepStatus(jaxWizardStep.getWizardStepStatus());
		wizardStep.setBemerkungen(jaxWizardStep.getBemerkungen());
		return wizardStep;
	}

	public JaxEbeguVorlage ebeguVorlageToJax(EbeguVorlage ebeguVorlage) {
		JaxEbeguVorlage jaxEbeguVorlage = new JaxEbeguVorlage();
		convertAbstractDateRangedFieldsToJAX(ebeguVorlage, jaxEbeguVorlage);

		jaxEbeguVorlage.setName(ebeguVorlage.getName());
		jaxEbeguVorlage.setProGesuchsperiode(ebeguVorlage.isProGesuchsperiode());
		if (ebeguVorlage.getVorlage() != null) {
			jaxEbeguVorlage.setVorlage(vorlageToJax(ebeguVorlage.getVorlage()));
		}

		return jaxEbeguVorlage;
	}

	private JaxVorlage vorlageToJax(Vorlage vorlage) {
		JaxVorlage jaxVorlage = convertAbstractFieldsToJAX(vorlage, new JaxVorlage());
		convertFileToJax(vorlage, jaxVorlage);
		return jaxVorlage;
	}

	private JaxFile convertFileToJax(FileMetadata fileMetadata, JaxFile jaxFile) {
		jaxFile.setFilename(fileMetadata.getFilename());
		jaxFile.setFilepfad(fileMetadata.getFilepfad());
		jaxFile.setFilesize(fileMetadata.getFilesize());
		return jaxFile;
	}

	public EbeguVorlage ebeguVorlageToEntity(@Nonnull final JaxEbeguVorlage ebeguVorlageJAXP, @Nonnull final EbeguVorlage ebeguVorlage) {
		Validate.notNull(ebeguVorlage);
		Validate.notNull(ebeguVorlageJAXP);
		convertAbstractDateRangedFieldsToEntity(ebeguVorlageJAXP, ebeguVorlage);

		ebeguVorlage.setName(ebeguVorlageJAXP.getName());
		ebeguVorlage.setProGesuchsperiode(ebeguVorlageJAXP.isProGesuchsperiode());
		if (ebeguVorlageJAXP.getVorlage() != null) {
			if (ebeguVorlage.getVorlage() == null) {
				ebeguVorlage.setVorlage(new Vorlage());
			}
			vorlageToEntity(ebeguVorlageJAXP.getVorlage(), ebeguVorlage.getVorlage());
		}

		return ebeguVorlage;
	}

	private Vorlage vorlageToEntity(JaxVorlage jaxVorlage, Vorlage vorlage) {
		Validate.notNull(vorlage);
		Validate.notNull(jaxVorlage);
		convertAbstractFieldsToEntity(jaxVorlage, vorlage);
		convertFileToEnity(jaxVorlage, vorlage);
		return vorlage;
	}

	private FileMetadata convertFileToEnity(JaxFile jaxFile, FileMetadata fileMetadata) {
		Validate.notNull(fileMetadata);
		Validate.notNull(jaxFile);
		fileMetadata.setFilename(jaxFile.getFilename());
		fileMetadata.setFilepfad(jaxFile.getFilepfad());
		fileMetadata.setFilesize(jaxFile.getFilesize());
		return fileMetadata;
	}

	public JaxAntragStatusHistory antragStatusHistoryToJAX(AntragStatusHistory antragStatusHistory) {
		final JaxAntragStatusHistory jaxAntragStatusHistory = convertAbstractFieldsToJAX(antragStatusHistory, new JaxAntragStatusHistory());
		jaxAntragStatusHistory.setGesuchId(antragStatusHistory.getGesuch().getId());
		jaxAntragStatusHistory.setStatus(AntragStatusConverterUtil.convertStatusToDTO(antragStatusHistory.getGesuch(), antragStatusHistory.getStatus()));
		jaxAntragStatusHistory.setBenutzer(benutzerToAuthLoginElement(antragStatusHistory.getBenutzer()));
		jaxAntragStatusHistory.setTimestampVon(antragStatusHistory.getTimestampVon());
		jaxAntragStatusHistory.setTimestampBis(antragStatusHistory.getTimestampBis());
		return jaxAntragStatusHistory;

	}

	public Collection<JaxAntragStatusHistory> antragStatusHistoryCollectionToJAX(Collection<AntragStatusHistory> antragStatusHistoryCollection) {
		final Collection<JaxAntragStatusHistory> jaxContainers = new ArrayList<>();
		if (antragStatusHistoryCollection != null) {
			for (final AntragStatusHistory antragStatusHistory : antragStatusHistoryCollection) {
				jaxContainers.add(antragStatusHistoryToJAX(antragStatusHistory));
			}
		}
		return jaxContainers;
	}

	/**
	 * Using the existing GesuchStatus and the UserRole it will translate the Status into the right one for this role.
	 */
	public void disguiseStatus(Gesuch gesuch, JaxAntragDTO antrag, @Nullable UserRole userRole) {
		if (userRole != null) {
			switch (userRole) {
			case GESUCHSTELLER:
			case SACHBEARBEITER_INSTITUTION:
			case SACHBEARBEITER_TRAEGERSCHAFT:
				switch (gesuch.getStatus()) {
				case PRUEFUNG_STV:
				case GEPRUEFT_STV:
				case IN_BEARBEITUNG_STV:
					antrag.setStatus(AntragStatusDTO.VERFUEGT);
					break;
				default:
					break;
				}
				break;
			default:
				break;
			}
		}
	}

	/**
	 * transformiert ein gesuch in ein JaxAntragDTO unter beruecksichtigung der rollen und erlaubten institutionen
	 * - Fuer die Rolle Steueramt werden saemtlichen Daten von den Kindern nicht geladen
	 * - Fuer die Rolle Institution/Traegerschaft werden nur die relevanten Institutionen und Angebote geladen
	 */
	public JaxAntragDTO gesuchToAntragDTO(Gesuch gesuch, @Nullable UserRole userRole, Collection<Institution> allowedInst) {
		//wir koennen nicht mit den container auf dem gesuch arbeiten weil das gesuch attached ist. hibernate
		//wuerde uns dann die kinder wegloeschen, daher besser transformieren
		Collection<JaxKindContainer> jaxKindContainers = new ArrayList<>(gesuch.getKindContainers().size());

		JaxAntragDTO antrag = gesuchToAntragDTOBasic(gesuch);

		if (userRole != UserRole.STEUERAMT) {
			for (final KindContainer kind : gesuch.getKindContainers()) {
				jaxKindContainers.add(kindContainerToJAX(kind));
			}
			antrag.setKinder(createKinderList(jaxKindContainers));
		}

		if (UserRole.SACHBEARBEITER_TRAEGERSCHAFT == userRole || UserRole.SACHBEARBEITER_INSTITUTION == userRole) {
			RestUtil.purgeKinderAndBetreuungenOfInstitutionen(jaxKindContainers, allowedInst);
		}

		disguiseStatus(gesuch, antrag, userRole);

		if (userRole != UserRole.STEUERAMT) {
			antrag.setAngebote(createAngeboteList(jaxKindContainers));
			antrag.setInstitutionen(createInstitutionenList(jaxKindContainers));
		}

		return antrag;
	}

	public JaxAntragDTO gesuchToAntragDTO(Gesuch gesuch, @Nullable UserRole userRole) {
		JaxAntragDTO antrag = gesuchToAntragDTOBasic(gesuch);
		antrag.setKinder(createKinderList(gesuch.getKindContainers()));
		antrag.setAngebote(createAngeboteList(gesuch.getKindContainers()));
		antrag.setInstitutionen(createInstitutionenList(gesuch.getKindContainers()));
		disguiseStatus(gesuch, antrag, userRole);
		return antrag;
	}

	@Nonnull
	private JaxAntragDTO gesuchToAntragDTOBasic(Gesuch gesuch) {
		JaxAntragDTO antrag = new JaxAntragDTO();
		antrag.setAntragId(gesuch.getId());
		antrag.setFallNummer(gesuch.getFall().getFallNummer());
		antrag.setFamilienName(gesuch.getGesuchsteller1() != null ? gesuch.getGesuchsteller1().extractNachname() : "");
		antrag.setEingangsdatum(gesuch.getEingangsdatum());
		antrag.setEingangsdatumSTV(gesuch.getEingangsdatumSTV());
		antrag.setAenderungsdatum(gesuch.getTimestampMutiert());
		antrag.setAntragTyp(gesuch.getTyp());
		antrag.setStatus(AntragStatusConverterUtil.convertStatusToDTO(gesuch, gesuch.getStatus()));
		antrag.setGesuchsperiodeGueltigAb(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb());
		antrag.setGesuchsperiodeGueltigBis(gesuch.getGesuchsperiode().getGueltigkeit().getGueltigBis());
		if (gesuch.getFall().getVerantwortlicher() != null) {
			antrag.setVerantwortlicher(gesuch.getFall().getVerantwortlicher().getFullName());
			antrag.setVerantwortlicherUsernameJA(gesuch.getFall().getVerantwortlicher().getUsername());
		}
		if (gesuch.getFall().getVerantwortlicherSCH() != null) {
			antrag.setVerantwortlicherSCH(gesuch.getFall().getVerantwortlicherSCH().getFullName());
			antrag.setVerantwortlicherUsernameSCH(gesuch.getFall().getVerantwortlicherSCH().getUsername());
		}
		antrag.setVerfuegt(gesuch.getStatus().isAnyStatusOfVerfuegt());
		antrag.setBeschwerdeHaengig(gesuch.getStatus() == AntragStatus.BESCHWERDE_HAENGIG);
		antrag.setLaufnummer(gesuch.getLaufnummer());
		antrag.setEingangsart(gesuch.getEingangsart());
		antrag.setBesitzerUsername(gesuch.getFall().getBesitzer() != null ? gesuch.getFall().getBesitzer().getUsername() : null);
		antrag.setGesuchBetreuungenStatus(gesuch.getGesuchBetreuungenStatus());
		antrag.setDokumenteHochgeladen(gesuch.getDokumenteHochgeladen());
		antrag.setFinSitStatus(gesuch.getFinSitStatus());
		return antrag;
	}

	public Mahnung mahnungToEntity(@Nonnull final JaxMahnung jaxMahnung, @Nonnull final Mahnung mahnung) {
		Validate.notNull(mahnung);
		Validate.notNull(jaxMahnung);
		convertAbstractFieldsToEntity(jaxMahnung, mahnung);

		Optional<Gesuch> gesuchFromDB = gesuchService.findGesuch(jaxMahnung.getGesuch().getId());
		if (gesuchFromDB.isPresent()) {
			mahnung.setGesuch(gesuchFromDB.get());// hier laden wir das geushc aus der db aber convertiren die Gesuchsdaten vom Client NICHT
		} else {
			throw new EbeguEntityNotFoundException("mahnungToEntity", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, jaxMahnung.getGesuch());
		}

		mahnung.setMahnungTyp(jaxMahnung.getMahnungTyp());
		mahnung.setDatumFristablauf(jaxMahnung.getDatumFristablauf());
		mahnung.setBemerkungen(jaxMahnung.getBemerkungen());
		mahnung.setTimestampAbgeschlossen(jaxMahnung.getTimestampAbgeschlossen());
		mahnung.setAbgelaufen(jaxMahnung.getAbgelaufen());
		return mahnung;
	}

	public JaxMahnung mahnungToJAX(@Nonnull final Mahnung persistedMahnung) {
		final JaxMahnung jaxMahnung = new JaxMahnung();
		convertAbstractFieldsToJAX(persistedMahnung, jaxMahnung);

		jaxMahnung.setGesuch(this.gesuchToJAX(persistedMahnung.getGesuch()));
		jaxMahnung.setMahnungTyp(persistedMahnung.getMahnungTyp());
		jaxMahnung.setDatumFristablauf(persistedMahnung.getDatumFristablauf());
		jaxMahnung.setBemerkungen(persistedMahnung.getBemerkungen());
		jaxMahnung.setTimestampAbgeschlossen(persistedMahnung.getTimestampAbgeschlossen());
		jaxMahnung.setAbgelaufen(persistedMahnung.getAbgelaufen());
		return jaxMahnung;
	}

	/**
	 * Geht durch die ganze Liste von KindContainers durch und gibt ein Set mit den BetreuungsangebotTyp aller Institutionen zurueck.
	 * Da ein Set zurueckgegeben wird, sind die Daten nie dupliziert.
	 */
	private Set<BetreuungsangebotTyp> createAngeboteList(Set<KindContainer> kindContainers) {
		Set<BetreuungsangebotTyp> resultSet = new HashSet<>();
		kindContainers.forEach(kindContainer
			-> kindContainer.getBetreuungen().forEach(betreuung -> resultSet.add(betreuung.getBetreuungsangebotTyp())));
		return resultSet;
	}

	private Set<String> createKinderList(Set<KindContainer> kindContainers) {
		Set<String> resultSet = new HashSet<>();
		kindContainers.forEach(kindContainer -> resultSet.add(kindContainer.getKindJA().getVorname()));
		return resultSet;
	}

	private Set<BetreuungsangebotTyp> createAngeboteList(Collection<JaxKindContainer> jaxKindContainers) {

		Set<BetreuungsangebotTyp> resultSet = new HashSet<>();
		jaxKindContainers.forEach(kindContainer -> kindContainer.getBetreuungen().forEach(betreuung -> resultSet.add(betreuung.getInstitutionStammdaten().getBetreuungsangebotTyp())));
		return resultSet;
	}

	private Set<String> createKinderList(Collection<JaxKindContainer> jaxKindContainers) {

		Set<String> resultSet = new HashSet<>();
		jaxKindContainers.forEach(kindContainer -> resultSet.add(kindContainer.getKindJA().getVorname()));
		return resultSet;
	}

	/**
	 * Geht durch die ganze Liste von KindContainers durch und gibt ein Set mit den Namen aller Institutionen zurueck.
	 * Da ein Set zurueckgegeben wird, sind die Daten nie dupliziert.
	 */
	private Set<String> createInstitutionenList(Set<KindContainer> kindContainers) {
		Set<String> resultSet = new HashSet<>();
		kindContainers.forEach(kindContainer -> kindContainer.getBetreuungen().forEach(betreuung -> {
			if (betreuung.getInstitutionStammdaten() != null && betreuung.getInstitutionStammdaten().getInstitution() != null) {
				resultSet.add(betreuung.getInstitutionStammdaten().getInstitution().getName());
			}
		}));
		return resultSet;
	}

	private Set<String> createInstitutionenList(Collection<JaxKindContainer> jaxKindContainers) {
		Set<String> resultSet = new HashSet<>();
		jaxKindContainers.forEach(kindContainer -> kindContainer.getBetreuungen().forEach(betreuung -> {
			if (betreuung.getInstitutionStammdaten() != null && betreuung.getInstitutionStammdaten().getInstitution() != null) {
				resultSet.add(betreuung.getInstitutionStammdaten().getInstitution().getName());
			}
		}));
		return resultSet;
	}

	public Mitteilung mitteilungToEntity(JaxMitteilung mitteilungJAXP, Mitteilung mitteilung) {
		Validate.notNull(mitteilungJAXP);
		Validate.notNull(mitteilung);

		convertAbstractFieldsToEntity(mitteilungJAXP, mitteilung);

		if (mitteilungJAXP.getEmpfaenger() != null) {
			Optional<Benutzer> empfaenger = benutzerService.findBenutzer(mitteilungJAXP.getEmpfaenger().getUsername());
			if (empfaenger.isPresent()) {
				mitteilung.setEmpfaenger(empfaenger.get()); // because the user doesn't come from the client but from the server
			} else {
				throw new EbeguEntityNotFoundException("mitteilungToEntity", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, mitteilungJAXP.getEmpfaenger());
			}
		}

		mitteilung.setEmpfaengerTyp(mitteilungJAXP.getEmpfaengerTyp());
		if (mitteilungJAXP.getFall() != null) {
			mitteilung.setFall(fallToEntity(mitteilungJAXP.getFall(), new Fall()));
		} else {
			throw new EbeguEntityNotFoundException("mitteilungToEntity", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, mitteilungJAXP.getFall());
		}
		if (mitteilungJAXP.getBetreuung() != null) {
			mitteilung.setBetreuung(betreuungToEntity(mitteilungJAXP.getBetreuung(), new Betreuung()));
		}
		mitteilung.setMessage(mitteilungJAXP.getMessage());
		mitteilung.setMitteilungStatus(mitteilungJAXP.getMitteilungStatus());

		if (mitteilungJAXP.getSender() != null) {
			Optional<Benutzer> sender = benutzerService.findBenutzer(mitteilungJAXP.getSender().getUsername());
			if (sender.isPresent()) {
				mitteilung.setSender(sender.get()); // because the user doesn't come from the client but from the server
			} else {
				throw new EbeguEntityNotFoundException("mitteilungToEntity", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, mitteilungJAXP.getSender());
			}
		}

		mitteilung.setSenderTyp(mitteilungJAXP.getSenderTyp());
		mitteilung.setSubject(mitteilungJAXP.getSubject());
		mitteilung.setSentDatum(mitteilungJAXP.getSentDatum());

		return mitteilung;
	}

	public JaxMitteilung mitteilungToJAX(Mitteilung persistedMitteilung, JaxMitteilung jaxMitteilung) {
		convertAbstractFieldsToJAX(persistedMitteilung, jaxMitteilung);
		if (persistedMitteilung.getEmpfaenger() != null) {
			jaxMitteilung.setEmpfaenger(benutzerToAuthLoginElement(persistedMitteilung.getEmpfaenger()));
		}
		jaxMitteilung.setEmpfaengerTyp(persistedMitteilung.getEmpfaengerTyp());
		if (persistedMitteilung.getFall() != null) {
			jaxMitteilung.setFall(fallToJAX(persistedMitteilung.getFall()));
		}
		if (persistedMitteilung.getBetreuung() != null) {
			jaxMitteilung.setBetreuung(betreuungToJAX(persistedMitteilung.getBetreuung()));
		}
		jaxMitteilung.setMessage(persistedMitteilung.getMessage());
		jaxMitteilung.setMitteilungStatus(persistedMitteilung.getMitteilungStatus());
		if (persistedMitteilung.getSender() != null) {
			jaxMitteilung.setSender(benutzerToAuthLoginElement(persistedMitteilung.getSender()));
		}
		jaxMitteilung.setSenderTyp(persistedMitteilung.getSenderTyp());
		jaxMitteilung.setSubject(persistedMitteilung.getSubject());
		jaxMitteilung.setSentDatum(persistedMitteilung.getSentDatum());
		return jaxMitteilung;
	}

	/**
	 * Creates the Betreuungsmitteilung without taking into accoutn if it already exists or not
	 */
	public Betreuungsmitteilung betreuungsmitteilungToEntity(JaxBetreuungsmitteilung mitteilungJAXP, Betreuungsmitteilung betreuungsmitteilung) {
		Validate.notNull(mitteilungJAXP);
		Validate.notNull(betreuungsmitteilung);

		mitteilungToEntity(mitteilungJAXP, betreuungsmitteilung);

		betreuungsmitteilung.setApplied(mitteilungJAXP.getApplied());
		if (mitteilungJAXP.getBetreuungspensen() != null) {
			betreuungsmitteilung.setBetreuungspensen(new HashSet<>());
			for (JaxBetreuungsmitteilungPensum jaxBetreuungspensum : mitteilungJAXP.getBetreuungspensen()) {
				final BetreuungsmitteilungPensum pensum = betreuungsmitteilungpensumToEntity(jaxBetreuungspensum, new BetreuungsmitteilungPensum());
				pensum.setBetreuungsmitteilung(betreuungsmitteilung);
				betreuungsmitteilung.getBetreuungspensen().add(pensum);
			}
		}
		return betreuungsmitteilung;
	}

	public JaxBetreuungsmitteilung betreuungsmitteilungToJAX(Betreuungsmitteilung persistedMitteilung) {
		final JaxBetreuungsmitteilung jaxBetreuungsmitteilung = new JaxBetreuungsmitteilung();
		mitteilungToJAX(persistedMitteilung, jaxBetreuungsmitteilung);

		jaxBetreuungsmitteilung.setApplied(persistedMitteilung.isApplied());
		if (persistedMitteilung.getBetreuungspensen() != null) {
			jaxBetreuungsmitteilung.setBetreuungspensen(new ArrayList<>());
			for (BetreuungsmitteilungPensum betreuungspensum : persistedMitteilung.getBetreuungspensen()) {
				jaxBetreuungsmitteilung.getBetreuungspensen().add(betreuungsmitteilungPensumToJax(betreuungspensum));
			}
		}
		return jaxBetreuungsmitteilung;
	}

	public JaxZahlungsauftrag zahlungsauftragToJAX(final Zahlungsauftrag persistedZahlungsauftrag, boolean convertZahlungen) {
		final JaxZahlungsauftrag jaxZahlungsauftrag = getJaxZahlungsauftrag(persistedZahlungsauftrag, convertZahlungen);
		return jaxZahlungsauftrag;
	}

	private JaxZahlungsauftrag getJaxZahlungsauftrag(Zahlungsauftrag persistedZahlungsauftrag, boolean convertZahlungen) {
		final JaxZahlungsauftrag jaxZahlungsauftrag = new JaxZahlungsauftrag();
		convertAbstractDateRangedFieldsToJAX(persistedZahlungsauftrag, jaxZahlungsauftrag);
		jaxZahlungsauftrag.setStatus(persistedZahlungsauftrag.getStatus());
		jaxZahlungsauftrag.setBeschrieb(persistedZahlungsauftrag.getBeschrieb());
		jaxZahlungsauftrag.setBetragTotalAuftrag(persistedZahlungsauftrag.getBetragTotalAuftrag());
		jaxZahlungsauftrag.setDatumFaellig(persistedZahlungsauftrag.getDatumFaellig());
		jaxZahlungsauftrag.setDatumGeneriert(persistedZahlungsauftrag.getDatumGeneriert());

		if (convertZahlungen) {
			jaxZahlungsauftrag.getZahlungen().addAll(
				persistedZahlungsauftrag.getZahlungen()
					.stream()
					.map(this::zahlungToJAX)
					.collect(Collectors.toList()));
		}
		return jaxZahlungsauftrag;
	}

	public JaxZahlungsauftrag zahlungsauftragToJAX(final Zahlungsauftrag persistedZahlungsauftrag, @Nullable UserRole userRole, Collection<Institution> allowedInst) {
		final JaxZahlungsauftrag jaxZahlungsauftrag = getJaxZahlungsauftrag(persistedZahlungsauftrag, true);

		// nur die Zahlungen welche inst sehen darf
		if (UserRole.SACHBEARBEITER_TRAEGERSCHAFT == userRole || UserRole.SACHBEARBEITER_INSTITUTION == userRole) {
			RestUtil.purgeZahlungenOfInstitutionen(jaxZahlungsauftrag, allowedInst);
			// es muss nochmal das Auftragstotal berechnet werden. Diesmal nur mit den erlaubten Zahlungen
			// Dies nur fuer Institutionen
			BigDecimal total = BigDecimal.ZERO;
			for (JaxZahlung zahlung : jaxZahlungsauftrag.getZahlungen()) {
				total = MathUtil.DEFAULT.add(total, zahlung.getBetragTotalZahlung());
			}
			jaxZahlungsauftrag.setBetragTotalAuftrag(total);
		} else {
			jaxZahlungsauftrag.setBetragTotalAuftrag(persistedZahlungsauftrag.getBetragTotalAuftrag());
		}
		return jaxZahlungsauftrag;
	}

	public JaxZahlung zahlungToJAX(final Zahlung persistedZahlung) {
		final JaxZahlung jaxZahlungs = new JaxZahlung();
		convertAbstractFieldsToJAX(persistedZahlung, jaxZahlungs);
		jaxZahlungs.setStatus(persistedZahlung.getStatus());
		jaxZahlungs.setBetragTotalZahlung(persistedZahlung.getBetragTotalZahlung());
		jaxZahlungs.setInstitutionsName(persistedZahlung.getInstitutionStammdaten().getInstitution().getName());
		jaxZahlungs.setInstitutionsId(persistedZahlung.getInstitutionStammdaten().getInstitution().getId());

		return jaxZahlungs;
	}

	public FerieninselStammdaten ferieninselStammdatenToEntity(JaxFerieninselStammdaten ferieninselStammdatenJAX, FerieninselStammdaten
		ferieninselStammdaten) {
		Validate.notNull(ferieninselStammdatenJAX);
		Validate.notNull(ferieninselStammdaten);

		convertAbstractFieldsToEntity(ferieninselStammdatenJAX, ferieninselStammdaten);
		ferieninselStammdaten.setFerienname(ferieninselStammdatenJAX.getFerienname());
		ferieninselStammdaten.setAnmeldeschluss(ferieninselStammdatenJAX.getAnmeldeschluss());

		if (ferieninselStammdatenJAX.getGesuchsperiode() != null && ferieninselStammdatenJAX.getGesuchsperiode().getId() != null) {
			final Optional<Gesuchsperiode> gesuchsperiode = gesuchsperiodeService.findGesuchsperiode(ferieninselStammdatenJAX.getGesuchsperiode().getId());
			if (gesuchsperiode.isPresent()) {
				// Gesuchsperiode darf nicht vom Client ueberschrieben werden
				ferieninselStammdaten.setGesuchsperiode(gesuchsperiode.get());
			} else {
				throw new EbeguEntityNotFoundException("ferieninselStammdatenToEntity", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND,
					ferieninselStammdatenJAX.getGesuchsperiode().getId());
			}
		}
		ferieninselZeitraumListToEntity(ferieninselStammdatenJAX.getZeitraumList(), ferieninselStammdaten.getZeitraumList());

		return ferieninselStammdaten;
	}

	private void ferieninselZeitraumListToEntity(final List<JaxFerieninselZeitraum> zeitraeumeListJAX, final Collection<FerieninselZeitraum> zeitraeumeList) {
		final Set<FerieninselZeitraum> transformedZeitraeume = new TreeSet<>();
		for (final JaxFerieninselZeitraum zeitraumJAX : zeitraeumeListJAX) {
			final FerieninselZeitraum zeitraumToMergeWith = zeitraeumeList
				.stream()
				.filter(existingZeitraum -> existingZeitraum.getId().equals(zeitraumJAX.getId()))
				.reduce(StreamsUtil.toOnlyElement())
				.orElse(new FerieninselZeitraum());
			final FerieninselZeitraum zeitraumToAdd = (FerieninselZeitraum) convertAbstractDateRangedFieldsToEntity(zeitraumJAX, zeitraumToMergeWith);
			final boolean added = transformedZeitraeume.add(zeitraumToAdd);
			if (!added) {
				LOGGER.warn(DROPPED_DUPLICATE_CONTAINER + "{}", zeitraumToAdd);
			}
		}
		zeitraeumeList.clear();
		zeitraeumeList.addAll(transformedZeitraeume);
	}

	public JaxFerieninselStammdaten ferieninselStammdatenToJAX(FerieninselStammdaten persistedFerieninselStammdaten) {
		final JaxFerieninselStammdaten jaxFerieninselStammdaten = new JaxFerieninselStammdaten();

		convertAbstractFieldsToJAX(persistedFerieninselStammdaten, jaxFerieninselStammdaten);
		jaxFerieninselStammdaten.setFerienname(persistedFerieninselStammdaten.getFerienname());
		jaxFerieninselStammdaten.setAnmeldeschluss(persistedFerieninselStammdaten.getAnmeldeschluss());
		jaxFerieninselStammdaten.setGesuchsperiode(gesuchsperiodeToJAX(persistedFerieninselStammdaten.getGesuchsperiode()));
		Collections.sort(persistedFerieninselStammdaten.getZeitraumList());
		for (FerieninselZeitraum ferieninselZeitraum : persistedFerieninselStammdaten.getZeitraumList()) {
			JaxFerieninselZeitraum jaxFerieninselZeitraum = new JaxFerieninselZeitraum();
			convertAbstractDateRangedFieldsToJAX(ferieninselZeitraum, jaxFerieninselZeitraum);
			jaxFerieninselStammdaten.getZeitraumList().add(jaxFerieninselZeitraum);
		}
		return jaxFerieninselStammdaten;
	}

	@Nullable
	public BelegungFerieninsel belegungFerieninselToEntity(@Nullable JaxBelegungFerieninsel belegungFerieninselJAX,
		@Nonnull BelegungFerieninsel belegungFerieninsel) {
		if (belegungFerieninselJAX != null) {
			Validate.notNull(belegungFerieninsel);

			convertAbstractFieldsToEntity(belegungFerieninselJAX, belegungFerieninsel);
			belegungFerieninsel.setFerienname(belegungFerieninselJAX.getFerienname());
			belegungFerieninselTageListToEntity(belegungFerieninselJAX.getTage(), belegungFerieninsel.getTage());
			return belegungFerieninsel;
		}
		return null;
	}

	private void belegungFerieninselTageListToEntity(@Nonnull final List<JaxBelegungFerieninselTag> jaxTagList,
		@Nonnull final Collection<BelegungFerieninselTag> tagList) {
		final Set<BelegungFerieninselTag> transformedTagList = new TreeSet<>();
		for (final JaxBelegungFerieninselTag jaxTag : jaxTagList) {
			final BelegungFerieninselTag tagToMergeWith = tagList
				.stream()
				.filter(existingTagEntity -> existingTagEntity.getId().equals(jaxTag.getId()))
				.reduce(StreamsUtil.toOnlyElement())
				.orElse(new BelegungFerieninselTag());
			final BelegungFerieninselTag tagToAdd = belegungFerieninselTagToEntity(jaxTag, tagToMergeWith);
			final boolean added = transformedTagList.add(tagToAdd);
			if (!added) {
				LOGGER.warn(DROPPED_DUPLICATE_CONTAINER + "{}", tagToAdd);
			}
		}

		//change the existing collection to reflect changes
		// Already tested: All existing Betreuungspensen of the list remain as they were, that means their data are updated
		// and the objects are not created again. ID and InsertTimeStamp are the same as before
		tagList.clear();
		tagList.addAll(transformedTagList);
	}

	private BelegungFerieninselTag belegungFerieninselTagToEntity(@Nonnull final JaxBelegungFerieninselTag jaxTag, @Nonnull final BelegungFerieninselTag tag) {
		Validate.notNull(jaxTag);
		Validate.notNull(tag);
		convertAbstractFieldsToEntity(jaxTag, tag);
		tag.setTag(jaxTag.getTag());
		return tag;
	}

	@Nullable
	public JaxBelegungFerieninsel belegungFerieninselToJAX(@Nullable BelegungFerieninsel persistedBelegungFerieninsel) {
		if (persistedBelegungFerieninsel != null) {
			final JaxBelegungFerieninsel jaxBelegungFerieninsel = new JaxBelegungFerieninsel();
			convertAbstractFieldsToJAX(persistedBelegungFerieninsel, jaxBelegungFerieninsel);
			jaxBelegungFerieninsel.setFerienname(persistedBelegungFerieninsel.getFerienname());
			jaxBelegungFerieninsel.setTage(belegungFerieninselTageListToJAX(persistedBelegungFerieninsel.getTage()));
			return jaxBelegungFerieninsel;
		}
		return null;
	}

	public List<JaxBelegungFerieninselTag> belegungFerieninselTageListToJAX(Collection<BelegungFerieninselTag> persistedFerieninselTageList) {
		List<JaxBelegungFerieninselTag> returnList = new ArrayList<>();
		persistedFerieninselTageList.forEach(ferieninselTag -> returnList.add(belegungFerieninselTagToJAX(ferieninselTag)));
		return returnList;
	}

	public JaxBelegungFerieninselTag belegungFerieninselTagToJAX(BelegungFerieninselTag persistedFerieninselTag) {
		JaxBelegungFerieninselTag jaxTag = new JaxBelegungFerieninselTag();
		convertAbstractFieldsToJAX(persistedFerieninselTag, jaxTag);
		jaxTag.setTag(persistedFerieninselTag.getTag());
		return jaxTag;
	}

	/**
	 * Kopiert die Daten die fuer den Motag eingegeben wurden in alle andere Wochentage
	 */
	public JaxInstitutionStammdaten updateJaxModuleTagesschule(@Nonnull JaxInstitutionStammdaten jaxInstDaten) {
		Objects.requireNonNull(jaxInstDaten);
		if (jaxInstDaten.getInstitutionStammdatenTagesschule() != null && !jaxInstDaten.getInstitutionStammdatenTagesschule().getModuleTagesschule().isEmpty()) {

			List<JaxModulTagesschule> moduleTagesschule = jaxInstDaten.getInstitutionStammdatenTagesschule().getModuleTagesschule();
			List<JaxModulTagesschule> moduleTagesschuleComplete = new ArrayList<>();
			DayOfWeek[] arbeitstageOhneMontag = { DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY };
			moduleTagesschule.stream()
				.filter(m -> m.getWochentag() == DayOfWeek.MONDAY)
				.forEach(res -> {
					moduleTagesschuleComplete.add(res);
					for (DayOfWeek dayOfWeek : arbeitstageOhneMontag) {
						JaxModulTagesschule modulTagesschule = new JaxModulTagesschule();
						modulTagesschule.setWochentag(dayOfWeek);
						modulTagesschule.setModulTagesschuleName(res.getModulTagesschuleName());
						modulTagesschule.setZeitVon(res.getZeitVon());
						modulTagesschule.setZeitBis(res.getZeitBis());
						moduleTagesschuleComplete.add(modulTagesschule);
					}
				});
			jaxInstDaten.getInstitutionStammdatenTagesschule().setModuleTagesschule(moduleTagesschuleComplete);
		}
		return jaxInstDaten;
	}
}
