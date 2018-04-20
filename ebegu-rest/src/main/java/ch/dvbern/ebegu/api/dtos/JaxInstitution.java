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

package ch.dvbern.ebegu.api.dtos;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by imanol on 17.03.16.
 * DTO fuer Institution
 */
@XmlRootElement(name = "institution")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxInstitution extends JaxAbstractDTO {

	private static final long serialVersionUID = -1393677898323418626L;

	@NotNull
	private String name;

	private JaxTraegerschaft traegerschaft;
	@NotNull
	private JaxMandant mandant;

	@NotNull
	private String mail;

	// just to communicate with client
	private boolean synchronizedWithOpenIdm = false;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public JaxTraegerschaft getTraegerschaft() {
		return traegerschaft;
	}

	public void setTraegerschaft(JaxTraegerschaft traegerschaft) {
		this.traegerschaft = traegerschaft;
	}

	public JaxMandant getMandant() {
		return mandant;
	}

	public void setMandant(JaxMandant mandant) {
		this.mandant = mandant;
	}

	public boolean isSynchronizedWithOpenIdm() {
		return synchronizedWithOpenIdm;
	}

	public void setSynchronizedWithOpenIdm(boolean synchronizedWithOpenIdm) {
		this.synchronizedWithOpenIdm = synchronizedWithOpenIdm;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}
}
