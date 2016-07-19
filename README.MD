# SwiftAdapter

SwiftAdapter is an easy to use expandable list recyclerview adapter

  - extend one class to get all the features
  - easy and quick to setup
  - easy to customize

### Version
1.0.0

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

There are 4 Levels where the view holders can exist. Each sections hosts the items and sections below it.

- Level 0 Items
- Level 1 Sections
  - Level 1 Items
  - Level 2 Sections
    - Level 2 Items
    - Level 3 Sections
      - Level 3 Items

Each layer has a set of methods to construct it
- getNumberOf_()
- CreateViewHolder_()
- BindViewHolder_()
- (Optional) a unique viewHolder for that layer

License
----



[//]:#
