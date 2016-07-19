package com.vkondrav.swiftadapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by vitaliykondratiev on 2016-07-05.
 */
public abstract class SwiftAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {

	/**
	 * open state of the sections when they are first started
	 */
	private boolean defaultOpenState = false;

	/**
	 * item type used to identify each type of item in the list
	 */
	public enum ItemType {
		LVL3_ITEM, //third level item, nested in lvl 3 section
		LVL2_ITEM, //second lvl item, nested in lvl 2 section
		LVL1_ITEM, //first lvl item, nested in lvl 1 section
		LVL0_ITEM, //appears without any section
		LVL3_SECTION, //third level section nested in lvl2 section
		LVL2_SECTION, //second level section, nested in lvl1 section
		LVL1_SECTION, //first level section
		UNDEFINED;

		public boolean isItem() {
			switch (this) {
				case LVL0_ITEM:
				case LVL3_ITEM:
				case LVL1_ITEM:
				case LVL2_ITEM:
					return true;
				default:
					return false;
			}
		}
	}

	/**
	 * hashmap for storing lvl1 section open/close data
	 */
	private HashMap<String, Boolean> lvl1SectionOpened = new HashMap<>();

	/**
	 * hashmap for storing lvl2 section open/close data
	 */
	private HashMap<String, Boolean> lvl2SectionOpened = new HashMap<>();

	/**
	 * hashmap for storing lvl3 section open/close data
	 */
	private HashMap<String, Boolean> lvl3SectionOpened = new HashMap<>();

	/**
	 * index class used to keep track of each item and its position in the view
	 */
	public static class ItemIndex {

		public int lvl1Section = -1; //position of the lvl 1 section
		public int lvl2Section = -1; //position of the lvl 2 section
		public int lvl3Section = -1; //position of the lvl 3 section
		public int item = -1;        //position of the item
		public int position = -1;    //the absolute position of the item in the list

		public ItemIndex(int lvl1Section, int lvl2Section, int lvl3Section, int item, int position) {
			this.lvl1Section = lvl1Section;
			this.lvl2Section = lvl2Section;
			this.lvl3Section = lvl3Section;
			this.item = item;
			this.position = position;
		}

		@Override
		public String toString() {
			return String.format(Locale.US, "LVL1 Section: %d, LVL2 Section: %d, LVL3 Section: %d, Item: %d, Position: %d",
					lvl1Section, item, lvl2Section, lvl3Section, position);
		}

		/**
		 * get the type of item this index represents
		 *
		 * @return item type
		 */
		public ItemType getType() {

			if (item != -1) {
				if (lvl3Section != -1) {
					return ItemType.LVL3_ITEM;
				}

				if (lvl2Section != -1) {
					return ItemType.LVL2_ITEM;
				}

				if (lvl1Section != -1) {
					return ItemType.LVL1_ITEM;
				}

				return ItemType.LVL0_ITEM;
			}

			if (lvl3Section != -1) {
				return ItemType.LVL3_SECTION;
			}

			if (lvl2Section != -1) {
				return ItemType.LVL2_SECTION;
			}

			if (lvl1Section != -1) {
				return ItemType.LVL1_SECTION;
			}

			return ItemType.UNDEFINED;
		}
	}

	/**
	 * wrapper method
	 *
	 * @param parent
	 * @param itemType
	 * @return
	 */
	public T onCreateViewHolder(ViewGroup parent, int itemType) {

		return onCreateViewHolderItemType(parent, ItemType.values()[itemType]);
	}

	/**
	 * main onCreate method
	 * do not overwrite unless necessary
	 *
	 * @param parent
	 * @param itemType
	 * @return
	 */
	public T onCreateViewHolderItemType(ViewGroup parent, ItemType itemType) {
		switch (itemType) {
			case LVL3_ITEM:
				return onCreateSubSubsectionRowViewHolder(parent);
			case LVL2_ITEM:
				return onCreateSubsectionRowViewHolder(parent);
			case LVL1_ITEM:
				return onCreateSectionRowViewHolder(parent);
			case LVL0_ITEM:
				return onCreateNoSectionRowViewHolder(parent);
			case LVL3_SECTION:
				return onCreateSubSubsectionViewHolder(parent);
			case LVL2_SECTION:
				return onCreateSubsectionViewHolder(parent);
			case LVL1_SECTION:
				return onCreateSectionViewHolder(parent);
			default:
				return null;
		}
	}

