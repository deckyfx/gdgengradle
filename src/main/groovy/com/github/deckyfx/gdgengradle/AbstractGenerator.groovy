package com.github.deckyfx.gdgengradle

import com.github.deckyfx.gdgengradle.generator.DaoGenerator
import com.github.deckyfx.gdgengradle.generator.Schema
import com.github.deckyfx.gdgengradle.schema.SchemaAdaptor
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.TaskAction

import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.regex.Matcher
import java.util.regex.Pattern

class AbstractGenerator extends DefaultTask {
    /* Do not modify this part unless necessary */
    public final static String TASK_NAME                    = "generateModels"
    public final static String FORCE_TASK_NAME              = "forceGenerateModels"
    private final static String DESCRIPTION                 = "Generate greendao models if detect schema file changes"


    private static final String SCHEMA_VERSION_FORMAT       = "yyMMddHHmm"
    private static final int CHANGES_TRESSHOLD              = 2

    /* Do not modify this part bellow */
    private Schema mSchema
    private int mSchemaVersion;
    private String mJavaSrcDir, mProjectRoot,
                   mProjectPackageName, mProjectDAOPackageName,
                   mProjectTestDAOPackageName
    private File mSchemaFile, mManifestFile

    final Property<Boolean> force                           = project.objects.property(Boolean)

    AbstractGenerator() {
        this.group                  = GDGenGradle.TASK_GROUP
        this.description            = DESCRIPTION
    }

    @TaskAction
    void start() {
        this.init()
    }

    protected void init() {
        assert project : 'Null project is illegal'
        this.mProjectRoot               = project.getProjectDir().toString()
        Log.log("Set root path to: " + this.mProjectRoot)
        assert Paths.get(this.mProjectRoot) : "Root path is invalid, quiting"
        this.mJavaSrcDir                = this.mProjectRoot + "/src/main/java/"
        assert Paths.get(this.mJavaSrcDir) : "Java source path is invalid, quiting"
        String manifestPath             = this.mProjectRoot + "/src/main/AndroidManifest.xml"
        this.mManifestFile              = new File(manifestPath)
        assert this.mManifestFile.isFile() : "AndroidManifest.xml in " + this.mProjectRoot + "/src/main/, quiting"
        String manifestText             = this.readFile(manifestPath)
        Pattern pattern                 = Pattern.compile("package=\"(.*)\"")
        Matcher matcher                 = pattern.matcher(manifestText)
        assert matcher.find() : "Package name is not declared in AndroidManifest.xml, quiting"
        this.mProjectPackageName        = matcher.group(1)
        this.mProjectDAOPackageName     = matcher.group(1) + ".dao"
        this.mProjectTestDAOPackageName = matcher.group(1) + ".test"
        String schemaPath               = this.mProjectRoot + "/schema.json"
        this.mSchemaFile                = new File(schemaPath)
        if (!this.mSchemaFile.isFile()) {
            File sampleFile             = new File(getClass().getResource('/sample.json').toURI());
            this.writeFile(schemaPath, sampleFile.text)
            this.mSchemaFile            = new File(schemaPath)
        }
        String schemaText               = this.readFile(schemaPath).replaceAll("\\r\\n|\\r|\\n|\\t", " ");
        SimpleDateFormat sdf            = new SimpleDateFormat(SCHEMA_VERSION_FORMAT)
        this.mSchemaVersion             = Integer.parseInt(sdf.format(this.mSchemaFile.lastModified()))
        String daoMasterPath            = this.mJavaSrcDir + this.mProjectDAOPackageName.replace(".", "/") + "/DaoMaster.java";
        File daoMasterFile              = new File(daoMasterPath)
        int lastVersion                 = 0
        if (daoMasterFile.isFile()) {
            String daoMasterText        = this.readFile(daoMasterPath).trim()
            Pattern patternDaoMaster    = Pattern.compile("public\\sstatic\\sfinal\\sint\\sSCHEMA_VERSION\\s?=\\s?(\\d+)\\;")
            Matcher matcherDaoMaster    = patternDaoMaster.matcher(daoMasterText)
            if (matcherDaoMaster.find()) {
                lastVersion             = Integer.parseInt(matcherDaoMaster.group(1))
            }
        }
        if (!force.get() && this.mSchemaVersion <= lastVersion) {
            Log.log("No schema changes, quiting")
            return
        }
        Log.log("Generating models for schema version " + this.mSchemaVersion)
        // Schema options
        // Create schema arguments => (schema_versionn, generated_dao_object_package)
        this.mSchema                    = new Schema(this.mSchemaVersion, this.mProjectDAOPackageName)
        // DAO classes go into the "dao" package
        this.mSchema.setDefaultJavaPackageDao(this.mProjectDAOPackageName)
        // test classes go into the "test" package
        this.mSchema.setDefaultJavaPackageTest(this.mProjectTestDAOPackageName)
        // optional: make entities active
        this.mSchema.enableActiveEntitiesByDefault()
        // optional: enable KEEP section support
        this.mSchema.enableKeepSectionsByDefault()

        SchemaAdaptor adaptor = new SchemaAdaptor(this.mSchema, this.mProjectPackageName)
        adaptor.parseJson(schemaText)
        this.mSchema = adaptor.getSchema()

        this.commit()
    }

    protected String readFile(String filename) {
        String content = null
        File file = new File(filename)
        FileReader reader
        try {
            reader = new FileReader(file)
            char[] chars = new char[(int) file.length()]
            reader.read(chars)
            content = new String(chars)
            reader.close()
        } catch (IOException e) {
            Log.log(e)
        }
        return content
    }

    protected void writeFile(String filename, String text) {
        // File output
        Writer file = null
        try {
            file = new FileWriter(new File(filename))
            file.write(text);
            file.flush()
            file.close()
        } catch (IOException e) {
            e.printStackTrace()
        }
    }

    private void commit() {
        try {
            println("--------------")
            (new DaoGenerator()).generateAll(this.mSchema, this.mJavaSrcDir)
            println("--------------")
            Log.log("Models generated with versions: " + String.valueOf(this.mSchemaVersion))
        } catch (Exception e) {
            Log.log(e)
        }
    }
}

