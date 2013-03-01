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
import org.dbunit.dataset.xml.FlatXmlDataSet
import org.dbunit.dataset.xml.XmlDataSet

/**
 * @author Andres Almiray
 */

includePluginScript('dbunit', '_DbunitCommon')


target(name: 'exportDatabase', description: 'Exports a database in XML format',
    prehook: null, posthook: null) {
    depends(setupDbconfig)

    if (!argsMap.format) argsMap.format = 'flatxml'

    File destinationDir = new File("${projectTargetDir}/dbunit")
    ant.mkdir(dir: destinationDir)

    String suffix = locale != Locale.default ? formatLocale(locale) : ''
    File output = new File("${destinationDir}/database-${dataSourceName}${suffix}.xml")

    IDatabaseConnection connection = createConnection(dbConfig)

    switch (argsMap.format) {
        case 'flatxml':
            output = new File("${destinationDir}/database-${dataSourceName}${suffix}.flat.xml")
            FlatXmlDataSet.write(connection.createDataSet(), new FileOutputStream(output))
            break
        case 'xml':
            XmlDataSet.write(connection.createDataSet(), new FileOutputStream(output))
            break
        default:
            event 'StatusError', ["Format ${argsMap.format} is not supported"]
            exit 1
    }

    event 'StatusFinal', ["Exported database to ${output}"]
}

setDefaultTarget('exportDatabase')