	/**
	 * onCreate Methods
	 * overwrite the methods to create each viewholder
	 **/

	public T onCreateSectionViewHolder(ViewGroup parent) {
		return null;
	}

	public T onCreateSubsectionViewHolder(ViewGroup parent) {
		return null;
	}

	public T onCreateNoSectionRowViewHolder(ViewGroup parent) {
		return null;
	}

	public T onCreateSectionRowViewHolder(ViewGroup parent) {
		return null;
	}

	public T onCreateSubsectionRowViewHolder(ViewGroup parent) {
		return null;
	}

	public T onCreateSubSubsectionRowViewHolder(ViewGroup parent) {
		return null;
	}

	public T onCreateSubSubsectionViewHolder(ViewGroup parent) {
		return null;
	}

	/**
	 * sets the default state of the sections when they are created
	 *
	 * @param defaultOpenState
	 */
	public void setDefaultOpenState(boolean defaultOpenState) {
		this.defaultOpenState = defaultOpenState;
	}

	@Override
	public int getItemViewType(int position) {

		return getItemIndex(position).getType().ordinal();
	}

	/**
	 * returns the current index based on the viewholder object within the
	 * recyclerview
	 *
	 * @param holder
	 * @return
	 */
	public ItemIndex getCurrentItemIndex(T holder) {
		return getItemIndex(holder.getLayoutPosition());
	}

	/**
	 * Calculate the idex of the item based on absolute position
	 * TODO: should probably have a more efficient way of calculating this
	 *
	 * @param position
	 * @return
	 */
	public ItemIndex getItemIndex(int position) {
		int n = 0;

		int numberOfLvl0Items = getNumberOfLvl0Items();
		for (int item = 0; item < numberOfLvl0Items; item++) {
			if (position == n) {
				return new ItemIndex(-1, -1, -1, n, position);
			}
			n++;
		}

		int numberOfLvl1Sections = getNumberOfLvl1Sections();
		for (int lvl1Section = 0; lvl1Section < numberOfLvl1Sections; lvl1Section++) {

			if (position == n) {
				return new ItemIndex(lvl1Section, -1, -1, -1, position);
			}

			n++;

			int numberOfLvl1ItemsForSection = getNumberOfLvl1ItemsForSectionS(lvl1Section);
			for (int item = 0; item < numberOfLvl1ItemsForSection; item++) {

				if (position == n) {
					return new ItemIndex(lvl1Section, -1, -1, item, position);
				}
				n++;
			}

			int numberOfLvl2SectionsForSection = getNumberOfLvl2SectionForSectionS(lvl1Section);
			for (int lvl2Section = 0; lvl2Section < numberOfLvl2SectionsForSection; lvl2Section++) {

				if (position == n) {
					return new ItemIndex(lvl1Section, lvl2Section, -1, -1, position);
				}

				n++;

				int numberOfLvl2ItemsForSection = getNumberOfLvl2ItemForSectionS(lvl1Section, lvl2Section);
				for (int item = 0; item < numberOfLvl2ItemsForSection; item++) {

					if (position == n) {
						return new ItemIndex(lvl1Section, lvl2Section, -1, item, position);
					}
					n++;
				}

				int numberOfLvl3SectionsForSection = getNumberOfLvl3SectionsForSectionS(lvl1Section, lvl2Section);
				for (int lvl3Section = 0; lvl3Section < numberOfLvl3SectionsForSection; lvl3Section++) {
					if (position == n) {
						return new ItemIndex(lvl1Section, lvl2Section, lvl3Section, -1, position);
					}

					n++;

					int numberOfLvl3ItemsForSection = getNumberOfLvl3ItemsForSectionS(lvl1Section, lvl2Section, lvl3Section);
					for (int item = 0; item < numberOfLvl3ItemsForSection; item++) {

						if (position == n) {
							return new ItemIndex(lvl1Section, lvl2Section, lvl3Section, item, position);
						}
						n++;
					}
				}
			}
		}

		return new ItemIndex(-1, -1, -1, -1, position);
	}

