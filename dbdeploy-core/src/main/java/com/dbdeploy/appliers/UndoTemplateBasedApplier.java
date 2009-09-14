package com.dbdeploy.appliers;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class UndoTemplateBasedApplier extends TemplateBasedApplier {
	public UndoTemplateBasedApplier(OutputStream outputStream, String syntax,
	                                String changeLogTableName, File templateDirectory) throws IOException {
		super(outputStream, syntax, changeLogTableName, templateDirectory);
	}

	@Override
	protected String getTemplateQualifier() {
		return "undo";
	}
}
