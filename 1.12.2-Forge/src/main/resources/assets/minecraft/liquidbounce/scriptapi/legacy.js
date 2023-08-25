var script = registerScript({
name: "Legacy Script",
version: "1.0.0",
authors: ["fix 2023 05 17 by tg"]
});

script.on("enable", function () {
try {
onEnable();
} catch (err) {
// 如果 onEnable 函数发生错误，不捕获该错误并继续使用脚本
}
});

// 当脚本停用时执行
script.on("disable", function () {
try {
onDisable();
} catch (err) {
// 如果 onDisable 函数发生错误，不捕获该错误并继续使用脚本
}
});

// 当脚本加载时执行
script.on("load", function () {
try {
// 设置脚本名、版本号和作者
script.setScriptName(scriptName);
script.setScriptVersion(scriptVersion.toString() + " §7[§4Legacy Script§7]");
script.setScriptAuthors([scriptAuthor]);
} catch (err) {
// 如果设置脚本信息发生错误，不捕获该错误并继续使用脚本
}

try {
    onLoad();
} catch (err) {
    // 如果 onLoad 函数发生错误，不捕获该错误并继续使用脚本
}
});

// 值适配器，用于适配 LiquidBounce 的 Value 类型
var _ValueAdapter = function () {

this.values = [];

// 添加一个 Value
this.add = function (value) {
    this.values.push(value);
}

// 获取适配后的 Value 对象
this.getAdaptedValues = function () {
    var valuesObject = {};

    for (var i = 0; i < this.values.length; i++) {
        var currentValue = this.values[i].getValue();

        valuesObject[currentValue.getName()] = currentValue;
    }

    return valuesObject;
}
}

// 物品适配器，用于适配 LiquidBounce 的 Item 类型
var _ItemAdaptar = function () {

this.items = [];

// 添加一个 Item
this.add = function (item) {
    this.items.push(item);
}

// 获取适配后的 Item 数组
this.getAdaptedItems = function () {
    return this.items;
}
}

// 适配后的 Value 对象
var _AdaptedValue = function (value) {

// 获取 Value 的值
this.get = function () {
    return value.get();
}

// 获取 Value 的名称
this.getName = function () {
    return value.getName();
}

// 获取适配后的 Value 对象
this.getValue = function () {
    return value;
}

// 设置 Value 的值
this.set = function (newValue) {
    value.set(newValue)
}
}

// 适配后的 Module 对象
var _AdaptedModule = function (module) {

this.module = module;
this.moduleManager = Java.type("net.ccbluex.liquidbounce.LiquidBounce").moduleManager;

// 获取 Module 的名称
this.getName = function () {
    return this.module.getName();
}

// 获取 Module 的描述
this.getDescription = function () {
    return this.module.getDescription();
}

// 获取 Module 的分类
this.getCategory = function () {
    return this.module.getCategory().displayName;
}

// 获取 Module 的状态
this.getState = function () {
    return this.module.getState();
}

// 设置 Module 的状态
this.setState = function (state) {
    this.module.setState(state);
}

// 获取 Module 的绑定键
this.getBind = function () {
    return this.module.keyBind;
}

// 设置 Module 的绑定键
this.setBind = function (bind) {
    this.module.keyBind = bind;
}

// 获取适配后的 Value 对象
this.getValue = function (name) {
    return new _AdaptedValue(this.module.getValue(name));
}

// 注册 Module
this.register = function () {
    this.moduleManager.registerModule(this.module);
}

// 注销 Module
this.unregister = function () {
    this.moduleManager.unregisterModule(this.module);
}

// 获取原始 Module 对象
this._getRaw = function () {
    return this.module;
}
}

// 为 _AdaptedModule 对象动态添加 state 和 bind 属性，以方便使用
Object.defineProperty(_AdaptedModule.prototype, "state", {
get: function() {
return this.module.getState();
},
set: function (newState) {
this.module.setState(newState);
}
});

Object.defineProperty(_AdaptedModule.prototype, "bind", {
get: function() {
return this.module.keyBind;
},
set: function (newBind) {
this.module.keyBind = newBind;
}
});

