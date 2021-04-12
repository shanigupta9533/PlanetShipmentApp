package com.virtual_market.planetshipmentapp.Modal;

public class LatLng {

    private Double longitude;
    private Double latitude;

    public LatLng(double latitude, double longitude) {

        this.latitude=latitude;
        this.longitude=longitude;

    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLattitude() {
        return latitude;
    }

    public void setLattitude(Double lattitude) {
        this.latitude = lattitude;
    }
}
