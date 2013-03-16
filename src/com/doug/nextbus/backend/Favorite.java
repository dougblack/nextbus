package com.doug.nextbus.backend;

import java.util.Comparator;

public class Favorite {

	public String routeTag;
	public String directionTag;
	public String directionTitle;
	public String stopTag;
	public String stopTitle;

	public Favorite(String routeTag, String directionTag,
			String directionTitle, String stopTag, String stopTitle) {
		this.routeTag = routeTag;
		this.directionTag = directionTag;
		this.directionTitle = directionTitle;
		this.stopTag = stopTag;
		this.stopTitle = stopTitle;
	}

	@Override
	public String toString() {
		return routeTag + " " + directionTag + " " + routeTag;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;

		// Returning false if Object o is not of type Favorite class
		if (!(o instanceof Favorite)) {
			return false;
		}

		Favorite fav = (Favorite) o;
		return (fav.routeTag.equals(this.routeTag)
				&& fav.directionTag.equals(this.directionTag)
				&& fav.directionTitle.equals(this.directionTitle)
				&& fav.stopTitle.equals(this.stopTitle) && fav.stopTag
					.equals(this.stopTag));

	}

	/** Used for sorting the favorites */
	public static class FavoriteComparator implements Comparator<Favorite> {

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
}
