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
import org.jmc.util.Messages;

@SuppressWarnings("serial")
public class TexsplitDialog extends JFrame
{
	private JProgressBar progress;
	
	private File destination;
	private boolean alphas;

	public TexsplitDialog(String dest)
	{
		super(Messages.getString("TexsplitDialog.WIN_TITLE")); 

		setSize(400,100);

		JPanel mp=new JPanel();
		add(mp);
		mp.setLayout(new BoxLayout(mp, BoxLayout.PAGE_AXIS));

		JPanel pDest=new JPanel();
		pDest.setLayout(new BoxLayout(pDest, BoxLayout.LINE_AXIS));
		pDest.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		JLabel lDest=new JLabel(Messages.getString("TexsplitDialog.DESTINATION")); 
		final JTextField tfDest=new JTextField(dest);
		pDest.add(lDest);
		pDest.add(tfDest);
		
		JPanel pScale = new JPanel();
		pScale.setLayout(new BoxLayout(pScale, BoxLayout.LINE_AXIS));
		pScale.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		JLabel lScale=new JLabel(Messages.getString("TexsplitDialog.PRESCALE")); 
		final JComboBox<String> cScale=new JComboBox<String>(new String[] {"1x","2x","4x","8x","16x"});  //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		pScale.add(lScale);
		pScale.add(cScale);

		JPanel pAlpha = new JPanel();
		pAlpha.setLayout(new BoxLayout(pAlpha, BoxLayout.LINE_AXIS));
		pAlpha.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		final JCheckBox cbAlpha=new JCheckBox(Messages.getString("TexsplitDialog.EXP_ALPHA"), true); 
		pAlpha.add(cbAlpha);

		JPanel pQuest=new JPanel();
		pQuest.setLayout(new BoxLayout(pQuest, BoxLayout.LINE_AXIS));
		JLabel lQuest=new JLabel(Messages.getString("TexsplitDialog.TEX_LOC")); 
		pQuest.add(lQuest);

		JPanel pButtons=new JPanel();
		pButtons.setLayout(new BoxLayout(pButtons, BoxLayout.LINE_AXIS));
		JButton bMinecraft=new JButton(Messages.getString("TexsplitDialog.MINECRAFT")); 
		JButton bCustom=new JButton(Messages.getString("TexsplitDialog.CUSTOM")); 
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
				Options.textureScale=Double.parseDouble(cScale.getSelectedItem().toString().replace("x",""));  //$NON-NLS-2$
				
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
				int retval=jfc.showDialog(TexsplitDialog.this,Messages.getString("TexsplitDialog.SEL_PACK")); 
				if(retval!=JFileChooser.APPROVE_OPTION) return;
				Options.texturePack=jfc.getSelectedFile();
				Options.textureScale=Double.parseDouble(cScale.getSelectedItem().toString().replace("x",""));  //$NON-NLS-2$
				
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
					Log.error(Messages.getString("TexsplitDialog.ERR_EXP"), e); 
				}			
				TexsplitDialog.this.dispose();
			}
		});
		t.start();								
	}

}
