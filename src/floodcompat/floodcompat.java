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

public class floodcompat extends Mod{
    boolean flood, applied;
    public floodcompat(){
        Log.info("Flood Compatibility loaded!");
        Ability PulsarAbility = UnitTypes.pulsar.abilities.get(0);
        Ability BrydeAbility = UnitTypes.bryde.abilities.get(0);

        Vars.netClient.addPacketHandler("flood", (integer) -> {
            if(Strings.canParseInt(integer)){
                flood = true;
                if(applied) return;
                Vars.ui.chatfrag.addMessage("[lime]Server check succeeded!\n[accent]Applying flood changes!");

                Blocks.scrapWall.solid=Blocks.titaniumWall.solid=Blocks.thoriumWall.solid=false;
                Blocks.scrapWall.health = 50;
                Blocks.scrapWall.armor = 0;
                Blocks.titaniumWall.health = 75;
                Blocks.titaniumWall.armor = 0;
                Blocks.thoriumWall.health = 100;
                Blocks.thoriumWall.armor = 0;
                Blocks.phaseWall.health = 125;
                Blocks.phaseWall.armor = 0;
                Blocks.surgeWall.health = 150;
                Blocks.surgeWall.armor = 0;
                Blocks.reinforcedSurgeWall.health = 175;
                Blocks.reinforcedSurgeWall.armor = 0;
                Blocks.plastaniumWall.health = 200;
                Blocks.plastaniumWall.armor = 0;
                Blocks.berylliumWall.health = 225;
                Blocks.berylliumWall.armor = 0;
                Blocks.berylliumWall.absorbLasers = true;
                Blocks.tungstenWall.health = 250;
                Blocks.tungstenWall.armor = 0;
                Blocks.tungstenWall.absorbLasers = true;
                Blocks.carbideWall.health = 300;
                Blocks.carbideWall.armor = 0;
                Blocks.carbideWall.absorbLasers = true;
                Blocks.radar.health = 500;
                Blocks.shockwaveTower.health = 2000;
                Blocks.thoriumReactor.health = 1400;
                Blocks.impactReactor.rebuildable = false;

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
                UnitTypes.pulsar.abilities.remove(0);
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
                UnitTypes.bryde.abilities.remove(0);

                applied = true;
            }
        });

        Events.on(EventType.WorldLoadEvent.class, e -> {
            // no delay if the client's hosting, that would break stuff!
            int delay = Vars.net.client() ? 3 : 0;
            flood = false;

            if(delay > 0) Call.serverPacketReliable("flood", "v0.1");
            Timer.schedule(() -> {
                // this is for cleanup only
                if(!flood){
                    if(Vars.net.client()) Vars.ui.chatfrag.addMessage("[scarlet]Server check failed...\n[accent]Playing on flood? Try rejoining!\nHave a nice day!");
                    if(applied){
                        Blocks.scrapWall.solid=Blocks.titaniumWall.solid=Blocks.thoriumWall.solid=true;
                        Blocks.scrapWall.health = 240;
                        Blocks.scrapWall.armor = 0;
                        Blocks.titaniumWall.health = 440;
                        Blocks.titaniumWall.armor = 0;
                        Blocks.thoriumWall.health = 800;
                        Blocks.thoriumWall.armor = 0;
                        Blocks.phaseWall.health = 600;
                        Blocks.phaseWall.armor = 0;
                        Blocks.surgeWall.health = 920;
                        Blocks.surgeWall.armor = 0;
                        Blocks.reinforcedSurgeWall.health = 1000;
                        Blocks.reinforcedSurgeWall.armor = 20f;
                        Blocks.plastaniumWall.health = 500;
                        Blocks.plastaniumWall.armor = 0;
                        Blocks.berylliumWall.health = 520;
                        Blocks.berylliumWall.armor = 2f;
                        Blocks.berylliumWall.absorbLasers = false;
                        Blocks.tungstenWall.health = 720;
                        Blocks.tungstenWall.armor = 14f;
                        Blocks.tungstenWall.absorbLasers = false;
                        Blocks.carbideWall.health = 1080;
                        Blocks.carbideWall.armor = 16f;
                        Blocks.carbideWall.absorbLasers = false;
                        Blocks.radar.health = 60;
                        Blocks.shockwaveTower.health = 915;
                        Blocks.thoriumReactor.health = 700;
                        Blocks.impactReactor.rebuildable = true;

                        UnitTypes.merui.weapons.forEach(w -> {
                            if(w.bullet instanceof ArtilleryBulletType) w.bullet.collides = false;
                        });
                        UnitTypes.quad.weapons.forEach(w -> {
                            w.bullet.pierceBuilding = false;
                            w.bullet.pierceCap = 0;
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
                        UnitTypes.pulsar.abilities.add(PulsarAbility);
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
                        UnitTypes.bryde.abilities.add(BrydeAbility);

                        Vars.ui.chatfrag.addMessage("[accent]Flood changes reverted!\nConsider using /sync if playing on a server!\nIf you are the host, ignore this message!");
                        applied = false;
                    }
                }
            }, delay);
        });
    }
}