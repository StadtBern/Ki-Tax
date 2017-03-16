package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsteller;
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


	@Override
	public void sendInfoBetreuungenBestaetigt(@Nonnull Gesuch gesuch) throws MailException {
		if (doSendMail(gesuch)) {
			Gesuchsteller gesuchsteller = extractGesuchsteller1(gesuch);
			if (gesuchsteller != null && StringUtils.isNotEmpty(gesuchsteller.getMail())) {
				String mailaddress = gesuchsteller.getMail();
				String message = mailTemplateConfig.getInfoBetreuungenBestaetigt(gesuch, gesuchsteller);
				sendMessageWithTemplate(message, mailaddress);
				LOG.debug("Email fuer InfoBetreuungAbgelehnt wurde versendet an" + mailaddress);
			} else {
				LOG.warn("skipping sendInfoBetreuungAbgelehnt because Gesuchsteller 1 is null");
			}
		}
	}

	@Override
	public void sendInfoBetreuungAbgelehnt(@Nonnull Betreuung betreuung) throws MailException {
		if (doSendMail(betreuung.extractGesuch())) {
			Gesuchsteller gesuchsteller = extractGesuchsteller1(betreuung.extractGesuch());
			if (gesuchsteller != null && StringUtils.isNotEmpty(gesuchsteller.getMail())) {
				String mailaddress = gesuchsteller.getMail();
				String message = mailTemplateConfig.getInfoBetreuungAbgelehnt(betreuung, gesuchsteller);
				sendMessageWithTemplate(message, mailaddress);
				LOG.debug("Email fuer InfoBetreuungAbgelehnt wurde versendet an" + mailaddress);
			} else {
				LOG.warn("skipping sendInfoBetreuungAbgelehnt because Gesuchsteller 1 is null");
			}
		}
	}

	@Override
	public void sendInfoVerfuegtGesuch(@Nonnull Gesuch gesuch) throws MailException {
		if (doSendMail(gesuch)) {
			Gesuchsteller gesuchsteller = extractGesuchsteller1(gesuch);
			if (gesuchsteller != null && StringUtils.isNotEmpty(gesuchsteller.getMail())) {
				String mailaddress = gesuchsteller.getMail();
				String message = mailTemplateConfig.getInfoVerfuegtGesuch(gesuch, gesuchsteller);
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
			Gesuchsteller gesuchsteller = extractGesuchsteller1(gesuch);
			if (gesuchsteller != null && StringUtils.isNotEmpty(gesuchsteller.getMail())) {
				String mailaddress = gesuchsteller.getMail();
				String message = mailTemplateConfig.getInfoVerfuegtMutaion(gesuch, gesuchsteller);
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
			Gesuchsteller gesuchsteller = extractGesuchsteller1(gesuch);
			if (gesuchsteller != null && StringUtils.isNotEmpty(gesuchsteller.getMail())) {
				String mailaddress = gesuchsteller.getMail();
				String message = mailTemplateConfig.getInfoMahnung(gesuch, gesuchsteller);
				sendMessageWithTemplate(message, mailaddress);
				LOG.debug("Email fuer InfoMahnung wurde versendet an" + mailaddress);
			} else {
				LOG.warn("skipping sendInfoMahnung because Gesuchsteller 1 is null");
			}
		}
	}

	/**
	 * Hier wird an einer Stelle definiert, an welche Benutzergruppen ein Mail geschickt werden soll.
	 */
	private boolean doSendMail(Gesuch gesuch) {
		return gesuch.getFall().getBesitzer() != null;
	}

	private Gesuchsteller extractGesuchsteller1(@Nonnull Gesuch gesuch) {
		if (gesuch.getGesuchsteller1() != null) {
			return gesuch.getGesuchsteller1().getGesuchstellerJA();
		}
		return null;
	}
}
