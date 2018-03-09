package com.github.deckyfx.gdgengradle.generator;

/**
 * Created by decky on 4/4/17.
 */
public abstract class RelationBase {
    protected String codeBeforeField;
    protected String codeBeforeGetter;
    protected String codeBeforeSetter;

    protected String javaDocField;
    protected String javaDocGetter;
    protected String javaDocSetter;

    // ##########################################
    // ######## Anotation and JAVA DOC ##########
    // ##########################################

    public String getCodeBeforeField() {
        return codeBeforeField;
    }

    public String getCodeBeforeGetter() {
        return codeBeforeGetter;
    }

    public String getCodeBeforeSetter() {
        return codeBeforeSetter;
    }

    public String getJavaDocField() {
        return javaDocField;
    }

    public String getJavaDocGetter() {
        return javaDocGetter;
    }

    public String getJavaDocSetter() {
        return javaDocSetter;
    }

    public RelationBase setCodeBeforeField(String code) {
        codeBeforeField = code;
        return this;
    }

    public RelationBase setCodeBeforeGetter(String code) {
        codeBeforeGetter = code;
        return this;
    }

    public RelationBase setCodeBeforeSetter(String code) {
        codeBeforeSetter = code;
        return this;
    }

    public RelationBase setCodeBeforeGetterAndSetter(String code) {
        codeBeforeGetter = code;
        codeBeforeSetter = code;
        return this;
    }

    public RelationBase setJavaDocField(String javaDoc) {
        javaDocField = checkConvertToJavaDoc(javaDoc);
        return this;
    }

    private String checkConvertToJavaDoc(String javaDoc) {
        return DaoUtil.checkConvertToJavaDoc(javaDoc, "    ");
    }

    public RelationBase setJavaDocGetter(String javaDoc) {
        javaDocGetter = checkConvertToJavaDoc(javaDoc);
        return this;
    }

    public RelationBase setJavaDocSetter(String javaDoc) {
        javaDocSetter = checkConvertToJavaDoc(javaDoc);
        return this;
    }

    public RelationBase setJavaDocGetterAndSetter(String javaDoc) {
        javaDoc = checkConvertToJavaDoc(javaDoc);
        javaDocGetter = javaDoc;
        javaDocSetter = javaDoc;
        return this;
    }

    // ##########################################
    // ###### END Anotation and JAVA DOC ########
    // ##########################################
}
