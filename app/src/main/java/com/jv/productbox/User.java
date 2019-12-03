package com.jv.productbox;

import java.io.Serializable;

public class User implements Serializable {

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getRoid() {
        return roid;
    }

    public void setRoid(int roid) {
        this.roid = roid;
    }

    private String account;
    private int roid;

}
