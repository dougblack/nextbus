package com.doug.nextbus.backend;

import java.util.ArrayList;

// Once I figure out how to use HashSets with GSON, then I won't have to do a linear search
public class FavoritesGSON {

	private ArrayList<Favorite> mFavs = new ArrayList<Favorite>();

	/** Returns true if favorite was added, false if removed */
	public boolean toggleFavorite(Favorite favorite) {
		if (mFavs.remove(favorite)) {
			return false;
		} else {
			mFavs.add(favorite);
			return true;
		}
	}

	public boolean contains(Favorite favorite) {
		return mFavs.contains(favorite);
	}

	public Favorite getFavorite(int index) {
		return mFavs.get(index);
	}

	public int getSize() {
		return mFavs.size();
	}

}