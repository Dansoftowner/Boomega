# Contributing to Boomega

## To do
You can look at the [issues](https://github.com/Dansoftowner/Boomega/issues) list if you want to fix/add something.
Also, you can look for `// TODO:` messages in the source code.

## Environment
You can use any JDK `17` (recommended: [OpenJDK](https://jdk.java.net/17/)).  
The recommended IDE for this project is Intellij.

## Writing code

Please commit your changes into the `dev` branch.

You can use both `Java` and `Kotlin` for writing your code.  

Use the default formatting of your IDE for the code you write.  
Only format code that you are modifying, do not format entire classes at a time!  

Consider using [Jetbrains Annotations](https://www.jetbrains.com/help/idea/annotating-source-code.html#bundled-annotations) 
in your code, especially if you use Java, the `@NotNull` and `@Nullable` annotations are super-helpful.

Also, check out:
* [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
* [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)

### Adding dependencies

If you add new dependencies to the project, please also mention that in the [acknowledgements](ACKNOWLEDGEMENTS.md) page.

## Commit messages

Use appropriate [gitmojies](https://gitmoji.dev/) in your commit messages.

Examples:  
`:sparkless: New record editor structure`  
`:lipstick: Update styles`  
`:bug: Fixed incorrect configuration saving policy`  
`:memo: Update README`