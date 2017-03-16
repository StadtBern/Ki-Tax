package ch.dvbern.ebegu.services;

import ch.dvbern.lib.excelmerger.ExcelMergeException;
import ch.dvbern.lib.excelmerger.ExcelMerger;
import ch.dvbern.lib.excelmerger.ExcelMergerDTO;
import ch.dvbern.lib.excelmerger.MergeField;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static ch.dvbern.ebegu.util.MonitoringUtil.monitor;

/**
 * Copyright (c) 2016 DV Bern AG, Switzerland
 * <p>
 * Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
 * geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
 * insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
 * elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
 * Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
 * <p>
 * Created by medu on 05/02/2017.
 */
public abstract class AbstractReportServiceBean extends AbstractBaseService {


	protected byte[] createWorkbook(@Nonnull Workbook workbook){
		byte[] bytes;
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			workbook.write(baos);
			baos.flush();
			bytes = baos.toByteArray();

		} catch (IOException | RuntimeException e) {
			throw new IllegalStateException("Error creating workbook", e);
		}
		return bytes;
	}

	protected void mergeData(@Nonnull Sheet sheet, @Nonnull ExcelMergerDTO excelMergerDTO, @Nonnull MergeField[] mergeFields) throws ExcelMergeException {
		monitor(ReportServiceBean.class, String.format("mergeData (sheet=%s)", sheet.getSheetName()),
			() -> ExcelMerger.mergeData(sheet, mergeFields, excelMergerDTO));
	}
}
