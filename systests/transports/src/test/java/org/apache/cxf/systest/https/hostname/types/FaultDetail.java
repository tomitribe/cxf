
package org.apache.cxf.systest.https.hostname.types;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="minor" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0" form="qualified"/&gt;
 *         &lt;element name="major" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0" form="qualified"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "minor",
    "major"
})
@XmlRootElement(name = "faultDetail")
@Generated(value = "com.sun.tools.xjc.Driver", date = "2018-07-05T11:48:08-03:00", comments = "JAXB RI v2.2.11")
public class FaultDetail {

    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-07-05T11:48:08-03:00", comments = "JAXB RI v2.2.11")
    protected Short minor;
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-07-05T11:48:08-03:00", comments = "JAXB RI v2.2.11")
    protected Short major;

    /**
     * Gets the value of the minor property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-07-05T11:48:08-03:00", comments = "JAXB RI v2.2.11")
    public Short getMinor() {
        return minor;
    }

    /**
     * Sets the value of the minor property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-07-05T11:48:08-03:00", comments = "JAXB RI v2.2.11")
    public void setMinor(Short value) {
        this.minor = value;
    }

    /**
     * Gets the value of the major property.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-07-05T11:48:08-03:00", comments = "JAXB RI v2.2.11")
    public Short getMajor() {
        return major;
    }

    /**
     * Sets the value of the major property.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2018-07-05T11:48:08-03:00", comments = "JAXB RI v2.2.11")
    public void setMajor(Short value) {
        this.major = value;
    }

}
