package courier;

import courier.database.DBManager;
import courier.gui.MainDashboard;

public class Main {
    public static void main(String[] Args) {
        try {
            DBManager.Getinstance().Initdatabase();
            MainDashboard Dashboard = new MainDashboard();
            Dashboard.setVisible(true);
        } catch (Exception E) {
            System.out.println("Error launching application: " + E.getMessage());
        }
    }
}
