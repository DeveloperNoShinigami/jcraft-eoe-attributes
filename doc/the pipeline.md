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

## Testing will be done with C-Moon's Utility facing +X (east), at the world origin
/tp @p 0.0 0.0 0.0 -90.0 0.0

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

# This leads me to believe that the issue is the Forge rendering pipeline, again.

In GameRenderer.class on Forge, there are extra hook lines that may impact the outcome.
Right after Camera.setup(), which eventually goes into inject_setRotation()
```java
this.resetProjectionMatrix(matrix4f);
camera.setup(this.minecraft.level, (Entity)(this.minecraft.getCameraEntity() == null ? this.minecraft.player : this.minecraft.getCameraEntity()), !this.minecraft.options.getCameraType().isFirstPerson(), this.minecraft.options.getCameraType().isMirrored(), partialTicks);
// Hook 1
ViewportEvent.ComputeCameraAngles cameraSetup = ForgeHooksClient.onCameraSetup(this, camera, partialTicks);
camera.setAnglesInternal(cameraSetup.getYaw(), cameraSetup.getPitch());
poseStack.mulPose(Axis.ZP.rotationDegrees(cameraSetup.getRoll()));
poseStack.mulPose(Axis.XP.rotationDegrees(camera.getXRot()));
poseStack.mulPose(Axis.YP.rotationDegrees(camera.getYRot() + 180.0F));
Matrix3f matrix3f = (new Matrix3f(poseStack.last().normal())).invert();
RenderSystem.setInverseViewRotationMatrix(matrix3f);
this.minecraft.levelRenderer.prepareCullFrustum(poseStack, camera.getPosition(), this.getProjectionMatrix(Math.max(d0, (double)(Integer)this.minecraft.options.fov().get())));
this.minecraft.levelRenderer.renderLevel(poseStack, partialTicks, finishTimeNano, flag, camera, this, this.lightTexture, matrix4f);
this.minecraft.getProfiler().popPush("forge_render_last");
// Hook 2
ForgeHooksClient.dispatchRenderStage(Stage.AFTER_LEVEL, this.minecraft.levelRenderer, posestack, matrix4f, this.minecraft.levelRenderer.getTicks(), camera, this.minecraft.levelRenderer.getFrustum());
this.minecraft.getProfiler().popPush("hand");
if (this.renderHand) {
    RenderSystem.clear(256, Minecraft.ON_OSX);
    this.renderItemInHand(poseStack, camera, partialTicks);
}
```
The default one, used on Fabric, looks like this:
```java
this.resetProjectionMatrix(matrix4f);
camera.setup(this.minecraft.level, (Entity)(this.minecraft.getCameraEntity() == null ? this.minecraft.player : this.minecraft.getCameraEntity()), !this.minecraft.options.getCameraType().isFirstPerson(), this.minecraft.options.getCameraType().isMirrored(), partialTicks);
poseStack.mulPose(Axis.XP.rotationDegrees(camera.getXRot()));
poseStack.mulPose(Axis.YP.rotationDegrees(camera.getYRot() + 180.0F));
Matrix3f matrix3f = (new Matrix3f(poseStack.last().normal())).invert();
RenderSystem.setInverseViewRotationMatrix(matrix3f);
this.minecraft.levelRenderer.prepareCullFrustum(poseStack, camera.getPosition(), this.getProjectionMatrix(Math.max(d, (double)(Integer)this.minecraft.options.fov().get())));
this.minecraft.levelRenderer.renderLevel(poseStack, partialTicks, finishTimeNano, bl, camera, this, this.lightTexture, matrix4f);
this.minecraft.getProfiler().popPush("hand");
if (this.renderHand) {
    RenderSystem.clear(256, Minecraft.ON_OSX);
    this.renderItemInHand(poseStack, camera, partialTicks);
}
```