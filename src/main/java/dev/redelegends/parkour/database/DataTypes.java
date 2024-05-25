package dev.redelegends.parkour.database;

public enum DataTypes {

    MYSQL("MySQL"),
    SQLITE("SQLite");

    private final String name;

    DataTypes(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
