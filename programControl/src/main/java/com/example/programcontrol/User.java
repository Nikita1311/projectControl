package com.example.programcontrol;

import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class User {
    String name;
    String password;
    int id;
    List<Project> projects;
    public User(UserBuilder userBuilder)
    {
        this.id= userBuilder.id;
        this.name= userBuilder.name;
        this.password= userBuilder.password;
        this.projects=userBuilder.projects;
    }
    public static class UserBuilder
    {
        String name;
        String password;
        List<Project> projects = new ArrayList<>();
        int id;
        public UserBuilder(String name, String password)
        {
            this.name= name;
            this.password= password;
        }

        public UserBuilder setId(int id) {
            this.id = id;
            return this;
        }
        public UserBuilder setProjects(ObservableList<Project> projects)
        {
            this.projects=projects;
            return this;
        }
        public User build()
        {
            return new User(this);
        }
    }

}
