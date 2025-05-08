![minecraft_title](https://github.com/user-attachments/assets/15b13cc0-ca25-46e1-b8db-f9cd584b8a07)


<p align="center">
<a href="https://modrinth.com/mod/create-entity-control"><img src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3.2.0/assets/cozy/available/modrinth_vector.svg" alt="Modrinth Page"></a>
<a href="https://www.curseforge.com/minecraft/mc-mods/create-entitycontroller"><img src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3.2.0/assets/cozy/available/curseforge_vector.svg" alt="CurseForge Page"></a>
</p>

---


## Mechanical Power: Entity Control ⚙️

A simple mechanical power add-on that allows for customizable block entity configurations!

1. **Allows for highly customizable whitelists and blacklists** ✅
2. **Allows for highly customizable compression feedback** 🔄  
   Choosing to keep or destroy blocks. This can prevent almost all compression miners (this may be controversial, as compression miners lag servers but are more efficient than drills, considered a bug, yet beloved by players).
3. **Entity length detection** 📏  
   Allows configuring the maximum entity length, which can prevent excessively long structures that lag the server.
4. **Customizable maximum entity limit for blocks** ⚠️  
   Preventing most structures that only consist of certain blocks (e.g., 2048 drill panels (causing crashes), 2048 storage units (exploding when put in inventory), 2048 mechanical arms (do you enjoy lagging the server with my interaction functions?)).
5. **Experimental feature** 🔬  
   This mod adds an additional parameter, the stability coefficient, with a default value of 100 for each block. Different values can be set for each type of block to achieve interesting configurations (e.g., configuring the stability coefficient of each storage container based on grid count to prevent most NBT overflow or interaction lag. Additionally, this number can be negative; when set to a negative value, it can reduce the stability coefficient, allowing structures to accommodate more blocks. This can be integrated into packs to implement features like spaceship stability cores and spaceship walls that increase entity stability, allowing for larger structures).  
   **Note**: 🛑 This may slightly increase the computational load of entity updates, but the lag caused by mischievous players often far exceeds it, so whether to enable it is up to the server owner.

---
