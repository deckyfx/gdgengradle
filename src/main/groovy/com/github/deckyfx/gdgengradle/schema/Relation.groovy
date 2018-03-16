package com.github.deckyfx.gdgengradle.schema
/**
 * Created by 1412 on 11/13/2016.
 */

class Relation extends AbstractData {
    public String           target
    public RelationType     type
    public String           chainField
    public String           name
    public String           anotation
    public String           anotationGetter
    public String           anotationSetter
    public String           anotationGetterSetter
    public String           javadoc
    public String           javadocGetter
    public String           javadocSetter
    public String           javadocGetterSetter

    @Override
    public void init() {
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
