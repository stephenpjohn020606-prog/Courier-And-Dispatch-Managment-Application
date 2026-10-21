package courier.gui;

import courier.model.*;
import courier.database.DBManager;
import courier.exceptions.*;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

public class RegisterCourierPanel extends JPanel implements ActionListener{
    private final MainDashboard Dashboard;
    private final DBManager Db;

    private final JTextField Regidfield;
    private final JComboBox<String> Regtypecombo;
    private final JTextField Regsendername,Regsenderphone,Regsenderemail;
    private final JTextField Regreceivername,Regreceiverphone,Regcity,Regaddress;
    private final JTextField Regparceltype,Regweight;
    private final JComboBox<String> Regslotcombo;
    private final JLabel Regfeelabel;
    private final JButton Calcbtn;
    private final JButton Savebtn;
    private final JButton Clearbtn;

    public RegisterCourierPanel(MainDashboard Dashboard,DBManager Db){
        this.Dashboard=Dashboard;
        this.Db=Db;
        setLayout(new BorderLayout(10,10));
        setBorder(new EmptyBorder(15,20,15,20));

        JPanel Form=new JPanel();
        Form.setLayout(new BoxLayout(Form,BoxLayout.Y_AXIS));

        JPanel Typebox=new JPanel(new GridLayout(1,4,10,5));
        Typebox.setBorder(BorderFactory.createTitledBorder("1. Courier Classification"));
        Typebox.add(new JLabel("Courier ID (Auto):"));
        Regidfield=new JTextField();
        Regidfield.setEditable(false);
        Typebox.add(Regidfield);

        Typebox.add(new JLabel("Courier Type:"));
        Regtypecombo=new JComboBox<>(new String[]{"Standard","Express","Fragile"});
        Regtypecombo.addActionListener(this);
        Typebox.add(Regtypecombo);
        Form.add(Typebox);
        Form.add(Box.createVerticalStrut(10));

        JPanel Parties=new JPanel(new GridLayout(1,2,15,0));
        JPanel Senderbox=new JPanel(new GridLayout(3,2,5,8));
        Senderbox.setBorder(BorderFactory.createTitledBorder("2. Sender Details"));
        Senderbox.add(new JLabel("Sender Name *:"));Regsendername=new JTextField();Senderbox.add(Regsendername);
        Senderbox.add(new JLabel("Sender Phone *:"));Regsenderphone=new JTextField();Senderbox.add(Regsenderphone);
        Senderbox.add(new JLabel("Sender Email:"));Regsenderemail=new JTextField();Senderbox.add(Regsenderemail);
        Parties.add(Senderbox);

        JPanel Receiverbox=new JPanel(new GridLayout(4,2,5,8));
        Receiverbox.setBorder(BorderFactory.createTitledBorder("3. Receiver Details"));
        Receiverbox.add(new JLabel("Receiver Name *:"));Regreceivername=new JTextField();Receiverbox.add(Regreceivername);
        Receiverbox.add(new JLabel("Receiver Phone *:"));Regreceiverphone=new JTextField();Receiverbox.add(Regreceiverphone);
        Receiverbox.add(new JLabel("City *:"));Regcity=new JTextField();Receiverbox.add(Regcity);
        Receiverbox.add(new JLabel("Address *:"));Regaddress=new JTextField();Receiverbox.add(Regaddress);
        Parties.add(Receiverbox);
        Form.add(Parties);
        Form.add(Box.createVerticalStrut(10));

        JPanel Pkgbox=new JPanel(new GridLayout(4,2,8,8));
        Pkgbox.setBorder(BorderFactory.createTitledBorder("4. Package Specifications & Storage"));
        Pkgbox.add(new JLabel("Parcel Description *:"));Regparceltype=new JTextField();Pkgbox.add(Regparceltype);
        Pkgbox.add(new JLabel("Weight (kg) *:"));Regweight=new JTextField();Pkgbox.add(Regweight);

        Pkgbox.add(new JLabel("Assign Basement Storage Slot:"));
        Regslotcombo=new JComboBox<>();
        Pkgbox.add(Regslotcombo);

        Pkgbox.add(new JLabel("Calculated Delivery Fee:"));
        JPanel Feep=new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));
        Regfeelabel=new JLabel("$0.00");
        Regfeelabel.setFont(new Font("Arial",Font.BOLD,14));
        Regfeelabel.setForeground(new Color(0,102,204));
        Calcbtn=new JButton("Calculate Fee");
        Calcbtn.addActionListener(this);
        Feep.add(Regfeelabel);
        Feep.add(Calcbtn);
        Pkgbox.add(Feep);
        Form.add(Pkgbox);
        Form.add(Box.createVerticalStrut(15));

        JPanel Btnpanel=new JPanel(new FlowLayout(FlowLayout.CENTER,20,10));
        Savebtn=new JButton("Register & Store Courier");
        Savebtn.setFont(new Font("Arial",Font.BOLD,13));
        Savebtn.setBackground(new Color(40,167,69));
        Savebtn.setForeground(Color.WHITE);
        Savebtn.setPreferredSize(new Dimension(220,36));
        Savebtn.addActionListener(this);

        Clearbtn=new JButton("Clear Form");
        Clearbtn.setPreferredSize(new Dimension(120,36));
        Clearbtn.addActionListener(this);

        Btnpanel.add(Savebtn);
        Btnpanel.add(Clearbtn);
        Form.add(Btnpanel);

        add(new JScrollPane(Form),BorderLayout.CENTER);
    }

    public void Refreshregisterform(){
        Regidfield.setText("CR-"+(System.currentTimeMillis()%100000));
        Regslotcombo.removeAllItems();
        Regslotcombo.addItem("None (No Storage Slot Assigned)");
        try{
            Vector<StorageLocation> Available=Db.Getavailablestoragelocations();
            for(int I=0;I<Available.size();I++){
                StorageLocation S=Available.get(I);
                Regslotcombo.addItem(S.Getlocationid()+": "+S.Getformattedcode()+" ("+S.Getsection()+")");
            }
        }catch(Exception Ignored){}
    }

    private void Calculatepreviewfee(){
        try{
            double W=Double.parseDouble(Regweight.getText().trim());
            String Type=(String)Regtypecombo.getSelectedItem();
            double Fee=0.0;
            if("Standard".equalsIgnoreCase(Type)){
                Fee=5.00+(W*2.50);
            }else if("Express".equalsIgnoreCase(Type)){
                Fee=12.00+(W*4.00)+5.00;
            }else if("Fragile".equalsIgnoreCase(Type)){
                Fee=8.00+(W*3.00)+8.00;
            }
            Regfeelabel.setText("$"+String.format("%.2f",Fee));
        }catch(Exception Ignored){
            Regfeelabel.setText("$0.00");
        }
    }

    private void Handleregister(){
        try{
            String Id=Regidfield.getText().trim();
            String Type=(String)Regtypecombo.getSelectedItem();
            Customer Sender=new Customer(Regsendername.getText().trim(),Regsenderphone.getText().trim(),Regsenderemail.getText().trim(),"Office");
            Customer Receiver=new Customer(Regreceivername.getText().trim(),Regreceiverphone.getText().trim(),"",Regaddress.getText().trim());
            String Addr=Regaddress.getText().trim();
            String City=Regcity.getText().trim();
            String Desc=Regparceltype.getText().trim();
            double Weight=Double.parseDouble(Regweight.getText().trim());
            String Date=new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());

            Courier Courierobj;
            if("Express".equalsIgnoreCase(Type)){
                Courierobj=new ExpressCourier(Id,Sender,Receiver,Addr,City,Weight,Desc,Date,24);
            }else if("Fragile".equalsIgnoreCase(Type)){
                Courierobj=new FragileCourier(Id,Sender,Receiver,Addr,City,Weight,Desc,Date,"General Fragile");
            }else{
                Courierobj=new StandardCourier(Id,Sender,Receiver,Addr,City,Weight,Desc,Date);
            }

            Integer Slotid=null;
            String Slotsel=(String)Regslotcombo.getSelectedItem();
            if(Slotsel!=null&&!Slotsel.startsWith("None")){
                Slotid=Integer.parseInt(Slotsel.split(":")[0].trim());
            }

            Db.Registercourier(Courierobj,Slotid);

            String Slotmsg="\nNo slot assigned.";
            if(Slotid!=null){
                Slotmsg="\nAssigned to Basement Slot: "+Slotsel.split(":")[1].trim();
            }

            JOptionPane.showMessageDialog(this,"Courier "+Id+" registered successfully!\nType: "+Courierobj.Getcouriertype()+
                    "\nFee: $"+String.format("%.2f",Courierobj.Getdeliverycharge())+Slotmsg,
                    "Registration Success",JOptionPane.INFORMATION_MESSAGE);

            Resetform();
            Dashboard.Refreshalldata();
        }catch(ValidationException Ex){
            JOptionPane.showMessageDialog(this,Ex.getMessage(),"Input Validation",JOptionPane.WARNING_MESSAGE);
        }catch(NumberFormatException Ex){
            JOptionPane.showMessageDialog(this,"Please enter a valid numeric weight.","Invalid Weight",JOptionPane.WARNING_MESSAGE);
        }catch(Exception Ex){
            JOptionPane.showMessageDialog(this,"Error: "+Ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void Resetform(){
        Regsendername.setText("");
        Regsenderphone.setText("");
        Regsenderemail.setText("");
        Regreceivername.setText("");
        Regreceiverphone.setText("");
        Regcity.setText("");
        Regaddress.setText("");
        Regparceltype.setText("");
        Regweight.setText("");
        Regfeelabel.setText("$0.00");
        Refreshregisterform();
    }

    @Override
    public void actionPerformed(ActionEvent E){
        Object Src=E.getSource();
        if(Src==Calcbtn||Src==Regtypecombo){
            Calculatepreviewfee();
        }else if(Src==Savebtn){
            Handleregister();
        }else if(Src==Clearbtn){
            Resetform();
        }
    }
}
