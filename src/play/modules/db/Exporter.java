/**
 *
 * Copyright 2010, Nicolas Leroux.
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * User: ${user}
 * Date: ${date}
 *
 */
package play.modules.db;

import org.hibernate.annotations.common.util.ReflectHelper;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import play.Play;

import javax.persistence.Entity;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

public class Exporter {


    public static void main(String[] args) throws Exception {

        File root = new File(System.getProperty("application.path"));
        Play.init(root, System.getProperty("play.id", ""));
        List<Class> entities = Play.classloader.getAnnotatedClasses(Entity.class);
        AnnotationConfiguration cfg = new AnnotationConfiguration();
        cfg.setProperty("hibernate.hbm2ddl.auto", "create");
        for (Class _class : entities) {
            cfg.addAnnotatedClass(_class);
        }
        
        Thread.currentThread().setContextClassLoader(Play.classloader);
        final String dialect = Play.configuration.getProperty("jpa.dialect");
        if (dialect != null)
            cfg.setProperty("hibernate.dialect", dialect);

        final String driver = Play.configuration.getProperty("db.driver");
        if (driver != null)
            cfg.setProperty("hibernate.connection.driver_class", driver);

        final String user = Play.configuration.getProperty("db.user");
        if (user != null)
            cfg.setProperty("hibernate.connection.username", user);

        final String password = Play.configuration.getProperty("db.pass");
        if (password != null)
            cfg.setProperty("hibernate.connection.password", password);

        final String url = Play.configuration.getProperty("db.url");
        if (url != null)
            cfg.setProperty("hibernate.connection.url", url);

        boolean script = true;
        boolean drop = false;
        boolean create = false;
        boolean halt = false;
        boolean export = false;
        String outFile = null;
        String importFile = "/import.sql";
        String propFile = null;
        boolean format = true;
        String delim = ";";

        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("--")) {
                if (args[i].equals("--quiet")) {
                    script = false;
                } else if (args[i].equals("--drop")) {
                    drop = true;
                } else if (args[i].equals("--create")) {
                    create = true;
                } else if (args[i].equals("--haltonerror")) {
                    halt = true;
                } else if (args[i].equals("--export")) {
                    export = true;
                } else if (args[i].startsWith("--output=")) {
                    outFile = args[i].substring(9);
                } else if (args[i].startsWith("--import=")) {
                    importFile = args[i].substring(9);
                } else if (args[i].startsWith("--properties=")) {
                    propFile = args[i].substring(13);
                } else if (args[i].equals("--noformat")) {
                    format = false;
                } else if (args[i].startsWith("--delimiter=")) {
                    delim = args[i].substring(12);
                } else if (args[i].startsWith("--config=")) {
                    cfg.configure(args[i].substring(9));
                } else if (args[i].startsWith("--naming=")) {
                    cfg.setNamingStrategy(
                            (NamingStrategy) ReflectHelper.classForName(args[i].substring(9))
                                    .newInstance()
                    );
                }
            }

        }

        if (propFile != null) {
            Properties props = new Properties();
            props.putAll(cfg.getProperties());
            props.load(new FileInputStream(propFile));
            cfg.setProperties(props);
        }

        SchemaExport se = new SchemaExport(cfg)
                .setHaltOnError(halt)
                .setOutputFile(outFile)
                .setImportFile(importFile)
                .setDelimiter(delim);
        if (format) {
            se.setFormat(true);
        }
        se.execute(script, export, drop, create);
    }


}
