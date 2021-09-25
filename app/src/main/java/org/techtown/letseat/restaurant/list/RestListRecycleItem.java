package org.techtown.letseat.restaurant.list;

public class RestListRecycleItem {

    private int src;
    private String genre;
    private String name;
    private String adress;

    public RestListRecycleItem(int src, String genre, String name, String adress) {

        this.src = src;
        this.genre = genre;
        this.name = name;
        this.adress = adress;

    }

    public int getSrc() {
        return src;
    }

    public String getGenre() {
        return genre;
    }

    public String getName() {
        return name;
    }

    public String getAdress() {
        return adress;
    }
}
