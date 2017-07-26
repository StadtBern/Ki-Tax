package ch.dvbern.ebegu.api.resource.util;

import ch.dvbern.ebegu.authentication.PrincipalBean;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.*;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.services.BetreuungService;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.ebegu.util.AntragStatusConverterUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.Optional;

/**
 * Helper fuer die Statusueberpruefung in Resourcen
 */
@SuppressWarnings({ "PMD.AvoidDuplicateLiterals", "ImplicitArrayToString", "DMI_INVOKING_TOSTRING_ON_ARRAY"})
@SuppressFBWarnings({ "ImplicitArrayToString", "DMI_INVOKING_TOSTRING_ON_ARRAY"})
@Stateless
public class ResourceHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(ResourceHelper.class);

	public static final String ASSERT_GESUCH_STATUS = "assertGesuchStatus";
	public static final String ASSERT_GESUCH_STATUS_EQUAL = "assertGesuchStatusEqual";
	public static final String ASSERT_BETREUUNG_STATUS_EQUAL = "assertBetreuungStatusEqual";
	@Inject
	private GesuchService gesuchService;

	@Inject
	private BetreuungService betreuungService;

	@Inject
	private PrincipalBean principalBean;


	@SuppressWarnings("ConstantConditions")
	public void assertGesuchStatusEqual(@Nonnull String gesuchId, @Nonnull AntragStatusDTO... antragStatusFromClient) {
		Validate.notNull(gesuchId);
		Optional<Gesuch> optGesuch = gesuchService.findGesuch(gesuchId);
		Gesuch gesuchFromDB = optGesuch.orElseThrow(() -> new EbeguEntityNotFoundException(ASSERT_GESUCH_STATUS_EQUAL, ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, gesuchId));
		// Der Status des Client-Objektes darf nicht weniger weit sein als der des Server-Objektes
		for (AntragStatusDTO antragStatusDTO : antragStatusFromClient) {
			if (gesuchFromDB.getStatus() == AntragStatusConverterUtil.convertStatusToEntity(antragStatusDTO)) {
				return;
			}
		}
		// Kein Status hat gepasst
		String msg = "Expected GesuchStatus to be one of " + Arrays.toString(antragStatusFromClient) + " but was "
			+ "" + gesuchFromDB.getStatus();
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

	public void assertBetreuungStatusEqual(@Nonnull String betreuungId, @Nonnull Betreuungsstatus betreuungsstatusFromClient) {
		Validate.notNull(betreuungId);
		Optional<Betreuung> optBetreuung = betreuungService.findBetreuung(betreuungId);
		Betreuung betreuungFromDB = optBetreuung.orElseThrow(() -> new EbeguEntityNotFoundException(ASSERT_BETREUUNG_STATUS_EQUAL, ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, betreuungId));
		// Der Status des Client-Objektes darf nicht weniger weit sein als der des Server-Objektes
		if (betreuungFromDB.getBetreuungsstatus() != betreuungsstatusFromClient) {
			String msg = "Expected BetreuungStatus to be " + betreuungsstatusFromClient + " but was " + betreuungFromDB.getBetreuungsstatus();
			LOGGER.error(msg);
			throw new EbeguRuntimeException(ASSERT_BETREUUNG_STATUS_EQUAL, ErrorCodeEnum.ERROR_INVALID_EBEGUSTATE, betreuungId, msg);
		}
	}

}
