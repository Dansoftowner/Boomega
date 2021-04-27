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
* `distribution/temp` - other images/installers and the raw image