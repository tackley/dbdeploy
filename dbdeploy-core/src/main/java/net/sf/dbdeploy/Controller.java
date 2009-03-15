package net.sf.dbdeploy;

import net.sf.dbdeploy.database.changelog.DatabaseSchemaVersionManager;
import net.sf.dbdeploy.exceptions.DbDeployException;
import net.sf.dbdeploy.scripts.ChangeScript;
import net.sf.dbdeploy.scripts.ChangeScriptRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Controller {

	private final DatabaseSchemaVersionManager schemaVersionManager;
	private final ConsolidatedChangeScriptWriter changeScriptWriter;
	private final ChangeScriptRepository changeScriptRepository;
	private final PrettyPrinter prettyPrinter = new PrettyPrinter();
	private List<ChangeScript> doChangeScripts;
	private List<ChangeScript> undoChangeScripts;
	private List<Integer> appliedChanges;
	private List<Integer> changesToApply;

	public Controller(DatabaseSchemaVersionManager schemaVersionManager,
	                  ChangeScriptRepository changeScriptRepository,
	                  ConsolidatedChangeScriptWriter changeScriptWriter) throws DbDeployException {
		this.schemaVersionManager = schemaVersionManager;
		this.changeScriptRepository = changeScriptRepository;
		this.changeScriptWriter = changeScriptWriter;
		this.doChangeScripts = changeScriptRepository.getOrderedListOfDoChangeScripts();
		this.appliedChanges = schemaVersionManager.getAppliedChangeNumbers();
	}

	public void processDoChangeScripts(Integer lastChangeToApply) throws DbDeployException, IOException {
		if (lastChangeToApply != Integer.MAX_VALUE) {
			info("Only applying changes up and including change script #" + lastChangeToApply);
		}

		info("Changes currently applied to database:\n  " + prettyPrinter.format(appliedChanges));
		info("Scripts available:\n  " + prettyPrinter.formatChangeScriptList(doChangeScripts));

		changesToApply = new ArrayList<Integer>();

		loopThruDoScripts(lastChangeToApply);

		info("To be applied:\n  " + prettyPrinter.format(changesToApply));
	}

	public void processUndoChangeScripts(Integer lastChangeToApply) throws DbDeployException, IOException {
		undoChangeScripts = changeScriptRepository.getOrderedListOfUndoChangeScripts();
		loopThruUndoScripts(lastChangeToApply);
	}

	private void info(String string) {
		System.err.println(string);
	}

	private void loopThruDoScripts(Integer lastChangeToApply) throws IOException {

		for (ChangeScript changeScript : doChangeScripts) {
			final int changeScriptId = changeScript.getId();

			if (changeScriptId <= lastChangeToApply && !appliedChanges.contains(changeScriptId)) {
				changesToApply.add(changeScriptId);

				String sql = schemaVersionManager.generateDoDeltaFragmentHeader(changeScript);
				changeScriptWriter.applyDeltaFragmentHeaderOrFooterSql(sql);

				changeScriptWriter.applyChangeDoScript(changeScript);

				sql = schemaVersionManager.generateDoDeltaFragmentFooter(changeScript);
				changeScriptWriter.applyDeltaFragmentHeaderOrFooterSql(sql);
			}
		}
	}

	private void loopThruUndoScripts(Integer lastChangeToApply) throws IOException {

		for (ChangeScript changeScript : undoChangeScripts) {
			final int changeScriptId = changeScript.getId();

			if (changeScriptId <= lastChangeToApply && !appliedChanges.contains(changeScriptId)) {
				changeScriptWriter.applyChangeUndoScript(changeScript);

				String sql = schemaVersionManager.generateUndoDeltaFragmentFooter(changeScript);
				changeScriptWriter.applyDeltaFragmentHeaderOrFooterSql(sql);
			}
		}
	}
}
