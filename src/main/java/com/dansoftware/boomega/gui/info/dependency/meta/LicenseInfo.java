/*
 * Boomega
 * Copyright (C)  2021  Daniel Gyoerffy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dansoftware.boomega.gui.info.dependency.meta;

import java.util.Optional;

/**
 * A {@link LicenseInfo} represents a software-license and holds the information of it.
 *
 * @author Daniel Gyorffy
 */
public class LicenseInfo {

    private final String name;
    private final String websiteUrl;

    public LicenseInfo(String name, String websiteUrl) {
        this.name = name;
        this.websiteUrl = websiteUrl;
    }

    public String getName() {
        return name;
    }

    public Optional<String> getWebsiteUrl() {
        return Optional.ofNullable(websiteUrl);
    }

    public static LicenseInfo lgpLv2() {
        return new LicenseInfo("GNU Library or Lesser General Public License version 2.0", "https://www.gnu.org/licenses/old-licenses/lgpl-2.1.html");
    }

    /**
     * Creates a {@link LicenseInfo} that represents the <b>GNU General Public License with the Classpath Exception</b>
     *
     * @return the LicenseInfo object
     */
    public static LicenseInfo gnu2ClassPath() {
        return new LicenseInfo("GPL v2 + Classpath", "http://openjdk.java.net/legal/gplv2+ce.html");
    }

    /**
     * Creates a {@link LicenseInfo} that represents a MIT License.
     *
     * @return the LicenseInfo object
     */
    public static LicenseInfo mitLicense() {
        return new LicenseInfo("MIT License", "https://opensource.org/licenses/MIT");
    }

    /**
     * Creates a {@link LicenseInfo} that represents an Apache 2.0 license.
     *
     * @return the LicenseInfo object
     */
    public static LicenseInfo apache20License() {
        return new LicenseInfo("Apache 2.0", "https://www.apache.org/licenses/LICENSE-2.0");
    }

    /**
     * Creates a {@link LicenseInfo} that represents a WTFPL license.
     *
     * @return the LicenseInfo object
     */
    public static LicenseInfo wtfplLicense() {
        return new LicenseInfo("WTFPL License", null);
    }
}
