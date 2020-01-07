#!/usr/bin/env bash

echo Latest bootstrap
npm view bootstrap dist-tags.latest

echo Previous bootstrap
npm view bootstrap dist-tags.previous

echo Latest Bulma
npm view bulma dist-tags.latest

echo Latest Semantic UI
npm view semantic-ui dist-tags.latest

echo Latest Fomantic UI
npm view fomantic-ui dist-tags.latest
