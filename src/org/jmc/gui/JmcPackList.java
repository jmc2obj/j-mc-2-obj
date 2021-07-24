package org.jmc.gui;

import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListModel;

import com.google.gson.Gson;

public class JmcPackList extends JList<String> {
	private static final long serialVersionUID = 3278592346405844376L;
	
	public JmcPackList() {
		super(new DefaultListModel<String>());
	}
	
	@Override
	public DefaultListModel<String> getModel() {
		return (DefaultListModel<String>) super.getModel();
	}
	
	@Override
	public void setModel(ListModel<String> model) {
		if (!(model instanceof DefaultListModel)) {
			throw new IllegalArgumentException("Model must be a PackListModel!");
		}
		super.setModel(model);
	}

	public void removeSelected() {
		int selected = getSelectedIndex();
		if (selected == -1) return;
		getModel().remove(selected);
	}

	public void moveSelectedUp() {
		int selected = getSelectedIndex();
		if (selected <= 0) return;
		DefaultListModel<String> model = getModel();
		String elem = model.remove(selected);
		model.add(--selected, elem);
		setSelectedIndex(selected);
	}

	public void moveSelectedDown() {
		int selected = getSelectedIndex();
		DefaultListModel<String> model = getModel();
		if (selected == -1 || selected >= model.size()-1) return;
		String elem = model.remove(selected);
		model.add(++selected, elem);
		setSelectedIndex(selected);
	}
	
	public String getPrefString() {
		return new Gson().toJson(getModel().toArray());
	}
	
	public void loadPrefString(String str) {
		DefaultListModel<String> model = getModel();
		model.clear();
		model.addAll(Arrays.asList(new Gson().fromJson(str, String[].class)));
	}

	public void reset() {
		getModel().clear();
	}
}
