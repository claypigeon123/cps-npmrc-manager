### Build for platform
```
mvn -Pnative clean package
```

### Build for Linux in Docker
```
docker build --tag cps-npmrcmanager-builder:0.1.0 -f ./Dockerfile_linux .
docker run --rm -v "G:\side-hustles\cps-npmrc-manager\target\linux:/opt/output" cps-npmrcmanager-builder:0.1.0
```