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

import java.util.List;
import java.util.concurrent.Future;

import javax.annotation.Nonnull;

import ch.dvbern.ebegu.entities.Betreuung;
import ch.dvbern.ebegu.entities.DownloadFile;
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
	 * Sendet eine Email mit der Information, dass eine Anmeldung fuer ein Schulamt-Angebot ins Backend uebernommen wurde
	 */
	void sendInfoSchulamtAnmeldungUebernommen(@Nonnull Betreuung betreuung) throws MailException;

	/**
	 * Sendet eine Email mit der Information, dass eine Anmeldung fuer ein Schulamt-Angebot abgelehnt wurde.
	 */
	void sendInfoSchulamtAnmeldungAbgelehnt(@Nonnull Betreuung betreuung) throws MailException;

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

	/**
	 * Sendet unter gewissen Bedingungen pro Betreuung eine Email mit der Information, dass ein Betreuungsplatz geloescht wurde.
	 */
	void sendInfoBetreuungGeloescht(@Nonnull List<Betreuung> betreuungen);

	/**
	 * Sendet eine Email mit der Information, dass eine Betreuung verfuegt wurde.
	 */
	void sendInfoBetreuungVerfuegt(@Nonnull Betreuung betreuung);

	/**
	 * schickt eine email an den uebergebenen Empfaenger die angibt wie das angehaengte File heruntergeladen werden kann
	 * @param receiverEmail
	 * @param downloadFile
	 */
	void sendDocumentCreatedEmail(String receiverEmail, DownloadFile downloadFile) throws MailException;
}
