#!/bin/bash
re-natal use-android-device avd
re-natal require-all
re-natal enable-auto-require
re-natal use-figwheel
lein figwheel android

