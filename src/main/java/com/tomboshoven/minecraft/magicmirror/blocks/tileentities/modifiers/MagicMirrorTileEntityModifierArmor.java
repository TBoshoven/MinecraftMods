package com.tomboshoven.minecraft.magicmirror.blocks.tileentities.modifiers;

import com.tomboshoven.minecraft.magicmirror.ModMagicMirror;
import com.tomboshoven.minecraft.magicmirror.blocks.modifiers.MagicMirrorModifier;
import com.tomboshoven.minecraft.magicmirror.blocks.tileentities.TileEntityMagicMirrorBase;
import com.tomboshoven.minecraft.magicmirror.reflection.Reflection;
import com.tomboshoven.minecraft.magicmirror.reflection.modifiers.ReflectionModifierArmor;
import com.tomboshoven.minecraft.magicmirror.reflection.modifiers.ReflectionModifierArmor.Factory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.SidedProxy;

import javax.annotation.Nullable;
import java.util.Random;

public class MagicMirrorTileEntityModifierArmor extends MagicMirrorTileEntityModifier {
    /**
     * The number of ticks this modifier needs to cool down.
     */
    private static final int COOLDOWN_TICKS = 20;
    private static final int SWAP_PARTICLE_COUNT = 64;

    /**
     * The replacement armor as stored in the mirror.
     */
    private final ReplacementArmor replacementArmor = new ReplacementArmor();

    @SidedProxy(
            serverSide = "com.tomboshoven.minecraft.magicmirror.reflection.modifiers.ReflectionModifierArmor$Factory",
            clientSide = "com.tomboshoven.minecraft.magicmirror.reflection.modifiers.ReflectionModifierArmorClient$Factory"
    )
    private static Factory reflectionModifierFactory;

    /**
     * The object that modifies the reflection in the mirror to show the replacement armor.
     */
    @Nullable
    private ReflectionModifierArmor reflectionModifier;

    /**
     * @param modifier The modifier that applied this object to the tile entity.
     */
    public MagicMirrorTileEntityModifierArmor(MagicMirrorModifier modifier) {
        super(modifier);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        return replacementArmor.writeToNBT(super.writeToNBT(nbt));
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        replacementArmor.readFromNBT(nbt);
    }

    @Override
    public void remove(World world, BlockPos pos) {
        InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.ARMOR_STAND));
        replacementArmor.spawn(world, pos);
    }

    @Override
    public void activate(TileEntityMagicMirrorBase tileEntity) {
        Reflection reflection = tileEntity.getReflection();
        if (reflection != null) {
            reflectionModifier = tileEntity.getWorld().isRemote ? reflectionModifierFactory.createClient(replacementArmor) : reflectionModifierFactory.createServer(replacementArmor);
            reflection.addModifier(reflectionModifier);
        }
    }

    @Override
    public void deactivate(TileEntityMagicMirrorBase tileEntity) {
        if (reflectionModifier != null) {
            Reflection reflection = tileEntity.getReflection();
            if (reflection != null) {
                reflection.removeModifier(reflectionModifier);
            }
        }
    }

    @Override
    public boolean tryPlayerActivate(TileEntityMagicMirrorBase tileEntity, EntityPlayer playerIn, EnumHand hand) {
        if (coolingDown()) {
            return false;
        }

        ModMagicMirror.logger.debug("Swapped inventory");
        replacementArmor.swap(playerIn.inventory.armorInventory);
        playerIn.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, .8f, .4f);
        Random random = new Random();
        for (int i = 0; i < SWAP_PARTICLE_COUNT; ++i) {
            playerIn.getEntityWorld().spawnParticle(
                    EnumParticleTypes.PORTAL,
                    playerIn.posX + random.nextGaussian() / 4,
                    playerIn.posY + 2 * random.nextDouble(),
                    playerIn.posZ + random.nextGaussian() / 4,
                    random.nextGaussian() / 2,
                    random.nextDouble(),
                    random.nextGaussian() / 2
            );
        }
        setCooldown(COOLDOWN_TICKS);
        tileEntity.markDirty();
        return true;
    }

    /**
     * Container class for a swapped-out inventory.
     */
    public static class ReplacementArmor {
        /**
         * The armor inventory that is swapped out.
         */
        private final NonNullList<ItemStack> replacementInventory = NonNullList.withSize(4, ItemStack.EMPTY);

        /**
         * Swap the current inventory with another.
         * They inventories should have the same size.
         *
         * @param inventory The inventory to swap with.
         */
        public void swap(NonNullList<ItemStack> inventory) {
            if (inventory != null) {
                for (int i = 0; i < 4; ++i) {
                    ItemStack replacement = replacementInventory.get(i);
                    replacementInventory.set(i, inventory.get(i));
                    inventory.set(i, replacement);
                }
            }
        }

        /**
         * Write the inventory out to an NBT tag compound.
         *
         * @param nbt The NBT tag compound to write to.
         * @return The input compound, for chaining.
         */
        NBTTagCompound writeToNBT(NBTTagCompound nbt) {
            ItemStackHelper.saveAllItems(nbt, replacementInventory, true);
            return nbt;
        }

        /**
         * Load inventory from an NBT tag.
         *
         * @param nbt The NBT tag compound to read from.
         */
        void readFromNBT(NBTTagCompound nbt) {
            ItemStackHelper.loadAllItems(nbt, replacementInventory);
        }

        /**
         * Spawn the inventory items as entities into the world.
         * <p>
         * This removes them from this inventory.
         *
         * @param world The world in which to spawn the item entities.
         * @param pos   The location to spawn the item entities in.
         */
        void spawn(World world, BlockPos pos) {
            for (ItemStack itemStack : replacementInventory) {
                if (!itemStack.isEmpty()) {
                    InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), itemStack);
                    itemStack.setCount(0);
                }
            }
        }
    }
}
