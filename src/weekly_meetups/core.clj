(ns weekly-meetups.core
  (:require [clojure.data.json :as json])
  (:use [clj-time.core]
        [clj-time.coerce]
        [clj-time.format]
        [clj-time.local]
        [clostache.parser])
  (:gen-class))

;;configuration
(def number-of-days 9)

(def meetups
  {:brisbane ["clj-bne"
              "qldjvm"
              "AWS-Brisbane"
              "BNoSQL"
              "brisbane-elixir"
              "Brisbane-Net-User-Group"
              "Brisbane-Functional-Programming-Group"
              "Brisbane-Hacks-for-Humanity"
              "Brisbane-Software-Testers-Meetup"
              "Brisbane-Web-Accessibility"
              "BrisRuby"
              "BrisJS"
              "Business-Analysts-Architects-Product-Owners-Brisbane"
              "Brisbane-Open-Knowledge-Meetup"
              "cocoaheads"
              "Agile-Brisbane"
              "hackbne"
              "Lean-Business-Strategies"
              "Brisbane-Python-User-Group"
              "Brisbane-Azure-User-Group"
              "The-Brisbane-Web-Design-Meetup-Group"
              "UXBrisbane"
              "Devops-Brisbane"
              "Brisbane-GPU-Users"
              "Brisbane-Big-Data-Analytics"
              "Brisbane-Coder-Club"
              "Queensland-based-MonoTouch-and-Mono-for-Android"
              "Women-Who-Code-Brisbane"
              "Brisbane-Internet-Safety-Meetup"
              "Hacks-Hackers-Brisbane"
              "BitcoinBrisbane"
              "Cryptohack-Melbourne"
              "SecTalks-Brisbane"
              "NextBankBrisbane"
              "WIDAUS"
              "Brisbane-Project-Management"
              "Brisbane-Golang-Meetup"
              "Ethereum-Brisbane"
              "RHoK-Brisbane"
              "Rust-Brisbane"
              "Brisbane-Kotlin-User-Group"
              "Brisbane-OpenShift-Group"
              "Brisbane-ReactJS-Meetup"
              "CTO-School-Brisbane"]
   :sydney []
   :melbourne []
   :perth []})

;;should need to change this
(def meetup-url
  "http://api.meetup.com/2/events?sign=true&key=%s&group_urlname=%s&time=01012014,%dd")

(def events-template
  "events.mustache")

(def output-file
  "output.html")

;;hacky code that does stuff
(defn- get-meetup-events [api-key meetup]
  (-> (format meetup-url api-key meetup number-of-days)
      slurp
      (json/read-str :key-fn keyword)
      :results))

(defn- get-all-meetups [api-key city]
  (->> (map #(get-meetup-events api-key %) (city meetups))
       flatten))

(defn- format-time [time]
   (unparse
    (with-zone (formatter "EEEE dd/MM") (default-time-zone))
    (from-long time)))

(defn- format-event [event]
  {:name (:name event)
   :group_name (:name (:group event))
   :time (format-time (:time event))
   :url (:event_url event)})

(defn events-to-html [events]
  (render-resource events-template {:events events}))

(defn get-events [api-key city]
  (->>(get-all-meetups api-key city)
      (sort-by :time)
      (map format-event)))

(defn -main
  ([api-key]
     (-main api-key "brisbane" output-file))
  ([api-key city]
     (-main api-key city output-file))
  ([api-key city output-file-name]
     (->> (get-events api-key (keyword city))
          events-to-html
          (spit output-file-name))))
