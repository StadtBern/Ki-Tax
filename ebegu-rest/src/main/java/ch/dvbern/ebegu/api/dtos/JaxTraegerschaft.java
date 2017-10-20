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
 * DTO fuer Traegerschaft
 */
@XmlRootElement(name = "traegerschaft")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxTraegerschaft extends JaxAbstractDTO {

	private static final long serialVersionUID = -1093676498323618626L;

	@NotNull
	private String name;

	@NotNull
	private Boolean active = true;

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

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
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
