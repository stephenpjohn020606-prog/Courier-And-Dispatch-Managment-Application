package courier.gui;

import courier.model.*;
import courier.database.DBManager;
import courier.exceptions.*;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

public class StorageManagementPanel extends JPanel implements ActionListener{
    private final MainDashboard Dashboard;
    private final DBManager Db;

    private final JComboBox<String> Secfiltercombo;
    private final JComboBox<String> Occfiltercombo;
    private final JButton Refreshbtn;
    private final JLabel Storagesummarylabel;

    private final JTable Storagetable;
    private final DefaultTableModel Storagetablemodel;

    private final JButton Assignbtn;
    private final JButton Addbtn;
    private final JButton Deletebtn;

    public StorageManagementPanel(MainDashboard Dashboard,DBManager Db){
        this.Dashboard=Dashboard;
        this.Db=Db;
        setLayout(new BorderLayout(10,10));
        setBorder(new EmptyBorder(10,15,10,15));

        JPanel Topbar=new JPanel(new BorderLayout(10,5));
        JPanel Filterpanel=new JPanel(new FlowLayout(FlowLayout.LEFT,10,5));
        Filterpanel.add(new JLabel("Section:"));
        Secfiltercombo=new JComboBox<>(new String[]{"All Sections","Section A (General)","Section B (Express)","Section C (Fragile)"});
        Secfiltercombo.addActionListener(this);
        Filterpanel.add(Secfiltercombo);

        Filterpanel.add(new JLabel("Status:"));
        Occfiltercombo=new JComboBox<>(new String[]{"All Slots","Available (Free) Only","Occupied Only"});
        Occfiltercombo.addActionListener(this);
        Filterpanel.add(Occfiltercombo);

        Refreshbtn=new JButton("Refresh");
        Refreshbtn.addActionListener(this);
        Filterpanel.add(Refreshbtn);
        Topbar.add(Filterpanel,BorderLayout.WEST);

        Storagesummarylabel=new JLabel("Total: 0 | Occupied: 0 | Free: 0  ",SwingConstants.RIGHT);
        Storagesummarylabel.setFont(new Font("Arial",Font.BOLD,12));
        Topbar.add(Storagesummarylabel,BorderLayout.EAST);
        add(Topbar,BorderLayout.NORTH);

        String[] Cols={"Slot ID","Section","Rack","Shelf","Location Code","Occupancy","Occupying Courier ID"};
        Storagetablemodel=new DefaultTableModel(Cols,0);
        Storagetable=new JTable(Storagetablemodel);
        Storagetable.setDefaultEditor(Object.class,null);
        Storagetable.setRowHeight(24);
        Storagetable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(Storagetable),BorderLayout.CENTER);

        JPanel Actionpanel=new JPanel(new FlowLayout(FlowLayout.RIGHT,10,5));
        Assignbtn=new JButton("Assign Courier to Slot");
        Assignbtn.addActionListener(this);
        Addbtn=new JButton("Add New Storage Slot");
        Addbtn.addActionListener(this);
        Deletebtn=new JButton("Delete Empty Slot");
        Deletebtn.addActionListener(this);

