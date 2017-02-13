
package ch.dvbern.ebegu.iso20022;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for InstructionForCreditorAgent1 complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="InstructionForCreditorAgent1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Cd" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.001.08}Instruction3Code" minOccurs="0"/>
 *         &lt;element name="InstrInf" type="{urn:iso:std:iso:20022:tech:xsd:pain.001.001.08}Max140Text" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InstructionForCreditorAgent1", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.001.08", propOrder = {
    "cd",
    "instrInf"
})
public class InstructionForCreditorAgent1 {

    @XmlElement(name = "Cd", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.001.08")
    @XmlSchemaType(name = "string")
    protected Instruction3Code cd;
    @XmlElement(name = "InstrInf", namespace = "urn:iso:std:iso:20022:tech:xsd:pain.001.001.08")
    protected String instrInf;

    /**
     * Gets the value of the cd property.
     *
     * @return
     *     possible object is
     *     {@link Instruction3Code }
     *
     */
    public Instruction3Code getCd() {
        return cd;
    }

    /**
     * Sets the value of the cd property.
     *
     * @param value
     *     allowed object is
     *     {@link Instruction3Code }
     *
     */
    public void setCd(Instruction3Code value) {
        this.cd = value;
    }

    /**
     * Gets the value of the instrInf property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getInstrInf() {
        return instrInf;
    }

    /**
     * Sets the value of the instrInf property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setInstrInf(String value) {
        this.instrInf = value;
    }

}
