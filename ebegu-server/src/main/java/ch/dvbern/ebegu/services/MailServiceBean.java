package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.*;
import ch.dvbern.ebegu.enums.Betreuungsstatus;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguEntityNotFoundException;
import ch.dvbern.ebegu.errors.MailException;
import ch.dvbern.ebegu.mail.MailTemplateConfiguration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

import static ch.dvbern.ebegu.enums.UserRoleName.*;

/**
 * Service fuer Senden von E-Mails
 */
@Stateless
@Local(MailService.class)
@PermitAll
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class MailServiceBean extends AbstractMailServiceBean implements MailService {

	private static final Logger LOG = LoggerFactory.getLogger(MailServiceBean.class.getSimpleName());

	@Inject
	private MailTemplateConfiguration mailTemplateConfig;

	@Inject
	private FallService fallService;

	@Inject
	private BetreuungService betreuungService;


	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION})
	public void sendInfoBetreuungenBestaetigt(@Nonnull Gesuch gesuch) throws MailException {
		if (doSendMail(gesuch.getFall())) {
			Gesuchsteller gesuchsteller = gesuch.extractGesuchsteller1();
			String mailaddress = fallService.getCurrentEmailAddress(gesuch.getFall().getId()).orElse(null);
			if (gesuchsteller != null && StringUtils.isNotEmpty(mailaddress)) {
				String message = mailTemplateConfig.getInfoBetreuungenBestaetigt(gesuch, gesuchsteller, mailaddress);
				sendMessageWithTemplate(message, mailaddress);
				LOG.debug("Email fuer InfoBetreuungAbgelehnt wurde versendet an {}", mailaddress);
			} else {
				LOG.warn("skipping sendInfoBetreuungAbgelehnt because Gesuchsteller 1 or mailaddr is null");
			}
		}
	}

	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_TRAEGERSCHAFT, SACHBEARBEITER_INSTITUTION})
	public void sendInfoBetreuungAbgelehnt(@Nonnull Betreuung betreuung) throws MailException {
		if (doSendMail(betreuung.extractGesuch().getFall())) {
			Gesuchsteller gesuchsteller = betreuung.extractGesuch().extractGesuchsteller1();
			String mailaddress = fallService.getCurrentEmailAddress(betreuung.extractGesuch().getFall().getId()).orElse(null);
			if (gesuchsteller != null && StringUtils.isNotEmpty(mailaddress)) {
				String message = mailTemplateConfig.getInfoBetreuungAbgelehnt(betreuung, gesuchsteller, mailaddress);
				sendMessageWithTemplate(message, mailaddress);
				LOG.debug("Email fuer InfoBetreuungAbgelehnt wurde versendet an {}", mailaddress);
			} else {
				LOG.warn("skipping sendInfoBetreuungAbgelehnt because Gesuchsteller 1 or mailaddress is null");
			}
		}
	}

	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER, SACHBEARBEITER_INSTITUTION, SACHBEARBEITER_TRAEGERSCHAFT})
	public void sendInfoMitteilungErhalten(@Nonnull Mitteilung mitteilung) throws MailException {
		if (doSendMail(mitteilung.getFall())) {
			String mailaddress = fallService.getCurrentEmailAddress(mitteilung.getFall().getId()).orElse(null);
			if (StringUtils.isNotEmpty(mailaddress)) {
				String message = mailTemplateConfig.getInfoMitteilungErhalten(mitteilung, mailaddress);
				sendMessageWithTemplate(message, mailaddress);
				LOG.debug("Email fuer InfoMitteilungErhalten wurde versendet an {}", mailaddress);
			} else {
				LOG.warn("skipping sendInfoMitteilungErhalten because Mitteilungsempfaenger is null");
			}
		}
	}

	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, SCHULAMT})
	public void sendInfoVerfuegtGesuch(@Nonnull Gesuch gesuch) throws MailException {
		if (doSendMail(gesuch.getFall())) {
			String mailaddress = fallService.getCurrentEmailAddress(gesuch.getFall().getId()).orElse(null);
			Gesuchsteller gesuchsteller = gesuch.extractGesuchsteller1();
			if (gesuchsteller != null && StringUtils.isNotEmpty(mailaddress)) {
				String message = mailTemplateConfig.getInfoVerfuegtGesuch(gesuch, gesuchsteller, mailaddress);
				sendMessageWithTemplate(message, mailaddress);
				LOG.debug("Email fuer InfoVerfuegtGesuch wurde versendet an {}", mailaddress);
			} else {
				LOG.warn("skipping sendInfoVerfuegtGesuch because Gesuchsteller 1 is null");
			}
		}
	}

	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, SCHULAMT})
	public void sendInfoVerfuegtMutation(@Nonnull Gesuch gesuch) throws MailException {
		if (doSendMail(gesuch.getFall())) {
			String mailaddress = fallService.getCurrentEmailAddress(gesuch.getFall().getId()).orElse(null);
			Gesuchsteller gesuchsteller = gesuch.extractGesuchsteller1();
			if (gesuchsteller != null && StringUtils.isNotEmpty(mailaddress)) {
				String message = mailTemplateConfig.getInfoVerfuegtMutaion(gesuch, gesuchsteller, mailaddress);
				sendMessageWithTemplate(message, mailaddress);
				LOG.debug("Email fuer InfoVerfuegtMutation wurde versendet an {}", mailaddress);
			} else {
				LOG.warn("skipping sendInfoVerfuegtMutation because Gesuchsteller 1 is null");
			}
		}
	}

	@Override
	@RolesAllowed({SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA})
	public void sendInfoMahnung(@Nonnull Gesuch gesuch) throws MailException {
		if (doSendMail(gesuch.getFall())) {
			String mailaddress = fallService.getCurrentEmailAddress(gesuch.getFall().getId()).orElse(null);
			Gesuchsteller gesuchsteller = gesuch.extractGesuchsteller1();
			if (gesuchsteller != null && StringUtils.isNotEmpty(mailaddress)) {
				String message = mailTemplateConfig.getInfoMahnung(gesuch, gesuchsteller, mailaddress);
				sendMessageWithTemplate(message, mailaddress);
				LOG.debug("Email fuer InfoMahnung wurde versendet an {}", mailaddress);
			} else {
				LOG.warn("skipping sendInfoMahnung because Gesuchsteller 1 is null");
			}
		}
	}

	@Override
	@RolesAllowed(SUPER_ADMIN)
	public void sendWarnungGesuchNichtFreigegeben(@Nonnull Gesuch gesuch, int anzahlTageBisLoeschung) throws MailException {
		if (doSendMail(gesuch.getFall())) {
			String mailaddress = fallService.getCurrentEmailAddress(gesuch.getFall().getId()).orElse(null);
			Gesuchsteller gesuchsteller = gesuch.extractGesuchsteller1();
			if (gesuchsteller != null && StringUtils.isNotEmpty(mailaddress)) {
				String message = mailTemplateConfig.getWarnungGesuchNichtFreigegeben(gesuch, gesuchsteller, mailaddress, anzahlTageBisLoeschung);
				sendMessageWithTemplate(message, mailaddress);
				LOG.debug("Email fuer WarnungGesuchNichtFreigegeben wurde versendet an {}", mailaddress);
			} else {
				LOG.warn("skipping sendWarnungGesuchNichtFreigegeben because Gesuchsteller 1 is null");
			}
		}
	}

	@Override
	@RolesAllowed(SUPER_ADMIN)
	public void sendWarnungFreigabequittungFehlt(@Nonnull Gesuch gesuch, int anzahlTageBisLoeschung) throws MailException {
		if (doSendMail(gesuch.getFall())) {
			String mailaddress = fallService.getCurrentEmailAddress(gesuch.getFall().getId()).orElse(null);
			Gesuchsteller gesuchsteller = gesuch.extractGesuchsteller1();
			if (gesuchsteller != null && StringUtils.isNotEmpty(mailaddress)) {
				String message = mailTemplateConfig.getWarnungFreigabequittungFehlt(gesuch, gesuchsteller, mailaddress, anzahlTageBisLoeschung);
				sendMessageWithTemplate(message, mailaddress);
				LOG.debug("Email fuer WarnungFreigabequittungFehlt wurde versendet an {}", mailaddress);
			} else {
				LOG.warn("skipping sendWarnungFreigabequittungFehlt because Gesuchsteller 1 is null");
			}
		}
	}

	@Override
	@RolesAllowed(SUPER_ADMIN)
	public void sendInfoGesuchGeloescht(@Nonnull Gesuch gesuch) throws MailException {
		if (doSendMail(gesuch.getFall())) {
			String mailaddress = fallService.getCurrentEmailAddress(gesuch.getFall().getId()).orElse(null);
			Gesuchsteller gesuchsteller = gesuch.extractGesuchsteller1();
			if (gesuchsteller != null && StringUtils.isNotEmpty(mailaddress)) {
				String message = mailTemplateConfig.getInfoGesuchGeloescht(gesuch, gesuchsteller, mailaddress);
				sendMessageWithTemplate(message, mailaddress);
				LOG.debug("Email fuer InfoGesuchGeloescht wurde versendet an {}", mailaddress);
			} else {
				LOG.warn("skipping sendInfoGesuchGeloescht because Gesuchsteller 1 is null");
			}
		}
	}

	@Override
	@Asynchronous
	@RolesAllowed({SUPER_ADMIN, ADMIN})
	public Future<Integer> sendInfoFreischaltungGesuchsperiode(@Nonnull Gesuchsperiode gesuchsperiode, @Nonnull List<Gesuch> gesucheToSendMail) {
		int i = 0;
		for (Gesuch gesuch : gesucheToSendMail) {
			try {
				if (doSendMail(gesuch.getFall())) {
					String mailaddress = fallService.getCurrentEmailAddress(gesuch.getFall().getId()).orElse(null);
					Gesuchsteller gesuchsteller = gesuch.extractGesuchsteller1();
					if (gesuchsteller != null && StringUtils.isNotEmpty(mailaddress)) {
						String message = mailTemplateConfig.getInfoFreischaltungGesuchsperiode(gesuchsperiode, gesuchsteller, mailaddress);
						sendMessageWithTemplate(message, mailaddress);
						LOG.debug("Email fuer InfoFreischaltungGesuchsperiode wurde versendet an {}", mailaddress);
					} else {
						LOG.warn("skipping InfoFreischaltungGesuchsperiode because Gesuchsteller 1 is null");
					}
				}
				i++;
			} catch (Exception e) {
				LOG.error("Mail InfoMahnung konnte nicht verschickt werden fuer Gesuch {}", gesuch.getId(), e);
			}
		}
		return new AsyncResult<>(i);
	}

	@Override
	@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER, SCHULAMT })
	public void sendInfoBetreuungGeloescht(@Nonnull List<Betreuung> betreuungen) {

		for (Betreuung betreuung : betreuungen) {

			Institution institution = betreuung.getInstitutionStammdaten().getInstitution();
			String mailaddress = institution.getMail();
			Gesuch gesuch = betreuung.extractGesuch();
			Fall fall = gesuch.getFall();
			Gesuchsteller gesuchsteller1 = gesuch.extractGesuchsteller1();
			Kind kind = betreuung.getKind().getKindJA();
			Betreuungsstatus status = betreuung.getBetreuungsstatus();
			LocalDate datumErstellung = betreuung.getTimestampErstellt().toLocalDate();
			LocalDate birthdayKind = kind.getGeburtsdatum();

			String message = mailTemplateConfig.getInfoBetreuungGeloescht(betreuung, fall, gesuchsteller1, kind,
				institution, mailaddress, datumErstellung, birthdayKind);

			try {
				if (gesuch.getTyp().isMutation()) {
					//wenn Gesuch Mutation ist
					if (betreuung.getVorgaengerId() == null) { //this is a new Betreuung for this Antrag
						if (status.isSendToInstitution()) { //wenn status warten, abgewiesen oder bestaetigt ist
							sendMessageWithTemplate(message, mailaddress);
							LOG.debug("Email fuer InfoBetreuungGeloescht wurde versendet an {}", mailaddress);
						}
					} else {
						Betreuung vorgaengerBetreuung = betreuungService.findBetreuung(betreuung
							.getVorgaengerId()).orElseThrow(() -> new EbeguEntityNotFoundException
							("sendInfoBetreuungGeloescht", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, betreuung.getVorgaengerId()));

						//wenn Vorgaengerbetreuung vorhanden
						if ((status == Betreuungsstatus.BESTAETIGT && !betreuung.isSame(vorgaengerBetreuung))
							|| (status == Betreuungsstatus.WARTEN || status == Betreuungsstatus.ABGEWIESEN)) {
							//wenn status der aktuellen Betreuung bestaetigt ist UND wenn vorgaenger NICHT die gleiche ist wie die aktuelle
							//oder wenn status der aktuellen Betreuung warten oder abgewiesen ist
							sendMessageWithTemplate(message, mailaddress);
							LOG.debug("Email fuer InfoBetreuungGeloescht wurde versendet an {}", mailaddress);
						}
					}
				} else {
					//wenn es keine Mutation ist
					if (status.isSendToInstitution()) {
						//wenn status warten, abgewiesen oder bestaetigt ist
						sendMessageWithTemplate(message, mailaddress);
						LOG.debug("Email fuer InfoBetreuungGeloescht wurde versendet an {}", mailaddress);
					}

				}
			} catch (MailException e) {
				LOG.error("Mail InfoBetreuungGeloescht konnte nicht verschickt werden fuer Betreuung {}", betreuung.getId(), e);
			}
		}
	}

	@Override
	@RolesAllowed({ SUPER_ADMIN, ADMIN, SACHBEARBEITER_JA, GESUCHSTELLER, SCHULAMT })
	public void sendInfoBetreuungVerfuegt(@Nonnull Betreuung betreuung) {

		Institution institution = betreuung.getInstitutionStammdaten().getInstitution();
		String mailaddress = institution.getMail();
		Gesuch gesuch = betreuung.extractGesuch();
		Fall fall = gesuch.getFall();
		Gesuchsteller gesuchsteller1 = gesuch.extractGesuchsteller1();
		Kind kind = betreuung.getKind().getKindJA();
		LocalDate birthdayKind = kind.getGeburtsdatum();

		String message = mailTemplateConfig.getInfoBetreuungVerfuegt(betreuung, fall, gesuchsteller1, kind,
			institution, mailaddress, birthdayKind);

		try {
			sendMessageWithTemplate(message, mailaddress);
			LOG.debug("Email fuer InfoBetreuungVerfuegt wurde versendet an {}", mailaddress);
		} catch (MailException e) {
			LOG.error("Mail InfoBetreuungVerfuegt konnte nicht verschickt werden fuer Betreuung {}", betreuung.getId(), e);
		}
	}

	/**
	 * Hier wird an einer Stelle definiert, an welche Benutzergruppen ein Mail geschickt werden soll.
	 */
	private boolean doSendMail(Fall fall) {
		return fall.getBesitzer() != null;
	}

}
