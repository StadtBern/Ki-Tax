package ch.dvbern.ebegu.services;

import java.util.List;
import java.util.concurrent.Future;

import javax.annotation.Nonnull;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.Gesuch;
import ch.dvbern.ebegu.entities.Gesuchsperiode;
import ch.dvbern.ebegu.entities.Mitteilung;
import ch.dvbern.ebegu.errors.MailException;

/**
 * Service zum Versenden von E-Mails
 */
public interface MailService {

	/**
	 * Sendet die Email mit gegebenem MessageBody an die gegebene Adresse. Dadurch kann eine beliebige Message gemailt werden
	 */
	void sendMessage(@Nonnull String subject, @Nonnull String messageBody, @Nonnull String mailadress) throws MailException;

	/**
	 * Sendet eine Email mit der Information, dass alle Betreuungsplaetze bestaetigt wurden und das Gesuch freigegeben werden kann.
	 */
	void sendInfoBetreuungenBestaetigt(@Nonnull Gesuch gesuch) throws MailException;

	/**
	 * Sendet eine Email mit der Information, dass ein Betreuungsplatz abgelehnt wurde.
	 */
	void sendInfoBetreuungAbgelehnt(@Nonnull Betreuung betreuung) throws MailException;

	/**
	 * Sendet eine Email mit der Benachrichtigung, dass eine In-System Nachricht erhalten wurde.
	 */
	void sendInfoMitteilungErhalten(@Nonnull Mitteilung mitteilung) throws MailException;

	/**
	 * Sendet eine Email mit der Information, dass ein Gesuch Verfügt wurde.
	 */
	void sendInfoVerfuegtGesuch(@Nonnull Gesuch gesuch) throws MailException;

	/**
	 * Sendet eine Email mit der Information, dass eine Mutation Verfügt wurde.
	 */
	void sendInfoVerfuegtMutation(@Nonnull Gesuch gesuch) throws MailException;

	/**
	 * Sendet eine Email mit der Information, dass eine Mahnung versendet wurde.
	 */
	void sendInfoMahnung(@Nonnull Gesuch gesuch) throws MailException;

	/**
	 * Sendet eine Email mit der Information, dass ein Gesuch Verfügt wurde.
	 */
	void sendWarnungGesuchNichtFreigegeben(@Nonnull Gesuch gesuch, int anzahlTageBisLoeschung) throws MailException;

	/**
	 * Sendet eine Email mit der Information, dass ein Gesuch Verfügt wurde.
	 */
	void sendWarnungFreigabequittungFehlt(@Nonnull Gesuch gesuch, int anzahlTageBisLoeschung) throws MailException;

	/**
	 * Sendet eine Email mit der Information, dass ein Gesuch Verfügt wurde.
	 */
	void sendInfoGesuchGeloescht(@Nonnull Gesuch gesuch) throws MailException;

	/**
	 * Sendet eine Mail an den GS1 des übergebenen Gesuchs, dass die übergebene Gesuchsperiode eröffnet wurde.
	 */
	Future<Integer> sendInfoFreischaltungGesuchsperiode(@Nonnull Gesuchsperiode gesuchsperiode, @Nonnull List<Gesuch> gesucheToSendMail);

}
