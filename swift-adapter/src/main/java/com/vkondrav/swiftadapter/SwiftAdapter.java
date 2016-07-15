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

	public enum ItemType {
		SUBSUBSECTIONROW,
		SUBSECTIONROW,
		SECTIONROW,
		NOSECTIONROW,
		SUBSUBSECTION,
		SUBSECTION,
		SECTION,
		UNDEFINED;

		public boolean isRow() {
			switch (this) {
				case NOSECTIONROW:
				case SUBSUBSECTIONROW:
				case SECTIONROW:
				case SUBSECTIONROW:
					return true;
				default:
					return false;
			}
		}
	}

	private HashMap<String, Boolean> sectionOpened = new HashMap<>();

	private HashMap<String, Boolean> subsectionOpened = new HashMap<>();

	private HashMap<String, Boolean> subSubsectionOpened = new HashMap<>();

	/**
	 * index class used to keep track of each item and its position in the view
	 */
	public static class ItemIndex {
		public int section = -1;
		public int subsection = -1;
		public int subsubsection = -1;
		public int row = -1;
		public int position = -1;

		public ItemIndex(int section, int subsection, int subsubsection, int row, int position) {
			this.section = section;
			this.subsection = subsection;
			this.subsubsection = subsubsection;
			this.row = row;
			this.position = position;
		}

		@Override
		public String toString() {
			return String.format(Locale.US, "Section: %d, Subsection: %d, SubSubsection: %d, Row: %d, Position: %d",
					section, row, subsection, subsubsection, position);
		}
	}

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
			case SUBSUBSECTIONROW:
				return onCreateSubSubsectionRowViewHolder(parent);
			case SUBSECTIONROW:
				return onCreateSubsectionRowViewHolder(parent);
			case SECTIONROW:
				return onCreateSectionRowViewHolder(parent);
			case NOSECTIONROW:
				return onCreateNoSectionRowViewHolder(parent);
			case SUBSUBSECTION:
				return onCreateSubSubsectionViewHolder(parent);
			case SUBSECTION:
				return onCreateSubsectionViewHolder(parent);
			case SECTION:
				return onCreateSectionViewHolder(parent);
			default:
				return null;
		}
	}

	/**
	 * onCreate Methods
	 * overwite the methonds to create each viewholder
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

	public void setDefaultOpenState(boolean defaultOpenState) {
		this.defaultOpenState = defaultOpenState;
	}

	@Override
	public int getItemViewType(int position) {

		ItemIndex index = getItemIndex(position);

		if (index.row != -1) {

			if (index.subsubsection != -1) {
				return ItemType.SUBSUBSECTIONROW.ordinal();
			} else if (index.subsection != -1) {
				return ItemType.SUBSECTIONROW.ordinal();
			} else if (index.section != -1) {
				return ItemType.SECTIONROW.ordinal();
			} else {
				return ItemType.NOSECTIONROW.ordinal();
			}
		} else if (index.subsubsection != -1) {
			return ItemType.SUBSUBSECTION.ordinal();
		} else if (index.subsection != -1) {
			return ItemType.SUBSECTION.ordinal();
		} else if (index.section != -1) {
			return ItemType.SECTION.ordinal();
		}

		return ItemType.UNDEFINED.ordinal();
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

	//TODO: should probably have a more efficient way of calculating this
	public ItemIndex getItemIndex(int position) {
		int n = 0;

		int numberOfRowsForNoSection = getNumberOfRowsForNoSection();
		for (int row = 0; row < numberOfRowsForNoSection; row++) {
			if (position == n) {
				return new ItemIndex(-1, -1, -1, n, position);
			}
			n++;
		}

		int numberOfSections = getNumberOfSections();
		for (int section = 0; section < numberOfSections; section++) {

			if (position == n) {
				return new ItemIndex(section, -1, -1, -1, position);
			}

			n++;

			int numberOfRowsForSection = getNumberOfRowsForSectionS(section);
			for (int row = 0; row < numberOfRowsForSection; row++) {

				if (position == n) {
					return new ItemIndex(section, -1, -1, row, position);
				}
				n++;
			}

			int numberOfSubsectionsForSection = getNumberOfSubsectionsForSectionS(section);
			for (int subsection = 0; subsection < numberOfSubsectionsForSection; subsection++) {

				if (position == n) {
					return new ItemIndex(section, subsection, -1, -1, position);
				}

				n++;

				int numberOfRowsForsubsection = getNumberOfRowsForSubsectionS(section, subsection);
				for (int row = 0; row < numberOfRowsForsubsection; row++) {

					if (position == n) {
						return new ItemIndex(section, subsection, -1, row, position);
					}
					n++;
				}

				int numberOfSubsectionForSubsection = getNumberOfSubsectionsForSubsectionS(section, subsection);
				for (int subsubsection = 0; subsubsection < numberOfSubsectionForSubsection; subsubsection++) {
					if (position == n) {
						return new ItemIndex(section, subsection, subsubsection, -1, position);
					}

					n++;

					int numberOfRowsForSubSubsection = getNumberOfRowsForSubSubsectionS(section, subsection, subsubsection);
					for (int row = 0; row < numberOfRowsForSubSubsection; row++) {

						if (position == n) {
							return new ItemIndex(section, subsection, subsubsection, row, position);
						}
						n++;
					}
				}
			}
		}

		return new ItemIndex(-1, -1, -1, -1, position);
	}

	/**
	 * get every item index for every item available in the list
	 * //TODO: should probably have a more efficient way of calculating this
	 *
	 * @return
	 */
	public AllItemIndexes getAllItemIndices() {

		AllItemIndexes allItemIndexes = new AllItemIndexes();

		int n = 0;

		int numberOfRowsForNoSection = getNumberOfRowsForNoSection();
		for (int row = 0; row < numberOfRowsForNoSection; row++) {

			allItemIndexes.noSectionRowItemIndices.add(new ItemIndex(-1, -1, -1, row, n));
			n++;
		}

		int numberOfSections = getNumberOfSections();
		for (int section = 0; section < numberOfSections; section++) {

			allItemIndexes.sectionItemIndices.add(new ItemIndex(section, -1, -1, -1, n));
			n++;

			int numberOfRowsForSection = getNumberOfRowsForSectionS(section);
			for (int row = 0; row < numberOfRowsForSection; row++) {

				allItemIndexes.sectionRowItemIndices.add(new ItemIndex(section, -1, -1, row, n));
				n++;
			}

			int numberOfSubsectionsForSection = getNumberOfSubsectionsForSectionS(section);
			for (int subsection = 0; subsection < numberOfSubsectionsForSection; subsection++) {

				allItemIndexes.subSectionItemIndices.add(new ItemIndex(section, subsection, -1, -1, n));
				n++;

				int numberOfRowsForsubsection = getNumberOfRowsForSubsectionS(section, subsection);
				for (int row = 0; row < numberOfRowsForsubsection; row++) {

					allItemIndexes.subSectionRowItemIdices.add(new ItemIndex(section, subsection, -1, row, n));
					n++;
				}

				int numberOfSubsectionForSubsection = getNumberOfSubsectionsForSubsectionS(section, subsection);
				for (int subsubsection = 0; subsubsection < numberOfSubsectionForSubsection; subsubsection++) {

					allItemIndexes.subSubSectionItemIndices.add(new ItemIndex(section, subsection, subsubsection, -1, n));
					n++;

					int numberOfRowsForSubSubsection = getNumberOfRowsForSubSubsectionS(section, subsection, subsubsection);
					for (int row = 0; row < numberOfRowsForSubSubsection; row++) {

						allItemIndexes.subSubSectionRowItemIndices.add(new ItemIndex(section, subsection, subsubsection, row, n));
						n++;
					}
				}
			}
		}

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
			case SUBSUBSECTIONROW:
				onBindSubSubsectionRow(holder, index);
				break;
			case SUBSECTIONROW:
				onBindSubsectionRow(holder, index);
				break;
			case SECTIONROW:
				onBindSectionRow(holder, index);
				break;
			case NOSECTIONROW:
				onBindNoSectionRow(holder, index);
				break;
			case SUBSUBSECTION:
				onBindSubsubsection(holder, index);
				break;
			case SUBSECTION:
				onBindSubsection(holder, index);
			case SECTION:
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
		ItemType itemType = ItemType.values()[getItemViewType(position)];

		onBindViewHolderItemType(holder, index, itemType);
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

		int n = getNumberOfRowsForNoSection();

		int numberOfSections = getNumberOfSections();
		n += numberOfSections;
		for (int section = 0; section < numberOfSections; section++) {

			n += getNumberOfRowsForSectionS(section);

			int numberOfSubsectionForSection = getNumberOfSubsectionsForSectionS(section);
			n += numberOfSubsectionForSection;
			for (int subsection = 0; subsection < numberOfSubsectionForSection; subsection++) {

				n += getNumberOfRowsForSubsectionS(section, subsection);

				int numberOfSubsectionsForSubsection = getNumberOfSubsectionsForSubsectionS(section, subsection);
				n += numberOfSubsectionsForSubsection;
				for (int subsubsection = 0; subsubsection < numberOfSubsectionsForSubsection; subsubsection++) {

					n += getNumberOfRowsForSubSubsectionS(section, subsection, subsubsection);
				}
			}
		}

		return n;
	}

	public boolean isSectionOpen(int section) {

		String key = getSectionKey(section);

		Boolean open = sectionOpened.get(key);

		if (open != null) {
			return open;
		} else {
			sectionOpened.put(key, defaultOpenState);
			return defaultOpenState;
		}
	}

	public void openCloseSection(ItemIndex index) {

		if (!isSectionOpen(index.section)) {
			openSection(index);
		} else {
			closeSection(index);
		}
	}

	public void openCloseSection(ItemIndex index, boolean open) {

		if (!open) {
			openSection(index);
		} else {
			closeSection(index);
		}
	}

	private void openSection(ItemIndex index) {

		sectionOpened.put(getSectionKey(index.section), true);

		int n = getNumberOfSubsectionsForSection(index.section);

		for (int i = 0; i < n; i++) {
			subsectionOpened.put(getSubsectionKey(index.section, index.subsection), false);
		}

		notifyItemRangeInserted(index.position + 1, getNumberOfRowsForSection(index.section) + n);
	}

	private void closeSection(ItemIndex index) {

		sectionOpened.put(getSectionKey(index.section), false);

		int numberOfSubsectionsForSection = getNumberOfSubsectionsForSection(index.section);

		int everythingOpen = 0;
		for (int i = 0; i < numberOfSubsectionsForSection; i++) {

			String key = getSubsectionKey(index.section, i);

			if (subsectionOpened.get(key)) {
				everythingOpen += getNumberOfRowsForSubsection(index.section, i);

				int numberOfSubsectionsForSubsection = getNumberOfSubsectionsForSubsection(index.section, i);

				for (int j = 0; j < numberOfSubsectionsForSubsection; j++) {

					String keyS = getSubSubsectionKey(index.section, i, j);
					if (subSubsectionOpened.get(keyS)) {
						everythingOpen += getNumberOfRowsForSubSubsection(index.section, i, j);
					}
					subSubsectionOpened.put(keyS, false);
				}

				everythingOpen += numberOfSubsectionsForSubsection;
			}
			subsectionOpened.put(key, false);
		}

		int numberOfRowsForSection = getNumberOfRowsForSection(index.section);

		notifyItemRangeRemoved(index.position + 1,
				numberOfRowsForSection + numberOfSubsectionsForSection + everythingOpen);
	}

	public boolean isSubSectionOpen(int section, int subSection) {

		String key = getSubsectionKey(section, subSection);

		Boolean open = subsectionOpened.get(key);

		if (open != null) {
			return open;
		} else {
			subsectionOpened.put(key, defaultOpenState);
			return defaultOpenState;
		}
	}

	public void openCloseSubsection(ItemIndex index) {

		if (!isSubSectionOpen(index.section, index.subsection)) {
			openSubsection(index);
		} else {
			closeSubsection(index);
		}
	}

	public void openCloseSubsection(ItemIndex index, boolean open) {

		if (!open) {
			openSubsection(index);
		} else {
			closeSubsection(index);
		}
	}

	private void openSubsection(ItemIndex index) {

		subsectionOpened.put(getSubsectionKey(index.section, index.subsection), true);

		int numberOfSubsectionsForSubsection = getNumberOfSubsectionsForSubsection(index.section, index.subsection);

		for (int i = 0; i < numberOfSubsectionsForSubsection; i++) {
			subSubsectionOpened.put(getSubSubsectionKey(index.section, index.subsection, i), false);
		}

		int numberOfRowsForSubsection = getNumberOfRowsForSubsection(index.section, index.subsection);

		notifyItemRangeInserted(index.position + 1, numberOfRowsForSubsection + numberOfSubsectionsForSubsection);
	}

	private void closeSubsection(ItemIndex index) {

		subsectionOpened.put(getSubsectionKey(index.section, index.subsection), false);

		int numberOfSubsectionForSubsection = getNumberOfSubsectionsForSubsection(index.section, index.subsection);

		int subsectionRows = 0;
		for (int i = 0; i < numberOfSubsectionForSubsection; i++) {

			String key = getSubSubsectionKey(index.section, index.subsection, i);

			if (subSubsectionOpened.get(key)) {
				subsectionRows += getNumberOfRowsForSubSubsection(index.section, index.subsection, i);
			}
			subSubsectionOpened.put(key, false);
		}

		int numberOfRowsForSubsection = getNumberOfRowsForSubsection(index.section, index.subsection);

		notifyItemRangeRemoved(index.position + 1,
				numberOfRowsForSubsection + numberOfSubsectionForSubsection + subsectionRows);
	}

	public boolean isSubSubsectionOpen(int section, int subSection, int subSubsection) {

		String key = getSubSubsectionKey(section, subSection, subSubsection);

		Boolean open = subSubsectionOpened.get(key);

		if (open != null) {
			return open;
		} else {
			subSubsectionOpened.put(key, defaultOpenState);
			return defaultOpenState;
		}
	}

	public void openCloseSubSubsection(ItemIndex index) {

		if (!isSubSubsectionOpen(index.section, index.subsection, index.subsubsection)) {
			openSubSubsection(index);
		} else {
			closeSubSubsection(index);
		}
	}

	public void openCloseSubSubsection(ItemIndex index, boolean open) {

		if (!open) {
			openSubSubsection(index);
		} else {
			closeSubSubsection(index);
		}
	}

	private void openSubSubsection(ItemIndex index) {

		subSubsectionOpened.put(getSubSubsectionKey(index.section, index.subsection, index.subsubsection), true);

		notifyItemRangeInserted(index.position + 1, getNumberOfRowsForSubSubsection(index.section, index.subsection, index.subsubsection));
	}

	private void closeSubSubsection(ItemIndex index) {

		subSubsectionOpened.put(getSubSubsectionKey(index.section, index.subsection, index.subsubsection), false);

		notifyItemRangeRemoved(index.position + 1, getNumberOfRowsForSubSubsection(index.section, index.subsection, index.subsubsection));
	}

	private String getSectionKey(int section) {
		return String.format(Locale.US, "%d", section);
	}

	private String getSubsectionKey(int section, int subsection) {
		return String.format(Locale.US, "%d%d", section, subsection);
	}

	private String getSubSubsectionKey(int section, int subsection, int subSubsection) {
		return String.format(Locale.US, "%d%d%d", section, subsection, subSubsection);
	}

	private int getNumberOfSubsectionsForSectionS(int section) {
		return isSectionOpen(section) ? getNumberOfSubsectionsForSection(section) : 0;
	}

	private int getNumberOfSubsectionsForSubsectionS(int section, int subsection) {
		return isSectionOpen(section) && isSubSectionOpen(section, subsection) ?
				getNumberOfSubsectionsForSubsection(section, subsection) : 0;
	}

	private int getNumberOfRowsForSectionS(int section) {
		return isSectionOpen(section) ? getNumberOfRowsForSection(section) : 0;
	}

	private int getNumberOfRowsForSubsectionS(int section, int subsection) {
		return isSectionOpen(section) && isSubSectionOpen(section, subsection) ? getNumberOfRowsForSubsection(section, subsection) : 0;
	}

	private int getNumberOfRowsForSubSubsectionS(int section, int subsection, int subSubsection) {
		return isSectionOpen(section) && isSubSectionOpen(section, subsection) && isSubSubsectionOpen(section, subsection, subSubsection) ?
				getNumberOfRowsForSubSubsection(section, subsection, subSubsection) : 0;
	}

	public int getNumberOfSubsectionsForSection(int section) {
		return 0;
	}

	public int getNumberOfSections() {
		return 0;
	}

	public int getNumberOfSubsectionsForSubsection(int section, int subsection) {
		return 0;
	}

	public int getNumberOfRowsForSection(int section) {
		return 0;
	}

	public int getNumberOfRowsForSubsection(int section, int subsection) {
		return 0;
	}

	public int getNumberOfRowsForSubSubsection(int section, int subsection, int subSubsection) {
		return 0;
	}

	public int getNumberOfRowsForNoSection() {
		return 0;
	}

	/**
	 * custom function to close all
	 * this should be called before setting your new dataset and before calling notifyDataset
	 * closes all sections to prevent weird behavior
	 */
	public void closeAll() {
		AllItemIndexes allItemIndexes = getAllItemIndices();

		//close all sections first then reset data
		//this prevents weird behaviour when the dataset is vastly different from the
		//current one

		for (ItemIndex itemIndex : allItemIndexes.subSubSectionItemIndices) {
			openCloseSubSubsection(itemIndex, false);
		}

		for (ItemIndex itemIndex : allItemIndexes.subSectionItemIndices) {
			openCloseSubsection(itemIndex, false);
		}

		for (ItemIndex itemIndex : allItemIndexes.sectionItemIndices) {
			openCloseSection(itemIndex, false);
		}

		notifyDataSetChanged();
	}

	/**
	 * custom function to open all
	 */
	public void openAll() {
		AllItemIndexes allItemIndexes = getAllItemIndices();

		//close all sections first then reset data
		//this prevents weird behaviour when the dataset is vastly different from the
		//current one

		for (ItemIndex itemIndex : allItemIndexes.sectionItemIndices) {
			openCloseSection(itemIndex, true);
		}

		for (ItemIndex itemIndex : allItemIndexes.subSectionItemIndices) {
			openCloseSubsection(itemIndex, true);
		}

		for (ItemIndex itemIndex : allItemIndexes.subSubSectionItemIndices) {
			openCloseSubSubsection(itemIndex, true);
		}

		notifyDataSetChanged();
	}

	public static class AllItemIndexes {
		public ArrayList<ItemIndex> noSectionRowItemIndices = new ArrayList<>();
		public ArrayList<ItemIndex> sectionItemIndices = new ArrayList<>();
		public ArrayList<ItemIndex> sectionRowItemIndices = new ArrayList<>();
		public ArrayList<ItemIndex> subSectionItemIndices = new ArrayList<>();
		public ArrayList<ItemIndex> subSectionRowItemIdices = new ArrayList<>();
		public ArrayList<ItemIndex> subSubSectionItemIndices = new ArrayList<>();
		public ArrayList<ItemIndex> subSubSectionRowItemIndices = new ArrayList<>();
	}
}
