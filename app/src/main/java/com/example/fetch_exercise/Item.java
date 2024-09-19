package com.example.fetch_exercise;

public class Item {
    private Integer id;
    private Integer listId;
    private String name;

    // constructor to initialize id, listId, and name
    public Item(Integer id, Integer listId, String name) {
        this.id = id;
        this.listId = listId;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public Integer getListId() {
        return listId;
    }

    public String getName() {
        return name;
    }
}
