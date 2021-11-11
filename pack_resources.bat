@echo off

SET PACKER=-cp D:\Programs\runnable-texturepacker.jar com.badlogic.gdx.tools.texturepacker.TexturePacker

set IN_ASSETS_DIR=.\input_assets
set OUT_DIR=.\core\assets\image

java %PACKER% %IN_ASSETS_DIR%\game\common %OUT_DIR% game
rem java %PACKER% %IN_ASSETS_DIR%\ui\atlas %OUT_DIR% ui
rem java %PACKER% %IN_ASSETS_DIR%\game\Temple %OUT_DIR% Temple
rem java %PACKER% %IN_ASSETS_DIR%\game\MountainsBG %OUT_DIR% MountainsBG
rem java %PACKER% %IN_ASSETS_DIR%\game\Forest %OUT_DIR% Forest
rem java %PACKER% %IN_ASSETS_DIR%\game\ForestBG %OUT_DIR% ForestBG
rem java %PACKER% %IN_ASSETS_DIR%\game\Misc %OUT_DIR% Misc

