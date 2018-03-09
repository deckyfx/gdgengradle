/*
 * Copyright (C) 2011-2016 Markus Junginger, greenrobot (http://greenrobot.org)
 *
 * This file is part of greenDAO Generator.
 *
 * greenDAO Generator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * greenDAO Generator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with greenDAO Generator.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.deckyfx.gdgengradle.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The "root" model class to which you can add entities to.
 * 
 * @see <a href="http://greendao-orm.com/documentation/modelling-entities/">Modelling Entities (Documentation page)</a>
 */
@SuppressWarnings("unused")
public class Schema {
    public static final String DEFAULT_NAME = "default";

    private final int version;
    private final String defaultJavaPackage;
    private String defaultJavaPackageDao;
    private String defaultJavaPackageTest;
    private final List<Entity> entities;
    private Map<com.github.deckyfx.gdgengradle.generator.PropertyType, String> propertyToDbType;
    private Map<com.github.deckyfx.gdgengradle.generator.PropertyType, String> propertyToJavaTypeNotNull;
    private Map<com.github.deckyfx.gdgengradle.generator.PropertyType, String> propertyToJavaTypeNullable;
    private boolean hasKeepSectionsByDefault;
    private boolean useActiveEntitiesByDefault;
    private final String name;
    private final String prefix;

    public Schema(String name, int version, String defaultJavaPackage) {
        this.name = name;
        this.prefix = name.equals(DEFAULT_NAME) ? "" : DaoUtil.capFirst(name);
        this.version = version;
        this.defaultJavaPackage = defaultJavaPackage;
        this.entities = new ArrayList<>();
        initTypeMappings();
    }

    public Schema(int version, String defaultJavaPackage) {
        this(DEFAULT_NAME, version, defaultJavaPackage);
    }

    public void enableKeepSectionsByDefault() {
        hasKeepSectionsByDefault = true;
    }

    public void enableActiveEntitiesByDefault() {
        useActiveEntitiesByDefault = true;
    }

    private void initTypeMappings() {
        propertyToDbType = new HashMap<>();
        propertyToDbType.put(com.github.deckyfx.gdgengradle.generator.PropertyType.Boolean, "INTEGER");
        propertyToDbType.put(com.github.deckyfx.gdgengradle.generator.PropertyType.Byte, "INTEGER");
        propertyToDbType.put(com.github.deckyfx.gdgengradle.generator.PropertyType.Short, "INTEGER");
        propertyToDbType.put(com.github.deckyfx.gdgengradle.generator.PropertyType.Int, "INTEGER");
        propertyToDbType.put(com.github.deckyfx.gdgengradle.generator.PropertyType.Long, "INTEGER");
        propertyToDbType.put(com.github.deckyfx.gdgengradle.generator.PropertyType.Float, "REAL");
        propertyToDbType.put(com.github.deckyfx.gdgengradle.generator.PropertyType.Double, "REAL");
        propertyToDbType.put(com.github.deckyfx.gdgengradle.generator.PropertyType.String, "TEXT");
        propertyToDbType.put(com.github.deckyfx.gdgengradle.generator.PropertyType.ByteArray, "BLOB");
        propertyToDbType.put(com.github.deckyfx.gdgengradle.generator.PropertyType.Date, "INTEGER");

        propertyToJavaTypeNotNull = new HashMap<>();
        propertyToJavaTypeNotNull.put(com.github.deckyfx.gdgengradle.generator.PropertyType.Boolean, "boolean");
        propertyToJavaTypeNotNull.put(com.github.deckyfx.gdgengradle.generator.PropertyType.Byte, "byte");
        propertyToJavaTypeNotNull.put(com.github.deckyfx.gdgengradle.generator.PropertyType.Short, "short");
        propertyToJavaTypeNotNull.put(com.github.deckyfx.gdgengradle.generator.PropertyType.Int, "int");
        propertyToJavaTypeNotNull.put(com.github.deckyfx.gdgengradle.generator.PropertyType.Long, "long");
        propertyToJavaTypeNotNull.put(com.github.deckyfx.gdgengradle.generator.PropertyType.Float, "float");
        propertyToJavaTypeNotNull.put(com.github.deckyfx.gdgengradle.generator.PropertyType.Double, "double");
        propertyToJavaTypeNotNull.put(com.github.deckyfx.gdgengradle.generator.PropertyType.String, "String");
        propertyToJavaTypeNotNull.put(com.github.deckyfx.gdgengradle.generator.PropertyType.ByteArray, "byte[]");
        propertyToJavaTypeNotNull.put(com.github.deckyfx.gdgengradle.generator.PropertyType.Date, "java.util.Date");

        propertyToJavaTypeNullable = new HashMap<>();
        propertyToJavaTypeNullable.put(com.github.deckyfx.gdgengradle.generator.PropertyType.Boolean, "Boolean");
        propertyToJavaTypeNullable.put(com.github.deckyfx.gdgengradle.generator.PropertyType.Byte, "Byte");
        propertyToJavaTypeNullable.put(com.github.deckyfx.gdgengradle.generator.PropertyType.Short, "Short");
        propertyToJavaTypeNullable.put(com.github.deckyfx.gdgengradle.generator.PropertyType.Int, "Integer");
        propertyToJavaTypeNullable.put(com.github.deckyfx.gdgengradle.generator.PropertyType.Long, "Long");
        propertyToJavaTypeNullable.put(com.github.deckyfx.gdgengradle.generator.PropertyType.Float, "Float");
        propertyToJavaTypeNullable.put(com.github.deckyfx.gdgengradle.generator.PropertyType.Double, "Double");
        propertyToJavaTypeNullable.put(com.github.deckyfx.gdgengradle.generator.PropertyType.String, "String");
        propertyToJavaTypeNullable.put(com.github.deckyfx.gdgengradle.generator.PropertyType.ByteArray, "byte[]");
        propertyToJavaTypeNullable.put(com.github.deckyfx.gdgengradle.generator.PropertyType.Date, "java.util.Date");
    }

