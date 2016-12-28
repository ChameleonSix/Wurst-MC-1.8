/*
 * Copyright � 2014 - 2016 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.features.mods;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.features.Feature;
import tk.wurst_client.settings.SliderSetting;
import tk.wurst_client.settings.SliderSetting.ValueDisplay;

@Mod.Info(
	description = "Automatically eats soup if your health is below the set value.",
	name = "AutoSoup",
	tags = "auto soup",
	help = "Mods/AutoSoup")
@Mod.Bypasses
public class AutoSoupMod extends Mod implements UpdateListener
{
	public float health = 20F;
	
	@Override
	public void initSettings()
	{
		settings.add(
			new SliderSetting("Health", health, 2, 20, 1, ValueDisplay.INTEGER)
			{
				@Override
				public void update()
				{
					health = (float)getValue();
				}
			});
	}
	
	@Override
	public Feature[] getSeeAlso()
	{
		return new Feature[]{wurst.mods.autoEatMod};
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		// check if no container is open
		if(mc.currentScreen instanceof GuiContainer
			&& !(mc.currentScreen instanceof GuiInventory))
			return;
		
		EntityPlayerSP player = mc.player;
		
		// check if health is low
		if(player.getHealth() >= health)
			return;
		
		// find soup
		int soupInInventory = findSoup(9, 36);
		int soupInHotbar = findSoup(36, 45);
		
		// check if any soup was found
		if(soupInInventory == -1 && soupInHotbar == -1)
			return;
		
		Container inventoryContainer = player.inventoryContainer;
		PlayerControllerMP playerController = mc.playerController;
		
		// sort empty bowls
		for(int i = 9; i < 45; i++)
		{
			ItemStack stack = inventoryContainer.getSlot(i).getStack();
			if(stack != null && stack.getItem() == Items.bowl)
			{
				playerController.windowClick(0, i, 0, 0, player);
				playerController.windowClick(0, 18, 0, 0, player);
			}
		}
		
		if(soupInHotbar != -1)
		{
			// eat soup in hotbar
			int oldSlot = player.inventory.currentItem;
			NetHandlerPlayClient sendQueue = player.connection;
			
			sendQueue
				.sendPacket(new C09PacketHeldItemChange(soupInHotbar - 36));
			playerController.updateController();
			sendQueue.sendPacket(
				new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), -1,
					inventoryContainer.getSlot(soupInHotbar).getStack(), 0.0F,
					0.0F, 0.0F));
			sendQueue.sendPacket(new C09PacketHeldItemChange(oldSlot));
		}else
			// move soup in inventory to hotbar
			playerController.windowClick(0, soupInInventory, 0, 1, player);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
	
	private int findSoup(int startSlot, int endSlot)
	{
		for(int i = startSlot; i < endSlot; i++)
		{
			ItemStack stack =
				mc.player.inventoryContainer.getSlot(i).getStack();
			if(stack != null && stack.getItem() == Items.mushroom_stew)
				return i;
		}
		return -1;
	}
}
