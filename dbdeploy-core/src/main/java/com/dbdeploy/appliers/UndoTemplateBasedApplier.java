package com.dbdeploy.appliers;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public class UndoTemplateBasedApplier extends TemplateBasedApplier {
	public UndoTemplateBasedApplier(Writer writer, String syntax,
	                                String changeLogTableName, File templateDirectory) throws IOException {
		super(writer, syntax, changeLogTableName, templateDirectory);
	}

	@Override
	protected String getTemplateQualifier() {
		return "undo";
	}
}
