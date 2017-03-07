package net.sergey.diplom.dto.airfoil;

public class CharacteristicsDto {
    private String coordinatesStl;
    private String fileName;
    private String filePath;
    private String renolgs;
    private String nCrit;
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

    public void setNCrit(String NCrit) {
        this.nCrit = NCrit;
    }

    public String getRenolgs() {
        return renolgs;
    }

    public void setRenolgs(String renolgs) {
        this.renolgs = renolgs;
    }

    public String getnCrit() {
        return nCrit;
    }

    public void setnCrit(String nCrit) {
        this.nCrit = nCrit;
    }

    public String getMaxClCd() {
        return maxClCd;
    }

    public void setMaxClCd(String maxClCd) {
        this.maxClCd = maxClCd;
    }
}
