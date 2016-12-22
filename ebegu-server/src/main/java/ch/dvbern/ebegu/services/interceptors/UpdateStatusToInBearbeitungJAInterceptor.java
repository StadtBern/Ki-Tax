package ch.dvbern.ebegu.services.interceptors;

import ch.dvbern.ebegu.authentication.PrincipalBean;
import ch.dvbern.ebegu.config.EbeguConfiguration;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.enums.AntragStatus;
import ch.dvbern.ebegu.enums.Eingangsart;
import ch.dvbern.ebegu.enums.UserRole;
import ch.dvbern.ebegu.services.GesuchService;
import ch.dvbern.lib.cdipersistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;

import static ch.dvbern.ebegu.enums.UserRole.*;

/**
 * UpdateStatusInterceptor: Wir muessen den Status des Gesuchs von Freigegeben auf {@link ch.dvbern.ebegu.enums.AntragStatus.IN_BEARBEITUNG_JA}
 * setzen wenn das Jugendamt etwas an einem {@link AntragStatus#FREIGEGEBEN} Gesuch aendert.
 *
 */
public class UpdateStatusToInBearbeitungJAInterceptor {

	private final Logger LOG = LoggerFactory.getLogger(UpdateStatusToInBearbeitungJAInterceptor.class.getSimpleName());

	private static final UserRole[] JA_OR_ADM = {SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA};

	@Inject
	private PrincipalBean principalBean;

	@Inject
	private Persistence<Gesuch> persistence;

	@Inject
	private GesuchService gesuchService;

	@Inject
	private EbeguConfiguration configuration;

	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	@AroundInvoke
	public Object maybeChangeGesuchstatusToInBearbeitungJA(javax.interceptor.InvocationContext ctx) throws Exception {

		if (ctx.getParameters() != null && ctx.getParameters().length != 0) {
			String gesuchID = ctx.getParameters()[0] instanceof String ? (String) ctx.getParameters()[0] : null;
			if (gesuchID != null && principalBean.isCallerInAnyOfRole(JA_OR_ADM)) {
				Gesuch gesuch = persistence.find(Gesuch.class, gesuchID);
				if (gesuch == null) {
					LOG.info("Gesuch mit ID "+gesuchID + " wurde nicht in der DB gefunden");
				}
				//wenn es sich um ein freigegebenes online gesuch handelt und wir ein JA Mitarbeiter sind dann setzten wir Status auf inBearbeitung
				if (gesuch != null && Eingangsart.ONLINE.equals(gesuch.getEingangsart()) && AntragStatus.FREIGEGEBEN.equals(gesuch.getStatus())) {
					gesuch.setStatus(AntragStatus.IN_BEARBEITUNG_JA);
					gesuchService.updateGesuch(gesuch, true);

					if (configuration.getIsDevmode() || LOG.isDebugEnabled()) {
						LOG.info("Antrag wurde in den Status IN_BEARBEITUNG_JA gesetzt. ID " + gesuch.getId());
					}
				}
			}
		}
		return ctx.proceed();
	}

}
