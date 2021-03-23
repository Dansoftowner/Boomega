package com.dansoftware.boomega.gui.login

import com.dansoftware.boomega.appdata.logindata.LoginData
import com.dansoftware.boomega.db.Credentials
import com.dansoftware.boomega.db.DatabaseMeta
import com.dansoftware.boomega.gui.context.Context
import com.dansoftware.boomega.gui.entry.DatabaseTracker
import com.dansoftware.boomega.gui.util.refresh
import com.dansoftware.boomega.i18n.I18N
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.beans.property.*
import javafx.beans.value.ObservableStringValue
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Group
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class LoginBox(private val controller: Controller) : VBox(10.0) {

    private val itemSelected: BooleanProperty = SimpleBooleanProperty()
    private val usernameInput: StringProperty = SimpleStringProperty()
    private val passwordInput: StringProperty = SimpleStringProperty()
    private val remember: BooleanProperty = SimpleBooleanProperty()
    private val databaseChooser: ObjectProperty<ComboBox<DatabaseMeta>> = SimpleObjectProperty()

    var selectedItem: DatabaseMeta?
    get() = databaseChooser.get().selectionModel.selectedItem
    set(value) { select(value) }

    init {
        this.styleClass.add("login-box")
        this.maxWidth = 650.0
        this.prefWidth = 550.0
        this.buildUI()
        controller.loginBox = this
    }

    fun titleProperty(): ObservableStringValue = databaseChooser.get().selectionModel.selectedItemProperty().asString()

    fun refresh() {
        databaseChooser.get().refresh()
    }

    fun addItem(item: DatabaseMeta) {
        databaseChooser.get().items.add(item)
    }

    fun removeItem(item: DatabaseMeta) {
        databaseChooser.get().items.remove(item)
    }

    fun select(databaseMeta: DatabaseMeta?) {
        databaseChooser.get().selectionModel.select(databaseMeta)
    }

    fun addSelectedItemListener(listener: (DatabaseMeta?) -> Unit) {
        databaseChooser.get().selectionModel.selectedItemProperty().addListener { _, _, it -> listener(it) }
    }

    fun fillForm(loginData: LoginData) {
        databaseChooser.get().let { databaseChooser ->
            databaseChooser.items.addAll(loginData.savedDatabases)
            loginData.selectedDatabase?.let(databaseChooser.selectionModel::select)
            loginData.autoLoginDatabase?.let {
                remember.set(true)
                loginData.autoLoginCredentials?.run {
                    usernameInput.set(username)
                    passwordInput.set(password)
                }
            }
        }
    }

    private fun buildUI() {
        children.add(buildHeader())
        children.add(buildDatabaseChooserArea())
        children.add(buildForm())
        children.add(Separator())
        children.add(buildDataSourceButton())
    }

    private fun buildHeader() = StackPane().apply {
        styleClass.add("header")
        padding = Insets(20.0)
        HBox(10.0).run {
            children.add(ImageView().apply { styleClass.add("logo") })
            children.add(StackPane(Label(System.getProperty("app.name"))))
            Group(this)
        }.let(children::add)
    }

    private fun buildDatabaseChooserArea() = HBox(5.0).apply {
        children.add(buildComboBox())
        children.add(buildFileChooserButton())
        children.add(buildDatabaseManagerButton())
        setMargin(this, Insets(0.0, 20.0, 0.0, 20.0))
    }

    private fun buildComboBox() = ComboBox<DatabaseMeta>().apply {
        databaseChooser.set(this)
        minHeight = 35.0
        minWidth = 355.0
        maxWidth = Double.MAX_VALUE
        promptText = I18N.getValue("login.source.combo.promt")
        buttonCell = ComboBoxButtonCell(controller.databaseTracker)
        setCellFactory { DatabaseChooserItem(controller.databaseTracker) }
        itemSelected.bind(selectionModel.selectedItemProperty().isNotNull)
        HBox.setHgrow(this, Priority.ALWAYS)
    }

    private fun buildFileChooserButton() = Button().apply {
        tooltip = Tooltip(I18N.getValue("login.source.open"))
        graphic = FontAwesomeIconView(FontAwesomeIcon.FOLDER_OPEN)
        contentDisplay = ContentDisplay.GRAPHIC_ONLY
        minHeight = 35.0
        minWidth = 40.0
        setOnAction {
            controller.openFile()
        }
    }

    private fun buildDatabaseManagerButton() = Button().apply {
        tooltip = Tooltip(I18N.getValue("login.db.manager.open"))
        graphic = MaterialDesignIconView(MaterialDesignIcon.DATABASE)
        contentDisplay = ContentDisplay.GRAPHIC_ONLY
        minHeight = 35.0
        minWidth = 40.0
        setOnAction {
            controller.openDatabaseManager()
        }
    }

    private fun buildDataSourceButton() = Button().apply {
        minHeight = 35.0
        styleClass.add("source-adder")
        text = I18N.getValue("login.add.source")
        //prefWidthProperty().bind(this@LoginBox.widthProperty())
        maxWidth = Double.MAX_VALUE
        graphic = MaterialDesignIconView(MaterialDesignIcon.DATABASE_PLUS)
        setOnAction {
            controller.openDatabaseCreator()
        }
    }

    private fun buildForm() = VBox(10.0).apply {
        children.add(Separator())
        children.add(buildUsernameInput())
        children.add(buildPasswordInput())
        children.add(buildCheckBox())
        children.add(buildLoginButton())
        managedProperty().bind(itemSelected)
        visibleProperty().bind(itemSelected)
        setMargin(this, Insets(0.0, 20.0, 20.0, 20.0))
    }

    private fun buildUsernameInput() = TextField().apply {
        minHeight = 35.0
        prefColumnCount = 10
        promptText = I18N.getValue("login.form.username.prompt")
        usernameInput.bindBidirectional(textProperty())
        textProperty().bindBidirectional(usernameInput)
    }

    private fun buildPasswordInput() = PasswordField().apply {
        minHeight = 35.0
        prefColumnCount = 10
        promptText = I18N.getValue("login.form.password.prompt")
        passwordInput.bindBidirectional(textProperty())
        textProperty().bindBidirectional(passwordInput)
    }

    private fun buildCheckBox() = CheckBox().apply {
        alignment = Pos.CENTER_RIGHT
        text = I18N.getValue("login.form.remember")
        remember.bindBidirectional(selectedProperty())
        selectedProperty().bindBidirectional(remember)
    }

    private fun buildLoginButton() = Button().apply {
        minHeight = 35.0
        maxWidth = Double.MAX_VALUE
        text = I18N.getValue("login.form.login")
        isDefaultButton = true
        setOnAction {
            databaseChooser.get().selectionModel.selectedItem?.let { dbMeta ->
                controller.login(
                    dbMeta, Credentials(
                        usernameInput.get().let(StringUtils::trim),
                        passwordInput.get().let(StringUtils::trim)
                    ), remember.get()
                )
            }
        }
    }

    private open class DatabaseChooserItem(
        private val databaseTracker: DatabaseTracker
    ) : ListCell<DatabaseMeta?>() {

        companion object {
            private const val NOT_EXISTS_CLASS = "state-indicator-file-not-exists"
            private const val USED_CLASS = "state-indicator-used"
        }

        init {
            maxWidth = 650.0
        }

        override fun updateItem(item: DatabaseMeta?, empty: Boolean) {
            super.updateItem(item, empty)
            when {
                item == null || empty -> {
                    text = null
                    graphic = null
                }
                else -> {
                    text = item.toString()
                    val dbFile = item.file
                    when {
                        dbFile.exists().not() || dbFile.isDirectory -> {
                            tooltip = Tooltip(I18N.getValue("file.not.exists"))
                            graphic = FontAwesomeIconView(FontAwesomeIcon.WARNING).apply {
                                styleClass.add(NOT_EXISTS_CLASS)
                            }
                        }
                        databaseTracker.isDatabaseUsed(item) -> {
                            tooltip = Tooltip(I18N.getValue("database.currently.used"))
                            graphic = FontAwesomeIconView(FontAwesomeIcon.PLAY).apply {
                                styleClass.add(USED_CLASS)
                            }
                        }
                        else -> {
                            graphic = null
                            tooltip = null
                        }
                    }
                }
            }
        }
    }

    private class ComboBoxButtonCell(databaseTracker: DatabaseTracker) : DatabaseChooserItem(databaseTracker) {
        override fun updateItem(item: DatabaseMeta?, empty: Boolean) {
            super.updateItem(item, empty)
            if (item === null) {
                text = I18N.getValue("login.source.combo.promt")
            }
        }
    }

    interface Controller {
        var loginBox: LoginBox?

        val context: Context
        val databaseTracker: DatabaseTracker
        val loginData: LoginData

        fun openDatabaseManager()
        fun openFile()
        fun openDatabaseCreator()
        fun login(databaseMeta: DatabaseMeta, credentials: Credentials, remember: Boolean)
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(LoginBox::class.java)
    }
}
