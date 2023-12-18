package floodcompat;

import arc.Events;
import arc.util.Log;
import arc.util.Strings;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.gen.Call;
import mindustry.mod.Mod;

public class floodcompat extends Mod{
    boolean flood, applied;
    public floodcompat(){
        Log.info("Flood Compatibility loaded!");

        Vars.netClient.addPacketHandler("flood", (integer) -> {
            if(Strings.canParseInt(integer)){
                Vars.ui.chatfrag.addMessage("[lime]Server check succeeded!\n[accent]Applying flood changes!");
                flood = true;
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
                        Vars.ui.chatfrag.addMessage("[accent]Flood changes reverted!\nConsider using /sync if playing on a server!\nIf you are the host, ignore this message!");
                        applied = false;
                    }
                }
            }, delay);
        });
    }
}