	public ArrayList<ItemIndex> getItemIndices(ItemType type) {

		ArrayList<ItemIndex> list = new ArrayList<>();

		int itemCount = getItemCount();
		for (int i = 0; i < itemCount; i++) {

			ItemIndex itemIndex = getItemIndex(i);

			if (type == itemIndex.getType()) {
				list.add(itemIndex);
			}
		}

		return list;
	}

	/**
	 * get every item index for every item available in the list
	 * //TODO: should probably have a more efficient way of calculating this
	 *
	 * @return
	 */
	public AllItemIndexes getAllItemIndices() {

		AllItemIndexes allItemIndexes = new AllItemIndexes();

		allItemIndexes.lvl3Items = getItemIndices(ItemType.LVL3_ITEM);
		allItemIndexes.lvl2Items = getItemIndices(ItemType.LVL2_ITEM);
		allItemIndexes.lvl1Items = getItemIndices(ItemType.LVL1_ITEM);
		allItemIndexes.lvl0Items = getItemIndices(ItemType.LVL0_ITEM);
		allItemIndexes.lvl3Sections = getItemIndices(ItemType.LVL3_SECTION);
		allItemIndexes.lvl2Sections = getItemIndices(ItemType.LVL2_SECTION);
		allItemIndexes.lvl1Sections = getItemIndices(ItemType.LVL1_SECTION);

		return allItemIndexes;
	}

	/**
	 * main onBind method
	 * controls all other methods
	 * do not overwrite unless necessary
	 *
	 * @param holder
	 * @param index
	 * @param itemType
	 */
	public void onBindViewHolderItemType(T holder, ItemIndex index, ItemType itemType) {
		switch (itemType) {
			case LVL3_ITEM:
				onBindSubSubsectionRow(holder, index);
				break;
			case LVL2_ITEM:
				onBindSubsectionRow(holder, index);
				break;
			case LVL1_ITEM:
				onBindSectionRow(holder, index);
				break;
			case LVL0_ITEM:
				onBindNoSectionRow(holder, index);
				break;
			case LVL3_SECTION:
				onBindSubsubsection(holder, index);
				break;
			case LVL2_SECTION:
				onBindSubsection(holder, index);
			case LVL1_SECTION:
				onBindSection(holder, index);
		}
	}

	/**
	 * onBind Methinds
	 * overwrite each one to bind during execution
	 **/

	@Override
	public void onBindViewHolder(T holder, int position) {

		ItemIndex index = getItemIndex(position);

		onBindViewHolderItemType(holder, index, index.getType());
	}

	public void onBindSection(T holder, ItemIndex index) {
	}

	public void onBindSubsection(T holder, ItemIndex index) {
	}

	public void onBindNoSectionRow(T holder, ItemIndex index) {
	}

	public void onBindSectionRow(T holder, ItemIndex index) {
	}

	public void onBindSubsectionRow(T holder, ItemIndex index) {
	}

	public void onBindSubSubsectionRow(T holder, ItemIndex index) {
	}

	public void onBindSubsubsection(T holder, ItemIndex index) {
	}

	@Override
	public int getItemCount() {

		int n = getNumberOfLvl0Items();

		int numberOfLvl1Sections = getNumberOfLvl1Sections();
		n += numberOfLvl1Sections;
		for (int lvl1Section = 0; lvl1Section < numberOfLvl1Sections; lvl1Section++) {

			n += getNumberOfLvl1ItemsForSectionS(lvl1Section);

			int numberOfLvl2SectionForSection = getNumberOfLvl2SectionForSectionS(lvl1Section);
			n += numberOfLvl2SectionForSection;
			for (int lvl2Section = 0; lvl2Section < numberOfLvl2SectionForSection; lvl2Section++) {

				n += getNumberOfLvl2ItemForSectionS(lvl1Section, lvl2Section);

				int numberOfLvl3SectionsForSection = getNumberOfLvl3SectionsForSectionS(lvl1Section, lvl2Section);
				n += numberOfLvl3SectionsForSection;
				for (int lvl3Section = 0; lvl3Section < numberOfLvl3SectionsForSection; lvl3Section++) {

					n += getNumberOfLvl3ItemsForSectionS(lvl1Section, lvl2Section, lvl3Section);
				}
			}
		}

		return n;
	}

