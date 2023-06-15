package com.example.programcontrol;

public class File {
    private int id;

    private String path;

    private String format;

    private byte[] file;
    private int versionID;
    public File(String path,String format,byte[] file, int id, int versionID)
    {
        this.file=file;
        this.id=id;
        this.path=path;
        this.versionID=versionID;
        this.format=format;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    public File clone()
    {
        return new File(this.path,this.format,this.file,this.id,this.versionID);
    }
    public byte[] getFile() {
        return file;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFormat() {
        return format;
    }

    public String getPath() {
        return path;
    }

    public int getVersionID() {
        return versionID;
    }

    @Override
    public String toString() {
        return path.split("/")[path.split("/").length-1];
    }
}
