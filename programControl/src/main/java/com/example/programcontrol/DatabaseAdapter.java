package com.example.programcontrol;

import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;

public class DatabaseAdapter {
    public static final String DB_URL = "jdbc:h2:/db/projects";
    public static final String DB_Driver = "org.h2.Driver";
    public static final String TableProject="Project";
    public static final String TableUser="User";
    public static final String TableFile="File";
    public static final String TableVersion="Version";

    /**
     * get connection
     */
    public static void getDBConnection() {
        try {
            Class.forName(DB_Driver);
            Connection connection = DriverManager.getConnection(DB_URL);
            DatabaseMetaData md = connection.getMetaData();
            ResultSet rs = md.getTables(null, null, TableProject, null);
            /*deleteTable(TableFile);
           deleteTable(TableVersion);
           deleteTable(TableProject);
           deleteTable(TableUser);*/
            if (!rs.next()) {
                createTableUser();
                createTableProject();
                createTableVersion();
                createTableFile();
            }
            connection.close();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("JDBC драйвер для СУБД не найден!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Ошибка SQL!");
        }
    }


    private static void deleteTable(String Table)
    {
        String deleteTableSQL = "DROP TABLE "+Table;
        try (Connection dbConnection = DriverManager.getConnection(DB_URL)) {
            assert dbConnection != null;
            try (Statement statement = dbConnection.createStatement()) {
                statement.execute(deleteTableSQL);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    private static void createTableProject() {
        String createTableSQL = "CREATE TABLE "+ TableProject+ " ("
                + "ID INT NOT NULL auto_increment, "
                + "Title varchar(100) NOT NULL, "
                + "UserID int NOT NULL, "
                + "PRIMARY KEY (ID), "
                + "FOREIGN KEY (UserID) REFERENCES "+TableUser+" (ID)"
                + ")";
        try (Connection dbConnection = DriverManager.getConnection(DB_URL)) {
            assert dbConnection != null;
            try (Statement statement = dbConnection.createStatement()) {
                statement.execute(createTableSQL);
            }
        } catch (SQLException ignored) {
        }
    }
    private static void createTableVersion() {
        String createTableSQL = "CREATE TABLE "+ TableVersion+ " ("
                + "ID INT NOT NULL auto_increment, "
                + "Title varchar(100) NOT NULL, "
                + "Date DATETIME NOT NULL, "
                + "Fixes varchar(150),"
                + "ProjectID int NOT NULL, "
                + "PRIMARY KEY (ID), "
                + "FOREIGN KEY (ProjectID) REFERENCES "+TableProject+" (ID)"
                + ")";
        try (Connection dbConnection = DriverManager.getConnection(DB_URL)) {
            assert dbConnection != null;
            try (Statement statement = dbConnection.createStatement()) {
                statement.execute(createTableSQL);
            }
        } catch (SQLException ignored) {
        }
    }
    private static void createTableFile() {
        String createTableSQL = "CREATE TABLE "+ TableFile+ " ("
                + "ID INT NOT NULL auto_increment, "
                + "Path varchar(150) NOT NULL, "
                + "Format varchar(100),"
                + "File BLOB not null,"
                + "VersionID int NOT NULL, "
                + "PRIMARY KEY (ID), "
                + "FOREIGN KEY (VersionID) REFERENCES "+TableVersion+" (ID)"
                + ")";
        try (Connection dbConnection = DriverManager.getConnection(DB_URL)) {
            assert dbConnection != null;
            try (Statement statement = dbConnection.createStatement()) {
                statement.execute(createTableSQL);
            }
        } catch (SQLException ignored) {
        }
    }

    private static void createTableUser() {
        String createTableSQL = "CREATE TABLE "+ TableUser+ " ("
                + "ID INT NOT NULL auto_increment, "
                + "Name varchar(20) NOT NULL UNIQUE, "
                + "Password varchar(20) NOT NULL, "
                + "PRIMARY KEY (ID) "
                + ")";
        try (Connection dbConnection = DriverManager.getConnection(DB_URL)) {
            assert dbConnection != null;
            try (Statement statement = dbConnection.createStatement()) {
                statement.execute(createTableSQL);
            }
        } catch (SQLException ignored) {
        }
    }

    public static int getUser(String Name,String Password) {
        String selection = "select * from "+ TableUser+" where Name='"+Name+"' AND Password='"+Password+"'";
        try (Connection dbConnection = DriverManager.getConnection(DB_URL)) {
            assert dbConnection != null;
            try (Statement statement = dbConnection.createStatement()) {
                ResultSet rs = statement.executeQuery(selection);
                if (rs.next()) {
                    return rs.getInt("ID");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return -1;
    }
    public static Project getProject(int ID) {
        String selection = "select * from "+ TableProject+" where ID="+ID;
        try (Connection dbConnection = DriverManager.getConnection(DB_URL)) {
            assert dbConnection != null;
            try (Statement statement = dbConnection.createStatement()) {
                ResultSet rs = statement.executeQuery(selection);
                if (rs.next()) {
                    return new Project(rs.getString("Title"),rs.getInt("ID"),rs.getInt("UserID"));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    public static Version getVersion(int ID) {
        String selection = "select * from "+ TableVersion+" where ID="+ID;
        try (Connection dbConnection = DriverManager.getConnection(DB_URL)) {
            assert dbConnection != null;
            try (Statement statement = dbConnection.createStatement()) {
                ResultSet rs = statement.executeQuery(selection);
                if (rs.next()) {
                    return new Version(rs.getString("Title"),LocalDateTime.of(rs.getDate("Date").toLocalDate(),rs.getTime("Date").toLocalTime()),
                            rs.getString("Fixes"), rs.getInt("ID"),rs.getInt("ProjectID"));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }


    public static User getUser(int id) {
        String selection = "select * from "+ TableUser+" where ID="+id;
        try (Connection dbConnection = DriverManager.getConnection(DB_URL)) {
            assert dbConnection != null;
            try (Statement statement = dbConnection.createStatement()) {
                ResultSet rs = statement.executeQuery(selection);
                if (rs.next()) {
                    return new User.UserBuilder(rs.getString("Name"),
                            rs.getString("Password")).setId(rs.getInt("ID")).build();
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }


    public static void getProjects(ObservableList<Project> elementsDAO,User user) {
        elementsDAO.clear();
        String selection = "select * from "+ TableProject+" where UserID="+user.id;
        try (Connection dbConnection = DriverManager.getConnection(DB_URL)) {
            assert dbConnection != null;
            try (Statement statement = dbConnection.createStatement()) {
                ResultSet rs = statement.executeQuery(selection);
                while (rs.next()) {
                    Project element = new Project(rs.getString("Title"),rs.getInt("ID"),rs.getInt("UserID"));
                    elementsDAO.add(element);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static void getVersions(ObservableList<Version> elementsDAO,Project project) {
        elementsDAO.clear();
        String selection = "select * from "+ TableVersion+" where ProjectID="+project.id;
        try (Connection dbConnection = DriverManager.getConnection(DB_URL)) {
            assert dbConnection != null;
            try (Statement statement = dbConnection.createStatement()) {
                ResultSet rs = statement.executeQuery(selection);
                while (rs.next()) {
                    Version element = new Version(rs.getString("Title"),LocalDateTime.of(rs.getDate("Date").toLocalDate(),rs.getTime("Date").toLocalTime()),
                            rs.getString("Fixes"), rs.getInt("ID"),rs.getInt("ProjectID"));
                    elementsDAO.add(element);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static void getFiles(ObservableList<File> elementsDAO,Version version) {
        elementsDAO.clear();
        String selection = "select * from "+ TableFile+" where VersionID="+version.id;
        try (Connection dbConnection = DriverManager.getConnection(DB_URL)) {
            assert dbConnection != null;
            try (Statement statement = dbConnection.createStatement()) {
                ResultSet rs = statement.executeQuery(selection);
                while (rs.next()) {
                    File element = new File(rs.getString("Path"),rs.getString("Format"),rs.getBytes("File"),rs.getInt("ID"),rs.getInt("VersionID"));
                    elementsDAO.add(element);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }



    public static int addUser(User user) {
        String insertTableSQL = "INSERT INTO "+ TableUser
                + " (Name,Password) " + "VALUES "
                + "('"+user.name+"','"+user.password+"')";
        try (Connection dbConnection = DriverManager.getConnection(DB_URL)) {
            assert dbConnection != null;
            try (Statement statement = dbConnection.createStatement()) {
                statement.executeUpdate(insertTableSQL);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        }
        try (Connection dbConnection = DriverManager.getConnection(DB_URL)) {
            assert dbConnection != null;
            try(Statement statement = dbConnection.createStatement()) {
                ResultSet rs=statement.executeQuery("SELECT TOP 1 ID FROM "+TableUser+" ORDER BY ID DESC");
                rs.next();
                return rs.getInt("ID");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return -1;
    }


    public static int addProject(Project element) {
        String insertTableSQL = "INSERT INTO "+ TableProject
                    + " (Title,UserID) " + "VALUES "
                    + "('"+element.title+"',"+element.owner+")";
        try (Connection dbConnection = DriverManager.getConnection(DB_URL)) {
            assert dbConnection != null;
            try (Statement statement = dbConnection.createStatement()) {
                statement.executeUpdate(insertTableSQL);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        }
        try (Connection dbConnection = DriverManager.getConnection(DB_URL)) {
            assert dbConnection != null;
            try(Statement statement = dbConnection.createStatement()) {
                ResultSet rs=statement.executeQuery("SELECT TOP 1 ID FROM "+TableProject+" ORDER BY ID DESC");
                rs.first();
                return rs.getInt("ID");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return -1;
    }
    public static int addVersion(Version element) {
        String insertTableSQL = "INSERT INTO "+ TableVersion
                + " (Title,Date,Fixes,ProjectID) " + "VALUES "
                + "('"+element.version+"','"+element.dateTime+"','"+element.fixes+"',"+element.project+")";
        try (Connection dbConnection = DriverManager.getConnection(DB_URL)) {
            assert dbConnection != null;
            try (Statement statement = dbConnection.createStatement()) {
                statement.executeUpdate(insertTableSQL);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        try (Connection dbConnection = DriverManager.getConnection(DB_URL)) {
            assert dbConnection != null;
            try(Statement statement = dbConnection.createStatement()) {
                ResultSet rs=statement.executeQuery("SELECT TOP 1 ID FROM "+TableVersion+" ORDER BY ID DESC");
                rs.first();
                return rs.getInt("ID");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return -1;
    }
    public static File addFile(File element) {
        String insertTableSQL = "INSERT INTO "+ TableFile
                + " (File,Path,Format,VersionID) " + "VALUES "
                + "(?,'"+element.getPath()+"','"+element.getFormat()+"',"+element.getVersionID()+")";
        try (Connection dbConnection = DriverManager.getConnection(DB_URL)) {
            assert dbConnection != null;
            try (PreparedStatement statement = dbConnection.prepareStatement(insertTableSQL)) {
                statement.setBytes(1, element.getFile());
                statement.executeUpdate();
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        try (Connection dbConnection = DriverManager.getConnection(DB_URL)) {
            assert dbConnection != null;
            try(Statement statement = dbConnection.createStatement()) {
                ResultSet rs=statement.executeQuery("SELECT TOP 1 ID FROM "+TableFile+" ORDER BY ID DESC");
                rs.next();
                element.setId(rs.getInt("ID"));
                return element;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    public static boolean renameFile(File element) {
        String updateTableSQL = String.format("UPDATE "+ TableFile
                        + " SET Path='%s' where ID=%s;",
                element.getPath(),element.getId());
        try (Connection dbConnection = DriverManager.getConnection(DB_URL)) {
            assert dbConnection != null;
            try (Statement statement = dbConnection.createStatement()) {
                statement.executeUpdate(updateTableSQL);
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
    public static boolean deleteFile(int ID) {
        String deleteTableSQL = String.format("DELETE from "+ TableFile +" WHERE ID=%s;",ID);
        try (Connection dbConnection = DriverManager.getConnection(DB_URL)) {
            assert dbConnection != null;
            try (Statement statement = dbConnection.createStatement()) {
                statement.executeUpdate(deleteTableSQL);
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

}
