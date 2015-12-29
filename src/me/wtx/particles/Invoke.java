package me.wtx.particles;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class Invoke extends JavaPlugin {

	public HashMap<UUID, Effect> stored = new HashMap<UUID, Effect>();

	private static Plugin instance = null;

	private String cmdLbl = "particles";
	private String[] effects = { "flames", "hearts", "happyvillager" };

	private final String prefix = ChatColor.GRAY + "[" + ChatColor.GOLD + "Particles" + ChatColor.GRAY + "] "
			+ ChatColor.AQUA;

	public String[] commands = { "/particles set <effect> [Set a particle effect on yourself]",
			"/particles list [Display all available effects]", "/particles remove [Remove the current effect]" };

	public static Plugin getInstance() {
		return instance;
	}

	public void sendMessageWithPrefix(String msg, Player player) {
		player.sendMessage(prefix + msg);
	}

	@Override
	public void onEnable() {
		if (instance == null) {
			instance = this;
		}

	}

	public void setPlayerParticles(final Effect effect, final Player player) {
		@SuppressWarnings({ "deprecation", "unused" })
		int i = Bukkit.getScheduler().scheduleAsyncRepeatingTask(instance, new Runnable() {

			@Override
			public void run() {

				if (stored.containsKey(player.getUniqueId())) {
					for (Player players : Bukkit.getOnlinePlayers()) {

						Location location;

						if (effect == Effect.MOBSPAWNER_FLAMES) {
							players.playEffect(player.getLocation(), stored.get(player.getUniqueId()), 10);
						} else if (effect == Effect.HEART) {
							location = Bukkit.getPlayer(player.getUniqueId()).getEyeLocation()
									.add(new Location(player.getWorld(), 0, 0.1, 0));
							players.playEffect(location, stored.get(player.getUniqueId()), 5);
						} else if (effect == Effect.HAPPY_VILLAGER) {
							location = Bukkit.getPlayer(player.getUniqueId()).getEyeLocation()
									.add(new Location(player.getWorld(), 0, 0.5, 0));
							players.playEffect(location, stored.get(player.getUniqueId()), 10);
						}

					}
				} else {
					return;
				}

			}

		}, 1, 25);
	}

	public boolean onCommand(CommandSender sender, Command command, String cmd, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage("Console implementation not included");
		}

		Player player = (Player) sender;

		if (command.getName().equalsIgnoreCase(cmdLbl)) {

			if (args.length == 0) {
				for (String s : commands) {
					sendMessageWithPrefix(s, player);
				}
			} else if (args.length > 0 && args.length < 3) {
				String param = args[0];

				switch (param) {

				case "list":

					this.sendMessageWithPrefix("---------------------------------", player);

					for (String s : this.effects) {
						this.sendMessageWithPrefix(s, player);
					}

					this.sendMessageWithPrefix("---------------------------------", player);

					break;

				case "remove":

					if (this.stored.containsKey(player.getUniqueId())) {

						this.stored.remove(player.getUniqueId());
						this.sendMessageWithPrefix("You have removed your particles", player);
						return true;

					} else {
						this.sendMessageWithPrefix("You do not have particles!", player);
						return true;
					}

				case "set":

					try {

						if (stored.containsKey(player.getUniqueId())) {
							this.sendMessageWithPrefix("You already have particles!", player);
							return true;
						}

						Effect effect = null;

						switch (args[1]) {

						case "flames":
							effect = Effect.MOBSPAWNER_FLAMES;
							this.setPlayerParticles(effect, player);
							break;
						case "hearts":
							effect = Effect.HEART;
							this.setPlayerParticles(effect, player);
							break;
						case "happyvillager":
							effect = Effect.HAPPY_VILLAGER;
							this.setPlayerParticles(effect, player);
							break;
						default:
							effect = null;
						}

						if (effect != null) {
							this.sendMessageWithPrefix("You have set your particles to " + args[1], player);
							this.stored.put(player.getUniqueId(), effect);
							return true;
						} else {
							this.sendMessageWithPrefix("Particles not found", player);
							return true;
						}

					} catch (Exception ex) {
						ex.printStackTrace();
						this.sendMessageWithPrefix(
								"Something happened. Try again and make sure you are choosing a valid particle.",
								player);
					}

					break;

				}

			}

		}

		return false;
	}

}
