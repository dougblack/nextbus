package com.doug.nextbus.backend;

public class Favorite {
	public String routeTag;
	public String directionTag;
	public String directionTitle;
	public String stopTag;
	public String stopTitle;

	public Favorite(String routeTag, String directionTag,
			String directionTitle, String stoptag, String stopTitle) {
		super();
		this.routeTag = routeTag;
		this.directionTag = directionTag;
		this.directionTitle = directionTitle;
		this.stopTag = stoptag;
		this.stopTitle = stopTitle;
	}

	@Override
	public boolean equals(Object o) {
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
