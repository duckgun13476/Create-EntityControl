# Create: Entity Control

Create: Entity Control 是一个面向服务器管理的 Create 附属模组，用于给移动结构的装配和移动过程增加可配置限制，并提供一个管理员使用的结构方块化命令。

## 功能

- 配置不能被 Create 结构移动的方块。
- 配置结构内特定方块的数量上限。
- 配置结构在 X/Z 和 Y 方向上的最大跨度。
- 通过不可压碎/可压碎列表调整结构挤压方块时的处理方式。
- 可选启用结构稳定值计算。
- 检测附近多个移动结构形成的结构簇，并应用聚合限制。
- 结构簇长度检查会排除 Create 列车车厢，避免长列车被误拦截。
- 保护原版矿车免受火焰/岩浆伤害，并降低非玩家来源伤害。
- 为机械轴承提供独立的最大旋转速度上限。
- 当安装 Open Parties and Claims 时，提供可选兼容钩子。
- 提供 `/cec blockify` 和 `/createentitycontrol blockify`，允许管理员在确认后将准星指向的非列车 Create 结构解体回方块。

## 支持版本

| 加载器 | Minecraft | Create 目标版本 |
| --- | --- | --- |
| Forge | 1.19.2 | 0.5.1i |
| Forge | 1.20.1 | 0.5.1j |
| Forge | 1.20.1 | 6.0 |
| NeoForge | 1.21.1 | 6.0.x |
| Fabric | 1.19.2 | 0.5.1i |
| Fabric | 1.20.1 | 6.0.x |

## 命令

`/cec blockify`

查找管理员准星中的 Create 移动结构，并要求二次确认。

`/cec blockify confirm`

确认并解体待处理结构。确认会在 30 秒后过期。

`/cec blockify cancel`

取消待处理操作。

同样的子命令也可通过 `/createentitycontrol` 使用。
列车车厢结构不支持使用该命令。

## 配置

模组提供以下主要配置项：

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
- 结构簇扫描与提示相关配置

方块选择器支持 `minecraft:stone` 这样的方块 id，也支持在 id 前加 `#` 使用方块标签。

## 开发

本仓库使用一个多版本 Gradle 工作区维护所有发布目标。

```powershell
.\gradlew.bat compileJavaAllVersions --console=plain --no-daemon
.\gradlew.bat allworkJarJarAllVersions --console=plain --no-daemon
.\gradlew.bat publishInfoAllVersions --console=plain --no-daemon
```

当前发布目标：

- `:forge-1.19.2-0.5.1i`
- `:forge-1.20.1-0.5.1j`
- `:forge-1.20.1-6.0.x`
- `:neoforge-1.21.1`
- `:fabric-1.19.2`
- `:fabric-1.20.1`

发布日志位于 `update/changelog.md`。
