gravitylib doc so planet doesnt explode trying to figure it out

Fabric works fine, Forge does not
to figure out why we need to know a bunch of stuff

Our most commonly used gravity manipulation method is this;
GravityChangerAPI, ln. 89-97 - addGravity()
```java
public static void addGravity(Entity entity, Gravity gravity) {
    if (onWrongSide(entity) || !EntityTags.canChangeGravity(entity)) {
    return;
    }
    JComponentPlatformUtils.getGravity(entity).ifPresent(gc -> {
        gc.addGravity(gravity, false);
        GravityChannel.UPDATE_GRAVITY.sendToClient(entity, new UpdateGravityPacket(gravity, false), NetworkUtil.PacketMode.EVERYONE);
    });
}
```

UpdateGravityPacket creation involves two writes
```java
public void write(FriendlyByteBuf buf) {
    NetworkUtil.writeGravity(buf, gravity);
    buf.writeBoolean(initialGravity);
}

    ... NetworkUtils
public static void writeGravity(FriendlyByteBuf buf, Gravity gravity) {
    writeDirection(buf, gravity.direction());
    buf.writeInt(gravity.priority());
    buf.writeInt(gravity.duration());
    buf.writeUtf(gravity.source());
    writeRotationParameters(buf, gravity.rotationParameters());
}
```
this is almost definitely not the source of the problem.
Because **on Forge, it's the visual effects that break.**
* The camera animates the RotationAnimation in the wrong direction,
* the camera thus retains an incorrect position,
* player animations are messed up (same as Fabric).

## Testing will be done with C-Moon's Utility facing +X (east)

The player receives the packet and the processing for it is defined in
GravityChannelClient, ln. 28-32
```java
public void receiveFromServer(Minecraft client, FriendlyByteBuf buf) {
    int entityId = buf.readInt();
    P packet = packetFactory.read(buf);
    client.execute(() -> NetworkUtilClient.getGravityComponent(client, entityId).ifPresent(packet::run));
}
```
For an UpdateGravityPacket, this just calls CommonGravityComponent.addGravity()
```java
public void addGravity(Gravity gravity, boolean initialGravity) {
    if (canChangeGravity()) {
        // New gravities override older ones if they share a source
        gravityList.removeIf(g -> Objects.equals(g.source(), gravity.source()));
        if (gravity.direction() != null)
            gravityList.add(new Gravity(gravity));
        // Handles some variable changes and defines a new RotationAnimation for the client
        updateGravity(gravity.rotationParameters(), initialGravity);
    }
}
```
```java
public void updateGravity(RotationParameters rotationParameters, boolean initialGravity) {
    if (canChangeGravity()) {
        Direction newGravity = getActualGravityDirection();
        Direction oldGravity = gravityDirection;
        if (oldGravity != newGravity) {
            long timeMs = entity.level().getGameTime() * 50;
            if (entity.level().isClientSide) {
                animation.applyRotationAnimation(
                        newGravity, oldGravity,
                        initialGravity ? 0 : rotationParameters.rotationTime(),
                        entity, timeMs, rotationParameters.rotateView()
                );
            }
            prevGravityDirection = oldGravity;
            gravityDirection = newGravity;
            onGravityChanged(oldGravity, newGravity, rotationParameters, initialGravity);
        }
    }
}
```
In our testing case, this would mean that:
newGravity = east
oldGravity = down
animation.startGravityRotation = (0, [really low number], 0, 1)
animation.endGravityRotation = (0, 0, -7.071E-1, 7.071E-1)

The values match up between Fabric and Forge.

So does the value defined at CameraMixin ln. 99 - inject_setRotation()
animation.getCurrentGravityRotation(gravityDirection, timeMs).conjugate();

Entities in different gravities are rendered in the proper orientation, albeit with fucked limb animations like Fabric.

This leads me to believe that the issue is the Forge rendering pipeline, again.
