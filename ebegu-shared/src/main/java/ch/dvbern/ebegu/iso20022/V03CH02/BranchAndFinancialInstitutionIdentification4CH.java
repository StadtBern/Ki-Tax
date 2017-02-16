
package ch.dvbern.ebegu.iso20022.V03CH02;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BranchAndFinancialInstitutionIdentification4-CH complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BranchAndFinancialInstitutionIdentification4-CH">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FinInstnId" type="{http://www.six-interbank-clearing.com/de/pain.001.001.03.ch.02.xsd}FinancialInstitutionIdentification7-CH"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BranchAndFinancialInstitutionIdentification4-CH", namespace = "http://www.six-interbank-clearing.com/de/pain.001.001.03.ch.02.xsd", propOrder = {
    "finInstnId"
})
public class BranchAndFinancialInstitutionIdentification4CH {

    @XmlElement(name = "FinInstnId", namespace = "http://www.six-interbank-clearing.com/de/pain.001.001.03.ch.02.xsd", required = true)
    protected FinancialInstitutionIdentification7CH finInstnId;

    /**
     * Gets the value of the finInstnId property.
     * 
     * @return
     *     possible object is
     *     {@link FinancialInstitutionIdentification7CH }
     *     
     */
    public FinancialInstitutionIdentification7CH getFinInstnId() {
        return finInstnId;
    }

    /**
     * Sets the value of the finInstnId property.
     * 
     * @param value
     *     allowed object is
     *     {@link FinancialInstitutionIdentification7CH }
     *     
     */
    public void setFinInstnId(FinancialInstitutionIdentification7CH value) {
        this.finInstnId = value;
    }

}
