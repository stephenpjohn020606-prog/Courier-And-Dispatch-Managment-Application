package courier.gui;

import courier.model.*;
import courier.database.DBManager;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

public class MainDashboard extends JFrame implements ChangeListener{
    private final DBManager Db;
    private final JLabel Summarylabel;
    private final JTabbedPane Tabbedpane;
    private final ViewCouriersPanel Viewcourierspanel;
    private final RegisterCourierPanel Registercourierpanel;
    private final StorageManagementPanel Storagemanagementpanel;
    private final DispatchManagementPanel Dispatchmanagementpanel;

    public MainDashboard(){
        super("Courier Storage and Dispatch Management System");
        DBManager Tempdb=null;
        try{
            Tempdb=DBManager.Getinstance();
        }catch(Exception Ignored){}
        this.Db=Tempdb;

        setSize(1100,720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel Topheader=new JPanel(new BorderLayout(10,5));
        Topheader.setBackground(new Color(24,76,120));
        Topheader.setBorder(new EmptyBorder(12,16,12,16));

        JLabel Titlelbl=new JLabel("Courier Storage & Dispatch Management System");
        Titlelbl.setFont(new Font("Arial",Font.BOLD,18));
        Titlelbl.setForeground(Color.WHITE);
        Topheader.add(Titlelbl,BorderLayout.WEST);

        JPanel Metricspanel=new JPanel(new FlowLayout(FlowLayout.CENTER,20,6));
        Metricspanel.setBackground(new Color(240,244,248));
        Summarylabel=new JLabel("Loading statistics...",SwingConstants.CENTER);
        Summarylabel.setFont(new Font("Arial",Font.BOLD,13));
        Metricspanel.add(Summarylabel);

        JPanel Northpanel=new JPanel(new BorderLayout());
        Northpanel.add(Topheader,BorderLayout.NORTH);
        Northpanel.add(Metricspanel,BorderLayout.SOUTH);
        add(Northpanel,BorderLayout.NORTH);

        Viewcourierspanel=new ViewCouriersPanel(this,Db);
        Registercourierpanel=new RegisterCourierPanel(this,Db);
        Storagemanagementpanel=new StorageManagementPanel(this,Db);
        Dispatchmanagementpanel=new DispatchManagementPanel(this,Db);

        Tabbedpane=new JTabbedPane();
        Tabbedpane.setFont(new Font("Arial",Font.BOLD,13));
        Tabbedpane.addTab("1. View & Search Couriers",Viewcourierspanel);
        Tabbedpane.addTab("2. Register Courier",Registercourierpanel);
        Tabbedpane.addTab("3. Basement Storage Management",Storagemanagementpanel);
        Tabbedpane.addTab("4. Dispatch Management",Dispatchmanagementpanel);
        Tabbedpane.addChangeListener(this);
        add(Tabbedpane,BorderLayout.CENTER);

        Refreshalldata();
    }

    public void Refreshalldata(){
        Refreshkpis();
        int Idx=Tabbedpane.getSelectedIndex();
        if(Idx==0||Idx==-1){
            Viewcourierspanel.Loadcouriertabledata();
        }else if(Idx==1){
            Registercourierpanel.Refreshregisterform();
        }else if(Idx==2){
            Storagemanagementpanel.Loadstoragetabledata();
        }else if(Idx==3){
            Dispatchmanagementpanel.Loaddispatchdata();
        }
    }

    private void Refreshkpis(){
        try{
            SystemStatistics S=Db.Getstatistics();
            Summarylabel.setText(String.format(
                "Total Couriers: %d   |   In Basement: %d   |   Ready for Dispatch: %d   |   Dispatched: %d   |   Delivered: %d   |   Free Storage Slots: %d",
                S.Gettotalcouriers(),S.Getstoredcount(),S.Getreadycount(),S.Getdispatchedcount(),S.Getdeliveredcount(),S.Getfreestorageslots()
            ));
        }catch(Exception Ignored){
            Summarylabel.setText("Courier Management System Ready");
        }
    }

    @Override
    public void stateChanged(ChangeEvent E){
        Refreshalldata();
    }
}
