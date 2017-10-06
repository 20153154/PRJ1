package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JOptionPane;

public class ConnDb {
  private Statement stmt;
  /**
   * This is Constructor.
   */
  
  public ConnDb() {
  
    try {  
      Class.forName("com.mysql.jdbc.Driver");  
      String jdbc = "jdbc:mysql://localhost:3306/supermarket_db";
      Connection con = DriverManager.getConnection(jdbc,"root","");  
      this.stmt = con.createStatement();  
    } catch (Exception e) { 
      JOptionPane.showMessageDialog(null, "Khong the ket noi den Database. \n" + e);
    }  
      
  }  
  /**
   * 
   * @param sqlQueryString = sqlString.
   * @return statement execute query
   */
  
  public ResultSet getResult(String sqlQueryString) {
    try {
      return stmt.executeQuery(sqlQueryString);
    } catch (Exception e) {
      System.out.println(e);
      return null;
    }
  }
  /**
   * 
   * @param sqlQueryString = sqlString.
   *
   */
  
  public void executeQuery(String sqlQueryString) {
    try {
      stmt.executeUpdate(sqlQueryString);
    } catch (Exception e) {
      System.out.println(e);
    }
  }
  /**
   * 
   * @return a list of employee object.
   */
  
  public ArrayList<Employee> getEmployeeList() {
    String sqlQuery = "SELECT * FROM `employeeinfo_tb`";
    ResultSet rs = this.getResult(sqlQuery);
    ArrayList<Employee> employeeList = new ArrayList<>();
    try {
      while (rs.next()) {
        Employee employee = new Employee();
        String id = rs.getString(1);
        Account acc = new Account();
        acc.setEmployeeId(id);
        employee.setEmployeeAccount(acc);
        employee.setEmployeeInfo(new EmployeeInfo());
        employee.setEmployeeTimework(new EmployeeTimework());
        ConnDb.loadEmployeeInfo(employee);
        ConnDb.loadEmployeeTimework(employee);
        employeeList.add(employee);
      }
    } catch (Exception e) {
      System.out.println(e);
    }
    return employeeList;
  }
  /**
   * This method save employee's information into database.
   * @param employee = employee
   */
  
  public static void saveEmployeeInfo(Employee employee) {
    ConnDb conn = new ConnDb();
    EmployeeInfo i = employee.getEmployeeInfo();
    String sqlQuery = "UPDATE `employeeinfo_tb` SET `name` = '" + i.getName() + "', `ages` = '" 
        + i.getAges() + "', `sex` = '" + i.getSex() + "'," + " `dateOfBirth` = '" 
        + i.getDateOfBirth() + "', `dateOfStartWork` = '" + i.getDateBeginWork() 
        + "' WHERE `employeeinfo_tb`.`employeeId` = '" + employee.getEmployeeId() + "'";
    conn.executeQuery(sqlQuery);
  }
  /**
   * This method save employee's worktime into database.
   * @param employee = employee
   */
  
  public static void saveEmployeeTimework(Employee employee) {
    EmployeeTimework t = employee.getEmployeeTimework();
    String sqlQuery = "UPDATE `employeeinfo_tb` SET `sessionCount` = '" + t.getSessionCount() 
        + "' WHERE `employeeinfo_tb`.`employeeId` = '" + employee.getEmployeeId() + "'";
    ConnDb conn = new ConnDb();
    conn.executeQuery(sqlQuery);
  }
  /**
   * This method save employee's account name and password into database.
   * @param employee = employee
   */
  
  public static void saveEmployeeAccount(Employee employee) {
    Account a = employee.getAccount();
    String sqlQuery = "UPDATE `account_tb` SET `accountName` = '" + a.getAccountName() 
        + "', `password` = '" + a.getPassword() + "' WHERE `account_tb`.`employeeId` = '" 
        + employee.getEmployeeId() + "'";
    ConnDb conn = new ConnDb();
    conn.executeQuery(sqlQuery);
  }
  /**
   * This method save goods's information into database when goods is exported.
   * @param goods = goods
   */
  
  public static void saveGoodsAfterSold(Goods goods) {
    ConnDb conn = new ConnDb();
    String sqlQuery = "UPDATE `goods_tb` SET `amountRemain` = '" + goods.getAmountReamain()
        + "', `amountSold` = '" + goods.getAmountSold() 
        + "' WHERE `goods_tb`.`code` = '" + goods.getCode() + "'";
    conn.executeQuery(sqlQuery);
  }
  /**
   * This method save goods's information into database when goods is imported.
   * @param goods = goods
   */
  
