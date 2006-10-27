package net.sf.dbdeploy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.dbdeploy.database.DatabaseSchemaVersionManager;
import net.sf.dbdeploy.exceptions.DbDeployException;
import net.sf.dbdeploy.exceptions.SchemaVersionTrackingException;
import net.sf.dbdeploy.scripts.ChangeScript;
import net.sf.dbdeploy.scripts.ChangeScriptRepository;

public class Controller {

	private final DatabaseSchemaVersionManager schemaVersion;
	private final ChangeScriptExecuter changeScriptExecuter;
	private final ChangeScriptRepository changeScriptRepository;
	private final PrettyPrinter prettyPrinter = new PrettyPrinter();
	private List<ChangeScript> doChangeScripts;
	private List<ChangeScript> undoChangeScripts;
	private List<Integer> appliedChanges;
	private List<Integer> changesToApply;

	public Controller(DatabaseSchemaVersionManager schemaVersion, 
			ChangeScriptRepository changeScriptRepository,
			ChangeScriptExecuter changeScriptExecuter) throws DbDeployException {
		this.schemaVersion = schemaVersion;
		this.changeScriptRepository = changeScriptRepository;
		this.changeScriptExecuter = changeScriptExecuter;
		
		doChangeScripts = changeScriptRepository.getOrderedListOfDoChangeScripts();

		appliedChanges = schemaVersion.getAppliedChangeNumbers();

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

				String sql = schemaVersion.generateDoDeltaFragmentHeader(changeScript);
				changeScriptExecuter.applyDeltaFragmentHeaderOrFooterSql(sql);
				
				changeScriptExecuter.applyChangeDoScript(changeScript);

				sql = schemaVersion.generateDoDeltaFragmentFooter(changeScript);
				changeScriptExecuter.applyDeltaFragmentHeaderOrFooterSql(sql);
			}
		}		
	}
	
	private void loopThruUndoScripts(Integer lastChangeToApply) throws IOException {
		
		for (ChangeScript changeScript : undoChangeScripts) {
			final int changeScriptId = changeScript.getId();
			
			if (changeScriptId <= lastChangeToApply && !appliedChanges.contains(changeScriptId)) {				
				changeScriptExecuter.applyChangeUndoScript(changeScript);

				String sql = schemaVersion.generateUndoDeltaFragmentFooter(changeScript);
				changeScriptExecuter.applyDeltaFragmentHeaderOrFooterSql(sql);
			}
		}		
	}
}
