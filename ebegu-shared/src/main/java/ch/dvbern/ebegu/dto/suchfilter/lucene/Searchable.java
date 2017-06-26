package ch.dvbern.ebegu.dto.suchfilter.lucene;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Interface welches von allen Indizierten Entities implementiert wird. Auf diese weise koennen diese auf einen
 * gemeinsamen nenner gebracht werden
 */
public interface Searchable {

	/**
	 * @return Liefert die Unique-ID dieser Entity.
	 */
	@Nonnull
	String getSearchResultId();

	/**
	 * @return Dieser Text wird in den Suchergebnissen zur Volltextsuche angezeigt.
	 */
	@Nonnull
	String getSearchResultSummary();

	/**
	 * @return Dieser Text wird in den Suchergebnissen zur Volltextsuche als Zusatz angezeigt.
	 */
	@Nullable
	String getSearchResultAdditionalInformation();

	/**
	 * @return Liefert die Unique_ID des Gesuchs zu dem dieses Entity gehoert. Kann null sein wenn es keine direkte verknuepfung gibt. In diesem Fall muss die gesuchID per query emittelt werden :(
	 */
	@Nullable
	String getOwningGesuchId();

}
