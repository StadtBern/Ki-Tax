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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;

import ch.dvbern.ebegu.dto.FinanzielleSituationResultateDTO;
import ch.dvbern.ebegu.entities.EbeguParameter;
import ch.dvbern.ebegu.entities.FinanzielleSituationContainer;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.EbeguParameterKey;
import ch.dvbern.ebegu.enums.WizardStepName;
import ch.dvbern.ebegu.persistence.CriteriaQueryHelper;
import ch.dvbern.ebegu.util.FinanzielleSituationRechner;
import ch.dvbern.ebegu.util.MathUtil;
import ch.dvbern.lib.cdipersistence.Persistence;

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
 * Service fuer FinanzielleSituation
 */
@Stateless
@Local(FinanzielleSituationService.class)
@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR, GESUCHSTELLER, STEUERAMT, SCHULAMT, ADMINISTRATOR_SCHULAMT })
public class FinanzielleSituationServiceBean extends AbstractBaseService implements FinanzielleSituationService {

	@Inject
	private Persistence persistence;

	@Inject
	private CriteriaQueryHelper criteriaQueryHelper;

	@Inject
	private FinanzielleSituationRechner finSitRechner;

	@Inject
	private WizardStepService wizardStepService;

	@Inject
	private Authorizer authorizer;

	@Inject
	private EbeguParameterService ebeguParameterService;

	@Inject
	private GesuchService gesuchService;

	@Nonnull
	@Override
	@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER, SCHULAMT, ADMINISTRATOR_SCHULAMT })
	public FinanzielleSituationContainer saveFinanzielleSituation(@Nonnull FinanzielleSituationContainer finanzielleSituation, String gesuchId) {
		Objects.requireNonNull(finanzielleSituation);
		authorizer.checkCreateAuthorizationFinSit(finanzielleSituation);
		FinanzielleSituationContainer finanzielleSituationPersisted = persistence.merge(finanzielleSituation);
		if (gesuchId != null) {
			wizardStepService.updateSteps(gesuchId, null, finanzielleSituationPersisted.getFinanzielleSituationJA(), WizardStepName
				.FINANZIELLE_SITUATION);
		}
		return finanzielleSituationPersisted;
	}

	@Nonnull
	@Override
	@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER, SCHULAMT, ADMINISTRATOR_SCHULAMT })
	public Gesuch saveFinanzielleSituationStart(@Nonnull Gesuch gesuch) {
		Gesuch modifiedGesuch = gesuchService.updateGesuch(gesuch, false, null);
		wizardStepService.updateSteps(modifiedGesuch.getId(), null, null, WizardStepName.FINANZIELLE_SITUATION, 1);
		return modifiedGesuch;
	}

	@Nonnull
	@Override
	@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR, GESUCHSTELLER, STEUERAMT, ADMINISTRATOR_SCHULAMT, SCHULAMT })
	public Optional<FinanzielleSituationContainer> findFinanzielleSituation(@Nonnull String id) {
		Objects.requireNonNull(id, "id muss gesetzt sein");
		FinanzielleSituationContainer finanzielleSituation = persistence.find(FinanzielleSituationContainer.class, id);
		authorizer.checkReadAuthorization(finanzielleSituation); //TODO: EBEGU-1610
		return Optional.ofNullable(finanzielleSituation);
	}

	@Nonnull
	@Override
	@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR, GESUCHSTELLER, STEUERAMT, SCHULAMT, ADMINISTRATOR_SCHULAMT })
	public Collection<FinanzielleSituationContainer> getAllFinanzielleSituationen() {
		Collection<FinanzielleSituationContainer> finanzielleSituationen = criteriaQueryHelper.getAll(FinanzielleSituationContainer.class);
		authorizer.checkReadAuthorization(finanzielleSituationen);
		return new ArrayList<>(finanzielleSituationen);
	}

	@Override
	@Nonnull
	@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR, GESUCHSTELLER, STEUERAMT, SACHBEARBEITER_INSTITUTION,
		SACHBEARBEITER_TRAEGERSCHAFT, ADMINISTRATOR_SCHULAMT, SCHULAMT })
	public FinanzielleSituationResultateDTO calculateResultate(@Nonnull Gesuch gesuch) {
		return finSitRechner.calculateResultateFinanzielleSituation(gesuch, true);
	}

	@Override
	@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, JURIST, REVISOR, GESUCHSTELLER, STEUERAMT, SACHBEARBEITER_INSTITUTION,
		SACHBEARBEITER_TRAEGERSCHAFT, ADMINISTRATOR_SCHULAMT, SCHULAMT })
	public void calculateFinanzDaten(@Nonnull Gesuch gesuch) {
		final BigDecimal minimumEKV = calculateGrenzwertEKV(gesuch);
		finSitRechner.calculateFinanzDaten(gesuch, minimumEKV);
	}

	/**
	 * Es wird nach dem Param PARAM_GRENZWERT_EINKOMMENSVERSCHLECHTERUNG gesucht, der einen Wert von 0 bis 100 haben muss. Dann wird
	 * die 100-komplementaer Zahl berechnet und 1-prozentuell zurueckgegeben. z.B:
	 * PARAM_GRENZWERT_EINKOMMENSVERSCHLECHTERUNG = 20 --> return 0.80
	 * Sollte der Parameter nicht definiert sein, wird 1.00 zurueckgegeben, d.h. keine Grenze fuer EKV
	 */
	private BigDecimal calculateGrenzwertEKV(@Nonnull Gesuch gesuch) {
		final Optional<EbeguParameter> optGrenzwert = ebeguParameterService.getEbeguParameterByKeyAndDate(EbeguParameterKey
				.PARAM_GRENZWERT_EINKOMMENSVERSCHLECHTERUNG,
			gesuch.getGesuchsperiode().getGueltigkeit().getGueltigAb());
		if (optGrenzwert.isPresent()) {
			return MathUtil.ZWEI_NACHKOMMASTELLE.divide(BigDecimal.valueOf(100).subtract(optGrenzwert.get().getValueAsBigDecimal()), BigDecimal.valueOf(100));
		}
		return BigDecimal.ONE; // By default wird 1 als Grenz gesetzt und alle EKV werden akzeptiert
	}
}
