
![minecraft_title](https://github.com/user-attachments/assets/15b13cc0-ca25-46e1-b8db-f9cd584b8a07)




<p align="center">
<a href="https://modrinth.com/mod/create-entity-control"><img src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3.2.0/assets/cozy/available/modrinth_vector.svg" alt="Modrinth Page"></a>
<a href="https://www.curseforge.com/minecraft/mc-mods/create-entitycontroller"><img src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3.2.0/assets/cozy/available/curseforge_vector.svg" alt="CurseForge Page"></a>
</p>



## 机械动力：实体控制 ⚙️

---


一个简单的机械动力附属，允许自定义机械动力的方块实体配置！

1. **允许高度自定义实体化白名单与黑名单** ✅
2. **允许高度自定义挤压反馈** 🔄  
   是保留方块还是破坏方块，这可以阻止几乎所有挤压矿机（这可能会引起争议，因为挤压矿机卡顿服务器，又比钻头好用，属于漏洞，但玩家非常喜爱）
3. **实体长度检测** 📏  
   允许配置实体最大长度，这可以阻止一些超长的卡服结构
4. **允许自定义方块的最大实体化上限** ⚠️  
   这可以防止大部分只有某些方块的结构（例如：2048个钻头面板（卡死了bushi），2048个存储库（塞进背包就炸），2048个机械手（喜欢我交互函数卡服吗？））
5. **实验功能** 🔬  
   本模组添加了一个额外的参数，稳定性系数，每个方块的默认值为100，可以给每种方块设置不同的值，以实现一些有趣的配置（例如：根据格子数量来配置每种存储容器的稳定性系数，以防止大部分NBT溢出或交互卡顿。此外，这个数可以为负值，当设置为负值时，可以减少稳定性系数，使结构容纳更多的方块，这可以使得它被制作进整合包中，来实现飞船稳定核心、飞船墙壁可以增加实体稳定性，容纳更大的结构之类的功能。）  
   **注意**: 🛑 这可能会导致些许增加实体化更新的计算量，但熊孩子带来的卡顿往往远超它，因此是否开启由服主决定。

---

test1

# 配置文件说明

### 方块实体使用的数量限制
- **blocks_limit**:
   - 这是一个数组，每个元素包含三个参数：
      - **块名称**（例如 `"create:deployer"`）
      - **数量限制**（例如 `256`）
      - **稳定参数**（例如 `100`）

### 记录块实体问题
- **"Log block entity problem"**:
   - 如果设置为 `true`，则当块无法转换为块实体时会记录问题；如果设置为 `false`，则不记录，方便定位修改原版实体代码的实体问题。

### 破坏速度的百分比
- **"10% of destroy speed"**:
   - 这是一个浮动值，用于计算破坏速度的倍数，大于此值的方块会被保留破坏方块实体，小于此值的方块会被方块实体挤压掉落。默认建议设置为 `14`，这意味着实际破坏速度将是 `1.4`，这个值刚好保证泥土沙子可以被实体挤掉，但是石头却可以破坏结构。

### 块实体的最长XZ距离
- **"block entity max length XZ"**:
   - 这是方块实体在X、Z方向上的最大允许距离。范围为 `3` 到 `500`。

### 块实体的最长Y距离
- **"block entity max length Y"**:
   - 这是方块实体在Y方向上的最大允许距离。范围为 `3` 到 `500`。

### 不可移动块列表
- **blocks_unmoved**:
   - 这是一个字符串数组，加入后的方块不能被移动（例如 `["minecraft:deepslate", "minecraft:stone"]`）。

### 不可被压碎的块列表
- **blocks_uncrushable**:
   - 这是一个字符串数组，加入的方块会破坏方块实体（例如 `["minecraft:deepslate", "minecraft:stone"]`）。

### 可被压碎的块列表
- **blocks_crushable**:
   - 这是一个字符串数组，加入的方块会保留机械动力原版的配置，即被方块实体破坏（例如 `["minecraft:water"]`）。

### 被忽略的块列表
- **blocks_ignore**:
   - 这是一个字符串数组，加入的方块会进入忽略列表，此实体不会再被实体控制函数捕获。
   - 为了防止熊孩子针对性研究bug，默认设置为实体内的方块必须全部在忽略列表里，才可以将此实体忽略。

### 计算块稳定参数
- **"calculate block stabilize para"**:
   - 如果设置为 `true`，则块会计算块实体的实验参数，可能会消耗一些性能，在服务器内的差距大约为：加入前40us/t - 加入后250us/t。
   - 但此参数产生的卡顿仅在无法实体化的时候才会出现，同时只在矿车装配站发生，因此极易定位，这些性能影响大可忽略。
   - Tips：（一个2048挤压面板的卡顿也就4ms吧，不多不多，也就这个的40倍~）（bushi）

### 最大稳定参数限制
- **"block entity max stabilize para"**:
   - 这是一个整数，表示最大稳定参数的限制。一个普通块的默认稳定计数为 `100`。
   - 设置可根据存储箱来避免块实体的NBT溢出。范围为 `> 0`。
