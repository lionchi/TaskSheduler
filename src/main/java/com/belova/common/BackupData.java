package com.belova.common;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Scope("prototype")
public class BackupData implements Runnable {

    private String pathDump;
    private String pathSaveDir;
    private String user;
    private String password;
    private String host;
    private String port;
    private String database;

    public BackupData(String pathDump, String pathSaveDir, String user, String password,
                      String host, String port, String database) {
        this.pathDump = pathDump;
        this.pathSaveDir = pathSaveDir;
        this.user = user;
        this.password = password;
        this.host = host;
        this.port = port;
        this.database = database;
    }

    @Override
    public void run() {
        checkOldFile();
        backupDataWithOutDatabase();
    }

    private void checkOldFile() {
        File file = new File(pathSaveDir);
        if (file.listFiles().length > 2) {
            Map<String, Date> map = new HashMap<>();
            for (File file1 : file.listFiles()) {
                map.put(file1.getAbsolutePath(), new Date(file1.lastModified()));
            }
            Date last = map.values().stream().min(Date::compareTo).get();
            Optional<String> deleteFile = map.entrySet().stream()
                    .filter(e -> e.getValue() == last)
                    .map(Map.Entry::getKey)
                    .findFirst();
            deleteFile.ifPresent(s -> new File(s).delete());
        }
    }

    public void backupDataWithOutDatabase() {
        boolean status = false;
        try {
            Process p = null;

            DateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
            Date date = new Date();
            String filepath = "backup " + database + "-" + host + "-(" + dateFormat.format(date) + ").sql";

            String batchCommand = "";
            if (password != "") {
                //only backup the data not included create database
                batchCommand = pathDump + " -h " + host + " --port " + port + " -u " + user + " --password=" + password + " " + database + " -r \"" + pathSaveDir + "" + filepath + "\"";
            } else {
                batchCommand = pathDump + " -h " + host + " --port " + port + " -u " + user + " " + database + " -r \"" + pathSaveDir + "" + filepath + "\"";
            }

            Runtime runtime = Runtime.getRuntime();
            p = runtime.exec(batchCommand);
            int processComplete = p.waitFor();

            if (processComplete == 0) {
                status = true;
            } else {
                status = false;
            }

        } catch (Exception e) {
            //  log.error(e, e.getCause());
        }
    }
}
