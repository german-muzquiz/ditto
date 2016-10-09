#!/usr/bin/env bash

cd $1
java -jar ditto-$2.jar record 9090 http://www.google.com test.txt

