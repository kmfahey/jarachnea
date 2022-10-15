This program is designed to ameliorate the Mastodon project's design decision
not to feature a profile search. It brute-forces the problem, recursively
downloading profiles from the Mastodon web interface. It uses a MySQL databse
as its data store. Each profile that it reads, it saves the bio (rendering
the HTML to text) to the data store, then reads the account's follower and
following lists. These lists are used as sources for further profiles to
download, and the script recurses. The MySQL database maintains a FULLTEXT
record for the bios saved, and the script offers a search interface allowing
the bios to be scanned for matching keywords.
