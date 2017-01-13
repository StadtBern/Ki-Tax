package ch.dvbern.ebegu.vorlagen;
/*
* Copyright (c) 2016 DV Bern AG, Switzerland
*
* Das vorliegende Dokument, einschliesslich aller seiner Teile, ist urheberrechtlich
* geschuetzt. Jede Verwertung ist ohne Zustimmung der DV Bern AG unzulaessig. Dies gilt
* insbesondere fuer Vervielfaeltigungen, die Einspeicherung und Verarbeitung in
* elektronischer Form. Wird das Dokument einem Kunden im Rahmen der Projektarbeit zur
* Ansicht uebergeben ist jede weitere Verteilung durch den Kunden an Dritte untersagt.
*
* Ersteller: zeab am: 10.08.2016
*/

import ch.dvbern.ebegu.errors.MergeDocException;
import ch.dvbern.lib.doctemplate.common.DocTemplateException;
import ch.dvbern.lib.doctemplate.docx.DOCXMergeEngine;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.*;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import javax.annotation.Nonnull;
import java.awt.color.ICC_Profile;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * Helper Klasse um einen DocX zu einem PDF zu konvertieren
 */
public class GeneratePDFDocumentHelper {

	private static final String PDFENCODING = "ISO-8859-1";
	private static final String NUMOFPAGE = "#PAGE";
	private static final String NUMOFPAGES = "#MAX";
	private static final String	PROP_STANDARD_ANZAHL_SEITEN = "expectedNumberOfPages";

