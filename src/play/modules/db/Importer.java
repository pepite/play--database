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
import play.Logger;
import play.Play;

import javax.persistence.Entity;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;

public class Importer {

    public static void main(String[] args) throws Exception {

		// init Play Framework
        File root = new File(System.getProperty("application.path"));
        Play.init(root, System.getProperty("play.id", ""));
        Thread.currentThread().setContextClassLoader(Play.classloader);
        
		// init Hibernate Tools Reverse Engineering
        JDBCMetaDataConfiguration cfg = new JDBCMetaDataConfiguration();
		
        cfg.setPreferBasicCompositeIds(true);

	    ReverseEngineeringStrategy reverseEngineeringStrategy = new DefaultReverseEngineeringStrategy();
        ReverseEngineeringSettings settings = new ReverseEngineeringSettings();
        settings.setDefaultPackageName("models");
        reverseEngineeringStrategy.setSettings(settings);

		// default value
		String hibernateRevengXmlFilename = null;
		boolean generateCRUDControllers = false;
		boolean generatePOJOs = true;

		// argument parameter is possible if we're not using the default value
		Logger.info("Iterating on arguments, args.length = " + args.length);
        for (int i = 0; i < args.length; i++) {
			Logger.info("args[" + i + "] = " + args[i]);
            if (args[i].startsWith("--")) {
				Logger.info("... args[" + i + "] starts with '--'");
                if (args[i].equals("--reveng")) {
					Logger.info("... args[" + i + "] starts with '--reveng'");
					//the use of "=" splits the "--reveng=file.xml" into 2 arguments : "--reveng" (i) and "file.xml" (i+1).
					if ((i+1) < args.length) {
						hibernateRevengXmlFilename = args[i+1];
						Logger.info("... hibernateRevengXmlFilename = '" + hibernateRevengXmlFilename + "'");
					}
                } else if (args[i].startsWith("--reveng=")) {
					Logger.info("... args[" + i + "] starts with '--reveng='");
                    hibernateRevengXmlFilename = args[i].substring(9);
					Logger.info("... hibernateRevengXmlFilename = '" + hibernateRevengXmlFilename + "'");
                } else if (args[i].startsWith("--reveng:")) {
					Logger.info("... args[" + i + "] starts with '--reveng:'");
                    hibernateRevengXmlFilename = args[i].substring(9);
					Logger.info("... hibernateRevengXmlFilename = '" + hibernateRevengXmlFilename + "'");
                } else if (args[i].startsWith("--crud")) {
					Logger.info("... args[" + i + "] starts with '--crud'");
                    generateCRUDControllers=true;
					Logger.info("... generateCRUDControllers = '" + generateCRUDControllers + "'");
                } else if (args[i].startsWith("--no-pojo")) {
					Logger.info("... args[" + i + "] starts with '--no-pojo'");
                    generatePOJOs=false;
					Logger.info("... generatePOJOs = '" + generatePOJOs + "'");
                }
            }
        }


		if (hibernateRevengXmlFilename != null) {
			if ((!hibernateRevengXmlFilename.startsWith(Play.applicationPath.toString())) 
				&& (!hibernateRevengXmlFilename.startsWith("/")) 
				&& (!hibernateRevengXmlFilename.startsWith("\\"))) {
				// adds Play.applicationPath to hibernate filename because the filename in argument is a path relative to app
				hibernateRevengXmlFilename = Play.applicationPath + "/" + hibernateRevengXmlFilename;
				Logger.info("hibernateRevengXmlFilename which used a relative path, becomes = '" + hibernateRevengXmlFilename + "'");
			}
		
			Logger.info("Checking for '" + hibernateRevengXmlFilename + "'");
			File revengXmlConf = new File(hibernateRevengXmlFilename);
			if (revengXmlConf.exists()) {
				Logger.info("File '" + hibernateRevengXmlFilename + "' found, Importer will use it.");
				OverrideRepository or = new OverrideRepository();
				or.addFile(revengXmlConf);
				reverseEngineeringStrategy = or.getReverseEngineeringStrategy(reverseEngineeringStrategy);
			} else {
				Logger.error("File '" + hibernateRevengXmlFilename + "' not found, Importer will continue without it.");
			}
		}
		
        cfg.setReverseEngineeringStrategy(reverseEngineeringStrategy);

        cfg.setProperty("hibernate.hbm2ddl.auto", "create");

        final String dialect = Play.configuration.getProperty("jpa.dialect");
        if (dialect != null) {
            cfg.setProperty("hibernate.dialect", dialect);
			Logger.info("Importer will use jpa.dialect = " + dialect);
		}

        final String driver = Play.configuration.getProperty("db.driver");
        if (driver != null) {
            cfg.setProperty("hibernate.connection.driver_class", driver);
			Logger.info("Importer will use db.driver = " + driver);
		}

        final String user = Play.configuration.getProperty("db.user");
        if (user != null) {
            cfg.setProperty("hibernate.connection.username", user);
			Logger.info("Importer will use db.user = " + user);
		}

        final String password = Play.configuration.getProperty("db.pass");
        if (password != null) {
            cfg.setProperty("hibernate.connection.password", password);
			Logger.info("Importer will use db.password");
		}

        final String url = Play.configuration.getProperty("db.url");
        if (url != null) {
            cfg.setProperty("hibernate.connection.url", url);
			Logger.info("Importer will use db.url = " + url);
		}
		
		final String defaultSchema = Play.configuration.getProperty("db.default.schema");
		if (defaultSchema != null) {
			cfg.setProperty("hibernate.default_schema", defaultSchema);  
			Logger.info("Importer will use db.default.schema = " + defaultSchema);
		}

        cfg.readFromJDBC();
		Logger.info("DB metadata reading done.");

		if (generatePOJOs) {
			Logger.info("POJO generation enabled.");
			POJOExporter se = new POJOExporter();
			se.setProperties(cfg.getProperties());
			se.setConfiguration(cfg);
			se.getProperties().setProperty("ejb3", "true");
			se.getProperties().setProperty("jdk5", "true");

			se.setOutputDirectory(new File(Play.applicationPath, "app/"));
			Logger.info("Starting POJO Exporter ...");
			se.start();
			Logger.info("POJO Exporter done.");
		}
		
		if (generateCRUDControllers) {
			Logger.info("CRUD generation enabled.");
			CRUDControllerExporter se = new CRUDControllerExporter();
			se.setProperties(cfg.getProperties());
			se.setConfiguration(cfg);
			// option to use the secure module and put the @Check annotation for the CRUD Controllers ...
			se.getProperties().setProperty("secure", "true");

			se.setOutputDirectory(new File(Play.applicationPath, "app/"));
			Logger.info("Starting CRUD Controller Exporter ...");
			se.start();
			Logger.info("CRUD Controller Exporter done.");
		}
	}
}