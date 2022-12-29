package com.yurisuika.compost.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.yurisuika.compost.mixin.command.argument.ItemStackArgumentAccessor;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.ArrayUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;

import static com.yurisuika.compost.Compost.*;
import static net.minecraft.server.command.CommandManager.*;

public class CompostCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        dispatcher.register(literal("compost")
                .then(literal("config")
                        .requires(source -> source.hasPermissionLevel(4))
                        .then(literal("reload")
                                .executes(context -> {
                                    loadConfig();

                                    context.getSource().sendFeedback(new TranslatableText("commands.compost.config.reload"), true);
                                    return 1;
                                })
                        )
                        .then(literal("reset")
                                .executes(context -> {
                                    int length = config.items.length;
                                    for (int i = 0; i < length; i++) {
                                        removeGroup(0);
                                    }

                                    addGroup("minecraft:dirt", 1.0D, 1,1);
                                    addGroup("minecraft:bone_meal", 1.0D, 1,1);
                                    setShuffle(true);
                                    context.getSource().sendFeedback(new TranslatableText("commands.compost.config.reset"), true);
                                    return 1;
                                })
                        )
                )
                .then(literal("shuffle")
                        .requires(source -> source.hasPermissionLevel(4))
                        .then(literal("query")
                                .executes(context -> {
                                    context.getSource().sendFeedback(new TranslatableText("commands.compost.shuffle.query", config.shuffle), false);
                                    return 1;
                                })
                        )
                        .then(literal("set")
                                .then(argument("value", BoolArgumentType.bool())
                                        .executes(context -> {
                                            boolean value = BoolArgumentType.getBool(context, "value");

                                            setShuffle(value);
                                            context.getSource().sendFeedback(new TranslatableText("commands.compost.shuffle.set", value), true);
                                            return 1;
                                        })
                                )
                        )
                )
                .then(literal("groups")
                        .requires(source -> source.hasPermissionLevel(4))
                        .then(literal("query")
                                .executes(context -> {
                                    context.getSource().sendFeedback(new TranslatableText("commands.compost.groups.query", config.items.length), false);
                                    return 1;
                                })
                        )
                        .then(literal("get")
                                .executes(context -> {
                                    for (Group group : config.items) {
                                        int index;
                                        Item item;
                                        if (group.item.contains("{")) {
                                            index = group.item.indexOf("{");
                                            item = Registry.ITEM.get(new Identifier(group.item.substring(0, index)));
                                        } else {
                                            index = 0;
                                            item = Registry.ITEM.get(new Identifier(group.item));
                                        }
                                        ItemStack itemStack = new ItemStack(item);
                                        if (group.item.contains("{")) {
                                            NbtCompound nbt;
                                            try {
                                                nbt = StringNbtReader.parse(group.item.substring(index));
                                                itemStack.setTag(nbt);
                                            } catch (CommandSyntaxException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        double chance = Math.max(0.0D, Math.min(group.chance, 1.0D));
                                        int maxCount = itemStack.getMaxCount();
                                        int max = Math.min(group.max, maxCount);
                                        int min = Math.min(Math.min(group.min, maxCount), max);

                                        context.getSource().sendFeedback(new TranslatableText("commands.compost.groups.get", ArrayUtils.indexOf(config.items, group) + 1, itemStack.toHoverableText(), new DecimalFormat("0.###############").format(BigDecimal.valueOf(chance).multiply(BigDecimal.valueOf(100))), min, max), false);
                                    }
                                    return 1;
                                })
                        )
                        .then(literal("reverse")
                                .executes(context -> {
                                    reverseGroups();

                                    context.getSource().sendFeedback(new TranslatableText("commands.compost.groups.reverse"), true);
                                    return 1;
                                })
                        )
                        .then(literal("shuffle")
                                .executes(context -> {
                                    shuffleGroups(new Random());

                                    context.getSource().sendFeedback(new TranslatableText("commands.compost.groups.shuffle"), true);
                                    return 1;
                                })
                        )
                )
                .then(literal("group")
                        .requires(source -> source.hasPermissionLevel(4))
                        .then(literal("get")
                                .then(argument("group", IntegerArgumentType.integer(1))
                                        .executes(context -> {
                                            int range = IntegerArgumentType.getInteger(context, "group");
                                            if (range > config.items.length) {
                                                context.getSource().sendError(new TranslatableText("commands.compost.group.failed", range, config.items.length));
                                                return 0;
                                            } else {
                                                Group group = getGroup(range - 1);
                                                int index;
                                                Item item;
                                                if (group.item.contains("{")) {
                                                    index = group.item.indexOf("{");
                                                    item = Registry.ITEM.get(new Identifier(group.item.substring(0, index)));
                                                } else {
                                                    index = 0;
                                                    item = Registry.ITEM.get(new Identifier(group.item));
                                                }
                                                ItemStack itemStack = new ItemStack(item);
                                                if (group.item.contains("{")) {
                                                    NbtCompound nbt;
                                                    try {
                                                        nbt = StringNbtReader.parse(group.item.substring(index));
                                                        itemStack.setTag(nbt);
                                                    } catch (CommandSyntaxException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                double chance = Math.max(0.0D, Math.min(group.chance, 1.0D));
                                                int maxCount = itemStack.getMaxCount();
                                                int max = Math.min(group.max, maxCount);
                                                int min = Math.min(Math.min(group.min, maxCount), max);

                                                context.getSource().sendFeedback(new TranslatableText("commands.compost.groups.get", ArrayUtils.indexOf(config.items, group) + 1, itemStack.toHoverableText(), new DecimalFormat("0.###############").format(BigDecimal.valueOf(chance).multiply(BigDecimal.valueOf(100))), min, max), false);
                                                return 1;
                                            }
                                        })
                                )
                        )
                        .then(literal("remove")
                                .then(argument("group", IntegerArgumentType.integer(1))
                                        .executes(context -> {
                                            int range = IntegerArgumentType.getInteger(context, "group");
                                            if (range > config.items.length) {
                                                context.getSource().sendError(new TranslatableText("commands.compost.group.failed", range, config.items.length));
                                                return 0;
                                            } else {
                                                int number = IntegerArgumentType.getInteger(context, "group") - 1;
                                                Group group = getGroup(number);
                                                int index;
                                                Item item;
                                                if (group.item.contains("{")) {
                                                    index = group.item.indexOf("{");
                                                    item = Registry.ITEM.get(new Identifier(group.item.substring(0, index)));
                                                } else {
                                                    index = 0;
                                                    item = Registry.ITEM.get(new Identifier(group.item));
                                                }
                                                ItemStack itemStack = new ItemStack(item);
                                                if (group.item.contains("{")) {
                                                    NbtCompound nbt;
                                                    try {
                                                        nbt = StringNbtReader.parse(group.item.substring(index));
                                                        itemStack.setTag(nbt);
                                                    } catch (CommandSyntaxException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                double chance = Math.max(0.0D, Math.min(group.chance, 1.0D));
                                                int maxCount = itemStack.getMaxCount();
                                                int max = Math.min(group.max, maxCount);
                                                int min = Math.min(Math.min(group.min, maxCount), max);

                                                removeGroup(number);
                                                context.getSource().sendFeedback(new TranslatableText("commands.compost.group.remove", itemStack.toHoverableText(), new DecimalFormat("0.###############").format(BigDecimal.valueOf(chance).multiply(BigDecimal.valueOf(100))), min, max), true);
                                                return 1;
                                            }
                                        })
                                )
                        )
                        .then(literal("add")
                                .then(argument("item", ItemStackArgumentType.itemStack())
                                        .then(argument("chance", DoubleArgumentType.doubleArg(0.0D, 1.0D))
                                                .then(argument("min", IntegerArgumentType.integer(0, 64))
                                                        .then(argument("max", IntegerArgumentType.integer(1, 64))
                                                                .executes(context -> {
                                                                    ItemStackArgument arg = ItemStackArgumentType.getItemStackArgument(context, "item");
                                                                    ItemStack itemStack = arg.createStack(1, false);
                                                                    StringBuilder stringBuilder = new StringBuilder(arg.getItem().getTranslationKey().replace("item.", "").replace(".", ":"));
                                                                    if (((ItemStackArgumentAccessor) arg).getNbt() != null) {
                                                                        stringBuilder.append(((ItemStackArgumentAccessor) arg).getNbt());
                                                                    }
                                                                    String item = stringBuilder.toString();
                                                                    double chance = Math.max(0.0D, Math.min(DoubleArgumentType.getDouble(context, "chance"), 1.0D));
                                                                    int maxCount = itemStack.getMaxCount();
                                                                    int max = Math.min(IntegerArgumentType.getInteger(context, "max"), maxCount);
                                                                    int min = Math.min(Math.min(IntegerArgumentType.getInteger(context, "min"), maxCount), max);

                                                                    addGroup(item, chance, min, max);
                                                                    context.getSource().sendFeedback(new TranslatableText("commands.compost.group.add", itemStack.toHoverableText(), new DecimalFormat("0.###############").format(BigDecimal.valueOf(chance).multiply(BigDecimal.valueOf(100))), min, max), true);
                                                                    return 1;
                                                                })
                                                        )
                                                )
                                        )
                                )
                        )
                        .then(literal("insert")
                                .then(argument("group", IntegerArgumentType.integer(1))
                                        .then(argument("item", ItemStackArgumentType.itemStack())
                                                .then(argument("chance", DoubleArgumentType.doubleArg(0.0D, 1.0D))
                                                        .then(argument("min", IntegerArgumentType.integer(0, 64))
                                                                .then(argument("max", IntegerArgumentType.integer(1, 64))
                                                                        .executes(context -> {
                                                                            int range = IntegerArgumentType.getInteger(context, "group");
                                                                            if (range > config.items.length) {
                                                                                context.getSource().sendError(new TranslatableText("commands.compost.group.failed", range, config.items.length));
                                                                                return 0;
                                                                            } else {
                                                                                int number = IntegerArgumentType.getInteger(context, "group") - 1;
                                                                                ItemStackArgument arg = ItemStackArgumentType.getItemStackArgument(context, "item");
                                                                                ItemStack itemStack = arg.createStack(1, false);
                                                                                StringBuilder stringBuilder = new StringBuilder(arg.getItem().getTranslationKey().replace("item.", "").replace(".", ":"));
                                                                                if (((ItemStackArgumentAccessor) arg).getNbt() != null) {
                                                                                    stringBuilder.append(((ItemStackArgumentAccessor) arg).getNbt());
                                                                                }
                                                                                String item = stringBuilder.toString();
                                                                                double chance = Math.max(0.0D, Math.min(DoubleArgumentType.getDouble(context, "chance"), 1.0D));
                                                                                int maxCount = itemStack.getMaxCount();
                                                                                int max = Math.min(IntegerArgumentType.getInteger(context, "max"), maxCount);
                                                                                int min = Math.min(Math.min(IntegerArgumentType.getInteger(context, "min"), maxCount), max);

                                                                                insertGroup(number, item, chance, min, max);
                                                                                context.getSource().sendFeedback(new TranslatableText("commands.compost.group.insert", itemStack.toHoverableText(), new DecimalFormat("0.###############").format(BigDecimal.valueOf(chance).multiply(BigDecimal.valueOf(100))), min, max), true);
                                                                                return 1;
                                                                            }
                                                                        })
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                        .then(literal("set")
                                .then(argument("group", IntegerArgumentType.integer(1))
                                        .then(argument("item", ItemStackArgumentType.itemStack())
                                                .then(argument("chance", DoubleArgumentType.doubleArg(0.0D, 1.0D))
                                                        .then(argument("min", IntegerArgumentType.integer(0, 64))
                                                                .then(argument("max", IntegerArgumentType.integer(1, 64))
                                                                        .executes(context -> {
                                                                            int range = IntegerArgumentType.getInteger(context, "group");
                                                                            if (range > config.items.length) {
                                                                                context.getSource().sendError(new TranslatableText("commands.compost.group.failed", range, config.items.length));
                                                                                return 0;
                                                                            } else {
                                                                                int number = IntegerArgumentType.getInteger(context, "group") - 1;
                                                                                ItemStackArgument arg = ItemStackArgumentType.getItemStackArgument(context, "item");
                                                                                ItemStack itemStack = arg.createStack(1, false);
                                                                                StringBuilder stringBuilder = new StringBuilder(arg.getItem().getTranslationKey().replace("item.", "").replace(".", ":"));
                                                                                if (((ItemStackArgumentAccessor) arg).getNbt() != null) {
                                                                                    stringBuilder.append(((ItemStackArgumentAccessor) arg).getNbt());
                                                                                }
                                                                                String item = stringBuilder.toString();
                                                                                double chance = Math.max(0.0D, Math.min(DoubleArgumentType.getDouble(context, "chance"), 1.0D));
                                                                                int maxCount = itemStack.getMaxCount();
                                                                                int max = Math.min(IntegerArgumentType.getInteger(context, "max"), maxCount);
                                                                                int min = Math.min(Math.min(IntegerArgumentType.getInteger(context, "min"), maxCount), max);

                                                                                setGroup(number, item, chance, min, max);
                                                                                context.getSource().sendFeedback(new TranslatableText("commands.compost.group.set", itemStack.toHoverableText(), new DecimalFormat("0.###############").format(BigDecimal.valueOf(chance).multiply(BigDecimal.valueOf(100))), min, max), true);
                                                                                return 1;
                                                                            }
                                                                        })
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );
    }

}