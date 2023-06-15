package com.example.programcontrol;

import java.util.List;

public class Project {
    List<Version> versions;
    String title;
    int id;
    int owner;
    public Project(String title,int id, int owner)
    {
        this.title = title;
        this.id = id;
        this.owner=owner;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Version> getVersions() {
        return versions;
    }
    public void addVersion(String title)
    {

    }

    public void setVersions(List<Version> versions) {
        this.versions = versions;
    }
    public String toString()
    {
        return title;
    }
}
