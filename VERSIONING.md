Versioning
==========

Mod versions are structured `<Minecraft Version>-<Mod Version>.<Build Number>`.
Here, the Minecraft version is the official version, except the missing patch version number is added (so 1.20 becomes 1.20.0).
The build number is a number that can be used to associate the asset with a specific build operation.

Release versions
----------------

The mod version uses a simple form of [Semantic Versioning](https://semver.org/) (`major.minor.patch`):
* **Major** starts at 1 and is bumped whenever an incompatible update is made. It is unlikely to ever change from 1.
* **Minor** starts at 0 and is bumped whenever a new feature is added and backward compatibility (cleanly rolling back to older versions) is not guaranteed.
* **Patch** starts at 0 and is bumped whenever a bug is fixed or a minor adjustment is made that does not impact gameplay. Backward compatibility should never be a problem for these.

Mod release versions reset for every Minecraft release, including patch versions.

Old release versions
--------------------

Older mod versions (before Minecraft 1.18.x) maintain the same scheme, except the major version starts on 0.
This was changed in order to communicate that the mods are in a stable state.

Pre-release versions
--------------------

Each versioned branch is automatically built whenever a change is made. These mod files should be stable and can be found on CircleCI as build artifacts.
They are versioned as above, with `MASTER` as the mod version. The build number can be used to identify the specific build.

Feature development versions
----------------------------

Features are added in the form of Pull Requests. While the Pull Requests are open, they are automatically built. These mod files may be unstable. They can be found on CircleCI as build artifacts.
They are versioned as above, with `DEV` as the mod version. The build number can be used to identify the specific build.
