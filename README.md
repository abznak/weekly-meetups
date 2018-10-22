# weekly-meetups

A Clojure application to get information about upcoming meetups that you care about. It will output a html string to do with what you will.

## Preparation
- Make sure you have [Docker](https://docker.com) installed and running
- Create an account on [Meetup.com](https://meetup.com), login, and get your [API key](https://secure.meetup.com/meetup_api/key/)

## Usage
1. Open **batect.yml**, replace the Meetup API key used in the run task with your own key, and set your desired city
2. Run `./batect run` in your terminal
3. Open **output.html** to view the results

## License

Copyright © 2014 FIXME
Distributed under the Eclipse Public License, the same as Clojure.
