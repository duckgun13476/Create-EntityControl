# Create Entity Control

Create Entity Control is maintained as a single multi-version Gradle workspace.

## Version Projects

- `:forge-1.19.2-0.5.1i` -> `project/forge/1.19.2-0.5.1i`
- `:forge-1.20.1-0.5.1j` -> `project/forge/1.20.1-0.5.1j`
- `:forge-1.20.1-6.0.x` -> `project/forge/1.20.1-6.0.x`
- `:neoforge-1.21.1` -> `project/neoforge/1.21.1`

## Common Commands

```powershell
.\gradlew.bat compileJavaAllVersions --console=plain --no-daemon
.\gradlew.bat :forge-1.20.1-6.0.x:compileJava --console=plain --no-daemon
.\gradlew.bat jarAllVersions --console=plain --no-daemon
```

Each version subproject keeps its own `gradle.properties`, source tree, resources, and publish metadata.
