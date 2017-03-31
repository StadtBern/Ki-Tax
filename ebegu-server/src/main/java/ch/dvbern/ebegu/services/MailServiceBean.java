package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsteller;
import ch.dvbern.ebegu.entities.Mitteilung;
import ch.dvbern.ebegu.errors.MailException;
import ch.dvbern.ebegu.mail.MailTemplateConfiguration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.security.PermitAll;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;


/**
 * Service fuer Senden von E-Mails
 */
@Stateless
@Local(MailService.class)
@PermitAll
@SuppressWarnings(value = {"PMD.AvoidDuplicateLiterals"})
public class MailServiceBean extends AbstractMailServiceBean implements MailService {

	private static final Logger LOG = LoggerFactory.getLogger(MailServiceBean.class.getSimpleName());

	@Inject
	private MailTemplateConfiguration mailTemplateConfig;

	@Inject
	private FallService fallService;


	@Override
	public void sendInfoBetreuungenBestaetigt(@Nonnull Gesuch gesuch) throws MailException {
		if (doSendMail(gesuch)) {
			Gesuchsteller gesuchsteller = gesuch.extractGesuchsteller1();
			String mailaddress = fallService.getCurrentEmailAddress(gesuch.getFall().getId()).orElse(null);
			if (gesuchsteller != null && StringUtils.isNotEmpty(mailaddress)) {
				String message = mailTemplateConfig.getInfoBetreuungenBestaetigt(gesuch, gesuchsteller, mailaddress);
				sendMessageWithTemplate(message, mailaddress);
				LOG.debug("Email fuer InfoBetreuungAbgelehnt wurde versendet an" + mailaddress);
			} else {
				LOG.warn("skipping sendInfoBetreuungAbgelehnt because Gesuchsteller 1 or mailaddr is null");
			}
		}
	}

	@Override
	public void sendInfoBetreuungAbgelehnt(@Nonnull Betreuung betreuung) throws MailException {
		if (doSendMail(betreuung.extractGesuch())) {
			Gesuchsteller gesuchsteller = betreuung.extractGesuch().extractGesuchsteller1();
			String mailaddress = fallService.getCurrentEmailAddress(betreuung.extractGesuch().getFall().getId()).orElse(null);
			if (gesuchsteller != null && StringUtils.isNotEmpty(mailaddress)) {
				String message = mailTemplateConfig.getInfoBetreuungAbgelehnt(betreuung, gesuchsteller, mailaddress);
				sendMessageWithTemplate(message, mailaddress);
				LOG.debug("Email fuer InfoBetreuungAbgelehnt wurde versendet an" + mailaddress);
			} else {
				LOG.warn("skipping sendInfoBetreuungAbgelehnt because Gesuchsteller 1 or mailaddress is null");
			}
		}
	}

	@Override
	public void sendInfoMitteilungErhalten(@Nonnull Mitteilung mitteilung) throws MailException {
		if (doSendMail(mitteilung.getBetreuung().extractGesuch())) {
			String mailaddress = fallService.getCurrentEmailAddress(mitteilung.getFall().getId()).orElse(null);
			if (StringUtils.isNotEmpty(mailaddress)) {
				String message = mailTemplateConfig.getInfoMitteilungErhalten(mitteilung, mailaddress);
				sendMessageWithTemplate(message, mailaddress);
				LOG.debug("Email fuer InfoMitteilungErhalten wurde versendet an" + mailaddress);
			} else {
				LOG.warn("skipping sendInfoMitteilungErhalten because Mitteilungsempfaenger is null");
			}
		}
	}

	@Override
	public void sendInfoVerfuegtGesuch(@Nonnull Gesuch gesuch) throws MailException {
		if (doSendMail(gesuch)) {
			String mailaddress = fallService.getCurrentEmailAddress(gesuch.getFall().getId()).orElse(null);
			Gesuchsteller gesuchsteller = gesuch.extractGesuchsteller1();
			if (gesuchsteller != null && StringUtils.isNotEmpty(mailaddress)) {
				String message = mailTemplateConfig.getInfoVerfuegtGesuch(gesuch, gesuchsteller, mailaddress);
				sendMessageWithTemplate(message, mailaddress);
				LOG.debug("Email fuer InfoVerfuegtGesuch wurde versendet an" + mailaddress);
			} else {
				LOG.warn("skipping sendInfoVerfuegtGesuch because Gesuchsteller 1 is null");
			}
		}
	}

