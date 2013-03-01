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


import org.dbunit.database.IDatabaseConnection
import org.dbunit.dataset.IDataSet
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.dbunit.dataset.xml.XmlDataSet
import org.dbunit.operation.DatabaseOperation

import static org.dbunit.operation.DatabaseOperation.*

/**
 * @author Andres Almiray
 */

includePluginScript('dbunit', '_DbunitCommon')


target(name: 'importDatabase', description: 'Imports a database in XML format',
    prehook: null, posthook: null) {
    depends(setupDbconfig)

    if (!argsMap.params) {
        event 'StatusError', ["No input files were given as parameter"]
        exit 1
    }

    if (!argsMap.format) argsMap.format = 'flatxml'

    DatabaseOperation operation = CLEAN_INSERT
    if (!argsMap.operation) argsMap.operation = 'clean-insert'
    switch (argsMap.operation) {
        case 'clean-insert':
            operation = CLEAN_INSERT
            break
        case 'insert':
            operation = INSERT
            break
        case 'update':
            operation = UPDATE
            break
        case CLEAN_INSERT:
        case INSERT:
        case UPDATE:
            // OK
            break
        default:
            event 'StatusError', ["Unrecognized operation [${argsMap.operation}]. Valid values are clean-insert, insert, update."]
            exit 1
    }

    if (!(argsMap.format in ['flatxml', 'xml'])) {
        event 'StatusError', ["Format ${argsMap.format} is not supported"]
        exit 1
    }

    IDataSet dataset = null
    for (String filename : argsMap.params) {
        File input = new File(filename)

        if (!input.exists() || input.directory) {
            event 'StatusUpdate', ["File ${input} does not exist or is a directory"]
            continue
        }

        switch (argsMap.format) {
            case 'flatxml':
                dataset = new FlatXmlDataSetBuilder()
                    .setCaseSensitiveTableNames(getBooleanArg('case-sensitive-table-names'))
                    .setColumnSensing(getBooleanArg('column-sensing'))
                    .setDtdMetadata(getBooleanArg('dtd-metadata'))
                    .build(input)
                break
            case 'xml':
                dataset = new XmlDataSet(new FileReader(input))
                break
        }

        IDatabaseConnection connection = createConnection(dbConfig)
        operation.execute(connection, dataset)
        connection.close()

        event 'StatusFinal', ["Imported database from ${input} using [${argsMap.operation}]"]
    }
}

setDefaultTarget('importDatabase')
