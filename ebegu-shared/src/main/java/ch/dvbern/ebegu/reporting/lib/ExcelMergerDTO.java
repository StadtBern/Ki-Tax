/*
 * Copyright (c) 2015 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */
package ch.dvbern.ebegu.reporting.lib;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class ExcelMergerDTO implements Serializable {

	private static final long serialVersionUID = -3046815212636566797L;
	/**
	 * Zellen, die im globalen Teil des Excel wiederholt werden (z.B. Ueberschriften mit Firmennamen)
	 */
	@Nonnull
	private final Map<MergeField, List<Serializable>> values = new HashMap<>();
	private final Map<MergeField, List<ExcelMergerDTO>> groups = new HashMap<>();

	public ExcelMergerDTO() {
	}

	@Nonnull
	public ExcelMergerDTO createGroup(@Nonnull MergeField group) {
		checkNotNull(group);
		List<ExcelMergerDTO> entries = groups.computeIfAbsent(group, (key) -> new LinkedList<>());
		ExcelMergerDTO newGroup = new ExcelMergerDTO();
		entries.add(newGroup);
		return newGroup;
	}

	private void addValueInternal(@Nonnull MergeField mergeField, @Nullable Serializable value) {
		checkNotNull(mergeField);
		List<Serializable> valuesList = values.computeIfAbsent(mergeField, (key) -> new LinkedList<>());
		valuesList.add(value);
	}

	public void addValue(@Nonnull MergeField mergeField, @Nullable String value) {
		addValueInternal(mergeField, value);
	}

	public void addValue(@Nonnull MergeField mergeField, @Nullable LocalDateTime value) {
		addValueInternal(mergeField, value);
	}

	public void addValue(@Nonnull MergeField mergeField, @Nullable LocalDate value) {
		addValueInternal(mergeField, value);
	}

	public void addValue(@Nonnull MergeField mergeField, @Nullable BigDecimal value) {
		addValueInternal(mergeField, value);
	}

	public void addValue(@Nonnull MergeField mergeField, @Nullable Boolean value) {
		addValueInternal(mergeField, value);
	}

	public void addValue(@Nonnull MergeField mergeField, @Nullable Integer value) {
		addValueInternal(mergeField, value);
	}

	public List<ExcelMergerDTO> getGroup(@Nonnull MergeField groupField) {
		return groups.get(groupField);
	}

	@Nullable
	public Serializable getValue(@Nonnull MergeField mergeField) {
		return getValue(mergeField, 0);
	}

	public boolean hasValue(@Nonnull MergeField mergeField, int valueOffset) {
		List<Serializable> serializables = values.get(mergeField);
		if (serializables == null) {
			return false;
		}

		return valueOffset < serializables.size();
	}

	@Nullable
	public Serializable getValue(@Nonnull MergeField mergeField, int valueOffset) {
		List<Serializable> serializables = values.get(mergeField);
		if (serializables == null) {
			return null;
		}

		if (valueOffset < serializables.size()) {
			return serializables.get(valueOffset);
		}
		return null;

	}

}
