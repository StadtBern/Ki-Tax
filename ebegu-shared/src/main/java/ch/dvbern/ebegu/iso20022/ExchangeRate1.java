
package ch.dvbern.ebegu.iso20022;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ExchangeRate1 complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ExchangeRate1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="UnitCcy" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.001.08}ActiveOrHistoricCurrencyCode" minOccurs="0"/>
 *         &lt;element name="XchgRate" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.001.08}BaseOneRate" minOccurs="0"/>
 *         &lt;element name="RateTp" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.001.08}ExchangeRateType1Code" minOccurs="0"/>
 *         &lt;element name="CtrctId" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.001.08}Max35Text" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExchangeRate1", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.001.08", propOrder = {
    "unitCcy",
    "xchgRate",
    "rateTp",
    "ctrctId"
})
public class ExchangeRate1 {

    @XmlElement(name = "UnitCcy", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.001.08")
    protected String unitCcy;
    @XmlElement(name = "XchgRate", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.001.08")
    protected BigDecimal xchgRate;
    @XmlElement(name = "RateTp", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.001.08")
    @XmlSchemaType(name = "string")
    protected ExchangeRateType1Code rateTp;
    @XmlElement(name = "CtrctId", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.001.08")
    protected String ctrctId;

    /**
     * Gets the value of the unitCcy property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUnitCcy() {
        return unitCcy;
    }

    /**
     * Sets the value of the unitCcy property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUnitCcy(String value) {
        this.unitCcy = value;
    }

    /**
     * Gets the value of the xchgRate property.
     *
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *
     */
    public BigDecimal getXchgRate() {
        return xchgRate;
    }

    /**
     * Sets the value of the xchgRate property.
     *
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *
     */
    public void setXchgRate(BigDecimal value) {
        this.xchgRate = value;
    }

    /**
     * Gets the value of the rateTp property.
     *
     * @return
     *     possible object is
     *     {@link ExchangeRateType1Code }
     *
     */
    public ExchangeRateType1Code getRateTp() {
        return rateTp;
    }

    /**
     * Sets the value of the rateTp property.
     *
     * @param value
     *     allowed object is
     *     {@link ExchangeRateType1Code }
     *
     */
    public void setRateTp(ExchangeRateType1Code value) {
        this.rateTp = value;
    }

    /**
     * Gets the value of the ctrctId property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getCtrctId() {
        return ctrctId;
    }

    /**
     * Sets the value of the ctrctId property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCtrctId(String value) {
        this.ctrctId = value;
    }

}
