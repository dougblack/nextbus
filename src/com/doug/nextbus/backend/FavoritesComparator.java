package com.doug.nextbus.backend;

import java.util.Comparator;

public class FavoritesComparator implements Comparator<Favorite> {

	@Override
	public int compare(Favorite lhs, Favorite rhs) {
		int lhsMinIndex = getIndex(lhs.routeTag);
		int rhsMinIndex = getIndex(rhs.routeTag);

		if (lhsMinIndex != rhsMinIndex) {
			if (lhsMinIndex < rhsMinIndex)
				return -1;
			else
				return 1;

		}

		return lhs.toString().compareTo(rhs.toString());
	}

	public int getIndex(String str) {
		for (int i = 0; i < str.length(); i++) {
			if (Data.DEFAULT_ALL_ROUTES[i].equals(str))
				return i;
		}
		return 0;
	}

}
