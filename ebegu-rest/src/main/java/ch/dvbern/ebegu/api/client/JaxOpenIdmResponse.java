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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Jax Element for Response of OpenIdm, gives result and page counts
 */
@XmlRootElement(name = "openIdmResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class JaxOpenIdmResponse implements Serializable {

	private static final long serialVersionUID = -1093677998323618626L;

	private String resultCount;
	private String pagedResultsCookie;
	private String totalPagedResultsPolicy;
	private String totalPagedResults;
	private String remainingPagedResults;

	private Set<JaxOpenIdmResult> result = new HashSet<>();

	public String getResultCount() {
		return resultCount;
	}

	public void setResultCount(String resultCount) {
		this.resultCount = resultCount;
	}

	public String getPagedResultsCookie() {
		return pagedResultsCookie;
	}

	public void setPagedResultsCookie(String pagedResultsCookie) {
		this.pagedResultsCookie = pagedResultsCookie;
	}

	public String getTotalPagedResultsPolicy() {
		return totalPagedResultsPolicy;
	}

	public void setTotalPagedResultsPolicy(String totalPagedResultsPolicy) {
		this.totalPagedResultsPolicy = totalPagedResultsPolicy;
	}

	public String getTotalPagedResults() {
		return totalPagedResults;
	}

	public void setTotalPagedResults(String totalPagedResults) {
		this.totalPagedResults = totalPagedResults;
	}

	public String getRemainingPagedResults() {
		return remainingPagedResults;
	}

	public void setRemainingPagedResults(String remainingPagedResults) {
		this.remainingPagedResults = remainingPagedResults;
	}

	public Set<JaxOpenIdmResult> getResult() {
		return result;
	}

	public void setResult(Set<JaxOpenIdmResult> result) {
		this.result = result;
	}
}
