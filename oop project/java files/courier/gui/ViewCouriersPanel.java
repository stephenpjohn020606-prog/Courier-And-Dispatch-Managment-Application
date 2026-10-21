package courier.gui;

import courier.model.*;
import courier.database.DBManager;
import courier.exceptions.*;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

public class ViewCouriersPanel extends JPanel implements ActionListener{
    private final MainDashboard Dashboard;
    private final DBManager Db;

    private final JTextField Searchfield;
    private final JComboBox<String> Statusfiltercombo;
    private final JButton Searchbtn;
    private final JButton Resetbtn;

    private final JTable Couriertable;
    private final DefaultTableModel Couriertablemodel;

    private final JButton Detailsbtn;
    private final JButton Updatestatusbtn;
    private final JButton Markdeliveredbtn;
    private final JButton Deletebtn;
    private final JButton Refreshbtn;

    public ViewCouriersPanel(MainDashboard Dashboard,DBManager Db){
        this.Dashboard=Dashboard;
        this.Db=Db;
        setLayout(new BorderLayout(10,10));
        setBorder(new EmptyBorder(10,15,10,15));

        JPanel Searchbar=new JPanel(new FlowLayout(FlowLayout.LEFT,10,5));
        Searchbar.setBorder(BorderFactory.createTitledBorder("Search & Filter"));
        Searchbar.add(new JLabel("Keyword:"));
        Searchfield=new JTextField(14);
        Searchfield.addActionListener(this);
        Searchbar.add(Searchfield);

        Searchbar.add(new JLabel("Status:"));
        Statusfiltercombo=new JComboBox<>(new String[]{"All","Stored","Ready for Dispatch","Dispatched","Delivered"});
        Statusfiltercombo.addActionListener(this);
        Searchbar.add(Statusfiltercombo);

        Searchbtn=new JButton("Search");
        Searchbtn.addActionListener(this);
        Searchbar.add(Searchbtn);

        Resetbtn=new JButton("Reset");
        Resetbtn.addActionListener(this);
        Searchbar.add(Resetbtn);
        add(Searchbar,BorderLayout.NORTH);

        String[] Columns={"Courier ID","Type","Sender","Receiver","City","Weight","Status","Storage Location","Fee ($)"};
        Couriertablemodel=new DefaultTableModel(Columns,0);
        Couriertable=new JTable(Couriertablemodel);
        Couriertable.setDefaultEditor(Object.class,null);
        Couriertable.setRowHeight(24);
        Couriertable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(Couriertable),BorderLayout.CENTER);

        JPanel Actionpanel=new JPanel(new FlowLayout(FlowLayout.RIGHT,10,5));
        Detailsbtn=new JButton("View Details");
        Detailsbtn.addActionListener(this);
        Updatestatusbtn=new JButton("Update Status");
        Updatestatusbtn.addActionListener(this);
        Markdeliveredbtn=new JButton("Mark Delivered");
        Markdeliveredbtn.addActionListener(this);
        Deletebtn=new JButton("Delete");
        Deletebtn.addActionListener(this);
        Refreshbtn=new JButton("Refresh");
        Refreshbtn.addActionListener(this);

