package dev.guardianware.api.utilities;

import dev.guardianware.client.events.EventMotion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.MathHelper;

public class RotationUtils implements IMinecraft {
    private static float c;
    private static float b;

    public static void rotateGrim(float yaw, float pitch) {
        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Full(mc.player.getX(), mc.player.getY(), mc.player.getZ(), yaw, pitch, mc.player.isOnGround()));
    }

    public static float[] getRotations(double posX, double posY, double posZ) {
        PlayerEntity player = mc.player;
        double x = posX - player.getX();
        double y = posY - (player.getY() + (double) player.getEyeHeight(player.getPose()));
        double z = posZ - player.getZ();
        double dist = MathHelper.sqrt((float) (x * x + z * z));
        float yaw = (float) (Math.atan2(z, x) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float) (-(Math.atan2(y, dist) * 180.0 / Math.PI));
        return new float[]{yaw, pitch};
    }

    public static float[] getRotationsEntity(Entity entity) {
        return RotationUtils.getRotations(entity.getX(), entity.getEyeY(), entity.getZ());
    }

    public static void rotate(EventMotion event, float[] angle) {
        event.setRotationYaw(angle[0]);
        event.setRotationPitch(angle[1]);
    }

    public static float[] getSmoothRotations(float[] angles, int smooth) {
        float var2 = MathHelper.clamp(1.0f - (float) smooth / 100.0f, 0.1f, 1.0f);
        c += (angles[0] - c) * var2;
        b += (angles[1] - b) * var2;
        return new float[]{MathHelper.wrapDegrees(c), b};
    }

    public static float getDistanceBetweenAngles(float angle1, float angle2) {
        float angle = Math.abs(angle1 - angle2) % 360.0f;
        if (angle > 180.0f) {
            angle = 360.0f - angle;
        }
        return angle;
    }

    public static float todeffyaw(float yaw) {
        float yawn = yaw;
        if (yawn < 0) {
            yawn *= -1;
            yawn += 180;
        } else {
            yawn = 360 - 180 - yaw;
        }
        if (yawn < 0) {
            yawn *= -1;
            yawn = yawn % 360;
            yawn = 360 - yawn;
        }
        return yawn % 360;
    }

    public static float tomineyaw(float yaw) {
        yaw = mody(yaw);
        if (yaw < 0) {
            yaw = 360 - (yaw * -1);
        }
        float yawn = yaw;
        if (yaw <= 180) {
            yawn = 180 - yaw;
        } else {
            yawn = yaw - 180;
            yawn *= -1;
        }

        return yawn % 360;
    }

    public static float getRotToPos(double PosX, double PosZ) {
        final float deltaX = (float) (PosX - mc.player.getX());

        final float deltaZ = (float) (PosZ - mc.player.getZ());

        final float distance = (float) (Math.sqrt(Math.pow(deltaX, 2.0))
                + Math.sqrt(Math.pow(deltaZ, 2.0)));

        float yaw = (float) Math.toDegrees(-Math.atan(deltaX / deltaZ));

        if (deltaX < 0.0f && deltaZ < 0.0f) {
            yaw = (float) (90.0 + Math.toDegrees(Math.atan(deltaZ / deltaX)));
        } else if (deltaX > 0.0f && deltaZ < 0.0f) {
            yaw = (float) (-90.0 + Math.toDegrees(Math.atan(deltaZ / deltaX)));
        }
        if (yaw < -360) {
            yaw += 360;
        }
        if (yaw > 360) {
            yaw -= 360;
        }

        return yaw;
    }

    public static float pitchdiff(float pitch1, float pitch2) {
        return Math.abs(pitch1 - pitch2);
    }

    public static float yawdiff(float yaw1, float yaw2) {
        return yawdeffdiff(todeffyaw(yaw1), todeffyaw(yaw2));
    }

    public static float yawdeffdiff(float yaw1, float yaw2) {
        yaw1 = mody(yaw1);
        if (yaw1 < 0) {
            yaw1 = 360 - (yaw1 * -1);
        }
        yaw2 = mody(yaw2);
        if (yaw2 < 0) {
            yaw2 = 360 - (yaw2 * -1);
        }

        float ans1 = Math.abs(yaw1 - yaw2);
        float fyawto360 = Math.min(360 - yaw1, yaw1);
        float syawto360 = Math.min(360 - yaw2, yaw2);
        float ans2 = Math.abs(fyawto360 + syawto360);

        float ans = Math.min(ans1, ans2);

        return ans;
    }

    public static float addpitch(
            float pitch1, float pitch2, float value, boolean over) {
        float absvalue = Math.abs(value);

        if (pitch1 > pitch2) {
            if (over && pitch1 - value < pitch2) {
                pitch1 = pitch2;
            } else {
                pitch1 -= value;
            }
        } else {
            if (over && pitch1 + value > pitch2) {
                pitch1 = pitch2;
            } else {
                pitch1 += value;
            }
        }
        return pitch1;
    }

    public static float addyaw(
            float yaw1, float yaw2, float value, boolean over) {
        return tomineyaw(adddeffyaw(todeffyaw(yaw1), todeffyaw(yaw2), value, over));
    }

    public static float addyaw(float yaw1, float value) {
        return tomineyaw(adddeffyaw(todeffyaw(yaw1), value));
    }

    public static float addyawWithDeffArg(
            float yaw1, float yaw2, float value, boolean over) {
        return tomineyaw(adddeffyaw(yaw1, yaw2, value, over));
    }

    public static float addyawWithDeffArg(float yaw1, float value) {
        return tomineyaw(adddeffyaw(yaw1, value));
    }

    public static float adddeffyaw(
            float yaw1, float yaw2, float value, boolean over) {
        yaw1 = mody(yaw1);
        if (yaw1 < 0) {
            yaw1 = 360 - (yaw1 * -1);
        }
        yaw2 = mody(yaw2);
        if (yaw2 < 0) {
            yaw2 = 360 - (yaw2 * -1);
        }

        value = mody(value);

        if (over && yawdeffdiff(yaw1, yaw2) <= value) {
            return yaw2;
        }

        if (yaw1 >= yaw2) {
            float ans1 = Math.abs(yaw1 - yaw2);
            float fyawto360 = 360 - yaw1;
            if (yaw1 <= 180) {
                fyawto360 = yaw1;
            }
            float syawto360 = 360 - yaw2;
            if (yaw2 <= 180) {
                syawto360 = yaw2;
            }

            float ans2 = Math.abs(fyawto360 + syawto360);
            float ans = 0;
            if (ans1 <= ans2) {
                ans = yaw1 - value;
                ans = mody(ans);
                if (ans < 0) {
                    ans = 360 - (ans * -1);
                }
                return ans;
            } else {
                ans = yaw1 + value;
                ans = mody(ans);
                if (ans < 0) {
                    ans = 360 - (ans * -1);
                }
                return ans;
            }
        } else {
            float ans1 = Math.abs(yaw1 - yaw2);
            float fyawto360 = 360 - yaw1;
            if (yaw1 <= 180) {
                fyawto360 = yaw1;
            }
            float syawto360 = 360 - yaw2;
            if (yaw2 <= 180) {
                syawto360 = yaw2;
            }
            float ans2 = Math.abs(fyawto360 + syawto360);
            float ans;

            if (ans1 < ans2) {
                ans = yaw1 + value;
                ans = mody(ans);
                if (ans < 0) {
                    ans = 360 - (ans * -1);
                }
                return ans;
            } else {
                ans = yaw1 - value;
                ans = mody(ans);
                if (ans < 0) {
                    ans *= -1;
                    ans = 360 - ans;
                }
                return ans;
            }
        }
    }

    public static float adddeffyaw(float yaw1, float value) {
        yaw1 = mody(yaw1);
        if (yaw1 < 0) {
            yaw1 = 360 - (yaw1 * -1);
        }
        value = mody(value);
        float ans = yaw1 + value;
        ans = mody(ans);
        if (ans < 0) {
            ans = 360 - (ans * -1);
        }
        return ans % 360;
    }

    public static float mody(float yaw) {
        boolean min = yaw < 0;
        float ans = yaw % 360;
        if (min) {
            return ans;
        } else {
            return ans;
        }
    }
}
