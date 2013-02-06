package com.dbdeploy.template;

import com.dbdeploy.database.DelimiterType;
import com.dbdeploy.database.QueryStatementSplitter;
import com.dbdeploy.exceptions.UsageException;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to handle template processing
 */
public class TemplateProcessor {
    private final Map<String, Object> model = new HashMap<String, Object>();
    private final Configuration configuration;
    private final Writer writer;

    public TemplateProcessor(Writer writer, String changeLogTableName, QueryStatementSplitter splitter, File templateDirectory) throws IOException {
        this.configuration = new Configuration();
        this.writer = writer;
        this.model.put("delimiter", splitter.getDelimiter());
        this.model.put("separator", splitter.getDelimiterType() == DelimiterType.row ? "\n" : "");
        this.model.put("changeLogTableName", changeLogTableName);

        FileTemplateLoader fileTemplateLoader = createFileTemplateLoader(templateDirectory);
        this.configuration.setTemplateLoader(
                new MultiTemplateLoader(new TemplateLoader[]{
                        fileTemplateLoader,
                        new ClassTemplateLoader(getClass(), "/"),
                }));
    }

    private FileTemplateLoader createFileTemplateLoader(File templateDirectory) throws IOException {
        if (templateDirectory == null) {
            return new FileTemplateLoader();
        } else {
            return new FileTemplateLoader(templateDirectory, true);
        }
    }

    public Object addToModel(String key, Object value) {
        return model.put(key, value);
    }

    public void process(String filename) throws IOException, TemplateException {
        try {
            Template template = configuration.getTemplate(filename);
            template.process(model, this.writer);
        } catch (FileNotFoundException ex) {
            throw new UsageException("Could not find template named " + filename + "\n" +
                    "Check that you have got the name of the database syntax correct.", ex);
        } finally {
            this.writer.close();
        }
    }
}
