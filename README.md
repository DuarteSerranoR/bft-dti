# Byzantine Fault Tolerant -> Decentralized Token Infrastructure

## Project done for academic purposes.

The objective of this project is to build a decentralized token infrastructure to support a NFT
market.

This project uses the BFT-SMaRT replication library - http://bft-smart.github.io/library/ 

## How to use this project

1. Make sure that the configuration is for your liking inside the ./config folder. This should be as indicated on ./bftsmart/README.md or read directly at http://bft-smart.github.io/library/ 
2. Run ./build.sh to compile the project
3. Foreach server run ./launch_server $SERVER_NUM
4. Foreach client run ./launch_client

Once again, for more information/understanding on how this work, read the bftsmart library at http://bft-smart.github.io/library/

## Run in a docker environment

# TODO 