	@Override
	public void sendInfoVerfuegtMutation(@Nonnull Gesuch gesuch) throws MailException {
		if (doSendMail(gesuch)) {
			String mailaddress = fallService.getCurrentEmailAddress(gesuch.getFall().getId()).orElse(null);
			Gesuchsteller gesuchsteller = gesuch.extractGesuchsteller1();
			if (gesuchsteller != null && StringUtils.isNotEmpty(mailaddress)) {
				String message = mailTemplateConfig.getInfoVerfuegtMutaion(gesuch, gesuchsteller, mailaddress);
				sendMessageWithTemplate(message, mailaddress);
				LOG.debug("Email fuer InfoVerfuegtMutation wurde versendet an" + mailaddress);
			} else {
				LOG.warn("skipping sendInfoVerfuegtMutation because Gesuchsteller 1 is null");
			}
		}
	}

	@Override
	public void sendInfoMahnung(@Nonnull Gesuch gesuch) throws MailException {
		if (doSendMail(gesuch)) {
			String mailaddress = fallService.getCurrentEmailAddress(gesuch.getFall().getId()).orElse(null);
			Gesuchsteller gesuchsteller = gesuch.extractGesuchsteller1();
			if (gesuchsteller != null && StringUtils.isNotEmpty(mailaddress)) {
				String message = mailTemplateConfig.getInfoMahnung(gesuch, gesuchsteller, mailaddress);
				sendMessageWithTemplate(message, mailaddress);
				LOG.debug("Email fuer InfoMahnung wurde versendet an" + mailaddress);
			} else {
				LOG.warn("skipping sendInfoMahnung because Gesuchsteller 1 is null");
			}
		}
	}

	@Override
	public void sendWarnungGesuchNichtFreigegeben(@Nonnull Gesuch gesuch, int anzahlMonate) throws MailException {
		if (doSendMail(gesuch)) {
			String mailaddress = fallService.getCurrentEmailAddress(gesuch.getFall().getId()).orElse(null);
			Gesuchsteller gesuchsteller = gesuch.extractGesuchsteller1();
			if (gesuchsteller != null && StringUtils.isNotEmpty(mailaddress)) {
				String message = mailTemplateConfig.getWarnungGesuchNichtFreigegeben(gesuch, gesuchsteller, mailaddress, anzahlMonate);
				sendMessageWithTemplate(message, mailaddress);
				LOG.debug("Email fuer WarnungGesuchNichtFreigegeben wurde versendet an" + mailaddress);
			} else {
				LOG.warn("skipping sendWarnungGesuchNichtFreigegeben because Gesuchsteller 1 is null");
			}
		}
	}

	@Override
	public void sendWarnungFreigabequittungFehlt(@Nonnull Gesuch gesuch) throws MailException {
		if (doSendMail(gesuch)) {
			String mailaddress = fallService.getCurrentEmailAddress(gesuch.getFall().getId()).orElse(null);
			Gesuchsteller gesuchsteller = gesuch.extractGesuchsteller1();
			if (gesuchsteller != null && StringUtils.isNotEmpty(mailaddress)) {
				String message = mailTemplateConfig.getWarnungFreigabequittungFehlt(gesuch, gesuchsteller, mailaddress);
				sendMessageWithTemplate(message, mailaddress);
				LOG.debug("Email fuer WarnungFreigabequittungFehlt wurde versendet an" + mailaddress);
			} else {
				LOG.warn("skipping sendWarnungFreigabequittungFehlt because Gesuchsteller 1 is null");
			}
		}
	}

	@Override
	public void sendInfoGesuchGeloescht(@Nonnull Gesuch gesuch) throws MailException {
		if (doSendMail(gesuch)) {
			String mailaddress = fallService.getCurrentEmailAddress(gesuch.getFall().getId()).orElse(null);
			Gesuchsteller gesuchsteller = gesuch.extractGesuchsteller1();
			if (gesuchsteller != null && StringUtils.isNotEmpty(mailaddress)) {
				String message = mailTemplateConfig.getInfoGesuchGeloescht(gesuch, gesuchsteller, mailaddress);
				sendMessageWithTemplate(message, mailaddress);
				LOG.debug("Email fuer InfoGesuchGeloescht wurde versendet an" + mailaddress);
			} else {
				LOG.warn("skipping sendInfoGesuchGeloescht because Gesuchsteller 1 is null");
			}
		}
	}

	/**
	 * Hier wird an einer Stelle definiert, an welche Benutzergruppen ein Mail geschickt werden soll.
	 */
	private boolean doSendMail(Gesuch gesuch) {
		return gesuch.getFall().getBesitzer() != null;
	}

}
