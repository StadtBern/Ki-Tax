package ch.dvbern.ebegu.reporting.lib;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public enum StandardConverters implements Converter {
	REPEAT_ROW_CONVERTER() {
		@Override
		public void setCellValueImpl(@Nonnull Cell cell, @Nonnull String pattern, @Nullable Object o) {
			// nop
		}
	},
	STRING_CONVERTER() {
		@Override
		public void setCellValueImpl(@Nonnull Cell cell, @Nonnull String pattern, @Nullable Object o) {
			String stringVal = (o != null ? String.valueOf(o) : "");
			cell.setCellValue(cell.getStringCellValue().replace(pattern, stringVal));
		}
	},
	BOOLEAN_COLORED_CONVERTER() {
		@Override
		public void setCellValueImpl(@Nonnull Cell cell, @Nonnull String pattern, @Nullable Object o) {
			Boolean boolVal = (Boolean) o;
			if (boolVal == null) {
				boolVal = false;
			}
			if (pattern.equals(cell.getStringCellValue())) {
				cell.setCellValue("");
				if (boolVal) {
					final XSSFWorkbook wb = (XSSFWorkbook) cell.getSheet().getWorkbook();
					XSSFCellStyle newCellStyle = wb.getStylesSource().createCellStyle();
					newCellStyle.cloneStyleFrom(cell.getCellStyle());
					XSSFColor color = new XSSFColor(new Color(220, 220, 220));
					newCellStyle.setFillForegroundColor(color);
					newCellStyle.setFillBackgroundColor(color);
					newCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					cell.setCellStyle(newCellStyle);
				}
			}
		}
	},
	DATE_CONVERTER() {
		@Override
		public void setCellValueImpl(@Nonnull Cell cell, @Nonnull String pattern, @Nullable Object o) {
			LocalDate dateVal = (LocalDate) o;
			Date date = null;
			if (dateVal != null) {
				Instant instant = dateVal.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
				date = Date.from(instant);
			}
			if (pattern.equals(cell.getStringCellValue())) {
				// schade... bei setCellValue(Date) darf kein null uebergeben werden
				if (date == null) {
					cell.setCellValue("");
				} else {
					cell.setCellValue(date);
				}
			} else {
				cell.setCellValue(cell.getStringCellValue().replace(pattern, dateVal.format(DateUtil.DEFAULT_DATE_FORMAT)));
			}
		}
	},
	DATETIME_CONVERTER() {
		@Override
		public void setCellValueImpl(@Nonnull Cell cell, @Nonnull String pattern, @Nullable Object o) {
			LocalDateTime dateVal = (LocalDateTime) o;
			if (pattern.equals(cell.getStringCellValue())) {
				// ganze Zelle ist Datum -> die Zelle auch als Datum setzen
				Date date = null;
				if (dateVal != null) {
					date = Date.from(dateVal.atZone(ZoneId.systemDefault()).toInstant());
				}
				// schade... bei setCellValue(Date) darf kein null uebergeben werden
				if (date == null) {
					cell.setCellValue("");
				} else {
					cell.setCellValue(date);
				}
			} else {
				// nur ein Ausschnitt
				String string = "";
				if (dateVal != null) {
					string = dateVal.format(DateUtil.DEFAULT_DATETIME_FORMAT);
				}
				cell.setCellValue(string);
			}
		}
	},
	INTEGER_CONVERTER() {
		@Override
		public void setCellValueImpl(@Nonnull Cell cell, @Nonnull String pattern, @Nullable Object o) {
			Integer intVal = (Integer) o;
			if (pattern.equals(cell.getStringCellValue())) {
				if (intVal != null) {
					cell.setCellValue(intVal);
				} else {
					cell.setCellValue((String) null);
				}
			} else {
				//TODO: schoene Formatierung
				cell.setCellValue(cell.getStringCellValue().replace(pattern, String.valueOf(intVal)));
			}
		}
	},
	BIGDECIMAL_CONVERTER() {
		@Override
		public void setCellValueImpl(@Nonnull Cell cell, @Nonnull String pattern, @Nullable Object o) {
			BigDecimal bdVal = (BigDecimal) o;
			if (pattern.equals(cell.getStringCellValue())) {
				if (bdVal != null) {
					cell.setCellValue(bdVal.doubleValue());
				} else {
					cell.setCellValue("");
				}
			} else {
				//TODO: schoene Formatierung
				cell.setCellValue(cell.getStringCellValue().replace(pattern, String.valueOf(bdVal)));
			}
		}
	},
	PERCENT_CONVERTER() {
		@Override
		public void setCellValueImpl(@Nonnull Cell cell, @Nonnull String pattern, @Nullable Object o) {
			BigDecimal bdVal = (BigDecimal) o;
			if (pattern.equals(cell.getStringCellValue())) {
				if (bdVal != null) {
					bdVal = bdVal.divide(new BigDecimal("100"), BigDecimal.ROUND_HALF_UP);
					cell.setCellValue(bdVal.doubleValue());
				} else {
					cell.setCellValue("");
				}
			} else {
				cell.setCellValue(cell.getStringCellValue().replace(pattern, bdVal + "%"));
			}
		}
	},
	BOOLEAN_CONVERTER() {
		@Override
		public void setCellValueImpl(@Nonnull Cell cell, @Nonnull String pattern, @Nullable Object o) {
			Boolean boolVal = (Boolean) o;
			if (pattern.equals(cell.getStringCellValue())) {
				cell.setCellValue(boolVal);
			} else {
				//TODO: schoene Formatierung
				cell.setCellValue(cell.getStringCellValue().replace(pattern, String.valueOf(boolVal)));
			}
		}
	},
	BOOLEAN_X_CONVERTER() {
		@Override
		public void setCellValueImpl(@Nonnull Cell cell, @Nonnull String pattern, @Nullable Object o) {
			Boolean boolVal = (Boolean) o;
			String stringVal;
			if (boolVal != null && boolVal) {
				stringVal = BOOLEAN_VALUE;
			} else {
				stringVal = "";
			}
			if (pattern.equals(cell.getStringCellValue())) {
				cell.setCellValue(stringVal);
			} else {
				//TODO: schoene Formatierung
				cell.setCellValue(cell.getStringCellValue().replace(pattern, stringVal));
			}
		}
	};


	public static final String BOOLEAN_VALUE = "X";
}
