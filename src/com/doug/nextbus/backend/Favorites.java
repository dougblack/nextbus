package com.doug.nextbus.backend;

import java.util.ArrayList;

// Once I figure out how to use HashSets, then I won't have to do a linear search
public class Favorites {

	private ArrayList<Favorite> favs = new ArrayList<Favorite>();

	public boolean toggleFavorite(Favorite favorite) {
		if (favs.remove(favorite)) {
			return false;
		} else {
			favs.add(favorite);
			return true;
		}
	}

	public boolean contains(Favorite favorite) {
		return favs.contains(favorite);
	}

	public Favorite getFavorite(int index) {
		return favs.get(index);
	}

	public int getSize() {
		return favs.size();
	}

}
