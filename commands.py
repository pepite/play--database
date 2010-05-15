# Here you can create play commands that are specific to the module, and extend existing commands
import os, os.path
import getopt
import sys
import subprocess

MODULE = 'db'

# Commands that are specific to your module

COMMANDS = ['db:export','db:import']

def execute(**kargs):
    command = kargs.get("command")
    app = kargs.get("app")
    args = kargs.get("args")
    env = kargs.get("env")

    if command == "db:export":
        print "~ Generating ddl database file"
        print "~ "
        java_cmd = app.java_cmd([], None, "play.modules.db.Exporter", args)
        try:
            subprocess.call(java_cmd, env=os.environ)
        except OSError:
            print "Could not execute the java executable, please make sure the JAVA_HOME environment variable is set properly (the java executable should reside at JAVA_HOME/bin/java). "
            sys.exit(-1)
        print

    if command == "db:import":
        print "~ Generating POJOs from the database"
        print "~ "
        java_cmd = app.java_cmd([], None, "play.modules.db.Importer", args)
        try:
            subprocess.call(java_cmd, env=os.environ)
        except OSError:
            print "Could not execute the java executable, please make sure the JAVA_HOME environment variable is set properly (the java executable should reside at JAVA_HOME/bin/java). "
            sys.exit(-1)
        print


# This will be executed before any command (new, run...)
def before(**kargs):
    command = kargs.get("command")
    app = kargs.get("app")
    args = kargs.get("args")
    env = kargs.get("env")


# This will be executed after any command (new, run...)
def after(**kargs):
    command = kargs.get("command")
    app = kargs.get("app")
    args = kargs.get("args")
    env = kargs.get("env")

    if command == "new":
        pass

