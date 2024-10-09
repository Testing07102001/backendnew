package org.kreyzon.stripe.entity;

import javax.persistence.Embeddable;

@Embeddable
public class CriteriaDetails {
    private String criteria;
    private String label;
    private String c_nyc;
    private String evidences;

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getC_nyc() {
        return c_nyc;
    }

    public void setC_nyc(String c_nyc) {
        this.c_nyc = c_nyc;
    }

    public String getEvidences() {
        return evidences;
    }

    public void setEvidences(String evidences) {
        this.evidences = evidences;
    }

}
