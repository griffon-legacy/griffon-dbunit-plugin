package org.codehaus.griffon.cli.shell.command;

import org.codehaus.griffon.cli.shell.AbstractGriffonCommand;
import org.codehaus.griffon.cli.shell.Command;
import org.codehaus.griffon.cli.shell.Option;

@Command(scope = "dbunit",
    name = "export-database",
    description = "Exports a database in XML format")
public class ExportDatabaseCommand extends AbstractGriffonCommand {
    @Option(name = "--datasource",
        description = "Name of the configured datasource found in DataSource.groovy",
        required = false)
    private String datasource = "default";

    @Option(name = "--format",
        description = "Format to use for exporting. Valid values are [flatxml, xml]",
        required = false)
    private String format = "flatxml";
}