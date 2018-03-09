package com.github.deckyfx.gdgengradle

import com.github.deckyfx.gdgengradle.generator.DaoGenerator
import com.github.deckyfx.gdgengradle.generator.Schema
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.json.JSONException
import org.json.JSONObject

import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.regex.Matcher
import java.util.regex.Pattern
import com.github.deckyfx.gdgengradle.EntityInfo

class GeneratorTask extends DefaultTask {
    /* Do not modify this part unless necessary */
    private static final String SCHEMA_VERSION_FORMAT       = "yyMMddHHmm"
    private static final int CHANGES_TRESSHOLD              = 2

    /* Do not modify this part bellow */
    private Schema mSchema
    private HashMap<String, EntityInfo> mEntities
    private int mSchemaVersion;
    private String mJavaSrcDir, mProjectRoot,
                   mProjectPackageName, mProjectDAOPackageName,
                   mProjectTestDAOPackageName

    private File mSchemaFile, mManifestFile

    GeneratorTask() {
        this.group                  = GDGenGradle.TASK_GROUP
        this.description            = "Generate model"
    }

    @TaskAction
    void start() {
        assert project : 'Null project is illegal'
        init(project.getProjectDir().toString())
    }

    void init(String rootPath) {
        this.mProjectRoot               = rootPath
        this.Log("Set root path to: " + this.mProjectRoot)
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
        String schemaPath               = rootPath + "/schema.json"
        this.mSchemaFile                = new File(schemaPath)
        if (!this.mSchemaFile.isFile()) {
            this.writeFile(schemaPath, "{\"Table1Name\":{\"active\":true,\"enableKeep\":true,\"extends\":\"\",\"implements\":\"\",\"serializeable\":true,\"import\":\"\",\"javadoc\":\"\",\"anotation\":\"\",\"defaultValue\":0,\"fields\":[{\"name\":\"id\",\"type\":\"id\",\"customType\":\"\",\"converter\":\"\",\"anotation\":\"\",\"anotationGetter\":\"\",\"anotationSetter\":\"\",\"anotationGetterSetter\":\"\",\"javadoc\":\"\",\"javadocGetter\":\"\",\"javadocSetter\":\"\",\"javadocGetterSetter\":\"\",\"isAutoIncrement\":true,\"notNull\":true}],\"relations\":[{\"target\":\"Table 2 Name\",\"type\":\"hasOne\",\"chainField\":\"tbl2_data\",\"name\":\"relationName\",\"anotation\":\"\",\"anotationGetter\":\"\",\"anotationSetter\":\"\",\"anotationGetterSetter\":\"\",\"javadoc\":\"\",\"javadocGetter\":\"\",\"javadocSetter\":\"\",\"javadocGetterSetter\":\"\"}]}}")
            this.mSchemaFile            = new File(schemaPath)
        }
        String schemaText               = this.readFile(schemaPath);
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
        if (this.mSchemaVersion <= lastVersion) {
            this.Log("No schema changes, quiting")
            return
        }
        this.Log("Generating models for schema version " + this.mSchemaVersion)
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
        this.mEntities                  = new HashMap<String, EntityInfo>()
        try {
            JSONObject schemajson       = new JSONObject(schemaText)
            Iterator<?> keys            = schemajson.keys()
            while( keys.hasNext() ){
                String tableName        = (String)keys.next()
                if( schemajson.get(tableName) instanceof JSONObject ){
                    this.mEntities.put(tableName, new EntityInfo(this.mSchema, this.mProjectPackageName, tableName, schemajson.getJSONObject(tableName)));
                    this.Log("Preparing model entity " + tableName);
                }
            }
            for (EntityInfo entityinfo : this.mEntities.values()) {
                entityinfo.buildRelation(this.mEntities);
                String log = entityinfo.getRelationLog();
                if (log.length() > 0) {
                    this.Log(log);
                }
            }
            this.commit()
        } catch (JSONException e) {
            this.Log(e)
        }
    }

    String readFile(String filename) {
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
            this.Log(e)
        }
        return content
    }

    void writeFile(String filename, String text) {
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

    void commit() {
        try {
            (new DaoGenerator()).generateAll(this.mSchema, this.mJavaSrcDir)
            this.Log("Models generated with versions: " + String.valueOf(this.mSchemaVersion))
        } catch (Exception e) {
            this.Log(e)
        }
    }

    void Log(String s){
        println("GreenDaoGradle Generator > " + s)
    }

    void Log(Throwable e){
        println("Error occured: " + e)
        println("--------------")
        println(e.getStackTrace().toString())
        println("--------------")
    }
}

