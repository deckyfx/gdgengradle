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

import java.util.List;

/** To-many relationship from a source entity to many target entities. */
@SuppressWarnings("unused")
public class ToMany extends ToManyBase {
    private com.github.deckyfx.gdgengradle.generator.Property[] sourceProperties;
    private final com.github.deckyfx.gdgengradle.generator.Property[] targetProperties;

    public ToMany(Schema schema, Entity sourceEntity, com.github.deckyfx.gdgengradle.generator.Property[] sourceProperties, Entity targetEntity,
                  com.github.deckyfx.gdgengradle.generator.Property[] targetProperties) {
        super(schema, sourceEntity, targetEntity);
        this.sourceProperties = sourceProperties;
        this.targetProperties = targetProperties;
    }



    public com.github.deckyfx.gdgengradle.generator.Property[] getSourceProperties() {
        return sourceProperties;
    }

    public void setSourceProperties(com.github.deckyfx.gdgengradle.generator.Property[] sourceProperties) {
        this.sourceProperties = sourceProperties;
    }

    public com.github.deckyfx.gdgengradle.generator.Property[] getTargetProperties() {
        return targetProperties;
    }

    void init2ndPass() {
        super.init2ndPass();
        if (sourceProperties == null) {
            List<com.github.deckyfx.gdgengradle.generator.Property> pks = sourceEntity.getPropertiesPk();
            if (pks.isEmpty()) {
                throw new RuntimeException("Source entity has no primary key, but we need it for " + this);
            }
            sourceProperties = new com.github.deckyfx.gdgengradle.generator.Property[pks.size()];
            sourceProperties = pks.toArray(sourceProperties);
        }
        int count = sourceProperties.length;
        if (count != targetProperties.length) {
            throw new RuntimeException("Source properties do not match target properties: " + this);
        }

        for (int i = 0; i < count; i++) {
            com.github.deckyfx.gdgengradle.generator.Property sourceProperty = sourceProperties[i];
            com.github.deckyfx.gdgengradle.generator.Property targetProperty = targetProperties[i];

            com.github.deckyfx.gdgengradle.generator.PropertyType sourceType = sourceProperty.getPropertyType();
            com.github.deckyfx.gdgengradle.generator.PropertyType targetType = targetProperty.getPropertyType();
            if (sourceType == null || targetType == null) {
                throw new RuntimeException("Property type uninitialized");
            }
            if (sourceType != targetType) {
                System.err.println("Warning to-one property type does not match target key type: " + this);
            }
        }
    }

    void init3rdPass() {
        super.init3rdPass();
    }

}
