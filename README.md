# weekly-meetups

A Clojure application to get information about upcoming meetups that you care about. It will output a html string to do with what you will.

The instructions below assume you have Leiningen installed - https://leiningen.org/

## Usage

`lein run YOUR_MEETUP_API_KEY`

or

`lein run YOUR_MEETUP_API_KEY CITY`

or

`lein run YOUR_MEETUP_API_KEY CITY OUTPUT_FILE_NAME`

Your api key can be found [here](https://secure.meetup.com/meetup_api/key/g).

The `OUTPUT_FILE_NAME` defaults to `output.html`
The `CITY` defaults to `brisbane`

##To Do

* Output iCal or other calander format

## License

Copyright © 2014 FIXME

Distributed under the Eclipse Public License, the same as Clojure.
