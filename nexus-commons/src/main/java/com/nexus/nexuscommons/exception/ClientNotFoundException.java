package com.nexus.nexuscommons.exception;

public class ClientNotFoundException extends NexusException {

    public ClientNotFoundException(String message) {
        super("CLIENT_NOT_FOUND",message);
    }
}