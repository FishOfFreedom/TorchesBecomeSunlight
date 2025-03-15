package com.freefish.torchesbecomesunlight.client.shader.shader;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.*;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ReloadShaderManager {

    private static Map<ResourceLocation, Resource> reloadResources = new HashMap<>();
    private static boolean foreReloadAll = false;
    private static final ResourceProvider reloadShaderResource = (res) -> Optional.of(reloadResources.get(res));

    private static void recordResource(ResourceLocation resourceLocation, Resource resource) {
        reloadResources.put(resourceLocation, resource);
    }

    private static void recordCopyResource(ResourceLocation resourceLocation, Resource resource) {
        try(var res = resource.open()) {
            final byte[] data = res.readAllBytes();
            final IoSupplier<InputStream> ioSupplier = () -> new ByteArrayInputStream(data);
            Resource copyResource = new Resource(resource.source(), ioSupplier);
            recordResource(resourceLocation, copyResource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public static ShaderInstance backupNewShaderInstance(ResourceProvider resourceProvider, String shaderName, VertexFormat vertexFormat) throws IOException {
        if (foreReloadAll) {
            return new ShaderInstance(reloadShaderResource, shaderName, vertexFormat);
        }
        ShaderInstance shaderInstance = new ShaderInstance(resourceProvider, shaderName, vertexFormat);
        ResourceLocation shaderResourceLocation = new ResourceLocation(shaderName);
        recordProgramResource(resourceProvider, shaderResourceLocation.getNamespace(), shaderResourceLocation.getPath());
        return shaderInstance;
    }

    public static ShaderInstance backupNewShaderInstance(ResourceProvider resourceProvider, ResourceLocation shaderLocation, VertexFormat vertexFormat) throws IOException {
        return backupNewShaderInstance(resourceProvider, shaderLocation.toString(), vertexFormat);
    }

    private static void recordProgramResource(ResourceProvider resourceProvider, String nameSpace, String shaderName) throws IOException {
        ResourceLocation programResourceLocation = new ResourceLocation(nameSpace, "shaders/core/" + shaderName + ".json");
        Resource programResource = resourceProvider.getResource(programResourceLocation).orElseThrow();
        ReloadShaderManager.recordCopyResource(programResourceLocation, programResource);
        JsonObject jsonObject = GsonHelper.parse(new InputStreamReader(resourceProvider.getResource(programResourceLocation).orElseThrow().open(), StandardCharsets.UTF_8));
        ResourceLocation vertex = new ResourceLocation(GsonHelper.getAsString(jsonObject, "vertex"));
        ResourceLocation vertexResourceLocation = new ResourceLocation(vertex.getNamespace(), "shaders/core/" + vertex.getPath() + ".vsh");
        ReloadShaderManager.recordCopyResource(vertexResourceLocation, resourceProvider.getResource(vertexResourceLocation).orElseThrow());
        ResourceLocation fragment = new ResourceLocation(GsonHelper.getAsString(jsonObject, "fragment"));
        ResourceLocation fragmentResourceLocation = new ResourceLocation(fragment.getNamespace(), "shaders/core/" + fragment.getPath() + ".fsh");
        ReloadShaderManager.recordCopyResource(fragmentResourceLocation, resourceProvider.getResource(fragmentResourceLocation).orElseThrow());
    }
}