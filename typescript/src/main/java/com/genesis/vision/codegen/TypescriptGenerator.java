package com.genesis.vision.codegen;

import io.swagger.codegen.v3.*;
import io.swagger.codegen.v3.generators.typescript.AbstractTypeScriptClientCodegen;
import io.swagger.codegen.v3.generators.util.OpenAPIUtil;

import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.DateSchema;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.MapSchema;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.BinarySchema;
import io.swagger.v3.oas.models.media.FileSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.swagger.codegen.v3.generators.handlebars.ExtensionHelper.getBooleanValue;
import static io.swagger.codegen.v3.CodegenConstants.IS_ENUM_EXT_NAME;

public class TypescriptGenerator extends AbstractTypeScriptClientCodegen {

  private static Logger LOGGER = LoggerFactory.getLogger(AbstractTypeScriptClientCodegen.class);

  // source folder where to write the files
  protected String sourceFolder = "src";
  protected String apiVersion = "1.0.0";

  /**
   * Configures the type of generator.
   * 
   * @return  the CodegenType for this generator
   * @see     io.swagger.codegen.CodegenType
   */
  public CodegenType getTag() {
    return CodegenType.CLIENT;
  }

  @Override
  public void processOpts() {
    super.processOpts();

    if (StringUtils.isBlank(templateDir)) {
      embeddedTemplateDir = templateDir = getTemplateDir();
    }

    languageSpecificPrimitives.add("File");
    typeMapping.put("file", "File");
  }

  /**
   * Configures a friendly name for the generator.  This will be used by the generator
   * to select the library with the -l flag.
   * 
   * @return the friendly name for the generator
   */
  public String getName() {
    return "typescript";
  }

  /**
   * Returns human-friendly help for the generator.  Provide the consumer with help
   * tips, parameters here
   * 
   * @return A string value for the help message
   */
  public String getHelp() {
    return "Generates a typescript client library.";
  }

  public TypescriptGenerator() {
    super();

    // set the output folder here
    outputFolder = "generated-code/typescript";

    /**
     * Models.  You can write model files using the modelTemplateFiles map.
     * if you want to create one template for file, you can do so here.
     * for multiple files for model, just put another entry in the `modelTemplateFiles` with
     * a different extension
     */
    modelTemplateFiles.put(
      "model.mustache", // the template to use
      ".ts");       // the extension for each file to write

    /**
     * Api classes.  You can write classes for each Api file with the apiTemplateFiles map.
     * as with models, add multiple entries with different extensions for multiple files per
     * class
     */
    apiTemplateFiles.put(
      "api.mustache",   // the template to use
      ".ts");       // the extension for each file to write

    /**
     * Template Location.  This is the location which templates will be read from.  The generator
     * will use the resource stream to attempt to read the templates.
     */
    templateDir = "typescript";

    /**
     * Api Package.  Optional, if needed, this can be used in templates
     */
    apiPackage = "api";

    /**
     * Model Package.  Optional, if needed, this can be used in templates
     */
    modelPackage = "model";


    /**
     * Additional Properties.  These values can be passed to the templates and
     * are available in models, apis, and supporting files
     */
    additionalProperties.put("apiVersion", apiVersion);

    /**
     * Supporting Files.  You can write single files for the generator with the
     * entire object tree available.  If the input file has a suffix of `.mustache
     * it will be processed by the template engine.  Otherwise, it will be copied
     */
    supportingFiles.add(new SupportingFile("utils.mustache",   // the input template or file
      sourceFolder,                                                       // the destination folder, relative `outputFolder`
      "utils.ts")                                          // the output file
    );
  }

  /**
   * Location to write model files.  You can use the modelPackage() as defined when the class is
   * instantiated
   */
  public String modelFileFolder() {
    return outputFolder + "/" + sourceFolder + "/" + modelPackage().replace('.', File.separatorChar);
  }

  /**
   * Location to write api files.  You can use the apiPackage() as defined when the class is
   * instantiated
   */
  @Override
  public String apiFileFolder() {
    return outputFolder + "/" + sourceFolder + "/" + apiPackage().replace('.', File.separatorChar);
  }

  @Override
  public String getArgumentsLocation() {
    return null;
  }

  @Override
  protected String getTemplateDir() {
    return templateDir;
  }

  @Override
  public String getDefaultTemplateDir() {
    return templateDir;
  }

  @Override
  public Map<String, Object> postProcessOperations(Map<String, Object> operations) {
    Map<String, Object> objs = (Map<String, Object>) operations.get("operations");

    // Add filename information for api imports
    objs.put("apiFilename", objs.get("classname"));

    List<Map<String, Object>> imports = (List<Map<String, Object>>) operations.get("imports");
    for (Map<String, Object> im : imports) {
      im.put("filename", im.get("import"));
      im.put("classname", getModelnameFromModelFilename(im.get("filename").toString()));
    }

    return operations;
  }

//  private String getApiFilenameFromClassname(String classname) {
//    String name = classname.substring(0, classname.length() - "Service".length());
//    return toApiFilename(name);
//  }
//
  private String getModelnameFromModelFilename(String filename) {
    String name = filename.substring((modelPackage() + File.separator).length());
    return camelize(name);
  }

  @Override
  public String toModelImport(String name) {
    return modelPackage() + "/" + toModelFilename(name);
  }

  @Override
  public String toApiImport(String name) {
    return apiPackage() + "/" + toApiFilename(name);
  }

  @Override
  public boolean isDataTypeFile(final String dataType) {
    return dataType != null && dataType.equals("File");
  }

  @Override
  public String getTypeDeclaration(Schema propertySchema) {
    if(propertySchema instanceof FileSchema || propertySchema instanceof BinarySchema) {
      return "File";
    }
    return super.getTypeDeclaration(propertySchema);
  }
}