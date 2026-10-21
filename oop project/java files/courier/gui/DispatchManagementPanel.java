package courier.gui;

import courier.model.*;
import courier.database.DBManager;
import courier.exceptions.*;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

public class DispatchManagementPanel extends JPanel implements ActionListener {
    private final MainDashboard Dashboard;
    private final DBManager Db;

    private final JTable Readytable;
    private final DefaultTableModel Readytablemodel;
    private final JTextField Dspcourieridfield;
    private final JTextField Dspvehiclefield;
    private final JTextField Dspdriverfield;
    private final JTextField Dspremarksfield;
    private final JButton Pickcourierbtn;
    private final JButton Dispatchbtn;

    private final JTable Dispatchhistorytable;
    private final DefaultTableModel Dispatchhistorymodel;

    public DispatchManagementPanel(MainDashboard Dashboard, DBManager Db) {
        this.Dashboard = Dashboard;
        this.Db = Db;
        setLayout(new GridLayout(2, 1, 0, 10));
        setBorder(new EmptyBorder(10, 15, 10, 15));

        JPanel Topsplit = new JPanel(new BorderLayout(10, 0));
        JPanel Readybox = new JPanel(new BorderLayout());
        Readybox.setBorder(BorderFactory.createTitledBorder("Couriers Ready for Dispatch"));
        String[] Readycols = { "Courier ID", "Type", "Receiver", "City", "Weight", "Storage Slot", "Status" };
        Readytablemodel = new DefaultTableModel(Readycols, 0);
        Readytable = new JTable(Readytablemodel);
        Readytable.setDefaultEditor(Object.class, null);
        Readytable.setRowHeight(22);
        Readytable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        Readybox.add(new JScrollPane(Readytable), BorderLayout.CENTER);

        JPanel Pickbar = new JPanel();
        Pickcourierbtn = new JButton("Select Highlighted Courier for Dispatch");
        Pickcourierbtn.addActionListener(this);
        Pickbar.add(Pickcourierbtn);
        Readybox.add(Pickbar, BorderLayout.SOUTH);
        Topsplit.add(Readybox, BorderLayout.CENTER);

        JPanel Formbox = new JPanel(new GridLayout(5, 2, 8, 8));
        Formbox.setPreferredSize(new Dimension(320, 0));
        Formbox.setBorder(BorderFactory.createTitledBorder("Dispatch Details"));
        Formbox.add(new JLabel("Courier ID *:"));
        Dspcourieridfield = new JTextField();
        Formbox.add(Dspcourieridfield);
        Formbox.add(new JLabel("Vehicle / Van *:"));
        Dspvehiclefield = new JTextField("VAN-67");
        Formbox.add(Dspvehiclefield);
        Formbox.add(new JLabel("Driver Name *:"));
        Dspdriverfield = new JTextField("Chris Joy");
        Formbox.add(Dspdriverfield);
        Formbox.add(new JLabel("Remarks:"));
        Dspremarksfield = new JTextField();
        Formbox.add(Dspremarksfield);

        Dispatchbtn = new JButton("Dispatch Courier");
        Dispatchbtn.setBackground(new Color(0, 102, 204));
        Dispatchbtn.setForeground(Color.WHITE);
        Dispatchbtn.setFont(new Font("Arial", Font.BOLD, 12));
        Dispatchbtn.addActionListener(this);
        Formbox.add(new JLabel(""));
        Formbox.add(Dispatchbtn);
        Topsplit.add(Formbox, BorderLayout.EAST);
        add(Topsplit);

        JPanel Historybox = new JPanel(new BorderLayout());
        Historybox.setBorder(BorderFactory.createTitledBorder("Dispatched Couriers History"));
        String[] Histcols = { "Dispatch ID", "Courier ID", "Date", "Vehicle", "Driver", "Destination", "Remarks" };
        Dispatchhistorymodel = new DefaultTableModel(Histcols, 0);
        Dispatchhistorytable = new JTable(Dispatchhistorymodel);
        Dispatchhistorytable.setDefaultEditor(Object.class, null);
        Dispatchhistorytable.setRowHeight(22);
        Historybox.add(new JScrollPane(Dispatchhistorytable), BorderLayout.CENTER);
        add(Historybox);
    }

