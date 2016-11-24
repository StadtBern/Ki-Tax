package ch.dvbern.ebegu.api.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Created by imanol on 17.03.16.
 * DTO fuer Adresse
 */
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

	public void set_id(String _id) {
		this._id = _id;
	}

	public String get_rev() {
		return _rev;
	}

	public void set_rev(String _rev) {
		this._rev = _rev;
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

