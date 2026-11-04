# Export regression checks

Build the application with `mvn clean compile assembly:single`. Compile the test
drivers against `JAR/jMc2Obj-128.jar` with `javac -encoding UTF-8 -cp
JAR/jMc2Obj-128.jar -d target/regression tests/*.java`. For the commands below,
run `java -cp JAR/jMc2Obj-128.jar:target/regression` followed by the class and
its arguments. Use `;` as the classpath separator on Windows.

| Driver | Arguments | Checks |
| --- | --- | --- |
| `RegressionHarness` | One or more Minecraft client JARs named `mc-<version>.jar` | Glass and panes, chain aliases, banners, end portal pixels, old and new bed/sign models |
| `SpriteScaleRegression` | A fresh output directory | Synthetic non-square atlas, scaled metadata, normal/specular sheets and legacy alpha export |
| `GrassRegression` | Resource directory, fresh output directory, `verify` | Model variants, tinted and untinted textures, grass-block overlays and MTL color |
| `GrassPairedTest` | Resource directory | Matching tall-plant halves and grass tint across a biome boundary |
| `org.jmc.gui.PresetStoreTest` | A fresh output directory | Storage, ordering, Unicode and handling unsupported files |
| `org.jmc.gui.PresetUiTest` | A fresh output directory, a client JAR, a resource pack | Save, select, overwrite, reopen and reset in Swing, using isolated settings |

To exercise first-run config extraction, copy the built JAR to a disposable
directory. From a different directory containing a regular file named `conf`,
run `RegressionHarness config` with that copied JAR and the compiled tests on
the classpath. The test modifies the disposable JAR's extracted `blocks.conf`.

The grass tests expect these filenames in the resource directory, in decreasing
priority: `pack2.zip` (Simple Grass Flowers v2.0.0), `pack1.zip` (Os' Colorful
Grasses Tall), `pack0.zip` (Fancy Crops v1.3), `pack3.zip` (Better Leaves 9.6)
and `minecraft.jar` (Minecraft 1.21.11).

## Debug-world export

1. Use the Minecraft 1.21.11 data generator to produce `reports/blocks.json`.
   Run `python tests/create_debug_world.py <blocks.json> <client.jar> <output>`
   and `python tests/verify_world.py <blocks.json> <output>`.
2. Export the generated save with the client JAR and any resource packs in
   priority order. Use `--export-edges=edges -a 0,0,352,352 -h 69,72` and
   `--tex-export=base,alpha` to exercise legacy alpha requests. For the
   animation comparison, use an animation-focused block whitelist with both
   builds. Put the new export in `<output>/debug-export`.
3. Run `python tests/verify_export.py <before> <after> <report.json> <packs...>`
   with Pillow installed. It compares texture dimensions and geometry counts
   across the exports and checks pixels and metadata against the source packs.
4. Run `blender --background --factory-startup --python-exit-code 1 --python
   tests/SingleRgbaBlenderTest.py -- <output>` to verify shared color/alpha
   nodes, fresh OBJ import and UV animation in Blender.

The scripts use locally supplied Minecraft assets and resource packs.
