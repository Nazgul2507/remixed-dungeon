package com.nyrds.pixeldungeon.items.chaos;

import com.nyrds.Packable;
import com.nyrds.android.util.TrackedRuntimeException;
import com.nyrds.pixeldungeon.ml.R;
import com.nyrds.pixeldungeon.mobs.common.MobFactory;
import com.watabou.noosa.Game;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.actors.Actor;
import com.watabou.pixeldungeon.actors.Char;
import com.watabou.pixeldungeon.actors.mobs.Boss;
import com.watabou.pixeldungeon.actors.mobs.Mob;
import com.watabou.pixeldungeon.actors.mobs.npcs.NPC;
import com.watabou.pixeldungeon.items.Item;
import com.watabou.pixeldungeon.items.potions.PotionOfHealing;
import com.watabou.pixeldungeon.items.wands.Wand;
import com.watabou.pixeldungeon.items.wands.WandOfTeleportation;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class ChaosStaff extends Wand implements IChaosItem {

    @Packable
    private int charge = 0;

    public ChaosStaff() {
        imageFile = "items/chaosStaff.png";
        image = 0;
    }


    @Override
    public Item upgrade() {
        super.upgrade();
        selectImage();
        return this;
    }

    @Override
    public Item degrade() {
        super.degrade();
        selectImage();
        return this;
    }

    @Override
    public void ownerTakesDamage(int damage) {
        charge++;
    }

    @Override
    public void ownerDoesDamage(Char ch, int damage) {
    }

    private void selectImage() {
        image = Math.max(0, Math.min(level() / 3, 4));
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);

        selectImage();
    }

    @Override
    protected void onZap(int cell) {

        ChaosCommon.doChaosMark(cell, 10 + level() * 10 + charge);
        charge = 0;

        if (Math.random() < 0.1f) {
            Char ch = Actor.findChar(cell);
            if (ch instanceof Mob) {
                Mob mob = (Mob) ch;

                if ((mob instanceof Boss) || (mob instanceof NPC)) {
                    return;
                }

                switch (Random.Int(0, 4)) {
                    case 0:
                        mob.die(getUser());
                        break;
                    case 1:
                        Mob.makePet(mob, getUser().getId());
                        break;

                    case 2:
                        int nextCell = Dungeon.level.getEmptyCellNextTo(cell);

                        if (Dungeon.level.cellValid(nextCell)) {
                            try {
                                Mob newMob = MobFactory.mobByName(mob.getEntityKind());
                                Dungeon.level.spawnMob(newMob);
                            } catch (Exception e) {
                                throw new TrackedRuntimeException(e);
                            }
                        }
                        break;
                    case 3:
                        WandOfTeleportation.teleport(mob);
                        break;
                    case 4:
                        PotionOfHealing.heal(ch, 1);
                        break;
                }
            }
        }
    }

    @Override
    public String name() {
        return Game.getVar(R.string.ChaosStaff_Name);
    }

    @Override
    public String info() {
        return Game.getVar(R.string.ChaosStaff_Info);
    }
}
