package net.sf.dbdeploy;

import java.util.List;

public interface AppliedChangesProvider {
	List<Integer> getAppliedChanges();
}
