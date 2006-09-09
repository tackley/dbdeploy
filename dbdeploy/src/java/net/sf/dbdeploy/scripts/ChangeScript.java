package net.sf.dbdeploy.scripts;

import java.io.File;

public class ChangeScript implements Comparable {

	private final int id;
	private final File file;
	private final String description;

	public ChangeScript(int id) {
		this(id, "test");
	}
	
	public ChangeScript(int id, File file) {
		this.id = id;
		this.file = file;
		this.description = file.getName();
	}
	
	public ChangeScript(int id, String description) {
		this.id = id;
		this.file = null;
		this.description = description;
	}

	public File getFile() {
		return file;
	}

	public int getId() {
		return id;
	}

	public String getDescription() {
		return description;
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