	public boolean isLvl1SectionOpened(int lvl1Section) {

		String key = getLvl1SectionKey(lvl1Section);

		Boolean open = lvl1SectionOpened.get(key);

		if (open != null) {
			return open;
		} else {
			lvl1SectionOpened.put(key, defaultOpenState);
			return defaultOpenState;
		}
	}

	public void openCloseLvl1Section(ItemIndex index) {

		if (!isLvl1SectionOpened(index.lvl1Section)) {
			openLvl1Section(index);
		} else {
			closeLvl1Section(index);
		}
	}

	public void openCloseLvl1Section(ItemIndex index, boolean open) {

		if (open) {
			openLvl1Section(index);
		} else {
			closeLvl1Section(index);
		}
	}

	private void openLvl1Section(ItemIndex index) {

		lvl1SectionOpened.put(getLvl1SectionKey(index.lvl1Section), true);

		int n = getNumberOfLvl2SectionsForSection(index.lvl1Section);

		for (int i = 0; i < n; i++) {
			lvl2SectionOpened.put(getLvl2SectionKey(index.lvl1Section, index.lvl2Section), false);
		}

		notifyItemRangeInserted(index.position + 1, getNumberOfLvl1ItemsForSection(index.lvl1Section) + n);
	}

	private void closeLvl1Section(ItemIndex index) {

		lvl1SectionOpened.put(getLvl1SectionKey(index.lvl1Section), false);

		int numberOfLvl2SectionsForSection = getNumberOfLvl2SectionsForSection(index.lvl1Section);

		int everythingOpen = 0;
		for (int i = 0; i < numberOfLvl2SectionsForSection; i++) {

			String key = getLvl2SectionKey(index.lvl1Section, i);

			if (isLvl2SectionOpened(index.lvl1Section, i)) {
				everythingOpen += getNumberOfLvl2ItemsForSection(index.lvl1Section, i);

				int numberOfLvl3SectionsForSection = getNumberOfLvl3SectionsForSection(index.lvl1Section, i);

				for (int j = 0; j < numberOfLvl3SectionsForSection; j++) {

					String keyS = getLvl3SectionKey(index.lvl1Section, i, j);
					if (isLvl3SectionOpened(index.lvl1Section, i, j)) {
						everythingOpen += getNumberOfLvl3ItemsForSection(index.lvl1Section, i, j);
					}
					lvl3SectionOpened.put(keyS, false);
				}

				everythingOpen += numberOfLvl3SectionsForSection;
			}
			lvl2SectionOpened.put(key, false);
		}

		int numberOfLvl1ItemsForSection = getNumberOfLvl1ItemsForSection(index.lvl1Section);

		notifyItemRangeRemoved(index.position + 1,
				numberOfLvl1ItemsForSection + numberOfLvl2SectionsForSection + everythingOpen);
	}

	public boolean isLvl2SectionOpened(int lvl1Section, int lvl2Section) {

		String key = getLvl2SectionKey(lvl1Section, lvl2Section);

		Boolean open = lvl2SectionOpened.get(key);

		if (open != null) {
			return open;
		} else {
			lvl2SectionOpened.put(key, defaultOpenState);
			return defaultOpenState;
		}
	}

	public void openCloseLvl2Section(ItemIndex index) {

		if (!isLvl2SectionOpened(index.lvl1Section, index.lvl2Section)) {
			openLvl2Section(index);
		} else {
			closeLvl2Section(index);
		}
	}

	public void openCloseLvl2Section(ItemIndex index, boolean open) {

		if (open) {
			openLvl2Section(index);
		} else {
			closeLvl2Section(index);
		}
	}

