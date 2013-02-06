package com.dbdeploy.appliers;

import com.dbdeploy.database.QueryStatementSplitter;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

public class UndoTemplateBasedApplier extends TemplateBasedApplier {
	public UndoTemplateBasedApplier(Writer writer, String syntax,
									String changeLogTableName, QueryStatementSplitter splitter, File templateDirectory) throws IOException {
		super(writer, syntax, changeLogTableName, null, splitter, templateDirectory);
	}

	@Override
	protected String getTemplateQualifier() {
		return "undo";
	}
}
