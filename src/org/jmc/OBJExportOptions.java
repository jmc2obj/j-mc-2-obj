package org.jmc;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

@SuppressWarnings("serial")
public class OBJExportOptions extends JPanel
{
	private Preferences prefs;
	private JTextField tfScale;
	private JRadioButton rbNoOffset,rbCenterOffset,rbCustomOffset;
	private JTextField tfXOffset,tfZOffset;
	private JCheckBox cbObjPerMat;
	private JCheckBox cbRemoveDuplicates;
	private JRadioButton rbOBJAlways, rbOBJNever, rbOBJAsk;
	private JRadioButton rbMTLAlways, rbMTLNever, rbMTLAsk;

	public OBJExportOptions() {
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		prefs=MainWindow.settings.getPreferences();

		JPanel pScale=new JPanel();		
		pScale.setLayout(new BoxLayout(pScale,BoxLayout.LINE_AXIS));
		pScale.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		JLabel lScale=new JLabel("Map Scale: ");
		tfScale=new JTextField(""+prefs.getFloat("DEFAULT_SCALE", 1.0f));
		pScale.add(lScale);
		pScale.add(tfScale);

		JPanel pOffset=new JPanel();
		pOffset.setLayout(new BoxLayout(pOffset,BoxLayout.LINE_AXIS));
		pOffset.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		JLabel lOffset=new JLabel("Offset: ");
		rbNoOffset=new JRadioButton("None");
		rbCenterOffset=new JRadioButton("Center");
		rbCustomOffset=new JRadioButton("Custom");
		tfXOffset=new JTextField("0");
		tfZOffset=new JTextField("0");
		pOffset.add(lOffset);
		pOffset.add(rbNoOffset);
		pOffset.add(rbCenterOffset);
		pOffset.add(rbCustomOffset);
		pOffset.add(tfXOffset);
		pOffset.add(tfZOffset);

		ButtonGroup gOffset=new ButtonGroup();
		gOffset.add(rbNoOffset);
		gOffset.add(rbCenterOffset);
		gOffset.add(rbCustomOffset);
		rbNoOffset.setActionCommand("none");
		rbCenterOffset.setActionCommand("center");
		rbCustomOffset.setActionCommand("custom");

		JPanel pObjPerMat=new JPanel();
		pObjPerMat.setLayout(new BoxLayout(pObjPerMat, BoxLayout.LINE_AXIS));
		pObjPerMat.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		cbObjPerMat=new JCheckBox("Create a separate object for each material");
		pObjPerMat.add(cbObjPerMat);
		
		JPanel pRemoveDuplicates=new JPanel();
		pRemoveDuplicates.setLayout(new BoxLayout(pRemoveDuplicates, BoxLayout.LINE_AXIS));
		pRemoveDuplicates.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		cbRemoveDuplicates=new JCheckBox("RemoveDuplicates OBJ file");
		pRemoveDuplicates.add(cbRemoveDuplicates);
		
		JPanel pOBJOver = new JPanel();
		pOBJOver.setLayout(new BoxLayout(pOBJOver, BoxLayout.LINE_AXIS));
		pOBJOver.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		JLabel lOBJOver=new JLabel("Overwrite OBJ: ");
		rbOBJAsk=new JRadioButton("Ask");
		rbOBJAlways=new JRadioButton("Always");
		rbOBJNever=new JRadioButton("Never");		
		pOBJOver.add(lOBJOver);
		pOBJOver.add(rbOBJAsk);
		pOBJOver.add(rbOBJAlways);
		pOBJOver.add(rbOBJNever);
		
		ButtonGroup gOBJOver=new ButtonGroup();
		gOBJOver.add(rbOBJAsk);
		gOBJOver.add(rbOBJAlways);
		gOBJOver.add(rbOBJNever);
		rbOBJAsk.setActionCommand("ask");
		rbOBJAlways.setActionCommand("always");
		rbOBJNever.setActionCommand("never");
		
		JPanel pMTLOver = new JPanel();
		pMTLOver.setLayout(new BoxLayout(pMTLOver, BoxLayout.LINE_AXIS));
		pMTLOver.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		JLabel lMTLOver=new JLabel("Overwrite MTL: ");
		rbMTLAsk=new JRadioButton("Ask");
		rbMTLAlways=new JRadioButton("Always");
		rbMTLNever=new JRadioButton("Never");		
		pMTLOver.add(lMTLOver);
		pMTLOver.add(rbMTLAsk);
		pMTLOver.add(rbMTLAlways);
		pMTLOver.add(rbMTLNever);
		
		JPanel pNames=new JPanel();
		pNames.setLayout(new BoxLayout(pNames, BoxLayout.LINE_AXIS));
		pNames.setMaximumSize(new Dimension(Short.MAX_VALUE,50));
		JButton bNames=new JButton("Rename files...");
		pNames.add(bNames);
		
		bNames.addActionListener(new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {	
				MainWindow.file_names.display();
			}
		});
		
