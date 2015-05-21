package org.jmc.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jmc.CloudsExporter;
import org.jmc.ObjExporter;
import org.jmc.Options;
import org.jmc.Options.OffsetType;
import org.jmc.ProgressCallback;
import org.jmc.StopCallback;
import org.jmc.TextureExporter;
import org.jmc.util.Log;
import org.jmc.util.Messages;

@SuppressWarnings("serial")
public class ExportWindow extends JFrame implements ProgressCallback{

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
	private JCheckBox chckbxSeparateChunk;
	private JCheckBox chckbxSeparateBlock;
	private JCheckBox chckbxGeoOpt;
	private JCheckBox chckbxConvertOreTo;
	private JCheckBox chckbxMergeVerticies;
	private JCheckBox chckbxSingleMat;
	private JCheckBox chckbxSingleTexture;
	
	private JComboBox<String> cboxTexScale;
	private JCheckBox chckbxSeparateAlphaTexture;
	private JCheckBox chckbxCombineAllTextures;
	
	private JButton btnBrowseUV;
	private JTextField textFieldSingleTexUV;
	
	private JButton btnStartExport;
	private JButton btnForceStop;
	private JButton btnFromResourcePack;
	private JButton btnMinecraftTextures;
	private JButton btnCustomResourcePack;
	private JButton btnMinecraftDefault;
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
	private JPanel holderOneMat;
	private JPanel holderSingleTex;
	private JPanel holderUV;
	private JPanel holderExportPanel;
	private JPanel holderLeft;
	private JPanel holderTop;
	private JPanel holderOffset;
	private JCheckBox chckbxUseLastSaveLoc;
	private JPanel holderExportBtns;