    /**
     * Adds a new entity to the schema. There can be multiple entities per table, but only one may be the primary entity
     * per table to create table scripts, etc.
     */
    public Entity addEntity(String className) {
        Entity entity = new Entity(this, className);
        entities.add(entity);
        return entity;
    }

    /**
     * Adds a new protocol buffers entity to the schema. There can be multiple entities per table, but only one may be
     * the primary entity per table to create table scripts, etc.
     */
    public Entity addProtobufEntity(String className) {
        Entity entity = addEntity(className);
        entity.useProtobuf();
        return entity;
    }

    public String mapToDbType(com.github.deckyfx.gdgengradle.generator.PropertyType propertyType) {
        return mapType(propertyToDbType, propertyType);
    }

    public String mapToJavaTypeNullable(com.github.deckyfx.gdgengradle.generator.PropertyType propertyType) {
        return mapType(propertyToJavaTypeNullable, propertyType);
    }

    public String mapToJavaTypeNotNull(com.github.deckyfx.gdgengradle.generator.PropertyType propertyType) {
        return mapType(propertyToJavaTypeNotNull, propertyType);
    }

    private String mapType(Map<com.github.deckyfx.gdgengradle.generator.PropertyType, String> map, com.github.deckyfx.gdgengradle.generator.PropertyType propertyType) {
        String dbType = map.get(propertyType);
        if (dbType == null) {
            throw new IllegalStateException("No mapping for " + propertyType);
        }
        return dbType;
    }

    public int getVersion() {
        return version;
    }

    public String getDefaultJavaPackage() {
        return defaultJavaPackage;
    }

    public String getDefaultJavaPackageDao() {
        return defaultJavaPackageDao;
    }

    public void setDefaultJavaPackageDao(String defaultJavaPackageDao) {
        this.defaultJavaPackageDao = defaultJavaPackageDao;
    }

    public String getDefaultJavaPackageTest() {
        return defaultJavaPackageTest;
    }

    public void setDefaultJavaPackageTest(String defaultJavaPackageTest) {
        this.defaultJavaPackageTest = defaultJavaPackageTest;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public boolean isHasKeepSectionsByDefault() {
        return hasKeepSectionsByDefault;
    }

    public boolean isUseActiveEntitiesByDefault() {
        return useActiveEntitiesByDefault;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    void init2ndPass() {
        if (defaultJavaPackageDao == null) {
            defaultJavaPackageDao = defaultJavaPackage;
        }
        if (defaultJavaPackageTest == null) {
            defaultJavaPackageTest = defaultJavaPackageDao;
        }
        for (Entity entity : entities) {
            entity.init2ndPass();
        }
    }

    void init3rdPass() {
        for (Entity entity : entities) {
            entity.init3rdPass();
        }
    }

}
