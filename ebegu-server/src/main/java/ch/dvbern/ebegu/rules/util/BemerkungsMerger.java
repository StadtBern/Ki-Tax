package ch.dvbern.ebegu.rules.util;

import ch.dvbern.ebegu.entities.VerfuegungZeitabschnitt;
import ch.dvbern.ebegu.types.DateRange;
import ch.dvbern.ebegu.util.Gueltigkeit;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SortedSetMultimap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.*;

/**
 * This class is supposed to find the longest timeperiods for which bemerkungen in {@link VerfuegungZeitabschnitt}en exists.
 * It takes a list of {@link VerfuegungZeitabschnitt} that has no overlap and returns a list of every bemerkungtext and its
 * continous ranges
 *
 * Example
 * <pre>
 * |AC  |AB |AC |
 * </pre>
 * where A,B and C are bemerkungen and the | | represent date ranges
 * is turned into to
 * A: 1-3
 * B: 2
 * C: 1,3
 */
public class BemerkungsMerger {
	private static final Logger LOG = LoggerFactory.getLogger(BemerkungsMerger.class);

	/**
	 * prints a string in the format "[dateFrom -d dateTo] bemerkungstext\n" for every zeitabschnitt in the list.
	 * It returns a newline separated String
	 */
	@SuppressWarnings("SimplifyStreamApiCallChains")
	public static String evaluateBemerkungenForVerfuegung(List<VerfuegungZeitabschnitt> zeitabschnitte) {
		if (zeitabschnitte == null || zeitabschnitte.isEmpty()) {
			return null;
		}

		StringJoiner joiner = new StringJoiner("\n");
		Map<String, Collection<DateRange>> rangesByBemerkungKey = evaluateRangesByBemerkungKey(zeitabschnitte);

		for (Map.Entry<String, Collection<DateRange>> stringCollectionEntry : rangesByBemerkungKey.entrySet()) {
			stringCollectionEntry.getValue().stream()
				.forEachOrdered(dateRange -> joiner.add("[" + dateRange.toRangeString() +"] " +  stringCollectionEntry.getKey()));
		}

		return joiner.toString();
	}

	/**
	 * analyzes the passed list of zeitabschnitte and finds the longest countinous date-ranges of a unique bemerkung.
	 *
	 * @param zeitabschnitte list to analyze
	 * @return a map with the bemerkung as key and the longest contious ranges as values
	 */
	public static Map<String, Collection<DateRange>> evaluateRangesByBemerkungKey(List<VerfuegungZeitabschnitt> zeitabschnitte) {

		SortedSetMultimap<String, Gueltigkeit> multimap = createMultimap(zeitabschnitte);
		Map<String, Collection<DateRange>> continousRangesPerKey = new HashMap<>();
			multimap.keySet().forEach(bemKey -> {
				Collection<DateRange> contRanges = mergeAdjacentRanges(multimap.get(bemKey));
				continousRangesPerKey.put(bemKey, contRanges);
			});

		return continousRangesPerKey;
	}



	private static Collection<DateRange> mergeAdjacentRanges(@Nullable SortedSet<Gueltigkeit> gueltigkeiten) {
		if (gueltigkeiten == null) {
			return Collections.emptyList();
		}
		Deque<DateRange> rangesWithoutGaps = new LinkedList<>();

		//noinspection SimplifyStreamApiCallChains
		gueltigkeiten.stream()
			.forEachOrdered(gueltigkeit -> {
				if (rangesWithoutGaps.isEmpty()) {
					rangesWithoutGaps.add(new DateRange(gueltigkeit.getGueltigkeit()));
				} else {

					LocalDate lastEndingDate = rangesWithoutGaps.getLast().getGueltigBis();
					//if the periods are adjacent make the existing period longer
					if (lastEndingDate.plusDays(1).equals(gueltigkeit.getGueltigkeit().getGueltigAb())) {
						DateRange longerRange = new DateRange(rangesWithoutGaps.getLast().getGueltigAb(), gueltigkeit.getGueltigkeit().getGueltigBis());
						rangesWithoutGaps.removeLast();
						rangesWithoutGaps.addLast(longerRange);
						//if there is a gap add the new period
					} else if (lastEndingDate.plusDays(1).isBefore(gueltigkeit.getGueltigkeit().getGueltigAb())) {
						rangesWithoutGaps.add(new DateRange(gueltigkeit.getGueltigkeit()));
					//this should not happen since the evaluator is supposed to eliminate gaps
					} else if (lastEndingDate.equals(gueltigkeit.getGueltigkeit().getGueltigAb()) || lastEndingDate.isAfter(gueltigkeit.getGueltigkeit().getGueltigAb())) {
						LOG.error("The passed list of gueltigkeiten must be ordered and may not have any overlapping" +
							" gueltigkeiten around date {}. The offending gueltigkeiten are {} and {}", lastEndingDate, rangesWithoutGaps.getLast(), gueltigkeit);
						throw new IllegalArgumentException("The passed list of gueltigkeiten may not have any overlap");
					}

				}
			});
		return rangesWithoutGaps;
	}


	private static SortedSetMultimap<String, Gueltigkeit> createMultimap(List<VerfuegungZeitabschnitt> zeitabschnitte) {
		SortedSetMultimap<String, Gueltigkeit> multimap = Multimaps.newSortedSetMultimap(new HashMap<>(), () -> new TreeSet<>(Gueltigkeit.GUELTIG_AB_COMPARATOR));
		for (VerfuegungZeitabschnitt verfuegungZeitabschnitt : zeitabschnitte) {
			if (StringUtils.isNotEmpty(verfuegungZeitabschnitt.getBemerkungen())) {
				//hier bemerkungen des zeitabschnitt vorher noch splitten anhand /n

				String[] split = verfuegungZeitabschnitt.getBemerkungen().split("\\n");
				for (String currBemerkung : split) {
					multimap.put(currBemerkung, verfuegungZeitabschnitt);

				}
			}
		}
		return multimap;
	}
}
