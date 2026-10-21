package courier.database;

import courier.model.*;
import courier.exceptions.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class DBManager {
    private static DBManager Instance;
    private static final String Dbhost = System.getProperty("db.host", "localhost");
    private static final String Dbport = System.getProperty("db.port", "3306");
    private static final String Dbname = System.getProperty("db.name", "courier_db");
    private static final String Dbuser = System.getProperty("db.user", "root");
    private static final String Dbpassword = System.getProperty("db.password", "");
    private Connection Connection;

    private DBManager() throws DatabaseOperationException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            boolean Isssl = !Dbhost.equalsIgnoreCase("localhost") && !Dbhost.equals("127.0.0.1")
                    && !Dbhost.startsWith("192.168.");
            String Sslparam = Isssl ? "useSSL=true&trustServerCertificate=true"
                    : "useSSL=false&allowPublicKeyRetrieval=true";
            if (Dbhost.equalsIgnoreCase("localhost") || Dbhost.equals("127.0.0.1")) {
                try (Connection Serverconn = DriverManager.getConnection("jdbc:mysql://" + Dbhost + ":" + Dbport
                        + "/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC", Dbuser, Dbpassword);
                        Statement Stmt = Serverconn.createStatement()) {
                    Stmt.execute("CREATE DATABASE IF NOT EXISTS " + Dbname + ";");
                }
            }
            String Dburl = "jdbc:mysql://" + Dbhost + ":" + Dbport + "/" + Dbname + "?" + Sslparam
                    + "&serverTimezone=UTC";
            this.Connection = DriverManager.getConnection(Dburl, Dbuser, Dbpassword);
        } catch (Exception E) {
            throw new DatabaseOperationException("Database connection error: " + E.getMessage(), E);
        }
    }

    public static synchronized DBManager Getinstance() throws DatabaseOperationException {
        if (Instance == null) {
            Instance = new DBManager();
        }
        return Instance;
    }

    public synchronized Connection Getconnection() throws DatabaseOperationException {
        try {
            if (Connection == null || Connection.isClosed()) {
                Instance = new DBManager();
            }
            return Connection;
        } catch (SQLException E) {
            throw new DatabaseOperationException("Error accessing database: " + E.getMessage(), E);
        }
    }

    public synchronized void Closeconnection() {
        if (Connection != null) {
            try {
                if (!Connection.isClosed())
                    Connection.close();
            } catch (SQLException Ignored) {
            }
            Connection = null;
        }
    }

    public void Initdatabase() throws DatabaseOperationException {
        try {
            Connection Conn = Getconnection();
            try (Statement Stmt = Conn.createStatement()) {
                Stmt.execute(
                        "CREATE TABLE IF NOT EXISTS customers (" +
                                "  customer_id INT AUTO_INCREMENT PRIMARY KEY," +
                                "  name VARCHAR(255) NOT NULL," +
                                "  phone VARCHAR(50) NOT NULL," +
                                "  email VARCHAR(100)," +
                                "  address TEXT" +
                                ");");
                Stmt.execute(
                        "CREATE TABLE IF NOT EXISTS storage_locations (" +
                                "  location_id INT AUTO_INCREMENT PRIMARY KEY," +
                                "  section VARCHAR(100) NOT NULL," +
                                "  rack VARCHAR(100) NOT NULL," +
                                "  shelf VARCHAR(100) NOT NULL," +
                                "  is_occupied TINYINT(1) NOT NULL DEFAULT 0," +
                                "  current_courier_id VARCHAR(50)," +
                                "  UNIQUE KEY uq_slot (section, rack, shelf)" +
                                ");");
                Stmt.execute(
                        "CREATE TABLE IF NOT EXISTS couriers (" +
                                "  courier_id VARCHAR(50) PRIMARY KEY," +
                                "  courier_type VARCHAR(50) NOT NULL," +
                                "  sender_id INT NOT NULL," +
                                "  receiver_id INT NOT NULL," +
                                "  destination_address TEXT NOT NULL," +
                                "  destination_city VARCHAR(100) NOT NULL," +
                                "  weight DOUBLE NOT NULL," +
                                "  parcel_type VARCHAR(100) NOT NULL," +
                                "  booking_date VARCHAR(50) NOT NULL," +
                                "  status VARCHAR(50) NOT NULL," +
                                "  storage_location_id INT," +
                                "  delivery_charge DOUBLE NOT NULL," +
                                "  extra_info TEXT," +
                                "  FOREIGN KEY (sender_id) REFERENCES customers(customer_id)," +
                                "  FOREIGN KEY (receiver_id) REFERENCES customers(customer_id)," +
                                "  FOREIGN KEY (storage_location_id) REFERENCES storage_locations(location_id) ON DELETE SET NULL"
                                +
                                ");");
                Stmt.execute(
                        "CREATE TABLE IF NOT EXISTS dispatches (" +
                                "  dispatch_id VARCHAR(50) PRIMARY KEY," +
                                "  courier_id VARCHAR(50) NOT NULL UNIQUE," +
                                "  dispatch_date VARCHAR(50) NOT NULL," +
                                "  vehicle_number VARCHAR(50) NOT NULL," +
                                "  driver_name VARCHAR(100) NOT NULL," +
                                "  destination VARCHAR(100) NOT NULL," +
                                "  remarks TEXT," +
                                "  FOREIGN KEY (courier_id) REFERENCES couriers(courier_id) ON DELETE CASCADE" +
                                ");");
            }
            Seeddefaultstorageslots();
        } catch (Exception E) {
            throw new DatabaseOperationException("Failed to initialize database: " + E.getMessage(), E);
        }
    }

    private void Seeddefaultstorageslots() throws SQLException, DatabaseOperationException {
        Connection Conn = Getconnection();
        try (Statement Stmt = Conn.createStatement();
                ResultSet Rs = Stmt.executeQuery("SELECT COUNT(*) FROM storage_locations")) {
            if (Rs.next() && Rs.getInt(1) == 0) {
                String Sql = "INSERT INTO storage_locations (section, rack, shelf, is_occupied) VALUES (?, ?, ?, 0)";
                try (PreparedStatement Pstmt = Conn.prepareStatement(Sql)) {
                    String[] Racks = { "Rack-1", "Rack-2" };
                    String[] Shelves = { "Shelf-A", "Shelf-B", "Shelf-C" };
                    for (String R : Racks) {
                        for (String S : Shelves) {
                            Pstmt.setString(1, "Section A (General)");
                            Pstmt.setString(2, R);
                            Pstmt.setString(3, S);
                            Pstmt.addBatch();
                        }
                    }
                    for (String R : Racks) {
                        for (int I = 0; I < 2; I++) {
                            Pstmt.setString(1, "Section B (Express)");
                            Pstmt.setString(2, R);
                            Pstmt.setString(3, Shelves[I]);
                            Pstmt.addBatch();
                        }
                    }
                    Pstmt.setString(1, "Section C (Fragile)");
                    Pstmt.setString(2, "Rack-1");
                    Pstmt.setString(3, "Shelf-A (Padded)");
                    Pstmt.addBatch();
                    Pstmt.executeBatch();
                }
            }
        }
    }

    public Customer Getorcreatecustomer(String Name, String Phone, String Email, String Address) throws Exception {
        Connection Conn = Getconnection();
        String Selectsql = "SELECT customer_id, name, phone, email, address FROM customers WHERE phone = ? LIMIT 1";
        try (PreparedStatement Pstmt = Conn.prepareStatement(Selectsql)) {
            Pstmt.setString(1, Phone.trim());
            try (ResultSet Rs = Pstmt.executeQuery()) {
                if (Rs.next()) {
                    return new Customer(Rs.getInt(1), Rs.getString(2), Rs.getString(3), Rs.getString(4),
                            Rs.getString(5));
                }
            }
        }
        String Insertsql = "INSERT INTO customers (name, phone, email, address) VALUES (?, ?, ?, ?)";
        try (PreparedStatement Pstmt = Conn.prepareStatement(Insertsql, Statement.RETURN_GENERATED_KEYS)) {
            Pstmt.setString(1, Name.trim());
            Pstmt.setString(2, Phone.trim());
            Pstmt.setString(3, Email != null ? Email.trim() : "");
            Pstmt.setString(4, Address != null ? Address.trim() : "");
            Pstmt.executeUpdate();
            try (ResultSet Gk = Pstmt.getGeneratedKeys()) {
                if (Gk.next())
                    return new Customer(Gk.getInt(1), Name, Phone, Email, Address);
            }
        }
        throw new DatabaseOperationException("Failed to register customer.");
    }

    public void Registercourier(Courier Courierobj, Integer Slotid)
            throws DatabaseOperationException, ValidationException {
        if (Courierobj == null)
            throw new ValidationException("Courier cannot be null.");
        try {
            Connection Conn = Getconnection();
            Conn.setAutoCommit(false);
            try {
                Customer S = Getorcreatecustomer(Courierobj.Getsender().Getname(), Courierobj.Getsender().Getphone(),
                        Courierobj.Getsender().Getemail(), Courierobj.Getsender().Getaddress());
                Customer R = Getorcreatecustomer(Courierobj.Getreceiver().Getname(),
                        Courierobj.Getreceiver().Getphone(), Courierobj.Getreceiver().Getemail(),
                        Courierobj.Getreceiver().Getaddress());
                Courierobj.Setsender(S);
                Courierobj.Setreceiver(R);
                String Extra = "";
                if (Courierobj instanceof ExpressCourier)
                    Extra = String.valueOf(((ExpressCourier) Courierobj).Getguaranteedhours());
                else if (Courierobj instanceof FragileCourier)
                    Extra = ((FragileCourier) Courierobj).Getfragilecategory();
                CourierStatus Initialstatus = CourierStatus.STORED;
                String Sql = "INSERT INTO couriers (courier_id, courier_type, sender_id, receiver_id, destination_address, "
                        +
                        "destination_city, weight, parcel_type, booking_date, status, storage_location_id, delivery_charge, extra_info) "
                        +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement Pstmt = Conn.prepareStatement(Sql)) {
                    Pstmt.setString(1, Courierobj.Getcourierid());
                    Pstmt.setString(2, Courierobj.Getcouriertype());
                    Pstmt.setInt(3, S.Getcustomerid());
                    Pstmt.setInt(4, R.Getcustomerid());
                    Pstmt.setString(5, Courierobj.Getdestinationaddress());
                    Pstmt.setString(6, Courierobj.Getdestinationcity());
                    Pstmt.setDouble(7, Courierobj.Getweight());
                    Pstmt.setString(8, Courierobj.Getparceltype());
                    Pstmt.setString(9, Courierobj.Getbookingdate());
                    Pstmt.setString(10, Initialstatus.Getdisplayname());
                    if (Slotid != null)
                        Pstmt.setInt(11, Slotid);
                    else
                        Pstmt.setNull(11, Types.INTEGER);
                    Pstmt.setDouble(12, Courierobj.Getdeliverycharge());
                    Pstmt.setString(13, Extra);
                    Pstmt.executeUpdate();
                }
                if (Slotid != null) {
                    try (PreparedStatement Pstmt = Conn.prepareStatement(
                            "UPDATE storage_locations SET is_occupied = 1, current_courier_id = ? WHERE location_id = ?")) {
                        Pstmt.setString(1, Courierobj.Getcourierid());
                        Pstmt.setInt(2, Slotid);
                        Pstmt.executeUpdate();
                    }
                }
                Conn.commit();
            } catch (Exception E) {
                Conn.rollback();
                throw E;
            } finally {
                Conn.setAutoCommit(true);
            }
        } catch (ValidationException E) {
            throw E;
        } catch (Exception E) {
            throw new DatabaseOperationException("Error registering courier: " + E.getMessage(), E);
        }
    }

    public Vector<Courier> Getallcouriers() throws DatabaseOperationException {
        return Searchcouriers("", "All", "All");
    }

    public Vector<Courier> Searchcouriers(String Keyword) throws DatabaseOperationException {
        return Searchcouriers(Keyword, "All", "All");
    }

    public Vector<Courier> Searchcouriers(String Keyword, String Category, String Statusfilter)
            throws DatabaseOperationException {
        Vector<Courier> List = new Vector<>();
        StringBuilder Sql = new StringBuilder(
                "SELECT c.courier_id, c.courier_type, c.destination_address, c.destination_city, c.weight, " +
                        "       c.parcel_type, c.booking_date, c.status, c.delivery_charge, c.extra_info, " +
                        "       s.customer_id AS sid, s.name AS sname, s.phone AS sphone, s.email AS semail, s.address AS saddr, "
                        +
                        "       r.customer_id AS rid, r.name AS rname, r.phone AS rphone, r.email AS remail, r.address AS raddr, "
                        +
                        "       sl.location_id, sl.section, sl.rack, sl.shelf " +
                        "FROM couriers c " +
                        "JOIN customers s ON c.sender_id = s.customer_id " +
                        "JOIN customers r ON c.receiver_id = r.customer_id " +
                        "LEFT JOIN storage_locations sl ON c.storage_location_id = sl.location_id " +
                        "WHERE 1=1 ");
        Vector<Object> Params = new Vector<>();
        if (Statusfilter != null && !Statusfilter.equalsIgnoreCase("All") && !Statusfilter.trim().isEmpty()) {
            Sql.append(" AND c.status = ? ");
            Params.add(Statusfilter.trim());
        }
        if (Keyword != null && !Keyword.trim().isEmpty()) {
            String Kw = "%" + Keyword.trim() + "%";
            if ("Courier ID".equalsIgnoreCase(Category)) {
                Sql.append(" AND c.courier_id LIKE ? ");
                Params.add(Kw);
            } else if ("Receiver".equalsIgnoreCase(Category)) {
                Sql.append(" AND r.name LIKE ? ");
                Params.add(Kw);
            } else if ("Destination".equalsIgnoreCase(Category)) {
                Sql.append(" AND (c.destination_city LIKE ? OR c.destination_address LIKE ?) ");
                Params.add(Kw);
                Params.add(Kw);
            } else if ("Storage Location".equalsIgnoreCase(Category)) {
                Sql.append(" AND (sl.section LIKE ? OR sl.rack LIKE ? OR sl.shelf LIKE ?) ");
                Params.add(Kw);
                Params.add(Kw);
                Params.add(Kw);
            } else if ("Status".equalsIgnoreCase(Category)) {
                Sql.append(" AND c.status LIKE ? ");
                Params.add(Kw);
            } else {
                Sql.append(
                        " AND (c.courier_id LIKE ? OR r.name LIKE ? OR s.name LIKE ? OR c.destination_city LIKE ? OR sl.section LIKE ?) ");
                for (int I = 0; I < 5; I++)
                    Params.add(Kw);
            }
        }
        Sql.append(" ORDER BY c.booking_date DESC");
        try {
            Connection Conn = Getconnection();
            try (PreparedStatement Pstmt = Conn.prepareStatement(Sql.toString())) {
                for (int I = 0; I < Params.size(); I++)
                    Pstmt.setObject(I + 1, Params.get(I));
                try (ResultSet Rs = Pstmt.executeQuery()) {
                    while (Rs.next()) {
                        Customer Sender = new Customer(Rs.getInt("sid"), Rs.getString("sname"), Rs.getString("sphone"),
                                Rs.getString("semail"), Rs.getString("saddr"));
                        Customer Receiver = new Customer(Rs.getInt("rid"), Rs.getString("rname"),
                                Rs.getString("rphone"), Rs.getString("remail"), Rs.getString("raddr"));
                        String Id = Rs.getString("courier_id");
                        String Type = Rs.getString("courier_type");
                        String Extra = Rs.getString("extra_info");
                        Courier C;
                        if ("Express".equalsIgnoreCase(Type)) {
                            int Hours = 24;
                            try {
                                Hours = Integer.parseInt(Extra);
                            } catch (Exception Ignored) {
                            }
                            C = new ExpressCourier(Id, Sender, Receiver, Rs.getString("destination_address"),
                                    Rs.getString("destination_city"), Rs.getDouble("weight"),
                                    Rs.getString("parcel_type"), Rs.getString("booking_date"), Hours);
                        } else if ("Fragile".equalsIgnoreCase(Type)) {
                            C = new FragileCourier(Id, Sender, Receiver, Rs.getString("destination_address"),
                                    Rs.getString("destination_city"), Rs.getDouble("weight"),
                                    Rs.getString("parcel_type"), Rs.getString("booking_date"), Extra);
                        } else {
                            C = new StandardCourier(Id, Sender, Receiver, Rs.getString("destination_address"),
                                    Rs.getString("destination_city"), Rs.getDouble("weight"),
                                    Rs.getString("parcel_type"), Rs.getString("booking_date"));
                        }
                        C.Setstatus(CourierStatus.Fromstring(Rs.getString("status")));
                        C.Setdeliverycharge(Rs.getDouble("delivery_charge"));
                        int Locid = Rs.getInt("location_id");
                        if (!Rs.wasNull()) {
                            C.Setstoragelocationdirectly(new StorageLocation(Locid, Rs.getString("section"),
                                    Rs.getString("rack"), Rs.getString("shelf"), true, Id));
                        }
                        if (C.Getstatus() == CourierStatus.DISPATCHED || C.Getstatus() == CourierStatus.DELIVERED) {
                            C.Setdispatchrecorddirectly(Getdispatchbycourierid(Id));
                        }
                        List.add(C);
                    }
                }
            }
        } catch (Exception E) {
            throw new DatabaseOperationException("Search error: " + E.getMessage(), E);
        }
        return List;
    }

    public Courier Getcourierbyid(String Courierid) throws CourierNotFoundException, DatabaseOperationException {
        Vector<Courier> List = Searchcouriers(Courierid, "Courier ID", "All");
        for (int I = 0; I < List.size(); I++) {
            if (List.get(I).Getcourierid().equalsIgnoreCase(Courierid.trim()))
                return List.get(I);
        }
        throw new CourierNotFoundException("Courier with ID '" + Courierid + "' not found.");
    }

    public void Updatecourierstatus(String Courierid, CourierStatus Newstatus)
            throws CourierNotFoundException, InvalidStatusTransitionException, DatabaseOperationException {
        Courier C = Getcourierbyid(Courierid);
        C.Updatestatus(Newstatus);
        try {
            Connection Conn = Getconnection();
            try (PreparedStatement Pstmt = Conn
                    .prepareStatement("UPDATE couriers SET status = ? WHERE courier_id = ?")) {
                Pstmt.setString(1, Newstatus.Getdisplayname());
                Pstmt.setString(2, Courierid);
                Pstmt.executeUpdate();
            }
        } catch (Exception E) {
            throw new DatabaseOperationException("Failed to update status: " + E.getMessage(), E);
        }
    }

    public void Deletecourier(String Courierid) throws DatabaseOperationException {
        try {
            Connection Conn = Getconnection();
            Conn.setAutoCommit(false);
            try {
                try (PreparedStatement Pstmt = Conn.prepareStatement(
                        "UPDATE storage_locations SET is_occupied = 0, current_courier_id = NULL WHERE current_courier_id = ?")) {
                    Pstmt.setString(1, Courierid);
                    Pstmt.executeUpdate();
                }
                try (PreparedStatement Pstmt = Conn.prepareStatement("DELETE FROM dispatches WHERE courier_id = ?")) {
                    Pstmt.setString(1, Courierid);
                    Pstmt.executeUpdate();
                }
                try (PreparedStatement Pstmt = Conn.prepareStatement("DELETE FROM couriers WHERE courier_id = ?")) {
                    Pstmt.setString(1, Courierid);
                    Pstmt.executeUpdate();
                }
                Conn.commit();
            } catch (Exception E) {
                Conn.rollback();
                throw E;
            } finally {
                Conn.setAutoCommit(true);
            }
        } catch (Exception E) {
            throw new DatabaseOperationException("Delete error: " + E.getMessage(), E);
        }
    }

    public Vector<StorageLocation> Getallstoragelocations() throws DatabaseOperationException {
        Vector<StorageLocation> List = new Vector<>();
        try {
            Connection Conn = Getconnection();
            try (Statement Stmt = Conn.createStatement();
                    ResultSet Rs = Stmt.executeQuery(
                            "SELECT location_id, section, rack, shelf, is_occupied, current_courier_id FROM storage_locations ORDER BY section, rack, shelf")) {
                while (Rs.next()) {
                    List.add(new StorageLocation(Rs.getInt(1), Rs.getString(2), Rs.getString(3), Rs.getString(4),
                            Rs.getInt(5) == 1, Rs.getString(6)));
                }
            }
        } catch (Exception E) {
            throw new DatabaseOperationException("Storage error: " + E.getMessage(), E);
        }
        return List;
    }

    public Vector<StorageLocation> Getavailablestoragelocations() throws DatabaseOperationException {
        Vector<StorageLocation> List = new Vector<>();
        try {
            Connection Conn = Getconnection();
            try (Statement Stmt = Conn.createStatement();
                    ResultSet Rs = Stmt.executeQuery(
                            "SELECT location_id, section, rack, shelf, is_occupied, current_courier_id FROM storage_locations WHERE is_occupied = 0 ORDER BY section, rack, shelf")) {
                while (Rs.next()) {
                    List.add(new StorageLocation(Rs.getInt(1), Rs.getString(2), Rs.getString(3), Rs.getString(4), false,
                            null));
                }
            }
        } catch (Exception E) {
            throw new DatabaseOperationException("Storage error: " + E.getMessage(), E);
        }
        return List;
    }

    public void Assignstorage(String Courierid, int Locationid)
            throws DatabaseOperationException, InvalidStorageLocationException, CourierNotFoundException {
        Getcourierbyid(Courierid);
        try {
            Connection Conn = Getconnection();
            Conn.setAutoCommit(false);
            try {
                try (PreparedStatement Check = Conn.prepareStatement(
                        "SELECT is_occupied, current_courier_id FROM storage_locations WHERE location_id = ?")) {
                    Check.setInt(1, Locationid);
                    try (ResultSet Rs = Check.executeQuery()) {
                        if (Rs.next() && Rs.getInt(1) == 1 && !Courierid.equalsIgnoreCase(Rs.getString(2))) {
                            throw new InvalidStorageLocationException(
                                    "Slot is already occupied by courier: " + Rs.getString(2));
                        }
                    }
                }
                try (PreparedStatement Pstmt = Conn.prepareStatement(
                        "UPDATE storage_locations SET is_occupied = 0, current_courier_id = NULL WHERE current_courier_id = ?")) {
                    Pstmt.setString(1, Courierid);
                    Pstmt.executeUpdate();
                }
                try (PreparedStatement Pstmt = Conn.prepareStatement(
                        "UPDATE storage_locations SET is_occupied = 1, current_courier_id = ? WHERE location_id = ?")) {
                    Pstmt.setString(1, Courierid);
                    Pstmt.setInt(2, Locationid);
                    Pstmt.executeUpdate();
                }
                try (PreparedStatement Pstmt = Conn
                        .prepareStatement("UPDATE couriers SET storage_location_id = ? WHERE courier_id = ?")) {
                    Pstmt.setInt(1, Locationid);
                    Pstmt.setString(2, Courierid);
                    Pstmt.executeUpdate();
                }
                Conn.commit();
            } catch (Exception E) {
                Conn.rollback();
                throw E;
            } finally {
                Conn.setAutoCommit(true);
            }
        } catch (InvalidStorageLocationException E) {
            throw E;
        } catch (Exception E) {
            throw new DatabaseOperationException("Assign storage error: " + E.getMessage(), E);
        }
    }

    public void Releasestorage(String Courierid) throws DatabaseOperationException {
        try {
            Connection Conn = Getconnection();
            try (PreparedStatement Pstmt = Conn.prepareStatement(
                    "UPDATE storage_locations SET is_occupied = 0, current_courier_id = NULL WHERE current_courier_id = ?");
                    PreparedStatement Pstmt2 = Conn
                            .prepareStatement("UPDATE couriers SET storage_location_id = NULL WHERE courier_id = ?")) {
                Pstmt.setString(1, Courierid);
                Pstmt.executeUpdate();
                Pstmt2.setString(1, Courierid);
                Pstmt2.executeUpdate();
            }
        } catch (Exception E) {
            throw new DatabaseOperationException("Release storage error: " + E.getMessage(), E);
        }
    }

    public void Addstorageslot(String Section, String Rack, String Shelf)
            throws DatabaseOperationException, ValidationException {
        StorageLocation Temp = new StorageLocation(Section, Rack, Shelf);
        try {
            Connection Conn = Getconnection();
            try (PreparedStatement Pstmt = Conn.prepareStatement(
                    "INSERT INTO storage_locations (section, rack, shelf, is_occupied) VALUES (?, ?, ?, 0)")) {
                Pstmt.setString(1, Temp.Getsection());
                Pstmt.setString(2, Temp.Getrack());
                Pstmt.setString(3, Temp.Getshelf());
                Pstmt.executeUpdate();
            }
        } catch (Exception E) {
            throw new DatabaseOperationException("Failed to add slot: " + E.getMessage(), E);
        }
    }

    public void Deletestorageslot(int Locationid) throws DatabaseOperationException, InvalidStorageLocationException {
        try {
            Connection Conn = Getconnection();
            try (PreparedStatement Pstmt = Conn
                    .prepareStatement("DELETE FROM storage_locations WHERE location_id = ? AND is_occupied = 0")) {
                Pstmt.setInt(1, Locationid);
                int Rows = Pstmt.executeUpdate();
                if (Rows == 0)
                    throw new InvalidStorageLocationException("Slot is occupied or does not exist.");
            }
        } catch (InvalidStorageLocationException E) {
            throw E;
        } catch (Exception E) {
            throw new DatabaseOperationException("Failed to delete slot: " + E.getMessage(), E);
        }
    }

    public DispatchRecord Dispatchcourier(String Courierid, String Vehicle, String Driver, String Remarks)
            throws DatabaseOperationException, ValidationException, InvalidStatusTransitionException,
            CourierNotFoundException {
        try {
            Courier C = Getcourierbyid(Courierid);
            if (!C.Canbedispatched()) {
                throw new InvalidStatusTransitionException(
                        "Courier must be 'Stored' or 'Ready for Dispatch' to be dispatched.");
            }
            Connection Conn = Getconnection();
            Conn.setAutoCommit(false);
            try {
                String Did = "DSP-" + (5000 + (int) (Math.random() * 4999));
                String Date = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
                DispatchRecord Record = new DispatchRecord(Did, Courierid, Date, Vehicle, Driver,
                        C.Getdestinationcity(), Remarks);
                String Sql = "INSERT INTO dispatches (dispatch_id, courier_id, dispatch_date, vehicle_number, driver_name, destination, remarks) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement Pstmt = Conn.prepareStatement(Sql)) {
                    Pstmt.setString(1, Record.Getdispatchid());
                    Pstmt.setString(2, Record.Getcourierid());
                    Pstmt.setString(3, Record.Getdispatchdate());
                    Pstmt.setString(4, Record.Getvehiclenumber());
                    Pstmt.setString(5, Record.Getdrivername());
                    Pstmt.setString(6, Record.Getdestination());
                    Pstmt.setString(7, Record.Getremarks());
                    Pstmt.executeUpdate();
                }
                try (PreparedStatement Pstmt = Conn.prepareStatement(
                        "UPDATE couriers SET status = 'Dispatched', storage_location_id = NULL WHERE courier_id = ?")) {
                    Pstmt.setString(1, Courierid);
                    Pstmt.executeUpdate();
                }
                try (PreparedStatement Pstmt = Conn.prepareStatement(
                        "UPDATE storage_locations SET is_occupied = 0, current_courier_id = NULL WHERE current_courier_id = ?")) {
                    Pstmt.setString(1, Courierid);
                    Pstmt.executeUpdate();
                }
                Conn.commit();
                return Record;
            } catch (Exception E) {
                Conn.rollback();
                throw E;
            } finally {
                Conn.setAutoCommit(true);
            }
        } catch (ValidationException | InvalidStatusTransitionException E) {
            throw E;
        } catch (Exception E) {
            throw new DatabaseOperationException("Dispatch error: " + E.getMessage(), E);
        }
    }

    public Vector<DispatchRecord> Getalldispatches() throws DatabaseOperationException {
        Vector<DispatchRecord> List = new Vector<>();
        try {
            Connection Conn = Getconnection();
            try (Statement Stmt = Conn.createStatement();
                    ResultSet Rs = Stmt.executeQuery(
                            "SELECT dispatch_id, courier_id, dispatch_date, vehicle_number, driver_name, destination, remarks FROM dispatches ORDER BY dispatch_date DESC")) {
                while (Rs.next()) {
                    List.add(new DispatchRecord(Rs.getString(1), Rs.getString(2), Rs.getString(3), Rs.getString(4),
                            Rs.getString(5), Rs.getString(6), Rs.getString(7)));
                }
            }
        } catch (Exception E) {
            throw new DatabaseOperationException("Dispatch fetch error: " + E.getMessage(), E);
        }
        return List;
    }

    public DispatchRecord Getdispatchbycourierid(String Courierid) throws DatabaseOperationException {
        try {
            Connection Conn = Getconnection();
            try (PreparedStatement Pstmt = Conn.prepareStatement(
                    "SELECT dispatch_id, courier_id, dispatch_date, vehicle_number, driver_name, destination, remarks FROM dispatches WHERE courier_id = ? LIMIT 1")) {
                Pstmt.setString(1, Courierid);
                try (ResultSet Rs = Pstmt.executeQuery()) {
                    if (Rs.next()) {
                        return new DispatchRecord(Rs.getString(1), Rs.getString(2), Rs.getString(3), Rs.getString(4),
                                Rs.getString(5), Rs.getString(6), Rs.getString(7));
                    }
                }
            }
        } catch (Exception E) {
            throw new DatabaseOperationException("Dispatch record error: " + E.getMessage(), E);
        }
        return null;
    }

    public SystemStatistics Getstatistics() throws DatabaseOperationException {
        int Total = 0, Stored = 0, Ready = 0, Disp = 0, Del = 0;
        double Revenue = 0.0;
        try {
            Connection Conn = Getconnection();
            try (Statement Stmt = Conn.createStatement();
                    ResultSet Rs = Stmt.executeQuery(
                            "SELECT status, COUNT(*), SUM(delivery_charge) FROM couriers GROUP BY status")) {
                while (Rs.next()) {
                    String St = Rs.getString(1);
                    int Count = Rs.getInt(2);
                    Total += Count;
                    Revenue += Rs.getDouble(3);
                    if ("Stored".equalsIgnoreCase(St))
                        Stored = Count;
                    else if ("Ready for Dispatch".equalsIgnoreCase(St))
                        Ready = Count;
                    else if ("Dispatched".equalsIgnoreCase(St))
                        Disp = Count;
                    else if ("Delivered".equalsIgnoreCase(St))
                        Del = Count;
                }
            }
            int Totalslots = 0, Occupiedslots = 0;
            try (Statement Stmt = Conn.createStatement();
                    ResultSet Rs = Stmt.executeQuery("SELECT COUNT(*), SUM(is_occupied) FROM storage_locations")) {
                if (Rs.next()) {
                    Totalslots = Rs.getInt(1);
                    Occupiedslots = Rs.getInt(2);
                }
            }
            return new SystemStatistics(Total, Stored, Ready, Disp, Del, Totalslots, Occupiedslots,
                    Totalslots - Occupiedslots, Math.round(Revenue * 100.0) / 100.0);
        } catch (Exception E) {
            throw new DatabaseOperationException("Stats error: " + E.getMessage(), E);
        }
    }
}
