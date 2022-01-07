# TapSDKSuite-Android
TapSDKSuite 提供了一个可以快速展示TapSDK功能的悬浮窗口。
## 效果展示
<img src=./art/example.gif width=222 height=480 />

## 1.接入前准备
### 1.1 最低版本
**最低Android版本为5.0** .SDK编译环境为Android Studio。

### 1.2 导入 TapSDKSuite
- 将TapSDKSuite_${TapSDKSuiteVersion}.aar拷贝到游戏目录下的src/main/libs目录中
- 引入gson库
- 在游戏目录下的build.gradle文件中添加代码
```
android {
...
    repositories{flatDir{dirs 'src/main/libs'}}
...
}

dependencies {
...
    implementation(name: "TapSDKSuite_${TapSDKSuiteVersion}", ext: "aar")
    implementation "com.google.code.gson:gson:2.8.6"
...
}
```

## 2. TapSDKSuite 使用说明
### 2.1 配置需要使用的功能列表

#### 2.1.1 配置功能 
TapComponent 参数说明：
<a name="参数说明"></a>
参数名称 | 参数说明 
--- | ---
type | enum {MOMENTS(0), FRIENDS(1), ACHIEVEMENT(2), CHAT(3), LEADERBOARD(4), Other(5 ~ Integer.MAX)}
componetName | const {"Moments", "Friends", "Achievement", "Chat", "Leaderboard"}
resourceId |  int [<package_name>.]R.drawable.<resource_name>
customAction | 需要自己实现点选事件 **void invoke(Object... args)** args[0]是开发者传入的type args[1] 是传入的componentName

提供两种创建功能的方法：
```
// 方法一
TapComponent default = TapComponent.createDefault(${type}, ${customAction});

// 方法二
TapComponent custom = TapComponent.createCustom(${type}, ${componentName}, ${resourceId}, ${customAction});
```

#### 2.1.2 配置列表 
配置列表
```
List<TapComponent> tapComponentList = Arrays.asList(component...) 
TapSDKSuite.configComponents(tapComponentList);
```

### 2.2 启动悬浮窗
悬浮窗分两种状态（1、收起，2、展开）
- 收起状态下点触小滑块展示详细的功能列表
- 展开状态下点击屏幕空白区域收起详细列表
```
TapSDKSuite.enable(Activity activity);
```

### 2.3 关闭悬浮窗
```
TapSDKSuite.disable();
```

> 注意点
SDK 暂时不支持自由旋转，在 enable 后只能保证当前展示情况，如果旋转的话需要 调用 diable 后重新 enable.

## License
TapSDKSuite is released under the MIT license. See [LICENSE](LICENSE) for details.