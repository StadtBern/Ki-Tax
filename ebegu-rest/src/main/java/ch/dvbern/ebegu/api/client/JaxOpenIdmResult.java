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

package ch.dvbern.ebegu.api.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;


/**
 * Jax Element for OpenIdm Result element
 */
@SuppressWarnings({"InstanceVariableNamingConvention", "InstanceMethodNamingConvention"})
@XmlRootElement(name = "institutionOpenIdm")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxOpenIdmResult implements Serializable {

	private static final long serialVersionUID = -1093677998323618626L;

	private String _id;
	private String _rev;
	private String mail;
	private String name;
	private String type;

	public String get_id() {
		return _id;
	}

	public void set_id(String id) {
		this._id = id;
	}

	public String get_rev() {
		return _rev;
	}

	public void set_rev(String rev) {
		this._rev = rev;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}

