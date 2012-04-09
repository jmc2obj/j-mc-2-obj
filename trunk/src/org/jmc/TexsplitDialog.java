package org.jmc;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class TexsplitDialog extends JFrame {

	JTextField tfDest;

	public TexsplitDialog(String dest) {
		super("Texture export");

		setSize(400,100);

		Container cp=getContentPane();
		cp.setLayout(new BoxLayout(cp, BoxLayout.PAGE_AXIS));

		JPanel pDest=new JPanel();
		pDest.setLayout(new BoxLayout(pDest, BoxLayout.LINE_AXIS));
		pDest.setMaximumSize(new Dimension(Short.MAX_VALUE,20));
		JLabel lDest=new JLabel("Destination:");
		tfDest=new JTextField(dest);
		pDest.add(lDest);
		pDest.add(tfDest);

		JPanel pQuest=new JPanel();
		pQuest.setLayout(new BoxLayout(pQuest, BoxLayout.LINE_AXIS));
		JLabel lQuest=new JLabel("Export textures from:");
		pQuest.add(lQuest);

		JPanel pButtons=new JPanel();
		pButtons.setLayout(new BoxLayout(pButtons, BoxLayout.LINE_AXIS));
		JButton bMinecraft=new JButton("Minecraft");
		JButton bCustom=new JButton("Custom texturepack");
		pButtons.add(bMinecraft);
		pButtons.add(bCustom);

		bMinecraft.addActionListener(new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				File destination=new File(tfDest.getText());
				
				try {
					Texsplit.splitTextures(destination,null);
				} catch (Exception e) {
					e.printStackTrace();
					Utility.logError("Error saving textures:", e);
				}			

				dispose();
				
			}
		});
		
		bCustom.addActionListener(new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				File destination=new File(tfDest.getText());
				JFileChooser jfc=new JFileChooser();
				jfc.setDialogType(JFileChooser.OPEN_DIALOG);
				int retval=jfc.showDialog(TexsplitDialog.this,"Select texture pack file");
				if(retval!=JFileChooser.APPROVE_OPTION) return;
				File texturepack=jfc.getSelectedFile();
				try {
					Texsplit.splitTextures(destination,texturepack);
				} catch (Exception e) {
					e.printStackTrace();
					Utility.logError("Error saving textures:", e);
				}			

				dispose();
			}
		});

		cp.add(pDest);
		cp.add(pQuest);
		cp.add(pButtons);
		
		setVisible(true);
	}

}
