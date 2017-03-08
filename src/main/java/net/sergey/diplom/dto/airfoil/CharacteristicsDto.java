package net.sergey.diplom.dto.airfoil;

public class CharacteristicsDto {
    private String coordinatesStl;
    private String fileName;
    private String filePath;
    private double renolgs;
    private double nCrit;
    private String maxClCd;


    public CharacteristicsDto() {
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getcoordinatesStl() {
        return coordinatesStl;
    }

    public void setcoordinatesStl(String coordinatesStl) {
        this.coordinatesStl = coordinatesStl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setNCrit(double NCrit) {
        this.nCrit = NCrit;
    }

    public double getRenolgs() {
        return renolgs;
    }

    public void setRenolgs(double renolgs) {
        this.renolgs = renolgs;
    }

    public double getnCrit() {
        return nCrit;
    }

    public void setnCrit(double nCrit) {
        this.nCrit = nCrit;
    }

    public String getMaxClCd() {
        return maxClCd;
    }

    public void setMaxClCd(String maxClCd) {
        this.maxClCd = maxClCd;
    }
}
