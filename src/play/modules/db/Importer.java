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
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.cfg.Settings;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.OverrideRepository;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2x.Cfg2JavaTool;
import org.hibernate.tool.hbm2x.POJOExporter;
import play.Play;

import javax.persistence.Entity;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

public class Importer {


    public static void main(String[] args) throws Exception {


         File root = new File(System.getProperty("application.path"));
        Play.init(root, System.getProperty("play.id", ""));
        Thread.currentThread().setContextClassLoader(Play.classloader);
              
        JDBCMetaDataConfiguration cfg = new JDBCMetaDataConfiguration();
		
        cfg.setPreferBasicCompositeIds(true);

	    ReverseEngineeringStrategy reverseEngineeringStrategy = new DefaultReverseEngineeringStrategy();
        ReverseEngineeringSettings settings = new ReverseEngineeringSettings();
        settings.setDefaultPackageName("models");
        reverseEngineeringStrategy.setSettings(settings);

        cfg.setReverseEngineeringStrategy(reverseEngineeringStrategy);

        cfg.setProperty("hibernate.hbm2ddl.auto", "create");

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

        final String defaultSchema = Play.configuration.getProperty("db.default.schema");
        if (defaultSchema != null) {
            cfg.setProperty("hibernate.default_schema", defaultSchema);
        }

        cfg.readFromJDBC();


        POJOExporter se = new POJOExporter();
        se.setProperties(cfg.getProperties());
        se.setConfiguration(cfg);
        se.getProperties().setProperty("ejb3", "true");
        se.getProperties().setProperty("jdk5", "true");

        se.setOutputDirectory(new File(Play.applicationPath, "app/"));
        se.start();
    }


}
