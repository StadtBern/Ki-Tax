package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Mitteilung;
import ch.dvbern.ebegu.mail.MailTemplateConfiguration;
import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsteller;
import ch.dvbern.ebegu.errors.MailException;
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

	@Override
	public void sendInfoBetreuungAbgelehnt(@Nonnull Betreuung betreuung) throws MailException {
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

	@Override
	public void sendInfoMitteilungErhalten(@Nonnull Mitteilung mitteilung) throws MailException {
		if (mitteilung.getEmpfaenger() != null && StringUtils.isNotEmpty(mitteilung.getEmpfaenger().getEmail())) {
			String mailaddress = mitteilung.getEmpfaenger().getEmail();
			String message = mailTemplateConfig.getInfoMitteilungErhalten(mitteilung);
			sendMessageWithTemplate(message, mailaddress);
			LOG.debug("Email fuer InfoMitteilungErhalten wurde versendet an" + mailaddress);
		} else {
			LOG.warn("skipping sendInfoMitteilungErhalten because Mitteilungsempfaenger is null");
		}
	}

	private Gesuchsteller extractGesuchsteller1(@Nonnull Gesuch gesuch) {
		if (gesuch.getGesuchsteller1() != null) {
			return gesuch.getGesuchsteller1().getGesuchstellerJA();
		}
		return null;
	}
}
