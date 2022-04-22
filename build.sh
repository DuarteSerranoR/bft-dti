#! /bin/bash

mkdir -p ./bftsmart/src/main/java/bft_dti
yes | cp -ri ./src/* ./bftsmart/src/main/java/bft_dti
cd bftsmart
gradle installDist # In windows works too if you have gradle installed. // TODO -> create an alternative
