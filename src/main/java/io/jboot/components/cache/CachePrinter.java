package io.jboot.components.cache;

public interface CachePrinter {

    default void println(String debugInfo){
        System.out.println(debugInfo+"\n");
    }

}
