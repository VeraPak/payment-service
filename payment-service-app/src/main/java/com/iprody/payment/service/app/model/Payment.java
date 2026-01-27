package com.iprody.payment.service.app.model;

public class Payment {
    private long id;
    private double value;

    public Payment(double value, long id)
        this.value = value;
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
