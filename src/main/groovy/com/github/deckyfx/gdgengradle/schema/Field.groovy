package com.github.deckyfx.gdgengradle.schema

import com.github.deckyfx.gdgengradle.generator.Property
import com.google.gson.annotations.SerializedName

/**
 * Created by 1412 on 11/13/2016.
 */

class Field extends AbstractData {
    public String               name
    public FieldType            type
    public String               customType
    public String               converter
    public String               anotation
    public String               anotationGetter
    public String               anotationSetter
    public String               anotationGetterSetter
    public String               javadoc
    public String               javadocGetter
    public String               javadocSetter
    public String               javadocGetterSetter
    public SortDirection        primary
    public SortDirection        index
    public boolean              autoIncrement
    public boolean              notNull
    public boolean              unique
    public Object               defaultValue

    public Property             property

    public void init() {
        if (this.customType?.trim()) {
            this.customType         = this.replaceProjectPackagePlaceHolder(this.customType)
        }
        if (this.converter?.trim()) {
            this.converter          = this.replaceProjectPackagePlaceHolder(this.converter)
        }
        if (this.anotation?.trim()) {
            this.anotation          = this.anotation.startsWith("@")? this.anotation:("@" + this.anotation)
        }
        if (this.anotationGetter?.trim()) {
            this.anotationGetter    = this.anotationGetter.startsWith("@")? this.anotationGetter:("@" + this.anotationGetter)
        }
        if (this.anotationSetter?.trim()) {
            this.anotationSetter    = this.anotationSetter.startsWith("@")? this.anotationSetter:("@" + this.anotationSetter)
        }
        if (this.anotationGetterSetter?.trim()) {
            this.anotationGetterSetter = this.anotationGetterSetter.startsWith("@")? this.anotationGetterSetter:("@" + this.anotationGetterSetter)
        }
    }
}
