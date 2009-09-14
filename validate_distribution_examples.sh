#!/bin/sh

mvn clean install

if [ "$?" -ne "0" ]; then
  echo "Hmm. Build failed."
  exit 99
fi

TMPDIR=$(mktemp --directory)

unzip dbdeploy-dist/target/dbdeploy-dist-*-distribution.zip -d $TMPDIR

cd $TMPDIR/dbdeploy-*/example/

ant default

# for some reason, the sql task doesn't display the output
# if it's run in the same session as the updates.
ant dump-tables