  public static void saveGoodsAfterImported(Goods goods) {
    ConnDb conn = new ConnDb();
    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
    try {
      Date date = format.parse(goods.getDateExpire());
      long t = date.getTime();
      String sqlQuery = "INSERT INTO `goods_tb` (`name`, `code`, `price`, `producedate`,"
          + " `expiredate" + "`, `producer`, `amountRemain`, `amountSold`) " + "VALUES ("
          + "'" + goods.getName() + "', '" + goods.getCode() + "', '" + goods.getPrice() 
          + "', '" + goods.getDateProduce() + "', '" + t + "', '" + goods.getProducer() 
          + "', '" + goods.getAmountReamain() + "', '" + goods.getAmountSold() + "')";
      conn.executeQuery(sqlQuery);
    } catch (Exception e) {
      System.out.println(e);
    }
    
    
  }
  /**
   * This method save bill's information into database when goods is exported.
   * @param bill = bill
   */
  
  public static void saveBill(Bill bill) {
    ConnDb conn = new ConnDb();
    String sqlQuery = "INSERT INTO `bill_tb` (`employeeId`, `code`, `name`, `price`, `amount`,"
        + " `time`)" + " VALUES ('" + bill.getEmployee().getEmployeeId() + "', '" 
        + bill.getGoods().getCode() + "', '" + bill.getGoods().getName() + "', '" 
        + bill.getGoods().getPrice() + "', '" + bill.getAmount() + "', '" 
        + bill.getDate().toString() + "')";
    conn.executeQuery(sqlQuery);
  }
  /**
   * This method get employee's information from database.
   * @param employee = employee
   */
  
  public static void loadEmployeeInfo(Employee employee) {
    ConnDb conn = new ConnDb();
    String sqlQuery = "SELECT * FROM `employeeinfo_tb` WHERE `employeeId` LIKE '" 
        + employee.getEmployeeId() + "'";
    ResultSet rs = conn.getResult(sqlQuery);
    EmployeeInfo i = employee.getEmployeeInfo();
    try {
      while (rs.next()) {
        i.setName(rs.getString(2));
        i.setAges(rs.getInt(3));
        i.setSex(rs.getString(4));
        i.setDateOfBirth(rs.getString(5));
        i.setDateBeginWork(rs.getString(6));
        i.setSalaryPerSession(rs.getInt(7));
      }
    } catch (Exception e) {
      System.out.println(e);
    }
  }
  /**
   * This method get employee's worktime information from database.
   * @param employee = employee
   */
  
  public static void loadEmployeeTimework(Employee employee) {
    EmployeeTimework t = employee.getEmployeeTimework();
    t.setTimePerSession(6);
    ConnDb conn = new ConnDb();
    String sqlQuery = "SELECT * FROM `employeeinfo_tb` WHERE `employeeId` LIKE '" 
        + employee.getEmployeeId() + "'";
    ResultSet rs = conn.getResult(sqlQuery);
    try {
      while (rs.next()) {
        t.setSessionCount(rs.getInt(8));
      }
    } catch (Exception e) {
      System.out.println(e);
    }
  }
  /**
   * This method get goods's information from database.
   * @param goods = goods
   */
  
  public static void loadGoodsInfo(Goods goods) {
    ConnDb conn = new ConnDb();
    String sqlQuery = "SELECT * FROM `goods_tb` WHERE `code` LIKE '" + goods.getCode() + "'";
    ResultSet rs = conn.getResult(sqlQuery);
 
    try {
      if (rs.wasNull()) {
        goods.setAmountRemain(0); 
        goods.setAmountSold(0);
        
      } else {
        while (rs.next()) {
          goods.setName(rs.getString(1));
          goods.setPrice(rs.getInt(3));
          goods.setDateProduce(rs.getString(4));
          goods.setDateExpire(MyDate.toDate(rs.getLong(5)));
          goods.setProducer(rs.getString(6));
          goods.setAmountRemain(rs.getInt(7));
          goods.setAmountSold(rs.getInt(8));
        }
      }
    } catch (Exception e) {
      System.out.println(e);
    }
  }
  /**
   * This method get list of goods that are top in amount sold.
   * @param number = number
   * @return a goods list
   */
  
