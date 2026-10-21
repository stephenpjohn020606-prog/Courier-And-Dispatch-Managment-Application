package courier.gui;

import courier.model.Courier;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CourierDetailsDialog extends JDialog implements ActionListener{
    private final JButton Closebtn;

    public CourierDetailsDialog(JFrame Parent,Courier C){
        super(Parent,"Courier Details - "+C.Getcourierid(),true);
        setSize(520,480);
        setLocationRelativeTo(Parent);
        setLayout(new BorderLayout(10,10));

        JPanel Panel=new JPanel();
        Panel.setLayout(new BoxLayout(Panel,BoxLayout.Y_AXIS));
        Panel.setBorder(new EmptyBorder(15,15,15,15));

        JPanel P1=new JPanel(new GridLayout(4,2,8,4));
        P1.setBorder(BorderFactory.createTitledBorder("Courier Information"));
        P1.add(new JLabel("Courier ID:"));P1.add(new JLabel(C.Getcourierid()));
        P1.add(new JLabel("Courier Type:"));P1.add(new JLabel(C.Getcouriertype()+" ("+C.Getprioritylevel()+")"));
        P1.add(new JLabel("Current Status:"));P1.add(new JLabel(C.Getstatus().Getdisplayname()));
        P1.add(new JLabel("Booking Date:"));P1.add(new JLabel(C.Getbookingdate()));
        Panel.add(P1);
        Panel.add(Box.createVerticalStrut(8));

        JPanel P2=new JPanel(new GridLayout(4,2,8,4));
        P2.setBorder(BorderFactory.createTitledBorder("Customer & Destination"));
        P2.add(new JLabel("Sender:"));P2.add(new JLabel(C.Getsender().Getname()+" ("+C.Getsender().Getphone()+")"));
        P2.add(new JLabel("Receiver:"));P2.add(new JLabel(C.Getreceiver().Getname()+" ("+C.Getreceiver().Getphone()+")"));
        P2.add(new JLabel("City:"));P2.add(new JLabel(C.Getdestinationcity()));
        P2.add(new JLabel("Address:"));P2.add(new JLabel(C.Getdestinationaddress()));
        Panel.add(P2);
        Panel.add(Box.createVerticalStrut(8));

        JPanel P3=new JPanel(new GridLayout(4,2,8,4));
        P3.setBorder(BorderFactory.createTitledBorder("Parcel & Storage"));
        P3.add(new JLabel("Parcel Type:"));P3.add(new JLabel(C.Getparceltype()));
        P3.add(new JLabel("Weight:"));P3.add(new JLabel(C.Getweight()+" kg"));
        P3.add(new JLabel("Delivery Fee:"));P3.add(new JLabel("$"+String.format("%.2f",C.Getdeliverycharge())));
        P3.add(new JLabel("Basement Slot:"));P3.add(new JLabel(C.Getstoragelocation()!=null?C.Getstoragelocation().Getformattedcode():"Unassigned"));
        Panel.add(P3);

        if(C.Getdispatchrecord()!=null){
            Panel.add(Box.createVerticalStrut(8));
            JPanel P4=new JPanel(new GridLayout(2,2,8,4));
            P4.setBorder(BorderFactory.createTitledBorder("Dispatch Record"));
            P4.add(new JLabel("Vehicle / Driver:"));P4.add(new JLabel(C.Getdispatchrecord().Getvehiclenumber()+" / "+C.Getdispatchrecord().Getdrivername()));
            P4.add(new JLabel("Date:"));P4.add(new JLabel(C.Getdispatchrecord().Getdispatchdate()));
            Panel.add(P4);
        }
        add(new JScrollPane(Panel),BorderLayout.CENTER);

        Closebtn=new JButton("Close");
        Closebtn.addActionListener(this);
        JPanel Bp=new JPanel(new FlowLayout(FlowLayout.RIGHT));
        Bp.add(Closebtn);
        add(Bp,BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent E){
        if(E.getSource()==Closebtn){
            dispose();
        }
    }
}
