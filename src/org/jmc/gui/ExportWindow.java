package org.jmc.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jmc.CloudsExporter;
import org.jmc.ObjExporter;
import org.jmc.Options;
import org.jmc.Options.OffsetType;
import org.jmc.ProgressCallback;
import org.jmc.StopCallback;
import org.jmc.util.Log;
import org.jmc.util.Messages;

@SuppressWarnings("serial")
public class ExportWindow extends JmcFrame implements ProgressCallback {

	private Preferences prefs;

	private boolean stop;

	private JPanel contentPane;

	private JRadioButton rdbtnNone;
	private JRadioButton rdbtnCenter;
	private JRadioButton rdbtnCustom;
	private JTextField txtX;
	private JTextField txtZ;

	private JCheckBox chckbxRenderWorldSides;
	private JCheckBox chckbxRenderBiomes;
	private JCheckBox chckbxRenderEntities;
	private JCheckBox chckbxRenderUnknownBlocks;
	private JCheckBox chckbxSeparateMat;
	private JCheckBox chckbxSeparateMatOccl;
	private JCheckBox chckbxSeparateChunk;
	private JCheckBox chckbxDoubleSidedFaces;
	private JCheckBox chckbxSeparateBlock;
	private JCheckBox chckbxOptimiseGeo;
	private JCheckBox chckbxConvertOreTo;
	private JCheckBox chckbxMergeVerticies;

	private JComboBox<String> cboxTexScale;
	private JCheckBox chckbxSeparateAlphaTexture;
	private JCheckBox chckbxCombineAllTextures;
	private JCheckBox chckbxSingleMat;

	private JButton btnStartExport;
	private JButton btnForceStop;
	private JButton btnFromResourcePack;
	private JButton btnMinecraftTextures;
	private JButton btnBlocksToExport;

	private JProgressBar progressBar;
	private JTextField textFieldMapScale;
	private JPanel holderXOffset;
	private JPanel holderYOffset;
	private JPanel holderOffsetFields;
	private JPanel holderPreScale;
	private JPanel holderCloudExports;
	private JPanel holderTexExport;
	private JPanel holderMapScale;
	private JPanel holderSepBlock;
	private JPanel holderExportPanel;
	private JPanel holderLeft;
	private JPanel holderTop;
	private JPanel holderOffset;
	private JCheckBox chckbxUseLastSaveLoc;
	private JPanel holderExportBtns;
	private JCheckBox chckbxExportSeparateLight;
	
	private JSpinner spinnerThreads;
	private JPanel holderThreads;
	private JCheckBox chckbxExportNormalMaps;
	private JCheckBox chckbxExportSpecularMaps;
	private JPanel holderExtraMapsChks;
	private JPanel holderExtraMaps;
	private JLabel lblExtraMaps;
	private JLabel lblExtraMapsHelp;
	private JPanel holderExtraMapsText;
	private JPanel holderSingleTexOpts;
	private JCheckBox chckbxExportTextures;