  public static ArrayList<Goods> getTop(int number) {
    Goods[] goods = new Goods[number];
    ArrayList<Goods> g = new ArrayList<>();
    ConnDb conn = new ConnDb();
    String sqlQuery = "SELECT * FROM `goods_tb` ORDER BY `goods_tb`.`amountSold` DESC";
    ResultSet rs = conn.getResult(sqlQuery);
    int count = 0;
    try {
      while (rs.next()) {
        if (count == number) {
          break;
        }
        goods[count] = new Goods();
        goods[count].setName(rs.getString(1));
        goods[count].setCode(rs.getString(2));
        goods[count].setPrice(rs.getInt(3));
        goods[count].setDateProduce(rs.getString(4));
        goods[count].setDateExpire(MyDate.toDate(rs.getLong(5)));
        goods[count].setProducer(rs.getString(6));
        goods[count].setAmountRemain(rs.getInt(7));
        goods[count].setAmountSold(rs.getInt(8));
        g.add(goods[count]);
        count++;
      }
      return g;
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, e);
    }
    return null;
  }
  /**
   * This method get list of goods that are top in amount sold.
   * @param number = number
   * @return a goods list
   */
  
  public static ArrayList<Goods> getBot(int number) {
    Goods[] goods = new Goods[number];
    ArrayList<Goods> g = new ArrayList<>();
    ConnDb conn = new ConnDb();
    String sqlQuery = "SELECT * FROM `goods_tb` ORDER BY `goods_tb`.`amountSold` ASC";
    ResultSet rs = conn.getResult(sqlQuery);
    int count = 0;
    try {
      while (rs.next()) {
        if (count == number) {
          break;
        }
        goods[count] = new Goods();
        goods[count].setName(rs.getString(1));
        goods[count].setCode(rs.getString(2));
        goods[count].setPrice(rs.getInt(3));
        goods[count].setDateProduce(rs.getString(4));
        goods[count].setDateExpire(MyDate.toDate(rs.getLong(5)));
        goods[count].setProducer(rs.getString(6));
        goods[count].setAmountRemain(rs.getInt(7));
        goods[count].setAmountSold(rs.getInt(8));
        g.add(goods[count]);
        count++;
      }
      return g;
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, e);
    }
    return null;
  }
  /**
   * This method get goods with amount remain equal zero.
   * @return number of goods 
   */
  
  public static int getNumberGoodsWithZeroAmount() {
    ConnDb conn = new ConnDb();
    String sqlQuery = "SELECT * FROM `goods_tb` WHERE `amountRemain` = 0";
    ResultSet rs = conn.getResult(sqlQuery);
    int count = 0;
    try {
      while (rs.next()) {
        count ++;
      }
      return count;
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, e);
      return 0;
    }
  }
  /**
   * This method get list goods with amount remain equal zero.
   * @return a list of goods
   */
  
  public static ArrayList<Goods> getListGoodsWithZeroAmount() {
    ArrayList<Goods> g = new ArrayList<>();
    ConnDb conn = new ConnDb();
    String sqlQuery = "SELECT * FROM `goods_tb` WHERE `amountRemain` LIKE 0 ";
    ResultSet rs = conn.getResult(sqlQuery);
    Goods goods = null;
    try {
      while (rs.next()) {
        goods = new Goods();
        goods.setName(rs.getString(1));
        goods.setCode(rs.getString(2));
        goods.setPrice(rs.getInt(3));
        goods.setDateProduce(rs.getString(4));
        goods.setDateExpire(MyDate.toDate(rs.getLong(5)));
        goods.setProducer(rs.getString(6));
        goods.setAmountRemain(rs.getInt(7));
        goods.setAmountSold(rs.getInt(8));
        g.add(goods);
      }
      return g;
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, e);
    }
    return null;
  }
  /**
   * This method check if goods is in store.
   * @param goods = goods
   * @return boolean value
   */
  
  public static boolean isAvailable(Goods goods) {
    String sqlQuery = "SELECT * FROM `goods_tb`";
    ConnDb conn = new ConnDb();
    ResultSet rs = conn.getResult(sqlQuery);
    boolean check = false;
    try {
      while (rs.next()) {
        if (goods.getCode().equals(rs.getString(2))) {
          check = true;
          break;
        }
      }
    } catch (Exception e) {
      System.out.println(e);
    }
    return check;
  }
  /**
  * This method check if account is register.
  * @param account = account
  * @return boolean value
   */
  
  public static boolean isRegisterAccount(Account account) {
    String sqlQuery = "SELECT * FROM `account_tb`";
    ConnDb conn = new ConnDb();
    ResultSet rs = conn.getResult(sqlQuery);
    boolean check = false;
    try {
      while (rs.next()) {
        if (account.getAccountName().equals(rs.getString(1)) 
            && account.getPassword().equals(rs.getString(2))) {
          account.setEmployeeId(rs.getString(3));
          check = true;
          break;
        }
      }
    } catch (Exception e) {
      System.out.println(e);
    }
    return check;
  }
  /**
   * This method check if account's id was register.
   * @param account = account
   * @return boolean value
   */
  
  public static boolean isLegalIdToRegister(Account account) {
    String sqlQuery = "SELECT * FROM `account_tb`";
    ConnDb conn = new ConnDb();
    ResultSet rs = conn.getResult(sqlQuery);
    boolean check = false;
    try {
      while (rs.next()) {
        if (account.getEmployeeId().equals(rs.getString(3)) && rs.getString(1).isEmpty()) {
          check = true;
          break;
        }
      }
    } catch (Exception e) {
      System.out.println(e);
    }
    return check;
  }
  /**
   * This method check if account's name is legal to register.
   * @param account = account
   * @return boolean value
   */
  
  public static boolean isLegalAccountName(Account account) {
    String sqlQuery = "SELECT * FROM `account_tb`";
    ConnDb conn = new ConnDb();
    ResultSet rs = conn.getResult(sqlQuery);
    boolean check = true;
    try {
      while (rs.next()) {
        if (account.getAccountName().equals(rs.getString(1))) {
          check = false;
          break;
        }
      }
    } catch (Exception e) {
      System.out.println(e);
    }
    return check;
  }
  /**
   * This method save new employee id into database.
   * @param account = account
   */
  
  public static void saveNewEmployeeId(Account account) {
    String sqlQuery = "INSERT INTO `account_tb` (`accountName`, `password`, `employeeId`) VALUES "
        + "('', '', '" + account.getEmployeeId() + "')";
    ConnDb conn = new ConnDb();
    conn.executeQuery(sqlQuery);
    String sqlQuery1 = "INSERT INTO `employeeinfo_tb` (`employeeId`, `name`, `ages`, `sex`, "
        + "`dateOfBirth`, `dateOfStartWork`)" + " VALUES ('" + account.getEmployeeId() 
        + "', '', '0', '', '', '')";
    conn.executeQuery(sqlQuery1);
  }
  /**
   * This method save account's information into database.
   * @param account = account
   */
  
  public static void savaAccountInfo(Account account) {
    String sqlQuery = "UPDATE `account_tb` SET `accountName` = '" + account.getAccountName() 
        + "', `password` = '" + account.getAccountName() + "' WHERE `account_tb`.`employeeId` "
        + "= '" + account.getEmployeeId() + "'";
    ConnDb conn = new ConnDb();
    conn.executeQuery(sqlQuery);
  }
  /**
   * Lay danh sach san pham sap het han.
   * @return array list
   */
  
  public static ArrayList<Goods> getListGoodsHetHan() {
    // TODO Auto-generated method stub
    ArrayList<Goods> g = new ArrayList<>();
    ConnDb conn = new ConnDb();
    String sqlQuery = "SELECT * FROM `goods_tb` WHERE `expiredate` < " + (new Date()).getTime();
    ResultSet rs = conn.getResult(sqlQuery);
    Goods goods = null;
    try {
      while (rs.next()) {
        goods = new Goods();
        goods.setName(rs.getString(1));
        goods.setCode(rs.getString(2));
        goods.setPrice(rs.getInt(3));
        goods.setDateProduce(rs.getString(4));
        goods.setDateExpire(MyDate.toDate(rs.getLong(5)));
        goods.setProducer(rs.getString(6));
        goods.setAmountRemain(rs.getInt(7));
        goods.setAmountSold(rs.getInt(8));
        g.add(goods);
      }
      return g;
    } catch (Exception e) {
      JOptionPane.showMessageDialog(null, e);
    }
    return null;
  }
}