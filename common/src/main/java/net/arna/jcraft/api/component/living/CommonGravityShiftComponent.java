package net.arna.jcraft.api.component.living;


public interface CommonGravityShiftComponent {
    void startRadial();

    void startDirectional();

    void swapRadialType();

    boolean isActive();

    void stop();
}
