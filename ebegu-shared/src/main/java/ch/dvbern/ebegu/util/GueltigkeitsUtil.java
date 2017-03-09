/*
 * Copyright (c) 2015 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */
package ch.dvbern.ebegu.util;


import ch.dvbern.ebegu.types.DateRange;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

public final class GueltigkeitsUtil {

	private GueltigkeitsUtil() {
		// utliity class
	}

	/**
	 * Aktualisiert gueltigAb/gueltigBis eines neuen Entities und gegebenenfalls der Collection von existingEntities.
	 *
	 * <p>Definition neachst-gueltiges Entity: min(entity.gueltigAb) where entity.gueltigAb > newEntity.gueltigAb und entity in existingEntities.</p>
	 * <p>Definition vorher-gueltiges Entity: max(entity.gueltigAb) where entity.gueltigAb < newEntity.gueltigAb und entity in existingEntities.</p>
	 * <ol>
	 *     <li>Falls es ein existing Entity gibt mit gueltigAb = newEntity.gueltigAb,
	 *     so werden die Properties von newEntity in existing Entity gemerged und existing Entity zurueck gegeben.
	 *     Der Merge Prozess ist durch die mergeFunction definiert.</li>
	 *
	 *     <li>gueltigBis von newEntity wird reduziert auf (gueltigAb - 1) des naechst-gueltigen Entities.
	 *     Falls es kein nachst-gueltiges Entity gibt, so ist gueltigAb = END_OF_TIME (unlimitiert).</li>
	 *
	 *     <li>Falls es ein vorher-gueltiges Entity gibt, welches gueltigBis > newEntity.gueltigAb,
	 *     so wird gueltigBis des vorher-gueltigen Entities reduziert auf (newEntity.gueltigAb - 1).</li>
	 * </ol>
	 */
	@Nonnull
	public static <T extends Gueltigkeit> T updateGueltigkeit(@Nonnull final Collection<? extends T> existingEntities, @Nonnull T newEntity, @Nonnull BiFunction<T, T, T> mergeFunction) {
		LocalDate gueltigAb = newEntity.getGueltigkeit().getGueltigAb();
		newEntity.getGueltigkeit().setGueltigBis(Constants.END_OF_TIME);

		List<T> laterOrEqual = findLaterOrEqual(existingEntities, gueltigAb);

		if (!laterOrEqual.isEmpty() && laterOrEqual.get(0).getGueltigkeit().getGueltigAb().equals(gueltigAb)) {
			T sameGueltigAb = laterOrEqual.get(0);
			// Keine neues Entity anlegen, falls gueltigAb identisch ist
			return mergeFunction.apply(newEntity, sameGueltigAb);
		}

		List<T> earlier = findEarlier(existingEntities, gueltigAb);
		if (!earlier.isEmpty()) {
			T earlierEntity = earlier.get(0);
			LocalDate earlierGueltigBis = earlierEntity.getGueltigkeit().getGueltigBis();
			if (!earlierGueltigBis.isBefore(gueltigAb)) {
				// GueltigBis eines vorgehenden Entities anpassen
				earlierEntity.getGueltigkeit().setGueltigBis(gueltigAb.minusDays(1));
				newEntity.getGueltigkeit().setGueltigBis(earlierGueltigBis);
			}
		}

		if (!laterOrEqual.isEmpty() && newEntity.getGueltigkeit().getGueltigBis().isEqual(Constants.END_OF_TIME)) {
			// Falls es ein nachgehendes Entity gibt gueltigBis des neuen Entities beschraenken
			newEntity.getGueltigkeit().setGueltigBis(laterOrEqual.get(0).getGueltigkeit().getGueltigAb().minusDays(1));
		}

		return newEntity;
	}



	/**
	 * Verlängert das gueltigBis Datum von {@code updateEntity} unter berücksichtigung von {@code existingEntities},
	 * so dass gueltigBis = gueltigAb - 1 Tag des nächst gültigen Entities ist.
	 * Falls es kein nächst gültiges Entity gibt, so ist gueltigBis = END_OF_TIME
	 * @return TRUE, falls updateEntity modifiziert wurde, FALSE otherwise
	 */
	public static <T extends Gueltigkeit> boolean extendGueltigkeit(@Nonnull final Collection<? extends T> existingEntities, @Nonnull final T updateEntity) {
		LocalDate maxGueltigBis = GueltigkeitsUtil.getMaxGueltigBis(existingEntities, updateEntity);

		if (maxGueltigBis.equals(updateEntity.getGueltigkeit().getGueltigBis())) {
			return false;
		}

		updateEntity.getGueltigkeit().setGueltigBis(maxGueltigBis);
		return true;
	}

