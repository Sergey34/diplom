package net.sergey.diplom.domain.model;

public class Data {
    private String fileName;
    private String data;
    private String reynolds;
    private String nCrit;
    private String maxClCd;

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

    public String getReynolds() {
        return reynolds;
    }

    public String getnCrit() {
        return nCrit;
    }

    public String getMaxClCd() {
        return maxClCd;
    }
}