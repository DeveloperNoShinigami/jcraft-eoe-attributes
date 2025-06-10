package net.arna.jcraft.common.gravity.util.packet;

import net.arna.jcraft.api.component.entity.CommonGravityComponent;
import net.arna.jcraft.common.gravity.api.RotationParameters;
import net.arna.jcraft.common.gravity.util.Gravity;
import net.arna.jcraft.common.gravity.util.NetworkUtil;
import net.minecraft.network.FriendlyByteBuf;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class OverwriteGravityPacket extends GravityPacket {
    public final List<Gravity> gravityList;
    public final boolean initialGravity;

    public OverwriteGravityPacket(List<Gravity> _gravityList, boolean _initialGravity) {
        gravityList = _gravityList;
        initialGravity = _initialGravity;
    }

    public OverwriteGravityPacket(FriendlyByteBuf buf) {
        int listSize = buf.readInt();
        gravityList = new ArrayList<>();
        for (int i = 0; i < listSize; i++) {
            gravityList.add(NetworkUtil.readGravity(buf));
        }
        initialGravity = buf.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(gravityList.size());
        for (Gravity gravity : gravityList) {
            NetworkUtil.writeGravity(buf, gravity);
        }
        buf.writeBoolean(initialGravity);
    }

    @Override
    public void run(CommonGravityComponent gc) {
        gc.setGravity(gravityList, initialGravity);
    }

    @Override
    public RotationParameters getRotationParameters() {
        Optional<Gravity> max = gravityList.stream().max(Comparator.comparingInt(Gravity::priority));
        if (max.isEmpty()) {
            return new RotationParameters();
        }
        return max.get().rotationParameters();
    }
}
