# Required JVM options

```
--add-exports javafx.base/com.sun.javafx=ALL-UNNAMED  
--add-exports javafx.base/com.sun.javafx.runtime=ALL-UNNAMED
--add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED
--add-exports javafx.base/com.sun.javafx=ALL-UNNAMED
--add-exports javafx.controls/com.sun.javafx.scene.control.skin.resources=ALL-UNNAMED
```
```
--add-exports javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED 
--add-exports javafx.graphics/com.sun.glass.ui=ALL-UNNAMED
--add-opens java.base/java.io=ALL-UNNAMED
```
```
--add-opens javafx.graphics/javafx.scene.text=ALL-UNNAMED 
--add-exports javafx.graphics/com.sun.javafx.text=ALL-UNNAMED 
--add-exports javafx.graphics/com.sun.javafx.scene.text=ALL-UNNAMED 
--add-opens javafx.graphics/com.sun.javafx.text=ALL-UNNAMED 
--add-exports javafx.graphics/com.sun.javafx.geom=ALL-UNNAMED
```