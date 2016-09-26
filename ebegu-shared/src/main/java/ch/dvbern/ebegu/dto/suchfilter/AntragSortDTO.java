package ch.dvbern.ebegu.dto.suchfilter;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

/**
 * * Klasse zum deserialisieren/serialisieren des Sortfilters in Java
 */
@XmlTransient
@XmlAccessorType(XmlAccessType.FIELD)
public class AntragSortDTO implements Serializable {

	private static final long serialVersionUID = -742377991134129869L;
	/**
	 * definiert den Namen des Feldes nach dem sortiert werden soll. Allenfalls koennen wir hier auch ein Enum machen
	 */
	private String predicate;

	private Boolean reverse = Boolean.FALSE;

	public String getPredicate() {
		return predicate;
	}

	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("predicate", predicate)
			.toString();
	}

	public Boolean getReverse() {
		return reverse;
	}

	public void setReverse(Boolean reverse) {
		this.reverse = reverse;
	}
}
