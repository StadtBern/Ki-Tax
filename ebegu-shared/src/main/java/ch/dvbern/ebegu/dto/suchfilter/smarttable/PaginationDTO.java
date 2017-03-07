package ch.dvbern.ebegu.dto.suchfilter.smarttable;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

/**
 * Paginationobjekt von Smarttable, beinhaltet den startindex, die anzahl resultate pro page (number) und das total
 * gefundener resultate
 */
@XmlTransient
@XmlAccessorType(XmlAccessType.FIELD)
public class PaginationDTO implements Serializable {

	private static final long serialVersionUID = 7555063492094787968L;
	private Integer number;
	private Integer start;
	private Long totalItemCount;

	private Integer numberOfPages;

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Long getTotalItemCount() {
		return totalItemCount;
	}

	public void setTotalItemCount(Long totalItemCount) {
		this.totalItemCount = totalItemCount;
	}

	public Integer getNumberOfPages() {
		return numberOfPages;
	}

	public void setNumberOfPages(Integer numberOfPages) {
		this.numberOfPages = numberOfPages;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("number", number)
			.append("start", start)
			.append("totalItemCount", totalItemCount)
			.toString();
	}
}
