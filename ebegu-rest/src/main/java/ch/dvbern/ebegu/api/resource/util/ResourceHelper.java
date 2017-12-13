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

package ch.dvbern.ebegu.api.resource.util;

import java.util.Arrays;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.ejb.Stateless;
import javax.inject.Inject;

import ch.dvbern.ebegu.authentication.PrincipalBean;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.AntragStatusDTO;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.services.BetreuungService;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.util.AntragStatusConverterUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper fuer die Statusueberpruefung in Resourcen
 */
@SuppressWarnings({ "PMD.AvoidDuplicateLiterals", "ImplicitArrayToString", "DMI_INVOKING_TOSTRING_ON_ARRAY" })
@SuppressFBWarnings({ "ImplicitArrayToString", "DMI_INVOKING_TOSTRING_ON_ARRAY" })
@Stateless
public class ResourceHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(ResourceHelper.class);

	public static final String ASSERT_GESUCH_STATUS_EQUAL = "assertGesuchStatusEqual";
	public static final String ASSERT_BETREUUNG_STATUS_EQUAL = "assertBetreuungStatusEqual";
	@Inject
	private GesuchService gesuchService;

	@Inject
	private BetreuungService betreuungService;

	@Inject
	private PrincipalBean principalBean;

	@SuppressWarnings("ConstantConditions")
	public void assertGesuchStatusForFreigabe(@Nonnull String gesuchId) {
		Validate.notNull(gesuchId);
		Optional<Gesuch> optGesuch = gesuchService.findGesuchForFreigabe(gesuchId);
		Gesuch gesuch = optGesuch.orElseThrow(() -> new EbeguEntityNotFoundException(ASSERT_GESUCH_STATUS_EQUAL, ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, gesuchId));
		assertGesuchStatus(gesuchId, gesuch, AntragStatusDTO.IN_BEARBEITUNG_GS, AntragStatusDTO.FREIGABEQUITTUNG);
	}

	@SuppressWarnings("ConstantConditions")
	public void assertGesuchStatusEqual(@Nonnull String gesuchId, @Nonnull AntragStatusDTO... antragStatusFromClient) {
		Validate.notNull(gesuchId);
		Optional<Gesuch> optGesuch = gesuchService.findGesuch(gesuchId);
		Gesuch gesuch = optGesuch.orElseThrow(() -> new EbeguEntityNotFoundException(ASSERT_GESUCH_STATUS_EQUAL, ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, gesuchId));
		assertGesuchStatus(gesuchId, gesuch, antragStatusFromClient);
	}

	/**
	 * Checks for the given gesuch if its status belongs to one of those that have been passed. If not an exception
	 * is thrown.
	 */
	private void assertGesuchStatus(@Nonnull String gesuchId, Gesuch gesuch, @Nonnull AntragStatusDTO... antragStatusFromClient) {
		for (AntragStatusDTO antragStatusDTO : antragStatusFromClient) {
			if (gesuch.getStatus() == AntragStatusConverterUtil.convertStatusToEntity(antragStatusDTO)) {
				return;
			}
		}
		// Kein Status hat gepasst
		String msg = "Expected GesuchStatus to be one of " + Arrays.toString(antragStatusFromClient) + " but was "
			+ gesuch.getStatus();
		LOGGER.error(msg);
		throw new EbeguRuntimeException(ASSERT_GESUCH_STATUS_EQUAL, ErrorCodeEnum.ERROR_INVALID_EBEGUSTATE, gesuchId, msg);
	}

	public void assertGesuchStatusForBenutzerRole(@Nonnull Gesuch gesuch) {
		UserRole userRole = principalBean.discoverMostPrivilegedRole();
		if (userRole == UserRole.SUPER_ADMIN) {
			// Superadmin darf alles
			return;
		}
		String msg = "Cannot update entity containing Gesuch " + gesuch.getId() + " in Status " + gesuch.getStatus() + " in UserRole " + userRole;
		if (userRole == UserRole.GESUCHSTELLER && gesuch.getStatus() != AntragStatus.IN_BEARBEITUNG_GS) {
			LOGGER.error(msg);
			throw new EbeguRuntimeException("assertGesuchStatusForBenutzerRole", ErrorCodeEnum.ERROR_INVALID_EBEGUSTATE, gesuch.getId(), msg);
		}
		if (gesuch.getStatus().ordinal() >= AntragStatus.VERFUEGEN.ordinal()) {
			LOGGER.error(msg);
			throw new EbeguRuntimeException("assertGesuchStatusForBenutzerRole", ErrorCodeEnum.ERROR_INVALID_EBEGUSTATE, gesuch.getId(), msg);
		}
	}

	@SuppressWarnings("PMD.CollapsibleIfStatements")
	public void assertGesuchStatusForBenutzerRole(@Nonnull Gesuch gesuch, @Nonnull Betreuung betreuung) {
		UserRole userRole = principalBean.discoverMostPrivilegedRole();
		if (userRole == UserRole.SUPER_ADMIN) {
			// Superadmin darf alles
			return;
		}
		String msg = "Cannot update entity containing Gesuch " + gesuch.getId() + " in Status " + gesuch.getStatus() + " in UserRole " + userRole;
		if (userRole == UserRole.GESUCHSTELLER && gesuch.getStatus() != AntragStatus.IN_BEARBEITUNG_GS) {
			// Schulamt-Anmeldungen duerfen auch nach der Freigabe hinzugefügt werden!
			if (betreuung.getBetreuungsangebotTyp() == null || !betreuung.getBetreuungsangebotTyp().isSchulamt()) {
				LOGGER.error(msg);
				throw new EbeguRuntimeException("assertGesuchStatusForBenutzerRole", ErrorCodeEnum.ERROR_INVALID_EBEGUSTATE, gesuch.getId(), msg);
			}
		}
		if (gesuch.getStatus().ordinal() >= AntragStatus.VERFUEGEN.ordinal()) {
			// Schulamt-Anmeldungen duerfen auch nach der Freigabe hinzugefügt werden!
			if (betreuung.getBetreuungsangebotTyp() == null || !betreuung.getBetreuungsangebotTyp().isSchulamt()) {
				LOGGER.error(msg);
				throw new EbeguRuntimeException("assertGesuchStatusForBenutzerRole", ErrorCodeEnum.ERROR_INVALID_EBEGUSTATE, gesuch.getId(), msg);
			}
		}
	}

	public void assertBetreuungStatusEqual(@Nonnull String betreuungId, @Nonnull Betreuungsstatus... betreuungsstatusFromClient) {
		Validate.notNull(betreuungId);
		Optional<Betreuung> optBetreuung = betreuungService.findBetreuung(betreuungId);
		Betreuung betreuungFromDB = optBetreuung.orElseThrow(() -> new EbeguEntityNotFoundException(ASSERT_BETREUUNG_STATUS_EQUAL, ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, betreuungId));
		// Der Status des Client-Objektes darf nicht weniger weit sein als der des Server-Objektes
		if (Arrays.stream(betreuungsstatusFromClient).noneMatch(status -> betreuungFromDB.getBetreuungsstatus() == status)) {
			String msg = "Expected BetreuungStatus to be " + betreuungsstatusFromClient + " but was " + betreuungFromDB.getBetreuungsstatus();
			LOGGER.error(msg);
			throw new EbeguRuntimeException(ASSERT_BETREUUNG_STATUS_EQUAL, ErrorCodeEnum.ERROR_INVALID_EBEGUSTATE, betreuungId, msg);
		}
	}

}
