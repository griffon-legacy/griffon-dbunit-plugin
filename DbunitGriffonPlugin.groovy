/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * @author Andres Almiray
 */
class DbunitGriffonPlugin {
    // the plugin version
    String version = '0.2'
    // the version or versions of Griffon the plugin is designed for
    String griffonVersion = '1.2.0 > *'
    // the other plugins this plugin depends on
    Map dependsOn = [datasource: '1.1.0']
    // resources that are included in plugin packaging
    List pluginIncludes = []
    // the plugin license
    String license = 'Apache Software License 2.0'
    // Toolkit compatibility. No value means compatible with all
    // Valid values are: swing, javafx, swt, pivot, gtk
    List toolkits = []
    // Platform compatibility. No value means compatible with all
    // Valid values are:
    // linux, linux64, windows, windows64, macosx, macosx64, solaris
    List platforms = []
    // URL where documentation can be found
    String documentation = ''
    // URL where source can be found
    String source = 'https://github.com/griffon/griffon-dbunit-plugin'

    List authors = [
        [
            name: 'Andres Almiray',
            email: 'aalmiray@yahoo.com'
        ]
    ]
    String title = 'DataSet import/export via DbUnit'
    // accepts Markdown syntax. See http://daringfireball.net/projects/markdown/ for details
    String description = '''
This plugin provides DataSet import/export capabilities based on [DbUnit][1]'s
IDataSet abstraction. DataSets may be imported/export at buildtime using a pair
of scripts and/or loaded at runtime using `DatabaseService`

Usage
-----

The service `DatabaseService` exposes the following methods

### Import ###
 * void importDataset(File source, Map options)
 * void importDataset(URL source, Map options)
 * void importDataset(InputStream source, Map options)
 * void importDataset(Reader source, Map options)
 * void importDataset(String source, Map options)

Where options may be

 * __datasource__: name of the datasource to use. Defaults to `default.
 * __format__: input format. Valid values are `flatxml`, `xml`. Defaults to `flatxml`.
 * __operation__: Database operation to use. Valid values are `clean-insert`,
   `insert`, `update`. Defaults to `clean-insert`.
 * __caseSensitiveTableNames__: Whether or not the created dataset should use case
   sensitive table names. Used only when `format = flatxml`.
 * __columnSensing__: Read in the whole XML into a buffer and dynamically add new
   columns as they appear. Used only when `format = flatxml`.
 * __dtdMetadata__: Whether or not DTD metadata is available to parse via a DTD
   handler. Used only when `format = flatxml`.

### Export ###

 * File exportDataset(File directory, Map options)

Where options may be

 * __datasource__: name of the datasource to use. Defaults to 'default'.
 * __format__: input format. Valid values are `flatxml`, `xml`. Defaults to `flatxml`.

The exported dataset will be placed inside `directory` in a file whose name follows
this convention

    database-<datasource>[locale].[extension]

Where `locale` represents the fully formatted Locale, i.e, `_[language]_[country]_[variant]`,
unless the Locale is equal to `Locale.default`, in which case the value will be
left empty. The value of `extension` is either `flat.xml` or `xml`.

Configuration
-------------

All scripts and `databaseService` rely on the datasource configuration (`DataSource.groovy`)
provided by the [datasource][2] plugin.

Scripts
-------

* **export-database**: Exports a database in XML format. Supports the same options
  for exporting as `databaseService`.
* **import-database**: Imports a database in XML format Supports the same options
  for importing as `databaseService`. Multiple files may be imported at the same
  time, however all must conform to the selected format.


[1]: http://dbunit.sourceforge.net/
[2]: /plugin/datasource
'''
}
