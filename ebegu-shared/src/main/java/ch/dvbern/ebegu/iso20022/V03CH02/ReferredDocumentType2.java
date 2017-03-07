
package ch.dvbern.ebegu.iso20022.V03CH02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ReferredDocumentType2 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReferredDocumentType2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CdOrPrtry" type="{http://www.six-interbank-clearing.com/de/pain.001.001.03.ch.02.xsd}ReferredDocumentType1Choice"/>
 *         &lt;element name="Issr" type="{http://www.six-interbank-clearing.com/de/pain.001.001.03.ch.02.xsd}Max35Text" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReferredDocumentType2", namespace = "http://www.six-interbank-clearing.com/de/pain.001.001.03.ch.02.xsd", propOrder = {
    "cdOrPrtry",
    "issr"
})
public class ReferredDocumentType2 {

    @XmlElement(name = "CdOrPrtry", namespace = "http://www.six-interbank-clearing.com/de/pain.001.001.03.ch.02.xsd", required = true)
    protected ReferredDocumentType1Choice cdOrPrtry;
    @XmlElement(name = "Issr", namespace = "http://www.six-interbank-clearing.com/de/pain.001.001.03.ch.02.xsd")
    protected String issr;

    /**
     * Gets the value of the cdOrPrtry property.
     * 
     * @return
     *     possible object is
     *     {@link ReferredDocumentType1Choice }
     *     
     */
    public ReferredDocumentType1Choice getCdOrPrtry() {
        return cdOrPrtry;
    }

    /**
     * Sets the value of the cdOrPrtry property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferredDocumentType1Choice }
     *     
     */
    public void setCdOrPrtry(ReferredDocumentType1Choice value) {
        this.cdOrPrtry = value;
    }

    /**
     * Gets the value of the issr property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIssr() {
        return issr;
    }

    /**
     * Sets the value of the issr property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIssr(String value) {
        this.issr = value;
    }

}
