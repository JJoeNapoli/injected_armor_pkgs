# injected_armor
an extention of armor with OWLOOP and the injected service SIT

this repository must be a child of the src folder in your workspace to reach the maven repository (set in the `build.gradle` inside each module as `../../../../devel/share/maven/`).

Run `catkin_make` and test the basic armor service with
``roslaunch armor_py_client_api armorTest.launch``

A sit test is starting (at line 46 in [this file](https://github.com/EmaroLab/injected_armor_pkgs/blob/master/injected_armor/armor/src/main/java/it/emarolab/armor/ARMORMainService.java)) by default as an *hello world* example, where you should set the correct path of the ontology (at line 33 in [this file](https://github.com/EmaroLab/injected_armor_pkgs/blob/master/injected_armor/sit/src/main/java/it/emarolab/sit/SITBase.java)).
