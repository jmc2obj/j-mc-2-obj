package org.jmc.gui;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jmc.CheckUpdate;
import org.jmc.Version;
import org.jmc.util.Filesystem;
import org.jmc.util.Log;
import org.jmc.util.Messages;

@SuppressWarnings("serial")
public class UpdateWindow extends JFrame{

	private JTextField tfCurrent,tfNew;
	private JLabel lStatus;

	public UpdateWindow() {

		super(Messages.getString("UpdateWindow.WIN_TITLE")); 

		setSize(250,170);

		JPanel cp=new JPanel();
		cp.setLayout(new BoxLayout(cp,BoxLayout.PAGE_AXIS));

		JPanel pCheck=new JPanel();
		pCheck.setLayout(new BoxLayout(pCheck, BoxLayout.LINE_AXIS));
		JButton bCheck=new JButton(Messages.getString("UpdateWindow.CHECK")); 
		bCheck.setMaximumSize(new Dimension(Short.MAX_VALUE,20));
		pCheck.add(bCheck);

		JPanel pCurrent=new JPanel();
		pCurrent.setLayout(new BoxLayout(pCurrent, BoxLayout.LINE_AXIS));
		JLabel lCurrent=new JLabel(Messages.getString("UpdateWindow.CUR_VER")); 
		tfCurrent=new JTextField(Messages.getString("UpdateWindow.CLICK")); 
		pCurrent.add(lCurrent);
		pCurrent.add(tfCurrent);

		JPanel pNew=new JPanel();
		pNew.setLayout(new BoxLayout(pNew, BoxLayout.LINE_AXIS));
		JLabel lNew=new JLabel(Messages.getString("UpdateWindow.NEW_VER")); 
		tfNew=new JTextField(Messages.getString("UpdateWindow.CLICK")); 
		pNew.add(lNew);
		pNew.add(tfNew);

		JPanel pStatus=new JPanel();
		pStatus.setLayout(new BoxLayout(pStatus, BoxLayout.LINE_AXIS));
		lStatus=new JLabel(" ");
		Font font=lStatus.getFont();
		lStatus.setFont(new Font(font.getFamily(),0,16));
		pStatus.add(lStatus);

		JPanel pUpdate=new JPanel();
		pUpdate.setLayout(new BoxLayout(pUpdate, BoxLayout.LINE_AXIS));
		JButton bUpdate=new JButton(Messages.getString("UpdateWindow.UPDATE")); 
		bUpdate.setMaximumSize(new Dimension(Short.MAX_VALUE,20));
		pUpdate.add(bUpdate);

		add(cp);
		cp.add(pCheck);
		cp.add(pCurrent);
		cp.add(pNew);
		cp.add(pStatus);		
		cp.add(pUpdate);
		cp.add(Box.createVerticalGlue());

		cp.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

	


		bCheck.addActionListener(new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				tfCurrent.setText(Messages.getString("UpdateWindow.CHECKING")); 
				tfNew.setText(Messages.getString("UpdateWindow.CHECKING")); 
				
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						DateFormat df=DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);				
						Date curr=Version.DATE();				
						Date newd=CheckUpdate.getDate();

						tfCurrent.setText(df.format(curr));
						tfNew.setText(df.format(newd));

						if(newd.after(curr))
							lStatus.setText(Messages.getString("UpdateWindow.NEW_AVAIL")); 
						else
							lStatus.setText(Messages.getString("UpdateWindow.NO_NEW")); 

						
						
					}
				}).start();
			}
		});

		bUpdate.addActionListener(new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent e) {

				File folder=Filesystem.getDatafilesDir();
				File updater=new File(folder,"conf/Updater.jar"); 
				if(updater.exists())
				{
					File program=Filesystem.getProgramExecutable();
					
					
					if(program==null)
					{
						Log.info(Messages.getString("UpdateWindow.UPD_ERR")); 
					}
					else
					{
						String url=CheckUpdate.getUrl();
						try {
							String cmd="java -jar "+updater.getAbsolutePath()+ 
									" -d "+url+ 
									" -f "+folder.getAbsolutePath()+ 
									" -r "+program.getAbsolutePath()+ 
									" -g"; 
							
							Log.info("running: "+cmd); 
							
							Process p = Runtime.getRuntime().exec(cmd);
							
							int ret=0;
							try{
								
								Thread.sleep(1000);							
								ret=p.exitValue();
								
							}catch (Exception ex) {
								System.exit(0);
							}
							
							Log.info("Updater exited with value: "+ret); 
							
						} catch (IOException e1) {
							Log.info(Messages.getString("UpdateWindow.UPD_ERR_DESC")+e1); 
						}
					}
				}

				
				try{
					if(Desktop.isDesktopSupported())
					{
						Desktop desktop = java.awt.Desktop.getDesktop();

						if(desktop.isSupported(Desktop.Action.BROWSE))
						{
							desktop.browse( new URI("http://www.jmc2obj.net/downloads/") ); 
							return;
						}
					}
				}catch (Exception ex) {}

				JOptionPane.showMessageDialog(UpdateWindow.this, Messages.getString("UpdateWindow.BROWSER_ERR")); 
			}

		});
	}

}
