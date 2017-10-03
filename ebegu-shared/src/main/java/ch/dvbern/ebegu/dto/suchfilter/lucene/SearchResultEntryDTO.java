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
