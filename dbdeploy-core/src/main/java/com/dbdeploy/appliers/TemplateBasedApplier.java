package com.dbdeploy.appliers;

import com.dbdeploy.ChangeScriptApplier;
import com.dbdeploy.exceptions.UsageException;
import com.dbdeploy.scripts.ChangeScript;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TemplateBasedApplier implements ChangeScriptApplier {
	private Configuration configuration;
	private Writer writer;
	private String syntax;
	private String changeLogTableName;

	public TemplateBasedApplier(Writer writer, String syntax, String changeLogTableName, File templateDirectory) throws IOException {
		this.syntax = syntax;
		this.changeLogTableName = changeLogTableName;
		this.writer = writer;
		this.configuration = new Configuration();

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

	public void apply(List<ChangeScript> changeScripts) {
		String filename = syntax + "_" + getTemplateQualifier() + ".ftl";

		try {
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("scripts", changeScripts);
			model.put("changeLogTableName", changeLogTableName);

			try {
				Template template = configuration.getTemplate(filename);
				template.process(model, writer);
			} finally {
				writer.close();
			}
		} catch (FileNotFoundException ex) {
			throw new UsageException("Could not find template named " + filename + "\n" +
					"Check that you have got the name of the database syntax correct.", ex);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String getTemplateQualifier() {
		return "apply";
	}

}
