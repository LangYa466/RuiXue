/*
 * LiquidBounce Base
 * God SkidBounce
 * Conding
 */
package net.ccbluex.liquidbounce.features.module

import me.kid.Bl1nk
import me.kid.GrimCivBreak
import me.nelly.ESP
import me.rainyfall.GroundTelly
import me.tiangong.*
import me.rainyfall.Scaffold2
import me.rainyfall.Scaffold3
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.KeyEvent
import net.ccbluex.liquidbounce.event.Listenable
import net.ccbluex.liquidbounce.features.module.modules.combat.*
import net.ccbluex.liquidbounce.features.module.modules.exploit.*
import net.ccbluex.liquidbounce.features.module.modules.misc.*
import net.ccbluex.liquidbounce.features.module.modules.movement.*
import net.ccbluex.liquidbounce.features.module.modules.player.*
import net.ccbluex.liquidbounce.features.module.modules.render.*
import net.ccbluex.liquidbounce.features.module.modules.hyt.*
import net.ccbluex.liquidbounce.features.module.modules.movement.Parkour
import net.ccbluex.liquidbounce.features.module.modules.world.*
import net.ccbluex.liquidbounce.features.module.modules.world.Timer
import net.ccbluex.liquidbounce.utils.ClientUtils
import java.util.*


class ModuleManager : Listenable {

    val modules = TreeSet<Module> { module1, module2 -> module1.name.compareTo(module2.name) }
    val modules2 = TreeSet<Module> { module1, module2 -> module1.chinesename.compareTo(module2.chinesename) }
    private val moduleClassMap = hashMapOf<Class<*>, Module>()

    init {
        LiquidBounce.eventManager.registerListener(this)
    }

    /**
     * Register all modules
     */
    fun registerModules() {
        ClientUtils.getLogger().info("[ModuleManager] Loading modules...")

        registerModules(
            Crosshair::class.java,
            GrimFullVelocity::class.java,
            Ambience::class.java,
            Logo::class.java,
            FollowTargetHud::class.java,
            StrafeFix::class.java,
            AutoArmor::class.java,
            CustomFont::class.java,
            AutoBow::class.java,
            AutoLeave::class.java,
            AutoPot::class.java,
            AutoSoup::class.java,
            AutoWeapon::class.java,
            BowAimbot::class.java,
            Criticals::class.java,
            KillAura::class.java,
            Trigger::class.java,
            Fly::class.java,
            ClickGUI::class.java,
            HighJump::class.java,
            InventoryMove::class.java,
            LiquidWalk::class.java,
            SafeWalk::class.java,
            WallClimb::class.java,
            Strafe::class.java,
            Sprint::class.java,
            Teams::class.java,
            NoRotateSet::class.java,
            AntiBot::class.java,
            ChestStealer::class.java,
            Scaffold::class.java,
            CivBreak::class.java,
            Tower::class.java,
            FastBreak::class.java,
            FastPlace::class.java,
            me.nelly.ESP::class.java,
            Velocity::class.java,
            Speed::class.java,
            Tracers::class.java,
            NameTags::class.java,
            FastUse::class.java,
            Teleport::class.java,
            Fullbright::class.java,
            ItemESP::class.java,
            StorageESP::class.java,
            Projectiles::class.java,
            NoClip::class.java,
            Nuker::class.java,
            PingSpoof::class.java,
            FastClimb::class.java,
            Step::class.java,
            AutoRespawn::class.java,
            AutoTool::class.java,
            NoWeb::class.java,
            Spammer::class.java,
            IceSpeed::class.java,
            Zoot::class.java,
            Regen::class.java,
            NoFall::class.java,
            Blink::class.java,
            NameProtect::class.java,
            NoHurtCam::class.java,
            Ghost::class.java,
            MidClick::class.java,
            XRay::class.java,
            Timer::class.java,
            Sneak::class.java,
            SkinDerp::class.java,
            Paralyze::class.java,
            GhostHand::class.java,
            AutoWalk::class.java,
            AutoBreak::class.java,
            FreeCam::class.java,
            Aimbot::class.java,
            Eagle::class.java,
            HitBox::class.java,
            AntiCactus::class.java,
            Plugins::class.java,
            AntiHunger::class.java,
            ConsoleSpammer::class.java,
            LongJump::class.java,
            Parkour::class.java,
            LadderJump::class.java,
            FastBow::class.java,
            MultiActions::class.java,
            AirJump::class.java,
            AutoClicker::class.java,
            NoBob::class.java,
            BlockOverlay::class.java,
            NoFriends::class.java,
            BlockESP::class.java,
            Chams::class.java,
            Clip::class.java,
            Phase::class.java,
            ServerCrasher::class.java,
            NoFOV::class.java,
            FastStairs::class.java,
            SwingAnimation::class.java,
            Derp::class.java,
            ReverseStep::class.java,
            TNTBlock::class.java,
            InventoryCleaner::class.java,
            TrueSight::class.java,
            LiquidChat::class.java,
            AntiBlind::class.java,
            NoSwing::class.java,
            BedGodMode::class.java,
            BugUp::class.java,
            Breadcrumbs::class.java,
            AbortBreaking::class.java,
            PotionSaver::class.java,
            CameraClip::class.java,
            WaterSpeed::class.java,
            Ignite::class.java,
            SlimeJump::class.java,
            MoreCarry::class.java,
            NoPitchLimit::class.java,
            Kick::class.java,
            Liquids::class.java,
            AtAllProvider::class.java,
            AirLadder::class.java,
            GodMode::class.java,
            TeleportHit::class.java,
            ForceUnicodeChat::class.java,
            ItemTeleport::class.java,
            BufferSpeed::class.java,
            SuperKnockback::class.java,
            ProphuntESP::class.java,
            AutoFish::class.java,
            Damage::class.java,
            Freeze::class.java,
            KeepContainer::class.java,
            VehicleOneHit::class.java,
            Reach::class.java,
            Rotations::class.java,
            NoJumpDelay::class.java,
            BlockWalk::class.java,
            AntiAFK::class.java,
            PerfectHorseJump::class.java,
            HUD::class.java,
            TNTESP::class.java,
            ComponentOnHover::class.java,
            KeepAlive::class.java,
            ResourcePackSpoof::class.java,
            NoSlowBreak::class.java,
            PortalMenu::class.java,
            EnchantEffect::class.java,
            SpeedMine::class.java,
            OldHitting::class.java,
            HytDisabler::class.java,
            PlayerSize::class.java,
         //   HytGetName::class.java,
            AutoL::class.java,
            AutoGG::class.java,
            AutoLeos::class.java,
            Gapple::class.java,
            NoLagBack::class.java,
            MemoryFix::class.java,
            WolrdAnim::class.java,
            FdpScaffold::class.java,
            ItemPhysics::class.java,
            Title::class.java,
            JumpCircle::class.java,
            CustomColor::class.java,
            PotionRender::class.java,
            HelpKillRange::class.java,
            AutoReach::class.java,
            Parkour::class.java,
            HytAntiVoid::class.java,
            BanChecker::class.java,
            TGVelocity::class.java,
            HytBlink::class.java,
            CustomHUD::class.java,
            CustomColor::class.java,
            FakeFPS::class.java,
            AntiAim::class.java,
            NoC03::class.java,
            Velocity::class.java,
            KillAura2::class.java,
            GroundTelly::class.java,
            Velocity2::class.java,
            PingSpoof::class.java,
            HytPingSpoof::class.java,
            //Test::class.java,
            //Test2::class.java,
         //  HytAntiBot::class.java,
            SuperheroFX::class.java,
            PotionWarn::class.java,
            LegitAura::class.java,
            AutoZhuangbi::class.java,
            HealthNoti::class.java,
            AntiFakePlayer::class.java,
            Blink2::class.java,
            Ambience::class.java,
            Gident::class.java,
            LOVELB::class.java,
            NoSlow2::class.java,
            Cape::class.java,
            Bl1nk::class.java,
            GrimCivBreak::class.java,
            StopMove::class.java,
            NoS32::class.java,
            Scaffold2::class.java,
            Scaffold3::class.java,
            Scaffold4::class.java,
            DMGParticle::class.java
            )

        registerModule(NoScoreboard)
        registerModule(Fucker)
        registerModule(ChestAura)

        ClientUtils.getLogger().info("[ModuleManager] Loaded ${modules.size} modules.")
    }

