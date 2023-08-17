# CPS NPMRC Manager
Tool that makes it easy to track and switch between multiple .npmrc configurations - referred to as .npmrc profiles.

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
| Windows  | ✔️ The npmrcm.exe file is runnable as is after extraction.                                                                                      |
| Linux    | ❗ After extraction, run `sudo chmod +x /path/to/npmrcm`                                                                                         |
| MacOS    | ❗ Extracted root folder needs to be removed from quarantine by running `sudo xattr -r -d com.apple.quarantine /path/to/extracted/npmrcm/folder` |


## Build

### Build for current platform
```
mvn -Pnative clean package
```

### Build for Linux in Docker
```
docker build --tag cps-npmrcmanager-builder:1.0.0 -f ./Dockerfile_linux .
docker run --rm -v "REPLACE_ME_TO_DESIRED_PATH:/opt/output" cps-npmrcmanager-builder:1.0.0
```