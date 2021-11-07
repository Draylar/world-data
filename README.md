# World Data

---

An experimental Fabric library designed to give you a simple way to attach data to worlds. 
This is basically just a wrapper around `PersistentState`.

This library is intended for my own projects (& not advertised as something anyone else should consider using), but you are still more than welcome to use it in your own projects.
I have not thoroughly tested it (or tried to break it), so use it at your own risk. Breaking API changes will probably occur in the future.


If you need a serious alternative for your projects, check out the wonderful [Cardinal Components API](https://github.com/OnyxStudios/Cardinal-Components-API).

### Usage

---

**Creation**

```java
public class MyWorldData implements WorldData {
    
    private final World world;
    private boolean property = true;
    
    public MyWorldData(World world) {
        this.world = world;
    }
    
    @Override
    public void writeNbt(NbtCompound root) {
        root.putBoolean("Property", property);
    }
    
    @Override
    public void readNbt(NbtCompound root) {
        this.property = root.getBoolean("Property");
    }
    
    @Override
    public World getWorld() {
        return world;
    }
    
    public boolean checkProperty() {
        return property;
    }
    
    public void setProperty(boolean property) {
        this.property = property;
        markDirty();
    }
}
```

**Registration**
```java
public class MyInitializer implements ModInitializer {
    
    public static final WorldDataKey<MyWorldData> MY_DATA = WorldDataRegistry.register(id("data"), MyWorldData::new);
    
    @Override
    public void onInitialize() {
        
    }
}
```

**Usage**
```java
public void somethingHappens(World world) {
    if(WorldData.getData(world, MyInitializer.MY_DATA).checkProperty()) {
        System.out.println("Value is true... setting to false!");
        WorldData.getData(world, MyInitializer.MY_DATA).setProperty(false);
    }
}
```

World Data also offers global data containers (not specific to any world), which can be registered with `WorldDataRegistry.registerGlobal()`.
You can check global data through `WorldData.getGlobalData()`.

### License

---

MIT - feel free to bundle this in your projects or use the code inside as needed.