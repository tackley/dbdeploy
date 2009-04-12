package com.dbdeploy.scripts;

import com.dbdeploy.exceptions.DbDeployException;

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
		return "#" + id + ": " + description;
	}

	public String getContent() {
		return getFileContents(false);
	}

	public String getUndoContent() {
		return getFileContents(true);
	}

	private String getFileContents(boolean onlyAfterUndoMarker) {
		try {
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
		} catch (IOException e) {
			throw new DbDeployException("Failed to read change script file", e);
		}
	}
}
