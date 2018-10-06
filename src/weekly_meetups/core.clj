





(ns weekly-meetups.core
  (:require [clojure.data.json :as json])
  (require [again.core :as again])
  (:use [clj-time.core]
        [clj-time.coerce]
        [clj-time.format]
        [clj-time.local]
        [clostache.parser])
  (:gen-class))

;;configuration
(def number-of-days 35)

(def meetups
  {:brisbane [
              "AWS-Brisbane"
              "Agile-Brisbane"
              "Angular-Brisbane"
              "BNoSQL"
              "BitcoinBrisbane"
              "BrisJS"
              "BrisRuby"
              "Brisbane-Artificial-Intelligence"
              "Brisbane-Azure-User-Group"
              "Brisbane-Big-Data-Analytics"
              "Brisbane-Coder-Club"
              "Brisbane-Data-Science-Meetup"
              "Brisbane-Functional-Programming-Group"
              "Brisbane-GPU-Users"
              "Brisbane-Golang-Meetup"
              "Brisbane-Hacks-for-Humanity"
              "Brisbane-Internet-Safety-Meetup"
              "Brisbane-Kotlin-User-Group"
              "Brisbane-Net-User-Group"
              "Brisbane-New-Technology-Meetup"
              "Brisbane-Open-Knowledge-Meetup"
              "Brisbane-OpenShift-Group"
              "Brisbane-Project-Management"
              "Brisbane-Python-User-Group"
              "Brisbane-ReactJS-Meetup"
              "Brisbane-Software-Developers-Startup-Community"
              "Brisbane-Software-Testers-Meetup"
              "Brisbane-Tech-Newbies"
              "Brisbane-Web-Accessibility"
              "Business-Analysts-Architects-Product-Owners-Brisbane"
              "CTO-School-Brisbane"
              "Devops-Brisbane"
              "Ethereum-Brisbane"
              "Hacks-Hackers-Brisbane"
              "Lean-Business-Strategies"
              "NextBankBrisbane"
              "PurposefulCX"
              "Queensland-based-MonoTouch-and-Mono-for-Android"
              "RHoK-Brisbane"
              "Rust-Brisbane"
              "SecTalks-Brisbane"
              "The-Brisbane-Web-Design-Meetup-Group"
              "UXBrisbane"
              "WIDAUS"
              "Women-Who-Code-Brisbane"
              "brisbane-elixir"
              "clj-bne"
              "cocoaheads"
              "hackbne"
              "qldjvm"
              ]
   :tst [     "BrisJS"
              "UXBrisbane"
              "Brisbane-Coder-Club"
              "Brisbane-Artificial-Intelligence"
         ]
   :sydney []
   :melbourne [
"Female-Coders-Lab-Melbourne"
"Melbourne-Scala-User-Group"
"golang-mel"
"Infrastructure-Coders"
"Melbourne-Haskell-Users-Group"
"devops-melbourne"
"AWS-AUS"
"MelbourneMUG"
"Docker-Melbourne-Australia"
"AngularJS-Melbourne"
"Meteor-Melbourne"
"gdg-melbourne"
"Melbourne-Python-Meetup-Group"
"Swift-Devs-Melbourne"
"Melbourne-Microservices"
"MelbCSS"
"Melbourne-Apache-Spark-Meetup"
"React-Melbourne"
"scrum-12"
"Agile-Project-Managers-Melbourne"
"Agile-Business-Analysts-Melbourne"
"OpenTechSchool-Melbourne"
"The-UX-Design-Group-of-Melbourne"
"Open-Knowledge-Melbourne"
"Application-Security-OWASP-Melbourne"
"Big-Data-Analytics-Meetup-Group"
"HadoopMelbourne"
"Ruby-On-Rails-Oceania-Melbourne"
"hack-for-privacy"
"BuzzConf"
"Melbourne-Java-JVM-Users-Group"
"JD-Junior-Developers-Melbourne"
"AgileCoach"
"Limited-WIP-Society"
"Melbourne-Lean-Change-Management-Meetup"
"Cynefin-Melbourne-Meetup-Group"
"Responsive-Org-Melbourne"
"CTO-School-Melbourne"
"Product-Anonymous-Meetup-Melbourne"
"ProductTank-Melbourne"
"Melbourne-Lean-Coffee"
"Visual-Practitioners-Melbourne"
"Design-Thinking-and-Business-Innovation-Melbourne"
"Melbourne-UX-Leadership-Meetup"
"SecTalks-Melbourne"
"Melbourne-VR"
"StartupVictoria"
"The-UX-Design-Group-of-Melbourne"
"Melbourne-CocoaHeads"
"PyLadies-Melbourne"
"Melbourne-Functional-User-Group-MFUG"
]
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
  (println "get " meetup)
  (Thread/sleep 5000)
  (again/with-retries
    [100 1000 10000]
		(-> (format meetup-url api-key meetup number-of-days)
				slurp
				(json/read-str :key-fn keyword)
				:results)))

(defn- get-all-meetups [api-key city]
  (->> (map #(get-meetup-events api-key %) (city meetups))
       flatten))

(defn- format-time [time]
   (unparse
    (with-zone (formatter "yyyy-MM-dd EEEE") (default-time-zone))
    (from-long time)))

(defn- format-event [event]
  {:name (:name event)
   :group_name (:name (:group event))
   :time (format-time (:time event))
   :url (:event_url event)
   :venue_name (:name (:venue event))})

(defn- group-events [events]
	(def g (for [[k v] (group-by #(get % :time) events)] {:time k :values v}))
	g)
	

(defn events-to-html [events]
  (render-resource events-template {:events (sort-by :time (group-events events))}))

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
