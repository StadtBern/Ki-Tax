
package ch.dvbern.ebegu.iso20022;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OrganisationIdentification8 complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="OrganisationIdentification8">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AnyBIC" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.001.08}AnyBICIdentifier" minOccurs="0"/>
 *         &lt;element name="Othr" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.001.08}GenericOrganisationIdentification1" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrganisationIdentification8", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.001.08", propOrder = {
    "anyBIC",
    "othr"
})
public class OrganisationIdentification8 {

    @XmlElement(name = "AnyBIC", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.001.08")
    protected String anyBIC;
    @XmlElement(name = "Othr", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.001.08")
    protected List<GenericOrganisationIdentification1> othr;

    /**
     * Gets the value of the anyBIC property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getAnyBIC() {
        return anyBIC;
    }

    /**
     * Sets the value of the anyBIC property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setAnyBIC(String value) {
        this.anyBIC = value;
    }

    /**
     * Gets the value of the othr property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the othr property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOthr().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GenericOrganisationIdentification1 }
     *
     *
     */
    public List<GenericOrganisationIdentification1> getOthr() {
        if (othr == null) {
            othr = new ArrayList<GenericOrganisationIdentification1>();
        }
        return this.othr;
    }

}