    /**
     * Register [module]
     */
    fun registerModule(module: Module) {
        if (!module.isSupported)
            return

        modules += module
        moduleClassMap[module.javaClass] = module

        generateCommand(module)
        LiquidBounce.eventManager.registerListener(module)
    }

    /**
     * Register [moduleClass]
     */
    private fun registerModule(moduleClass: Class<out Module>) {
        try {
            registerModule(moduleClass.newInstance())
        } catch (e: Throwable) {
            ClientUtils.getLogger().error("Failed to load module: ${moduleClass.name} (${e.javaClass.name}: ${e.message})")
        }
    }

    /**
     * Register a list of modules
     */
    @SafeVarargs
    fun registerModules(vararg modules: Class<out Module>) {
        modules.forEach(this::registerModule)
    }

    private fun registerModule(cbModule: Any?) {
        registerModule((cbModule as Class<*>).newInstance())
    }

    /**
     * Unregister module
     */
    fun unregisterModule(module: Module) {
        modules.remove(module)
        moduleClassMap.remove(module::class.java)
        LiquidBounce.eventManager.unregisterListener(module)
    }

    /**
     * Generate command for [module]
     */
    internal fun generateCommand(module: Module) {
        val values = module.values

        if (values.isEmpty())
            return

        LiquidBounce.commandManager.registerCommand(ModuleCommand(module, values))
    }

    /**
     * Legacy stuff
     *
     * TODO: Remove later when everything is translated to Kotlin
     */

    /**
     * Get module by [moduleClass]
     */
    fun getModule(moduleClass: Class<*>) = moduleClassMap[moduleClass]!!

    operator fun get(clazz: Class<*>) = getModule(clazz)

    /**
     * Get module by [moduleName]
     */
    fun getModule(moduleName: String?) = modules.find { it.name.equals(moduleName, ignoreCase = true) }

    /**
     * Module related events
     */

    /**
     * Handle incoming key presses
     */
    @EventTarget
    private fun onKey(event: KeyEvent) = modules.filter { it.keyBind == event.key }.forEach { it.toggle() }

    override fun handleEvents() = true
}
