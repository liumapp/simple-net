package simplenet.packet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.function.Consumer;
import javax.crypto.Cipher;
import simplenet.Client;
import simplenet.Server;
import simplenet.utility.Utility;

/**
 * A {@link Packet} that will be sent from a {@link Client} to the {@link Server} or vice versa.
 * <br><br>
 * This class is <strong>NOT</strong> safe for concurrent use among multiple threads.
 */
public final class Packet {

    /**
     * A {@code boolean} that designates whether data should be added to the front of the {@link Deque} rather than
     * the end.
     */
    private boolean prepend;

    /**
     * A {@link Deque} that lazily writes data to the backing {@link ByteBuffer}.
     */
    private final Deque<byte[]> queue;

    /**
     * A {@code private} constructor.
     */
    private Packet() {
        this.queue = new ArrayDeque<>();
    }

    /**
     * Instantiates a new {@link Packet}.
     *
     * @return An instance of {@link Packet}.
     */
    public static Packet builder() {
        return new Packet();
    }
    
    /**
     * A helper method that eliminates duplicate code and enqueues a {@code byte[]} to the backing {@link Deque}
     * (either at the front or back depending on the value of {@code prepend}).
     *
     * @param data The data to enqueue.
     * @return This {@link Packet} to allow for chained writes.
     */
    private Packet enqueue(byte[] data) {
        if (prepend) {
            queue.offerFirst(data);
        } else {
            queue.offerLast(data);
        }
        
        return this;
    }
    
    /**
     * Writes a single {@code boolean} to this {@link Packet}'s payload.
     * <br><br>
     * The {@code boolean} is sent over the network as a {@code byte} with a value of {@code 1} for {@code true} and a
     * value of {@code 0} for {@code false}.
     *
     * @param b A {@code boolean}, that is internally written as a {@code byte}.
     * @return The {@link Packet} to allow for chained writes.
     */
    public Packet putBoolean(boolean b) {
        return enqueue(new byte[] { (byte) (b ? 1 : 0) });
    }
    
    /**
     * Writes a single {@code byte} to this {@link Packet}'s payload.
     *
     * @param b An {@code int} for ease-of-use, but internally down-casted to a {@code byte}.
     * @return The {@link Packet} to allow for chained writes.
     */
    public Packet putByte(int b) {
        return enqueue(new byte[] { (byte) b });
    }

    /**
     * Writes a variable amount of {@code byte}s with {@link ByteOrder#BIG_ENDIAN} order to this {@link Packet}'s
     * payload.
     *
     * @param src A variable amount of {@code byte}s.
     * @return The {@link Packet} to allow for chained writes.
     * @see #putBytes(ByteOrder, byte...)
     */
    public Packet putBytes(byte... src) {
        return putBytes(ByteOrder.BIG_ENDIAN, src);
    }
    
    /**
     * Writes a variable amount of {@code byte}s with the specified {@link ByteOrder} to this {@link Packet}'s payload.
     *
     * @param src A variable amount of {@code byte}s.
     * @return The {@link Packet} to allow for chained writes.
     */
    public Packet putBytes(ByteOrder order, byte... src) {
        return enqueue(ByteBuffer.allocate(src.length).put(src).order(order).array());
    }

    /**
     * Writes a single {@code char} with {@link ByteOrder#BIG_ENDIAN} order to this {@link Packet}'s payload.
     *
     * @param c A {@code char}.
     * @return The {@link Packet} to allow for chained writes.
     * @see #putChar(char, ByteOrder)
     */
    public Packet putChar(char c) {
        return putChar(c, ByteOrder.BIG_ENDIAN);
    }
    
    /**
     * Writes a single {@code char} with the specified {@link ByteOrder} to this {@link Packet}'s payload.
     *
     * @param c A {@code char}.
     * @return The {@link Packet} to allow for chained writes.
     */
    public Packet putChar(char c, ByteOrder order) {
        return enqueue(ByteBuffer.allocate(Character.BYTES).order(order).putChar(c).array());
    }
    
    /**
     * Writes a single {@code double} with {@link ByteOrder#BIG_ENDIAN} order to this {@link Packet}'s payload.
     *
     * @param d A {@code double}.
     * @return The {@link Packet} to allow for chained writes.
     * @see #putDouble(double, ByteOrder)
     */
    public Packet putDouble(double d) {
        return putDouble(d, ByteOrder.BIG_ENDIAN);
    }
    
    /**
     * Writes a single {@code double} with the specified {@link ByteOrder} to this {@link Packet}'s payload.
     *
     * @param d A {@code double}.
     * @return The {@link Packet} to allow for chained writes.
     */
    public Packet putDouble(double d, ByteOrder order) {
        return enqueue(ByteBuffer.allocate(Double.BYTES).order(order).putDouble(d).array());
    }
    
    /**
     * Writes a single {@code float} with {@link ByteOrder#BIG_ENDIAN} order to this {@link Packet}'s payload.
     *
     * @param f A {@code float}.
     * @return The {@link Packet} to allow for chained writes.
     * @see #putFloat(float, ByteOrder)
     */
    public Packet putFloat(float f) {
        return putFloat(f, ByteOrder.BIG_ENDIAN);
    }
    
