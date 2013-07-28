We have 2 dirs: assetsCollections and assets.

assetsCollections contains all the js/css assets at our disposal. assets contains only the files used by YASGUI, so the maven YUI css/js minifier only has to compile the needed files. 
Files in the assets folders are all symlinks to the assetsCollections folder

Procedure for adding new assets:
- add toe assetsCollections dir
- make symlink of necessary files in assets dir
- add file to aggregate specification in pom, so it gets aggregated with all other files to 1 single js/css file for yasgui
