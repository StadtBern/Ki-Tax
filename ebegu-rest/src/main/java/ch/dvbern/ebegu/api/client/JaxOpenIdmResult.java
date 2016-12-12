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