	/**
	 * Konvertiert ein docx zu einem PDF
	 *
	 * @return das PDF Dokument als Byte
	 * @throws MergeDocException
	 */
	@Nonnull
	private byte[] generatePDFDocument(@Nonnull byte[] generateFrom) throws MergeDocException {
		try {
			Objects.requireNonNull(generateFrom, "generateFrom muss gesetzt sein");
			final XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(generateFrom));

			final PdfOptions options = PdfOptions.create();
			options.setConfiguration(this::setXDocReportPDFWriterOptions);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			PdfConverter.getInstance().convert(document, out, options);

			return manipulatePdf(out.toByteArray());
		} catch (IOException | InvocationTargetException | DocumentException | IllegalAccessException | NoSuchMethodException e) {
			throw new MergeDocException("generatePDFDocument()", "Bei der Generierung der Verfuegungsmustervorlage ist einen Fehler aufgetretten", e, new Objects[] {});
		}
	}

	/**
	 * Konvertiert ein docx zu einem PDF
	 *
	 * @return das PDF Dokument als Byte
	 * @throws MergeDocException
	 */
	@Nonnull
	public byte[] generatePDFDocument(@Nonnull byte[] docxTemplate, @Nonnull EBEGUMergeSource mergeSource) throws MergeDocException {
		try {
			Objects.requireNonNull(docxTemplate, "generateFrom muss gesetzt sein");
			Objects.requireNonNull(docxTemplate, "mergeSource muss gesetzt sein");

			DOCXMergeEngine docxme = new DOCXMergeEngine(mergeSource.getClass().getName());

			byte[] mergedDocx = docxme.getDocument(new ByteArrayInputStream(docxTemplate), mergeSource);
			byte[] mergedPdf = generatePDFDocument(mergedDocx);
			PdfReader reader = new PdfReader(mergedPdf);
			int numOfPDFPages = reader.getNumberOfPages();
			reader.close();

			int expectedNumOfDOCXPages = 0;
			XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(mergedDocx));
			if (document.getProperties().getCustomProperties().contains(PROP_STANDARD_ANZAHL_SEITEN)){
				expectedNumOfDOCXPages = document.getProperties().getCustomProperties()
					.getProperty(PROP_STANDARD_ANZAHL_SEITEN).getI4();
			}

			if(expectedNumOfDOCXPages > 0 && expectedNumOfDOCXPages != numOfPDFPages){
				mergeSource.setPDFLongerThanExpected(true);
				mergedDocx = docxme.getDocument(new ByteArrayInputStream(docxTemplate), mergeSource);
				mergedPdf = generatePDFDocument(mergedDocx);
			}

			return mergedPdf;
		} catch (IOException | DocTemplateException e) {
			throw new MergeDocException("generatePDFDocument()", "Bei der Generierung der Verfuegungsmustervorlage ist einen Fehler aufgetretten", e, new Objects[] {});
		}
	}

	private void setXDocReportPDFWriterOptions(@Nonnull PdfWriter pdfWriter) {
		pdfWriter.setPdfVersion(PdfWriter.PDF_VERSION_1_4);
		pdfWriter.setTagged();
		PDFFontUtil.embedStandardFonts();
	}

	/**
	 * PDF has to be manipulated in order to set the right page number.
	 * @throws IOException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws DocumentException
	 */
	@Nonnull
	private byte[] manipulatePdf(@Nonnull byte[] doc) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, DocumentException {

		PDFFontUtil.embedStandardFonts();

		ByteArrayOutputStream manipulated = new ByteArrayOutputStream();
		PdfStamper stamper = null;
		PdfReader reader = new PdfReader(doc);
		int pages = reader.getNumberOfPages();
		try {
			for (int i = 1; i <= pages; i++) {
				PdfDictionary dict = reader.getPageN(i);
				PdfObject object = dict.getDirectObject(PdfName.CONTENTS);
				if (object instanceof PRStream) {
					PRStream stream = (PRStream) object;
					byte[] data = PdfReader.getStreamBytes(stream);
					// Man muss hier doppelt die Kodierung angeben und es funktioniert nicht mit UTF-8. Scheint nicht
					// auf unseren Windows-Rechner zu funktionieren!!!
					String correctedStr = new String(data, Charset.forName(PDFENCODING)).replace(NUMOFPAGE, String.valueOf(i)).replace(NUMOFPAGES, String.valueOf(pages));
					stream.setData(correctedStr.getBytes(PDFENCODING));
				}
			}
			stamper = new PdfStamper(reader, manipulated);

			setWriterPDFA(stamper);

		} finally {
				if (stamper != null) {
					stamper.close();
				}
				reader.close();
				manipulated.close();
		}
		return manipulated.toByteArray();
	}

	private void setWriterPDFA(@Nonnull PdfStamper stamper) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {

		PdfWriter writer = stamper.getWriter();

		writer.setPDFXConformance(PdfWriter.PDFA1B);
		writer.createXmpMetadata();

		setColorProfile(writer);
	}

	/**
	 * Zusätlich zu die Fonts, soll auch das Color Profil im PDF.Dictionnary addiert werden.
	 */
	@SuppressWarnings("PMD.AvoidCatchingNPE")
	@SuppressFBWarnings(value = "UI_INHERITANCE_UNSAFE_GETRESOURCE")
	private void setColorProfile(@Nonnull PdfWriter pdfWriter) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {

		pdfWriter.setDefaultColorspace(PdfName.DEFAULTRGB, null);

		// Hack: Bevor pdfWriter.setOutputIntents zu aufrufen, soll das Document geöffnet werden (das Document selbst,
		// nicht den Writer)
		try {
			Method m = PdfWriter.class.getDeclaredMethod("getPdfDocument");
			m.setAccessible(true);
			((PdfDocument) m.invoke(pdfWriter)).open();
		} catch (NullPointerException eN) {
			// Invoking führt zur NullPointer bei initPage aber Rest den Job ist gemacht, was genug ist hier.
			// Open wird später wieder bei xdocreport mit success durchgeführt.
		}

		final ICC_Profile icc = ICC_Profile.getInstance(this.getClass().getResourceAsStream("/font/sRGB.profile"));
		pdfWriter.setOutputIntents("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", icc);
	}
}
