package net.sf.dbdeploy.scripts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ChangeScript implements Comparable {

	private final int id;
	private final File file;
	private final String description;
	private static final String UNDO_MARKER = "--//@UNDO";

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

	public String getContent() throws IOException {
		return getFileContents(false);
	}

	public String getUndoContent() throws IOException {
		return getFileContents(true);
	}

	private String getFileContents(boolean onlyAfterUndoMarker) throws IOException {
		StringBuilder content = new StringBuilder();
		boolean foundUndoMarker = false;
		BufferedReader reader = new BufferedReader(new FileReader(file));

		try {
			for (;;) {
				String str = reader.readLine();

				if (str == null)
					break;

				if (str.trim().equals(UNDO_MARKER)) {
					foundUndoMarker = true;
					continue;
				}

				if (foundUndoMarker == onlyAfterUndoMarker) {
					content.append(str);
					content.append('\n');
				}
			}
		} finally {
			reader.close();
		}

		return content.toString();
	}
}
