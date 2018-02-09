#!/bin/bash

#
# Usage : backslash-out.sh [DEFINITION-FILE-WITH-BACKSLASHES] 
#

cat $1 | sed -e 's/\\//g'
