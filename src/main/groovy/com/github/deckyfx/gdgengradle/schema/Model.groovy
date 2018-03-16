package com.github.deckyfx.gdgengradle.schema

import com.github.deckyfx.gdgengradle.generator.Entity
import com.google.gson.annotations.SerializedName

import java.util.regex.Pattern

/**
 * Created by 1412 on 11/13/2016.
 */

class Model extends AbstractData {

    public String               name
    public boolean              active              = true
    public boolean              enableKeep          = true
    @SerializedName("extends")
    public String               extendsClass
    @SerializedName("implements")
    public ArrayList<String>    implementsClass     = new ArrayList<String>()
    public boolean              serializeable       = true
    @SerializedName("imports")
    public ArrayList<String>    importClass         = new ArrayList<String>()
    public String               javadoc
    public String               anotation
    public ArrayList<Field>     fields              = new ArrayList<Field>()
    public ArrayList<Relation>  relations           = new ArrayList<Relation>()

    public Entity               entity

    @Override
    public void init() {
        for (int i = 0; i < this.importClass.size(); i++) {
            this.importClass.set(i, this.replaceProjectPackagePlaceHolder(this.importClass.get(i)))
        }
        if (this.extendsClass?.trim()) {
            if (!this.isImporting(this.extendsClass)) {
                this.importClass.add(this.extendsClass)
            }
        }
        for (String implement : this.implementsClass) {
            if (!this.isImporting(this.extendsClass)) {
                this.importClass.add(implement)
            }
        }
        if (this.anotation?.trim()) {
            this.anotation = this.anotation.startsWith("@")? this.anotation:("@" + this.anotation)
        }
    }

    public Field getFieldWithName(String name) {
        for (Field field : this.fields) {
            if (field.name.equals(name)) {
                return field
            }
        }
        return null
    }

    private boolean isImporting(String className){
        for (int i = 0; i < this.importClass.size(); i++) {
            String cls = this.importClass.get(i)
            if (cls.equals(className) || cls.endsWith(className)) {
                return true
            }
        }
        return false
    }
}
