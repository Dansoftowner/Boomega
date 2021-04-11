# Required JVM options

## Compiler options

These options are required for compiling the application.  
These are specified in the `build.gradle` script in the compiler tasks.

```
--add-exports javafx.base/com.sun.javafx=ALL-UNNAMED  
--add-exports javafx.base/com.sun.javafx.runtime=ALL-UNNAMED
--add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED
--add-exports javafx.base/com.sun.javafx=ALL-UNNAMED
--add-exports javafx.controls/com.sun.javafx.scene.control.skin.resources=ALL-UNNAMED
```

> Note: after `Java 16` these options should also be specified as JVM options

## Runtime JVM options

These options should be specified if the JVM is version `16` or later.
```
--add-exports javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED 
--add-exports javafx.graphics/com.sun.glass.ui=ALL-UNNAMED
--add-opens java.base/java.io=ALL-UNNAMED
```