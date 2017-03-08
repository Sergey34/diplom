package net.sergey.diplom.dto.airfoil;

public class Data {
    private String fileName;
    private String data;
    private double reynolds;
    private double nCrit;
    private double maxClCd;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public double getReynolds() {
        return reynolds;
    }

    public double getnCrit() {
        return nCrit;
    }

    public double getMaxClCd() {
        return maxClCd;
    }
}