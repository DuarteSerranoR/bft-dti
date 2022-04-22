package bft_dti;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.Map;

import bftsmart.tom.ServiceProxy;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.Set;

public class BFTNftMap {
    private final Logger logger;
    private final ServiceProxy serviceProxy;

    public BFTNftMap(ServiceProxy sProxy, Logger l) {
        logger = l;
        serviceProxy = sProxy;
    }

}