    /**
     * Writes a single {@code float} with the specified {@link ByteOrder} to this {@link Packet}'s payload.
     *
     * @param f A {@code float}.
     * @return The {@link Packet} to allow for chained writes.
     */
    public Packet putFloat(float f, ByteOrder order) {
        return enqueue(ByteBuffer.allocate(Float.BYTES).order(order).putFloat(f).array());
    }
    
    /**
     * Writes a single {@code int} with {@link ByteOrder#BIG_ENDIAN} order to this {@link Packet}'s payload.
     *
     * @param i An {@code int}.
     * @return The {@link Packet} to allow for chained writes.
     * @see #putInt(int, ByteOrder)
     */
    public Packet putInt(int i) {
        return putInt(i, ByteOrder.BIG_ENDIAN);
    }
    
    /**
     * Writes a single {@code int} with the specified {@link ByteOrder} to this {@link Packet}'s payload.
     *
     * @param i An {@code int}.
     * @return The {@link Packet} to allow for chained writes.
     */
    public Packet putInt(int i, ByteOrder order) {
        return enqueue(ByteBuffer.allocate(Integer.BYTES).order(order).putInt(i).array());
    }
    
    /**
     * Writes a single {@code long} with {@link ByteOrder#BIG_ENDIAN} order to this {@link Packet}'s payload.
     *
     * @param l A {@code long}.
     * @return The {@link Packet} to allow for chained writes.
     * @see #putLong(long, ByteOrder)
     */
    public Packet putLong(long l) {
        return putLong(l, ByteOrder.BIG_ENDIAN);
    }
    
    /**
     * Writes a single {@code long} with the specified {@link ByteOrder} to this {@link Packet}'s payload.
     *
     * @param l A {@code long}.
     * @return The {@link Packet} to allow for chained writes.
     */
    public Packet putLong(long l, ByteOrder order) {
        return enqueue(ByteBuffer.allocate(Long.BYTES).order(order).putLong(l).array());
    }
    
    /**
     * Writes a single {@code short} with {@link ByteOrder#BIG_ENDIAN} order to this {@link Packet}'s payload.
     *
     * @param s An {@code int} for ease-of-use, but internally down-casted to a {@code short}.
     * @return The {@link Packet} to allow for chained writes.
     * @see #putShort(int, ByteOrder)
     */
    public Packet putShort(int s) {
        return putShort(s, ByteOrder.BIG_ENDIAN);
    }
    
    /**
     * Writes a single {@code short} with the specified {@link ByteOrder} to this {@link Packet}'s payload.
     *
     * @param s An {@code int} for ease-of-use, but internally down-casted to a {@code short}.
     * @return The {@link Packet} to allow for chained writes.
     */
    public Packet putShort(int s, ByteOrder order) {
        return enqueue(ByteBuffer.allocate(Short.BYTES).order(order).putShort((short) s).array());
    }

    /**
     * Writes a single {@link StandardCharsets#UTF_8}-encoded {@link String} with {@link ByteOrder#BIG_ENDIAN} order to
     * this {@link Packet}'s payload.
     * <br><br>
     * The {@link String} can have a maximum length of {@code 65,535}.
     *
     * @param s A {@link String}.
     * @return The {@link Packet} to allow for chained writes.
     * @see #putString(String, Charset, ByteOrder)
     */
    public Packet putString(String s) {
        return putString(s, StandardCharsets.UTF_8, ByteOrder.BIG_ENDIAN);
    }
    
    /**
     * Writes a single {@link String} encoded with the specified {@link Charset} and {@link ByteOrder#BIG_ENDIAN}
     * order to this {@link Packet}'s payload.
     * <br><br>
     * A {@code short} is used to store the length of the {@link String} in the payload header, which imposes a
     * maximum {@link String} length of {@code 65,535} with a {@link StandardCharsets#UTF_8} encoding or
     * {@code 32,767} (or less) with a different encoding.
     *
     * @param s A {@link String}.
     * @return The {@link Packet} to allow for chained writes.
     * @see #putString(String, Charset, ByteOrder)
     */
    public Packet putString(String s, Charset charset) {
        return putString(s, charset, ByteOrder.BIG_ENDIAN);
    }
    
    /**
     * Writes a single {@link String} encoded with the specified {@link Charset} and {@link ByteOrder} to this
     * {@link Packet}'s payload.
     * <br><br>
     * A {@code short} is used to store the length of the {@link String} in the payload header, which imposes a
     * maximum {@link String} length of {@code 65,535} with a {@link StandardCharsets#UTF_8} encoding or
     * {@code 32,767} (or less) with a different encoding.
     *
     * @param s A {@link String}.
     * @return The {@link Packet} to allow for chained writes.
     */
    public Packet putString(String s, Charset charset, ByteOrder order) {
        var bytes = s.getBytes(charset);
        putShort(bytes.length, order);
        putBytes(order, bytes);
        return this;
    }

