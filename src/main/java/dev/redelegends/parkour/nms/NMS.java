package dev.redelegends.parkour.nms;

import dev.redelegends.parkour.nms.entity.EntityTopAmorStand;
import dev.redelegends.reflection.Accessors;
import dev.redelegends.reflection.acessors.FieldAccessor;
import dev.redelegends.utils.Utils;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@SuppressWarnings({"unchecked", "rawtypes"})
public class NMS {

  private static final FieldAccessor<Map> CLASS_TO_ID, CLASS_TO_NAME;
  private static final FieldAccessor<List> PATHFINDERGOAL_B, PATHFINDERGOAL_C;

  static {
    CLASS_TO_ID = Accessors.getField(EntityTypes.class, "f", Map.class);
    CLASS_TO_NAME = Accessors.getField(EntityTypes.class, "d", Map.class);
    PATHFINDERGOAL_B = Accessors.getField(PathfinderGoalSelector.class, 0, List.class);
    PATHFINDERGOAL_C = Accessors.getField(PathfinderGoalSelector.class, 1, List.class);

    CLASS_TO_ID.get(null).put(EntityTopAmorStand.class, 55);
    CLASS_TO_NAME.get(null).put(EntityTopAmorStand.class, "LegendsAmorStandTop");
  }

  public static void look(Object entity, float yaw, float pitch) {
    if (entity instanceof Entity) {
      entity = ((CraftEntity) entity).getHandle();
    }

    yaw = Utils.clampYaw(yaw);
    net.minecraft.server.v1_8_R3.Entity handle = (net.minecraft.server.v1_8_R3.Entity) entity;
    handle.yaw = yaw;
    handle.pitch = pitch;
    if (handle instanceof EntityLiving) {
      ((EntityLiving) handle).aJ = yaw;
      if (handle instanceof EntityHuman) {
        ((EntityHuman) handle).aI = yaw;
      }
      ((EntityLiving) handle).aK = yaw;
    }
  }

  public static void clearPathfinderGoal(Object entity) {
    if (entity instanceof Entity) {
      entity = ((CraftEntity) entity).getHandle();
    }

    net.minecraft.server.v1_8_R3.Entity handle = (net.minecraft.server.v1_8_R3.Entity) entity;
    if (handle instanceof EntityInsentient) {
      EntityInsentient entityInsentient = (EntityInsentient) handle;
      PATHFINDERGOAL_B.get(entityInsentient.goalSelector).clear();
      PATHFINDERGOAL_C.get(entityInsentient.targetSelector).clear();
    }
  }

  public static EntityTopAmorStand createAmorStandTop(Location loc, Integer position, ItemStack head) {
    EntityTopAmorStand topAmorStand = new EntityTopAmorStand(loc, position, head);
    topAmorStand.world.addEntity(topAmorStand, CreatureSpawnEvent.SpawnReason.CUSTOM);
    return topAmorStand;
  }
}
