gdgen
=====

SubClass of greenDAO Generator, read schema from your schema json file, support relationship and auto version migration

## Prerequisite

- Java 6 or above
- Android Studio 5.2

## How To Use

### Apply Plugin

```
buildscript {
    ext.gdgengradle_version = '1.0.3'
    repositories {
        maven {
            url "https://plugins.gradle.org/m2/"
        }
    }
    dependencies {
        classpath "gradle.plugin.com.github.deckyfx:autoversion:$autoversion_version"
    }

}
apply plugin: "com.github.deckyfx.gdgengradle"
```

### Run RunGDGenJar task
Generate DAO class by:
- Simply sync your project
- run task `gdgengradle/generateModels` in your project gradle task list inside `other` folder


## Schema file structure:
Edit `schema.json` file, and start write your database schema
### schema.json structure
```
{
    "TABLE_NAME":{
        "table_option_1": "value",
        "table_option_2": "value",
        "table_option_n": "value",
        "fields": [{
            "name": "field_name",
            "type": "field_type",
            "field_option_1": "value",
            "field_option_2": "value",
            "field_option_n": "value"
        },
        ....
        ],
        // optionals
        "relations": [{
            "target": "TABLE_NAME_2",
            "type": "hasMany", // or "hasOne"
            "chainField": "TABLE_2_FOREIGN_KEY",
            "name": "field_name_in_TABLE_1",
            "relation_option_1": "value",
            "relation_option_2": "value",
            "relation_option_n": "value"
        },
        ...]
    },
    ....
}
```
### Table options

Several table options can be set, (see greendao documenttation for each option info)


|    *Option*       |   *Value Type*    |                 *Example*                     |                   *Info*                      |
|:-------------     |:---------------:  |:-----------------------------------------     |:------------------------------------------    |
| name*             | String            | "MyTable"                                     | Table name                                    |
| active            | boolean           | true                                          | is table active or not                        |
| enableKeep        | boolean           | false                                         | enable `@KEEP` section                        |
| extends           | String            | "com.mylib.myclass"                           | this DAO Class will extend to class           |
| implements        | Array of String   | \[ "com.mylib.myclass","com.mylib.myclass" \] | this DAO Class will implement to classes      |
| serializeable     | boolean           | true                                          | table will implement Serializeable class      |
| imports           | Array of String   | \[ "com.mylib.myclass", "$(PROJECT).R" \]     | this DAO Class will importing classes         |
| javadoc           | String            | "Sample"                                      | Javadoc generated                             |
| anotation         | String            | "SomeAnotation"                               | Will add `@SomeAnotation` before the class    |
| fields            | Field Array       | *See below                                    | *See below                                    |
| relations         | Relation Array    | *See below                                    | *See below                                    |


\* Note: `($PROJECT)` is magic word to be replaced with your project package name

### Field options
|        *Option*           | *Value Type*   |      *example*        |                               *info*                                  |
|:---------------------     |:-------------: |:-------------------   |:------------------------------------------------------------------    |
| name*                     | String         | "number"              | field name, `required`, and will be cammelized                        |
| type*                     | ENUM(Type)     | "long"                | field type, `required`                                                |
| customType                | String         | "com.mylib.myclass"   | this field will implement custom type of class                        |
| converter                 | String         | "com.mylib.myclass"   | this field will have custom converter of class                        |
| anotation                 | String         | "SomeAnotation"       | Will add `@SomeAnotation` before the property definition              |
| anotationGetter           | String         | "SomeAnotation"       | Will add `@SomeAnotation` before the getter method                    |
| anotationSetter           | String         | "SomeAnotation"       | Will add `@SomeAnotation` before the setter method                    |
| anotationGetterSetter     | String         | "SomeAnotation"       | Will add `@SomeAnotation` before the getter and setter method         |
| javadoc                   | String         | "doc"                 | Java doc generated for the property                                   |
| javadocGetter             | String         | "doc"                 | Java doc generated for the getter method                              |
| javadocSetter             | String         | "doc"                 | Java doc generated for the setter method                              |
| javadocGetterSetter       | String         | "doc"                 | Java doc generated for the getter and setter method                   |
| autoIncrement             | boolean        | true                  | `DEPRECATED` set field as autoIncrement, only use it to `id` field    |
| notNull                   | boolean        | true                  | allow null or not                                                     |
| unique                    | boolean        | true                  | set is field unique or not                                            |
| primary                   | ENUM(Direction)| true                  | is Index or not                                                       |
| index                     | ENUM(Direction)| true                  | is Index or not                                                       |
| defaultValue              | any            | 0.0                   | (jar ver. 0.0.8 or higher), set entity constructor default value      |

### ENUM(Type)
|        *Value*            | *Description*  |
|:---------------------     |:-------------  |
| id*                       | Long (automatic is primary) // required         |
| boolean                   | Integer 0 or 1     |
| bool                      | Integer 0 or 1     |
| bytearray                 | ByteArray          |
| byte                      | Byte               |
| date                      | Date               |
| double                    | Double             |
| float                     | Float              |
| int                       | Number             |
| integer                   | Number             |
| number                    | Number             |
| long                      | Long               |
| short                     | Short              |
| string                    | String             |
| text                      | String             |
| custom                    | Custom Class       |

\* please note that first field should be named `id` and have type `id`

### ENUM(Direction)
|        *Value*            | *Description*  |
|:---------------------     |:-------------  |
| asc                       | Ascending      |
| desc                      | Descending     |
| nonce                     | Not specified  |

### Relation options
|        *Option*           | *Value Type*   |      *example*        |                               *info*                                  |
|:---------------------     |:-------------: |:-------------------   |:------------------------------------------------------------------    |
| name*                     | String         | "Relation with X"     | Relationship name                                                     |
| target*                   | String         | "EntityX"             | Foreign entity name                                                   |
| type*                     | ENUM(RelationType) | "hasone"          | Relationship type                                                     |
| chainField*               | String         | "EntityXFieldY"       | Foreign entity field                                                  |
| anotation                 | String         | "SomeAnotation"       | Will add `@SomeAnotation` before the property definition              |
| anotationGetter           | String         | "SomeAnotation"       | Will add `@SomeAnotation` before the getter method                    |
| anotationSetter           | String         | "SomeAnotation"       | Will add `@SomeAnotation` before the setter method                    |
| anotationGetterSetter     | String         | "SomeAnotation"       | Will add `@SomeAnotation` before the getter and setter method         |
| javadoc                   | String         | "doc"                 | Java doc generated for the property                                   |
| javadocGetter             | String         | "doc"                 | Java doc generated for the getter method                              |
| javadocSetter             | String         | "doc"                 | Java doc generated for the setter method                              |
| javadocGetterSetter       | String         | "doc"                 | Java doc generated for the getter and setter method                   |

### ENUM(RelationType)
|        *Value*            | *Description*  |
|:---------------------     |:-------------  |
| hasone                    | Has One        |
| hasmany                   | Has Many       |

## Roadmap

### 1.0.2
1. Replace json parse with gson parser
2. Rewrote most codes

### 1.0.0
1. Migrate from gradle script to gradle plugin

### 0.0.8
1. Add default value to field
2. Put JavaDoc and Anotation in relationship field, getter, and setter

## To Do

1. Nothing, various improvement will be added when i have more ideas

## Documentation


## Tests


## Contribution

You are welcome to contribute by writing issues or pull requests.

You are also welcome to correct any spelling mistakes or any language issues, because my english is not perfect...


## License

Copyright (c) 2014 Decky Fx.

No License