    /**
     * Prepends data to the front of this {@link Packet}.
     * <br><br>
     * This is primarily used for headers, such as when one or more of the headers depend on the size
     * of the data contained within the {@link Packet} itself.
     *
     * @param consumer The {@link Consumer} containing calls to add more data to this {@link Packet}.
     * @return This {@link Packet} to allow for chained writes.
     */
    public Packet prepend(Consumer<Packet> consumer) {
        prepend = true;
        consumer.accept(this);
        prepend = false;
        return this;
    }
    
    /**
     * Queues this {@link Packet} to a single {@link Client}.
     * <br><br>
     * The {@link Client} will not receive this {@link Packet} until {@link Client#flush()} is called.
     *
     * @param <T> A {@link Client} or any of its children.
     * @param client The {@link Client} to queue this {@link Packet} to.
     */
    public final <T extends Client> void write(T client) {
        int size = getSize(client);
        
        if (size > client.getBufferSize()) {
            throw new IllegalStateException("Packet is too large (Size: " + size + ") for client buffer size" +
                    " (Limit: " + client.getBufferSize() + ")");
        }
    
        client.getOutgoingPackets().offer(this);
    }
    
    /**
     * Queues this {@link Packet} to one (or more) {@link Client}(s).
     * <br><br>
     * No {@link Client} will receive this {@link Packet} until {@link Client#flush()} is called for that respective
     * {@link Client}.
     *
     * @param <T> A {@link Client} or any of its children.
     * @param clients A variable amount of {@link Client}s.
     */
    @SafeVarargs
    public final <T extends Client> void write(T... clients) {
        if (clients.length == 0) {
            throw new IllegalArgumentException("You must send this packet to at least one client!");
        }

        for (Client client : clients) {
            write(client);
        }
    }

    /**
     * Queues this {@link Packet} to one (or more) {@link Client}(s).
     * <br><br>
     * No {@link Client} will receive this {@link Packet} until {@link Client#flush()} is called for that respective
     * {@link Client}.
     *
     * @param clients A {@link Collection} of {@link Client}s.
     */
    public final void write(Collection<? extends Client> clients) {
        if (clients.isEmpty()) {
            throw new IllegalArgumentException("You must send this packet to at least one client!");
        }

        clients.forEach(this::write);
    }
    
    /**
     * Queues this {@link Packet} to a single {@link Client} and calls {@link Client#flush()}, flushing all
     * previously-queued packets as well.
     *
     * @param <T> A {@link Client} or any of its children.
     * @param client The {@link Client} to queue (and flush) this {@link Packet} to.
     */
    public final <T extends Client> void writeAndFlush(T client) {
        write(client);
        client.flush();
    }

    /**
     * Queues this {@link Packet} to one or more {@link Client}s and calls {@link Client#flush()},
     * flushing all previously-queued packets as well.
     *
     * @param <T> A {@link Client} or any of its children.
     * @param clients A variable amount of {@link Client}s.
     */
    @SafeVarargs
    public final <T extends Client> void writeAndFlush(T... clients) {
        if (clients.length == 0) {
            throw new IllegalArgumentException("You must send this packet to at least one client!");
        }

        for (Client client : clients) {
            writeAndFlush(client);
        }
    }

    /**
     * Queues this {@link Packet} to one or more {@link Client}s and calls {@link Client#flush()},
     * flushing all previously-queued packets as well.
     *
     * @param clients A {@link Collection} of {@link Client}s.
     */
    public final void writeAndFlush(Collection<? extends Client> clients) {
        if (clients.isEmpty()) {
            throw new IllegalArgumentException("You must send this packet to at least one client!");
        }

        clients.forEach(this::writeAndFlush);
    }

    /**
     * Gets the size of this {@link Packet}'s payload in {@code byte}s.
     * <br><br>
     * This method has been deprecated, as it does not take packet encryption into account.
     *
     * @return The current size of this {@link Packet} in {@code byte}s.
     * @deprecated Use {@link #getSize(Client)} instead.
     */
    @Deprecated
    public int getSize() {
        return queue.stream().mapToInt(array -> array.length).sum();
    }
    
    /**
     * Gets the size of this {@link Packet}'s payload in {@code byte}s, while taking the specified {@link Client}'s
     * encryption into account, as a {@link Cipher}'s padding may increase the size of this {@link Packet}.
     *
     * @param client The {@link Client} that this {@link Packet} will be sent to.
     * @return The current size of this {@link Packet} in {@code byte}s.
     */
    public int getSize(Client client) {
        Cipher encryption;
        
        if ((encryption = client.getEncryption()) == null) {
            return queue.stream().mapToInt(array -> array.length).sum();
        }
        
        int blockSize = encryption.getBlockSize();
        
        return queue.stream().mapToInt(array -> Utility.roundUpToNextMultiple(array.length, blockSize)).sum();
    }

    /**
     * Gets the backing {@link Deque} of this {@link Packet}.
     * <br><br>
     * This method should only be used internally; modifying this deque in any way can produce unintended results!
     *
     * @return A {@link Deque}.
     */
    public Deque<byte[]> getQueue() {
        return queue;
    }

}
