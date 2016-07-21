## SwiftAdapter

SwiftAdapter is an easy to use expandable list recyclerview adapter

  - extend one class to get all the features
  - easy and quick to setup
  - easy to customize

### Version
[ ![Download](https://api.bintray.com/packages/vkondrav/maven/swift-adapter/images/download.svg) ](https://bintray.com/vkondrav/maven/swift-adapter/_latestVersion)

**Note**: this version is stable but I have not tested on anything outside my own devices and my specific data sets. For best results try to not steer too far away from the sample project.

### Preview

A [sample] app has been included with the project, just compile and run, or install this [sample apk]. You can also watch a [preview] video. I'm working on releasing a version to the play store soon.

<a href="https://streamable.com/hgri">
<img border="0" alt="SwiftAdapter" src="http://cdn.streamable.com/image/hgri.jpg" width="345" height="614">
</a>

### Import Using Gradle
```gradle
compile 'com.vkondrav.swiftadapter:swift-adapter:0.2.0'
```

### Quickstart

Extend the SwiftAdapter
```java
private static class Adapter extends SwiftAdapter<Adapter.ViewHolder> {
```

Declare you main ViewHolder
```java
public class ViewHolder extends RecyclerView.ViewHolder {
  public ViewHolder(View itemView) {
    super(itemView);
  }
}
```

Override count method to declare how many items of that type you will have

```java
@Override
public int getNumberOfLvl0Items() {
  return 10;
}
```

Override onCreate method to create a specific type of item
```java
@Override
public ViewHolder onCreateLvl0ItemViewHolder(ViewGroup parent) {
  return new ViewHolder(layoutInflater.inflate(R.layout.lvl0_item_layout, parent, false));
}
```

Override to bind data to the viewholder

```java
@Override
public void onBindLvl0Item(ViewHolder holder, ItemIndex index) {
}
```

### Advanced

There are 4 Levels where the view holders can exist. Each section hosts the items and sections below it.

- Level 0 Items
- Level 1 Sections
  - Level 1 Items
  - Level 2 Sections
    - Level 2 Items
    - Level 3 Sections
      - Level 3 Items

Each layer has a set of methods to construct it.
By overriding these methods you can create each layer of the list
```java
int getNumberOf_()
ViewHolder CreateViewHolder_()
void BindViewHolder_(ViewHolder holder, ItemIndex)
```

Open and Close each section by calling these methods and passing ItemIndex from that section.
```java
void openCloseLvl1Section(ItemIndex itemIndex);
void openCloseLvl2Section(ItemIndex itemIndex);
void openCloseLvl3Section(ItemIndex itemIndex);
```

ItemIndex can always be obtained from the specific OnBind Method or by calling getCurrentItemIndex and passing the ViewHolder for that section
```java
public ItemIndex getCurrentItemIndex(T holder)
```

License
----
[Apache 2.0]

[//]:#
[preview]: https://streamable.com/hgri
[sample]: https://github.com/vkondrav/SwiftAdapter/tree/master/sample
[sample apk]: https://github.com/vkondrav/SwiftAdapter/blob/master/sample/sample-apk/sample-release.apk
[Apache 2.0]:http://www.apache.org/licenses/LICENSE-2.0