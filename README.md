# arquillian-testng-suite
Arquillian example with TestNG and [suite extension](https://github.com/ingwarsw/arquillian-suite-extension) authored by @ingwarsw.
Also added automatic generation of TestNG suite XML file by `TestSuiteGenerator`helper class.

**Hint:** ask Arquillian to save archive to disk 
````
archive.as(ZipExporter.class).exportTo(new File("./target/myPackage.jar"), true); 
return archive;
````
