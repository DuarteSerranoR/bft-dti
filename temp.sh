#! /bin/bash

rm -r ./bftsmart/src/main/java/bft_dti
mkdir -p ./bftsmart/src/main/java/bft_dti
yes | cp -ri ./src/* ./bftsmart/src/main/java/bft_dti
