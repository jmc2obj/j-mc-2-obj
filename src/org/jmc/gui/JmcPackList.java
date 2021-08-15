package org.jmc.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListModel;

import com.google.gson.Gson;

public class JmcPackList extends JList<File> {
	private static final long serialVersionUID = 3278592346405844376L;
	
	public JmcPackList() {
		super(new DefaultListModel<File>());
	}
	
	@Override
	public DefaultListModel<File> getModel() {
		return (DefaultListModel<File>) super.getModel();
	}
	
	@Override
	public void setModel(ListModel<File> model) {
		if (!(model instanceof DefaultListModel)) {
			throw new IllegalArgumentException("Model must be a PackListModel!");
		}
		super.setModel(model);
	}
	
	public boolean removeSelected() {
		int selected = getSelectedIndex();
		if (selected == -1) return false;
		getModel().remove(selected);
		return true;
	}
	
	public boolean moveSelectedUp() {
		int selected = getSelectedIndex();
		if (selected <= 0) return false;
		DefaultListModel<File> model = getModel();
		File elem = model.remove(selected);
		model.add(--selected, elem);
		setSelectedIndex(selected);
		return true;
	}
	
	public boolean moveSelectedDown() {
		int selected = getSelectedIndex();
		DefaultListModel<File> model = getModel();
		if (selected == -1 || selected >= model.size()-1) return false;
		File elem = model.remove(selected);
		model.add(++selected, elem);
		setSelectedIndex(selected);
		return true;
	}
	
	public String getPrefString() {
		ArrayList<String> files = new ArrayList<>();
		for (File file : getList()) {
			files.add(file.getAbsolutePath());
		}
		return new Gson().toJson(files);
	}
	
	public void loadPrefString(String str) {
		DefaultListModel<File> model = getModel();
		model.clear();
		for (String path : new Gson().fromJson(str, String[].class)) {
			model.addElement(new File(path));
		}
	}

	public List<File> getList() {
		ArrayList<File> files = new ArrayList<>();
		for (Object file : getModel().toArray()) {
			files.add((File)file);
		}
		return Collections.unmodifiableList(files);
	}
	
	public void reset() {
		getModel().clear();
	}
}
