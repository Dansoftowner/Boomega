package com.dansoftware.libraryapp.main;

import javafx.scene.image.Image;

import java.util.List;

/**
 * This class gives ability for another parts of the application to access some
 * global information/object
 */
public final class Globals {

    /**
     * The libraryapp database file-extension
     */
    public static final String FILE_EXTENSION = "lbadb";

    /**
     * The current version-info object
     */
    public static final VersionInfo VERSION_INFO = new VersionInfo("0.0.0");

    /**
     * The 16px libraryapp icon's path.
     * The icon made by <a href="https://www.flaticon.com/authors/freepik" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
     * <p>
     * <a href="https://www.flaticon.com/free-icon/bookshelf_3100669?term=library&page=1&position=12">Go to website</a>
     */
    public static final String LOGO_16 = "/com/dansoftware/libraryapp/image/logo/bookshelf_16.png";

    /**
     * The 32px libraryapp icon's path.
     * The icon made by <a href="https://www.flaticon.com/authors/freepik" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
     * <p>
     * <a href="https://www.flaticon.com/free-icon/bookshelf_3100669?term=library&page=1&position=12">Go to website</a>
     */
    public static final String LOGO_32 = "/com/dansoftware/libraryapp/image/logo/bookshelf_32.png";

    /**
     * The 128px libraryapp icon's path.
     * The icon made by <a href="https://www.flaticon.com/authors/freepik" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
     * <p>
     * <a href="https://www.flaticon.com/free-icon/bookshelf_3100669?term=library&page=1&position=12">Go to website</a>
     */
    public static final String LOGO_128 = "/com/dansoftware/libraryapp/image/logo/bookshelf_128.png";

    /**
     * The 256px libraryapp icon's path.
     * The icon made by <a href="https://www.flaticon.com/authors/freepik" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
     * <p>
     * <a href="https://www.flaticon.com/free-icon/bookshelf_3100669?term=library&page=1&position=12">Go to website</a>
     */
    public static final String LOGO_256 = "/com/dansoftware/libraryapp/image/logo/bookshelf_256.png";

    /**
     * The 512px libraryapp icon's path.
     * The icon made by <a href="https://www.flaticon.com/authors/freepik" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
     * <p>
     * <a href="https://www.flaticon.com/free-icon/bookshelf_3100669?term=library&page=1&position=12">Go to website</a>
     */
    public static final String LOGO_512 = "/com/dansoftware/libraryapp/image/logo/bookshelf_512.png";

    public static List<Image> windowIconPack() {
        return List.of(
                new Image(LOGO_16),
                new Image(LOGO_32),
                new Image(LOGO_128),
                new Image(LOGO_256),
                new Image(LOGO_512)
        );
    }

    /**
     * Don't let anyone to create an instance of this class
     */
    private Globals() {
    }

}
