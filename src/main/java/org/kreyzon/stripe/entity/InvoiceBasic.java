package org.kreyzon.stripe.entity;

import javax.persistence.*;

@Entity
public class InvoiceBasic {
    @Id
    @GeneratedValue
    private Long id;


    private String InvoiceNo;
    private String InvoiceStart;
    private String Payment;




    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getInvoiceNo() { // Updated getter method name
        return InvoiceNo;
    }


    public void setInvoiceNo(String invoiceNo) { // Updated setter method name
        InvoiceNo = invoiceNo;
    }


    public String getInvoiceStart() { // Updated getter method name
        return InvoiceStart;
    }


    public void setInvoiceStart(String invoiceStart) { // Updated setter method name
        InvoiceStart = invoiceStart;
    }


    public String getPayment() { // Updated getter method name
        return Payment;
    }


    public void setPayment(String payment) { // Updated setter method name
        Payment = payment;
    }


}
