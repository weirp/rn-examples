#!/bin/bash
npm cache clean --force
watchman watch-del-all
rm -rf node_modules
npm install
npm start -- --reset-cache

