# Boomega unreleased

**Date:** UNKNOWN

### Enhancements/features

* 💥 Refactored database api for allowing integrating other database managements systems into the app
* ⬆️Migrate to JDK 17
* ✨ MacOS specific menu-bar
* ✨ [#161](https://github.com/Dansoftowner/Boomega/issues/161): 'Maximize' item in window menu
* 💄 Look and feel updates
* 💄 New application icon

### Bug fixes/improvements

* 🐛 [#158](https://github.com/Dansoftowner/Boomega/issues/158): Json export does not serialize languages
* 🐛 [#113](https://github.com/Dansoftowner/Boomega/issues/113): Incorrect login data saving strategy
* 🐛 Native MacOS menubar insertion fails
* 🐛 Progress indication does not stop after searching for updates

> **Warning:** Your data saved in databases created in previous versions might not show up
> due to new retrieving process.

# Boomega 0.7.5 (pre-release)

**Date:** 2021-08-22

### Enhancements/features

* ✨ Exporting to **JSON** & **Excel Spreadsheet format** (see [#41](https://github.com/Dansoftowner/Boomega/issues/41))
* ✨💄 Updated Google Book Details Panel (Closes [#126](https://github.com/Dansoftowner/Boomega/issues/126))
* ✨ [#150](https://github.com/Dansoftowner/Boomega/issues/150): Ability to copy the description in Google Book details pane
* ✨ [#110](https://github.com/Dansoftowner/Boomega/issues/150): Records view configuration panel
* ✨ [#104](https://github.com/Dansoftowner/Boomega/issues/104): Duplicate record item
* ✨ [#94](https://github.com/Dansoftowner/Boomega/issues/94): Showing key combinations in the tooltips of the toolbar items
* 💥 [#152](https://github.com/Dansoftowner/Boomega/issues/152): New Update detection policy
* ✨ [#23](https://github.com/Dansoftowner/Boomega/issues/23): Handling duplicate file in UpdateDialog
* ✨ [#8](https://github.com/Dansoftowner/Boomega/issues/8): Saving the time of the last update-searching
* ✨ Hiding docks when no items selected in records-view
* ✨ Disable insert-item when the find-dialog is showing in records-view
* 💄 Look and feel updates

### Bug fixes/improvements

* 🐛 [#136](https://github.com/Dansoftowner/Boomega/issues/136): Record table sorting does not work
* 🐛 [#151](https://github.com/Dansoftowner/Boomega/issues/151): Can't replace record attributes with empty strings.

> Note: your column-configurations might be broken after updating to this release

# Boomega v0.7.0 (pre-release)

**Date:** 2020-07-20

### Enhancements/features

* ✨ [#147](https://github.com/Dansoftowner/Boomega/issues/147): Global toolbar in database view
* 💥 New Google Books Import View (Closes [#11](https://github.com/Dansoftowner/Boomega/issues/11), [#13](https://github.com/Dansoftowner/Boomega/issues/13) and [#10](https://github.com/Dansoftowner/Boomega/issues/10))
* ✨ [#14](https://github.com/Dansoftowner/Boomega/issues/14): Google Book preview panel tells the user if the preview is not available
* ✨ [#149](https://github.com/Dansoftowner/Boomega/issues/149): Google Book Preview toolbar
* ✨ Google Book Preview is now displayed in a tab
* ✨ New way of joining existing records with Google Books
* 💄 Updated look and feel, more animations
* ✨ Showing dialogs for uncaught exceptions
* ✨ Context menu for `database view` tabs that allows to access some basic tab-operation utility (like closing all tabs etc...)
* ✨ More precise failed login messages
* ✨ Showing total items/selected items in record-view's toolbar

### Bug fixes/improvements

* 🐛 [#146](https://github.com/Dansoftowner/Boomega/issues/146): UnsatisfiedLinkError when running the program as a native app on Windows
* 🐛 [#148](https://github.com/Dansoftowner/Boomega/issues/148): Google book information overlay - minimum height is too low
* 🐛 Database view module configurations are not saved when restarting the program
* 🐛 Closing a non auto-login database removes the auto-login database as well
* 🐛 Google Books Table: "publisher" column called "subtitle"
* 🐛 Record Table: missing "author" column

# Boomega v0.6.5 (pre-release)

**Date:** 2021-06-22

_First public release_