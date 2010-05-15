# Here you can create play commands that are specific to the module, and extend existing commands
import os, os.path
import getopt
import sys
import subprocess

if play_command == 'db:export':
        print "~ Generating ddl database file"
        print "~ "
	check_application()
        load_modules()
        do_classpath()
        do_java('play.modules.db.Exporter', sys.argv)
        try:
            subprocess.call(java_cmd, env=os.environ)
        except OSError:
            print "Could not execute the java executable, please make sure the JAVA_HOME environment variable is set properly (the java executable should reside at JAVA_HOME/bin/java). "
            sys.exit(-1)
        print
 	sys.exit(0)

if play_command == "db:import":
        print "~ Generating POJOs from the database"
        print "~ "
 	check_application()
        load_modules()
        do_classpath()
        do_java('play.modules.db.Importer', sys.argv)
        try:
            subprocess.call(java_cmd, env=os.environ)
        except OSError:
            print "Could not execute the java executable, please make sure the JAVA_HOME environment variable is set properly (the java executable should reside at JAVA_HOME/bin/java). "
            sys.exit(-1)
        print
	sys.exit(0)

