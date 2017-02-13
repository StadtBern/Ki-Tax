
package ch.dvbern.ebegu.iso20022;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for TaxPeriod1 complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="TaxPeriod1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Yr" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.001.08}ISODate" minOccurs="0"/>
 *         &lt;element name="Tp" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.001.08}TaxRecordPeriod1Code" minOccurs="0"/>
 *         &lt;element name="FrToDt" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.001.08}DatePeriodDetails" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TaxPeriod1", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.001.08", propOrder = {
    "yr",
    "tp",
    "frToDt"
})
public class TaxPeriod1 {

    @XmlElement(name = "Yr", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.001.08")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar yr;
    @XmlElement(name = "Tp", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.001.08")
    @XmlSchemaType(name = "string")
    protected TaxRecordPeriod1Code tp;
    @XmlElement(name = "FrToDt", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.001.08")
    protected DatePeriodDetails frToDt;

    /**
     * Gets the value of the yr property.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getYr() {
        return yr;
    }

    /**
     * Sets the value of the yr property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setYr(XMLGregorianCalendar value) {
        this.yr = value;
    }

    /**
     * Gets the value of the tp property.
     *
     * @return
     *     possible object is
     *     {@link TaxRecordPeriod1Code }
     *
     */
    public TaxRecordPeriod1Code getTp() {
        return tp;
    }

    /**
     * Sets the value of the tp property.
     *
     * @param value
     *     allowed object is
     *     {@link TaxRecordPeriod1Code }
     *
     */
    public void setTp(TaxRecordPeriod1Code value) {
        this.tp = value;
    }

    /**
     * Gets the value of the frToDt property.
     *
     * @return
     *     possible object is
     *     {@link DatePeriodDetails }
     *
     */
    public DatePeriodDetails getFrToDt() {
        return frToDt;
    }

    /**
     * Sets the value of the frToDt property.
     *
     * @param value
     *     allowed object is
     *     {@link DatePeriodDetails }
     *
     */
    public void setFrToDt(DatePeriodDetails value) {
        this.frToDt = value;
    }

}
