package ch.dvbern.ebegu.iso20022;

import org.hibernate.jpamodelgen.util.xml.XmlParsingException;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by gatschet on 2/13/17.
 */
public class Pain001Helper extends AbstractXmlBindingHelper {

	public static final String SCHEMA_NAME = "pain.001.001.03.ch.02.xsd";
	public static final String SCHEMA_LOCATION_LOCAL = "/schemas/" + SCHEMA_NAME;
	public static final String SCHEMA_LOCATION = "http://www.six-interbank-clearing.com/de/" + SCHEMA_NAME;

	public static final String VALIDATION_ERROR_MSG = "Validierungs-Fehler C-Level (CreditTransferTransactionInformation10CH)";

	private static Pain001Helper instance;
	private static Document tempValidateDocument;

	private final Schema schema;

	private Pain001Helper() {

		super(AbstractXmlBindingHelper.createJaxbContext(ObjectFactory.class));
		schema = AbstractXmlBindingHelper.createSchema(getClass().getResource(SCHEMA_LOCATION_LOCAL));
	}

	public static synchronized Pain001Helper getInstance() {

		if (instance == null) {
			instance = new Pain001Helper();
		}
		return instance;
	}

	public static CreditTransferTransactionInformation10CH getCLevel(byte[] blob) {

		return getInstance().createElement(CreditTransferTransactionInformation10CH.class, blob).getValue();
	}

	public static byte[] getCLevelBlob(CreditTransferTransactionInformation10CH creditTransferTransactionInformation10CH) {

		return getInstance().createXMLOutput(creditTransferTransactionInformation10CH).toByteArray();
	}

	public static void validateCLevel(CreditTransferTransactionInformation10CH creditTransferTransactionInformation10CH) {

		try {
			getInstance().validateCreditTransferTransactionInformation10CH(creditTransferTransactionInformation10CH);
		} catch (XmlParsingException e) {
			throw e;
		} catch (Exception e) {
			throw new XmlParsingException(VALIDATION_ERROR_MSG, e);
		}
	}

	public static Document getDocument(byte[] blob) {

		return (Document) getInstance().createElement(Document.class, blob).getValue();
	}

	public static byte[] getDocumentBlob(Document document) {

		return getInstance().createDocument(document).toByteArray();
	}

