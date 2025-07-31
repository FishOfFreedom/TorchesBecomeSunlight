package com.freefish.torchesbecomesunlight.server.block.blockentity.sync;

import com.freefish.rosmontislib.RosmontisLib;
import com.freefish.torchesbecomesunlight.server.block.blockentity.AutoSynTagBlockEntity;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import org.apache.commons.lang3.ObjectUtils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SynchedBlockEntityData {
   private static final Object2IntMap<Class<? extends AutoSynTagBlockEntity>> ENTITY_ID_POOL = new Object2IntOpenHashMap<>();
   private final AutoSynTagBlockEntity entity;
   private final Int2ObjectMap<DataItem<?>> itemsById = new Int2ObjectOpenHashMap<>();
   private final ReadWriteLock lock = new ReentrantReadWriteLock();
   private boolean isDirty;

   public SynchedBlockEntityData(AutoSynTagBlockEntity pEntity) {
      this.entity = pEntity;
   }

   public static <T> EntityDataAccessor<T> defineId(Class<? extends AutoSynTagBlockEntity> pClazz, EntityDataSerializer<T> pSerializer) {
      if (true) {
         try {
            Class<?> oclass = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());
            if (!oclass.equals(pClazz)) {
               if (RosmontisLib.LOGGER.isDebugEnabled()) RosmontisLib.LOGGER.warn("defineId called for: {} from {}", pClazz, oclass, new RuntimeException());
               else RosmontisLib.LOGGER.warn("defineId called for: {} from {}", pClazz, oclass);
            }
         } catch (ClassNotFoundException classnotfoundexception) {
         }
      }

      int j;
      if (ENTITY_ID_POOL.containsKey(pClazz)) {
         j = ENTITY_ID_POOL.getInt(pClazz) + 1;
      } else {
         int i = 0;
         Class<?> oclass1 = pClazz;

         while(oclass1 != AutoSynTagBlockEntity.class) {
            oclass1 = oclass1.getSuperclass();
            if (ENTITY_ID_POOL.containsKey(oclass1)) {
               i = ENTITY_ID_POOL.getInt(oclass1) + 1;
               break;
            }
         }

         j = i;
      }

      if (j > 254) {
         throw new IllegalArgumentException("Data value id is too big with " + j + "! (Max is 254)");
      } else {
         ENTITY_ID_POOL.put(pClazz, j);
         return pSerializer.createAccessor(j);
      }
   }

   public <T> void define(EntityDataAccessor<T> pKey, T pValue) {
      int i = pKey.getId();
      if (i > 254) {
         throw new IllegalArgumentException("Data value id is too big with " + i + "! (Max is 254)");
      } else if (this.itemsById.containsKey(i)) {
         throw new IllegalArgumentException("Duplicate id value for " + i + "!");
      } else if (EntityDataSerializers.getSerializedId(pKey.getSerializer()) < 0) {
         throw new IllegalArgumentException("Unregistered serializer " + pKey.getSerializer() + " for " + i + "!");
      } else {
         this.createDataItem(pKey, pValue);
      }
   }

   private <T> void createDataItem(EntityDataAccessor<T> pKey, T pValue) {
      DataItem<T> dataitem = new DataItem<>(pKey, pValue);
      this.lock.writeLock().lock();
      this.itemsById.put(pKey.getId(), dataitem);
      this.lock.writeLock().unlock();
   }

   public <T> boolean hasItem(EntityDataAccessor<T> pKey) {
      return this.itemsById.containsKey(pKey.getId());
   }

   private <T> DataItem<T> getItem(EntityDataAccessor<T> pKey) {
      this.lock.readLock().lock();

      DataItem<T> dataitem;
      try {
         dataitem = (DataItem<T>)this.itemsById.get(pKey.getId());
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.forThrowable(throwable, "Getting synched entity data");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Synched entity data");
         crashreportcategory.setDetail("Data ID", pKey);
         throw new ReportedException(crashreport);
      } finally {
         this.lock.readLock().unlock();
      }

      return dataitem;
   }

   public <T> T get(EntityDataAccessor<T> pKey) {
      return this.getItem(pKey).getValue();
   }

   public <T> void set(EntityDataAccessor<T> pKey, T pValue) {
      this.set(pKey, pValue, false);
   }

   public <T> void set(EntityDataAccessor<T> pKey, T pValue, boolean pForce) {
      DataItem<T> dataitem = this.getItem(pKey);
      if (pForce || ObjectUtils.notEqual(pValue, dataitem.getValue())) {
         dataitem.setValue(pValue);
         this.entity.onSyncedDataUpdated(pKey);
         dataitem.setDirty(true);
         this.isDirty = true;
      }

   }

   public boolean isDirty() {
      return this.isDirty;
   }

   @Nullable
   public List<DataValue<?>> packDirty() {
      List<DataValue<?>> list = null;
      if (this.isDirty) {
         this.lock.readLock().lock();

         for(DataItem<?> dataitem : this.itemsById.values()) {
            if (dataitem.isDirty()) {
               dataitem.setDirty(false);
               if (list == null) {
                  list = new ArrayList<>();
               }

               list.add(dataitem.value());
            }
         }

         this.lock.readLock().unlock();
      }

      this.isDirty = false;
      return list;
   }

   @Nullable
   public List<DataValue<?>> getNonDefaultValues() {
      List<DataValue<?>> list = null;
      this.lock.readLock().lock();

      for(DataItem<?> dataitem : this.itemsById.values()) {
         if (!dataitem.isSetToDefault()) {
            if (list == null) {
               list = new ArrayList<>();
            }

            list.add(dataitem.value());
         }
      }

      this.lock.readLock().unlock();
      return list;
   }

   public void assignValues(List<DataValue<?>> pEntries) {
      this.lock.writeLock().lock();

      try {
         for(DataValue<?> datavalue : pEntries) {
            DataItem<?> dataitem = this.itemsById.get(datavalue.id);
            if (dataitem != null) {
               this.assignValue(dataitem, datavalue);
               this.entity.onSyncedDataUpdated(dataitem.getAccessor());
            }
         }
      } finally {
         this.lock.writeLock().unlock();
      }

      this.entity.onSyncedDataUpdated(pEntries);
   }

   private <T> void assignValue(DataItem<T> pTarget, DataValue<?> pEntry) {
      if (!Objects.equals(pEntry.serializer(), pTarget.accessor.getSerializer())) {
         throw new IllegalStateException(String.format(Locale.ROOT, "Invalid entity data item type for field %d on entity %s: old=%s(%s), new=%s(%s)", pTarget.accessor.getId(), this.entity, pTarget.value, pTarget.value.getClass(), pEntry.value, pEntry.value.getClass()));
      } else {
         pTarget.setValue((T) pEntry.value);
      }
   }

   public boolean isEmpty() {
      return this.itemsById.isEmpty();
   }

   public static class DataItem<T> {
      @Getter
      final EntityDataAccessor<T> accessor;
      @Getter
      @Setter
      T value;
      private final T initialValue;
      @Setter
      @Getter
      private boolean dirty;

      public DataItem(EntityDataAccessor<T> pAccessor, T pValue) {
         this.accessor = pAccessor;
         this.initialValue = pValue;
         this.value = pValue;
      }

       public boolean isSetToDefault() {
         return this.initialValue.equals(this.value);
      }

      public DataValue<T> value() {
         return DataValue.create(this.accessor, this.value);
      }
   }

   public static record DataValue<T>(int id, EntityDataSerializer<T> serializer, T value) {
      public static <T> DataValue<T> create(EntityDataAccessor<T> pDataAccessor, T pValue) {
         EntityDataSerializer<T> entitydataserializer = pDataAccessor.getSerializer();
         return new DataValue<>(pDataAccessor.getId(), entitydataserializer, entitydataserializer.copy(pValue));
      }

      public void write(FriendlyByteBuf pBuffer) {
         int i = EntityDataSerializers.getSerializedId(this.serializer);
         if (i < 0) {
            throw new EncoderException("Unknown serializer type " + this.serializer);
         } else {
            pBuffer.writeByte(this.id);
            pBuffer.writeVarInt(i);
            this.serializer.write(pBuffer, this.value);
         }
      }

      public static DataValue<?> read(FriendlyByteBuf pBuffer, int pId) {
         int i = pBuffer.readVarInt();
         EntityDataSerializer<?> entitydataserializer = EntityDataSerializers.getSerializer(i);
         if (entitydataserializer == null) {
            throw new DecoderException("Unknown serializer type " + i);
         } else {
            return read(pBuffer, pId, entitydataserializer);
         }
      }

      private static <T> DataValue<T> read(FriendlyByteBuf pBuffer, int pId, EntityDataSerializer<T> pSerializer) {
         return new DataValue<>(pId, pSerializer, pSerializer.read(pBuffer));
      }
   }
}