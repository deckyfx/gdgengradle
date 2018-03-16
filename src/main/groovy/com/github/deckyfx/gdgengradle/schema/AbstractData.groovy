package com.github.deckyfx.gdgengradle.schema

import com.google.gson.annotations.SerializedName

import java.util.regex.Pattern

/**
 * Created by 1412 on 11/13/2016.
 */

abstract class AbstractData {
    protected static final Pattern PROJECT_PACKAGE_PLACHOLDER         =  ~/\$\(PROJECT\)/

    public enum FieldType {
        @SerializedName("id")           ID,
        @SerializedName("boolean")      BOOLEAN,
        @SerializedName("bool")         BOOL,
        @SerializedName("flag")         FLAG,
        @SerializedName("bytearray")    BYTEARRAY,
        @SerializedName("byte")         BYTE,
        @SerializedName("date")         DATE,
        @SerializedName("double")       DOUBLE,
        @SerializedName("float")        FLOAT,
        @SerializedName("int")          INT,
        @SerializedName("integer")      INTEGER,
        @SerializedName("number")       NUMBER,
        @SerializedName("long")         LONG,
        @SerializedName("short")        SHORT,
        @SerializedName("string")       STRING,
        @SerializedName("text")         TEXT,
        @SerializedName("custom")       CUSTOM
    }

    public enum SortDirection {
        @SerializedName("asc")          ASC,
        @SerializedName("desc")         DESC,
        @SerializedName("none")         NONE,
    }

    public enum RelationType {
        @SerializedName("hasone")       HASONE,
        @SerializedName("hasmany")      HASMANY,
    }

    protected String       projectPackage

    public abstract void init()

    protected String replaceProjectPackagePlaceHolder(String text){
        return text.replaceAll(PROJECT_PACKAGE_PLACHOLDER, this.projectPackage)
    }
}
