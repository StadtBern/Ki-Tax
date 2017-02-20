/*
 * Copyright © 2016 DV Bern AG, Switzerland
 *
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschützt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulässig. Dies gilt
 * insbesondere für Vervielfältigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht übergeben, ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 */

package ch.dvbern.ebegu.reporting.lib;


import ch.dvbern.ebegu.reporting.lib.WorkbookCalculationException.ErrorCode;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.poi.ss.SpreadsheetVersion.EXCEL2007;

public final class PoiUtil {

	private static final Logger LOG = LoggerFactory.getLogger(PoiUtil.class);

	private PoiUtil() {
		// utility function
	}

	@Nullable
	public static Serializable getAnyValue(@Nonnull Cell cell, @Nonnull CellType cellType) {
		switch (cellType) {
		case BLANK:
			return null;
		case BOOLEAN:
			return cell.getBooleanCellValue();
		case ERROR:
			return cell.getErrorCellValue();
		case FORMULA:
			return getAnyValue(cell, cell.getCachedFormulaResultTypeEnum());
		case NUMERIC:
			return cell.getNumericCellValue();
		case STRING:
			return cell.getStringCellValue();
		default:
			throw new WorkbookCalculationException(new CellReference(cell), ErrorCode.CELLTYPE_NOT_SUPPORTED, "unknown");
		}
	}

	@Nullable
	public static Serializable getAnyValue(@Nonnull Cell cell) {
		Serializable value = getAnyValue(cell, cell.getCellTypeEnum());
		return value;
	}

	@Nullable
	public static LocalDate readDate(@Nonnull Cell cell) {
		try {
			Date date = cell.getDateCellValue();
			if (date == null) {
				return null;
			}

			return LocalDate.from(date.toInstant().atZone(ZoneId.systemDefault()));
		} catch (RuntimeException rte) {
			throw new WorkbookCalculationException(new CellReference(cell), ErrorCode.FORMAT_DATE, getAnyValue(cell), rte);
		}
	}

	@Nonnull
	public static LocalDate readDateNN(@Nonnull Cell cell) {
		LocalDate value = readDate(cell);
		if (value == null) {
			throw new WorkbookCalculationException(new CellReference(cell), ErrorCode.VALUE_IS_NULL, getAnyValue(cell));
		}
		return value;
	}

	@Nonnull
	public static BigDecimal readDecimal(@Nonnull Cell cell, int scale) {
		try {
			double value = cell.getNumericCellValue();
			BigDecimal unrounded = BigDecimal.valueOf(value);
			BigDecimal rounded = unrounded.setScale(scale, BigDecimal.ROUND_HALF_UP);
			return rounded;
		} catch (RuntimeException rte) {
			throw new WorkbookCalculationException(new CellReference(cell), ErrorCode.FORMAT_NUMERIC, getAnyValue(cell), rte);
		}
	}

	public static int readInt(@Nonnull Cell cell) {
		BigDecimal value = readDecimal(cell, 0);
		try {
			return value.intValueExact();
		} catch (ArithmeticException ae) {
			throw new WorkbookCalculationException(new CellReference(cell), ErrorCode.FORMAT_INTEGER, getAnyValue(cell), ae);
		}
	}

	@Nonnull
	public static String readString(@Nonnull Cell cell) {
		try {
			String value = cell.getStringCellValue();
			return value;
		} catch (RuntimeException rte) {
			throw new WorkbookCalculationException(new CellReference(cell), ErrorCode.FORMAT_STRING, getAnyValue(cell), rte);
		}
	}

	public static boolean readBool(@Nonnull Cell cell) {
		try {
			boolean value = cell.getBooleanCellValue();
			return value;
		} catch (RuntimeException rte) {
			throw new WorkbookCalculationException(new CellReference(cell), ErrorCode.FORMAT_BOOLEAN, getAnyValue(cell), rte);
		}
	}

	/*@Nonnull
	public static KindergartenBelegung readKindergartenBelegung(@Nonnull Cell cell) {
		int excelValue = readInt(cell);
		KindergartenBelegung bel = KindergartenBelegung.fromLeistungsrechnungExcelValue(excelValue);
		if (bel == null) {
			throw new WorkbookCalculationException(new CellReference(cell), ErrorCode.FORMAT_KINDERGARTEN_BELEGUNG, getAnyValue(cell));
		}
		return bel;
	}

	public static void writekindergartenBelegung(@Nonnull Cell cell, @Nullable KindergartenBelegung value) {
		if (value == null) {
			cell.setCellType(CellType.BLANK);
			return;
		}

		writeInteger(cell, value.getLeistungsrechnungExcelValue());
	}*/

	public static void writeDate(@Nonnull Cell cell, @Nullable LocalDate value) {
		if (value == null) {
			cell.setCellType(CellType.BLANK);
		} else {
			Date date = Date.from(value.atStartOfDay(ZoneId.systemDefault()).toInstant());
			cell.setCellValue(date);
		}
	}

	public static void writeString(@Nonnull Cell cell, @Nullable String value) {
		if (StringUtils.isEmpty(value)) {
			cell.setCellType(CellType.BLANK);
		} else {
			cell.setCellValue(value);
		}
	}

	public static void writeInteger(@Nonnull Cell cell, @Nullable Integer value) {
		if (value == null) {
			cell.setCellType(CellType.BLANK);
		} else {
			cell.setCellValue(value.doubleValue());
		}
	}

	public static void writeDecimal(@Nonnull Cell cell, @Nullable BigDecimal value) {
		if (value == null) {
			cell.setCellType(CellType.BLANK);
		} else {
			cell.setCellValue(value.doubleValue());
		}
	}

