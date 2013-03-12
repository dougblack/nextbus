package com.doug.nextbus.backend;

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
		// Returning false if Object o is not of type Favorite class
		if (!(o.getClass().equals(Favorite.class))) {
			return false;
		}

		Favorite fav = (Favorite) o;
		return (fav.routeTag.equals(this.routeTag)
				&& fav.directionTag.equals(this.directionTag)
				&& fav.directionTitle.equals(this.directionTitle)
				&& fav.stopTitle.equals(this.stopTitle) && fav.stopTag
					.equals(this.stopTag));

	}
}
