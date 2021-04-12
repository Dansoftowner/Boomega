# Contributing to Boomega

## Writing code

You can use both `Java` and `Kotlin` for writing your code.  

Use the default formatting of your IDE for the code you write.  
Only format code that you are modifying, do not format entire classes at a time!  

You can use any JDK `11+` for building the app but make sure that you write code compatible with `Java 11`.
In **Intellij**, go to `File > Project structure > Project settings > Project > Project language level` and set `11` as the language level.
This will prevent you from writing code that's not compatible with the Java 11 specification.

## Working with JavaFX

In this project `fxml` shouldn't be used. Build your UIs from code.  
The stylesheets (`.css`) should be located in the `resources/.../gui/theme/css` directory.

## Commit messages

Use appropriate [gitmojies](https://gitmoji.dev/) in your commit messages.

Examples:  
`:sparkless: New record editor structure`  
`:lipstick: Update styles`  
`:bug: Fixed incorrect configuration saving policy`  