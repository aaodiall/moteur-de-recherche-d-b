package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class testSQLite {
	
	public static void main(String[] args) {
		
        // TODO Auto-generated method stub
        try {
            // 0 The JDBC connection SQLite
            String sql="jdbc:sqlite:///home/alpha/sqlite/tim.db";
            Class.forName("org.sqlite.JDBC");

            // 1Establish a database connection name zieckey.db, if it does not exist create it in the current directory
            Connection conn = DriverManager.getConnection(sql);
            Statement stat = conn.createStatement();
            
            // 2 Create a table tbl1, insert data 
            stat.executeUpdate("drop table if exists tbl2;");
            stat.executeUpdate("create virtual table tbl2 using FTS3(name,body);");// Create a table
          /*  stat.executeUpdate("insert into tbl2(name,body) values('text1','je suis a l ecole 1');"); // insert data
            stat.executeUpdate("insert into tbl2(name,body) values('text2','je suis a l ecole2');"); // insert data
            stat.executeUpdate("insert into tbl2(name,body) values('text3','je suis a l ecole3');"); // insert data
            stat.executeUpdate("insert into tbl2(name,body) values('text4','je suis a l ecole 4');"); // insert data*/
            ResultSet rs = stat.executeQuery("select * from tbl2 where body match 'ecole';"); // query data
            System.out.println("Create a table structure and insert data operation and demonstration:");
            while (rs.next()) { // print data
                System.out.print("name = " + rs.getString("name") + ", "); // Column attribute 
                System.out.println("body = " + rs.getString("body")); // Column attribute
            }
            rs.close();
            
            
            // 3 Modify table structure, add fields address varchar(20) default 'changsha';
            stat.executeUpdate("alter table tbl1 add column address varchar(20) not null default 'changsha'; ");//create a table
            stat.executeUpdate("insert into tbl1 values('HongQi',9000,'tianjing');"); // insert data
            stat.executeUpdate("insert into tbl1(name,salary) values('HongQi',9000);"); // insert data
            rs = stat.executeQuery("select * from tbl1;"); // query data
            System.out.println(" Table structure change operation and demonstration: ");
            while (rs.next()) { //print data
                System.out.print("name = " + rs.getString("name") + ", "); // Column attribute
                System.out.print("name = " + rs.getString("name") + ", "); // Column attribute
                System.out.println("address = " + rs.getString("address")); // Column attribute
            }
            rs.close();
            
            conn.close(); // close database
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