	private void openLvl2Section(ItemIndex index) {

		lvl2SectionOpened.put(getLvl2SectionKey(index.lvl1Section, index.lvl2Section), true);

		int numberOfLvl3SectionsForSection = getNumberOfLvl3SectionsForSection(index.lvl1Section, index.lvl2Section);

		for (int i = 0; i < numberOfLvl3SectionsForSection; i++) {
			lvl3SectionOpened.put(getLvl3SectionKey(index.lvl1Section, index.lvl2Section, i), false);
		}

		int numberOfLvl2ItemsForSection = getNumberOfLvl2ItemsForSection(index.lvl1Section, index.lvl2Section);

		notifyItemRangeInserted(index.position + 1, numberOfLvl2ItemsForSection + numberOfLvl3SectionsForSection);
	}

	private void closeLvl2Section(ItemIndex index) {

		lvl2SectionOpened.put(getLvl2SectionKey(index.lvl1Section, index.lvl2Section), false);

		int numberOfLvl3SectionsForSection = getNumberOfLvl3SectionsForSection(index.lvl1Section, index.lvl2Section);

		int subsectionRows = 0;
		for (int i = 0; i < numberOfLvl3SectionsForSection; i++) {

			String key = getLvl3SectionKey(index.lvl1Section, index.lvl2Section, i);

			if (isLvl3SectionOpened(index.lvl1Section, index.lvl2Section, i)) {
				subsectionRows += getNumberOfLvl3ItemsForSection(index.lvl1Section, index.lvl2Section, i);
			}
			lvl3SectionOpened.put(key, false);
		}

		int numberOfLvl2ItemsForSection = getNumberOfLvl2ItemsForSection(index.lvl1Section, index.lvl2Section);

		notifyItemRangeRemoved(index.position + 1,
				numberOfLvl2ItemsForSection + numberOfLvl3SectionsForSection + subsectionRows);
	}

	public boolean isLvl3SectionOpened(int lvl1Section, int lvl2Section, int lvl3Section) {

		String key = getLvl3SectionKey(lvl1Section, lvl2Section, lvl3Section);

		Boolean open = lvl3SectionOpened.get(key);

		if (open != null) {
			return open;
		} else {
			lvl3SectionOpened.put(key, defaultOpenState);
			return defaultOpenState;
		}
	}

	public void openCloseLvl3Section(ItemIndex index) {

		if (!isLvl3SectionOpened(index.lvl1Section, index.lvl2Section, index.lvl3Section)) {
			openLvl3Section(index);
		} else {
			closeLvl3Section(index);
		}
	}

	public void openCloseLvl3Section(ItemIndex index, boolean open) {

		if (open) {
			openLvl3Section(index);
		} else {
			closeLvl3Section(index);
		}
	}

	private void openLvl3Section(ItemIndex index) {

		lvl3SectionOpened.put(getLvl3SectionKey(index.lvl1Section, index.lvl2Section, index.lvl3Section), true);

		notifyItemRangeInserted(index.position + 1, getNumberOfLvl3ItemsForSection(index.lvl1Section, index.lvl2Section, index.lvl3Section));
	}

	private void closeLvl3Section(ItemIndex index) {

		lvl3SectionOpened.put(getLvl3SectionKey(index.lvl1Section, index.lvl2Section, index.lvl3Section), false);

		notifyItemRangeRemoved(index.position + 1, getNumberOfLvl3ItemsForSection(index.lvl1Section, index.lvl2Section, index.lvl3Section));
	}

	private String getLvl1SectionKey(int section) {
		return String.format(Locale.US, "%d", section);
	}

	private String getLvl2SectionKey(int section, int subsection) {
		return String.format(Locale.US, "%d%d", section, subsection);
	}

	private String getLvl3SectionKey(int section, int subsection, int subSubsection) {
		return String.format(Locale.US, "%d%d%d", section, subsection, subSubsection);
	}

	private int getNumberOfLvl2SectionForSectionS(int section) {
		return isLvl1SectionOpened(section) ? getNumberOfLvl2SectionsForSection(section) : 0;
	}

	private int getNumberOfLvl3SectionsForSectionS(int section, int subsection) {
		return isLvl1SectionOpened(section) && isLvl2SectionOpened(section, subsection) ?
				getNumberOfLvl3SectionsForSection(section, subsection) : 0;
	}

