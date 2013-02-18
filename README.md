GT NextBus
==========

**Planned Changes**

- Change static modifiers to private [Done]
- Removed same route for "other route" popup. [Done]
- Added support for Emory route [Done]
- Fix support for stops that have different tags for different routes,
but are the same. (Like fitten_a for blue and fitten for red) [Done]
- Change from native JSON parser to GSON [Done]
- Fix Other Arrival Cells [Done]
- Fix times for buses [Done, needs verification]
- Update the JSON data [work in progress]
- Implement warning for when the API is down?
- Add support for Pause/onResume

***

**My preferences for code are:**

- Annotate @Overrides to keep help understand the purpose of a method.
- Move getter/setters/trivial override methods to bottom of file.
- Prefer smaller, more compact code.

***

Quick Documentation
-------------

**Random Notes for Documentation**

- Since Heroku puts dynos to sleep when they become inactive and waking them up is a 10 second process, on the main page load a wake up request is made to the server.

**Activities**

- Route Picker
- Stop List
- Stop View
- Favorites
- Preferences
- Credits
- Map View 

**Backend**

- API Controller
- Data

==========
Old README Below

An Android client for the Georgia Tech Nextbus stop service.

Link:
- https://play.google.com/store/apps/details?id=com.doug.nextbus

Development has ceased due to lack of time and working Android device. However, it's been installed over 1000 times and has over 500 active users. I'd love it if some other kind soul could take over development to provide the Georgia Tech student body with a beautiful, simple Android Nextbus app.

I hope you decide to fork/pull and help out. Contact me if you have any questions!
