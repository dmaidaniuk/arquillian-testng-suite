package org.arquillian.example.testng;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

public class TestSuiteGenerator {

    public static void main(String[] args) throws IOException {
        execute(args[0], args[1], args[2]);
    }

    public static void execute(String destinationPath, String testsPath, String suffixFileFilter) throws IOException {
        File baseDir = new File(testsPath);
        Collection<File> integrationTests = FileUtils.listFiles(
                baseDir,
                new SuffixFileFilter(suffixFileFilter),
                TrueFileFilter.INSTANCE);
        URI baseUri = baseDir.toURI();

        FileWriter writer = new FileWriter(destinationPath);
        writer.write(
                "<!DOCTYPE suite SYSTEM \"http://testng.org/testng-1.0.dtd\" >\n" +
                "<suite name=\"IT-Suite\" verbose=\"1\">\n" +
                "  <test name=\"All-tests\">\n" +
                "    <classes>\n");
        for (File integrationTest : integrationTests) {
            String path = baseUri.relativize(integrationTest.toURI()).getPath();
            path = path.replace("/", ".").replace(".java", "");
            writer.write("      <class name=\"" + path + "\" />\n");
        }
        writer.write(
                "    </classes>\n" +
                "  </test>\n" +
                "</suite>");
        writer.close();
    }
}
