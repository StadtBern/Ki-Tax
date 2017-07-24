package ch.dvbern.ebegu.api.resource.util;

import java.util.Arrays;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import ch.dvbern.ebegu.api.dtos.JaxGesuch;
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
	public void assertGesuchStatusEqual(@Nonnull JaxGesuch jaxGesuch) {
		Validate.notNull(jaxGesuch.getId());
		Optional<Gesuch> optGesuch = gesuchService.findGesuch(jaxGesuch.getId());
		Gesuch gesuchFromDB = optGesuch.orElseThrow(() -> new EbeguEntityNotFoundException(ASSERT_GESUCH_STATUS_EQUAL, ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, jaxGesuch.getId()));
		// Der Status des Client-Objektes muss gleich sein wie der des Server-Objektes
		if (gesuchFromDB.getStatus() != AntragStatusConverterUtil.convertStatusToEntity(jaxGesuch.getStatus())) {
			String msg = "Cannot update GesuchStatus from " + gesuchFromDB.getStatus() + " to " + jaxGesuch.getStatus();
			LOGGER.error(msg);
			throw new EbeguRuntimeException(ASSERT_GESUCH_STATUS_EQUAL, ErrorCodeEnum.ERROR_INVALID_EBEGUSTATE, jaxGesuch.getId(), msg);
		}
	}

	@SuppressWarnings("ConstantConditions")
	public void assertGesuchStatus(@Nonnull JaxGesuch jaxGesuch) {
		assertGesuchStatus(jaxGesuch.getId(), jaxGesuch.getStatus());
	}

	@SuppressWarnings("ConstantConditions")
	public void assertGesuchStatus(@Nonnull String gesuchId, @Nonnull AntragStatusDTO antragStatusFromClient) {
		Validate.notNull(gesuchId);
		Optional<Gesuch> optGesuch = gesuchService.findGesuch(gesuchId);
		Gesuch gesuchFromDB = optGesuch.orElseThrow(() -> new EbeguEntityNotFoundException(ASSERT_GESUCH_STATUS, ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, gesuchId));
		// Der Status des Client-Objektes darf nicht weniger weit sein als der des Server-Objektes
		if (!isAntragStatusTransitionAllowed(AntragStatusConverterUtil.convertStatusToEntity(antragStatusFromClient), gesuchFromDB.getStatus())) {
			String msg = "Cannot update GesuchStatus from " + gesuchFromDB.getStatus() + " to " + antragStatusFromClient;
			LOGGER.error(msg);
			throw new EbeguRuntimeException(ASSERT_GESUCH_STATUS, ErrorCodeEnum.ERROR_INVALID_EBEGUSTATE, gesuchId, msg);
		}
	}

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

	/**
	 * This method will check if it is allowed to change from the status that the gesuch has in the server to the
	 * status that the client wants to save.
	 */
	private boolean isAntragStatusTransitionAllowed(@NotNull AntragStatus clientStatus, @NotNull AntragStatus serverStatus) {
		switch (clientStatus) {
			case IN_BEARBEITUNG_GS:
			case FREIGABEQUITTUNG: {
				return AntragStatus.IN_BEARBEITUNG_GS == serverStatus;
			}
			case NUR_SCHULAMT: {
				return AntragStatus.GEPRUEFT == serverStatus
					|| AntragStatus.NUR_SCHULAMT_DOKUMENTE_HOCHGELADEN == serverStatus;
			}
			case NUR_SCHULAMT_DOKUMENTE_HOCHGELADEN: {
				return AntragStatus.NUR_SCHULAMT == serverStatus;
			}
			case FREIGEGEBEN: {
				return AntragStatus.FREIGABEQUITTUNG == serverStatus;
			}
			case IN_BEARBEITUNG_JA: {
				return AntragStatus.IN_BEARBEITUNG_JA == serverStatus
					|| AntragStatus.FREIGEGEBEN == serverStatus
					|| AntragStatus.ERSTE_MAHNUNG == serverStatus
					|| AntragStatus.ERSTE_MAHNUNG_ABGELAUFEN == serverStatus
					|| AntragStatus.ERSTE_MAHNUNG_DOKUMENTE_HOCHGELADEN == serverStatus
					|| AntragStatus.ZWEITE_MAHNUNG == serverStatus
					|| AntragStatus.ZWEITE_MAHNUNG_ABGELAUFEN == serverStatus
					|| AntragStatus.ZWEITE_MAHNUNG_DOKUMENTE_HOCHGELADEN == serverStatus;
			}
			case ERSTE_MAHNUNG: {
				return AntragStatus.IN_BEARBEITUNG_JA == serverStatus
					|| AntragStatus.ERSTE_MAHNUNG_DOKUMENTE_HOCHGELADEN == serverStatus;
			}
			case ERSTE_MAHNUNG_DOKUMENTE_HOCHGELADEN: {
				return AntragStatus.ERSTE_MAHNUNG == serverStatus;
			}
			case ERSTE_MAHNUNG_ABGELAUFEN: {
				return AntragStatus.ERSTE_MAHNUNG == serverStatus
					|| AntragStatus.ERSTE_MAHNUNG_DOKUMENTE_HOCHGELADEN == serverStatus;
			}
			case ZWEITE_MAHNUNG: {
				return AntragStatus.ERSTE_MAHNUNG_ABGELAUFEN == serverStatus
					|| AntragStatus.ZWEITE_MAHNUNG_DOKUMENTE_HOCHGELADEN == serverStatus;
			}
			case ZWEITE_MAHNUNG_ABGELAUFEN: {
				return AntragStatus.ZWEITE_MAHNUNG == serverStatus
					|| AntragStatus.ZWEITE_MAHNUNG_DOKUMENTE_HOCHGELADEN == serverStatus;
			}
			case ZWEITE_MAHNUNG_DOKUMENTE_HOCHGELADEN: {
				return AntragStatus.ZWEITE_MAHNUNG == serverStatus;
			}
			case GEPRUEFT: {
				return AntragStatus.IN_BEARBEITUNG_JA == serverStatus;
			}
			case VERFUEGEN:
			case KEIN_ANGEBOT: {
				return AntragStatus.GEPRUEFT == serverStatus;
			}
			case VERFUEGT: {
				return AntragStatus.VERFUEGEN == serverStatus
					|| AntragStatus.GEPRUEFT_STV == serverStatus
					|| AntragStatus.BESCHWERDE_HAENGIG == serverStatus;
			}
			case BESCHWERDE_HAENGIG: {
				return AntragStatus.getAllVerfuegtStates().contains(serverStatus)
					&& AntragStatus.BESCHWERDE_HAENGIG != serverStatus;
			}
			case PRUEFUNG_STV: {
				return AntragStatus.VERFUEGT == serverStatus;
			}
			case IN_BEARBEITUNG_STV: {
				return AntragStatus.PRUEFUNG_STV == serverStatus;
			}
			case GEPRUEFT_STV: {
				return AntragStatus.IN_BEARBEITUNG_STV == serverStatus;
			}
		}
		return false; //by default no transition is allowed
	}
}
