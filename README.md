# key-path plugin for SBT #

Inspect full dependency paths between settings and tasks.

Current version is 0.2

### Usage ###

* Add the plugin (globally or per project): `addSbtPlugin("com.hanhuy.sbt" % "key-path" % "0.2")`
* Use the new command `keypath`
  * `keypath <target-key> <dependency-key>`

#### Example Output ####

```
$ sbt "keypath android:package android:proguard"
[info] Loading global plugins from C:\Users\pfnguyen\.sbt\0.13\plugins
[info] Updating {file:/C:/Users/pfnguyen/.sbt/0.13/plugins/}global-plugins...
[info] Done updating.
[info] Loading project definition from C:\Users\pfnguyen\src\tvm\project
[info] Updating {file:/C:/Users/pfnguyen/src/tvm/project/}tvm-build...
[info] Done updating.
[info] Set current project to rel (in build file:/C:/Users/pfnguyen/src/tvm/)
[info] Searching for: rel/android:package <- rel/android:proguard
...
[info] rel/android:package
[info]   +-- rel/android:zipalign
[info]   +-- rel/android:signRelease
[info]   +-- rel/android:apkbuild
[info]   +-- rel/android:apkbuildAggregate
[info]   +-- rel/android:predex
[info]   +-- rel/android:proguard
[info]
[info] rel/android:package
[info]   +-- rel/android:zipalign
[info]   +-- rel/android:signRelease
[info]   +-- rel/android:apkbuild
[info]   +-- rel/android:apkbuildAggregate
[info]   +-- rel/android:predex
[info]   +-- rel/android:dexAggregate
[info]   +-- rel/android:dexInputs
[info]   +-- rel/android:proguard
[info]
[info] rel/android:package
[info]   +-- rel/android:zipalign
[info]   +-- rel/android:signRelease
[info]   +-- rel/android:apkbuild
[info]   +-- rel/android:apkbuildAggregate
[info]   +-- rel/android:resourceShrinker
[info]   +-- rel/android:proguard
[info]
[info] rel/android:package
[info]   +-- rel/android:zipalign
[info]   +-- rel/android:signRelease
[info]   +-- rel/android:apkbuild
[info]   +-- rel/android:apkbuildAggregate
[info]   +-- rel/android:dex
[info]   +-- rel/android:proguard

$ sbt "keypath android:proguard android:package"
[info] Loading global plugins from C:\Users\pfnguyen\.sbt\0.13\plugins
[info] Loading project definition from C:\Users\pfnguyen\src\tvm\project
[info] Set current project to rel (in build file:/C:/Users/pfnguyen/src/tvm/)
[info] Searching for: rel/android:proguard <- rel/android:package
...
[error] rel/android:proguard does not depend on rel/android:package
```