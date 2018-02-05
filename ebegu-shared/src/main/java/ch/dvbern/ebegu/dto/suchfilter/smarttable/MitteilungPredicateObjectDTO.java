/*
 * Ki-Tax: System for the management of external childcare subsidies
 * Copyright (C) 2018 City of Bern Switzerland
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

package ch.dvbern.ebegu.dto.suchfilter.smarttable;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Klasse zum deserialisieren/serialisieren des SmartTable Filter Objekts fuer suchfilter in Java
 */
@XmlTransient
@XmlAccessorType(XmlAccessType.FIELD)
public class MitteilungPredicateObjectDTO implements Serializable {

	private static final long serialVersionUID = -2248051428962150142L;

	private String sender; 			// mitteilung.sender.fullName
	private String fallNummer; 		// mitteilung.fall.fallNummer
	private String familienName; 	// mitteilung.fall.besitzer.fullName
	private String subject; 		// mitteilung.subject
	private String sentDatum;		// mitteilung.sentDatum
	private String empfaenger; 		// mitteilung.empfaenger.fullName
	private String empfaengerAmt; 	// mitteilung.empfaengerAmt;
	private String mitteilungStatus;// mitteilung.status


	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getFallNummer() {
		return fallNummer;
	}

	public void setFallNummer(String fallNummer) {
		this.fallNummer = fallNummer;
	}

	public String getFamilienName() {
		return familienName;
	}

	public void setFamilienName(String familienName) {
		this.familienName = familienName;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSentDatum() {
		return sentDatum;
	}

	public void setSentDatum(String sentDatum) {
		this.sentDatum = sentDatum;
	}

	public String getEmpfaenger() {
		return empfaenger;
	}

	public void setEmpfaenger(String empfaenger) {
		this.empfaenger = empfaenger;
	}

	public String getEmpfaengerAmt() {
		return empfaengerAmt;
	}

	public void setEmpfaengerAmt(String empfaengerAmt) {
		this.empfaengerAmt = empfaengerAmt;
	}

	public String getMitteilungStatus() {
		return mitteilungStatus;
	}

	public void setMitteilungStatus(String mitteilungStatus) {
		this.mitteilungStatus = mitteilungStatus;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("sender", sender)
			.append("fallNummer", fallNummer)
			.append("familienName", familienName)
			.append("subject", subject)
			.append("sentDatum", sentDatum)
			.append("empfaenger", empfaenger)
			.append("emfaengerAmt", empfaengerAmt)
			.append("mitteilungStatus", mitteilungStatus)
			.toString();
	}
}
