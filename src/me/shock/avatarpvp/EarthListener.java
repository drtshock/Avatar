package me.shock.avatarpvp;

import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EarthListener implements Listener {

	public Main plugin;

	public EarthListener(Main instance) {
		this.plugin = instance;
	}
	EarthTask earthTask = new EarthTask();

	public HashMap<String, Long> fortify = new HashMap<String, Long>();
	public HashMap<String, Long> golem = new HashMap<String, Long>();

	// int fcool = plugin.getConfig().getInt("EarthNation.fortify.cooldown");
	// int gcool = plugin.getConfig().getInt("EarthNation.golem.cooldown");
	public HashMap<String, Long> noDamage = new HashMap<String, Long>();

	//int fcool = plugin.getConfig().getInt("EarthNation.fortify.cooldown");
	//int gcool = plugin.getConfig().getInt("EarthNation.golem.cooldown");
	long fortifycool = (long) 30;
	long golemcool = (long) 30;

	String apvp = ChatColor.BLUE + "[" + ChatColor.WHITE + "AvatarPvP"
			+ ChatColor.BLUE + "]" + ChatColor.WHITE + ": ";

	/**
	 * Listen to earth abilities. Fortify - 5 seconds sphere protection. Golem -
	 * summon a rock golem to protect you.
	 */

	@EventHandler
	public void earthInteract(PlayerInteractEvent event) 
	{
		/**
		 * Stuff we need for everything. Set up cooldowns in seconds.
		 */

		Player player = event.getPlayer();
		Action action = event.getAction();
		if (action == Action.LEFT_CLICK_BLOCK) 
		{
			if (player.getItemInHand().getAmount() > 0) 
			{
				ItemStack itemStack = player.getItemInHand();
				ItemMeta meta = itemStack.getItemMeta();
				List<String> lore = meta.getLore();
				if (itemStack.hasItemMeta()) 
				{
					if (itemStack.getItemMeta().hasEnchants())
						return;

					if (lore.contains(ChatColor.GREEN + "Golem")) 
					{
						if (player.hasPermission("avatarpvp.earth.golem")) 
						{
							// Check if the player has used the ability already.
							if (golem.containsKey(player.getName())) 
							{
								long diff = (System.currentTimeMillis() - golem
										.get(player.getName())) / 1000;

								// Used it too recently.
								if (golemcool > diff) 
								{
									player.sendMessage(apvp
											+ "You must wait "
											+ ChatColor.RED
											+ (golemcool - diff)
											+ ChatColor.WHITE
											+ " seconds before using this again.");
								}

								// Can use it again.
								else 
								{
									Block clickedBlock = event.getClickedBlock();
									Location loc = clickedBlock.getLocation().add(0, 1, 0);
									loc.getWorld().spawnEntity(loc, EntityType.IRON_GOLEM);
									golem.remove(player.getName());
									golem.put(player.getName(),System.currentTimeMillis());
									player.sendMessage(apvp + "Spawned an iron golem to protect you.");
								}
							}
						} 
						else 
						{
							player.sendMessage(apvp
									+ "You don't have permission to use this ability.");
						}
					} else
						return;
				} else
					return;

			}
		}

		/**
		 * Check for lore of earth abilities added via commands.
		 */

		if (action == Action.LEFT_CLICK_AIR) 
		{
			/**
			 * Need to handle left click air so that way we get the location of
			 * the player and not the clicked block so they don't spawn the box
			 * on themselves.
			 */
			if (player.getItemInHand().getAmount() > 0) 
			{
				ItemStack itemStack = player.getItemInHand();
				ItemMeta meta = itemStack.getItemMeta();
				List<String> lore = meta.getLore();

				if (!(itemStack.hasItemMeta()))
					return;
				if (itemStack.getItemMeta().hasEnchants())
					return;

				if (lore.contains(ChatColor.GREEN + "Fortify")) 
				{

					if (player.hasPermission("avatarpvp.earth.fortify")) 
					{
						if (fortify.containsKey(player.getName())) 
						{
							long diff = (System.currentTimeMillis() - fortify.get(player.getName())) / 1000;

							// Used it too recently.
							if (fortifycool > diff) 
							{
								player.sendMessage(apvp + "You must wait " + ChatColor.RED + (fortifycool - diff) + ChatColor.WHITE + " seconds before using this again.");
							}
							else
							{
								// Can use it.
							}
						}
						else
						{
							// allow them to use it for the first time.
						}
					}
					player.sendMessage(apvp + "You can't use that ability!");
					return;
				}
			}
		}
	}

					/**
					 * Get the iron golem spawned then make it so it doesn't attack anyone else.
					 */

					@EventHandler
					public void onGolemTarget(EntityTargetLivingEntityEvent event) {
						EntityType type = event.getEntityType();
						if (type == EntityType.IRON_GOLEM) {
							LivingEntity entityTarget = event.getTarget();
							if (entityTarget instanceof Player) {
								Player player = (Player) entityTarget;
								if (player.hasPermission("avatarpvp.earth")) {
									event.setCancelled(true);
								}
							}
							return;
						}
						return;
					}

					/**
					 * Cancel damage if a player has fortify on.
					 * @param event
					 */
					@EventHandler
					public void onDamage(EntityDamageEvent event)
					{
						if(event.getEntity() instanceof Player)
						{
							Player player = (Player) event.getEntity();
							if(noDamage.containsKey(player.getName()))
							{
								event.setCancelled(true);
							}
						}
					}

					public boolean checkBlocked(Player player)
					{
						if(plugin.blocked.contains(player.getName()))
						{
							player.sendMessage(ChatColor.RED + "You are blocked from using any abilities!");
							return true;
						}
						return false;
					}
				}
