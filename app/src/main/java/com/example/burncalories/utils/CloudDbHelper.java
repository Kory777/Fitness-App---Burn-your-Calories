package com.example.burncalories.utils;



import android.util.Log;

import com.example.burncalories.Account;
import com.example.burncalories.DayDistance;
import com.example.burncalories.DayStep;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CloudDbHelper {
    // 2.设置好IP/端口/数据库名/用户名/密码等必要的连接信息
    private static String ip = "cdb-qb1ncrmy.bj.tencentcdb.com";
    private static int port = 10242;
    private static String dbName = "test";
    private static final String URL = "jdbc:mysql://" + ip + ":" + port
            + "/" + dbName + "?useSSL=false"; // 构建连接mysql的字符串
    private static final String USER = "root";
    private static final String PASSWORD = "Huyuang1998";
    private static final String ACCOUNT_TABLE = "account";
    private static final String DAY_STEP_TABLE = "day_step";
    private static final String DAY_DISTANCE_TABLE = "day_distance";
    private static final String TAG = "CloudDbHelper";
    private boolean networkOK = false;
    private static final int INSERT_MODE = 6666;
    private static final int UPDATE_MODE = 7777;
    private static final int ERROR_MODE = 4444;

    private Map<String, String> accountNames;
    private List<DayStep> steps;
    private List<DayDistance> distances;

    private Connection conn;
    private Statement st;

    public CloudDbHelper(){

    }

    public Connection getConnection(){
        // 1.加载JDBC驱动
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    Log.v(TAG, "加载JDBC驱动成功");
                } catch (ClassNotFoundException e) {
                    Log.e(TAG, "加载JDBC驱动失败");
                }
                try {
                    conn = DriverManager.getConnection(URL, USER, PASSWORD);
                } catch (SQLException e) {
                    e.printStackTrace();
                    Log.e(TAG, "远程连接失败");
                }
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public boolean insertAccount(String account, String password){
        new Thread(){
            @Override
            public void run() {
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    Log.v(TAG, "加载JDBC驱动成功");
                } catch (ClassNotFoundException e) {
                    Log.e(TAG, "加载JDBC驱动失败");
                }
                try {
                    conn = DriverManager.getConnection(URL, USER, PASSWORD);
                } catch (SQLException e) {
                    e.printStackTrace();
                    Log.e(TAG, "远程连接失败");
                }


                try {
                    Statement st = conn.createStatement();
                    String sql = "INSERT INTO account(account_name, account_password)"+
                            " VALUES(?, ?)";
                    PreparedStatement ptmt = conn.prepareStatement(sql);
                    ptmt.setString(1, account);
                    ptmt.setString(2, password);

                    ptmt.execute();
                    conn.close();
                    networkOK = true;
                } catch (SQLException e) {
                    e.printStackTrace();
                    Log.e(TAG, "");
                }
            }
        }.start();
        return networkOK;
    }
    public void updateHeadShot(String account, byte[] image){
            new Thread(){
            @Override
            public void run() {
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    Log.v(TAG, "加载JDBC驱动成功");
                } catch (ClassNotFoundException e) {
                    Log.e(TAG, "加载JDBC驱动失败");
                }
                try {
                    conn = DriverManager.getConnection(URL, USER, PASSWORD);
                } catch (SQLException e) {
                    e.printStackTrace();
                    Log.e(TAG, "远程连接失败");
                }


                try {
                    Statement st = conn.createStatement();
                    String sql = "UPDATE account SET "+
                            " headshot = ? where account_name = ?";

                    PreparedStatement ptmt = conn.prepareStatement(sql);

                    Blob headshot=conn.createBlob();
                    OutputStream os=headshot.setBinaryStream(1);

                    os.write(image);
                    os.close();
                    ptmt.setBlob(1, headshot);
                    ptmt.setString(2, account);
                    ptmt.executeUpdate();
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    Log.e(TAG, "");
                } catch (IOException e) {
                    Log.e(TAG,"写入文件失败");
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public byte[] queryHeadShot(String name){
        byte []headshot = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Log.v(TAG, "加载JDBC驱动成功");
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "加载JDBC驱动失败");
        }
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            String sql = "select * from " + ACCOUNT_TABLE + " where account_name = ?";     // 查询数据的sql语句
            PreparedStatement ps  = conn.prepareStatement(sql); //创建用于执行静态sql语句的Statement对象，st属局部变量
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();    //执行sql查询语句，返回查询数据的结果集
            System.out.println("最后的查询结果为：");
            while (rs.next()) { // 判断是否还有下一个数据
                Blob b = rs.getBlob("headshot");
                if(b == null){
                    Log.e(TAG,"云数据库内是空的");
                    return null;
                }else {
                    Log.e(TAG,"云数据库加载成功");
                    InputStream is = b.getBinaryStream();
                    headshot = new byte[is.available()];
                    is.read(headshot);
                }
            }
            conn.close();   //关闭数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "远程连接失败");
        } catch (IOException e) {
            Log.e(TAG, "IO失败");
            e.printStackTrace();
        }
        return headshot;
    }

    public Map<String, String> queryAccountNames(){

        accountNames = new HashMap<String, String>();

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Log.v(TAG, "加载JDBC驱动成功");
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "加载JDBC驱动失败");
        }
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            String sql = "select * from " + ACCOUNT_TABLE;     // 查询数据的sql语句
            st = conn.createStatement(); //创建用于执行静态sql语句的Statement对象，st属局部变量
            ResultSet rs = st.executeQuery(sql);    //执行sql查询语句，返回查询数据的结果集
            System.out.println("最后的查询结果为：");
            while (rs.next()) { // 判断是否还有下一个数据
                String accountName = rs.getString("account_name");
                String accountPassword = rs.getString("account_password");
                accountNames.put(accountName, accountPassword);
            }
            conn.close();   //关闭数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "远程连接失败");
        }
        return accountNames;
    }

    public List<DayStep> queryUserStepByDate(String date){
        steps = new ArrayList<DayStep>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Log.v(TAG, "加载JDBC驱动成功");
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "加载JDBC驱动失败");
        }
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            String sql = "select * from " + DAY_STEP_TABLE + " where date = ?";     // 查询数据的sql语句
            PreparedStatement ps = conn.prepareStatement(sql); //创建用于执行静态sql语句的Statement对象，st属局部变量
            ps.setString(1, date);
            ResultSet rs = ps.executeQuery();    //执行sql查询语句，返回查询数据的结果集
            System.out.println("最后的查询结果为：");
            while (rs.next()) { // 判断是否还有下一个数据
                String name = rs.getString("account_name");
                int step = rs.getInt("step");
                DayStep dayStep = new DayStep(date, step, name);
                steps.add(dayStep);
            }
            conn.close();   //关闭数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "远程连接失败");
        }
        return steps;

    }

    public List<Account> queryAccounts(){
        List<Account> accounts = new ArrayList();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Log.v(TAG, "加载JDBC驱动成功");
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "加载JDBC驱动失败");
        }
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            String sql = "select * from " + ACCOUNT_TABLE;     // 查询数据的sql语句
            st = conn.createStatement(); //创建用于执行静态sql语句的Statement对象，st属局部变量
            ResultSet rs = st.executeQuery(sql);    //执行sql查询语句，返回查询数据的结果集
            System.out.println("最后的查询结果为：");
            while (rs.next()) { // 判断是否还有下一个数据
                String accountName = rs.getString("account_name");
                Account account = new Account(accountName);
                Blob b = rs.getBlob("headshot");
                if(b == null){
                    Log.e(TAG,"云数据库内的图片是空的");
                }else {
                    Log.e(TAG,"云数据库加载成功");
                    InputStream is = b.getBinaryStream();
                    byte[] headshot = new byte[is.available()];
                    is.read(headshot);
                    account.setHeadshot(headshot);
                }
                accounts.add(account);
            }
            conn.close();   //关闭数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "远程连接失败");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "文件读写失败");
        }
        return accounts;
    }

    public List<DayDistance> queryUserDitanceByDate(String date){
        distances = new ArrayList<DayDistance>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Log.v(TAG, "加载JDBC驱动成功");
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "加载JDBC驱动失败");
        }
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            String sql = "select * from " + DAY_DISTANCE_TABLE + " where date = ?";     // 查询数据的sql语句
            PreparedStatement ps = conn.prepareStatement(sql); //创建用于执行静态sql语句的Statement对象，st属局部变量
            ps.setString(1, date);
            ResultSet rs = ps.executeQuery();    //执行sql查询语句，返回查询数据的结果集
            System.out.println("最后的查询结果为：");
            while (rs.next()) { // 判断是否还有下一个数据
                String name = rs.getString("account_name");
                int distance = rs.getInt("distance");
                DayDistance dayDistance = new DayDistance(distance, date, name);
                distances.add(dayDistance);
            }
            conn.close();   //关闭数据库连接
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "远程连接失败");
        }
        return distances;

    }

    /**
     * 查看云数据库内有无该条数据,进而选择更新还是插入操作
     * @param accountName
     * @param date
     * @return
     */
    public void updateStepInCloud(String accountName, String date, int step){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Log.v(TAG, "加载JDBC驱动成功");
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "加载JDBC驱动失败");
        }
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            String sql = "select * from " + DAY_STEP_TABLE + " where account_name = ? and date = ?";     // 查询数据的sql语句
            PreparedStatement ps = conn.prepareStatement(sql); //创建用于执行静态sql语句的Statement对象，st属局部变量
            ps.setString(1, accountName);
            ps.setString(2, date);
            ResultSet rs = ps.executeQuery();    //执行sql查询语句，返回查询数据的结果集
            if(!rs.next()) {

                String sql2 = "insert into " + DAY_STEP_TABLE +"(account_name, date, step) values(?, ?, ?)";
                PreparedStatement ptmt = conn.prepareStatement(sql2);
                ptmt.setString(1, accountName);
                ptmt.setString(2, date);
                ptmt.setInt(3, step);
                ptmt.execute();
                conn.close();
            }
            else{
                String sql2 = "update " + DAY_STEP_TABLE +" set step = ? where account_name = ? and date = ?";
                PreparedStatement ptmt = conn.prepareStatement(sql2);
                ptmt.setInt(1, step);
                ptmt.setString(2, accountName);
                ptmt.setString(3, date);
                ptmt.execute();
                conn.close();
            }


        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "远程连接失败");
        }
    }

    /**
     * 查看云数据库内有无该条distance数据,进而选择更新/插入操作
     * @param accountName
     * @param date
     * @param distance
     */
    public void updateDistanceInCloud(String accountName, String date, int distance){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Log.v(TAG, "加载JDBC驱动成功");
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "加载JDBC驱动失败");
        }
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            String sql = "select * from " + DAY_DISTANCE_TABLE + " where account_name = ? and date = ?";     // 查询数据的sql语句
            PreparedStatement ps = conn.prepareStatement(sql); //创建用于执行静态sql语句的Statement对象，st属局部变量
            ps.setString(1, accountName);
            ps.setString(2, date);
            ResultSet rs = ps.executeQuery();    //执行sql查询语句，返回查询数据的结果集
            if(!rs.next()) {

                String sql2 = "insert into " + DAY_DISTANCE_TABLE +"(account_name, date, distance) values(?, ?, ?)";
                PreparedStatement ptmt = conn.prepareStatement(sql2);
                ptmt.setString(1, accountName);
                ptmt.setString(2, date);
                ptmt.setInt(3, distance);
                ptmt.execute();
                conn.close();
            }
            else{
                String sql2 = "update " + DAY_DISTANCE_TABLE +" set distance = ? where account_name = ? and date = ?";
                PreparedStatement ptmt = conn.prepareStatement(sql2);
                ptmt.setInt(1, distance);
                ptmt.setString(2, accountName);
                ptmt.setString(3, date);
                ptmt.execute();
                conn.close();
            }


        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "远程连接失败");
        }
    }


    public void update(){

    }

    public void delete(){

    }

}