	public static int getRowNumWithTextInFirstCell(@Nonnull Sheet sheet, @Nonnull String text) {
		for (Row row : sheet) {
			Cell cell = row.getCell(0);
			if (cell != null && cell.getCellTypeEnum() == CellType.STRING && text.equals(cell.getStringCellValue())) {
				return row.getRowNum();
			}
		}
		return -1;
	}

	public static void copyStylesFromSourceRow(@Nonnull Row sourceRow, @Nonnull Row targetRow, int cellCount) {
		for (int i = 0; i < cellCount; i++) {
			targetRow.getCell(i).setCellStyle(sourceRow.getCell(i).getCellStyle());
		}
	}

	@Nullable
	public static Cell getCellByReference(@Nonnull Sheet sheet, @Nonnull CellReference pos) {
		Row row = sheet.getRow(pos.getRow());
		return row.getCell(pos.getCol());
	}

	@Nonnull
	public static Cell getCellByReferenceChecked(@Nonnull Sheet sheet, @Nonnull CellReference pos) throws ExcelTemplateParseException {
		Cell cell = getCellByReference(sheet, pos);
		if (cell == null) {
			throw new ExcelTemplateParseException("Could not find referenced cell: " + pos);
		}
		return cell;
	}

	@Nonnull
	public static Optional<Cell> findCell(@Nonnull Sheet sheet, @Nonnull String title) {
		for (Row row : sheet) {
			Cell cell = row.getCell(0);
			if (cell != null && cell.getCellTypeEnum() == CellType.STRING && title.equals(cell.getStringCellValue())) {
				return Optional.of(cell);
			}
		}
		return Optional.empty();
	}

	/**
	 * Workaround für POI >= 3.15: Bei shiftRows gehen die MergedRegions (Cell-Verbindungen) verloren.
	 * @see <a href="https://bz.apache.org/bugzilla/show_bug.cgi?id=60384">https://bz.apache.org/bugzilla/show_bug.cgi?id=60384</a>
	 */
	public static void shiftRowsAndMergedRegions(@Nonnull Sheet sheet, int startRow, int endRow, int anzNewRows) {
		List<CellRangeAddress> mergedRegionsBeforeShift = sheet.getMergedRegions();
		List<CellRangeAddress> containedMergedRegions = mergedRegionsBeforeShift.stream()
			.filter(cra -> isContained(cra, startRow, endRow))
			.collect(Collectors.toList());

		sheet.shiftRows(startRow, endRow, anzNewRows);

		List<CellRangeAddress> remainingMergedRegions = sheet.getMergedRegions();

		// for some obscure reason, not all merged regions within [startRow, endRow] disappear
		List<CellRangeAddress> containedRemainingMergedRegions = sheet.getMergedRegions().stream()
			.filter(cra -> isContained(cra, startRow, endRow))
			.collect(Collectors.toList());

		if (mergedRegionsBeforeShift.size() - remainingMergedRegions.size() == containedMergedRegions.size() - containedRemainingMergedRegions.size()) {
			// restore lost merged regions
			containedMergedRegions.stream()
				.map(cra -> {
					CellRangeAddress copy = cra.copy();
					copy.setFirstRow(cra.getFirstRow() + anzNewRows);
					copy.setLastRow(cra.getLastRow() + anzNewRows);
					return copy;
				})
				.filter(cra -> !containedRemainingMergedRegions.contains(cra))
				.forEach(sheet::addMergedRegion);
		}

		if (sheet.getMergedRegions().size() != mergedRegionsBeforeShift.size()) {
			LOG.warn("Lost some merged regions in sheet {} when shifting {} rows from {} to {}",
				sheet.getSheetName(), anzNewRows, startRow, endRow);
		}
	}

	private static boolean isContained(@Nonnull CellRangeAddress cra, int startRow, int endRow) {
		return isContained(cra.getFirstRow(), startRow, endRow) && isContained(cra.getLastRow(), startRow, endRow);
	}

	private static boolean isContained(int rowNum, int startRow, int endRow) {
		return startRow <= rowNum && rowNum <= endRow;
	}

	public static void shiftNamedRanges(@Nonnull Sheet sheet, int startRow, int endRow, int anzNewRows) {
		sheet.getWorkbook().getAllNames().stream()
			.filter(name -> name.getRefersToFormula() != null)
			.filter(name -> sheet.getSheetName().equals(name.getSheetName()))
			.filter(name -> {
				AreaReference areaReference = new AreaReference(name.getRefersToFormula(), EXCEL2007);
				return intersects(areaReference, startRow, endRow);
			})
			.forEach(name -> shiftNamedRange(name, anzNewRows));
	}

	private static void shiftNamedRange(@Nonnull Name name, int anzNewRows) {
		AreaReference areaReference = new AreaReference(name.getRefersToFormula(), EXCEL2007);
		CellReference firstCell = areaReference.getFirstCell();
		CellReference lastCell = areaReference.getLastCell();

		String formula = String.format("%s!$%s$%d:$%s$%d", name.getSheetName(),
			CellReference.convertNumToColString(firstCell.getCol()), firstCell.getRow() + 1,
			CellReference.convertNumToColString(lastCell.getCol()), lastCell.getRow() + 1 + anzNewRows
		);

		LOG.debug("formula conversion: {} -> {}", name.getRefersToFormula(), formula);
		name.setRefersToFormula(formula);
	}

	private static boolean intersects(@Nonnull AreaReference areaReference, int startRow, int endRow) {
		int firstRow = areaReference.getFirstCell().getRow();
		int lastRow = areaReference.getLastCell().getRow();

		if (firstRow < startRow && lastRow < startRow) {
			return false;
		}

		return !(firstRow > endRow && lastRow > endRow);
	}
}
