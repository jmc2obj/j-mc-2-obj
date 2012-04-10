package org.jmc;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class TexsplitDialog extends JFrame implements ProgessDisplay {

	JTextField tfDest;
	JCheckBox cbAlpha;
	JProgressBar progress;
	
	File destination,texturepack;
	boolean alphas;

	public TexsplitDialog(String dest) {
		super("Texture export");

		setSize(400,100);

		JPanel mp=new JPanel();
		add(mp);
		mp.setLayout(new BoxLayout(mp, BoxLayout.PAGE_AXIS));

		JPanel pDest=new JPanel();
		pDest.setLayout(new BoxLayout(pDest, BoxLayout.LINE_AXIS));
		pDest.setMaximumSize(new Dimension(Short.MAX_VALUE,20));
		JLabel lDest=new JLabel("Destination:");
		tfDest=new JTextField(dest);
		pDest.add(lDest);
		pDest.add(tfDest);
		
		JPanel pAlpha = new JPanel();
		pAlpha.setLayout(new BoxLayout(pAlpha, BoxLayout.LINE_AXIS));
		cbAlpha=new JCheckBox("Export alpha channel in separate files", true);
		pAlpha.add(cbAlpha);

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
		
		progress=new JProgressBar();
		progress.setStringPainted(true);		

		bMinecraft.addActionListener(new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				texturepack=null;
				destination=new File(tfDest.getText());
				alphas=cbAlpha.isSelected();
				
				new TexsplitThread(TexsplitDialog.this).start();								
			}
		});
		
		bCustom.addActionListener(new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				alphas=cbAlpha.isSelected();
				destination=new File(tfDest.getText());
				JFileChooser jfc=new JFileChooser();
				jfc.setDialogType(JFileChooser.OPEN_DIALOG);
				int retval=jfc.showDialog(TexsplitDialog.this,"Select texture pack file");
				if(retval!=JFileChooser.APPROVE_OPTION) return;
				texturepack=jfc.getSelectedFile();
				
				new TexsplitThread(TexsplitDialog.this).start();				
			}
		});

		mp.add(pDest);
		mp.add(pAlpha);
		mp.add(Box.createVerticalStrut(10));
		mp.add(pQuest);
		mp.add(pButtons);
		mp.add(Box.createVerticalStrut(10));
		mp.add(progress);
		
		mp.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		pack();
		setVisible(true);
	}
	
	public File getDestination()
	{
		return destination;
	}
	
	public File getTexturepack()
	{
		return texturepack;
	}
	
	public boolean getAlphas()
	{
		return alphas;
	}

	@Override
	public void setProgressMax(int val) {
		progress.setMaximum(val);
		
	}

	@Override
	public void setProgress(int val) {
		progress.setValue(val);
		
	}

	@Override
	public void setProgressPercent(float val) {
		progress.setMaximum(100);
		progress.setValue((int)val);
		
	}

}

class TexsplitThread extends Thread
{
	TexsplitDialog dialog;
	
	public TexsplitThread(TexsplitDialog dialog) 
	{
		this.dialog=dialog;
	}
	
	@Override
	public void run() {		
		
		try {
			Texsplit.splitTextures(dialog.getDestination(),dialog.getTexturepack(),dialog.getAlphas(),dialog);
		} catch (Exception e) {
			e.printStackTrace();
			Utility.logError("Error saving textures:", e);
		}			
		
		dialog.dispose();		
	}	
}