	/**
	 * @return END_OF_TIME oder gueltigAb - 1 Tag des nächsten gültigen Entities
	 */
	@Nonnull
	public static <T extends Gueltigkeit> LocalDate getMaxGueltigBis(@Nonnull final Collection<? extends T> existingEntities, @Nonnull final T updateEntity) {
		LocalDate gueltigBis = updateEntity.getGueltigkeit().getGueltigBis();

		if (gueltigBis.equals(Constants.END_OF_TIME)) {
			return Constants.END_OF_TIME;
		}

		List<T> later = findLaterOrEqual(existingEntities, gueltigBis.plusDays(1));
		if (later.isEmpty()) {
			return Constants.END_OF_TIME;
		}

		return later.get(0).getGueltigkeit().getGueltigAb().minusDays(1);
	}


	/**
	 * @return letzt gueltiges Entity in der Collection. Falls die Collection leer ist, wird ein Optional.empty zurueck gegeben.
	 */
	@Nonnull
	public static <T extends Gueltigkeit> Optional<T> findLast(@Nonnull Collection<T> existingEntities) {
		return existingEntities.stream().sorted(Gueltigkeit.GUELTIG_AB_COMPARATOR.reversed()).findFirst();
	}

	/**
	 * @return absteigend sortiert nach Gueltigkeitsdatum
	 */
	@Nonnull
	public static <T extends Gueltigkeit> List<T> findEarlier(@Nonnull Collection<? extends T> existingEntities, @Nonnull LocalDate stichtag) {
		return existingEntities.stream().filter(e -> e.getGueltigkeit().getGueltigAb().isBefore(stichtag)).sorted(Gueltigkeit.GUELTIG_AB_COMPARATOR.reversed()).collect(Collectors.toList());
	}

	/**
	 * @return falls zu {@code entity} ein direkt vor-angrenzendes Entity in exsitingEntities gefunden wird, so wird dieses zurueck gegeben
	 * Beispiel: |--A--|--B--|    |--C--|
	 * Die Collection umfasst 3 Entities mit DateRanges A, B und C.
	 * Wenn {@code entity} der DateRange B entspricht, so wird A zurueck gegeben.
	 * Wenn {@code entity} der DateRange C entspricht, so wird Empty zurueck gegeben.
	 */
	@Nonnull
	public static <T extends Gueltigkeit> Optional<T> findPreceding(@Nonnull Collection<? extends T> existingEntities, @Nonnull T entity) {
		List<T> earlier = GueltigkeitsUtil.findEarlier(existingEntities, entity.getGueltigkeit().getGueltigAb());
		if (!earlier.isEmpty() && earlier.get(0).getGueltigkeit().endsDayBefore(entity.getGueltigkeit())) {
			return Optional.ofNullable(earlier.get(0));
		}
		return Optional.empty();
	}

	/**
	 * @return falls zu {@code entity} ein direkt danach angrenzendes Entity in exsitingEntities gefunden wird, so wird dieses zurueck gegeben
	 * Beispiel: |--A--|--B--|    |--C--|
	 * Die Collection umfasst 3 Entities mit DateRanges A, B und C.
	 * Wenn {@code entity} der DateRange B entspricht, so wird Empty zurueck gegeben.
	 * Wenn {@code entity} der DateRange A entspricht, so wird B zurueck gegeben.
	 */
	@Nonnull
	public static <T extends Gueltigkeit> Optional<T> findFollowing(@Nonnull Collection<? extends T> existingEntities, @Nonnull T entity) {
		List<T> later = GueltigkeitsUtil.findLater(existingEntities, entity.getGueltigkeit().getGueltigBis());
		if (!later.isEmpty() && later.get(0).getGueltigkeit().startsDayAfter(entity.getGueltigkeit())) {
			return Optional.ofNullable(later.get(0));
		}
		return Optional.empty();
	}

	/**
	 * @return aufsteigend sortiert nach Gueltigkeitsdatum
	 */
	@Nonnull
	public static <T extends Gueltigkeit> List<T> findLater(@Nonnull Collection<? extends T> existingEntities, @Nonnull LocalDate stichtag) {
		return existingEntities.stream().filter(e -> e.getGueltigkeit().getGueltigAb().isAfter(stichtag)).sorted(Gueltigkeit.GUELTIG_AB_COMPARATOR).collect(Collectors.toList());
	}

