package com.example.programcontrol;

import java.time.LocalDateTime;
import java.util.List;

public class Version {
    List<File> files;
    String version;
    String fixes;
    int id;
    int project;
    LocalDateTime dateTime;
    public Version(String version, LocalDateTime dateTime,String fixes,int id,int project)
    {
        this.version = version;
        this.dateTime = dateTime;
        this.fixes = fixes;
        this.id = id;
        this.project = project;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<File> getFiles() {
        return files;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }
    public String toString()
    {
        return version+"("+dateTime+"): "+fixes;
    }
}
