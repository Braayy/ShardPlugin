package braayy.shard;

import braayy.shard.inventory.ShardShopInventoryHolder;
import braayy.shard.service.impl.ShardService;
import braayy.shard.service.impl.ShardShopInventoryService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class ShardCommand implements CommandExecutor {

    private final ShardPlugin plugin;

    public ShardCommand(ShardPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            handleHelp(sender, label, command.getDescription());

            return true;
        }

        if (args[0].equalsIgnoreCase("balance")) {
            handleBalance(sender, args);
        } else if (args[0].equalsIgnoreCase("shop")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can do that");

                return true;
            }

            if (!sender.hasPermission("shard.shop")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");

                return true;
            }

            ShardShopInventoryService shardShopInventoryService = this.plugin.getService(ShardShopInventoryService.class);
            shardShopInventoryService.open((Player) sender);
        } else if (args[0].equalsIgnoreCase("pay")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can do that");

                return true;
            }

            if (!sender.hasPermission("shard.pay")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");

                return true;
            }

            handlePay((Player) sender, label, Arrays.copyOfRange(args, 1, args.length));
        } else if (args[0].equalsIgnoreCase("take")) {
            if (!sender.hasPermission("shard.admin")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");

                return true;
            }

            handleTake(sender, label, Arrays.copyOfRange(args, 1, args.length));
        } else if (args[0].equalsIgnoreCase("give")) {
            if (!sender.hasPermission("shard.admin")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");

                return true;
            }

            handleGive(sender, label, Arrays.copyOfRange(args, 1, args.length));
        } else if (args[0].equalsIgnoreCase("set")) {
            if (!sender.hasPermission("shard.admin")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");

                return true;
            }

            handleSet(sender, label, Arrays.copyOfRange(args, 1, args.length));
        } else {
            handleHelp(sender, label, command.getDescription());
        }

        return true;
    }

    public void register() {
        this.plugin.getCommand("shard").setExecutor(this);
    }

    private void handleHelp(CommandSender sender, String label, String description) {
        sender.sendMessage(ChatColor.RED + description);
        if (sender.hasPermission("shard.balance")) {
            sender.sendMessage(ChatColor.RED + "/" + label + " balance - Shows your balance");
        }
        if (sender.hasPermission("shard.balance.others")) {
            sender.sendMessage(ChatColor.RED + "/" + label + " balance <player> - Shows the balance of another player");
        }
        if (sender.hasPermission("shard.shop")) {
            sender.sendMessage(ChatColor.RED + "/" + label + " shop - Opens the Shard Shop GUI");
        }
        if (sender.hasPermission("shard.pay")) {
            sender.sendMessage(ChatColor.RED + "/" + label + " pay <player> <amount> - Pays shards to a player");
        }
        if (sender.hasPermission("shard.admin")) {
            sender.sendMessage(ChatColor.RED + "/" + label + " take <player> <amount> - Takes shards from a player");
            sender.sendMessage(ChatColor.RED + "/" + label + " give <player> <amount> - Gives shards to a player");
            sender.sendMessage(ChatColor.RED + "/" + label + " set <player> <amount> - Sets the shards of a player");
        }
    }

    private void handleBalance(CommandSender sender, String[] args) {
        ShardService shardService = this.plugin.getService(ShardService.class);

        if (args.length < 2) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can do that");

                return;
            }

            if (!sender.hasPermission("shard.balance")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");

                return;
            }

            int shards = shardService.getShards((Player) sender);
            sender.sendMessage(ChatColor.GREEN + "Your balance is " + shards);

            return;
        }

        if (!sender.hasPermission("shard.balance.others")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");

            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + args[1] + " is offline!");

            return;
        }

        int shards = shardService.getShards(target);
        sender.sendMessage(ChatColor.GREEN + target.getName() + "'s balance is " + shards);
    }

    private void handlePay(Player player, String label, String[] args) {
        ShardService shardService = this.plugin.getService(ShardService.class);

        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "/" + label + " pay <player> <amount> - Pays shards to a player");

            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + args[0] + " is offline!");

            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (Exception ex) {
            player.sendMessage(ChatColor.RED + args[1] + " is not a number");

            return;
        }

        if (!shardService.hasShards(player, amount)) {
            player.sendMessage(ChatColor.RED + "You don't have enough shards to pay");

            return;
        }

        shardService.removeShards(player, amount);
        shardService.addShards(target, amount);

        player.sendMessage(ChatColor.GREEN + "You have paid " + amount + " shards to " + target.getName());
        target.sendMessage(ChatColor.GREEN + "You have received " + amount + " shards from " + player.getName());
    }

    private void handleTake(CommandSender sender, String label, String[] args) {
        ShardService shardService = this.plugin.getService(ShardService.class);

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "/" + label + " take <player> <amount> - Takes shards from a player");

            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + args[0] + " is offline!");

            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (Exception ex) {
            sender.sendMessage(ChatColor.RED + args[1] + " is not a number");

            return;
        }

        shardService.removeShards(target, amount);

        sender.sendMessage(ChatColor.GREEN + "You have take " + amount + " shards from " + target.getName());
        target.sendMessage(ChatColor.RED + "Your balance have been taken " + amount + " shards");
    }

    private void handleGive(CommandSender sender, String label, String[] args) {
        ShardService shardService = this.plugin.getService(ShardService.class);

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "/" + label + " give <player> <amount> - Gives shards to a player");

            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + args[0] + " is offline!");

            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (Exception ex) {
            sender.sendMessage(ChatColor.RED + args[1] + " is not a number");

            return;
        }

        shardService.addShards(target, amount);

        sender.sendMessage(ChatColor.GREEN + "You have gave " + amount + " shards to " + target.getName());
        target.sendMessage(ChatColor.GREEN + "You have received " + amount + " shards");
    }

    private void handleSet(CommandSender sender, String label, String[] args) {
        ShardService shardService = this.plugin.getService(ShardService.class);

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "/" + label + " set <player> <amount> - Sets the shards of a player");

            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + args[0] + " is offline!");

            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (Exception ex) {
            sender.sendMessage(ChatColor.RED + args[1] + " is not a number");

            return;
        }

        shardService.setShards(target, amount);

        sender.sendMessage(ChatColor.GREEN + "You have set " + target.getName() + "'s balance to " + amount + " shards");
        target.sendMessage(ChatColor.GREEN + "Your balance has been set to  " + amount + " shards");
    }
}