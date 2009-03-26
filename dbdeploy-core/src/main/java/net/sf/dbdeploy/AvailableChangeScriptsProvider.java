package net.sf.dbdeploy;

import net.sf.dbdeploy.scripts.ChangeScript;

import java.util.List;

public interface AvailableChangeScriptsProvider {
	List<ChangeScript> getAvailableChangeScripts();
}
