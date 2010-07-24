package play.modules.db;

import java.io.File;

import org.hibernate.cfg.Configuration;

import org.hibernate.tool.hbm2x.GenericExporter;

/**
 * Exporter to generate Play! Framework CRUD Controller based on Hibernate Tools exporters.
 * @author doppelganger9
 */
public class CRUDControllerExporter extends GenericExporter {

  private static final String POJO_JAVACLASS_FTL = "play/modules/db/crud/CrudController.ftl";

  public CRUDControllerExporter(Configuration cfg, File outputdir) {
    super(cfg, outputdir);
    init();
  }

  protected void init() {
    setTemplateName(POJO_JAVACLASS_FTL);
	// Play! convention for CRUD Controllers names : put an 's' at the end of the domain classname.
	// Play! convention for CRUD Controllers classes : put in the "controllers" package.
    setFilePattern("controllers/{class-name}s.java");
  }

  public CRUDControllerExporter() {
    init();
  }

  public String getName() {
    return "hbm2crud";
  }

  protected void setupContext() {
    if (!getProperties().containsKey("secure")) {
      getProperties().put("secure", "false");
    }
    super.setupContext();
  }
}