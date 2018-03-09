package com.github.deckyfx.gdgengradle

import com.github.deckyfx.gdgengradle.generator.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.regex.Pattern

/**
 * Created by 1412 on 11/13/2016.
 */

public class EntityInfo implements Serializable {
    private static final String TAG_FIELDS                          = "fields";
    private static final String TAG_RELATIONS                       = "relations";
    private static final Pattern PROJECT_PACKAGE_PLACHOLDER         =  ~/\$\(PROJECT\)/;

    private static class ENTITY {
        private static final String TAG_ACTIVE                      = "active";
        private static final String TAG_ENABLE_KEEP                 = "enableKeep";
        private static final String TAG_EXTENDS                     = "extends";
        private static final String TAG_IMPLEMENTS                  = "implements";
        private static final String TAG_IMPORT                      = "import";
        private static final String TAG_IMPLEMENTS_SERIALIZABLE     = "serializeable";
        private static final String TAG_JAVADOC                     = "javadoc";
        private static final String TAG_ANOTATION                   = "anotation";
    }
    private static class PROPERTY {
        private static final String TAG_NAME                        = "name";
        private static final String TAG_TYPE                        = "type";
        private static final String TAG_CUSTOM_TYPE                 = "customType";
        private static final String TAG_CONVERTER                   = "converter";
        private static final String TAG_ANOTATION                   = "anotation";
        private static final String TAG_ANOTATION_GETTER            = "anotationGetter";
        private static final String TAG_ANOTATION_SETTER            = "anotationSetter";
        private static final String TAG_ANOTATION_GETTER_SETTER     = "anotationGetterSetter";
        private static final String TAG_JAVADOC                     = "javadoc";
        private static final String TAG_JAVADOC_SETTER              = "javadocSetter";
        private static final String TAG_JAVADOC_GETTER              = "javadocGetter";
        private static final String TAG_JAVADOC_GETTER_SETTER       = "javadocGetterSetter";
        private static final String TAG_PRIMARY                     = "isPrimary";
        private static final String TAG_DIRECTION_ASC               = "asc";
        private static final String TAG_DIRECTION_DESC              = "desc";
        private static final String TAG_AUTO_INCREMENT              = "isAutoIncrement";
        private static final String TAG_NOT_NULL                    = "notNull";
        private static final String TAG_UNIQUE                      = "isUnique";
        private static final String TAG_INDEX                       = "isIndex";;
        private static final String TAG_INDEX_NAME                  = "indexName";
        private static final String TAG_DEFAULT_VALUE               = "defaultValue";
    }
    private static class PROPERTY_TYPE {
        private static final String ID                              = "id";
        private static final String BOOLEAN                         = "boolean";
        private static final String BOOL                            = "bool";
        private static final String FLAG                            = "flag";
        private static final String BYTEARRAY                       = "bytearray";
        private static final String BYTE                            = "byte";
        private static final String DATE                            = "date";
        private static final String DOUBLE                          = "double";
        private static final String FLOAT                           = "float";
        private static final String INT                             = "int";
        private static final String INTEGER                         = "integer";
        private static final String NUMBER                          = "number";
        private static final String LONG                            = "long";
        private static final String SHORT                           = "short";
        private static final String STRING                          = "string";
        private static final String TEXT                            = "text";
    }
    private static class RELATION {
        private static final String TYPE_HAS_ONE                    = "hasOne";
        private static final String TYPE_HAS_MANY                   = "hasMany";
        private static final String TAG_NAME                        = "name";
        private static final String TAG_TYPE                        = "type";
        private static final String TAG_TARGET                      = "target";
        private static final String TAG_CHAIN_FIELD                 = "chainField";
    }

    private final JSONObject mConfig;
    private final String mEntityName;
    private final String mProjectPackage;
    private Schema mSchema;
    private Entity mEntity;
    private HashMap<String, Property> mProperties   = new HashMap<String, Property>();
    private JSONArray mFieldsConfig                 = new JSONArray();
    private JSONArray mRelationsConfig              = new JSONArray();
    private String mRelationLogs                     = "";
    private ArrayList<String> mImportedClass        = new ArrayList<String>();

