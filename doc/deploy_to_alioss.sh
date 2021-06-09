#!/usr/bin/env sh

# abort on errors
set -e

# build
yarn build

ossutil cp -rf .vuepress/dist oss://jboot-doc-site-hk/

cd -