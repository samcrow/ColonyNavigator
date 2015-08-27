#!/bin/bash

cd `pwd`

# Download map writer plugin
mkdir -p plugins
cd plugins
curl -O http://ci.mapsforge.org/job/dev/lastSuccessfulBuild/artifact/mapsforge-map-writer/build/libs/mapsforge-map-writer-dev-SNAPSHOT.jar
cd ..

# Run Osmosis
osmosis --rx file=Site.osm --mapfile-writer file=Site.map
