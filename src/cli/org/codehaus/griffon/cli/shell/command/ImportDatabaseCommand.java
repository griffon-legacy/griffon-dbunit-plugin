package org.codehaus.griffon.cli.shell.command;

import org.codehaus.griffon.cli.shell.AbstractGriffonCommand;
import org.codehaus.griffon.cli.shell.Argument;
import org.codehaus.griffon.cli.shell.Command;
import org.codehaus.griffon.cli.shell.Option;

import java.util.List;

@Command(scope = "dbunit",
    name = "import-database",
    description = "Imports a database in XML format")
public class ImportDatabaseCommand extends AbstractGriffonCommand {
    @Option(name = "--datasource",
        description = "Name of the configured datasource found in DataSource.groovy",
        required = false)
    private String datasource = "default";

    @Option(name = "--format",
        description = "Format to use for importing. Valid values are [flatxml, xml]",
        required = false)
    private String format = "flatxml";

    @Option(name = "--operation",
        description = "Database operation to use. Valid values are [clean-insert, insert, update]",
        required = false)
    private String operation = "clean-insert";

    @Option(name = "--case-sensitive-table-names",
        description = " Whether or not the created dataset should use case sensitive table names",
        required = false)
    private boolean caseSensitiveTableNames;

    @Option(name = "--column-sensing",
        description = "Read in the whole XML into a buffer and dynamically add new columns as they appear.",
        required = false)
    private boolean columnSensing;

    @Option(name = "--dtd-metadata",
        description = "Whether or not DTD metadata is available to parse via a DTD handler.",
        required = false)
    private boolean dtdMetadata;

    @Argument(index = 0,
        name = "files",
        description = "List of files to be imported.",
        multiValued = true,
        required = true)
    private List<String> files;
}