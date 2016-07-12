package com.vkondrav.swiftadapter;

/**
 * Created by vitaliykondratiev on 2016-07-12.
 */

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

/**
 * Created by vitaliykondratiev on 2016-06-20.
 */
public class SwipeToRefreshLayout extends SwipeRefreshLayout {

	public SwipeToRefreshLayout(Context context) {
		super(context);
		this.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);
	}

	public SwipeToRefreshLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);
	}

	private void setSuperRefreshing(boolean refreshing) {
		super.setRefreshing(refreshing);
	}

	@Override
	public void setProgressViewOffset(boolean scale, int start, int end) {

		/**
		 int tripleWord = StyleHelper.getDimensionSize(new int[]{R.dimen.size_triple_word}, MyApplication.getAppContext());
		 int word = StyleHelper.getDimensionSize(new int[]{R.dimen.size_word}, MyApplication.getAppContext());

		 start = start - tripleWord;
		 end = end + word;
		 **/

		super.setProgressViewOffset(scale, start, end);
	}

	//for some reason set refreshing must be executed in post to allow onMeasure to complete
	@Override
	public void setRefreshing(final boolean refreshing) {
		this.post(new Runnable() {
			@Override
			public void run() {
				SwipeToRefreshLayout.this.setSuperRefreshing(refreshing);
			}
		});
	}
}