	private <T> JAXBElement<T> createElement(Class<T> clazz, byte[] elementToUnmarshall) {

		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(elementToUnmarshall);
			Unmarshaller unmarshaller = getJAXBContext().createUnmarshaller();
			return unmarshaller.unmarshal(new StreamSource(inputStream), clazz);
		} catch (JAXBException e) {
			throw new XmlBindingHelperException(
				"Problem beim Konvertieren XML zu Objekts", e);
		}
	}

	private Document getTempValidateDocument(CreditTransferTransactionInformation10CH creditTransferTransactionInformation10CH) throws Exception {

		if (tempValidateDocument == null) {
			// DocumentStruktur (A und B-Level) zum Validieren
			ObjectFactory objectFactory = new ObjectFactory();
			tempValidateDocument = objectFactory.createDocument();
			tempValidateDocument.setCstmrCdtTrfInitn(objectFactory.createCustomerCreditTransferInitiationV03CH());
			tempValidateDocument.getCstmrCdtTrfInitn().setGrpHdr(objectFactory.createGroupHeader32CH());
			tempValidateDocument.getCstmrCdtTrfInitn().getGrpHdr().setMsgId("1");
			GregorianCalendar gc = new GregorianCalendar();
			gc.set(1900, 1, 1);
			XMLGregorianCalendar aDateTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
			tempValidateDocument.getCstmrCdtTrfInitn().getGrpHdr().setCreDtTm(aDateTime);
			tempValidateDocument.getCstmrCdtTrfInitn().getGrpHdr().setNbOfTxs("1");
			tempValidateDocument.getCstmrCdtTrfInitn().getGrpHdr().setInitgPty(objectFactory.createPartyIdentification32CHNameAndId());

			PaymentInstructionInformation3CH paymentInstructionInformation3CH = objectFactory.createPaymentInstructionInformation3CH();
			paymentInstructionInformation3CH.setPmtInfId("Test");
			paymentInstructionInformation3CH.setPmtMtd(PaymentMethod3Code.TRA);
			XMLGregorianCalendar aDate = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(1900, 1, 1, DatatypeConstants.FIELD_UNDEFINED);
			paymentInstructionInformation3CH.setReqdExctnDt(aDate);
			paymentInstructionInformation3CH.setDbtr(objectFactory.createPartyIdentification32CH());
			paymentInstructionInformation3CH.setDbtrAcct(objectFactory.createCashAccount16CHIdTpCcy());
			paymentInstructionInformation3CH.getDbtrAcct().setId(objectFactory.createAccountIdentification4ChoiceCH());
			paymentInstructionInformation3CH.getDbtrAcct().getId().setIBAN("CH0809000000300270001");
			paymentInstructionInformation3CH.setDbtrAgt(objectFactory.createBranchAndFinancialInstitutionIdentification4CHBicOrClrId());
			paymentInstructionInformation3CH.getDbtrAgt().setFinInstnId(objectFactory.createFinancialInstitutionIdentification7CHBicOrClrId());
			tempValidateDocument.getCstmrCdtTrfInitn().getPmtInves().add(paymentInstructionInformation3CH);
		}
		tempValidateDocument.getCstmrCdtTrfInitn().getPmtInves().get(0).getCdtTrfTxInves().clear();
		tempValidateDocument.getCstmrCdtTrfInitn().getPmtInves().get(0).getCdtTrfTxInves().add(creditTransferTransactionInformation10CH);

		return tempValidateDocument;
	}

	private void validateCreditTransferTransactionInformation10CH(CreditTransferTransactionInformation10CH creditTransferTransactionInformation10CH) throws Exception {

		Document document = getTempValidateDocument(creditTransferTransactionInformation10CH);

		Marshaller marshaller = getJAXBContext().createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, SCHEMA_LOCATION + " " + SCHEMA_NAME);

		Validator validatorDocument = schema.newValidator();
		validatorDocument.setErrorHandler(new DefaultHandler() {

			@Override
			public void error(SAXParseException exception) throws SAXException {

				throw new XmlParsingException(VALIDATION_ERROR_MSG, exception);
			}

			@Override
			public void fatalError(SAXParseException exception) throws SAXException {

				throw new XmlParsingException(VALIDATION_ERROR_MSG, exception);
			}
		});
		JAXBSource source = new JAXBSource(marshaller, document);
		validatorDocument.validate(source);
	}

	/**
	 * marshal einer DatenStruktur OHNE @XmlRootElement annotation (Es wird nur und Sub-Teil der XML-Stuktur erzeugt)
	 *
	 * @param elemToMarshall
	 * @return
	 */
	private ByteArrayOutputStream createXMLOutput(Object elemToMarshall) {

		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			Marshaller marshaller = getJAXBContext().createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(new JAXBElement(new QName(SCHEMA_NAME, elemToMarshall.getClass().getSimpleName()), elemToMarshall.getClass(), elemToMarshall), outputStream); // ohne @XmlRootElement annotation
			return outputStream;
		} catch (JAXBException e) {
			throw new XmlBindingHelperException(
				"Problem beim Konvertieren Objekts zu XML", e);
		}
	}

	/**
	 * Erstellt XML zum Document
	 *
	 * @param elemToMarshall
	 * @return
	 */
	private ByteArrayOutputStream createDocument(Object elemToMarshall) {

		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			Marshaller marshaller = getJAXBContext().createMarshaller();
			marshaller.setSchema(schema);
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, SCHEMA_LOCATION + " " + SCHEMA_NAME);
			marshaller.marshal(elemToMarshall, outputStream);
			return outputStream;
		} catch (JAXBException e) {
			throw new XmlBindingHelperException(
				"Problem beim Konvertieren Objekts zu XML", e);
		}
	}
}
