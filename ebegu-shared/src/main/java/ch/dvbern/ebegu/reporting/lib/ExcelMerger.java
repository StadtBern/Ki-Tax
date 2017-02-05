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

import com.google.common.base.MoreObjects;
import org.apache.commons.io.IOUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ch.dvbern.ebegu.util.MonitoringUtil.monitor;
import static com.google.common.base.Preconditions.checkNotNull;

public final class ExcelMerger {

	private static final Logger LOG = LoggerFactory.getLogger(ExcelMerger.class);

	private static final Pattern MERGEFIELD_REX = Pattern.compile(".*(\\{([a-zA-Z1-9]+)(:(\\d))?\\}).*");
	private static final int REX_GROUP_PATTERN = 1;
	private static final int REX_GROUP_KEY = 2;
	private static final int REF_GROUP_ROWS = 4;
	// nur ein willkuerlicher Counter, damit's kein while(true) geben muss
	private static final int MAX_PLACEHOLDERS_PER_CELL = 10;

	private ExcelMerger() {
		// utliity class
	}

	@FunctionalInterface
	private interface TetraConsumer<T, U, V, S> {
		void accept(T a, U b, V c, S s) throws ExcelMergeException;
	}

	@FunctionalInterface
	interface GroupMerger extends TetraConsumer<ExcelMerger.Context, ExcelMerger.GroupPlaceholder, List<ExcelMergerDTO>, Row> {
	}


	@Nonnull
	private static InputStream toSeekable(@Nonnull InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		IOUtils.copy(is, baos);
		baos.flush();
		ByteArrayInputStream bis = new ByteArrayInputStream(baos.toByteArray());
		return bis;
	}

	@Nonnull
	public static Workbook createWorkbookFromTemplate(@Nonnull InputStream is) throws ExcelMergeException {
		Objects.requireNonNull(is);

		try {
			InputStream poiCompatibleIS = toSeekable(is);
			// POI braucht einen Seekable InputStream
			Workbook workbook = WorkbookFactory.create(poiCompatibleIS);
			return workbook;

		} catch (IOException | InvalidFormatException | RuntimeException e) {
			throw new ExcelMergeException("Error parsing template", e);
		}
	}

	static class Placeholder {
		@Nonnull
		private final Cell cell;
		@Nonnull
		private final String pattern;
		@Nonnull
		private final String key;
		@Nonnull
		private final MergeField field;

		Placeholder(@Nonnull Cell cell, @Nonnull String pattern, @Nonnull String key, @Nonnull MergeField field) {
			this.cell = checkNotNull(cell);
			this.pattern = checkNotNull(pattern);
			this.key = checkNotNull(key);
			this.field = field;
		}

		@Nonnull
		public Cell getCell() {
			return cell;
		}

		@Nonnull
		public String getPattern() {
			return pattern;
		}

		@Nonnull
		public String getKey() {
			return key;
		}

