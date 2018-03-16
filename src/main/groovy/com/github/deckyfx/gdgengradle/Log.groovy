package com.github.deckyfx.gdgengradle

import java.util.regex.Pattern

/**
 * Created by 1412 on 11/13/2016.
 */

class Log {
    static void log(String s){
        println("GreenDaoGradle Generator > " + s)
    }

    static void log(Throwable e){
        println("Error occured: " + e.getMessage())
        println("--------------")
        println(e.getStackTrace())
        println("--------------")
    }
}
