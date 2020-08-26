#!/usr/bin/env sh

# abort on errors
set -e

# build
vuepress build .



cp CNAME .vuepress/dist

# navigate into the build output directory
cd .vuepress/dist


# if you are deploying to a custom domain
# echo 'www.example.com' > CNAME

git init
git add -A
git commit -m 'deploy'

# need add config    base:'/docs/',
# if you are deploying to https://<USERNAME>.github.io
git push -f https://gitee.com/JbootProjects/docs.git master

# if you are deploying to https://<USERNAME>.github.io/<REPO>
# git push -f git@github.com:<USERNAME>/<REPO>.git master:gh-pages

cd -