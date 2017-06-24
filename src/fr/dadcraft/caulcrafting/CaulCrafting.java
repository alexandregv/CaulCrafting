package fr.dadcraft.caulcrafting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class CaulCrafting extends JavaPlugin implements Listener {

	 public void onEnable()
	 {
		saveConfig();
	 	getServer().getPluginManager().registerEvents(this, this);
	 	ArrayList<String> testcraft = new ArrayList<String>();
	 	testcraft.add("DIRT");
	 	testcraft.add("LONG_GRASS");
	 	getConfig().set("1.craft", testcraft);
	 	getConfig().set("1.result", "GRASS");
	 	saveConfig();
	 	try {
	        Metrics metrics = new Metrics(this);
	        metrics.start();
	    } catch (IOException e) {
	        // Failed to submit the stats :-(
	    }
	 	
	 }
	 
	 HashMap<Player,ArrayList<Item>> inCaul = new HashMap<Player,ArrayList<Item>>();
	 ArrayList<Player> craftProc = new ArrayList<Player>();
	 HashMap<Player,Location> caulLoc = new HashMap<Player,Location>();
	 HashMap<Player,ArrayList<Item>> inCaulFin = new HashMap<Player,ArrayList<Item>>();
	 
	 @SuppressWarnings("deprecation")
	@Override
	 public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		 if(sender.isOp()){
		 if (cmd.getName().equalsIgnoreCase("caulcrafting")){
		      if (args.length >= 1)
		      {
		        if (args[0].equalsIgnoreCase("create"))
		        {
		        	int nMax = 2;
		        	for(int i = 1; i<nMax;i++){
						 if(getConfig().isSet(String.valueOf(i))){
							 nMax++;
						 } else {
							 sender.sendMessage("§eLe craft n°§b" + i + "§e a été enregistré.");
							 getConfig().createSection(String.valueOf(i));
							 saveConfig();
							 return true;
						 }
					 }
		        }
		        if(args[0].equalsIgnoreCase("add")){
		        	if(args.length >= 2){
		        	if(!args[1].equalsIgnoreCase("new")){
		        		if(Integer.valueOf(args[1]) > 0){
		        			//on ajoute à un craft existant
		        			if(getConfig().isSet(args[1])){
		        				if(args.length >= 3){
		        					String material = args[2].toUpperCase();
		        					if(Material.getMaterial(args[2].toUpperCase()) != null){
		        						ArrayList<String> blockCraft = new ArrayList<String>(getConfig().getStringList(args[1] + ".craft"));
		        						if(!blockCraft.contains(material)){
			        						blockCraft.add(material);
			        						getConfig().set(args[1] + ".craft", blockCraft);
			        						saveConfig();
			        						sender.sendMessage("§aL'objet §b" + material + "§a a été rajouté au craft n°§b" + args[1]);
			        						ArrayList<String> blockCraftFinal = new ArrayList<String>(blockCraft);
			        						blockCraftFinal.remove(material);
			        						StringBuilder sb = new StringBuilder();
			        						for(String mat : blockCraftFinal){
			        							sb.append(mat).append(", ");
			        						}
			        						sender.sendMessage("§8-----");
			        						sender.sendMessage("§eCraft n°" + args[1] + " : §b" + sb.toString() + "§o" + material + " §e->§6 " + getConfig().getString(args[1] + ".result"));
			        						return true;
		        						}
		        						sender.sendMessage("§cERROR. Le Material §b" + material + "§c est déjà présent");
			        					return false;
		        					}
		        					if(material.equalsIgnoreCase("hand")){
		        						if(sender instanceof Player){
		        							Player player = (Player)sender;
		        							if(player.getItemInHand() != null && player.getItemInHand().getType() != Material.AIR){
		        								material = player.getItemInHand().getType().toString();
		        								ArrayList<String> blockCraft = new ArrayList<String>(getConfig().getStringList(args[1] + ".craft"));
				        						if(!blockCraft.contains(material)){
					        						blockCraft.add(material);
					        						getConfig().set(args[1] + ".craft", blockCraft);
					        						saveConfig();
					        						sender.sendMessage("§aL'objet §b" + material + "§a a été rajouté au craft n°§b" + args[1]);
					        						ArrayList<String> blockCraftFinal = new ArrayList<String>(blockCraft);
					        						blockCraftFinal.remove(material);
					        						StringBuilder sb = new StringBuilder();
					        						for(String mat : blockCraftFinal){
					        							sb.append(mat).append(", ");
					        						}
					        						sender.sendMessage("§8-----");
					        						sender.sendMessage("§eCraft n°" + args[1] + " : §b" + sb.toString() + "§o" + material + " §e->§6 " + getConfig().getString(args[1] + ".result"));
					        						return true;
				        						}
				        						sender.sendMessage("§cERROR. Le Material §b" + material + "§c est déjà présent");
					        					return false;
		        							}
		        							sender.sendMessage("§cERROR. Vous devez avoir un objet dans les mains");
				        					return false;
		        						}
		        						sender.sendMessage("§cERROR. Opé-ra-tion imp-ossibl-e par d-es robots !");
			        					return false;
		        					}
		        					sender.sendMessage("§cERROR. Le Material §b" + material + "§c n'existe pas");
		        					return false;
		        				}
		        				sender.sendMessage("§cERROR. Veuillez spécifier un nom de §bMaterial§c ou §bhand§c pour selectionner celui dans votre main");
		        				return false;
		        			}
		        			sender.sendMessage("§cERROR. Le craft n°§b" + args[1] + "§c n'existe pas !" );
		        			return false;
		        		}
		        		sender.sendMessage("§cERROR. /caulcrafting add §4§n"+ args[1] +"§7 [...]");
				        sender.sendMessage("§4'" + args[1] + "'§c est invalide. Il faut un numéro de craft §bsupérieur à 0§c ou §bnew§c pour créer un nouveau craft");
				        return false;
		        	} else
		        	if(args[1].equalsIgnoreCase("new")) {
		        		//nouveau craft
		        		int nMax = 2;
		        		int nbCraft = 0;
			        	for(int i = 1; i<nMax;i++){
							 if(getConfig().isSet(String.valueOf(i))){
								 nMax++;
							 } else {
								 nbCraft = i;
							 }
						 }
	        				if(args.length >= 3){
	        					String material = args[2].toUpperCase();
	        					if(Material.getMaterial(args[2].toUpperCase()) != null){
	        						ArrayList<String> blockCraft = new ArrayList<String>(getConfig().getStringList(nbCraft + ".craft"));
	        						if(!blockCraft.contains(material)){
		        						blockCraft.add(material);
		        						getConfig().set(nbCraft + ".craft", blockCraft);
		        						saveConfig();
		        						sender.sendMessage("§aL'objet §b" + material + "§a a été rajouté au §onouveau §acraft n°§b" + nbCraft);
		        						ArrayList<String> blockCraftFinal = new ArrayList<String>(blockCraft);
		        						blockCraftFinal.remove(material);
		        						StringBuilder sb = new StringBuilder();
		        						for(String mat : blockCraftFinal){
		        							sb.append(mat).append(", ");
		        						}
		        						sender.sendMessage("§8-----");
		        						sender.sendMessage("§eCraft n°" + nbCraft + " : §b" + sb.toString() + "§o" + material + " §e->§6 " + getConfig().getString(nbCraft + ".result"));
		        						return true;
	        						}
	        						sender.sendMessage("§cERROR. Le Material §b" + material + "§c est déjà présent");
		        					return false;
	        					}
	        					if(material.equalsIgnoreCase("hand")){
	        						if(sender instanceof Player){
	        							Player player = (Player)sender;
	        							if(player.getItemInHand() != null && player.getItemInHand().getType() != Material.AIR){
	        								material = player.getItemInHand().getType().toString();
	        								ArrayList<String> blockCraft = new ArrayList<String>(getConfig().getStringList(nbCraft + ".craft"));
			        						if(!blockCraft.contains(material)){
				        						blockCraft.add(material);
				        						getConfig().set(nbCraft + ".craft", blockCraft);
				        						saveConfig();
				        						sender.sendMessage("§aL'objet §b" + material + "§a a été rajouté au §onouveau §acraft n°§b" + nbCraft);
				        						ArrayList<String> blockCraftFinal = new ArrayList<String>(blockCraft);
				        						blockCraftFinal.remove(material);
				        						StringBuilder sb = new StringBuilder();
				        						for(String mat : blockCraftFinal){
				        							sb.append(mat).append(", ");
				        						}
				        						sender.sendMessage("§8-----");
				        						sender.sendMessage("§eCraft n°" + nbCraft + " : §b" + sb.toString() + "§o" + material + " §e->§6 " + getConfig().getString(nbCraft + ".result"));
				        						return true;
			        						}
			        						sender.sendMessage("§cERROR. Le Material §b" + material + "§c est déjà présent");
				        					return false;
	        							}
	        							sender.sendMessage("§cERROR. Vous devez avoir un objet dans les mains");
			        					return false;
	        						}
	        						sender.sendMessage("§cERROR. Opé-ra-tion imp-ossibl-e par d-es robots !");
		        					return false;
	        					}
	        					sender.sendMessage("§cERROR. Le Material §b" + material + "§c n'existe pas");
	        					return false;
	        				}
	        				sender.sendMessage("§cERROR. Veuillez spécifier un nom de §bMaterial§c ou §bhand§c pour selectionner celui dans votre main");
	        				return false;
	        				//end new craft
		        	} else 
		        	sender.sendMessage("§cERROR. /caulcrafting add §4§n"+ args[1] +"§7 [...]");
		        	sender.sendMessage("§4'" + args[1] + "'§c est invalide. Il faut un numéro de craft §bsupérieur à 0§c ou §bnew§c pour créer un nouveau craft");
		        	return false;
		        	}
		        	sender.sendMessage("§e/caulcrafting add §b<craft|new> <material|hand> §8>§7 Ajoute §bmaterial§7 au craft n°§bcraft§7. Ecrivez 'hand' pour selectionner celui dans votre main.");
		        	return false;
		        }
		        if(args[0].equalsIgnoreCase("setresult")){
		        	if(args.length >= 2){
		        		if(Integer.valueOf(args[1]) > 0){
		        			if(getConfig().isSet(args[1])){
		        				if(args.length >= 3){
		        					String material = args[2];
		        					if(Material.getMaterial(material.toUpperCase()) != null){
		        						getConfig().set(args[1] + ".result", material.toUpperCase());
		        						saveConfig();
		        						ArrayList<String> blockCraft = new ArrayList<String>(getConfig().getStringList(args[1] + ".craft"));
		        						StringBuilder sb = new StringBuilder();
		        						for(String mat : blockCraft){
		        							sb.append(mat).append(", ");
		        						}
		        						sender.sendMessage("§aL'objet §b" + material.toUpperCase() + "§a a été rajouté comme résultat au craft n°§b" + args[1]);
		        						sender.sendMessage("§8-----");
		        						sender.sendMessage("§eCraft n°" + args[1] + " : §b" + sb.toString().substring(0, sb.toString().length()-2) + " §e->§6 §o" + getConfig().getString(args[1] + ".result"));
		        						
		        						return true;
		        					
		        					}
		        					if(material.equalsIgnoreCase("hand")){
		        						if(sender instanceof Player){
		        							Player player = (Player)sender;
		        							if(player.getItemInHand() != null && player.getItemInHand().getType() != Material.AIR){
		        								material = player.getItemInHand().getType().toString();
		 
		        								getConfig().set(args[1] + ".result", material.toUpperCase());
				        						saveConfig();
				        						ArrayList<String> blockCraft = new ArrayList<String>(getConfig().getStringList(args[1] + ".craft"));
				        						StringBuilder sb = new StringBuilder();
				        						for(String mat : blockCraft){
				        							sb.append(mat).append(", ");
				        						}
				        						sender.sendMessage("§aL'objet §b" + material.toUpperCase() + "§a a été rajouté comme résultat au craft n°§b" + args[1]);
				        						sender.sendMessage("§8-----");
				        						sender.sendMessage("§eCraft n°" + args[1] + " : §b" + sb.toString().substring(0, sb.toString().length()-2) + " §e->§6 §o" + getConfig().getString(args[1] + ".result"));
				        						return true;
		        							}
		        							sender.sendMessage("§cERROR. Vous devez avoir un objet dans les mains");
				        					return false;
		        						}
		        						sender.sendMessage("§cERROR. Opé-ra-tion imp-ossibl-e par d-es robots !");
			        					return false;
		        					}
		        					sender.sendMessage("§cERROR. Le Material §b" + material + "§c n'existe pas");
		        					return false;
		        				}
		        				sender.sendMessage("§cERROR. Veuillez spécifier un nom de §bMaterial§c ou §bhand§c pour selectionner celui dans votre main");
		        				return false;
		        			}
		        			sender.sendMessage("§cERROR. Le craft n°§b" + args[1] + "§c n'existe pas !" );
		        			return false;
		        		}
		        		sender.sendMessage("§cERROR. /caulcrafting setresult §4§n"+ args[1] +"§7 [...]");
			        	sender.sendMessage("§4'" + args[1] + "'§c est invalide. Il faut un numéro de craft §bsupérieur à 0");
			        	return false;
		        	}
		        	sender.sendMessage("§e/caulcrafting setresult §b<craft> <material|hand> §8>§7 Ajoute le résultat §bmaterial§7 au craft n°§bcraft§7. Ecrivez 'hand' pour selectionner celui dans votre main.");
		        	return false;
		        }
		        if(args[0].equalsIgnoreCase("remove")){
		        	if(args.length >= 2){
		        		if(args.length == 2){
		        			if(Integer.valueOf(args[1]) > 0){
		        				if(getConfig().isSet(args[1])){
		        					sender.sendMessage("§aLe craft n°§b" + args[1] + "§a a été supprimé.");
		        					ArrayList<String> blockCraft = new ArrayList<String>(getConfig().getStringList(args[1] + ".craft"));
	        						StringBuilder sb = new StringBuilder();
	        						for(String mat : blockCraft){
	        							sb.append(mat).append(", ");
	        						}
	        						sender.sendMessage("§8-----");
	        						sender.sendMessage("§c§l(Supprimé) §b§m" + sb.toString().substring(0, sb.toString().length()-2) + "§e ->§6 §m" + getConfig().getString(args[1] + ".result"));
		        					
		        					getConfig().set(args[1], null);
		        					saveConfig();
		        					return true;
		        				}
		        				sender.sendMessage("§cERROR. Le craft n°§b" + args[1] + "§c n'existe pas !" );
			        			return false;
		        			}
				        	sender.sendMessage("§4'" + args[1] + "'§c est invalide. Il faut un numéro de craft §bsupérieur à 0");
				        	return false;
		        		}
		        		if(args.length >= 3){
		        			if(Integer.valueOf(args[1]) > 0){
		        			if(getConfig().isSet(args[1])){
		        			String material = args[2].toUpperCase();
		        			ArrayList<String> blockCraft = new ArrayList<String>(getConfig().getStringList(args[1] + ".craft"));
		        			if(Material.getMaterial(material) != null){
		        				if(blockCraft.contains(material)){
		        					blockCraft.remove(material);
		        					getConfig().set(args[1] + ".craft", blockCraft);
		        					saveConfig();
		        					sender.sendMessage("§aLe material §b" + material + "§a du craft n°§b" + args[1] + "§a a été supprimé");
		        					StringBuilder sb = new StringBuilder();
		        					for(String mat : blockCraft){
	        							sb.append(mat).append(", ");
	        						}
	        						sender.sendMessage("§8-----");
	        						sender.sendMessage("§eCraft n°" + args[1] + " : §b" + sb.toString().substring(0, sb.toString().length()-2) + ", §o§m" + material + "§e ->§6 " + getConfig().getString(args[1] + ".result"));
	        						return true;
		        				}
		        				sender.sendMessage("§cLe craft n°" + args[1] + " ne contient pas §b" + material);
		        				return false;
		        			}
		        			if(material.equalsIgnoreCase("hand")){
		        				if(sender instanceof Player){
        							Player player = (Player)sender;
        							if(player.getItemInHand() != null && player.getItemInHand().getType() != Material.AIR){
        								material = player.getItemInHand().getType().toString();
        								if(blockCraft.contains(material)){
        									blockCraft.remove(material);
        		        					getConfig().set(args[1] + ".craft", blockCraft);
        		        					saveConfig();
        		        					sender.sendMessage("§aLe material §b" + material + "§a du craft n°§b" + args[1] + "§a a été supprimé");
        		        					StringBuilder sb = new StringBuilder();
        		        					for(String mat : blockCraft){
        	        							sb.append(mat).append(", ");
        	        						}
        	        						sender.sendMessage("§8-----");
        	        						sender.sendMessage("§eCraft n°" + args[1] + " : §b" + sb.toString().substring(0, sb.toString().length()-2) + ", §o§m" + material + "§e ->§6 " + getConfig().getString(args[1] + ".result"));
        									
        									return true;
        								}
        								sender.sendMessage("§cLe craft n°" + args[1] + " ne contient pas §b" + material);
        		        				return false;
		        						
        							}
        							sender.sendMessage("§cERROR. Vous devez avoir un objet dans les mains");
		        					return false;
        						}
        						sender.sendMessage("§cERROR. Opé-ra-tion imp-ossibl-e par d-es robots !");
	        					return false;
		        			}
		        			sender.sendMessage("§cERROR. Le Material §b" + material + "§c n'existe pas");
        					return false;
		        			}
		        			sender.sendMessage("§cERROR. Le craft n°§b" + args[1] + "§c n'existe pas !" );
		        			return false;
		        			}
		        			sender.sendMessage("§4'" + args[1] + "'§c est invalide. Il faut un numéro de craft §bsupérieur à 0");
		        			return false;
		        		}
		        		sender.sendMessage("§e/caulcrafting remove §b<craft>§b [material|hand]§8 >§7 Supprime le craft n°§bcraft§c§l définitivement§7 ou seulement §bmaterial§7 du craft n°§bcraft.");
			        	return false;
		        	}
		        	sender.sendMessage("§e/caulcrafting remove §b<craft>§b [material|hand]§8 >§7 Supprime le craft n°§bcraft§c§l définitivement§7 ou seulement §bmaterial§7 du craft n°§bcraft.");
		        	return false;
		        	
		        }
		        if(args[0].equalsIgnoreCase("info")){
		        	if(args.length == 1){
		        		int nMax = 2;
		        		int nbCraft = 0;
			        	for(int i = 1; i<nMax;i++){
							 if(getConfig().isSet(String.valueOf(i))){
								 nMax++;
							 } else {
								 nbCraft = i - 1;
							 }
						 }
			        	sender.sendMessage("§b" + nbCraft + "§a crafts sont enregistrés sur le serveur.");
			        	return true;
		        	}
		        	if(args.length >= 2){
		        		if(Integer.valueOf(args[1]) > 0){
		        			if(getConfig().isSet(args[1])){
		        				ArrayList<String> blockCraft = new ArrayList<String>(getConfig().getStringList(args[1] + ".craft"));
		        				StringBuilder sb = new StringBuilder();
	        					for(String mat : blockCraft){
        							sb.append(mat).append(", ");
        						}
	        					sender.sendMessage("§eCraft n°" + args[1] + " : §b" + sb.toString().substring(0, sb.toString().length()-2) + "§e ->§6 " + getConfig().getString(args[1] + ".result"));
        						return true;
		        			}
		        			sender.sendMessage("§cERROR. Le craft n°§b" + args[1] + "§c n'existe pas !" );
		        			return false;
		        		}
		        		sender.sendMessage("§4'" + args[1] + "'§c est invalide. Il faut un numéro de craft §bsupérieur à 0");
		        		return false;
		        	}
		        	sender.sendMessage("§e/caulcrafting info §b[craft] §8>§7 Affiche le détail du craft n°§bcraft§7 sinon affiche le nombre de craft enregistré.");
		        	return false;
		        }
		        sender.sendMessage("§8-----");
			    sender.sendMessage("§e/caulcrafting §8>§7 Affiche ceci.");
			    sender.sendMessage("§e/caulcrafting add §b<craft|new> <material|hand> §8>§7 Ajoute §bmaterial§7 au craft n°§bcraft§7. Ecrivez 'hand' pour selectionner celui dans votre main.");
			    sender.sendMessage("§e/caulcrafting setresult §b<craft> <material|hand> §8>§7 Ajoute le résultat §bmaterial§7 au craft n°§bcraft§7. Ecrivez 'hand' pour selectionner celui dans votre main.");
			    sender.sendMessage("§e/caulcrafting remove §b<craft>§b [material|hand]§8 >§7 Supprime le craft n°§bcraft§c§l définitivement§7 ou seulement §bmaterial§7 du craft n°§bcraft.");
			    sender.sendMessage("§e/caulcrafting info §b[craft] §8>§7 Affiche le détail du craft n°§bcraft§7 sinon affiche le nombre de craft enregistré.");
			    sender.sendMessage("§b<>§7 : Paramètre obligatoire §8|§b []§7 : Paramètre facultatif");
			    sender.sendMessage("§8-----");
			    return false;
		      }
		      sender.sendMessage("§8-----");
		      sender.sendMessage("§e/caulcrafting §8>§7 Affiche ceci.");
		      sender.sendMessage("§e/caulcrafting add §b<craft|new> <material|hand> §8>§7 Ajoute §bmaterial§7 au craft n°§bcraft§7. Ecrivez 'hand' pour selectionner celui dans votre main.");
		      sender.sendMessage("§e/caulcrafting setresult §b<craft> <material|hand> §8>§7 Ajoute le résultat §bmaterial§7 au craft n°§bcraft§7. Ecrivez 'hand' pour selectionner celui dans votre main.");
		      sender.sendMessage("§e/caulcrafting remove §b<craft>§b [material|hand]§8 >§7 Supprime le craft n°§bcraft§c§l définitivement§7 ou seulement §bmaterial§7 du craft n°§bcraft.");
		      sender.sendMessage("§e/caulcrafting info §b[craft] §8>§7 Affiche le détail du craft n°§bcraft§7 sinon affiche le nombre de craft enregistré.");
		      sender.sendMessage("§b<>§7 : Paramètre obligatoire §8|§b []§7 : Paramètre facultatif");
		      sender.sendMessage("§8-----");
		      return false;
		    }
		 }
		return false;
	 }
	 
	 
	 @EventHandler
	 public void onItemDrop(final PlayerDropItemEvent e)
	 {
		 final Player player = e.getPlayer();
		 final Item item = e.getItemDrop();
		 Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()
		    {
				@SuppressWarnings("deprecation")
				public void run()
				{
					Location itemLoc = item.getLocation();
					if(item.isOnGround()){
						if(itemLoc.getBlock().getType() == Material.CAULDRON){
							Block caul = itemLoc.getBlock();
							if(!caulLoc.containsKey(player))
								caulLoc.put(player, caul.getLocation());
							if(caul.getData() > 0){
								for(int ii = 0; ii<100;ii++){
									
   								 player.getWorld().playEffect(itemLoc, Effect.POTION_SWIRL, 1);
   							 	}
								player.getWorld().playSound(player.getLocation(),Sound.BLOCK_BREWING_STAND_BREW,1, 0);
								if(!inCaul.containsKey(player)){
									ArrayList<Item> itemToAdd = new ArrayList<Item>();
									itemToAdd.add(item);
									inCaul.put(player, itemToAdd);
								} else {
									ArrayList<Item> itemInHM = inCaul.get(player);
									inCaul.remove(player);
									itemInHM.add(item);
									inCaul.put(player, itemInHM);
								}
							}
						}
					}
				}
		    },20);
		 if(!craftProc.contains(player)){
			 craftProc.add(player);
			 Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()
			 {
				@SuppressWarnings("deprecation")
				public void run()
				 {
					 ArrayList<Item> itemsInCaul = inCaul.get(player);
					 int count = 0;
					 clearVar(player);
					 for(Entity entIn : caulLoc.get(player).getChunk().getEntities()){
						 if(entIn.getType() == EntityType.DROPPED_ITEM){
							 entIn = (Item)entIn;
							 if(entIn.getLocation().getBlock().getType() == Material.CAULDRON){
								 count++;
								 if(!inCaulFin.containsKey(player)){
									 ArrayList<Item> itemToAdd = new ArrayList<Item>();
									 itemToAdd.add((Item) entIn);
									 inCaulFin.put(player, itemToAdd);
								 } else {
									 ArrayList<Item> itemInHM = inCaulFin.get(player);
									 inCaulFin.remove(player);
									 itemInHM.add((Item) entIn);
									 inCaulFin.put(player, itemInHM);
								 }
			    				 if(count == itemsInCaul.size()) {
			    					 ArrayList<Integer> nubrOK = new ArrayList<Integer>();
			    					 int nMax = 2;
			    					 for(int nubr = 1;nubr <= nMax;nubr++){
			    						 if(getConfig().isSet(String.valueOf(nubr))){
			    							 nMax++;
			    							 int siL = getConfig().getList(nubr + ".craft").size();
			    							 if(siL == count)
			    								 nubrOK.add(nubr);
			    						 }
			    					 }
			    					 for(int i = 0;i <= nubrOK.size();i++){
			    						 ArrayList<String> blockCraft = new ArrayList<String>(getConfig().getStringList(nubrOK.get(i) + ".craft"));
			    						 ArrayList<Material> matConfig = new ArrayList<Material>();
			    						 ArrayList<Material> matInCaul = new ArrayList<Material>();
			    						 for(String matStr : blockCraft){
			    							 Material mat = Material.getMaterial(matStr);
			    							 matConfig.add(mat);
			    						 }
			    						 for(Item itm : inCaulFin.get(player)){
			    							 Material mat = itm.getItemStack().getType();
			    							 matInCaul.add(mat);
			    						 }
			    						 if(matConfig.containsAll(matInCaul)){
			    							 //bon craft
			    							 for(Item ItemsIn : inCaulFin.get(player)){
			    								 ItemsIn.remove();
			    							 }
			    							 ItemStack resultItem = new ItemStack(Material.getMaterial(getConfig().getString(nubrOK.get(i) + ".result")),1);
			    							 player.getWorld().dropItem(caulLoc.get(player).add(0, 2, 0), resultItem);
			    							 for(int ii = 0; ii<100;ii++){
			    								 player.getWorld().playEffect(caulLoc.get(player), Effect.FLYING_GLYPH, 1);
			    							 }
			    							 player.getWorld().playSound(player.getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1, 0);
			    							 Block caul = caulLoc.get(player).add(0, -2, 0).getBlock();
			    							 byte caulData = caul.getData();
			    							 caul.setData((byte) (caulData-1));
			    						 }
			    					 }
			    					 
			    				 } else {
			    					 clearVar(player);
			    				 }
			    				 
							 }
						 }
					 }
				 }
			 },100);
		} else {
		}
		 
	 }
	 
	 public void clearVar(final Player player){
		 Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable()
		 {
			 public void run()
			 {
				 inCaul.remove(player);
				 craftProc.remove(player);
				 caulLoc.remove(player);
				 inCaulFin.remove(player);
			 }
		 },10);
	 }
	 
	}
	

