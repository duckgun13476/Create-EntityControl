# Create: Entity Control

Create: Entity Control is a Create add-on for server-side control of moving contraptions.
It adds configurable checks for contraption assembly and movement, plus a small operator command for safely disassembling a targeted Create contraption.

## Features

- Configure blocks that cannot be moved by Create contraptions.
- Configure per-block count limits for contraptions.
- Configure X/Z and Y span limits for assembled contraptions.
- Configure crush behavior through uncrushable and crushable block lists.
- Optionally calculate a stability value for assembled contraptions.
- Detect nearby contraption clusters and apply aggregate limit checks.
- Exclude Create train carriage contraptions from cluster length checks.
- Protect vanilla minecarts from fire/lava damage and reduce non-player damage.
- Add a dedicated maximum rotation speed cap for Mechanical Bearings.
- Provide optional OPAC compatibility hooks when Open Parties and Claims is present.
- Provide `/cec blockify` and `/createentitycontrol blockify` for operators to disassemble the targeted non-train Create contraption after confirmation.

## Supported Builds

| Loader | Minecraft | Create target |
| --- | --- | --- |
| Forge | 1.19.2 | 0.5.1i |
| Forge | 1.20.1 | 0.5.1j |
| Forge | 1.20.1 | 6.0 |
| NeoForge | 1.21.1 | 6.0.x |
| Fabric | 1.19.2 | 0.5.1i |
| Fabric | 1.20.1 | 6.0.x |

## Commands

`/cec blockify`

Finds the Create contraption in the operator's crosshair and asks for confirmation.

`/cec blockify confirm`

Disassembles the pending targeted contraption. The confirmation expires after 30 seconds.

`/cec blockify cancel`

Cancels the pending operation.

The same subcommands are also available under `/createentitycontrol`.
Train carriage contraptions are intentionally not supported by this command.

## Configuration

The mod exposes loader config entries for:

- `blocks_limit`
- `blocks_unmoved`
- `blocks_uncrushable`
- `blocks_crushable`
- `blocks_ignore`
- `block entity max length XZ`
- `block entity max length Y`
- `calculate block stabilize para`
- `block entity max stabilize para`
- `minecart improve`
- `mechanical bearing gear max speed`
- contraption cluster scan and notification settings

Block selectors can use block ids such as `minecraft:stone`; tag selectors are also supported by prefixing the id with `#`.

## Development

This repository is maintained as one multi-version Gradle workspace.

```powershell
.\gradlew.bat compileJavaAllVersions --console=plain --no-daemon
.\gradlew.bat allworkJarJarAllVersions --console=plain --no-daemon
.\gradlew.bat publishInfoAllVersions --console=plain --no-daemon
```

The publishable subprojects are:

- `:forge-1.19.2-0.5.1i`
- `:forge-1.20.1-0.5.1j`
- `:forge-1.20.1-6.0.x`
- `:neoforge-1.21.1`
- `:fabric-1.19.2`
- `:fabric-1.20.1`

Release notes are kept in `update/changelog.md`.
