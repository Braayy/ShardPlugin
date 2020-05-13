package braayy.shard;

import braayy.shard.inventory.ShardShopInventoryHolder;
import braayy.shard.service.impl.MessageService;
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

        MessageService messageService = this.plugin.getService(MessageService.class);

        if (args[0].equalsIgnoreCase("balance")) {
            handleBalance(sender, args);
        } else if (args[0].equalsIgnoreCase("shop")) {
            if (!(sender instanceof Player)) {
                messageService.sendMessage(sender, "command.only-players");

                return true;
            }

            if (!sender.hasPermission("shard.shop")) {
                messageService.sendMessage(sender, "command.no-permission");

                return true;
            }

            ShardShopInventoryService shardShopInventoryService = this.plugin.getService(ShardShopInventoryService.class);
            shardShopInventoryService.open((Player) sender);
        } else if (args[0].equalsIgnoreCase("pay")) {
            if (!(sender instanceof Player)) {
                messageService.sendMessage(sender, "command.only-players");

                return true;
            }

            if (!sender.hasPermission("shard.pay")) {
                messageService.sendMessage(sender, "command.no-permission");

                return true;
            }

            handlePay((Player) sender, label, Arrays.copyOfRange(args, 1, args.length));
        } else if (args[0].equalsIgnoreCase("take")) {
            if (!sender.hasPermission("shard.admin")) {
                messageService.sendMessage(sender, "command.no-permission");

                return true;
            }

            handleTake(sender, label, Arrays.copyOfRange(args, 1, args.length));
        } else if (args[0].equalsIgnoreCase("give")) {
            if (!sender.hasPermission("shard.admin")) {
                messageService.sendMessage(sender, "command.no-permission");

                return true;
            }

            handleGive(sender, label, Arrays.copyOfRange(args, 1, args.length));
        } else if (args[0].equalsIgnoreCase("set")) {
            if (!sender.hasPermission("shard.admin")) {
                messageService.sendMessage(sender, "command.no-permission");

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
        MessageService messageService = this.plugin.getService(MessageService.class);


        if (args.length < 2) {
            if (!(sender instanceof Player)) {
                messageService.sendMessage(sender, "command.only-players");

                return;
            }

            if (!sender.hasPermission("shard.balance")) {
                messageService.sendMessage(sender, "command.no-permission");

                return;
            }

            int shards = shardService.getShards((Player) sender);
            messageService.sendMessage(sender, "command.balance", "balance", shards);

            return;
        }

        if (!sender.hasPermission("shard.balance.others")) {
            messageService.sendMessage(sender, "command.no-permission");

            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            messageService.sendMessage(sender, "command.offline", "player", args[1]);

            return;
        }

        int shards = shardService.getShards(target);
        messageService.sendMessage(sender, "command.balance.others", "player", target.getName(), "balance", shards);
    }

    private void handlePay(Player player, String label, String[] args) {
        ShardService shardService = this.plugin.getService(ShardService.class);
        MessageService messageService = this.plugin.getService(MessageService.class);

        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "/" + label + " pay <player> <amount> - Pays shards to a player");

            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            messageService.sendMessage(player, "command.offline", "player", args[0]);

            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (Exception ex) {
            messageService.sendMessage(player, "command.not-number", "number", args[1]);

            return;
        }

        if (!shardService.hasShards(player, amount)) {
            messageService.sendMessage(player, "command.pay.not-enough");

            return;
        }

        shardService.removeShards(player, amount);
        shardService.addShards(target, amount);

        messageService.sendMessage(player, "command.pay.paid", "player", target.getName(), "amount", amount);
        messageService.sendMessage(target, "command.pay.received", "player", player.getName(), "amount", amount);
    }

    private void handleTake(CommandSender sender, String label, String[] args) {
        ShardService shardService = this.plugin.getService(ShardService.class);
        MessageService messageService = this.plugin.getService(MessageService.class);

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "/" + label + " take <player> <amount> - Takes shards from a player");

            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            messageService.sendMessage(sender, "command.offline", "player", args[0]);

            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (Exception ex) {
            messageService.sendMessage(sender, "command.not-number", "number", args[1]);

            return;
        }

        shardService.removeShards(target, amount);

        messageService.sendMessage(sender, "command.take.taken", "player", target.getName(), "amount", amount);
    }

    private void handleGive(CommandSender sender, String label, String[] args) {
        ShardService shardService = this.plugin.getService(ShardService.class);
        MessageService messageService = this.plugin.getService(MessageService.class);

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "/" + label + " give <player> <amount> - Gives shards to a player");

            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            messageService.sendMessage(sender, "command.offline", "player", args[0]);

            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (Exception ex) {
            messageService.sendMessage(sender, "command.not-number", "number", args[1]);

            return;
        }

        shardService.addShards(target, amount);

        messageService.sendMessage(sender, "command.give.gave", "player", target.getName(), "amount", amount);
    }

    private void handleSet(CommandSender sender, String label, String[] args) {
        ShardService shardService = this.plugin.getService(ShardService.class);
        MessageService messageService = this.plugin.getService(MessageService.class);

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "/" + label + " set <player> <amount> - Sets the shards of a player");

            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            messageService.sendMessage(sender, "command.offline", "player", args[0]);

            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (Exception ex) {
            messageService.sendMessage(sender, "command.not-number", "number", args[1]);

            return;
        }

        shardService.setShards(target, amount);

        messageService.sendMessage(sender, "command.set.setted", "player", target.getName(), "amount", amount);
    }
}