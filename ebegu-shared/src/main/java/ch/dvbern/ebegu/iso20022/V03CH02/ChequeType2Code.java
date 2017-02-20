
package ch.dvbern.ebegu.iso20022.V03CH02;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ChequeType2Code.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ChequeType2Code">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CCHQ"/>
 *     &lt;enumeration value="CCCH"/>
 *     &lt;enumeration value="BCHQ"/>
 *     &lt;enumeration value="DRFT"/>
 *     &lt;enumeration value="ELDR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ChequeType2Code", namespace = "http://www.six-interbank-clearing.com/de/pain.001.001.03.ch.02.xsd")
@XmlEnum
public enum ChequeType2Code {

    CCHQ,
    CCCH,
    BCHQ,
    DRFT,
    ELDR;

    public String value() {
        return name();
    }

    public static ChequeType2Code fromValue(String v) {
        return valueOf(v);
    }

}
