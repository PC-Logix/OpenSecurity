package pcl.opensecurity.blockentity;

import pcl.opensecurity.OpenSecurity;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.mindrot.jbcrypt.BCrypt;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.zip.CRC32;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

public final class DataBlockEntity extends SecurityBlockEntity {
    public DataBlockEntity(BlockPos pos, BlockState state) {
        super(OpenSecurity.DATA_BLOCK_BE.get(), pos, state, "os_datablock", 32);
    }

    @Callback(direct = true, doc = "function():number -- Maximum input size in bytes.")
    public Object[] getLimit(Context context, Arguments args) {
        return new Object[]{li.cil.oc.Settings.get().dataCardHardLimit()};
    }

    @Callback(direct = true, limit = 32, doc = "function(data:string):string -- Base64 encodes data.")
    public Object[] encode64(Context context, Arguments args) throws Exception {
        return new Object[]{Base64.getEncoder().encode(checked(context, args, li.cil.oc.Settings.get().dataCardComplex()))};
    }

    @Callback(direct = true, limit = 32, doc = "function(data:string):string -- Base64 decodes data.")
    public Object[] decode64(Context context, Arguments args) throws Exception {
        return new Object[]{Base64.getDecoder().decode(checked(context, args, li.cil.oc.Settings.get().dataCardComplex()))};
    }

    @Callback(direct = true, limit = 6, doc = "function(data:string):string -- Deflate compresses data.")
    public Object[] deflate(Context context, Arguments args) throws Exception {
        byte[] data = checked(context, args, li.cil.oc.Settings.get().dataCardComplex());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (DeflaterOutputStream stream = new DeflaterOutputStream(output)) { stream.write(data); }
        return new Object[]{output.toByteArray()};
    }

    @Callback(direct = true, limit = 6, doc = "function(data:string):string -- Inflates compressed data.")
    public Object[] inflate(Context context, Arguments args) throws Exception {
        byte[] data = checked(context, args, li.cil.oc.Settings.get().dataCardComplex());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (InflaterOutputStream stream = new InflaterOutputStream(output)) { stream.write(data); }
        if (output.size() > li.cil.oc.Settings.get().dataCardHardLimit()) throw new IllegalArgumentException("inflated data size limit exceeded");
        return new Object[]{output.toByteArray()};
    }

    @Callback(direct = true, limit = 32, doc = "function(data:string):string -- Computes SHA-256.")
    public Object[] sha256(Context context, Arguments args) throws Exception {
        return new Object[]{MessageDigest.getInstance("SHA-256").digest(checked(context, args, li.cil.oc.Settings.get().dataCardSimple()))};
    }

    @Callback(direct = true, limit = 32, doc = "function(data:string):string -- Computes MD5.")
    public Object[] md5(Context context, Arguments args) throws Exception {
        return new Object[]{MessageDigest.getInstance("MD5").digest(checked(context, args, li.cil.oc.Settings.get().dataCardSimple()))};
    }

    @Callback(direct = true, limit = 32, doc = "function(data:string):number -- Computes CRC-32.")
    public Object[] crc32(Context context, Arguments args) throws Exception {
        CRC32 crc = new CRC32();
        crc.update(checked(context, args, li.cil.oc.Settings.get().dataCardSimple()));
        return new Object[]{ByteBuffer.allocate(Integer.BYTES).order(ByteOrder.LITTLE_ENDIAN)
                .putInt((int) crc.getValue()).array()};
    }

    @Callback(direct = true, limit = 32, doc = "function(data:string):string -- Applies ROT13.")
    public Object[] rot13(Context context, Arguments args) {
        return new Object[]{rot13Text(args.checkString(0))};
    }

    @Callback(limit = 1, doc = "function(text:string[,rounds:number]):string -- Computes a bcrypt hash.")
    public Object[] bCryptHash(Context context, Arguments args) {
        int rounds = Math.max(4, Math.min(15, args.optInteger(1, 10)));
        return new Object[]{BCrypt.hashpw(args.checkString(0), BCrypt.gensalt(rounds))};
    }

    @Callback(limit = 4, doc = "function(text:string,hash:string):boolean -- Checks a bcrypt hash.")
    public Object[] bCryptCheck(Context context, Arguments args) {
        return new Object[]{BCrypt.checkpw(args.checkString(0), args.checkString(1))};
    }

    private byte[] checked(Context context, Arguments args, double energy) throws Exception {
        byte[] data = args.checkByteArray(0);
        if (data.length > li.cil.oc.Settings.get().dataCardHardLimit()) throw new IllegalArgumentException("data size limit exceeded");
        if (!consumeEnergy(energy)) throw new IllegalStateException("not enough energy");
        if (data.length > li.cil.oc.Settings.get().dataCardSoftLimit()) {
            context.pause(li.cil.oc.Settings.get().dataCardTimeout());
        }
        return data;
    }

    private static String rot13Text(String input) {
        StringBuilder output = new StringBuilder(input.length());
        for (char value : input.toCharArray()) {
            if (value >= 'a' && value <= 'm' || value >= 'A' && value <= 'M') value += 13;
            else if (value >= 'n' && value <= 'z' || value >= 'N' && value <= 'Z') value -= 13;
            output.append(value);
        }
        return output.toString();
    }
}
