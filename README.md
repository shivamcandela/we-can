# WE-CAN Android Application Project

NOTE:  lfresource.jar is removed from repo.  Build it in btbits/x64_btbits/client directory with:
make -f Makefile.resource

Code QL Badge

[![CodeQL](https://github.com/shivamcandela/we-can/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/shivamcandela/we-can/actions/workflows/codeql-analysis.yml)


Build on next Branch

[![Build APK](https://github.com/shivamcandela/we-can/actions/workflows/NightlyBuild.yml/badge.svg?branch=next)](https://github.com/shivamcandela/we-can/actions/workflows/NightlyBuild.yml)

Build on main Branch

[![Build APK](https://github.com/shivamcandela/we-can/actions/workflows/NightlyBuild.yml/badge.svg?branch=main)](https://github.com/shivamcandela/we-can/actions/workflows/NightlyBuild.yml)


Files of interest:
app/src/main/java/com/candela/wecan/StartupActivity.java                  This is 'main' startup logic.
app/src/main/java/com/candela/wecan/tests/base_tools/ResourceUtils.java   This implements the callbacks that lfresource makes to the app.
