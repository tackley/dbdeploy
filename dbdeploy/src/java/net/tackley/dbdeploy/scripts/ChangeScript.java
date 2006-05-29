package net.tackley.dbdeploy.scripts;

import java.io.File;

public class ChangeScript implements Comparable {

	private final int id;
	private final File file;

	public ChangeScript(int id) {
		this(id, null);
	}
	
	public ChangeScript(int id, File file) {
		this.id = id;
		this.file = file;
	}

	public File getFile() {
		return file;
	}

	public int getId() {
		return id;
	}

	public int compareTo(Object o) {
		ChangeScript other = (ChangeScript) o;
		return this.id - other.id;
	}
	
	@Override
	public String toString() {
		if (file != null)
			return "#" + id + ": " + file.getName();
		
		return "#" + id;
	}

}
