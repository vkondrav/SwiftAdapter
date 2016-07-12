package com.vkondrav.swiftadapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		final SwipeToRefreshLayout swipeToRefreshLayout = (SwipeToRefreshLayout) findViewById(R.id.swipe_refresh);

		setSupportActionBar(toolbar);

		getSupportActionBar().setTitle(getString(R.string.app_name));
		getSupportActionBar().setSubtitle("Sample");

		final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		final Adapter adapter = new Adapter(this);
		recyclerView.setAdapter(adapter);

		adapter.setBucket(createBucket(0, 0));

		swipeToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				adapter.setBucket(createBucket(0, 0));
				swipeToRefreshLayout.setRefreshing(false);
			}
		});
	}

	private static Bucket createBucket(Integer level, Integer section) {
		Bucket bucket = new Bucket(String.format(Locale.US, "Lvl: %d | Sec: %d", level, section + 1));

		Integer n = new Random().nextInt(7) + 3;

		for (int i = 0; i < n; i++) {
			bucket.itemList.add(String.format(Locale.US, "Item: %d", i + 1));
		}

		if (level < 3) {
			Integer k = new Random().nextInt(5) + 2;

			for (int i = 0; i < k; i++) {
				bucket.bucketList.add(createBucket(level + 1, i));
			}
		}

		return bucket;
	}

	private static class Bucket {
		String name;
		ArrayList<String> itemList;
		ArrayList<Bucket> bucketList;

		public Bucket(String name) {
			this.name = name;
			itemList = new ArrayList<>();
			bucketList = new ArrayList<>();
		}
	}

	private static class Adapter extends SwiftAdapter<Adapter.ViewHolder> {

		private Bucket bucket;

		private Context context;

		private LayoutInflater layoutInflater;

		public Adapter(Context context) {
			this.context = context;
			bucket = new Bucket("");
			layoutInflater = LayoutInflater.from(context);
		}

		public void setBucket(Bucket bucket) {
			this.bucket = bucket;
			notifyDataSetChanged();
		}

		public class ViewHolder extends RecyclerView.ViewHolder {

			public TextView title;
			public View icon1;
			public View icon2;
			public View icon3;
			public ImageView expand;

			public ViewHolder(View itemView) {
				super(itemView);

				title = (TextView) itemView.findViewById(R.id.title);
				icon1 = itemView.findViewById(R.id.icon1);
				icon2 = itemView.findViewById(R.id.icon2);
				icon3 = itemView.findViewById(R.id.icon3);
				expand = (ImageView) itemView.findViewById(R.id.expand);
			}
		}

		/**
		 * NO SECTION ROWS
		 **/

		@Override
		public int getNumberOfRowsForNoSection() {
			return bucket.itemList.size();
		}

		public class NoSectionItemViewHolder extends ViewHolder {

			public NoSectionItemViewHolder(View itemView) {
				super(itemView);
			}
		}

		@Override
		public ViewHolder onCreateNoSectionRowViewHolder(ViewGroup parent) {
			return new NoSectionItemViewHolder(layoutInflater.inflate(R.layout.custom_row, parent, false));
		}

		@Override
		public void onBindNoSectionRow(ViewHolder holder, ItemIndex index) {
			if (holder instanceof NoSectionItemViewHolder) {

				NoSectionItemViewHolder noSectionItemViewHolder = (NoSectionItemViewHolder) holder;

				String title = bucket
						.itemList
						.get(index.row);

				noSectionItemViewHolder.title.setText(title);
			}
		}

		/**
		 * SECTIONS
		 **/

		@Override
		public int getNumberOfSections() {
			return bucket.bucketList.size();
		}

		public class SectionViewHolder extends ViewHolder {

			public SectionViewHolder(View itemView) {
				super(itemView);

				itemView.setBackgroundResource(R.color.section);
			}

			public void setOpen(ItemIndex index) {
				if (isSectionOpen(index.section)) {
					expand.setRotation(180);
				} else {
					expand.setRotation(0);
				}
			}
		}

		@Override
		public ViewHolder onCreateSectionViewHolder(ViewGroup parent) {
			return new SectionViewHolder(layoutInflater.inflate(R.layout.custom_row, parent, false));
		}

		@Override
		public void onBindSection(ViewHolder holder, final ItemIndex index) {
			if (holder instanceof SectionViewHolder) {

				final SectionViewHolder sectionViewHolder = (SectionViewHolder) holder;

				String title = bucket
						.bucketList
						.get(index.section)
						.name;

				sectionViewHolder.title.setText(title);

				sectionViewHolder.icon1.setVisibility(View.VISIBLE);
				sectionViewHolder.expand.setVisibility(View.VISIBLE);

				sectionViewHolder.setOpen(index);

				sectionViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						openCloseSection(index);
						sectionViewHolder.setOpen(index);
					}
				});
			}
		}

		/**
		 * SECTION ROWS
		 **/

		@Override
		public int getNumberOfRowsForSection(int section) {
			return bucket.bucketList.get(section)
					.itemList.size();
		}

		public class SectionItemViewHolder extends ViewHolder {

			public SectionItemViewHolder(View itemView) {
				super(itemView);
			}
		}

		@Override
		public ViewHolder onCreateSectionRowViewHolder(ViewGroup parent) {
			return new SectionItemViewHolder(layoutInflater.inflate(R.layout.custom_row, parent, false));
		}

		@Override
		public void onBindSectionRow(ViewHolder holder, ItemIndex index) {
			if (holder instanceof SectionItemViewHolder) {

				SectionItemViewHolder sectionItemViewHolder = (SectionItemViewHolder) holder;

				String title = bucket
						.bucketList
						.get(index.section)
						.itemList
						.get(index.row);

				sectionItemViewHolder.icon1.setVisibility(View.VISIBLE);

				sectionItemViewHolder.title.setText(title);
			}
		}

		/**
		 * SUBSECTIONS
		 **/

		@Override
		public int getNumberOfSubsectionsForSection(int section) {
			return bucket.bucketList.get(section)
					.bucketList.size();
		}

		public class SubsectionViewHolder extends ViewHolder {

			public SubsectionViewHolder(View itemView) {
				super(itemView);

				itemView.setBackgroundResource(R.color.subsection);
			}

			public void setOpen(ItemIndex index) {
				if (isSubSectionOpen(index.section, index.subsection)) {
					expand.setRotation(180);
				} else {
					expand.setRotation(0);
				}
			}
		}

		@Override
		public ViewHolder onCreateSubsectionViewHolder(ViewGroup parent) {
			return new SubsectionViewHolder(layoutInflater.inflate(R.layout.custom_row, parent, false));
		}

		@Override
		public void onBindSubsection(ViewHolder holder, final ItemIndex index) {
			if (holder instanceof SubsectionViewHolder) {

				final SubsectionViewHolder subsectionViewHolder = (SubsectionViewHolder) holder;

				String title = bucket
						.bucketList
						.get(index.section)
						.bucketList
						.get(index.subsection)
						.name;

				subsectionViewHolder.title.setText(title);

				subsectionViewHolder.icon1.setVisibility(View.VISIBLE);
				subsectionViewHolder.icon2.setVisibility(View.VISIBLE);
				subsectionViewHolder.expand.setVisibility(View.VISIBLE);

				subsectionViewHolder.setOpen(index);

				subsectionViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						openCloseSubsection(index);
						subsectionViewHolder.setOpen(index);
					}
				});
			}
		}

		/**
		 * SUBSECTION ROWS
		 **/

		@Override
		public int getNumberOfRowsForSubsection(int section, int subsection) {
			return bucket.bucketList.get(section)
					.bucketList.get(subsection)
					.itemList.size();
		}

		public class SubsectionItemViewHolder extends ViewHolder {

			public SubsectionItemViewHolder(View itemView) {
				super(itemView);
			}
		}

		@Override
		public ViewHolder onCreateSubsectionRowViewHolder(ViewGroup parent) {
			return new SubsectionItemViewHolder(layoutInflater.inflate(R.layout.custom_row, parent, false));
		}

		@Override
		public void onBindSubsectionRow(ViewHolder holder, ItemIndex index) {
			if (holder instanceof SubsectionItemViewHolder) {

				SubsectionItemViewHolder subsectionItemViewHolder = (SubsectionItemViewHolder) holder;

				String title = bucket
						.bucketList
						.get(index.section)
						.bucketList
						.get(index.subsection)
						.itemList
						.get(index.row);

				subsectionItemViewHolder.icon1.setVisibility(View.VISIBLE);
				subsectionItemViewHolder.icon2.setVisibility(View.VISIBLE);

				subsectionItemViewHolder.title.setText(title);
			}
		}

		/**
		 * SUBSUBSECTIONS
		 **/

		@Override
		public int getNumberOfSubsectionsForSubsection(int section, int subsection) {
			return bucket.bucketList.get(section)
					.bucketList.get(subsection)
					.bucketList.size();
		}

		public class SubSubSectionViewHolder extends ViewHolder {

			public SubSubSectionViewHolder(View itemView) {
				super(itemView);

				itemView.setBackgroundResource(R.color.subsubsection);
			}

			public void setOpen(ItemIndex index) {
				if (isSubSubsectionOpen(index.section, index.subsection, index.subsubsection)) {
					expand.setRotation(180);
				} else {
					expand.setRotation(0);
				}
			}
		}

		@Override
		public ViewHolder onCreateSubSubsectionViewHolder(ViewGroup parent) {
			return new SubSubSectionViewHolder(layoutInflater.inflate(R.layout.custom_row, parent, false));
		}

		@Override
		public void onBindSubsubsection(ViewHolder holder, final ItemIndex index) {
			if (holder instanceof SubSubSectionViewHolder) {

				final SubSubSectionViewHolder subSubSectionViewHolder = (SubSubSectionViewHolder) holder;

				String title = bucket
						.bucketList
						.get(index.section)
						.bucketList
						.get(index.subsection)
						.bucketList
						.get(index.subsubsection)
						.name;

				subSubSectionViewHolder.title.setText(title);

				subSubSectionViewHolder.icon1.setVisibility(View.VISIBLE);
				subSubSectionViewHolder.icon2.setVisibility(View.VISIBLE);
				subSubSectionViewHolder.icon3.setVisibility(View.VISIBLE);
				subSubSectionViewHolder.expand.setVisibility(View.VISIBLE);

				subSubSectionViewHolder.setOpen(index);

				subSubSectionViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						openCloseSubSubsection(index);
						subSubSectionViewHolder.setOpen(index);
					}
				});
			}
		}

		/**
		 * SUBSUBSECTION ROWS
		 **/

		@Override
		public int getNumberOfRowsForSubSubsection(int section, int subsection, int subSubsection) {
			return bucket.bucketList.get(section)
					.bucketList.get(subsection)
					.bucketList.get(subSubsection)
					.itemList.size();
		}

		public class SubSubSectionItemViewHolder extends ViewHolder {

			public SubSubSectionItemViewHolder(View itemView) {
				super(itemView);
			}
		}

		@Override
		public ViewHolder onCreateSubSubsectionRowViewHolder(ViewGroup parent) {
			return new SubSubSectionItemViewHolder(layoutInflater.inflate(R.layout.custom_row, parent, false));
		}

		@Override
		public void onBindSubSubsectionRow(ViewHolder holder, ItemIndex index) {
			if (holder instanceof SubSubSectionItemViewHolder) {

				SubSubSectionItemViewHolder subSubSectionItemViewHolder = (SubSubSectionItemViewHolder) holder;

				String title = bucket
						.bucketList
						.get(index.section)
						.bucketList
						.get(index.subsection)
						.bucketList
						.get(index.subsubsection)
						.itemList
						.get(index.row);

				subSubSectionItemViewHolder.icon1.setVisibility(View.VISIBLE);
				subSubSectionItemViewHolder.icon2.setVisibility(View.VISIBLE);
				subSubSectionItemViewHolder.icon3.setVisibility(View.VISIBLE);

				subSubSectionItemViewHolder.title.setText(title);
			}
		}
	}
}
