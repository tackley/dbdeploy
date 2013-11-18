package com.dbdeploy.scripts;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.junit.Test;

/**
 * @author alanraison
 */
public class ClasspathScannerTest {
    @Test
    public void shouldFindScriptsInPlainDirectoryPackage() {
        ClasspathScanner scanner = new ClasspathScanner("com.dbdeploy.test.directory", null);

        final List<ChangeScript> scripts = scanner.getChangeScripts();

        assertThat(scripts.size(), is(2));
        assertThat(scripts.get(0).getDescription(), is(equalTo("001 Change 1.sql")));
        assertThat(scripts.get(1).getDescription(), is(equalTo("002 second change.ddl")));
    }

    @Test
    public void shouldFindScriptsInJarDependencyPackage() throws MalformedURLException, ClassNotFoundException {
        URLClassLoader cl = new URLClassLoader(new URL[]{
                new File("C:\\Users\\hisg029\\projects\\dbdeploy\\dbdeploy-core\\src\\test\\resources\\dbdeploy-test.jar").toURI().toURL()
            },
            Thread.currentThread().getContextClassLoader());
        Thread.currentThread().setContextClassLoader(cl);
        //cl.loadClass("com.dbdeploy.scripts.ClasspathScanner");
        ClasspathScanner scanner = new ClasspathScanner("com.dbdeploy.test.jar", null);

        final List<ChangeScript> scripts = scanner.getChangeScripts();

        assertThat(scripts.size(), is(2));
        assertThat(scripts.get(0).getDescription(), is(equalTo("001 change one.sql")));
        assertThat(scripts.get(1).getDescription(), is(equalTo("002 2nd Change.ddl")));
    }
}
