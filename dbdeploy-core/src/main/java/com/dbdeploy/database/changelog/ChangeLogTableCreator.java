package com.dbdeploy.database.changelog;

import com.dbdeploy.database.QueryStatementSplitter;
import com.dbdeploy.template.TemplateProcessor;

import java.io.*;

/**
 * Creates a changelog table
 */
public class ChangeLogTableCreator {
    private final String dbms;
    private final TemplateProcessor templateProcessor;
    private final StringWriter stringWriter;
    public ChangeLogTableCreator(String changeLogTableName, String dbms, QueryStatementSplitter splitter, File templatedir) throws IOException {
        this.dbms = dbms;
        stringWriter = new StringWriter();
        templateProcessor = new TemplateProcessor(stringWriter, changeLogTableName, splitter, templatedir);
    }

    public String create() {
        try {
            templateProcessor.process(dbms + "_changelog.ftl");
            return stringWriter.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
