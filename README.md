GT NextBus
==========

**Planned Changes**

- [x] Change static modifiers to private.
- [x] Removed same route for "other route" popup
- [x] Added support for Emory route
- [x] Fix support for stops that have different tags for different routes,
but are the same. (Like fitten_a for blue and fitten for red)
- [x] Change from native JSON parser to GSON
- [x] Fix Other Arrival Cells
- [?] Fix times for buses
- [?] Update the JSON data [work in progress]
- [ ] Implement warning for when the API is down?
- [ ] Add support for Pause/onResume

**My preferences for code are:**

- Prefer smaller, more compact code.
- Move getter/setters/trivial override methods to bottom of file.
- Annotate @Overrides to keep help understand the purpose of a method.

Quick Documentation
-------------

**How to have a working source**

Necessary libraries are:

* Know how to clone from a GitHub repo!
* RoboGuice: used for depedency injection so there aren't unneeded ```` = (TextView) findViewById(…)```` everywhere.
    * Already Included in /libs/ and necessary libraries 
    * [Github Page](https://github.com/roboguice/roboguice)
    * [Tutorials](https://github.com/roboguice/roboguice/wiki/InjectView)
* ViewPagerIndicator: used for displaying the information.
    * Requires to make the project as a library available for this project. Should be in the same workspace.
    * [GitHub Page](https://github.com/JakeWharton/Android-ViewPagerIndicator)

**Random Notes for Documentation**

- Since Heroku puts dynos to sleep when they become inactive and waking them up is a 10 second process, on the main page load a wake up request is made to the server.
- Prefer factory methods for creating intents.
- Do not include "android-support-v4.jar" since it is included with the ViewPagerIndicator library.
- Routes, directions, and stops have titles and tags. Titles are displayed to the user because they are the full names. Tags are used for API calls since they are smaller. Example shown at the end of this list. Except Route titles are not used because they are too long to display properly. In that case, use the capitalize method to convert the tag into an appropriate format for display.
- There are inconsistencies in stop tags for different routes. For example, Red and Blue both haave a stop at Fitten but for the tags are "fitten" and "fitten_a" for Red and Blue routes, respectively. There are other stops with similar pattern, like for northave and northave_a, and Student Center is sometimes referred to as studcentr and centrstud. This might be a limitation of the NextBus API, but it's an inconvenience for us.
- A route may use one stop in multiple directions. For example, the Midnight Rambler (Night), stops at the Transit Hub twice for its two directions (To Clough Commons and To Fitten). So the user should be able to select the direction and route.


Sample Route/Direction/Stop tag/titles:

    RouteTitle: "Red" [Not used]
    RouteTag: "red"    
    DirectionTitle: "To Transit Hub"
    DirectionTag: "hub"    
    StopTitle: "Recreation Center"
    StopTag: "recctr"

**Activities**

- Route Picker: Shows stops if a route only has one direction. Otherwise shows the direction options for routes with more than one direction, which when selected creates an Stop List intent. 
- Stop List: If a route has more than one direction, this is used to display the stops available for a certain direction.
- Stop View: Shows the predicted times for a route/direction/stop.
- Favorites: Displays the favorited route/direction/stops and when selected creates a Stop View for predictions.
- Preferences: Uses native Android preferences activity.
- Credits: Simple list view with contact information.
- Map View: Shows the routes overlayed on a map. 

**Backend**

- API Controller: Used to interact with the API for prediction data.
- Data: Used for save/loading information about route/direction/stop from JSON.

Old README Below
-------------

An Android client for the Georgia Tech Nextbus stop service.

Link:
- https://play.google.com/store/apps/details?id=com.doug.nextbus

Development has ceased due to lack of time and working Android device. However, it's been installed over 1000 times and has over 500 active users. I'd love it if some other kind soul could take over development to provide the Georgia Tech student body with a beautiful, simple Android Nextbus app.

I hope you decide to fork/pull and help out. Contact me if you have any questions!
