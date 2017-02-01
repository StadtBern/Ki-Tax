package ch.dvbern.ebegu.dto.suchfilter.lucene;

import ch.dvbern.ebegu.dto.JaxAntragDTO;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

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
	private JaxAntragDTO antragDTO; //dto mit detailinfos


	public SearchResultEntryDTO(
		@Nonnull SearchEntityType entity,
		@Nonnull String resultId,
		@Nonnull String text,
		@Nullable String additionalInformation,
		@Nullable String gesuchID) {

		this.entity = entity;
		this.resultId = resultId;
		this.text = text;
		this.additionalInformation = additionalInformation;
		this.gesuchID = gesuchID;
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

	public String getGesuchID() {
		return gesuchID;
	}

	public void setGesuchID(@Nullable String gesuchID) {
		this.gesuchID = gesuchID;
	}

	public static List<SearchResultEntryDTO> convertSearchResult(SearchFilter filter, List<Searchable> results) {
		return results.stream().map(f -> new SearchResultEntryDTO(
			filter.getSearchEntityType(),
			f.getSearchResultId(),
			f.getSearchResultSummary(),
			f.getSearchResultAdditionalInformation(),
			f.getOwningGesuchId())
		)
			.collect(Collectors.toList());
	}

	@Nullable
	public JaxAntragDTO getAntragDTO() {
		return antragDTO;
	}

	public void setAntragDTO(@Nullable JaxAntragDTO antragDTO) {
		this.antragDTO = antragDTO;
	}
}
