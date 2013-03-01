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

package griffon.plugins.dbunit

import griffon.util.ApplicationHolder
import griffon.plugins.datasource.DataSourceHolder
import org.dbunit.database.DatabaseDataSourceConnection
import org.dbunit.database.IDatabaseConnection
import org.dbunit.dataset.IDataSet
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.dbunit.dataset.xml.XmlDataSet
import org.dbunit.operation.DatabaseOperation

import javax.sql.DataSource

import static griffon.util.GriffonNameUtils.isBlank

/**
 * @author Andres Almiray
 */
class DatabaseProcessor {
    void importDataset(File source, Map options = [:]) {
        if (!source) {
            throw new IllegalArgumentException("Invalid input, source is null.")
        }
        if (!source.exists() || source.directory) {
            throw new IllegalArgumentException("Invalid input, file is non-existent or a directory [$source].")
        }
        doImportDataSet(source, normalizeOptions(options))
    }

    void importDataset(Reader source, Map options = [:]) {
        if (!source) {
            throw new IllegalArgumentException("Invalid input, source is null.")
        }
        doImportDataSet(source, normalizeOptions(options))
    }

    void importDataset(URL source, Map options = [:]) {
        if (!source) {
            throw new IllegalArgumentException("Invalid input, source is null.")
        }
        doImportDataSet(source, normalizeOptions(options))
    }

    void importDataset(InputStream source, Map options = [:]) {
        if (!source) {
            throw new IllegalArgumentException("Invalid input, source is null.")
        }
        doImportDataSet(source, normalizeOptions(options))
    }

    void importDataset(String source, Map options = [:]) {
        if (isBlank(source)) {
            throw new IllegalArgumentException("Invalid input, source is null or empty.")
        }
        doImportDataSet(new StringReader(source), normalizeOptions(options))
    }

    File exportDataset(File directory, Map options = [:]) {
        if (!directory) {
            throw new IllegalArgumentException("Invalid input, directory is null.")
        }
        if (!directory.directory) {
            throw new IllegalArgumentException("Invalid input, directory is not a directory [$directory].")
        }
        doExportDataSet(directory, normalizeOptions(options))
    }

    private static void doImportDataSet(source, Map options) {
        IDataSet dataset = null
        switch (options.format) {
            case 'flatxml':
                dataset = new FlatXmlDataSetBuilder()
                    .setCaseSensitiveTableNames(options.caseSensitiveTableNames)
                    .setColumnSensing(options.columnSensing)
                    .setDtdMetadata(options.dtdMetadata)
                    .build(source)
                break
            case 'xml':
                switch (source) {
                    case File: source = new FileReader(source); break
                    case URL: source = source.openStream(); break
                }
                dataset = new XmlDataSet(source)
                break
        }

        IDatabaseConnection connection = createConnection(options)
        options.operation.execute(connection, dataset)
        connection.close()
    }

    private static File doExportDataSet(File directory, Map options) {
        Locale locale = ApplicationHolder.application?.locale ?: Locale.default
        String suffix = locale != Locale.default ? formatLocale(locale) : ''
        File output = new File("${directory}/database-${options.datasource}${suffix}.xml")

        IDatabaseConnection connection = createConnection(options)

        switch (options.format) {
            case 'flatxml':
                output = new File("${directory}/database-${options.datasource}${suffix}.flat.xml")
                FlatXmlDataSet.write(connection.createDataSet(), new FileOutputStream(output))
                break
            case 'xml':
                XmlDataSet.write(connection.createDataSet(), new FileOutputStream(output))
                break
        }

        output
    }

    private static Map normalizeOptions(Map options) {
        Map normalized = [
            format: options.format ?: 'flatxml',
            caseSensitiveTableNames: options.caseSensitiveTableNames ?: false,
            columnSensing: options.columnSensing ?: false,
            dtdMetadata: options.dtdMetadata ?: false,
            operation: options.operation ?: 'clean-insert',
            datasource: options.datasource ?: 'default'
        ]

        if (!(normalized.format in ['flatxml', 'xml'])) {
            throw new IllegalArgumentException("Format ${normalized.format} is not supported")
        }

        switch (normalized.operation) {
            case 'clean-insert':
                normalized.operation = DatabaseOperation.CLEAN_INSERT
                break
            case 'insert':
                normalized.operation = DatabaseOperation.INSERT
                break
            case 'update':
                normalized.operation = DatabaseOperation.UPDATE
                break
            default:
                throw new IllegalArgumentException("Unrecognized operation [${normalized.operation}]. Valid values are clean-insert, insert, update.")
        }

        normalized.caseSensitiveTableNames = normalized.caseSensitiveTableNames as boolean
        normalized.columnSensing = normalized.columnSensing as boolean
        normalized.dtdMetadata = normalized.dtdMetadata as boolean

        normalized
    }

    private static IDatabaseConnection createConnection(Map options) {
        DataSource dataSource = DataSourceHolder.instance.fetchDataSource(options.datasource)
        IDatabaseConnection connection = new DatabaseDataSourceConnection(dataSource)
        connection
    }

    public static String formatLocale(Locale locale) {
        String formatted = '_' + locale.language
        if (locale.country) formatted += '_' + locale.country
        if (locale.variant) formatted += '_' + locale.variant
        formatted
    }
}