    public EntityInfo(Schema schema, String projectPackage, String name, JSONObject config) {
        this.mSchema = schema;
        this.mConfig = config;
        this.mEntityName = name;
        this.mProjectPackage = projectPackage;
        this.mEntity = this.mSchema.addEntity(this.mEntityName);
        try {
            if (this.mConfig.get(TAG_FIELDS) instanceof JSONArray) {
                this.mFieldsConfig = this.mConfig.getJSONArray(TAG_FIELDS);
                this.mConfig.remove(TAG_FIELDS);
            }
            if (this.mConfig.get(TAG_RELATIONS) instanceof JSONArray) {
                this.mRelationsConfig = this.mConfig.getJSONArray(TAG_RELATIONS);
                this.mConfig.remove(TAG_RELATIONS);
            }
            this.buildEntity(this.mConfig);
            this.buildFields(this.mFieldsConfig);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Entity getEntity() {
        return this.mEntity;
    }

    private void buildEntity(JSONObject config) {
        // Active
        if (config.has(ENTITY.TAG_ACTIVE)) {
            boolean isActive = false;
            if (config.get(ENTITY.TAG_ACTIVE) instanceof Boolean) {
                isActive = config.getBoolean(ENTITY.TAG_ACTIVE);
            }
            if (isActive) {
                this.mEntity.setActive(isActive);
            }
        }

        // Enable Keep
        if (config.has(ENTITY.TAG_ENABLE_KEEP)) {
            boolean isKeepEnabled = false;
            if (config.get(ENTITY.TAG_ENABLE_KEEP) instanceof Boolean) {
                isKeepEnabled = config.getBoolean(ENTITY.TAG_ENABLE_KEEP);
            }
            if (isKeepEnabled) {
                this.mEntity.setHasKeepSections(isKeepEnabled);
            }
        }

        // Import classes
        if (config.has(ENTITY.TAG_IMPORT)) {
            String importClass = "";
            String[] importClasses = new String[0];
            if (config.get(ENTITY.TAG_IMPORT) instanceof String) {
                importClass = config.getString(ENTITY.TAG_IMPORT);
                if (importClass.length() > 0) {
                    importClasses = importClass.split(",");
                }
            } else if (config.get(ENTITY.TAG_IMPORT) instanceof JSONArray) {
                JSONArray array = config.getJSONArray(ENTITY.TAG_IMPORT);
                ArrayList<String> classes = new ArrayList<String>();
                for (int n = 0; n < array.length(); n++) {
                    if (array.get(n) instanceof String) {
                        classes.add(array.getString(n));
                    }
                }
                importClasses = new String[classes.size()];
                importClasses = classes.toArray(importClasses);
            }
            if (importClasses.length > 0) {
                //Method method = this.mEntity.getClass().getMethod("addImport", String[].class);
                //method.invoke(this.mEntity, importClasses);
                for (int n = 0; n < importClasses.length; n++) {
                    String className = this.replaceProjectPackagePlaceHolder(importClasses[n]);
                    this.mImportedClass.add(className);
                    this.mEntity.addImport(className);
                }
            }
        }

        // Extends from class
        if (config.has(ENTITY.TAG_EXTENDS)) {
            String extendsClass = "";
            if (config.get(ENTITY.TAG_EXTENDS) instanceof String) {
                extendsClass = this.replaceProjectPackagePlaceHolder(config.getString(ENTITY.TAG_EXTENDS));
            }
            if (extendsClass.length() > 0) {
                if (!this.isImporting(extendsClass)) {
                    this.mImportedClass.add(extendsClass);
                    this.mEntity.addImport(extendsClass);
                }
                this.mEntity.setSuperclass(extendsClass);
            }
        }

        // Implements from class
        if (config.has(ENTITY.TAG_IMPLEMENTS)) {
            String implementsClass = "";
            String[] implementsClasses = new String[0];
            if (config.get(ENTITY.TAG_IMPLEMENTS) instanceof String) {
                implementsClass = config.getString(ENTITY.TAG_IMPLEMENTS);
                if (implementsClass.length() > 0) {
                    implementsClasses = implementsClass.split(",");
                    for (int n = 0; n < implementsClasses.length; n++) {
                        implementsClasses[n] = this.replaceProjectPackagePlaceHolder(implementsClasses[n]);
                    }
                }
            } else if (config.get(ENTITY.TAG_IMPLEMENTS) instanceof JSONArray) {
                JSONArray array = config.getJSONArray(ENTITY.TAG_IMPLEMENTS);
                ArrayList<String> classes = new ArrayList<String>();
                for (int n = 0; n < array.length(); n++) {
                    if (array.get(n) instanceof String) {
                        classes.add(this.replaceProjectPackagePlaceHolder(array.getString(n)));
                    }
                }
                implementsClasses = new String[classes.size()];
                implementsClasses = classes.toArray(implementsClasses);
            }
            if (implementsClasses.length > 0) {
                try {
                    Method method = null;
                    method = this.mEntity.getClass().getMethod("implementsInterface", String[].class);
                    method.invoke(this.mEntity, implementsClasses);
                    for (int n = 0; n < implementsClasses.length; n++) {
                        String className = implementsClasses[n];
                        if (!this.isImporting(className)) {
                            this.mImportedClass.add(className);
                            this.mEntity.addImport(className);
                        }
                        this.mEntity.implementsInterface(className);
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }

        // Implements from Serializeable
        if (config.has(ENTITY.TAG_IMPLEMENTS_SERIALIZABLE)) {
            boolean serializeable = false;
            if (config.get(ENTITY.TAG_IMPLEMENTS_SERIALIZABLE) instanceof Boolean) {
                serializeable = config.getBoolean(ENTITY.TAG_IMPLEMENTS_SERIALIZABLE);
            }
            if (serializeable) {
                this.mEntity.implementsSerializable();
            }
        }

        // Add Java Doc
        if (config.has(ENTITY.TAG_JAVADOC)) {
            String javadocComment = "";
            if (config.get(ENTITY.TAG_JAVADOC) instanceof String) {
                javadocComment = config.getString(ENTITY.TAG_JAVADOC);
            }
            if (javadocComment.length() > 0) {
                this.mEntity.setJavaDoc(javadocComment);
            }
        }

        // Add Java Doc
        if (config.has(ENTITY.TAG_ANOTATION)) {
            String anotation = "";
            if (config.get(ENTITY.TAG_ANOTATION) instanceof String) {
                anotation = config.getString(ENTITY.TAG_ANOTATION);
            }
            if (anotation.length() > 0) {
                this.mEntity.setCodeBeforeClass("@" + anotation);
            }
        }
    }

    private void buildFields(JSONArray config) {
        Property.PropertyBuilder propbuilder = null;
        try {
            for (int i = 0; i < config.length(); i++) {
                JSONObject field = config.getJSONObject(i);
                String fieldName = field.getString(PROPERTY.TAG_NAME);
                String type = field.getString(PROPERTY.TAG_TYPE).toLowerCase();
                if (type.equals(PROPERTY_TYPE.ID)) {
                    propbuilder = this.mEntity.addIdProperty();
                    propbuilder.primaryKey();
                } else if (type.equals(PROPERTY_TYPE.BOOLEAN) || type.equals(PROPERTY_TYPE.BOOL) || type.equals(PROPERTY_TYPE.FLAG)) {
                    propbuilder = this.mEntity.addBooleanProperty(fieldName);
                } else if (type.equals(PROPERTY_TYPE.BYTEARRAY)) {
                    propbuilder = this.mEntity.addByteArrayProperty(fieldName);
                } else if (type.equals(PROPERTY_TYPE.BYTE)) {
                    propbuilder = this.mEntity.addByteProperty(fieldName);
                } else if (type.equals(PROPERTY_TYPE.DATE)) {
                    propbuilder = this.mEntity.addDateProperty(fieldName);
                } else if (type.equals(PROPERTY_TYPE.DOUBLE)) {
                    propbuilder = this.mEntity.addDoubleProperty(fieldName);
                } else if (type.equals(PROPERTY_TYPE.FLOAT)) {
                    propbuilder = this.mEntity.addFloatProperty(fieldName);
                } else if (type.equals(PROPERTY_TYPE.INT) || type.equals(PROPERTY_TYPE.NUMBER) || type.equals(PROPERTY_TYPE.INTEGER)) {
                    propbuilder = this.mEntity.addIntProperty(fieldName);
                } else if (type.equals(PROPERTY_TYPE.LONG)) {
                    propbuilder = this.mEntity.addLongProperty(fieldName);
                } else if (type.equals(PROPERTY_TYPE.SHORT)) {
                    propbuilder = this.mEntity.addShortProperty(fieldName);
                } else if (type.equals(PROPERTY_TYPE.STRING) || type.equals(PROPERTY_TYPE.TEXT)) {
                    propbuilder = this.mEntity.addStringProperty(fieldName);
                }
                // Custom type and Converter
                if (field.has(PROPERTY.TAG_CUSTOM_TYPE) && field.has(PROPERTY.TAG_CONVERTER)) {
                    String customTypeClass = "";
                    String converterClass = "";
                    if (field.get(PROPERTY.TAG_CUSTOM_TYPE) instanceof String) {
                        customTypeClass = field.getString(PROPERTY.TAG_CUSTOM_TYPE);
                    }
                    if (field.get(PROPERTY.TAG_CONVERTER) instanceof String) {
                        converterClass = field.getString(PROPERTY.TAG_CONVERTER);
                    }
                    if (customTypeClass.length() > 0 && converterClass.length() > 0) {
                        propbuilder.customType(customTypeClass, converterClass);
                    }
                }
                if (field.has(PROPERTY.TAG_ANOTATION)) {
                    String value = "";
                    if (field.get(PROPERTY.TAG_ANOTATION) instanceof String) {
                        value = field.getString(PROPERTY.TAG_ANOTATION);
                    }
                    if (value.length() > 0) {
                        propbuilder.codeBeforeField("@" + value);
                    }
                }
                if (field.has(PROPERTY.TAG_ANOTATION_GETTER)) {
                    String value = "";
                    if (field.get(PROPERTY.TAG_ANOTATION_GETTER) instanceof String) {
                        value = field.getString(PROPERTY.TAG_ANOTATION_GETTER);
                    }
                    if (value.length() > 0) {
                        propbuilder.codeBeforeGetter("@" + value);
                    }
                }
                if (field.has(PROPERTY.TAG_ANOTATION_SETTER)) {
                    String value = "";
                    if (field.get(PROPERTY.TAG_ANOTATION_SETTER) instanceof String) {
                        value = field.getString(PROPERTY.TAG_ANOTATION_SETTER);
                    }
                    if (value.length() > 0) {
                        propbuilder.codeBeforeSetter("@" + value);
                    }
                }
                if (field.has(PROPERTY.TAG_ANOTATION_GETTER_SETTER)) {
                    String value = "";
                    if (field.get(PROPERTY.TAG_ANOTATION_GETTER_SETTER) instanceof String) {
                        value = field.getString(PROPERTY.TAG_ANOTATION_SETTER);
                    }
                    if (value.length() > 0) {
                        propbuilder.codeBeforeGetterAndSetter("@" + value);
                    }
                }
                if (field.has(PROPERTY.TAG_JAVADOC)) {
                    String value = "";
                    if (field.get(PROPERTY.TAG_JAVADOC) instanceof String) {
                        value = field.getString(PROPERTY.TAG_JAVADOC);
                    }
                    if (value.length() > 0) {
                        propbuilder.javaDocField(value);
                    }
                }
                if (field.has(PROPERTY.TAG_JAVADOC_GETTER)) {
                    String value = "";
                    if (field.get(PROPERTY.TAG_JAVADOC_GETTER) instanceof String) {
                        value = field.getString(PROPERTY.TAG_JAVADOC_GETTER);
                    }
                    if (value.length() > 0) {
                        propbuilder.javaDocGetter(value);
                    }
                }
                if (field.has(PROPERTY.TAG_JAVADOC_SETTER)) {
                    String value = "";
                    if (field.get(PROPERTY.TAG_JAVADOC_SETTER) instanceof String) {
                        value = field.getString(PROPERTY.TAG_JAVADOC_SETTER);
                    }
                    if (value.length() > 0) {
                        propbuilder.javaDocSetter(value);
                    }
                }
                if (field.has(PROPERTY.TAG_JAVADOC_GETTER_SETTER)) {
                    String value = "";
                    if (field.get(PROPERTY.TAG_JAVADOC_GETTER_SETTER) instanceof String) {
                        value = field.getString(PROPERTY.TAG_JAVADOC_GETTER_SETTER);
                    }
                    if (value.length() > 0) {
                        propbuilder.javaDocGetterAndSetter(value);
                    }
                }
                if (propbuilder != null) {
                    if (field.has(PROPERTY.TAG_PRIMARY)) {
                        boolean isprimary = false;
                        String primarydir = "";
                        if (field.get(PROPERTY.TAG_PRIMARY) instanceof String) {
                            primarydir = field.getString(PROPERTY.TAG_PRIMARY).toLowerCase();
                        } else if (field.get(PROPERTY.TAG_PRIMARY) instanceof Boolean) {
                            isprimary = field.getBoolean(PROPERTY.TAG_PRIMARY);
                        }
                        if (primarydir.equals(PROPERTY.TAG_DIRECTION_ASC)) {
                            propbuilder.primaryKeyAsc();
                        } else if (primarydir.equals(PROPERTY.TAG_DIRECTION_DESC)) {
                            propbuilder.primaryKeyDesc();
                        } else {
                            if (isprimary) {
                                propbuilder.primaryKey();
                            }
                        }
                    }
                    if (field.has(PROPERTY.TAG_AUTO_INCREMENT)) {
                        boolean isautoinc = field.getBoolean(PROPERTY.TAG_AUTO_INCREMENT);
                        if (isautoinc) {
                            propbuilder.autoincrement();
                            propbuilder.primaryKey();
                        }
                    }
                    if (field.has(PROPERTY.TAG_NOT_NULL)) {
                        boolean notnull = field.getBoolean(PROPERTY.TAG_NOT_NULL);
                        if (notnull) {
                            propbuilder.notNull();
                        }
                    }
                    if (field.has(PROPERTY.TAG_UNIQUE)) {
                        boolean isunique = field.getBoolean(PROPERTY.TAG_UNIQUE);
                        if (isunique) {
                            propbuilder.unique();
                        }
                    }
                    if (field.has(PROPERTY.TAG_INDEX)) {
                        boolean isindex = false;
                        String indexdir = "";
                        boolean isunique = field.getBoolean(PROPERTY.TAG_UNIQUE);
                        String indexname = field.getString(PROPERTY.TAG_INDEX_NAME);
                        if (field.get(PROPERTY.TAG_INDEX) instanceof String) {
                            indexdir = field.getString(PROPERTY.TAG_INDEX).toLowerCase();
                        } else if (field.get(PROPERTY.TAG_INDEX) instanceof Boolean) {
                            isindex = field.getBoolean(PROPERTY.TAG_INDEX);
                        }
                        if (indexdir.equals(PROPERTY.TAG_DIRECTION_ASC)) {
                            propbuilder.indexAsc(indexname, isunique);
                        } else if (indexdir.equals(PROPERTY.TAG_DIRECTION_DESC)) {
                            propbuilder.indexDesc(indexname, isunique);
                        } else {
                            if (isindex) {
                                propbuilder.index();
                            }
                        }
                    }
                    if (field.has(PROPERTY.TAG_DEFAULT_VALUE)) {
                        Object defaultValue = field.get(PROPERTY.TAG_DEFAULT_VALUE);
                        propbuilder.defaultValue(defaultValue);
                    }
                    this.mProperties.put(fieldName, propbuilder.getProperty());
                } else {
                    throw (new JSONException("Failed create new field for " + this.mEntityName + "." + fieldName));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void buildRelation(HashMap<String, EntityInfo> entitiesList) {
        for (int i = 0; i < this.mRelationsConfig.length(); i++) {
            if( this.mRelationsConfig.get(i) instanceof JSONObject ){
                JSONObject relationconfig = (JSONObject) this.mRelationsConfig.get(i);
                String dst, type, fieldName, relationName;
                EntityInfo dstTable;
                if (relationconfig.has(RELATION.TAG_TARGET) &&
                        relationconfig.has(RELATION.TAG_TYPE) &&
                        relationconfig.has(RELATION.TAG_CHAIN_FIELD)) {

                    if (relationconfig.get(RELATION.TAG_TARGET) instanceof String &&
                            relationconfig.get(RELATION.TAG_TYPE) instanceof String &&
                            relationconfig.get(RELATION.TAG_CHAIN_FIELD) instanceof String) {

                        dst = relationconfig.getString(RELATION.TAG_TARGET);
                        type = relationconfig.getString(RELATION.TAG_TYPE);
                        fieldName = relationconfig.getString(RELATION.TAG_CHAIN_FIELD);
                        relationName = fieldName;
                        if (relationconfig.has(RELATION.TAG_NAME)) {
                            if (relationconfig.get(RELATION.TAG_NAME) instanceof String) {
                                relationName = relationconfig.getString(RELATION.TAG_NAME);
                            }
                        }
                        dstTable = entitiesList.get(dst);
                        if (dstTable != null) {
                            if (type.equals(RELATION.TYPE_HAS_MANY)) {
                                Property property = dstTable.mProperties.get(fieldName);

                                RelationBase relation = this.mEntity.createToMany(dstTable.getEntity(), property, relationName);
                                relation = this.setRelationAnotationAndJavaDoc(relation, relationconfig);
                                this.mEntity.addToMany((ToMany) relation, dstTable.getEntity());
                            } else if (type.equals(RELATION.TYPE_HAS_ONE)) {
                                Property property = this.mProperties.get(fieldName);

                                RelationBase relation = this.mEntity.createToOne(dstTable.getEntity(), property, relationName);
                                relation = this.setRelationAnotationAndJavaDoc(relation, relationconfig);
                                this.mEntity.addToOne((ToOne) relation);
                            }
                            this.mRelationLogs += "Relate " + this.mEntityName + " " + type + " " + dst + " via " + fieldName + " as "+relationName+", ";
                        }
                    }
                }
            }
        }
    }

    private RelationBase setRelationAnotationAndJavaDoc(RelationBase relation, JSONObject config) {
        if (config.has(PROPERTY.TAG_ANOTATION)) {
            String value = "";
            if (config.get(PROPERTY.TAG_ANOTATION) instanceof String) {
                value = config.getString(PROPERTY.TAG_ANOTATION);
            }
            if (value.length() > 0) {
                relation.setCodeBeforeField("@" + value);
            }
        }
        if (config.has(PROPERTY.TAG_ANOTATION_GETTER)) {
            String value = "";
            if (config.get(PROPERTY.TAG_ANOTATION_GETTER) instanceof String) {
                value = config.getString(PROPERTY.TAG_ANOTATION_GETTER);
            }
            if (value.length() > 0) {
                relation.setCodeBeforeGetter("@" + value);
            }
        }
        if (config.has(PROPERTY.TAG_ANOTATION_SETTER)) {
            String value = "";
            if (config.get(PROPERTY.TAG_ANOTATION_SETTER) instanceof String) {
                value = config.getString(PROPERTY.TAG_ANOTATION_SETTER);
            }
            if (value.length() > 0) {
                relation.setCodeBeforeSetter("@" + value);
            }
        }
        if (config.has(PROPERTY.TAG_ANOTATION_GETTER_SETTER)) {
            String value = "";
            if (config.get(PROPERTY.TAG_ANOTATION_GETTER_SETTER) instanceof String) {
                value = config.getString(PROPERTY.TAG_ANOTATION_SETTER);
            }
            if (value.length() > 0) {
                relation.setCodeBeforeGetterAndSetter("@" + value);
            }
        }
        if (config.has(PROPERTY.TAG_JAVADOC)) {
            String value = "";
            if (config.get(PROPERTY.TAG_JAVADOC) instanceof String) {
                value = config.getString(PROPERTY.TAG_JAVADOC);
            }
            if (value.length() > 0) {
                relation.setJavaDocField(value);
            }
        }
        if (config.has(PROPERTY.TAG_JAVADOC_GETTER)) {
            String value = "";
            if (config.get(PROPERTY.TAG_JAVADOC_GETTER) instanceof String) {
                value = config.getString(PROPERTY.TAG_JAVADOC_GETTER);
            }
            if (value.length() > 0) {
                relation.setJavaDocGetter(value);
            }
        }
        if (config.has(PROPERTY.TAG_JAVADOC_SETTER)) {
            String value = "";
            if (config.get(PROPERTY.TAG_JAVADOC_SETTER) instanceof String) {
                value = config.getString(PROPERTY.TAG_JAVADOC_SETTER);
            }
            if (value.length() > 0) {
                relation.setJavaDocSetter(value);
            }
        }
        if (config.has(PROPERTY.TAG_JAVADOC_GETTER_SETTER)) {
            String value = "";
            if (config.get(PROPERTY.TAG_JAVADOC_GETTER_SETTER) instanceof String) {
                value = config.getString(PROPERTY.TAG_JAVADOC_GETTER_SETTER);
            }
            if (value.length() > 0) {
                relation.setJavaDocGetterAndSetter(value);
            }
        }
        return relation;
    }

    private String replaceProjectPackagePlaceHolder(String text){
        return text.replaceAll(PROJECT_PACKAGE_PLACHOLDER, this.mProjectPackage);
    }

    private boolean isImporting(String className){
        for (int i = 0; i < this.mImportedClass.size(); i++) {
            String cls = this.mImportedClass.get(i);
            if (cls.equals(className) || cls.endsWith(className)) {
                return true;
            }
        }
        return false;
    }

    public String getRelationLog() {
        if (this.mRelationLogs.length() > 0) {
            return this.mRelationLogs;
        }
        return "";
    }
}
