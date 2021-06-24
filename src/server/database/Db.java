//package server.database;
//
//public class Db {
//    
//    public void createDb() {
//
//        String url = "jdbc:sqlite:C:/sqlite/db/" + fileName;
//
//        try (Connection conn = DriverManager.getConnection(url)) {
//            if (conn != null) {
//                DatabaseMetaData meta = conn.getMetaData();
//                System.out.println("The driver name is " + meta.getDriverName());
//                System.out.println("A new database has been created.");
//            }
//
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
//    }
//}
