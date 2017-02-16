
package ch.dvbern.ebegu.iso20022.V03CH02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AccountIdentification4Choice-CH complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AccountIdentification4Choice-CH">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="IBAN" type="{http://www.six-interbank-clearing.com/de/pain.001.001.03.ch.02.xsd}IBAN2007Identifier"/>
 *           &lt;element name="Othr" type="{http://www.six-interbank-clearing.com/de/pain.001.001.03.ch.02.xsd}GenericAccountIdentification1-CH"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AccountIdentification4Choice-CH", namespace = "http://www.six-interbank-clearing.com/de/pain.001.001.03.ch.02.xsd", propOrder = {
    "iban",
    "othr"
})
public class AccountIdentification4ChoiceCH {

    @XmlElement(name = "IBAN", namespace = "http://www.six-interbank-clearing.com/de/pain.001.001.03.ch.02.xsd")
    protected String iban;
    @XmlElement(name = "Othr", namespace = "http://www.six-interbank-clearing.com/de/pain.001.001.03.ch.02.xsd")
    protected GenericAccountIdentification1CH othr;

    /**
     * Gets the value of the iban property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIBAN() {
        return iban;
    }

    /**
     * Sets the value of the iban property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIBAN(String value) {
        this.iban = value;
    }

    /**
     * Gets the value of the othr property.
     * 
     * @return
     *     possible object is
     *     {@link GenericAccountIdentification1CH }
     *     
     */
    public GenericAccountIdentification1CH getOthr() {
        return othr;
    }

    /**
     * Sets the value of the othr property.
     * 
     * @param value
     *     allowed object is
     *     {@link GenericAccountIdentification1CH }
     *     
     */
    public void setOthr(GenericAccountIdentification1CH value) {
        this.othr = value;
    }

}
