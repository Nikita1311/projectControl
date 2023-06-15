package com.example.programcontrol;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Objects;

public class ProjectsController{
    public ListView<Project> projectsList;
    public ListView<Version> versions;
    public TextField version;
    public TextField fixes;
    public ListView<com.example.programcontrol.File> filesList;
    public TextField newName;

    User user;
    public ObservableList<Version> versionsList= FXCollections.observableArrayList();
    ObservableList<Project> projects =FXCollections.observableArrayList();
    ObservableList<com.example.programcontrol.File> files=FXCollections.observableArrayList();
    public void init(int id)
    {
        DatabaseAdapter.getDBConnection();
        user = DatabaseAdapter.getUser(id);
        assert user != null;
        DatabaseAdapter.getProjects(projects,user);
        projectsList.setItems(projects);
        InvalidationListener listener = e->{
            DatabaseAdapter.getFiles(files,versions.getSelectionModel().getSelectedItem());
            filesList.setItems(files);
        };
        projectsList.getSelectionModel().selectedItemProperty().addListener(e->{
            DatabaseAdapter.getVersions(versionsList,projectsList.getSelectionModel().getSelectedItem());
            versions.setItems(versionsList);
            versions.getSelectionModel().selectedItemProperty().removeListener(listener);
            versions.getSelectionModel().selectedItemProperty().addListener(listener);
        });
    }
    @FXML
    private void loadVersion() throws IOException {
        int id;
        if(!projectsList.getSelectionModel().isEmpty()) {
            id=DatabaseAdapter.addVersion(new Version(version.getText(), LocalDateTime.now(), fixes.getText(), -1, projectsList.getSelectionModel().getSelectedItem().id));
            Version version1 = DatabaseAdapter.getVersion(id);
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Выбор папки с проектом");
            File selectedDirectory = directoryChooser.showDialog(null);
            if(selectedDirectory!=null) {
                for (File file : Objects.requireNonNull(selectedDirectory.listFiles())) {
                    if (file.isDirectory()) {
                        filesInDirectory(file, version1, file.getParentFile().getName() + "/");
                    } else
                        DatabaseAdapter.addFile(new com.example.programcontrol.File(file.getParentFile().getName() + "/" + file.getName(), file.getName().substring(file.getName().lastIndexOf(".") + 1), IOUtils.toByteArray(new FileInputStream(file)), -1, version1.id));
                }
                DatabaseAdapter.getVersions(versionsList, projectsList.getSelectionModel().getSelectedItem());
            }
        }
    }
    @FXML
    private void addFile() throws IOException {
        if(!versions.getSelectionModel().isEmpty())
        {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выбор файла");
            File selectedFile = fileChooser.showOpenDialog(null);
            if(selectedFile!=null) {
                String prj = filesList.getSelectionModel().getSelectedItem().getPath().substring(0,filesList.getSelectionModel().getSelectedItem().getPath().indexOf("/")+1);
                com.example.programcontrol.File temp =DatabaseAdapter.addFile(new com.example.programcontrol.File(prj+selectedFile.getName(),selectedFile.getName().substring(selectedFile.getName().lastIndexOf(".") + 1), IOUtils.toByteArray(new FileInputStream(selectedFile)), -1, versions.getSelectionModel().getSelectedItem().id));
                files.add(temp);
                filesList.refresh();
            }
        }
    }
    @FXML
    private void removeFile()
    {
        if(!filesList.getSelectionModel().isEmpty())
        {
            DatabaseAdapter.deleteFile(filesList.getSelectionModel().getSelectedItem().getId());
            files.remove(filesList.getSelectionModel().getSelectedItem());
            filesList.refresh();
        }
    }
    @FXML
    private void renameFile()
    {
        if(!filesList.getSelectionModel().isEmpty())
        {
            String path=filesList.getSelectionModel().getSelectedItem().getPath().substring(0,filesList.getSelectionModel().getSelectedItem().getPath().lastIndexOf("/")+1);
            filesList.getSelectionModel().getSelectedItem().setPath(path+newName.getText()+"."+filesList.getSelectionModel().getSelectedItem().getFormat() );
            DatabaseAdapter.renameFile(filesList.getSelectionModel().getSelectedItem());
            filesList.refresh();
        }
    }
    @FXML
    private void copy()
    {
        if(!filesList.getSelectionModel().isEmpty())
        {
            files.add(DatabaseAdapter.addFile(filesList.getSelectionModel().getSelectedItem().clone()));
            filesList.refresh();
        }
    }
    @FXML
    private void loadProject() throws IOException {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Выбор папки с проектом");
        File selectedDirectory = directoryChooser.showDialog(null);
        if(selectedDirectory!=null) {
            Project project1;
            Version version1;
            project1=DatabaseAdapter.getProject(DatabaseAdapter.addProject(new Project(selectedDirectory.getName(),-1,user.id)));
            assert project1 != null;
            version1=DatabaseAdapter.getVersion(DatabaseAdapter.addVersion(new Version("1.0",LocalDateTime.now(),"проект создан",-1, project1.id)));
            for (File file: Objects.requireNonNull(selectedDirectory.listFiles())) {
                if(file.isDirectory())
                {
                    filesInDirectory(file,version1,file.getParentFile().getName()+"/");
                }
                else {
                    assert version1 != null;
                    DatabaseAdapter.addFile(new com.example.programcontrol.File(file.getParentFile().getName()+"/"+file.getName(),file.getName().substring(file.getName().lastIndexOf(".") + 1), IOUtils.toByteArray(new FileInputStream(file)),-1,version1.id));
                }
            }
            DatabaseAdapter.getProjects(projects,user);
        }
    }
    private void filesInDirectory(File file,Version version1,String parentPath) throws IOException {
        for (File file1: Objects.requireNonNull(file.listFiles())) {
            if(file1.isDirectory())
            {
                filesInDirectory(file1,version1,parentPath+file1.getParentFile().getName()+"/");
            }
            else DatabaseAdapter.addFile(new com.example.programcontrol.File(parentPath+file1.getName(),file1.getName().substring(file1.getName().lastIndexOf(".") + 1), IOUtils.toByteArray(new FileInputStream(file1)),-1,version1.id));
        }
    }
    @FXML
    private void unloadProject() throws IOException {
        ObservableList<com.example.programcontrol.File> files=FXCollections.observableArrayList();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("src"));
        directoryChooser.setTitle("Выбор папки для проекта");
        File selectedDirectory = directoryChooser.showDialog(null);
        if(selectedDirectory!=null) {
            DatabaseAdapter.getFiles(files,versions.getSelectionModel().getSelectedItem());
            for (com.example.programcontrol.File file: files) {
                File newFile = new File(selectedDirectory.getAbsolutePath()+"/"+file.getPath());
                newFile.getParentFile().mkdirs();
                if(!newFile.exists()) {
                    Files.createFile(newFile.toPath());
                    writeByte(file.getFile(), newFile);
                }
            }

        }
    }

    static void writeByte(byte[] bytes, File file)
    {
        try {
            OutputStream os = new FileOutputStream(file);
            os.write(bytes);
            os.close();
        }
        catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }

}
