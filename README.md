# CPS NPMRC Manager
Tool that makes it easy to track and switch between multiple .npmrc configurations - referred to as npmrc profiles.

## Usage
### Installation
Binary distributions are tracked here via GitHub releases (scroll down for a description of supported platforms / arches). Download, extract, put the `npmrcm` executable on your PATH, and it's ready for use.

### Setup
Run `npmrcm setup` to run through the guided configuration. If you have an existing .npmrc file, you will be asked to give it a name, and it will be recorded as a profile - otherwise, a default profile will be created with a .npmrc file pointing to the npm central registry.

After this short setup is done, the tool is ready for use. Use `npmrcm --help` to list available options, or `npmrcm --version` to view the version of the installed tool.

### Adding Profiles
If you want to add a new profile, create a file with a name of your liking within the `~/.npmrcm/profiles` directory. The name of the file will be treated as the npmrc profile name, and the content should be a valid .npmrc configuration.

### Profile Commands
#### `npmrcm list`
Lists available profiles and shows which one(s) are active. Use the `--verbose` / `-v` flag for a more detailed view.

#### `npmrcm active`
View the currently active profile(s).

The active profile is decided by comparing the content of the current .npmrc file to the contents of profile files in the `~/.npmrcm/profiles` directory.

#### `npmrcm switch <profile name>`
Switch to the specified target profile. `<profile name>` should be a valid profile name from the `npmrcm list` command.

Switching will grab the contents of the target profile from the `~/.npmrcm/profiles` directory, and replace the current .npmrc contents with it.

## Releases
Releases are tracked via GitHub releases.

The following platforms are supported:

| Platform  | Architecture(s) |
|-----------|-----------------|
| *Windows* | x64             |
| *Linux*   | x64, arm64      |
| *MacOS*   | arm64           |


Some platforms have specific requirements to make the executables runnable.

| Platform | Requirement                                                                                                                                     |
|----------|-------------------------------------------------------------------------------------------------------------------------------------------------|
| Windows  | ✔️ The npmrcm.exe file is runnable as is after extraction.                                                                                       |
| Linux    | ✔️ / ❗ After extraction, there may be a need to run `sudo chmod +x /path/to/extracted/npmrcm`                                                   |
| MacOS    | ❗ Extracted root folder needs to be removed from quarantine by running `sudo xattr -r -d com.apple.quarantine /path/to/extracted/npmrcm`        |

## Build
If you want to build the tool yourself for your own platform, you will need the following:

- JDK 17 _(optional if using GraalVM integrated JDK)_
- GraalVM - any recent release for Java 17 _(set the `GRAALVM_HOME` environment variable if you don't want to mess up your `JAVA_HOME` to use the integrated JDK of GraalVM)_
- Maven 3.9.4 _(any recent version is fine)_
- Various platform-specific development kits - GraalVM will report during build if anything additional is needed _(these can usually be installed simply with a package manager for your platform)_

Once the required tools are in place, `cd` into the root directory of the project, and run `mvn -Pnative package`. During build, a `target` directory will be generated, containing the `npmrcm` executable for your platform.
