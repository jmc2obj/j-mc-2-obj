package org.jmc.gui;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jmc.CheckUpdate;
import org.jmc.Version;

@SuppressWarnings("serial")
public class UpdateWindow extends JFrame{

	private JTextField tfCurrent,tfNew;
	private JLabel lStatus;

	public UpdateWindow() {

		super("Program Update");

		setSize(400,300);

		JPanel cp=new JPanel();
		cp.setLayout(new BoxLayout(cp,BoxLayout.PAGE_AXIS));

		JPanel pCheck=new JPanel();
		pCheck.setLayout(new BoxLayout(pCheck, BoxLayout.LINE_AXIS));
		JButton bCheck=new JButton("Check");
		bCheck.setMaximumSize(new Dimension(Short.MAX_VALUE,20));
		pCheck.add(bCheck);

		JPanel pCurrent=new JPanel();
		pCurrent.setLayout(new BoxLayout(pCurrent, BoxLayout.LINE_AXIS));
		JLabel lCurrent=new JLabel("Current version: ");
		tfCurrent=new JTextField("CLICK CHECK");
		pCurrent.add(lCurrent);
		pCurrent.add(tfCurrent);

		JPanel pNew=new JPanel();
		pNew.setLayout(new BoxLayout(pNew, BoxLayout.LINE_AXIS));
		JLabel lNew=new JLabel("Newest version: ");
		tfNew=new JTextField("CLICK CHECK");
		pNew.add(lNew);
		pNew.add(tfNew);

		JPanel pStatus=new JPanel();
		pStatus.setLayout(new BoxLayout(pStatus, BoxLayout.LINE_AXIS));
		lStatus=new JLabel();
		pStatus.add(lStatus);

		JPanel pUpdate=new JPanel();
		pUpdate.setLayout(new BoxLayout(pUpdate, BoxLayout.LINE_AXIS));
		JButton bUpdate=new JButton("Update");
		bUpdate.setMaximumSize(new Dimension(Short.MAX_VALUE,20));
		pUpdate.add(bUpdate);

		add(cp);
		cp.add(pCheck);
		cp.add(pCurrent);
		cp.add(pNew);
		cp.add(pStatus);
		cp.add(pUpdate);

		pack();


		bCheck.addActionListener(new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent e) {

				DateFormat df=DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);				
				Date curr=Version.DATE();				
				Date newd=CheckUpdate.getDate();

				tfCurrent.setText(df.format(curr));
				tfNew.setText(df.format(newd));

				if(newd.after(curr))
					lStatus.setText("New version is available!");
				else
					lStatus.setText("You have the newest version.");

				pack();
			}
		});

		bUpdate.addActionListener(new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent e) {

				//TODO: make a separate program that will download and run the new version automatically
				
				String url=CheckUpdate.getUrl();
				try{
					if(Desktop.isDesktopSupported())
					{
						Desktop desktop = java.awt.Desktop.getDesktop();

						if(desktop.isSupported(Desktop.Action.BROWSE))
						{
							desktop.browse( new URI(url) );
							return;
						}
					}
				}catch (Exception ex) {}

				JOptionPane.showMessageDialog(UpdateWindow.this, "Cannot open browser!\nVisit http://www.jmc2obj.net/ to udpate manually.");
			}
		});
	}

}
