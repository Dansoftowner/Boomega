# Distribution guideline

For creating installers/native application images, run `gradlew jpackage`.

## Requirements

Required third-party software (by the `jpackage tool`):
* On **Windows**: install [Wix Toolset](https://wixtoolset.org/)
* On **Linux**: `rpm-build` (on RedHat) or `fakeroot` (on Ubuntu) packages
* On **Mac**: The `Xcode command line tools`

Required items:

* `distribution/file-associations/*` - for file association configurations
* `distribution/icon/*` - for the distribution icons

## Output

* `distribution/build` - contains the one primary image/installer according to the platform 
  (usually **.exe** for Windows and **.deb** for Linux Debian)
* `distribution/temp` - contains other types of images/installers (like **.msi** for Windows), and the directory containing the raw image 