        Actionpanel.add(Detailsbtn);
        Actionpanel.add(Updatestatusbtn);
        Actionpanel.add(Markdeliveredbtn);
        Actionpanel.add(Deletebtn);
        Actionpanel.add(Refreshbtn);
        add(Actionpanel,BorderLayout.SOUTH);
    }

    public void Loadcouriertabledata(){
        Couriertablemodel.setRowCount(0);
        try{
            String Kw=Searchfield.getText().trim();
            String St=(String)Statusfiltercombo.getSelectedItem();
            Vector<Courier> List=Db.Searchcouriers(Kw,"All",St);
            for(int I=0;I<List.size();I++){
                Courier C=List.get(I);
                Vector<Object> Row=new Vector<>();
                Row.add(C.Getcourierid());
                Row.add(C.Getcouriertype());
                Row.add(C.Getsender().Getname());
                Row.add(C.Getreceiver().Getname());
                Row.add(C.Getdestinationcity());
                Row.add(C.Getweight()+" kg");
                Row.add(C.Getstatus().Getdisplayname());
                String Storagetext="Unassigned";
                if(C.Getstoragelocation()!=null){
                    Storagetext=C.Getstoragelocation().Getformattedcode();
                }
                Row.add(Storagetext);
                Row.add(String.format("%.2f",C.Getdeliverycharge()));
                Couriertablemodel.addRow(Row);
            }
        }catch(Exception Ex){
            JOptionPane.showMessageDialog(this,"Error loading couriers: "+Ex.getMessage(),"Database Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private Courier Getselectedcourier(){
        int Row=Couriertable.getSelectedRow();
        if(Row==-1){
            JOptionPane.showMessageDialog(this,"Please select a courier row from the table first.","No Selection",JOptionPane.WARNING_MESSAGE);
            return null;
        }
        String Courierid=(String)Couriertablemodel.getValueAt(Row,0);
        try{
            return Db.Getcourierbyid(Courierid);
        }catch(Exception Ex){
            JOptionPane.showMessageDialog(this,"Error fetching courier details: "+Ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void Handleviewdetails(){
        Courier C=Getselectedcourier();
        if(C!=null){
            new CourierDetailsDialog(Dashboard,C).setVisible(true);
        }
    }

    private void Handleupdatestatus(){
        Courier C=Getselectedcourier();
        if(C!=null){
            new UpdateStatusDialog(Dashboard,C,Db,Dashboard).setVisible(true);
        }
    }

    private void Handlemarkdelivered(){
        Courier C=Getselectedcourier();
        if(C==null)return;
        if(C.Getstatus()==CourierStatus.DELIVERED){
            JOptionPane.showMessageDialog(this,"Courier is already Delivered.");
            return;
        }
        if(C.Getstatus()!=CourierStatus.DISPATCHED){
            JOptionPane.showMessageDialog(this,"Courier must be 'Dispatched' before marking 'Delivered'.","Cannot Deliver",JOptionPane.WARNING_MESSAGE);
            return;
        }
        int Confirm=JOptionPane.showConfirmDialog(this,"Mark "+C.Getcourierid()+" as Delivered?","Confirm",JOptionPane.YES_NO_OPTION);
        if(Confirm==JOptionPane.YES_OPTION){
            try{
                Db.Updatecourierstatus(C.Getcourierid(),CourierStatus.DELIVERED);
                JOptionPane.showMessageDialog(this,"Courier marked as DELIVERED!");
                Dashboard.Refreshalldata();
            }catch(InvalidStatusTransitionException Ex){
                JOptionPane.showMessageDialog(this,Ex.getMessage(),"Invalid Status Jump",JOptionPane.WARNING_MESSAGE);
            }catch(Exception Ex){
                JOptionPane.showMessageDialog(this,"Error: "+Ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void Handledeletecourier(){
        Courier C=Getselectedcourier();
        if(C==null)return;
        int Confirm=JOptionPane.showConfirmDialog(this,"Delete courier "+C.Getcourierid()+"?","Confirm Delete",JOptionPane.YES_NO_OPTION);
        if(Confirm==JOptionPane.YES_OPTION){
            try{
                Db.Deletecourier(C.Getcourierid());
                JOptionPane.showMessageDialog(this,"Courier deleted successfully.");
                Dashboard.Refreshalldata();
            }catch(Exception Ex){
                JOptionPane.showMessageDialog(this,"Error: "+Ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent E){
        Object Src=E.getSource();
        if(Src==Searchbtn||Src==Searchfield||Src==Statusfiltercombo){
            Loadcouriertabledata();
        }else if(Src==Resetbtn){
            Searchfield.setText("");
            Statusfiltercombo.setSelectedIndex(0);
            Loadcouriertabledata();
        }else if(Src==Detailsbtn){
            Handleviewdetails();
        }else if(Src==Updatestatusbtn){
            Handleupdatestatus();
        }else if(Src==Markdeliveredbtn){
            Handlemarkdelivered();
        }else if(Src==Deletebtn){
            Handledeletecourier();
        }else if(Src==Refreshbtn){
            Dashboard.Refreshalldata();
        }
    }
}
