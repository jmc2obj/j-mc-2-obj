package org.jmc.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import org.jmc.Options;
import org.jmc.ProgressCallback;
import org.jmc.Texsplit;
import org.jmc.util.Log;

@SuppressWarnings("serial")
public class TexsplitDialog extends JFrame
{
	private JProgressBar progress;
	
	private File destination;
	private boolean alphas;

	public TexsplitDialog(String dest)
	{
		super("Texture export");

		setSize(400,100);

		JPanel mp=new JPanel();
		add(mp);
		mp.setLayout(new BoxLayout(mp, BoxLayout.PAGE_AXIS));

		JPanel pDest=new JPanel();
		pDest.setLayout(new BoxLayout(pDest, BoxLayout.LINE_AXIS));
		pDest.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		JLabel lDest=new JLabel("Destination:");
		final JTextField tfDest=new JTextField(dest);
		pDest.add(lDest);
		pDest.add(tfDest);
		
		JPanel pScale = new JPanel();
		pScale.setLayout(new BoxLayout(pScale, BoxLayout.LINE_AXIS));
		pScale.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		JLabel lScale=new JLabel("Pre-scale textures:");
		final JComboBox<String> cScale=new JComboBox<String>(new String[] {"1x","2x","4x","8x","16x"});
		pScale.add(lScale);
		pScale.add(cScale);

		JPanel pAlpha = new JPanel();
		pAlpha.setLayout(new BoxLayout(pAlpha, BoxLayout.LINE_AXIS));
		pAlpha.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		final JCheckBox cbAlpha=new JCheckBox("Export alpha channel in separate files", true);
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
		progress.setMaximum(100);
		progress.setStringPainted(true);		

		bMinecraft.addActionListener(new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				destination=new File(tfDest.getText());
				alphas=cbAlpha.isSelected();
				Options.texturePack=null;
				Options.textureScale=Double.parseDouble(cScale.getSelectedItem().toString().replace("x",""));
				
				doExport();
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
				Options.texturePack=jfc.getSelectedFile();
				Options.textureScale=Double.parseDouble(cScale.getSelectedItem().toString().replace("x",""));
				
				doExport();
			}
		});

		mp.add(pDest);
		mp.add(pScale);
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
	
	private void doExport()
	{
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Texsplit.splitTextures(
							destination, Options.texturePack, Options.textureScale, alphas,
							new ProgressCallback() {
								@Override
								public void setProgress(float value) {
									progress.setValue((int)(100 * value));
								}
							});
				}
				catch (Exception e) {
					Log.error("Error saving textures:", e);
				}			
				TexsplitDialog.this.dispose();
			}
		});
		t.start();								
	}

}
