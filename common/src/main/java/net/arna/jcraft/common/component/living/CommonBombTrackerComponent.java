package net.arna.jcraft.common.component.living;

import net.arna.jcraft.common.component.JComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public interface CommonBombTrackerComponent extends JComponent {
    class BombData {
        public boolean dirty = false;
        public boolean isEntity = false, isBlock = false, isItem = false;
        public Entity bombEntity;
        public BlockPos bombBlock;
        public ItemStack bombItem;
        /**
         * For recognizing the bombEntity in case the client world hadn't loaded it yet.
         */
        public int bombEntityID = -1;

        public void reset() {
            isEntity = isBlock = isItem = false;
            bombEntity = null;
            bombEntityID = -1;
            bombBlock = null;
            bombItem = null;
            dirty = true;
        }

        public @Nullable Vec3 getBombPos() {
            // Failsafe due to clientside bullshit
            if (bombEntity == null) {
                isEntity = false;
            }

            if (isEntity) {
                return bombEntity.position();
            }
            if (isBlock) {
                return Vec3.atLowerCornerOf(bombBlock);
            }
            if (isItem && bombItem.getEntityRepresentation() != null) {
                return bombItem.getEntityRepresentation().position();
            }
            return null;
        }

        public void setBomb(@Nullable Entity entity) {
            isEntity = isBlock = isItem = false;
            if (entity != null) {
                isEntity = true;
                bombEntityID = entity.getId();
                bombEntity = entity;
            }

            dirty = true;
        }

        public void setBomb(BlockPos blockPos) {
            isEntity = isItem = false;
            isBlock = true;
            bombBlock = blockPos;
            bombEntityID = -1;

            dirty = true;
        }

        public void setBomb(@Nullable ItemStack itemStack) {
            isEntity = isBlock = isItem = false;
            if (itemStack != null) {
                isItem = true;
                bombItem = itemStack;
            }
            bombEntityID = -1;

            dirty = true;
        }
    }

    BombData getMainBomb();

    BombData getBTD();
}
