package com.github.deckyfx.gdgengradle.schema

import com.github.deckyfx.gdgengradle.Log
import com.github.deckyfx.gdgengradle.generator.Entity
import com.github.deckyfx.gdgengradle.generator.Property
import com.github.deckyfx.gdgengradle.generator.RelationBase
import com.github.deckyfx.gdgengradle.generator.Schema
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import java.lang.reflect.Type

/**
 * Created by 1412 on 11/13/2016.
 */

class SchemaAdaptor {
    private Schema mSchema
    private HashMap<String, Model> mEntities
    private String projectPackage

    SchemaAdaptor(Schema schema, String projectPackage) {
        this.mSchema = schema
        this.projectPackage = projectPackage
    }

    public void parseJson(String json) {
        Gson gson                       = new Gson()
        Type type                       = new TypeToken<HashMap<String, Model>>(){}.getType()
        try {
            this.mEntities              = gson.fromJson( json, type )
        } catch (Exception e) {
            Log.log(e)
        }
        assert this.mEntities : "Error parse JSON, please refer to \"https://github.com/deckyfx/gdgengradle\" for schema definitions"
        for (Map.Entry<String, Model> entry in this.mEntities) {
            Log.log("Preparing model entity: " + entry.key)
            entry.value.name            = entry.key
            entry.value.projectPackage  = this.projectPackage
            this.addEntityFromModel(entry.value)
        }
        for (Map.Entry<String, Model> entry in this.mEntities) {
            Log.log("Building relation for entity: " + entry.key)
            if (entry.value.relations.size() > 0) {
                this.buildRelation(entry.value)
            }
        }
    }
    
    private void addEntityFromModel(Model model) {
        model.init()
        Entity entity = this.mSchema.addEntity(model.name)
        entity.setActive(model.active)
        entity.setHasKeepSections(model.enableKeep)
        if (model.extendsClass?.trim()) {
            entity.setSuperclass(model.extendsClass)
        }
        if (model.implementsClass.size() > 0) {
            entity.implementsInterface(model.implementsClass.toArray(new String[0]))
        }
        if (model.serializeable) {
            entity.implementsSerializable()
        }
        for (String klas : model.importClass) {
            entity.addImport(klas)
        }
        if (model.javadoc?.trim()) {
            entity.setJavaDoc(model.javadoc)
        }
        if (model.anotation?.trim()) {
            entity.setCodeBeforeClass("@" + model.anotation)
        }
        for (int i = 0; i < model.fields.size(); i++) {
            Field field             = model.fields.get(i)
            field.projectPackage    = this.projectPackage
            field.init()
            Property.PropertyBuilder propbuilder = null
            switch (field.type) {
                case AbstractData.FieldType.ID :
                    propbuilder                  = entity.addIdProperty()
                    propbuilder.primaryKey()
                    break
                case AbstractData.FieldType.BOOLEAN :
                case AbstractData.FieldType.BOOL :
                case AbstractData.FieldType.FLAG :
                    propbuilder                  = entity.addBooleanProperty(field.name)
                    propbuilder.primaryKey()
                    break
                case AbstractData.FieldType.BYTEARRAY :
                    propbuilder                  = entity.addByteArrayProperty(field.name)
                    break
                case AbstractData.FieldType.BYTE :
                    propbuilder                  = entity.addByteProperty(field.name)
                    break
                case AbstractData.FieldType.DATE :
                    propbuilder                  = entity.addDateProperty(field.name)
                    break
                case AbstractData.FieldType.DOUBLE :
                    propbuilder                  = entity.addDoubleProperty(field.name)
                    break
                case AbstractData.FieldType.FLOAT :
                    propbuilder                  = entity.addFloatProperty(field.name)
                    break
                case AbstractData.FieldType.INT :
                case AbstractData.FieldType.INTEGER :
                case AbstractData.FieldType.NUMBER :
                    propbuilder                  = entity.addIntProperty(field.name)
                    break
                case AbstractData.FieldType.LONG :
                    propbuilder                  = entity.addLongProperty(field.name)
                    break
                case AbstractData.FieldType.SHORT :
                    propbuilder                  = entity.addShortProperty(field.name)
                    break
                case AbstractData.FieldType.STRING :
                case AbstractData.FieldType.TEXT :
                    propbuilder                  = entity.addStringProperty(field.name)
                    break
            }
            assert propbuilder : "Failed create new field for " + model.name + "." + field.name
            if (field.customType?.trim() && field.converter?.trim()) {
                propbuilder.customType(field.customType, field.converter)
            }
            if (field.anotation?.trim()) {
                propbuilder.codeBeforeField(field.anotation)
            }
            if (field.anotationGetter?.trim()) {
                propbuilder.codeBeforeGetter(field.anotationGetter)
            }
            if (field.anotationSetter?.trim()) {
                propbuilder.codeBeforeSetter(field.anotationSetter)
            }
            if (field.anotationGetterSetter?.trim()) {
                propbuilder.codeBeforeGetterAndSetter(field.anotationGetterSetter)
            }
            if (field.javadoc?.trim()) {
                propbuilder.javaDocField(field.javadoc)
            }
            if (field.javadocGetter?.trim()) {
                propbuilder.javaDocGetter(field.javadocGetter)
            }
            if (field.javadocSetter?.trim()) {
                propbuilder.javaDocSetter(field.javadocSetter)
            }
            if (field.javadocGetterSetter?.trim()) {
                propbuilder.javaDocGetterAndSetter(field.javadocGetterSetter)
            }
            if (field.autoIncrement) {
                propbuilder.autoincrement()
                propbuilder.primaryKey()
            }
            if (field.primary != null) {
                switch (field.primaryDirection) {
                    case AbstractData.SortDirection.ASC:
                        propbuilder.primaryKeyAsc()
                        break
                    case AbstractData.SortDirection.DESC:
                        propbuilder.primaryKeyDesc()
                        break
                    case AbstractData.SortDirection.NONE:
                        propbuilder.primaryKey()
                        break
                }
            }
            if (field.notNull) {
                propbuilder.notNull()
            }
            if (field.unique) {
                propbuilder.unique()
            }
            if (field.index != null) {
                switch (field.primaryDirection) {
                    case AbstractData.SortDirection.ASC:
                        propbuilder.indexAsc(field.index, field.isUnique)
                        break
                    case AbstractData.SortDirection.DESC:
                        propbuilder.indexDesc(field.index, field.isUnique)
                        break
                    case AbstractData.SortDirection.NONE:
                        propbuilder.index()
                        break
                }
            }
            if (field.defaultValue) {
                propbuilder.defaultValue(field.defaultValue);
            }
            field.property = propbuilder.getProperty()
            model.fields.set(i, field)
        }
        model.entity = entity;
        this.mEntities.put(model.name, model)
    }