	/**
	 * Create the frame.
	 */
	public ExportWindow() {

		super("Export...");

		setBounds(100, 100, 500, 475);

		contentPane = new JPanel();
		prefs = MainWindow.settings.getPreferences();
		ToolTipManager.sharedInstance().setInitialDelay(0);

		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

		holderTop = new JPanel();
		contentPane.add(holderTop);
		holderTop.setLayout(new BoxLayout(holderTop, BoxLayout.X_AXIS));

		holderLeft = new JPanel();
		holderTop.add(holderLeft);
		holderLeft.setLayout(new BoxLayout(holderLeft, BoxLayout.Y_AXIS));

		//##########################################################################################################
		//ExportOffset
		//##########################################################################################################
		JPanel pMapExportOffset = new JPanel();
		pMapExportOffset.setAlignmentX(Component.LEFT_ALIGNMENT);
		holderLeft.add(pMapExportOffset);
		pMapExportOffset.setBorder(new TitledBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null),
				Messages.getString("OBJExportOptions.OFFSET"), TitledBorder.CENTER, TitledBorder.TOP, null,
				new Color(0, 0, 0)));

		ButtonGroup gOffset = new ButtonGroup();
		pMapExportOffset.setLayout(new BoxLayout(pMapExportOffset, BoxLayout.Y_AXIS));

		holderMapScale = new JPanel();
		pMapExportOffset.add(holderMapScale);
		holderMapScale.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JLabel lblMapScale = new JLabel(Messages.getString("OBJExportOptions.MAP_SCALE"));
		holderMapScale.add(lblMapScale);

		textFieldMapScale = new JTextField();
		holderMapScale.add(textFieldMapScale);
		textFieldMapScale.setColumns(10);

		holderOffset = new JPanel();
		pMapExportOffset.add(holderOffset);

		JPanel holderOffsetBtns = new JPanel();
		holderOffset.add(holderOffsetBtns);

		rdbtnNone = new JRadioButton(Messages.getString("OBJExportOptions.NONE"));
		rdbtnNone.setSelected(true);
		gOffset.add(rdbtnNone);

		rdbtnCenter = new JRadioButton(Messages.getString("OBJExportOptions.CENTER"));
		gOffset.add(rdbtnCenter);

		rdbtnCustom = new JRadioButton(Messages.getString("OBJExportOptions.CUSTOM"));
		gOffset.add(rdbtnCustom);
		holderOffsetBtns.setLayout(new BoxLayout(holderOffsetBtns, BoxLayout.Y_AXIS));
		holderOffsetBtns.add(rdbtnNone);
		holderOffsetBtns.add(rdbtnCenter);
		holderOffsetBtns.add(rdbtnCustom);

		holderOffsetFields = new JPanel();
		holderOffset.add(holderOffsetFields);
		holderOffsetFields.setLayout(new BoxLayout(holderOffsetFields, BoxLayout.Y_AXIS));

		holderXOffset = new JPanel();
		holderOffsetFields.add(holderXOffset);
		holderXOffset.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JLabel lblX = new JLabel("X:");
		holderXOffset.add(lblX);

		txtX = new JTextField();
		holderXOffset.add(txtX);
		txtX.setText("0");
		txtX.setColumns(10);

		holderYOffset = new JPanel();
		holderOffsetFields.add(holderYOffset);

		JLabel lblY = new JLabel("Y:");
		holderYOffset.add(lblY);

		txtZ = new JTextField();
		holderYOffset.add(txtZ);
		txtZ.setText("0");
		txtZ.setColumns(10);

		if (!rdbtnCustom.isSelected()) {
			txtX.setEnabled(false);
			txtZ.setEnabled(false);
		}

		//##########################################################################################################
		//TextureExporting
		//##########################################################################################################
		JPanel pTextureOptions = new JPanel();
		pTextureOptions.setAlignmentX(Component.LEFT_ALIGNMENT);
		holderLeft.add(pTextureOptions);
		pTextureOptions.setBorder(new TitledBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null),
				Messages.getString("TexsplitDialog.WIN_TITLE"), TitledBorder.CENTER, TitledBorder.TOP, null, null));
		pTextureOptions.setLayout(new BoxLayout(pTextureOptions, BoxLayout.Y_AXIS));

		holderPreScale = new JPanel();
		holderPreScale.setAlignmentX(Component.LEFT_ALIGNMENT);
		holderPreScale.setBorder(new EmptyBorder(5, 5, 5, 5));
		pTextureOptions.add(holderPreScale);
		holderPreScale.setLayout(new BoxLayout(holderPreScale, BoxLayout.X_AXIS));

		JLabel lblPrescaleTextures = new JLabel(Messages.getString("TexsplitDialog.PRESCALE"));
		holderPreScale.add(lblPrescaleTextures);

		cboxTexScale = new JComboBox<String>();
		holderPreScale.add(cboxTexScale);
		cboxTexScale.setMaximumRowCount(16);
		cboxTexScale.setModel(new DefaultComboBoxModel<String>(new String[] { "1x", "2x", "4x", "8x", "16x" }));
		cboxTexScale.setMaximumSize(cboxTexScale.getPreferredSize());

		holderTexExport = new JPanel();
		holderTexExport.setAlignmentX(Component.LEFT_ALIGNMENT);
		holderTexExport.setBorder(new EmptyBorder(5, 5, 5, 5));
		pTextureOptions.add(holderTexExport);
		holderTexExport.setLayout(new BoxLayout(holderTexExport, BoxLayout.Y_AXIS));
		
		chckbxExportTextures = new JCheckBox(Messages.getString("TexsplitDialog.EXP_TEXTURES"));
		holderTexExport.add(chckbxExportTextures);

		chckbxSeparateAlphaTexture = new JCheckBox(Messages.getString("TexsplitDialog.EXP_ALPHA"));
		holderTexExport.add(chckbxSeparateAlphaTexture);
		
		//##########################################################################################################
		//SingleTexFile
		//##########################################################################################################

		holderSingleTexOpts = new JPanel();
		holderSingleTexOpts.setAlignmentX(Component.LEFT_ALIGNMENT);
		holderSingleTexOpts.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		//holderTexExport.add(holderSingleTexOpts);// TODO fix single tex export
		holderSingleTexOpts.setLayout(new BoxLayout(holderSingleTexOpts, BoxLayout.Y_AXIS));

		chckbxCombineAllTextures = new JCheckBox(Messages.getString("TexsplitDialog.EXP_SINGLE"));
		holderSingleTexOpts.add(chckbxCombineAllTextures);

		chckbxExportSeparateLight = new JCheckBox(Messages.getString("TexsplitDialog.EXP_SEPERATE_LIGHT"));
		holderSingleTexOpts.add(chckbxExportSeparateLight);
		
		chckbxSingleMat = new JCheckBox(Messages.getString("OBJExportOptions.SINGLE_MTL"));
		holderSingleTexOpts.add(chckbxSingleMat);
		
		holderExtraMaps = new JPanel();
		holderExtraMaps.setAlignmentX(Component.LEFT_ALIGNMENT);
		holderExtraMaps.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		holderTexExport.add(holderExtraMaps);
		holderExtraMaps.setLayout(new BoxLayout(holderExtraMaps, BoxLayout.Y_AXIS));
		
		holderExtraMapsText = new JPanel();
		holderExtraMapsText.setAlignmentX(Component.LEFT_ALIGNMENT);
		FlowLayout fl_holderExtraMapsText = (FlowLayout) holderExtraMapsText.getLayout();
		fl_holderExtraMapsText.setVgap(2);
		holderExtraMaps.add(holderExtraMapsText);
		
		lblExtraMaps = new JLabel(Messages.getString("TexsplitDialog.EXP_MAPS"));
		holderExtraMapsText.add(lblExtraMaps);
		lblExtraMaps.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		lblExtraMapsHelp = new JLabel("???");
		holderExtraMapsText.add(lblExtraMapsHelp);
		lblExtraMapsHelp.setToolTipText(Messages.getString("TexsplitDialog.EXP_MAPS_HELP"));
		lblExtraMapsHelp.setForeground(Color.RED);
		lblExtraMapsHelp.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		holderExtraMapsChks = new JPanel();
		holderExtraMapsChks.setAlignmentX(Component.LEFT_ALIGNMENT);
		FlowLayout fl_holderExtraMapsChks = (FlowLayout) holderExtraMapsChks.getLayout();
		fl_holderExtraMapsChks.setVgap(0);
		fl_holderExtraMapsChks.setHgap(0);
		holderExtraMaps.add(holderExtraMapsChks);
		
		chckbxExportNormalMaps = new JCheckBox(Messages.getString("TexsplitDialog.EXP_NORMAL"));
		holderExtraMapsChks.add(chckbxExportNormalMaps);
		
		chckbxExportSpecularMaps = new JCheckBox(Messages.getString("TexsplitDialog.EXP_SPECULAR"));
		holderExtraMapsChks.add(chckbxExportSpecularMaps);

		//##########################################################################################################
		//CloudsExport
		//##########################################################################################################
		JPanel pCloudExport = new JPanel();
		pCloudExport.setAlignmentX(Component.LEFT_ALIGNMENT);
		pTextureOptions.add(pCloudExport);
		pCloudExport.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		pCloudExport.setLayout(new BoxLayout(pCloudExport, BoxLayout.X_AXIS));

		holderCloudExports = new JPanel();
		holderCloudExports.setBorder(new EmptyBorder(5, 5, 5, 5));
		pCloudExport.add(holderCloudExports);
		holderCloudExports.setLayout(new BoxLayout(holderCloudExports, BoxLayout.Y_AXIS));

		JLabel lblExportCloudsObj = new JLabel(Messages.getString("TexsplitDialog.EXP_CLOUDS"));
		lblExportCloudsObj.setAlignmentX(Component.CENTER_ALIGNMENT);
		holderCloudExports.add(lblExportCloudsObj);
		lblExportCloudsObj.setHorizontalAlignment(SwingConstants.CENTER);

		btnMinecraftTextures = new JButton(Messages.getString("TexsplitDialog.MINECRAFT"));
		btnMinecraftTextures.setAlignmentX(Component.CENTER_ALIGNMENT);
		holderCloudExports.add(btnMinecraftTextures);

		btnFromResourcePack = new JButton(Messages.getString("TexsplitDialog.CUSTOM"));
		btnFromResourcePack.setAlignmentX(Component.CENTER_ALIGNMENT);
		holderCloudExports.add(btnFromResourcePack);

		//##########################################################################################################
		//ExportOptions
		//##########################################################################################################
		JPanel pExportOptions = new JPanel();
		holderTop.add(pExportOptions);
		pExportOptions.setBorder(new TitledBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null),
				Messages.getString("OBJExportPanel.WIN_TITLE"), TitledBorder.CENTER, TitledBorder.TOP, null, null));
		pExportOptions.setLayout(new BoxLayout(pExportOptions, BoxLayout.Y_AXIS));

		btnBlocksToExport = new JButton(Messages.getString("OBJExportOptions.BLOCKS"));
		pExportOptions.add(btnBlocksToExport);

		chckbxRenderUnknownBlocks = new JCheckBox(Messages.getString("OBJExportOptions.R_UNKNOWN"));
		pExportOptions.add(chckbxRenderUnknownBlocks);

		chckbxRenderWorldSides = new JCheckBox(Messages.getString("OBJExportOptions.R_SIDES"));
		pExportOptions.add(chckbxRenderWorldSides);

		chckbxRenderBiomes = new JCheckBox(Messages.getString("OBJExportOptions.R_BIOMES"));
		pExportOptions.add(chckbxRenderBiomes);

		chckbxRenderEntities = new JCheckBox(Messages.getString("OBJExportOptions.R_ENTITIES"));
		pExportOptions.add(chckbxRenderEntities);

		chckbxConvertOreTo = new JCheckBox(Messages.getString("OBJExportOptions.CONVERTORES"));
		pExportOptions.add(chckbxConvertOreTo);

		chckbxSeparateMat = new JCheckBox(Messages.getString("OBJExportOptions.SEP_OBJ_MTL"));
		pExportOptions.add(chckbxSeparateMat);

		chckbxSeparateMatOccl = new JCheckBox(Messages.getString("OBJExportOptions.SEP_OBJ_MTL_OCCL"));
		pExportOptions.add(chckbxSeparateMatOccl);

		chckbxSeparateChunk = new JCheckBox(Messages.getString("OBJExportOptions.SEP_OBJ_CHUNK"));
		pExportOptions.add(chckbxSeparateChunk);

		//##########################################################################################################
		//SeperateBlocks
		//##########################################################################################################
		holderSepBlock = new JPanel();
		holderSepBlock.setAlignmentX(Component.LEFT_ALIGNMENT);
		pExportOptions.add(holderSepBlock);
		holderSepBlock.setLayout(new BoxLayout(holderSepBlock, BoxLayout.X_AXIS));

		chckbxSeparateBlock = new JCheckBox(Messages.getString("OBJExportOptions.SEP_OBJ_BLOCK"));
		holderSepBlock.add(chckbxSeparateBlock);

		JLabel lblSepBlockWarn = new JLabel("!!!");
		holderSepBlock.add(lblSepBlockWarn);
		lblSepBlockWarn.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblSepBlockWarn.setToolTipText(Messages.getString("OBJExportOptions.SEP_OBJ_BLOCK_WARNING"));
		lblSepBlockWarn.setForeground(Color.RED);

		//##########################################################################################################
		//DoubleSidedFaces
		//##########################################################################################################
		chckbxDoubleSidedFaces = new JCheckBox(Messages.getString("OBJExportOptions.DOUBLE_SIDED_FACES"));
		pExportOptions.add(chckbxDoubleSidedFaces);

		//##########################################################################################################
		//OptimizeGeo
		//##########################################################################################################
		chckbxOptimiseGeo = new JCheckBox(Messages.getString("OBJExportOptions.OPTIMIZE_MESH"));
		pExportOptions.add(chckbxOptimiseGeo);

		//##########################################################################################################
		//MergeVerts
		//##########################################################################################################
		chckbxMergeVerticies = new JCheckBox(Messages.getString("OBJExportOptions.DUPL_VERT"));
		pExportOptions.add(chckbxMergeVerticies);

		//##########################################################################################################
		//ExportThreads
		//##########################################################################################################
		holderThreads = new JPanel();
		holderThreads.setAlignmentX(Component.LEFT_ALIGNMENT);
		pExportOptions.add(holderThreads);
		holderThreads.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		SpinnerNumberModel threadSpinnerModel = new SpinnerNumberModel(8, 1, 512, 1);
		spinnerThreads = new JSpinner(threadSpinnerModel);
		holderThreads.add(spinnerThreads);
		
		JLabel lblThreadsText = new JLabel(Messages.getString("OBJExportOptions.EXPORT_THREADS"));
		holderThreads.add(lblThreadsText);

		JLabel lblThreadsHelp = new JLabel("???");
		holderThreads.add(lblThreadsHelp);
		lblThreadsHelp.setToolTipText(Messages.getString("OBJExportOptions.EXPORT_THREADS_HELP"));
		lblThreadsHelp.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblThreadsHelp.setForeground(Color.RED);

		JLabel lblThreadsWarn = new JLabel("!!!");
		holderThreads.add(lblThreadsWarn);
		lblThreadsWarn.setToolTipText(Messages.getString("OBJExportOptions.EXPORT_THREADS_WARN"));
		lblThreadsWarn.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblThreadsWarn.setForeground(Color.RED);

		//##########################################################################################################
		//Export
		//##########################################################################################################
		holderExportPanel = new JPanel();
		holderExportPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		pExportOptions.add(holderExportPanel);
		holderExportPanel.setLayout(new BoxLayout(holderExportPanel, BoxLayout.Y_AXIS));

		chckbxUseLastSaveLoc = new JCheckBox(Messages.getString("OBJExportOptions.USE_LAST_SAVE_LOC"));
		chckbxUseLastSaveLoc.setAlignmentX(Component.CENTER_ALIGNMENT);
		holderExportPanel.add(chckbxUseLastSaveLoc);

		holderExportBtns = new JPanel();
		holderExportPanel.add(holderExportBtns);
		btnStartExport = new JButton(Messages.getString("OBJExportPanel.EXPORT_BTN"));

		if (Options.worldDir == null) {
			btnStartExport.setEnabled(false);
		}

		holderExportBtns.add(btnStartExport);

		btnForceStop = new JButton(Messages.getString("OBJExportPanel.STOP_BTN"));
		holderExportBtns.add(btnForceStop);
		btnForceStop.setEnabled(false);

		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		contentPane.add(progressBar);

		loadSettings();
		addActionListenersToAll();

		pack();

	}

	private void addActionListenersToAll() {

		// ACTION HANDLERS
		DocumentListener tf_listener = new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				saveSettings();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				saveSettings();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				saveSettings();
			}
		};

		AbstractAction genericSaveAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveSettings();
			}
		};

		ChangeListener genericSaveChange = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				saveSettings();
			}
		};

		AbstractAction offsetSaveAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				if (ev.getSource() == rdbtnCustom) {
					txtX.setEnabled(true);
					txtZ.setEnabled(true);
				} else {
					txtX.setEnabled(false);
					txtZ.setEnabled(false);
				}
				saveSettings();
			}
		};

		AbstractAction exportCloudsFromMC = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				JFileChooser jfc = new JFileChooser(MainWindow.settings.getLastExportPath()) {
					@Override
					public void approveSelection() {
						File f = getSelectedFile();
						if (!f.toString().substring(f.toString().length() - 4).contentEquals(".obj")
								|| f.toString().length() < 4)
							setSelectedFile(new File(f.toString() + ".obj"));
						f = getSelectedFile();

						if (f.exists()) {
							int result = JOptionPane.showConfirmDialog(this,
									Messages.getString("OBJExportPanel.OBJ_ERR"),
									Messages.getString("OBJExportOptions.OVER_OBJ"), JOptionPane.YES_NO_CANCEL_OPTION);
							switch (result) {
							case JOptionPane.YES_OPTION:
								super.approveSelection();
								return;
							case JOptionPane.NO_OPTION:
								return;
							case JOptionPane.CLOSED_OPTION:
								return;
							case JOptionPane.CANCEL_OPTION:
								cancelSelection();
								return;
							}
						}
						super.approveSelection();
					}
				};
				jfc.setFileFilter(new FileNameExtensionFilter("Obj files", "obj", "OBJ", "Obj"));
				int retval = jfc.showDialog(ExportWindow.this, Messages.getString("TexsplitDialog.SEL_EXPORT_DEST"));
				if (retval != JFileChooser.APPROVE_OPTION)
					return;
				ExportCloudsOBJ(new File(jfc.getCurrentDirectory().toString()), jfc.getSelectedFile(), null);

			}
		};

		AbstractAction exportCloudsFromRP = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				JFileChooser jfcRP = new JFileChooser(MainWindow.settings.getLastExportPath());
				jfcRP.setFileFilter(new FileNameExtensionFilter("Zip files", "zip", "ZIP", "Zip"));
				int retval = jfcRP.showDialog(ExportWindow.this, Messages.getString("TexsplitDialog.SEL_RP"));
				if (retval != JFileChooser.APPROVE_OPTION)
					return;

				JFileChooser jfc = new JFileChooser(MainWindow.settings.getLastExportPath()) {
					@Override
					public void approveSelection() {
						File f = getSelectedFile();
						if (!f.toString().substring(f.toString().length() - 4).contentEquals(".obj")
								|| f.toString().length() < 4)
							setSelectedFile(new File(f.toString() + ".obj"));
						f = getSelectedFile();

						if (f.exists()) {
							int result = JOptionPane.showConfirmDialog(this,
									Messages.getString("OBJExportPanel.OBJ_ERR"),
									Messages.getString("OBJExportOptions.OVER_OBJ"), JOptionPane.YES_NO_CANCEL_OPTION);
							switch (result) {
							case JOptionPane.YES_OPTION:
								super.approveSelection();
								return;
							case JOptionPane.NO_OPTION:
								return;
							case JOptionPane.CLOSED_OPTION:
								return;
							case JOptionPane.CANCEL_OPTION:
								cancelSelection();
								return;
							}
						}
						super.approveSelection();
					}
				};

				jfc.setFileFilter(new FileNameExtensionFilter("Obj files", "obj", "OBJ", "Obj"));
				retval = jfc.showDialog(ExportWindow.this, Messages.getString("TexsplitDialog.SEL_EXPORT_DEST"));
				if (retval != JFileChooser.APPROVE_OPTION)
					return;
				ExportCloudsOBJ(new File(jfc.getCurrentDirectory().toString()), jfc.getSelectedFile(),
						jfcRP.getSelectedFile());

			}
		};

		AbstractAction startExport = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {

				JFileChooser jfc = new JFileChooser(MainWindow.settings.getLastExportPath()) {
					@Override
					public void approveSelection() {
						File f = getSelectedFile();
						if (!f.toString().substring(f.toString().length() - 4).contentEquals(".obj")
								|| f.toString().length() < 4)
							setSelectedFile(new File(f.toString() + ".obj"));
						f = getSelectedFile();
						File f2 = new File(f.toString().replace(".obj", ".mtl"));

						if (f.exists()) {
							int result = JOptionPane.showConfirmDialog(this,
									Messages.getString("OBJExportPanel.OBJ_ERR"),
									Messages.getString("OBJExportOptions.OVER_OBJ"), JOptionPane.YES_NO_CANCEL_OPTION);
							switch (result) {
							case JOptionPane.YES_OPTION:

								if (f2.exists()) {
									int result2 = JOptionPane.showConfirmDialog(this,
											Messages.getString("OBJExportPanel.MTL_ERR"),
											Messages.getString("OBJExportOptions.OVER_MTL"),
											JOptionPane.YES_NO_CANCEL_OPTION);
									switch (result2) {
									case JOptionPane.YES_OPTION:
										sendExport();
										super.approveSelection();
										return;
									case JOptionPane.NO_OPTION:
										return;
									case JOptionPane.CLOSED_OPTION:
										return;
									case JOptionPane.CANCEL_OPTION:
										cancelSelection();
										return;
									}
								} else {
									sendExport();
									super.approveSelection();
									return;
								}

							case JOptionPane.NO_OPTION:
								return;
							case JOptionPane.CLOSED_OPTION:
								return;
							case JOptionPane.CANCEL_OPTION:
								cancelSelection();
								return;
							}
						} else {
							sendExport();
							super.approveSelection();
						}

					}

					private void sendExport() {

						File savePath = getCurrentDirectory();
						Options.outputDir = savePath;
						Options.objFileName = getSelectedFile().getName();
						Options.mtlFileName = getSelectedFile().getName().replace(".obj", ".mtl");

						prefs.put("LAST_USED_NAME", Options.objFileName);

						MainWindow.settings.setLastExportPath(savePath.toString());
						MainWindow.updateSelectionOptions();
						btnStartExport.setEnabled(false);
						btnForceStop.setEnabled(true);

						Thread t = new Thread(new Runnable() {
							@Override
							public void run() {
								stop = false;

								ObjExporter.export(ExportWindow.this, new StopCallback() {
									@Override
									public boolean stopRequested() {
										return stop;
									}
								}, true, true, Options.exportTex);

								btnStartExport.setEnabled(true);
								btnForceStop.setEnabled(false);
							}
						});
						t.setName("ExportThread");
						t.start();

					}

				};

				if (Options.useLastSaveLoc && !prefs.get("LAST_EXPORT_PATH", "not here").equals("not here")
						&& new File(prefs.get("LAST_EXPORT_PATH", "not here")).exists()) {
					Options.outputDir = new File(MainWindow.settings.getLastExportPath());
					Options.objFileName = prefs.get("LAST_USED_NAME", "minceaft.obj");
					Options.mtlFileName = prefs.get("LAST_USED_NAME", "minceaft.obj").replace(".obj", ".mtl");

					boolean fileExists = false;

					if (new File(Options.outputDir, Options.objFileName).exists()) {
						int result2 = JOptionPane.showConfirmDialog(jfc, Messages.getString("OBJExportPanel.OBJ_ERR"),
								Messages.getString("OBJExportOptions.OVER_OBJ"), JOptionPane.YES_NO_CANCEL_OPTION);
						switch (result2) {
						case JOptionPane.YES_OPTION:
							break;
						// return;
						case JOptionPane.NO_OPTION:
							fileExists = true;
							break;
						case JOptionPane.CLOSED_OPTION:
							return;
						case JOptionPane.CANCEL_OPTION:
							return;
						}
					}

					if (!fileExists && new File(Options.outputDir, Options.mtlFileName).exists()) {
						int result2 = JOptionPane.showConfirmDialog(jfc, Messages.getString("OBJExportPanel.MTL_ERR"),
								Messages.getString("OBJExportOptions.OVER_MTL"), JOptionPane.YES_NO_CANCEL_OPTION);
						switch (result2) {
						case JOptionPane.YES_OPTION:
							break;
						// return;
						case JOptionPane.NO_OPTION:
							fileExists = true;
							break;
						case JOptionPane.CLOSED_OPTION:
							return;
						case JOptionPane.CANCEL_OPTION:
							return;
						}
					}

					while (fileExists) {

						fileExists = false;
						jfc.showDialog(ExportWindow.this, Messages.getString("TexsplitDialog.SEL_EXPORT_DEST"));

					}

					MainWindow.updateSelectionOptions();
					btnStartExport.setEnabled(false);
					btnForceStop.setEnabled(true);

					Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							stop = false;

							ObjExporter.export(ExportWindow.this, new StopCallback() {
								@Override
								public boolean stopRequested() {
									return stop;
								}
							}, true, true, Options.exportTex);

							btnStartExport.setEnabled(true);
							btnForceStop.setEnabled(false);
						}
					});
					t.setName("ExportThread");
					t.start();

				} else {

					jfc.showDialog(ExportWindow.this, Messages.getString("TexsplitDialog.SEL_EXPORT_DEST"));

				}
			}

		};

		rdbtnCustom.addActionListener(offsetSaveAction);
		rdbtnCenter.addActionListener(offsetSaveAction);
		rdbtnNone.addActionListener(offsetSaveAction);

		textFieldMapScale.getDocument().addDocumentListener(tf_listener);
		txtZ.getDocument().addDocumentListener(tf_listener);
		txtX.getDocument().addDocumentListener(tf_listener);

		btnBlocksToExport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MainWindow.blocksWindow.setVisible(true);
			}
		});

		chckbxRenderUnknownBlocks.addActionListener(genericSaveAction);

		chckbxRenderWorldSides.addActionListener(genericSaveAction);
		chckbxRenderBiomes.addActionListener(genericSaveAction);
		chckbxRenderEntities.addActionListener(genericSaveAction);

		chckbxConvertOreTo.addActionListener(genericSaveAction);

		chckbxSeparateMat.addActionListener(genericSaveAction);
		chckbxSeparateMatOccl.addActionListener(genericSaveAction);
		chckbxSeparateChunk.addActionListener(genericSaveAction);
		chckbxSeparateBlock.addActionListener(genericSaveAction);

		chckbxDoubleSidedFaces.addActionListener(genericSaveAction);
		chckbxOptimiseGeo.addActionListener(genericSaveAction);
		chckbxMergeVerticies.addActionListener(genericSaveAction);

		chckbxSingleMat.addActionListener(genericSaveAction);
		
		spinnerThreads.addChangeListener(genericSaveChange);

		chckbxUseLastSaveLoc.addActionListener(genericSaveAction);
		btnStartExport.addActionListener(startExport);
		btnForceStop.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				stop = true;
			}
		});

		cboxTexScale.addActionListener(genericSaveAction);

		chckbxExportTextures.addActionListener(genericSaveAction);
		chckbxSeparateAlphaTexture.addActionListener(genericSaveAction);
		chckbxExportNormalMaps.addActionListener(genericSaveAction);
		chckbxExportSpecularMaps.addActionListener(genericSaveAction);
		chckbxCombineAllTextures.addActionListener(genericSaveAction);
		chckbxExportSeparateLight.addActionListener(genericSaveAction);

		btnFromResourcePack.addActionListener(exportCloudsFromRP);
		btnMinecraftTextures.addActionListener(exportCloudsFromMC);

	}

	private void loadSettings() {

		textFieldMapScale.setText("" + prefs.getFloat("DEFAULT_SCALE", 1.0f));

		switch (prefs.getInt("OFFSET_TYPE", 0)) {
		case 0:
			rdbtnNone.setSelected(true);
			txtX.setEnabled(false);
			txtZ.setEnabled(false);
			break;
		case 1:
			rdbtnCenter.setSelected(true);
			txtX.setEnabled(false);
			txtZ.setEnabled(false);
			break;
		case 2:
			rdbtnCustom.setSelected(true);
			txtX.setEnabled(true);
			txtZ.setEnabled(true);
			break;
		}

		txtX.setText("" + prefs.getInt("OFFSET_X", 0));
		txtZ.setText("" + prefs.getInt("OFFSET_Z", 0));

		chckbxRenderUnknownBlocks.setSelected(prefs.getBoolean("RENDER_UNKNOWN", true));
		chckbxRenderWorldSides.setSelected(prefs.getBoolean("RENDER_SIDES", false));
		chckbxRenderBiomes.setSelected(prefs.getBoolean("RENDER_BIOMES", true));
		chckbxRenderEntities.setSelected(prefs.getBoolean("RENDER_ENTITIES", true));
		chckbxConvertOreTo.setSelected(prefs.getBoolean("CONVERT_ORES", true));
		chckbxSeparateMat.setSelected(prefs.getBoolean("OBJ_PER_MTL", false));
		chckbxSeparateMatOccl.setSelected(prefs.getBoolean("OBJ_PER_MTL_OCCL", true));
		chckbxSeparateChunk.setSelected(prefs.getBoolean("OBJ_PER_CHUNK", false));
		chckbxSeparateBlock.setSelected(prefs.getBoolean("OBJ_PER_BLOCK", false));
		chckbxDoubleSidedFaces.setSelected(prefs.getBoolean("DOUBLE_SINGLE_FACES", false));
		chckbxOptimiseGeo.setSelected(prefs.getBoolean("OPTIMISE_GEO", true));
		chckbxMergeVerticies.setSelected(prefs.getBoolean("REMOVE_DUPLICATES", true));
		
		chckbxUseLastSaveLoc.setSelected(prefs.getBoolean("USE_LAST_SAVE_LOC", false));
		
		cboxTexScale.setSelectedItem("" + prefs.getDouble("TEXTURE_SCALE_ID", 1.0));
		chckbxExportTextures.setSelected(prefs.getBoolean("TEXTURE_EXPORT", true));
		chckbxSeparateAlphaTexture.setSelected(prefs.getBoolean("TEXTURE_ALPHA", false));
		chckbxExportNormalMaps.setSelected(prefs.getBoolean("TEXTURE_NORMAL", false));
		chckbxExportSpecularMaps.setSelected(prefs.getBoolean("TEXTURE_SPECULAR", false));
		chckbxExportSeparateLight.setSelected(prefs.getBoolean("TEXTURE_LIGHT", false));
		chckbxCombineAllTextures.setSelected(prefs.getBoolean("TEXTURE_MERGE", false));
		chckbxSingleMat.setSelected(prefs.getBoolean("SINGLE_MTL", true));
		
		spinnerThreads.setValue(prefs.getInt("EXPORT_THREADS", 8));
		
		updateEnabledSettings();

		updateOptions();

	}

	private void saveSettings() {

		Log.debug("Saving export settings");

		prefs.putFloat("DEFAULT_SCALE", Options.scale);
		prefs.putInt("OFFSET_X", Options.offsetX);
		prefs.putInt("OFFSET_Z", Options.offsetZ);

		switch (Options.offsetType) {
		case NONE:
			prefs.putInt("OFFSET_TYPE", 0);
			break;
		case CENTER:
			prefs.putInt("OFFSET_TYPE", 1);
			break;
		case CUSTOM:
			prefs.putInt("OFFSET_TYPE", 2);
			break;
		}

		switch (Options.objOverwriteAction) {
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

		switch (Options.mtlOverwriteAction) {
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
		
		updateEnabledSettings();

		updateOptions();

		prefs.putBoolean("RENDER_SIDES", Options.renderSides);
		prefs.putBoolean("RENDER_BIOMES", Options.renderBiomes);
		prefs.putBoolean("RENDER_ENTITIES", Options.renderEntities);
		prefs.putBoolean("RENDER_UNKNOWN", Options.renderUnknown);
		prefs.putBoolean("OBJ_PER_MTL", Options.objectPerMaterial);
		prefs.putBoolean("OBJ_PER_MTL_OCCL", Options.objectPerMaterialOcclusion);
		prefs.putBoolean("OBJ_PER_CHUNK", Options.objectPerChunk);
		prefs.putBoolean("OBJ_PER_BLOCK", Options.objectPerBlock);
		prefs.putBoolean("DOUBLE_SINGLE_FACES", Options.doubleSidedFaces);
		prefs.putBoolean("OPTIMISE_GEO", Options.optimiseGeometry);
		prefs.putBoolean("CONVERT_ORES", Options.convertOres);
		prefs.putBoolean("SINGLE_MTL", Options.singleMaterial);
		prefs.putBoolean("REMOVE_DUPLICATES", Options.removeDuplicates);
		prefs.putBoolean("USE_LAST_SAVE_LOC", Options.useLastSaveLoc);

		prefs.putDouble("TEXTURE_SCALE_ID", Options.textureScale);
		prefs.putBoolean("TEXTURE_EXPORT", Options.exportTex);
		prefs.putBoolean("TEXTURE_ALPHA", Options.textureAlpha);
		prefs.putBoolean("TEXTURE_NORMAL", Options.textureNormal);
		prefs.putBoolean("TEXTURE_SPECULAR", Options.textureSpecular);
		prefs.putBoolean("TEXTURE_LIGHT", Options.textureLight);
		prefs.putBoolean("TEXTURE_MERGE", Options.textureMerge);
		
		prefs.putInt("EXPORT_THREADS", Options.exportThreads);
	}

	private void updateEnabledSettings() {
		/*if (chckbxCombineAllTextures.isSelected()) {TODO fix single tex export
			chckbxExportSeparateLight.setEnabled(true);
			chckbxSingleMat.setEnabled(true);
			chckbxOptimiseGeo.setEnabled(false);
		} else*/ {
			chckbxExportSeparateLight.setEnabled(false);
			chckbxSingleMat.setEnabled(false);
			chckbxOptimiseGeo.setEnabled(true);
		}

		if (chckbxSeparateMat.isSelected()) {
			chckbxSeparateMatOccl.setEnabled(true);
		} else {
			chckbxSeparateMatOccl.setEnabled(false);
		}
	}

	private void updateOptions() {

		try {
			Options.scale = Float.parseFloat(textFieldMapScale.getText());
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, Messages.getString("OBJExportOptions.SCALE_NUM_ERR"));
			Options.scale = 1.0f;
		}

		try {
			String txt = txtX.getText();
			if (!txt.isEmpty() && !txt.equals("-"))
				Options.offsetX = Integer.parseInt(txt);
			txt = txtZ.getText();
			if (!txt.isEmpty() && !txt.equals("-"))
				Options.offsetZ = Integer.parseInt(txt);

		} catch (NumberFormatException e) {
			Log.error("Offset number format error!", e, false);
		}

		if (rdbtnCenter.isSelected())
			Options.offsetType = OffsetType.CENTER;
		else if (rdbtnCustom.isSelected())
			Options.offsetType = OffsetType.CUSTOM;
		else
			Options.offsetType = OffsetType.NONE;

		Options.renderSides = chckbxRenderWorldSides.isSelected();
		Options.renderBiomes = chckbxRenderBiomes.isSelected();
		Options.renderEntities = chckbxRenderEntities.isSelected();
		Options.renderUnknown = chckbxRenderUnknownBlocks.isSelected();
		Options.objectPerMaterial = chckbxSeparateMat.isSelected();
		Options.objectPerMaterialOcclusion = chckbxSeparateMatOccl.isSelected();
		Options.objectPerChunk = chckbxSeparateChunk.isSelected();
		Options.objectPerBlock = chckbxSeparateBlock.isSelected();
		Options.doubleSidedFaces = chckbxDoubleSidedFaces.isSelected();
		Options.optimiseGeometry = chckbxOptimiseGeo.isSelected() && chckbxOptimiseGeo.isEnabled();
		Options.convertOres = chckbxConvertOreTo.isSelected();
		Options.removeDuplicates = chckbxMergeVerticies.isSelected();
		Options.useLastSaveLoc = chckbxUseLastSaveLoc.isSelected();

		String txt = cboxTexScale.getSelectedItem().toString();
		if (!txt.isEmpty()) {
			if (txt.endsWith("x"))
				txt = txt.substring(0, txt.length() - 1);

			try {
				Options.textureScale = Double.parseDouble(txt);
			} catch (NumberFormatException e) {
				Log.error(Messages.getString("TexsplitDialog.ERR_SCALE"), e, false);
			}
		}

		Options.exportTex = chckbxExportTextures.isSelected();
		Options.textureAlpha = chckbxSeparateAlphaTexture.isSelected();
		Options.textureNormal = chckbxExportNormalMaps.isSelected();
		Options.textureSpecular = chckbxExportSpecularMaps.isSelected();
		Options.textureMerge = chckbxCombineAllTextures.isSelected();
		Options.textureLight = chckbxExportSeparateLight.isSelected() && chckbxExportSeparateLight.isEnabled();
		Options.singleMaterial = chckbxSingleMat.isSelected() && chckbxSingleMat.isEnabled();
		
		Options.exportThreads = (Integer)spinnerThreads.getValue();
	}

	private void ExportCloudsOBJ(final File destination, final File file, final File texturepack) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					CloudsExporter.exportClouds(destination, texturepack, file.getName());
				} catch (Exception e) {
					Log.error(Messages.getString("TexsplitDialog.ERR_EXP"), e);
				}
			}
		}).start();
	}

	@Override
	public void setProgress(float value) {
		progressBar.setValue((int) (value * 100f));
	}

	public void mapLoaded() {
		btnStartExport.setEnabled(true);
	}
}