package com.dbdeploy.scripts;

import java.util.List;

/**
 * @author alanraison
 */
public interface Scanner {
    /**
     * Get the available change scripts
     * @return the available change scripts with open file handles
     */
    List<ChangeScript> getChangeScripts();
}
