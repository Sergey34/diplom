package net.sergey.diplom.service.Parsers.SSU_Table;

import java.util.ArrayList;

public final class ManySubject {
    private ArrayList<Subject> subject = new ArrayList<Subject>();
    private boolean f = false;//флаг наличия в ячейке предмета в нескольких кабинетах

    public void setF(boolean f) {
        this.f = f;
    }

    public void addSubject(Subject subject) {
        this.subject.add(subject);
    }

    public ArrayList<Subject> getSubject() {
        return subject;
    }

    @Override
    public String toString() {
        return "ManySubject{" +
                "subject=" + subject +
                '}';
    }

    public boolean isF() {
        return f;
    }
}
