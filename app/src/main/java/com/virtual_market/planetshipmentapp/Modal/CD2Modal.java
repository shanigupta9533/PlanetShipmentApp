package com.virtual_market.planetshipmentapp.Modal;

import java.util.ArrayList;

public class CD2Modal {

    public String success,message;

    public ArrayList<InfoDocType> Info;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<InfoDocType> getInfo() {
        return Info;
    }

    public void setInfo(ArrayList<InfoDocType> info) {
        Info = info;
    }
}