// 模块管理器，用于管理 LiquidBounce 的模块
var _ModuleManager = function () {

// 获取 LiquidBounce 的模块管理器、Module 类型和 ArrayList 类型
this.moduleManager = Java.type("net.ccbluex.liquidbounce.LiquidBounce").moduleManager;
this.Module = Java.type("net.ccbluex.liquidbounce.features.module.Module");
this.ArrayList = Java.type("java.util.ArrayList");

// 注册模块
this.registerModule = function (scriptModule) {
    var moduleConfig = {
        name: scriptModule.getName(),
        description: scriptModule.getDescription(),
        category: scriptModule.getCategory()
    };

    if (scriptModule.addValues) {
        var valueAdapter = new _ValueAdapter();
        scriptModule.addValues(valueAdapter);
        moduleConfig.settings = valueAdapter.getAdaptedValues();
    }

    if (scriptModule.getTag) {
        moduleConfig.tag = scriptModule.getTag();
    }

    script.registerModule(moduleConfig, function (module) {
        var registerEvent = function (eventName, legacyName) {
            if (scriptModule[legacyName]) {
                module.on(eventName, function (event) {
                    scriptModule[legacyName](event);
                });
            }
        }

        if (scriptModule.getTag) {
            var Timer = Java.type("java.util.Timer");

            var updateTagTimer = new Timer("updateTagTimer", true);
            updateTagTimer.schedule(function () {
                module.tag = scriptModule.getTag();
            }, 500, 500);
        }

        registerEvent("update", "onUpdate");
        registerEvent("enable", "onEnable");
        registerEvent("disable", "onDisable");
        registerEvent("packet", "onPacket");
        registerEvent("motion", "onMotion");
        registerEvent("render2D", "onRender2D");
        registerEvent("render3D", "onRender3D");
        registerEvent("jump", "onJump");
        registerEvent("attack", "onAttack");
        registerEvent("key", "onKey");
        registerEvent("move", "onMove");
        registerEvent("step", "onStep");
        registerEvent("stepConfirm", "onStepConfirm");
        registerEvent("world", "onWorld");
        registerEvent("session", "onSession");
        registerEvent("clickBlock", "onClickBlock");
        registerEvent("strafe", "onStrafe");
        registerEvent("slowDown", "onSlowDown");
    });
}

// 注销模块
this.unregisterModule = function (module, autoDisable) {
    if (module instanceof this.Module || module instanceof _AdaptedModule) {
        if (module instanceof _AdaptedModule)
            module = module._getRaw();

        if (autoDisable === undefined)
            autoDisable = true;

        if (autoDisable)
            module.state = false

        this.moduleManager.unregisterModule(module);
    }
}

// 获取适配后的 Module 对象
this.getModule = function (name) {
    return new _AdaptedModule(this.moduleManager.getModule(name));
}

// 获取适配后的 Module 对象数组
this.getModules = function () {
    var modules = new this.ArrayList(this.moduleManager.getModules());
    var adaptedModules = [];

    for (var i = 0; i < modules.size(); i++) {
        adaptedModules.push(new _AdaptedModule(modules[i]));
    }

    return adaptedModules;
}
}

// 命令管理器，用于管理 LiquidBounce 的命令
var _CommandManager = function () {

// 获取 LiquidBounce 的 Command 类型和 commandManager 对象
this.Command = Java.type("net.ccbluex.liquidbounce.features.command.Command");
this.commandManager = Java.type("net.ccbluex.liquidbounce.LiquidBounce").commandManager;

// 注册命令
this.registerCommand = function (scriptCommand) {
    script.registerCommand({
        name: scriptCommand.getName(),
        aliases: scriptCommand.getAliases()
    }, function (command) {
        command.on("execute", function (args) {
            scriptCommand.execute(args);
        });
    })
}

// 注销命令
this.unregisterCommand = function (command) {
    if (command instanceof this.Command) {
        this.commandManager.unregisterCommand(command);
    }
}

// 执行命令
this.executeCommand = function

(commandString) {
this.commandManager.executeCommands(commandString);
}
}
// ChatUtils，用于发送聊天消息
var _ChatUtils = function () {

// 发送聊天消息
this.printChatMessage = function (message) {
    var chatUtil = Java.type("net.ccbluex.liquidbounce.utils.ChatUtils");
    chatUtil.printChatMessage(message);
}

// 发送聊天消息，支持格式化
this.printFormattedChatMessage = function (message, color) {
    var chatUtil = Java.type("net.ccbluex.liquidbounce.utils.ChatUtils");
    chatUtil.printChatMessage(chatUtil.formatMessage(message, color));
}
}

// Minecraft，用于获取 Minecraft 的对象
var _Minecraft = function () {

// 获取 Minecraft 对象
this.getMinecraft = function () {
    return Java.type("net.minecraft.client.Minecraft").getMinecraft();
}

// 获取当前世界的对象
this.getWorld = function () {
    return this.getMinecraft().theWorld;
}

// 获取玩家对象
this.getPlayer = function () {
    return this.getMinecraft().thePlayer;
}

// 获取本地玩家对象
this.getLocalPlayer = function () {
    return this.getMinecraft().thePlayer;
}

// 获取游戏设置对象
this.getGameSettings = function () {
    return this.getMinecraft().gameSettings;
}

// 获取渲染器对象
this.getRenderer = function () {
    return this.getMinecraft().entityRenderer;
}

// 获取屏幕宽度
this.getScreenWidth = function () {
    return this.getMinecraft().displayWidth;
}

// 获取屏幕高度
this.getScreenHeight = function () {
    return this.getMinecraft().displayHeight;
}
}

// 事件类，用于封装 Minecraft 的事件
var _Event = function (event) {

this.event = event;

// 获取原始事件对象
this.getRawEvent = function () {
    return this.event;
}

// 取消事件
this.cancel = function () {
    this.event.setCanceled(true);
}
}

// 渲染器类，用于渲染物品和图形
var _Renderer = function () {

// 渲染物品
this.renderItem = function (itemStack, x, y) {
    var itemRenderer = Java.type("net.minecraft.client.renderer.entity.RenderItem").getInstance();
    itemRenderer.renderItemIntoGUI(itemStack, x, y);
}

// 渲染矩形
this.drawRect = function (x1, y1, x2, y2, color) {
    var gui = Java.type("net.minecraft.client.gui.Gui");
    gui.drawRect(x1, y1, x2, y2, color);
}

// 渲染文本
this.drawString = function (text, x, y, color) {
    var fontRenderer = _Minecraft().getMinecraft().fontRendererObj;
    fontRenderer.drawString(text, x, y, color);
}
}

// 创建全局变量
var moduleManager = new _ModuleManager();
var commandManager = new _CommandManager();
var chatUtils = new _ChatUtils();
var minecraft = new _Minecraft();
var renderer = new _Renderer();

// onLoad 函数，在脚本加载时执行
function onLoad() {
}

// onEnable 函数，在脚本启用时执行
function onEnable() {
}

// onDisable 函数，在脚本停用时执行
function onDisable() {
}