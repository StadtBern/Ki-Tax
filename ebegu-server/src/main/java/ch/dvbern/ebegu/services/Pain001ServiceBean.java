package ch.dvbern.ebegu.services;

import ch.dvbern.ebegu.entities.Zahlung;
import ch.dvbern.ebegu.entities.Zahlungsauftrag;
import ch.dvbern.ebegu.enums.ApplicationPropertyKey;
import ch.dvbern.ebegu.enums.ErrorCodeEnum;
import ch.dvbern.ebegu.errors.EbeguRuntimeException;
import ch.dvbern.ebegu.iso20022.V03CH02.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.GregorianCalendar;

/**
 * Service fuer Generierung des Zahlungsfile gemäss ISI200022
 */
@Stateless
@Local(Pain001Service.class)
public class Pain001ServiceBean extends AbstractBaseService implements Pain001Service {


	@Inject
	private ApplicationPropertyService applicationPropertyService;

	public static final String DEF_DEBTOR_NAME = "Direktion für Bildung, Soziales und Sport der Stadt Bern";
	public static final String DEF_DEBTOR_BIC = "POFICHBEXXX";
	public static final String DEF_DEBTOR_IBAN = "CH330900000300008233";

	private static final String CtgyPurp_Cd = "SSBE";
	private static final String CCY = "CHF";
	private static final String CtctDtls_Nm = "KITAX";
	private static final String CtctDtls_Othr = "V01";
	private static final PaymentMethod3Code PAYMENT_METHOD_3_CODE = PaymentMethod3Code.TRA;
	private static final Boolean BtchBookg = true;

	public static final String SCHEMA_NAME = "pain.001.001.03.ch.02.xsd";
	public static final String SCHEMA_LOCATION_LOCAL = "ch.dvbern.ebegu.iso20022.V03CH02/" + SCHEMA_NAME;
	public static final String SCHEMA_LOCATION = "http://www.six-interbank-clearing.com/de/" + SCHEMA_NAME;

	private final Logger LOG = LoggerFactory.getLogger(Pain001ServiceBean.class.getSimpleName());

	private JAXBContext jaxbContext;


	@Override
	public String getPainFileContent(Zahlungsauftrag zahlungsauftrag) {

		final Document document = createDocument(zahlungsauftrag);

		return getXMLStringFromDocument(document);
	}


