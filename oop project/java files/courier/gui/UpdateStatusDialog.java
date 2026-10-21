package courier.gui;

import courier.model.*;
import courier.database.DBManager;
import courier.exceptions.*;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UpdateStatusDialog extends JDialog implements ActionListener{
    private final Courier Courierobj;
    private final DBManager Db;
    private final MainDashboard Dashboard;
    private final JComboBox<CourierStatus> Statuscombo;
    private final JButton Okbtn;
    private final JButton Cancelbtn;

    public UpdateStatusDialog(JFrame Parent,Courier Courierobj,DBManager Db,MainDashboard Dashboard){
        super(Parent,"Update Status - "+Courierobj.Getcourierid(),true);
        this.Courierobj=Courierobj;
        this.Db=Db;
        this.Dashboard=Dashboard;
        setSize(400,200);
        setLocationRelativeTo(Parent);
        setLayout(new BorderLayout(10,10));

        JPanel Form=new JPanel(new GridLayout(3,2,8,8));
        Form.setBorder(new EmptyBorder(15,15,10,15));
        Form.add(new JLabel("Courier ID:"));
        Form.add(new JLabel(Courierobj.Getcourierid()));
        Form.add(new JLabel("Current Status:"));
        Form.add(new JLabel(Courierobj.Getstatus().Getdisplayname()));
        Form.add(new JLabel("New Status:"));

        Statuscombo=new JComboBox<>(CourierStatus.values());
        Statuscombo.setSelectedItem(Courierobj.Getstatus());
        Form.add(Statuscombo);
        add(Form,BorderLayout.CENTER);

        JPanel Bp=new JPanel(new FlowLayout(FlowLayout.RIGHT,10,10));
        Cancelbtn=new JButton("Cancel");
        Cancelbtn.addActionListener(this);
        Okbtn=new JButton("Update Status");
        Okbtn.addActionListener(this);

        Bp.add(Cancelbtn);
        Bp.add(Okbtn);
        add(Bp,BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent E){
        if(E.getSource()==Cancelbtn){
            dispose();
        }else if(E.getSource()==Okbtn){
            CourierStatus Selected=(CourierStatus)Statuscombo.getSelectedItem();
            if(Selected!=null&&Selected!=Courierobj.Getstatus()){
                try{
                    Db.Updatecourierstatus(Courierobj.Getcourierid(),Selected);
                    JOptionPane.showMessageDialog(this,"Status updated to "+Selected.Getdisplayname());
                    dispose();
                    if(Dashboard!=null){
                        Dashboard.Refreshalldata();
                    }
                }catch(InvalidStatusTransitionException Ex){
                    JOptionPane.showMessageDialog(this,Ex.getMessage(),"Invalid Status Jump",JOptionPane.WARNING_MESSAGE);
                }catch(Exception Ex){
                    JOptionPane.showMessageDialog(this,"Error: "+Ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                }
            }else{
                dispose();
            }
        }
    }
}
