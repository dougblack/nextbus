GT NextBus
==========

Vidhur's Planned Changes

- Fix times for buses [Done, need verification]
- Change static modifiers to private [Done]
- Removed same route for "other route" popup. [Done]
- Added support for Emory route [Done, needs verification]
- Fix support for stops that have different tags for different routes,
but are the same. (Like fitten_a for blue and fitten for red) [Done]
- Update the JSON data [work in progress]
- Change from native JSON parser to GSON [Done]
- Implement warning for when the API is down?
- Fix Other Arrival Cells

==========
Random Notes for Documentation
- Since Heroku puts dynos to sleep when they become inactive and waking them up is a 10 second process, on the main page load a wake up request is made to the server.

==========
An Android client for the Georgia Tech Nextbus stop service.

Link:
- https://play.google.com/store/apps/details?id=com.doug.nextbus

Development has ceased due to lack of time and working Android device. However, it's been installed over 1000 times and has over 500 active users. I'd love it if some other kind soul could take over development to provide the Georgia Tech student body with a beautiful, simple Android Nextbus app.

I hope you decide to fork/pull and help out. Contact me if you have any questions!
