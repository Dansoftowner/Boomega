package com.dansoftware.libraryapp.gui.util;

import com.dansoftware.libraryapp.locale.I18N;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link LibraryAppStage} is a {@link Stage} implementation that
 * supports internationalized titles and automatically adds the libraryapp icon-bundle.
 *
 * @author Daniel Gyorffy
 */
public abstract class LibraryAppStage extends Stage {

    /**
     * The 16px libraryapp icon's path.
     * The icon made by <a href="https://www.flaticon.com/authors/freepik" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
     * <p>
     * <a href="https://www.flaticon.com/free-icon/bookshelf_3100669?term=library&page=1&position=12">Go to website</a>
     */
    private static final String LOGO_16 = "/com/dansoftware/libraryapp/image/logo/bookshelf_16.png";

    /**
     * The 32px libraryapp icon's path.
     * The icon made by <a href="https://www.flaticon.com/authors/freepik" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
     * <p>
     * <a href="https://www.flaticon.com/free-icon/bookshelf_3100669?term=library&page=1&position=12">Go to website</a>
     */
    private static final String LOGO_32 = "/com/dansoftware/libraryapp/image/logo/bookshelf_32.png";

    /**
     * The 128px libraryapp icon's path.
     * The icon made by <a href="https://www.flaticon.com/authors/freepik" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
     * <p>
     * <a href="https://www.flaticon.com/free-icon/bookshelf_3100669?term=library&page=1&position=12">Go to website</a>
     */
    private static final String LOGO_128 = "/com/dansoftware/libraryapp/image/logo/bookshelf_128.png";

    /**
     * The 256px libraryapp icon's path.
     * The icon made by <a href="https://www.flaticon.com/authors/freepik" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
     * <p>
     * <a href="https://www.flaticon.com/free-icon/bookshelf_3100669?term=library&page=1&position=12">Go to website</a>
     */
    private static final String LOGO_256 = "/com/dansoftware/libraryapp/image/logo/bookshelf_256.png";

    /**
     * The 512px libraryapp icon's path.
     * The icon made by <a href="https://www.flaticon.com/authors/freepik" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
     * <p>
     * <a href="https://www.flaticon.com/free-icon/bookshelf_3100669?term=library&page=1&position=12">Go to website</a>
     */
    private static final String LOGO_512 = "/com/dansoftware/libraryapp/image/logo/bookshelf_512.png";

    protected LibraryAppStage() {
        super();
        getIcons().addAll(
                new Image(LOGO_16),
                new Image(LOGO_32),
                new Image(LOGO_128),
                new Image(LOGO_256),
                new Image(LOGO_512)
        );
    }

    protected LibraryAppStage(@NotNull String i18n) {
        this();
        setTitle(I18N.getWindowTitles().getString(i18n));
    }

    protected LibraryAppStage(@NotNull String i18n, @NotNull Parent content) {
        this(i18n);
        setScene(new Scene(content));
    }

}
