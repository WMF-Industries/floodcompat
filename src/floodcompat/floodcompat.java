package floodcompat;

import arc.*;
import arc.struct.Seq;
import arc.util.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.mod.*;
import mindustry.world.blocks.defense.*;
import mindustry.world.blocks.defense.turrets.*;

import java.util.Objects;

import static mindustry.content.UnitTypes.*;
import static mindustry.content.Blocks.*;
import static mindustry.Vars.*;

public class floodcompat extends Mod{
    boolean flood, applied, enabled;
    Ability pulsarAbility, brydeAbility;
    public floodcompat(){
        Log.info("Flood Compatibility loaded!");

        Events.on(EventType.ContentInitEvent.class, e -> {
            pulsarAbility = pulsar.abilities.first();
            brydeAbility = bryde.abilities.first();
        });

        Events.on(EventType.ClientLoadEvent.class, e -> {
            if(Structs.contains(Version.class.getDeclaredFields(), var -> var.getName().equals("foos"))){
                ui.showInfo("[accent]Foo's Client [scarlet]detected, [cyan]FloodCompat[] is unnecessary!");
                enabled = false;
            }else enabled = true;
        });

        netClient.addPacketHandler("flood", (integer) -> {
            if(Strings.canParseInt(integer)){
                flood = true;
                if(applied) return;
                ui.chatfrag.addMessage("[lime]Server check succeeded!\n[accent]Applying flood changes!");

                state.rules.hideBannedBlocks = true;
                state.rules.bannedBlocks.addAll(Blocks.lancer, Blocks.arc);
                state.rules.revealedBlocks.addAll(Blocks.coreShard, Blocks.scrapWall, Blocks.scrapWallLarge, Blocks.scrapWallHuge, Blocks.scrapWallGigantic);

                Seq.with(scrapWall, titaniumWall, thoriumWall).each(w -> w.solid = false);
                Seq.with(berylliumWall, tungstenWall, carbideWall).each(w -> {
                    w.insulated = w.absorbLasers = true;
                });
                ((Wall) phaseWall).chanceDeflect = 0;
                ((Wall) surgeWall).lightningChance = 0;
                ((Wall) reinforcedSurgeWall).lightningChance = 0;
                ((MendProjector) mender).reload = 800;
                ((MendProjector) mendProjector).reload = 500;
                radar.health = 500;
                shockwaveTower.health = 2000;
                thoriumReactor.health = 1400;
                massDriver.health = 1250;
                impactReactor.rebuildable = false;
                ((ItemTurret) fuse).ammoTypes.values().toSeq().each(a -> a.pierce = false);
                ((ItemTurret) fuse).ammoTypes.get(Items.titanium).damage = 10;
                ((ItemTurret) fuse).ammoTypes.get(Items.thorium).damage = 20;
                ((ItemTurret) scathe).ammoTypes.values().toSeq().each(a -> {
                    a.buildingDamageMultiplier = 0.3f;
                    a.damage = 700;
                    a.splashDamage = 80;
                });
                ((PowerTurret) lancer).shootType.damage = 10;
                ((PowerTurret) arc).shootType.damage = 4;
                ((PowerTurret) arc).shootType.lightningLength = 15;
                ((TractorBeamTurret) parallax).force = 8;
                ((TractorBeamTurret) parallax).scaledForce = 7;
                ((TractorBeamTurret) parallax).range = 230;
                ((TractorBeamTurret) parallax).damage = 6;
                ((ForceProjector) forceProjector).shieldHealth = 2500;

                merui.weapons.each(w -> w.bullet.collides = true);
                quad.weapons.each(w -> {
                    w.bullet.pierceBuilding = true;
                    w.bullet.pierceCap = 9;
                });
                Seq.with(alpha, beta, gamma).flatMap(u -> u.weapons).each(w -> w.bullet.buildingDamageMultiplier = 1);
                Seq.with(crawler, spiroct, arkyid).each(u -> u.targetAir = false);
                crawler.health = 100;
                crawler.speed = 1.5f;
                crawler.accel = 0.08f;
                crawler.drag = 0.016f;
                crawler.hitSize = 6f;
                atrax.speed = 0.5f;
                pulsar.abilities.clear();
                bryde.abilities.clear();
                spiroct.speed = 0.4f;
                spiroct.weapons.each(w -> {
                    if(Objects.equals(w.name, "spiroct-weapon")){
                        w.bullet.damage = 25;
                    }else w.bullet.damage = 20;
                    if(w.bullet instanceof SapBulletType b) b.sapStrength = 0;
                });
                arkyid.speed = 0.5f;
                arkyid.hitSize = 21f;
                arkyid.weapons.each(w -> {
                    if(w.bullet instanceof SapBulletType b){
                        b.sapStrength = 0;
                    }else{
                        w.bullet.pierceBuilding = true;
                        w.bullet.pierceCap = 5;
                    }
                });
                toxopid.hitSize = 21f;
                flare.health = 275;
                flare.engineOffset = 5.5f; // why?
                flare.range = 140;
                horizon.health = 440;
                horizon.speed = 1.7f;
                horizon.itemCapacity = 20;
                zenith.health = 1400;
                zenith.speed = 1.8f;
                vela.weapons.each(w -> w.bullet.damage = 20f);
                oct.abilities.each(a -> {
                    if(a instanceof ForceFieldAbility f){
                        f.regen = 16f;
                        f.max = 15000f;
                    }
                });
                minke.weapons.each(w -> {
                    if(w.bullet instanceof FlakBulletType){
                        w.bullet.collidesGround = true;
                    }
                });

                applied = true;
            }
        });

        Events.on(EventType.WorldLoadEvent.class, e -> {
            if(!enabled) return;
            // no delay if the client's hosting, that would break stuff!
            int delay = net.client() ? 3 : 0;
            flood = false;

            if(delay > 0) Call.serverPacketReliable("flood", "v0.3");
            Timer.schedule(() -> {
                // this is for cleanup only
                if(!flood){
                    if(net.client()) ui.chatfrag.addMessage("[scarlet]Server check failed...\n[accent]Playing on flood? Try rejoining!\nHave a nice day!");
                    if(applied){
                        Seq.with(scrapWall, titaniumWall, thoriumWall).each(w -> w.solid = true);
                        Seq.with(berylliumWall, tungstenWall, carbideWall).each(w -> {
                            w.insulated = w.absorbLasers = false;
                        });
                        ((Wall) phaseWall).chanceDeflect = 10;
                        ((Wall) surgeWall).lightningChance = 0.05f;
                        ((Wall) reinforcedSurgeWall).lightningChance = 0.05f;
                        ((MendProjector) mender).reload = 200;
                        ((MendProjector) mendProjector).reload = 250;
                        radar.health = 60;
                        shockwaveTower.health = 915;
                        thoriumReactor.health = 700;
                        massDriver.health = 430;
                        impactReactor.rebuildable = true;
                        ((ItemTurret) fuse).ammoTypes.values().toSeq().each(a -> a.pierce = true);
                        ((ItemTurret) fuse).ammoTypes.get(Items.titanium).damage = 66;
                        ((ItemTurret) fuse).ammoTypes.get(Items.thorium).damage = 105;
                        ((ItemTurret) scathe).ammoTypes.values().toSeq().each(a -> {
                            a.buildingDamageMultiplier = 0.2f;
                            a.damage = 1500;
                            a.splashDamage = 160;
                        });
                        ((PowerTurret) lancer).shootType.damage = 140;
                        ((PowerTurret) arc).shootType.damage = 20;
                        ((PowerTurret) arc).shootType.lightningLength = 25;
                        ((TractorBeamTurret) parallax).force = 12f;
                        ((TractorBeamTurret) parallax).scaledForce = 6f;
                        ((TractorBeamTurret) parallax).range = 240f;
                        ((TractorBeamTurret) parallax).damage = 0.3f;
                        ((ForceProjector) forceProjector).shieldHealth = 750;

                        merui.weapons.each(w -> w.bullet.collides = false);
                        quad.weapons.each(w -> {
                            w.bullet.pierceBuilding = false;
                            w.bullet.pierceCap = -1;
                        });
                        Seq.with(alpha, beta, gamma).flatMap(u -> u.weapons).each(w -> w.bullet.buildingDamageMultiplier = 0.01f);
                        Seq.with(crawler, spiroct, arkyid).each(u -> u.targetAir = true);
                        crawler.health = 200;
                        crawler.speed = 1f;
                        crawler.accel = 0;
                        crawler.drag = 0;
                        crawler.hitSize = 8f;
                        atrax.speed = 0.6f;
                        pulsar.abilities.add(pulsarAbility);
                        bryde.abilities.add(brydeAbility);
                        spiroct.speed = 0.54f;
                        spiroct.weapons.each(w -> {
                            if(Objects.equals(w.name, "spiroct-weapon")){
                                w.bullet.damage = 23;
                            }else w.bullet.damage = 18;
                            if(w.bullet instanceof SapBulletType b){
                                if(Objects.equals(w.name, "spiroct-weapon")){
                                    b.sapStrength = 0.5f;
                                }else b.sapStrength = 0.8f;
                            }
                        });
                        arkyid.speed = 0.62f;
                        arkyid.hitSize = 23f;
                        arkyid.weapons.each(w -> {
                            if(w.bullet instanceof SapBulletType b) {
                                b.sapStrength = 0.85f;
                            }else{
                                w.bullet.pierceBuilding = false;
                                w.bullet.pierceCap = -1;
                            }
                        });
                        toxopid.hitSize = 26f;
                        flare.health = 70;
                        flare.range = 104;
                        horizon.health = 340;
                        horizon.speed = 1.65f;
                        horizon.itemCapacity = 0;
                        zenith.health = 700;
                        zenith.speed = 1.7f;
                        vela.weapons.each(w -> w.bullet.damage = 35f);
                        oct.abilities.each(a -> {
                            if(a instanceof ForceFieldAbility f){
                                f.regen = 4f;
                                f.max = 7000f;
                            }
                        });
                        minke.weapons.each(w -> {
                            if(w.bullet instanceof FlakBulletType){
                                w.bullet.collidesGround = false;
                            }
                        });

                        ui.chatfrag.addMessage("[accent]Flood changes reverted!\nConsider using /sync if playing on a server!\nIf you are the host, ignore this message!");
                        applied = false;
                    }
                }
            }, delay);
        });
    }
}
