package com.dansoftware.libraryapp.locale

import java.util.*

/**
 * An [InternalLanguagePack] is an abstract [LanguagePack] implementation
 * that represents a Language pack that is nested into the application by default.
 *
 * @author Daniel Gyorffy
 */
abstract class InternalLanguagePack(locale: Locale) : LanguagePack(locale) {

    companion object {
        private const val WINDOW_TITLES = "com.dansoftware.libraryapp.locale.WindowTitles"
        private const val FIRST_TIME_DIALOG = "com.dansoftware.libraryapp.locale.FirstTimeDialog"
        private const val UPDATE_DIALOG = "com.dansoftware.libraryapp.locale.UpdateDialog"
        private const val PROGRESS_MESSAGES = "com.dansoftware.libraryapp.locale.ProgressMessages"
        private const val GENERALS = "com.dansoftware.libraryapp.locale.Generals"
        private const val BUTTON_TYPES = "com.dansoftware.libraryapp.locale.ButtonTypes"
        private const val LOGIN_VIEW = "com.dansoftware.libraryapp.locale.LoginView"
        private const val INFO_VIEW = "com.dansoftware.libraryapp.locale.InfoView"
        private const val DATABASE_CREATOR = "com.dansoftware.libraryapp.locale.DatabaseCreator"
        private const val PLUGIN_MANAGER = "com.dansoftware.libraryapp.locale.PluginManager"
        private const val DATABASE_MANAGER = "com.dansoftware.libraryapp.locale.DatabaseManager"
        private const val NOTIFICATIONS = "com.dansoftware.libraryapp.locale.Notifications"
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

    override fun getNotificationMessages(): ResourceBundle = getBundle(NOTIFICATIONS);
}