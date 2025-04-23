package net.soren.custom;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.*;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.soren.custom.sound.ModSounds;
import net.soren.custom.config.ModConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;

public class twitchGriefMain implements ModInitializer {
	public static final String MOD_ID = "twitch-griefs";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static final int COOLDOWN_SECONDS = 60;
	public static TwitchClient twitchClient;

	private final Map<String, Instant> cooldowns = new HashMap<>();
	private final Map<String, Text> userColors = new HashMap<>();
	private Set<String> cooldownExemptUsers = Set.of("admin");

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");

		ModConfigs.registerConfigs();

		cooldownExemptUsers = Set.of(ModConfigs.MINECRAFT_USERNAME);

		OAuth2Credential credential = new OAuth2Credential("twitch", "oauth:" + ModConfigs.TWITCH_OAUTH_KEY);

		twitchClient = TwitchClientBuilder.builder()
				.withEnableChat(true)
				.withChatAccount(credential)
				.build();

		twitchClient.getChat().getEventManager().onEvent(ChannelMessageEvent.class, this::onMessage);

		ModSounds.initialize();
	}

	private void onMessage(ChannelMessageEvent event) {
		String message = event.getMessage().toLowerCase();
		String username = event.getUser().getName();

		LOGGER.info("Received chat message: {}", message);

		switch (message) {

			//spawn commands
			case "!creeper" -> spawnMob(event, username, world -> new CreeperEntity(EntityType.CREEPER, world), "Creeper");
			case "!zombie" -> spawnMob(event, username, world -> new ZombieEntity(EntityType.ZOMBIE, world), "Zombie");
			case "!skeleton" -> spawnMob(event, username, world -> {
				SkeletonEntity skeleton = new SkeletonEntity(EntityType.SKELETON, world);
				skeleton.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
				return skeleton;
			}, "Skeleton");
			case "!spider" -> spawnMob(event, username, world -> new SpiderEntity(EntityType.SPIDER, world), "Spider");
			case "!jockey" -> spawnChickenJockey(event, username);

			//player tamper commands


			//
		}
	}

	private void spawnMob(ChannelMessageEvent event, String username, Function<ServerWorld, Entity> entitySupplier, String mobName) {
		MinecraftServer server = getServer();
		if (server == null) return;

		ServerWorld world = server.getOverworld();
		PlayerEntity player = getClosestPlayer(world);
		if (player == null) return;

		server.execute(() -> {
			if (!checkCooldown(username, player)) return;

			Entity entity = entitySupplier.apply(world);
			if (entity != null) {
				entity.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), 0, 0);
				world.spawnEntity(entity);
				sendMessage(player, username + " sent a " + mobName + "!", username);
			}
		});
	}

	private void spawnChickenJockey(ChannelMessageEvent event, String username) {
		MinecraftServer server = getServer();
		if (server == null) return;

		ServerWorld world = server.getOverworld();
		PlayerEntity player = getClosestPlayer(world);
		if (player == null) return;

		server.execute(() -> {
			if (!checkCooldown(username, player)) return;

			ChickenEntity chicken = new ChickenEntity(EntityType.CHICKEN, world);
			ZombieEntity babyZombie = new ZombieEntity(EntityType.ZOMBIE, world);
			babyZombie.setBaby(true);
			babyZombie.setPersistent();

			chicken.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), 0, 0);
			world.spawnEntity(chicken);

			babyZombie.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), 0, 0);
			world.spawnEntity(babyZombie);

			babyZombie.startRiding(chicken);

			// Play the sound after spawning the entities
			if (!world.isClient()) {
				world.playSound(null, player.getBlockPos(), ModSounds.CHICKEN_JOCKEY_SPAWN, SoundCategory.PLAYERS, 1f, 1f);
				// If you have a custom sound registered:
				//world.playSound(null, player.getBlockPos(), ModSounds.CHICKEN_JOCKEY_SPAWN, SoundCategory.PLAYERS, 1f, 1f);
			}


			sendMessage(player, username + " sent a Chicken Jockey!", username);
		});
	}


	private boolean checkCooldown(String username, PlayerEntity player) {
		Instant now = Instant.now();
		Instant lastUsed = cooldowns.get(username);
		boolean exempt = cooldownExemptUsers.contains(username.toLowerCase());

		if (!exempt && lastUsed != null && now.isBefore(lastUsed.plusSeconds(COOLDOWN_SECONDS))) {
			long secondsLeft = lastUsed.plusSeconds(COOLDOWN_SECONDS).getEpochSecond() - now.getEpochSecond();
			sendMessage(player, username + " is on cooldown for " + secondsLeft + " more seconds!", username);
			LOGGER.info("Cooldown active for {} - {}s left", username, secondsLeft);
			return false;
		}

		if (!exempt) cooldowns.put(username, now);
		return true;
	}

	private void sendMessage(PlayerEntity player, String message, String username) {
		Text coloredName = userColors.computeIfAbsent(username, name -> {
			int r = (int) (Math.random() * 200 + 30);
			int g = (int) (Math.random() * 200 + 30);
			int b = (int) (Math.random() * 200 + 30);
			return Text.literal(name).styled(style -> style.withColor((r << 16) | (g << 8) | b));
		});
		Text text = Text.literal(message).styled(style -> style.withColor(coloredName.getStyle().getColor()));
		player.sendMessage(text, false);
	}

	private MinecraftServer getServer() {
		return net.minecraft.client.MinecraftClient.getInstance().getServer();
	}

	private PlayerEntity getClosestPlayer(ServerWorld world) {
		Vec3d pos = Vec3d.ofCenter(world.getSpawnPos());
		return world.getClosestPlayer(pos.x, pos.y, pos.z, Double.MAX_VALUE, false);
	}
}