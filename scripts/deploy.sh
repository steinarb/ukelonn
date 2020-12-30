#!/bin/sh
#
cp .travis.settings.xml $HOME/.m2/settings.xml
mkdir -p $HOME/.ssh
echo $DEPLOYKEY | base64 -d>$HOME/.ssh/id_rsa
chmod -R go-rwx $HOME/.ssh
mvn -B deploy