    public void Loaddispatchdata() {
        Readytablemodel.setRowCount(0);
        try {
            Vector<Courier> List = Db.Getallcouriers();
            for (int I = 0; I < List.size(); I++) {
                Courier C = List.get(I);
                if (C.Getstatus() == CourierStatus.READY_FOR_DISPATCH || C.Getstatus() == CourierStatus.STORED) {
                    Vector<Object> Row = new Vector<>();
                    Row.add(C.Getcourierid());
                    Row.add(C.Getcouriertype());
                    Row.add(C.Getreceiver().Getname());
                    Row.add(C.Getdestinationcity());
                    Row.add(C.Getweight() + " kg");
                    String Storagetext = "Unassigned";
                    if (C.Getstoragelocation() != null) {
                        Storagetext = C.Getstoragelocation().Getformattedcode();
                    }
                    Row.add(Storagetext);
                    Row.add(C.Getstatus().Getdisplayname());
                    Readytablemodel.addRow(Row);
                }
            }
        } catch (Exception Ignored) {
        }

        Dispatchhistorymodel.setRowCount(0);
        try {
            Vector<DispatchRecord> List = Db.Getalldispatches();
            for (int I = 0; I < List.size(); I++) {
                DispatchRecord D = List.get(I);
                Vector<Object> Row = new Vector<>();
                Row.add(D.Getdispatchid());
                Row.add(D.Getcourierid());
                Row.add(D.Getdispatchdate());
                Row.add(D.Getvehiclenumber());
                Row.add(D.Getdrivername());
                Row.add(D.Getdestination());
                Row.add(D.Getremarks());
                Dispatchhistorymodel.addRow(Row);
            }
        } catch (Exception Ignored) {
        }
    }

    private void Handlepickcourier() {
        int R = Readytable.getSelectedRow();
        if (R == -1) {
            JOptionPane.showMessageDialog(this, "Please select a courier row from the table first.");
            return;
        }
        String Cid = (String) Readytablemodel.getValueAt(R, 0);
        Dspcourieridfield.setText(Cid);
    }

    private void Handledispatchcourier() {
        String Cid = Dspcourieridfield.getText().trim();
        if (Cid.isEmpty()) {
            int R = Readytable.getSelectedRow();
            if (R != -1) {
                Cid = (String) Readytablemodel.getValueAt(R, 0);
                Dspcourieridfield.setText(Cid);
            }
        }
        if (Cid.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a courier row or enter a Courier ID.");
            return;
        }
        String Vehicle = Dspvehiclefield.getText().trim();
        String Driver = Dspdriverfield.getText().trim();
        String Remarks = Dspremarksfield.getText().trim();
        try {
            DispatchRecord Rec = Db.Dispatchcourier(Cid, Vehicle, Driver, Remarks);
            JOptionPane.showMessageDialog(this, "Courier " + Cid + " has been DISPATCHED successfully!\nDispatch ID: " +
                    Rec.Getdispatchid() + "\nVehicle: " + Rec.Getvehiclenumber() + " | Driver: " + Rec.Getdrivername() +
                    "\nBasement storage slot has been automatically freed.", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            Dspcourieridfield.setText("");
            Dspremarksfield.setText("");
            Dashboard.Refreshalldata();
        } catch (ValidationException | InvalidStatusTransitionException Ex) {
            JOptionPane.showMessageDialog(this, Ex.getMessage(), "Workflow Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception Ex) {
            JOptionPane.showMessageDialog(this, "Error: " + Ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent E) {
        Object Src = E.getSource();
        if (Src == Pickcourierbtn) {
            Handlepickcourier();
        } else if (Src == Dispatchbtn) {
            Handledispatchcourier();
        }
    }
}
