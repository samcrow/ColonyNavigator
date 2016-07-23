package org.samcrow.colonynavigator.data4;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Calculates a message digest of Objects and other values, converting objects into bytes using an
 * {@link ObjectOutputStream}.
 */
public class Digest {

    private final MessageDigest digest;

    public Digest() {
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 checksum not available", e);
        }
    }

    public void update(Object digestObject) {
        try {
            // Convert the object to bytes
            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
            final ObjectOutputStream objectStream = new ObjectOutputStream(stream);
            objectStream.writeObject(digestObject);
            objectStream.flush();

            digest.update(stream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update(byte value) {
        try {
            // Convert the object to bytes
            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
            final ObjectOutputStream objectStream = new ObjectOutputStream(stream);
            objectStream.writeByte(value);
            objectStream.flush();

            digest.update(stream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update(short value) {
        try {
            // Convert the object to bytes
            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
            final ObjectOutputStream objectStream = new ObjectOutputStream(stream);
            objectStream.writeShort(value);
            objectStream.flush();

            digest.update(stream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update(int value) {
        try {
            // Convert the object to bytes
            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
            final ObjectOutputStream objectStream = new ObjectOutputStream(stream);
            objectStream.writeInt(value);
            objectStream.flush();

            digest.update(stream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update(long value) {
        try {
            // Convert the object to bytes
            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
            final ObjectOutputStream objectStream = new ObjectOutputStream(stream);
            objectStream.writeLong(value);
            objectStream.flush();

            digest.update(stream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the bytes of the message digest of all the provided objects
     *
     * @return
     */
    public byte[] digest() {
        return digest.digest();
    }
}
