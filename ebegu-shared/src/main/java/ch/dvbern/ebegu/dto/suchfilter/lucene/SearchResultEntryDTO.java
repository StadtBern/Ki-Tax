package ch.dvbern.ebegu.dto.suchfilter.lucene;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import ch.dvbern.ebegu.dto.JaxAbstractAntragDTO;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Resultatdto fuer ein einzelnes Resultat aus dem Lucene index
 */
public class SearchResultEntryDTO implements Serializable {

	private static final long serialVersionUID = 1633427122097823502L;

	@Nonnull
	private final SearchEntityType entity;
	@Nonnull
	private final String resultId;
	@Nonnull
	private final String text;
	@Nullable
	private final String additionalInformation;
	@Nullable
	private String gesuchID;
	@Nullable
	private String fallID;

	@Nullable
	private JaxAbstractAntragDTO antragDTO; //dto mit detailinfos


	public SearchResultEntryDTO(
		@Nonnull SearchEntityType entity,
		@Nonnull String resultId,
		@Nonnull String text,
		@Nullable String additionalInformation,
		@Nullable String gesuchID,
		@Nullable String fallID) {

		this.entity = entity;
		this.resultId = resultId;
		this.text = text;
		this.additionalInformation = additionalInformation;
		this.gesuchID = gesuchID;
		this.fallID = fallID;
	}

	@Nonnull
	public SearchEntityType getEntity() {
		return entity;
	}

	@Nonnull
	public String getResultId() {
		return resultId;
	}

	@Nonnull
	public String getText() {
		return text;
	}

	@Nullable
	public String getAdditionalInformation() {
		return additionalInformation;
	}

	@Nullable
	@SuppressFBWarnings("NM_CONFUSING")
	public String getGesuchID() {
		return gesuchID;
	}

	@SuppressFBWarnings("NM_CONFUSING")
	public void setGesuchID(@Nullable String gesuchID) {
		this.gesuchID = gesuchID;
	}

	@Nullable
	public String getFallID() {
		return fallID;
	}

	public void setFallID(@Nullable String fallID) {
		this.fallID = fallID;
	}

	public static List<SearchResultEntryDTO> convertSearchResult(SearchFilter filter, List<Searchable> results) {
		return results.stream().map(result -> new SearchResultEntryDTO(
			filter.getSearchEntityType(),
			result.getSearchResultId(),
			result.getSearchResultSummary(),
			result.getSearchResultAdditionalInformation(),
			result.getOwningGesuchId(),
			result.getOwningFallId())
		)
			.collect(Collectors.toList());
	}

	@Nullable
	public JaxAbstractAntragDTO getAntragDTO() {
		return antragDTO;
	}

	public void setAntragDTO(@Nullable JaxAbstractAntragDTO antragDTO) {
		this.antragDTO = antragDTO;
	}
}
