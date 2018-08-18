#!/bin/bash

# Before running, download a jar-with-dependencies from https://search.maven.org/search?q=a:mapsforge-map-writer
# and place it in the plugins folder

# Run Osmosis
osmosis --rx file=Site.osm --mapfile-writer file=Site.map
