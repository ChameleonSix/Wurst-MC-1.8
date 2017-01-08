/*
 * Copyright � 2014 - 2017 | Wurst-Imperium | All rights reserved.
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package tk.wurst_client.features.mods;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.RayTraceResult;
import net.minecraft.world.Explosion;
import tk.wurst_client.events.listeners.UpdateListener;
import tk.wurst_client.settings.SliderSetting;
import tk.wurst_client.settings.SliderSetting.ValueDisplay;
import tk.wurst_client.utils.BlockUtils;
import tk.wurst_client.utils.ChatUtils;

@Mod.Info(
	description = "Breaks blocks around you like an explosion.\n"
		+ "This can be a lot faster than Nuker if the server\n"
		+ "doesn't have NoCheat+. It works best with fast tools\n"
		+ "and weak blocks.\n" + "Note that this is not an actual explosion.",
	name = "Kaboom",
	help = "Mods/Kaboom")
@Mod.Bypasses
public class KaboomMod extends Mod implements UpdateListener
{
	private int range = 6;
	public int power = 128;
	
	@Override
	public void initSettings()
	{
		settings.add(
			new SliderSetting("Power", power, 32, 512, 32, ValueDisplay.INTEGER)
			{
				@Override
				public void update()
				{
					power = (int)getValue();
				}
			});
	}
	
	@Override
	public void onEnable()
	{
		wurst.events.add(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
		if(mc.player.capabilities.isCreativeMode)
		{
			ChatUtils.error("Surivival mode only.");
			setEnabled(false);
			return;
		}
		new Thread("Kaboom")
		{
			@Override
			public void run()
			{
				for(int y = range; y >= -range; y--)
				{
					new Explosion(mc.world, mc.player, mc.player.posX,
						mc.player.posY, mc.player.posZ, 6F, false, true)
							.doExplosionB(true);
					for(int x = range; x >= -range - 1; x--)
						for(int z = range; z >= -range; z--)
						{
							int posX = (int)(Math.floor(mc.player.posX) + x);
							int posY = (int)(Math.floor(mc.player.posY) + y);
							int posZ = (int)(Math.floor(mc.player.posZ) + z);
							if(x == 0 && y == -1 && z == 0)
								continue;
							BlockPos pos = new BlockPos(posX, posY, posZ);
							Block block =
								mc.world.getBlockState(pos).getBlock();
							float xDiff = (float)(mc.player.posX - posX);
							float yDiff = (float)(mc.player.posY - posY);
							float zDiff = (float)(mc.player.posZ - posZ);
							float currentDistance = BlockUtils
								.getBlockDistance(xDiff, yDiff, zDiff);
							RayTraceResult fakeObjectMouseOver =
								mc.objectMouseOver;
							fakeObjectMouseOver
								.setBlockPos(new BlockPos(posX, posY, posZ));
							if(Block.getIdFromBlock(block) != 0 && posY >= 0
								&& currentDistance <= range)
							{
								if(!mc.player.onGround)
									continue;
								EnumFacing side = fakeObjectMouseOver.sideHit;
								BlockUtils.faceBlockPacket(pos);
								mc.player.connection
									.sendPacket(new C0APacketAnimation());
								mc.player.connection
									.sendPacket(new C07PacketPlayerDigging(
										Action.START_DESTROY_BLOCK, pos, side));
								for(int i = 0; i < power; i++)
									mc.player.connection
										.sendPacket(new C07PacketPlayerDigging(
											Action.STOP_DESTROY_BLOCK, pos,
											side));
								block.onBlockDestroyedByPlayer(
									Minecraft.getMinecraft().world, pos,
									mc.world.getBlockState(pos));
							}
						}
				}
			}
		}.start();
		setEnabled(false);
	}
	
	@Override
	public void onDisable()
	{
		wurst.events.remove(UpdateListener.class, this);
	}
}
