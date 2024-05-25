package dev.redelegends.parkour.database;

import dev.redelegends.parkour.Main;
import dev.redelegends.parkour.database.databases.MySQL;
import dev.redelegends.parkour.database.databases.SQLite;
import dev.redelegends.parkour.database.interfaces.DatabaseInterface;

public abstract class DataBase {

    private static DatabaseInterface<? extends DataBase> databse;

    public static void setupDataBases(DataTypes dataTypes, Main main) {
        if (main.getConfig().getBoolean("bungeemode")) {
            if (dataTypes == DataTypes.SQLITE) {
                main.sendMessage("Foi ativado o MySQL pelo fato do plugin estar com o modo BungeeMode ativo!", "e");
            }
            dataTypes = DataTypes.MYSQL;
        }

        if (dataTypes.equals(DataTypes.MYSQL)) {
            MySQL mySQL = new MySQL(main);
            mySQL.setupDataBase();
            mySQL.createDefaultTables();
            databse = mySQL;
        } else {
            SQLite sqLite = new SQLite(main);
            sqLite.setupDataBase();
            sqLite.createDefaultTables();
            databse = sqLite;
        }

        main.sendMessage("Você escolheu a database do tipo: " + dataTypes.getName(), "6");
    }

    @SuppressWarnings("unchecked")
    public static <T extends DataBase> T getDatabase(Class<T> databaseClass) {
        return databse != null && databaseClass.isAssignableFrom(databse.getClass()) ? (T) databse : null;
    }
}
