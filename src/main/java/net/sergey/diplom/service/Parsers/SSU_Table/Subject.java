package net.sergey.diplom.service.Parsers.SSU_Table;

final class Subject {
    private String CHtitle = "",//название предмета
            CHname = "",//имя препода
            CHaudit = "",//аудитори
            CHlecPr = "",//лекция/практика
            CHtype = "",//доп классификация
            CHchisZnam = "";//числитель/знаменатель
    private String Ztitle = "",//название предмета
            Zname = "",//имя препода
            Zaudit = "",//аудитори
            ZlecPr = "",//лекция/практика
            Ztype = "",//доп классификация
            ZchisZnam = "";//числитель/знаменатель
    private String title = "",//название предмета
            name = "",//имя препода
            audit = "",//аудитори
            lecPr = "",//лекция/практика
            numberGroup = "";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAudit() {
        return audit;
    }

    public void setAudit(String audit) {
        this.audit = audit;
    }

    public String getLecPr() {
        return lecPr;
    }

    public void setLecPr(String lecPr) {
        this.lecPr = lecPr;
    }

    public String getNumberGroup() {
        return numberGroup;
    }

    public void setNumberGroup(String numberGroup) {
        this.numberGroup = numberGroup;
    }

    public String getCHtitle() {
        return CHtitle;
    }

    public void setCHtitle(String CHtitle) {
        this.CHtitle = CHtitle;
    }

    public String getCHname() {
        return CHname;
    }

    public void setCHname(String CHname) {
        this.CHname = CHname;
    }

    public String getCHaudit() {
        return CHaudit;
    }

    public void setCHaudit(String CHaudit) {
        this.CHaudit = CHaudit;
    }

    public String getCHlecPr() {
        return CHlecPr;
    }

    public void setCHlecPr(String CHlecPr) {
        this.CHlecPr = CHlecPr;
    }

    public String getCHtype() {
        return CHtype;
    }

    public void setCHtype(String CHtype) {
        this.CHtype = CHtype;
    }

    public String getCHchisZnam() {
        return CHchisZnam;
    }

    public void setCHchisZnam(String CHchisZnam) {
        this.CHchisZnam = CHchisZnam;
    }

    public String getZtitle() {
        return Ztitle;
    }

    public void setZtitle(String ztitle) {
        Ztitle = ztitle;
    }

    public String getZname() {
        return Zname;
    }

    public void setZname(String zname) {
        Zname = zname;
    }

    public String getZaudit() {
        return Zaudit;
    }

    public void setZaudit(String zaudit) {
        Zaudit = zaudit;
    }

    public String getZlecPr() {
        return ZlecPr;
    }

    public void setZlecPr(String zlecPr) {
        ZlecPr = zlecPr;
    }

    public String getZtype() {
        return Ztype;
    }

    public void setZtype(String ztype) {
        Ztype = ztype;
    }

    public String getZchisZnam() {
        return ZchisZnam;
    }

    public void setZchisZnam(String zchisZnam) {
        ZchisZnam = zchisZnam;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "CHtitle='" + CHtitle + '\'' +
                ", CHname='" + CHname + '\'' +
                ", CHaudit='" + CHaudit + '\'' +
                ", CHlecPr='" + CHlecPr + '\'' +
                ", CHtype='" + CHtype + '\'' +
                ", CHchisZnam='" + CHchisZnam + '\'' +
                ", Ztitle='" + Ztitle + '\'' +
                ", Zname='" + Zname + '\'' +
                ", Zaudit='" + Zaudit + '\'' +
                ", ZlecPr='" + ZlecPr + '\'' +
                ", Ztype='" + Ztype + '\'' +
                ", ZchisZnam='" + ZchisZnam + '\'' +
                ", title='" + title + '\'' +
                ", name='" + name + '\'' +
                ", audit='" + audit + '\'' +
                ", lecPr='" + lecPr + '\'' +
                ", numberGroup='" + numberGroup + '\'' +
                '}';
    }
}