	private int getNumberOfLvl1ItemsForSectionS(int section) {
		return isLvl1SectionOpened(section) ? getNumberOfLvl1ItemsForSection(section) : 0;
	}

	private int getNumberOfLvl2ItemForSectionS(int section, int subsection) {
		return isLvl1SectionOpened(section) && isLvl2SectionOpened(section, subsection) ? getNumberOfLvl2ItemsForSection(section, subsection) : 0;
	}

	private int getNumberOfLvl3ItemsForSectionS(int section, int subsection, int subSubsection) {
		return isLvl1SectionOpened(section) && isLvl2SectionOpened(section, subsection) && isLvl3SectionOpened(section, subsection, subSubsection) ?
				getNumberOfLvl3ItemsForSection(section, subsection, subSubsection) : 0;
	}

	public int getNumberOfLvl2SectionsForSection(int section) {
		return 0;
	}

	public int getNumberOfLvl1Sections() {
		return 0;
	}

	public int getNumberOfLvl3SectionsForSection(int section, int subsection) {
		return 0;
	}

	public int getNumberOfLvl1ItemsForSection(int section) {
		return 0;
	}

	public int getNumberOfLvl2ItemsForSection(int section, int subsection) {
		return 0;
	}

	public int getNumberOfLvl3ItemsForSection(int section, int subsection, int subSubsection) {
		return 0;
	}

	public int getNumberOfLvl0Items() {
		return 0;
	}

	/**
	 * custom function to close all
	 * this should be called before setting your new dataset and before calling notifyDataset
	 * closes all sections to prevent weird behavior
	 * REALLY FUCKING SLOW
	 */
	public void closeAll() {

		//close all sections first then reset data
		//this prevents weird behaviour when the dataset is vastly different from the
		//current one

		ArrayList<ItemIndex> lvl3Sections = getItemIndices(ItemType.LVL3_SECTION);

		for (ItemIndex itemIndex : lvl3Sections) {
			openCloseLvl3Section(itemIndex, false);
		}

		ArrayList<ItemIndex> lvl2Sections = getItemIndices(ItemType.LVL2_SECTION);

		for (ItemIndex itemIndex : lvl2Sections) {
			openCloseLvl2Section(itemIndex, false);
		}

		ArrayList<ItemIndex> lvl1Sections = getItemIndices(ItemType.LVL1_SECTION);

		for (ItemIndex itemIndex : lvl1Sections) {
			openCloseLvl1Section(itemIndex, false);
		}
	}

	/**
	 * custom function to open all
	 * REALLY FUCKING SLOW
	 */
	public void openAll() {

		ArrayList<ItemIndex> lvl1Sections = getItemIndices(ItemType.LVL1_SECTION);

		for (ItemIndex itemIndex : lvl1Sections) {
			openCloseLvl1Section(itemIndex, true);
		}

		ArrayList<ItemIndex> lvl2Sections = getItemIndices(ItemType.LVL2_SECTION);

		for (ItemIndex itemIndex : lvl2Sections) {
			openCloseLvl2Section(itemIndex, true);
		}

		ArrayList<ItemIndex> lvl3Sections = getItemIndices(ItemType.LVL3_SECTION);

		for (ItemIndex itemIndex : lvl3Sections) {
			openCloseLvl3Section(itemIndex, true);
		}
	}

	/**
	 * object use to represent all indices in a given list
	 */
	public static class AllItemIndexes {
		public ArrayList<ItemIndex> lvl0Items = new ArrayList<>();
		public ArrayList<ItemIndex> lvl1Items = new ArrayList<>();
		public ArrayList<ItemIndex> lvl2Items = new ArrayList<>();
		public ArrayList<ItemIndex> lvl3Items = new ArrayList<>();

		public ArrayList<ItemIndex> lvl1Sections = new ArrayList<>();
		public ArrayList<ItemIndex> lvl2Sections = new ArrayList<>();
		public ArrayList<ItemIndex> lvl3Sections = new ArrayList<>();
	}
}
