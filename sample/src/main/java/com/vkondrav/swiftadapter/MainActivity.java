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
			closeAll();
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
		public int getNumberOfLvl0Items() {
			return bucket.itemList.size();
		}

		public class Lvl0ItemViewHolder extends ViewHolder {

			public Lvl0ItemViewHolder(View itemView) {
				super(itemView);
			}
		}

		@Override
		public ViewHolder onCreateLvl0ItemViewHolder(ViewGroup parent) {
			return new Lvl0ItemViewHolder(layoutInflater.inflate(R.layout.custom_row, parent, false));
		}

		@Override
		public void onBindLvl0Item(ViewHolder holder, ItemIndex index) {
			if (holder instanceof Lvl0ItemViewHolder) {

				Lvl0ItemViewHolder lvl0ItemViewHolder = (Lvl0ItemViewHolder) holder;

				String title = bucket
						.itemList
						.get(index.item);

				lvl0ItemViewHolder.title.setText(title);
			}
		}

		/**
		 * SECTIONS
		 **/

		@Override
		public int getNumberOfLvl1Sections() {
			return bucket.bucketList.size();
		}

		public class Lv1SectionViewHolder extends ViewHolder {

			public Lv1SectionViewHolder(View itemView) {
				super(itemView);

				itemView.setBackgroundResource(R.color.section);
			}

			public void setOpen(ItemIndex index) {
				if (isLvl1SectionOpened(index.lvl1Section)) {
					expand.setRotation(180);
				} else {
					expand.setRotation(0);
				}
			}
		}

		@Override
		public ViewHolder onCreateLvl1SectionViewHolder(ViewGroup parent) {
			return new Lv1SectionViewHolder(layoutInflater.inflate(R.layout.custom_row, parent, false));
		}

		@Override
		public void onBindLvl1Section(ViewHolder holder, final ItemIndex index) {
			if (holder instanceof Lv1SectionViewHolder) {

				final Lv1SectionViewHolder lv1SectionViewHolder = (Lv1SectionViewHolder) holder;

				String title = bucket
						.bucketList
						.get(index.lvl1Section)
						.name;

				lv1SectionViewHolder.title.setText(title);

				lv1SectionViewHolder.icon1.setVisibility(View.VISIBLE);
				lv1SectionViewHolder.expand.setVisibility(View.VISIBLE);

				lv1SectionViewHolder.setOpen(index);

				lv1SectionViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						openCloseLvl1Section(index);
						lv1SectionViewHolder.setOpen(index);
					}
				});
			}
		}

		/**
		 * SECTION ROWS
		 **/

		@Override
		public int getNumberOfLvl1ItemsForSection(int lvl1Section) {
			return bucket.bucketList.get(lvl1Section)
					.itemList.size();
		}

		public class Lvl1ItemViewHolder extends ViewHolder {

			public Lvl1ItemViewHolder(View itemView) {
				super(itemView);
			}
		}

		@Override
		public ViewHolder onCreateLvl1ItemViewHolder(ViewGroup parent) {
			return new Lvl1ItemViewHolder(layoutInflater.inflate(R.layout.custom_row, parent, false));
		}

		@Override
		public void onBindLvl1Item(ViewHolder holder, ItemIndex index) {
			if (holder instanceof Lvl1ItemViewHolder) {

				Lvl1ItemViewHolder lvl1ItemViewHolder = (Lvl1ItemViewHolder) holder;

				String title = bucket
						.bucketList
						.get(index.lvl1Section)
						.itemList
						.get(index.item);

				lvl1ItemViewHolder.icon1.setVisibility(View.VISIBLE);

				lvl1ItemViewHolder.title.setText(title);
			}
		}

		/**
		 * SUBSECTIONS
		 **/

		@Override
		public int getNumberOfLvl2SectionsForSection(int section) {
			return bucket.bucketList.get(section)
					.bucketList.size();
		}

		public class Lvl2SectionViewHolder extends ViewHolder {

			public Lvl2SectionViewHolder(View itemView) {
				super(itemView);

				itemView.setBackgroundResource(R.color.subsection);
			}

			public void setOpen(ItemIndex index) {
				if (isLvl2SectionOpened(index.lvl1Section, index.lvl2Section)) {
					expand.setRotation(180);
				} else {
					expand.setRotation(0);
				}
			}
		}

		@Override
		public ViewHolder onCreateLvl2SectionViewHolder(ViewGroup parent) {
			return new Lvl2SectionViewHolder(layoutInflater.inflate(R.layout.custom_row, parent, false));
		}

		@Override
		public void onBindLvl2Section(ViewHolder holder, final ItemIndex index) {
			if (holder instanceof Lvl2SectionViewHolder) {

				final Lvl2SectionViewHolder lvl2SectionViewHolder = (Lvl2SectionViewHolder) holder;

				String title = bucket
						.bucketList
						.get(index.lvl1Section)
						.bucketList
						.get(index.lvl2Section)
						.name;

				lvl2SectionViewHolder.title.setText(title);

				lvl2SectionViewHolder.icon1.setVisibility(View.VISIBLE);
				lvl2SectionViewHolder.icon2.setVisibility(View.VISIBLE);
				lvl2SectionViewHolder.expand.setVisibility(View.VISIBLE);

				lvl2SectionViewHolder.setOpen(index);

				lvl2SectionViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						openCloseLvl2Section(index);
						lvl2SectionViewHolder.setOpen(index);
					}
				});
			}
		}

		/**
		 * SUBSECTION ROWS
		 **/

		@Override
		public int getNumberOfLvl2ItemsForSection(int lvl1Section, int lvl2Section) {
			return bucket.bucketList.get(lvl1Section)
					.bucketList.get(lvl2Section)
					.itemList.size();
		}

		public class Lvl2ItemViewHolder extends ViewHolder {

			public Lvl2ItemViewHolder(View itemView) {
				super(itemView);
			}
		}

		@Override
		public ViewHolder onCreateLvl2ItemViewHolder(ViewGroup parent) {
			return new Lvl2ItemViewHolder(layoutInflater.inflate(R.layout.custom_row, parent, false));
		}

		@Override
		public void onBindLvl2Item(ViewHolder holder, ItemIndex index) {
			if (holder instanceof Lvl2ItemViewHolder) {

				Lvl2ItemViewHolder lvl2ItemViewHolder = (Lvl2ItemViewHolder) holder;

				String title = bucket
						.bucketList
						.get(index.lvl1Section)
						.bucketList
						.get(index.lvl2Section)
						.itemList
						.get(index.item);

				lvl2ItemViewHolder.icon1.setVisibility(View.VISIBLE);
				lvl2ItemViewHolder.icon2.setVisibility(View.VISIBLE);

				lvl2ItemViewHolder.title.setText(title);
			}
		}

		/**
		 * SUBSUBSECTIONS
		 **/

		@Override
		public int getNumberOfLvl3SectionsForSection(int lvl1Section, int lvl2Section) {
			return bucket.bucketList.get(lvl1Section)
					.bucketList.get(lvl2Section)
					.bucketList.size();
		}

		public class Lvl3SectionViewHolder extends ViewHolder {

			public Lvl3SectionViewHolder(View itemView) {
				super(itemView);

				itemView.setBackgroundResource(R.color.subsubsection);
			}

			public void setOpen(ItemIndex index) {
				if (isLvl3SectionOpened(index.lvl1Section, index.lvl2Section, index.lvl3Section)) {
					expand.setRotation(180);
				} else {
					expand.setRotation(0);
				}
			}
		}

		@Override
		public ViewHolder onCreateLvl3SectionViewHolder(ViewGroup parent) {
			return new Lvl3SectionViewHolder(layoutInflater.inflate(R.layout.custom_row, parent, false));
		}

		@Override
		public void onBindLvl3Section(ViewHolder holder, final ItemIndex index) {
			if (holder instanceof Lvl3SectionViewHolder) {

				final Lvl3SectionViewHolder lvl3SectionViewHolder = (Lvl3SectionViewHolder) holder;

				String title = bucket
						.bucketList
						.get(index.lvl1Section)
						.bucketList
						.get(index.lvl2Section)
						.bucketList
						.get(index.lvl3Section)
						.name;

				lvl3SectionViewHolder.title.setText(title);

				lvl3SectionViewHolder.icon1.setVisibility(View.VISIBLE);
				lvl3SectionViewHolder.icon2.setVisibility(View.VISIBLE);
				lvl3SectionViewHolder.icon3.setVisibility(View.VISIBLE);
				lvl3SectionViewHolder.expand.setVisibility(View.VISIBLE);

				lvl3SectionViewHolder.setOpen(index);

				lvl3SectionViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						openCloseLvl3Section(index);
						lvl3SectionViewHolder.setOpen(index);
					}
				});
			}
		}

		/**
		 * SUBSUBSECTION ROWS
		 **/

		@Override
		public int getNumberOfLvl3ItemsForSection(int lvl1Section, int lvl2Section, int lvl3Section) {
			return bucket.bucketList.get(lvl1Section)
					.bucketList.get(lvl2Section)
					.bucketList.get(lvl3Section)
					.itemList.size();
		}

		public class Lvl3ItemViewHolder extends ViewHolder {

			public Lvl3ItemViewHolder(View itemView) {
				super(itemView);
			}
		}

		@Override
		public ViewHolder onCreateLvl3ItemViewHolder(ViewGroup parent) {
			return new Lvl3ItemViewHolder(layoutInflater.inflate(R.layout.custom_row, parent, false));
		}

		@Override
		public void onBindLvl3Item(ViewHolder holder, ItemIndex index) {
			if (holder instanceof Lvl3ItemViewHolder) {

				Lvl3ItemViewHolder lvl3ItemViewHolder = (Lvl3ItemViewHolder) holder;

				String title = bucket
						.bucketList
						.get(index.lvl1Section)
						.bucketList
						.get(index.lvl2Section)
						.bucketList
						.get(index.lvl3Section)
						.itemList
						.get(index.item);

				lvl3ItemViewHolder.icon1.setVisibility(View.VISIBLE);
				lvl3ItemViewHolder.icon2.setVisibility(View.VISIBLE);
				lvl3ItemViewHolder.icon3.setVisibility(View.VISIBLE);

				lvl3ItemViewHolder.title.setText(title);
			}
		}
	}
}
