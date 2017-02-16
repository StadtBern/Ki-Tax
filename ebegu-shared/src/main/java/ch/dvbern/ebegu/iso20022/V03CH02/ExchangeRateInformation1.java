
package ch.dvbern.ebegu.iso20022.V03CH02;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ExchangeRateInformation1 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExchangeRateInformation1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="XchgRate" type="{http://www.six-interbank-clearing.com/de/pain.001.001.03.ch.02.xsd}BaseOneRate" minOccurs="0"/>
 *         &lt;element name="RateTp" type="{http://www.six-interbank-clearing.com/de/pain.001.001.03.ch.02.xsd}ExchangeRateType1Code" minOccurs="0"/>
 *         &lt;element name="CtrctId" type="{http://www.six-interbank-clearing.com/de/pain.001.001.03.ch.02.xsd}Max35Text" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExchangeRateInformation1", namespace = "http://www.six-interbank-clearing.com/de/pain.001.001.03.ch.02.xsd", propOrder = {
    "xchgRate",
    "rateTp",
    "ctrctId"
})
public class ExchangeRateInformation1 {

    @XmlElement(name = "XchgRate", namespace = "http://www.six-interbank-clearing.com/de/pain.001.001.03.ch.02.xsd")
    protected BigDecimal xchgRate;
    @XmlElement(name = "RateTp", namespace = "http://www.six-interbank-clearing.com/de/pain.001.001.03.ch.02.xsd")
    @XmlSchemaType(name = "string")
    protected ExchangeRateType1Code rateTp;
    @XmlElement(name = "CtrctId", namespace = "http://www.six-interbank-clearing.com/de/pain.001.001.03.ch.02.xsd")
    protected String ctrctId;

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
