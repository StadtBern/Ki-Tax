package ch.dvbern.ebegu.dto.suchfilter;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Aggregat Klasse zum deserialisieren/serialisieren des gesamten SmartTable-Filterobjekts
 */
@XmlRootElement(name = "fallsucheFilter")
@XmlAccessorType(XmlAccessType.FIELD)
public class AntragTableFilterDTO implements Serializable {

	private static final long serialVersionUID = 404959569485575365L;
	private PaginationDTO pagination;

	private AntragSearchDTO search;

	private AntragSortDTO sort;

	public PaginationDTO getPagination() {
		return pagination;
	}

	public void setPagination(PaginationDTO pagination) {
		this.pagination = pagination;
	}

	public AntragSearchDTO getSearch() {
		return search;
	}

	public void setSearch(AntragSearchDTO search) {
		this.search = search;
	}

	public AntragSortDTO getSort() {
		return sort;
	}

	public void setSort(AntragSortDTO sort) {
		this.sort = sort;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("pagination", pagination)
			.append("search", search)
			.append("sort", sort)
			.toString();
	}
}
