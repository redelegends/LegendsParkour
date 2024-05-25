package dev.redelegends.parkour.utils;


import dev.redelegends.parkour.database.databases.SQLite;

import java.io.File;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class FileUtils {

    private final String fileName;
    private final String pluginName;

    public FileUtils(String fileName, String pluginName, Class<SQLite> sqLiteClass) {
        this.fileName = fileName;
        this.pluginName = pluginName;
    }

    public File loadFileYamlTermination(String termination) {
        File file = new File("plugins/" + this.pluginName + "/" + this.fileName + "." + termination);
        if (!file.exists()) {
            File folder = file.getParentFile();
            if (!folder.exists()) {
                folder.mkdirs();
            }
        }
        return file;
    }
}
