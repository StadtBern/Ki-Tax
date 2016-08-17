package ch.dvbern.ebegu.services.vorlagen;
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

import java.awt.color.ICC_Profile;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;

import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PRStream;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfDocument;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;

import fr.opensagres.xdocreport.itext.extension.IPdfWriterConfiguration;

/**
 * Helper Klasse um einen DocX zu einem PDF zu konvertieren
 */
public class GeneratePDFDocumentHelper {

	private static final String PDFENCODING = "ISO-8859-1";
	private static final String NUMOFPAGE = "#PAGE";
	private static final String NUMOFPAGES = "#MAX";

	/**
	 * Konvertiert ein docx zu einem PDF
	 *
	 * @param generateFrom
	 * @return das PDF Dokument als Byte
	 * @throws IOException
	 * @throws DocumentException
	 */
	public byte[] generatePDFDocument(byte[] generateFrom) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, DocumentException {

		if (generateFrom == null) {
			throw new IllegalArgumentException("Das Argument 'generateFrom' darf nicht leer sein");
		}
		final XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(generateFrom));

		final PdfOptions options = PdfOptions.create();
		options.setConfiguration(new IPdfWriterConfiguration() {

			@Override
			public void configure(PdfWriter pdfWriter) {

				setXDocReportPDFWriterOptions(pdfWriter);
			}
		});

		java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
		PdfConverter.getInstance().convert(document, out, options);

		return manipulatePdf(out.toByteArray());
	}

	private void setXDocReportPDFWriterOptions(PdfWriter pdfWriter) {

		pdfWriter.setPdfVersion(PdfWriter.PDF_VERSION_1_4);

		pdfWriter.setTagged();

		PDFFontUtil.embedStandardFonts();
	}

	/**
	 * PDF has to be manipulated in order to set the right page number.
	 *
	 * @param doc
	 * @return
	 * @throws IOException
	 * @throws DocumentException
	 */
	private byte[] manipulatePdf(byte[] doc) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, DocumentException {

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

	private void setWriterPDFA(PdfStamper stamper) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {

		PdfWriter writer = stamper.getWriter();

		writer.setPDFXConformance(PdfWriter.PDFA1B);
		writer.createXmpMetadata();

		setColorProfile(writer);
	}

	/**
	 * Zusätlich zu die Fonts, soll auch das Color Profil im PDF.Dictionnary addiert werden.
	 *
	 * @param pdfWriter
	 */
	private void setColorProfile(PdfWriter pdfWriter) throws IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {

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