    private void buildRelation(Model model) {        
        for (int i = 0; i < model.relations.size(); i++) {
            relation.projectPackage = this.projectPackage
            Relation relation = model.relations.get(i)
            relation.init()
            assert relation.type : "Need to have relation type, hasOne or hasMany"
            assert relation.target : "Need to set model target"
            assert relation.chainField : "Need to set chainField"
            Model dstTable = this.mEntities.get(relation.target)
            assert dstTable.entity : "Model has no entity"
            assert dstTable : "No known model named " + relation.target
            Field field = dstTable.getFieldWithName(relation.chainField)
            assert field : "No known field named " + relation.chainField + " for model " + relation.target
            assert field.property : "Field " + relation.chainField + " for model " + relation.target + " has no property"
            RelationBase relationBase
            switch (relation.type) {
                case AbstractData.RelationType.HASMANY:
                    relationBase = dstTable.entity.createToMany(dstTable.getEntity(), field.property, relation.name)
                    break
                case AbstractData.RelationType.HASONE:
                    relationBase = dstTable.entity.createToOne(dstTable.getEntity(), field.property, relation.name)
                    break
            }
            if (relation.anotation?.trim()) {
                relationBase.setCodeBeforeField(relation.anotation)
            }
            if (relation.anotationGetter?.trim()) {
                relationBase.setCodeBeforeGetter(relation.anotationGetter)
            }
            if (relation.anotationSetter?.trim()) {
                relationBase.setCodeBeforeSetter(relation.anotationSetter)
            }
            if (relation.anotationGetterSetter?.trim()) {
                relationBase.setCodeBeforeGetterAndSetter(relation.anotationGetterSetter)
            }
            if (relation.javadoc?.trim()) {
                relationBase.setJavaDocSetter(relation.javadoc)
            }
            if (relation.javadocGetter?.trim()) {
                relationBase.setJavaDocGetter(relation.javadocGetter)
            }
            if (relation.javadocSetter?.trim()) {
                relationBase.setJavaDocSetter(relation.javadocSetter)
            }
            if (relation.javadocGetterSetter?.trim()) {
                relationBase.setJavaDocGetterAndSetter(relation.javadocGetterSetter)
            }
            Log.log("Relate " + model.name + " " + relation.type.toString() + " " + dstTable.name + " via " + relation.chainField + " as "+relation.name+", ")
        }
    }

    private String normalizeString(String string) {
        return ""
    }

    private String replaceProjectPackagePlaceHolder(String) {
        return ""
    }

    public Schema getSchema() {
        return this.mSchema
    }
}
