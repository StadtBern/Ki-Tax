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

import java.io.IOException;
import java.io.Writer;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.transaction.Status;
import javax.transaction.TransactionSynchronizationRegistry;

import org.apache.commons.lang.Validate;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;
import org.apache.commons.net.smtp.SMTPClient;
import org.apache.commons.net.smtp.SMTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.dvbern.ebegu.config.EbeguConfiguration;
import ch.dvbern.ebegu.entities.DownloadFile;
import ch.dvbern.ebegu.errors.MailException;
import ch.dvbern.lib.cdipersistence.Persistence;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Allgemeine Mailing-Funktionalität
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
@PermitAll
public abstract class AbstractMailServiceBean extends AbstractBaseService {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractMailServiceBean.class.getSimpleName());
	private static final int CONNECTION_TIMEOUT = 15000;

	@Inject
	private Persistence persistence;

	@Inject
	private EbeguConfiguration configuration;

	@Resource
	private TransactionSynchronizationRegistry txReg;

	@PermitAll
	public void sendMessage(@Nonnull String subject, @Nonnull String messageBody, @Nonnull String mailadress) throws MailException {
		Validate.notNull(subject);
		Validate.notNull(messageBody);
		Validate.notNull(mailadress);
		if (configuration.isSendingOfMailsDisabled()) {
			pretendToSendMessage(messageBody, mailadress);
		} else {
			doSendMessage(subject, messageBody, mailadress);
		}
	}

	public void sendMessage(@Nonnull String subject, @Nonnull String messageBody, @Nonnull String mailadress, @Nonnull DownloadFile attachement) throws
		MailException {
		Validate.notNull(subject);
		Validate.notNull(messageBody);
		Validate.notNull(mailadress);
		if (configuration.isSendingOfMailsDisabled()) {
			pretendToSendMessage(messageBody, mailadress);
		} else {
			doSendMessage(subject, messageBody, mailadress, attachement);
		}
	}

	private void doSendMessage(@Nonnull String subject, @Nonnull String messageBody, @Nonnull String mailadress) throws MailException {
		try {
			Email email = new SimpleEmail();
			email.setHostName(configuration.getSMTPHost());
			email.setSmtpPort(configuration.getSMTPPort());
			email.setSSLOnConnect(false);
			email.setFrom(configuration.getSenderAddress());
			email.setSubject(subject);
			email.setMsg(messageBody);
			email.addTo(mailadress);
			email.send();
		} catch (final EmailException e) {
			throw new MailException("Error while sending Mail to: '" + mailadress + '\'', e);
		}
	}

	private void doSendMessage(@Nonnull String subject, @Nonnull String messageBody, @Nonnull String mailadress, @Nonnull DownloadFile file) throws MailException {

		try {
			// Create the attachment
			EmailAttachment attachment = new EmailAttachment();
			attachment.setPath(file.getFilepfad());
			attachment.setDisposition(EmailAttachment.ATTACHMENT);
			attachment.setDescription("Statistik");
			attachment.setName(file.getFilename());

			// Create the email message
			MultiPartEmail email = new MultiPartEmail();
			email.setHostName(configuration.getSMTPHost());
			email.setSmtpPort(configuration.getSMTPPort());
			email.setSSLOnConnect(false);
			email.setFrom(configuration.getSenderAddress());
			email.setSubject(subject);
			email.setMsg(messageBody);
			email.addTo(mailadress);

			// add the attachment
			email.attach(attachment);

			// send the email
			email.send();

		} catch (final EmailException e) {
			throw new MailException("Error while sending Mail to: '" + mailadress + '\'', e);
		}
	}


	@SuppressFBWarnings("REC_CATCH_EXCEPTION")
	private void doSendMessage(@Nonnull String messageBody, @Nonnull String mailadress) throws MailException {
		final SMTPClient client = new SMTPClient("UTF-8");
		try {
			client.setDefaultTimeout(CONNECTION_TIMEOUT);
			client.connect(configuration.getSMTPHost(), configuration.getSMTPPort());
			client.setSoTimeout(CONNECTION_TIMEOUT);
			assertPositiveCompletion(client);
			client.helo(configuration.getHostname());
			assertPositiveCompletion(client);
			client.setSender(configuration.getSenderAddress());
			assertPositiveCompletion(client);
			client.addRecipient(mailadress);
			assertPositiveCompletion(client);
			final Writer writer = client.sendMessageData();
			writer.write(messageBody);
			writer.close();
			assertPositiveIntermediate(client);
			client.quit();
		} catch (final Exception e) {
			throw new MailException("Error while sending Mail to: '" + mailadress + '\'', e);
		} finally {
			if (client.isConnected()) {
				try {
					client.disconnect();
				} catch (final IOException e) {
					LOG.error("Could not disconnetct client", e);
				}
			}
		}
	}

	/**
	 * Emails should only be sent when all actions were performed withou any error.
	 * For this reason this method flushes the EntityManager before sending emails.
	 */
	protected void sendMessageWithTemplate(@Nonnull final String messageBody, @Nonnull final String mailadress) throws MailException {
		Validate.notNull(mailadress);
		Validate.notNull(messageBody);
		// wir haben hier nicht zwingend immer eine transaktion
		final int transactionStatus = txReg.getTransactionStatus();
		if (Status.STATUS_NO_TRANSACTION != transactionStatus) {
			// nur wenn eine Transaction existiert, macht ein flush Sinn
			persistence.getEntityManager().flush();
		}
		if (configuration.isSendingOfMailsDisabled()) {
			pretendToSendMessage(messageBody, mailadress);
		} else {
			doSendMessage(messageBody, mailadress);
		}
	}

	private void pretendToSendMessage(final String messageBody, final String mailadress) {
		LOG.info("Sending of Emails disabled. Mail would be sent to " + mailadress + " : {}", messageBody);
	}

	private void assertPositiveIntermediate(final SMTPClient client) {
		assertPositiveIntermediate(client.getReplyCode());
	}

	private void assertPositiveIntermediate(final int replyCode) {
		if (!SMTPReply.isPositiveIntermediate(replyCode)) {
			throw new IllegalStateException("Reply code is not as expected: " + replyCode);
		}
	}

	private void assertPositiveCompletion(final int replyCode) {
		if (!SMTPReply.isPositiveCompletion(replyCode)) {
			throw new IllegalStateException("Reply code is not as expected: " + replyCode);
		}
	}

	private void assertPositiveCompletion(final SMTPClient client) {
		assertPositiveCompletion(client.getReplyCode());
	}
}