		ButtonGroup gMTLOver=new ButtonGroup();
		gMTLOver.add(rbMTLAsk);
		gMTLOver.add(rbMTLAlways);
		gMTLOver.add(rbMTLNever);
		rbMTLAsk.setActionCommand("ask");
		rbMTLAlways.setActionCommand("always");
		rbMTLNever.setActionCommand("never");

		AbstractAction rbOffsetAction=new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent ev) {
				if(ev.getActionCommand().equals("custom"))
				{
					tfXOffset.setEnabled(true);
					tfZOffset.setEnabled(true);
				}
				else
				{
					tfXOffset.setEnabled(false);
					tfZOffset.setEnabled(false);
				}

				int x=Integer.parseInt(tfXOffset.getText());
				int z=Integer.parseInt(tfZOffset.getText());

				prefs.putInt("OFFSET_X", x);
				prefs.putInt("OFFSET_Z", z);

				if(ev.getActionCommand().equals("none"))
					prefs.putInt("OFFSET_TYPE", 0);
				else if(ev.getActionCommand().equals("center"))
					prefs.putInt("OFFSET_TYPE", 1);
				else if(ev.getActionCommand().equals("custom"))
					prefs.putInt("OFFSET_TYPE", 2);
			}
		};

		rbNoOffset.addActionListener(rbOffsetAction);
		rbCenterOffset.addActionListener(rbOffsetAction);
		rbCustomOffset.addActionListener(rbOffsetAction);
		
		AbstractAction SaveAction=new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveSettings();
			}	
		};
		
		rbOBJAsk.addActionListener(SaveAction);
		rbOBJAlways.addActionListener(SaveAction);
		rbOBJNever.addActionListener(SaveAction);
		rbMTLAsk.addActionListener(SaveAction);
		rbMTLAlways.addActionListener(SaveAction);
		rbMTLNever.addActionListener(SaveAction);
		cbObjPerMat.addActionListener(SaveAction);
		cbRemoveDuplicates.addActionListener(SaveAction);

		switch(prefs.getInt("OFFSET_TYPE", 0))
		{
		case 0:
			rbNoOffset.setSelected(true);
			tfXOffset.setEnabled(false);
			tfZOffset.setEnabled(false);
			break;
		case 1:
			rbCenterOffset.setSelected(true);
			tfXOffset.setEnabled(false);
			tfZOffset.setEnabled(false);
			break;
		case 2:
			rbCustomOffset.setSelected(true);
			tfXOffset.setEnabled(true);
			tfZOffset.setEnabled(true);
			break;
		}
		tfXOffset.setText(""+prefs.getInt("OFFSET_X", 0));
		tfZOffset.setText(""+prefs.getInt("OFFSET_Z", 0));
		
		switch(prefs.getInt("OBJ_OVERWRITE", 0))
		{
		case 0:
			rbOBJAsk.setSelected(true);
			break;
		case 1:
			rbOBJAlways.setSelected(true);
			break;
		case 2:
			rbOBJNever.setSelected(true);
			break;
		}
		
		switch(prefs.getInt("MTL_OVERWRITE", 0))
		{
		case 0:
			rbMTLAsk.setSelected(true);
			break;
		case 1:
			rbMTLAlways.setSelected(true);
			break;
		case 2:
			rbMTLNever.setSelected(true);
			break;
		}
		
		cbObjPerMat.setSelected(prefs.getBoolean("OBJ_PER_MTL", false));
		cbRemoveDuplicates.setSelected(prefs.getBoolean("REMOVE_DUPLICATES", false));
		
		add(pScale);
		add(pOffset);
		add(pObjPerMat);
		add(pRemoveDuplicates);
		add(pOBJOver);
		add(pMTLOver);
		add(pNames);
	}

	enum OffsetType{ NO_OFFSET, CENTER_OFFSET, CUSTOM_OFFSET };

	public OffsetType getOffsetType()
	{
		if(rbNoOffset.isSelected())
			return OffsetType.NO_OFFSET;

		if(rbCenterOffset.isSelected())
			return OffsetType.CENTER_OFFSET;

		if(rbCustomOffset.isSelected())
			return OffsetType.CUSTOM_OFFSET;

		return OffsetType.NO_OFFSET;
	}
	
	enum OverwriteAction { ASK, ALWAYS, NEVER };
	
	public OverwriteAction getOBJOverwriteAction()
	{
		if(rbOBJAsk.isSelected())
			return OverwriteAction.ASK;
		
		if(rbOBJAlways.isSelected())
			return OverwriteAction.ALWAYS;
		
		if(rbOBJNever.isSelected())
			return OverwriteAction.NEVER;
		
		return OverwriteAction.ASK;
	}
	
	public OverwriteAction getMTLOverwriteAction()
	{
		if(rbMTLAsk.isSelected())
			return OverwriteAction.ASK;
		
		if(rbMTLAlways.isSelected())
			return OverwriteAction.ALWAYS;
		
		if(rbMTLNever.isSelected())
			return OverwriteAction.NEVER;
		
		return OverwriteAction.ASK;
	}

	public void saveSettings()
	{
		prefs.putFloat("DEFAULT_SCALE", getScale());
		
		switch(getOBJOverwriteAction())
		{
		case ASK:
			prefs.putInt("OBJ_OVERWRITE", 0);
			break;
		case ALWAYS:
			prefs.putInt("OBJ_OVERWRITE", 1);
			break;
		case NEVER:
			prefs.putInt("OBJ_OVERWRITE", 2);
			break;
		}
		
		switch(getMTLOverwriteAction())
		{
		case ASK:
			prefs.putInt("MTL_OVERWRITE", 0);
			break;
		case ALWAYS:
			prefs.putInt("MTL_OVERWRITE", 1);
			break;
		case NEVER:
			prefs.putInt("MTL_OVERWRITE", 2);
			break;
		}
		
		prefs.putBoolean("OBJ_PER_MTL", cbObjPerMat.isSelected());
		prefs.putBoolean("REMOVE_DUPLICATES", cbRemoveDuplicates.isSelected());
	}

	public Point getCustomOffset()
	{
		int x=Integer.parseInt(tfXOffset.getText());
		int z=Integer.parseInt(tfZOffset.getText());
		return new Point(x,z);
	}

	public float getScale()
	{
		float ret=1;

		try
		{
			ret=Float.parseFloat(tfScale.getText());
		}catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(OBJExportOptions.this, "Cannot parse the scale value! Assuming 1!");
			return 1.0f;
		}

		return ret;
	}

	public boolean getObjPerMat()
	{
		return cbObjPerMat.isSelected();
	}
	
	public boolean getRemoveDuplicates()
	{
		return cbRemoveDuplicates.isSelected();
	}
}
