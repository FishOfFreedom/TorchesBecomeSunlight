package com.freefish.torchesbecomesunlight.client.render.gui.partnercommand;

import com.freefish.rosmontislib.RosmontisLib;
import com.freefish.rosmontislib.gui.factory.UIFactory;
import com.freefish.rosmontislib.gui.modular.IUIHolder;
import com.freefish.rosmontislib.gui.modular.ModularUI;
import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class PartnerCommandMenuFactory extends UIFactory<PartnerCommandMenuFactory> implements IUIHolder {
	public Part[] parts;
	public boolean isOpen = false;

	public static final PartnerCommandMenuFactory INSTANCE = new PartnerCommandMenuFactory();

	public void tick(){
		if(isOpen){
			if(parts!=null){
				for(Part part:parts){
					part.tick();
				}
			}
		}
	}

	public void open(int commandCounter){
		if(isOpen) return;

		parts = new Part[commandCounter];
		for(int i =0;i<commandCounter;i++){
			parts[i] = new Part();
		}
		isOpen = true;
	}

	public void close(){
		if(!isOpen) return;

		isOpen = false;
	}

	private PartnerCommandMenuFactory(){
		super(new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"partner_command"));
	}

	@Override
	protected ModularUI createUITemplate(PartnerCommandMenuFactory holder, Player entityPlayer) {
		return createUI(entityPlayer);
	}

	@Override
	protected PartnerCommandMenuFactory readHolderFromSyncData(FriendlyByteBuf syncData) {
		return this;
	}

	@Override
	protected void writeHolderToSyncData(FriendlyByteBuf syncData, PartnerCommandMenuFactory holder) {

	}

	@Override
	public ModularUI createUI(Player entityPlayer) {
		return new ModularUI(this, entityPlayer)
				.widget(new PartnerCommandMenu());
	}

	@Override
	public boolean isInvalid() {
		return false;
	}

	@Override
	public boolean isRemote() {
		return RosmontisLib.isRemote();
	}

	@Override
	public void markAsDirty() {

	}

	private class Animation{
		public float value,endValue;
		public long time;
		public int dura;

		public Animation(int dura,float value,float endValue) {
			time = System.currentTimeMillis();
			this.dura = dura;
			this.value = value;
			this.endValue = endValue;
		}

		public float getTimeScale(){
			float v = (System.currentTimeMillis() - time) / 1000f;
			if(v>=dura) return endValue;
			return Mth.lerp(MathUtils.easeOutQuart(v/dura),value,endValue);
		}
	}

	@Getter@Setter
	public class Part{
		public boolean isMouseOver;
		public long tickCount = System.currentTimeMillis();
		public float r;
		public Animation animation;

		public Part() {
		}

		public void setMouseOver(boolean mouseOver) {
			if(isMouseOver != mouseOver){
				isMouseOver = mouseOver;
				if(mouseOver){
					setAnimation(new Animation(1,r,0.2f));
				}else {
					setAnimation(new Animation(1,r,0));
				}
			}
		}

		public void tick(){
		}

		public float getCommandRadiu() {
			float tickCount1 = ((System.currentTimeMillis()-tickCount)/ 1000f);
			if(animation!=null){
				r = animation.getTimeScale();
			}
			if(tickCount1<1){
				return (1+r)*(MathUtils.easeOutQuart(tickCount1));
			}

			return 1+r;
		}
	}
}
