# shadowlib
Library for colored, customizable, easy to use shadows on Android

This library is based on
https://github.com/Devlight/ShadowLayout

Differences:
- ported to kotlin
- parameter names changed (similar to CSS)
- removed angle parameter to give flexiblity of setting dx, dy
- added ViewPump interceptor

<img src="https://github.com/maciekczwa/shadowlib/raw/master/screenshot.png" width="200px"/>

## Setup - without Viewpump

### Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:
```groovy
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
### Step 2. Add the dependency
```groovy
	dependencies {
	        implementation 'com.github.maciekczwa:shadowlib:1.0.2'
	}
```

## Setup - with ViewPump

When using ViewPump additional steps are needed.

### Add ViewPump to your build.gradle:

```groovy
implementation 'io.github.inflationx:viewpump:1.0.0'
```

Library can automatically add ShadowLayout to existing view in hierarchy when you use ViewPump library and set ShadowInterceptor.

### Setup ViewPump in Application:

```kotlin
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        ViewPump.init(ViewPump.builder()
                .addInterceptor(ShadowsInterceptor(this))
                .build())
    }
}
```

### Setup ViewPump in Activity:

```kotlin
class MainActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }
}
```

## Usage - without ViewPump

Just add ShadowLayout into your view and set shadow parameters:
```xml
       <pl.looksoft.shadowlib.ShadowLayout
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            app:shadowLayoutBlur="1dp"
            app:shadowLayoutColor="#33FF00FF"
            app:shadowLayoutDx="12dp"
            app:shadowLayoutDy="32dp"
            app:shadowLayoutSpread="-10dp">

            <Button
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="b4 - layout test"
                app:layout_constraintLeft_toLeftOf="@id/b1"
                app:layout_constraintTop_toBottomOf="@id/b2" />

        </pl.looksoft.shadowlib.ShadowLayout>
```

## Usage - with ViewPump

When using ViewPump you don't need to add ShadowLayout - it will be automatically added during inflation if you set any of shadow parameters on the view.

Be sure not to include namespace in xml parameters.

```xml
    <Button
            android:id="@+id/b1"
            shadowLibBlur="5dp"
            shadowLibColor="#FF0000"
            shadowLibDx="10dp"
            shadowLibDy="5dp"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="b1 - auto add layout"
            app:layout_constraintLeft_toLeftOf="parent"
            app:layout_constraintRight_toRightOf="parent"
            app:layout_constraintTop_toTopOf="parent" />
```

You can also use styles:
```xml
       <Button
            android:id="@+id/b8"
            style="@style/ShadowReferenceStyle"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="b8 - reference style test"
            app:layout_constraintLeft_toLeftOf="@id/b1"
            app:layout_constraintTop_toBottomOf="@id/b7" />
```

```xml
   <style name="ShadowReferenceStyle">
        <item name="shadowLibBlur">@dimen/ten_dp</item>
        <item name="shadowLibColor">@color/colorPrimaryDark</item>
        <item name="shadowLibDx">@dimen/ten_dp</item>
        <item name="shadowLibDy">@dimen/ten_dp</item>
        <item name="shadowLibSpread">@dimen/ten_dp</item>
    </style>
```

Warning kotlin extensions won't work correctly for views which are put into ShadowLayout by injection, because ShadowLayout takes id of original view.
Please use ```Activity.findShadowedView(@IdRes id: Int)``` or ```View.findShadowedView(@IdRes id: Int)```

## Known Problems
- Kotlin extensions views don't work correctly when using ViewPump injection
- View will be larger than on preview because of added padding (to show shadow) when using ViewPump incjection

## [Changelog](CHANGELOG.md)