	public String getXMLStringFromDocument(final Document document) {
		final StringWriter documentXmlString = new StringWriter();
		try {
			if (jaxbContext == null) {
				jaxbContext = JAXBContext.newInstance(Document.class);
			}

			final Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, SCHEMA_LOCATION + " " + SCHEMA_NAME);

			jaxbMarshaller.setEventHandler(new ValidationEventHandler() {
				@Override
				public boolean handleEvent(final ValidationEvent event) {
					throw new EbeguRuntimeException("Kaput", event.getMessage(), event.getLinkedException());
				}
			});

			jaxbMarshaller.marshal(getElementToMarshall(document), documentXmlString); // ohne @XmlRootElement annotation

			LOG.info("XML={}", documentXmlString);

		} catch (final Exception e) {
			LOG.error("Failed to marshal Document", e.getMessage());
			throw new EbeguRuntimeException("Kaput", ErrorCodeEnum.ERROR_ENTITY_NOT_FOUND, e);
		}
		return documentXmlString.toString();
	}

	private JAXBElement getElementToMarshall(Object elemToMarshall) {
		return new JAXBElement(new QName(SCHEMA_LOCATION, elemToMarshall.getClass().getSimpleName()), elemToMarshall.getClass(), elemToMarshall);
	}

	protected Schema getSchema() throws SAXException {
		final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		final URL resourceURL = Document.class.getClassLoader().getResource(SCHEMA_LOCATION_LOCAL);
		if (resourceURL == null) {
			throw new EbeguRuntimeException("Schema not found", SCHEMA_LOCATION_LOCAL);
		}

		final Schema schema = schemaFactory.newSchema(resourceURL);
		return schema;
	}

	/**
	 * Beispiel:
	 * <p>
	 * <pre>
	 * < ?xml version="1.0" encoding="UTF-8" standalone="yes"?>
	 * < Document xmlns="http://www.six-interbank-clearing.com/de/pain.001.001.03.ch.02.xsd"
	 * xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	 * xsi:schemaLocation="http://www.six-interbank-clearing.com/de/pain.001.001.03.ch.02.xsd pain.001.001.03.ch.02.xsd">
	 * 	< CstmrCdtTrfInitn>
	 * 		< GrpHdr>
	 * 			< !--Group header-->
	 * 		< /GrpHdr>
	 * 		< PmtInf>
	 * 			< !--Payment Information-->
	 * 		< /PmtInf>
	 * 	< /CstmrCdtTrfInitn>
	 * < /Document>
	 * </pre>
	 */
	private Document createDocument(Zahlungsauftrag zahlungsauftrag) {
		String debtor_name = applicationPropertyService.findApplicationPropertyAsString(ApplicationPropertyKey.DEBTOR_NAME);
		String debtor_bic = applicationPropertyService.findApplicationPropertyAsString(ApplicationPropertyKey.DEBTOR_BIC);
		String debtor_iban = applicationPropertyService.findApplicationPropertyAsString(ApplicationPropertyKey.DEBTOR_IBAN);
		String debtor_iban_gebuehren = applicationPropertyService.findApplicationPropertyAsString(ApplicationPropertyKey.DEBTOR_IBAN_GEBUEHREN);

		if (debtor_name == null) {
			debtor_name = DEF_DEBTOR_NAME;
		}
		if (debtor_bic == null) {
			debtor_bic = DEF_DEBTOR_BIC;
		}
		if (debtor_iban == null) {
			debtor_iban = DEF_DEBTOR_IBAN;
		}
		if (debtor_iban_gebuehren == null) {
			debtor_iban_gebuehren = debtor_iban;
		}


		Document document = null;

		// DocumentStruktur (A und B-Level) zum Validieren
		ObjectFactory objectFactory = new ObjectFactory();
		document = objectFactory.createDocument();
		document.setCstmrCdtTrfInitn(objectFactory.createCustomerCreditTransferInitiationV03CH());

		PaymentInstructionInformation3CH paymentInstructionInformation3CH = createPaymentInstructionInformation3CH(zahlungsauftrag, objectFactory, debtor_name, debtor_iban, debtor_bic, debtor_iban_gebuehren);
		document.getCstmrCdtTrfInitn().getPmtInf().add(paymentInstructionInformation3CH);

		document.getCstmrCdtTrfInitn().getPmtInf().get(0).getCdtTrfTxInf().clear();

		int transaktion = 0;
		BigDecimal ctrlSum = new BigDecimal(0);
		for (Zahlung zahlung : zahlungsauftrag.getZahlungen()) {
			transaktion++;
			ctrlSum = ctrlSum.add(zahlung.getTotal());
			document.getCstmrCdtTrfInitn().getPmtInf().get(0).getCdtTrfTxInf().add(createCreditTransferTransactionInformation10CH(objectFactory, transaktion, zahlung));
		}

		document.getCstmrCdtTrfInitn().setGrpHdr(createGroupHeader(zahlungsauftrag, objectFactory, transaktion, ctrlSum, debtor_name));

		return document;
	}


	/**
	 * Beispiel:
	 * <pre>
	 * < CdtTrfTxInf>
	 * 	< PmtId>
	 * 		< InstrId>18< /InstrId>
	 * 	< /PmtId>
	 * 	< Amt>
	 * 		< InstdAmt Ccy="CHF">1175< /InstdAmt>
	 * 	< /Amt>
	 * 	< CdtrAgt>
	 * 		< FinInstnId>
	 * 			< BIC>RAIFCH22XXX< /BIC>
	 * 		< /FinInstnId>
	 * 	< /CdtrAgt>
	 * 	< Cdtr>
	 * 		< Nm>Tester-Ncbijgep Tim< /Nm>
	 * 		< PstlAdr>
	 * 			< StrtNm>Thunstrasse 17< /StrtNm>
	 * 			< PstCd>3000< /PstCd>
	 * 			< TwnNm>Bern< /TwnNm>
	 * 			< Ctry>CH< /Ctry>
	 * 		< /PstlAdr>
	 * 	< /Cdtr>
	 * 	< CdtrAcct>
	 * 		< Id>
	 * 			< IBAN>CH3780817000000576623< /IBAN>
	 * 		< /Id>
	 * 	< /CdtrAcct>
	 * 	< RmtInf>
	 * 		< Ustrd>Irgend ein blabla< /Ustrd>
	 * 	< /RmtInf>
	 * < /CdtTrfTxInf>
	 * </pre>
	 */
	private CreditTransferTransactionInformation10CH createCreditTransferTransactionInformation10CH(ObjectFactory objectFactory, int transaktion, Zahlung zahlung) {
		CreditTransferTransactionInformation10CH cTTI10CH = objectFactory.createCreditTransferTransactionInformation10CH();

		// struktur
		cTTI10CH.setPmtId(objectFactory.createPaymentIdentification1());

		cTTI10CH.setAmt(objectFactory.createAmountType3Choice());
		cTTI10CH.getAmt().setInstdAmt(objectFactory.createActiveOrHistoricCurrencyAndAmount());

		cTTI10CH.setCdtr(objectFactory.createPartyIdentification32CHName());
		cTTI10CH.getCdtr().setPstlAdr(objectFactory.createPostalAddress6CH());

		cTTI10CH.setRmtInf(objectFactory.createRemittanceInformation5CH());

		cTTI10CH.setCdtrAgt(objectFactory.createBranchAndFinancialInstitutionIdentification4CH());
		cTTI10CH.getCdtrAgt().setFinInstnId(objectFactory.createFinancialInstitutionIdentification7CH());

		// data
		cTTI10CH.getPmtId().setInstrId(String.valueOf(transaktion)); // 2.29
		cTTI10CH.getPmtId().setEndToEndId(transaktion + " / " + zahlung.getInstitutionStammdaten().getInstitution().getName()); // 2.30

		// Wert
		cTTI10CH.getAmt().getInstdAmt().setCcy(CCY);// 2.43
		cTTI10CH.getAmt().getInstdAmt().setValue(zahlung.getTotal());// 2.43

		//BIC
		cTTI10CH.setCdtrAgt(objectFactory.createBranchAndFinancialInstitutionIdentification4CH());
		cTTI10CH.getCdtrAgt().setFinInstnId(objectFactory.createFinancialInstitutionIdentification7CH());
		cTTI10CH.getCdtrAgt().getFinInstnId().setBIC(zahlung.getInstitutionStammdaten().getBIC());

		//IBAN
		cTTI10CH.setCdtrAcct(objectFactory.createCashAccount16CHId());
		cTTI10CH.getCdtrAcct().setId(objectFactory.createAccountIdentification4ChoiceCH()); // 2.80
		cTTI10CH.getCdtrAcct().getId().setIBAN(zahlung.getInstitutionStammdaten().getIban().toString().replaceAll(" ", "")); // 2.80

		// 1.1.1	ETAB 503.2: EZAG für PC- / IBAN-Auszahlung aufbereiten, 2. Teil
		// Strukturierte Daten
		cTTI10CH.setCdtr(objectFactory.createPartyIdentification32CHName());
		cTTI10CH.getCdtr().setNm(zahlung.getInstitutionStammdaten().getInstitution().getName()); // 2.79
		cTTI10CH.getCdtr().setPstlAdr(objectFactory.createPostalAddress6CH());
		cTTI10CH.getCdtr().getPstlAdr().setStrtNm(zahlung.getInstitutionStammdaten().getAdresse().getHausnummer()); // 2.79
		cTTI10CH.getCdtr().getPstlAdr().setPstCd(zahlung.getInstitutionStammdaten().getAdresse().getPlz());// 2.79
		cTTI10CH.getCdtr().getPstlAdr().setTwnNm(zahlung.getInstitutionStammdaten().getAdresse().getOrt());// 2.79
		cTTI10CH.getCdtr().getPstlAdr().setCtry(zahlung.getInstitutionStammdaten().getAdresse().getLand().toString());// 2.79

		cTTI10CH.setRmtInf(objectFactory.createRemittanceInformation5CH());
		cTTI10CH.getRmtInf().setUstrd(zahlung.getZahlungstext());    // 2.99
		return cTTI10CH;
	}

	/**
	 * Beispiel PaymentInformation:
	 * <p>
	 * <pre>
	 * < PmtInf>
	 * 	< PmtInfId>01-201611-01< /PmtInfId>
	 * 	< PmtMtd>TRA< /PmtMtd>
	 * 	< BtchBookg>true< /BtchBookg>
	 * 	< PmtTpInf>
	 * 		< CtgyPurp>
	 * 			< Cd>PENS< /Cd>
	 * 		< /CtgyPurp>
	 * 	< /PmtTpInf>
	 * 	< ReqdExctnDt>2017-01< /ReqdExctnDt>
	 * 	< Dbtr>
	 * 		< Nm>Jugendamt< /Nm>
	 * 	< /Dbtr>
	 * 	< DbtrAcct>
	 * 		< Id>
	 * 			< IBAN>CH0809000000300270001< /IBAN>
	 * 		< /Id>
	 * 	< /DbtrAcct>
	 * 	< DbtrAgt>
	 * 		< FinInstnId>
	 * 			< BIC>POFICHBEXXX< /BIC>
	 * 		< /FinInstnId>
	 * 	< /DbtrAgt>
	 * 	< ChrgsAcct>
	 * 		< Id>
	 * 			< IBAN>CH4709000000300003131< /IBAN>
	 * 		< /Id>
	 * 	< /ChrgsAcct>
	 * 	< CdtTrfTxInf>
	 * 		< !--Auszahlungen-->
	 * 	< /CdtTrfTxInf>
	 * < /PmtInf>
	 * </pre>
	 */
	private PaymentInstructionInformation3CH createPaymentInstructionInformation3CH(Zahlungsauftrag zahlungsauftrag, ObjectFactory objectFactory, String debtor_name, String debtor_iban, String debtor_bic, String debtor_iban_gebuehren) {
		PaymentInstructionInformation3CH paymentInstructionInformation3CH = objectFactory.createPaymentInstructionInformation3CH();
		paymentInstructionInformation3CH.setPmtInfId(getMsgId(zahlungsauftrag));
		paymentInstructionInformation3CH.setPmtMtd(PAYMENT_METHOD_3_CODE);

		paymentInstructionInformation3CH.setBtchBookg(BtchBookg);

		paymentInstructionInformation3CH.setPmtTpInf(objectFactory.createPaymentTypeInformation19CH());
		paymentInstructionInformation3CH.getPmtTpInf().setCtgyPurp(objectFactory.createCategoryPurpose1CHCode());
		paymentInstructionInformation3CH.getPmtTpInf().getCtgyPurp().setCd(CtgyPurp_Cd);

		paymentInstructionInformation3CH.setReqdExctnDt(getXmlGregorianCalendar(zahlungsauftrag.getDatumFaellig()));

		// Debtor name
		paymentInstructionInformation3CH.setDbtr(objectFactory.createPartyIdentification32CH());
		paymentInstructionInformation3CH.getDbtr().setNm(debtor_name);

		// Debtor Iban
		paymentInstructionInformation3CH.setDbtrAcct(objectFactory.createCashAccount16CHIdTpCcy());
		paymentInstructionInformation3CH.getDbtrAcct().setId(objectFactory.createAccountIdentification4ChoiceCH());
		paymentInstructionInformation3CH.getDbtrAcct().getId().setIBAN(debtor_iban);

		// Debtor BIC
		paymentInstructionInformation3CH.setDbtrAgt(objectFactory.createBranchAndFinancialInstitutionIdentification4CHBicOrClrId());
		paymentInstructionInformation3CH.getDbtrAgt().setFinInstnId(objectFactory.createFinancialInstitutionIdentification7CHBicOrClrId());
		paymentInstructionInformation3CH.getDbtrAgt().getFinInstnId().setBIC(debtor_bic);

		// Debtor charge Iban
		paymentInstructionInformation3CH.setChrgsAcct(objectFactory.createCashAccount16CHIdAndCurrency());
		paymentInstructionInformation3CH.getChrgsAcct().setId(objectFactory.createAccountIdentification4ChoiceCH());
		paymentInstructionInformation3CH.getChrgsAcct().getId().setIBAN(debtor_iban_gebuehren);

		return paymentInstructionInformation3CH;
	}

	/**
	 * Beispiel:
	 * <p>
	 * <pre>
	 * < GrpHdr>
	 * 	< MsgId>01-201611-01< /MsgId>
	 * 	< CreDtTm>2016-10-28T00:00:00.000+02:00< /CreDtTm>
	 * 	< NbOfTxs>130< /NbOfTxs>
	 * 	< CtrlSum>204013< /CtrlSum>
	 * 	< InitgPty>
	 * 		< Nm>Jugendamt< /Nm>
	 * 		< CtctDtls>
	 * 			< Nm>Kitac< /Nm>
	 * 			< Othr>V01< /Othr>
	 * 		< /CtctDtls>
	 * 	< /InitgPty>
	 * < /GrpHdr>
	 * </pre>
	 */
	private GroupHeader32CH createGroupHeader(Zahlungsauftrag zahlungsauftrag, ObjectFactory objectFactory, int transaktion, BigDecimal ctrlSum, String debtor_name) {
		// GroupHeader
		// struktur
		GroupHeader32CH groupHeader32CH = objectFactory.createGroupHeader32CH();

		groupHeader32CH.setInitgPty(objectFactory.createPartyIdentification32CHNameAndId());
		groupHeader32CH.getInitgPty().setCtctDtls(objectFactory.createContactDetails2CH());

		// data
		groupHeader32CH.setMsgId(getMsgId(zahlungsauftrag)); // 1.1

		groupHeader32CH.setNbOfTxs(Integer.toString(transaktion)); // 1.6
		groupHeader32CH.setCtrlSum(ctrlSum); // 1.7

		groupHeader32CH.getInitgPty().setNm(debtor_name); // 1.8

		groupHeader32CH.getInitgPty().getCtctDtls().setNm(CtctDtls_Nm); // 1.8
		groupHeader32CH.getInitgPty().getCtctDtls().setOthr(CtctDtls_Othr); // 1.8

		groupHeader32CH.setCreDtTm(getXmlGregorianCalendar(LocalDateTime.now())); // 1.2
		return groupHeader32CH;


	}

	private XMLGregorianCalendar getXmlGregorianCalendar(LocalDateTime datum) {
		ZoneId zoneId = ZoneId.of("Europe/Paris");
		ZonedDateTime zdt = datum.atZone(zoneId);
		GregorianCalendar gc = GregorianCalendar.from(zdt);

		XMLGregorianCalendar aDateTime = null;
		try {
			aDateTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
		return aDateTime;
	}

	private String getMsgId(Zahlungsauftrag zahlungsauftrag) {
		return zahlungsauftrag.getDatumFaellig().getYear() + "-" + zahlungsauftrag.getDatumFaellig().getMonthValue();
	}
}
