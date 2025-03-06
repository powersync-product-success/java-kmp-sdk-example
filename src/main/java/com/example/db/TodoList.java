package com.example.db;

public class TodoList {
    public String id;
    public String created_at;
    public String name;
    public String owner_id;

    public TodoList(String id, String name, String created_at, String owner_id) {
        this.name = name;
        this.id = id;
        this.created_at = created_at;
        this.owner_id = owner_id;
    }

    @Override
    public String toString() {
        return "TodoList{" +
                "id='" + id + '\'' +
                ", created_at='" + created_at + '\'' +
                ", name='" + name + '\'' +
                ", owner_id='" + owner_id + '\'' +
                '}';
    }
}
