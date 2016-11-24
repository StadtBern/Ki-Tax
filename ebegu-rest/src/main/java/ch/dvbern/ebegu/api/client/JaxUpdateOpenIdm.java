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
public class JaxUpdateOpenIdm implements Serializable {

	private static final long serialVersionUID = -1093677998323618626L;

	private String operation;
	private String field;
	private String value;

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "JaxUpdateOpenIdm{" +
			"operation='" + operation + '\'' +
			", field='" + field + '\'' +
			", value='" + value + '\'' +
			'}';
	}
}
