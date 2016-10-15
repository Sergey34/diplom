package net.sergey.diplom.service.Parsers;


import net.sergey.diplom.service.Parsers.SSU_Table.ManySubject;
import net.sergey.diplom.service.Parsers.homeCinema.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public enum Hash {
    INSTANCE;

    private final Map<String, List<Film>> hashCinema = new HashMap<String, List<Film>>();
    private final Map<String, ManySubject[][]> hashTable = new HashMap<String, ManySubject[][]>();
    private Map<String, String> hashGroups = new HashMap<String, String>();

    public Map<String, String> getHashGroups() {
        return new HashMap<String, String>(hashGroups);
    }

    public void setHashGroups(Map<String, String> hashGroups) {
        this.hashGroups = new HashMap<String, String>(hashGroups);
    }

    public Map<String, ManySubject[][]> getHashTable() {
        return new HashMap<String, ManySubject[][]>(hashTable);
    }

    public Map<String, List<Film>> getHashCinema() {
        return new HashMap<String, List<Film>>(hashCinema);
    }

    public void putAllToHashCinema(Map<String, List<Film>> result) {
        hashCinema.putAll(result);
    }

    public void putAllToHashTable(Map<String, ManySubject[][]> result) {
        hashTable.putAll(result);
    }


}
