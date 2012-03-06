/*******************************************************************************
 * Copyright (c) 2012
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package org.jmc;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

@SuppressWarnings("serial")
public class OBJExportThread extends JFrame implements Runnable {

	File objfile;
	File savepath;
	Rectangle bounds;
	int ymin;

	JProgressBar progress;

	public OBJExportThread(File objfile, File savepath, Rectangle bounds, int ymin) 
	{		
		super("Export in progress...");		

		this.objfile=objfile;
		this.savepath=savepath;
		this.bounds=bounds;
		this.ymin=ymin;

		setSize(200,50);

		progress=new JProgressBar();
		progress.setStringPainted(true);
		add(progress);
	}

	@Override
	public void run() {

		setVisible(true);

		try {
			FileWriter file=new FileWriter(objfile);
			PrintWriter writer=new PrintWriter(file);

			MTLFile mtl=new MTLFile();

			File mtlfile=new File(objfile.getParent()+"/minecraft.mtl");
			
			if(mtlfile.exists())
			{
				int ret=JOptionPane.showConfirmDialog(MainWindow.main, "This folder already contains the linked minecraft.mtl file. Do you want to overwrite it?");
				if(ret!=JOptionPane.YES_OPTION)
				{
					setVisible(false);
					return;
				}
			}
			
			mtl.saveMTLFile(mtlfile);

			mtl.header(writer);

			int cxs=(int)Math.floor(bounds.x/16.0f);
			int czs=(int)Math.floor(bounds.y/16.0f);
			int cxe=(int)Math.ceil((bounds.x+bounds.width)/16.0f);
			int cze=(int)Math.ceil((bounds.y+bounds.height)/16.0f);
			int oxs=(cxe-cxs)/-2;
			int ozs=(cze-czs)/-2;

			progress.setMaximum((cxe-cxs)*(cze-czs));

			int progress_count=0;
			for(int cx=cxs,ox=oxs; cx<=cxe; cx++,ox++)
				for(int cz=czs,oz=ozs; cz<=cze; cz++,oz++,progress_count++)
				{
					progress.setValue(progress_count);

					Chunk chunk=null;
					try{
						Region region=Region.findRegion(savepath, cx, cz);							
						if(region==null) continue;					
						chunk=region.getChunk(cx, cz);
						if(chunk==null) continue;
					}catch (Exception e) {
						continue;
					}

					OBJFile obj=chunk.getOBJ(mtl,bounds,ymin);

					if(obj==null) continue;

					obj.setOffset(ox*16, -ymin, oz*16);

					obj.append(writer);
				}

			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		setVisible(false);

	}

}