	/**
	 * @return aufsteigend sortiert nach Gueltigkeitsdatum
	 */
	@Nonnull
	public static <T extends Gueltigkeit> List<T> findLaterOrEqual(@Nonnull Collection<? extends T> existingEntities, @Nonnull LocalDate stichtag) {
		return findLater(existingEntities, stichtag.minusDays(1));
	}

	/**
	 * @return aufsteigend sortiert nach Gueltigkeitsdatum bis zur 1. Gueltigkeits-Lücke
	 * Beispiel-Zeitverlauf mit 3 DateRanges A, B und C:
	 * <pre>
	 *     |--A--|--B--|    |--C--|
	 * </pre>
	 * Falls {@code stichtag} der DateRange A entspricht, so werden die Entities mit DateRanges A & B zurück gegeben,
	 * nicht aber das Entity mit Range C, da es zwischen DateRange B und C eine Lücke gibt.
	 */
	@Nonnull
	public static <T extends Gueltigkeit> List<T> findLaterOrEqualBisGueltigkeitsLuecke(@Nonnull final Collection<? extends T> existingEntities, @Nonnull final LocalDate stichtag) {
		List<T> laterOrEqual = findLaterOrEqual(existingEntities, stichtag);
		if (laterOrEqual.size() <= 1) {
			return laterOrEqual;
		}

		List<T> zusammenhaengendeEntities = new ArrayList<>();
		Iterator<T> iter = laterOrEqual.iterator();
		T current = iter.next();
		zusammenhaengendeEntities.add(current);
		while (iter.hasNext()) {
			T next = iter.next();
			if (!current.getGueltigkeit().endsDayBefore(next.getGueltigkeit())) {
				// Gueltigkeits-Luecke
				break;
			}
			current = next;
			zusammenhaengendeEntities.add(next);
		}
		return zusammenhaengendeEntities;
	}

	@Nonnull
	public static <T extends Gueltigkeit> Optional<T> findFirst(@Nonnull List<T> entities) {
		checkNotNull(entities);
		Optional<T> first = entities.stream()
			.collect(Collectors.reducing((a, b) -> a.getGueltigkeit().getGueltigAb().isBefore(b.getGueltigkeit().getGueltigAb()) ? a : b));
		return first;
	}

	@Nonnull
	public static Optional<DateRange> calcGueltigkeitsRange(@Nonnull Collection<? extends Gueltigkeit> entities) {
		checkNotNull(entities);

		return entities.stream().map(Gueltigkeit::getGueltigkeit).reduce((a, b) -> {
			LocalDate minGueltigAb = a.getGueltigAb().isBefore(b.getGueltigAb()) ? a.getGueltigAb() : b.getGueltigAb();
			LocalDate maxGueltigBis = a.getGueltigBis().isAfter(b.getGueltigBis()) ? a.getGueltigBis() : b.getGueltigBis();
			return new DateRange(minGueltigAb, maxGueltigBis);
		});
	}

	/**
	 * Lücke vorhanden, wenn Zeitraum zwischen zwei angrenzenden Entities > 1 Tag ist
	 */
	public static <T extends Gueltigkeit> boolean hasGap(@Nonnull List<T> entities) {
		if (entities.isEmpty()) {
			return false;
		}

		entities.sort(Gueltigkeit.GUELTIG_AB_COMPARATOR);

		Iterator<T> iter = entities.iterator();
		T current = iter.next();
		while (iter.hasNext()) {
			T next = iter.next();
			if (Period.between(current.getGueltigkeit().getGueltigBis(), next.getGueltigkeit().getGueltigAb().minusDays(1L)).getDays() > 0) {
				return true;
			}
			current = next;
		}
		return false;
	}

	/**
	 * @return TRUE, wenn die übergebenen Entities über den ganzen gueltigkeitsZeitraum definiert sind, sonst FALSE.
	 */
	public static <T extends Gueltigkeit> boolean isCoveringGueltigkeitszeitraum(@Nonnull List<T> gueltigkeitProviders, @Nonnull DateRange gueltigkeitsZeitraum) {
		//noinspection OptionalGetWithoutIsPresent
		return !gueltigkeitProviders.isEmpty() &&
			!GueltigkeitsUtil.hasGap(gueltigkeitProviders) &&
			GueltigkeitsUtil.calcGueltigkeitsRange(gueltigkeitProviders).get().contains(gueltigkeitsZeitraum);
	}
}
