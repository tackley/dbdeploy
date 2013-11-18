package com.dbdeploy.scripts;

import com.dbdeploy.exceptions.DbDeployException;

import java.io.*;

public class ChangeScript implements Comparable {

	private final long id;
	private final Supplier<Reader> readerSupplier;
	private final String description;
    private static final String UNDO_MARKER = "--//@UNDO";

	public ChangeScript(long id) {
		this(id, "test");
	}

    public ChangeScript(long id, String description) {
        this.id = id;
        this.readerSupplier = null;
        this.description = description;
    }

	public ChangeScript(long id, Supplier<Reader> readerSupplier, String description) {
		this.id = id;
		this.readerSupplier = readerSupplier;
		this.description = description;
    }

	public long getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public int compareTo(Object o) {
		ChangeScript other = (ChangeScript) o;
		return Long.valueOf(this.id).compareTo(other.id);
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
            BufferedReader bufferedReader = new BufferedReader(readerSupplier.get());

			try {
				for (;;) {
					String str = bufferedReader.readLine();

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
				bufferedReader.close();
			}

			return content.toString();
		} catch (Exception e) {
			throw new DbDeployException("Failed to read change script file", e);
		}
    }
}
