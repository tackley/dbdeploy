package com.dbdeploy.appliers;

import com.dbdeploy.ChangeScriptApplier;
import com.dbdeploy.database.QueryStatementSplitter;
import com.dbdeploy.database.changelog.ChangeLogTableCreator;
import com.dbdeploy.scripts.ChangeScript;
import com.dbdeploy.template.TemplateProcessor;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.List;


public class TemplateBasedApplier implements ChangeScriptApplier {
    private final Writer writer;
    private final String syntax;
    private final ChangeLogTableCreator tableCreator;
    private final TemplateProcessor templateProcessor;

	public TemplateBasedApplier(Writer writer, String syntax, String changeLogTableName, ChangeLogTableCreator tableCreator, QueryStatementSplitter splitter, File templateDirectory) throws IOException {
        this.writer = writer;
        this.syntax = syntax;
        this.tableCreator = tableCreator;
        this.templateProcessor = new TemplateProcessor(writer, changeLogTableName, splitter, templateDirectory);
	}


    public void createChangeLogTable(String databaseType) {
        String filename = syntax + "_changelog.ftl";

        try {
            templateProcessor.process(filename);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void apply(List<ChangeScript> changeScripts) {
		try {
            if (tableCreator != null) {
                writer.write(tableCreator.create());
            }

            String filename = syntax + "_" + getTemplateQualifier() + ".ftl";
            templateProcessor.addToModel("scripts", changeScripts);

			templateProcessor.process(filename);
		} catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TemplateException e) {
			throw new RuntimeException(e);
		}
	}

	protected String getTemplateQualifier() {
		return "apply";
	}

}