	/**
	 * Create the frame.
	 */
	public ExportWindow() {
		setBounds(100, 100, 500, 475);
		
		
		contentPane = new JPanel();
		prefs=MainWindow.settings.getPreferences();
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
		
		
		JPanel pMapExportOffset = new JPanel();
		holderLeft.add(pMapExportOffset);
		pMapExportOffset.setToolTipText("Map Offset Options");
		pMapExportOffset.setBorder(new TitledBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), "Map Export Ofset", TitledBorder.CENTER, TitledBorder.TOP, null, new Color(0, 0, 0)));
		
		ButtonGroup gOffset=new ButtonGroup();		
		pMapExportOffset.setLayout(new BoxLayout(pMapExportOffset, BoxLayout.Y_AXIS));
		
		holderMapScale = new JPanel();
		pMapExportOffset.add(holderMapScale);
		holderMapScale.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		
		JLabel lblMapScale = new JLabel("Map Scale");
		holderMapScale.add(lblMapScale);
		
		textFieldMapScale = new JTextField();
		holderMapScale.add(textFieldMapScale);
		textFieldMapScale.setColumns(10);
		
		holderOffset = new JPanel();
		pMapExportOffset.add(holderOffset);
		
		JPanel holderOffsetBtns = new JPanel();
		holderOffset.add(holderOffsetBtns);
		
		rdbtnNone = new JRadioButton("None");
		rdbtnNone.setSelected(true);
		gOffset.add(rdbtnNone);
		
		rdbtnCenter = new JRadioButton("Center");
		gOffset.add(rdbtnCenter);
		
		rdbtnCustom = new JRadioButton("Custom");
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
		
		if(!rdbtnCustom.isSelected()){
			txtX.setEnabled(false);
			txtZ.setEnabled(false);
		}
		
		JPanel pTextureOptions = new JPanel();
		holderLeft.add(pTextureOptions);
		pTextureOptions.setBorder(new TitledBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), "Texture Options", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		pTextureOptions.setLayout(new BoxLayout(pTextureOptions, BoxLayout.Y_AXIS));
		
		holderPreScale = new JPanel();
		holderPreScale.setBorder(new EmptyBorder(5, 5, 5, 5));
		pTextureOptions.add(holderPreScale);
		holderPreScale.setLayout(new BoxLayout(holderPreScale, BoxLayout.X_AXIS));

		JLabel lblPrescaleTextures = new JLabel("Pre-Scale Textures  ");
		holderPreScale.add(lblPrescaleTextures);
		
		cboxTexScale = new JComboBox<String>();
		holderPreScale.add(cboxTexScale);
		cboxTexScale.setMaximumRowCount(16);
		cboxTexScale.setModel(new DefaultComboBoxModel<String>(new String[] {"1x", "2x", "4x", "8x", "16x"}));
		cboxTexScale.setMaximumSize(cboxTexScale.getPreferredSize());
		
		holderTexExport = new JPanel();
		holderTexExport.setBorder(new EmptyBorder(5, 5, 5, 5));
		pTextureOptions.add(holderTexExport);
		holderTexExport.setLayout(new BoxLayout(holderTexExport, BoxLayout.Y_AXIS));

		chckbxSeparateAlphaTexture = new JCheckBox("Separate Alpha Texture");
		chckbxSeparateAlphaTexture.setAlignmentX(Component.CENTER_ALIGNMENT);
		holderTexExport.add(chckbxSeparateAlphaTexture);
		
		chckbxCombineAllTextures = new JCheckBox("Combine All Textures");
		chckbxCombineAllTextures.setAlignmentX(Component.CENTER_ALIGNMENT);
		holderTexExport.add(chckbxCombineAllTextures);
		
		JLabel lblExportTexturesFrom = new JLabel("Export Textures From:");
		lblExportTexturesFrom.setAlignmentX(Component.CENTER_ALIGNMENT);
		holderTexExport.add(lblExportTexturesFrom);
		
		btnMinecraftDefault = new JButton("Minecraft Default");
		btnMinecraftDefault.setAlignmentX(Component.CENTER_ALIGNMENT);
		holderTexExport.add(btnMinecraftDefault);
		
		btnCustomResourcePack = new JButton("Custom Resource Pack");
		btnCustomResourcePack.setAlignmentX(Component.CENTER_ALIGNMENT);
		holderTexExport.add(btnCustomResourcePack);
		
		JPanel pCloudExport = new JPanel();
		pTextureOptions.add(pCloudExport);
		pCloudExport.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		pCloudExport.setLayout(new BoxLayout(pCloudExport, BoxLayout.X_AXIS));
		
		holderCloudExports = new JPanel();
		holderCloudExports.setBorder(new EmptyBorder(5, 5, 5, 5));
		pCloudExport.add(holderCloudExports);
		holderCloudExports.setAlignmentX(Component.RIGHT_ALIGNMENT);
		holderCloudExports.setLayout(new BoxLayout(holderCloudExports, BoxLayout.Y_AXIS));
		
		JLabel lblExportCloudsObj = new JLabel("Export Clouds OBJ:");
		lblExportCloudsObj.setAlignmentX(Component.CENTER_ALIGNMENT);
		holderCloudExports.add(lblExportCloudsObj);
		lblExportCloudsObj.setHorizontalAlignment(SwingConstants.CENTER);
		
		btnMinecraftTextures = new JButton("From Minecraft");
		btnMinecraftTextures.setAlignmentX(Component.CENTER_ALIGNMENT);
		holderCloudExports.add(btnMinecraftTextures);
		
		btnFromResourcePack = new JButton("From Resource Pack");
		btnFromResourcePack.setAlignmentX(Component.CENTER_ALIGNMENT);
		holderCloudExports.add(btnFromResourcePack);
		
		JPanel pExportOptions = new JPanel();
		holderTop.add(pExportOptions);
		pExportOptions.setBorder(new TitledBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null), "Map Export Options", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		pExportOptions.setLayout(new BoxLayout(pExportOptions, BoxLayout.Y_AXIS));
		
		btnBlocksToExport = new JButton("Select Blocks to Export");
		pExportOptions.add(btnBlocksToExport);
		
		chckbxRenderUnknownBlocks = new JCheckBox("Render Unknown Blocks");
		pExportOptions.add(chckbxRenderUnknownBlocks);
		
		chckbxRenderWorldSides = new JCheckBox("Render World Sides & Bottom");
		pExportOptions.add(chckbxRenderWorldSides);
		
		chckbxRenderBiomes = new JCheckBox("Render Biomes");
		pExportOptions.add(chckbxRenderBiomes);
		
		chckbxRenderEntities = new JCheckBox("Render Entities (paintings)");
		pExportOptions.add(chckbxRenderEntities);
		
		chckbxConvertOreTo = new JCheckBox("Convert Ore to Stone");
		pExportOptions.add(chckbxConvertOreTo);
		
		chckbxSeparateMat = new JCheckBox("Separate Object per Material");
		pExportOptions.add(chckbxSeparateMat);
		
		chckbxSeparateChunk = new JCheckBox("Separate Object per Chunk");
		pExportOptions.add(chckbxSeparateChunk);
		
		holderSepBlock = new JPanel();
		holderSepBlock.setAlignmentX(Component.LEFT_ALIGNMENT);
		pExportOptions.add(holderSepBlock);
		holderSepBlock.setLayout(new BoxLayout(holderSepBlock, BoxLayout.X_AXIS));
		
		chckbxSeparateBlock = new JCheckBox("Separate Object per Block");
		holderSepBlock.add(chckbxSeparateBlock);
		
		JLabel lblSepBlockWarn = new JLabel("!!!");
		holderSepBlock.add(lblSepBlockWarn);
		lblSepBlockWarn.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblSepBlockWarn.setToolTipText("WARNING! Will increase model size DRASTICALLY");
		lblSepBlockWarn.setForeground(Color.RED);
			
		chckbxGeoOpt = new JCheckBox("Optimize Mesh");
		pExportOptions.add(chckbxGeoOpt);
		
		chckbxMergeVerticies = new JCheckBox("Merge Verticies");
		pExportOptions.add(chckbxMergeVerticies);
		
		holderOneMat = new JPanel();
		holderOneMat.setAlignmentX(Component.LEFT_ALIGNMENT);
		pExportOptions.add(holderOneMat);
		holderOneMat.setLayout(new BoxLayout(holderOneMat, BoxLayout.X_AXIS));

		chckbxSingleMat = new JCheckBox("Only Create 1 Material");
		holderOneMat.add(chckbxSingleMat);
		
		JLabel lblOneMatHelp = new JLabel("???");
		holderOneMat.add(lblOneMatHelp);
		lblOneMatHelp.setToolTipText("Should be used with the Single Texture Option below");
		lblOneMatHelp.setForeground(Color.RED);
		lblOneMatHelp.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		holderSingleTex = new JPanel();
		holderSingleTex.setAlignmentX(Component.LEFT_ALIGNMENT);
		pExportOptions.add(holderSingleTex);
		holderSingleTex.setLayout(new BoxLayout(holderSingleTex, BoxLayout.X_AXIS));

		chckbxSingleTexture = new JCheckBox("Use Single Texture");
		holderSingleTex.add(chckbxSingleTexture);
		
		JLabel lblSingleTexHelp = new JLabel("???");
		holderSingleTex.add(lblSingleTexHelp);
		lblSingleTexHelp.setToolTipText("Use the Combine Textures option in the Texture Export Option to generate a uv file");
		lblSingleTexHelp.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblSingleTexHelp.setForeground(Color.RED);
		
		holderUV = new JPanel();
		holderUV.setAlignmentX(Component.LEFT_ALIGNMENT);
		pExportOptions.add(holderUV);
		holderUV.setLayout(new BoxLayout(holderUV, BoxLayout.X_AXIS));
		
		textFieldSingleTexUV = new JTextField();
		textFieldSingleTexUV.setColumns(24);
		textFieldSingleTexUV.setMaximumSize(new Dimension(Integer.MAX_VALUE, textFieldSingleTexUV.getPreferredSize().height));
		holderUV.add(textFieldSingleTexUV);
		
		btnBrowseUV = new JButton("Browse");
		holderUV.add(btnBrowseUV);
		
		holderExportPanel = new JPanel();
		holderExportPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		pExportOptions.add(holderExportPanel);
		holderExportPanel.setLayout(new BoxLayout(holderExportPanel, BoxLayout.Y_AXIS));
		
		chckbxUseLastSaveLoc = new JCheckBox("Use the last save location and name?");
		chckbxUseLastSaveLoc.setAlignmentX(Component.CENTER_ALIGNMENT);
		holderExportPanel.add(chckbxUseLastSaveLoc);
		
		holderExportBtns = new JPanel();
		holderExportPanel.add(holderExportBtns);
		btnStartExport = new JButton("Start Export");
		holderExportBtns.add(btnStartExport);
		
		btnForceStop = new JButton("Force Stop");
		holderExportBtns.add(btnForceStop);
		btnForceStop.setEnabled(false);
		
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		contentPane.add(progressBar);
		
		
		loadSettings();
		addActionListenersToAll();
		
	}
	
	
	private void addActionListenersToAll(){
		
		//ACTION HANDLERS
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
		
		AbstractAction offsetSaveAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				if(ev.getSource() == rdbtnCustom)
				{
					txtX.setEnabled(true);
					txtZ.setEnabled(true);
				}
				else
				{
					txtX.setEnabled(false);
					txtZ.setEnabled(false);
				}
				saveSettings();
			}
		};
		
		AbstractAction exportTexFromMC = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				JFileChooser jfc=new JFileChooser(MainWindow.settings.getLastExportPath());
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int retval=jfc.showDialog(ExportWindow.this, "Select Export Destination");
				if(retval!=JFileChooser.APPROVE_OPTION) return;
				ExportTextures(new File(jfc.getSelectedFile().toString().concat("/tex")), null, Double.parseDouble(cboxTexScale.getSelectedItem().toString().replace("x","")), chckbxCombineAllTextures.isSelected(), chckbxSeparateAlphaTexture.isSelected());
				
			}
		};
		
		AbstractAction exportTexFromRP = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				JFileChooser jfc=new JFileChooser(MainWindow.settings.getLastExportPath());
				jfc.setFileFilter(new FileNameExtensionFilter("Zip files", "zip", "ZIP", "Zip"));
				int retval=jfc.showDialog(ExportWindow.this, "Select Resource Pack");
				if(retval!=JFileChooser.APPROVE_OPTION) return;
				JFileChooser jfcDest=new JFileChooser(MainWindow.settings.getLastExportPath());
				jfcDest.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				retval=jfcDest.showDialog(ExportWindow.this, "Select Export Destination");
				if(retval!=JFileChooser.APPROVE_OPTION) return;
				ExportTextures(new File(jfcDest.getSelectedFile().toString().concat("/tex")), jfc.getSelectedFile(), Double.parseDouble(cboxTexScale.getSelectedItem().toString().replace("x","")), chckbxCombineAllTextures.isSelected(), chckbxSeparateAlphaTexture.isSelected());
				
			}
		};
		
		AbstractAction exportCloudsFromMC = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser jfc=new JFileChooser(MainWindow.settings.getLastExportPath()){
					@Override
					public void approveSelection(){
				        File f = getSelectedFile();
				        if(!f.toString().substring(f.toString().length()-4).contentEquals(".obj") || f.toString().length() < 4)
				        	setSelectedFile(new File(f.toString() + ".obj")); f = getSelectedFile();
				        
				        if(f.exists()){
				            int result = JOptionPane.showConfirmDialog(this,"The file exists, overwrite?","Existing file",JOptionPane.YES_NO_CANCEL_OPTION);
				            switch(result){
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
				int retval=jfc.showDialog(ExportWindow.this, "Select Export Destination");
				if(retval!=JFileChooser.APPROVE_OPTION) return;
				ExportCloudsOBJ(new File(jfc.getCurrentDirectory().toString()), jfc.getSelectedFile(), null);
				
			}
		};
		
		AbstractAction exportCloudsFromRP = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				JFileChooser jfcRP=new JFileChooser(MainWindow.settings.getLastExportPath());
				jfcRP.setFileFilter(new FileNameExtensionFilter("Zip files", "zip", "ZIP", "Zip"));
				int retval=jfcRP.showDialog(ExportWindow.this, "Select Resource Pack");
				if(retval!=JFileChooser.APPROVE_OPTION) return;
				
				JFileChooser jfc=new JFileChooser(MainWindow.settings.getLastExportPath()){
					@Override
				    public void approveSelection(){
				        File f = getSelectedFile();
				        if(!f.toString().substring(f.toString().length()-4).contentEquals(".obj") || f.toString().length() < 4)
				        	setSelectedFile(new File(f.toString() + ".obj")); f = getSelectedFile();
				        
				        if(f.exists()){
				            int result = JOptionPane.showConfirmDialog(this,"The file exists, overwrite?","Existing file",JOptionPane.YES_NO_CANCEL_OPTION);
				            switch(result){
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
				retval=jfc.showDialog(ExportWindow.this, "Select Export Destination");
				if(retval!=JFileChooser.APPROVE_OPTION) return;
				ExportCloudsOBJ(new File(jfc.getCurrentDirectory().toString()), jfc.getSelectedFile(), jfcRP.getSelectedFile());
				
			}
		};
		
		AbstractAction uvSelect = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				JFileChooser jfcFile=new JFileChooser(MainWindow.settings.getLastExportPath());
				jfcFile.setDialogTitle("UV File");
				jfcFile.setFileFilter(new FileNameExtensionFilter("UVfile", "uv"));
				if(jfcFile.showDialog(ExportWindow.this, "Select UV File")!=JFileChooser.APPROVE_OPTION)	{
					return;
				}

				File save_path=jfcFile.getSelectedFile();													
				textFieldSingleTexUV.setText(save_path.getAbsolutePath());
			}
		};		
		
		AbstractAction startExport = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e){
				
				JFileChooser jfc=new JFileChooser(MainWindow.settings.getLastExportPath()){
					@Override
				    public void approveSelection(){
				        File f = getSelectedFile();
				        if(!f.toString().substring(f.toString().length()-4).contentEquals(".obj") || f.toString().length() < 4)
				        	setSelectedFile(new File(f.toString() + ".obj")); f = getSelectedFile();	
				        File f2 = new File(f.toString().replace(".obj", ".mtl"));
				        
				        if(f.exists()){
				            int result = JOptionPane.showConfirmDialog(this,"The OBJ file exists, overwrite?","Existing file",JOptionPane.YES_NO_CANCEL_OPTION);
				            switch(result){
				                case JOptionPane.YES_OPTION:
				                	
				                    if(f2.exists()){
							            int result2 = JOptionPane.showConfirmDialog(this,"The MTL file exists, overwrite?","Existing file",JOptionPane.YES_NO_CANCEL_OPTION);
							            switch(result2){
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
							        }
							        else{
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
				        }
				        else{
				            sendExport();
				        	super.approveSelection();
				        }
				        
				    }
					
					private void sendExport(){
						
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
								stop=false;
		
								ObjExporter.export(ExportWindow.this,
									new StopCallback() {
										@Override
										public boolean stopRequested() {
											return stop;
										}
									}, 
									true,
									true);
								
								btnStartExport.setEnabled(true);
								btnForceStop.setEnabled(false);
							}
						});
						t.start();
						
				    }
					
				};
				
				if(Options.useLastSaveLoc && !prefs.get("LAST_EXPORT_PATH", "not here").equals("not here") && new File(prefs.get("LAST_EXPORT_PATH", "not here")).exists()){
					Options.outputDir = new File(MainWindow.settings.getLastExportPath());
					Options.objFileName = prefs.get("LAST_USED_NAME", "minceaft.obj");
					Options.mtlFileName = prefs.get("LAST_USED_NAME", "minceaft.obj").replace(".obj", ".mtl");
					
					if(new File(Options.outputDir, Options.objFileName).exists() || new File(Options.outputDir, Options.mtlFileName).exists()){
			            int result2 = JOptionPane.showConfirmDialog(jfc,"The OBJ or MTL file exists, overwrite?","Existing file",JOptionPane.YES_NO_CANCEL_OPTION);
			            switch(result2){
			                case JOptionPane.YES_OPTION:
			                	break;
			                    //return;
			                case JOptionPane.NO_OPTION:
			                	jfc.showDialog(ExportWindow.this, "Select Export Destination");
			                    return;
			                case JOptionPane.CLOSED_OPTION:
			                    return;
			                case JOptionPane.CANCEL_OPTION:
			                    return;
			            }
			        }
					
					MainWindow.updateSelectionOptions();
					btnStartExport.setEnabled(false);
					btnForceStop.setEnabled(true);
					
					Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							stop=false;
	
							ObjExporter.export(ExportWindow.this,
								new StopCallback() {
									@Override
									public boolean stopRequested() {
										return stop;
									}
								}, 
								true,
								true);
							
							btnStartExport.setEnabled(true);
							btnForceStop.setEnabled(false);
						}
					});
					t.start();
					
				}
				else{
					jfc.showDialog(ExportWindow.this, "Select Export Destination");
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
		chckbxSeparateChunk.addActionListener(genericSaveAction);
		chckbxSeparateBlock.addActionListener(genericSaveAction);
		
		chckbxGeoOpt.addActionListener(genericSaveAction);
		chckbxMergeVerticies.addActionListener(genericSaveAction);
		
		chckbxSingleMat.addActionListener(genericSaveAction);
		chckbxSingleTexture.addActionListener(genericSaveAction);
		textFieldSingleTexUV.getDocument().addDocumentListener(tf_listener);
		btnBrowseUV.addActionListener(uvSelect);
		
		chckbxUseLastSaveLoc.addActionListener(genericSaveAction);
		btnStartExport.addActionListener(startExport);
		btnForceStop.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				stop = true;				
			}
		});
		
		cboxTexScale.addActionListener(genericSaveAction);
		
		chckbxSeparateAlphaTexture.addActionListener(genericSaveAction);
		chckbxCombineAllTextures.addActionListener(genericSaveAction);
		
		btnMinecraftDefault.addActionListener(exportTexFromMC);
		btnCustomResourcePack.addActionListener(exportTexFromRP);
		
		btnFromResourcePack.addActionListener(exportCloudsFromRP);
		btnMinecraftTextures.addActionListener(exportCloudsFromMC);
		
	}
	
	
	private void loadSettings(){
		
		textFieldMapScale.setText("" + prefs.getFloat("DEFAULT_SCALE", 1.0f));
		
		switch(prefs.getInt("OFFSET_TYPE", 0)){
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
		
		txtX.setText(""+prefs.getInt("OFFSET_X", 0));
		txtZ.setText(""+prefs.getInt("OFFSET_Z", 0));
		
		chckbxRenderWorldSides.setSelected(prefs.getBoolean("RENDER_SIDES", true));
		chckbxRenderBiomes.setSelected(prefs.getBoolean("RENDER_BIOMES", true));
		chckbxRenderEntities.setSelected(prefs.getBoolean("RENDER_ENTITIES", true));
		chckbxRenderUnknownBlocks.setSelected(prefs.getBoolean("RENDER_UNKNOWN", true));
		chckbxSeparateMat.setSelected(prefs.getBoolean("OBJ_PER_MTL", true));
		chckbxSeparateChunk.setSelected(prefs.getBoolean("OBJ_PER_CHUNK", true));
		chckbxSeparateBlock.setSelected(prefs.getBoolean("OBJ_PER_BLOCK", true));
		chckbxGeoOpt.setSelected(prefs.getBoolean("OPTIMISE_GEO", false));
		chckbxConvertOreTo.setSelected(prefs.getBoolean("CONVERT_ORES", true));
		chckbxSingleMat.setSelected(prefs.getBoolean("SINGLE_MTL", true));
		chckbxMergeVerticies.setSelected(prefs.getBoolean("REMOVE_DUPLICATES", true));
		chckbxSingleTexture.setSelected(prefs.getBoolean("USE_UV_FILE", true));
		textFieldSingleTexUV.setText(prefs.get("UV_FILE", ""));
		chckbxUseLastSaveLoc.setSelected(prefs.getBoolean("USE_LAST_SAVE_LOC", true));
		cboxTexScale.setSelectedItem(""+prefs.getDouble("TEXTURE_SCALE_ID", 1.0));
		chckbxSeparateAlphaTexture.setSelected(prefs.getBoolean("TEXTURE_ALPHA", false));
		chckbxCombineAllTextures.setSelected(prefs.getBoolean("TEXTURE_MERGE", false));
		
		if(!chckbxSingleTexture.isSelected()){
			textFieldSingleTexUV.setEnabled(false); btnBrowseUV.setEnabled(false);
		}
		else{
			textFieldSingleTexUV.setEnabled(true); btnBrowseUV.setEnabled(true);
		}
		
	}
	
	private void saveSettings(){
		
		MainWindow.log("Saving Options");
		
		updateOptions();
		
		prefs.putFloat("DEFAULT_SCALE", Options.scale);
		prefs.putInt("OFFSET_X", Options.offsetX);
		prefs.putInt("OFFSET_Z", Options.offsetZ);
		
		switch(Options.offsetType)
		{
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

		switch(Options.objOverwriteAction)
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

		switch(Options.mtlOverwriteAction)
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
		
		if(!chckbxSingleTexture.isSelected()){
			textFieldSingleTexUV.setEnabled(false); btnBrowseUV.setEnabled(false);
		}
		else{
			textFieldSingleTexUV.setEnabled(true); btnBrowseUV.setEnabled(true);
		}

		prefs.putBoolean("RENDER_SIDES", Options.renderSides);
		prefs.putBoolean("RENDER_BIOMES", Options.renderBiomes);
		prefs.putBoolean("RENDER_ENTITIES", Options.renderEntities);
		prefs.putBoolean("RENDER_UNKNOWN", Options.renderUnknown);
		prefs.putBoolean("OBJ_PER_MTL", Options.objectPerMaterial);
		prefs.putBoolean("OBJ_PER_CHUNK", Options.objectPerChunk);
		prefs.putBoolean("OBJ_PER_BLOCK", Options.objectPerBlock);
		prefs.putBoolean("OPTIMISE_GEO", Options.optimiseGeometry);
		prefs.putBoolean("CONVERT_ORES", Options.convertOres);
		prefs.putBoolean("SINGLE_MTL", Options.singleMaterial);
		prefs.putBoolean("REMOVE_DUPLICATES", Options.removeDuplicates);
		prefs.putBoolean("USE_UV_FILE", Options.useUVFile);
		prefs.putBoolean("USE_LAST_SAVE_LOC", Options.useLastSaveLoc);
		prefs.put("UV_FILE", Options.UVFile.getAbsolutePath());
		
		prefs.putDouble("TEXTURE_SCALE_ID", Options.textureScale);
		prefs.putBoolean("TEXTURE_ALPHA", Options.textureAlpha);
		prefs.putBoolean("TEXTURE_MERGE", Options.textureMerge);
		
	}
	
	private void updateOptions(){
				
		try{
			Options.scale = Float.parseFloat(textFieldMapScale.getText());
		}catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, Messages.getString("OBJExportOptions.SCALE_NUM_ERR"));
			Options.scale =  1.0f;
		}
		
		try{
			String txt=txtX.getText();
			if(!txt.isEmpty() && !txt.equals("-"))
				Options.offsetX = Integer.parseInt(txt);
			txt=txtZ.getText();
			if(!txt.isEmpty() && !txt.equals("-"))
				Options.offsetZ = Integer.parseInt(txt);

		}catch (NumberFormatException e) {
			Log.error("Offset number format error!", e, false);
		}
		
		if(rdbtnCenter.isSelected())
			Options.offsetType = OffsetType.CENTER;
		else if(rdbtnCustom.isSelected())
			Options.offsetType = OffsetType.CUSTOM;
		else
			Options.offsetType = OffsetType.NONE;
		
		
		Options.renderSides = chckbxRenderWorldSides.isSelected();
		Options.renderBiomes = chckbxRenderBiomes.isSelected();
		Options.renderEntities = chckbxRenderEntities.isSelected();
		Options.renderUnknown = chckbxRenderUnknownBlocks.isSelected();
		Options.objectPerMaterial = chckbxSeparateMat.isSelected();
		Options.objectPerChunk = chckbxSeparateChunk.isSelected();
		Options.objectPerBlock = chckbxSeparateBlock.isSelected();
		Options.optimiseGeometry = chckbxGeoOpt.isSelected();
		Options.convertOres = chckbxConvertOreTo.isSelected();
		Options.singleMaterial = chckbxSingleMat.isSelected();
		Options.removeDuplicates = chckbxMergeVerticies.isSelected();
		Options.useUVFile=chckbxSingleTexture.isSelected();
		Options.useLastSaveLoc=chckbxUseLastSaveLoc.isSelected();
		Options.UVFile=new File(textFieldSingleTexUV.getText());
		
		String txt=cboxTexScale.getSelectedItem().toString();
		if(!txt.isEmpty())
		{
			if(txt.endsWith("x")) txt=txt.substring(0,txt.length()-1);
			
			try{
				Options.textureScale=Double.parseDouble(txt);
			}catch (NumberFormatException e) {
				Log.error(Messages.getString("TexsplitDialog.ERR_SCALE"), e,false);
			}
		}
		
		Options.textureAlpha=chckbxSeparateAlphaTexture.isSelected();
		Options.textureMerge=chckbxCombineAllTextures.isSelected();
		
	}
	
	private void ExportTextures(final File destination, final File texturepack, final double texScale, final boolean texMerge, final boolean alphas){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if(texMerge){
						TextureExporter.mergeTextures(destination, texturepack, texScale, alphas,	ExportWindow.this);
						ExportWindow.this.textFieldSingleTexUV.setText(new File(destination, "texture.uv").toString());
					}
					else{
						TextureExporter.splitTextures(destination, texturepack, texScale, alphas,	ExportWindow.this);
					}
				}
				catch (Exception e) {
					Log.error(Messages.getString("TexsplitDialog.ERR_EXP"), e);
				}			
			}
		}).start();								
	}
	
	private void ExportCloudsOBJ(final File destination, final File file, final File texturepack){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					CloudsExporter.exportClouds(destination, texturepack, file.getName());
				}
				catch (Exception e) {
					Log.error(Messages.getString("TexsplitDialog.ERR_EXP"), e);
				}			
			}
		}).start();								
	}
	
	@Override
	public void setProgress(float value) {
		progressBar.setValue((int)(value*100f));		
	}
}