package com.freefish.torchesbecomesunlight.server.story;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.story.data.Dialogue;
import com.freefish.torchesbecomesunlight.server.story.data.canoption.CanOption;
import com.freefish.torchesbecomesunlight.server.story.data.canoption.CanOptionDeserializer;
import com.freefish.torchesbecomesunlight.server.story.data.choose.Choose;
import com.freefish.torchesbecomesunlight.server.story.data.choose.ChooseDeserializer;
import com.freefish.torchesbecomesunlight.server.story.data.generatext.Generatext;
import com.freefish.torchesbecomesunlight.server.story.data.generatext.GeneratextDeserializer;
import com.freefish.torchesbecomesunlight.server.story.data.trigger.Trigger;
import com.freefish.torchesbecomesunlight.server.story.data.trigger.TriggerDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.Level;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public enum DialogueManager {
    INSTANCE;

    public final Gson DIALOGUE_GSON = new GsonBuilder()
            .registerTypeAdapter(Trigger.class, new TriggerDeserializer())
            .registerTypeAdapter(CanOption.class, new CanOptionDeserializer())
            .registerTypeAdapter(Choose.class, new ChooseDeserializer())
            .registerTypeAdapter(Generatext.class, new GeneratextDeserializer())
            .create();

    public Dialogue readDialogueFromData(ResourceLocation resourceLocation, Level level){
        if(level.getServer() != null){
            return this.readDialogueFromData(resourceLocation, level.getServer().getResourceManager());
        }
        else {
            return null;
        }
    }

    public Dialogue readDialogueFromData(ResourceLocation resourceLocation, ResourceManager resourceManager){
        Optional<Resource> re = resourceManager.getResource(resourceLocation);
        if(re.isPresent()){
            Resource resource = re.get();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.open(), StandardCharsets.UTF_8))) {
                return DIALOGUE_GSON.fromJson(reader, Dialogue.class);
            } catch (IOException | com.google.gson.JsonSyntaxException e) {
                TorchesBecomeSunlight.LOGGER.error("Failure to read or parse dialog JSON file ({}): {}", resource.sourcePackId(), e.getMessage());
                try (BufferedReader contentReader = new BufferedReader(new InputStreamReader(resource.open(), StandardCharsets.UTF_8))) {
                    StringBuilder jsonContent = new StringBuilder();
                    String line;
                    while ((line = contentReader.readLine()) != null) {
                        jsonContent.append(line);
                    }
                    TorchesBecomeSunlight.LOGGER.debug("JSON: {}", jsonContent.toString());
                } catch (IOException ioe) {
                    TorchesBecomeSunlight.LOGGER.error("Unable to read problematic JSON content for debugging. {}", ioe.getMessage());
                }
                return null;
            }
        }else {
            TorchesBecomeSunlight.LOGGER.error("Failure to find file ({})", resourceLocation.toString());
            return null;
        }
    }
}