        Actionpanel.add(Assignbtn);
        Actionpanel.add(Addbtn);
        Actionpanel.add(Deletebtn);
        add(Actionpanel,BorderLayout.SOUTH);
    }

    public void Loadstoragetabledata(){
        Storagetablemodel.setRowCount(0);
        try{
            Vector<StorageLocation> List=Db.Getallstoragelocations();
            int Total=List.size();
            int Occ=0;
            String Sec=(String)Secfiltercombo.getSelectedItem();
            String Occf=(String)Occfiltercombo.getSelectedItem();
            for(int I=0;I<List.size();I++){
                StorageLocation S=List.get(I);
                if(S.Isoccupied())Occ++;
                if(Sec!=null&&!Sec.equals("All Sections")&&!S.Getsection().equalsIgnoreCase(Sec))continue;
                if("Available (Free) Only".equals(Occf)&&S.Isoccupied())continue;
                if("Occupied Only".equals(Occf)&&!S.Isoccupied())continue;

                Vector<Object> Row=new Vector<>();
                Row.add(S.Getlocationid());
                Row.add(S.Getsection());
                Row.add(S.Getrack());
                Row.add(S.Getshelf());
                Row.add(S.Getformattedcode());
                String Occtext="Available";
                if(S.Isoccupied()){
                    Occtext="Occupied";
                }
                Row.add(Occtext);
                String Occupyingid="-";
                if(S.Getcurrentcourierid()!=null){
                    Occupyingid=S.Getcurrentcourierid();
                }
                Row.add(Occupyingid);
                Storagetablemodel.addRow(Row);
            }
            Storagesummarylabel.setText("Total: "+Total+" | Occupied: "+Occ+" | Free: "+(Total-Occ)+"  ");
        }catch(Exception Ex){
            JOptionPane.showMessageDialog(this,"Error loading storage: "+Ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void Handleassigncouriertoslot(){
        int Row=Storagetable.getSelectedRow();
        if(Row==-1){
            JOptionPane.showMessageDialog(this,"Please select a slot row first.");
            return;
        }
        int Slotid=(Integer)Storagetablemodel.getValueAt(Row,0);
        String Code=(String)Storagetablemodel.getValueAt(Row,4);
        String Cid=JOptionPane.showInputDialog(this,"Enter Courier ID to store at ["+Code+"]:");
        if(Cid==null||Cid.trim().isEmpty())return;
        Cid=Cid.trim().toUpperCase();
        try{
            Db.Assignstorage(Cid,Slotid);
            JOptionPane.showMessageDialog(this,"Courier "+Cid+" stored at "+Code+" successfully!");
            Dashboard.Refreshalldata();
        }catch(CourierNotFoundException Ex){
            JOptionPane.showMessageDialog(this,Ex.getMessage(),"Courier Not Found",JOptionPane.WARNING_MESSAGE);
        }catch(InvalidStorageLocationException Ex){
            JOptionPane.showMessageDialog(this,Ex.getMessage(),"Slot Occupied",JOptionPane.WARNING_MESSAGE);
        }catch(Exception Ex){
            JOptionPane.showMessageDialog(this,"Error: "+Ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void Handleaddnewslot(){
        JTextField Secf=new JTextField("Section A (General)");
        JTextField Rackf=new JTextField("Rack-3");
        JTextField Shelff=new JTextField("Shelf-A");
        JPanel F=new JPanel(new GridLayout(3,2,5,5));
        F.add(new JLabel("Section:"));F.add(Secf);
        F.add(new JLabel("Rack:"));F.add(Rackf);
        F.add(new JLabel("Shelf:"));F.add(Shelff);
        int Res=JOptionPane.showConfirmDialog(this,F,"Add New Storage Slot",JOptionPane.OK_CANCEL_OPTION);
        if(Res==JOptionPane.OK_OPTION){
            try{
                Db.Addstorageslot(Secf.getText().trim(),Rackf.getText().trim(),Shelff.getText().trim());
                JOptionPane.showMessageDialog(this,"Slot added successfully!");
                Dashboard.Refreshalldata();
            }catch(ValidationException Ex){
                JOptionPane.showMessageDialog(this,Ex.getMessage(),"Input Validation",JOptionPane.WARNING_MESSAGE);
            }catch(Exception Ex){
                JOptionPane.showMessageDialog(this,"Error: "+Ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void Handledeleteslot(){
        int Row=Storagetable.getSelectedRow();
        if(Row==-1){
            JOptionPane.showMessageDialog(this,"Please select an empty slot to delete.");
            return;
        }
        int Slotid=(Integer)Storagetablemodel.getValueAt(Row,0);
        String Status=(String)Storagetablemodel.getValueAt(Row,5);
        if("Occupied".equalsIgnoreCase(Status)){
            JOptionPane.showMessageDialog(this,"Cannot delete an occupied storage slot.","Busy",JOptionPane.WARNING_MESSAGE);
            return;
        }
        int Confirm=JOptionPane.showConfirmDialog(this,"Delete slot #"+Slotid+"?","Confirm",JOptionPane.YES_NO_OPTION);
        if(Confirm==JOptionPane.YES_OPTION){
            try{
                Db.Deletestorageslot(Slotid);
                JOptionPane.showMessageDialog(this,"Slot deleted successfully.");
                Dashboard.Refreshalldata();
            }catch(InvalidStorageLocationException Ex){
                JOptionPane.showMessageDialog(this,Ex.getMessage(),"Slot Error",JOptionPane.WARNING_MESSAGE);
            }catch(Exception Ex){
                JOptionPane.showMessageDialog(this,"Error: "+Ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent E){
        Object Src=E.getSource();
        if(Src==Secfiltercombo||Src==Occfiltercombo||Src==Refreshbtn){
            Loadstoragetabledata();
        }else if(Src==Assignbtn){
            Handleassigncouriertoslot();
        }else if(Src==Addbtn){
            Handleaddnewslot();
        }else if(Src==Deletebtn){
            Handledeleteslot();
        }
    }
}
