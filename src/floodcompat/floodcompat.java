package floodcompat;

import arc.*;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.mod.*;
import mindustry.world.blocks.defense.ForceProjector;
import mindustry.world.blocks.defense.MendProjector;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.blocks.defense.turrets.TractorBeamTurret;

import static mindustry.Vars.state;

public class floodcompat extends Mod{
    boolean flood, applied;
    public floodcompat(){
        Log.info("Flood Compatibility loaded!");

        Vars.netClient.addPacketHandler("flood", (integer) -> {
            if(Strings.canParseInt(integer)){
                flood = true;
                if(applied) return;
                Vars.ui.chatfrag.addMessage("[lime]Server check succeeded!\n[accent]Applying flood changes!");

                state.rules.hideBannedBlocks = true;
                state.rules.bannedBlocks.addAll(Blocks.lancer, Blocks.arc);
                state.rules.revealedBlocks.addAll(Blocks.coreShard, Blocks.scrapWall, Blocks.scrapWallLarge, Blocks.scrapWallHuge, Blocks.scrapWallGigantic);

                Blocks.scrapWall.solid=Blocks.titaniumWall.solid=Blocks.thoriumWall.solid=false;
                ((Wall) Blocks.phaseWall).chanceDeflect = 0;
                ((Wall) Blocks.surgeWall).lightningChance = 0;
                ((Wall) Blocks.reinforcedSurgeWall).lightningChance = 0;
                Blocks.berylliumWall.absorbLasers = true;
                Blocks.berylliumWall.insulated = true;
                Blocks.tungstenWall.absorbLasers = true;
                Blocks.tungstenWall.insulated = true;
                Blocks.carbideWall.absorbLasers = true;
                Blocks.carbideWall.insulated = true;
                ((MendProjector) Blocks.mender).reload = 800;
                ((MendProjector) Blocks.mendProjector).reload = 500;
                Blocks.radar.health = 500;
                Blocks.shockwaveTower.health = 2000;
                Blocks.thoriumReactor.health = 1400;
                Blocks.massDriver.health = 1250;
                Blocks.impactReactor.rebuildable = false;
                ((ItemTurret) Blocks.fuse).ammoTypes.forEach(a -> {
                    a.value.pierce = false;
                    if(a.value.damage == 66){
                        a.value.damage = 10;
                    }else a.value.damage = 20;
                });
                ((ItemTurret) Blocks.scathe).ammoTypes.forEach(a -> {
                    a.value.buildingDamageMultiplier = 0.3f;
                    a.value.damage = 700;
                    a.value.splashDamage = 80;
                });
                ((PowerTurret) Blocks.lancer).shootType.damage = 10;
                ((PowerTurret) Blocks.arc).shootType.damage = 4;
                ((PowerTurret) Blocks.arc).shootType.lightningLength = 15;
                ((TractorBeamTurret) Blocks.parallax).force = 8;
                ((TractorBeamTurret) Blocks.parallax).scaledForce = 7;
                ((TractorBeamTurret) Blocks.parallax).range = 230;
                ((TractorBeamTurret) Blocks.parallax).damage = 6;
                ((ForceProjector) Blocks.forceProjector).shieldHealth = 2500;

                UnitTypes.merui.weapons.forEach(w -> {
                    if(w.bullet instanceof ArtilleryBulletType) w.bullet.collides = true;
                });
                UnitTypes.quad.weapons.forEach(w -> {
                    w.bullet.pierceBuilding = true;
                    w.bullet.pierceCap = 9;
                });
                UnitTypes.alpha.weapons.forEach(w -> {
                    w.bullet.buildingDamageMultiplier = 1;
                });
                UnitTypes.beta.weapons.forEach(w -> {
                    w.bullet.buildingDamageMultiplier = 1;
                });
                UnitTypes.gamma.weapons.forEach(w -> {
                    w.bullet.buildingDamageMultiplier = 1;
                });
                UnitTypes.crawler.health = 100;
                UnitTypes.crawler.speed = 1.5f;
                UnitTypes.crawler.accel = 0.08f;
                UnitTypes.crawler.drag = 0.016f;
                UnitTypes.crawler.hitSize = 6f;
                UnitTypes.crawler.targetAir = false;
                UnitTypes.atrax.speed = 0.5f;
                UnitTypes.spiroct.speed = 0.4f;
                UnitTypes.spiroct.targetAir = false;
                UnitTypes.spiroct.weapons.forEach(w -> {
                    if(w.bullet.damage == 23){
                        w.bullet.damage = 25;
                    }else w.bullet.damage = 20;
                    if(w.bullet instanceof SapBulletType b) b.sapStrength = 0;
                });
                UnitTypes.arkyid.speed = 0.5f;
                UnitTypes.arkyid.hitSize = 21f;
                UnitTypes.arkyid.targetAir = false;
                UnitTypes.arkyid.weapons.forEach(w -> {
                    if(w.bullet instanceof SapBulletType b) b.sapStrength = 0;
                    if(w.bullet instanceof ArtilleryBulletType){
                        w.bullet.pierceBuilding = true;
                        w.bullet.pierceCap = 5;
                    }
                });
                UnitTypes.toxopid.hitSize = 21f;
                UnitTypes.flare.health = 275;
                UnitTypes.flare.engineOffset = 5.5f; // why?
                UnitTypes.flare.range = 140;
                UnitTypes.horizon.health = 440;
                UnitTypes.horizon.speed = 1.7f;
                UnitTypes.horizon.itemCapacity = 20;
                UnitTypes.zenith.health = 1400;
                UnitTypes.zenith.speed = 1.8f;
                UnitTypes.oct.abilities.forEach(a -> {
                    if(a instanceof ForceFieldAbility f){
                        f.regen = 16f;
                        f.max = 15000f;
                    }
                });
                UnitTypes.minke.weapons.forEach(w -> {
                    if(w.bullet instanceof FlakBulletType){
                        w.bullet.collidesGround = true;
                    }
                });

                applied = true;
            }
        });

        Events.on(EventType.WorldLoadEvent.class, e -> {
            // no delay if the client's hosting, that would break stuff!
            int delay = Vars.net.client() ? 3 : 0;
            flood = false;

            if(delay > 0) Call.serverPacketReliable("flood", "v0.2");
            Timer.schedule(() -> {
                // this is for cleanup only
                if(!flood){
                    if(Vars.net.client()) Vars.ui.chatfrag.addMessage("[scarlet]Server check failed...\n[accent]Playing on flood? Try rejoining!\nHave a nice day!");
                    if(applied){
                        Blocks.scrapWall.solid=Blocks.titaniumWall.solid=Blocks.thoriumWall.solid=true;
                        ((Wall) Blocks.phaseWall).chanceDeflect = 10;
                        ((Wall) Blocks.surgeWall).lightningChance = 0.05f;
                        ((Wall) Blocks.reinforcedSurgeWall).lightningChance = 0.05f;
                        Blocks.berylliumWall.absorbLasers = false;
                        Blocks.berylliumWall.insulated = false;
                        Blocks.tungstenWall.absorbLasers = false;
                        Blocks.tungstenWall.insulated = false;
                        Blocks.carbideWall.absorbLasers = false;
                        Blocks.carbideWall.insulated = false;
                        ((MendProjector) Blocks.mender).reload = 200;
                        ((MendProjector) Blocks.mendProjector).reload = 250;
                        Blocks.radar.health = 60;
                        Blocks.shockwaveTower.health = 915;
                        Blocks.thoriumReactor.health = 700;
                        Blocks.massDriver.health = 430;
                        Blocks.impactReactor.rebuildable = true;
                        ((ItemTurret) Blocks.fuse).ammoTypes.forEach(a -> {
                            a.value.pierce = true;
                            if(a.value.damage == 10){
                                a.value.damage = 66;
                            }else a.value.damage = 105;
                        });
                        ((ItemTurret) Blocks.scathe).ammoTypes.forEach(a -> {
                            a.value.buildingDamageMultiplier = 0.2f;
                            a.value.damage = 1500;
                            a.value.splashDamage = 160;
                        });
                        ((PowerTurret) Blocks.lancer).shootType.damage = 140;
                        ((PowerTurret) Blocks.arc).shootType.damage = 20;
                        ((PowerTurret) Blocks.arc).shootType.lightningLength = 25;
                        ((TractorBeamTurret) Blocks.parallax).force = 12f;
                        ((TractorBeamTurret) Blocks.parallax).scaledForce = 6f;
                        ((TractorBeamTurret) Blocks.parallax).range = 240f;
                        ((TractorBeamTurret) Blocks.parallax).damage = 0.3f;
                        ((ForceProjector) Blocks.forceProjector).shieldHealth = 750;

                        UnitTypes.merui.weapons.forEach(w -> {
                            if(w.bullet instanceof ArtilleryBulletType) w.bullet.collides = false;
                        });
                        UnitTypes.quad.weapons.forEach(w -> {
                            w.bullet.pierceBuilding = false;
                            w.bullet.pierceCap = -1;
                        });
                        UnitTypes.alpha.weapons.forEach(w -> {
                            w.bullet.buildingDamageMultiplier = 0.01f;
                        });
                        UnitTypes.beta.weapons.forEach(w -> {
                            w.bullet.buildingDamageMultiplier = 0.01f;
                        });
                        UnitTypes.gamma.weapons.forEach(w -> {
                            w.bullet.buildingDamageMultiplier = 0.01f;
                        });
                        UnitTypes.crawler.health = 200;
                        UnitTypes.crawler.speed = 1f;
                        UnitTypes.crawler.accel = 0;
                        UnitTypes.crawler.drag = 0;
                        UnitTypes.crawler.hitSize = 8f;
                        UnitTypes.crawler.targetAir = true;
                        UnitTypes.atrax.speed = 0.6f;
                        UnitTypes.spiroct.speed = 0.54f;
                        UnitTypes.spiroct.targetAir = true;
                        UnitTypes.spiroct.weapons.forEach(w -> {
                            if(w.bullet.damage == 25){
                                w.bullet.damage = 23;
                            }else w.bullet.damage = 18;
                            if(w.bullet instanceof SapBulletType b){
                                if(b.damage == 23){
                                    b.sapStrength = 0.5f;
                                }else b.sapStrength = 0.8f;
                            }
                        });
                        UnitTypes.arkyid.speed = 0.62f;
                        UnitTypes.arkyid.hitSize = 23f;
                        UnitTypes.arkyid.targetAir = true;
                        UnitTypes.arkyid.weapons.forEach(w -> {
                            if(w.bullet instanceof SapBulletType b) b.sapStrength = 0.85f;
                            if(w.bullet instanceof ArtilleryBulletType){
                                w.bullet.pierceBuilding = false;
                                w.bullet.pierceCap = 0;
                            }
                        });
                        UnitTypes.toxopid.hitSize = 26f;
                        UnitTypes.flare.health = 70;
                        UnitTypes.flare.range = 104;
                        UnitTypes.horizon.health = 340;
                        UnitTypes.horizon.speed = 1.65f;
                        UnitTypes.horizon.itemCapacity = 0;
                        UnitTypes.zenith.health = 700;
                        UnitTypes.zenith.speed = 1.7f;
                        UnitTypes.oct.abilities.forEach(a -> {
                            if(a instanceof ForceFieldAbility f){
                                f.regen = 4f;
                                f.max = 7000f;
                            }
                        });
                        UnitTypes.minke.weapons.forEach(w -> {
                            if(w.bullet instanceof FlakBulletType){
                                w.bullet.collidesGround = false;
                            }
                        });

                        Vars.ui.chatfrag.addMessage("[accent]Flood changes reverted!\nConsider using /sync if playing on a server!\nIf you are the host, ignore this message!");
                        applied = false;
                    }
                }
            }, delay);
        });
    }
}