		@Nonnull
		public MergeField getField() {
			return field;
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this).add("pattern", pattern).add("key", key).add("field", field).toString();
		}
	}

	static class GroupPlaceholder extends Placeholder {
		private final int rows;

		GroupPlaceholder(@Nonnull Cell cell, @Nonnull String pattern, @Nonnull String key, @Nonnull MergeField field, @Nullable Integer rowsParsed) {
			super(cell, pattern, key, field);
			this.rows = rowsParsed == null ? 1 : rowsParsed;
		}

		public int getRows() {
			return rows;
		}

		@Override
		@Nonnull
		public String toString() {
			return MoreObjects.toStringHelper(this)
				.add("pattern", getPattern())
				.add("key", getKey())
				.add("field", getField())
				.add("rows", rows)
				.toString();
		}

	}

	static class Context {
		@Nonnull
		private final Workbook workbook;
		@Nonnull
		private final Sheet sheet;
		@Nonnull
		private final Map<String, MergeField> mergeFields;

		private int currentRow = 0;

		Context(@Nonnull Workbook workbook, @Nonnull Sheet sheet, @Nonnull Map<String, MergeField> mergeFields) {
			this(workbook, sheet, mergeFields, sheet.getFirstRowNum());
		}

		Context(@Nonnull Workbook workbook, @Nonnull Sheet sheet, @Nonnull Map<String, MergeField> mergeFields, int startRow) {
			this.workbook = checkNotNull(workbook);
			this.sheet = checkNotNull(sheet);
			this.mergeFields = checkNotNull(mergeFields);
			this.currentRow = startRow;
		}

		@Nonnull
		public Workbook getWorkbook() {
			return workbook;
		}

		@Nonnull
		public Sheet getSheet() {
			return sheet;
		}

		public int currentRowNum() {
			return currentRow;
		}

		@Nonnull
		public Row currentRow() {
			Row row = sheet.getRow(currentRowNum());
			if (row == null) {
				row = sheet.createRow(currentRowNum());
			}
			return row;
		}

		public int advanceRow() {
			currentRow++;
			return currentRow;
		}

		@Nullable
		public GroupPlaceholder detectGroup() {
			Row row = currentRow();

			// von hinten nach vorne durcharbeiten
			for (int i = Math.max(row.getLastCellNum(), 0); i >= Math.max(row.getFirstCellNum(), 0); i--) {
				Cell cell = row.getCell(i);
				Placeholder placeholder = parsePlaceholder(cell);
				if (placeholder instanceof GroupPlaceholder) {
					return (GroupPlaceholder) placeholder;
				}
			}

			return null;
		}

		@Nullable
		public Placeholder parsePlaceholder(@Nullable Cell cell) {
			if (cell == null || cell.getCellType() != Cell.CELL_TYPE_STRING) {
				return null;
			}

			Matcher matcher = MERGEFIELD_REX.matcher(cell.getStringCellValue());
			if (!matcher.matches()) {
				return null;
			}

			String pattern = matcher.group(REX_GROUP_PATTERN);
			String key = matcher.group(REX_GROUP_KEY);
			Integer groupRows = matcher.group(REF_GROUP_ROWS) != null ? Integer.valueOf(matcher.group(REF_GROUP_ROWS), 10) : null;
			MergeField field = mergeFields.get(key);
			if (field == null) {
				return null;
			}

			if (field.getType() == MergeField.Type.REPEAT_ROW) {
				return new GroupPlaceholder(cell, pattern, key, field, groupRows);
			}

			return new Placeholder(cell, pattern, key, field);
		}

	}

	private static void mergeRow(@Nonnull Context ctx, @Nonnull ExcelMergerDTO data) {
		Row row = ctx.currentRow();

		Map<MergeField, Integer> valueOffsets = new HashMap<>();

		for (int colNum = Math.max(row.getFirstCellNum(), 0); colNum <= Math.max(row.getLastCellNum(), 0); colNum++) {
			Cell cell = row.getCell(colNum);
			if (cell == null) {
				continue;
			}

			for (int i = 0; i < MAX_PLACEHOLDERS_PER_CELL; i++) {
				Placeholder placeholder = ctx.parsePlaceholder(cell);
				if (placeholder == null) {
					break; // gibt keine Placeholder, da kann sofort abgebrochen werden
				}

				MergeField field = placeholder.getField();

				if (!field.getType().doMergeValue()) {
					break;
				}

				Integer valueOffset = 0;
				if (field.getType().doConsumeValue()) {
					valueOffsets.compute(field, (key, oldVal) -> oldVal == null ? 0 : oldVal + 1);
					valueOffset = valueOffsets.get(field);
				}
				if (data.hasValue(field, valueOffset)) {
					Serializable value = data.getValue(field, valueOffset);
					field.getConverter().setCellValue(cell, placeholder.getPattern(), value);
				} else {
					field.getConverter().setCellValue(cell, placeholder.getPattern(), null);
					// spalte ausblenden
					if (field.getType().doHideColumnOnEmpty()) {
						ctx.getSheet().setColumnHidden(cell.getColumnIndex(), true);
					}
				}
			}
		}
	}

	private static void mergeGroup(@Nonnull Context ctx, @Nonnull List<ExcelMergerDTO> groupRows, int rowSize) throws ExcelMergeException {
		for (ExcelMergerDTO dto : groupRows) {
			for (int rowNum = 0; rowNum < rowSize; rowNum++) {
				try {
					Row row = ctx.currentRow();

					GroupPlaceholder group = ctx.detectGroup();
					if (group == null) {
						mergeRow(ctx, dto);
						ctx.advanceRow();
					} else {
						mergeGroup(ctx, group, dto, row, ExcelMerger::mergeSubGroup);
					}
				} catch (RuntimeException rte) {
					throw new ExcelMergeException("Caught error in sheet " + ctx.getSheet().getSheetName() + " on row/col: " + ctx.currentRowNum(), rte);
				}
			}

		}
	}

	static void mergeGroup(@Nonnull Context ctx, @Nonnull GroupPlaceholder group, @Nonnull ExcelMergerDTO dto, @Nonnull Row currentRow, @Nonnull GroupMerger merger) throws ExcelMergeException {
		List<ExcelMergerDTO> subGroups = dto.getGroup(group.getField());
		group.getCell().setCellValue((String) null); // Group-Repeat-Info aus der Zelle loeschen
		if (subGroups == null) {
			//LOG.warn("Keine Gruppendaten gefunden fuer group: {}", group.getPattern());
			mergeRow(ctx, dto);
			ctx.advanceRow();
		} else {
			merger.accept(ctx, group, subGroups, currentRow);
		}
	}

	static void mergeSubGroup(@Nonnull Context ctx, @Nonnull GroupPlaceholder group, @Nonnull List<ExcelMergerDTO> subGroups, @Nonnull Row currentRow) throws ExcelMergeException {
		duplicateRowsWithStylesMultipleRowShift(ctx, currentRow, group.getRows(), subGroups.size());
		mergeGroup(ctx, subGroups, group.getRows());
	}

	/**
	 * Dupliziert Rows:
	 * 1. Platz machen fuer die neuen Rows (i.E.: shift rows)
	 * 2. Zellen inkl. Styles kopieren
	 * 3. Ggf. Named-Ranges um die neuen Zeilen erweitern
	 */
	private static void duplicateRowsWithStylesMultipleRowShift(@Nonnull Context ctx, @Nonnull Row startRow, @Nonnull Integer anzSrcRows, @Nonnull Integer anzGroups) {
		monitor(ExcelMerger.class, "duplicateRowsWithStylesMultipleRowShift(numGroups=" + anzGroups + ')', () -> {
			final int startNeuerBereich = startRow.getRowNum() + anzSrcRows;
			final int anzRows = anzSrcRows * (anzGroups - 1);

			// Wenns nach dem zu duplizierenden Bereich noch Zeilen hat: nach unten wegschieben
			if (anzRows > 0 && startNeuerBereich <= ctx.getSheet().getLastRowNum()) {
				PoiUtil.shiftRowsAndMergedRegions(ctx.getSheet(), startNeuerBereich, ctx.getSheet().getLastRowNum() + 1, anzRows);
				PoiUtil.shiftNamedRanges(ctx.getSheet(), startRow.getRowNum(), ctx.getSheet().getLastRowNum() + 1, anzRows);
			}

			// Kopieren
			monitor(ExcelMerger.class, "duplicateRowsWithStylesMultipleRowShift(numGroups=" + anzGroups + ").copyRows", () -> {
				for (int rowNum = 0; rowNum < anzSrcRows; rowNum++) {
					Row srcRow = getRow(ctx.getSheet(), startRow.getRowNum() + rowNum);

					for (int i = 0; i < anzGroups - 1; i++) {
						int startGroup = startNeuerBereich + i * anzSrcRows;
						Row newRow = getRow(ctx.getSheet(), startGroup + rowNum);

						copyStyles(srcRow, newRow);
					}
				}
			});
		});
	}

	private static void copyStyles(@Nonnull Row srcRow, @Nonnull Row newRow) {
		for (int cellNum = 0; cellNum < srcRow.getLastCellNum(); cellNum++) {
			Cell srcCell = srcRow.getCell(cellNum);
			if (srcCell != null) {
				Cell newCell = getCell(newRow, cellNum);
				newCell.setCellStyle(srcCell.getCellStyle());
				switch (srcCell.getCellType()) {
				case Cell.CELL_TYPE_STRING:
					newCell.setCellValue(srcCell.getStringCellValue());
					break;
				case Cell.CELL_TYPE_FORMULA:
					newCell.setCellFormula(srcCell.getCellFormula());
					break;
				case Cell.CELL_TYPE_BLANK:
					// nop
					break;
				default:
					LOG.warn("Cell type not supported: {} @{}/{}", srcCell.getCellType(), srcCell.getRowIndex(), srcCell.getColumnIndex());
				}
			}
		}
	}

	/**
	 * Fuellt ein Excel-Sheet mit den uebergebenen Daten aus.
	 * Das Sheet wird in Repeat-Gruppen aufgeteilt, die auch verschachtelt sein koennen.
	 * Repeat-Gruppen-Bezeichner ('z.B. {myRepeat}') muessen ein Feld vom Typ {@link MergeField.Type#REPEAT_ROW} sein.
	 * Normale Felder - (also 1 Wert pro Repeat-Gruppe) sind vom Typ {@link MergeField.Type#SIMPLE}.
	 * Spalten-Repeater sind vom Typ {@link MergeField.Type#REPEAT_COL}. Findet sich in den Daten nicht ausreichend Werte, werden die Spalten ausgeblendet.
	 * Nuetzlich z.B. in Ueberschriften
	 * Werte-Repeater gehoeren zu Spalten-Repeater und sind die Daten zur Ueberschrift. Sie unterscheiden sich zu Spalten-Repeatern dadurch, das sie keine Spalten ausblenden.
	 * => Spalten-Repeater legen die anzahl sichtbarer Spalten (und ggf. defen Ueberschrift) fest und Werte-Repeater sind die dazugehoerigen Daten die m.o.w. vollstaendig sind.
	 */
	public static void mergeData(@Nonnull Sheet sheet, @Nonnull MergeField fields[], @Nonnull ExcelMergerDTO excelMergerDTO) throws ExcelMergeException {
		Objects.requireNonNull(sheet);
		Objects.requireNonNull(fields);
		Objects.requireNonNull(excelMergerDTO);

		Map<String, MergeField> fieldMap = new HashMap<>();
		Arrays.asList(fields)
			.forEach(field -> fieldMap.put(field.getKey(), field));

		Workbook wb = sheet.getWorkbook();
		Context ctx = new Context(wb, sheet, fieldMap);

		monitor(ExcelMerger.class, "mergeData.mergeSheet(" + sheet.getSheetName() + ')',
			() -> mergeGroup(ctx, Collections.singletonList(excelMergerDTO), sheet.getLastRowNum() + 1));

		monitor(ExcelMerger.class, "mergeData.evalFormulas(" + sheet.getSheetName() + ')', () -> {
			FormulaEvaluator eval = wb.getCreationHelper().createFormulaEvaluator();
			eval.clearAllCachedResultValues();
			eval.evaluateAll();
		});
	}

	@Nonnull
	private static Row getRow(@Nonnull Sheet sheet, int index) {
		Row row = sheet.getRow(index);
		return row == null ? sheet.createRow(index) : row;
	}

	@Nonnull
	private static Cell getCell(@Nonnull Row row, int column) {
		Cell cell = row.getCell(column);
		return cell == null ? row.createCell(column) : cell;
	}

}
