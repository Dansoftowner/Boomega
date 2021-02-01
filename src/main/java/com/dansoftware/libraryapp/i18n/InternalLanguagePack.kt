package com.dansoftware.libraryapp.i18n

import java.util.*

/**
 * An [InternalLanguagePack] is an abstract [LanguagePack] implementation
 * that represents a Language pack that is nested into the application by default.
 *
 * @author Daniel Gyorffy
 */
abstract class InternalLanguagePack(locale: Locale) : LanguagePack(locale) {

    companion object {
        private const val WINDOW_TITLES = "com.dansoftware.libraryapp.i18n.WindowTitles"
        private const val FIRST_TIME_DIALOG = "com.dansoftware.libraryapp.i18n.FirstTimeDialog"
        private const val UPDATE_DIALOG = "com.dansoftware.libraryapp.i18n.UpdateDialog"
        private const val PROGRESS_MESSAGES = "com.dansoftware.libraryapp.i18n.ProgressMessages"
        private const val GENERALS = "com.dansoftware.libraryapp.i18n.Generals"
        private const val BUTTON_TYPES = "com.dansoftware.libraryapp.i18n.ButtonTypes"
        private const val LOGIN_VIEW = "com.dansoftware.libraryapp.i18n.LoginView"
        private const val INFO_VIEW = "com.dansoftware.libraryapp.i18n.InfoView"
        private const val DATABASE_CREATOR = "com.dansoftware.libraryapp.i18n.DatabaseCreator"
        private const val PLUGIN_MANAGER = "com.dansoftware.libraryapp.i18n.PluginManager"
        private const val DATABASE_MANAGER = "com.dansoftware.libraryapp.i18n.DatabaseManager"
        private const val NOTIFICATIONS = "com.dansoftware.libraryapp.i18n.Notifications"
        private const val MENU_BAR = "com.dansoftware.libraryapp.i18n.MenuBar"
        private const val GOOGLE_BOOKS_IMPORT = "com.dansoftware.libraryapp.i18n.GoogleBooks"
        private const val RECORD_ADD_FORM = "com.dansoftware.libraryapp.i18n.RecordAddForm"
        private const val BOOK_VIEW = "com.dansoftware.libraryapp.i18n.RecordsView"
        private const val DOCK_SYSTEM = "com.dansoftware.libraryapp.i18n.DockSystem"
    }

    override fun getTranslator(): LanguageTranslator? {
        return LanguageTranslator("Dániel", "Györffy", "dansoftwareowner@gmail.com")
    }

    override fun getButtonTypeValues(): ResourceBundle = getBundle(BUTTON_TYPES)

    override fun getWindowTitles(): ResourceBundle = getBundle(WINDOW_TITLES)

    override fun getFirstTimeDialogValues(): ResourceBundle = getBundle(FIRST_TIME_DIALOG)

    override fun getProgressMessages(): ResourceBundle = getBundle(PROGRESS_MESSAGES)

    override fun getGenerals(): ResourceBundle = getBundle(GENERALS)

    override fun getUpdateDialogValues(): ResourceBundle = getBundle(UPDATE_DIALOG)

    override fun getLoginViewValues(): ResourceBundle = getBundle(LOGIN_VIEW)

    override fun getInfoViewValues(): ResourceBundle = getBundle(INFO_VIEW)

    override fun getDatabaseCreatorValues(): ResourceBundle = getBundle(DATABASE_CREATOR)

    override fun getPluginManagerValues(): ResourceBundle = getBundle(PLUGIN_MANAGER)

    override fun getDatabaseManagerValues(): ResourceBundle = getBundle(DATABASE_MANAGER)

    override fun getNotificationMessages(): ResourceBundle = getBundle(NOTIFICATIONS)

    override fun getMenuBarValues(): ResourceBundle = getBundle(MENU_BAR)

    override fun getGoogleBooksValues(): ResourceBundle = getBundle(GOOGLE_BOOKS_IMPORT)

    override fun getRecordAddFormValues(): ResourceBundle = getBundle(RECORD_ADD_FORM)

    override fun getRecordsViewValues(): ResourceBundle = getBundle(BOOK_VIEW)

    override fun getDockSystemValues(): ResourceBundle = getBundle(DOCK_SYSTEM)
}