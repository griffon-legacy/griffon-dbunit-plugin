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


import org.dbunit.database.DatabaseConnection
import org.dbunit.database.IDatabaseConnection

import java.sql.Connection
import java.sql.DriverManager

import static griffon.util.ConfigUtils.*

/**
 * @author Andres Almiray
 */

includeTargets << griffonScript('_GriffonCompile')

dbConfig = new ConfigObject()
dataSourceName = ''
locale = Locale.default

target(name: 'setupDbconfig', description: 'Reads the database configuration',
    prehook: null, posthook: null) {
    depends(compile)

    dataSourceName = argsMap.datasource ?: 'default'

    ConfigObject appConfig = loadConfig('Application' as Class)
    locale = getConfigValue(appConfig, "application.locale", Locale.default)
    appConfig = loadConfigWithI18n(locale, createConfigReader(), 'DataSource' as Class, 'DataSource')
    dbConfig = dataSourceName == 'default' ? appConfig.dataSource : appConfig.dataSources[dataSourceName]
}

createConnection = { ConfigObject dbconfig ->
    Class driverClass = Class.forName(dbconfig.driverClassName)
    Connection jdbcConnection = null
    if (dbconfig.username) {
        jdbcConnection = DriverManager.getConnection(
            dbconfig.url,
            dbconfig.username,
            dbconfig.password
        )
    } else {
        jdbcConnection = DriverManager.getConnection(dbconfig.url)
    }

    new DatabaseConnection(jdbcConnection)
}

formatLocale = { Locale l ->
    String formatted = '_' + l.language
    if (l.country) formatted += '_' + l.country
    if (l.variant) formatted += '_' + l.variant
    formatted
}

getBooleanArg = { String argName, boolean defaultValue = false ->
    boolean value = defaultValue
    if (argsMap.containsKey(argName)) {
        value = argsMap[argName]
        if (value instanceof Boolean) {
            value = value.booleanValue()
        } else if (value instanceof CharSequence) {
            value = Boolean.parseBoolean(value.toString().toLowerCase())
        }
    }
    value
}