package ch.dvbern.ebegu.api.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

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
