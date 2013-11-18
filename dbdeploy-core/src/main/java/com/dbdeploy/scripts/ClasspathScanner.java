package com.dbdeploy.scripts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import com.dbdeploy.exceptions.DbDeployException;

/**
 * @author alanraison
 */
public class ClasspathScanner implements Scanner {
    private final String packagePath;
    private final String encoding;
    private final Pattern fullScriptPattern;

    public ClasspathScanner(String scriptPackage, String encoding) {
        this.packagePath = scriptPackage.replace('.','/');
        this.encoding = encoding;
        this.fullScriptPattern = Pattern.compile("^" + packagePath + "/\\d+[^/]*$");
    }

    public List<ChangeScript> getChangeScripts() {
        final List<ChangeScript> scripts = new ArrayList<ChangeScript>();
        try {
            final Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(packagePath);

            while (resources.hasMoreElements()) {
                URL resourceUrl = resources.nextElement();

                if (resourceUrl.getProtocol().equals("jar")) {
                    scripts.addAll(addFromJar((JarURLConnection) resourceUrl.openConnection()));

                } else if (resourceUrl.getProtocol().equals("file")) {
                    scripts.addAll(addFromDirectory(new File(resourceUrl.toURI())));
                }
            }
        } catch (IOException e) {
            throw new DbDeployException(e);
        } catch (URISyntaxException e) {
            throw new DbDeployException(e);
        }
        return scripts;
    }

    private List<ChangeScript> addFromDirectory(final File directory) {
        final List<ChangeScript> scripts = new ArrayList<ChangeScript>();
        File[] scriptFiles = directory.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return FilenameParser.PATTERN.matcher(name).matches();
            }
        });

        for (final File script : scriptFiles) {
            scripts.add(new ChangeScript(FilenameParser.extractIdFromFilename(script.getName()), supplyFileContent(script), script.getName()));
        }
        return scripts;
    }

    private List<ChangeScript> addFromJar(JarURLConnection jarURLConnection) throws IOException {
        final List<ChangeScript> scripts = new ArrayList<ChangeScript>();
        final JarFile jarFile = jarURLConnection.getJarFile();
        final Enumeration<JarEntry> entries = jarFile.entries();

        while (entries.hasMoreElements()) {
            final JarEntry jarEntry = entries.nextElement();
            if (!jarEntry.isDirectory()) {
                final String path = jarEntry.getName();

                if (fullScriptPattern.matcher(path).matches()) {
                    String name = path.substring(path.lastIndexOf('/') + 1);
                    scripts.add(new ChangeScript(FilenameParser.extractIdFromFilename(name), supplyJarContent(jarFile, jarEntry), name));
                }
            }
        }
        return scripts;
    }

    private Supplier<Reader> supplyFileContent(final File script) {
        return new Supplier<Reader>() {
            public Reader get() throws Exception {
                return new InputStreamReader(new FileInputStream(script), encoding);
            }
        };
    }

    private Supplier<Reader> supplyJarContent(final JarFile jar, final JarEntry entry) {
        return new Supplier<Reader>() {
            public Reader get() throws Exception {
                return new InputStreamReader(jar.getInputStream(entry), encoding);
            }
        };
    }
}
