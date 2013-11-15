package com.dbdeploy.scripts;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author alanraison
 */
public class DirectoryScannerTest {
    private DirectoryScanner scanner;
    private File testDir;

    @Before
    public void createTestDirectory() throws IOException {
        testDir = File.createTempFile(DirectoryScannerTest.class.getSimpleName(), null);
        testDir.delete();
        testDir.mkdir();
    }

    @After
    public void deleteTestDirectory() throws IOException {
        FileUtils.deleteDirectory(testDir);
    }

    @Test
    public void shouldFindFiles() throws IOException {
        File scriptFile = new File(testDir, "001 test file.sql");
        if (!scriptFile.createNewFile()) {
            throw new IOException("Couldn't create file");
        }
        scanner = new DirectoryScanner("UTF-8", testDir);

        final List<ChangeScript> scripts = scanner.getChangeScripts();

        assertThat(scripts, is(notNullValue()));
        assertThat(scripts.size(), is(1));
        assertThat(scripts.get(0).getId(), is(equalTo(1L)));
    }

    @Test
    public void shouldIgnoreIncorrectlyFormattedFiles() throws IOException {
        File f1 = new File(testDir, "foo.sql");
        File f2 = new File(testDir, "123 go.txt");
        File f3 = new File(testDir, "test 100.sql");

        if (!(f1.createNewFile() &&
            f2.createNewFile() &&
            f3.createNewFile())) {
            throw new IOException("Couldn't create files");
        }

        scanner = new DirectoryScanner("utf-8", testDir);
        final List<ChangeScript> scripts = scanner.getChangeScripts();

        assertThat(scripts, is(notNullValue()));
        assertThat(scripts.size(), equalTo(1));
        assertThat(scripts.get(0).getDescription(), is("123 go.txt"));
    }
}
